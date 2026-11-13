package com.blockingapp

/** Picks one element of a screen snapshot; returns its index, or null if absent. */
typealias ScreenFinder = (List<FeatureBlocks.ScreenNode>) -> Int?

/**
 * Apps where we restrict certain *sections* instead of the whole app (mode
 * [com.blockingapp.data.BlockedApp.MODE_FEATURE]). The rest of the app keeps
 * working, and the normal daily breaks lift these restrictions too.
 *
 * Each [Section] looks at a snapshot of what's on screen. When it matches, the
 * section is either *blocked* (the app is stepped back and the block screen
 * appears), *redirected* (we tap through the app for the user, e.g. switching
 * Instagram's home feed to "Following"), or *walled* (the list is scrolled back
 * so the user can't scroll into it, e.g. Instagram's suggested posts).
 *
 * Detection reads the app's on-screen labels through the accessibility service,
 * so the rules must match what the app actually shows. When an app update changes
 * them, use the debug log (Logcat tag "FocusFeature") to see the new labels.
 */
object FeatureBlocks {

    /** One visible on-screen element. Text and description are trimmed and lowercased. */
    data class ScreenNode(
        val text: String?,
        val desc: String?,
        /** Full view id, e.g. "com.instagram.android:id/title_logo", or null. */
        val id: String?,
        val clickable: Boolean,
        val selected: Boolean,
        val focused: Boolean,
        /** An empty text field showing its placeholder (e.g. "Search"). */
        val showingHint: Boolean,
        /** A text field. */
        val editable: Boolean = false
    )

    data class Section(
        /** Shown on the block screen and in the app list, e.g. "Spotlight". */
        val name: String,
        /** The element proving this section is open right now, or null. */
        val detect: ScreenFinder,
        /**
         * Null: block the section. Otherwise tap these elements in order instead,
         * each looked up on the screen as it is at that step.
         */
        val redirect: List<ScreenFinder>? = null,
        /**
         * If set, neither block nor redirect: scroll the list containing the
         * detected element back up, and show this message. Acts as a wall at the
         * end of a feed.
         */
        val wall: String? = null
    )

    data class Feature(
        val sections: List<Section>,
        /** The choice offered when adding the app in Focus Blocker. */
        val optionLabel: String,
        /** What the app list shows for this restriction, e.g. "Discover & Spotlight blocked". */
        val summary: String,
        /**
         * Buttons to hide: while one is on screen, a solid patch is drawn over it
         * that also swallows taps, so the button is effectively gone.
         */
        val covers: List<ScreenFinder> = emptyList()
    ) {
        /** Short name for dialogs and the app list, e.g. "Discover & Spotlight". */
        val name: String get() = sections.joinToString(" & ") { it.name }
    }

    private fun first(match: (ScreenNode) -> Boolean): ScreenFinder = { screen ->
        screen.indexOfFirst(match).takeIf { it >= 0 }
    }

    // ---- Snapchat ---------------------------------------------------------

    private val snapchat = Feature(
        sections = listOf(
            // "Discover" heading on the Stories page. Unselected buttons are
            // ignored: a button that *leads* there isn't the section being open.
            Section("Discover", first {
                (it.text == "discover" || it.desc == "discover") && !(it.clickable && !it.selected)
            }),
            // The bottom-bar Spotlight button only has "Spotlight" as its
            // description and is never marked selected; the Spotlight page itself
            // shows "Spotlight" as title *text*.
            Section("Spotlight", first { it.text == "spotlight" }),
            // The Map page: its root is described "Snap Map", and its overlay
            // views carry "map_chrome/…" ids. Catches swiping in from Chat,
            // since the tab button itself is covered.
            Section("Map", first { it.desc == "snap map" || it.id?.contains("map_chrome") == true })
        ),
        optionLabel = "Block Discover, Spotlight & Map only (rest of app works)",
        summary = "🚫 Discover, Spotlight & Map blocked · their tabs hidden",
        // The tab buttons: the label only as a description (the pages' titles
        // have it as text instead). Discover has no tab of its own; it lives on
        // the Stories page, so the Stories button is the way in.
        covers = listOf(
            first { it.desc == "stories" && it.text.isNullOrEmpty() && it.clickable },
            first { it.desc == "spotlight" && it.text.isNullOrEmpty() && it.clickable },
            first { it.desc == "map" && it.text.isNullOrEmpty() && it.clickable }
        )
    )

    // ---- Instagram --------------------------------------------------------
    // Instagram hides its view ids from accessibility (every element's id is
    // null), so these rules use labels. Search & Explore is deliberately left alone.

    private fun ScreenNode.labelStartsWith(vararg prefixes: String): Boolean =
        prefixes.any { p -> text?.startsWith(p) == true || desc?.startsWith(p) == true }

    /**
     * A post from an account you don't follow. Each one's media carries a
     * description like "Suggested Photo by …" / "Suggested Reel by …", and
     * blocks of them get a "Suggested posts" / "Suggested reels" header.
     * Deliberately not "Suggested for you" alone: that also labels the
     * friend-suggestion row.
     */
    private val instagramSuggestedPost: ScreenFinder = first {
        it.text == "suggested posts" || it.text == "suggested reels" ||
            it.labelStartsWith(
                "suggested photo by", "suggested reel by", "suggested video by",
                "suggested carousel by", "suggested post by"
            )
    }

    /**
     * The Instagram logo at the top of the main feed. A device log shows it as
     * an unlabelled button around an element described "Instagram Home Feed";
     * tapping that element taps the button.
     */
    private val instagramHomeLogo: ScreenFinder = first {
        it.desc == "instagram home feed" ||
            (it.clickable && (it.desc == "instagram" || it.text == "instagram"))
    }

    /** "Following" in the menu the logo opens. */
    private val instagramFollowingOption: ScreenFinder = first {
        it.text == "following" || it.desc == "following"
    }

    private val instagram = Feature(
        sections = listOf(
            // A suggested post on the main ("For you") feed: switch to the
            // Following feed, which only has posts from accounts you follow. It
            // has no logo, so this can't loop once you're there.
            Section(
                name = "For-you feed",
                detect = { screen ->
                    instagramHomeLogo(screen)?.takeIf { instagramSuggestedPost(screen) != null }
                },
                redirect = listOf(instagramHomeLogo, instagramFollowingOption)
            ),
            // Fallback while the logo is scrolled out of view, or if the switch
            // ever fails: scroll back so you can't scroll into suggested posts.
            Section(
                name = "Suggested posts",
                detect = instagramSuggestedPost,
                wall = "That's everything from people you follow."
            )
        ),
        optionLabel = "Following feed only (rest of app works)",
        summary = "👥 Following feed only · suggested posts hidden"
    )

    private val features = mapOf(
        "com.snapchat.android" to snapchat,
        "com.instagram.android" to instagram
    )

    fun forPackage(packageName: String): Feature? = features[packageName]
}

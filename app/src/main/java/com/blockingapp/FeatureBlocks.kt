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
        val showingHint: Boolean
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
        val summary: String
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
            Section("Spotlight", first { it.text == "spotlight" })
        ),
        optionLabel = "Block Discover & Spotlight only (rest of app works)",
        summary = "🚫 Discover & Spotlight blocked · rest of app works"
    )

    // ---- Instagram --------------------------------------------------------

    /** The Instagram logo at the top of the home ("For you") feed. */
    private val instagramHomeLogo: ScreenFinder = first {
        it.id?.endsWith(":id/title_logo") == true || (it.desc == "instagram" && it.clickable)
    }

    /** The search box on the Explore/Search tab. */
    private val instagramSearchBox: ScreenFinder = first {
        it.id?.endsWith(":id/action_bar_search_edit_text") == true
    }

    private val instagram = Feature(
        sections = listOf(
            // Home feed: tap the logo, then "Following" in the menu that opens,
            // which shows only posts from accounts you follow.
            Section(
                name = "For-you feed",
                detect = instagramHomeLogo,
                redirect = listOf(instagramHomeLogo, first { it.text == "following" })
            ),
            // Suggested posts from accounts you don't follow, which Instagram adds
            // under "You're all caught up". Not "Suggested for you": Instagram also
            // uses that for the friend-suggestion row *between* real posts, and a
            // wall there would hide your followers' posts.
            Section(
                name = "Suggested posts",
                detect = first { it.text == "suggested posts" || it.text == "suggested reels" },
                wall = "That's everything from people you follow."
            ),
            // Explore grid: shown while the search box is idle. Tapping the box
            // swaps the grid for the search screen, so searching still works.
            Section(
                name = "Explore",
                detect = { screen ->
                    instagramSearchBox(screen)?.takeIf { i ->
                        val box = screen[i]
                        !box.focused && (box.text.isNullOrEmpty() || box.showingHint)
                    }
                },
                redirect = listOf(instagramSearchBox)
            )
        ),
        optionLabel = "Following feed only, no Explore (rest of app works)",
        summary = "👥 Following feed only · Explore hidden"
    )

    private val features = mapOf(
        "com.snapchat.android" to snapchat,
        "com.instagram.android" to instagram
    )

    fun forPackage(packageName: String): Feature? = features[packageName]
}

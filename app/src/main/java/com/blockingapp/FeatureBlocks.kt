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
    // Instagram hides its view ids from accessibility (every element's id is
    // null), so these rules use labels and the bottom tab's selected state, which
    // a device log confirmed Instagram does expose.

    private fun ScreenNode.labelStartsWith(vararg prefixes: String): Boolean =
        prefixes.any { p -> text?.startsWith(p) == true || desc?.startsWith(p) == true }

    /** The bottom-bar tab with this description is the selected one. */
    private fun tabSelected(screen: List<ScreenNode>, tab: String): Boolean =
        screen.any { it.desc == tab && it.selected }

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

    /** The Instagram logo at the top of the home feed (unconfirmed: not seen in a log yet). */
    private val instagramHomeLogo: ScreenFinder = first {
        it.clickable && (it.desc == "instagram" || it.text == "instagram")
    }

    /** The search box on the Search tab: an editable field, or the "Search" placeholder. */
    private val instagramSearchBox: ScreenFinder = { screen ->
        screen.indexOfFirst { it.editable }.takeIf { it >= 0 }
            ?: screen.indexOfFirst {
                it.desc != "search and explore" &&
                    (it.text == "search" || it.desc == "search" || it.labelStartsWith("ask meta ai"))
            }.takeIf { it >= 0 }
    }

    private val instagram = Feature(
        sections = listOf(
            // Suggested posts: scroll back so you can't scroll into them. Checked
            // first so it still works while the home-feed redirect can't find the
            // logo.
            Section(
                name = "Suggested posts",
                detect = instagramSuggestedPost,
                wall = "That's everything from people you follow."
            ),
            // Home feed: tap the logo, then "Following" in the menu that opens.
            // Stays inactive until the logo's label is confirmed from a log.
            Section(
                name = "For-you feed",
                detect = instagramHomeLogo,
                redirect = listOf(instagramHomeLogo, first { it.text == "following" })
            ),
            // Explore grid: on the Search tab while the search box is idle. Tapping
            // the box swaps the grid for the search screen, so searching works.
            Section(
                name = "Explore",
                detect = { screen ->
                    if (!tabSelected(screen, "search and explore")) null
                    else instagramSearchBox(screen)?.takeIf { i ->
                        val box = screen[i]
                        !box.focused && (box.text.isNullOrEmpty() || box.showingHint ||
                            box.text == "search" || box.labelStartsWith("ask meta ai"))
                    }
                },
                redirect = listOf(instagramSearchBox)
            )
        ),
        optionLabel = "No suggested posts, no Explore (rest of app works)",
        summary = "👥 Suggested posts & Explore hidden"
    )

    private val features = mapOf(
        "com.snapchat.android" to snapchat,
        "com.instagram.android" to instagram
    )

    fun forPackage(packageName: String): Feature? = features[packageName]
}

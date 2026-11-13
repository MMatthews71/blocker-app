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
 * appears), *redirected* (we tap through the app for the user, e.g. sending
 * Instagram's feed to Messages), *walled* (the list is scrolled back so the
 * user can't scroll into it), or *limited to the first item* (e.g. the one reel
 * a friend sent you, but not the next one swiped to).
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
        val wall: String? = null,
        /**
         * If set, the first item [detect] finds after the section opens is
         * allowed; moving on to a different one (told apart by its label) steps
         * back out and shows this message.
         */
        val onlyFirst: String? = null
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
    // Messages and search only: every feed in the app is chosen by the
    // algorithm, so the Home and Reels tabs are covered and their pages sent to
    // Messages. The Search tab stays, but its Explore grid never shows: the
    // search box is tapped straight away, so you can still look people up.
    // Instagram hides its view ids (all null), so these rules use the bottom
    // tabs' descriptions and selected state, which a device log confirmed.

    private fun ScreenNode.labelStartsWith(vararg prefixes: String): Boolean =
        prefixes.any { p -> text?.startsWith(p) == true || desc?.startsWith(p) == true }

    /** A bottom-bar tab button, by its description. */
    private fun instagramTab(tab: String): ScreenFinder =
        first { it.desc == tab && it.text.isNullOrEmpty() && it.clickable }

    private val instagramMessagesTab = instagramTab("message")

    /** The bottom-bar [tab] is the open one (and the Messages tab is there to go to). */
    private fun instagramOnTab(tab: String): ScreenFinder = { screen ->
        screen.indexOfFirst { it.desc == tab && it.selected && it.clickable }
            .takeIf { it >= 0 && instagramMessagesTab(screen) != null }
    }

    /** Sends the [tab] page to Messages; if that ever fails, blocks it instead. */
    private fun instagramTabToMessages(name: String, tab: String): List<Section> = listOf(
        Section(name = name, detect = instagramOnTab(tab), redirect = listOf(instagramMessagesTab)),
        Section(name = name, detect = instagramOnTab(tab))
    )

    /** The search box at the top of the Search tab (placeholder "Search with Meta AI"). */
    private val instagramSearchBox: ScreenFinder = first { it.editable }

    /**
     * The Explore grid: the search box sitting idle above it, on the Search tab
     * or above the grid's tiles ("… at row 1, column 2"; the bar can be hidden
     * mid-transition). While you type, or once a search has run, it's left alone.
     */
    private val instagramExploreGrid: ScreenFinder = { screen ->
        instagramSearchBox(screen)?.takeIf { i ->
            val box = screen[i]
            val idle = !box.focused && (box.text.isNullOrEmpty() || box.showingHint ||
                box.text == "search" || box.labelStartsWith("search with meta ai", "ask meta ai"))
            val onGrid = screen.any { it.desc == "search and explore" && it.selected } ||
                screen.any { it.desc?.contains(" at row ") == true }
            idle && onGrid
        }
    }

    private val instagram = Feature(
        sections = instagramTabToMessages("Feed", "home") +
            instagramTabToMessages("Reels", "reels") +
            // Explore grid: tap into the search box instead; block if that fails.
            Section(name = "Explore", detect = instagramExploreGrid, redirect = listOf(instagramSearchBox)) +
            Section(name = "Explore", detect = instagramExploreGrid) +
            // A reel opened from a chat or a profile: watch that one, but swiping
            // up to the next (algorithm-picked) reel steps back out. (On the
            // Reels tab itself the rules above already apply.)
            Section(
                name = "More reels",
                // The full-screen player ("Reel by x. Double tap to play or
                // pause"), not grid tiles or feed posts that are also "Reel by …".
                detect = first { it.labelStartsWith("reel by ") && it.desc?.contains("double tap") == true },
                onlyFirst = "Just the reel you opened."
            ),
        optionLabel = "Messages & search only: no feed, Reels or Explore",
        summary = "💬 Messages & search only · feed, Reels & Explore hidden",
        covers = listOf(instagramTab("home"), instagramTab("reels"))
    )

    private val features = mapOf(
        "com.snapchat.android" to snapchat,
        "com.instagram.android" to instagram
    )

    fun forPackage(packageName: String): Feature? = features[packageName]
}

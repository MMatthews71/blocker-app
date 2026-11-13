package com.blockingapp

/**
 * Apps where we can block certain *sections* instead of the whole app (mode
 * [com.blockingapp.data.BlockedApp.MODE_FEATURE]). The rest of the app keeps
 * working; when a blocked section shows up on screen, the block screen appears
 * and the normal daily breaks can lift it.
 *
 * Detection reads the app's on-screen text through the accessibility service, so
 * the labels must match what the app actually shows. If an app update renames a
 * section, add the new label here.
 */
object FeatureBlocks {

    data class Section(
        /** Shown on the block screen, e.g. "Spotlight". */
        val name: String,
        /**
         * Matched against a visible element's text or content description, whole
         * label only (case-insensitive), so a chat message that merely mentions
         * the word doesn't set it off. Unselected buttons are ignored, since a
         * button that *leads* to the section isn't the section being open.
         */
        val labels: Set<String>,
        /**
         * Match only an element's visible *text* (never its content description)
         * and allow it to be clickable. For a page title whose name also appears
         * as an always-visible navigation button: Snapchat's bottom bar has a
         * "Spotlight" button with that name only as its description, while the
         * Spotlight page itself shows "Spotlight" as title text.
         */
        val pageTitle: Boolean = false
    )

    data class Feature(val sections: List<Section>) {
        /** Shown in our UI, e.g. "Discover & Spotlight". */
        val name: String get() = sections.joinToString(" & ") { it.name }
    }

    private val features = mapOf(
        "com.snapchat.android" to Feature(
            listOf(
                Section("Discover", setOf("discover")),
                Section("Spotlight", setOf("spotlight"), pageTitle = true)
            )
        )
    )

    fun forPackage(packageName: String): Feature? = features[packageName]
}

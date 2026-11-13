package com.blockingapp

/**
 * Apps where we can block one *section* instead of the whole app (mode
 * [com.blockingapp.data.BlockedApp.MODE_FEATURE]). The rest of the app keeps
 * working; when the section shows up on screen, the block screen appears and
 * the normal daily breaks can lift it.
 *
 * Detection reads the app's on-screen text through the accessibility service, so
 * [screenLabels] must match what the app actually shows. If an app update renames
 * the section, add the new label here.
 */
object FeatureBlocks {

    data class Feature(
        /** Shown in our UI, e.g. "Discover". */
        val name: String,
        /**
         * The section counts as open when a visible element's text or content
         * description *equals* one of these (case-insensitive). We match the whole
         * label, not a substring, so a chat message that only mentions the word
         * doesn't set it off.
         */
        val screenLabels: Set<String>
    )

    private val features = mapOf(
        "com.snapchat.android" to Feature(
            name = "Discover",
            screenLabels = setOf("discover")
        )
    )

    fun forPackage(packageName: String): Feature? = features[packageName]
}

package com.blockingapp.ui

import android.graphics.drawable.Drawable

data class AppInfo(
    val name: String,
    val packageName: String,
    val icon: Drawable,
    val isBlocked: Boolean,
    val isPendingRemoval: Boolean = false,
    val isMusicLimited: Boolean = false,
    /** Name of the blocked section (e.g. "Discover") when only part of the app is blocked. */
    val blockedFeature: String? = null
) {
    // Exclude icon from equality so DiffUtil only redraws on meaningful changes.
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is AppInfo) return false
        return name == other.name &&
            packageName == other.packageName &&
            isBlocked == other.isBlocked &&
            isPendingRemoval == other.isPendingRemoval &&
            isMusicLimited == other.isMusicLimited &&
            blockedFeature == other.blockedFeature
    }

    override fun hashCode(): Int {
        var result = 31 * (31 * name.hashCode() + packageName.hashCode()) + isBlocked.hashCode()
        result = 31 * result + isPendingRemoval.hashCode()
        result = 31 * result + isMusicLimited.hashCode()
        result = 31 * result + (blockedFeature?.hashCode() ?: 0)
        return result
    }
}

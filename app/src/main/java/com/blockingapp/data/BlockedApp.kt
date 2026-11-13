package com.blockingapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blocked_apps")
data class BlockedApp(
    @PrimaryKey val packageName: String,
    val pendingRemoval: Boolean = false,
    /**
     * [MODE_BLOCK] = full app block; [MODE_MUSIC] = music limited to a daily budget;
     * [MODE_FEATURE] = only one section blocked (see [com.blockingapp.FeatureBlocks]).
     */
    val mode: String = MODE_BLOCK
) {
    companion object {
        const val MODE_BLOCK = "BLOCK"
        const val MODE_MUSIC = "MUSIC"
        const val MODE_FEATURE = "FEATURE"
    }
}

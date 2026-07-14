package com.blockingapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class FocusApp : Application() {

    val applicationScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val repository: BlockerRepository by lazy { BlockerRepository.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        applicationScope.launch {
            repository.getDailyState()
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Focus Blocker",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Keeps the app blocker running" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_ID = "blocker_service"
    }
}

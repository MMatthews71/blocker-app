package com.blockingapp.admin

import android.app.admin.DeviceAdminReceiver
import android.content.ComponentName
import android.content.Context

/**
 * Registers the app as a device administrator. While this admin is active,
 * Android blocks direct uninstall of the app (the user must first deactivate
 * admin), which — combined with the accessibility-service escape guarding —
 * lets us enforce the 24-hour uninstall wait.
 *
 * We deliberately request no enforcement policies; mere active-admin status is
 * enough to gate uninstall.
 */
class FocusDeviceAdminReceiver : DeviceAdminReceiver() {

    /**
     * Shown on the system "Deactivate device admin" screen. Framing it as a
     * warning nudges the user back toward the in-app 24-hour flow.
     */
    override fun onDisableRequested(context: Context, intent: android.content.Intent): CharSequence {
        return "Deactivating this removes Focus Blocker's uninstall protection. " +
            "If you're trying to delete the app to reach a blocked app, remember " +
            "you set up the 24-hour wait for a reason."
    }

    companion object {
        fun componentName(context: Context): ComponentName =
            ComponentName(context, FocusDeviceAdminReceiver::class.java)
    }
}

package com.blockingapp.admin;

/**
 * Registers the app as a device administrator. While this admin is active,
 * Android blocks direct uninstall of the app (the user must first deactivate
 * admin), which — combined with the accessibility-service escape guarding —
 * lets us enforce the 24-hour uninstall wait.
 *
 * We deliberately request no enforcement policies; mere active-admin status is
 * enough to gate uninstall.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\r\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \t2\u00020\u0001:\u0001\tB\u0005\u00a2\u0006\u0002\u0010\u0002J\u0018\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016\u00a8\u0006\n"}, d2 = {"Lcom/blockingapp/admin/FocusDeviceAdminReceiver;", "Landroid/app/admin/DeviceAdminReceiver;", "()V", "onDisableRequested", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "Companion", "app_debug"})
public final class FocusDeviceAdminReceiver extends android.app.admin.DeviceAdminReceiver {
    @org.jetbrains.annotations.NotNull()
    public static final com.blockingapp.admin.FocusDeviceAdminReceiver.Companion Companion = null;
    
    public FocusDeviceAdminReceiver() {
        super();
    }
    
    /**
     * Shown on the system "Deactivate device admin" screen. Framing it as a
     * warning nudges the user back toward the in-app 24-hour flow.
     */
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.CharSequence onDisableRequested(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.content.Intent intent) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006\u00a8\u0006\u0007"}, d2 = {"Lcom/blockingapp/admin/FocusDeviceAdminReceiver$Companion;", "", "()V", "componentName", "Landroid/content/ComponentName;", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final android.content.ComponentName componentName(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}
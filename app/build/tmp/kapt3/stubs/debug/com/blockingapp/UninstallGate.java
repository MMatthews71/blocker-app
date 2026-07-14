package com.blockingapp;

/**
 * The 24-hour "wait before you can delete the app" gate.
 *
 * Stored in SharedPreferences (not the Room daily-state table) on purpose: the
 * daily state resets every calendar day, but this timer must survive across days
 * — otherwise the wait would silently disappear at midnight.
 *
 * States (see [state]):
 * - LOCKED  : uninstall is blocked; user must request it.
 * - PENDING : user requested; still counting down the 24 hours.
 * - OPEN    : the wait elapsed; there's a short window to actually uninstall
 *             before it re-locks itself.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0001\u0017B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0018\u0010\r\u001a\n \u000f*\u0004\u0018\u00010\u000e0\u000e2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u000e\u0010\u0010\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fJ\u0018\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0013\u001a\u00020\u0007J\u0018\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\u0013\u001a\u00020\u0007J\u000e\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2 = {"Lcom/blockingapp/UninstallGate;", "", "()V", "KEY_UNLOCK_AT", "", "PREFS", "WAIT_MS", "", "WINDOW_MS", "cancel", "", "context", "Landroid/content/Context;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "requestUninstall", "shouldGuard", "", "now", "state", "Lcom/blockingapp/UninstallGate$State;", "unlockAt", "State", "app_debug"})
public final class UninstallGate {
    
    /**
     * How long the user must wait after requesting an uninstall.
     */
    public static final long WAIT_MS = 86400000L;
    
    /**
     * After the wait elapses, how long the uninstall window stays open before it
     * re-locks. Keeps protection from silently turning off forever if the user
     * requests an uninstall but then changes their mind.
     */
    public static final long WINDOW_MS = 3600000L;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS = "uninstall_gate";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_UNLOCK_AT = "unlock_at";
    @org.jetbrains.annotations.NotNull()
    public static final com.blockingapp.UninstallGate INSTANCE = null;
    
    private UninstallGate() {
        super();
    }
    
    private final android.content.SharedPreferences prefs(android.content.Context context) {
        return null;
    }
    
    /**
     * Timestamp when the wait completes, or 0 if no request is active.
     */
    public final long unlockAt(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final com.blockingapp.UninstallGate.State state(@org.jetbrains.annotations.NotNull()
    android.content.Context context, long now) {
        return null;
    }
    
    /**
     * Whether the accessibility service should block routes to uninstall/deactivate.
     */
    public final boolean shouldGuard(@org.jetbrains.annotations.NotNull()
    android.content.Context context, long now) {
        return false;
    }
    
    /**
     * Start the 24-hour countdown.
     */
    public final void requestUninstall(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    /**
     * Cancel a pending request (or clear an open/expired window) and re-lock.
     */
    public final void cancel(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005\u00a8\u0006\u0006"}, d2 = {"Lcom/blockingapp/UninstallGate$State;", "", "(Ljava/lang/String;I)V", "LOCKED", "PENDING", "OPEN", "app_debug"})
    public static enum State {
        /*public static final*/ LOCKED /* = new LOCKED() */,
        /*public static final*/ PENDING /* = new PENDING() */,
        /*public static final*/ OPEN /* = new OPEN() */;
        
        State() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<com.blockingapp.UninstallGate.State> getEntries() {
            return null;
        }
    }
}
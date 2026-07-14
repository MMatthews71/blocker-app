package com.blockingapp.service;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u0000 82\u00020\u0001:\u00018B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010#\u001a\u00020$H\u0002J\b\u0010%\u001a\u00020$H\u0002J\u0014\u0010&\u001a\u0004\u0018\u00010\u00042\b\u0010\'\u001a\u0004\u0018\u00010(H\u0002J\u0010\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020\u0004H\u0002J\b\u0010,\u001a\u00020*H\u0002J\b\u0010-\u001a\u00020*H\u0002J\b\u0010.\u001a\u00020$H\u0002J\u000e\u0010/\u001a\u00020$2\u0006\u00100\u001a\u00020\nJ\u0010\u00101\u001a\u00020$2\u0006\u00102\u001a\u000203H\u0016J\b\u00104\u001a\u00020$H\u0016J\b\u00105\u001a\u00020$H\u0016J\b\u00106\u001a\u00020$H\u0014J\b\u00107\u001a\u00020$H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00040\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u00020\nX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\nX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\u0015\u001a\n \u0017*\u0004\u0018\u00010\u00160\u00168BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001a\u0010\u001b\u001a\u0004\b\u0018\u0010\u0019R\u001b\u0010\u001c\u001a\u00020\u001d8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b \u0010\u001b\u001a\u0004\b\u001e\u0010\u001fR\u000e\u0010!\u001a\u00020\"X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00069"}, d2 = {"Lcom/blockingapp/service/AppBlockerAccessibilityService;", "Landroid/accessibilityservice/AccessibilityService;", "()V", "appLabel", "", "blockedPackages", "", "heartbeatFormat", "Ljava/text/SimpleDateFormat;", "inMemoryBreakEndsAt", "", "getInMemoryBreakEndsAt", "()J", "setInMemoryBreakEndsAt", "(J)V", "lastBounceAt", "lastForegroundPackage", "mainHandler", "Landroid/os/Handler;", "pollRunnable", "Ljava/lang/Runnable;", "powerManager", "Landroid/os/PowerManager;", "kotlin.jvm.PlatformType", "getPowerManager", "()Landroid/os/PowerManager;", "powerManager$delegate", "Lkotlin/Lazy;", "repository", "Lcom/blockingapp/BlockerRepository;", "getRepository", "()Lcom/blockingapp/BlockerRepository;", "repository$delegate", "scope", "Lkotlinx/coroutines/CoroutineScope;", "bounceFromEscapeScreen", "", "checkAndBlockForeground", "collectScreenText", "root", "Landroid/view/accessibility/AccessibilityNodeInfo;", "isEscapeRoutePackage", "", "pkg", "isEscapeScreen", "isOnBreakFast", "launchBlockActivity", "notifyBreakStarted", "breakEndsAt", "onAccessibilityEvent", "event", "Landroid/view/accessibility/AccessibilityEvent;", "onDestroy", "onInterrupt", "onServiceConnected", "updateHeartbeat", "Companion", "app_debug"})
public final class AppBlockerAccessibilityService extends android.accessibilityservice.AccessibilityService {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler mainHandler = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy repository$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy powerManager$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private java.util.Set<java.lang.String> blockedPackages;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Runnable pollRunnable = null;
    @kotlin.jvm.Volatile()
    private volatile long inMemoryBreakEndsAt = 0L;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastForegroundPackage;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String appLabel = "";
    private long lastBounceAt = 0L;
    @org.jetbrains.annotations.NotNull()
    private final java.text.SimpleDateFormat heartbeatFormat = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.Object BREAK_END_TOKEN = null;
    private static final long POLL_INTERVAL_MS = 1000L;
    private static final int MAX_NODES_SCANNED = 400;
    private static final long BOUNCE_DEBOUNCE_MS = 1500L;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<java.lang.String> ESCAPE_KEYWORDS = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.blockingapp.service.AppBlockerAccessibilityService instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.blockingapp.service.AppBlockerAccessibilityService.Companion Companion = null;
    
    public AppBlockerAccessibilityService() {
        super();
    }
    
    private final com.blockingapp.BlockerRepository getRepository() {
        return null;
    }
    
    private final android.os.PowerManager getPowerManager() {
        return null;
    }
    
    public final long getInMemoryBreakEndsAt() {
        return 0L;
    }
    
    public final void setInMemoryBreakEndsAt(long p0) {
    }
    
    @java.lang.Override()
    protected void onServiceConnected() {
    }
    
    @java.lang.Override()
    public void onAccessibilityEvent(@org.jetbrains.annotations.NotNull()
    android.view.accessibility.AccessibilityEvent event) {
    }
    
    @java.lang.Override()
    public void onInterrupt() {
    }
    
    private final boolean isEscapeRoutePackage(java.lang.String pkg) {
        return false;
    }
    
    /**
     * True when the current screen is one of the "delete / disable Focus Blocker"
     * routes:
     * - app-info page (Uninstall / Force stop) and the uninstall confirmation,
     * - device-admin *deactivation* screen,
     * - the accessibility "Turn off / Stop Focus Blocker?" confirmation dialog
     *   that Android always shows before disabling our service.
     *
     * Heuristic: the screen mentions our app label AND an escape keyword. We
     * require both to avoid bouncing the user out of unrelated Settings pages,
     * and we deliberately match the *action* words ("deactivate", "turn off",
     * "stop") rather than page words like "device admin"/"accessibility", so the
     * admin activation screen and the general Accessibility list still work — we
     * only intervene at the final confirmation step.
     */
    private final boolean isEscapeScreen() {
        return false;
    }
    
    /**
     * Depth/size-bounded traversal collecting lowercased text from the window.
     */
    private final java.lang.String collectScreenText(android.view.accessibility.AccessibilityNodeInfo root) {
        return null;
    }
    
    private final void bounceFromEscapeScreen() {
    }
    
    private final void checkAndBlockForeground() {
    }
    
    private final void updateHeartbeat() {
    }
    
    private final boolean isOnBreakFast() {
        return false;
    }
    
    private final void launchBlockActivity() {
    }
    
    /**
     * Called by BlockActivity the moment a break is confirmed, before finish().
     */
    public final void notifyBreakStarted(long breakEndsAt) {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u001c\u0010\f\u001a\u0004\u0018\u00010\rX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011\u00a8\u0006\u0012"}, d2 = {"Lcom/blockingapp/service/AppBlockerAccessibilityService$Companion;", "", "()V", "BOUNCE_DEBOUNCE_MS", "", "BREAK_END_TOKEN", "ESCAPE_KEYWORDS", "", "", "MAX_NODES_SCANNED", "", "POLL_INTERVAL_MS", "instance", "Lcom/blockingapp/service/AppBlockerAccessibilityService;", "getInstance", "()Lcom/blockingapp/service/AppBlockerAccessibilityService;", "setInstance", "(Lcom/blockingapp/service/AppBlockerAccessibilityService;)V", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.blockingapp.service.AppBlockerAccessibilityService getInstance() {
            return null;
        }
        
        public final void setInstance(@org.jetbrains.annotations.Nullable()
        com.blockingapp.service.AppBlockerAccessibilityService p0) {
        }
    }
}
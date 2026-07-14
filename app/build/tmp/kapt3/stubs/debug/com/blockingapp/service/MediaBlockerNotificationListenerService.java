package com.blockingapp.service;

/**
 * Limits — rather than fully blocks — music from "music-limited" apps.
 *
 * The user may listen for up to the configurable [MusicSettings] budget per day
 * (shared across all music-limited apps). Browsing those apps is unaffected —
 * only active playback burns the budget. Once it's spent, playback is paused
 * whenever it resumes for the rest of the day; the budget resets at midnight.
 *
 * Time is measured by polling the system's active media sessions rather than by
 * reacting to notification posts, which don't fire continuously during playback.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 ,2\u00020\u0001:\u0001,B\u0005\u00a2\u0006\u0002\u0010\u0002J\b\u0010#\u001a\u00020$H\u0002J\b\u0010%\u001a\u00020$H\u0002J\b\u0010&\u001a\u00020$H\u0016J\b\u0010\'\u001a\u00020$H\u0016J\b\u0010(\u001a\u00020$H\u0002J\u000e\u0010)\u001a\b\u0012\u0004\u0012\u00020+0*H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\t8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\r\u0010\u000e\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\u000f\u001a\u00020\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u0013\u001a\u00020\u00148BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0017\u0010\u000e\u001a\u0004\b\u0015\u0010\u0016R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001b\u0010\u001a\u001a\u00020\u001b8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u001e\u0010\u000e\u001a\u0004\b\u001c\u0010\u001dR\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\"X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006-"}, d2 = {"Lcom/blockingapp/service/MediaBlockerNotificationListenerService;", "Landroid/service/notification/NotificationListenerService;", "()V", "lastAccrualAt", "", "lastLimitToastAt", "mainHandler", "Landroid/os/Handler;", "mediaSessionManager", "Landroid/media/session/MediaSessionManager;", "kotlin.jvm.PlatformType", "getMediaSessionManager", "()Landroid/media/session/MediaSessionManager;", "mediaSessionManager$delegate", "Lkotlin/Lazy;", "musicMsUsedToday", "musicPackages", "", "", "repository", "Lcom/blockingapp/BlockerRepository;", "getRepository", "()Lcom/blockingapp/BlockerRepository;", "repository$delegate", "scope", "Lkotlinx/coroutines/CoroutineScope;", "selfComponent", "Landroid/content/ComponentName;", "getSelfComponent", "()Landroid/content/ComponentName;", "selfComponent$delegate", "sessionsChangedListener", "Landroid/media/session/MediaSessionManager$OnActiveSessionsChangedListener;", "ticker", "Ljava/lang/Runnable;", "enforceTick", "", "notifyLimitReached", "onDestroy", "onListenerConnected", "pauseNow", "playingMusicControllers", "", "Landroid/media/session/MediaController;", "Companion", "app_debug"})
public final class MediaBlockerNotificationListenerService extends android.service.notification.NotificationListenerService {
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final android.os.Handler mainHandler = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy repository$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy mediaSessionManager$delegate = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy selfComponent$delegate = null;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.NotNull()
    private volatile java.util.Set<java.lang.String> musicPackages;
    @kotlin.jvm.Volatile()
    private volatile long musicMsUsedToday = 0L;
    private long lastAccrualAt = 0L;
    private long lastLimitToastAt = 0L;
    @org.jetbrains.annotations.NotNull()
    private final android.media.session.MediaSessionManager.OnActiveSessionsChangedListener sessionsChangedListener = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.Runnable ticker = null;
    private static final long TICK_MS = 15000L;
    private static final long LIMIT_TOAST_INTERVAL_MS = 300000L;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.blockingapp.service.MediaBlockerNotificationListenerService instance;
    @org.jetbrains.annotations.NotNull()
    public static final com.blockingapp.service.MediaBlockerNotificationListenerService.Companion Companion = null;
    
    public MediaBlockerNotificationListenerService() {
        super();
    }
    
    private final com.blockingapp.BlockerRepository getRepository() {
        return null;
    }
    
    private final android.media.session.MediaSessionManager getMediaSessionManager() {
        return null;
    }
    
    private final android.content.ComponentName getSelfComponent() {
        return null;
    }
    
    @java.lang.Override()
    public void onListenerConnected() {
    }
    
    /**
     * Active media sessions belonging to music-limited apps that are playing.
     */
    private final java.util.List<android.media.session.MediaController> playingMusicControllers() {
        return null;
    }
    
    /**
     * One accrual/enforcement pass. Runs on the main thread; the DB write and any
     * pause happen off it. [lastAccrualAt] is only touched here, so it needs no
     * synchronization.
     */
    private final void enforceTick() {
    }
    
    private final void pauseNow() {
    }
    
    private final void notifyLimitReached() {
    }
    
    @java.lang.Override()
    public void onDestroy() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000b\u00a8\u0006\f"}, d2 = {"Lcom/blockingapp/service/MediaBlockerNotificationListenerService$Companion;", "", "()V", "LIMIT_TOAST_INTERVAL_MS", "", "TICK_MS", "instance", "Lcom/blockingapp/service/MediaBlockerNotificationListenerService;", "getInstance", "()Lcom/blockingapp/service/MediaBlockerNotificationListenerService;", "setInstance", "(Lcom/blockingapp/service/MediaBlockerNotificationListenerService;)V", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.Nullable()
        public final com.blockingapp.service.MediaBlockerNotificationListenerService getInstance() {
            return null;
        }
        
        public final void setInstance(@org.jetbrains.annotations.Nullable()
        com.blockingapp.service.MediaBlockerNotificationListenerService p0) {
        }
    }
}
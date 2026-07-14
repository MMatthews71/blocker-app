package com.blockingapp;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u000f\u0018\u0000 +2\u00020\u0001:\u0001+B\u000f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u001e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u0019H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u000e\u0010\u001c\u001a\u00020\u001dH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0014\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0014\u0010 \u001a\b\u0012\u0004\u0012\u00020\u00150\tH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\u00150\tH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u000e\u0010\"\u001a\u00020\u000eH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0014\u0010#\u001a\b\u0012\u0004\u0012\u00020\u00150\tH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u000e\u0010$\u001a\u00020\u0019H\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u0010%\u001a\u00020\u001d2\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010&J\u000e\u0010\'\u001a\u00020\u001dH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u000e\u0010(\u001a\u00020\u0019H\u0086@\u00a2\u0006\u0002\u0010\u001eJ\b\u0010)\u001a\u00020\u0015H\u0002J\u0016\u0010*\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010&R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0007\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\n0\t0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0019\u0010\r\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000e0\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lcom/blockingapp/BlockerRepository;", "", "db", "Lcom/blockingapp/data/AppDatabase;", "(Lcom/blockingapp/data/AppDatabase;)V", "blockedAppDao", "Lcom/blockingapp/data/BlockedAppDao;", "blockedAppsFlow", "Lkotlinx/coroutines/flow/Flow;", "", "Lcom/blockingapp/data/BlockedApp;", "getBlockedAppsFlow", "()Lkotlinx/coroutines/flow/Flow;", "dailyStateFlow", "Lcom/blockingapp/data/DailyFocusState;", "getDailyStateFlow", "stateDao", "Lcom/blockingapp/data/DailyFocusStateDao;", "addApp", "", "packageName", "", "mode", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "addMusicListeningTime", "", "deltaMs", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "canTakeBreak", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllBlockedApps", "getAllBlockedPackages", "getBlockModePackages", "getDailyState", "getMusicModePackages", "getMusicMsUsedToday", "isBlocked", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "isOnBreak", "startBreak", "todayString", "toggleBlock", "Companion", "app_debug"})
public final class BlockerRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.blockingapp.data.BlockedAppDao blockedAppDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.blockingapp.data.DailyFocusStateDao stateDao = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<java.util.List<com.blockingapp.data.BlockedApp>> blockedAppsFlow = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<com.blockingapp.data.DailyFocusState> dailyStateFlow = null;
    public static final int MAX_BREAKS = 2;
    public static final long BREAK_DURATION_MS = 900000L;
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile com.blockingapp.BlockerRepository INSTANCE;
    @org.jetbrains.annotations.NotNull()
    public static final com.blockingapp.BlockerRepository.Companion Companion = null;
    
    private BlockerRepository(com.blockingapp.data.AppDatabase db) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<com.blockingapp.data.BlockedApp>> getBlockedAppsFlow() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<com.blockingapp.data.DailyFocusState> getDailyStateFlow() {
        return null;
    }
    
    private final java.lang.String todayString() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getDailyState(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.blockingapp.data.DailyFocusState> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isOnBreak(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object canTakeBreak(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Returns the breakEndsAt timestamp, or -1 if no breaks remain.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object startBreak(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllBlockedPackages(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
    
    /**
     * Packages that trigger the full block screen — excludes music-limited apps.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getBlockModePackages(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
    
    /**
     * Packages whose music playback is capped by the daily budget.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMusicModePackages(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllBlockedApps(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.blockingapp.data.BlockedApp>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isBlocked(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    /**
     * Add an app in a specific mode, or clear a pending removal if it already exists.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addApp(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    java.lang.String mode, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    /**
     * Tap toggle for an existing entry: a scheduled removal is undone (re-enabled),
     * otherwise removal is scheduled for the next day (kept until midnight so the
     * user can't instantly undo their own limit).
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object toggleBlock(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getMusicMsUsedToday(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    /**
     * Add elapsed listening time to today's music budget; returns the new total.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object addMusicListeningTime(long deltaMs, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\t\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000bR\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\f"}, d2 = {"Lcom/blockingapp/BlockerRepository$Companion;", "", "()V", "BREAK_DURATION_MS", "", "INSTANCE", "Lcom/blockingapp/BlockerRepository;", "MAX_BREAKS", "", "getInstance", "context", "Landroid/content/Context;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final com.blockingapp.BlockerRepository getInstance(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
    }
}
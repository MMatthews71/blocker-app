package com.blockingapp;

/**
 * User-configurable daily music-listening allowance for music-limited apps.
 * Stored in SharedPreferences so the media service can read it cheaply.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\fJ\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\fJ\u0018\u0010\u000f\u001a\n \u0011*\u0004\u0018\u00010\u00100\u00102\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0016\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2 = {"Lcom/blockingapp/MusicSettings;", "", "()V", "DEFAULT_BUDGET_MIN", "", "KEY_BUDGET_MIN", "", "MAX_BUDGET_MIN", "PREFS", "STEP_MIN", "budgetMinutes", "context", "Landroid/content/Context;", "budgetMs", "", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "setBudgetMinutes", "", "minutes", "app_debug"})
public final class MusicSettings {
    public static final int DEFAULT_BUDGET_MIN = 60;
    public static final int MAX_BUDGET_MIN = 180;
    public static final int STEP_MIN = 15;
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS = "music_settings";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_BUDGET_MIN = "budget_min";
    @org.jetbrains.annotations.NotNull()
    public static final com.blockingapp.MusicSettings INSTANCE = null;
    
    private MusicSettings() {
        super();
    }
    
    private final android.content.SharedPreferences prefs(android.content.Context context) {
        return null;
    }
    
    public final int budgetMinutes(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0;
    }
    
    public final void setBudgetMinutes(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int minutes) {
    }
    
    public final long budgetMs(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0L;
    }
}
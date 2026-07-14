package com.blockingapp.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u0007\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u0004\u0018\u00010\u00052\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0014\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00050\u000eH\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u000e0\u0010H\'J\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\u000eH\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000b0\u000eH\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000b0\u000eH\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0016\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0015\u001a\u00020\u00162\u0006\u0010\n\u001a\u00020\u000bH\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0016\u0010\u0017\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0018"}, d2 = {"Lcom/blockingapp/data/BlockedAppDao;", "", "delete", "", "app", "Lcom/blockingapp/data/BlockedApp;", "(Lcom/blockingapp/data/BlockedApp;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deletePendingRemovals", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "get", "pkg", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "", "getAllFlow", "Lkotlinx/coroutines/flow/Flow;", "getAllPackageNames", "getBlockModePackageNames", "getMusicModePackageNames", "insert", "isBlocked", "", "update", "app_debug"})
@androidx.room.Dao()
public abstract interface BlockedAppDao {
    
    @androidx.room.Query(value = "SELECT * FROM blocked_apps")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<com.blockingapp.data.BlockedApp>> getAllFlow();
    
    @androidx.room.Query(value = "SELECT * FROM blocked_apps")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAll(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.blockingapp.data.BlockedApp>> $completion);
    
    @androidx.room.Query(value = "SELECT packageName FROM blocked_apps")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllPackageNames(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
    
    /**
     * Packages that should trigger the full block screen (music-limited apps stay browsable).
     */
    @androidx.room.Query(value = "SELECT packageName FROM blocked_apps WHERE mode = \'BLOCK\'")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getBlockModePackageNames(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
    
    /**
     * Packages whose music playback is limited to the daily budget.
     */
    @androidx.room.Query(value = "SELECT packageName FROM blocked_apps WHERE mode = \'MUSIC\'")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMusicModePackageNames(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM blocked_apps WHERE packageName = :pkg LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object get(@org.jetbrains.annotations.NotNull()
    java.lang.String pkg, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.blockingapp.data.BlockedApp> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM blocked_apps WHERE packageName = :pkg")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object isBlocked(@org.jetbrains.annotations.NotNull()
    java.lang.String pkg, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
    
    @androidx.room.Insert(onConflict = 5)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insert(@org.jetbrains.annotations.NotNull()
    com.blockingapp.data.BlockedApp app, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object update(@org.jetbrains.annotations.NotNull()
    com.blockingapp.data.BlockedApp app, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object delete(@org.jetbrains.annotations.NotNull()
    com.blockingapp.data.BlockedApp app, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM blocked_apps WHERE pendingRemoval = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deletePendingRemovals(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}
package com.blockingapp.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bH\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0010\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u000bH\'J\u0010\u0010\f\u001a\u0004\u0018\u00010\u0005H\u00a7@\u00a2\u0006\u0002\u0010\tJ\u0016\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\bH\u00a7@\u00a2\u0006\u0002\u0010\u000f\u00a8\u0006\u0010"}, d2 = {"Lcom/blockingapp/data/DailyFocusStateDao;", "", "addMusicMs", "", "deltaMs", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "get", "Lcom/blockingapp/data/DailyFocusState;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getFlow", "Lkotlinx/coroutines/flow/Flow;", "getMusicMs", "upsert", "state", "(Lcom/blockingapp/data/DailyFocusState;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface DailyFocusStateDao {
    
    @androidx.room.Query(value = "SELECT * FROM daily_focus_state WHERE id = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object get(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.blockingapp.data.DailyFocusState> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM daily_focus_state WHERE id = 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<com.blockingapp.data.DailyFocusState> getFlow();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object upsert(@org.jetbrains.annotations.NotNull()
    com.blockingapp.data.DailyFocusState state, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    /**
     * Atomic increment so frequent music updates don't clobber the break columns.
     */
    @androidx.room.Query(value = "UPDATE daily_focus_state SET musicMsUsed = musicMsUsed + :deltaMs WHERE id = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addMusicMs(long deltaMs, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT musicMsUsed FROM daily_focus_state WHERE id = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMusicMs(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
}
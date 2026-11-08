.class public Lmonitor_test;
.super Ljava/lang/Object;

.method public static enter(Ljava/lang/Object;)V
    .registers 4

    monitor-enter p0
    return-void
.end method

.method public static exit(Ljava/lang/Object;)V
    .registers 3

    monitor-exit p0
    return-void
.end method

# The try covers only the const after the monitor. monitor-enter searches at its own address, outside
# this range, so a null receiver escapes. monitor-exit searches at the advanced pc and is caught.
.method public static enterBeforeSingleInstructionTry(Ljava/lang/Object;)I
    .registers 4

    monitor-enter p0
    :try_start
    const/4 v0, 0x1
    :try_end
    return v0

    :handler
    move-exception v0
    const/4 v0, 0x2
    return v0
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
.end method

.method public static exitBeforeSingleInstructionTry(Ljava/lang/Object;)I
    .registers 3

    monitor-exit p0
    :try_start
    const/4 v0, 0x1
    :try_end
    return v0

    :handler
    move-exception v0
    const/4 v0, 0x2
    return v0
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
.end method

# The monitor is the last instruction in the try. Its advanced exception pc is the exclusive end, so the
# local handler must not catch a null-receiver NPE.
.method public static exitAtTryRangeEnd(Ljava/lang/Object;)I
    .registers 3

    :try_start
    const/4 v0, 0x1
    monitor-exit p0
    :try_end
    return v0

    :handler
    move-exception v0
    const/4 v0, 0x2
    return v0
    .catchall {:try_start .. :try_end} :handler
.end method

# The assembler permits a terminal monitor-exit with no return. A definite-null path throws before it
# could fall off the method, so the scheduler must finish it as one escaping guest NPE.
.method public static terminalExitWithoutHandler(Ljava/lang/Object;)V
    .registers 1

    monitor-exit p0
.end method

# The payload begins at the monitor's advanced pc. Smali accepts an unreferenced payload in a try range;
# the null path never executes it, but handler search must still use its numeric address.
.method public static exitBeforePayload(Ljava/lang/Object;)I
    .registers 3

    nop
    monitor-exit p0
    :try_start
    :payload
    .array-data 1
        0x1t
    .end array-data
    :try_end
    const/4 v0, 0x1
    return v0

    :handler
    move-exception v0
    const/4 v0, 0x2
    return v0
    .catchall {:try_start .. :try_end} :handler
.end method

.method public static nestedExit(Ljava/lang/Object;)V
    .registers 1

    monitor-exit p0
    return-void
.end method

.method public static callNestedExit(Ljava/lang/Object;)I
    .registers 3

    :try_start
    invoke-static {p0}, Lmonitor_test;->nestedExit(Ljava/lang/Object;)V
    :try_end
    const/4 v0, 0x1
    return v0

    :handler
    move-exception v1
    const/4 v0, 0x2
    return v0
    .catchall {:try_start .. :try_end} :handler
.end method

# Two returning paths that share one array: `div-int` with an unknown divisor (a parameter) forks
# into the normal path and the throw, and the declared handler returns too.
#
# The array is allocated before the fork, so both paths carry the same HeapRef and consensus keeps it.
# Element 0 is written differently on the two arms (7 on the normal path, 9 in the handler) and element 1
# is left at its default on both, so the join must answer Unknown(I) for one index and Known(0, I) for
# the other. Anything that read a single path's heap would report a confident 7 or 9.
.class Lheap_consensus_test;
.super Ljava/lang/Object;

.method public static forkThenWriteDifferentElements(I)V
    .locals 5

    const/4 v1, 0x2
    new-array v0, v1, [I        # v0 = new int[2], allocated before the fork

    const/4 v1, 0x0             # index 0
    const/16 v3, 0xa            # dividend -- const/4 cannot encode 10 (range is -8..7)

    :try_start_0
    div-int v4, v3, p0          # p0 is an unknown parameter -> [Continue, Throw]
    :try_end_0
    .catch Ljava/lang/ArithmeticException; {:try_start_0 .. :try_end_0} :catch_0

    const/4 v2, 0x7
    aput v2, v0, v1             # normal path: v0[0] = 7
    return-void

    :catch_0
    move-exception v4
    const/16 v2, 0x9            # const/4 cannot encode 9 (range is -8..7)
    aput v2, v0, v1             # handler path: v0[0] = 9
    return-void
.end method

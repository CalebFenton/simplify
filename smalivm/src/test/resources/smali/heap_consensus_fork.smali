# Two-path fixtures for heap consensus: p0 is unknown at entry, so every method forks at its if-eqz.
.class Lheap_consensus_fork;
.super Ljava/lang/Object;

# Both arms return without touching the heap, so a seeded entry heap reaches two returning paths unchanged.
.method public static forkThenReturn(I)V
    .registers 1

    if-eqz p0, :zero
    return-void

    :zero
    return-void
.end method

# Only the nonzero arm allocates, so the zero arm's returning heap has no object at that ref.
.method public static allocateOnOnePath(I)Ljava/lang/Object;
    .registers 2

    if-eqz p0, :empty
    new-instance v0, Ljava/lang/Object;
    return-object v0

    :empty
    const/4 v0, 0x0
    return-object v0
.end method

# p1 is a reflected Field; only the nonzero arm makes it accessible, so the returning paths disagree on accessibility.
.method public static setAccessibleOnOnePath(ILjava/lang/reflect/Field;)V
    .registers 3

    if-eqz p0, :unchanged
    const/4 v0, 0x1
    invoke-virtual {p1, v0}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    :unchanged
    return-void
.end method

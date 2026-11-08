.class public Lreflected_member_lookups;
.super Ljava/lang/Object;

# One branch looks up String.<p1>, the other String.<p2>; both paths meet at the nop.
.method public static methodOnEitherBranch(ZLjava/lang/String;Ljava/lang/String;)V
    .locals 3

    const-class v1, Ljava/lang/String;
    const/4 v2, 0x0
    new-array v2, v2, [Ljava/lang/Class;
    if-eqz p0, :second

    invoke-virtual {v1, p1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    goto :join

    :second
    invoke-virtual {v1, p2, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0

    :join
    nop
    return-void
.end method

# One branch looks up DummyClass.<p1>, the other DummyClass.<p2>; both paths meet at the nop.
.method public static fieldOnEitherBranch(ZLjava/lang/String;Ljava/lang/String;)V
    .locals 2

    const-class v1, Lorg/cf/smalivm/DummyClass;
    if-eqz p0, :second

    invoke-virtual {v1, p1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    goto :join

    :second
    invoke-virtual {v1, p2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0

    :join
    nop
    return-void
.end method

# Only the fall-through branch puts a member in v0 (a method) and v1 (a field); the other leaves both unassigned.
.method public static membersOnOneBranch(Z)V
    .locals 4

    if-eqz p0, :join

    const-class v0, Ljava/lang/String;
    const-string v2, "length"
    const/4 v3, 0x0
    new-array v3, v3, [Ljava/lang/Class;
    invoke-virtual {v0, v2, v3}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0

    const-class v1, Lorg/cf/smalivm/DummyClass;
    const-string v2, "publicInstanceField"
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v1

    :join
    nop
    return-void
.end method

# v0 holds a Method on one path and a Field on the other when the paths meet at the nop.
.method public static methodOrField(Z)V
    .locals 3

    if-eqz p0, :field

    const-class v1, Ljava/lang/String;
    const-string v2, "length"
    const/4 v0, 0x0
    new-array v0, v0, [Ljava/lang/Class;
    invoke-virtual {v1, v2, v0}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    goto :join

    :field
    const-class v1, Lorg/cf/smalivm/DummyClass;
    const-string v2, "publicInstanceField"
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0

    :join
    nop
    return-void
.end method

# The first visit to :loop holds String.length in v0; the loop's revisit holds String.isEmpty.
.method public static methodChangesOnLoopRevisit()V
    .locals 3

    const-class v1, Ljava/lang/String;
    const/4 v2, 0x0
    new-array v2, v2, [Ljava/lang/Class;
    const-string v0, "length"
    invoke-virtual {v1, v0, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0

    :loop
    nop
    const-string v0, "isEmpty"
    invoke-virtual {v1, v0, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    goto :loop
.end method

# Each branch allocates an array whose length is the matching parameter; both paths meet at the nop.
.method public static arrayOnEitherBranch(ZII)V
    .locals 1

    if-eqz p0, :second
    new-array v0, p1, [Ljava/lang/Object;
    goto :join

    :second
    new-array v0, p2, [Ljava/lang/Object;

    :join
    nop
    return-void
.end method

# A nonzero p0 looks up String.length and DummyClass.publicInstanceField, then recurses once with zero,
# whose activation looks up String.isEmpty and DummyClass.protectedInstanceField at the same addresses.
.method public static recursiveLookup(I)V
    .locals 4

    const-class v0, Ljava/lang/String;
    const-class v3, Lorg/cf/smalivm/DummyClass;
    if-eqz p0, :inner
    const-string v1, "length"
    const-string v2, "publicInstanceField"
    goto :lookup

    :inner
    const-string v1, "isEmpty"
    const-string v2, "protectedInstanceField"

    :lookup
    invoke-virtual {v3, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v3
    const/4 v2, 0x0
    new-array v2, v2, [Ljava/lang/Class;
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    move-result-object v0
    if-eqz p0, :done
    const/4 v1, 0x0
    invoke-static {v1}, Lreflected_member_lookups;->recursiveLookup(I)V

    :done
    return-void
.end method

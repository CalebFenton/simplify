.class Linvoke_static_test;
.super Ljava/lang/Object;

.field public static mutable:[I
.field public static not_initialized:Ljava/lang/String;
.field public static sometimes_initialized:Ljava/lang/String;

.method public static InvokeReturnVoid()V
    .locals 0

    invoke-static {}, Linvoke_static_test;->ReturnVoid()V

    return-void
.end method

.method public static InvokeReturnInt()V
    .locals 0

    invoke-static {}, Linvoke_static_test;->ReturnInt()I

    return-void
.end method

.method public static InvokeReturnParameter()V
    .locals 1

    invoke-static {v0}, Linvoke_static_test;->ReturnParameter(I)I

    return-void
.end method

.method public static InvokeMutateString()V
    .locals 1

    invoke-static {v0}, Linvoke_static_test;->MutateString(Ljava/lang/String;)V

    return-void
.end method

.method public static InvokeMutateStringBuilder()V
    .locals 1

    invoke-static {v0}, Linvoke_static_test;->MutateStringBuilder(Ljava/lang/StringBuilder;)V

    return-void
.end method

.method public static InvokeNonLocalMethodWithKnownAndUnknownMutableParameters()V
    .locals 1

    invoke-static {v0, v1}, Lim_not_your_friend_buddy;->Im_not_your_buddy_guy([I[I)V

    return-void
.end method


.method public static InvokeSet0thElementOfFirstParameterTo0IfSecondParameterIs0()V
    .locals 1

    invoke-static {v0, v1}, Linvoke_static_test;->Set0thElementOfFirstParameterTo0IfSecondParameterIs0([II)V

    return-void
.end method

.method public InvokeMutateStaticClassField()V
    .locals 1

    invoke-static {}, Linvoke_static_test;->MutateStaticClassField()V

    return-void
.end method

.method public InvokeMutateStaticClassFieldNonDeterministically()V
    .locals 1

    invoke-static {}, Linvoke_static_test;->MutateStaticClassFieldNonDeterministically()V

    return-void
.end method
# Need proper error handling, and to test private and instance from static
#.method public static TestPrivateMethodInaccessible()V
#.end method

#.method public static TestUnknownMethodMutableAndImmutableParametersMutateOnlyMutableAndReturnUnknownValue()V
#.end method

.method public static NonDeterministicallyInitializeClassWithStaticInit()V
    .locals 1

    sget v0, Lsome_unexistant_class;->fieldy:I
    if-eqz v0, :was_zero

    invoke-static {}, Lclass_with_static_init;->getString()Ljava/lang/String;
    move-result v0

    :was_zero
    return-void
.end method

.method public static MutateStaticClassField()V
    .locals 2

    sget-object v0, Linvoke_static_test;->mutable:[I

    const/4 v1, 0x0
    aput v1, v0, v1

    return-void
.end method

.method public static InvokeReturnUninitializedField()V
    .locals 0

    invoke-static {}, Linvoke_static_test;->ReturnUninitializedField()Ljava/lang/String;

    return-void
.end method

.method public static InvokeMethodOutsideClassThatAccessesThisClass()V
    .locals 1

    invoke-static {}, Lclass_with_static_init;->getStaticFieldFromInvokeStaticTestClass()Ljava/lang/String;

    return-void
.end method

.method public static MutateStaticClassFieldNonDeterministically()V
    .locals 2

    sget-object v0, Linvoke_static_test;->mutable:[I

    sget v1, Lsome_unexistant_class;->fieldy:I
    if-eqz v1, :was_zero

    const/4 v1, 0x1
    aput v1, v0, v1

    :was_zero
    const/4 v1, 0x0
    aput v1, v0, v1

    return-void
.end method




.method private static ReturnUninitializedField()Ljava/lang/String;
    .locals 1

    sget-object v0, Linvoke_static_test;->not_initialized:Ljava/lang/String;
    return v0

    return-void
.end method

.method private static ReturnVoid()V
    .locals 0

    return-void
.end method

.method private static ReturnInt()I
    .locals 1

    const/4 v0, 0x7

    return v0
.end method

.method private static ReturnParameter(I)I
    .locals 1

    move v0, p0
    const-string p0, "can't trust p0"

    return v0
.end method

.method private static MutateString(Ljava/lang/String;)V
    .locals 0

    const-string p0, "mutated"

    return-void
.end method

.method private static MutateStringBuilder(Ljava/lang/StringBuilder;)V
    .locals 1

    const-string v0, " mutated"
    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    const/4 p0, 0x1 # ensure this works if the register is blasted away

    return-void
.end method

# test assumemaximum unknown
.method private static Set0thElementOfFirstParameterTo0IfSecondParameterIs0([II)V
    .locals 1

    const v0, 0x0
    if-eq p1, v0, :eq_0
    goto :end

    :eq_0
    aput v0, p0, v0
    move v0, p0
    # wipe out p0 value to make sure we don't care about register index
    new-instance p0, Ljava/lang/StringBuilder;

    :end
    return-void
.end method

# Test case would have p0 == p1
.method private static Set0thElementOfFirstParameterTo0IfThirdParameterIs0([I[II)V
    .locals 1

    const v0, 0x0
    if-eq p2, v0, :eq_0
    goto :end

    :eq_0
    aput v0, p0, v0
    const/4 p0, 0x1

    :end
    return-void
.end method

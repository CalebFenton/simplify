.class public Lbackend/class_for_name_test;
.super Ljava/lang/Object;

.field public static guestInitCount:I
.field public static sentinel:I

.method static constructor <clinit>()V
    .locals 1

    sget v0, Lbackend/class_for_name_test;->guestInitCount:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Lbackend/class_for_name_test;->guestInitCount:I

    const/16 v0, 0x29
    sput v0, Lbackend/class_for_name_test;->sentinel:I

    invoke-static {}, Lbackend/class_for_name_test;->strongEffect()V
    return-void
.end method

.method public static native strongEffect()V
.end method

.method public static knownLocalClassLoadsWithoutHostInitialization()Ljava/lang/Class;
    .locals 1

    const-string v0, "org.cf.smalivm.core.ClassForNameHostInitProbe"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0
    return-object v0
.end method

.method public static guestInitializationIsVisibleThenRetryReturnsStableClass()Ljava/lang/Class;
    .locals 1

    const-string v0, "backend.class_for_name_test"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0
    return-object v0
.end method

.method public static sourceInvalidLocalBinaryNameLoads()Ljava/lang/Class;
    .locals 1

    const-string v0, "backend.1-obfuscated"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0
    return-object v0
.end method

.method public static guestInitializerEffectSurvivesRetry()I
    .locals 1

    const-string v0, "backend.class_for_name_test"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0
    sget v0, Lbackend/class_for_name_test;->sentinel:I
    return v0
.end method

.method public static missingClassRoutesGuestClassNotFoundException()Ljava/lang/Class;
    .locals 1

    const-string v0, "backend.class_that_does_not_exist"
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object v0
    return-object v0
.end method

.method public static unknownNameReturnsUnknownClass(Ljava/lang/String;)Ljava/lang/Class;
    .locals 0

    invoke-static {p0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    move-result-object p0
    return-object p0
.end method

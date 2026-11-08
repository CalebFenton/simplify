.class public Lentry_class_state/holder;
.super Ljava/lang/Object;

# <clinit> leaves mutable = 1 and computedFinal = 5; literalFinal is encoded as 42.
.field static mutable:I
.field static final computedFinal:I
.field static final literalFinal:I = 0x2a

.method static constructor <clinit>()V
    .registers 1
    const/4 v0, 0x1
    sput v0, Lentry_class_state/holder;->mutable:I
    const/4 v0, 0x5
    sput v0, Lentry_class_state/holder;->computedFinal:I
    return-void
.end method

# 20 when mutable is non-zero, 10 when it is zero.
.method public static branchOnMutable()I
    .registers 1
    sget v0, Lentry_class_state/holder;->mutable:I
    if-eqz v0, :zero
    const/16 v0, 0x14
    return v0
    :zero
    const/16 v0, 0xa
    return v0
.end method

# computedFinal + literalFinal.
.method public static sumOfFinals()I
    .registers 2
    sget v0, Lentry_class_state/holder;->computedFinal:I
    sget v1, Lentry_class_state/holder;->literalFinal:I
    add-int/2addr v0, v1
    return v0
.end method

.method public static writeThenRead()I
    .registers 1
    const/16 v0, 0x9
    sput v0, Lentry_class_state/holder;->mutable:I
    sget v0, Lentry_class_state/holder;->mutable:I
    return v0
.end method

# Writes mutable = 9, then triggers writer, whose <clinit> writes mutable = 3 if it has not run before.
.method public static writeThenTriggerAnotherInitializer()I
    .registers 1
    const/16 v0, 0x9
    sput v0, Lentry_class_state/holder;->mutable:I
    invoke-static {}, Lentry_class_state/writer;->touch()V
    sget v0, Lentry_class_state/holder;->mutable:I
    return v0
.end method

# A non-final framework static whose declaration encodes the literal 3.
.method public static frameworkMutableLiteral()I
    .registers 1
    sget v0, Lcom/android/internal/telephony/GsmCdmaPhone;->ENABLE_UICC_APPS_MAX_RETRIES:I
    return v0
.end method

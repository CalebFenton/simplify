.class public final Llibcore/math/MathUtils;
.super Ljava/lang/Object;
.source "MathUtils.java"


# static fields
.field public static final LONG_POWERS_OF_TEN:[J


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 26
    const/16 v0, 0x13

    new-array v0, v0, [J

    fill-array-data v0, :array_a

    sput-object v0, Llibcore/math/MathUtils;->LONG_POWERS_OF_TEN:[J

    return-void

    :array_a
    .array-data 8
        0x1
        0xa
        0x64
        0x3e8
        0x2710
        0x186a0
        0xf4240
        0x989680
        0x5f5e100
        0x3b9aca00
        0x2540be400L
        0x174876e800L
        0xe8d4a51000L
        0x9184e72a000L
        0x5af3107a4000L
        0x38d7ea4c68000L
        0x2386f26fc10000L
        0x16345785d8a0000L
        0xde0b6b3a7640000L
    .end array-data
.end method

.method private constructor <init>()V
    .registers 1

    .prologue
    .line 20
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.class final Landroid/support/v4/widget/BakedBezierInterpolator;
.super Ljava/lang/Object;
.source "BakedBezierInterpolator.java"

# interfaces
.implements Landroid/view/animation/Interpolator;


# static fields
.field private static final INSTANCE:Landroid/support/v4/widget/BakedBezierInterpolator;

.field private static final STEP_SIZE:F

.field private static final VALUES:[F


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 25
    new-instance v0, Landroid/support/v4/widget/BakedBezierInterpolator;

    #@2
    invoke-direct {v0}, Landroid/support/v4/widget/BakedBezierInterpolator;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/widget/BakedBezierInterpolator;->INSTANCE:Landroid/support/v4/widget/BakedBezierInterpolator;

    #@7
    .line 48
    const/16 v0, 0x65

    #@9
    new-array v0, v0, [F

    #@b
    fill-array-data v0, :array_0

    #@e
    sput-object v0, Landroid/support/v4/widget/BakedBezierInterpolator;->VALUES:[F

    #@10
    .line 61
    const/high16 v0, 0x3f800000    # 1.0f

    #@12
    sget-object v1, Landroid/support/v4/widget/BakedBezierInterpolator;->VALUES:[F

    #@14
    array-length v1, v1

    #@15
    add-int/lit8 v1, v1, -0x1

    #@17
    int-to-float v1, v1

    #@18
    div-float/2addr v0, v1

    #@19
    sput v0, Landroid/support/v4/widget/BakedBezierInterpolator;->STEP_SIZE:F

    #@1b
    return-void

    #@1c
    .line 48
    :array_0
    .array-data 4
        0x0
        0x3951b717    # 2.0E-4f
        0x3a6bedfa    # 9.0E-4f
        0x3af9096c    # 0.0019f
        0x3b6bedfa    # 0.0036f
        0x3bc154ca    # 0.0059f
        0x3c0ce704    # 0.0086f
        0x3c42f838    # 0.0119f
        0x3c809d49    # 0.0157f
        0x3cab367a    # 0.0209f
        0x3cd288ce    # 0.0257f
        0x3d037b4a
        0x3d20902e    # 0.0392f
        0x3d401a37    # 0.0469f
        0x3d67d567    # 0.0566f
        0x3d86594b    # 0.0656f
        0x3d9d4952    # 0.0768f
        0x3db5a858    # 0.0887f
        0x3dd38ef3    # 0.1033f
        0x3df2e48f    # 0.1186f
        0x3e0a233a    # 0.1349f
        0x3e1b8bac    # 0.1519f
        0x3e2dab9f    # 0.1696f
        0x3e456d5d    # 0.1928f
        0x3e5930be    # 0.2121f
        0x3e72b021    # 0.237f
        0x3e86809d    # 0.2627f
        0x3e941206    # 0.2892f
        0x3e9f2e49    # 0.3109f
        0x3ead5cfb    # 0.3386f
        0x3ebbc01a    # 0.3667f
        0x3eca57a8    # 0.3952f
        0x3ed923a3    # 0.4241f
        0x3ee5119d    # 0.4474f
        0x3ef404ea    # 0.4766f
        0x3f000000    # 0.5f
        0x3f05fd8b    # 0.5234f
        0x3f0bfb16    # 0.5468f
        0x3f11f213    # 0.5701f
        0x3f17e282    # 0.5933f
        0x3f1d07c8    # 0.6134f
        0x3f221ff3    # 0.6333f
        0x3f273190    # 0.6531f
        0x3f2b7803    # 0.6698f
        0x3f3068dc    # 0.6891f
        0x3f349518    # 0.7054f
        0x3f38adac    # 0.7214f
        0x3f3c0ebf    # 0.7346f
        0x3f400d1b    # 0.7502f
        0x3f4353f8    # 0.763f
        0x3f468db9    # 0.7756f
        0x3f49b3d0    # 0.7879f
        0x3f4ccccd    # 0.8f
        0x3f4f8a09    # 0.8107f
        0x3f523a2a    # 0.8212f
        0x3f552546    # 0.8326f
        0x3f576c8b    # 0.8415f
        0x3f59ad43    # 0.8503f
        0x3f5bda51    # 0.8588f
        0x3f5e00d2    # 0.8672f
        0x3f601a37    # 0.8754f
        0x3f621ff3    # 0.8833f
        0x3f641f21    # 0.8911f
        0x3f65cfab    # 0.8977f
        0x3f677319    # 0.9041f
        0x3f694af5    # 0.9113f
        0x3f6a9fbe    # 0.9165f
        0x3f6c56d6    # 0.9232f
        0x3f6d97f6    # 0.9281f
        0x3f6ecbfb    # 0.9328f
        0x3f702de0    # 0.9382f
        0x3f7182aa    # 0.9434f
        0x3f7295ea    # 0.9476f
        0x3f73a92a    # 0.9518f
        0x3f74a8c1    # 0.9557f
        0x3f75a858    # 0.9596f
        0x3f769446    # 0.9632f
        0x3f7758e2    # 0.9662f
        0x3f783127    # 0.9695f
        0x3f78e219    # 0.9722f
        0x3f79ad43    # 0.9753f
        0x3f7a4a8c    # 0.9777f
        0x3f7b020c    # 0.9805f
        0x3f7b8bac    # 0.9826f
        0x3f7c154d    # 0.9847f
        0x3f7c91d1    # 0.9866f
        0x3f7d07c8    # 0.9884f
        0x3f7d7732    # 0.9901f
        0x3f7de00d    # 0.9917f
        0x3f7e3bcd    # 0.9931f
        0x3f7e9100    # 0.9944f
        0x3f7ed917    # 0.9955f
        0x3f7f1412    # 0.9964f
        0x3f7f4f0e    # 0.9973f
        0x3f7f837b    # 0.9981f
        0x3f7fa440    # 0.9986f
        0x3f7fcb92    # 0.9992f
        0x3f7fdf3b    # 0.9995f
        0x3f7ff2e5    # 0.9998f
        0x3f800000    # 1.0f
        0x3f800000    # 1.0f
    .end array-data
.end method

.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 35
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 36
    return-void
.end method

.method public static final getInstance()Landroid/support/v4/widget/BakedBezierInterpolator;
    .locals 1

    #@0
    .prologue
    .line 28
    sget-object v0, Landroid/support/v4/widget/BakedBezierInterpolator;->INSTANCE:Landroid/support/v4/widget/BakedBezierInterpolator;

    #@2
    return-object v0
.end method


# virtual methods
.method public getInterpolation(F)F
    .locals 7
    .param p1, "input"    # F

    #@0
    .prologue
    const/high16 v4, 0x3f800000    # 1.0f

    #@2
    const/4 v5, 0x0

    #@3
    .line 65
    cmpl-float v6, p1, v4

    #@5
    if-ltz v6, :cond_0

    #@7
    .line 81
    :goto_0
    return v4

    #@8
    .line 69
    :cond_0
    cmpg-float v4, p1, v5

    #@a
    if-gtz v4, :cond_1

    #@c
    move v4, v5

    #@d
    .line 70
    goto :goto_0

    #@e
    .line 73
    :cond_1
    sget-object v4, Landroid/support/v4/widget/BakedBezierInterpolator;->VALUES:[F

    #@10
    array-length v4, v4

    #@11
    add-int/lit8 v4, v4, -0x1

    #@13
    int-to-float v4, v4

    #@14
    mul-float/2addr v4, p1

    #@15
    float-to-int v4, v4

    #@16
    sget-object v5, Landroid/support/v4/widget/BakedBezierInterpolator;->VALUES:[F

    #@18
    array-length v5, v5

    #@19
    add-int/lit8 v5, v5, -0x2

    #@1b
    invoke-static {v4, v5}, Ljava/lang/Math;->min(II)I

    #@1e
    move-result v1

    #@1f
    .line 77
    .local v1, "position":I
    int-to-float v4, v1

    #@20
    sget v5, Landroid/support/v4/widget/BakedBezierInterpolator;->STEP_SIZE:F

    #@22
    mul-float v2, v4, v5

    #@24
    .line 78
    .local v2, "quantized":F
    sub-float v0, p1, v2

    #@26
    .line 79
    .local v0, "difference":F
    sget v4, Landroid/support/v4/widget/BakedBezierInterpolator;->STEP_SIZE:F

    #@28
    div-float v3, v0, v4

    #@2a
    .line 81
    .local v3, "weight":F
    sget-object v4, Landroid/support/v4/widget/BakedBezierInterpolator;->VALUES:[F

    #@2c
    aget v4, v4, v1

    #@2e
    sget-object v5, Landroid/support/v4/widget/BakedBezierInterpolator;->VALUES:[F

    #@30
    add-int/lit8 v6, v1, 0x1

    #@32
    aget v5, v5, v6

    #@34
    sget-object v6, Landroid/support/v4/widget/BakedBezierInterpolator;->VALUES:[F

    #@36
    aget v6, v6, v1

    #@38
    sub-float/2addr v5, v6

    #@39
    mul-float/2addr v5, v3

    #@3a
    add-float/2addr v4, v5

    #@3b
    goto :goto_0
.end method

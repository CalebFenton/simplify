.class public Landroid/support/v4/graphics/BitmapCompat;
.super Ljava/lang/Object;
.source "BitmapCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/graphics/BitmapCompat$KitKatBitmapCompatImpl;,
        Landroid/support/v4/graphics/BitmapCompat$JbMr2BitmapCompatImpl;,
        Landroid/support/v4/graphics/BitmapCompat$HcMr1BitmapCompatImpl;,
        Landroid/support/v4/graphics/BitmapCompat$BaseBitmapImpl;,
        Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;
    }
.end annotation


# static fields
.field static final IMPL:Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 81
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 82
    .local v0, "version":I
    const/16 v1, 0x13

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 83
    new-instance v1, Landroid/support/v4/graphics/BitmapCompat$KitKatBitmapCompatImpl;

    #@8
    invoke-direct {v1}, Landroid/support/v4/graphics/BitmapCompat$KitKatBitmapCompatImpl;-><init>()V

    #@b
    sput-object v1, Landroid/support/v4/graphics/BitmapCompat;->IMPL:Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;

    #@d
    .line 91
    :goto_0
    return-void

    #@e
    .line 84
    :cond_0
    const/16 v1, 0x12

    #@10
    if-lt v0, v1, :cond_1

    #@12
    .line 85
    new-instance v1, Landroid/support/v4/graphics/BitmapCompat$JbMr2BitmapCompatImpl;

    #@14
    invoke-direct {v1}, Landroid/support/v4/graphics/BitmapCompat$JbMr2BitmapCompatImpl;-><init>()V

    #@17
    sput-object v1, Landroid/support/v4/graphics/BitmapCompat;->IMPL:Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;

    #@19
    goto :goto_0

    #@1a
    .line 86
    :cond_1
    const/16 v1, 0xc

    #@1c
    if-lt v0, v1, :cond_2

    #@1e
    .line 87
    new-instance v1, Landroid/support/v4/graphics/BitmapCompat$HcMr1BitmapCompatImpl;

    #@20
    invoke-direct {v1}, Landroid/support/v4/graphics/BitmapCompat$HcMr1BitmapCompatImpl;-><init>()V

    #@23
    sput-object v1, Landroid/support/v4/graphics/BitmapCompat;->IMPL:Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;

    #@25
    goto :goto_0

    #@26
    .line 89
    :cond_2
    new-instance v1, Landroid/support/v4/graphics/BitmapCompat$BaseBitmapImpl;

    #@28
    invoke-direct {v1}, Landroid/support/v4/graphics/BitmapCompat$BaseBitmapImpl;-><init>()V

    #@2b
    sput-object v1, Landroid/support/v4/graphics/BitmapCompat;->IMPL:Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;

    #@2d
    goto :goto_0
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 69
    return-void
.end method

.method public static getAllocationByteCount(Landroid/graphics/Bitmap;)I
    .locals 1
    .param p0, "bitmap"    # Landroid/graphics/Bitmap;

    #@0
    .prologue
    .line 109
    sget-object v0, Landroid/support/v4/graphics/BitmapCompat;->IMPL:Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;->getAllocationByteCount(Landroid/graphics/Bitmap;)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static hasMipMap(Landroid/graphics/Bitmap;)Z
    .locals 1
    .param p0, "bitmap"    # Landroid/graphics/Bitmap;

    #@0
    .prologue
    .line 94
    sget-object v0, Landroid/support/v4/graphics/BitmapCompat;->IMPL:Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;->hasMipMap(Landroid/graphics/Bitmap;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static setHasMipMap(Landroid/graphics/Bitmap;Z)V
    .locals 1
    .param p0, "bitmap"    # Landroid/graphics/Bitmap;
    .param p1, "hasMipMap"    # Z

    #@0
    .prologue
    .line 98
    sget-object v0, Landroid/support/v4/graphics/BitmapCompat;->IMPL:Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;

    #@2
    invoke-interface {v0, p0, p1}, Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;->setHasMipMap(Landroid/graphics/Bitmap;Z)V

    #@5
    .line 99
    return-void
.end method

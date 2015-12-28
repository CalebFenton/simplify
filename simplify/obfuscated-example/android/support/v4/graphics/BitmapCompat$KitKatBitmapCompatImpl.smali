.class Landroid/support/v4/graphics/BitmapCompat$KitKatBitmapCompatImpl;
.super Landroid/support/v4/graphics/BitmapCompat$JbMr2BitmapCompatImpl;
.source "BitmapCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/graphics/BitmapCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "KitKatBitmapCompatImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 69
    invoke-direct {p0}, Landroid/support/v4/graphics/BitmapCompat$JbMr2BitmapCompatImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getAllocationByteCount(Landroid/graphics/Bitmap;)I
    .locals 1
    .param p1, "bitmap"    # Landroid/graphics/Bitmap;

    #@0
    .prologue
    .line 72
    invoke-static {p1}, Landroid/support/v4/graphics/BitmapCompatKitKat;->getAllocationByteCount(Landroid/graphics/Bitmap;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

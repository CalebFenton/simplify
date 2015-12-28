.class Landroid/support/v4/graphics/BitmapCompat$JbMr2BitmapCompatImpl;
.super Landroid/support/v4/graphics/BitmapCompat$HcMr1BitmapCompatImpl;
.source "BitmapCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/graphics/BitmapCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "JbMr2BitmapCompatImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 57
    invoke-direct {p0}, Landroid/support/v4/graphics/BitmapCompat$HcMr1BitmapCompatImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public hasMipMap(Landroid/graphics/Bitmap;)Z
    .locals 1
    .param p1, "bitmap"    # Landroid/graphics/Bitmap;

    #@0
    .prologue
    .line 60
    invoke-static {p1}, Landroid/support/v4/graphics/BitmapCompatJellybeanMR2;->hasMipMap(Landroid/graphics/Bitmap;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public setHasMipMap(Landroid/graphics/Bitmap;Z)V
    .locals 0
    .param p1, "bitmap"    # Landroid/graphics/Bitmap;
    .param p2, "hasMipMap"    # Z

    #@0
    .prologue
    .line 65
    invoke-static {p1, p2}, Landroid/support/v4/graphics/BitmapCompatJellybeanMR2;->setHasMipMap(Landroid/graphics/Bitmap;Z)V

    #@3
    .line 66
    return-void
.end method

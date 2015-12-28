.class Landroid/support/v4/graphics/BitmapCompat$BaseBitmapImpl;
.super Ljava/lang/Object;
.source "BitmapCompat.java"

# interfaces
.implements Landroid/support/v4/graphics/BitmapCompat$BitmapImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/graphics/BitmapCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "BaseBitmapImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 34
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getAllocationByteCount(Landroid/graphics/Bitmap;)I
    .locals 2
    .param p1, "bitmap"    # Landroid/graphics/Bitmap;

    #@0
    .prologue
    .line 46
    invoke-virtual {p1}, Landroid/graphics/Bitmap;->getRowBytes()I

    #@3
    move-result v0

    #@4
    invoke-virtual {p1}, Landroid/graphics/Bitmap;->getHeight()I

    #@7
    move-result v1

    #@8
    mul-int/2addr v0, v1

    #@9
    return v0
.end method

.method public hasMipMap(Landroid/graphics/Bitmap;)Z
    .locals 1
    .param p1, "bitmap"    # Landroid/graphics/Bitmap;

    #@0
    .prologue
    .line 37
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public setHasMipMap(Landroid/graphics/Bitmap;Z)V
    .locals 0
    .param p1, "bitmap"    # Landroid/graphics/Bitmap;
    .param p2, "hasMipMap"    # Z

    #@0
    .prologue
    .line 42
    return-void
.end method

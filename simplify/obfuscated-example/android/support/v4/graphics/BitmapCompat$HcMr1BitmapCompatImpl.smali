.class Landroid/support/v4/graphics/BitmapCompat$HcMr1BitmapCompatImpl;
.super Landroid/support/v4/graphics/BitmapCompat$BaseBitmapImpl;
.source "BitmapCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/graphics/BitmapCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "HcMr1BitmapCompatImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 50
    invoke-direct {p0}, Landroid/support/v4/graphics/BitmapCompat$BaseBitmapImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getAllocationByteCount(Landroid/graphics/Bitmap;)I
    .locals 1
    .param p1, "bitmap"    # Landroid/graphics/Bitmap;

    #@0
    .prologue
    .line 53
    invoke-static {p1}, Landroid/support/v4/graphics/BitmapCompatHoneycombMr1;->getAllocationByteCount(Landroid/graphics/Bitmap;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

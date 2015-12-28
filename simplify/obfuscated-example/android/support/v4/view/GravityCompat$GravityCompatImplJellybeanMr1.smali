.class Landroid/support/v4/view/GravityCompat$GravityCompatImplJellybeanMr1;
.super Ljava/lang/Object;
.source "GravityCompat.java"

# interfaces
.implements Landroid/support/v4/view/GravityCompat$GravityCompatImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/GravityCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "GravityCompatImplJellybeanMr1"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 62
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public apply(IIILandroid/graphics/Rect;IILandroid/graphics/Rect;I)V
    .locals 0
    .param p1, "gravity"    # I
    .param p2, "w"    # I
    .param p3, "h"    # I
    .param p4, "container"    # Landroid/graphics/Rect;
    .param p5, "xAdj"    # I
    .param p6, "yAdj"    # I
    .param p7, "outRect"    # Landroid/graphics/Rect;
    .param p8, "layoutDirection"    # I

    #@0
    .prologue
    .line 77
    invoke-static/range {p1 .. p8}, Landroid/support/v4/view/GravityCompatJellybeanMr1;->apply(IIILandroid/graphics/Rect;IILandroid/graphics/Rect;I)V

    #@3
    .line 79
    return-void
.end method

.method public apply(IIILandroid/graphics/Rect;Landroid/graphics/Rect;I)V
    .locals 0
    .param p1, "gravity"    # I
    .param p2, "w"    # I
    .param p3, "h"    # I
    .param p4, "container"    # Landroid/graphics/Rect;
    .param p5, "outRect"    # Landroid/graphics/Rect;
    .param p6, "layoutDirection"    # I

    #@0
    .prologue
    .line 71
    invoke-static/range {p1 .. p6}, Landroid/support/v4/view/GravityCompatJellybeanMr1;->apply(IIILandroid/graphics/Rect;Landroid/graphics/Rect;I)V

    #@3
    .line 72
    return-void
.end method

.method public applyDisplay(ILandroid/graphics/Rect;Landroid/graphics/Rect;I)V
    .locals 0
    .param p1, "gravity"    # I
    .param p2, "display"    # Landroid/graphics/Rect;
    .param p3, "inoutObj"    # Landroid/graphics/Rect;
    .param p4, "layoutDirection"    # I

    #@0
    .prologue
    .line 83
    invoke-static {p1, p2, p3, p4}, Landroid/support/v4/view/GravityCompatJellybeanMr1;->applyDisplay(ILandroid/graphics/Rect;Landroid/graphics/Rect;I)V

    #@3
    .line 84
    return-void
.end method

.method public getAbsoluteGravity(II)I
    .locals 1
    .param p1, "gravity"    # I
    .param p2, "layoutDirection"    # I

    #@0
    .prologue
    .line 65
    invoke-static {p1, p2}, Landroid/support/v4/view/GravityCompatJellybeanMr1;->getAbsoluteGravity(II)I

    #@3
    move-result v0

    #@4
    return v0
.end method

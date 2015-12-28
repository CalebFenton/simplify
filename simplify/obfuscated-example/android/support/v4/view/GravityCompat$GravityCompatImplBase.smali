.class Landroid/support/v4/view/GravityCompat$GravityCompatImplBase;
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
    name = "GravityCompatImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 36
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
    .line 52
    invoke-static/range {p1 .. p7}, Landroid/view/Gravity;->apply(IIILandroid/graphics/Rect;IILandroid/graphics/Rect;)V

    #@3
    .line 53
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
    .line 46
    invoke-static {p1, p2, p3, p4, p5}, Landroid/view/Gravity;->apply(IIILandroid/graphics/Rect;Landroid/graphics/Rect;)V

    #@3
    .line 47
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
    .line 58
    invoke-static {p1, p2, p3}, Landroid/view/Gravity;->applyDisplay(ILandroid/graphics/Rect;Landroid/graphics/Rect;)V

    #@3
    .line 59
    return-void
.end method

.method public getAbsoluteGravity(II)I
    .locals 1
    .param p1, "gravity"    # I
    .param p2, "layoutDirection"    # I

    #@0
    .prologue
    .line 40
    const v0, -0x800001

    #@3
    and-int/2addr v0, p1

    #@4
    return v0
.end method

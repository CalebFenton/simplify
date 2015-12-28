.class Landroid/support/v4/view/MotionEventCompatEclair;
.super Ljava/lang/Object;
.source "MotionEventCompatEclair.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static findPointerIndex(Landroid/view/MotionEvent;I)I
    .locals 1
    .param p0, "event"    # Landroid/view/MotionEvent;
    .param p1, "pointerId"    # I

    #@0
    .prologue
    .line 26
    invoke-virtual {p0, p1}, Landroid/view/MotionEvent;->findPointerIndex(I)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getPointerCount(Landroid/view/MotionEvent;)I
    .locals 1
    .param p0, "event"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 38
    invoke-virtual {p0}, Landroid/view/MotionEvent;->getPointerCount()I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getPointerId(Landroid/view/MotionEvent;I)I
    .locals 1
    .param p0, "event"    # Landroid/view/MotionEvent;
    .param p1, "pointerIndex"    # I

    #@0
    .prologue
    .line 29
    invoke-virtual {p0, p1}, Landroid/view/MotionEvent;->getPointerId(I)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getX(Landroid/view/MotionEvent;I)F
    .locals 1
    .param p0, "event"    # Landroid/view/MotionEvent;
    .param p1, "pointerIndex"    # I

    #@0
    .prologue
    .line 32
    invoke-virtual {p0, p1}, Landroid/view/MotionEvent;->getX(I)F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static getY(Landroid/view/MotionEvent;I)F
    .locals 1
    .param p0, "event"    # Landroid/view/MotionEvent;
    .param p1, "pointerIndex"    # I

    #@0
    .prologue
    .line 35
    invoke-virtual {p0, p1}, Landroid/view/MotionEvent;->getY(I)F

    #@3
    move-result v0

    #@4
    return v0
.end method

.class Landroid/support/v4/view/MotionEventCompat$BaseMotionEventVersionImpl;
.super Ljava/lang/Object;
.source "MotionEventCompat.java"

# interfaces
.implements Landroid/support/v4/view/MotionEventCompat$MotionEventVersionImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/MotionEventCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "BaseMotionEventVersionImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 40
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public findPointerIndex(Landroid/view/MotionEvent;I)I
    .locals 1
    .param p1, "event"    # Landroid/view/MotionEvent;
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 43
    if-nez p2, :cond_0

    #@2
    .line 45
    const/4 v0, 0x0

    #@3
    .line 47
    :goto_0
    return v0

    #@4
    :cond_0
    const/4 v0, -0x1

    #@5
    goto :goto_0
.end method

.method public getPointerCount(Landroid/view/MotionEvent;)I
    .locals 1
    .param p1, "event"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    .line 73
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method public getPointerId(Landroid/view/MotionEvent;I)I
    .locals 2
    .param p1, "event"    # Landroid/view/MotionEvent;
    .param p2, "pointerIndex"    # I

    #@0
    .prologue
    .line 51
    if-nez p2, :cond_0

    #@2
    .line 53
    const/4 v0, 0x0

    #@3
    return v0

    #@4
    .line 55
    :cond_0
    new-instance v0, Ljava/lang/IndexOutOfBoundsException;

    #@6
    const-string v1, "Pre-Eclair does not support multiple pointers"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IndexOutOfBoundsException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0
.end method

.method public getX(Landroid/view/MotionEvent;I)F
    .locals 2
    .param p1, "event"    # Landroid/view/MotionEvent;
    .param p2, "pointerIndex"    # I

    #@0
    .prologue
    .line 59
    if-nez p2, :cond_0

    #@2
    .line 60
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getX()F

    #@5
    move-result v0

    #@6
    return v0

    #@7
    .line 62
    :cond_0
    new-instance v0, Ljava/lang/IndexOutOfBoundsException;

    #@9
    const-string v1, "Pre-Eclair does not support multiple pointers"

    #@b
    invoke-direct {v0, v1}, Ljava/lang/IndexOutOfBoundsException;-><init>(Ljava/lang/String;)V

    #@e
    throw v0
.end method

.method public getY(Landroid/view/MotionEvent;I)F
    .locals 2
    .param p1, "event"    # Landroid/view/MotionEvent;
    .param p2, "pointerIndex"    # I

    #@0
    .prologue
    .line 66
    if-nez p2, :cond_0

    #@2
    .line 67
    invoke-virtual {p1}, Landroid/view/MotionEvent;->getY()F

    #@5
    move-result v0

    #@6
    return v0

    #@7
    .line 69
    :cond_0
    new-instance v0, Ljava/lang/IndexOutOfBoundsException;

    #@9
    const-string v1, "Pre-Eclair does not support multiple pointers"

    #@b
    invoke-direct {v0, v1}, Ljava/lang/IndexOutOfBoundsException;-><init>(Ljava/lang/String;)V

    #@e
    throw v0
.end method

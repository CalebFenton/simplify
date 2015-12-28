.class public abstract Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;
.super Ljava/lang/Object;
.source "ListPopupWindow.java"

# interfaces
.implements Landroid/view/View$OnTouchListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/ListPopupWindow;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "ForwardingListener"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$TriggerLongPress;,
        Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$DisallowIntercept;
    }
.end annotation


# instance fields
.field private mActivePointerId:I

.field private mDisallowIntercept:Ljava/lang/Runnable;

.field private mForwarding:Z

.field private final mLongPressTimeout:I

.field private final mScaledTouchSlop:F

.field private final mSrc:Landroid/view/View;

.field private final mTapTimeout:I

.field private final mTmpLocation:[I

.field private mTriggerLongPress:Ljava/lang/Runnable;

.field private mWasLongPress:Z


# direct methods
.method public constructor <init>(Landroid/view/View;)V
    .locals 2
    .param p1, "src"    # Landroid/view/View;

    #@0
    .prologue
    .line 1247
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 1245
    const/4 v0, 0x2

    #@4
    new-array v0, v0, [I

    #@6
    iput-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTmpLocation:[I

    #@8
    .line 1248
    iput-object p1, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@a
    .line 1249
    invoke-virtual {p1}, Landroid/view/View;->getContext()Landroid/content/Context;

    #@d
    move-result-object v0

    #@e
    invoke-static {v0}, Landroid/view/ViewConfiguration;->get(Landroid/content/Context;)Landroid/view/ViewConfiguration;

    #@11
    move-result-object v0

    #@12
    invoke-virtual {v0}, Landroid/view/ViewConfiguration;->getScaledTouchSlop()I

    #@15
    move-result v0

    #@16
    int-to-float v0, v0

    #@17
    iput v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mScaledTouchSlop:F

    #@19
    .line 1250
    invoke-static {}, Landroid/view/ViewConfiguration;->getTapTimeout()I

    #@1c
    move-result v0

    #@1d
    iput v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTapTimeout:I

    #@1f
    .line 1252
    iget v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTapTimeout:I

    #@21
    invoke-static {}, Landroid/view/ViewConfiguration;->getLongPressTimeout()I

    #@24
    move-result v1

    #@25
    add-int/2addr v0, v1

    #@26
    div-int/lit8 v0, v0, 0x2

    #@28
    iput v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mLongPressTimeout:I

    #@2a
    .line 1253
    return-void
.end method

.method static synthetic access$1000(Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@0
    .prologue
    .line 1214
    invoke-direct {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onLongPress()V

    #@3
    return-void
.end method

.method static synthetic access$900(Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;)Landroid/view/View;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;

    #@0
    .prologue
    .line 1214
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@2
    return-object v0
.end method

.method private clearCallbacks()V
    .locals 2

    #@0
    .prologue
    .line 1377
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTriggerLongPress:Ljava/lang/Runnable;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1378
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@6
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTriggerLongPress:Ljava/lang/Runnable;

    #@8
    invoke-virtual {v0, v1}, Landroid/view/View;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@b
    .line 1381
    :cond_0
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mDisallowIntercept:Ljava/lang/Runnable;

    #@d
    if-eqz v0, :cond_1

    #@f
    .line 1382
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@11
    iget-object v1, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mDisallowIntercept:Ljava/lang/Runnable;

    #@13
    invoke-virtual {v0, v1}, Landroid/view/View;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@16
    .line 1384
    :cond_1
    return-void
.end method

.method private onLongPress()V
    .locals 11

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    const/4 v10, 0x1

    #@2
    .line 1387
    invoke-direct {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->clearCallbacks()V

    #@5
    .line 1389
    iget-object v9, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@7
    .line 1390
    .local v9, "src":Landroid/view/View;
    invoke-virtual {v9}, Landroid/view/View;->isEnabled()Z

    #@a
    move-result v2

    #@b
    if-nez v2, :cond_1

    #@d
    .line 1409
    :cond_0
    :goto_0
    return-void

    #@e
    .line 1394
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onForwardingStarted()Z

    #@11
    move-result v2

    #@12
    if-eqz v2, :cond_0

    #@14
    .line 1399
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@16
    invoke-virtual {v2}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@19
    move-result-object v2

    #@1a
    invoke-interface {v2, v10}, Landroid/view/ViewParent;->requestDisallowInterceptTouchEvent(Z)V

    #@1d
    .line 1402
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    #@20
    move-result-wide v0

    #@21
    .line 1403
    .local v0, "now":J
    const/4 v4, 0x3

    #@22
    const/4 v7, 0x0

    #@23
    move-wide v2, v0

    #@24
    move v6, v5

    #@25
    invoke-static/range {v0 .. v7}, Landroid/view/MotionEvent;->obtain(JJIFFI)Landroid/view/MotionEvent;

    #@28
    move-result-object v8

    #@29
    .line 1404
    .local v8, "e":Landroid/view/MotionEvent;
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@2b
    invoke-virtual {v2, v8}, Landroid/view/View;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@2e
    .line 1405
    invoke-virtual {v8}, Landroid/view/MotionEvent;->recycle()V

    #@31
    .line 1407
    iput-boolean v10, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mForwarding:Z

    #@33
    .line 1408
    iput-boolean v10, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mWasLongPress:Z

    #@35
    goto :goto_0
.end method

.method private onTouchForwarded(Landroid/view/MotionEvent;)Z
    .locals 10
    .param p1, "srcEvent"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v7, 0x1

    #@1
    const/4 v8, 0x0

    #@2
    .line 1418
    iget-object v6, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@4
    .line 1419
    .local v6, "src":Landroid/view/View;
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->getPopup()Landroid/support/v7/widget/ListPopupWindow;

    #@7
    move-result-object v5

    #@8
    .line 1420
    .local v5, "popup":Landroid/support/v7/widget/ListPopupWindow;
    if-eqz v5, :cond_0

    #@a
    invoke-virtual {v5}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@d
    move-result v9

    #@e
    if-nez v9, :cond_1

    #@10
    .line 1443
    :cond_0
    :goto_0
    return v8

    #@11
    .line 1424
    :cond_1
    # getter for: Landroid/support/v7/widget/ListPopupWindow;->mDropDownList:Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
    invoke-static {v5}, Landroid/support/v7/widget/ListPopupWindow;->access$600(Landroid/support/v7/widget/ListPopupWindow;)Landroid/support/v7/widget/ListPopupWindow$DropDownListView;

    #@14
    move-result-object v1

    #@15
    .line 1425
    .local v1, "dst":Landroid/support/v7/widget/ListPopupWindow$DropDownListView;
    if-eqz v1, :cond_0

    #@17
    invoke-virtual {v1}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->isShown()Z

    #@1a
    move-result v9

    #@1b
    if-eqz v9, :cond_0

    #@1d
    .line 1430
    invoke-static {p1}, Landroid/view/MotionEvent;->obtainNoHistory(Landroid/view/MotionEvent;)Landroid/view/MotionEvent;

    #@20
    move-result-object v2

    #@21
    .line 1431
    .local v2, "dstEvent":Landroid/view/MotionEvent;
    invoke-direct {p0, v6, v2}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->toGlobalMotionEvent(Landroid/view/View;Landroid/view/MotionEvent;)Z

    #@24
    .line 1432
    invoke-direct {p0, v1, v2}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->toLocalMotionEvent(Landroid/view/View;Landroid/view/MotionEvent;)Z

    #@27
    .line 1435
    iget v9, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mActivePointerId:I

    #@29
    invoke-virtual {v1, v2, v9}, Landroid/support/v7/widget/ListPopupWindow$DropDownListView;->onForwardedEvent(Landroid/view/MotionEvent;I)Z

    #@2c
    move-result v3

    #@2d
    .line 1436
    .local v3, "handled":Z
    invoke-virtual {v2}, Landroid/view/MotionEvent;->recycle()V

    #@30
    .line 1439
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@33
    move-result v0

    #@34
    .line 1440
    .local v0, "action":I
    if-eq v0, v7, :cond_2

    #@36
    const/4 v9, 0x3

    #@37
    if-eq v0, v9, :cond_2

    #@39
    move v4, v7

    #@3a
    .line 1443
    .local v4, "keepForwarding":Z
    :goto_1
    if-eqz v3, :cond_3

    #@3c
    if-eqz v4, :cond_3

    #@3e
    :goto_2
    move v8, v7

    #@3f
    goto :goto_0

    #@40
    .end local v4    # "keepForwarding":Z
    :cond_2
    move v4, v8

    #@41
    .line 1440
    goto :goto_1

    #@42
    .restart local v4    # "keepForwarding":Z
    :cond_3
    move v7, v8

    #@43
    .line 1443
    goto :goto_2
.end method

.method private onTouchObserved(Landroid/view/MotionEvent;)Z
    .locals 11
    .param p1, "srcEvent"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v10, 0x0

    #@1
    const/4 v6, 0x1

    #@2
    const/4 v5, 0x0

    #@3
    .line 1333
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@5
    .line 1334
    .local v2, "src":Landroid/view/View;
    invoke-virtual {v2}, Landroid/view/View;->isEnabled()Z

    #@8
    move-result v7

    #@9
    if-nez v7, :cond_1

    #@b
    .line 1373
    :cond_0
    :goto_0
    return v5

    #@c
    .line 1338
    :cond_1
    invoke-static {p1}, Landroid/support/v4/view/MotionEventCompat;->getActionMasked(Landroid/view/MotionEvent;)I

    #@f
    move-result v0

    #@10
    .line 1339
    .local v0, "actionMasked":I
    packed-switch v0, :pswitch_data_0

    #@13
    goto :goto_0

    #@14
    .line 1341
    :pswitch_0
    invoke-virtual {p1, v5}, Landroid/view/MotionEvent;->getPointerId(I)I

    #@17
    move-result v6

    #@18
    iput v6, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mActivePointerId:I

    #@1a
    .line 1342
    iput-boolean v5, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mWasLongPress:Z

    #@1c
    .line 1344
    iget-object v6, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mDisallowIntercept:Ljava/lang/Runnable;

    #@1e
    if-nez v6, :cond_2

    #@20
    .line 1345
    new-instance v6, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$DisallowIntercept;

    #@22
    invoke-direct {v6, p0, v10}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$DisallowIntercept;-><init>(Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;Landroid/support/v7/widget/ListPopupWindow$1;)V

    #@25
    iput-object v6, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mDisallowIntercept:Ljava/lang/Runnable;

    #@27
    .line 1347
    :cond_2
    iget-object v6, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mDisallowIntercept:Ljava/lang/Runnable;

    #@29
    iget v7, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTapTimeout:I

    #@2b
    int-to-long v8, v7

    #@2c
    invoke-virtual {v2, v6, v8, v9}, Landroid/view/View;->postDelayed(Ljava/lang/Runnable;J)Z

    #@2f
    .line 1348
    iget-object v6, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTriggerLongPress:Ljava/lang/Runnable;

    #@31
    if-nez v6, :cond_3

    #@33
    .line 1349
    new-instance v6, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$TriggerLongPress;

    #@35
    invoke-direct {v6, p0, v10}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener$TriggerLongPress;-><init>(Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;Landroid/support/v7/widget/ListPopupWindow$1;)V

    #@38
    iput-object v6, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTriggerLongPress:Ljava/lang/Runnable;

    #@3a
    .line 1351
    :cond_3
    iget-object v6, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTriggerLongPress:Ljava/lang/Runnable;

    #@3c
    iget v7, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mLongPressTimeout:I

    #@3e
    int-to-long v8, v7

    #@3f
    invoke-virtual {v2, v6, v8, v9}, Landroid/view/View;->postDelayed(Ljava/lang/Runnable;J)Z

    #@42
    goto :goto_0

    #@43
    .line 1354
    :pswitch_1
    iget v7, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mActivePointerId:I

    #@45
    invoke-virtual {p1, v7}, Landroid/view/MotionEvent;->findPointerIndex(I)I

    #@48
    move-result v1

    #@49
    .line 1355
    .local v1, "activePointerIndex":I
    if-ltz v1, :cond_0

    #@4b
    .line 1356
    invoke-virtual {p1, v1}, Landroid/view/MotionEvent;->getX(I)F

    #@4e
    move-result v3

    #@4f
    .line 1357
    .local v3, "x":F
    invoke-virtual {p1, v1}, Landroid/view/MotionEvent;->getY(I)F

    #@52
    move-result v4

    #@53
    .line 1358
    .local v4, "y":F
    iget v7, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mScaledTouchSlop:F

    #@55
    invoke-static {v2, v3, v4, v7}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->pointInView(Landroid/view/View;FFF)Z

    #@58
    move-result v7

    #@59
    if-nez v7, :cond_0

    #@5b
    .line 1359
    invoke-direct {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->clearCallbacks()V

    #@5e
    .line 1362
    invoke-virtual {v2}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@61
    move-result-object v5

    #@62
    invoke-interface {v5, v6}, Landroid/view/ViewParent;->requestDisallowInterceptTouchEvent(Z)V

    #@65
    move v5, v6

    #@66
    .line 1363
    goto :goto_0

    #@67
    .line 1369
    .end local v1    # "activePointerIndex":I
    .end local v3    # "x":F
    .end local v4    # "y":F
    :pswitch_2
    invoke-direct {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->clearCallbacks()V

    #@6a
    goto :goto_0

    #@6b
    .line 1339
    nop

    #@6c
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_2
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method private static pointInView(Landroid/view/View;FFF)Z
    .locals 2
    .param p0, "view"    # Landroid/view/View;
    .param p1, "localX"    # F
    .param p2, "localY"    # F
    .param p3, "slop"    # F

    #@0
    .prologue
    .line 1447
    neg-float v0, p3

    #@1
    cmpl-float v0, p1, v0

    #@3
    if-ltz v0, :cond_0

    #@5
    neg-float v0, p3

    #@6
    cmpl-float v0, p2, v0

    #@8
    if-ltz v0, :cond_0

    #@a
    invoke-virtual {p0}, Landroid/view/View;->getRight()I

    #@d
    move-result v0

    #@e
    invoke-virtual {p0}, Landroid/view/View;->getLeft()I

    #@11
    move-result v1

    #@12
    sub-int/2addr v0, v1

    #@13
    int-to-float v0, v0

    #@14
    add-float/2addr v0, p3

    #@15
    cmpg-float v0, p1, v0

    #@17
    if-gez v0, :cond_0

    #@19
    invoke-virtual {p0}, Landroid/view/View;->getBottom()I

    #@1c
    move-result v0

    #@1d
    invoke-virtual {p0}, Landroid/view/View;->getTop()I

    #@20
    move-result v1

    #@21
    sub-int/2addr v0, v1

    #@22
    int-to-float v0, v0

    #@23
    add-float/2addr v0, p3

    #@24
    cmpg-float v0, p2, v0

    #@26
    if-gez v0, :cond_0

    #@28
    const/4 v0, 0x1

    #@29
    :goto_0
    return v0

    #@2a
    :cond_0
    const/4 v0, 0x0

    #@2b
    goto :goto_0
.end method

.method private toGlobalMotionEvent(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 4
    .param p1, "view"    # Landroid/view/View;
    .param p2, "event"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 1468
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTmpLocation:[I

    #@3
    .line 1469
    .local v0, "loc":[I
    invoke-virtual {p1, v0}, Landroid/view/View;->getLocationOnScreen([I)V

    #@6
    .line 1470
    const/4 v1, 0x0

    #@7
    aget v1, v0, v1

    #@9
    int-to-float v1, v1

    #@a
    aget v2, v0, v3

    #@c
    int-to-float v2, v2

    #@d
    invoke-virtual {p2, v1, v2}, Landroid/view/MotionEvent;->offsetLocation(FF)V

    #@10
    .line 1471
    return v3
.end method

.method private toLocalMotionEvent(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 4
    .param p1, "view"    # Landroid/view/View;
    .param p2, "event"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 1457
    iget-object v0, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mTmpLocation:[I

    #@3
    .line 1458
    .local v0, "loc":[I
    invoke-virtual {p1, v0}, Landroid/view/View;->getLocationOnScreen([I)V

    #@6
    .line 1459
    const/4 v1, 0x0

    #@7
    aget v1, v0, v1

    #@9
    neg-int v1, v1

    #@a
    int-to-float v1, v1

    #@b
    aget v2, v0, v3

    #@d
    neg-int v2, v2

    #@e
    int-to-float v2, v2

    #@f
    invoke-virtual {p2, v1, v2}, Landroid/view/MotionEvent;->offsetLocation(FF)V

    #@12
    .line 1460
    return v3
.end method


# virtual methods
.method public abstract getPopup()Landroid/support/v7/widget/ListPopupWindow;
.end method

.method protected onForwardingStarted()Z
    .locals 2

    #@0
    .prologue
    .line 1305
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->getPopup()Landroid/support/v7/widget/ListPopupWindow;

    #@3
    move-result-object v0

    #@4
    .line 1306
    .local v0, "popup":Landroid/support/v7/widget/ListPopupWindow;
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@9
    move-result v1

    #@a
    if-nez v1, :cond_0

    #@c
    .line 1307
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow;->show()V

    #@f
    .line 1309
    :cond_0
    const/4 v1, 0x1

    #@10
    return v1
.end method

.method protected onForwardingStopped()Z
    .locals 2

    #@0
    .prologue
    .line 1319
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->getPopup()Landroid/support/v7/widget/ListPopupWindow;

    #@3
    move-result-object v0

    #@4
    .line 1320
    .local v0, "popup":Landroid/support/v7/widget/ListPopupWindow;
    if-eqz v0, :cond_0

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow;->isShowing()Z

    #@9
    move-result v1

    #@a
    if-eqz v1, :cond_0

    #@c
    .line 1321
    invoke-virtual {v0}, Landroid/support/v7/widget/ListPopupWindow;->dismiss()V

    #@f
    .line 1323
    :cond_0
    const/4 v1, 0x1

    #@10
    return v1
.end method

.method public onTouch(Landroid/view/View;Landroid/view/MotionEvent;)Z
    .locals 12
    .param p1, "v"    # Landroid/view/View;
    .param p2, "event"    # Landroid/view/MotionEvent;

    #@0
    .prologue
    const/4 v5, 0x0

    #@1
    const/4 v11, 0x1

    #@2
    const/4 v7, 0x0

    #@3
    .line 1269
    iget-boolean v10, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mForwarding:Z

    #@5
    .line 1271
    .local v10, "wasForwarding":Z
    if-eqz v10, :cond_6

    #@7
    .line 1272
    iget-boolean v2, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mWasLongPress:Z

    #@9
    if-eqz v2, :cond_3

    #@b
    .line 1276
    invoke-direct {p0, p2}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onTouchForwarded(Landroid/view/MotionEvent;)Z

    #@e
    move-result v9

    #@f
    .line 1293
    .local v9, "forwarding":Z
    :cond_0
    :goto_0
    iput-boolean v9, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mForwarding:Z

    #@11
    .line 1294
    if-nez v9, :cond_1

    #@13
    if-eqz v10, :cond_2

    #@15
    :cond_1
    move v7, v11

    #@16
    :cond_2
    return v7

    #@17
    .line 1278
    .end local v9    # "forwarding":Z
    :cond_3
    invoke-direct {p0, p2}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onTouchForwarded(Landroid/view/MotionEvent;)Z

    #@1a
    move-result v2

    #@1b
    if-nez v2, :cond_4

    #@1d
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onForwardingStopped()Z

    #@20
    move-result v2

    #@21
    if-nez v2, :cond_5

    #@23
    :cond_4
    move v9, v11

    #@24
    .restart local v9    # "forwarding":Z
    :goto_1
    goto :goto_0

    #@25
    .end local v9    # "forwarding":Z
    :cond_5
    move v9, v7

    #@26
    goto :goto_1

    #@27
    .line 1281
    :cond_6
    invoke-direct {p0, p2}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onTouchObserved(Landroid/view/MotionEvent;)Z

    #@2a
    move-result v2

    #@2b
    if-eqz v2, :cond_7

    #@2d
    invoke-virtual {p0}, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->onForwardingStarted()Z

    #@30
    move-result v2

    #@31
    if-eqz v2, :cond_7

    #@33
    move v9, v11

    #@34
    .line 1283
    .restart local v9    # "forwarding":Z
    :goto_2
    if-eqz v9, :cond_0

    #@36
    .line 1285
    invoke-static {}, Landroid/os/SystemClock;->uptimeMillis()J

    #@39
    move-result-wide v0

    #@3a
    .line 1286
    .local v0, "now":J
    const/4 v4, 0x3

    #@3b
    move-wide v2, v0

    #@3c
    move v6, v5

    #@3d
    invoke-static/range {v0 .. v7}, Landroid/view/MotionEvent;->obtain(JJIFFI)Landroid/view/MotionEvent;

    #@40
    move-result-object v8

    #@41
    .line 1288
    .local v8, "e":Landroid/view/MotionEvent;
    iget-object v2, p0, Landroid/support/v7/widget/ListPopupWindow$ForwardingListener;->mSrc:Landroid/view/View;

    #@43
    invoke-virtual {v2, v8}, Landroid/view/View;->onTouchEvent(Landroid/view/MotionEvent;)Z

    #@46
    .line 1289
    invoke-virtual {v8}, Landroid/view/MotionEvent;->recycle()V

    #@49
    goto :goto_0

    #@4a
    .end local v0    # "now":J
    .end local v8    # "e":Landroid/view/MotionEvent;
    .end local v9    # "forwarding":Z
    :cond_7
    move v9, v7

    #@4b
    .line 1281
    goto :goto_2
.end method

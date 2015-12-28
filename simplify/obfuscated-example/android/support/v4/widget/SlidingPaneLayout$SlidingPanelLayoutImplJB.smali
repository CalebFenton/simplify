.class Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;
.super Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplBase;
.source "SlidingPaneLayout.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/SlidingPaneLayout;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "SlidingPanelLayoutImplJB"
.end annotation


# instance fields
.field private mGetDisplayList:Ljava/lang/reflect/Method;

.field private mRecreateDisplayList:Ljava/lang/reflect/Field;


# direct methods
.method constructor <init>()V
    .locals 4

    #@0
    .prologue
    .line 1499
    invoke-direct {p0}, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplBase;-><init>()V

    #@3
    .line 1501
    :try_start_0
    const-class v2, Landroid/view/View;

    #@5
    const-string v3, "getDisplayList"

    #@7
    const/4 v1, 0x0

    #@8
    check-cast v1, [Ljava/lang/Class;

    #@a
    invoke-virtual {v2, v3, v1}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@d
    move-result-object v1

    #@e
    iput-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;->mGetDisplayList:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0

    #@10
    .line 1506
    :goto_0
    :try_start_1
    const-class v1, Landroid/view/View;

    #@12
    const-string v2, "mRecreateDisplayList"

    #@14
    invoke-virtual {v1, v2}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    #@17
    move-result-object v1

    #@18
    iput-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;->mRecreateDisplayList:Ljava/lang/reflect/Field;

    #@1a
    .line 1507
    iget-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;->mRecreateDisplayList:Ljava/lang/reflect/Field;

    #@1c
    const/4 v2, 0x1

    #@1d
    invoke-virtual {v1, v2}, Ljava/lang/reflect/Field;->setAccessible(Z)V
    :try_end_1
    .catch Ljava/lang/NoSuchFieldException; {:try_start_1 .. :try_end_1} :catch_1

    #@20
    .line 1511
    :goto_1
    return-void

    #@21
    .line 1502
    :catch_0
    move-exception v0

    #@22
    .line 1503
    .local v0, "e":Ljava/lang/NoSuchMethodException;
    const-string v1, "SlidingPaneLayout"

    #@24
    const-string v2, "Couldn\'t fetch getDisplayList method; dimming won\'t work right."

    #@26
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@29
    goto :goto_0

    #@2a
    .line 1508
    .end local v0    # "e":Ljava/lang/NoSuchMethodException;
    :catch_1
    move-exception v0

    #@2b
    .line 1509
    .local v0, "e":Ljava/lang/NoSuchFieldException;
    const-string v1, "SlidingPaneLayout"

    #@2d
    const-string v2, "Couldn\'t fetch mRecreateDisplayList field; dimming will be slow."

    #@2f
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@32
    goto :goto_1
.end method


# virtual methods
.method public invalidateChildRegion(Landroid/support/v4/widget/SlidingPaneLayout;Landroid/view/View;)V
    .locals 3
    .param p1, "parent"    # Landroid/support/v4/widget/SlidingPaneLayout;
    .param p2, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 1515
    iget-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;->mGetDisplayList:Ljava/lang/reflect/Method;

    #@2
    if-eqz v1, :cond_0

    #@4
    iget-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;->mRecreateDisplayList:Ljava/lang/reflect/Field;

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 1517
    :try_start_0
    iget-object v1, p0, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;->mRecreateDisplayList:Ljava/lang/reflect/Field;

    #@a
    const/4 v2, 0x1

    #@b
    invoke-virtual {v1, p2, v2}, Ljava/lang/reflect/Field;->setBoolean(Ljava/lang/Object;Z)V

    #@e
    .line 1518
    iget-object v2, p0, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplJB;->mGetDisplayList:Ljava/lang/reflect/Method;

    #@10
    const/4 v1, 0x0

    #@11
    check-cast v1, [Ljava/lang/Object;

    #@13
    invoke-virtual {v2, p2, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@16
    .line 1527
    :goto_0
    invoke-super {p0, p1, p2}, Landroid/support/v4/widget/SlidingPaneLayout$SlidingPanelLayoutImplBase;->invalidateChildRegion(Landroid/support/v4/widget/SlidingPaneLayout;Landroid/view/View;)V

    #@19
    .line 1528
    :goto_1
    return-void

    #@1a
    .line 1519
    :catch_0
    move-exception v0

    #@1b
    .line 1520
    .local v0, "e":Ljava/lang/Exception;
    const-string v1, "SlidingPaneLayout"

    #@1d
    const-string v2, "Error refreshing display list state"

    #@1f
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@22
    goto :goto_0

    #@23
    .line 1524
    .end local v0    # "e":Ljava/lang/Exception;
    :cond_0
    invoke-virtual {p2}, Landroid/view/View;->invalidate()V

    #@26
    goto :goto_1
.end method

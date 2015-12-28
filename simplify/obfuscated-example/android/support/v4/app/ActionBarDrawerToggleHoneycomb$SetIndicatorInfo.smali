.class Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;
.super Ljava/lang/Object;
.source "ActionBarDrawerToggleHoneycomb.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "SetIndicatorInfo"
.end annotation


# instance fields
.field public setHomeActionContentDescription:Ljava/lang/reflect/Method;

.field public setHomeAsUpIndicator:Ljava/lang/reflect/Method;

.field public upIndicatorView:Landroid/widget/ImageView;


# direct methods
.method constructor <init>(Landroid/app/Activity;)V
    .locals 14
    .param p1, "activity"    # Landroid/app/Activity;

    #@0
    .prologue
    const v13, 0x102002c

    #@3
    const/4 v12, 0x1

    #@4
    const/4 v11, 0x0

    #@5
    .line 104
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@8
    .line 106
    :try_start_0
    const-class v6, Landroid/app/ActionBar;

    #@a
    const-string v7, "setHomeAsUpIndicator"

    #@c
    const/4 v8, 0x1

    #@d
    new-array v8, v8, [Ljava/lang/Class;

    #@f
    const/4 v9, 0x0

    #@10
    const-class v10, Landroid/graphics/drawable/Drawable;

    #@12
    aput-object v10, v8, v9

    #@14
    invoke-virtual {v6, v7, v8}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@17
    move-result-object v6

    #@18
    iput-object v6, p0, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeAsUpIndicator:Ljava/lang/reflect/Method;

    #@1a
    .line 108
    const-class v6, Landroid/app/ActionBar;

    #@1c
    const-string v7, "setHomeActionContentDescription"

    #@1e
    const/4 v8, 0x1

    #@1f
    new-array v8, v8, [Ljava/lang/Class;

    #@21
    const/4 v9, 0x0

    #@22
    sget-object v10, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    #@24
    aput-object v10, v8, v9

    #@26
    invoke-virtual {v6, v7, v8}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@29
    move-result-object v6

    #@2a
    iput-object v6, p0, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->setHomeActionContentDescription:Ljava/lang/reflect/Method;
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_0

    #@2c
    .line 138
    :cond_0
    :goto_0
    return-void

    #@2d
    .line 113
    :catch_0
    move-exception v6

    #@2e
    .line 117
    invoke-virtual {p1, v13}, Landroid/app/Activity;->findViewById(I)Landroid/view/View;

    #@31
    move-result-object v2

    #@32
    .line 118
    .local v2, "home":Landroid/view/View;
    if-eqz v2, :cond_0

    #@34
    .line 123
    invoke-virtual {v2}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@37
    move-result-object v3

    #@38
    check-cast v3, Landroid/view/ViewGroup;

    #@3a
    .line 124
    .local v3, "parent":Landroid/view/ViewGroup;
    invoke-virtual {v3}, Landroid/view/ViewGroup;->getChildCount()I

    #@3d
    move-result v0

    #@3e
    .line 125
    .local v0, "childCount":I
    const/4 v6, 0x2

    #@3f
    if-ne v0, v6, :cond_0

    #@41
    .line 130
    invoke-virtual {v3, v11}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@44
    move-result-object v1

    #@45
    .line 131
    .local v1, "first":Landroid/view/View;
    invoke-virtual {v3, v12}, Landroid/view/ViewGroup;->getChildAt(I)Landroid/view/View;

    #@48
    move-result-object v4

    #@49
    .line 132
    .local v4, "second":Landroid/view/View;
    invoke-virtual {v1}, Landroid/view/View;->getId()I

    #@4c
    move-result v6

    #@4d
    if-ne v6, v13, :cond_1

    #@4f
    move-object v5, v4

    #@50
    .line 134
    .local v5, "up":Landroid/view/View;
    :goto_1
    instance-of v6, v5, Landroid/widget/ImageView;

    #@52
    if-eqz v6, :cond_0

    #@54
    .line 136
    check-cast v5, Landroid/widget/ImageView;

    #@56
    .end local v5    # "up":Landroid/view/View;
    iput-object v5, p0, Landroid/support/v4/app/ActionBarDrawerToggleHoneycomb$SetIndicatorInfo;->upIndicatorView:Landroid/widget/ImageView;

    #@58
    goto :goto_0

    #@59
    :cond_1
    move-object v5, v1

    #@5a
    .line 132
    goto :goto_1
.end method

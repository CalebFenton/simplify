.class Landroid/support/v4/widget/DrawerLayoutCompatApi21;
.super Ljava/lang/Object;
.source "DrawerLayoutCompatApi21.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/DrawerLayoutCompatApi21$InsetsListener;
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 28
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 69
    return-void
.end method

.method public static applyMarginInsets(Landroid/view/ViewGroup$MarginLayoutParams;Ljava/lang/Object;I)V
    .locals 5
    .param p0, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;
    .param p1, "insets"    # Ljava/lang/Object;
    .param p2, "gravity"    # I

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 51
    move-object v0, p1

    #@2
    check-cast v0, Landroid/view/WindowInsets;

    #@4
    .line 52
    .local v0, "wi":Landroid/view/WindowInsets;
    const/4 v1, 0x3

    #@5
    if-ne p2, v1, :cond_1

    #@7
    .line 53
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetLeft()I

    #@a
    move-result v1

    #@b
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetTop()I

    #@e
    move-result v2

    #@f
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetBottom()I

    #@12
    move-result v3

    #@13
    invoke-virtual {v0, v1, v2, v4, v3}, Landroid/view/WindowInsets;->replaceSystemWindowInsets(IIII)Landroid/view/WindowInsets;

    #@16
    move-result-object v0

    #@17
    .line 59
    :cond_0
    :goto_0
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetLeft()I

    #@1a
    move-result v1

    #@1b
    iput v1, p0, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@1d
    .line 60
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetTop()I

    #@20
    move-result v1

    #@21
    iput v1, p0, Landroid/view/ViewGroup$MarginLayoutParams;->topMargin:I

    #@23
    .line 61
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetRight()I

    #@26
    move-result v1

    #@27
    iput v1, p0, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@29
    .line 62
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetBottom()I

    #@2c
    move-result v1

    #@2d
    iput v1, p0, Landroid/view/ViewGroup$MarginLayoutParams;->bottomMargin:I

    #@2f
    .line 63
    return-void

    #@30
    .line 55
    :cond_1
    const/4 v1, 0x5

    #@31
    if-ne p2, v1, :cond_0

    #@33
    .line 56
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetTop()I

    #@36
    move-result v1

    #@37
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetRight()I

    #@3a
    move-result v2

    #@3b
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetBottom()I

    #@3e
    move-result v3

    #@3f
    invoke-virtual {v0, v4, v1, v2, v3}, Landroid/view/WindowInsets;->replaceSystemWindowInsets(IIII)Landroid/view/WindowInsets;

    #@42
    move-result-object v0

    #@43
    goto :goto_0
.end method

.method public static configureApplyInsets(Landroid/view/View;)V
    .locals 1
    .param p0, "drawerLayout"    # Landroid/view/View;

    #@0
    .prologue
    .line 30
    instance-of v0, p0, Landroid/support/v4/widget/DrawerLayoutImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 31
    new-instance v0, Landroid/support/v4/widget/DrawerLayoutCompatApi21$InsetsListener;

    #@6
    invoke-direct {v0}, Landroid/support/v4/widget/DrawerLayoutCompatApi21$InsetsListener;-><init>()V

    #@9
    invoke-virtual {p0, v0}, Landroid/view/View;->setOnApplyWindowInsetsListener(Landroid/view/View$OnApplyWindowInsetsListener;)V

    #@c
    .line 32
    const/16 v0, 0x500

    #@e
    invoke-virtual {p0, v0}, Landroid/view/View;->setSystemUiVisibility(I)V

    #@11
    .line 35
    :cond_0
    return-void
.end method

.method public static dispatchChildInsets(Landroid/view/View;Ljava/lang/Object;I)V
    .locals 5
    .param p0, "child"    # Landroid/view/View;
    .param p1, "insets"    # Ljava/lang/Object;
    .param p2, "gravity"    # I

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 38
    move-object v0, p1

    #@2
    check-cast v0, Landroid/view/WindowInsets;

    #@4
    .line 39
    .local v0, "wi":Landroid/view/WindowInsets;
    const/4 v1, 0x3

    #@5
    if-ne p2, v1, :cond_1

    #@7
    .line 40
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetLeft()I

    #@a
    move-result v1

    #@b
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetTop()I

    #@e
    move-result v2

    #@f
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetBottom()I

    #@12
    move-result v3

    #@13
    invoke-virtual {v0, v1, v2, v4, v3}, Landroid/view/WindowInsets;->replaceSystemWindowInsets(IIII)Landroid/view/WindowInsets;

    #@16
    move-result-object v0

    #@17
    .line 46
    :cond_0
    :goto_0
    invoke-virtual {p0, v0}, Landroid/view/View;->dispatchApplyWindowInsets(Landroid/view/WindowInsets;)Landroid/view/WindowInsets;

    #@1a
    .line 47
    return-void

    #@1b
    .line 42
    :cond_1
    const/4 v1, 0x5

    #@1c
    if-ne p2, v1, :cond_0

    #@1e
    .line 43
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetTop()I

    #@21
    move-result v1

    #@22
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetRight()I

    #@25
    move-result v2

    #@26
    invoke-virtual {v0}, Landroid/view/WindowInsets;->getSystemWindowInsetBottom()I

    #@29
    move-result v3

    #@2a
    invoke-virtual {v0, v4, v1, v2, v3}, Landroid/view/WindowInsets;->replaceSystemWindowInsets(IIII)Landroid/view/WindowInsets;

    #@2d
    move-result-object v0

    #@2e
    goto :goto_0
.end method

.method public static getTopInset(Ljava/lang/Object;)I
    .locals 1
    .param p0, "insets"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 66
    if-eqz p0, :cond_0

    #@2
    check-cast p0, Landroid/view/WindowInsets;

    #@4
    .end local p0    # "insets":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/WindowInsets;->getSystemWindowInsetTop()I

    #@7
    move-result v0

    #@8
    :goto_0
    return v0

    #@9
    .restart local p0    # "insets":Ljava/lang/Object;
    :cond_0
    const/4 v0, 0x0

    #@a
    goto :goto_0
.end method

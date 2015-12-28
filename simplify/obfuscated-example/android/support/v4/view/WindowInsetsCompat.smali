.class public Landroid/support/v4/view/WindowInsetsCompat;
.super Ljava/lang/Object;
.source "WindowInsetsCompat.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 31
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public consumeStableInsets()Landroid/support/v4/view/WindowInsetsCompat;
    .locals 0

    #@0
    .prologue
    .line 256
    return-object p0
.end method

.method public consumeSystemWindowInsets()Landroid/support/v4/view/WindowInsetsCompat;
    .locals 0

    #@0
    .prologue
    .line 144
    return-object p0
.end method

.method public getStableInsetBottom()I
    .locals 1

    #@0
    .prologue
    .line 232
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getStableInsetLeft()I
    .locals 1

    #@0
    .prologue
    .line 201
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getStableInsetRight()I
    .locals 1

    #@0
    .prologue
    .line 216
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getStableInsetTop()I
    .locals 1

    #@0
    .prologue
    .line 185
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getSystemWindowInsetBottom()I
    .locals 1

    #@0
    .prologue
    .line 82
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getSystemWindowInsetLeft()I
    .locals 1

    #@0
    .prologue
    .line 43
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getSystemWindowInsetRight()I
    .locals 1

    #@0
    .prologue
    .line 69
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getSystemWindowInsetTop()I
    .locals 1

    #@0
    .prologue
    .line 56
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public hasInsets()Z
    .locals 1

    #@0
    .prologue
    .line 104
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public hasStableInsets()Z
    .locals 1

    #@0
    .prologue
    .line 247
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public hasSystemWindowInsets()Z
    .locals 1

    #@0
    .prologue
    .line 95
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public isConsumed()Z
    .locals 1

    #@0
    .prologue
    .line 121
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public isRound()Z
    .locals 1

    #@0
    .prologue
    .line 135
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public replaceSystemWindowInsets(IIII)Landroid/support/v4/view/WindowInsetsCompat;
    .locals 0
    .param p1, "left"    # I
    .param p2, "top"    # I
    .param p3, "right"    # I
    .param p4, "bottom"    # I

    #@0
    .prologue
    .line 158
    return-object p0
.end method

.method public replaceSystemWindowInsets(Landroid/graphics/Rect;)Landroid/support/v4/view/WindowInsetsCompat;
    .locals 0
    .param p1, "systemWindowInsets"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 170
    return-object p0
.end method

.class public final Landroid/support/v7/internal/widget/ViewStubCompat;
.super Landroid/view/View;
.source "ViewStubCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/widget/ViewStubCompat$OnInflateListener;
    }
.end annotation


# instance fields
.field private mInflateListener:Landroid/support/v7/internal/widget/ViewStubCompat$OnInflateListener;

.field private mInflatedId:I

.field private mInflatedViewRef:Ljava/lang/ref/WeakReference;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/lang/ref/WeakReference",
            "<",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation
.end field

.field private mInflater:Landroid/view/LayoutInflater;

.field private mLayoutResource:I


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 48
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/ViewStubCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 49
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    const/4 v3, -0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 52
    invoke-direct {p0, p1, p2, p3}, Landroid/view/View;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 39
    iput v2, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mLayoutResource:I

    #@7
    .line 54
    sget-object v1, Landroid/support/v7/appcompat/R$styleable;->ViewStubCompat:[I

    #@9
    invoke-virtual {p1, p2, v1, p3, v2}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@c
    move-result-object v0

    #@d
    .line 57
    .local v0, "a":Landroid/content/res/TypedArray;
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ViewStubCompat_android_inflatedId:I

    #@f
    invoke-virtual {v0, v1, v3}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@12
    move-result v1

    #@13
    iput v1, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflatedId:I

    #@15
    .line 58
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ViewStubCompat_android_layout:I

    #@17
    invoke-virtual {v0, v1, v2}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@1a
    move-result v1

    #@1b
    iput v1, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mLayoutResource:I

    #@1d
    .line 60
    sget v1, Landroid/support/v7/appcompat/R$styleable;->ViewStubCompat_android_id:I

    #@1f
    invoke-virtual {v0, v1, v3}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@22
    move-result v1

    #@23
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ViewStubCompat;->setId(I)V

    #@26
    .line 61
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@29
    .line 63
    const/16 v1, 0x8

    #@2b
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ViewStubCompat;->setVisibility(I)V

    #@2e
    .line 64
    const/4 v1, 0x1

    #@2f
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ViewStubCompat;->setWillNotDraw(Z)V

    #@32
    .line 65
    return-void
.end method


# virtual methods
.method protected dispatchDraw(Landroid/graphics/Canvas;)V
    .locals 0
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 153
    return-void
.end method

.method public draw(Landroid/graphics/Canvas;)V
    .locals 0
    .param p1, "canvas"    # Landroid/graphics/Canvas;

    #@0
    .prologue
    .line 149
    return-void
.end method

.method public getInflatedId()I
    .locals 1

    #@0
    .prologue
    .line 78
    iget v0, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflatedId:I

    #@2
    return v0
.end method

.method public getLayoutInflater()Landroid/view/LayoutInflater;
    .locals 1

    #@0
    .prologue
    .line 139
    iget-object v0, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflater:Landroid/view/LayoutInflater;

    #@2
    return-object v0
.end method

.method public getLayoutResource()I
    .locals 1

    #@0
    .prologue
    .line 108
    iget v0, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mLayoutResource:I

    #@2
    return v0
.end method

.method public inflate()Landroid/view/View;
    .locals 8

    #@0
    .prologue
    .line 190
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ViewStubCompat;->getParent()Landroid/view/ViewParent;

    #@3
    move-result-object v5

    #@4
    .line 192
    .local v5, "viewParent":Landroid/view/ViewParent;
    if-eqz v5, :cond_5

    #@6
    instance-of v6, v5, Landroid/view/ViewGroup;

    #@8
    if-eqz v6, :cond_5

    #@a
    .line 193
    iget v6, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mLayoutResource:I

    #@c
    if-eqz v6, :cond_4

    #@e
    move-object v3, v5

    #@f
    .line 194
    check-cast v3, Landroid/view/ViewGroup;

    #@11
    .line 196
    .local v3, "parent":Landroid/view/ViewGroup;
    iget-object v6, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflater:Landroid/view/LayoutInflater;

    #@13
    if-eqz v6, :cond_2

    #@15
    .line 197
    iget-object v0, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflater:Landroid/view/LayoutInflater;

    #@17
    .line 201
    .local v0, "factory":Landroid/view/LayoutInflater;
    :goto_0
    iget v6, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mLayoutResource:I

    #@19
    const/4 v7, 0x0

    #@1a
    invoke-virtual {v0, v6, v3, v7}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@1d
    move-result-object v4

    #@1e
    .line 204
    .local v4, "view":Landroid/view/View;
    iget v6, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflatedId:I

    #@20
    const/4 v7, -0x1

    #@21
    if-eq v6, v7, :cond_0

    #@23
    .line 205
    iget v6, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflatedId:I

    #@25
    invoke-virtual {v4, v6}, Landroid/view/View;->setId(I)V

    #@28
    .line 208
    :cond_0
    invoke-virtual {v3, p0}, Landroid/view/ViewGroup;->indexOfChild(Landroid/view/View;)I

    #@2b
    move-result v1

    #@2c
    .line 209
    .local v1, "index":I
    invoke-virtual {v3, p0}, Landroid/view/ViewGroup;->removeViewInLayout(Landroid/view/View;)V

    #@2f
    .line 211
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ViewStubCompat;->getLayoutParams()Landroid/view/ViewGroup$LayoutParams;

    #@32
    move-result-object v2

    #@33
    .line 212
    .local v2, "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    if-eqz v2, :cond_3

    #@35
    .line 213
    invoke-virtual {v3, v4, v1, v2}, Landroid/view/ViewGroup;->addView(Landroid/view/View;ILandroid/view/ViewGroup$LayoutParams;)V

    #@38
    .line 218
    :goto_1
    new-instance v6, Ljava/lang/ref/WeakReference;

    #@3a
    invoke-direct {v6, v4}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    #@3d
    iput-object v6, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflatedViewRef:Ljava/lang/ref/WeakReference;

    #@3f
    .line 220
    iget-object v6, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflateListener:Landroid/support/v7/internal/widget/ViewStubCompat$OnInflateListener;

    #@41
    if-eqz v6, :cond_1

    #@43
    .line 221
    iget-object v6, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflateListener:Landroid/support/v7/internal/widget/ViewStubCompat$OnInflateListener;

    #@45
    invoke-interface {v6, p0, v4}, Landroid/support/v7/internal/widget/ViewStubCompat$OnInflateListener;->onInflate(Landroid/support/v7/internal/widget/ViewStubCompat;Landroid/view/View;)V

    #@48
    .line 224
    :cond_1
    return-object v4

    #@49
    .line 199
    .end local v0    # "factory":Landroid/view/LayoutInflater;
    .end local v1    # "index":I
    .end local v2    # "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    .end local v4    # "view":Landroid/view/View;
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ViewStubCompat;->getContext()Landroid/content/Context;

    #@4c
    move-result-object v6

    #@4d
    invoke-static {v6}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@50
    move-result-object v0

    #@51
    .restart local v0    # "factory":Landroid/view/LayoutInflater;
    goto :goto_0

    #@52
    .line 215
    .restart local v1    # "index":I
    .restart local v2    # "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    .restart local v4    # "view":Landroid/view/View;
    :cond_3
    invoke-virtual {v3, v4, v1}, Landroid/view/ViewGroup;->addView(Landroid/view/View;I)V

    #@55
    goto :goto_1

    #@56
    .line 226
    .end local v0    # "factory":Landroid/view/LayoutInflater;
    .end local v1    # "index":I
    .end local v2    # "layoutParams":Landroid/view/ViewGroup$LayoutParams;
    .end local v3    # "parent":Landroid/view/ViewGroup;
    .end local v4    # "view":Landroid/view/View;
    :cond_4
    new-instance v6, Ljava/lang/IllegalArgumentException;

    #@58
    const-string v7, "ViewStub must have a valid layoutResource"

    #@5a
    invoke-direct {v6, v7}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@5d
    throw v6

    #@5e
    .line 229
    :cond_5
    new-instance v6, Ljava/lang/IllegalStateException;

    #@60
    const-string v7, "ViewStub must have a non-null ViewGroup viewParent"

    #@62
    invoke-direct {v6, v7}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@65
    throw v6
.end method

.method protected onMeasure(II)V
    .locals 1
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 144
    invoke-virtual {p0, v0, v0}, Landroid/support/v7/internal/widget/ViewStubCompat;->setMeasuredDimension(II)V

    #@4
    .line 145
    return-void
.end method

.method public setInflatedId(I)V
    .locals 0
    .param p1, "inflatedId"    # I

    #@0
    .prologue
    .line 92
    iput p1, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflatedId:I

    #@2
    .line 93
    return-void
.end method

.method public setLayoutInflater(Landroid/view/LayoutInflater;)V
    .locals 0
    .param p1, "inflater"    # Landroid/view/LayoutInflater;

    #@0
    .prologue
    .line 132
    iput-object p1, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflater:Landroid/view/LayoutInflater;

    #@2
    .line 133
    return-void
.end method

.method public setLayoutResource(I)V
    .locals 0
    .param p1, "layoutResource"    # I

    #@0
    .prologue
    .line 124
    iput p1, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mLayoutResource:I

    #@2
    .line 125
    return-void
.end method

.method public setOnInflateListener(Landroid/support/v7/internal/widget/ViewStubCompat$OnInflateListener;)V
    .locals 0
    .param p1, "inflateListener"    # Landroid/support/v7/internal/widget/ViewStubCompat$OnInflateListener;

    #@0
    .prologue
    .line 242
    iput-object p1, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflateListener:Landroid/support/v7/internal/widget/ViewStubCompat$OnInflateListener;

    #@2
    .line 243
    return-void
.end method

.method public setVisibility(I)V
    .locals 3
    .param p1, "visibility"    # I

    #@0
    .prologue
    .line 167
    iget-object v1, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflatedViewRef:Ljava/lang/ref/WeakReference;

    #@2
    if-eqz v1, :cond_2

    #@4
    .line 168
    iget-object v1, p0, Landroid/support/v7/internal/widget/ViewStubCompat;->mInflatedViewRef:Ljava/lang/ref/WeakReference;

    #@6
    invoke-virtual {v1}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    check-cast v0, Landroid/view/View;

    #@c
    .line 169
    .local v0, "view":Landroid/view/View;
    if-eqz v0, :cond_1

    #@e
    .line 170
    invoke-virtual {v0, p1}, Landroid/view/View;->setVisibility(I)V

    #@11
    .line 180
    .end local v0    # "view":Landroid/view/View;
    :cond_0
    :goto_0
    return-void

    #@12
    .line 172
    .restart local v0    # "view":Landroid/view/View;
    :cond_1
    new-instance v1, Ljava/lang/IllegalStateException;

    #@14
    const-string v2, "setVisibility called on un-referenced view"

    #@16
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@19
    throw v1

    #@1a
    .line 175
    .end local v0    # "view":Landroid/view/View;
    :cond_2
    invoke-super {p0, p1}, Landroid/view/View;->setVisibility(I)V

    #@1d
    .line 176
    if-eqz p1, :cond_3

    #@1f
    const/4 v1, 0x4

    #@20
    if-ne p1, v1, :cond_0

    #@22
    .line 177
    :cond_3
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ViewStubCompat;->inflate()Landroid/view/View;

    #@25
    goto :goto_0
.end method

.class Landroid/support/v4/view/MarginLayoutParamsCompat$MarginLayoutParamsCompatImplBase;
.super Ljava/lang/Object;
.source "MarginLayoutParamsCompat.java"

# interfaces
.implements Landroid/support/v4/view/MarginLayoutParamsCompat$MarginLayoutParamsCompatImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/MarginLayoutParamsCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "MarginLayoutParamsCompatImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 39
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getLayoutDirection(Landroid/view/ViewGroup$MarginLayoutParams;)I
    .locals 1
    .param p1, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;

    #@0
    .prologue
    .line 68
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getMarginEnd(Landroid/view/ViewGroup$MarginLayoutParams;)I
    .locals 1
    .param p1, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;

    #@0
    .prologue
    .line 48
    iget v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@2
    return v0
.end method

.method public getMarginStart(Landroid/view/ViewGroup$MarginLayoutParams;)I
    .locals 1
    .param p1, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;

    #@0
    .prologue
    .line 43
    iget v0, p1, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@2
    return v0
.end method

.method public isMarginRelative(Landroid/view/ViewGroup$MarginLayoutParams;)Z
    .locals 1
    .param p1, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;

    #@0
    .prologue
    .line 63
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public resolveLayoutDirection(Landroid/view/ViewGroup$MarginLayoutParams;I)V
    .locals 0
    .param p1, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;
    .param p2, "layoutDirection"    # I

    #@0
    .prologue
    .line 79
    return-void
.end method

.method public setLayoutDirection(Landroid/view/ViewGroup$MarginLayoutParams;I)V
    .locals 0
    .param p1, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;
    .param p2, "layoutDirection"    # I

    #@0
    .prologue
    .line 74
    return-void
.end method

.method public setMarginEnd(Landroid/view/ViewGroup$MarginLayoutParams;I)V
    .locals 0
    .param p1, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;
    .param p2, "marginEnd"    # I

    #@0
    .prologue
    .line 58
    iput p2, p1, Landroid/view/ViewGroup$MarginLayoutParams;->rightMargin:I

    #@2
    .line 59
    return-void
.end method

.method public setMarginStart(Landroid/view/ViewGroup$MarginLayoutParams;I)V
    .locals 0
    .param p1, "lp"    # Landroid/view/ViewGroup$MarginLayoutParams;
    .param p2, "marginStart"    # I

    #@0
    .prologue
    .line 53
    iput p2, p1, Landroid/view/ViewGroup$MarginLayoutParams;->leftMargin:I

    #@2
    .line 54
    return-void
.end method

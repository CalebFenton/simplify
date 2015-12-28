.class final Landroid/support/v4/app/FragmentTransitionCompat21$3;
.super Landroid/transition/Transition$EpicenterCallback;
.source "FragmentTransitionCompat21.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/app/FragmentTransitionCompat21;->setSharedElementEpicenter(Landroid/transition/Transition;Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# instance fields
.field private mEpicenter:Landroid/graphics/Rect;

.field final synthetic val$epicenterView:Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;


# direct methods
.method constructor <init>(Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;)V
    .locals 0

    #@0
    .prologue
    .line 207
    iput-object p1, p0, Landroid/support/v4/app/FragmentTransitionCompat21$3;->val$epicenterView:Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

    #@2
    invoke-direct {p0}, Landroid/transition/Transition$EpicenterCallback;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onGetEpicenter(Landroid/transition/Transition;)Landroid/graphics/Rect;
    .locals 1
    .param p1, "transition"    # Landroid/transition/Transition;

    #@0
    .prologue
    .line 212
    iget-object v0, p0, Landroid/support/v4/app/FragmentTransitionCompat21$3;->mEpicenter:Landroid/graphics/Rect;

    #@2
    if-nez v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v4/app/FragmentTransitionCompat21$3;->val$epicenterView:Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

    #@6
    iget-object v0, v0, Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;->epicenter:Landroid/view/View;

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 213
    iget-object v0, p0, Landroid/support/v4/app/FragmentTransitionCompat21$3;->val$epicenterView:Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

    #@c
    iget-object v0, v0, Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;->epicenter:Landroid/view/View;

    #@e
    # invokes: Landroid/support/v4/app/FragmentTransitionCompat21;->getBoundsOnScreen(Landroid/view/View;)Landroid/graphics/Rect;
    invoke-static {v0}, Landroid/support/v4/app/FragmentTransitionCompat21;->access$100(Landroid/view/View;)Landroid/graphics/Rect;

    #@11
    move-result-object v0

    #@12
    iput-object v0, p0, Landroid/support/v4/app/FragmentTransitionCompat21$3;->mEpicenter:Landroid/graphics/Rect;

    #@14
    .line 215
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/FragmentTransitionCompat21$3;->mEpicenter:Landroid/graphics/Rect;

    #@16
    return-object v0
.end method

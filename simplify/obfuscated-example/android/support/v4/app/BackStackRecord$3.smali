.class Landroid/support/v4/app/BackStackRecord$3;
.super Ljava/lang/Object;
.source "BackStackRecord.java"

# interfaces
.implements Landroid/view/ViewTreeObserver$OnPreDrawListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/app/BackStackRecord;->excludeHiddenFragmentsAfterEnter(Landroid/view/View;Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v4/app/BackStackRecord;

.field final synthetic val$containerId:I

.field final synthetic val$sceneRoot:Landroid/view/View;

.field final synthetic val$state:Landroid/support/v4/app/BackStackRecord$TransitionState;

.field final synthetic val$transition:Ljava/lang/Object;


# direct methods
.method constructor <init>(Landroid/support/v4/app/BackStackRecord;Landroid/view/View;Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V
    .locals 0

    #@0
    .prologue
    .line 1365
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord$3;->this$0:Landroid/support/v4/app/BackStackRecord;

    #@2
    iput-object p2, p0, Landroid/support/v4/app/BackStackRecord$3;->val$sceneRoot:Landroid/view/View;

    #@4
    iput-object p3, p0, Landroid/support/v4/app/BackStackRecord$3;->val$state:Landroid/support/v4/app/BackStackRecord$TransitionState;

    #@6
    iput p4, p0, Landroid/support/v4/app/BackStackRecord$3;->val$containerId:I

    #@8
    iput-object p5, p0, Landroid/support/v4/app/BackStackRecord$3;->val$transition:Ljava/lang/Object;

    #@a
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@d
    return-void
.end method


# virtual methods
.method public onPreDraw()Z
    .locals 4

    #@0
    .prologue
    .line 1367
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord$3;->val$sceneRoot:Landroid/view/View;

    #@2
    invoke-virtual {v0}, Landroid/view/View;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p0}, Landroid/view/ViewTreeObserver;->removeOnPreDrawListener(Landroid/view/ViewTreeObserver$OnPreDrawListener;)V

    #@9
    .line 1368
    iget-object v0, p0, Landroid/support/v4/app/BackStackRecord$3;->this$0:Landroid/support/v4/app/BackStackRecord;

    #@b
    iget-object v1, p0, Landroid/support/v4/app/BackStackRecord$3;->val$state:Landroid/support/v4/app/BackStackRecord$TransitionState;

    #@d
    iget v2, p0, Landroid/support/v4/app/BackStackRecord$3;->val$containerId:I

    #@f
    iget-object v3, p0, Landroid/support/v4/app/BackStackRecord$3;->val$transition:Ljava/lang/Object;

    #@11
    # invokes: Landroid/support/v4/app/BackStackRecord;->excludeHiddenFragments(Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V
    invoke-static {v0, v1, v2, v3}, Landroid/support/v4/app/BackStackRecord;->access$300(Landroid/support/v4/app/BackStackRecord;Landroid/support/v4/app/BackStackRecord$TransitionState;ILjava/lang/Object;)V

    #@14
    .line 1369
    const/4 v0, 0x1

    #@15
    return v0
.end method

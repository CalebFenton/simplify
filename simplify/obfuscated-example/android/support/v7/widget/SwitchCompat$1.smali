.class Landroid/support/v7/widget/SwitchCompat$1;
.super Landroid/view/animation/Animation;
.source "SwitchCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/widget/SwitchCompat;->animateThumbToCheckedState(Z)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/widget/SwitchCompat;

.field final synthetic val$diff:F

.field final synthetic val$startPosition:F


# direct methods
.method constructor <init>(Landroid/support/v7/widget/SwitchCompat;FF)V
    .locals 0

    #@0
    .prologue
    .line 710
    iput-object p1, p0, Landroid/support/v7/widget/SwitchCompat$1;->this$0:Landroid/support/v7/widget/SwitchCompat;

    #@2
    iput p2, p0, Landroid/support/v7/widget/SwitchCompat$1;->val$startPosition:F

    #@4
    iput p3, p0, Landroid/support/v7/widget/SwitchCompat$1;->val$diff:F

    #@6
    invoke-direct {p0}, Landroid/view/animation/Animation;-><init>()V

    #@9
    return-void
.end method


# virtual methods
.method protected applyTransformation(FLandroid/view/animation/Transformation;)V
    .locals 3
    .param p1, "interpolatedTime"    # F
    .param p2, "t"    # Landroid/view/animation/Transformation;

    #@0
    .prologue
    .line 713
    iget-object v0, p0, Landroid/support/v7/widget/SwitchCompat$1;->this$0:Landroid/support/v7/widget/SwitchCompat;

    #@2
    iget v1, p0, Landroid/support/v7/widget/SwitchCompat$1;->val$startPosition:F

    #@4
    iget v2, p0, Landroid/support/v7/widget/SwitchCompat$1;->val$diff:F

    #@6
    mul-float/2addr v2, p1

    #@7
    add-float/2addr v1, v2

    #@8
    # invokes: Landroid/support/v7/widget/SwitchCompat;->setThumbPosition(F)V
    invoke-static {v0, v1}, Landroid/support/v7/widget/SwitchCompat;->access$000(Landroid/support/v7/widget/SwitchCompat;F)V

    #@b
    .line 714
    return-void
.end method

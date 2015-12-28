.class Landroid/support/v4/widget/MaterialProgressDrawable$1;
.super Landroid/view/animation/Animation;
.source "MaterialProgressDrawable.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/widget/MaterialProgressDrawable;->setupAnimators()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v4/widget/MaterialProgressDrawable;

.field final synthetic val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;


# direct methods
.method constructor <init>(Landroid/support/v4/widget/MaterialProgressDrawable;Landroid/support/v4/widget/MaterialProgressDrawable$Ring;)V
    .locals 0

    #@0
    .prologue
    .line 312
    iput-object p1, p0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->this$0:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@2
    iput-object p2, p0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@4
    invoke-direct {p0}, Landroid/view/animation/Animation;-><init>()V

    #@7
    return-void
.end method


# virtual methods
.method public applyTransformation(FLandroid/view/animation/Transformation;)V
    .locals 18
    .param p1, "interpolatedTime"    # F
    .param p2, "t"    # Landroid/view/animation/Transformation;

    #@0
    .prologue
    .line 315
    move-object/from16 v0, p0

    #@2
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->this$0:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@4
    iget-boolean v11, v11, Landroid/support/v4/widget/MaterialProgressDrawable;->mFinishing:Z

    #@6
    if-eqz v11, :cond_0

    #@8
    .line 316
    move-object/from16 v0, p0

    #@a
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->this$0:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@c
    move-object/from16 v0, p0

    #@e
    iget-object v12, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@10
    move/from16 v0, p1

    #@12
    # invokes: Landroid/support/v4/widget/MaterialProgressDrawable;->applyFinishTranslation(FLandroid/support/v4/widget/MaterialProgressDrawable$Ring;)V
    invoke-static {v11, v0, v12}, Landroid/support/v4/widget/MaterialProgressDrawable;->access$200(Landroid/support/v4/widget/MaterialProgressDrawable;FLandroid/support/v4/widget/MaterialProgressDrawable$Ring;)V

    #@15
    .line 345
    :goto_0
    return-void

    #@16
    .line 321
    :cond_0
    move-object/from16 v0, p0

    #@18
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@1a
    invoke-virtual {v11}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStrokeWidth()F

    #@1d
    move-result v11

    #@1e
    float-to-double v12, v11

    #@1f
    const-wide v14, 0x401921fb54442d18L    # 6.283185307179586

    #@24
    move-object/from16 v0, p0

    #@26
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@28
    invoke-virtual {v11}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getCenterRadius()D

    #@2b
    move-result-wide v16

    #@2c
    mul-double v14, v14, v16

    #@2e
    div-double/2addr v12, v14

    #@2f
    invoke-static {v12, v13}, Ljava/lang/Math;->toRadians(D)D

    #@32
    move-result-wide v12

    #@33
    double-to-float v5, v12

    #@34
    .line 323
    .local v5, "minProgressArc":F
    move-object/from16 v0, p0

    #@36
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@38
    invoke-virtual {v11}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingEndTrim()F

    #@3b
    move-result v8

    #@3c
    .line 324
    .local v8, "startingEndTrim":F
    move-object/from16 v0, p0

    #@3e
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@40
    invoke-virtual {v11}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingStartTrim()F

    #@43
    move-result v10

    #@44
    .line 325
    .local v10, "startingTrim":F
    move-object/from16 v0, p0

    #@46
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@48
    invoke-virtual {v11}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->getStartingRotation()F

    #@4b
    move-result v9

    #@4c
    .line 329
    .local v9, "startingRotation":F
    const v11, 0x3f4ccccd    # 0.8f

    #@4f
    sub-float v4, v11, v5

    #@51
    .line 330
    .local v4, "minArc":F
    # getter for: Landroid/support/v4/widget/MaterialProgressDrawable;->START_CURVE_INTERPOLATOR:Landroid/view/animation/Interpolator;
    invoke-static {}, Landroid/support/v4/widget/MaterialProgressDrawable;->access$300()Landroid/view/animation/Interpolator;

    #@54
    move-result-object v11

    #@55
    move/from16 v0, p1

    #@57
    invoke-interface {v11, v0}, Landroid/view/animation/Interpolator;->getInterpolation(F)F

    #@5a
    move-result v11

    #@5b
    mul-float/2addr v11, v4

    #@5c
    add-float v2, v8, v11

    #@5e
    .line 332
    .local v2, "endTrim":F
    move-object/from16 v0, p0

    #@60
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@62
    invoke-virtual {v11, v2}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setEndTrim(F)V

    #@65
    .line 334
    const v11, 0x3f4ccccd    # 0.8f

    #@68
    # getter for: Landroid/support/v4/widget/MaterialProgressDrawable;->END_CURVE_INTERPOLATOR:Landroid/view/animation/Interpolator;
    invoke-static {}, Landroid/support/v4/widget/MaterialProgressDrawable;->access$400()Landroid/view/animation/Interpolator;

    #@6b
    move-result-object v12

    #@6c
    move/from16 v0, p1

    #@6e
    invoke-interface {v12, v0}, Landroid/view/animation/Interpolator;->getInterpolation(F)F

    #@71
    move-result v12

    #@72
    mul-float/2addr v11, v12

    #@73
    add-float v7, v10, v11

    #@75
    .line 336
    .local v7, "startTrim":F
    move-object/from16 v0, p0

    #@77
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@79
    invoke-virtual {v11, v7}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setStartTrim(F)V

    #@7c
    .line 338
    const/high16 v11, 0x3e800000    # 0.25f

    #@7e
    mul-float v11, v11, p1

    #@80
    add-float v6, v9, v11

    #@82
    .line 339
    .local v6, "rotation":F
    move-object/from16 v0, p0

    #@84
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->val$ring:Landroid/support/v4/widget/MaterialProgressDrawable$Ring;

    #@86
    invoke-virtual {v11, v6}, Landroid/support/v4/widget/MaterialProgressDrawable$Ring;->setRotation(F)V

    #@89
    .line 341
    const/high16 v11, 0x43100000    # 144.0f

    #@8b
    mul-float v11, v11, p1

    #@8d
    const/high16 v12, 0x44340000    # 720.0f

    #@8f
    move-object/from16 v0, p0

    #@91
    iget-object v13, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->this$0:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@93
    # getter for: Landroid/support/v4/widget/MaterialProgressDrawable;->mRotationCount:F
    invoke-static {v13}, Landroid/support/v4/widget/MaterialProgressDrawable;->access$500(Landroid/support/v4/widget/MaterialProgressDrawable;)F

    #@96
    move-result v13

    #@97
    const/high16 v14, 0x40a00000    # 5.0f

    #@99
    div-float/2addr v13, v14

    #@9a
    mul-float/2addr v12, v13

    #@9b
    add-float v3, v11, v12

    #@9d
    .line 343
    .local v3, "groupRotation":F
    move-object/from16 v0, p0

    #@9f
    iget-object v11, v0, Landroid/support/v4/widget/MaterialProgressDrawable$1;->this$0:Landroid/support/v4/widget/MaterialProgressDrawable;

    #@a1
    invoke-virtual {v11, v3}, Landroid/support/v4/widget/MaterialProgressDrawable;->setRotation(F)V

    #@a4
    goto/16 :goto_0
.end method

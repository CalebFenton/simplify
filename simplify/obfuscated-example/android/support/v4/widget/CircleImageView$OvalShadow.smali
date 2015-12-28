.class Landroid/support/v4/widget/CircleImageView$OvalShadow;
.super Landroid/graphics/drawable/shapes/OvalShape;
.source "CircleImageView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/CircleImageView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "OvalShadow"
.end annotation


# instance fields
.field private mCircleDiameter:I

.field private mRadialGradient:Landroid/graphics/RadialGradient;

.field private mShadowPaint:Landroid/graphics/Paint;

.field private mShadowRadius:I

.field final synthetic this$0:Landroid/support/v4/widget/CircleImageView;


# direct methods
.method public constructor <init>(Landroid/support/v4/widget/CircleImageView;II)V
    .locals 7
    .param p2, "shadowRadius"    # I
    .param p3, "circleDiameter"    # I

    #@0
    .prologue
    .line 128
    iput-object p1, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->this$0:Landroid/support/v4/widget/CircleImageView;

    #@2
    .line 129
    invoke-direct {p0}, Landroid/graphics/drawable/shapes/OvalShape;-><init>()V

    #@5
    .line 130
    new-instance v0, Landroid/graphics/Paint;

    #@7
    invoke-direct {v0}, Landroid/graphics/Paint;-><init>()V

    #@a
    iput-object v0, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mShadowPaint:Landroid/graphics/Paint;

    #@c
    .line 131
    iput p2, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mShadowRadius:I

    #@e
    .line 132
    iput p3, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mCircleDiameter:I

    #@10
    .line 133
    new-instance v0, Landroid/graphics/RadialGradient;

    #@12
    iget v1, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mCircleDiameter:I

    #@14
    div-int/lit8 v1, v1, 0x2

    #@16
    int-to-float v1, v1

    #@17
    iget v2, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mCircleDiameter:I

    #@19
    div-int/lit8 v2, v2, 0x2

    #@1b
    int-to-float v2, v2

    #@1c
    iget v3, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mShadowRadius:I

    #@1e
    int-to-float v3, v3

    #@1f
    const/4 v4, 0x2

    #@20
    new-array v4, v4, [I

    #@22
    fill-array-data v4, :array_0

    #@25
    const/4 v5, 0x0

    #@26
    sget-object v6, Landroid/graphics/Shader$TileMode;->CLAMP:Landroid/graphics/Shader$TileMode;

    #@28
    invoke-direct/range {v0 .. v6}, Landroid/graphics/RadialGradient;-><init>(FFF[I[FLandroid/graphics/Shader$TileMode;)V

    #@2b
    iput-object v0, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mRadialGradient:Landroid/graphics/RadialGradient;

    #@2d
    .line 137
    iget-object v0, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mShadowPaint:Landroid/graphics/Paint;

    #@2f
    iget-object v1, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mRadialGradient:Landroid/graphics/RadialGradient;

    #@31
    invoke-virtual {v0, v1}, Landroid/graphics/Paint;->setShader(Landroid/graphics/Shader;)Landroid/graphics/Shader;

    #@34
    .line 138
    return-void

    #@35
    .line 133
    nop

    #@36
    :array_0
    .array-data 4
        0x3d000000    # 0.03125f
        0x0
    .end array-data
.end method


# virtual methods
.method public draw(Landroid/graphics/Canvas;Landroid/graphics/Paint;)V
    .locals 6
    .param p1, "canvas"    # Landroid/graphics/Canvas;
    .param p2, "paint"    # Landroid/graphics/Paint;

    #@0
    .prologue
    .line 142
    iget-object v2, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->this$0:Landroid/support/v4/widget/CircleImageView;

    #@2
    invoke-virtual {v2}, Landroid/support/v4/widget/CircleImageView;->getWidth()I

    #@5
    move-result v1

    #@6
    .line 143
    .local v1, "viewWidth":I
    iget-object v2, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->this$0:Landroid/support/v4/widget/CircleImageView;

    #@8
    invoke-virtual {v2}, Landroid/support/v4/widget/CircleImageView;->getHeight()I

    #@b
    move-result v0

    #@c
    .line 144
    .local v0, "viewHeight":I
    div-int/lit8 v2, v1, 0x2

    #@e
    int-to-float v2, v2

    #@f
    div-int/lit8 v3, v0, 0x2

    #@11
    int-to-float v3, v3

    #@12
    iget v4, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mCircleDiameter:I

    #@14
    div-int/lit8 v4, v4, 0x2

    #@16
    iget v5, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mShadowRadius:I

    #@18
    add-int/2addr v4, v5

    #@19
    int-to-float v4, v4

    #@1a
    iget-object v5, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mShadowPaint:Landroid/graphics/Paint;

    #@1c
    invoke-virtual {p1, v2, v3, v4, v5}, Landroid/graphics/Canvas;->drawCircle(FFFLandroid/graphics/Paint;)V

    #@1f
    .line 146
    div-int/lit8 v2, v1, 0x2

    #@21
    int-to-float v2, v2

    #@22
    div-int/lit8 v3, v0, 0x2

    #@24
    int-to-float v3, v3

    #@25
    iget v4, p0, Landroid/support/v4/widget/CircleImageView$OvalShadow;->mCircleDiameter:I

    #@27
    div-int/lit8 v4, v4, 0x2

    #@29
    int-to-float v4, v4

    #@2a
    invoke-virtual {p1, v2, v3, v4, p2}, Landroid/graphics/Canvas;->drawCircle(FFFLandroid/graphics/Paint;)V

    #@2d
    .line 147
    return-void
.end method

.class public Landroid/support/v7/internal/widget/RtlSpacingHelper;
.super Ljava/lang/Object;
.source "RtlSpacingHelper.java"


# static fields
.field public static final UNDEFINED:I = -0x80000000


# instance fields
.field private mEnd:I

.field private mExplicitLeft:I

.field private mExplicitRight:I

.field private mIsRelative:Z

.field private mIsRtl:Z

.field private mLeft:I

.field private mRight:I

.field private mStart:I


# direct methods
.method public constructor <init>()V
    .locals 2

    #@0
    .prologue
    const/high16 v1, -0x80000000

    #@2
    const/4 v0, 0x0

    #@3
    .line 27
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@6
    .line 30
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@8
    .line 31
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@a
    .line 32
    iput v1, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mStart:I

    #@c
    .line 33
    iput v1, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mEnd:I

    #@e
    .line 34
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitLeft:I

    #@10
    .line 35
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitRight:I

    #@12
    .line 37
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRtl:Z

    #@14
    .line 38
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRelative:Z

    #@16
    return-void
.end method


# virtual methods
.method public getEnd()I
    .locals 1

    #@0
    .prologue
    .line 53
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRtl:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@6
    :goto_0
    return v0

    #@7
    :cond_0
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@9
    goto :goto_0
.end method

.method public getLeft()I
    .locals 1

    #@0
    .prologue
    .line 41
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@2
    return v0
.end method

.method public getRight()I
    .locals 1

    #@0
    .prologue
    .line 45
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@2
    return v0
.end method

.method public getStart()I
    .locals 1

    #@0
    .prologue
    .line 49
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRtl:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@6
    :goto_0
    return v0

    #@7
    :cond_0
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@9
    goto :goto_0
.end method

.method public setAbsolute(II)V
    .locals 2
    .param p1, "left"    # I
    .param p2, "right"    # I

    #@0
    .prologue
    const/high16 v1, -0x80000000

    #@2
    .line 70
    const/4 v0, 0x0

    #@3
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRelative:Z

    #@5
    .line 71
    if-eq p1, v1, :cond_0

    #@7
    iput p1, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitLeft:I

    #@9
    iput p1, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@b
    .line 72
    :cond_0
    if-eq p2, v1, :cond_1

    #@d
    iput p2, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitRight:I

    #@f
    iput p2, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@11
    .line 73
    :cond_1
    return-void
.end method

.method public setDirection(Z)V
    .locals 2
    .param p1, "isRtl"    # Z

    #@0
    .prologue
    const/high16 v1, -0x80000000

    #@2
    .line 76
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRtl:Z

    #@4
    if-ne p1, v0, :cond_0

    #@6
    .line 92
    :goto_0
    return-void

    #@7
    .line 79
    :cond_0
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRtl:Z

    #@9
    .line 80
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRelative:Z

    #@b
    if-eqz v0, :cond_6

    #@d
    .line 81
    if-eqz p1, :cond_3

    #@f
    .line 82
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mEnd:I

    #@11
    if-eq v0, v1, :cond_1

    #@13
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mEnd:I

    #@15
    :goto_1
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@17
    .line 83
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mStart:I

    #@19
    if-eq v0, v1, :cond_2

    #@1b
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mStart:I

    #@1d
    :goto_2
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@1f
    goto :goto_0

    #@20
    .line 82
    :cond_1
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitLeft:I

    #@22
    goto :goto_1

    #@23
    .line 83
    :cond_2
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitRight:I

    #@25
    goto :goto_2

    #@26
    .line 85
    :cond_3
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mStart:I

    #@28
    if-eq v0, v1, :cond_4

    #@2a
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mStart:I

    #@2c
    :goto_3
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@2e
    .line 86
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mEnd:I

    #@30
    if-eq v0, v1, :cond_5

    #@32
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mEnd:I

    #@34
    :goto_4
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@36
    goto :goto_0

    #@37
    .line 85
    :cond_4
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitLeft:I

    #@39
    goto :goto_3

    #@3a
    .line 86
    :cond_5
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitRight:I

    #@3c
    goto :goto_4

    #@3d
    .line 89
    :cond_6
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitLeft:I

    #@3f
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@41
    .line 90
    iget v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mExplicitRight:I

    #@43
    iput v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@45
    goto :goto_0
.end method

.method public setRelative(II)V
    .locals 2
    .param p1, "start"    # I
    .param p2, "end"    # I

    #@0
    .prologue
    const/high16 v1, -0x80000000

    #@2
    .line 57
    iput p1, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mStart:I

    #@4
    .line 58
    iput p2, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mEnd:I

    #@6
    .line 59
    const/4 v0, 0x1

    #@7
    iput-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRelative:Z

    #@9
    .line 60
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mIsRtl:Z

    #@b
    if-eqz v0, :cond_2

    #@d
    .line 61
    if-eq p2, v1, :cond_0

    #@f
    iput p2, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@11
    .line 62
    :cond_0
    if-eq p1, v1, :cond_1

    #@13
    iput p1, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@15
    .line 67
    :cond_1
    :goto_0
    return-void

    #@16
    .line 64
    :cond_2
    if-eq p1, v1, :cond_3

    #@18
    iput p1, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mLeft:I

    #@1a
    .line 65
    :cond_3
    if-eq p2, v1, :cond_1

    #@1c
    iput p2, p0, Landroid/support/v7/internal/widget/RtlSpacingHelper;->mRight:I

    #@1e
    goto :goto_0
.end method

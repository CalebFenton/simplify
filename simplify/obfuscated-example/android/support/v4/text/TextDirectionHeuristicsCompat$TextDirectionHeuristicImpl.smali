.class abstract Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;
.super Ljava/lang/Object;
.source "TextDirectionHeuristicsCompat.java"

# interfaces
.implements Landroid/support/v4/text/TextDirectionHeuristicCompat;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/text/TextDirectionHeuristicsCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x40a
    name = "TextDirectionHeuristicImpl"
.end annotation


# instance fields
.field private final mAlgorithm:Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;


# direct methods
.method public constructor <init>(Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;)V
    .locals 0
    .param p1, "algorithm"    # Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;

    #@0
    .prologue
    .line 114
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 115
    iput-object p1, p0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;->mAlgorithm:Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;

    #@5
    .line 116
    return-void
.end method

.method private doCheck(Ljava/lang/CharSequence;II)Z
    .locals 1
    .param p1, "cs"    # Ljava/lang/CharSequence;
    .param p2, "start"    # I
    .param p3, "count"    # I

    #@0
    .prologue
    .line 140
    iget-object v0, p0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;->mAlgorithm:Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;

    #@2
    invoke-interface {v0, p1, p2, p3}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;->checkRtl(Ljava/lang/CharSequence;II)I

    #@5
    move-result v0

    #@6
    packed-switch v0, :pswitch_data_0

    #@9
    .line 146
    invoke-virtual {p0}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;->defaultIsRtl()Z

    #@c
    move-result v0

    #@d
    :goto_0
    return v0

    #@e
    .line 142
    :pswitch_0
    const/4 v0, 0x1

    #@f
    goto :goto_0

    #@10
    .line 144
    :pswitch_1
    const/4 v0, 0x0

    #@11
    goto :goto_0

    #@12
    .line 140
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method


# virtual methods
.method protected abstract defaultIsRtl()Z
.end method

.method public isRtl(Ljava/lang/CharSequence;II)Z
    .locals 1
    .param p1, "cs"    # Ljava/lang/CharSequence;
    .param p2, "start"    # I
    .param p3, "count"    # I

    #@0
    .prologue
    .line 130
    if-eqz p1, :cond_0

    #@2
    if-ltz p2, :cond_0

    #@4
    if-ltz p3, :cond_0

    #@6
    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    #@9
    move-result v0

    #@a
    sub-int/2addr v0, p3

    #@b
    if-ge v0, p2, :cond_1

    #@d
    .line 131
    :cond_0
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@f
    invoke-direct {v0}, Ljava/lang/IllegalArgumentException;-><init>()V

    #@12
    throw v0

    #@13
    .line 133
    :cond_1
    iget-object v0, p0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;->mAlgorithm:Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;

    #@15
    if-nez v0, :cond_2

    #@17
    .line 134
    invoke-virtual {p0}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;->defaultIsRtl()Z

    #@1a
    move-result v0

    #@1b
    .line 136
    :goto_0
    return v0

    #@1c
    :cond_2
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;->doCheck(Ljava/lang/CharSequence;II)Z

    #@1f
    move-result v0

    #@20
    goto :goto_0
.end method

.method public isRtl([CII)Z
    .locals 1
    .param p1, "array"    # [C
    .param p2, "start"    # I
    .param p3, "count"    # I

    #@0
    .prologue
    .line 125
    invoke-static {p1}, Ljava/nio/CharBuffer;->wrap([C)Ljava/nio/CharBuffer;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {p0, v0, p2, p3}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;->isRtl(Ljava/lang/CharSequence;II)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

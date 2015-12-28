.class Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicInternal;
.super Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;
.source "TextDirectionHeuristicsCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/text/TextDirectionHeuristicsCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "TextDirectionHeuristicInternal"
.end annotation


# instance fields
.field private final mDefaultIsRtl:Z


# direct methods
.method private constructor <init>(Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;Z)V
    .locals 0
    .param p1, "algorithm"    # Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;
    .param p2, "defaultIsRtl"    # Z

    #@0
    .prologue
    .line 156
    invoke-direct {p0, p1}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;-><init>(Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;)V

    #@3
    .line 157
    iput-boolean p2, p0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicInternal;->mDefaultIsRtl:Z

    #@5
    .line 158
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;ZLandroid/support/v4/text/TextDirectionHeuristicsCompat$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;
    .param p2, "x1"    # Z
    .param p3, "x2"    # Landroid/support/v4/text/TextDirectionHeuristicsCompat$1;

    #@0
    .prologue
    .line 151
    invoke-direct {p0, p1, p2}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicInternal;-><init>(Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;Z)V

    #@3
    return-void
.end method


# virtual methods
.method protected defaultIsRtl()Z
    .locals 1

    #@0
    .prologue
    .line 162
    iget-boolean v0, p0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicInternal;->mDefaultIsRtl:Z

    #@2
    return v0
.end method

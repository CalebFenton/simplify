.class Landroid/support/v4/view/ScaleGestureDetectorCompat$BaseScaleGestureDetectorImpl;
.super Ljava/lang/Object;
.source "ScaleGestureDetectorCompat.java"

# interfaces
.implements Landroid/support/v4/view/ScaleGestureDetectorCompat$ScaleGestureDetectorImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/ScaleGestureDetectorCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "BaseScaleGestureDetectorImpl"
.end annotation


# direct methods
.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 33
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v4/view/ScaleGestureDetectorCompat$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v4/view/ScaleGestureDetectorCompat$1;

    #@0
    .prologue
    .line 33
    invoke-direct {p0}, Landroid/support/v4/view/ScaleGestureDetectorCompat$BaseScaleGestureDetectorImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public isQuickScaleEnabled(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "o"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 41
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public setQuickScaleEnabled(Ljava/lang/Object;Z)V
    .locals 0
    .param p1, "o"    # Ljava/lang/Object;
    .param p2, "enabled"    # Z

    #@0
    .prologue
    .line 37
    return-void
.end method

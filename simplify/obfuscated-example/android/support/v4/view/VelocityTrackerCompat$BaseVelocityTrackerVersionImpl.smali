.class Landroid/support/v4/view/VelocityTrackerCompat$BaseVelocityTrackerVersionImpl;
.super Ljava/lang/Object;
.source "VelocityTrackerCompat.java"

# interfaces
.implements Landroid/support/v4/view/VelocityTrackerCompat$VelocityTrackerVersionImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/VelocityTrackerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "BaseVelocityTrackerVersionImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 37
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getXVelocity(Landroid/view/VelocityTracker;I)F
    .locals 1
    .param p1, "tracker"    # Landroid/view/VelocityTracker;
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 40
    invoke-virtual {p1}, Landroid/view/VelocityTracker;->getXVelocity()F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getYVelocity(Landroid/view/VelocityTracker;I)F
    .locals 1
    .param p1, "tracker"    # Landroid/view/VelocityTracker;
    .param p2, "pointerId"    # I

    #@0
    .prologue
    .line 44
    invoke-virtual {p1}, Landroid/view/VelocityTracker;->getYVelocity()F

    #@3
    move-result v0

    #@4
    return v0
.end method

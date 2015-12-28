.class Landroid/support/v4/widget/ScrollerCompatIcs;
.super Ljava/lang/Object;
.source "ScrollerCompatIcs.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getCurrVelocity(Ljava/lang/Object;)F
    .locals 1
    .param p0, "scroller"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 26
    check-cast p0, Landroid/widget/OverScroller;

    #@2
    .end local p0    # "scroller":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/widget/OverScroller;->getCurrVelocity()F

    #@5
    move-result v0

    #@6
    return v0
.end method

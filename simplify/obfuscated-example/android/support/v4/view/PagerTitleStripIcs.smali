.class Landroid/support/v4/view/PagerTitleStripIcs;
.super Ljava/lang/Object;
.source "PagerTitleStripIcs.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/view/PagerTitleStripIcs$SingleLineAllCapsTransform;
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 26
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 31
    return-void
.end method

.method public static setSingleLineAllCaps(Landroid/widget/TextView;)V
    .locals 2
    .param p0, "text"    # Landroid/widget/TextView;

    #@0
    .prologue
    .line 28
    new-instance v0, Landroid/support/v4/view/PagerTitleStripIcs$SingleLineAllCapsTransform;

    #@2
    invoke-virtual {p0}, Landroid/widget/TextView;->getContext()Landroid/content/Context;

    #@5
    move-result-object v1

    #@6
    invoke-direct {v0, v1}, Landroid/support/v4/view/PagerTitleStripIcs$SingleLineAllCapsTransform;-><init>(Landroid/content/Context;)V

    #@9
    invoke-virtual {p0, v0}, Landroid/widget/TextView;->setTransformationMethod(Landroid/text/method/TransformationMethod;)V

    #@c
    .line 29
    return-void
.end method

.class Landroid/support/v4/app/ShareCompatJB;
.super Ljava/lang/Object;
.source "ShareCompatJB.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static escapeHtml(Ljava/lang/CharSequence;)Ljava/lang/String;
    .locals 1
    .param p0, "html"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 23
    invoke-static {p0}, Landroid/text/Html;->escapeHtml(Ljava/lang/CharSequence;)Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

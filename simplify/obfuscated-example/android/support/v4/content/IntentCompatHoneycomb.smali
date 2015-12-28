.class Landroid/support/v4/content/IntentCompatHoneycomb;
.super Ljava/lang/Object;
.source "IntentCompatHoneycomb.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 22
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static makeMainActivity(Landroid/content/ComponentName;)Landroid/content/Intent;
    .locals 1
    .param p0, "mainActivity"    # Landroid/content/ComponentName;

    #@0
    .prologue
    .line 24
    invoke-static {p0}, Landroid/content/Intent;->makeMainActivity(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static makeRestartActivityTask(Landroid/content/ComponentName;)Landroid/content/Intent;
    .locals 1
    .param p0, "mainActivity"    # Landroid/content/ComponentName;

    #@0
    .prologue
    .line 28
    invoke-static {p0}, Landroid/content/Intent;->makeRestartActivityTask(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

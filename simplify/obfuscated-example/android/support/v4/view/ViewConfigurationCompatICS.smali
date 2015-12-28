.class Landroid/support/v4/view/ViewConfigurationCompatICS;
.super Ljava/lang/Object;
.source "ViewConfigurationCompatICS.java"


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

.method static hasPermanentMenuKey(Landroid/view/ViewConfiguration;)Z
    .locals 1
    .param p0, "config"    # Landroid/view/ViewConfiguration;

    #@0
    .prologue
    .line 26
    invoke-virtual {p0}, Landroid/view/ViewConfiguration;->hasPermanentMenuKey()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.class Landroid/support/v4/app/NavUtilsJB;
.super Ljava/lang/Object;
.source "NavUtilsJB.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getParentActivityIntent(Landroid/app/Activity;)Landroid/content/Intent;
    .locals 1
    .param p0, "activity"    # Landroid/app/Activity;

    #@0
    .prologue
    .line 25
    invoke-virtual {p0}, Landroid/app/Activity;->getParentActivityIntent()Landroid/content/Intent;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static getParentActivityName(Landroid/content/pm/ActivityInfo;)Ljava/lang/String;
    .locals 1
    .param p0, "info"    # Landroid/content/pm/ActivityInfo;

    #@0
    .prologue
    .line 37
    iget-object v0, p0, Landroid/content/pm/ActivityInfo;->parentActivityName:Ljava/lang/String;

    #@2
    return-object v0
.end method

.method public static navigateUpTo(Landroid/app/Activity;Landroid/content/Intent;)V
    .locals 0
    .param p0, "activity"    # Landroid/app/Activity;
    .param p1, "upIntent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 33
    invoke-virtual {p0, p1}, Landroid/app/Activity;->navigateUpTo(Landroid/content/Intent;)Z

    #@3
    .line 34
    return-void
.end method

.method public static shouldUpRecreateTask(Landroid/app/Activity;Landroid/content/Intent;)Z
    .locals 1
    .param p0, "activity"    # Landroid/app/Activity;
    .param p1, "targetIntent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 29
    invoke-virtual {p0, p1}, Landroid/app/Activity;->shouldUpRecreateTask(Landroid/content/Intent;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

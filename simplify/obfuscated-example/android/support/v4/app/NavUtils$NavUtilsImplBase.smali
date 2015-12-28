.class Landroid/support/v4/app/NavUtils$NavUtilsImplBase;
.super Ljava/lang/Object;
.source "NavUtils.java"

# interfaces
.implements Landroid/support/v4/app/NavUtils$NavUtilsImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/NavUtils;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "NavUtilsImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 49
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getParentActivityIntent(Landroid/app/Activity;)Landroid/content/Intent;
    .locals 8
    .param p1, "activity"    # Landroid/app/Activity;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 53
    invoke-static {p1}, Landroid/support/v4/app/NavUtils;->getParentActivityName(Landroid/app/Activity;)Ljava/lang/String;

    #@4
    move-result-object v3

    #@5
    .line 54
    .local v3, "parentName":Ljava/lang/String;
    if-nez v3, :cond_0

    #@7
    .line 67
    :goto_0
    return-object v2

    #@8
    .line 57
    :cond_0
    new-instance v4, Landroid/content/ComponentName;

    #@a
    invoke-direct {v4, p1, v3}, Landroid/content/ComponentName;-><init>(Landroid/content/Context;Ljava/lang/String;)V

    #@d
    .line 59
    .local v4, "target":Landroid/content/ComponentName;
    :try_start_0
    invoke-static {p1, v4}, Landroid/support/v4/app/NavUtils;->getParentActivityName(Landroid/content/Context;Landroid/content/ComponentName;)Ljava/lang/String;

    #@10
    move-result-object v1

    #@11
    .line 60
    .local v1, "grandparent":Ljava/lang/String;
    if-nez v1, :cond_1

    #@13
    invoke-static {v4}, Landroid/support/v4/content/IntentCompat;->makeMainActivity(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@16
    move-result-object v2

    #@17
    .line 63
    .local v2, "parentIntent":Landroid/content/Intent;
    :goto_1
    goto :goto_0

    #@18
    .line 60
    .end local v2    # "parentIntent":Landroid/content/Intent;
    :cond_1
    new-instance v5, Landroid/content/Intent;

    #@1a
    invoke-direct {v5}, Landroid/content/Intent;-><init>()V

    #@1d
    invoke-virtual {v5, v4}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;
    :try_end_0
    .catch Landroid/content/pm/PackageManager$NameNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@20
    move-result-object v2

    #@21
    goto :goto_1

    #@22
    .line 64
    .end local v1    # "grandparent":Ljava/lang/String;
    :catch_0
    move-exception v0

    #@23
    .line 65
    .local v0, "e":Landroid/content/pm/PackageManager$NameNotFoundException;
    const-string v5, "NavUtils"

    #@25
    new-instance v6, Ljava/lang/StringBuilder;

    #@27
    invoke-direct {v6}, Ljava/lang/StringBuilder;-><init>()V

    #@2a
    const-string v7, "getParentActivityIntent: bad parentActivityName \'"

    #@2c
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2f
    move-result-object v6

    #@30
    invoke-virtual {v6, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@33
    move-result-object v6

    #@34
    const-string v7, "\' in manifest"

    #@36
    invoke-virtual {v6, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@39
    move-result-object v6

    #@3a
    invoke-virtual {v6}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@3d
    move-result-object v6

    #@3e
    invoke-static {v5, v6}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@41
    goto :goto_0
.end method

.method public getParentActivityName(Landroid/content/Context;Landroid/content/pm/ActivityInfo;)Ljava/lang/String;
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "info"    # Landroid/content/pm/ActivityInfo;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 86
    iget-object v2, p2, Landroid/content/pm/ActivityInfo;->metaData:Landroid/os/Bundle;

    #@3
    if-nez v2, :cond_1

    #@5
    move-object v0, v1

    #@6
    .line 92
    :cond_0
    :goto_0
    return-object v0

    #@7
    .line 87
    :cond_1
    iget-object v2, p2, Landroid/content/pm/ActivityInfo;->metaData:Landroid/os/Bundle;

    #@9
    const-string v3, "android.support.PARENT_ACTIVITY"

    #@b
    invoke-virtual {v2, v3}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    #@e
    move-result-object v0

    #@f
    .line 88
    .local v0, "parentActivity":Ljava/lang/String;
    if-nez v0, :cond_2

    #@11
    move-object v0, v1

    #@12
    goto :goto_0

    #@13
    .line 89
    :cond_2
    const/4 v1, 0x0

    #@14
    invoke-virtual {v0, v1}, Ljava/lang/String;->charAt(I)C

    #@17
    move-result v1

    #@18
    const/16 v2, 0x2e

    #@1a
    if-ne v1, v2, :cond_0

    #@1c
    .line 90
    new-instance v1, Ljava/lang/StringBuilder;

    #@1e
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    #@21
    invoke-virtual {p1}, Landroid/content/Context;->getPackageName()Ljava/lang/String;

    #@24
    move-result-object v2

    #@25
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@28
    move-result-object v1

    #@29
    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2c
    move-result-object v1

    #@2d
    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@30
    move-result-object v0

    #@31
    goto :goto_0
.end method

.method public navigateUpTo(Landroid/app/Activity;Landroid/content/Intent;)V
    .locals 1
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "upIntent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 79
    const/high16 v0, 0x4000000

    #@2
    invoke-virtual {p2, v0}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    #@5
    .line 80
    invoke-virtual {p1, p2}, Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V

    #@8
    .line 81
    invoke-virtual {p1}, Landroid/app/Activity;->finish()V

    #@b
    .line 82
    return-void
.end method

.method public shouldUpRecreateTask(Landroid/app/Activity;Landroid/content/Intent;)Z
    .locals 2
    .param p1, "activity"    # Landroid/app/Activity;
    .param p2, "targetIntent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 73
    invoke-virtual {p1}, Landroid/app/Activity;->getIntent()Landroid/content/Intent;

    #@3
    move-result-object v1

    #@4
    invoke-virtual {v1}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    #@7
    move-result-object v0

    #@8
    .line 74
    .local v0, "action":Ljava/lang/String;
    if-eqz v0, :cond_0

    #@a
    const-string v1, "android.intent.action.MAIN"

    #@c
    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@f
    move-result v1

    #@10
    if-nez v1, :cond_0

    #@12
    const/4 v1, 0x1

    #@13
    :goto_0
    return v1

    #@14
    :cond_0
    const/4 v1, 0x0

    #@15
    goto :goto_0
.end method

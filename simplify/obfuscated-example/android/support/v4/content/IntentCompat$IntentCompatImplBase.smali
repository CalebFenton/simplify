.class Landroid/support/v4/content/IntentCompat$IntentCompatImplBase;
.super Ljava/lang/Object;
.source "IntentCompat.java"

# interfaces
.implements Landroid/support/v4/content/IntentCompat$IntentCompatImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/content/IntentCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "IntentCompatImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 36
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public makeMainActivity(Landroid/content/ComponentName;)Landroid/content/Intent;
    .locals 2
    .param p1, "componentName"    # Landroid/content/ComponentName;

    #@0
    .prologue
    .line 39
    new-instance v0, Landroid/content/Intent;

    #@2
    const-string v1, "android.intent.action.MAIN"

    #@4
    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    #@7
    .line 40
    .local v0, "intent":Landroid/content/Intent;
    invoke-virtual {v0, p1}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@a
    .line 41
    const-string v1, "android.intent.category.LAUNCHER"

    #@c
    invoke-virtual {v0, v1}, Landroid/content/Intent;->addCategory(Ljava/lang/String;)Landroid/content/Intent;

    #@f
    .line 42
    return-object v0
.end method

.method public makeMainSelectorActivity(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    .locals 1
    .param p1, "selectorAction"    # Ljava/lang/String;
    .param p2, "selectorCategory"    # Ljava/lang/String;

    #@0
    .prologue
    .line 51
    new-instance v0, Landroid/content/Intent;

    #@2
    invoke-direct {v0, p1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    #@5
    .line 52
    .local v0, "intent":Landroid/content/Intent;
    invoke-virtual {v0, p2}, Landroid/content/Intent;->addCategory(Ljava/lang/String;)Landroid/content/Intent;

    #@8
    .line 53
    return-object v0
.end method

.method public makeRestartActivityTask(Landroid/content/ComponentName;)Landroid/content/Intent;
    .locals 2
    .param p1, "mainActivity"    # Landroid/content/ComponentName;

    #@0
    .prologue
    .line 58
    invoke-virtual {p0, p1}, Landroid/support/v4/content/IntentCompat$IntentCompatImplBase;->makeMainActivity(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@3
    move-result-object v0

    #@4
    .line 59
    .local v0, "intent":Landroid/content/Intent;
    const v1, 0x10008000

    #@7
    invoke-virtual {v0, v1}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    #@a
    .line 61
    return-object v0
.end method

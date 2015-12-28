.class Landroid/support/v4/content/IntentCompat$IntentCompatImplIcsMr1;
.super Landroid/support/v4/content/IntentCompat$IntentCompatImplHC;
.source "IntentCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/content/IntentCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "IntentCompatImplIcsMr1"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 76
    invoke-direct {p0}, Landroid/support/v4/content/IntentCompat$IntentCompatImplHC;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public makeMainSelectorActivity(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;
    .locals 1
    .param p1, "selectorAction"    # Ljava/lang/String;
    .param p2, "selectorCategory"    # Ljava/lang/String;

    #@0
    .prologue
    .line 79
    invoke-static {p1, p2}, Landroid/support/v4/content/IntentCompatIcsMr1;->makeMainSelectorActivity(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

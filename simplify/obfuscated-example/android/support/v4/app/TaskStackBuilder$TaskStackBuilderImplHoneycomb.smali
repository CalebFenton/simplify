.class Landroid/support/v4/app/TaskStackBuilder$TaskStackBuilderImplHoneycomb;
.super Ljava/lang/Object;
.source "TaskStackBuilder.java"

# interfaces
.implements Landroid/support/v4/app/TaskStackBuilder$TaskStackBuilderImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/TaskStackBuilder;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "TaskStackBuilderImplHoneycomb"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 90
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getPendingIntent(Landroid/content/Context;[Landroid/content/Intent;IILandroid/os/Bundle;)Landroid/app/PendingIntent;
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "intents"    # [Landroid/content/Intent;
    .param p3, "requestCode"    # I
    .param p4, "flags"    # I
    .param p5, "options"    # Landroid/os/Bundle;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 93
    new-instance v0, Landroid/content/Intent;

    #@3
    aget-object v1, p2, v2

    #@5
    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    #@8
    const v1, 0x1000c000

    #@b
    invoke-virtual {v0, v1}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    #@e
    move-result-object v0

    #@f
    aput-object v0, p2, v2

    #@11
    .line 96
    invoke-static {p1, p3, p2, p4}, Landroid/support/v4/app/TaskStackBuilderHoneycomb;->getActivitiesPendingIntent(Landroid/content/Context;I[Landroid/content/Intent;I)Landroid/app/PendingIntent;

    #@14
    move-result-object v0

    #@15
    return-object v0
.end method

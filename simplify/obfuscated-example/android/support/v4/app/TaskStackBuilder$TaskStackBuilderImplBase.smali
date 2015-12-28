.class Landroid/support/v4/app/TaskStackBuilder$TaskStackBuilderImplBase;
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
    name = "TaskStackBuilderImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 81
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getPendingIntent(Landroid/content/Context;[Landroid/content/Intent;IILandroid/os/Bundle;)Landroid/app/PendingIntent;
    .locals 2
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "intents"    # [Landroid/content/Intent;
    .param p3, "requestCode"    # I
    .param p4, "flags"    # I
    .param p5, "options"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 84
    new-instance v0, Landroid/content/Intent;

    #@2
    array-length v1, p2

    #@3
    add-int/lit8 v1, v1, -0x1

    #@5
    aget-object v1, p2, v1

    #@7
    invoke-direct {v0, v1}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    #@a
    .line 85
    .local v0, "topIntent":Landroid/content/Intent;
    const/high16 v1, 0x10000000

    #@c
    invoke-virtual {v0, v1}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    #@f
    .line 86
    invoke-static {p1, p3, v0, p4}, Landroid/app/PendingIntent;->getActivity(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    #@12
    move-result-object v1

    #@13
    return-object v1
.end method

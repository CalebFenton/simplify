.class Landroid/support/v7/app/ActionBarActivityDelegateBase$1;
.super Ljava/lang/Object;
.source "ActionBarActivityDelegateBase.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/app/ActionBarActivityDelegateBase;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;


# direct methods
.method constructor <init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V
    .locals 0

    #@0
    .prologue
    .line 111
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public run()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 114
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@3
    # getter for: Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuFeatures:I
    invoke-static {v0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$000(Landroid/support/v7/app/ActionBarActivityDelegateBase;)I

    #@6
    move-result v0

    #@7
    and-int/lit8 v0, v0, 0x1

    #@9
    if-eqz v0, :cond_0

    #@b
    .line 115
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@d
    # invokes: Landroid/support/v7/app/ActionBarActivityDelegateBase;->doInvalidatePanelMenu(I)V
    invoke-static {v0, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$100(Landroid/support/v7/app/ActionBarActivityDelegateBase;I)V

    #@10
    .line 117
    :cond_0
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@12
    # getter for: Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuFeatures:I
    invoke-static {v0}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$000(Landroid/support/v7/app/ActionBarActivityDelegateBase;)I

    #@15
    move-result v0

    #@16
    and-int/lit16 v0, v0, 0x100

    #@18
    if-eqz v0, :cond_1

    #@1a
    .line 118
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@1c
    const/16 v1, 0x8

    #@1e
    # invokes: Landroid/support/v7/app/ActionBarActivityDelegateBase;->doInvalidatePanelMenu(I)V
    invoke-static {v0, v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$100(Landroid/support/v7/app/ActionBarActivityDelegateBase;I)V

    #@21
    .line 120
    :cond_1
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@23
    # setter for: Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuPosted:Z
    invoke-static {v0, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$202(Landroid/support/v7/app/ActionBarActivityDelegateBase;Z)Z

    #@26
    .line 121
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$1;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@28
    # setter for: Landroid/support/v7/app/ActionBarActivityDelegateBase;->mInvalidatePanelMenuFeatures:I
    invoke-static {v0, v2}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$002(Landroid/support/v7/app/ActionBarActivityDelegateBase;I)I

    #@2b
    .line 122
    return-void
.end method

.class final Landroid/security/KeyChain$1;
.super Ljava/lang/Object;
.source "KeyChain.java"

# interfaces
.implements Landroid/content/ServiceConnection;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/security/KeyChain;->bind(Landroid/content/Context;)Landroid/security/KeyChain$KeyChainConnection;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# instance fields
.field volatile mConnectedAtLeastOnce:Z

.field final synthetic val$q:Ljava/util/concurrent/BlockingQueue;


# direct methods
.method constructor <init>(Ljava/util/concurrent/BlockingQueue;)V
    .registers 3

    .prologue
    .line 412
    iput-object p1, p0, Landroid/security/KeyChain$1;->val$q:Ljava/util/concurrent/BlockingQueue;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 413
    const/4 v0, 0x0

    iput-boolean v0, p0, Landroid/security/KeyChain$1;->mConnectedAtLeastOnce:Z

    return-void
.end method


# virtual methods
.method public onServiceConnected(Landroid/content/ComponentName;Landroid/os/IBinder;)V
    .registers 5
    .param p1, "name"    # Landroid/content/ComponentName;
    .param p2, "service"    # Landroid/os/IBinder;

    .prologue
    .line 415
    iget-boolean v0, p0, Landroid/security/KeyChain$1;->mConnectedAtLeastOnce:Z

    if-nez v0, :cond_10

    .line 416
    const/4 v0, 0x1

    iput-boolean v0, p0, Landroid/security/KeyChain$1;->mConnectedAtLeastOnce:Z

    .line 418
    :try_start_7
    iget-object v0, p0, Landroid/security/KeyChain$1;->val$q:Ljava/util/concurrent/BlockingQueue;

    invoke-static {p2}, Landroid/security/IKeyChainService$Stub;->asInterface(Landroid/os/IBinder;)Landroid/security/IKeyChainService;

    move-result-object v1

    invoke-interface {v0, v1}, Ljava/util/concurrent/BlockingQueue;->put(Ljava/lang/Object;)V
    :try_end_10
    .catch Ljava/lang/InterruptedException; {:try_start_7 .. :try_end_10} :catch_11

    .line 423
    :cond_10
    :goto_10
    return-void

    .line 419
    :catch_11
    move-exception v0

    goto :goto_10
.end method

.method public onServiceDisconnected(Landroid/content/ComponentName;)V
    .registers 2
    .param p1, "name"    # Landroid/content/ComponentName;

    .prologue
    .line 424
    return-void
.end method

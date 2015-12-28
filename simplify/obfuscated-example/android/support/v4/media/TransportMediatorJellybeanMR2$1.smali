.class Landroid/support/v4/media/TransportMediatorJellybeanMR2$1;
.super Ljava/lang/Object;
.source "TransportMediatorJellybeanMR2.java"

# interfaces
.implements Landroid/view/ViewTreeObserver$OnWindowAttachListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/TransportMediatorJellybeanMR2;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v4/media/TransportMediatorJellybeanMR2;


# direct methods
.method constructor <init>(Landroid/support/v4/media/TransportMediatorJellybeanMR2;)V
    .locals 0

    #@0
    .prologue
    .line 42
    iput-object p1, p0, Landroid/support/v4/media/TransportMediatorJellybeanMR2$1;->this$0:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onWindowAttached()V
    .locals 1

    #@0
    .prologue
    .line 45
    iget-object v0, p0, Landroid/support/v4/media/TransportMediatorJellybeanMR2$1;->this$0:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;->windowAttached()V

    #@5
    .line 46
    return-void
.end method

.method public onWindowDetached()V
    .locals 1

    #@0
    .prologue
    .line 49
    iget-object v0, p0, Landroid/support/v4/media/TransportMediatorJellybeanMR2$1;->this$0:Landroid/support/v4/media/TransportMediatorJellybeanMR2;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/media/TransportMediatorJellybeanMR2;->windowDetached()V

    #@5
    .line 50
    return-void
.end method

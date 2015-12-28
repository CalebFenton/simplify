.class Landroid/support/v4/print/PrintHelperKitkat$2$1$1;
.super Ljava/lang/Object;
.source "PrintHelperKitkat.java"

# interfaces
.implements Landroid/os/CancellationSignal$OnCancelListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/print/PrintHelperKitkat$2$1;->onPreExecute()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$2:Landroid/support/v4/print/PrintHelperKitkat$2$1;


# direct methods
.method constructor <init>(Landroid/support/v4/print/PrintHelperKitkat$2$1;)V
    .locals 0

    #@0
    .prologue
    .line 336
    iput-object p1, p0, Landroid/support/v4/print/PrintHelperKitkat$2$1$1;->this$2:Landroid/support/v4/print/PrintHelperKitkat$2$1;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onCancel()V
    .locals 2

    #@0
    .prologue
    .line 339
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2$1$1;->this$2:Landroid/support/v4/print/PrintHelperKitkat$2$1;

    #@2
    iget-object v0, v0, Landroid/support/v4/print/PrintHelperKitkat$2$1;->this$1:Landroid/support/v4/print/PrintHelperKitkat$2;

    #@4
    # invokes: Landroid/support/v4/print/PrintHelperKitkat$2;->cancelLoad()V
    invoke-static {v0}, Landroid/support/v4/print/PrintHelperKitkat$2;->access$100(Landroid/support/v4/print/PrintHelperKitkat$2;)V

    #@7
    .line 340
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2$1$1;->this$2:Landroid/support/v4/print/PrintHelperKitkat$2$1;

    #@9
    const/4 v1, 0x0

    #@a
    invoke-virtual {v0, v1}, Landroid/support/v4/print/PrintHelperKitkat$2$1;->cancel(Z)Z

    #@d
    .line 341
    return-void
.end method

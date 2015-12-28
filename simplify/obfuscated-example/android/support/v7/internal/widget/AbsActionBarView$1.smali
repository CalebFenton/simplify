.class Landroid/support/v7/internal/widget/AbsActionBarView$1;
.super Ljava/lang/Object;
.source "AbsActionBarView.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/internal/widget/AbsActionBarView;->postShowOverflowMenu()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/AbsActionBarView;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/AbsActionBarView;)V
    .locals 0

    #@0
    .prologue
    .line 184
    iput-object p1, p0, Landroid/support/v7/internal/widget/AbsActionBarView$1;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public run()V
    .locals 1

    #@0
    .prologue
    .line 186
    iget-object v0, p0, Landroid/support/v7/internal/widget/AbsActionBarView$1;->this$0:Landroid/support/v7/internal/widget/AbsActionBarView;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AbsActionBarView;->showOverflowMenu()Z

    #@5
    .line 187
    return-void
.end method

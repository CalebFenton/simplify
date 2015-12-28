.class Landroid/support/v7/internal/widget/ActionBarContextView$1;
.super Ljava/lang/Object;
.source "ActionBarContextView.java"

# interfaces
.implements Landroid/view/View$OnClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/internal/widget/ActionBarContextView;->initForMode(Landroid/support/v7/view/ActionMode;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/ActionBarContextView;

.field final synthetic val$mode:Landroid/support/v7/view/ActionMode;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/ActionBarContextView;Landroid/support/v7/view/ActionMode;)V
    .locals 0

    #@0
    .prologue
    .line 219
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActionBarContextView$1;->this$0:Landroid/support/v7/internal/widget/ActionBarContextView;

    #@2
    iput-object p2, p0, Landroid/support/v7/internal/widget/ActionBarContextView$1;->val$mode:Landroid/support/v7/view/ActionMode;

    #@4
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@7
    return-void
.end method


# virtual methods
.method public onClick(Landroid/view/View;)V
    .locals 1
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    .line 221
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActionBarContextView$1;->val$mode:Landroid/support/v7/view/ActionMode;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/view/ActionMode;->finish()V

    #@5
    .line 222
    return-void
.end method

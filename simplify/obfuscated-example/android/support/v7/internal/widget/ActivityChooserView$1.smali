.class Landroid/support/v7/internal/widget/ActivityChooserView$1;
.super Landroid/database/DataSetObserver;
.source "ActivityChooserView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ActivityChooserView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/ActivityChooserView;


# direct methods
.method constructor <init>(Landroid/support/v7/internal/widget/ActivityChooserView;)V
    .locals 0

    #@0
    .prologue
    .line 124
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$1;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@2
    invoke-direct {p0}, Landroid/database/DataSetObserver;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onChanged()V
    .locals 1

    #@0
    .prologue
    .line 128
    invoke-super {p0}, Landroid/database/DataSetObserver;->onChanged()V

    #@3
    .line 129
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$1;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@5
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserView;->mAdapter:Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActivityChooserView;->access$000(Landroid/support/v7/internal/widget/ActivityChooserView;)Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;

    #@8
    move-result-object v0

    #@9
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->notifyDataSetChanged()V

    #@c
    .line 130
    return-void
.end method

.method public onInvalidated()V
    .locals 1

    #@0
    .prologue
    .line 133
    invoke-super {p0}, Landroid/database/DataSetObserver;->onInvalidated()V

    #@3
    .line 134
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$1;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@5
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserView;->mAdapter:Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;
    invoke-static {v0}, Landroid/support/v7/internal/widget/ActivityChooserView;->access$000(Landroid/support/v7/internal/widget/ActivityChooserView;)Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;

    #@8
    move-result-object v0

    #@9
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->notifyDataSetInvalidated()V

    #@c
    .line 135
    return-void
.end method

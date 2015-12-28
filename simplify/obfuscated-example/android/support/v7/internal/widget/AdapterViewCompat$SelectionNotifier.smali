.class Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;
.super Ljava/lang/Object;
.source "AdapterViewCompat.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/AdapterViewCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "SelectionNotifier"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;


# direct methods
.method private constructor <init>(Landroid/support/v7/internal/widget/AdapterViewCompat;)V
    .locals 0

    #@0
    .prologue
    .line 855
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.SelectionNotifier;"
    iput-object p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/support/v7/internal/widget/AdapterViewCompat$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/internal/widget/AdapterViewCompat;
    .param p2, "x1"    # Landroid/support/v7/internal/widget/AdapterViewCompat$1;

    #@0
    .prologue
    .line 855
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.SelectionNotifier;"
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;-><init>(Landroid/support/v7/internal/widget/AdapterViewCompat;)V

    #@3
    return-void
.end method


# virtual methods
.method public run()V
    .locals 1

    #@0
    .prologue
    .line 857
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.SelectionNotifier;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@2
    iget-boolean v0, v0, Landroid/support/v7/internal/widget/AdapterViewCompat;->mDataChanged:Z

    #@4
    if-eqz v0, :cond_1

    #@6
    .line 861
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@8
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->getAdapter()Landroid/widget/Adapter;

    #@b
    move-result-object v0

    #@c
    if-eqz v0, :cond_0

    #@e
    .line 862
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@10
    invoke-virtual {v0, p0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->post(Ljava/lang/Runnable;)Z

    #@13
    .line 867
    :cond_0
    :goto_0
    return-void

    #@14
    .line 865
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$SelectionNotifier;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@16
    # invokes: Landroid/support/v7/internal/widget/AdapterViewCompat;->fireOnSelected()V
    invoke-static {v0}, Landroid/support/v7/internal/widget/AdapterViewCompat;->access$200(Landroid/support/v7/internal/widget/AdapterViewCompat;)V

    #@19
    goto :goto_0
.end method

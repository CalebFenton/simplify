.class Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListenerWrapper;
.super Ljava/lang/Object;
.source "AdapterViewCompat.java"

# interfaces
.implements Landroid/widget/AdapterView$OnItemClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/AdapterViewCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = "OnItemClickListenerWrapper"
.end annotation


# instance fields
.field private final mWrappedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

.field final synthetic this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;)V
    .locals 0
    .param p2, "listener"    # Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@0
    .prologue
    .line 267
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListenerWrapper;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.OnItemClickListenerWrapper;"
    iput-object p1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListenerWrapper;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 268
    iput-object p2, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListenerWrapper;->mWrappedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@7
    .line 269
    return-void
.end method


# virtual methods
.method public onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V
    .locals 6
    .param p2, "view"    # Landroid/view/View;
    .param p3, "position"    # I
    .param p4, "id"    # J
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/widget/AdapterView",
            "<*>;",
            "Landroid/view/View;",
            "IJ)V"
        }
    .end annotation

    #@0
    .prologue
    .line 273
    .local p0, "this":Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListenerWrapper;, "Landroid/support/v7/internal/widget/AdapterViewCompat<TT;>.OnItemClickListenerWrapper;"
    .local p1, "parent":Landroid/widget/AdapterView;, "Landroid/widget/AdapterView<*>;"
    iget-object v0, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListenerWrapper;->mWrappedListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@2
    iget-object v1, p0, Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListenerWrapper;->this$0:Landroid/support/v7/internal/widget/AdapterViewCompat;

    #@4
    move-object v2, p2

    #@5
    move v3, p3

    #@6
    move-wide v4, p4

    #@7
    invoke-interface/range {v0 .. v5}, Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;->onItemClick(Landroid/support/v7/internal/widget/AdapterViewCompat;Landroid/view/View;IJ)V

    #@a
    .line 274
    return-void
.end method

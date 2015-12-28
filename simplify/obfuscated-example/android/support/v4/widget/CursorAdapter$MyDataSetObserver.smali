.class Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;
.super Landroid/database/DataSetObserver;
.source "CursorAdapter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/CursorAdapter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "MyDataSetObserver"
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v4/widget/CursorAdapter;


# direct methods
.method private constructor <init>(Landroid/support/v4/widget/CursorAdapter;)V
    .locals 0

    #@0
    .prologue
    .line 469
    iput-object p1, p0, Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;->this$0:Landroid/support/v4/widget/CursorAdapter;

    #@2
    invoke-direct {p0}, Landroid/database/DataSetObserver;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v4/widget/CursorAdapter;Landroid/support/v4/widget/CursorAdapter$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v4/widget/CursorAdapter;
    .param p2, "x1"    # Landroid/support/v4/widget/CursorAdapter$1;

    #@0
    .prologue
    .line 469
    invoke-direct {p0, p1}, Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;-><init>(Landroid/support/v4/widget/CursorAdapter;)V

    #@3
    return-void
.end method


# virtual methods
.method public onChanged()V
    .locals 2

    #@0
    .prologue
    .line 472
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;->this$0:Landroid/support/v4/widget/CursorAdapter;

    #@2
    const/4 v1, 0x1

    #@3
    iput-boolean v1, v0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@5
    .line 473
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;->this$0:Landroid/support/v4/widget/CursorAdapter;

    #@7
    invoke-virtual {v0}, Landroid/support/v4/widget/CursorAdapter;->notifyDataSetChanged()V

    #@a
    .line 474
    return-void
.end method

.method public onInvalidated()V
    .locals 2

    #@0
    .prologue
    .line 478
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;->this$0:Landroid/support/v4/widget/CursorAdapter;

    #@2
    const/4 v1, 0x0

    #@3
    iput-boolean v1, v0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@5
    .line 479
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;->this$0:Landroid/support/v4/widget/CursorAdapter;

    #@7
    invoke-virtual {v0}, Landroid/support/v4/widget/CursorAdapter;->notifyDataSetInvalidated()V

    #@a
    .line 480
    return-void
.end method

.class Landroid/support/v4/widget/DrawerLayout$ViewDragCallback$1;
.super Ljava/lang/Object;
.source "DrawerLayout.java"

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;


# direct methods
.method constructor <init>(Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;)V
    .locals 0

    #@0
    .prologue
    .line 1599
    iput-object p1, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback$1;->this$1:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

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
    .line 1601
    iget-object v0, p0, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback$1;->this$1:Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;

    #@2
    # invokes: Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->peekDrawer()V
    invoke-static {v0}, Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;->access$000(Landroid/support/v4/widget/DrawerLayout$ViewDragCallback;)V

    #@5
    .line 1602
    return-void
.end method

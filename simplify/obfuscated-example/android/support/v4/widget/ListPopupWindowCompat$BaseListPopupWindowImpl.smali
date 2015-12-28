.class Landroid/support/v4/widget/ListPopupWindowCompat$BaseListPopupWindowImpl;
.super Ljava/lang/Object;
.source "ListPopupWindowCompat.java"

# interfaces
.implements Landroid/support/v4/widget/ListPopupWindowCompat$ListPopupWindowImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/ListPopupWindowCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "BaseListPopupWindowImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 37
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public createDragToOpenListener(Ljava/lang/Object;Landroid/view/View;)Landroid/view/View$OnTouchListener;
    .locals 1
    .param p1, "listPopupWindow"    # Ljava/lang/Object;
    .param p2, "src"    # Landroid/view/View;

    #@0
    .prologue
    .line 40
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

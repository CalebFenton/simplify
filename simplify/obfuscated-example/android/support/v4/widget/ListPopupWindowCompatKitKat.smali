.class Landroid/support/v4/widget/ListPopupWindowCompatKitKat;
.super Ljava/lang/Object;
.source "ListPopupWindowCompatKitKat.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 26
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static createDragToOpenListener(Ljava/lang/Object;Landroid/view/View;)Landroid/view/View$OnTouchListener;
    .locals 1
    .param p0, "listPopupWindow"    # Ljava/lang/Object;
    .param p1, "src"    # Landroid/view/View;

    #@0
    .prologue
    .line 28
    check-cast p0, Landroid/widget/ListPopupWindow;

    #@2
    .end local p0    # "listPopupWindow":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/widget/ListPopupWindow;->createDragToOpenListener(Landroid/view/View;)Landroid/view/View$OnTouchListener;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

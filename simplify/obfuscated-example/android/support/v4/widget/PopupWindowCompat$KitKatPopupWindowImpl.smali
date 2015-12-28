.class Landroid/support/v4/widget/PopupWindowCompat$KitKatPopupWindowImpl;
.super Landroid/support/v4/widget/PopupWindowCompat$BasePopupWindowImpl;
.source "PopupWindowCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/widget/PopupWindowCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "KitKatPopupWindowImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 50
    invoke-direct {p0}, Landroid/support/v4/widget/PopupWindowCompat$BasePopupWindowImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public showAsDropDown(Landroid/widget/PopupWindow;Landroid/view/View;III)V
    .locals 0
    .param p1, "popup"    # Landroid/widget/PopupWindow;
    .param p2, "anchor"    # Landroid/view/View;
    .param p3, "xoff"    # I
    .param p4, "yoff"    # I
    .param p5, "gravity"    # I

    #@0
    .prologue
    .line 54
    invoke-static {p1, p2, p3, p4, p5}, Landroid/support/v4/widget/PopupWindowCompatKitKat;->showAsDropDown(Landroid/widget/PopupWindow;Landroid/view/View;III)V

    #@3
    .line 55
    return-void
.end method

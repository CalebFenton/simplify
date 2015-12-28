.class public Landroid/support/v4/widget/ListPopupWindowCompat;
.super Ljava/lang/Object;
.source "ListPopupWindowCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/ListPopupWindowCompat$KitKatListPopupWindowImpl;,
        Landroid/support/v4/widget/ListPopupWindowCompat$BaseListPopupWindowImpl;,
        Landroid/support/v4/widget/ListPopupWindowCompat$ListPopupWindowImpl;
    }
.end annotation


# static fields
.field static final IMPL:Landroid/support/v4/widget/ListPopupWindowCompat$ListPopupWindowImpl;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 59
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 60
    .local v0, "version":I
    const/16 v1, 0x13

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 61
    new-instance v1, Landroid/support/v4/widget/ListPopupWindowCompat$KitKatListPopupWindowImpl;

    #@8
    invoke-direct {v1}, Landroid/support/v4/widget/ListPopupWindowCompat$KitKatListPopupWindowImpl;-><init>()V

    #@b
    sput-object v1, Landroid/support/v4/widget/ListPopupWindowCompat;->IMPL:Landroid/support/v4/widget/ListPopupWindowCompat$ListPopupWindowImpl;

    #@d
    .line 65
    :goto_0
    return-void

    #@e
    .line 63
    :cond_0
    new-instance v1, Landroid/support/v4/widget/ListPopupWindowCompat$BaseListPopupWindowImpl;

    #@10
    invoke-direct {v1}, Landroid/support/v4/widget/ListPopupWindowCompat$BaseListPopupWindowImpl;-><init>()V

    #@13
    sput-object v1, Landroid/support/v4/widget/ListPopupWindowCompat;->IMPL:Landroid/support/v4/widget/ListPopupWindowCompat$ListPopupWindowImpl;

    #@15
    goto :goto_0
.end method

.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 67
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 69
    return-void
.end method

.method public static createDragToOpenListener(Ljava/lang/Object;Landroid/view/View;)Landroid/view/View$OnTouchListener;
    .locals 1
    .param p0, "listPopupWindow"    # Ljava/lang/Object;
    .param p1, "src"    # Landroid/view/View;

    #@0
    .prologue
    .line 97
    sget-object v0, Landroid/support/v4/widget/ListPopupWindowCompat;->IMPL:Landroid/support/v4/widget/ListPopupWindowCompat$ListPopupWindowImpl;

    #@2
    invoke-interface {v0, p0, p1}, Landroid/support/v4/widget/ListPopupWindowCompat$ListPopupWindowImpl;->createDragToOpenListener(Ljava/lang/Object;Landroid/view/View;)Landroid/view/View$OnTouchListener;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

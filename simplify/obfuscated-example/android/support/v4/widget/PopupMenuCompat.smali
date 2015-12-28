.class public Landroid/support/v4/widget/PopupMenuCompat;
.super Ljava/lang/Object;
.source "PopupMenuCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/PopupMenuCompat$KitKatPopupMenuImpl;,
        Landroid/support/v4/widget/PopupMenuCompat$BasePopupMenuImpl;,
        Landroid/support/v4/widget/PopupMenuCompat$PopupMenuImpl;
    }
.end annotation


# static fields
.field static final IMPL:Landroid/support/v4/widget/PopupMenuCompat$PopupMenuImpl;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 58
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 59
    .local v0, "version":I
    const/16 v1, 0x13

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 60
    new-instance v1, Landroid/support/v4/widget/PopupMenuCompat$KitKatPopupMenuImpl;

    #@8
    invoke-direct {v1}, Landroid/support/v4/widget/PopupMenuCompat$KitKatPopupMenuImpl;-><init>()V

    #@b
    sput-object v1, Landroid/support/v4/widget/PopupMenuCompat;->IMPL:Landroid/support/v4/widget/PopupMenuCompat$PopupMenuImpl;

    #@d
    .line 64
    :goto_0
    return-void

    #@e
    .line 62
    :cond_0
    new-instance v1, Landroid/support/v4/widget/PopupMenuCompat$BasePopupMenuImpl;

    #@10
    invoke-direct {v1}, Landroid/support/v4/widget/PopupMenuCompat$BasePopupMenuImpl;-><init>()V

    #@13
    sput-object v1, Landroid/support/v4/widget/PopupMenuCompat;->IMPL:Landroid/support/v4/widget/PopupMenuCompat$PopupMenuImpl;

    #@15
    goto :goto_0
.end method

.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 66
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 68
    return-void
.end method

.method public static getDragToOpenListener(Ljava/lang/Object;)Landroid/view/View$OnTouchListener;
    .locals 1
    .param p0, "popupMenu"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 90
    sget-object v0, Landroid/support/v4/widget/PopupMenuCompat;->IMPL:Landroid/support/v4/widget/PopupMenuCompat$PopupMenuImpl;

    #@2
    invoke-interface {v0, p0}, Landroid/support/v4/widget/PopupMenuCompat$PopupMenuImpl;->getDragToOpenListener(Ljava/lang/Object;)Landroid/view/View$OnTouchListener;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.class public Landroid/support/v7/internal/widget/TintSpinner;
.super Landroid/widget/Spinner;
.source "TintSpinner.java"


# static fields
.field private static final TINT_ATTRS:[I


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 36
    const/4 v0, 0x2

    #@1
    new-array v0, v0, [I

    #@3
    fill-array-data v0, :array_0

    #@6
    sput-object v0, Landroid/support/v7/internal/widget/TintSpinner;->TINT_ATTRS:[I

    #@8
    return-void

    #@9
    nop

    #@a
    :array_0
    .array-data 4
        0x10100d4
        0x1010176
    .end array-data
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 42
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/widget/TintSpinner;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 43
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 46
    const v0, 0x1010081

    #@3
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/TintSpinner;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@6
    .line 47
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 5
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 50
    invoke-direct {p0, p1, p2, p3}, Landroid/widget/Spinner;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 52
    sget-object v2, Landroid/support/v7/internal/widget/TintSpinner;->TINT_ATTRS:[I

    #@7
    invoke-static {p1, p2, v2, p3, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@a
    move-result-object v0

    #@b
    .line 54
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@e
    move-result-object v2

    #@f
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/widget/TintSpinner;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@12
    .line 56
    invoke-virtual {v0, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->hasValue(I)Z

    #@15
    move-result v2

    #@16
    if-eqz v2, :cond_0

    #@18
    .line 57
    invoke-virtual {v0, v4}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@1b
    move-result-object v1

    #@1c
    .line 58
    .local v1, "background":Landroid/graphics/drawable/Drawable;
    sget v2, Landroid/os/Build$VERSION;->SDK_INT:I

    #@1e
    const/16 v3, 0x10

    #@20
    if-lt v2, v3, :cond_1

    #@22
    .line 59
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/TintSpinner;->setPopupBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@25
    .line 65
    .end local v1    # "background":Landroid/graphics/drawable/Drawable;
    :cond_0
    :goto_0
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@28
    .line 66
    return-void

    #@29
    .line 60
    .restart local v1    # "background":Landroid/graphics/drawable/Drawable;
    :cond_1
    sget v2, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2b
    const/16 v3, 0xb

    #@2d
    if-lt v2, v3, :cond_0

    #@2f
    .line 61
    invoke-static {p0, v1}, Landroid/support/v7/internal/widget/TintSpinner;->setPopupBackgroundDrawableV11(Landroid/widget/Spinner;Landroid/graphics/drawable/Drawable;)V

    #@32
    goto :goto_0
.end method

.method private static setPopupBackgroundDrawableV11(Landroid/widget/Spinner;Landroid/graphics/drawable/Drawable;)V
    .locals 5
    .param p0, "view"    # Landroid/widget/Spinner;
    .param p1, "background"    # Landroid/graphics/drawable/Drawable;
    .annotation build Landroid/annotation/TargetApi;
        value = 0xb
    .end annotation

    #@0
    .prologue
    .line 71
    :try_start_0
    const-class v3, Landroid/widget/Spinner;

    #@2
    const-string v4, "mPopup"

    #@4
    invoke-virtual {v3, v4}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;

    #@7
    move-result-object v2

    #@8
    .line 72
    .local v2, "popupField":Ljava/lang/reflect/Field;
    const/4 v3, 0x1

    #@9
    invoke-virtual {v2, v3}, Ljava/lang/reflect/Field;->setAccessible(Z)V

    #@c
    .line 74
    invoke-virtual {v2, p0}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@f
    move-result-object v1

    #@10
    .line 76
    .local v1, "popup":Ljava/lang/Object;
    instance-of v3, v1, Landroid/widget/ListPopupWindow;

    #@12
    if-eqz v3, :cond_0

    #@14
    .line 77
    check-cast v1, Landroid/widget/ListPopupWindow;

    #@16
    .end local v1    # "popup":Ljava/lang/Object;
    invoke-virtual {v1, p1}, Landroid/widget/ListPopupWindow;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    :try_end_0
    .catch Ljava/lang/NoSuchFieldException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/lang/IllegalAccessException; {:try_start_0 .. :try_end_0} :catch_1

    #@19
    .line 84
    .end local v2    # "popupField":Ljava/lang/reflect/Field;
    :cond_0
    :goto_0
    return-void

    #@1a
    .line 79
    :catch_0
    move-exception v0

    #@1b
    .line 80
    .local v0, "e":Ljava/lang/NoSuchFieldException;
    invoke-virtual {v0}, Ljava/lang/NoSuchFieldException;->printStackTrace()V

    #@1e
    goto :goto_0

    #@1f
    .line 81
    .end local v0    # "e":Ljava/lang/NoSuchFieldException;
    :catch_1
    move-exception v0

    #@20
    .line 82
    .local v0, "e":Ljava/lang/IllegalAccessException;
    invoke-virtual {v0}, Ljava/lang/IllegalAccessException;->printStackTrace()V

    #@23
    goto :goto_0
.end method

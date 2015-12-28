.class public Landroid/support/v7/internal/widget/TintCheckedTextView;
.super Landroid/widget/CheckedTextView;
.source "TintCheckedTextView.java"


# static fields
.field private static final TINT_ATTRS:[I


# instance fields
.field private final mTintManager:Landroid/support/v7/internal/widget/TintManager;


# direct methods
.method static constructor <clinit>()V
    .locals 3

    #@0
    .prologue
    .line 31
    const/4 v0, 0x1

    #@1
    new-array v0, v0, [I

    #@3
    const/4 v1, 0x0

    #@4
    const v2, 0x1010108

    #@7
    aput v2, v0, v1

    #@9
    sput-object v0, Landroid/support/v7/internal/widget/TintCheckedTextView;->TINT_ATTRS:[I

    #@b
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 38
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/widget/TintCheckedTextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 39
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 42
    const v0, 0x10103c8

    #@3
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/internal/widget/TintCheckedTextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@6
    .line 43
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 46
    invoke-direct {p0, p1, p2, p3}, Landroid/widget/CheckedTextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 48
    sget-object v1, Landroid/support/v7/internal/widget/TintCheckedTextView;->TINT_ATTRS:[I

    #@6
    invoke-static {p1, p2, v1, p3, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@9
    move-result-object v0

    #@a
    .line 50
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    invoke-virtual {v0, v2}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@d
    move-result-object v1

    #@e
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/TintCheckedTextView;->setCheckMarkDrawable(Landroid/graphics/drawable/Drawable;)V

    #@11
    .line 51
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@14
    .line 53
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@17
    move-result-object v1

    #@18
    iput-object v1, p0, Landroid/support/v7/internal/widget/TintCheckedTextView;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@1a
    .line 54
    return-void
.end method


# virtual methods
.method public setCheckMarkDrawable(I)V
    .locals 1
    .param p1, "resid"    # I

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintCheckedTextView;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/TintCheckedTextView;->setCheckMarkDrawable(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 59
    return-void
.end method

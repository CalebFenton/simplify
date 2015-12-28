.class public Landroid/support/v7/widget/SearchView$SearchAutoComplete;
.super Landroid/widget/AutoCompleteTextView;
.source "SearchView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/SearchView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "SearchAutoComplete"
.end annotation


# instance fields
.field private final POPUP_WINDOW_ATTRS:[I

.field private mSearchView:Landroid/support/v7/widget/SearchView;

.field private mThreshold:I

.field private final mTintManager:Landroid/support/v7/internal/widget/TintManager;


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 1620
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 1621
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 1624
    const v0, 0x101006b

    #@3
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@6
    .line 1625
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyle"    # I

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 1628
    invoke-direct {p0, p1, p2, p3}, Landroid/widget/AutoCompleteTextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@4
    .line 1610
    const/4 v1, 0x1

    #@5
    new-array v1, v1, [I

    #@7
    const v2, 0x1010176

    #@a
    aput v2, v1, v3

    #@c
    iput-object v1, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->POPUP_WINDOW_ATTRS:[I

    #@e
    .line 1629
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getThreshold()I

    #@11
    move-result v1

    #@12
    iput v1, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mThreshold:I

    #@14
    .line 1631
    iget-object v1, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->POPUP_WINDOW_ATTRS:[I

    #@16
    invoke-static {p1, p2, v1, p3, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@19
    move-result-object v0

    #@1a
    .line 1633
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->hasValue(I)Z

    #@1d
    move-result v1

    #@1e
    if-eqz v1, :cond_0

    #@20
    .line 1634
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@23
    move-result-object v1

    #@24
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setDropDownBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@27
    .line 1636
    :cond_0
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@2a
    .line 1639
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@2d
    move-result-object v1

    #@2e
    iput-object v1, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@30
    .line 1640
    return-void
.end method

.method static synthetic access$1700(Landroid/support/v7/widget/SearchView$SearchAutoComplete;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@0
    .prologue
    .line 1608
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->isEmpty()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method private isEmpty()Z
    .locals 1

    #@0
    .prologue
    .line 1661
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0}, Landroid/text/TextUtils;->getTrimmedLength(Ljava/lang/CharSequence;)I

    #@7
    move-result v0

    #@8
    if-nez v0, :cond_0

    #@a
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_0
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method


# virtual methods
.method public enoughToFilter()Z
    .locals 1

    #@0
    .prologue
    .line 1714
    iget v0, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mThreshold:I

    #@2
    if-lez v0, :cond_0

    #@4
    invoke-super {p0}, Landroid/widget/AutoCompleteTextView;->enoughToFilter()Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method protected onFocusChanged(ZILandroid/graphics/Rect;)V
    .locals 1
    .param p1, "focused"    # Z
    .param p2, "direction"    # I
    .param p3, "previouslyFocusedRect"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 1704
    invoke-super {p0, p1, p2, p3}, Landroid/widget/AutoCompleteTextView;->onFocusChanged(ZILandroid/graphics/Rect;)V

    #@3
    .line 1705
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@5
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView;->onTextFocusChanged()V

    #@8
    .line 1706
    return-void
.end method

.method public onKeyPreIme(ILandroid/view/KeyEvent;)Z
    .locals 4
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 1719
    const/4 v2, 0x4

    #@2
    if-ne p1, v2, :cond_3

    #@4
    .line 1722
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getAction()I

    #@7
    move-result v2

    #@8
    if-nez v2, :cond_1

    #@a
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getRepeatCount()I

    #@d
    move-result v2

    #@e
    if-nez v2, :cond_1

    #@10
    .line 1723
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getKeyDispatcherState()Landroid/view/KeyEvent$DispatcherState;

    #@13
    move-result-object v0

    #@14
    .line 1724
    .local v0, "state":Landroid/view/KeyEvent$DispatcherState;
    if-eqz v0, :cond_0

    #@16
    .line 1725
    invoke-virtual {v0, p2, p0}, Landroid/view/KeyEvent$DispatcherState;->startTracking(Landroid/view/KeyEvent;Ljava/lang/Object;)V

    #@19
    .line 1740
    .end local v0    # "state":Landroid/view/KeyEvent$DispatcherState;
    :cond_0
    :goto_0
    return v1

    #@1a
    .line 1728
    :cond_1
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getAction()I

    #@1d
    move-result v2

    #@1e
    if-ne v2, v1, :cond_3

    #@20
    .line 1729
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getKeyDispatcherState()Landroid/view/KeyEvent$DispatcherState;

    #@23
    move-result-object v0

    #@24
    .line 1730
    .restart local v0    # "state":Landroid/view/KeyEvent$DispatcherState;
    if-eqz v0, :cond_2

    #@26
    .line 1731
    invoke-virtual {v0, p2}, Landroid/view/KeyEvent$DispatcherState;->handleUpEvent(Landroid/view/KeyEvent;)V

    #@29
    .line 1733
    :cond_2
    invoke-virtual {p2}, Landroid/view/KeyEvent;->isTracking()Z

    #@2c
    move-result v2

    #@2d
    if-eqz v2, :cond_3

    #@2f
    invoke-virtual {p2}, Landroid/view/KeyEvent;->isCanceled()Z

    #@32
    move-result v2

    #@33
    if-nez v2, :cond_3

    #@35
    .line 1734
    iget-object v2, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@37
    invoke-virtual {v2}, Landroid/support/v7/widget/SearchView;->clearFocus()V

    #@3a
    .line 1735
    iget-object v2, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@3c
    const/4 v3, 0x0

    #@3d
    # invokes: Landroid/support/v7/widget/SearchView;->setImeVisibility(Z)V
    invoke-static {v2, v3}, Landroid/support/v7/widget/SearchView;->access$2200(Landroid/support/v7/widget/SearchView;Z)V

    #@40
    goto :goto_0

    #@41
    .line 1740
    .end local v0    # "state":Landroid/view/KeyEvent$DispatcherState;
    :cond_3
    invoke-super {p0, p1, p2}, Landroid/widget/AutoCompleteTextView;->onKeyPreIme(ILandroid/view/KeyEvent;)Z

    #@44
    move-result v1

    #@45
    goto :goto_0
.end method

.method public onWindowFocusChanged(Z)V
    .locals 3
    .param p1, "hasWindowFocus"    # Z

    #@0
    .prologue
    .line 1688
    invoke-super {p0, p1}, Landroid/widget/AutoCompleteTextView;->onWindowFocusChanged(Z)V

    #@3
    .line 1690
    if-eqz p1, :cond_0

    #@5
    iget-object v1, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@7
    invoke-virtual {v1}, Landroid/support/v7/widget/SearchView;->hasFocus()Z

    #@a
    move-result v1

    #@b
    if-eqz v1, :cond_0

    #@d
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getVisibility()I

    #@10
    move-result v1

    #@11
    if-nez v1, :cond_0

    #@13
    .line 1691
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getContext()Landroid/content/Context;

    #@16
    move-result-object v1

    #@17
    const-string v2, "input_method"

    #@19
    invoke-virtual {v1, v2}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@1c
    move-result-object v0

    #@1d
    check-cast v0, Landroid/view/inputmethod/InputMethodManager;

    #@1f
    .line 1693
    .local v0, "inputManager":Landroid/view/inputmethod/InputMethodManager;
    const/4 v1, 0x0

    #@20
    invoke-virtual {v0, p0, v1}, Landroid/view/inputmethod/InputMethodManager;->showSoftInput(Landroid/view/View;I)Z

    #@23
    .line 1696
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getContext()Landroid/content/Context;

    #@26
    move-result-object v1

    #@27
    invoke-static {v1}, Landroid/support/v7/widget/SearchView;->isLandscapeMode(Landroid/content/Context;)Z

    #@2a
    move-result v1

    #@2b
    if-eqz v1, :cond_0

    #@2d
    .line 1697
    sget-object v1, Landroid/support/v7/widget/SearchView;->HIDDEN_METHOD_INVOKER:Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;

    #@2f
    const/4 v2, 0x1

    #@30
    invoke-virtual {v1, p0, v2}, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->ensureImeVisible(Landroid/widget/AutoCompleteTextView;Z)V

    #@33
    .line 1700
    .end local v0    # "inputManager":Landroid/view/inputmethod/InputMethodManager;
    :cond_0
    return-void
.end method

.method public performCompletion()V
    .locals 0

    #@0
    .prologue
    .line 1680
    return-void
.end method

.method protected replaceText(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 1670
    return-void
.end method

.method public setDropDownBackgroundResource(I)V
    .locals 1
    .param p1, "id"    # I

    #@0
    .prologue
    .line 1654
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setDropDownBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@9
    .line 1655
    return-void
.end method

.method setSearchView(Landroid/support/v7/widget/SearchView;)V
    .locals 0
    .param p1, "searchView"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 1643
    iput-object p1, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mSearchView:Landroid/support/v7/widget/SearchView;

    #@2
    .line 1644
    return-void
.end method

.method public setThreshold(I)V
    .locals 0
    .param p1, "threshold"    # I

    #@0
    .prologue
    .line 1648
    invoke-super {p0, p1}, Landroid/widget/AutoCompleteTextView;->setThreshold(I)V

    #@3
    .line 1649
    iput p1, p0, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->mThreshold:I

    #@5
    .line 1650
    return-void
.end method

.class Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;
.super Ljava/lang/Object;
.source "SearchView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/widget/SearchView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "AutoCompleteTextViewReflector"
.end annotation


# instance fields
.field private doAfterTextChanged:Ljava/lang/reflect/Method;

.field private doBeforeTextChanged:Ljava/lang/reflect/Method;

.field private ensureImeVisible:Ljava/lang/reflect/Method;

.field private showSoftInputUnchecked:Ljava/lang/reflect/Method;


# direct methods
.method constructor <init>()V
    .locals 5

    #@0
    .prologue
    .line 1749
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 1751
    :try_start_0
    const-class v0, Landroid/widget/AutoCompleteTextView;

    #@5
    const-string v1, "doBeforeTextChanged"

    #@7
    const/4 v2, 0x0

    #@8
    new-array v2, v2, [Ljava/lang/Class;

    #@a
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@d
    move-result-object v0

    #@e
    iput-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doBeforeTextChanged:Ljava/lang/reflect/Method;

    #@10
    .line 1753
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doBeforeTextChanged:Ljava/lang/reflect/Method;

    #@12
    const/4 v1, 0x1

    #@13
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Method;->setAccessible(Z)V
    :try_end_0
    .catch Ljava/lang/NoSuchMethodException; {:try_start_0 .. :try_end_0} :catch_3

    #@16
    .line 1758
    :goto_0
    :try_start_1
    const-class v0, Landroid/widget/AutoCompleteTextView;

    #@18
    const-string v1, "doAfterTextChanged"

    #@1a
    const/4 v2, 0x0

    #@1b
    new-array v2, v2, [Ljava/lang/Class;

    #@1d
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getDeclaredMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@20
    move-result-object v0

    #@21
    iput-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doAfterTextChanged:Ljava/lang/reflect/Method;

    #@23
    .line 1760
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doAfterTextChanged:Ljava/lang/reflect/Method;

    #@25
    const/4 v1, 0x1

    #@26
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Method;->setAccessible(Z)V
    :try_end_1
    .catch Ljava/lang/NoSuchMethodException; {:try_start_1 .. :try_end_1} :catch_2

    #@29
    .line 1765
    :goto_1
    :try_start_2
    const-class v0, Landroid/widget/AutoCompleteTextView;

    #@2b
    const-string v1, "ensureImeVisible"

    #@2d
    const/4 v2, 0x1

    #@2e
    new-array v2, v2, [Ljava/lang/Class;

    #@30
    const/4 v3, 0x0

    #@31
    sget-object v4, Ljava/lang/Boolean;->TYPE:Ljava/lang/Class;

    #@33
    aput-object v4, v2, v3

    #@35
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@38
    move-result-object v0

    #@39
    iput-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->ensureImeVisible:Ljava/lang/reflect/Method;

    #@3b
    .line 1767
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->ensureImeVisible:Ljava/lang/reflect/Method;

    #@3d
    const/4 v1, 0x1

    #@3e
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Method;->setAccessible(Z)V
    :try_end_2
    .catch Ljava/lang/NoSuchMethodException; {:try_start_2 .. :try_end_2} :catch_1

    #@41
    .line 1772
    :goto_2
    :try_start_3
    const-class v0, Landroid/view/inputmethod/InputMethodManager;

    #@43
    const-string v1, "showSoftInputUnchecked"

    #@45
    const/4 v2, 0x2

    #@46
    new-array v2, v2, [Ljava/lang/Class;

    #@48
    const/4 v3, 0x0

    #@49
    sget-object v4, Ljava/lang/Integer;->TYPE:Ljava/lang/Class;

    #@4b
    aput-object v4, v2, v3

    #@4d
    const/4 v3, 0x1

    #@4e
    const-class v4, Landroid/os/ResultReceiver;

    #@50
    aput-object v4, v2, v3

    #@52
    invoke-virtual {v0, v1, v2}, Ljava/lang/Class;->getMethod(Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;

    #@55
    move-result-object v0

    #@56
    iput-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->showSoftInputUnchecked:Ljava/lang/reflect/Method;

    #@58
    .line 1774
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->showSoftInputUnchecked:Ljava/lang/reflect/Method;

    #@5a
    const/4 v1, 0x1

    #@5b
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Method;->setAccessible(Z)V
    :try_end_3
    .catch Ljava/lang/NoSuchMethodException; {:try_start_3 .. :try_end_3} :catch_0

    #@5e
    .line 1778
    :goto_3
    return-void

    #@5f
    .line 1775
    :catch_0
    move-exception v0

    #@60
    goto :goto_3

    #@61
    .line 1768
    :catch_1
    move-exception v0

    #@62
    goto :goto_2

    #@63
    .line 1761
    :catch_2
    move-exception v0

    #@64
    goto :goto_1

    #@65
    .line 1754
    :catch_3
    move-exception v0

    #@66
    goto :goto_0
.end method


# virtual methods
.method doAfterTextChanged(Landroid/widget/AutoCompleteTextView;)V
    .locals 2
    .param p1, "view"    # Landroid/widget/AutoCompleteTextView;

    #@0
    .prologue
    .line 1790
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doAfterTextChanged:Ljava/lang/reflect/Method;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1792
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doAfterTextChanged:Ljava/lang/reflect/Method;

    #@6
    const/4 v1, 0x0

    #@7
    new-array v1, v1, [Ljava/lang/Object;

    #@9
    invoke-virtual {v0, p1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@c
    .line 1796
    :cond_0
    :goto_0
    return-void

    #@d
    .line 1793
    :catch_0
    move-exception v0

    #@e
    goto :goto_0
.end method

.method doBeforeTextChanged(Landroid/widget/AutoCompleteTextView;)V
    .locals 2
    .param p1, "view"    # Landroid/widget/AutoCompleteTextView;

    #@0
    .prologue
    .line 1781
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doBeforeTextChanged:Ljava/lang/reflect/Method;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1783
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doBeforeTextChanged:Ljava/lang/reflect/Method;

    #@6
    const/4 v1, 0x0

    #@7
    new-array v1, v1, [Ljava/lang/Object;

    #@9
    invoke-virtual {v0, p1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@c
    .line 1787
    :cond_0
    :goto_0
    return-void

    #@d
    .line 1784
    :catch_0
    move-exception v0

    #@e
    goto :goto_0
.end method

.method ensureImeVisible(Landroid/widget/AutoCompleteTextView;Z)V
    .locals 4
    .param p1, "view"    # Landroid/widget/AutoCompleteTextView;
    .param p2, "visible"    # Z

    #@0
    .prologue
    .line 1799
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->ensureImeVisible:Ljava/lang/reflect/Method;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1801
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->ensureImeVisible:Ljava/lang/reflect/Method;

    #@6
    const/4 v1, 0x1

    #@7
    new-array v1, v1, [Ljava/lang/Object;

    #@9
    const/4 v2, 0x0

    #@a
    invoke-static {p2}, Ljava/lang/Boolean;->valueOf(Z)Ljava/lang/Boolean;

    #@d
    move-result-object v3

    #@e
    aput-object v3, v1, v2

    #@10
    invoke-virtual {v0, p1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@13
    .line 1805
    :cond_0
    :goto_0
    return-void

    #@14
    .line 1802
    :catch_0
    move-exception v0

    #@15
    goto :goto_0
.end method

.method showSoftInputUnchecked(Landroid/view/inputmethod/InputMethodManager;Landroid/view/View;I)V
    .locals 4
    .param p1, "imm"    # Landroid/view/inputmethod/InputMethodManager;
    .param p2, "view"    # Landroid/view/View;
    .param p3, "flags"    # I

    #@0
    .prologue
    .line 1808
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->showSoftInputUnchecked:Ljava/lang/reflect/Method;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1810
    :try_start_0
    iget-object v0, p0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->showSoftInputUnchecked:Ljava/lang/reflect/Method;

    #@6
    const/4 v1, 0x2

    #@7
    new-array v1, v1, [Ljava/lang/Object;

    #@9
    const/4 v2, 0x0

    #@a
    invoke-static {p3}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@d
    move-result-object v3

    #@e
    aput-object v3, v1, v2

    #@10
    const/4 v2, 0x1

    #@11
    const/4 v3, 0x0

    #@12
    aput-object v3, v1, v2

    #@14
    invoke-virtual {v0, p1, v1}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    #@17
    .line 1818
    :goto_0
    return-void

    #@18
    .line 1812
    :catch_0
    move-exception v0

    #@19
    .line 1817
    :cond_0
    invoke-virtual {p1, p2, p3}, Landroid/view/inputmethod/InputMethodManager;->showSoftInput(Landroid/view/View;I)Z

    #@1c
    goto :goto_0
.end method

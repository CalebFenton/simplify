.class Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;
.super Landroid/support/v7/widget/ListPopupWindow;
.source "SpinnerCompat.java"

# interfaces
.implements Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/SpinnerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "DropdownPopup"
.end annotation


# instance fields
.field private mAdapter:Landroid/widget/ListAdapter;

.field private mHintText:Ljava/lang/CharSequence;

.field final synthetic this$0:Landroid/support/v7/internal/widget/SpinnerCompat;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/widget/SpinnerCompat;Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 1
    .param p2, "context"    # Landroid/content/Context;
    .param p3, "attrs"    # Landroid/util/AttributeSet;
    .param p4, "defStyleAttr"    # I

    #@0
    .prologue
    .line 981
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    .line 982
    invoke-direct {p0, p2, p3, p4}, Landroid/support/v7/widget/ListPopupWindow;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 984
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setAnchorView(Landroid/view/View;)V

    #@8
    .line 985
    const/4 v0, 0x1

    #@9
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setModal(Z)V

    #@c
    .line 986
    const/4 v0, 0x0

    #@d
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setPromptPosition(I)V

    #@10
    .line 988
    new-instance v0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$1;

    #@12
    invoke-direct {v0, p0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$1;-><init>(Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;Landroid/support/v7/internal/widget/SpinnerCompat;)V

    #@15
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setOnItemClickListener(Landroid/widget/AdapterView$OnItemClickListener;)V

    #@18
    .line 999
    return-void
.end method

.method static synthetic access$300(Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;)Landroid/widget/ListAdapter;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

    #@0
    .prologue
    .line 974
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->mAdapter:Landroid/widget/ListAdapter;

    #@2
    return-object v0
.end method

.method static synthetic access$501(Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;

    #@0
    .prologue
    .line 974
    invoke-super {p0}, Landroid/support/v7/widget/ListPopupWindow;->show()V

    #@3
    return-void
.end method


# virtual methods
.method computeContentWidth()V
    .locals 10

    #@0
    .prologue
    .line 1017
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->getBackground()Landroid/graphics/drawable/Drawable;

    #@3
    move-result-object v0

    #@4
    .line 1018
    .local v0, "background":Landroid/graphics/drawable/Drawable;
    const/4 v3, 0x0

    #@5
    .line 1019
    .local v3, "hOffset":I
    if-eqz v0, :cond_2

    #@7
    .line 1020
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@9
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;
    invoke-static {v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$400(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/graphics/Rect;

    #@c
    move-result-object v7

    #@d
    invoke-virtual {v0, v7}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@10
    .line 1021
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@12
    invoke-static {v7}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@15
    move-result v7

    #@16
    if-eqz v7, :cond_1

    #@18
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@1a
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;
    invoke-static {v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$400(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/graphics/Rect;

    #@1d
    move-result-object v7

    #@1e
    iget v3, v7, Landroid/graphics/Rect;->right:I

    #@20
    .line 1027
    :goto_0
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@22
    invoke-virtual {v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->getPaddingLeft()I

    #@25
    move-result v4

    #@26
    .line 1028
    .local v4, "spinnerPaddingLeft":I
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@28
    invoke-virtual {v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->getPaddingRight()I

    #@2b
    move-result v5

    #@2c
    .line 1029
    .local v5, "spinnerPaddingRight":I
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2e
    invoke-virtual {v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->getWidth()I

    #@31
    move-result v6

    #@32
    .line 1030
    .local v6, "spinnerWidth":I
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@34
    iget v7, v7, Landroid/support/v7/internal/widget/SpinnerCompat;->mDropDownWidth:I

    #@36
    const/4 v8, -0x2

    #@37
    if-ne v7, v8, :cond_3

    #@39
    .line 1031
    iget-object v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@3b
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->mAdapter:Landroid/widget/ListAdapter;

    #@3d
    check-cast v7, Landroid/widget/SpinnerAdapter;

    #@3f
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->getBackground()Landroid/graphics/drawable/Drawable;

    #@42
    move-result-object v9

    #@43
    invoke-virtual {v8, v7, v9}, Landroid/support/v7/internal/widget/SpinnerCompat;->measureContentWidth(Landroid/widget/SpinnerAdapter;Landroid/graphics/drawable/Drawable;)I

    #@46
    move-result v1

    #@47
    .line 1033
    .local v1, "contentWidth":I
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@49
    invoke-virtual {v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->getContext()Landroid/content/Context;

    #@4c
    move-result-object v7

    #@4d
    invoke-virtual {v7}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@50
    move-result-object v7

    #@51
    invoke-virtual {v7}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@54
    move-result-object v7

    #@55
    iget v7, v7, Landroid/util/DisplayMetrics;->widthPixels:I

    #@57
    iget-object v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@59
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;
    invoke-static {v8}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$400(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/graphics/Rect;

    #@5c
    move-result-object v8

    #@5d
    iget v8, v8, Landroid/graphics/Rect;->left:I

    #@5f
    sub-int/2addr v7, v8

    #@60
    iget-object v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@62
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;
    invoke-static {v8}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$400(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/graphics/Rect;

    #@65
    move-result-object v8

    #@66
    iget v8, v8, Landroid/graphics/Rect;->right:I

    #@68
    sub-int v2, v7, v8

    #@6a
    .line 1035
    .local v2, "contentWidthLimit":I
    if-le v1, v2, :cond_0

    #@6c
    .line 1036
    move v1, v2

    #@6d
    .line 1038
    :cond_0
    sub-int v7, v6, v4

    #@6f
    sub-int/2addr v7, v5

    #@70
    invoke-static {v1, v7}, Ljava/lang/Math;->max(II)I

    #@73
    move-result v7

    #@74
    invoke-virtual {p0, v7}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setContentWidth(I)V

    #@77
    .line 1045
    .end local v1    # "contentWidth":I
    .end local v2    # "contentWidthLimit":I
    :goto_1
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@79
    invoke-static {v7}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@7c
    move-result v7

    #@7d
    if-eqz v7, :cond_5

    #@7f
    .line 1046
    sub-int v7, v6, v5

    #@81
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->getWidth()I

    #@84
    move-result v8

    #@85
    sub-int/2addr v7, v8

    #@86
    add-int/2addr v3, v7

    #@87
    .line 1050
    :goto_2
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setHorizontalOffset(I)V

    #@8a
    .line 1051
    return-void

    #@8b
    .line 1021
    .end local v4    # "spinnerPaddingLeft":I
    .end local v5    # "spinnerPaddingRight":I
    .end local v6    # "spinnerWidth":I
    :cond_1
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@8d
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;
    invoke-static {v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$400(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/graphics/Rect;

    #@90
    move-result-object v7

    #@91
    iget v7, v7, Landroid/graphics/Rect;->left:I

    #@93
    neg-int v3, v7

    #@94
    goto :goto_0

    #@95
    .line 1024
    :cond_2
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@97
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;
    invoke-static {v7}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$400(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/graphics/Rect;

    #@9a
    move-result-object v7

    #@9b
    iget-object v8, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@9d
    # getter for: Landroid/support/v7/internal/widget/SpinnerCompat;->mTempRect:Landroid/graphics/Rect;
    invoke-static {v8}, Landroid/support/v7/internal/widget/SpinnerCompat;->access$400(Landroid/support/v7/internal/widget/SpinnerCompat;)Landroid/graphics/Rect;

    #@a0
    move-result-object v8

    #@a1
    const/4 v9, 0x0

    #@a2
    iput v9, v8, Landroid/graphics/Rect;->right:I

    #@a4
    iput v9, v7, Landroid/graphics/Rect;->left:I

    #@a6
    goto/16 :goto_0

    #@a8
    .line 1040
    .restart local v4    # "spinnerPaddingLeft":I
    .restart local v5    # "spinnerPaddingRight":I
    .restart local v6    # "spinnerWidth":I
    :cond_3
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@aa
    iget v7, v7, Landroid/support/v7/internal/widget/SpinnerCompat;->mDropDownWidth:I

    #@ac
    const/4 v8, -0x1

    #@ad
    if-ne v7, v8, :cond_4

    #@af
    .line 1041
    sub-int v7, v6, v4

    #@b1
    sub-int/2addr v7, v5

    #@b2
    invoke-virtual {p0, v7}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setContentWidth(I)V

    #@b5
    goto :goto_1

    #@b6
    .line 1043
    :cond_4
    iget-object v7, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@b8
    iget v7, v7, Landroid/support/v7/internal/widget/SpinnerCompat;->mDropDownWidth:I

    #@ba
    invoke-virtual {p0, v7}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setContentWidth(I)V

    #@bd
    goto :goto_1

    #@be
    .line 1048
    :cond_5
    add-int/2addr v3, v4

    #@bf
    goto :goto_2
.end method

.method public getHintText()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 1008
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->mHintText:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public setAdapter(Landroid/widget/ListAdapter;)V
    .locals 0
    .param p1, "adapter"    # Landroid/widget/ListAdapter;

    #@0
    .prologue
    .line 1003
    invoke-super {p0, p1}, Landroid/support/v7/widget/ListPopupWindow;->setAdapter(Landroid/widget/ListAdapter;)V

    #@3
    .line 1004
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->mAdapter:Landroid/widget/ListAdapter;

    #@5
    .line 1005
    return-void
.end method

.method public setPromptText(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "hintText"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 1013
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->mHintText:Ljava/lang/CharSequence;

    #@2
    .line 1014
    return-void
.end method

.method public show(II)V
    .locals 5
    .param p1, "textDirection"    # I
    .param p2, "textAlignment"    # I

    #@0
    .prologue
    .line 1054
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->isShowing()Z

    #@3
    move-result v3

    #@4
    .line 1056
    .local v3, "wasShowing":Z
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->computeContentWidth()V

    #@7
    .line 1057
    const/4 v4, 0x2

    #@8
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setInputMethodMode(I)V

    #@b
    .line 1058
    invoke-super {p0}, Landroid/support/v7/widget/ListPopupWindow;->show()V

    #@e
    .line 1059
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->getListView()Landroid/widget/ListView;

    #@11
    move-result-object v1

    #@12
    .line 1060
    .local v1, "listView":Landroid/widget/ListView;
    const/4 v4, 0x1

    #@13
    invoke-virtual {v1, v4}, Landroid/widget/ListView;->setChoiceMode(I)V

    #@16
    .line 1063
    iget-object v4, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@18
    invoke-virtual {v4}, Landroid/support/v7/internal/widget/SpinnerCompat;->getSelectedItemPosition()I

    #@1b
    move-result v4

    #@1c
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setSelection(I)V

    #@1f
    .line 1065
    if-eqz v3, :cond_1

    #@21
    .line 1098
    :cond_0
    :goto_0
    return-void

    #@22
    .line 1074
    :cond_1
    iget-object v4, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@24
    invoke-virtual {v4}, Landroid/support/v7/internal/widget/SpinnerCompat;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@27
    move-result-object v2

    #@28
    .line 1075
    .local v2, "vto":Landroid/view/ViewTreeObserver;
    if-eqz v2, :cond_0

    #@2a
    .line 1076
    new-instance v0, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$2;

    #@2c
    invoke-direct {v0, p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$2;-><init>(Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;)V

    #@2f
    .line 1087
    .local v0, "layoutListener":Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;
    invoke-virtual {v2, v0}, Landroid/view/ViewTreeObserver;->addOnGlobalLayoutListener(Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;)V

    #@32
    .line 1088
    new-instance v4, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$3;

    #@34
    invoke-direct {v4, p0, v0}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup$3;-><init>(Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;)V

    #@37
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/widget/SpinnerCompat$DropdownPopup;->setOnDismissListener(Landroid/widget/PopupWindow$OnDismissListener;)V

    #@3a
    goto :goto_0
.end method

.class Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;
.super Ljava/lang/Object;
.source "SpinnerCompat.java"

# interfaces
.implements Landroid/content/DialogInterface$OnClickListener;
.implements Landroid/support/v7/internal/widget/SpinnerCompat$SpinnerPopup;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/SpinnerCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "DialogPopup"
.end annotation


# instance fields
.field private mListAdapter:Landroid/widget/ListAdapter;

.field private mPopup:Landroid/app/AlertDialog;

.field private mPrompt:Ljava/lang/CharSequence;

.field final synthetic this$0:Landroid/support/v7/internal/widget/SpinnerCompat;


# direct methods
.method private constructor <init>(Landroid/support/v7/internal/widget/SpinnerCompat;)V
    .locals 0

    #@0
    .prologue
    .line 891
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/internal/widget/SpinnerCompat;Landroid/support/v7/internal/widget/SpinnerCompat$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/internal/widget/SpinnerCompat;
    .param p2, "x1"    # Landroid/support/v7/internal/widget/SpinnerCompat$1;

    #@0
    .prologue
    .line 891
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;-><init>(Landroid/support/v7/internal/widget/SpinnerCompat;)V

    #@3
    return-void
.end method


# virtual methods
.method public dismiss()V
    .locals 1

    #@0
    .prologue
    .line 900
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPopup:Landroid/app/AlertDialog;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 901
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPopup:Landroid/app/AlertDialog;

    #@6
    invoke-virtual {v0}, Landroid/app/AlertDialog;->dismiss()V

    #@9
    .line 902
    const/4 v0, 0x0

    #@a
    iput-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPopup:Landroid/app/AlertDialog;

    #@c
    .line 904
    :cond_0
    return-void
.end method

.method public getBackground()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 960
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

.method public getHintText()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 919
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPrompt:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getHorizontalOffset()I
    .locals 1

    #@0
    .prologue
    .line 970
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getVerticalOffset()I
    .locals 1

    #@0
    .prologue
    .line 965
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public isShowing()Z
    .locals 1

    #@0
    .prologue
    .line 907
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPopup:Landroid/app/AlertDialog;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPopup:Landroid/app/AlertDialog;

    #@6
    invoke-virtual {v0}, Landroid/app/AlertDialog;->isShowing()Z

    #@9
    move-result v0

    #@a
    :goto_0
    return v0

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public onClick(Landroid/content/DialogInterface;I)V
    .locals 4
    .param p1, "dialog"    # Landroid/content/DialogInterface;
    .param p2, "which"    # I

    #@0
    .prologue
    .line 936
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@2
    invoke-virtual {v0, p2}, Landroid/support/v7/internal/widget/SpinnerCompat;->setSelection(I)V

    #@5
    .line 937
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@7
    iget-object v0, v0, Landroid/support/v7/internal/widget/SpinnerCompat;->mOnItemClickListener:Landroid/support/v7/internal/widget/AdapterViewCompat$OnItemClickListener;

    #@9
    if-eqz v0, :cond_0

    #@b
    .line 938
    iget-object v0, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@d
    const/4 v1, 0x0

    #@e
    iget-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mListAdapter:Landroid/widget/ListAdapter;

    #@10
    invoke-interface {v2, p2}, Landroid/widget/ListAdapter;->getItemId(I)J

    #@13
    move-result-wide v2

    #@14
    invoke-virtual {v0, v1, p2, v2, v3}, Landroid/support/v7/internal/widget/SpinnerCompat;->performItemClick(Landroid/view/View;IJ)Z

    #@17
    .line 940
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->dismiss()V

    #@1a
    .line 941
    return-void
.end method

.method public setAdapter(Landroid/widget/ListAdapter;)V
    .locals 0
    .param p1, "adapter"    # Landroid/widget/ListAdapter;

    #@0
    .prologue
    .line 911
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mListAdapter:Landroid/widget/ListAdapter;

    #@2
    .line 912
    return-void
.end method

.method public setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V
    .locals 2
    .param p1, "bg"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 945
    const-string v0, "Spinner"

    #@2
    const-string v1, "Cannot set popup background for MODE_DIALOG, ignoring"

    #@4
    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@7
    .line 946
    return-void
.end method

.method public setHorizontalOffset(I)V
    .locals 2
    .param p1, "px"    # I

    #@0
    .prologue
    .line 955
    const-string v0, "Spinner"

    #@2
    const-string v1, "Cannot set horizontal offset for MODE_DIALOG, ignoring"

    #@4
    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@7
    .line 956
    return-void
.end method

.method public setPromptText(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "hintText"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 915
    iput-object p1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPrompt:Ljava/lang/CharSequence;

    #@2
    .line 916
    return-void
.end method

.method public setVerticalOffset(I)V
    .locals 2
    .param p1, "px"    # I

    #@0
    .prologue
    .line 950
    const-string v0, "Spinner"

    #@2
    const-string v1, "Cannot set vertical offset for MODE_DIALOG, ignoring"

    #@4
    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@7
    .line 951
    return-void
.end method

.method public show()V
    .locals 3

    #@0
    .prologue
    .line 923
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mListAdapter:Landroid/widget/ListAdapter;

    #@2
    if-nez v1, :cond_0

    #@4
    .line 933
    :goto_0
    return-void

    #@5
    .line 926
    :cond_0
    new-instance v0, Landroid/app/AlertDialog$Builder;

    #@7
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@9
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/SpinnerCompat;->getContext()Landroid/content/Context;

    #@c
    move-result-object v1

    #@d
    invoke-direct {v0, v1}, Landroid/app/AlertDialog$Builder;-><init>(Landroid/content/Context;)V

    #@10
    .line 927
    .local v0, "builder":Landroid/app/AlertDialog$Builder;
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPrompt:Ljava/lang/CharSequence;

    #@12
    if-eqz v1, :cond_1

    #@14
    .line 928
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPrompt:Ljava/lang/CharSequence;

    #@16
    invoke-virtual {v0, v1}, Landroid/app/AlertDialog$Builder;->setTitle(Ljava/lang/CharSequence;)Landroid/app/AlertDialog$Builder;

    #@19
    .line 930
    :cond_1
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mListAdapter:Landroid/widget/ListAdapter;

    #@1b
    iget-object v2, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->this$0:Landroid/support/v7/internal/widget/SpinnerCompat;

    #@1d
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/SpinnerCompat;->getSelectedItemPosition()I

    #@20
    move-result v2

    #@21
    invoke-virtual {v0, v1, v2, p0}, Landroid/app/AlertDialog$Builder;->setSingleChoiceItems(Landroid/widget/ListAdapter;ILandroid/content/DialogInterface$OnClickListener;)Landroid/app/AlertDialog$Builder;

    #@24
    move-result-object v1

    #@25
    invoke-virtual {v1}, Landroid/app/AlertDialog$Builder;->create()Landroid/app/AlertDialog;

    #@28
    move-result-object v1

    #@29
    iput-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPopup:Landroid/app/AlertDialog;

    #@2b
    .line 932
    iget-object v1, p0, Landroid/support/v7/internal/widget/SpinnerCompat$DialogPopup;->mPopup:Landroid/app/AlertDialog;

    #@2d
    invoke-virtual {v1}, Landroid/app/AlertDialog;->show()V

    #@30
    goto :goto_0
.end method

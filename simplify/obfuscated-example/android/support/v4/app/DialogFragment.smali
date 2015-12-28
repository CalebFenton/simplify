.class public Landroid/support/v4/app/DialogFragment;
.super Landroid/support/v4/app/Fragment;
.source "DialogFragment.java"

# interfaces
.implements Landroid/content/DialogInterface$OnCancelListener;
.implements Landroid/content/DialogInterface$OnDismissListener;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/DialogFragment$DialogStyle;
    }
.end annotation


# static fields
.field private static final SAVED_BACK_STACK_ID:Ljava/lang/String; = "android:backStackId"

.field private static final SAVED_CANCELABLE:Ljava/lang/String; = "android:cancelable"

.field private static final SAVED_DIALOG_STATE_TAG:Ljava/lang/String; = "android:savedDialogState"

.field private static final SAVED_SHOWS_DIALOG:Ljava/lang/String; = "android:showsDialog"

.field private static final SAVED_STYLE:Ljava/lang/String; = "android:style"

.field private static final SAVED_THEME:Ljava/lang/String; = "android:theme"

.field public static final STYLE_NORMAL:I = 0x0

.field public static final STYLE_NO_FRAME:I = 0x2

.field public static final STYLE_NO_INPUT:I = 0x3

.field public static final STYLE_NO_TITLE:I = 0x1


# instance fields
.field mBackStackId:I

.field mCancelable:Z

.field mDialog:Landroid/app/Dialog;

.field mDismissed:Z

.field mShownByMe:Z

.field mShowsDialog:Z

.field mStyle:I

.field mTheme:I

.field mViewDestroyed:Z


# direct methods
.method public constructor <init>()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v0, 0x0

    #@2
    .line 95
    invoke-direct {p0}, Landroid/support/v4/app/Fragment;-><init>()V

    #@5
    .line 84
    iput v0, p0, Landroid/support/v4/app/DialogFragment;->mStyle:I

    #@7
    .line 85
    iput v0, p0, Landroid/support/v4/app/DialogFragment;->mTheme:I

    #@9
    .line 86
    iput-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mCancelable:Z

    #@b
    .line 87
    iput-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@d
    .line 88
    const/4 v0, -0x1

    #@e
    iput v0, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@10
    .line 96
    return-void
.end method


# virtual methods
.method public dismiss()V
    .locals 1

    #@0
    .prologue
    .line 166
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v4/app/DialogFragment;->dismissInternal(Z)V

    #@4
    .line 167
    return-void
.end method

.method public dismissAllowingStateLoss()V
    .locals 1

    #@0
    .prologue
    .line 176
    const/4 v0, 0x1

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v4/app/DialogFragment;->dismissInternal(Z)V

    #@4
    .line 177
    return-void
.end method

.method dismissInternal(Z)V
    .locals 4
    .param p1, "allowStateLoss"    # Z

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 180
    iget-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mDismissed:Z

    #@3
    if-eqz v1, :cond_0

    #@5
    .line 203
    :goto_0
    return-void

    #@6
    .line 183
    :cond_0
    iput-boolean v3, p0, Landroid/support/v4/app/DialogFragment;->mDismissed:Z

    #@8
    .line 184
    const/4 v1, 0x0

    #@9
    iput-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mShownByMe:Z

    #@b
    .line 185
    iget-object v1, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@d
    if-eqz v1, :cond_1

    #@f
    .line 186
    iget-object v1, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@11
    invoke-virtual {v1}, Landroid/app/Dialog;->dismiss()V

    #@14
    .line 187
    const/4 v1, 0x0

    #@15
    iput-object v1, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@17
    .line 189
    :cond_1
    iput-boolean v3, p0, Landroid/support/v4/app/DialogFragment;->mViewDestroyed:Z

    #@19
    .line 190
    iget v1, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@1b
    if-ltz v1, :cond_2

    #@1d
    .line 191
    invoke-virtual {p0}, Landroid/support/v4/app/DialogFragment;->getFragmentManager()Landroid/support/v4/app/FragmentManager;

    #@20
    move-result-object v1

    #@21
    iget v2, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@23
    invoke-virtual {v1, v2, v3}, Landroid/support/v4/app/FragmentManager;->popBackStack(II)V

    #@26
    .line 193
    const/4 v1, -0x1

    #@27
    iput v1, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@29
    goto :goto_0

    #@2a
    .line 195
    :cond_2
    invoke-virtual {p0}, Landroid/support/v4/app/DialogFragment;->getFragmentManager()Landroid/support/v4/app/FragmentManager;

    #@2d
    move-result-object v1

    #@2e
    invoke-virtual {v1}, Landroid/support/v4/app/FragmentManager;->beginTransaction()Landroid/support/v4/app/FragmentTransaction;

    #@31
    move-result-object v0

    #@32
    .line 196
    .local v0, "ft":Landroid/support/v4/app/FragmentTransaction;
    invoke-virtual {v0, p0}, Landroid/support/v4/app/FragmentTransaction;->remove(Landroid/support/v4/app/Fragment;)Landroid/support/v4/app/FragmentTransaction;

    #@35
    .line 197
    if-eqz p1, :cond_3

    #@37
    .line 198
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentTransaction;->commitAllowingStateLoss()I

    #@3a
    goto :goto_0

    #@3b
    .line 200
    :cond_3
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentTransaction;->commit()I

    #@3e
    goto :goto_0
.end method

.method public getDialog()Landroid/app/Dialog;
    .locals 1

    #@0
    .prologue
    .line 206
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@2
    return-object v0
.end method

.method public getLayoutInflater(Landroid/os/Bundle;)Landroid/view/LayoutInflater;
    .locals 2
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 303
    iget-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 304
    invoke-super {p0, p1}, Landroid/support/v4/app/Fragment;->getLayoutInflater(Landroid/os/Bundle;)Landroid/view/LayoutInflater;

    #@7
    move-result-object v0

    #@8
    .line 322
    :goto_0
    return-object v0

    #@9
    .line 307
    :cond_0
    invoke-virtual {p0, p1}, Landroid/support/v4/app/DialogFragment;->onCreateDialog(Landroid/os/Bundle;)Landroid/app/Dialog;

    #@c
    move-result-object v0

    #@d
    iput-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@f
    .line 308
    iget v0, p0, Landroid/support/v4/app/DialogFragment;->mStyle:I

    #@11
    packed-switch v0, :pswitch_data_0

    #@14
    .line 318
    :goto_1
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@16
    if-eqz v0, :cond_1

    #@18
    .line 319
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@1a
    invoke-virtual {v0}, Landroid/app/Dialog;->getContext()Landroid/content/Context;

    #@1d
    move-result-object v0

    #@1e
    const-string v1, "layout_inflater"

    #@20
    invoke-virtual {v0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@23
    move-result-object v0

    #@24
    check-cast v0, Landroid/view/LayoutInflater;

    #@26
    goto :goto_0

    #@27
    .line 310
    :pswitch_0
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@29
    invoke-virtual {v0}, Landroid/app/Dialog;->getWindow()Landroid/view/Window;

    #@2c
    move-result-object v0

    #@2d
    const/16 v1, 0x18

    #@2f
    invoke-virtual {v0, v1}, Landroid/view/Window;->addFlags(I)V

    #@32
    .line 316
    :pswitch_1
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@34
    const/4 v1, 0x1

    #@35
    invoke-virtual {v0, v1}, Landroid/app/Dialog;->requestWindowFeature(I)Z

    #@38
    goto :goto_1

    #@39
    .line 322
    :cond_1
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mActivity:Landroid/support/v4/app/FragmentActivity;

    #@3b
    const-string v1, "layout_inflater"

    #@3d
    invoke-virtual {v0, v1}, Landroid/support/v4/app/FragmentActivity;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@40
    move-result-object v0

    #@41
    check-cast v0, Landroid/view/LayoutInflater;

    #@43
    goto :goto_0

    #@44
    .line 308
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_1
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public getShowsDialog()Z
    .locals 1

    #@0
    .prologue
    .line 260
    iget-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@2
    return v0
.end method

.method public getTheme()I
    .locals 1

    #@0
    .prologue
    .line 211
    iget v0, p0, Landroid/support/v4/app/DialogFragment;->mTheme:I

    #@2
    return v0
.end method

.method public isCancelable()Z
    .locals 1

    #@0
    .prologue
    .line 232
    iget-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mCancelable:Z

    #@2
    return v0
.end method

.method public onActivityCreated(Landroid/os/Bundle;)V
    .locals 4
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 368
    invoke-super {p0, p1}, Landroid/support/v4/app/Fragment;->onActivityCreated(Landroid/os/Bundle;)V

    #@3
    .line 370
    iget-boolean v2, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@5
    if-nez v2, :cond_1

    #@7
    .line 391
    :cond_0
    :goto_0
    return-void

    #@8
    .line 374
    :cond_1
    invoke-virtual {p0}, Landroid/support/v4/app/DialogFragment;->getView()Landroid/view/View;

    #@b
    move-result-object v1

    #@c
    .line 375
    .local v1, "view":Landroid/view/View;
    if-eqz v1, :cond_3

    #@e
    .line 376
    invoke-virtual {v1}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@11
    move-result-object v2

    #@12
    if-eqz v2, :cond_2

    #@14
    .line 377
    new-instance v2, Ljava/lang/IllegalStateException;

    #@16
    const-string v3, "DialogFragment can not be attached to a container view"

    #@18
    invoke-direct {v2, v3}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@1b
    throw v2

    #@1c
    .line 379
    :cond_2
    iget-object v2, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@1e
    invoke-virtual {v2, v1}, Landroid/app/Dialog;->setContentView(Landroid/view/View;)V

    #@21
    .line 381
    :cond_3
    iget-object v2, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@23
    invoke-virtual {p0}, Landroid/support/v4/app/DialogFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@26
    move-result-object v3

    #@27
    invoke-virtual {v2, v3}, Landroid/app/Dialog;->setOwnerActivity(Landroid/app/Activity;)V

    #@2a
    .line 382
    iget-object v2, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@2c
    iget-boolean v3, p0, Landroid/support/v4/app/DialogFragment;->mCancelable:Z

    #@2e
    invoke-virtual {v2, v3}, Landroid/app/Dialog;->setCancelable(Z)V

    #@31
    .line 383
    iget-object v2, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@33
    invoke-virtual {v2, p0}, Landroid/app/Dialog;->setOnCancelListener(Landroid/content/DialogInterface$OnCancelListener;)V

    #@36
    .line 384
    iget-object v2, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@38
    invoke-virtual {v2, p0}, Landroid/app/Dialog;->setOnDismissListener(Landroid/content/DialogInterface$OnDismissListener;)V

    #@3b
    .line 385
    if-eqz p1, :cond_0

    #@3d
    .line 386
    const-string v2, "android:savedDialogState"

    #@3f
    invoke-virtual {p1, v2}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    #@42
    move-result-object v0

    #@43
    .line 387
    .local v0, "dialogState":Landroid/os/Bundle;
    if-eqz v0, :cond_0

    #@45
    .line 388
    iget-object v2, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@47
    invoke-virtual {v2, v0}, Landroid/app/Dialog;->onRestoreInstanceState(Landroid/os/Bundle;)V

    #@4a
    goto :goto_0
.end method

.method public onAttach(Landroid/app/Activity;)V
    .locals 1
    .param p1, "activity"    # Landroid/app/Activity;

    #@0
    .prologue
    .line 265
    invoke-super {p0, p1}, Landroid/support/v4/app/Fragment;->onAttach(Landroid/app/Activity;)V

    #@3
    .line 266
    iget-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mShownByMe:Z

    #@5
    if-nez v0, :cond_0

    #@7
    .line 269
    const/4 v0, 0x0

    #@8
    iput-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mDismissed:Z

    #@a
    .line 271
    :cond_0
    return-void
.end method

.method public onCancel(Landroid/content/DialogInterface;)V
    .locals 0
    .param p1, "dialog"    # Landroid/content/DialogInterface;

    #@0
    .prologue
    .line 354
    return-void
.end method

.method public onCreate(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 286
    invoke-super {p0, p1}, Landroid/support/v4/app/Fragment;->onCreate(Landroid/os/Bundle;)V

    #@5
    .line 288
    iget v0, p0, Landroid/support/v4/app/DialogFragment;->mContainerId:I

    #@7
    if-nez v0, :cond_1

    #@9
    move v0, v1

    #@a
    :goto_0
    iput-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@c
    .line 290
    if-eqz p1, :cond_0

    #@e
    .line 291
    const-string v0, "android:style"

    #@10
    invoke-virtual {p1, v0, v2}, Landroid/os/Bundle;->getInt(Ljava/lang/String;I)I

    #@13
    move-result v0

    #@14
    iput v0, p0, Landroid/support/v4/app/DialogFragment;->mStyle:I

    #@16
    .line 292
    const-string v0, "android:theme"

    #@18
    invoke-virtual {p1, v0, v2}, Landroid/os/Bundle;->getInt(Ljava/lang/String;I)I

    #@1b
    move-result v0

    #@1c
    iput v0, p0, Landroid/support/v4/app/DialogFragment;->mTheme:I

    #@1e
    .line 293
    const-string v0, "android:cancelable"

    #@20
    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;Z)Z

    #@23
    move-result v0

    #@24
    iput-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mCancelable:Z

    #@26
    .line 294
    const-string v0, "android:showsDialog"

    #@28
    iget-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@2a
    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;Z)Z

    #@2d
    move-result v0

    #@2e
    iput-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@30
    .line 295
    const-string v0, "android:backStackId"

    #@32
    const/4 v1, -0x1

    #@33
    invoke-virtual {p1, v0, v1}, Landroid/os/Bundle;->getInt(Ljava/lang/String;I)I

    #@36
    move-result v0

    #@37
    iput v0, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@39
    .line 298
    :cond_0
    return-void

    #@3a
    :cond_1
    move v0, v2

    #@3b
    .line 288
    goto :goto_0
.end method

.method public onCreateDialog(Landroid/os/Bundle;)Landroid/app/Dialog;
    .locals 3
    .param p1, "savedInstanceState"    # Landroid/os/Bundle;
    .annotation build Landroid/support/annotation/NonNull;
    .end annotation

    #@0
    .prologue
    .line 350
    new-instance v0, Landroid/app/Dialog;

    #@2
    invoke-virtual {p0}, Landroid/support/v4/app/DialogFragment;->getActivity()Landroid/support/v4/app/FragmentActivity;

    #@5
    move-result-object v1

    #@6
    invoke-virtual {p0}, Landroid/support/v4/app/DialogFragment;->getTheme()I

    #@9
    move-result v2

    #@a
    invoke-direct {v0, v1, v2}, Landroid/app/Dialog;-><init>(Landroid/content/Context;I)V

    #@d
    return-object v0
.end method

.method public onDestroyView()V
    .locals 1

    #@0
    .prologue
    .line 441
    invoke-super {p0}, Landroid/support/v4/app/Fragment;->onDestroyView()V

    #@3
    .line 442
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 446
    const/4 v0, 0x1

    #@8
    iput-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mViewDestroyed:Z

    #@a
    .line 447
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@c
    invoke-virtual {v0}, Landroid/app/Dialog;->dismiss()V

    #@f
    .line 448
    const/4 v0, 0x0

    #@10
    iput-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@12
    .line 450
    :cond_0
    return-void
.end method

.method public onDetach()V
    .locals 1

    #@0
    .prologue
    .line 275
    invoke-super {p0}, Landroid/support/v4/app/Fragment;->onDetach()V

    #@3
    .line 276
    iget-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mShownByMe:Z

    #@5
    if-nez v0, :cond_0

    #@7
    iget-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mDismissed:Z

    #@9
    if-nez v0, :cond_0

    #@b
    .line 280
    const/4 v0, 0x1

    #@c
    iput-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mDismissed:Z

    #@e
    .line 282
    :cond_0
    return-void
.end method

.method public onDismiss(Landroid/content/DialogInterface;)V
    .locals 1
    .param p1, "dialog"    # Landroid/content/DialogInterface;

    #@0
    .prologue
    .line 357
    iget-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mViewDestroyed:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 362
    const/4 v0, 0x1

    #@5
    invoke-virtual {p0, v0}, Landroid/support/v4/app/DialogFragment;->dismissInternal(Z)V

    #@8
    .line 364
    :cond_0
    return-void
.end method

.method public onSaveInstanceState(Landroid/os/Bundle;)V
    .locals 3
    .param p1, "outState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 404
    invoke-super {p0, p1}, Landroid/support/v4/app/Fragment;->onSaveInstanceState(Landroid/os/Bundle;)V

    #@3
    .line 405
    iget-object v1, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@5
    if-eqz v1, :cond_0

    #@7
    .line 406
    iget-object v1, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@9
    invoke-virtual {v1}, Landroid/app/Dialog;->onSaveInstanceState()Landroid/os/Bundle;

    #@c
    move-result-object v0

    #@d
    .line 407
    .local v0, "dialogState":Landroid/os/Bundle;
    if-eqz v0, :cond_0

    #@f
    .line 408
    const-string v1, "android:savedDialogState"

    #@11
    invoke-virtual {p1, v1, v0}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    #@14
    .line 411
    .end local v0    # "dialogState":Landroid/os/Bundle;
    :cond_0
    iget v1, p0, Landroid/support/v4/app/DialogFragment;->mStyle:I

    #@16
    if-eqz v1, :cond_1

    #@18
    .line 412
    const-string v1, "android:style"

    #@1a
    iget v2, p0, Landroid/support/v4/app/DialogFragment;->mStyle:I

    #@1c
    invoke-virtual {p1, v1, v2}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    #@1f
    .line 414
    :cond_1
    iget v1, p0, Landroid/support/v4/app/DialogFragment;->mTheme:I

    #@21
    if-eqz v1, :cond_2

    #@23
    .line 415
    const-string v1, "android:theme"

    #@25
    iget v2, p0, Landroid/support/v4/app/DialogFragment;->mTheme:I

    #@27
    invoke-virtual {p1, v1, v2}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    #@2a
    .line 417
    :cond_2
    iget-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mCancelable:Z

    #@2c
    if-nez v1, :cond_3

    #@2e
    .line 418
    const-string v1, "android:cancelable"

    #@30
    iget-boolean v2, p0, Landroid/support/v4/app/DialogFragment;->mCancelable:Z

    #@32
    invoke-virtual {p1, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    #@35
    .line 420
    :cond_3
    iget-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@37
    if-nez v1, :cond_4

    #@39
    .line 421
    const-string v1, "android:showsDialog"

    #@3b
    iget-boolean v2, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@3d
    invoke-virtual {p1, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    #@40
    .line 423
    :cond_4
    iget v1, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@42
    const/4 v2, -0x1

    #@43
    if-eq v1, v2, :cond_5

    #@45
    .line 424
    const-string v1, "android:backStackId"

    #@47
    iget v2, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@49
    invoke-virtual {p1, v1, v2}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    #@4c
    .line 426
    :cond_5
    return-void
.end method

.method public onStart()V
    .locals 1

    #@0
    .prologue
    .line 395
    invoke-super {p0}, Landroid/support/v4/app/Fragment;->onStart()V

    #@3
    .line 396
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 397
    const/4 v0, 0x0

    #@8
    iput-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mViewDestroyed:Z

    #@a
    .line 398
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@c
    invoke-virtual {v0}, Landroid/app/Dialog;->show()V

    #@f
    .line 400
    :cond_0
    return-void
.end method

.method public onStop()V
    .locals 1

    #@0
    .prologue
    .line 430
    invoke-super {p0}, Landroid/support/v4/app/Fragment;->onStop()V

    #@3
    .line 431
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 432
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@9
    invoke-virtual {v0}, Landroid/app/Dialog;->hide()V

    #@c
    .line 434
    :cond_0
    return-void
.end method

.method public setCancelable(Z)V
    .locals 1
    .param p1, "cancelable"    # Z

    #@0
    .prologue
    .line 224
    iput-boolean p1, p0, Landroid/support/v4/app/DialogFragment;->mCancelable:Z

    #@2
    .line 225
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@4
    if-eqz v0, :cond_0

    #@6
    iget-object v0, p0, Landroid/support/v4/app/DialogFragment;->mDialog:Landroid/app/Dialog;

    #@8
    invoke-virtual {v0, p1}, Landroid/app/Dialog;->setCancelable(Z)V

    #@b
    .line 226
    :cond_0
    return-void
.end method

.method public setShowsDialog(Z)V
    .locals 0
    .param p1, "showsDialog"    # Z

    #@0
    .prologue
    .line 253
    iput-boolean p1, p0, Landroid/support/v4/app/DialogFragment;->mShowsDialog:Z

    #@2
    .line 254
    return-void
.end method

.method public setStyle(II)V
    .locals 2
    .param p1, "style"    # I
    .param p2, "theme"    # I

    #@0
    .prologue
    .line 113
    iput p1, p0, Landroid/support/v4/app/DialogFragment;->mStyle:I

    #@2
    .line 114
    iget v0, p0, Landroid/support/v4/app/DialogFragment;->mStyle:I

    #@4
    const/4 v1, 0x2

    #@5
    if-eq v0, v1, :cond_0

    #@7
    iget v0, p0, Landroid/support/v4/app/DialogFragment;->mStyle:I

    #@9
    const/4 v1, 0x3

    #@a
    if-ne v0, v1, :cond_1

    #@c
    .line 115
    :cond_0
    const v0, 0x1030059

    #@f
    iput v0, p0, Landroid/support/v4/app/DialogFragment;->mTheme:I

    #@11
    .line 117
    :cond_1
    if-eqz p2, :cond_2

    #@13
    .line 118
    iput p2, p0, Landroid/support/v4/app/DialogFragment;->mTheme:I

    #@15
    .line 120
    :cond_2
    return-void
.end method

.method public show(Landroid/support/v4/app/FragmentTransaction;Ljava/lang/String;)I
    .locals 2
    .param p1, "transaction"    # Landroid/support/v4/app/FragmentTransaction;
    .param p2, "tag"    # Ljava/lang/String;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 151
    iput-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mDismissed:Z

    #@3
    .line 152
    const/4 v0, 0x1

    #@4
    iput-boolean v0, p0, Landroid/support/v4/app/DialogFragment;->mShownByMe:Z

    #@6
    .line 153
    invoke-virtual {p1, p0, p2}, Landroid/support/v4/app/FragmentTransaction;->add(Landroid/support/v4/app/Fragment;Ljava/lang/String;)Landroid/support/v4/app/FragmentTransaction;

    #@9
    .line 154
    iput-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mViewDestroyed:Z

    #@b
    .line 155
    invoke-virtual {p1}, Landroid/support/v4/app/FragmentTransaction;->commit()I

    #@e
    move-result v0

    #@f
    iput v0, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@11
    .line 156
    iget v0, p0, Landroid/support/v4/app/DialogFragment;->mBackStackId:I

    #@13
    return v0
.end method

.method public show(Landroid/support/v4/app/FragmentManager;Ljava/lang/String;)V
    .locals 2
    .param p1, "manager"    # Landroid/support/v4/app/FragmentManager;
    .param p2, "tag"    # Ljava/lang/String;

    #@0
    .prologue
    .line 134
    const/4 v1, 0x0

    #@1
    iput-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mDismissed:Z

    #@3
    .line 135
    const/4 v1, 0x1

    #@4
    iput-boolean v1, p0, Landroid/support/v4/app/DialogFragment;->mShownByMe:Z

    #@6
    .line 136
    invoke-virtual {p1}, Landroid/support/v4/app/FragmentManager;->beginTransaction()Landroid/support/v4/app/FragmentTransaction;

    #@9
    move-result-object v0

    #@a
    .line 137
    .local v0, "ft":Landroid/support/v4/app/FragmentTransaction;
    invoke-virtual {v0, p0, p2}, Landroid/support/v4/app/FragmentTransaction;->add(Landroid/support/v4/app/Fragment;Ljava/lang/String;)Landroid/support/v4/app/FragmentTransaction;

    #@d
    .line 138
    invoke-virtual {v0}, Landroid/support/v4/app/FragmentTransaction;->commit()I

    #@10
    .line 139
    return-void
.end method

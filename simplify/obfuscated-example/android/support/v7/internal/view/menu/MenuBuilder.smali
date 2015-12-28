.class public Landroid/support/v7/internal/view/menu/MenuBuilder;
.super Ljava/lang/Object;
.source "MenuBuilder.java"

# interfaces
.implements Landroid/support/v4/internal/view/SupportMenu;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/view/menu/MenuBuilder$ItemInvoker;,
        Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;
    }
.end annotation


# static fields
.field private static final ACTION_VIEW_STATES_KEY:Ljava/lang/String; = "android:menu:actionviewstates"

.field private static final EXPANDED_ACTION_VIEW_ID:Ljava/lang/String; = "android:menu:expandedactionview"

.field private static final PRESENTER_KEY:Ljava/lang/String; = "android:menu:presenters"

.field private static final TAG:Ljava/lang/String; = "MenuBuilder"

.field private static final sCategoryToOrder:[I


# instance fields
.field private mActionItems:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;"
        }
    .end annotation
.end field

.field private mCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

.field private final mContext:Landroid/content/Context;

.field private mCurrentMenuInfo:Landroid/view/ContextMenu$ContextMenuInfo;

.field private mDefaultShowAsAction:I

.field private mExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

.field private mFrozenViewStates:Landroid/util/SparseArray;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/util/SparseArray",
            "<",
            "Landroid/os/Parcelable;",
            ">;"
        }
    .end annotation
.end field

.field mHeaderIcon:Landroid/graphics/drawable/Drawable;

.field mHeaderTitle:Ljava/lang/CharSequence;

.field mHeaderView:Landroid/view/View;

.field private mIsActionItemsStale:Z

.field private mIsClosing:Z

.field private mIsVisibleItemsStale:Z

.field private mItems:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;"
        }
    .end annotation
.end field

.field private mItemsChangedWhileDispatchPrevented:Z

.field private mNonActionItems:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;"
        }
    .end annotation
.end field

.field private mOptionalIconsVisible:Z

.field private mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/concurrent/CopyOnWriteArrayList",
            "<",
            "Ljava/lang/ref/WeakReference",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuPresenter;",
            ">;>;"
        }
    .end annotation
.end field

.field private mPreventDispatchingItemsChanged:Z

.field private mQwertyMode:Z

.field private final mResources:Landroid/content/res/Resources;

.field private mShortcutsVisible:Z

.field private mTempShortcutItemList:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;"
        }
    .end annotation
.end field

.field private mVisibleItems:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 62
    const/4 v0, 0x6

    #@1
    new-array v0, v0, [I

    #@3
    fill-array-data v0, :array_0

    #@6
    sput-object v0, Landroid/support/v7/internal/view/menu/MenuBuilder;->sCategoryToOrder:[I

    #@8
    return-void

    #@9
    nop

    #@a
    :array_0
    .array-data 4
        0x1
        0x4
        0x5
        0x3
        0x2
        0x0
    .end array-data
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v0, 0x0

    #@2
    .line 209
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 129
    iput v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mDefaultShowAsAction:I

    #@7
    .line 160
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPreventDispatchingItemsChanged:Z

    #@9
    .line 162
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItemsChangedWhileDispatchPrevented:Z

    #@b
    .line 164
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mOptionalIconsVisible:Z

    #@d
    .line 166
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsClosing:Z

    #@f
    .line 168
    new-instance v0, Ljava/util/ArrayList;

    #@11
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@14
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mTempShortcutItemList:Ljava/util/ArrayList;

    #@16
    .line 170
    new-instance v0, Ljava/util/concurrent/CopyOnWriteArrayList;

    #@18
    invoke-direct {v0}, Ljava/util/concurrent/CopyOnWriteArrayList;-><init>()V

    #@1b
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@1d
    .line 210
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mContext:Landroid/content/Context;

    #@1f
    .line 211
    invoke-virtual {p1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@22
    move-result-object v0

    #@23
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mResources:Landroid/content/res/Resources;

    #@25
    .line 213
    new-instance v0, Ljava/util/ArrayList;

    #@27
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@2a
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@2c
    .line 215
    new-instance v0, Ljava/util/ArrayList;

    #@2e
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@31
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mVisibleItems:Ljava/util/ArrayList;

    #@33
    .line 216
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsVisibleItemsStale:Z

    #@35
    .line 218
    new-instance v0, Ljava/util/ArrayList;

    #@37
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@3a
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mActionItems:Ljava/util/ArrayList;

    #@3c
    .line 219
    new-instance v0, Ljava/util/ArrayList;

    #@3e
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@41
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mNonActionItems:Ljava/util/ArrayList;

    #@43
    .line 220
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsActionItemsStale:Z

    #@45
    .line 222
    invoke-direct {p0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setShortcutsVisibleInner(Z)V

    #@48
    .line 223
    return-void
.end method

.method private addInternal(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 8
    .param p1, "group"    # I
    .param p2, "id"    # I
    .param p3, "categoryOrder"    # I
    .param p4, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 428
    invoke-static {p3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getOrdering(I)I

    #@3
    move-result v4

    #@4
    .line 430
    .local v4, "ordering":I
    iget v6, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mDefaultShowAsAction:I

    #@6
    move-object v0, p0

    #@7
    move v1, p1

    #@8
    move v2, p2

    #@9
    move v3, p3

    #@a
    move-object v5, p4

    #@b
    invoke-direct/range {v0 .. v6}, Landroid/support/v7/internal/view/menu/MenuBuilder;->createNewMenuItem(IIIILjava/lang/CharSequence;I)Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@e
    move-result-object v7

    #@f
    .line 433
    .local v7, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mCurrentMenuInfo:Landroid/view/ContextMenu$ContextMenuInfo;

    #@11
    if-eqz v0, :cond_0

    #@13
    .line 435
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mCurrentMenuInfo:Landroid/view/ContextMenu$ContextMenuInfo;

    #@15
    invoke-virtual {v7, v0}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setMenuInfo(Landroid/view/ContextMenu$ContextMenuInfo;)V

    #@18
    .line 438
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@1a
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@1c
    invoke-static {v1, v4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findInsertIndex(Ljava/util/ArrayList;I)I

    #@1f
    move-result v1

    #@20
    invoke-virtual {v0, v1, v7}, Ljava/util/ArrayList;->add(ILjava/lang/Object;)V

    #@23
    .line 439
    const/4 v0, 0x1

    #@24
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@27
    .line 441
    return-object v7
.end method

.method private createNewMenuItem(IIIILjava/lang/CharSequence;I)Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .locals 8
    .param p1, "group"    # I
    .param p2, "id"    # I
    .param p3, "categoryOrder"    # I
    .param p4, "ordering"    # I
    .param p5, "title"    # Ljava/lang/CharSequence;
    .param p6, "defaultShowAsAction"    # I

    #@0
    .prologue
    .line 447
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    move-object v1, p0

    #@3
    move v2, p1

    #@4
    move v3, p2

    #@5
    move v4, p3

    #@6
    move v5, p4

    #@7
    move-object v6, p5

    #@8
    move v7, p6

    #@9
    invoke-direct/range {v0 .. v7}, Landroid/support/v7/internal/view/menu/MenuItemImpl;-><init>(Landroid/support/v7/internal/view/menu/MenuBuilder;IIIILjava/lang/CharSequence;I)V

    #@c
    return-object v0
.end method

.method private dispatchPresenterUpdate(Z)V
    .locals 4
    .param p1, "cleared"    # Z

    #@0
    .prologue
    .line 271
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2
    invoke-virtual {v3}, Ljava/util/concurrent/CopyOnWriteArrayList;->isEmpty()Z

    #@5
    move-result v3

    #@6
    if-eqz v3, :cond_0

    #@8
    .line 283
    :goto_0
    return-void

    #@9
    .line 273
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@c
    .line 274
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@e
    invoke-virtual {v3}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@11
    move-result-object v0

    #@12
    .local v0, "i$":Ljava/util/Iterator;
    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@15
    move-result v3

    #@16
    if-eqz v3, :cond_2

    #@18
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@1b
    move-result-object v2

    #@1c
    check-cast v2, Ljava/lang/ref/WeakReference;

    #@1e
    .line 275
    .local v2, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v2}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@21
    move-result-object v1

    #@22
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@24
    .line 276
    .local v1, "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-nez v1, :cond_1

    #@26
    .line 277
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@28
    invoke-virtual {v3, v2}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@2b
    goto :goto_1

    #@2c
    .line 279
    :cond_1
    invoke-interface {v1, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter;->updateMenuView(Z)V

    #@2f
    goto :goto_1

    #@30
    .line 282
    .end local v1    # "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    .end local v2    # "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@33
    goto :goto_0
.end method

.method private dispatchRestoreInstanceState(Landroid/os/Bundle;)V
    .locals 7
    .param p1, "state"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 331
    const-string v6, "android:menu:presenters"

    #@2
    invoke-virtual {p1, v6}, Landroid/os/Bundle;->getSparseParcelableArray(Ljava/lang/String;)Landroid/util/SparseArray;

    #@5
    move-result-object v4

    #@6
    .line 333
    .local v4, "presenterStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    if-eqz v4, :cond_0

    #@8
    iget-object v6, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@a
    invoke-virtual {v6}, Ljava/util/concurrent/CopyOnWriteArrayList;->isEmpty()Z

    #@d
    move-result v6

    #@e
    if-eqz v6, :cond_1

    #@10
    .line 349
    :cond_0
    return-void

    #@11
    .line 335
    :cond_1
    iget-object v6, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@13
    invoke-virtual {v6}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@16
    move-result-object v0

    #@17
    .local v0, "i$":Ljava/util/Iterator;
    :cond_2
    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@1a
    move-result v6

    #@1b
    if-eqz v6, :cond_0

    #@1d
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@20
    move-result-object v5

    #@21
    check-cast v5, Ljava/lang/ref/WeakReference;

    #@23
    .line 336
    .local v5, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v5}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@26
    move-result-object v3

    #@27
    check-cast v3, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@29
    .line 337
    .local v3, "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-nez v3, :cond_3

    #@2b
    .line 338
    iget-object v6, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2d
    invoke-virtual {v6, v5}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@30
    goto :goto_0

    #@31
    .line 340
    :cond_3
    invoke-interface {v3}, Landroid/support/v7/internal/view/menu/MenuPresenter;->getId()I

    #@34
    move-result v1

    #@35
    .line 341
    .local v1, "id":I
    if-lez v1, :cond_2

    #@37
    .line 342
    invoke-virtual {v4, v1}, Landroid/util/SparseArray;->get(I)Ljava/lang/Object;

    #@3a
    move-result-object v2

    #@3b
    check-cast v2, Landroid/os/Parcelable;

    #@3d
    .line 343
    .local v2, "parcel":Landroid/os/Parcelable;
    if-eqz v2, :cond_2

    #@3f
    .line 344
    invoke-interface {v3, v2}, Landroid/support/v7/internal/view/menu/MenuPresenter;->onRestoreInstanceState(Landroid/os/Parcelable;)V

    #@42
    goto :goto_0
.end method

.method private dispatchSaveInstanceState(Landroid/os/Bundle;)V
    .locals 7
    .param p1, "outState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 308
    iget-object v6, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2
    invoke-virtual {v6}, Ljava/util/concurrent/CopyOnWriteArrayList;->isEmpty()Z

    #@5
    move-result v6

    #@6
    if-eqz v6, :cond_0

    #@8
    .line 328
    :goto_0
    return-void

    #@9
    .line 310
    :cond_0
    new-instance v3, Landroid/util/SparseArray;

    #@b
    invoke-direct {v3}, Landroid/util/SparseArray;-><init>()V

    #@e
    .line 312
    .local v3, "presenterStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    iget-object v6, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@10
    invoke-virtual {v6}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@13
    move-result-object v0

    #@14
    .local v0, "i$":Ljava/util/Iterator;
    :cond_1
    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@17
    move-result v6

    #@18
    if-eqz v6, :cond_3

    #@1a
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@1d
    move-result-object v4

    #@1e
    check-cast v4, Ljava/lang/ref/WeakReference;

    #@20
    .line 313
    .local v4, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v4}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@23
    move-result-object v2

    #@24
    check-cast v2, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@26
    .line 314
    .local v2, "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-nez v2, :cond_2

    #@28
    .line 315
    iget-object v6, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2a
    invoke-virtual {v6, v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@2d
    goto :goto_1

    #@2e
    .line 317
    :cond_2
    invoke-interface {v2}, Landroid/support/v7/internal/view/menu/MenuPresenter;->getId()I

    #@31
    move-result v1

    #@32
    .line 318
    .local v1, "id":I
    if-lez v1, :cond_1

    #@34
    .line 319
    invoke-interface {v2}, Landroid/support/v7/internal/view/menu/MenuPresenter;->onSaveInstanceState()Landroid/os/Parcelable;

    #@37
    move-result-object v5

    #@38
    .line 320
    .local v5, "state":Landroid/os/Parcelable;
    if-eqz v5, :cond_1

    #@3a
    .line 321
    invoke-virtual {v3, v1, v5}, Landroid/util/SparseArray;->put(ILjava/lang/Object;)V

    #@3d
    goto :goto_1

    #@3e
    .line 327
    .end local v1    # "id":I
    .end local v2    # "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    .end local v4    # "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    .end local v5    # "state":Landroid/os/Parcelable;
    :cond_3
    const-string v6, "android:menu:presenters"

    #@40
    invoke-virtual {p1, v6, v3}, Landroid/os/Bundle;->putSparseParcelableArray(Ljava/lang/String;Landroid/util/SparseArray;)V

    #@43
    goto :goto_0
.end method

.method private dispatchSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;Landroid/support/v7/internal/view/menu/MenuPresenter;)Z
    .locals 5
    .param p1, "subMenu"    # Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    .param p2, "preferredPresenter"    # Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@0
    .prologue
    .line 287
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->isEmpty()Z

    #@5
    move-result v4

    #@6
    if-eqz v4, :cond_1

    #@8
    const/4 v3, 0x0

    #@9
    .line 304
    :cond_0
    return v3

    #@a
    .line 289
    :cond_1
    const/4 v3, 0x0

    #@b
    .line 292
    .local v3, "result":Z
    if-eqz p2, :cond_2

    #@d
    .line 293
    invoke-interface {p2, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter;->onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z

    #@10
    move-result v3

    #@11
    .line 296
    :cond_2
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@13
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@16
    move-result-object v0

    #@17
    .local v0, "i$":Ljava/util/Iterator;
    :cond_3
    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@1a
    move-result v4

    #@1b
    if-eqz v4, :cond_0

    #@1d
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@20
    move-result-object v2

    #@21
    check-cast v2, Ljava/lang/ref/WeakReference;

    #@23
    .line 297
    .local v2, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v2}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@26
    move-result-object v1

    #@27
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@29
    .line 298
    .local v1, "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-nez v1, :cond_4

    #@2b
    .line 299
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2d
    invoke-virtual {v4, v2}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@30
    goto :goto_0

    #@31
    .line 300
    :cond_4
    if-nez v3, :cond_3

    #@33
    .line 301
    invoke-interface {v1, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter;->onSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)Z

    #@36
    move-result v3

    #@37
    goto :goto_0
.end method

.method private static findInsertIndex(Ljava/util/ArrayList;I)I
    .locals 3
    .param p1, "ordering"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;I)I"
        }
    .end annotation

    #@0
    .prologue
    .line 815
    .local p0, "items":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    invoke-virtual {p0}, Ljava/util/ArrayList;->size()I

    #@3
    move-result v2

    #@4
    add-int/lit8 v0, v2, -0x1

    #@6
    .local v0, "i":I
    :goto_0
    if-ltz v0, :cond_1

    #@8
    .line 816
    invoke-virtual {p0, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@b
    move-result-object v1

    #@c
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@e
    .line 817
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getOrdering()I

    #@11
    move-result v2

    #@12
    if-gt v2, p1, :cond_0

    #@14
    .line 818
    add-int/lit8 v2, v0, 0x1

    #@16
    .line 822
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :goto_1
    return v2

    #@17
    .line 815
    .restart local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_0
    add-int/lit8 v0, v0, -0x1

    #@19
    goto :goto_0

    #@1a
    .line 822
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1
    const/4 v2, 0x0

    #@1b
    goto :goto_1
.end method

.method private static getOrdering(I)I
    .locals 3
    .param p0, "categoryOrder"    # I

    #@0
    .prologue
    .line 748
    const/high16 v1, -0x10000

    #@2
    and-int/2addr v1, p0

    #@3
    shr-int/lit8 v0, v1, 0x10

    #@5
    .line 750
    .local v0, "index":I
    if-ltz v0, :cond_0

    #@7
    sget-object v1, Landroid/support/v7/internal/view/menu/MenuBuilder;->sCategoryToOrder:[I

    #@9
    array-length v1, v1

    #@a
    if-lt v0, v1, :cond_1

    #@c
    .line 751
    :cond_0
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@e
    const-string v2, "order does not contain a valid category."

    #@10
    invoke-direct {v1, v2}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@13
    throw v1

    #@14
    .line 754
    :cond_1
    sget-object v1, Landroid/support/v7/internal/view/menu/MenuBuilder;->sCategoryToOrder:[I

    #@16
    aget v1, v1, v0

    #@18
    shl-int/lit8 v1, v1, 0x10

    #@1a
    const v2, 0xffff

    #@1d
    and-int/2addr v2, p0

    #@1e
    or-int/2addr v1, v2

    #@1f
    return v1
.end method

.method private removeItemAtInt(IZ)V
    .locals 1
    .param p1, "index"    # I
    .param p2, "updateChildrenOnMenuViews"    # Z

    #@0
    .prologue
    .line 557
    if-ltz p1, :cond_0

    #@2
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@4
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@7
    move-result v0

    #@8
    if-lt p1, v0, :cond_1

    #@a
    .line 562
    :cond_0
    :goto_0
    return-void

    #@b
    .line 559
    :cond_1
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@d
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->remove(I)Ljava/lang/Object;

    #@10
    .line 561
    if-eqz p2, :cond_0

    #@12
    const/4 v0, 0x1

    #@13
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@16
    goto :goto_0
.end method

.method private setHeaderInternal(ILjava/lang/CharSequence;ILandroid/graphics/drawable/Drawable;Landroid/view/View;)V
    .locals 3
    .param p1, "titleRes"    # I
    .param p2, "title"    # Ljava/lang/CharSequence;
    .param p3, "iconRes"    # I
    .param p4, "icon"    # Landroid/graphics/drawable/Drawable;
    .param p5, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 1175
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getResources()Landroid/content/res/Resources;

    #@4
    move-result-object v0

    #@5
    .line 1177
    .local v0, "r":Landroid/content/res/Resources;
    if-eqz p5, :cond_0

    #@7
    .line 1178
    iput-object p5, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderView:Landroid/view/View;

    #@9
    .line 1181
    iput-object v2, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderTitle:Ljava/lang/CharSequence;

    #@b
    .line 1182
    iput-object v2, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderIcon:Landroid/graphics/drawable/Drawable;

    #@d
    .line 1201
    :goto_0
    const/4 v1, 0x0

    #@e
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@11
    .line 1202
    return-void

    #@12
    .line 1184
    :cond_0
    if-lez p1, :cond_3

    #@14
    .line 1185
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getText(I)Ljava/lang/CharSequence;

    #@17
    move-result-object v1

    #@18
    iput-object v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderTitle:Ljava/lang/CharSequence;

    #@1a
    .line 1190
    :cond_1
    :goto_1
    if-lez p3, :cond_4

    #@1c
    .line 1191
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getContext()Landroid/content/Context;

    #@1f
    move-result-object v1

    #@20
    invoke-static {v1, p3}, Landroid/support/v4/content/ContextCompat;->getDrawable(Landroid/content/Context;I)Landroid/graphics/drawable/Drawable;

    #@23
    move-result-object v1

    #@24
    iput-object v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderIcon:Landroid/graphics/drawable/Drawable;

    #@26
    .line 1197
    :cond_2
    :goto_2
    iput-object v2, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderView:Landroid/view/View;

    #@28
    goto :goto_0

    #@29
    .line 1186
    :cond_3
    if-eqz p2, :cond_1

    #@2b
    .line 1187
    iput-object p2, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderTitle:Ljava/lang/CharSequence;

    #@2d
    goto :goto_1

    #@2e
    .line 1192
    :cond_4
    if-eqz p4, :cond_2

    #@30
    .line 1193
    iput-object p4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderIcon:Landroid/graphics/drawable/Drawable;

    #@32
    goto :goto_2
.end method

.method private setShortcutsVisibleInner(Z)V
    .locals 3
    .param p1, "shortcutsVisible"    # Z

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 781
    if-eqz p1, :cond_0

    #@3
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mResources:Landroid/content/res/Resources;

    #@5
    invoke-virtual {v1}, Landroid/content/res/Resources;->getConfiguration()Landroid/content/res/Configuration;

    #@8
    move-result-object v1

    #@9
    iget v1, v1, Landroid/content/res/Configuration;->keyboard:I

    #@b
    if-eq v1, v0, :cond_0

    #@d
    iget-object v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mResources:Landroid/content/res/Resources;

    #@f
    sget v2, Landroid/support/v7/appcompat/R$bool;->abc_config_showMenuShortcutsWhenKeyboardPresent:I

    #@11
    invoke-virtual {v1, v2}, Landroid/content/res/Resources;->getBoolean(I)Z

    #@14
    move-result v1

    #@15
    if-eqz v1, :cond_0

    #@17
    :goto_0
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mShortcutsVisible:Z

    #@19
    .line 784
    return-void

    #@1a
    .line 781
    :cond_0
    const/4 v0, 0x0

    #@1b
    goto :goto_0
.end method


# virtual methods
.method public add(I)Landroid/view/MenuItem;
    .locals 2
    .param p1, "titleRes"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 457
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mResources:Landroid/content/res/Resources;

    #@3
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@6
    move-result-object v0

    #@7
    invoke-direct {p0, v1, v1, v1, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addInternal(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@a
    move-result-object v0

    #@b
    return-object v0
.end method

.method public add(IIII)Landroid/view/MenuItem;
    .locals 1
    .param p1, "group"    # I
    .param p2, "id"    # I
    .param p3, "categoryOrder"    # I
    .param p4, "title"    # I

    #@0
    .prologue
    .line 467
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mResources:Landroid/content/res/Resources;

    #@2
    invoke-virtual {v0, p4}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    invoke-direct {p0, p1, p2, p3, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addInternal(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@9
    move-result-object v0

    #@a
    return-object v0
.end method

.method public add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "group"    # I
    .param p2, "id"    # I
    .param p3, "categoryOrder"    # I
    .param p4, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 462
    invoke-direct {p0, p1, p2, p3, p4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addInternal(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public add(Ljava/lang/CharSequence;)Landroid/view/MenuItem;
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 452
    invoke-direct {p0, v0, v0, v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addInternal(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@4
    move-result-object v0

    #@5
    return-object v0
.end method

.method public addIntentOptions(IIILandroid/content/ComponentName;[Landroid/content/Intent;Landroid/content/Intent;I[Landroid/view/MenuItem;)I
    .locals 13
    .param p1, "group"    # I
    .param p2, "id"    # I
    .param p3, "categoryOrder"    # I
    .param p4, "caller"    # Landroid/content/ComponentName;
    .param p5, "specifics"    # [Landroid/content/Intent;
    .param p6, "intent"    # Landroid/content/Intent;
    .param p7, "flags"    # I
    .param p8, "outSpecificItems"    # [Landroid/view/MenuItem;

    #@0
    .prologue
    .line 497
    iget-object v10, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {v10}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@5
    move-result-object v7

    #@6
    .line 498
    .local v7, "pm":Landroid/content/pm/PackageManager;
    const/4 v10, 0x0

    #@7
    move-object/from16 v0, p4

    #@9
    move-object/from16 v1, p5

    #@b
    move-object/from16 v2, p6

    #@d
    invoke-virtual {v7, v0, v1, v2, v10}, Landroid/content/pm/PackageManager;->queryIntentActivityOptions(Landroid/content/ComponentName;[Landroid/content/Intent;Landroid/content/Intent;I)Ljava/util/List;

    #@10
    move-result-object v6

    #@11
    .line 500
    .local v6, "lri":Ljava/util/List;, "Ljava/util/List<Landroid/content/pm/ResolveInfo;>;"
    if-eqz v6, :cond_2

    #@13
    invoke-interface {v6}, Ljava/util/List;->size()I

    #@16
    move-result v3

    #@17
    .line 502
    .local v3, "N":I
    :goto_0
    and-int/lit8 v10, p7, 0x1

    #@19
    if-nez v10, :cond_0

    #@1b
    .line 503
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->removeGroup(I)V

    #@1e
    .line 506
    :cond_0
    const/4 v4, 0x0

    #@1f
    .local v4, "i":I
    :goto_1
    if-ge v4, v3, :cond_4

    #@21
    .line 507
    invoke-interface {v6, v4}, Ljava/util/List;->get(I)Ljava/lang/Object;

    #@24
    move-result-object v8

    #@25
    check-cast v8, Landroid/content/pm/ResolveInfo;

    #@27
    .line 508
    .local v8, "ri":Landroid/content/pm/ResolveInfo;
    new-instance v9, Landroid/content/Intent;

    #@29
    iget v10, v8, Landroid/content/pm/ResolveInfo;->specificIndex:I

    #@2b
    if-gez v10, :cond_3

    #@2d
    move-object/from16 v10, p6

    #@2f
    :goto_2
    invoke-direct {v9, v10}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    #@32
    .line 510
    .local v9, "rintent":Landroid/content/Intent;
    new-instance v10, Landroid/content/ComponentName;

    #@34
    iget-object v11, v8, Landroid/content/pm/ResolveInfo;->activityInfo:Landroid/content/pm/ActivityInfo;

    #@36
    iget-object v11, v11, Landroid/content/pm/ActivityInfo;->applicationInfo:Landroid/content/pm/ApplicationInfo;

    #@38
    iget-object v11, v11, Landroid/content/pm/ApplicationInfo;->packageName:Ljava/lang/String;

    #@3a
    iget-object v12, v8, Landroid/content/pm/ResolveInfo;->activityInfo:Landroid/content/pm/ActivityInfo;

    #@3c
    iget-object v12, v12, Landroid/content/pm/ActivityInfo;->name:Ljava/lang/String;

    #@3e
    invoke-direct {v10, v11, v12}, Landroid/content/ComponentName;-><init>(Ljava/lang/String;Ljava/lang/String;)V

    #@41
    invoke-virtual {v9, v10}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@44
    .line 513
    invoke-virtual {v8, v7}, Landroid/content/pm/ResolveInfo;->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;

    #@47
    move-result-object v10

    #@48
    move/from16 v0, p3

    #@4a
    invoke-virtual {p0, p1, p2, v0, v10}, Landroid/support/v7/internal/view/menu/MenuBuilder;->add(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@4d
    move-result-object v10

    #@4e
    invoke-virtual {v8, v7}, Landroid/content/pm/ResolveInfo;->loadIcon(Landroid/content/pm/PackageManager;)Landroid/graphics/drawable/Drawable;

    #@51
    move-result-object v11

    #@52
    invoke-interface {v10, v11}, Landroid/view/MenuItem;->setIcon(Landroid/graphics/drawable/Drawable;)Landroid/view/MenuItem;

    #@55
    move-result-object v10

    #@56
    invoke-interface {v10, v9}, Landroid/view/MenuItem;->setIntent(Landroid/content/Intent;)Landroid/view/MenuItem;

    #@59
    move-result-object v5

    #@5a
    .line 516
    .local v5, "item":Landroid/view/MenuItem;
    if-eqz p8, :cond_1

    #@5c
    iget v10, v8, Landroid/content/pm/ResolveInfo;->specificIndex:I

    #@5e
    if-ltz v10, :cond_1

    #@60
    .line 517
    iget v10, v8, Landroid/content/pm/ResolveInfo;->specificIndex:I

    #@62
    aput-object v5, p8, v10

    #@64
    .line 506
    :cond_1
    add-int/lit8 v4, v4, 0x1

    #@66
    goto :goto_1

    #@67
    .line 500
    .end local v3    # "N":I
    .end local v4    # "i":I
    .end local v5    # "item":Landroid/view/MenuItem;
    .end local v8    # "ri":Landroid/content/pm/ResolveInfo;
    .end local v9    # "rintent":Landroid/content/Intent;
    :cond_2
    const/4 v3, 0x0

    #@68
    goto :goto_0

    #@69
    .line 508
    .restart local v3    # "N":I
    .restart local v4    # "i":I
    .restart local v8    # "ri":Landroid/content/pm/ResolveInfo;
    :cond_3
    iget v10, v8, Landroid/content/pm/ResolveInfo;->specificIndex:I

    #@6b
    aget-object v10, p5, v10

    #@6d
    goto :goto_2

    #@6e
    .line 521
    .end local v8    # "ri":Landroid/content/pm/ResolveInfo;
    :cond_4
    return v3
.end method

.method public addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;)V
    .locals 1
    .param p1, "presenter"    # Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@0
    .prologue
    .line 237
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mContext:Landroid/content/Context;

    #@2
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;Landroid/content/Context;)V

    #@5
    .line 238
    return-void
.end method

.method public addMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;Landroid/content/Context;)V
    .locals 2
    .param p1, "presenter"    # Landroid/support/v7/internal/view/menu/MenuPresenter;
    .param p2, "menuContext"    # Landroid/content/Context;

    #@0
    .prologue
    .line 250
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2
    new-instance v1, Ljava/lang/ref/WeakReference;

    #@4
    invoke-direct {v1, p1}, Ljava/lang/ref/WeakReference;-><init>(Ljava/lang/Object;)V

    #@7
    invoke-virtual {v0, v1}, Ljava/util/concurrent/CopyOnWriteArrayList;->add(Ljava/lang/Object;)Z

    #@a
    .line 251
    invoke-interface {p1, p2, p0}, Landroid/support/v7/internal/view/menu/MenuPresenter;->initForMenu(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@d
    .line 252
    const/4 v0, 0x1

    #@e
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsActionItemsStale:Z

    #@10
    .line 253
    return-void
.end method

.method public addSubMenu(I)Landroid/view/SubMenu;
    .locals 2
    .param p1, "titleRes"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 477
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mResources:Landroid/content/res/Resources;

    #@3
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@6
    move-result-object v0

    #@7
    invoke-virtual {p0, v1, v1, v1, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addSubMenu(IIILjava/lang/CharSequence;)Landroid/view/SubMenu;

    #@a
    move-result-object v0

    #@b
    return-object v0
.end method

.method public addSubMenu(IIII)Landroid/view/SubMenu;
    .locals 1
    .param p1, "group"    # I
    .param p2, "id"    # I
    .param p3, "categoryOrder"    # I
    .param p4, "title"    # I

    #@0
    .prologue
    .line 491
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mResources:Landroid/content/res/Resources;

    #@2
    invoke-virtual {v0, p4}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, p1, p2, p3, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addSubMenu(IIILjava/lang/CharSequence;)Landroid/view/SubMenu;

    #@9
    move-result-object v0

    #@a
    return-object v0
.end method

.method public addSubMenu(IIILjava/lang/CharSequence;)Landroid/view/SubMenu;
    .locals 3
    .param p1, "group"    # I
    .param p2, "id"    # I
    .param p3, "categoryOrder"    # I
    .param p4, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 482
    invoke-direct {p0, p1, p2, p3, p4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addInternal(IIILjava/lang/CharSequence;)Landroid/view/MenuItem;

    #@3
    move-result-object v0

    #@4
    check-cast v0, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@6
    .line 483
    .local v0, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    new-instance v1, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@8
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mContext:Landroid/content/Context;

    #@a
    invoke-direct {v1, v2, p0, v0}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;-><init>(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)V

    #@d
    .line 484
    .local v1, "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setSubMenu(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)V

    #@10
    .line 486
    return-object v1
.end method

.method public addSubMenu(Ljava/lang/CharSequence;)Landroid/view/SubMenu;
    .locals 1
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 472
    invoke-virtual {p0, v0, v0, v0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->addSubMenu(IIILjava/lang/CharSequence;)Landroid/view/SubMenu;

    #@4
    move-result-object v0

    #@5
    return-object v0
.end method

.method public changeMenuMode()V
    .locals 1

    #@0
    .prologue
    .line 809
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 810
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@6
    invoke-interface {v0, p0}, Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;->onMenuModeChange(Landroid/support/v7/internal/view/menu/MenuBuilder;)V

    #@9
    .line 812
    :cond_0
    return-void
.end method

.method public clear()V
    .locals 1

    #@0
    .prologue
    .line 579
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 580
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->collapseItemActionView(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z

    #@9
    .line 582
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v0}, Ljava/util/ArrayList;->clear()V

    #@e
    .line 584
    const/4 v0, 0x1

    #@f
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@12
    .line 585
    return-void
.end method

.method public clearAll()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v0, 0x0

    #@2
    .line 569
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPreventDispatchingItemsChanged:Z

    #@4
    .line 570
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->clear()V

    #@7
    .line 571
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->clearHeader()V

    #@a
    .line 572
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPreventDispatchingItemsChanged:Z

    #@c
    .line 573
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItemsChangedWhileDispatchPrevented:Z

    #@e
    .line 574
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@11
    .line 575
    return-void
.end method

.method public clearHeader()V
    .locals 1

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 1166
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderIcon:Landroid/graphics/drawable/Drawable;

    #@3
    .line 1167
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderTitle:Ljava/lang/CharSequence;

    #@5
    .line 1168
    iput-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderView:Landroid/view/View;

    #@7
    .line 1170
    const/4 v0, 0x0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@b
    .line 1171
    return-void
.end method

.method public close()V
    .locals 1

    #@0
    .prologue
    .line 1004
    const/4 v0, 0x1

    #@1
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@4
    .line 1005
    return-void
.end method

.method public final close(Z)V
    .locals 4
    .param p1, "allMenusAreClosing"    # Z

    #@0
    .prologue
    .line 988
    iget-boolean v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsClosing:Z

    #@2
    if-eqz v3, :cond_0

    #@4
    .line 1000
    :goto_0
    return-void

    #@5
    .line 990
    :cond_0
    const/4 v3, 0x1

    #@6
    iput-boolean v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsClosing:Z

    #@8
    .line 991
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@a
    invoke-virtual {v3}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@d
    move-result-object v0

    #@e
    .local v0, "i$":Ljava/util/Iterator;
    :goto_1
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@11
    move-result v3

    #@12
    if-eqz v3, :cond_2

    #@14
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@17
    move-result-object v2

    #@18
    check-cast v2, Ljava/lang/ref/WeakReference;

    #@1a
    .line 992
    .local v2, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v2}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@1d
    move-result-object v1

    #@1e
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@20
    .line 993
    .local v1, "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-nez v1, :cond_1

    #@22
    .line 994
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@24
    invoke-virtual {v3, v2}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@27
    goto :goto_1

    #@28
    .line 996
    :cond_1
    invoke-interface {v1, p0, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter;->onCloseMenu(Landroid/support/v7/internal/view/menu/MenuBuilder;Z)V

    #@2b
    goto :goto_1

    #@2c
    .line 999
    .end local v1    # "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    .end local v2    # "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    :cond_2
    const/4 v3, 0x0

    #@2d
    iput-boolean v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsClosing:Z

    #@2f
    goto :goto_0
.end method

.method public collapseItemActionView(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 5
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 1327
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->isEmpty()Z

    #@5
    move-result v4

    #@6
    if-nez v4, :cond_0

    #@8
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@a
    if-eq v4, p1, :cond_2

    #@c
    :cond_0
    const/4 v0, 0x0

    #@d
    .line 1345
    :cond_1
    :goto_0
    return v0

    #@e
    .line 1329
    :cond_2
    const/4 v0, 0x0

    #@f
    .line 1331
    .local v0, "collapsed":Z
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@12
    .line 1332
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@14
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@17
    move-result-object v1

    #@18
    .local v1, "i$":Ljava/util/Iterator;
    :cond_3
    :goto_1
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    #@1b
    move-result v4

    #@1c
    if-eqz v4, :cond_5

    #@1e
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@21
    move-result-object v3

    #@22
    check-cast v3, Ljava/lang/ref/WeakReference;

    #@24
    .line 1333
    .local v3, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v3}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@27
    move-result-object v2

    #@28
    check-cast v2, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@2a
    .line 1334
    .local v2, "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-nez v2, :cond_4

    #@2c
    .line 1335
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2e
    invoke-virtual {v4, v3}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@31
    goto :goto_1

    #@32
    .line 1336
    :cond_4
    invoke-interface {v2, p0, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter;->collapseItemActionView(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z

    #@35
    move-result v0

    #@36
    if-eqz v0, :cond_3

    #@38
    .line 1340
    .end local v2    # "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    .end local v3    # "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    :cond_5
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@3b
    .line 1342
    if-eqz v0, :cond_1

    #@3d
    .line 1343
    const/4 v4, 0x0

    #@3e
    iput-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@40
    goto :goto_0
.end method

.method dispatchMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z
    .locals 1
    .param p1, "menu"    # Landroid/support/v7/internal/view/menu/MenuBuilder;
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 802
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@6
    invoke-interface {v0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;->onMenuItemSelected(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/view/MenuItem;)Z

    #@9
    move-result v0

    #@a
    if-eqz v0, :cond_0

    #@c
    const/4 v0, 0x1

    #@d
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public expandItemActionView(Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z
    .locals 5
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    .line 1305
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->isEmpty()Z

    #@5
    move-result v4

    #@6
    if-eqz v4, :cond_1

    #@8
    const/4 v0, 0x0

    #@9
    .line 1323
    :cond_0
    :goto_0
    return v0

    #@a
    .line 1307
    :cond_1
    const/4 v0, 0x0

    #@b
    .line 1309
    .local v0, "expanded":Z
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->stopDispatchingItemsChanged()V

    #@e
    .line 1310
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@10
    invoke-virtual {v4}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@13
    move-result-object v1

    #@14
    .local v1, "i$":Ljava/util/Iterator;
    :cond_2
    :goto_1
    invoke-interface {v1}, Ljava/util/Iterator;->hasNext()Z

    #@17
    move-result v4

    #@18
    if-eqz v4, :cond_4

    #@1a
    invoke-interface {v1}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@1d
    move-result-object v3

    #@1e
    check-cast v3, Ljava/lang/ref/WeakReference;

    #@20
    .line 1311
    .local v3, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v3}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@23
    move-result-object v2

    #@24
    check-cast v2, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@26
    .line 1312
    .local v2, "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-nez v2, :cond_3

    #@28
    .line 1313
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2a
    invoke-virtual {v4, v3}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@2d
    goto :goto_1

    #@2e
    .line 1314
    :cond_3
    invoke-interface {v2, p0, p1}, Landroid/support/v7/internal/view/menu/MenuPresenter;->expandItemActionView(Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)Z

    #@31
    move-result v0

    #@32
    if-eqz v0, :cond_2

    #@34
    .line 1318
    .end local v2    # "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    .end local v3    # "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    :cond_4
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->startDispatchingItemsChanged()V

    #@37
    .line 1320
    if-eqz v0, :cond_0

    #@39
    .line 1321
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@3b
    goto :goto_0
.end method

.method public findGroupIndex(I)I
    .locals 1
    .param p1, "group"    # I

    #@0
    .prologue
    .line 693
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, p1, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findGroupIndex(II)I

    #@4
    move-result v0

    #@5
    return v0
.end method

.method public findGroupIndex(II)I
    .locals 4
    .param p1, "group"    # I
    .param p2, "start"    # I

    #@0
    .prologue
    .line 697
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->size()I

    #@3
    move-result v2

    #@4
    .line 699
    .local v2, "size":I
    if-gez p2, :cond_0

    #@6
    .line 700
    const/4 p2, 0x0

    #@7
    .line 703
    :cond_0
    move v0, p2

    #@8
    .local v0, "i":I
    :goto_0
    if-ge v0, v2, :cond_2

    #@a
    .line 704
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@c
    invoke-virtual {v3, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@f
    move-result-object v1

    #@10
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@12
    .line 706
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@15
    move-result v3

    #@16
    if-ne v3, p1, :cond_1

    #@18
    .line 711
    .end local v0    # "i":I
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :goto_1
    return v0

    #@19
    .line 703
    .restart local v0    # "i":I
    .restart local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1
    add-int/lit8 v0, v0, 0x1

    #@1b
    goto :goto_0

    #@1c
    .line 711
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_2
    const/4 v0, -0x1

    #@1d
    goto :goto_1
.end method

.method public findItem(I)Landroid/view/MenuItem;
    .locals 5
    .param p1, "id"    # I

    #@0
    .prologue
    .line 662
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->size()I

    #@3
    move-result v3

    #@4
    .line 663
    .local v3, "size":I
    const/4 v0, 0x0

    #@5
    .local v0, "i":I
    :goto_0
    if-ge v0, v3, :cond_2

    #@7
    .line 664
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@9
    invoke-virtual {v4, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@c
    move-result-object v1

    #@d
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@f
    .line 665
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getItemId()I

    #@12
    move-result v4

    #@13
    if-ne v4, p1, :cond_0

    #@15
    .line 676
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :goto_1
    return-object v1

    #@16
    .line 667
    .restart local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_0
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->hasSubMenu()Z

    #@19
    move-result v4

    #@1a
    if-eqz v4, :cond_1

    #@1c
    .line 668
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getSubMenu()Landroid/view/SubMenu;

    #@1f
    move-result-object v4

    #@20
    invoke-interface {v4, p1}, Landroid/view/SubMenu;->findItem(I)Landroid/view/MenuItem;

    #@23
    move-result-object v2

    #@24
    .line 670
    .local v2, "possibleItem":Landroid/view/MenuItem;
    if-eqz v2, :cond_1

    #@26
    move-object v1, v2

    #@27
    .line 671
    goto :goto_1

    #@28
    .line 663
    .end local v2    # "possibleItem":Landroid/view/MenuItem;
    :cond_1
    add-int/lit8 v0, v0, 0x1

    #@2a
    goto :goto_0

    #@2b
    .line 676
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_2
    const/4 v1, 0x0

    #@2c
    goto :goto_1
.end method

.method public findItemIndex(I)I
    .locals 4
    .param p1, "id"    # I

    #@0
    .prologue
    .line 680
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->size()I

    #@3
    move-result v2

    #@4
    .line 682
    .local v2, "size":I
    const/4 v0, 0x0

    #@5
    .local v0, "i":I
    :goto_0
    if-ge v0, v2, :cond_1

    #@7
    .line 683
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@9
    invoke-virtual {v3, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@c
    move-result-object v1

    #@d
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@f
    .line 684
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getItemId()I

    #@12
    move-result v3

    #@13
    if-ne v3, p1, :cond_0

    #@15
    .line 689
    .end local v0    # "i":I
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :goto_1
    return v0

    #@16
    .line 682
    .restart local v0    # "i":I
    .restart local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_0
    add-int/lit8 v0, v0, 0x1

    #@18
    goto :goto_0

    #@19
    .line 689
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1
    const/4 v0, -0x1

    #@1a
    goto :goto_1
.end method

.method findItemWithShortcutForKey(ILandroid/view/KeyEvent;)Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .locals 12
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/4 v8, 0x0

    #@1
    const/4 v11, 0x0

    #@2
    .line 894
    iget-object v2, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mTempShortcutItemList:Ljava/util/ArrayList;

    #@4
    .line 895
    .local v2, "items":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    invoke-virtual {v2}, Ljava/util/ArrayList;->clear()V

    #@7
    .line 896
    invoke-virtual {p0, v2, p1, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findItemsWithShortcutForKey(Ljava/util/List;ILandroid/view/KeyEvent;)V

    #@a
    .line 898
    invoke-virtual {v2}, Ljava/util/ArrayList;->isEmpty()Z

    #@d
    move-result v9

    #@e
    if-eqz v9, :cond_1

    #@10
    .line 929
    :cond_0
    :goto_0
    return-object v8

    #@11
    .line 902
    :cond_1
    invoke-virtual {p2}, Landroid/view/KeyEvent;->getMetaState()I

    #@14
    move-result v3

    #@15
    .line 903
    .local v3, "metaState":I
    new-instance v4, Landroid/view/KeyCharacterMap$KeyData;

    #@17
    invoke-direct {v4}, Landroid/view/KeyCharacterMap$KeyData;-><init>()V

    #@1a
    .line 905
    .local v4, "possibleChars":Landroid/view/KeyCharacterMap$KeyData;
    invoke-virtual {p2, v4}, Landroid/view/KeyEvent;->getKeyData(Landroid/view/KeyCharacterMap$KeyData;)Z

    #@1d
    .line 908
    invoke-virtual {v2}, Ljava/util/ArrayList;->size()I

    #@20
    move-result v7

    #@21
    .line 909
    .local v7, "size":I
    const/4 v9, 0x1

    #@22
    if-ne v7, v9, :cond_2

    #@24
    .line 910
    invoke-virtual {v2, v11}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@27
    move-result-object v8

    #@28
    check-cast v8, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2a
    goto :goto_0

    #@2b
    .line 913
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->isQwertyMode()Z

    #@2e
    move-result v5

    #@2f
    .line 916
    .local v5, "qwerty":Z
    const/4 v0, 0x0

    #@30
    .local v0, "i":I
    :goto_1
    if-ge v0, v7, :cond_0

    #@32
    .line 917
    invoke-virtual {v2, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@35
    move-result-object v1

    #@36
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@38
    .line 918
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    if-eqz v5, :cond_6

    #@3a
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getAlphabeticShortcut()C

    #@3d
    move-result v6

    #@3e
    .line 920
    .local v6, "shortcutChar":C
    :goto_2
    iget-object v9, v4, Landroid/view/KeyCharacterMap$KeyData;->meta:[C

    #@40
    aget-char v9, v9, v11

    #@42
    if-ne v6, v9, :cond_3

    #@44
    and-int/lit8 v9, v3, 0x2

    #@46
    if-eqz v9, :cond_5

    #@48
    :cond_3
    iget-object v9, v4, Landroid/view/KeyCharacterMap$KeyData;->meta:[C

    #@4a
    const/4 v10, 0x2

    #@4b
    aget-char v9, v9, v10

    #@4d
    if-ne v6, v9, :cond_4

    #@4f
    and-int/lit8 v9, v3, 0x2

    #@51
    if-nez v9, :cond_5

    #@53
    :cond_4
    if-eqz v5, :cond_7

    #@55
    const/16 v9, 0x8

    #@57
    if-ne v6, v9, :cond_7

    #@59
    const/16 v9, 0x43

    #@5b
    if-ne p1, v9, :cond_7

    #@5d
    :cond_5
    move-object v8, v1

    #@5e
    .line 926
    goto :goto_0

    #@5f
    .line 918
    .end local v6    # "shortcutChar":C
    :cond_6
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getNumericShortcut()C

    #@62
    move-result v6

    #@63
    goto :goto_2

    #@64
    .line 916
    .restart local v6    # "shortcutChar":C
    :cond_7
    add-int/lit8 v0, v0, 0x1

    #@66
    goto :goto_1
.end method

.method findItemsWithShortcutForKey(Ljava/util/List;ILandroid/view/KeyEvent;)V
    .locals 11
    .param p2, "keyCode"    # I
    .param p3, "event"    # Landroid/view/KeyEvent;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/util/List",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;I",
            "Landroid/view/KeyEvent;",
            ")V"
        }
    .end annotation

    #@0
    .prologue
    .local p1, "items":Ljava/util/List;, "Ljava/util/List<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    const/16 v10, 0x43

    #@2
    .line 850
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->isQwertyMode()Z

    #@5
    move-result v6

    #@6
    .line 851
    .local v6, "qwerty":Z
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getMetaState()I

    #@9
    move-result v4

    #@a
    .line 852
    .local v4, "metaState":I
    new-instance v5, Landroid/view/KeyCharacterMap$KeyData;

    #@c
    invoke-direct {v5}, Landroid/view/KeyCharacterMap$KeyData;-><init>()V

    #@f
    .line 854
    .local v5, "possibleChars":Landroid/view/KeyCharacterMap$KeyData;
    invoke-virtual {p3, v5}, Landroid/view/KeyEvent;->getKeyData(Landroid/view/KeyCharacterMap$KeyData;)Z

    #@12
    move-result v2

    #@13
    .line 856
    .local v2, "isKeyCodeMapped":Z
    if-nez v2, :cond_1

    #@15
    if-eq p2, v10, :cond_1

    #@17
    .line 878
    :cond_0
    return-void

    #@18
    .line 861
    :cond_1
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@1a
    invoke-virtual {v8}, Ljava/util/ArrayList;->size()I

    #@1d
    move-result v0

    #@1e
    .line 862
    .local v0, "N":I
    const/4 v1, 0x0

    #@1f
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@21
    .line 863
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@23
    invoke-virtual {v8, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@26
    move-result-object v3

    #@27
    check-cast v3, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@29
    .line 864
    .local v3, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->hasSubMenu()Z

    #@2c
    move-result v8

    #@2d
    if-eqz v8, :cond_2

    #@2f
    .line 865
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getSubMenu()Landroid/view/SubMenu;

    #@32
    move-result-object v8

    #@33
    check-cast v8, Landroid/support/v7/internal/view/menu/MenuBuilder;

    #@35
    invoke-virtual {v8, p1, p2, p3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findItemsWithShortcutForKey(Ljava/util/List;ILandroid/view/KeyEvent;)V

    #@38
    .line 867
    :cond_2
    if-eqz v6, :cond_5

    #@3a
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getAlphabeticShortcut()C

    #@3d
    move-result v7

    #@3e
    .line 868
    .local v7, "shortcutChar":C
    :goto_1
    and-int/lit8 v8, v4, 0x5

    #@40
    if-nez v8, :cond_4

    #@42
    if-eqz v7, :cond_4

    #@44
    iget-object v8, v5, Landroid/view/KeyCharacterMap$KeyData;->meta:[C

    #@46
    const/4 v9, 0x0

    #@47
    aget-char v8, v8, v9

    #@49
    if-eq v7, v8, :cond_3

    #@4b
    iget-object v8, v5, Landroid/view/KeyCharacterMap$KeyData;->meta:[C

    #@4d
    const/4 v9, 0x2

    #@4e
    aget-char v8, v8, v9

    #@50
    if-eq v7, v8, :cond_3

    #@52
    if-eqz v6, :cond_4

    #@54
    const/16 v8, 0x8

    #@56
    if-ne v7, v8, :cond_4

    #@58
    if-ne p2, v10, :cond_4

    #@5a
    :cond_3
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isEnabled()Z

    #@5d
    move-result v8

    #@5e
    if-eqz v8, :cond_4

    #@60
    .line 875
    invoke-interface {p1, v3}, Ljava/util/List;->add(Ljava/lang/Object;)Z

    #@63
    .line 862
    :cond_4
    add-int/lit8 v1, v1, 0x1

    #@65
    goto :goto_0

    #@66
    .line 867
    .end local v7    # "shortcutChar":C
    :cond_5
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getNumericShortcut()C

    #@69
    move-result v7

    #@6a
    goto :goto_1
.end method

.method public flagActionItems()V
    .locals 10

    #@0
    .prologue
    .line 1116
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getVisibleItems()Ljava/util/ArrayList;

    #@3
    move-result-object v7

    #@4
    .line 1118
    .local v7, "visibleItems":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Landroid/support/v7/internal/view/menu/MenuItemImpl;>;"
    iget-boolean v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsActionItemsStale:Z

    #@6
    if-nez v8, :cond_0

    #@8
    .line 1153
    :goto_0
    return-void

    #@9
    .line 1123
    :cond_0
    const/4 v0, 0x0

    #@a
    .line 1124
    .local v0, "flagged":Z
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@c
    invoke-virtual {v8}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@f
    move-result-object v2

    #@10
    .local v2, "i$":Ljava/util/Iterator;
    :goto_1
    invoke-interface {v2}, Ljava/util/Iterator;->hasNext()Z

    #@13
    move-result v8

    #@14
    if-eqz v8, :cond_2

    #@16
    invoke-interface {v2}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@19
    move-result-object v6

    #@1a
    check-cast v6, Ljava/lang/ref/WeakReference;

    #@1c
    .line 1125
    .local v6, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v6}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@1f
    move-result-object v5

    #@20
    check-cast v5, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@22
    .line 1126
    .local v5, "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-nez v5, :cond_1

    #@24
    .line 1127
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@26
    invoke-virtual {v8, v6}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@29
    goto :goto_1

    #@2a
    .line 1129
    :cond_1
    invoke-interface {v5}, Landroid/support/v7/internal/view/menu/MenuPresenter;->flagActionItems()Z

    #@2d
    move-result v8

    #@2e
    or-int/2addr v0, v8

    #@2f
    goto :goto_1

    #@30
    .line 1133
    .end local v5    # "presenter":Landroid/support/v7/internal/view/menu/MenuPresenter;
    .end local v6    # "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    :cond_2
    if-eqz v0, :cond_4

    #@32
    .line 1134
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mActionItems:Ljava/util/ArrayList;

    #@34
    invoke-virtual {v8}, Ljava/util/ArrayList;->clear()V

    #@37
    .line 1135
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mNonActionItems:Ljava/util/ArrayList;

    #@39
    invoke-virtual {v8}, Ljava/util/ArrayList;->clear()V

    #@3c
    .line 1136
    invoke-virtual {v7}, Ljava/util/ArrayList;->size()I

    #@3f
    move-result v4

    #@40
    .line 1137
    .local v4, "itemsSize":I
    const/4 v1, 0x0

    #@41
    .local v1, "i":I
    :goto_2
    if-ge v1, v4, :cond_5

    #@43
    .line 1138
    invoke-virtual {v7, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@46
    move-result-object v3

    #@47
    check-cast v3, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@49
    .line 1139
    .local v3, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isActionButton()Z

    #@4c
    move-result v8

    #@4d
    if-eqz v8, :cond_3

    #@4f
    .line 1140
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mActionItems:Ljava/util/ArrayList;

    #@51
    invoke-virtual {v8, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@54
    .line 1137
    :goto_3
    add-int/lit8 v1, v1, 0x1

    #@56
    goto :goto_2

    #@57
    .line 1142
    :cond_3
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mNonActionItems:Ljava/util/ArrayList;

    #@59
    invoke-virtual {v8, v3}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@5c
    goto :goto_3

    #@5d
    .line 1148
    .end local v1    # "i":I
    .end local v3    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .end local v4    # "itemsSize":I
    :cond_4
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mActionItems:Ljava/util/ArrayList;

    #@5f
    invoke-virtual {v8}, Ljava/util/ArrayList;->clear()V

    #@62
    .line 1149
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mNonActionItems:Ljava/util/ArrayList;

    #@64
    invoke-virtual {v8}, Ljava/util/ArrayList;->clear()V

    #@67
    .line 1150
    iget-object v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mNonActionItems:Ljava/util/ArrayList;

    #@69
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getVisibleItems()Ljava/util/ArrayList;

    #@6c
    move-result-object v9

    #@6d
    invoke-virtual {v8, v9}, Ljava/util/ArrayList;->addAll(Ljava/util/Collection;)Z

    #@70
    .line 1152
    :cond_5
    const/4 v8, 0x0

    #@71
    iput-boolean v8, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsActionItemsStale:Z

    #@73
    goto :goto_0
.end method

.method public getActionItems()Ljava/util/ArrayList;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 1156
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->flagActionItems()V

    #@3
    .line 1157
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mActionItems:Ljava/util/ArrayList;

    #@5
    return-object v0
.end method

.method protected getActionViewStatesKey()Ljava/lang/String;
    .locals 1

    #@0
    .prologue
    .line 417
    const-string v0, "android:menu:actionviewstates"

    #@2
    return-object v0
.end method

.method public getContext()Landroid/content/Context;
    .locals 1

    #@0
    .prologue
    .line 798
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mContext:Landroid/content/Context;

    #@2
    return-object v0
.end method

.method public getExpandedItem()Landroid/support/v7/internal/view/menu/MenuItemImpl;
    .locals 1

    #@0
    .prologue
    .line 1349
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mExpandedItem:Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@2
    return-object v0
.end method

.method public getHeaderIcon()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 1269
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderIcon:Landroid/graphics/drawable/Drawable;

    #@2
    return-object v0
.end method

.method public getHeaderTitle()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 1265
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderTitle:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getHeaderView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 1273
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mHeaderView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method public getItem(I)Landroid/view/MenuItem;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 721
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Landroid/view/MenuItem;

    #@8
    return-object v0
.end method

.method public getNonActionItems()Ljava/util/ArrayList;
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 1161
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->flagActionItems()V

    #@3
    .line 1162
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mNonActionItems:Ljava/util/ArrayList;

    #@5
    return-object v0
.end method

.method getOptionalIconsVisible()Z
    .locals 1

    #@0
    .prologue
    .line 1301
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mOptionalIconsVisible:Z

    #@2
    return v0
.end method

.method getResources()Landroid/content/res/Resources;
    .locals 1

    #@0
    .prologue
    .line 794
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mResources:Landroid/content/res/Resources;

    #@2
    return-object v0
.end method

.method public getRootMenu()Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 0

    #@0
    .prologue
    .line 1282
    return-object p0
.end method

.method public getVisibleItems()Ljava/util/ArrayList;
    .locals 4
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()",
            "Ljava/util/ArrayList",
            "<",
            "Landroid/support/v7/internal/view/menu/MenuItemImpl;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 1071
    iget-boolean v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsVisibleItemsStale:Z

    #@2
    if-nez v3, :cond_0

    #@4
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mVisibleItems:Ljava/util/ArrayList;

    #@6
    .line 1086
    :goto_0
    return-object v3

    #@7
    .line 1074
    :cond_0
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mVisibleItems:Ljava/util/ArrayList;

    #@9
    invoke-virtual {v3}, Ljava/util/ArrayList;->clear()V

    #@c
    .line 1076
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@e
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@11
    move-result v2

    #@12
    .line 1078
    .local v2, "itemsSize":I
    const/4 v0, 0x0

    #@13
    .local v0, "i":I
    :goto_1
    if-ge v0, v2, :cond_2

    #@15
    .line 1079
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@17
    invoke-virtual {v3, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@1a
    move-result-object v1

    #@1b
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@1d
    .line 1080
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isVisible()Z

    #@20
    move-result v3

    #@21
    if-eqz v3, :cond_1

    #@23
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mVisibleItems:Ljava/util/ArrayList;

    #@25
    invoke-virtual {v3, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@28
    .line 1078
    :cond_1
    add-int/lit8 v0, v0, 0x1

    #@2a
    goto :goto_1

    #@2b
    .line 1083
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_2
    const/4 v3, 0x0

    #@2c
    iput-boolean v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsVisibleItemsStale:Z

    #@2e
    .line 1084
    const/4 v3, 0x1

    #@2f
    iput-boolean v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsActionItemsStale:Z

    #@31
    .line 1086
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mVisibleItems:Ljava/util/ArrayList;

    #@33
    goto :goto_0
.end method

.method public hasVisibleItems()Z
    .locals 4

    #@0
    .prologue
    .line 648
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->size()I

    #@3
    move-result v2

    #@4
    .line 650
    .local v2, "size":I
    const/4 v0, 0x0

    #@5
    .local v0, "i":I
    :goto_0
    if-ge v0, v2, :cond_1

    #@7
    .line 651
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@9
    invoke-virtual {v3, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@c
    move-result-object v1

    #@d
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@f
    .line 652
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isVisible()Z

    #@12
    move-result v3

    #@13
    if-eqz v3, :cond_0

    #@15
    .line 653
    const/4 v3, 0x1

    #@16
    .line 657
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :goto_1
    return v3

    #@17
    .line 650
    .restart local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_0
    add-int/lit8 v0, v0, 0x1

    #@19
    goto :goto_0

    #@1a
    .line 657
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1
    const/4 v3, 0x0

    #@1b
    goto :goto_1
.end method

.method isQwertyMode()Z
    .locals 1

    #@0
    .prologue
    .line 761
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mQwertyMode:Z

    #@2
    return v0
.end method

.method public isShortcutKey(ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 726
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findItemWithShortcutForKey(ILandroid/view/KeyEvent;)Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@3
    move-result-object v0

    #@4
    if-eqz v0, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    return v0

    #@8
    :cond_0
    const/4 v0, 0x0

    #@9
    goto :goto_0
.end method

.method public isShortcutsVisible()Z
    .locals 1

    #@0
    .prologue
    .line 790
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mShortcutsVisible:Z

    #@2
    return v0
.end method

.method onItemActionRequestChanged(Landroid/support/v7/internal/view/menu/MenuItemImpl;)V
    .locals 1
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 1066
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsActionItemsStale:Z

    #@3
    .line 1067
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@6
    .line 1068
    return-void
.end method

.method onItemVisibleChanged(Landroid/support/v7/internal/view/menu/MenuItemImpl;)V
    .locals 1
    .param p1, "item"    # Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    .line 1055
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsVisibleItemsStale:Z

    #@3
    .line 1056
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@6
    .line 1057
    return-void
.end method

.method public onItemsChanged(Z)V
    .locals 2
    .param p1, "structureChanged"    # Z

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 1015
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPreventDispatchingItemsChanged:Z

    #@3
    if-nez v0, :cond_1

    #@5
    .line 1016
    if-eqz p1, :cond_0

    #@7
    .line 1017
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsVisibleItemsStale:Z

    #@9
    .line 1018
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mIsActionItemsStale:Z

    #@b
    .line 1021
    :cond_0
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->dispatchPresenterUpdate(Z)V

    #@e
    .line 1025
    :goto_0
    return-void

    #@f
    .line 1023
    :cond_1
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItemsChangedWhileDispatchPrevented:Z

    #@11
    goto :goto_0
.end method

.method public performIdentifierAction(II)Z
    .locals 1
    .param p1, "id"    # I
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 935
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findItem(I)Landroid/view/MenuItem;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {p0, v0, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->performItemAction(Landroid/view/MenuItem;I)Z

    #@7
    move-result v0

    #@8
    return v0
.end method

.method public performItemAction(Landroid/view/MenuItem;I)Z
    .locals 1
    .param p1, "item"    # Landroid/view/MenuItem;
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 939
    const/4 v0, 0x0

    #@1
    invoke-virtual {p0, p1, v0, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->performItemAction(Landroid/view/MenuItem;Landroid/support/v7/internal/view/menu/MenuPresenter;I)Z

    #@4
    move-result v0

    #@5
    return v0
.end method

.method public performItemAction(Landroid/view/MenuItem;Landroid/support/v7/internal/view/menu/MenuPresenter;I)Z
    .locals 8
    .param p1, "item"    # Landroid/view/MenuItem;
    .param p2, "preferredPresenter"    # Landroid/support/v7/internal/view/menu/MenuPresenter;
    .param p3, "flags"    # I

    #@0
    .prologue
    const/4 v6, 0x0

    #@1
    const/4 v5, 0x1

    #@2
    .line 943
    move-object v1, p1

    #@3
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@5
    .line 945
    .local v1, "itemImpl":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    if-eqz v1, :cond_0

    #@7
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isEnabled()Z

    #@a
    move-result v7

    #@b
    if-nez v7, :cond_2

    #@d
    :cond_0
    move v0, v6

    #@e
    .line 975
    :cond_1
    :goto_0
    return v0

    #@f
    .line 949
    :cond_2
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->invoke()Z

    #@12
    move-result v0

    #@13
    .line 951
    .local v0, "invoked":Z
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getSupportActionProvider()Landroid/support/v4/view/ActionProvider;

    #@16
    move-result-object v2

    #@17
    .line 952
    .local v2, "provider":Landroid/support/v4/view/ActionProvider;
    if-eqz v2, :cond_3

    #@19
    invoke-virtual {v2}, Landroid/support/v4/view/ActionProvider;->hasSubMenu()Z

    #@1c
    move-result v7

    #@1d
    if-eqz v7, :cond_3

    #@1f
    move v3, v5

    #@20
    .line 953
    .local v3, "providerHasSubMenu":Z
    :goto_1
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->hasCollapsibleActionView()Z

    #@23
    move-result v7

    #@24
    if-eqz v7, :cond_4

    #@26
    .line 954
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->expandActionView()Z

    #@29
    move-result v6

    #@2a
    or-int/2addr v0, v6

    #@2b
    .line 955
    if-eqz v0, :cond_1

    #@2d
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@30
    goto :goto_0

    #@31
    .end local v3    # "providerHasSubMenu":Z
    :cond_3
    move v3, v6

    #@32
    .line 952
    goto :goto_1

    #@33
    .line 956
    .restart local v3    # "providerHasSubMenu":Z
    :cond_4
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->hasSubMenu()Z

    #@36
    move-result v7

    #@37
    if-nez v7, :cond_5

    #@39
    if-eqz v3, :cond_8

    #@3b
    .line 957
    :cond_5
    invoke-virtual {p0, v6}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@3e
    .line 959
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->hasSubMenu()Z

    #@41
    move-result v6

    #@42
    if-nez v6, :cond_6

    #@44
    .line 960
    new-instance v6, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@46
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getContext()Landroid/content/Context;

    #@49
    move-result-object v7

    #@4a
    invoke-direct {v6, v7, p0, v1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;-><init>(Landroid/content/Context;Landroid/support/v7/internal/view/menu/MenuBuilder;Landroid/support/v7/internal/view/menu/MenuItemImpl;)V

    #@4d
    invoke-virtual {v1, v6}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setSubMenu(Landroid/support/v7/internal/view/menu/SubMenuBuilder;)V

    #@50
    .line 963
    :cond_6
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getSubMenu()Landroid/view/SubMenu;

    #@53
    move-result-object v4

    #@54
    check-cast v4, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@56
    .line 964
    .local v4, "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    if-eqz v3, :cond_7

    #@58
    .line 965
    invoke-virtual {v2, v4}, Landroid/support/v4/view/ActionProvider;->onPrepareSubMenu(Landroid/view/SubMenu;)V

    #@5b
    .line 967
    :cond_7
    invoke-direct {p0, v4, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->dispatchSubMenuSelected(Landroid/support/v7/internal/view/menu/SubMenuBuilder;Landroid/support/v7/internal/view/menu/MenuPresenter;)Z

    #@5e
    move-result v6

    #@5f
    or-int/2addr v0, v6

    #@60
    .line 968
    if-nez v0, :cond_1

    #@62
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@65
    goto :goto_0

    #@66
    .line 970
    .end local v4    # "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    :cond_8
    and-int/lit8 v6, p3, 0x1

    #@68
    if-nez v6, :cond_1

    #@6a
    .line 971
    invoke-virtual {p0, v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@6d
    goto :goto_0
.end method

.method public performShortcut(ILandroid/view/KeyEvent;I)Z
    .locals 3
    .param p1, "keyCode"    # I
    .param p2, "event"    # Landroid/view/KeyEvent;
    .param p3, "flags"    # I

    #@0
    .prologue
    .line 827
    invoke-virtual {p0, p1, p2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findItemWithShortcutForKey(ILandroid/view/KeyEvent;)Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@3
    move-result-object v1

    #@4
    .line 829
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    const/4 v0, 0x0

    #@5
    .line 831
    .local v0, "handled":Z
    if-eqz v1, :cond_0

    #@7
    .line 832
    invoke-virtual {p0, v1, p3}, Landroid/support/v7/internal/view/menu/MenuBuilder;->performItemAction(Landroid/view/MenuItem;I)Z

    #@a
    move-result v0

    #@b
    .line 835
    :cond_0
    and-int/lit8 v2, p3, 0x2

    #@d
    if-eqz v2, :cond_1

    #@f
    .line 836
    const/4 v2, 0x1

    #@10
    invoke-virtual {p0, v2}, Landroid/support/v7/internal/view/menu/MenuBuilder;->close(Z)V

    #@13
    .line 839
    :cond_1
    return v0
.end method

.method public removeGroup(I)V
    .locals 5
    .param p1, "group"    # I

    #@0
    .prologue
    .line 531
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findGroupIndex(I)I

    #@3
    move-result v0

    #@4
    .line 533
    .local v0, "i":I
    if-ltz v0, :cond_1

    #@6
    .line 534
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@8
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@b
    move-result v4

    #@c
    sub-int v1, v4, v0

    #@e
    .line 535
    .local v1, "maxRemovable":I
    const/4 v2, 0x0

    #@f
    .local v2, "numRemoved":I
    move v3, v2

    #@10
    .line 536
    .end local v2    # "numRemoved":I
    .local v3, "numRemoved":I
    :goto_0
    add-int/lit8 v2, v3, 0x1

    #@12
    .end local v3    # "numRemoved":I
    .restart local v2    # "numRemoved":I
    if-ge v3, v1, :cond_0

    #@14
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@16
    invoke-virtual {v4, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@19
    move-result-object v4

    #@1a
    check-cast v4, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@1c
    invoke-virtual {v4}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@1f
    move-result v4

    #@20
    if-ne v4, p1, :cond_0

    #@22
    .line 538
    const/4 v4, 0x0

    #@23
    invoke-direct {p0, v0, v4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->removeItemAtInt(IZ)V

    #@26
    move v3, v2

    #@27
    .end local v2    # "numRemoved":I
    .restart local v3    # "numRemoved":I
    goto :goto_0

    #@28
    .line 542
    .end local v3    # "numRemoved":I
    .restart local v2    # "numRemoved":I
    :cond_0
    const/4 v4, 0x1

    #@29
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@2c
    .line 544
    .end local v1    # "maxRemovable":I
    .end local v2    # "numRemoved":I
    :cond_1
    return-void
.end method

.method public removeItem(I)V
    .locals 2
    .param p1, "id"    # I

    #@0
    .prologue
    .line 526
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findItemIndex(I)I

    #@3
    move-result v0

    #@4
    const/4 v1, 0x1

    #@5
    invoke-direct {p0, v0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->removeItemAtInt(IZ)V

    #@8
    .line 527
    return-void
.end method

.method public removeItemAt(I)V
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 565
    const/4 v0, 0x1

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->removeItemAtInt(IZ)V

    #@4
    .line 566
    return-void
.end method

.method public removeMenuPresenter(Landroid/support/v7/internal/view/menu/MenuPresenter;)V
    .locals 4
    .param p1, "presenter"    # Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@0
    .prologue
    .line 262
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@2
    invoke-virtual {v3}, Ljava/util/concurrent/CopyOnWriteArrayList;->iterator()Ljava/util/Iterator;

    #@5
    move-result-object v0

    #@6
    .local v0, "i$":Ljava/util/Iterator;
    :cond_0
    :goto_0
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z

    #@9
    move-result v3

    #@a
    if-eqz v3, :cond_2

    #@c
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;

    #@f
    move-result-object v2

    #@10
    check-cast v2, Ljava/lang/ref/WeakReference;

    #@12
    .line 263
    .local v2, "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    invoke-virtual {v2}, Ljava/lang/ref/WeakReference;->get()Ljava/lang/Object;

    #@15
    move-result-object v1

    #@16
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuPresenter;

    #@18
    .line 264
    .local v1, "item":Landroid/support/v7/internal/view/menu/MenuPresenter;
    if-eqz v1, :cond_1

    #@1a
    if-ne v1, p1, :cond_0

    #@1c
    .line 265
    :cond_1
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPresenters:Ljava/util/concurrent/CopyOnWriteArrayList;

    #@1e
    invoke-virtual {v3, v2}, Ljava/util/concurrent/CopyOnWriteArrayList;->remove(Ljava/lang/Object;)Z

    #@21
    goto :goto_0

    #@22
    .line 268
    .end local v1    # "item":Landroid/support/v7/internal/view/menu/MenuPresenter;
    .end local v2    # "ref":Ljava/lang/ref/WeakReference;, "Ljava/lang/ref/WeakReference<Landroid/support/v7/internal/view/menu/MenuPresenter;>;"
    :cond_2
    return-void
.end method

.method public restoreActionViewStates(Landroid/os/Bundle;)V
    .locals 10
    .param p1, "states"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 387
    if-nez p1, :cond_1

    #@2
    .line 414
    :cond_0
    :goto_0
    return-void

    #@3
    .line 391
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getActionViewStatesKey()Ljava/lang/String;

    #@6
    move-result-object v8

    #@7
    invoke-virtual {p1, v8}, Landroid/os/Bundle;->getSparseParcelableArray(Ljava/lang/String;)Landroid/util/SparseArray;

    #@a
    move-result-object v7

    #@b
    .line 394
    .local v7, "viewStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->size()I

    #@e
    move-result v3

    #@f
    .line 395
    .local v3, "itemCount":I
    const/4 v1, 0x0

    #@10
    .local v1, "i":I
    :goto_1
    if-ge v1, v3, :cond_4

    #@12
    .line 396
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getItem(I)Landroid/view/MenuItem;

    #@15
    move-result-object v2

    #@16
    .line 397
    .local v2, "item":Landroid/view/MenuItem;
    invoke-static {v2}, Landroid/support/v4/view/MenuItemCompat;->getActionView(Landroid/view/MenuItem;)Landroid/view/View;

    #@19
    move-result-object v6

    #@1a
    .line 398
    .local v6, "v":Landroid/view/View;
    if-eqz v6, :cond_2

    #@1c
    invoke-virtual {v6}, Landroid/view/View;->getId()I

    #@1f
    move-result v8

    #@20
    const/4 v9, -0x1

    #@21
    if-eq v8, v9, :cond_2

    #@23
    .line 399
    invoke-virtual {v6, v7}, Landroid/view/View;->restoreHierarchyState(Landroid/util/SparseArray;)V

    #@26
    .line 401
    :cond_2
    invoke-interface {v2}, Landroid/view/MenuItem;->hasSubMenu()Z

    #@29
    move-result v8

    #@2a
    if-eqz v8, :cond_3

    #@2c
    .line 402
    invoke-interface {v2}, Landroid/view/MenuItem;->getSubMenu()Landroid/view/SubMenu;

    #@2f
    move-result-object v5

    #@30
    check-cast v5, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@32
    .line 403
    .local v5, "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    invoke-virtual {v5, p1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->restoreActionViewStates(Landroid/os/Bundle;)V

    #@35
    .line 395
    .end local v5    # "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    :cond_3
    add-int/lit8 v1, v1, 0x1

    #@37
    goto :goto_1

    #@38
    .line 407
    .end local v2    # "item":Landroid/view/MenuItem;
    .end local v6    # "v":Landroid/view/View;
    :cond_4
    const-string v8, "android:menu:expandedactionview"

    #@3a
    invoke-virtual {p1, v8}, Landroid/os/Bundle;->getInt(Ljava/lang/String;)I

    #@3d
    move-result v0

    #@3e
    .line 408
    .local v0, "expandedId":I
    if-lez v0, :cond_0

    #@40
    .line 409
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->findItem(I)Landroid/view/MenuItem;

    #@43
    move-result-object v4

    #@44
    .line 410
    .local v4, "itemToExpand":Landroid/view/MenuItem;
    if-eqz v4, :cond_0

    #@46
    .line 411
    invoke-static {v4}, Landroid/support/v4/view/MenuItemCompat;->expandActionView(Landroid/view/MenuItem;)Z

    #@49
    goto :goto_0
.end method

.method public restorePresenterStates(Landroid/os/Bundle;)V
    .locals 0
    .param p1, "state"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 356
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->dispatchRestoreInstanceState(Landroid/os/Bundle;)V

    #@3
    .line 357
    return-void
.end method

.method public saveActionViewStates(Landroid/os/Bundle;)V
    .locals 8
    .param p1, "outStates"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 360
    const/4 v5, 0x0

    #@1
    .line 362
    .local v5, "viewStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->size()I

    #@4
    move-result v2

    #@5
    .line 363
    .local v2, "itemCount":I
    const/4 v0, 0x0

    #@6
    .local v0, "i":I
    :goto_0
    if-ge v0, v2, :cond_3

    #@8
    .line 364
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getItem(I)Landroid/view/MenuItem;

    #@b
    move-result-object v1

    #@c
    .line 365
    .local v1, "item":Landroid/view/MenuItem;
    invoke-static {v1}, Landroid/support/v4/view/MenuItemCompat;->getActionView(Landroid/view/MenuItem;)Landroid/view/View;

    #@f
    move-result-object v4

    #@10
    .line 366
    .local v4, "v":Landroid/view/View;
    if-eqz v4, :cond_1

    #@12
    invoke-virtual {v4}, Landroid/view/View;->getId()I

    #@15
    move-result v6

    #@16
    const/4 v7, -0x1

    #@17
    if-eq v6, v7, :cond_1

    #@19
    .line 367
    if-nez v5, :cond_0

    #@1b
    .line 368
    new-instance v5, Landroid/util/SparseArray;

    #@1d
    .end local v5    # "viewStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    invoke-direct {v5}, Landroid/util/SparseArray;-><init>()V

    #@20
    .line 370
    .restart local v5    # "viewStates":Landroid/util/SparseArray;, "Landroid/util/SparseArray<Landroid/os/Parcelable;>;"
    :cond_0
    invoke-virtual {v4, v5}, Landroid/view/View;->saveHierarchyState(Landroid/util/SparseArray;)V

    #@23
    .line 371
    invoke-static {v1}, Landroid/support/v4/view/MenuItemCompat;->isActionViewExpanded(Landroid/view/MenuItem;)Z

    #@26
    move-result v6

    #@27
    if-eqz v6, :cond_1

    #@29
    .line 372
    const-string v6, "android:menu:expandedactionview"

    #@2b
    invoke-interface {v1}, Landroid/view/MenuItem;->getItemId()I

    #@2e
    move-result v7

    #@2f
    invoke-virtual {p1, v6, v7}, Landroid/os/Bundle;->putInt(Ljava/lang/String;I)V

    #@32
    .line 375
    :cond_1
    invoke-interface {v1}, Landroid/view/MenuItem;->hasSubMenu()Z

    #@35
    move-result v6

    #@36
    if-eqz v6, :cond_2

    #@38
    .line 376
    invoke-interface {v1}, Landroid/view/MenuItem;->getSubMenu()Landroid/view/SubMenu;

    #@3b
    move-result-object v3

    #@3c
    check-cast v3, Landroid/support/v7/internal/view/menu/SubMenuBuilder;

    #@3e
    .line 377
    .local v3, "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    invoke-virtual {v3, p1}, Landroid/support/v7/internal/view/menu/SubMenuBuilder;->saveActionViewStates(Landroid/os/Bundle;)V

    #@41
    .line 363
    .end local v3    # "subMenu":Landroid/support/v7/internal/view/menu/SubMenuBuilder;
    :cond_2
    add-int/lit8 v0, v0, 0x1

    #@43
    goto :goto_0

    #@44
    .line 381
    .end local v1    # "item":Landroid/view/MenuItem;
    .end local v4    # "v":Landroid/view/View;
    :cond_3
    if-eqz v5, :cond_4

    #@46
    .line 382
    invoke-virtual {p0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->getActionViewStatesKey()Ljava/lang/String;

    #@49
    move-result-object v6

    #@4a
    invoke-virtual {p1, v6, v5}, Landroid/os/Bundle;->putSparseParcelableArray(Ljava/lang/String;Landroid/util/SparseArray;)V

    #@4d
    .line 384
    :cond_4
    return-void
.end method

.method public savePresenterStates(Landroid/os/Bundle;)V
    .locals 0
    .param p1, "outState"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 352
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->dispatchSaveInstanceState(Landroid/os/Bundle;)V

    #@3
    .line 353
    return-void
.end method

.method public setCallback(Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;)V
    .locals 0
    .param p1, "cb"    # Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@0
    .prologue
    .line 421
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mCallback:Landroid/support/v7/internal/view/menu/MenuBuilder$Callback;

    #@2
    .line 422
    return-void
.end method

.method public setCurrentMenuInfo(Landroid/view/ContextMenu$ContextMenuInfo;)V
    .locals 0
    .param p1, "menuInfo"    # Landroid/view/ContextMenu$ContextMenuInfo;

    #@0
    .prologue
    .line 1293
    iput-object p1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mCurrentMenuInfo:Landroid/view/ContextMenu$ContextMenuInfo;

    #@2
    .line 1294
    return-void
.end method

.method public setDefaultShowAsAction(I)Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 0
    .param p1, "defaultShowAsAction"    # I

    #@0
    .prologue
    .line 226
    iput p1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mDefaultShowAsAction:I

    #@2
    .line 227
    return-object p0
.end method

.method setExclusiveItemChecked(Landroid/view/MenuItem;)V
    .locals 5
    .param p1, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 588
    invoke-interface {p1}, Landroid/view/MenuItem;->getGroupId()I

    #@3
    move-result v2

    #@4
    .line 590
    .local v2, "group":I
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@6
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@9
    move-result v0

    #@a
    .line 591
    .local v0, "N":I
    const/4 v3, 0x0

    #@b
    .local v3, "i":I
    :goto_0
    if-ge v3, v0, :cond_3

    #@d
    .line 592
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@f
    invoke-virtual {v4, v3}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@12
    move-result-object v1

    #@13
    check-cast v1, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@15
    .line 593
    .local v1, "curItem":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@18
    move-result v4

    #@19
    if-ne v4, v2, :cond_0

    #@1b
    .line 594
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isExclusiveCheckable()Z

    #@1e
    move-result v4

    #@1f
    if-nez v4, :cond_1

    #@21
    .line 591
    :cond_0
    :goto_1
    add-int/lit8 v3, v3, 0x1

    #@23
    goto :goto_0

    #@24
    .line 595
    :cond_1
    invoke-virtual {v1}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->isCheckable()Z

    #@27
    move-result v4

    #@28
    if-eqz v4, :cond_0

    #@2a
    .line 598
    if-ne v1, p1, :cond_2

    #@2c
    const/4 v4, 0x1

    #@2d
    :goto_2
    invoke-virtual {v1, v4}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setCheckedInt(Z)V

    #@30
    goto :goto_1

    #@31
    :cond_2
    const/4 v4, 0x0

    #@32
    goto :goto_2

    #@33
    .line 601
    .end local v1    # "curItem":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_3
    return-void
.end method

.method public setGroupCheckable(IZZ)V
    .locals 4
    .param p1, "group"    # I
    .param p2, "checkable"    # Z
    .param p3, "exclusive"    # Z

    #@0
    .prologue
    .line 605
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@5
    move-result v0

    #@6
    .line 607
    .local v0, "N":I
    const/4 v1, 0x0

    #@7
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_1

    #@9
    .line 608
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v3, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@e
    move-result-object v2

    #@f
    check-cast v2, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@11
    .line 609
    .local v2, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@14
    move-result v3

    #@15
    if-ne v3, p1, :cond_0

    #@17
    .line 610
    invoke-virtual {v2, p3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setExclusiveCheckable(Z)V

    #@1a
    .line 611
    invoke-virtual {v2, p2}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setCheckable(Z)Landroid/view/MenuItem;

    #@1d
    .line 607
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@1f
    goto :goto_0

    #@20
    .line 614
    .end local v2    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1
    return-void
.end method

.method public setGroupEnabled(IZ)V
    .locals 4
    .param p1, "group"    # I
    .param p2, "enabled"    # Z

    #@0
    .prologue
    .line 636
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@5
    move-result v0

    #@6
    .line 638
    .local v0, "N":I
    const/4 v1, 0x0

    #@7
    .local v1, "i":I
    :goto_0
    if-ge v1, v0, :cond_1

    #@9
    .line 639
    iget-object v3, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@b
    invoke-virtual {v3, v1}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@e
    move-result-object v2

    #@f
    check-cast v2, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@11
    .line 640
    .local v2, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v2}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@14
    move-result v3

    #@15
    if-ne v3, p1, :cond_0

    #@17
    .line 641
    invoke-virtual {v2, p2}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setEnabled(Z)Landroid/view/MenuItem;

    #@1a
    .line 638
    :cond_0
    add-int/lit8 v1, v1, 0x1

    #@1c
    goto :goto_0

    #@1d
    .line 644
    .end local v2    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1
    return-void
.end method

.method public setGroupVisible(IZ)V
    .locals 5
    .param p1, "group"    # I
    .param p2, "visible"    # Z

    #@0
    .prologue
    .line 618
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v4}, Ljava/util/ArrayList;->size()I

    #@5
    move-result v0

    #@6
    .line 623
    .local v0, "N":I
    const/4 v1, 0x0

    #@7
    .line 624
    .local v1, "changedAtLeastOneItem":Z
    const/4 v2, 0x0

    #@8
    .local v2, "i":I
    :goto_0
    if-ge v2, v0, :cond_1

    #@a
    .line 625
    iget-object v4, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@c
    invoke-virtual {v4, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@f
    move-result-object v3

    #@10
    check-cast v3, Landroid/support/v7/internal/view/menu/MenuItemImpl;

    #@12
    .line 626
    .local v3, "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    invoke-virtual {v3}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->getGroupId()I

    #@15
    move-result v4

    #@16
    if-ne v4, p1, :cond_0

    #@18
    .line 627
    invoke-virtual {v3, p2}, Landroid/support/v7/internal/view/menu/MenuItemImpl;->setVisibleInt(Z)Z

    #@1b
    move-result v4

    #@1c
    if-eqz v4, :cond_0

    #@1e
    const/4 v1, 0x1

    #@1f
    .line 624
    :cond_0
    add-int/lit8 v2, v2, 0x1

    #@21
    goto :goto_0

    #@22
    .line 631
    .end local v3    # "item":Landroid/support/v7/internal/view/menu/MenuItemImpl;
    :cond_1
    if-eqz v1, :cond_2

    #@24
    const/4 v4, 0x1

    #@25
    invoke-virtual {p0, v4}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@28
    .line 632
    :cond_2
    return-void
.end method

.method protected setHeaderIconInt(I)Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 6
    .param p1, "iconRes"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 1248
    const/4 v1, 0x0

    #@2
    move-object v0, p0

    #@3
    move v3, p1

    #@4
    move-object v4, v2

    #@5
    move-object v5, v2

    #@6
    invoke-direct/range {v0 .. v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderInternal(ILjava/lang/CharSequence;ILandroid/graphics/drawable/Drawable;Landroid/view/View;)V

    #@9
    .line 1249
    return-object p0
.end method

.method protected setHeaderIconInt(Landroid/graphics/drawable/Drawable;)Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 6
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    const/4 v1, 0x0

    #@2
    .line 1236
    move-object v0, p0

    #@3
    move v3, v1

    #@4
    move-object v4, p1

    #@5
    move-object v5, v2

    #@6
    invoke-direct/range {v0 .. v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderInternal(ILjava/lang/CharSequence;ILandroid/graphics/drawable/Drawable;Landroid/view/View;)V

    #@9
    .line 1237
    return-object p0
.end method

.method protected setHeaderTitleInt(I)Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 6
    .param p1, "titleRes"    # I

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 1224
    const/4 v3, 0x0

    #@2
    move-object v0, p0

    #@3
    move v1, p1

    #@4
    move-object v4, v2

    #@5
    move-object v5, v2

    #@6
    invoke-direct/range {v0 .. v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderInternal(ILjava/lang/CharSequence;ILandroid/graphics/drawable/Drawable;Landroid/view/View;)V

    #@9
    .line 1225
    return-object p0
.end method

.method protected setHeaderTitleInt(Ljava/lang/CharSequence;)Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 6
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    const/4 v1, 0x0

    #@2
    .line 1212
    move-object v0, p0

    #@3
    move-object v2, p1

    #@4
    move v3, v1

    #@5
    move-object v5, v4

    #@6
    invoke-direct/range {v0 .. v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderInternal(ILjava/lang/CharSequence;ILandroid/graphics/drawable/Drawable;Landroid/view/View;)V

    #@9
    .line 1213
    return-object p0
.end method

.method protected setHeaderViewInt(Landroid/view/View;)Landroid/support/v7/internal/view/menu/MenuBuilder;
    .locals 6
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    const/4 v1, 0x0

    #@2
    .line 1260
    move-object v0, p0

    #@3
    move v3, v1

    #@4
    move-object v4, v2

    #@5
    move-object v5, p1

    #@6
    invoke-direct/range {v0 .. v5}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setHeaderInternal(ILjava/lang/CharSequence;ILandroid/graphics/drawable/Drawable;Landroid/view/View;)V

    #@9
    .line 1261
    return-object p0
.end method

.method setOptionalIconsVisible(Z)V
    .locals 0
    .param p1, "visible"    # Z

    #@0
    .prologue
    .line 1297
    iput-boolean p1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mOptionalIconsVisible:Z

    #@2
    .line 1298
    return-void
.end method

.method public setQwertyMode(Z)V
    .locals 1
    .param p1, "isQwerty"    # Z

    #@0
    .prologue
    .line 731
    iput-boolean p1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mQwertyMode:Z

    #@2
    .line 733
    const/4 v0, 0x0

    #@3
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@6
    .line 734
    return-void
.end method

.method public setShortcutsVisible(Z)V
    .locals 1
    .param p1, "shortcutsVisible"    # Z

    #@0
    .prologue
    .line 772
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mShortcutsVisible:Z

    #@2
    if-ne v0, p1, :cond_0

    #@4
    .line 778
    :goto_0
    return-void

    #@5
    .line 776
    :cond_0
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/menu/MenuBuilder;->setShortcutsVisibleInner(Z)V

    #@8
    .line 777
    const/4 v0, 0x0

    #@9
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@c
    goto :goto_0
.end method

.method public size()I
    .locals 1

    #@0
    .prologue
    .line 716
    iget-object v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItems:Ljava/util/ArrayList;

    #@2
    invoke-virtual {v0}, Ljava/util/ArrayList;->size()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public startDispatchingItemsChanged()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 1040
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPreventDispatchingItemsChanged:Z

    #@3
    .line 1042
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItemsChangedWhileDispatchPrevented:Z

    #@5
    if-eqz v0, :cond_0

    #@7
    .line 1043
    iput-boolean v1, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItemsChangedWhileDispatchPrevented:Z

    #@9
    .line 1044
    const/4 v0, 0x1

    #@a
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/view/menu/MenuBuilder;->onItemsChanged(Z)V

    #@d
    .line 1046
    :cond_0
    return-void
.end method

.method public stopDispatchingItemsChanged()V
    .locals 1

    #@0
    .prologue
    .line 1033
    iget-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPreventDispatchingItemsChanged:Z

    #@2
    if-nez v0, :cond_0

    #@4
    .line 1034
    const/4 v0, 0x1

    #@5
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mPreventDispatchingItemsChanged:Z

    #@7
    .line 1035
    const/4 v0, 0x0

    #@8
    iput-boolean v0, p0, Landroid/support/v7/internal/view/menu/MenuBuilder;->mItemsChangedWhileDispatchPrevented:Z

    #@a
    .line 1037
    :cond_0
    return-void
.end method

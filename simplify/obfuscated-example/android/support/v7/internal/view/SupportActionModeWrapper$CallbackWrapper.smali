.class public Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;
.super Ljava/lang/Object;
.source "SupportActionModeWrapper.java"

# interfaces
.implements Landroid/support/v7/view/ActionMode$Callback;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/view/SupportActionModeWrapper;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "CallbackWrapper"
.end annotation


# instance fields
.field final mActionModes:Landroid/support/v4/util/SimpleArrayMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/support/v4/util/SimpleArrayMap",
            "<",
            "Landroid/support/v7/view/ActionMode;",
            "Landroid/support/v7/internal/view/SupportActionModeWrapper;",
            ">;"
        }
    .end annotation
.end field

.field final mContext:Landroid/content/Context;

.field final mMenus:Landroid/support/v4/util/SimpleArrayMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/support/v4/util/SimpleArrayMap",
            "<",
            "Landroid/view/Menu;",
            "Landroid/view/Menu;",
            ">;"
        }
    .end annotation
.end field

.field final mWrappedCallback:Landroid/view/ActionMode$Callback;


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/view/ActionMode$Callback;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "supportCallback"    # Landroid/view/ActionMode$Callback;

    #@0
    .prologue
    .line 145
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 146
    iput-object p1, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mContext:Landroid/content/Context;

    #@5
    .line 147
    iput-object p2, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mWrappedCallback:Landroid/view/ActionMode$Callback;

    #@7
    .line 148
    new-instance v0, Landroid/support/v4/util/SimpleArrayMap;

    #@9
    invoke-direct {v0}, Landroid/support/v4/util/SimpleArrayMap;-><init>()V

    #@c
    iput-object v0, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mActionModes:Landroid/support/v4/util/SimpleArrayMap;

    #@e
    .line 149
    new-instance v0, Landroid/support/v4/util/SimpleArrayMap;

    #@10
    invoke-direct {v0}, Landroid/support/v4/util/SimpleArrayMap;-><init>()V

    #@13
    iput-object v0, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mMenus:Landroid/support/v4/util/SimpleArrayMap;

    #@15
    .line 150
    return-void
.end method

.method private getActionModeWrapper(Landroid/support/v7/view/ActionMode;)Landroid/view/ActionMode;
    .locals 3
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;

    #@0
    .prologue
    .line 187
    iget-object v2, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mActionModes:Landroid/support/v4/util/SimpleArrayMap;

    #@2
    invoke-virtual {v2, p1}, Landroid/support/v4/util/SimpleArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Landroid/support/v7/internal/view/SupportActionModeWrapper;

    #@8
    .line 188
    .local v0, "wrapper":Landroid/support/v7/internal/view/SupportActionModeWrapper;
    if-eqz v0, :cond_0

    #@a
    move-object v1, v0

    #@b
    .line 196
    .end local v0    # "wrapper":Landroid/support/v7/internal/view/SupportActionModeWrapper;
    .local v1, "wrapper":Ljava/lang/Object;
    :goto_0
    return-object v1

    #@c
    .line 194
    .end local v1    # "wrapper":Ljava/lang/Object;
    .restart local v0    # "wrapper":Landroid/support/v7/internal/view/SupportActionModeWrapper;
    :cond_0
    new-instance v0, Landroid/support/v7/internal/view/SupportActionModeWrapper;

    #@e
    .end local v0    # "wrapper":Landroid/support/v7/internal/view/SupportActionModeWrapper;
    iget-object v2, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mContext:Landroid/content/Context;

    #@10
    invoke-direct {v0, v2, p1}, Landroid/support/v7/internal/view/SupportActionModeWrapper;-><init>(Landroid/content/Context;Landroid/support/v7/view/ActionMode;)V

    #@13
    .line 195
    .restart local v0    # "wrapper":Landroid/support/v7/internal/view/SupportActionModeWrapper;
    iget-object v2, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mActionModes:Landroid/support/v4/util/SimpleArrayMap;

    #@15
    invoke-virtual {v2, p1, v0}, Landroid/support/v4/util/SimpleArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@18
    move-object v1, v0

    #@19
    .line 196
    .restart local v1    # "wrapper":Ljava/lang/Object;
    goto :goto_0
.end method

.method private getMenuWrapper(Landroid/view/Menu;)Landroid/view/Menu;
    .locals 3
    .param p1, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 177
    iget-object v1, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mMenus:Landroid/support/v4/util/SimpleArrayMap;

    #@2
    invoke-virtual {v1, p1}, Landroid/support/v4/util/SimpleArrayMap;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@5
    move-result-object v0

    #@6
    check-cast v0, Landroid/view/Menu;

    #@8
    .line 178
    .local v0, "wrapper":Landroid/view/Menu;
    if-nez v0, :cond_0

    #@a
    .line 179
    iget-object v2, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mContext:Landroid/content/Context;

    #@c
    move-object v1, p1

    #@d
    check-cast v1, Landroid/support/v4/internal/view/SupportMenu;

    #@f
    invoke-static {v2, v1}, Landroid/support/v7/internal/view/menu/MenuWrapperFactory;->wrapSupportMenu(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenu;)Landroid/view/Menu;

    #@12
    move-result-object v0

    #@13
    .line 180
    iget-object v1, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mMenus:Landroid/support/v4/util/SimpleArrayMap;

    #@15
    invoke-virtual {v1, p1, v0}, Landroid/support/v4/util/SimpleArrayMap;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@18
    .line 182
    :cond_0
    return-object v0
.end method


# virtual methods
.method public onActionItemClicked(Landroid/support/v7/view/ActionMode;Landroid/view/MenuItem;)Z
    .locals 3
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;
    .param p2, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 167
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mWrappedCallback:Landroid/view/ActionMode$Callback;

    #@2
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->getActionModeWrapper(Landroid/support/v7/view/ActionMode;)Landroid/view/ActionMode;

    #@5
    move-result-object v1

    #@6
    iget-object v2, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mContext:Landroid/content/Context;

    #@8
    check-cast p2, Landroid/support/v4/internal/view/SupportMenuItem;

    #@a
    .end local p2    # "item":Landroid/view/MenuItem;
    invoke-static {v2, p2}, Landroid/support/v7/internal/view/menu/MenuWrapperFactory;->wrapSupportMenuItem(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenuItem;)Landroid/view/MenuItem;

    #@d
    move-result-object v2

    #@e
    invoke-interface {v0, v1, v2}, Landroid/view/ActionMode$Callback;->onActionItemClicked(Landroid/view/ActionMode;Landroid/view/MenuItem;)Z

    #@11
    move-result v0

    #@12
    return v0
.end method

.method public onCreateActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z
    .locals 3
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 154
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mWrappedCallback:Landroid/view/ActionMode$Callback;

    #@2
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->getActionModeWrapper(Landroid/support/v7/view/ActionMode;)Landroid/view/ActionMode;

    #@5
    move-result-object v1

    #@6
    invoke-direct {p0, p2}, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->getMenuWrapper(Landroid/view/Menu;)Landroid/view/Menu;

    #@9
    move-result-object v2

    #@a
    invoke-interface {v0, v1, v2}, Landroid/view/ActionMode$Callback;->onCreateActionMode(Landroid/view/ActionMode;Landroid/view/Menu;)Z

    #@d
    move-result v0

    #@e
    return v0
.end method

.method public onDestroyActionMode(Landroid/support/v7/view/ActionMode;)V
    .locals 2
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;

    #@0
    .prologue
    .line 173
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mWrappedCallback:Landroid/view/ActionMode$Callback;

    #@2
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->getActionModeWrapper(Landroid/support/v7/view/ActionMode;)Landroid/view/ActionMode;

    #@5
    move-result-object v1

    #@6
    invoke-interface {v0, v1}, Landroid/view/ActionMode$Callback;->onDestroyActionMode(Landroid/view/ActionMode;)V

    #@9
    .line 174
    return-void
.end method

.method public onPrepareActionMode(Landroid/support/v7/view/ActionMode;Landroid/view/Menu;)Z
    .locals 3
    .param p1, "mode"    # Landroid/support/v7/view/ActionMode;
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 160
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->mWrappedCallback:Landroid/view/ActionMode$Callback;

    #@2
    invoke-direct {p0, p1}, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->getActionModeWrapper(Landroid/support/v7/view/ActionMode;)Landroid/view/ActionMode;

    #@5
    move-result-object v1

    #@6
    invoke-direct {p0, p2}, Landroid/support/v7/internal/view/SupportActionModeWrapper$CallbackWrapper;->getMenuWrapper(Landroid/view/Menu;)Landroid/view/Menu;

    #@9
    move-result-object v2

    #@a
    invoke-interface {v0, v1, v2}, Landroid/view/ActionMode$Callback;->onPrepareActionMode(Landroid/view/ActionMode;Landroid/view/Menu;)Z

    #@d
    move-result v0

    #@e
    return v0
.end method

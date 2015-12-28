.class public final Landroid/support/v7/internal/view/menu/MenuWrapperFactory;
.super Ljava/lang/Object;
.source "MenuWrapperFactory.java"


# direct methods
.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 32
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 33
    return-void
.end method

.method public static wrapSupportMenu(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenu;)Landroid/view/Menu;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "supportMenu"    # Landroid/support/v4/internal/view/SupportMenu;

    #@0
    .prologue
    .line 36
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xe

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 37
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuWrapperICS;

    #@8
    invoke-direct {v0, p0, p1}, Landroid/support/v7/internal/view/menu/MenuWrapperICS;-><init>(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenu;)V

    #@b
    return-object v0

    #@c
    .line 39
    :cond_0
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@e
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@11
    throw v0
.end method

.method public static wrapSupportMenuItem(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenuItem;)Landroid/view/MenuItem;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "supportMenuItem"    # Landroid/support/v4/internal/view/SupportMenuItem;

    #@0
    .prologue
    .line 43
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x10

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 44
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB;

    #@8
    invoke-direct {v0, p0, p1}, Landroid/support/v7/internal/view/menu/MenuItemWrapperJB;-><init>(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenuItem;)V

    #@b
    .line 46
    :goto_0
    return-object v0

    #@c
    .line 45
    :cond_0
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@e
    const/16 v1, 0xe

    #@10
    if-lt v0, v1, :cond_1

    #@12
    .line 46
    new-instance v0, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;

    #@14
    invoke-direct {v0, p0, p1}, Landroid/support/v7/internal/view/menu/MenuItemWrapperICS;-><init>(Landroid/content/Context;Landroid/support/v4/internal/view/SupportMenuItem;)V

    #@17
    goto :goto_0

    #@18
    .line 48
    :cond_1
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@1a
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@1d
    throw v0
.end method

.method public static wrapSupportSubMenu(Landroid/content/Context;Landroid/support/v4/internal/view/SupportSubMenu;)Landroid/view/SubMenu;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "supportSubMenu"    # Landroid/support/v4/internal/view/SupportSubMenu;

    #@0
    .prologue
    .line 52
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0xe

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 53
    new-instance v0, Landroid/support/v7/internal/view/menu/SubMenuWrapperICS;

    #@8
    invoke-direct {v0, p0, p1}, Landroid/support/v7/internal/view/menu/SubMenuWrapperICS;-><init>(Landroid/content/Context;Landroid/support/v4/internal/view/SupportSubMenu;)V

    #@b
    return-object v0

    #@c
    .line 55
    :cond_0
    new-instance v0, Ljava/lang/UnsupportedOperationException;

    #@e
    invoke-direct {v0}, Ljava/lang/UnsupportedOperationException;-><init>()V

    #@11
    throw v0
.end method

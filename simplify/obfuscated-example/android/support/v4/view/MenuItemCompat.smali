.class public Landroid/support/v4/view/MenuItemCompat;
.super Ljava/lang/Object;
.source "MenuItemCompat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/view/MenuItemCompat$IcsMenuVersionImpl;,
        Landroid/support/v4/view/MenuItemCompat$HoneycombMenuVersionImpl;,
        Landroid/support/v4/view/MenuItemCompat$BaseMenuVersionImpl;,
        Landroid/support/v4/view/MenuItemCompat$OnActionExpandListener;,
        Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;
    }
.end annotation


# static fields
.field static final IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

.field public static final SHOW_AS_ACTION_ALWAYS:I = 0x2

.field public static final SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW:I = 0x8

.field public static final SHOW_AS_ACTION_IF_ROOM:I = 0x1

.field public static final SHOW_AS_ACTION_NEVER:I = 0x0

.field public static final SHOW_AS_ACTION_WITH_TEXT:I = 0x4

.field private static final TAG:Ljava/lang/String; = "MenuItemCompat"


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 247
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    .line 248
    .local v0, "version":I
    const/16 v1, 0xe

    #@4
    if-lt v0, v1, :cond_0

    #@6
    .line 249
    new-instance v1, Landroid/support/v4/view/MenuItemCompat$IcsMenuVersionImpl;

    #@8
    invoke-direct {v1}, Landroid/support/v4/view/MenuItemCompat$IcsMenuVersionImpl;-><init>()V

    #@b
    sput-object v1, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@d
    .line 255
    :goto_0
    return-void

    #@e
    .line 250
    :cond_0
    const/16 v1, 0xb

    #@10
    if-lt v0, v1, :cond_1

    #@12
    .line 251
    new-instance v1, Landroid/support/v4/view/MenuItemCompat$HoneycombMenuVersionImpl;

    #@14
    invoke-direct {v1}, Landroid/support/v4/view/MenuItemCompat$HoneycombMenuVersionImpl;-><init>()V

    #@17
    sput-object v1, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@19
    goto :goto_0

    #@1a
    .line 253
    :cond_1
    new-instance v1, Landroid/support/v4/view/MenuItemCompat$BaseMenuVersionImpl;

    #@1c
    invoke-direct {v1}, Landroid/support/v4/view/MenuItemCompat$BaseMenuVersionImpl;-><init>()V

    #@1f
    sput-object v1, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@21
    goto :goto_0
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 32
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 200
    return-void
.end method

.method public static collapseActionView(Landroid/view/MenuItem;)Z
    .locals 1
    .param p0, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 402
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 403
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0}, Landroid/support/v4/internal/view/SupportMenuItem;->collapseActionView()Z

    #@9
    move-result v0

    #@a
    .line 405
    .restart local p0    # "item":Landroid/view/MenuItem;
    :goto_0
    return v0

    #@b
    :cond_0
    sget-object v0, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@d
    invoke-interface {v0, p0}, Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;->collapseActionView(Landroid/view/MenuItem;)Z

    #@10
    move-result v0

    #@11
    goto :goto_0
.end method

.method public static expandActionView(Landroid/view/MenuItem;)Z
    .locals 1
    .param p0, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 383
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 384
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0}, Landroid/support/v4/internal/view/SupportMenuItem;->expandActionView()Z

    #@9
    move-result v0

    #@a
    .line 386
    .restart local p0    # "item":Landroid/view/MenuItem;
    :goto_0
    return v0

    #@b
    :cond_0
    sget-object v0, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@d
    invoke-interface {v0, p0}, Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;->expandActionView(Landroid/view/MenuItem;)Z

    #@10
    move-result v0

    #@11
    goto :goto_0
.end method

.method public static getActionProvider(Landroid/view/MenuItem;)Landroid/support/v4/view/ActionProvider;
    .locals 2
    .param p0, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 361
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 362
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0}, Landroid/support/v4/internal/view/SupportMenuItem;->getSupportActionProvider()Landroid/support/v4/view/ActionProvider;

    #@9
    move-result-object v0

    #@a
    .line 367
    .restart local p0    # "item":Landroid/view/MenuItem;
    :goto_0
    return-object v0

    #@b
    .line 366
    :cond_0
    const-string v0, "MenuItemCompat"

    #@d
    const-string v1, "getActionProvider: item does not implement SupportMenuItem; returning null"

    #@f
    invoke-static {v0, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@12
    .line 367
    const/4 v0, 0x0

    #@13
    goto :goto_0
.end method

.method public static getActionView(Landroid/view/MenuItem;)Landroid/view/View;
    .locals 1
    .param p0, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 322
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 323
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0}, Landroid/support/v4/internal/view/SupportMenuItem;->getActionView()Landroid/view/View;

    #@9
    move-result-object v0

    #@a
    .line 325
    .restart local p0    # "item":Landroid/view/MenuItem;
    :goto_0
    return-object v0

    #@b
    :cond_0
    sget-object v0, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@d
    invoke-interface {v0, p0}, Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;->getActionView(Landroid/view/MenuItem;)Landroid/view/View;

    #@10
    move-result-object v0

    #@11
    goto :goto_0
.end method

.method public static isActionViewExpanded(Landroid/view/MenuItem;)Z
    .locals 1
    .param p0, "item"    # Landroid/view/MenuItem;

    #@0
    .prologue
    .line 418
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 419
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0}, Landroid/support/v4/internal/view/SupportMenuItem;->isActionViewExpanded()Z

    #@9
    move-result v0

    #@a
    .line 421
    .restart local p0    # "item":Landroid/view/MenuItem;
    :goto_0
    return v0

    #@b
    :cond_0
    sget-object v0, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@d
    invoke-interface {v0, p0}, Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;->isActionViewExpanded(Landroid/view/MenuItem;)Z

    #@10
    move-result v0

    #@11
    goto :goto_0
.end method

.method public static setActionProvider(Landroid/view/MenuItem;Landroid/support/v4/view/ActionProvider;)Landroid/view/MenuItem;
    .locals 2
    .param p0, "item"    # Landroid/view/MenuItem;
    .param p1, "provider"    # Landroid/support/v4/view/ActionProvider;

    #@0
    .prologue
    .line 344
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 345
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setSupportActionProvider(Landroid/support/v4/view/ActionProvider;)Landroid/support/v4/internal/view/SupportMenuItem;

    #@9
    move-result-object p0

    #@a
    .line 349
    :goto_0
    return-object p0

    #@b
    .line 348
    .restart local p0    # "item":Landroid/view/MenuItem;
    :cond_0
    const-string v0, "MenuItemCompat"

    #@d
    const-string v1, "setActionProvider: item does not implement SupportMenuItem; ignoring"

    #@f
    invoke-static {v0, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@12
    goto :goto_0
.end method

.method public static setActionView(Landroid/view/MenuItem;I)Landroid/view/MenuItem;
    .locals 1
    .param p0, "item"    # Landroid/view/MenuItem;
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 309
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 310
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setActionView(I)Landroid/view/MenuItem;

    #@9
    move-result-object v0

    #@a
    .line 312
    :goto_0
    return-object v0

    #@b
    .restart local p0    # "item":Landroid/view/MenuItem;
    :cond_0
    sget-object v0, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@d
    invoke-interface {v0, p0, p1}, Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;->setActionView(Landroid/view/MenuItem;I)Landroid/view/MenuItem;

    #@10
    move-result-object v0

    #@11
    goto :goto_0
.end method

.method public static setActionView(Landroid/view/MenuItem;Landroid/view/View;)Landroid/view/MenuItem;
    .locals 1
    .param p0, "item"    # Landroid/view/MenuItem;
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 287
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 288
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setActionView(Landroid/view/View;)Landroid/view/MenuItem;

    #@9
    move-result-object v0

    #@a
    .line 290
    :goto_0
    return-object v0

    #@b
    .restart local p0    # "item":Landroid/view/MenuItem;
    :cond_0
    sget-object v0, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@d
    invoke-interface {v0, p0, p1}, Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;->setActionView(Landroid/view/MenuItem;Landroid/view/View;)Landroid/view/MenuItem;

    #@10
    move-result-object v0

    #@11
    goto :goto_0
.end method

.method public static setOnActionExpandListener(Landroid/view/MenuItem;Landroid/support/v4/view/MenuItemCompat$OnActionExpandListener;)Landroid/view/MenuItem;
    .locals 1
    .param p0, "item"    # Landroid/view/MenuItem;
    .param p1, "listener"    # Landroid/support/v4/view/MenuItemCompat$OnActionExpandListener;

    #@0
    .prologue
    .line 435
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 436
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setSupportOnActionExpandListener(Landroid/support/v4/view/MenuItemCompat$OnActionExpandListener;)Landroid/support/v4/internal/view/SupportMenuItem;

    #@9
    move-result-object v0

    #@a
    .line 438
    :goto_0
    return-object v0

    #@b
    .restart local p0    # "item":Landroid/view/MenuItem;
    :cond_0
    sget-object v0, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@d
    invoke-interface {v0, p0, p1}, Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;->setOnActionExpandListener(Landroid/view/MenuItem;Landroid/support/v4/view/MenuItemCompat$OnActionExpandListener;)Landroid/view/MenuItem;

    #@10
    move-result-object v0

    #@11
    goto :goto_0
.end method

.method public static setShowAsAction(Landroid/view/MenuItem;I)V
    .locals 1
    .param p0, "item"    # Landroid/view/MenuItem;
    .param p1, "actionEnum"    # I

    #@0
    .prologue
    .line 268
    instance-of v0, p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 269
    check-cast p0, Landroid/support/v4/internal/view/SupportMenuItem;

    #@6
    .end local p0    # "item":Landroid/view/MenuItem;
    invoke-interface {p0, p1}, Landroid/support/v4/internal/view/SupportMenuItem;->setShowAsAction(I)V

    #@9
    .line 273
    :goto_0
    return-void

    #@a
    .line 271
    .restart local p0    # "item":Landroid/view/MenuItem;
    :cond_0
    sget-object v0, Landroid/support/v4/view/MenuItemCompat;->IMPL:Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;

    #@c
    invoke-interface {v0, p0, p1}, Landroid/support/v4/view/MenuItemCompat$MenuVersionImpl;->setShowAsAction(Landroid/view/MenuItem;I)V

    #@f
    goto :goto_0
.end method

.class Landroid/support/v7/app/ActionBarActivityDelegateBase$2;
.super Ljava/lang/Object;
.source "ActionBarActivityDelegateBase.java"

# interfaces
.implements Landroid/support/v4/view/OnApplyWindowInsetsListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v7/app/ActionBarActivityDelegateBase;->ensureSubDecor()V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;


# direct methods
.method constructor <init>(Landroid/support/v7/app/ActionBarActivityDelegateBase;)V
    .locals 0

    #@0
    .prologue
    .line 305
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$2;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onApplyWindowInsets(Landroid/view/View;Landroid/support/v4/view/WindowInsetsCompat;)Landroid/support/v4/view/WindowInsetsCompat;
    .locals 5
    .param p1, "v"    # Landroid/view/View;
    .param p2, "insets"    # Landroid/support/v4/view/WindowInsetsCompat;

    #@0
    .prologue
    .line 309
    invoke-virtual {p2}, Landroid/support/v4/view/WindowInsetsCompat;->getSystemWindowInsetTop()I

    #@3
    move-result v1

    #@4
    .line 310
    .local v1, "top":I
    iget-object v2, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$2;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@6
    # invokes: Landroid/support/v7/app/ActionBarActivityDelegateBase;->updateStatusGuard(I)I
    invoke-static {v2, v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$300(Landroid/support/v7/app/ActionBarActivityDelegateBase;I)I

    #@9
    move-result v0

    #@a
    .line 312
    .local v0, "newTop":I
    if-eq v1, v0, :cond_0

    #@c
    .line 313
    invoke-virtual {p2}, Landroid/support/v4/view/WindowInsetsCompat;->getSystemWindowInsetLeft()I

    #@f
    move-result v2

    #@10
    invoke-virtual {p2}, Landroid/support/v4/view/WindowInsetsCompat;->getSystemWindowInsetRight()I

    #@13
    move-result v3

    #@14
    invoke-virtual {p2}, Landroid/support/v4/view/WindowInsetsCompat;->getSystemWindowInsetBottom()I

    #@17
    move-result v4

    #@18
    invoke-virtual {p2, v2, v0, v3, v4}, Landroid/support/v4/view/WindowInsetsCompat;->replaceSystemWindowInsets(IIII)Landroid/support/v4/view/WindowInsetsCompat;

    #@1b
    move-result-object p2

    #@1c
    .line 319
    .end local p2    # "insets":Landroid/support/v4/view/WindowInsetsCompat;
    :cond_0
    return-object p2
.end method

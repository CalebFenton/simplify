.class Landroid/support/v7/app/ActionBarActivityDelegateBase$3;
.super Ljava/lang/Object;
.source "ActionBarActivityDelegateBase.java"

# interfaces
.implements Landroid/support/v7/internal/widget/FitWindowsViewGroup$OnFitSystemWindowsListener;


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
    .line 326
    iput-object p1, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$3;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    return-void
.end method


# virtual methods
.method public onFitSystemWindows(Landroid/graphics/Rect;)V
    .locals 2
    .param p1, "insets"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 329
    iget-object v0, p0, Landroid/support/v7/app/ActionBarActivityDelegateBase$3;->this$0:Landroid/support/v7/app/ActionBarActivityDelegateBase;

    #@2
    iget v1, p1, Landroid/graphics/Rect;->top:I

    #@4
    # invokes: Landroid/support/v7/app/ActionBarActivityDelegateBase;->updateStatusGuard(I)I
    invoke-static {v0, v1}, Landroid/support/v7/app/ActionBarActivityDelegateBase;->access$300(Landroid/support/v7/app/ActionBarActivityDelegateBase;I)I

    #@7
    move-result v0

    #@8
    iput v0, p1, Landroid/graphics/Rect;->top:I

    #@a
    .line 330
    return-void
.end method

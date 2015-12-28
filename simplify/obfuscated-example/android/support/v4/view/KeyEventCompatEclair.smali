.class Landroid/support/v4/view/KeyEventCompatEclair;
.super Ljava/lang/Object;
.source "KeyEventCompatEclair.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static dispatch(Landroid/view/KeyEvent;Landroid/view/KeyEvent$Callback;Ljava/lang/Object;Ljava/lang/Object;)Z
    .locals 1
    .param p0, "event"    # Landroid/view/KeyEvent;
    .param p1, "receiver"    # Landroid/view/KeyEvent$Callback;
    .param p2, "state"    # Ljava/lang/Object;
    .param p3, "target"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 30
    check-cast p2, Landroid/view/KeyEvent$DispatcherState;

    #@2
    .end local p2    # "state":Ljava/lang/Object;
    invoke-virtual {p0, p1, p2, p3}, Landroid/view/KeyEvent;->dispatch(Landroid/view/KeyEvent$Callback;Landroid/view/KeyEvent$DispatcherState;Ljava/lang/Object;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getKeyDispatcherState(Landroid/view/View;)Ljava/lang/Object;
    .locals 1
    .param p0, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 25
    invoke-virtual {p0}, Landroid/view/View;->getKeyDispatcherState()Landroid/view/KeyEvent$DispatcherState;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static isTracking(Landroid/view/KeyEvent;)Z
    .locals 1
    .param p0, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 38
    invoke-virtual {p0}, Landroid/view/KeyEvent;->isTracking()Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static startTracking(Landroid/view/KeyEvent;)V
    .locals 0
    .param p0, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 34
    invoke-virtual {p0}, Landroid/view/KeyEvent;->startTracking()V

    #@3
    .line 35
    return-void
.end method

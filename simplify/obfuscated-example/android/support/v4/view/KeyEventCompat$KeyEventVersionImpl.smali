.class interface abstract Landroid/support/v4/view/KeyEventCompat$KeyEventVersionImpl;
.super Ljava/lang/Object;
.source "KeyEventCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/KeyEventCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x608
    name = "KeyEventVersionImpl"
.end annotation


# virtual methods
.method public abstract dispatch(Landroid/view/KeyEvent;Landroid/view/KeyEvent$Callback;Ljava/lang/Object;Ljava/lang/Object;)Z
.end method

.method public abstract getKeyDispatcherState(Landroid/view/View;)Ljava/lang/Object;
.end method

.method public abstract isTracking(Landroid/view/KeyEvent;)Z
.end method

.method public abstract metaStateHasModifiers(II)Z
.end method

.method public abstract metaStateHasNoModifiers(I)Z
.end method

.method public abstract normalizeMetaState(I)I
.end method

.method public abstract startTracking(Landroid/view/KeyEvent;)V
.end method

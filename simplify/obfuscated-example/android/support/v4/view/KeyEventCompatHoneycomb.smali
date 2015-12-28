.class Landroid/support/v4/view/KeyEventCompatHoneycomb;
.super Ljava/lang/Object;
.source "KeyEventCompatHoneycomb.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static metaStateHasModifiers(II)Z
    .locals 1
    .param p0, "metaState"    # I
    .param p1, "modifiers"    # I

    #@0
    .prologue
    .line 30
    invoke-static {p0, p1}, Landroid/view/KeyEvent;->metaStateHasModifiers(II)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static metaStateHasNoModifiers(I)Z
    .locals 1
    .param p0, "metaState"    # I

    #@0
    .prologue
    .line 34
    invoke-static {p0}, Landroid/view/KeyEvent;->metaStateHasNoModifiers(I)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public static normalizeMetaState(I)I
    .locals 1
    .param p0, "metaState"    # I

    #@0
    .prologue
    .line 26
    invoke-static {p0}, Landroid/view/KeyEvent;->normalizeMetaState(I)I

    #@3
    move-result v0

    #@4
    return v0
.end method

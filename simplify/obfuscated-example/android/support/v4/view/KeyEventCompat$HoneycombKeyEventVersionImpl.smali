.class Landroid/support/v4/view/KeyEventCompat$HoneycombKeyEventVersionImpl;
.super Landroid/support/v4/view/KeyEventCompat$EclairKeyEventVersionImpl;
.source "KeyEventCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/KeyEventCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "HoneycombKeyEventVersionImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 144
    invoke-direct {p0}, Landroid/support/v4/view/KeyEventCompat$EclairKeyEventVersionImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public metaStateHasModifiers(II)Z
    .locals 1
    .param p1, "metaState"    # I
    .param p2, "modifiers"    # I

    #@0
    .prologue
    .line 152
    invoke-static {p1, p2}, Landroid/support/v4/view/KeyEventCompatHoneycomb;->metaStateHasModifiers(II)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public metaStateHasNoModifiers(I)Z
    .locals 1
    .param p1, "metaState"    # I

    #@0
    .prologue
    .line 157
    invoke-static {p1}, Landroid/support/v4/view/KeyEventCompatHoneycomb;->metaStateHasNoModifiers(I)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public normalizeMetaState(I)I
    .locals 1
    .param p1, "metaState"    # I

    #@0
    .prologue
    .line 147
    invoke-static {p1}, Landroid/support/v4/view/KeyEventCompatHoneycomb;->normalizeMetaState(I)I

    #@3
    move-result v0

    #@4
    return v0
.end method

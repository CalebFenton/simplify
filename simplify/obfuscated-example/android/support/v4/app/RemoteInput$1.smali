.class final Landroid/support/v4/app/RemoteInput$1;
.super Ljava/lang/Object;
.source "RemoteInput.java"

# interfaces
.implements Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/RemoteInput;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 263
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public build(Ljava/lang/String;Ljava/lang/CharSequence;[Ljava/lang/CharSequence;ZLandroid/os/Bundle;)Landroid/support/v4/app/RemoteInput;
    .locals 6
    .param p1, "resultKey"    # Ljava/lang/String;
    .param p2, "label"    # Ljava/lang/CharSequence;
    .param p3, "choices"    # [Ljava/lang/CharSequence;
    .param p4, "allowFreeFormInput"    # Z
    .param p5, "extras"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 268
    new-instance v0, Landroid/support/v4/app/RemoteInput;

    #@2
    move-object v1, p1

    #@3
    move-object v2, p2

    #@4
    move-object v3, p3

    #@5
    move v4, p4

    #@6
    move-object v5, p5

    #@7
    invoke-direct/range {v0 .. v5}, Landroid/support/v4/app/RemoteInput;-><init>(Ljava/lang/String;Ljava/lang/CharSequence;[Ljava/lang/CharSequence;ZLandroid/os/Bundle;)V

    #@a
    return-object v0
.end method

.method public bridge synthetic build(Ljava/lang/String;Ljava/lang/CharSequence;[Ljava/lang/CharSequence;ZLandroid/os/Bundle;)Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    .locals 1
    .param p1, "x0"    # Ljava/lang/String;
    .param p2, "x1"    # Ljava/lang/CharSequence;
    .param p3, "x2"    # [Ljava/lang/CharSequence;
    .param p4, "x3"    # Z
    .param p5, "x4"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 263
    invoke-virtual/range {p0 .. p5}, Landroid/support/v4/app/RemoteInput$1;->build(Ljava/lang/String;Ljava/lang/CharSequence;[Ljava/lang/CharSequence;ZLandroid/os/Bundle;)Landroid/support/v4/app/RemoteInput;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public newArray(I)[Landroid/support/v4/app/RemoteInput;
    .locals 1
    .param p1, "size"    # I

    #@0
    .prologue
    .line 273
    new-array v0, p1, [Landroid/support/v4/app/RemoteInput;

    #@2
    return-object v0
.end method

.method public bridge synthetic newArray(I)[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    .locals 1
    .param p1, "x0"    # I

    #@0
    .prologue
    .line 263
    invoke-virtual {p0, p1}, Landroid/support/v4/app/RemoteInput$1;->newArray(I)[Landroid/support/v4/app/RemoteInput;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

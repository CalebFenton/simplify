.class public abstract Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
.super Ljava/lang/Object;
.source "RemoteInputCompatBase.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/RemoteInputCompatBase;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "RemoteInput"
.end annotation

.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;
    }
.end annotation


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 30
    return-void
.end method


# virtual methods
.method protected abstract getAllowFreeFormInput()Z
.end method

.method protected abstract getChoices()[Ljava/lang/CharSequence;
.end method

.method protected abstract getExtras()Landroid/os/Bundle;
.end method

.method protected abstract getLabel()Ljava/lang/CharSequence;
.end method

.method protected abstract getResultKey()Ljava/lang/String;
.end method

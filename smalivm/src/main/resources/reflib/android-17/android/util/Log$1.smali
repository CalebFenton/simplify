.class final Landroid/util/Log$1;
.super Ljava/lang/Object;
.source "Log.java"

# interfaces
.implements Landroid/util/Log$TerribleFailureHandler;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/util/Log;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# direct methods
.method constructor <init>()V
    .registers 1

    .prologue
    .line 101
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method


# virtual methods
.method public onTerribleFailure(Ljava/lang/String;Landroid/util/Log$TerribleFailure;)V
    .registers 3
    .param p1, "tag"    # Ljava/lang/String;
    .param p2, "what"    # Landroid/util/Log$TerribleFailure;

    .prologue
    .line 103
    invoke-static {p1, p2}, Lcom/android/internal/os/RuntimeInit;->wtf(Ljava/lang/String;Ljava/lang/Throwable;)V

    .line 104
    return-void
.end method

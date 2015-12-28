.class abstract Landroid/support/v4/content/ModernAsyncTask$WorkerRunnable;
.super Ljava/lang/Object;
.source "ModernAsyncTask.java"

# interfaces
.implements Ljava/util/concurrent/Callable;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/content/ModernAsyncTask;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x40a
    name = "WorkerRunnable"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<Params:",
        "Ljava/lang/Object;",
        "Result:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;",
        "Ljava/util/concurrent/Callable",
        "<TResult;>;"
    }
.end annotation


# instance fields
.field mParams:[Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "[TParams;"
        }
    .end annotation
.end field


# direct methods
.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 483
    .local p0, "this":Landroid/support/v4/content/ModernAsyncTask$WorkerRunnable;, "Landroid/support/v4/content/ModernAsyncTask$WorkerRunnable<TParams;TResult;>;"
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v4/content/ModernAsyncTask$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v4/content/ModernAsyncTask$1;

    #@0
    .prologue
    .line 483
    .local p0, "this":Landroid/support/v4/content/ModernAsyncTask$WorkerRunnable;, "Landroid/support/v4/content/ModernAsyncTask$WorkerRunnable<TParams;TResult;>;"
    invoke-direct {p0}, Landroid/support/v4/content/ModernAsyncTask$WorkerRunnable;-><init>()V

    #@3
    return-void
.end method

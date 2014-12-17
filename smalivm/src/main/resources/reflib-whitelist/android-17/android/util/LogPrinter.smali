.class public Landroid/util/LogPrinter;
.super Ljava/lang/Object;
.source "LogPrinter.java"

# interfaces
.implements Landroid/util/Printer;


# instance fields
.field private final mBuffer:I

.field private final mPriority:I

.field private final mTag:Ljava/lang/String;


# direct methods
.method public constructor <init>(ILjava/lang/String;)V
    .registers 4
    .param p1, "priority"    # I
    .param p2, "tag"    # Ljava/lang/String;

    .prologue
    .line 40
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 41
    iput p1, p0, Landroid/util/LogPrinter;->mPriority:I

    .line 42
    iput-object p2, p0, Landroid/util/LogPrinter;->mTag:Ljava/lang/String;

    .line 43
    const/4 v0, 0x0

    iput v0, p0, Landroid/util/LogPrinter;->mBuffer:I

    .line 44
    return-void
.end method

.method public constructor <init>(ILjava/lang/String;I)V
    .registers 4
    .param p1, "priority"    # I
    .param p2, "tag"    # Ljava/lang/String;
    .param p3, "buffer"    # I

    .prologue
    .line 50
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 51
    iput p1, p0, Landroid/util/LogPrinter;->mPriority:I

    .line 52
    iput-object p2, p0, Landroid/util/LogPrinter;->mTag:Ljava/lang/String;

    .line 53
    iput p3, p0, Landroid/util/LogPrinter;->mBuffer:I

    .line 54
    return-void
.end method


# virtual methods
.method public println(Ljava/lang/String;)V
    .registers 5
    .param p1, "x"    # Ljava/lang/String;

    .prologue
    .line 57
    iget v0, p0, Landroid/util/LogPrinter;->mBuffer:I

    iget v1, p0, Landroid/util/LogPrinter;->mPriority:I

    iget-object v2, p0, Landroid/util/LogPrinter;->mTag:Ljava/lang/String;

    invoke-static {v0, v1, v2, p1}, Landroid/util/Log;->println_native(IILjava/lang/String;Ljava/lang/String;)I

    .line 58
    return-void
.end method

.class public final Llibcore/util/EmptyArray;
.super Ljava/lang/Object;
.source "EmptyArray.java"


# static fields
.field public static final BOOLEAN:[Z

.field public static final BYTE:[B

.field public static final CHAR:[C

.field public static final CLASS:[Ljava/lang/Class;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "[",
            "Ljava/lang/Class",
            "<*>;"
        }
    .end annotation
.end field

.field public static final DOUBLE:[D

.field public static final INT:[I

.field public static final OBJECT:[Ljava/lang/Object;

.field public static final STACK_TRACE_ELEMENT:[Ljava/lang/StackTraceElement;

.field public static final STRING:[Ljava/lang/String;

.field public static final THROWABLE:[Ljava/lang/Throwable;


# direct methods
.method static constructor <clinit>()V
    .registers 2

    .prologue
    const/4 v1, 0x0

    .line 22
    new-array v0, v1, [Z

    sput-object v0, Llibcore/util/EmptyArray;->BOOLEAN:[Z

    .line 23
    new-array v0, v1, [B

    sput-object v0, Llibcore/util/EmptyArray;->BYTE:[B

    .line 24
    new-array v0, v1, [C

    sput-object v0, Llibcore/util/EmptyArray;->CHAR:[C

    .line 25
    new-array v0, v1, [D

    sput-object v0, Llibcore/util/EmptyArray;->DOUBLE:[D

    .line 26
    new-array v0, v1, [I

    sput-object v0, Llibcore/util/EmptyArray;->INT:[I

    .line 28
    new-array v0, v1, [Ljava/lang/Class;

    sput-object v0, Llibcore/util/EmptyArray;->CLASS:[Ljava/lang/Class;

    .line 29
    new-array v0, v1, [Ljava/lang/Object;

    sput-object v0, Llibcore/util/EmptyArray;->OBJECT:[Ljava/lang/Object;

    .line 30
    new-array v0, v1, [Ljava/lang/String;

    sput-object v0, Llibcore/util/EmptyArray;->STRING:[Ljava/lang/String;

    .line 31
    new-array v0, v1, [Ljava/lang/Throwable;

    sput-object v0, Llibcore/util/EmptyArray;->THROWABLE:[Ljava/lang/Throwable;

    .line 32
    new-array v0, v1, [Ljava/lang/StackTraceElement;

    sput-object v0, Llibcore/util/EmptyArray;->STACK_TRACE_ELEMENT:[Ljava/lang/StackTraceElement;

    return-void
.end method

.method private constructor <init>()V
    .registers 1

    .prologue
    .line 20
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

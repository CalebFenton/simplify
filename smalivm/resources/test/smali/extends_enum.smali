.class public final enum Lextends_enum;
.super Ljava/lang/Enum;
.source "Level.java"


# annotations
.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Enum",
        "<",
        "Lextends_enum;",
        ">;"
    }
.end annotation


# static fields
.field private static final synthetic $VALUES:[Lextends_enum;

.field public static final enum NONE:Lextends_enum;

.field public static final enum STRONG:Lextends_enum;

.field public static final enum WEAK:Lextends_enum;


# instance fields
.field private final value:I


# direct methods
.method static constructor <clinit>()V
    .locals 5

    .prologue
    const/4 v4, 0x2

    const/4 v3, 0x1

    const/4 v2, 0x0

    .line 2
    new-instance v0, Lextends_enum;

    const-string v1, "NONE"

    invoke-direct {v0, v1, v2, v2}, Lextends_enum;-><init>(Ljava/lang/String;II)V

    sput-object v0, Lextends_enum;->NONE:Lextends_enum;

    .line 3
    new-instance v0, Lextends_enum;

    const-string v1, "WEAK"

    invoke-direct {v0, v1, v3, v3}, Lextends_enum;-><init>(Ljava/lang/String;II)V

    sput-object v0, Lextends_enum;->WEAK:Lextends_enum;

    .line 4
    new-instance v0, Lextends_enum;

    const-string v1, "STRONG"

    invoke-direct {v0, v1, v4, v4}, Lextends_enum;-><init>(Ljava/lang/String;II)V

    sput-object v0, Lextends_enum;->STRONG:Lextends_enum;

    .line 1
    const/4 v0, 0x3

    new-array v0, v0, [Lextends_enum;

    sget-object v1, Lextends_enum;->NONE:Lextends_enum;

    aput-object v1, v0, v2

    sget-object v1, Lextends_enum;->WEAK:Lextends_enum;

    aput-object v1, v0, v3

    sget-object v1, Lextends_enum;->STRONG:Lextends_enum;

    aput-object v1, v0, v4

    sput-object v0, Lextends_enum;->$VALUES:[Lextends_enum;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;II)V
    .locals 0
    .param p3, "value"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I)V"
        }
    .end annotation

    .prologue
    .line 9
    invoke-direct {p0, p1, p2}, Ljava/lang/Enum;-><init>(Ljava/lang/String;I)V

    .line 10
    iput p3, p0, Lextends_enum;->value:I

    .line 11
    return-void
.end method

.method public static valueOf(Ljava/lang/String;)Lextends_enum;
    .locals 1
    .param p0, "name"    # Ljava/lang/String;

    .prologue
    .line 1
    const-class v0, Lextends_enum;

    invoke-static {v0, p0}, Ljava/lang/Enum;->valueOf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;

    move-result-object v0

    check-cast v0, Lextends_enum;

    return-object v0
.end method

.method public static values()[Lextends_enum;
    .locals 1

    .prologue
    .line 1
    sget-object v0, Lextends_enum;->$VALUES:[Lextends_enum;

    invoke-virtual {v0}, [Lextends_enum;->clone()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lextends_enum;

    return-object v0
.end method


# virtual methods
.method public getValue()I
    .locals 1

    .prologue
    .line 14
    iget v0, p0, Lextends_enum;->value:I

    return v0
.end method

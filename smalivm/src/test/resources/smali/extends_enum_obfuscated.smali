.class public final enum Lextends_enum_obfuscated;
.super Ljava/lang/Enum;
.source "Level.java"


.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Enum",
        "<",
        "Lextends_enum_obfuscated;",
        ">;"
    }
.end annotation


.field public static final enum a:Lextends_enum_obfuscated;
.field public static final enum b:Lextends_enum_obfuscated;
.field public static final enum c:Lextends_enum_obfuscated;
.field private static final synthetic d:[Lextends_enum_obfuscated;

.field private final value:I


.method static constructor <clinit>()V
    .locals 5

    .prologue
    const/4 v4, 0x2

    const/4 v3, 0x1

    const/4 v2, 0x0

    .line 2
    new-instance v0, Lextends_enum_obfuscated;

    const-string v1, "NONE"

    invoke-direct {v0, v1, v2, v2}, Lextends_enum_obfuscated;-><init>(Ljava/lang/String;II)V

    sput-object v0, Lextends_enum_obfuscated;->a:Lextends_enum_obfuscated;

    .line 3
    new-instance v0, Lextends_enum_obfuscated;

    const-string v1, "WEAK"

    invoke-direct {v0, v1, v3, v3}, Lextends_enum_obfuscated;-><init>(Ljava/lang/String;II)V

    sput-object v0, Lextends_enum_obfuscated;->c:Lextends_enum_obfuscated;

    .line 4
    new-instance v0, Lextends_enum_obfuscated;

    const-string v1, "STRONG"

    invoke-direct {v0, v1, v4, v4}, Lextends_enum_obfuscated;-><init>(Ljava/lang/String;II)V

    sput-object v0, Lextends_enum_obfuscated;->b:Lextends_enum_obfuscated;

    .line 1
    const/4 v0, 0x3

    new-array v0, v0, [Lextends_enum_obfuscated;

    sget-object v1, Lextends_enum_obfuscated;->a:Lextends_enum_obfuscated;

    aput-object v1, v0, v2

    sget-object v1, Lextends_enum_obfuscated;->c:Lextends_enum_obfuscated;

    aput-object v1, v0, v3

    sget-object v1, Lextends_enum_obfuscated;->b:Lextends_enum_obfuscated;

    aput-object v1, v0, v4

    sput-object v0, Lextends_enum_obfuscated;->d:[Lextends_enum_obfuscated;

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
    iput p3, p0, Lextends_enum_obfuscated;->value:I

    .line 11
    return-void
.end method

.method public static valueOf(Ljava/lang/String;)Lextends_enum_obfuscated;
    .locals 1
    .param p0, "name"    # Ljava/lang/String;

    .prologue
    .line 1
    const-class v0, Lextends_enum_obfuscated;

    invoke-static {v0, p0}, Ljava/lang/Enum;->valueOf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;

    move-result-object v0

    check-cast v0, Lextends_enum_obfuscated;

    return-object v0
.end method

.method public static values()[Lextends_enum_obfuscated;
    .locals 1

    .prologue
    .line 1
    sget-object v0, Lextends_enum_obfuscated;->d:[Lextends_enum_obfuscated;

    invoke-virtual {v0}, [Lextends_enum_obfuscated;->clone()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lextends_enum_obfuscated;

    return-object v0
.end method


# virtual methods
.method public getValue()I
    .locals 1

    .prologue
    .line 14
    iget v0, p0, Lextends_enum_obfuscated;->value:I

    return v0
.end method

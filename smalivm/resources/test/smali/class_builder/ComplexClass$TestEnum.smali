.class public final enum Lorg/cf/test/ComplexClass$TestEnum;
.super Ljava/lang/Enum;
.source "TestEnum.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lorg/cf/ComplexClass;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x4019
    name = "Result"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Enum",
        "<",
        "Lorg/cf/test/ComplexClass$TestEnum;",
        ">;"
    }
.end annotation


# static fields
.field private static final synthetic $VALUES:[Lorg/cf/test/ComplexClass$TestEnum;

.field public static final enum LEFT:Lorg/cf/test/ComplexClass$TestEnum;

.field public static final enum RIGHT:Lorg/cf/test/ComplexClass$TestEnum;


# direct methods
.method static constructor <clinit>()V
    .registers 5

    .prologue
    const/4 v4, 0x2

    const/4 v3, 0x1

    const/4 v2, 0x0

    .line 60
    new-instance v0, Lorg/cf/test/ComplexClass$TestEnum;
    const-string v1, "RIGHT"
    invoke-direct {v0, v1, v2}, Lorg/cf/test/ComplexClass$TestEnum;-><init>(Ljava/lang/String;I)V
    sput-object v0, Lorg/cf/test/ComplexClass$TestEnum;->RIGHT:Lorg/cf/test/ComplexClass$TestEnum;

    .line 62
    new-instance v0, Lorg/cf/test/ComplexClass$TestEnum;
    const-string v1, "LEFT"
    invoke-direct {v0, v1, v3}, Lorg/cf/test/ComplexClass$TestEnum;-><init>(Ljava/lang/String;I)V
    sput-object v0, Lorg/cf/test/ComplexClass$TestEnum;->LEFT:Lorg/cf/test/ComplexClass$TestEnum;


    .line 58
    const/4 v0, 0x2
    new-array v0, v0, [Lorg/cf/test/ComplexClass$TestEnum;

    sget-object v1, Lorg/cf/test/ComplexClass$TestEnum;->RIGHT:Lorg/cf/test/ComplexClass$TestEnum;
    aput-object v1, v0, v2

    sget-object v1, Lorg/cf/test/ComplexClass$TestEnum;->LEFT:Lorg/cf/test/ComplexClass$TestEnum;
    aput-object v1, v0, v3


    sput-object v0, Lorg/cf/test/ComplexClass$TestEnum;->$VALUES:[Lorg/cf/test/ComplexClass$TestEnum;

    return-void
.end method

.method private constructor <init>(Ljava/lang/String;I)V
    .registers 3
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "()V"
        }
    .end annotation

    .prologue
    .line 58
    invoke-direct {p0, p1, p2}, Ljava/lang/Enum;-><init>(Ljava/lang/String;I)V

    return-void
.end method

.method public static valueOf(Ljava/lang/String;)Lorg/cf/test/ComplexClass$TestEnum;
    .registers 2
    .param p0, "name"    # Ljava/lang/String;

    .prologue
    .line 58
    const-class v0, Lorg/cf/test/ComplexClass$TestEnum;

    invoke-static {v0, p0}, Ljava/lang/Enum;->valueOf(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;

    move-result-object v0

    check-cast v0, Lorg/cf/test/ComplexClass$TestEnum;

    return-object v0
.end method

.method public static values()[Lorg/cf/test/ComplexClass$TestEnum;
    .registers 1

    .prologue
    .line 58
    sget-object v0, Lorg/cf/test/ComplexClass$TestEnum;->$VALUES:[Lorg/cf/test/ComplexClass$TestEnum;

    invoke-virtual {v0}, [Lorg/cf/test/ComplexClass$TestEnum;->clone()Ljava/lang/Object;

    move-result-object v0

    check-cast v0, [Lorg/cf/test/ComplexClass$TestEnum;

    return-object v0
.end method

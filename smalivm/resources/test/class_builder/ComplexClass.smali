# ADD reference to super class as parameter
.class public Lorg/cf/test/ComplexClass;
.super Lorg/cf/test/SuperClass;

.field public static someField:[Lorg/cf/test/ComplexClass$TestEnum;

.method public static constructor <clinit>()V
    .locals 0
    return-void
.end method

# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 3
    invoke-direct {p0}, Lorg/cf/test/SuperClass;-><init>()V

    #@3
    return-void
.end method

.method abstract public publicAbstractMethod()V
.end method

.method native protected nativeProtectedReturnsVoid()V
.end method

.method public static publicStaticWithIntParameter(I)V
    .locals 0

    return-void
.end method

.method public static publicStaticWhichTakesItself(Lorg/cf/test/ComplexClass;)V
    .locals 0

    return-void
.end method

.method private static privateStaticWithManyParameters(ZBCIJF[D[Ljava/lang/Object;)V
    .locals 0

    return-void
.end method

.method static packagePrivateWhichReturnsObject()Ljava/lang/Object;
    .locals 0
    const/4 v0, 0x0
    return-object v0
.end method

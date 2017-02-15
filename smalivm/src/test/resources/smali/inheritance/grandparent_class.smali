.class Lgrandparent_class;
.super Ljava/lang/Object;
.implements Lgrandparent_interface;

.field public static grandparentField:I
.field public static intLiteral:I = 0x5

.method public constructor <clinit>()V
    .locals 1

    const/4 v0, 0x4
    sput v0, Lgrandparent_class;->grandparentField:I

    return-void
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 3
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public interfaceMethod()V
    .locals 0

    #@0
    return-void
.end method

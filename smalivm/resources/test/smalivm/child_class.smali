.class Lchild_class;
.super Lparent_class;


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 3
    invoke-direct {p0}, Lparent_class;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public someString()Ljava/lang/String;
    .locals 1

    #@0
    .prologue
    .line 7
    const-string v0, "child"

    #@2
    return-object v0
.end method

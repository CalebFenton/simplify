.class public Lchild_class;
.super Lparent_class;

.field public static childField:I

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

.method public abstractMethod()Ljava/lang/String;
    .locals 1

    const-string v0, "abstract?"

    return-object v0
.end method

.method public static callsAbstractMethod()Ljava/lang/String;
    .locals 1

    new-instance v0, Lchild_class;
    invoke-direct {v0}, Lchild_class;-><init>()V

    invoke-virtual {v0}, Lparent_class;->abstractMethod()Ljava/lang/String;
    move-result-object v0

    return-object v0
.end method

.method public static callsParentMethod()Ljava/lang/String;
    .locals 1

    new-instance v0, Lchild_class;
    invoke-direct {v0}, Lchild_class;-><init>()V

    invoke-virtual {v0}, Lchild_class;->parentMethod()Ljava/lang/String;
    move-result-object v0

    return-object v0
.end method

.method public static takesParameter(I)V
    .locals 0

    return-void
.end method

.method public static stubMethod()V
    .locals 0

    return-void
.end method

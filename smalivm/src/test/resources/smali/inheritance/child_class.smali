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

.method public callsSuperParentMethod()Ljava/lang/String;
    .locals 1
    invoke-super {p0}, Lparent_class;->parentMethod()Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public callsSuperParentMethodRange()Ljava/lang/String;
    .locals 1
    invoke-super/range {p0 .. p0}, Lparent_class;->parentMethod()Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static callsSuperParentMethodFromChild()Ljava/lang/String;
    .locals 1
    new-instance v0, Lchild_class;
    invoke-direct {v0}, Lchild_class;-><init>()V
    invoke-virtual {v0}, Lchild_class;->callsSuperParentMethod()Ljava/lang/String;
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

# ---------------------------------------------------------------------------------------------
# Plan 9 Task 19: the ported TestFieldInheritance's triggers (core/FieldInheritanceTest.kt). The
# original ran stubMethod()V and relied on the oracle harness initializing the ENTRY class; the
# core models entry as part of invocation (Tasks 20+ own the top-level entry trigger), so each
# ported claim reads its field through an explicit sget -- the same re-expression
# StaticFieldAccessTest.canGetFieldsWhileInitializing records. One method per (field x referencing
# class), because the resolution WALK from each level is itself half of the original's claim.
# ---------------------------------------------------------------------------------------------

.method public static getParentFieldThroughChild()I
    .locals 1
    sget v0, Lchild_class;->parentField:I
    return v0
.end method

.method public static getParentFieldThroughParent()I
    .locals 1
    sget v0, Lparent_class;->parentField:I
    return v0
.end method

.method public static getGrandparentFieldThroughChild()I
    .locals 1
    sget v0, Lchild_class;->grandparentField:I
    return v0
.end method

.method public static getGrandparentFieldThroughParent()I
    .locals 1
    sget v0, Lparent_class;->grandparentField:I
    return v0
.end method

.method public static getGrandparentFieldThroughGrandparent()I
    .locals 1
    sget v0, Lgrandparent_class;->grandparentField:I
    return v0
.end method

.method public static getIntLiteralThroughChild()I
    .locals 1
    sget v0, Lchild_class;->intLiteral:I
    return v0
.end method

.method public static getIntLiteralThroughParent()I
    .locals 1
    sget v0, Lparent_class;->intLiteral:I
    return v0
.end method

.method public static getIntLiteralThroughGrandparent()I
    .locals 1
    sget v0, Lgrandparent_class;->intLiteral:I
    return v0
.end method

.class public Lunreflection_kotlin_regression;
.super Ljava/lang/Object;

.field public static number:I = 0x29
.field public static text:Ljava/lang/String; = "static"
.field public instance:I
.field public static flag:Z = true

.method public static readFlag()Ljava/lang/Object;
    .registers 3
    const-class v0, Lunreflection_kotlin_regression;
    const-string v1, "flag"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

.method public static call(Ljava/lang/reflect/Method;Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    .registers 4
    nop
    invoke-virtual {p0, p1, p2}, Ljava/lang/reflect/Method;->invoke(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v0
    return-object v0
.end method

.method public static readNumber()Ljava/lang/Object;
    .registers 3
    const-class v0, Lunreflection_kotlin_regression;
    const-string v1, "number"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

.method public static readText()Ljava/lang/Object;
    .registers 3
    const-class v0, Lunreflection_kotlin_regression;
    const-string v1, "text"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

.method public static readNumberIgnored()V
    .registers 2
    const-class v0, Lunreflection_kotlin_regression;
    const-string v1, "number"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    return-void
.end method

.method public static readInstance()Ljava/lang/Object;
    .registers 3
    const-class v0, Lunreflection_kotlin_regression;
    const-string v1, "instance"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

.method public static readOtherOwner()Ljava/lang/Object;
    .registers 3
    const-class v0, Ljava/lang/Integer;
    const-string v1, "MAX_VALUE"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

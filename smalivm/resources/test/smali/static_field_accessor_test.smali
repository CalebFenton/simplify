.class public Lstatic_field_accessor_test;
.super Ljava/lang/Object;

.field private static alwaysZero:I
.field private static testInt1:I
.field private static testInt2:I

.method public static <clinit>()V
    .locals 1
    sget v0, Lstatic_field_accessor_test;->alwaysZero:I
    sput v0, Lstatic_field_accessor_test;->testInt1:I

    const/4 v0, 0x1
    sput v0, Lstatic_field_accessor_test;->testInt2:I

    return-void
.end method

.method stubMethod()V
    .locals 0
    return-void
.end method
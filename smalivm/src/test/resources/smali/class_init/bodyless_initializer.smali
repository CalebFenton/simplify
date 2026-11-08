.class public Lclass_init/bodyless_initializer;
.super Ljava/lang/Object;

.field public static value:I

# A static initializer with no code. ART's dex verifier rejects it: "Constructor ... must not be abstract or native".
.method static native constructor <clinit>()V
.end method

.method public static read()I
    .registers 1
    sget v0, Lclass_init/bodyless_initializer;->value:I
    return v0
.end method

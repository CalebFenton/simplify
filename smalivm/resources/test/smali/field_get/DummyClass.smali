.class public Lorg/cf/smalivm/DummyClass;
.super Ljava/lang/Object;
.source "DummyClass.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Lorg/cf/smalivm/DummyClass$DummyInnerClass;
    }
.end annotation


# static fields
.field public static final publicStaticField:I = 0x10
.field protected static final protectedStaticField:I = 0x11
.field static final packagePrivateStaticField:I = 0x12
.field private static final privateStaticField:I = 0x13


# instance fields
.field public final publicInstanceField:I
.field protected final protectedInstanceField:I
.field final packagePrivateInstanceField:I
.field private final privateInstanceField:I


# direct methods
.method constructor <init>()V
    .registers 2

    .prologue
    .line 13
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 14
    const/16 v0, 0x20

    iput v0, p0, Lorg/cf/smalivm/DummyClass;->publicInstanceField:I

    .line 15
    const/16 v0, 0x21

    iput v0, p0, Lorg/cf/smalivm/DummyClass;->protectedInstanceField:I

    .line 16
    const/16 v0, 0x22

    iput v0, p0, Lorg/cf/smalivm/DummyClass;->privateInstanceField:I

    .line 17
    return-void
.end method

.method public static dummyMethod()V
    .locals 0
    return-void
.end method

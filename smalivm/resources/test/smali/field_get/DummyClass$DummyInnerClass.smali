.class Lorg/cf/smalivm/DummyClass$DummyInnerClass;
.super Ljava/lang/Object;
.source "DummyClass.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Lorg/cf/smalivm/DummyClass;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "DummyInnerClass"
.end annotation


# instance fields
.field final synthetic this$0:Lorg/cf/smalivm/DummyClass;


# direct methods
.method private constructor <init>(Lorg/cf/smalivm/DummyClass;)V
    .registers 2

    .prologue
    .line 19
    iput-object p1, p0, Lorg/cf/smalivm/DummyClass$DummyInnerClass;->this$0:Lorg/cf/smalivm/DummyClass;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method public static dummyMethod()V
    .locals 0
    return-void
.end method

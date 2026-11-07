.class public Lclass_init_failure/error;
.super Ljava/lang/Object;

.method public static constructor <clinit>()V
    .registers 1
    new-instance v0, Ljava/lang/NoClassDefFoundError;
    invoke-direct {v0}, Ljava/lang/NoClassDefFoundError;-><init>()V
    sput-object v0, Lclass_init_failure/state;->error:Ljava/lang/Error;
    throw v0
.end method

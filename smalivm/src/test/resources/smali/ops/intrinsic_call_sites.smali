.class public Lintrinsic_call_sites;
.super Ljava/lang/Object;

# Call sites of emulator intrinsics. Every method takes no parameters: tests seed the registers the
# invoke reads, so one method serves receivers of several runtime types.

.method public static cloneIntArray()Ljava/lang/Object;
    .registers 2
    invoke-virtual {v0}, [I->clone()Ljava/lang/Object;
    move-result-object v1
    return-object v1
.end method

.method public static cloneObject()Ljava/lang/Object;
    .registers 2
    invoke-virtual {v0}, Ljava/lang/Object;->clone()Ljava/lang/Object;
    move-result-object v1
    return-object v1
.end method

.method public static getClassOf()Ljava/lang/Class;
    .registers 2
    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v1
    return-object v1
.end method

.method public static componentTypeOf()Ljava/lang/Class;
    .registers 2
    invoke-virtual {v0}, Ljava/lang/Class;->getComponentType()Ljava/lang/Class;
    move-result-object v1
    return-object v1
.end method

# v1 keeps the class `getClass` returned; v2 receives its component type.
.method public static componentTypeOfClassOf()Ljava/lang/Class;
    .registers 3
    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v1
    invoke-virtual {v1}, Ljava/lang/Class;->getComponentType()Ljava/lang/Class;
    move-result-object v2
    return-object v2
.end method

.method public static initObject()V
    .registers 2
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static initExtendsEnum()V
    .registers 4
    invoke-direct {v0, v1, v2, v3}, Lextends_enum;-><init>(Ljava/lang/String;II)V
    return-void
.end method

# v3 and v4 are free for tests that hold other references across the call.
.method public static initEnum()V
    .registers 5
    invoke-direct {v0, v1, v2}, Ljava/lang/Enum;-><init>(Ljava/lang/String;I)V
    return-void
.end method

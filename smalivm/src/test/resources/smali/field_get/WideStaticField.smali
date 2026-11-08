.class public Lorg/cf/smalivm/WideStaticField;
.super Ljava/lang/Object;

# Wide statics read back through java.lang.reflect.Field, the shape unreflection consumes.

.field public static longValue:J = 0x1122334455667788L

.field public static doubleValue:D = 2.5

.field public static defaultLong:J

.field public static clinitLong:J

.method static constructor <clinit>()V
    .registers 2
    const-wide v0, 0x7L
    sput-wide v0, Lorg/cf/smalivm/WideStaticField;->clinitLong:J
    return-void
.end method

.method public static readLong()Ljava/lang/Object;
    .registers 3
    const-class v0, Lorg/cf/smalivm/WideStaticField;
    const-string v1, "longValue"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

.method public static readDouble()Ljava/lang/Object;
    .registers 3
    const-class v0, Lorg/cf/smalivm/WideStaticField;
    const-string v1, "doubleValue"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

.method public static readDefaultLong()Ljava/lang/Object;
    .registers 3
    const-class v0, Lorg/cf/smalivm/WideStaticField;
    const-string v1, "defaultLong"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

.method public static readClinitLong()Ljava/lang/Object;
    .registers 3
    const-class v0, Lorg/cf/smalivm/WideStaticField;
    const-string v1, "clinitLong"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2
    return-object v2
.end method

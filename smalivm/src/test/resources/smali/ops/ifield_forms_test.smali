# Every standard iget/iput opcode, plus both types shared by the generic and wide forms.
.class public Lifield_forms_test;
.super Ljava/lang/Object;

.field public booleanValue:Z
.field public byteValue:B
.field public shortValue:S
.field public charValue:C
.field public intValue:I
.field public longValue:J
.field public floatValue:F
.field public doubleValue:D
.field public objectValue:Ljava/lang/String;

.method public static getBooleanDefault()Z
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget-boolean v1, v0, Lifield_forms_test;->booleanValue:Z
    return v1
.end method

.method public static getByteDefault()B
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget-byte v1, v0, Lifield_forms_test;->byteValue:B
    return v1
.end method

.method public static getShortDefault()S
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget-short v1, v0, Lifield_forms_test;->shortValue:S
    return v1
.end method

.method public static getCharDefault()C
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget-char v1, v0, Lifield_forms_test;->charValue:C
    return v1
.end method

.method public static getIntDefault()I
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget v1, v0, Lifield_forms_test;->intValue:I
    return v1
.end method

.method public static getLongDefault()J
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget-wide v0, v0, Lifield_forms_test;->longValue:J
    return-wide v0
.end method

.method public static getFloatDefault()F
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget v1, v0, Lifield_forms_test;->floatValue:F
    return v1
.end method

.method public static getDoubleDefault()D
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget-wide v0, v0, Lifield_forms_test;->doubleValue:D
    return-wide v0
.end method

.method public static getObjectDefault()Ljava/lang/String;
    .locals 2
    new-instance v0, Lifield_forms_test;
    iget-object v1, v0, Lifield_forms_test;->objectValue:Ljava/lang/String;
    return-object v1
.end method

.method public static putBoolean(Z)Z
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput-boolean p0, v0, Lifield_forms_test;->booleanValue:Z
    iget-boolean v1, v0, Lifield_forms_test;->booleanValue:Z
    return v1
.end method

.method public static putByte(B)B
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput-byte p0, v0, Lifield_forms_test;->byteValue:B
    iget-byte v1, v0, Lifield_forms_test;->byteValue:B
    return v1
.end method

.method public static putShort(S)S
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput-short p0, v0, Lifield_forms_test;->shortValue:S
    iget-short v1, v0, Lifield_forms_test;->shortValue:S
    return v1
.end method

.method public static putChar(C)C
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput-char p0, v0, Lifield_forms_test;->charValue:C
    iget-char v1, v0, Lifield_forms_test;->charValue:C
    return v1
.end method

.method public static putInt(I)I
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput p0, v0, Lifield_forms_test;->intValue:I
    iget v1, v0, Lifield_forms_test;->intValue:I
    return v1
.end method

.method public static putLong(J)J
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput-wide p0, v0, Lifield_forms_test;->longValue:J
    iget-wide v0, v0, Lifield_forms_test;->longValue:J
    return-wide v0
.end method

.method public static putFloat(F)F
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput p0, v0, Lifield_forms_test;->floatValue:F
    iget v1, v0, Lifield_forms_test;->floatValue:F
    return v1
.end method

.method public static putDouble(D)D
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput-wide p0, v0, Lifield_forms_test;->doubleValue:D
    iget-wide v0, v0, Lifield_forms_test;->doubleValue:D
    return-wide v0
.end method

.method public static putObject(Ljava/lang/String;)Ljava/lang/String;
    .locals 2
    new-instance v0, Lifield_forms_test;
    iput-object p0, v0, Lifield_forms_test;->objectValue:Ljava/lang/String;
    iget-object v1, v0, Lifield_forms_test;->objectValue:Ljava/lang/String;
    return-object v1
.end method

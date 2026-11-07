.class public Lhost_nondeterminism_test;
.super Ljava/lang/Object;

.method public static strictMathRandom()D
    .locals 2

    invoke-static {}, Ljava/lang/StrictMath;->random()D
    move-result-wide v0
    return-wide v0
.end method

.method public static currentTimeMillis()J
    .locals 2

    invoke-static {}, Ljava/lang/System;->currentTimeMillis()J
    move-result-wide v0
    return-wide v0
.end method

.method public static nanoTime()J
    .locals 2

    invoke-static {}, Ljava/lang/System;->nanoTime()J
    move-result-wide v0
    return-wide v0
.end method

.method public static newDateTime()J
    .locals 2

    new-instance v0, Ljava/util/Date;
    invoke-direct {v0}, Ljava/util/Date;-><init>()V
    invoke-virtual {v0}, Ljava/util/Date;->getTime()J
    move-result-wide v0
    return-wide v0
.end method

.method public static epochDateString()Ljava/lang/String;
    .locals 3

    new-instance v0, Ljava/util/Date;
    const-wide/16 v1, 0x0
    invoke-direct {v0, v1, v2}, Ljava/util/Date;-><init>(J)V
    invoke-virtual {v0}, Ljava/util/Date;->toString()Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static calendarInstanceTime()J
    .locals 2

    invoke-static {}, Ljava/util/Calendar;->getInstance()Ljava/util/Calendar;
    move-result-object v0
    invoke-virtual {v0}, Ljava/util/Calendar;->getTimeInMillis()J
    move-result-wide v0
    return-wide v0
.end method

.method public static gregorianCalendarTime()J
    .locals 2

    new-instance v0, Ljava/util/GregorianCalendar;
    invoke-direct {v0}, Ljava/util/GregorianCalendar;-><init>()V
    invoke-virtual {v0}, Ljava/util/GregorianCalendar;->getTimeInMillis()J
    move-result-wide v0
    return-wide v0
.end method

.method public static epochSqlDateString()Ljava/lang/String;
    .locals 3

    new-instance v0, Ljava/sql/Date;
    const-wide/16 v1, 0x0
    invoke-direct {v0, v1, v2}, Ljava/sql/Date;-><init>(J)V
    invoke-virtual {v0}, Ljava/sql/Date;->toString()Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static epochTimestampString()Ljava/lang/String;
    .locals 3

    new-instance v0, Ljava/sql/Timestamp;
    const-wide/16 v1, 0x0
    invoke-direct {v0, v1, v2}, Ljava/sql/Timestamp;-><init>(J)V
    invoke-virtual {v0}, Ljava/sql/Timestamp;->toString()Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static enumIdentityHash()I
    .locals 1

    sget-object v0, Ljava/math/RoundingMode;->HALF_UP:Ljava/math/RoundingMode;
    invoke-virtual {v0}, Ljava/math/RoundingMode;->hashCode()I
    move-result v0
    return v0
.end method

.method public static booleanSystemProperty()Z
    .locals 1

    const-string v0, "java.specification.version"
    invoke-static {v0}, Ljava/lang/Boolean;->getBoolean(Ljava/lang/String;)Z
    move-result v0
    return v0
.end method

.method public static integerSystemProperty()Ljava/lang/Integer;
    .locals 1

    const-string v0, "java.specification.version"
    invoke-static {v0}, Ljava/lang/Integer;->getInteger(Ljava/lang/String;)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method

.method public static integerSystemPropertyWithIntDefault()Ljava/lang/Integer;
    .locals 2

    const-string v0, "java.specification.version"
    const/4 v1, 0x7
    invoke-static {v0, v1}, Ljava/lang/Integer;->getInteger(Ljava/lang/String;I)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method

.method public static integerSystemPropertyWithBoxedDefault()Ljava/lang/Integer;
    .locals 2

    const-string v0, "java.specification.version"
    const/4 v1, 0x0
    invoke-static {v0, v1}, Ljava/lang/Integer;->getInteger(Ljava/lang/String;Ljava/lang/Integer;)Ljava/lang/Integer;
    move-result-object v0
    return-object v0
.end method

.method public static longSystemProperty()Ljava/lang/Long;
    .locals 1

    const-string v0, "java.specification.version"
    invoke-static {v0}, Ljava/lang/Long;->getLong(Ljava/lang/String;)Ljava/lang/Long;
    move-result-object v0
    return-object v0
.end method

.method public static longSystemPropertyWithLongDefault()Ljava/lang/Long;
    .locals 3

    const-string v0, "java.specification.version"
    const-wide/16 v1, 0x7
    invoke-static {v0, v1, v2}, Ljava/lang/Long;->getLong(Ljava/lang/String;J)Ljava/lang/Long;
    move-result-object v0
    return-object v0
.end method

.method public static longSystemPropertyWithBoxedDefault()Ljava/lang/Long;
    .locals 2

    const-string v0, "java.specification.version"
    const/4 v1, 0x0
    invoke-static {v0, v1}, Ljava/lang/Long;->getLong(Ljava/lang/String;Ljava/lang/Long;)Ljava/lang/Long;
    move-result-object v0
    return-object v0
.end method

.method public static urlEncodeWithDefaultCharset()Ljava/lang/String;
    .locals 1

    const-string v0, "\u00e9"
    invoke-static {v0}, Ljava/net/URLEncoder;->encode(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static urlDecodeWithDefaultCharset()Ljava/lang/String;
    .locals 1

    const-string v0, "%C3%A9"
    invoke-static {v0}, Ljava/net/URLDecoder;->decode(Ljava/lang/String;)Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

.method public static hostPackage()Ljava/lang/Package;
    .locals 1

    const-string v0, "java.lang"
    invoke-static {v0}, Ljava/lang/Package;->getPackage(Ljava/lang/String;)Ljava/lang/Package;
    move-result-object v0
    return-object v0
.end method

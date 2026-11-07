.class public Lclass_init_failure/checks;
.super Ljava/lang/Object;

.method public static causesMatch(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    .registers 6
    invoke-virtual {p0}, Ljava/lang/ExceptionInInitializerError;->getCause()Ljava/lang/Throwable;
    move-result-object v0
    invoke-virtual {p0}, Ljava/lang/ExceptionInInitializerError;->getException()Ljava/lang/Throwable;
    move-result-object v1
    if-ne v0, v1, :bad
    sget-object v3, Lclass_init_failure/state;->nonError:Ljava/lang/Throwable;
    if-ne v0, v3, :bad
    invoke-virtual {p1}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v2
    if-ne p0, v2, :bad
    const/4 v0, 0x1
    return v0
  :bad
    const/4 v0, 0x0
    return v0
.end method

.method public static errorCausesMatch(Ljava/lang/Error;Ljava/lang/NoClassDefFoundError;)I
    .registers 4
    invoke-virtual {p1}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v0
    if-ne p0, v0, :bad
    const/4 v0, 0x1
    return v0
  :bad
    const/4 v0, 0x0
    return v0
.end method

.method public static outwardMatches(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    .registers 3
    invoke-virtual {p1}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v0
    if-ne p0, v0, :bad
    const/4 v0, 0x1
    return v0
  :bad
    const/4 v0, 0x0
    return v0
.end method

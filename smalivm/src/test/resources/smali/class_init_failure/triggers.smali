.class public Lclass_init_failure/triggers;
.super Ljava/lang/Object;

.method public static newInstance()I
    .registers 4
  :first_start
    new-instance v0, Lclass_init_failure/non_error;
  :first_end
    const/4 v3, 0x0
    return v3
  :first_catch
    move-exception v0
  :later_start
    new-instance v1, Lclass_init_failure/non_error;
  :later_end
    const/4 v3, 0x0
    return v3
  :later_catch
    move-exception v1
    invoke-static {v0, v1}, Lclass_init_failure/checks;->causesMatch(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    move-result v2
    return v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static writesBeforeFailureRemainVisible()I
    .registers 4
  :first_start
    new-instance v3, Lclass_init_failure/non_error;
  :first_end
    const/4 v2, 0x0
    return v2
  :first_catch
    move-exception v0
    sget v1, Lclass_init_failure/state;->attempts:I
    const/4 v2, 0x1
    if-ne v1, v2, :bad
    sget v1, Lclass_init_failure/state;->beforeFailure:I
    const/4 v2, 0x7
    if-ne v1, v2, :bad
    const/4 v2, 0x1
    return v2
  :bad
    const/4 v2, 0x0
    return v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
.end method

.method public static sget()I
    .registers 4
  :first_start
    sget v3, Lclass_init_failure/non_error;->value:I
  :first_end
    const/4 v3, 0x0
    return v3
  :first_catch
    move-exception v0
  :later_start
    sget v3, Lclass_init_failure/non_error;->value:I
  :later_end
    const/4 v3, 0x0
    return v3
  :later_catch
    move-exception v1
    invoke-static {v0, v1}, Lclass_init_failure/checks;->causesMatch(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    move-result v2
    return v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static sput()I
    .registers 4
    const/4 v3, 0x2
  :first_start
    sput v3, Lclass_init_failure/non_error;->value:I
  :first_end
    const/4 v3, 0x0
    return v3
  :first_catch
    move-exception v0
  :later_start
    sput v3, Lclass_init_failure/non_error;->value:I
  :later_end
    const/4 v3, 0x0
    return v3
  :later_catch
    move-exception v1
    invoke-static {v0, v1}, Lclass_init_failure/checks;->causesMatch(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    move-result v2
    return v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static invokeStatic()I
    .registers 3
  :first_start
    invoke-static {}, Lclass_init_failure/non_error;->touch()V
  :first_end
    const/4 v2, 0x0
    return v2
  :first_catch
    move-exception v0
  :later_start
    invoke-static {}, Lclass_init_failure/non_error;->touch()V
  :later_end
    const/4 v2, 0x0
    return v2
  :later_catch
    move-exception v1
    invoke-static {v0, v1}, Lclass_init_failure/checks;->causesMatch(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    move-result v2
    return v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static classForName()I
    .registers 4
    const-string v3, "class_init_failure.non_error"
  :first_start
    invoke-static {v3}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
  :first_end
    const/4 v2, 0x0
    return v2
  :first_catch
    move-exception v0
  :later_start
    invoke-static {v3}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
  :later_end
    const/4 v2, 0x0
    return v2
  :later_catch
    move-exception v1
    invoke-static {v0, v1}, Lclass_init_failure/checks;->causesMatch(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    move-result v2
    return v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static reflectiveStatic()I
    .registers 6
    const-class v3, Lclass_init_failure/non_error;
    const-string v4, "value"
    invoke-virtual {v3, v4}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v3
    const/4 v4, 0x0
  :first_start
    invoke-virtual {v3, v4}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
  :first_end
    const/4 v2, 0x0
    return v2
  :first_catch
    move-exception v0
  :later_start
    invoke-virtual {v3, v4}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
  :later_end
    const/4 v2, 0x0
    return v2
  :later_catch
    move-exception v1
    invoke-static {v0, v1}, Lclass_init_failure/checks;->causesMatch(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    move-result v2
    return v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static errorIdentity()I
    .registers 4
  :first_start
    new-instance v3, Lclass_init_failure/error;
  :first_end
    const/4 v2, 0x0
    return v2
  :first_catch
    move-exception v0
    sget-object v2, Lclass_init_failure/state;->error:Ljava/lang/Error;
    if-ne v0, v2, :bad
  :later_start
    new-instance v3, Lclass_init_failure/error;
  :later_end
    const/4 v2, 0x0
    return v2
  :later_catch
    move-exception v1
    invoke-static {v0, v1}, Lclass_init_failure/checks;->errorCausesMatch(Ljava/lang/Error;Ljava/lang/NoClassDefFoundError;)I
    move-result v2
    return v2
  :bad
    const/4 v2, 0x0
    return v2
    .catch Ljava/lang/Error; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static grandparentFailure()I
    .registers 5
  :first_start
    new-instance v4, Lclass_init_failure/child;
  :first_end
    const/4 v3, 0x0
    return v3
  :first_catch
    move-exception v0
  :later_start
    new-instance v4, Lclass_init_failure/child;
  :later_end
    const/4 v3, 0x0
    return v3
  :later_catch
    move-exception v1
    invoke-static {v0, v1}, Lclass_init_failure/checks;->outwardMatches(Ljava/lang/ExceptionInInitializerError;Ljava/lang/NoClassDefFoundError;)I
    move-result v2
    sget v3, Lclass_init_failure/state;->grandparentAttempts:I
    if-ne v2, v3, :bad
    sget v3, Lclass_init_failure/state;->parentAttempts:I
    if-nez v3, :bad
    sget v3, Lclass_init_failure/state;->childAttempts:I
    if-nez v3, :bad
    return v2
  :bad
    const/4 v2, 0x0
    return v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static sequentialDependencyFailureChain()I
    .registers 5
  :grandparent_start
    new-instance v4, Lclass_init_failure/grandparent;
  :grandparent_end
    const/4 v4, 0x0
    return v4
  :grandparent_catch
    move-exception v0
  :parent_start
    new-instance v4, Lclass_init_failure/parent;
  :parent_end
    const/4 v4, 0x0
    return v4
  :parent_catch
    move-exception v1
    invoke-virtual {v1}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v4
    if-ne v0, v4, :bad
  :child_start
    new-instance v4, Lclass_init_failure/child;
  :child_end
    const/4 v4, 0x0
    return v4
  :child_catch
    move-exception v2
    invoke-virtual {v2}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v4
    if-ne v1, v4, :bad
  :later_child_start
    new-instance v4, Lclass_init_failure/child;
  :later_child_end
    const/4 v4, 0x0
    return v4
  :later_child_catch
    move-exception v3
    invoke-virtual {v3}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v4
    if-ne v2, v4, :bad
    const/4 v4, 0x1
    return v4
  :bad
    const/4 v4, 0x0
    return v4
    .catch Ljava/lang/ExceptionInInitializerError; {:grandparent_start .. :grandparent_end} :grandparent_catch
    .catch Ljava/lang/NoClassDefFoundError; {:parent_start .. :parent_end} :parent_catch
    .catch Ljava/lang/NoClassDefFoundError; {:child_start .. :child_end} :child_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_child_start .. :later_child_end} :later_child_catch
.end method

.method public static initializerLocalCatch()I
    .registers 3
    new-instance v2, Lclass_init_failure/caught_initializer;
    new-instance v2, Lclass_init_failure/caught_initializer;
    sget v0, Lclass_init_failure/caught_initializer;->attempts:I
    const/4 v1, 0x1
    if-ne v0, v1, :bad
    return v1
  :bad
    const/4 v0, 0x0
    return v0
.end method

.method public static unknownSplit()I
    .registers 2
  :try_start
    new-instance v0, Lclass_init_failure/unknown;
  :try_end
    const/4 v0, 0x0
    return v0
  :error
    move-exception v0
    const/4 v1, 0x1
    return v1
  :wrapper
    move-exception v0
    const/4 v1, 0x2
    return v1
    .catch Ljava/lang/ExceptionInInitializerError; {:try_start .. :try_end} :wrapper
    .catch Ljava/lang/Error; {:try_start .. :try_end} :error
.end method

.method public static freshLaterTriggers()I
    .registers 5
  :first_start
    new-instance v4, Lclass_init_failure/non_error;
  :first_end
    const/4 v3, 0x0
    return v3
  :first_catch
    move-exception v0
  :second_start
    new-instance v4, Lclass_init_failure/non_error;
  :second_end
    const/4 v3, 0x0
    return v3
  :second_catch
    move-exception v1
  :third_start
    new-instance v4, Lclass_init_failure/non_error;
  :third_end
    const/4 v3, 0x0
    return v3
  :third_catch
    move-exception v2
    if-eq v1, v2, :bad
    invoke-virtual {v1}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v3
    if-ne v0, v3, :bad
    invoke-virtual {v2}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v3
    if-ne v0, v3, :bad
    sget v4, Lclass_init_failure/state;->attempts:I
    const/4 v3, 0x1
    if-ne v3, v4, :bad
    return v3
  :bad
    const/4 v3, 0x0
    return v3
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:second_start .. :second_end} :second_catch
    .catch Ljava/lang/NoClassDefFoundError; {:third_start .. :third_end} :third_catch
.end method

.method public static fanoutLaterTrigger()V
    .registers 2
  :first_start
    new-instance v1, Lclass_init_failure/non_error;
  :first_end
    return-void
  :first_catch
    move-exception v0
  :later_start
    new-instance v1, Lclass_init_failure/non_error;
  :later_end
    return-void
  :missing
    move-exception v0
    throw v0
  :known
    move-exception v0
    throw v0
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Lclass_init_failure/missing; {:later_start .. :later_end} :missing
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :known
.end method

.method public static hostBoundaryKeepsStoredCause(I)Ljava/lang/Throwable;
    .registers 5
  :first_start
    new-instance v3, Lclass_init_failure/non_error;
  :first_end
    const/4 v0, 0x0
    return-object v0
  :first_catch
    move-exception v0
    if-eqz p0, :host
    return-object v0
  :host
    const/4 v0, 0x0
    const-string v1, "host"
    invoke-virtual {v1}, Ljava/lang/String;->length()I
    move-result v2
  :later_start
    new-instance v3, Lclass_init_failure/non_error;
  :later_end
    return-object v0
  :later_catch
    move-exception v1
    invoke-virtual {v1}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v2
    return-object v2
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:later_start .. :later_end} :later_catch
.end method

.method public static limitAbort()I
    .registers 2
    new-instance v0, Lclass_init_failure/limit;
    const/4 v1, 0x1
    return v1
.end method

.method public static hostAbort()I
    .registers 2
    new-instance v0, Lclass_init_failure/host_abort;
    const/4 v1, 0x1
    return v1
.end method

.method public static transformFailureBoundary()I
    .registers 2
    new-instance v0, Lclass_init_failure/transform_boundary;
    const/4 v1, 0x7
    return v1
.end method

.method public static hostOnlyError()I
    .registers 2
  :try_start
    new-instance v0, Lclass_init_failure/host_only_error;
  :try_end
    const/4 v1, 0x0
    return v1
  :wrapper
    move-exception v0
    const/4 v1, 0x1
    return v1
  :error
    move-exception v0
    const/4 v1, 0x2
    return v1
    .catch Ljava/lang/ExceptionInInitializerError; {:try_start .. :try_end} :wrapper
    .catch Ljava/lang/Error; {:try_start .. :try_end} :error
.end method

.method public static branchLocal(I)Ljava/lang/Throwable;
    .registers 5
    if-eqz p0, :left
  :right_first_start
    new-instance v4, Lclass_init_failure/non_error;
  :right_first_end
    const/4 v0, 0x0
    return-object v0
  :right_first_catch
    move-exception v0
    const/4 v2, 0x0
    sput-object v2, Lclass_init_failure/state;->nonError:Ljava/lang/Throwable;
    const/4 v0, 0x0
  :right_later_start
    new-instance v4, Lclass_init_failure/non_error;
  :right_later_end
    return-object v0
  :right_later_catch
    move-exception v1
    invoke-virtual {v1}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v2
    return-object v2
  :left
  :left_first_start
    new-instance v4, Lclass_init_failure/non_error;
  :left_first_end
    const/4 v0, 0x0
    return-object v0
  :left_first_catch
    move-exception v0
    const/4 v2, 0x0
    sput-object v2, Lclass_init_failure/state;->nonError:Ljava/lang/Throwable;
    const/4 v0, 0x0
  :left_later_start
    new-instance v4, Lclass_init_failure/non_error;
  :left_later_end
    return-object v0
  :left_later_catch
    move-exception v1
    invoke-virtual {v1}, Ljava/lang/NoClassDefFoundError;->getCause()Ljava/lang/Throwable;
    move-result-object v2
    return-object v2
    .catch Ljava/lang/ExceptionInInitializerError; {:right_first_start .. :right_first_end} :right_first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:right_later_start .. :right_later_end} :right_later_catch
    .catch Ljava/lang/ExceptionInInitializerError; {:left_first_start .. :left_first_end} :left_first_catch
    .catch Ljava/lang/NoClassDefFoundError; {:left_later_start .. :left_later_end} :left_later_catch
.end method

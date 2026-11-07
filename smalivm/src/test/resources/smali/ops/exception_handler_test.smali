.class public Lexception_handler_test;
.super Ljava/lang/Object;

.field public saved:Ljava/lang/Throwable;
.field public static savedStatic:Ljava/lang/Throwable;

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
.end method

.method public static exceptionThenRuntime(Ljava/lang/Throwable;)I
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :exception
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    :runtime
    move-exception v0
    const/4 v0, 0x2
    return v0
    :exception
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static runtimeThenException(Ljava/lang/Throwable;)I
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :exception
    :runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static catchThrowable(Ljava/lang/Throwable;)I
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :handler
    :handler
    move-exception v0
    const/4 v0, 0x3
    return v0
.end method

.method public static rethrowResidual(Ljava/lang/Throwable;)I
    .locals 2
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :exception
    :runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    move-object v1, v0
    :rethrow_start
    throw v1
    :rethrow_end
    .catch Ljava/lang/RuntimeException; {:rethrow_start .. :rethrow_end} :shadowed
    :shadowed
    move-exception v0
    const/4 v0, 0x3
    return v0
.end method

.method public static nonThrowableReachesError(Ljava/lang/Object;)I
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :exception
    .catch Ljava/lang/Error; {:try_start .. :try_end} :error
    :exception
    move-exception v0
    const/4 v0, 0x1
    return v0
    :error
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static throwInCallee(Ljava/lang/Throwable;)V
    .locals 0
    throw p0
.end method

.method public static callerRuntimeThenException(Ljava/lang/Throwable;)I
    .locals 1
    :try_start
    invoke-static {p0}, Lexception_handler_test;->throwInCallee(Ljava/lang/Throwable;)V
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :exception
    const/4 v0, 0x0
    return v0
    :runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static directRethrowResidual(Ljava/lang/Throwable;)I
    .locals 1
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    :second_start
    throw v0
    :second_end
    .catch Ljava/lang/RuntimeException; {:second_start .. :second_end} :second_runtime
    :second_runtime
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static castObjectResidual(Ljava/lang/Throwable;)I
    .locals 1
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    check-cast v0, Ljava/lang/Object;
    :second_start
    throw v0
    :second_end
    .catch Ljava/lang/RuntimeException; {:second_start .. :second_end} :second_runtime
    :second_runtime
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static narrowCastResidual(Ljava/lang/Throwable;)I
    .locals 2
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    :cast_start
    check-cast v0, Ljava/io/IOException;
    :cast_end
    .catch Ljava/lang/ClassCastException; {:cast_start .. :cast_end} :cast_failed
    :success_throw_start
    throw v0
    :success_throw_end
    .catch Ljava/io/IOException; {:success_throw_start .. :success_throw_end} :cast_success
    :cast_success
    move-exception v1
    const/4 v0, 0x2
    return v0
    :cast_failed
    move-exception v1
    :failure_throw_start
    throw v0
    :failure_throw_end
    .catch Ljava/io/IOException; {:failure_throw_start .. :failure_throw_end} :bad_failure_route
    :bad_failure_route
    move-exception v1
    const/4 v0, 0x3
    return v0
.end method

.method public static guestArgumentResidual(Ljava/lang/Throwable;)I
    .locals 1
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    :call_start
    invoke-static {v0}, Lexception_handler_test;->throwInCallee(Ljava/lang/Throwable;)V
    :call_end
    .catch Ljava/lang/RuntimeException; {:call_start .. :call_end} :second_runtime
    :second_runtime
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static returnThrowable(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    .locals 0
    return-object p0
.end method

.method public static guestReturnResidual(Ljava/lang/Throwable;)I
    .locals 2
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    invoke-static {v0}, Lexception_handler_test;->returnThrowable(Ljava/lang/Throwable;)Ljava/lang/Throwable;
    move-result-object v1
    :second_start
    throw v1
    :second_end
    .catch Ljava/lang/RuntimeException; {:second_start .. :second_end} :second_runtime
    :second_runtime
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static arrayForgetsResidual(Ljava/lang/Throwable;)I
    .locals 3
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    const/4 v1, 0x1
    new-array v2, v1, [Ljava/lang/Throwable;
    const/4 v1, 0x0
    aput-object v0, v2, v1
    aget-object v0, v2, v1
    :second_start
    throw v0
    :second_end
    .catch Ljava/lang/RuntimeException; {:second_start .. :second_end} :forgotten
    :forgotten
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static fieldForgetsResidual(Ljava/lang/Throwable;)I
    .locals 2
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    new-instance v1, Lexception_handler_test;
    invoke-direct {v1}, Lexception_handler_test;-><init>()V
    iput-object v0, v1, Lexception_handler_test;->saved:Ljava/lang/Throwable;
    iget-object v0, v1, Lexception_handler_test;->saved:Ljava/lang/Throwable;
    :second_start
    throw v0
    :second_end
    .catch Ljava/lang/RuntimeException; {:second_start .. :second_end} :forgotten
    :forgotten
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static staticForgetsResidual(Ljava/lang/Throwable;)I
    .locals 1
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    sput-object v0, Lexception_handler_test;->savedStatic:Ljava/lang/Throwable;
    sget-object v0, Lexception_handler_test;->savedStatic:Ljava/lang/Throwable;
    :second_start
    throw v0
    :second_end
    .catch Ljava/lang/RuntimeException; {:second_start .. :second_end} :forgotten
    :forgotten
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static rawRuntimeTypeBeatsThrowableTag(Ljava/lang/Throwable;)I
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :npe
    .catch Ljava/lang/Throwable; {:try_start .. :try_end} :throwable
    :throwable
    move-exception v0
    const/4 v0, 0x2
    return v0
    :npe
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static callerExceptionThenRuntime(Ljava/lang/Throwable;)I
    .locals 1
    :try_start
    invoke-static {p0}, Lexception_handler_test;->throwInCallee(Ljava/lang/Throwable;)V
    :try_end
    .catch Ljava/lang/Exception; {:try_start .. :try_end} :exception
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    const/4 v0, 0x0
    return v0
    :runtime
    move-exception v0
    const/4 v0, 0x2
    return v0
    :exception
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static absentDeclaredType(Lmissing/AbsentThrowable;)I
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    :runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static catchAll(Ljava/lang/Throwable;)I
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    .catchall {:try_start .. :try_end} :all
    :runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :all
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static boundaryStartMinusOne()I
    .locals 1
    const/4 v0, 0x0
    throw v0
    :try_start
    nop
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static boundaryStart()I
    .locals 1
    const/4 v0, 0x0
    :try_start
    throw v0
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static boundaryEndMinusOne()I
    .locals 1
    const/4 v0, 0x0
    :try_start
    nop
    throw v0
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static boundaryEnd()I
    .locals 1
    const/4 v0, 0x0
    :try_start
    nop
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    throw v0
    const/4 v0, 0x0
    return v0
    :handler
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

.method public static hostCallForgetsResidual(Ljava/lang/Throwable;)I
    .locals 2
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    invoke-virtual {v0}, Ljava/lang/Object;->getClass()Ljava/lang/Class;
    move-result-object v1
    :second_start
    throw v0
    :second_end
    .catch Ljava/lang/RuntimeException; {:second_start .. :second_end} :forgotten
    :forgotten
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static native opaqueConsume(Ljava/lang/Throwable;)V
.end method

.method public static opaqueCallForgetsResidual(Ljava/lang/Throwable;)I
    .locals 1
    :first_start
    throw p0
    :first_end
    .catch Ljava/lang/RuntimeException; {:first_start .. :first_end} :first_runtime
    .catch Ljava/lang/Exception; {:first_start .. :first_end} :exception
    :first_runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
    :exception
    move-exception v0
    invoke-static {v0}, Lexception_handler_test;->opaqueConsume(Ljava/lang/Throwable;)V
    :second_start
    throw v0
    :second_end
    .catch Ljava/lang/RuntimeException; {:second_start .. :second_end} :forgotten
    :forgotten
    move-exception v0
    const/4 v0, 0x2
    return v0
.end method

.method public static callerReturnsCaught()Ljava/lang/Throwable;
    .locals 1
    :try_start
    invoke-static {}, Linvoke_review_callee;->throwsNpe()V
    :try_end
    .catch Ljava/lang/NullPointerException; {:try_start .. :try_end} :handler
    const/4 v0, 0x0
    return-object v0
    :handler
    move-exception v0
    return-object v0
.end method

.method public static routePublicValue(Ljava/lang/Throwable;)I
    .locals 1
    :try_start
    throw p0
    :try_end
    .catch Ljava/lang/RuntimeException; {:try_start .. :try_end} :runtime
    :runtime
    move-exception v0
    const/4 v0, 0x1
    return v0
.end method

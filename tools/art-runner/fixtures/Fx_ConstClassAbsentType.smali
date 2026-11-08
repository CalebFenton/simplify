# Const-class naming an ABSENT type (the resolution-failure family).
# Lcom/example/DefinitelyMissing; resolves against nothing on the boot classpath. The question
# this measures is the handler-reachability one: is the failure an Error or an Exception, and
# which type? A device that raises java.lang.Error-family (NoClassDefFoundError, via LinkageError)
# is NOT caught by `catch Ljava/lang/Exception;` -- which is what const_test.smali's
# constClassUnknown()V does. The throw is left to propagate; the dispatcher reports THREW <class>.
# Read WITH the control Fx_ConstClassPresentType, which resolves java.lang.String successfully, so
# "everything fails to resolve" cannot masquerade as this result.
# Expected: THREW java.lang.NoClassDefFoundError (measured; see docs/DALVIK-SEMANTICS.md).
.class public LFx_ConstClassAbsentType;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 2

    const-class v0, Lcom/example/DefinitelyMissing;

    # Only reached if resolution unexpectedly succeeded.
    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;
    move-result-object v1
    return-object v1
.end method

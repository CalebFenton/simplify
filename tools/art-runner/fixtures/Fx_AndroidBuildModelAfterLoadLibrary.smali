# Can a bare VM reach a JNI-backed framework class by loading the library itself?
#
# `Fx_AndroidBuildModel` fails with UnsatisfiedLinkError because
# `SystemProperties.native_get` has no implementation registered. The obvious question is
# whether `System.loadLibrary("android_runtime")` is enough to fix that, which decides how
# much of the framework a bare `dalvikvm` can actually reach.
#
# Returns the model string on success; the dispatcher reports whatever it throws otherwise.
.class public LFx_AndroidBuildModelAfterLoadLibrary;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    const-string v0, "android_runtime"
    invoke-static {v0}, Ljava/lang/System;->loadLibrary(Ljava/lang/String;)V

    sget-object v0, Landroid/os/Build;->MODEL:Ljava/lang/String;

    return-object v0
.end method

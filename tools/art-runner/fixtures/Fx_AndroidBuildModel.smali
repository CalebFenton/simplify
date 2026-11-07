# Can bare `dalvikvm` touch the Android framework? Reads a static field off
# `android.os.Build`, which is backed by system properties.
#
# Absent on the ART runner (no android.* at all) -- expect ClassNotFoundException there,
# and that contrast is the point of running it on both.
.class public LFx_AndroidBuildModel;
.super Ljava/lang/Object;

.method public static run()Ljava/lang/Object;
    .locals 1

    sget-object v0, Landroid/os/Build;->MODEL:Ljava/lang/String;

    return-object v0
.end method

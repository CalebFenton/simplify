.class Landroid/support/v4/app/BundleUtil;
.super Ljava/lang/Object;
.source "BundleUtil.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 11
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getBundleArrayFromBundle(Landroid/os/Bundle;Ljava/lang/String;)[Landroid/os/Bundle;
    .locals 4
    .param p0, "bundle"    # Landroid/os/Bundle;
    .param p1, "key"    # Ljava/lang/String;

    #@0
    .prologue
    .line 18
    invoke-virtual {p0, p1}, Landroid/os/Bundle;->getParcelableArray(Ljava/lang/String;)[Landroid/os/Parcelable;

    #@3
    move-result-object v0

    #@4
    .line 19
    .local v0, "array":[Landroid/os/Parcelable;
    instance-of v2, v0, [Landroid/os/Bundle;

    #@6
    if-nez v2, :cond_0

    #@8
    if-nez v0, :cond_1

    #@a
    .line 20
    :cond_0
    check-cast v0, [Landroid/os/Bundle;

    #@c
    .end local v0    # "array":[Landroid/os/Parcelable;
    check-cast v0, [Landroid/os/Bundle;

    #@e
    move-object v1, v0

    #@f
    .line 25
    :goto_0
    return-object v1

    #@10
    .line 22
    .restart local v0    # "array":[Landroid/os/Parcelable;
    :cond_1
    array-length v2, v0

    #@11
    const-class v3, [Landroid/os/Bundle;

    #@13
    invoke-static {v0, v2, v3}, Ljava/util/Arrays;->copyOf([Ljava/lang/Object;ILjava/lang/Class;)[Ljava/lang/Object;

    #@16
    move-result-object v1

    #@17
    check-cast v1, [Landroid/os/Bundle;

    #@19
    .line 24
    .local v1, "typedArray":[Landroid/os/Bundle;
    invoke-virtual {p0, p1, v1}, Landroid/os/Bundle;->putParcelableArray(Ljava/lang/String;[Landroid/os/Parcelable;)V

    #@1c
    goto :goto_0
.end method

.class final Landroid/support/v4/media/RatingCompat$1;
.super Ljava/lang/Object;
.source "RatingCompat.java"

# interfaces
.implements Landroid/os/Parcelable$Creator;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/media/RatingCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Ljava/lang/Object;",
        "Landroid/os/Parcelable$Creator",
        "<",
        "Landroid/support/v4/media/RatingCompat;",
        ">;"
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 103
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public createFromParcel(Landroid/os/Parcel;)Landroid/support/v4/media/RatingCompat;
    .locals 4
    .param p1, "p"    # Landroid/os/Parcel;

    #@0
    .prologue
    .line 111
    new-instance v0, Landroid/support/v4/media/RatingCompat;

    #@2
    invoke-virtual {p1}, Landroid/os/Parcel;->readInt()I

    #@5
    move-result v1

    #@6
    invoke-virtual {p1}, Landroid/os/Parcel;->readFloat()F

    #@9
    move-result v2

    #@a
    const/4 v3, 0x0

    #@b
    invoke-direct {v0, v1, v2, v3}, Landroid/support/v4/media/RatingCompat;-><init>(IFLandroid/support/v4/media/RatingCompat$1;)V

    #@e
    return-object v0
.end method

.method public bridge synthetic createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;
    .locals 1
    .param p1, "x0"    # Landroid/os/Parcel;

    #@0
    .prologue
    .line 103
    invoke-virtual {p0, p1}, Landroid/support/v4/media/RatingCompat$1;->createFromParcel(Landroid/os/Parcel;)Landroid/support/v4/media/RatingCompat;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public newArray(I)[Landroid/support/v4/media/RatingCompat;
    .locals 1
    .param p1, "size"    # I

    #@0
    .prologue
    .line 116
    new-array v0, p1, [Landroid/support/v4/media/RatingCompat;

    #@2
    return-object v0
.end method

.method public bridge synthetic newArray(I)[Ljava/lang/Object;
    .locals 1
    .param p1, "x0"    # I

    #@0
    .prologue
    .line 103
    invoke-virtual {p0, p1}, Landroid/support/v4/media/RatingCompat$1;->newArray(I)[Landroid/support/v4/media/RatingCompat;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

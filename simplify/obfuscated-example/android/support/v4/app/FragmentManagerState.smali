.class final Landroid/support/v4/app/FragmentManagerState;
.super Ljava/lang/Object;
.source "FragmentManager.java"

# interfaces
.implements Landroid/os/Parcelable;


# static fields
.field public static final CREATOR:Landroid/os/Parcelable$Creator;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/os/Parcelable$Creator",
            "<",
            "Landroid/support/v4/app/FragmentManagerState;",
            ">;"
        }
    .end annotation
.end field


# instance fields
.field mActive:[Landroid/support/v4/app/FragmentState;

.field mAdded:[I

.field mBackStack:[Landroid/support/v4/app/BackStackState;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 386
    new-instance v0, Landroid/support/v4/app/FragmentManagerState$1;

    #@2
    invoke-direct {v0}, Landroid/support/v4/app/FragmentManagerState$1;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/app/FragmentManagerState;->CREATOR:Landroid/os/Parcelable$Creator;

    #@7
    return-void
.end method

.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 367
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 368
    return-void
.end method

.method public constructor <init>(Landroid/os/Parcel;)V
    .locals 1
    .param p1, "in"    # Landroid/os/Parcel;

    #@0
    .prologue
    .line 370
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 371
    sget-object v0, Landroid/support/v4/app/FragmentState;->CREATOR:Landroid/os/Parcelable$Creator;

    #@5
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->createTypedArray(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;

    #@8
    move-result-object v0

    #@9
    check-cast v0, [Landroid/support/v4/app/FragmentState;

    #@b
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerState;->mActive:[Landroid/support/v4/app/FragmentState;

    #@d
    .line 372
    invoke-virtual {p1}, Landroid/os/Parcel;->createIntArray()[I

    #@10
    move-result-object v0

    #@11
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerState;->mAdded:[I

    #@13
    .line 373
    sget-object v0, Landroid/support/v4/app/BackStackState;->CREATOR:Landroid/os/Parcelable$Creator;

    #@15
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->createTypedArray(Landroid/os/Parcelable$Creator;)[Ljava/lang/Object;

    #@18
    move-result-object v0

    #@19
    check-cast v0, [Landroid/support/v4/app/BackStackState;

    #@1b
    iput-object v0, p0, Landroid/support/v4/app/FragmentManagerState;->mBackStack:[Landroid/support/v4/app/BackStackState;

    #@1d
    .line 374
    return-void
.end method


# virtual methods
.method public describeContents()I
    .locals 1

    #@0
    .prologue
    .line 377
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public writeToParcel(Landroid/os/Parcel;I)V
    .locals 1
    .param p1, "dest"    # Landroid/os/Parcel;
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 381
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerState;->mActive:[Landroid/support/v4/app/FragmentState;

    #@2
    invoke-virtual {p1, v0, p2}, Landroid/os/Parcel;->writeTypedArray([Landroid/os/Parcelable;I)V

    #@5
    .line 382
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerState;->mAdded:[I

    #@7
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeIntArray([I)V

    #@a
    .line 383
    iget-object v0, p0, Landroid/support/v4/app/FragmentManagerState;->mBackStack:[Landroid/support/v4/app/BackStackState;

    #@c
    invoke-virtual {p1, v0, p2}, Landroid/os/Parcel;->writeTypedArray([Landroid/os/Parcelable;I)V

    #@f
    .line 384
    return-void
.end method

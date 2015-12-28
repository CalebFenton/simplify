.class public final Landroid/support/v4/media/session/PlaybackStateCompat;
.super Ljava/lang/Object;
.source "PlaybackStateCompat.java"

# interfaces
.implements Landroid/os/Parcelable;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/media/session/PlaybackStateCompat$Builder;
    }
.end annotation


# static fields
.field public static final ACTION_FAST_FORWARD:J = 0x40L

.field public static final ACTION_PAUSE:J = 0x2L

.field public static final ACTION_PLAY:J = 0x4L

.field public static final ACTION_PLAY_FROM_MEDIA_ID:J = 0x400L

.field public static final ACTION_PLAY_FROM_SEARCH:J = 0x800L

.field public static final ACTION_PLAY_PAUSE:J = 0x200L

.field public static final ACTION_REWIND:J = 0x8L

.field public static final ACTION_SEEK_TO:J = 0x100L

.field public static final ACTION_SET_RATING:J = 0x80L

.field public static final ACTION_SKIP_TO_NEXT:J = 0x20L

.field public static final ACTION_SKIP_TO_PREVIOUS:J = 0x10L

.field public static final ACTION_SKIP_TO_QUEUE_ITEM:J = 0x1000L

.field public static final ACTION_STOP:J = 0x1L

.field public static final CREATOR:Landroid/os/Parcelable$Creator;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/os/Parcelable$Creator",
            "<",
            "Landroid/support/v4/media/session/PlaybackStateCompat;",
            ">;"
        }
    .end annotation
.end field

.field public static final PLAYBACK_POSITION_UNKNOWN:J = -0x1L

.field public static final STATE_BUFFERING:I = 0x6

.field public static final STATE_CONNECTING:I = 0x8

.field public static final STATE_ERROR:I = 0x7

.field public static final STATE_FAST_FORWARDING:I = 0x4

.field public static final STATE_NONE:I = 0x0

.field public static final STATE_PAUSED:I = 0x2

.field public static final STATE_PLAYING:I = 0x3

.field public static final STATE_REWINDING:I = 0x5

.field public static final STATE_SKIPPING_TO_NEXT:I = 0xa

.field public static final STATE_SKIPPING_TO_PREVIOUS:I = 0x9

.field public static final STATE_STOPPED:I = 0x1


# instance fields
.field private final mActions:J

.field private final mBufferedPosition:J

.field private final mErrorMessage:Ljava/lang/CharSequence;

.field private final mPosition:J

.field private final mSpeed:F

.field private final mState:I

.field private mStateObj:Ljava/lang/Object;

.field private final mUpdateTime:J


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 394
    new-instance v0, Landroid/support/v4/media/session/PlaybackStateCompat$1;

    #@2
    invoke-direct {v0}, Landroid/support/v4/media/session/PlaybackStateCompat$1;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/media/session/PlaybackStateCompat;->CREATOR:Landroid/os/Parcelable$Creator;

    #@7
    return-void
.end method

.method private constructor <init>(IJJFJLjava/lang/CharSequence;J)V
    .locals 0
    .param p1, "state"    # I
    .param p2, "position"    # J
    .param p4, "bufferedPosition"    # J
    .param p6, "rate"    # F
    .param p7, "actions"    # J
    .param p9, "errorMessage"    # Ljava/lang/CharSequence;
    .param p10, "updateTime"    # J

    #@0
    .prologue
    .line 220
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 221
    iput p1, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mState:I

    #@5
    .line 222
    iput-wide p2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mPosition:J

    #@7
    .line 223
    iput-wide p4, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mBufferedPosition:J

    #@9
    .line 224
    iput p6, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mSpeed:F

    #@b
    .line 225
    iput-wide p7, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mActions:J

    #@d
    .line 226
    iput-object p9, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mErrorMessage:Ljava/lang/CharSequence;

    #@f
    .line 227
    iput-wide p10, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mUpdateTime:J

    #@11
    .line 228
    return-void
.end method

.method synthetic constructor <init>(IJJFJLjava/lang/CharSequence;JLandroid/support/v4/media/session/PlaybackStateCompat$1;)V
    .locals 0
    .param p1, "x0"    # I
    .param p2, "x1"    # J
    .param p4, "x2"    # J
    .param p6, "x3"    # F
    .param p7, "x4"    # J
    .param p9, "x5"    # Ljava/lang/CharSequence;
    .param p10, "x6"    # J
    .param p12, "x7"    # Landroid/support/v4/media/session/PlaybackStateCompat$1;

    #@0
    .prologue
    .line 29
    invoke-direct/range {p0 .. p11}, Landroid/support/v4/media/session/PlaybackStateCompat;-><init>(IJJFJLjava/lang/CharSequence;J)V

    #@3
    return-void
.end method

.method private constructor <init>(Landroid/os/Parcel;)V
    .locals 2
    .param p1, "in"    # Landroid/os/Parcel;

    #@0
    .prologue
    .line 230
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 231
    invoke-virtual {p1}, Landroid/os/Parcel;->readInt()I

    #@6
    move-result v0

    #@7
    iput v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mState:I

    #@9
    .line 232
    invoke-virtual {p1}, Landroid/os/Parcel;->readLong()J

    #@c
    move-result-wide v0

    #@d
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mPosition:J

    #@f
    .line 233
    invoke-virtual {p1}, Landroid/os/Parcel;->readFloat()F

    #@12
    move-result v0

    #@13
    iput v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mSpeed:F

    #@15
    .line 234
    invoke-virtual {p1}, Landroid/os/Parcel;->readLong()J

    #@18
    move-result-wide v0

    #@19
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mUpdateTime:J

    #@1b
    .line 235
    invoke-virtual {p1}, Landroid/os/Parcel;->readLong()J

    #@1e
    move-result-wide v0

    #@1f
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mBufferedPosition:J

    #@21
    .line 236
    invoke-virtual {p1}, Landroid/os/Parcel;->readLong()J

    #@24
    move-result-wide v0

    #@25
    iput-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mActions:J

    #@27
    .line 237
    sget-object v0, Landroid/text/TextUtils;->CHAR_SEQUENCE_CREATOR:Landroid/os/Parcelable$Creator;

    #@29
    invoke-interface {v0, p1}, Landroid/os/Parcelable$Creator;->createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object;

    #@2c
    move-result-object v0

    #@2d
    check-cast v0, Ljava/lang/CharSequence;

    #@2f
    iput-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mErrorMessage:Ljava/lang/CharSequence;

    #@31
    .line 238
    return-void
.end method

.method synthetic constructor <init>(Landroid/os/Parcel;Landroid/support/v4/media/session/PlaybackStateCompat$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/os/Parcel;
    .param p2, "x1"    # Landroid/support/v4/media/session/PlaybackStateCompat$1;

    #@0
    .prologue
    .line 29
    invoke-direct {p0, p1}, Landroid/support/v4/media/session/PlaybackStateCompat;-><init>(Landroid/os/Parcel;)V

    #@3
    return-void
.end method

.method static synthetic access$100(Landroid/support/v4/media/session/PlaybackStateCompat;)I
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 29
    iget v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mState:I

    #@2
    return v0
.end method

.method static synthetic access$200(Landroid/support/v4/media/session/PlaybackStateCompat;)J
    .locals 2
    .param p0, "x0"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 29
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mPosition:J

    #@2
    return-wide v0
.end method

.method static synthetic access$300(Landroid/support/v4/media/session/PlaybackStateCompat;)F
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 29
    iget v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mSpeed:F

    #@2
    return v0
.end method

.method static synthetic access$400(Landroid/support/v4/media/session/PlaybackStateCompat;)J
    .locals 2
    .param p0, "x0"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 29
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mUpdateTime:J

    #@2
    return-wide v0
.end method

.method static synthetic access$500(Landroid/support/v4/media/session/PlaybackStateCompat;)J
    .locals 2
    .param p0, "x0"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 29
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mBufferedPosition:J

    #@2
    return-wide v0
.end method

.method static synthetic access$600(Landroid/support/v4/media/session/PlaybackStateCompat;)J
    .locals 2
    .param p0, "x0"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 29
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mActions:J

    #@2
    return-wide v0
.end method

.method static synthetic access$700(Landroid/support/v4/media/session/PlaybackStateCompat;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/media/session/PlaybackStateCompat;

    #@0
    .prologue
    .line 29
    iget-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mErrorMessage:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public static fromPlaybackState(Ljava/lang/Object;)Landroid/support/v4/media/session/PlaybackStateCompat;
    .locals 12
    .param p0, "stateObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 360
    if-eqz p0, :cond_0

    #@2
    sget v1, Landroid/os/Build$VERSION;->SDK_INT:I

    #@4
    const/16 v2, 0x15

    #@6
    if-ge v1, v2, :cond_1

    #@8
    .line 361
    :cond_0
    const/4 v0, 0x0

    #@9
    .line 373
    :goto_0
    return-object v0

    #@a
    .line 364
    :cond_1
    new-instance v0, Landroid/support/v4/media/session/PlaybackStateCompat;

    #@c
    invoke-static {p0}, Landroid/support/v4/media/session/PlaybackStateCompatApi21;->getState(Ljava/lang/Object;)I

    #@f
    move-result v1

    #@10
    invoke-static {p0}, Landroid/support/v4/media/session/PlaybackStateCompatApi21;->getPosition(Ljava/lang/Object;)J

    #@13
    move-result-wide v2

    #@14
    invoke-static {p0}, Landroid/support/v4/media/session/PlaybackStateCompatApi21;->getBufferedPosition(Ljava/lang/Object;)J

    #@17
    move-result-wide v4

    #@18
    invoke-static {p0}, Landroid/support/v4/media/session/PlaybackStateCompatApi21;->getPlaybackSpeed(Ljava/lang/Object;)F

    #@1b
    move-result v6

    #@1c
    invoke-static {p0}, Landroid/support/v4/media/session/PlaybackStateCompatApi21;->getActions(Ljava/lang/Object;)J

    #@1f
    move-result-wide v7

    #@20
    invoke-static {p0}, Landroid/support/v4/media/session/PlaybackStateCompatApi21;->getErrorMessage(Ljava/lang/Object;)Ljava/lang/CharSequence;

    #@23
    move-result-object v9

    #@24
    invoke-static {p0}, Landroid/support/v4/media/session/PlaybackStateCompatApi21;->getLastPositionUpdateTime(Ljava/lang/Object;)J

    #@27
    move-result-wide v10

    #@28
    invoke-direct/range {v0 .. v11}, Landroid/support/v4/media/session/PlaybackStateCompat;-><init>(IJJFJLjava/lang/CharSequence;J)V

    #@2b
    .line 372
    .local v0, "state":Landroid/support/v4/media/session/PlaybackStateCompat;
    iput-object p0, v0, Landroid/support/v4/media/session/PlaybackStateCompat;->mStateObj:Ljava/lang/Object;

    #@2d
    goto :goto_0
.end method


# virtual methods
.method public describeContents()I
    .locals 1

    #@0
    .prologue
    .line 256
    const/4 v0, 0x0

    #@1
    return v0
.end method

.method public getActions()J
    .locals 2

    #@0
    .prologue
    .line 329
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mActions:J

    #@2
    return-wide v0
.end method

.method public getBufferedPosition()J
    .locals 2

    #@0
    .prologue
    .line 299
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mBufferedPosition:J

    #@2
    return-wide v0
.end method

.method public getErrorMessage()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 337
    iget-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mErrorMessage:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getLastPositionUpdateTime()J
    .locals 2

    #@0
    .prologue
    .line 347
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mUpdateTime:J

    #@2
    return-wide v0
.end method

.method public getPlaybackSpeed()F
    .locals 1

    #@0
    .prologue
    .line 310
    iget v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mSpeed:F

    #@2
    return v0
.end method

.method public getPlaybackState()Ljava/lang/Object;
    .locals 12

    #@0
    .prologue
    .line 385
    iget-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mStateObj:Ljava/lang/Object;

    #@2
    if-nez v0, :cond_0

    #@4
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@6
    const/16 v1, 0x15

    #@8
    if-ge v0, v1, :cond_1

    #@a
    .line 386
    :cond_0
    iget-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mStateObj:Ljava/lang/Object;

    #@c
    .line 391
    :goto_0
    return-object v0

    #@d
    .line 389
    :cond_1
    iget v1, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mState:I

    #@f
    iget-wide v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mPosition:J

    #@11
    iget-wide v4, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mBufferedPosition:J

    #@13
    iget v6, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mSpeed:F

    #@15
    iget-wide v7, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mActions:J

    #@17
    iget-object v9, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mErrorMessage:Ljava/lang/CharSequence;

    #@19
    iget-wide v10, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mUpdateTime:J

    #@1b
    invoke-static/range {v1 .. v11}, Landroid/support/v4/media/session/PlaybackStateCompatApi21;->newInstance(IJJFJLjava/lang/CharSequence;J)Ljava/lang/Object;

    #@1e
    move-result-object v0

    #@1f
    iput-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mStateObj:Ljava/lang/Object;

    #@21
    .line 391
    iget-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mStateObj:Ljava/lang/Object;

    #@23
    goto :goto_0
.end method

.method public getPosition()J
    .locals 2

    #@0
    .prologue
    .line 290
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mPosition:J

    #@2
    return-wide v0
.end method

.method public getState()I
    .locals 1

    #@0
    .prologue
    .line 283
    iget v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mState:I

    #@2
    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 4

    #@0
    .prologue
    .line 242
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    const-string v1, "PlaybackState {"

    #@4
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    #@7
    .line 243
    .local v0, "bob":Ljava/lang/StringBuilder;
    const-string v1, "state="

    #@9
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@c
    move-result-object v1

    #@d
    iget v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mState:I

    #@f
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@12
    .line 244
    const-string v1, ", position="

    #@14
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@17
    move-result-object v1

    #@18
    iget-wide v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mPosition:J

    #@1a
    invoke-virtual {v1, v2, v3}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    #@1d
    .line 245
    const-string v1, ", buffered position="

    #@1f
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@22
    move-result-object v1

    #@23
    iget-wide v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mBufferedPosition:J

    #@25
    invoke-virtual {v1, v2, v3}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    #@28
    .line 246
    const-string v1, ", speed="

    #@2a
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@2d
    move-result-object v1

    #@2e
    iget v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mSpeed:F

    #@30
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(F)Ljava/lang/StringBuilder;

    #@33
    .line 247
    const-string v1, ", updated="

    #@35
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@38
    move-result-object v1

    #@39
    iget-wide v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mUpdateTime:J

    #@3b
    invoke-virtual {v1, v2, v3}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    #@3e
    .line 248
    const-string v1, ", actions="

    #@40
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@43
    move-result-object v1

    #@44
    iget-wide v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mActions:J

    #@46
    invoke-virtual {v1, v2, v3}, Ljava/lang/StringBuilder;->append(J)Ljava/lang/StringBuilder;

    #@49
    .line 249
    const-string v1, ", error="

    #@4b
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4e
    move-result-object v1

    #@4f
    iget-object v2, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mErrorMessage:Ljava/lang/CharSequence;

    #@51
    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/CharSequence;)Ljava/lang/StringBuilder;

    #@54
    .line 250
    const-string v1, "}"

    #@56
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@59
    .line 251
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@5c
    move-result-object v1

    #@5d
    return-object v1
.end method

.method public writeToParcel(Landroid/os/Parcel;I)V
    .locals 2
    .param p1, "dest"    # Landroid/os/Parcel;
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 261
    iget v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mState:I

    #@2
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeInt(I)V

    #@5
    .line 262
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mPosition:J

    #@7
    invoke-virtual {p1, v0, v1}, Landroid/os/Parcel;->writeLong(J)V

    #@a
    .line 263
    iget v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mSpeed:F

    #@c
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeFloat(F)V

    #@f
    .line 264
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mUpdateTime:J

    #@11
    invoke-virtual {p1, v0, v1}, Landroid/os/Parcel;->writeLong(J)V

    #@14
    .line 265
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mBufferedPosition:J

    #@16
    invoke-virtual {p1, v0, v1}, Landroid/os/Parcel;->writeLong(J)V

    #@19
    .line 266
    iget-wide v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mActions:J

    #@1b
    invoke-virtual {p1, v0, v1}, Landroid/os/Parcel;->writeLong(J)V

    #@1e
    .line 267
    iget-object v0, p0, Landroid/support/v4/media/session/PlaybackStateCompat;->mErrorMessage:Ljava/lang/CharSequence;

    #@20
    invoke-static {v0, p1, p2}, Landroid/text/TextUtils;->writeToParcel(Ljava/lang/CharSequence;Landroid/os/Parcel;I)V

    #@23
    .line 268
    return-void
.end method

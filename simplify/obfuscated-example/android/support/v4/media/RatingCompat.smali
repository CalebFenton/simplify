.class public final Landroid/support/v4/media/RatingCompat;
.super Ljava/lang/Object;
.source "RatingCompat.java"

# interfaces
.implements Landroid/os/Parcelable;


# static fields
.field public static final CREATOR:Landroid/os/Parcelable$Creator;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/os/Parcelable$Creator",
            "<",
            "Landroid/support/v4/media/RatingCompat;",
            ">;"
        }
    .end annotation
.end field

.field public static final RATING_3_STARS:I = 0x3

.field public static final RATING_4_STARS:I = 0x4

.field public static final RATING_5_STARS:I = 0x5

.field public static final RATING_HEART:I = 0x1

.field public static final RATING_NONE:I = 0x0

.field private static final RATING_NOT_RATED:F = -1.0f

.field public static final RATING_PERCENTAGE:I = 0x6

.field public static final RATING_THUMB_UP_DOWN:I = 0x2

.field private static final TAG:Ljava/lang/String; = "Rating"


# instance fields
.field private mRatingObj:Ljava/lang/Object;

.field private final mRatingStyle:I

.field private final mRatingValue:F


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 102
    new-instance v0, Landroid/support/v4/media/RatingCompat$1;

    #@2
    invoke-direct {v0}, Landroid/support/v4/media/RatingCompat$1;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/media/RatingCompat;->CREATOR:Landroid/os/Parcelable$Creator;

    #@7
    return-void
.end method

.method private constructor <init>(IF)V
    .locals 0
    .param p1, "ratingStyle"    # I
    .param p2, "rating"    # F

    #@0
    .prologue
    .line 80
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 81
    iput p1, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@5
    .line 82
    iput p2, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@7
    .line 83
    return-void
.end method

.method synthetic constructor <init>(IFLandroid/support/v4/media/RatingCompat$1;)V
    .locals 0
    .param p1, "x0"    # I
    .param p2, "x1"    # F
    .param p3, "x2"    # Landroid/support/v4/media/RatingCompat$1;

    #@0
    .prologue
    .line 32
    invoke-direct {p0, p1, p2}, Landroid/support/v4/media/RatingCompat;-><init>(IF)V

    #@3
    return-void
.end method

.method public static fromRating(Ljava/lang/Object;)Landroid/support/v4/media/RatingCompat;
    .locals 4
    .param p0, "ratingObj"    # Ljava/lang/Object;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 301
    if-eqz p0, :cond_0

    #@3
    sget v2, Landroid/os/Build$VERSION;->SDK_INT:I

    #@5
    const/16 v3, 0x15

    #@7
    if-ge v2, v3, :cond_1

    #@9
    .line 331
    :cond_0
    :goto_0
    return-object v0

    #@a
    .line 305
    :cond_1
    invoke-static {p0}, Landroid/support/v4/media/RatingCompatApi21;->getRatingStyle(Ljava/lang/Object;)I

    #@d
    move-result v1

    #@e
    .line 307
    .local v1, "ratingStyle":I
    invoke-static {p0}, Landroid/support/v4/media/RatingCompatApi21;->isRated(Ljava/lang/Object;)Z

    #@11
    move-result v2

    #@12
    if-eqz v2, :cond_2

    #@14
    .line 308
    packed-switch v1, :pswitch_data_0

    #@17
    goto :goto_0

    #@18
    .line 310
    :pswitch_0
    invoke-static {p0}, Landroid/support/v4/media/RatingCompatApi21;->hasHeart(Ljava/lang/Object;)Z

    #@1b
    move-result v2

    #@1c
    invoke-static {v2}, Landroid/support/v4/media/RatingCompat;->newHeartRating(Z)Landroid/support/v4/media/RatingCompat;

    #@1f
    move-result-object v0

    #@20
    .line 330
    .local v0, "rating":Landroid/support/v4/media/RatingCompat;
    :goto_1
    iput-object p0, v0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@22
    goto :goto_0

    #@23
    .line 313
    .end local v0    # "rating":Landroid/support/v4/media/RatingCompat;
    :pswitch_1
    invoke-static {p0}, Landroid/support/v4/media/RatingCompatApi21;->isThumbUp(Ljava/lang/Object;)Z

    #@26
    move-result v2

    #@27
    invoke-static {v2}, Landroid/support/v4/media/RatingCompat;->newThumbRating(Z)Landroid/support/v4/media/RatingCompat;

    #@2a
    move-result-object v0

    #@2b
    .line 314
    .restart local v0    # "rating":Landroid/support/v4/media/RatingCompat;
    goto :goto_1

    #@2c
    .line 318
    .end local v0    # "rating":Landroid/support/v4/media/RatingCompat;
    :pswitch_2
    invoke-static {p0}, Landroid/support/v4/media/RatingCompatApi21;->getStarRating(Ljava/lang/Object;)F

    #@2f
    move-result v2

    #@30
    invoke-static {v1, v2}, Landroid/support/v4/media/RatingCompat;->newStarRating(IF)Landroid/support/v4/media/RatingCompat;

    #@33
    move-result-object v0

    #@34
    .line 320
    .restart local v0    # "rating":Landroid/support/v4/media/RatingCompat;
    goto :goto_1

    #@35
    .line 322
    .end local v0    # "rating":Landroid/support/v4/media/RatingCompat;
    :pswitch_3
    invoke-static {p0}, Landroid/support/v4/media/RatingCompatApi21;->getPercentRating(Ljava/lang/Object;)F

    #@38
    move-result v2

    #@39
    invoke-static {v2}, Landroid/support/v4/media/RatingCompat;->newPercentageRating(F)Landroid/support/v4/media/RatingCompat;

    #@3c
    move-result-object v0

    #@3d
    .line 323
    .restart local v0    # "rating":Landroid/support/v4/media/RatingCompat;
    goto :goto_1

    #@3e
    .line 328
    .end local v0    # "rating":Landroid/support/v4/media/RatingCompat;
    :cond_2
    invoke-static {v1}, Landroid/support/v4/media/RatingCompat;->newUnratedRating(I)Landroid/support/v4/media/RatingCompat;

    #@41
    move-result-object v0

    #@42
    .restart local v0    # "rating":Landroid/support/v4/media/RatingCompat;
    goto :goto_1

    #@43
    .line 308
    nop

    #@44
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_3
    .end packed-switch
.end method

.method public static newHeartRating(Z)Landroid/support/v4/media/RatingCompat;
    .locals 3
    .param p0, "hasHeart"    # Z

    #@0
    .prologue
    .line 151
    new-instance v1, Landroid/support/v4/media/RatingCompat;

    #@2
    const/4 v2, 0x1

    #@3
    if-eqz p0, :cond_0

    #@5
    const/high16 v0, 0x3f800000    # 1.0f

    #@7
    :goto_0
    invoke-direct {v1, v2, v0}, Landroid/support/v4/media/RatingCompat;-><init>(IF)V

    #@a
    return-object v1

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public static newPercentageRating(F)Landroid/support/v4/media/RatingCompat;
    .locals 2
    .param p0, "percent"    # F

    #@0
    .prologue
    .line 208
    const/4 v0, 0x0

    #@1
    cmpg-float v0, p0, v0

    #@3
    if-ltz v0, :cond_0

    #@5
    const/high16 v0, 0x42c80000    # 100.0f

    #@7
    cmpl-float v0, p0, v0

    #@9
    if-lez v0, :cond_1

    #@b
    .line 209
    :cond_0
    const-string v0, "Rating"

    #@d
    const-string v1, "Invalid percentage-based rating value"

    #@f
    invoke-static {v0, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@12
    .line 210
    const/4 v0, 0x0

    #@13
    .line 212
    :goto_0
    return-object v0

    #@14
    :cond_1
    new-instance v0, Landroid/support/v4/media/RatingCompat;

    #@16
    const/4 v1, 0x6

    #@17
    invoke-direct {v0, v1, p0}, Landroid/support/v4/media/RatingCompat;-><init>(IF)V

    #@1a
    goto :goto_0
.end method

.method public static newStarRating(IF)Landroid/support/v4/media/RatingCompat;
    .locals 5
    .param p0, "starRatingStyle"    # I
    .param p1, "starRating"    # F

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 178
    const/high16 v0, -0x40800000    # -1.0f

    #@3
    .line 179
    .local v0, "maxRating":F
    packed-switch p0, :pswitch_data_0

    #@6
    .line 190
    const-string v2, "Rating"

    #@8
    new-instance v3, Ljava/lang/StringBuilder;

    #@a
    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    #@d
    const-string v4, "Invalid rating style ("

    #@f
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    move-result-object v3

    #@13
    invoke-virtual {v3, p0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@16
    move-result-object v3

    #@17
    const-string v4, ") for a star rating"

    #@19
    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v3

    #@1d
    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@20
    move-result-object v3

    #@21
    invoke-static {v2, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@24
    .line 197
    :goto_0
    return-object v1

    #@25
    .line 181
    :pswitch_0
    const/high16 v0, 0x40400000    # 3.0f

    #@27
    .line 193
    :goto_1
    const/4 v2, 0x0

    #@28
    cmpg-float v2, p1, v2

    #@2a
    if-ltz v2, :cond_0

    #@2c
    cmpl-float v2, p1, v0

    #@2e
    if-lez v2, :cond_1

    #@30
    .line 194
    :cond_0
    const-string v2, "Rating"

    #@32
    const-string v3, "Trying to set out of range star-based rating"

    #@34
    invoke-static {v2, v3}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;)I

    #@37
    goto :goto_0

    #@38
    .line 184
    :pswitch_1
    const/high16 v0, 0x40800000    # 4.0f

    #@3a
    .line 185
    goto :goto_1

    #@3b
    .line 187
    :pswitch_2
    const/high16 v0, 0x40a00000    # 5.0f

    #@3d
    .line 188
    goto :goto_1

    #@3e
    .line 197
    :cond_1
    new-instance v1, Landroid/support/v4/media/RatingCompat;

    #@40
    invoke-direct {v1, p0, p1}, Landroid/support/v4/media/RatingCompat;-><init>(IF)V

    #@43
    goto :goto_0

    #@44
    .line 179
    :pswitch_data_0
    .packed-switch 0x3
        :pswitch_0
        :pswitch_1
        :pswitch_2
    .end packed-switch
.end method

.method public static newThumbRating(Z)Landroid/support/v4/media/RatingCompat;
    .locals 3
    .param p0, "thumbIsUp"    # Z

    #@0
    .prologue
    .line 162
    new-instance v1, Landroid/support/v4/media/RatingCompat;

    #@2
    const/4 v2, 0x2

    #@3
    if-eqz p0, :cond_0

    #@5
    const/high16 v0, 0x3f800000    # 1.0f

    #@7
    :goto_0
    invoke-direct {v1, v2, v0}, Landroid/support/v4/media/RatingCompat;-><init>(IF)V

    #@a
    return-object v1

    #@b
    :cond_0
    const/4 v0, 0x0

    #@c
    goto :goto_0
.end method

.method public static newUnratedRating(I)Landroid/support/v4/media/RatingCompat;
    .locals 2
    .param p0, "ratingStyle"    # I

    #@0
    .prologue
    .line 130
    packed-switch p0, :pswitch_data_0

    #@3
    .line 139
    const/4 v0, 0x0

    #@4
    :goto_0
    return-object v0

    #@5
    .line 137
    :pswitch_0
    new-instance v0, Landroid/support/v4/media/RatingCompat;

    #@7
    const/high16 v1, -0x40800000    # -1.0f

    #@9
    invoke-direct {v0, p0, v1}, Landroid/support/v4/media/RatingCompat;-><init>(IF)V

    #@c
    goto :goto_0

    #@d
    .line 130
    nop

    #@e
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
        :pswitch_0
    .end packed-switch
.end method


# virtual methods
.method public describeContents()I
    .locals 1

    #@0
    .prologue
    .line 93
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@2
    return v0
.end method

.method public getPercentRating()F
    .locals 2

    #@0
    .prologue
    .line 284
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@2
    const/4 v1, 0x6

    #@3
    if-ne v0, v1, :cond_0

    #@5
    invoke-virtual {p0}, Landroid/support/v4/media/RatingCompat;->isRated()Z

    #@8
    move-result v0

    #@9
    if-nez v0, :cond_1

    #@b
    .line 285
    :cond_0
    const/high16 v0, -0x40800000    # -1.0f

    #@d
    .line 287
    :goto_0
    return v0

    #@e
    :cond_1
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@10
    goto :goto_0
.end method

.method public getRating()Ljava/lang/Object;
    .locals 2

    #@0
    .prologue
    .line 343
    iget-object v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@2
    if-nez v0, :cond_0

    #@4
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@6
    const/16 v1, 0x15

    #@8
    if-ge v0, v1, :cond_1

    #@a
    .line 344
    :cond_0
    iget-object v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@c
    .line 368
    :goto_0
    return-object v0

    #@d
    .line 347
    :cond_1
    invoke-virtual {p0}, Landroid/support/v4/media/RatingCompat;->isRated()Z

    #@10
    move-result v0

    #@11
    if-eqz v0, :cond_2

    #@13
    .line 348
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@15
    packed-switch v0, :pswitch_data_0

    #@18
    .line 363
    :goto_1
    const/4 v0, 0x0

    #@19
    goto :goto_0

    #@1a
    .line 350
    :pswitch_0
    invoke-virtual {p0}, Landroid/support/v4/media/RatingCompat;->hasHeart()Z

    #@1d
    move-result v0

    #@1e
    invoke-static {v0}, Landroid/support/v4/media/RatingCompatApi21;->newHeartRating(Z)Ljava/lang/Object;

    #@21
    move-result-object v0

    #@22
    iput-object v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@24
    .line 368
    :goto_2
    iget-object v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@26
    goto :goto_0

    #@27
    .line 353
    :pswitch_1
    invoke-virtual {p0}, Landroid/support/v4/media/RatingCompat;->isThumbUp()Z

    #@2a
    move-result v0

    #@2b
    invoke-static {v0}, Landroid/support/v4/media/RatingCompatApi21;->newThumbRating(Z)Ljava/lang/Object;

    #@2e
    move-result-object v0

    #@2f
    iput-object v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@31
    goto :goto_2

    #@32
    .line 358
    :pswitch_2
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@34
    invoke-virtual {p0}, Landroid/support/v4/media/RatingCompat;->getStarRating()F

    #@37
    move-result v1

    #@38
    invoke-static {v0, v1}, Landroid/support/v4/media/RatingCompatApi21;->newStarRating(IF)Ljava/lang/Object;

    #@3b
    move-result-object v0

    #@3c
    iput-object v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@3e
    goto :goto_2

    #@3f
    .line 361
    :pswitch_3
    invoke-virtual {p0}, Landroid/support/v4/media/RatingCompat;->getPercentRating()F

    #@42
    move-result v0

    #@43
    invoke-static {v0}, Landroid/support/v4/media/RatingCompatApi21;->newPercentageRating(F)Ljava/lang/Object;

    #@46
    move-result-object v0

    #@47
    iput-object v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@49
    goto :goto_1

    #@4a
    .line 366
    :cond_2
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@4c
    invoke-static {v0}, Landroid/support/v4/media/RatingCompatApi21;->newUnratedRating(I)Ljava/lang/Object;

    #@4f
    move-result-object v0

    #@50
    iput-object v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingObj:Ljava/lang/Object;

    #@52
    goto :goto_2

    #@53
    .line 348
    nop

    #@54
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_0
        :pswitch_1
        :pswitch_2
        :pswitch_2
        :pswitch_2
        :pswitch_3
    .end packed-switch
.end method

.method public getRatingStyle()I
    .locals 1

    #@0
    .prologue
    .line 231
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@2
    return v0
.end method

.method public getStarRating()F
    .locals 1

    #@0
    .prologue
    .line 266
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@2
    packed-switch v0, :pswitch_data_0

    #@5
    .line 274
    :cond_0
    const/high16 v0, -0x40800000    # -1.0f

    #@7
    :goto_0
    return v0

    #@8
    .line 270
    :pswitch_0
    invoke-virtual {p0}, Landroid/support/v4/media/RatingCompat;->isRated()Z

    #@b
    move-result v0

    #@c
    if-eqz v0, :cond_0

    #@e
    .line 271
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@10
    goto :goto_0

    #@11
    .line 266
    nop

    #@12
    :pswitch_data_0
    .packed-switch 0x3
        :pswitch_0
        :pswitch_0
        :pswitch_0
    .end packed-switch
.end method

.method public hasHeart()Z
    .locals 4

    #@0
    .prologue
    const/4 v0, 0x1

    #@1
    const/4 v1, 0x0

    #@2
    .line 240
    iget v2, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@4
    if-eq v2, v0, :cond_0

    #@6
    .line 243
    :goto_0
    return v1

    #@7
    :cond_0
    iget v2, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@9
    const/high16 v3, 0x3f800000    # 1.0f

    #@b
    cmpl-float v2, v2, v3

    #@d
    if-nez v2, :cond_1

    #@f
    :goto_1
    move v1, v0

    #@10
    goto :goto_0

    #@11
    :cond_1
    move v0, v1

    #@12
    goto :goto_1
.end method

.method public isRated()Z
    .locals 2

    #@0
    .prologue
    .line 221
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@2
    const/4 v1, 0x0

    #@3
    cmpl-float v0, v0, v1

    #@5
    if-ltz v0, :cond_0

    #@7
    const/4 v0, 0x1

    #@8
    :goto_0
    return v0

    #@9
    :cond_0
    const/4 v0, 0x0

    #@a
    goto :goto_0
.end method

.method public isThumbUp()Z
    .locals 3

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 253
    iget v1, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@3
    const/4 v2, 0x2

    #@4
    if-eq v1, v2, :cond_1

    #@6
    .line 256
    :cond_0
    :goto_0
    return v0

    #@7
    :cond_1
    iget v1, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@9
    const/high16 v2, 0x3f800000    # 1.0f

    #@b
    cmpl-float v1, v1, v2

    #@d
    if-nez v1, :cond_0

    #@f
    const/4 v0, 0x1

    #@10
    goto :goto_0
.end method

.method public toString()Ljava/lang/String;
    .locals 3

    #@0
    .prologue
    .line 87
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@5
    const-string v1, "Rating:style="

    #@7
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@a
    move-result-object v0

    #@b
    iget v1, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@d
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@10
    move-result-object v0

    #@11
    const-string v1, " rating="

    #@13
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@16
    move-result-object v1

    #@17
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@19
    const/4 v2, 0x0

    #@1a
    cmpg-float v0, v0, v2

    #@1c
    if-gez v0, :cond_0

    #@1e
    const-string v0, "unrated"

    #@20
    :goto_0
    invoke-virtual {v1, v0}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@23
    move-result-object v0

    #@24
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@27
    move-result-object v0

    #@28
    return-object v0

    #@29
    :cond_0
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@2b
    invoke-static {v0}, Ljava/lang/String;->valueOf(F)Ljava/lang/String;

    #@2e
    move-result-object v0

    #@2f
    goto :goto_0
.end method

.method public writeToParcel(Landroid/os/Parcel;I)V
    .locals 1
    .param p1, "dest"    # Landroid/os/Parcel;
    .param p2, "flags"    # I

    #@0
    .prologue
    .line 98
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingStyle:I

    #@2
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeInt(I)V

    #@5
    .line 99
    iget v0, p0, Landroid/support/v4/media/RatingCompat;->mRatingValue:F

    #@7
    invoke-virtual {p1, v0}, Landroid/os/Parcel;->writeFloat(F)V

    #@a
    .line 100
    return-void
.end method

.class Landroid/support/v4/media/RatingCompatApi21;
.super Ljava/lang/Object;
.source "RatingCompatApi21.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 21
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getPercentRating(Ljava/lang/Object;)F
    .locals 1
    .param p0, "ratingObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 63
    check-cast p0, Landroid/media/Rating;

    #@2
    .end local p0    # "ratingObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/Rating;->getPercentRating()F

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getRatingStyle(Ljava/lang/Object;)I
    .locals 1
    .param p0, "ratingObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 47
    check-cast p0, Landroid/media/Rating;

    #@2
    .end local p0    # "ratingObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/Rating;->getRatingStyle()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getStarRating(Ljava/lang/Object;)F
    .locals 1
    .param p0, "ratingObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 59
    check-cast p0, Landroid/media/Rating;

    #@2
    .end local p0    # "ratingObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/Rating;->getStarRating()F

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static hasHeart(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "ratingObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 51
    check-cast p0, Landroid/media/Rating;

    #@2
    .end local p0    # "ratingObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/Rating;->hasHeart()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isRated(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "ratingObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 43
    check-cast p0, Landroid/media/Rating;

    #@2
    .end local p0    # "ratingObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/Rating;->isRated()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isThumbUp(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "ratingObj"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 55
    check-cast p0, Landroid/media/Rating;

    #@2
    .end local p0    # "ratingObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/Rating;->isThumbUp()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static newHeartRating(Z)Ljava/lang/Object;
    .locals 1
    .param p0, "hasHeart"    # Z

    #@0
    .prologue
    .line 27
    invoke-static {p0}, Landroid/media/Rating;->newHeartRating(Z)Landroid/media/Rating;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static newPercentageRating(F)Ljava/lang/Object;
    .locals 1
    .param p0, "percent"    # F

    #@0
    .prologue
    .line 39
    invoke-static {p0}, Landroid/media/Rating;->newPercentageRating(F)Landroid/media/Rating;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static newStarRating(IF)Ljava/lang/Object;
    .locals 1
    .param p0, "starRatingStyle"    # I
    .param p1, "starRating"    # F

    #@0
    .prologue
    .line 35
    invoke-static {p0, p1}, Landroid/media/Rating;->newStarRating(IF)Landroid/media/Rating;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static newThumbRating(Z)Ljava/lang/Object;
    .locals 1
    .param p0, "thumbIsUp"    # Z

    #@0
    .prologue
    .line 31
    invoke-static {p0}, Landroid/media/Rating;->newThumbRating(Z)Landroid/media/Rating;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static newUnratedRating(I)Ljava/lang/Object;
    .locals 1
    .param p0, "ratingStyle"    # I

    #@0
    .prologue
    .line 23
    invoke-static {p0}, Landroid/media/Rating;->newUnratedRating(I)Landroid/media/Rating;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

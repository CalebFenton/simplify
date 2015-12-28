.class Landroid/support/v4/media/MediaMetadataCompatApi21;
.super Ljava/lang/Object;
.source "MediaMetadataCompatApi21.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/media/MediaMetadataCompatApi21$Builder;
    }
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 46
    return-void
.end method

.method public static getBitmap(Ljava/lang/Object;Ljava/lang/String;)Landroid/graphics/Bitmap;
    .locals 1
    .param p0, "metadataObj"    # Ljava/lang/Object;
    .param p1, "key"    # Ljava/lang/String;

    #@0
    .prologue
    .line 31
    check-cast p0, Landroid/media/MediaMetadata;

    #@2
    .end local p0    # "metadataObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaMetadata;->getBitmap(Ljava/lang/String;)Landroid/graphics/Bitmap;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getLong(Ljava/lang/Object;Ljava/lang/String;)J
    .locals 2
    .param p0, "metadataObj"    # Ljava/lang/Object;
    .param p1, "key"    # Ljava/lang/String;

    #@0
    .prologue
    .line 35
    check-cast p0, Landroid/media/MediaMetadata;

    #@2
    .end local p0    # "metadataObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaMetadata;->getLong(Ljava/lang/String;)J

    #@5
    move-result-wide v0

    #@6
    return-wide v0
.end method

.method public static getRating(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;
    .locals 1
    .param p0, "metadataObj"    # Ljava/lang/Object;
    .param p1, "key"    # Ljava/lang/String;

    #@0
    .prologue
    .line 39
    check-cast p0, Landroid/media/MediaMetadata;

    #@2
    .end local p0    # "metadataObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaMetadata;->getRating(Ljava/lang/String;)Landroid/media/Rating;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getText(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "metadataObj"    # Ljava/lang/Object;
    .param p1, "key"    # Ljava/lang/String;

    #@0
    .prologue
    .line 43
    check-cast p0, Landroid/media/MediaMetadata;

    #@2
    .end local p0    # "metadataObj":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/media/MediaMetadata;->getText(Ljava/lang/String;)Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static keySet(Ljava/lang/Object;)Ljava/util/Set;
    .locals 1
    .param p0, "metadataObj"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            ")",
            "Ljava/util/Set",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 27
    check-cast p0, Landroid/media/MediaMetadata;

    #@2
    .end local p0    # "metadataObj":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/media/MediaMetadata;->keySet()Ljava/util/Set;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

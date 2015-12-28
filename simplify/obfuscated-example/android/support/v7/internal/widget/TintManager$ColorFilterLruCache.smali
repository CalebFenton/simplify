.class Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;
.super Landroid/support/v4/util/LruCache;
.source "TintManager.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/TintManager;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "ColorFilterLruCache"
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "Landroid/support/v4/util/LruCache",
        "<",
        "Ljava/lang/Integer;",
        "Landroid/graphics/PorterDuffColorFilter;",
        ">;"
    }
.end annotation


# direct methods
.method public constructor <init>(I)V
    .locals 0
    .param p1, "maxSize"    # I

    #@0
    .prologue
    .line 350
    invoke-direct {p0, p1}, Landroid/support/v4/util/LruCache;-><init>(I)V

    #@3
    .line 351
    return-void
.end method

.method private static generateCacheKey(ILandroid/graphics/PorterDuff$Mode;)I
    .locals 3
    .param p0, "color"    # I
    .param p1, "mode"    # Landroid/graphics/PorterDuff$Mode;

    #@0
    .prologue
    .line 362
    const/4 v0, 0x1

    #@1
    .line 363
    .local v0, "hashCode":I
    add-int/lit8 v0, p0, 0x1f

    #@3
    .line 364
    mul-int/lit8 v1, v0, 0x1f

    #@5
    invoke-virtual {p1}, Landroid/graphics/PorterDuff$Mode;->hashCode()I

    #@8
    move-result v2

    #@9
    add-int v0, v1, v2

    #@b
    .line 365
    return v0
.end method


# virtual methods
.method get(ILandroid/graphics/PorterDuff$Mode;)Landroid/graphics/PorterDuffColorFilter;
    .locals 1
    .param p1, "color"    # I
    .param p2, "mode"    # Landroid/graphics/PorterDuff$Mode;

    #@0
    .prologue
    .line 354
    invoke-static {p1, p2}, Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;->generateCacheKey(ILandroid/graphics/PorterDuff$Mode;)I

    #@3
    move-result v0

    #@4
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;->get(Ljava/lang/Object;)Ljava/lang/Object;

    #@b
    move-result-object v0

    #@c
    check-cast v0, Landroid/graphics/PorterDuffColorFilter;

    #@e
    return-object v0
.end method

.method put(ILandroid/graphics/PorterDuff$Mode;Landroid/graphics/PorterDuffColorFilter;)Landroid/graphics/PorterDuffColorFilter;
    .locals 1
    .param p1, "color"    # I
    .param p2, "mode"    # Landroid/graphics/PorterDuff$Mode;
    .param p3, "filter"    # Landroid/graphics/PorterDuffColorFilter;

    #@0
    .prologue
    .line 358
    invoke-static {p1, p2}, Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;->generateCacheKey(ILandroid/graphics/PorterDuff$Mode;)I

    #@3
    move-result v0

    #@4
    invoke-static {v0}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;

    #@7
    move-result-object v0

    #@8
    invoke-virtual {p0, v0, p3}, Landroid/support/v7/internal/widget/TintManager$ColorFilterLruCache;->put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;

    #@b
    move-result-object v0

    #@c
    check-cast v0, Landroid/graphics/PorterDuffColorFilter;

    #@e
    return-object v0
.end method

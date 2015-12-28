.class Landroid/support/v4/print/PrintHelperKitkat;
.super Ljava/lang/Object;
.source "PrintHelperKitkat.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;
    }
.end annotation


# static fields
.field public static final COLOR_MODE_COLOR:I = 0x2

.field public static final COLOR_MODE_MONOCHROME:I = 0x1

.field private static final LOG_TAG:Ljava/lang/String; = "PrintHelperKitkat"

.field private static final MAX_PRINT_SIZE:I = 0xdac

.field public static final ORIENTATION_LANDSCAPE:I = 0x1

.field public static final ORIENTATION_PORTRAIT:I = 0x2

.field public static final SCALE_MODE_FILL:I = 0x2

.field public static final SCALE_MODE_FIT:I = 0x1


# instance fields
.field mColorMode:I

.field final mContext:Landroid/content/Context;

.field mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

.field private final mLock:Ljava/lang/Object;

.field mOrientation:I

.field mScaleMode:I


# direct methods
.method constructor <init>(Landroid/content/Context;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    const/4 v1, 0x2

    #@1
    .line 91
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@4
    .line 51
    const/4 v0, 0x0

    #@5
    iput-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@7
    .line 52
    new-instance v0, Ljava/lang/Object;

    #@9
    invoke-direct {v0}, Ljava/lang/Object;-><init>()V

    #@c
    iput-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mLock:Ljava/lang/Object;

    #@e
    .line 85
    iput v1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mScaleMode:I

    #@10
    .line 87
    iput v1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mColorMode:I

    #@12
    .line 89
    const/4 v0, 0x1

    #@13
    iput v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mOrientation:I

    #@15
    .line 92
    iput-object p1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mContext:Landroid/content/Context;

    #@17
    .line 93
    return-void
.end method

.method static synthetic access$000(Landroid/support/v4/print/PrintHelperKitkat;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/print/PrintHelperKitkat;
    .param p1, "x1"    # I
    .param p2, "x2"    # I
    .param p3, "x3"    # Landroid/graphics/RectF;
    .param p4, "x4"    # I

    #@0
    .prologue
    .line 46
    invoke-direct {p0, p1, p2, p3, p4}, Landroid/support/v4/print/PrintHelperKitkat;->getMatrix(IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static synthetic access$200(Landroid/support/v4/print/PrintHelperKitkat;Landroid/net/Uri;I)Landroid/graphics/Bitmap;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/print/PrintHelperKitkat;
    .param p1, "x1"    # Landroid/net/Uri;
    .param p2, "x2"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation

    #@0
    .prologue
    .line 46
    invoke-direct {p0, p1, p2}, Landroid/support/v4/print/PrintHelperKitkat;->loadConstrainedBitmap(Landroid/net/Uri;I)Landroid/graphics/Bitmap;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v4/print/PrintHelperKitkat;)Ljava/lang/Object;
    .locals 1
    .param p0, "x0"    # Landroid/support/v4/print/PrintHelperKitkat;

    #@0
    .prologue
    .line 46
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mLock:Ljava/lang/Object;

    #@2
    return-object v0
.end method

.method private getMatrix(IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
    .locals 7
    .param p1, "imageWidth"    # I
    .param p2, "imageHeight"    # I
    .param p3, "content"    # Landroid/graphics/RectF;
    .param p4, "fittingMode"    # I

    #@0
    .prologue
    const/high16 v6, 0x40000000    # 2.0f

    #@2
    .line 270
    new-instance v0, Landroid/graphics/Matrix;

    #@4
    invoke-direct {v0}, Landroid/graphics/Matrix;-><init>()V

    #@7
    .line 273
    .local v0, "matrix":Landroid/graphics/Matrix;
    invoke-virtual {p3}, Landroid/graphics/RectF;->width()F

    #@a
    move-result v4

    #@b
    int-to-float v5, p1

    #@c
    div-float v1, v4, v5

    #@e
    .line 274
    .local v1, "scale":F
    const/4 v4, 0x2

    #@f
    if-ne p4, v4, :cond_0

    #@11
    .line 275
    invoke-virtual {p3}, Landroid/graphics/RectF;->height()F

    #@14
    move-result v4

    #@15
    int-to-float v5, p2

    #@16
    div-float/2addr v4, v5

    #@17
    invoke-static {v1, v4}, Ljava/lang/Math;->max(FF)F

    #@1a
    move-result v1

    #@1b
    .line 279
    :goto_0
    invoke-virtual {v0, v1, v1}, Landroid/graphics/Matrix;->postScale(FF)Z

    #@1e
    .line 282
    invoke-virtual {p3}, Landroid/graphics/RectF;->width()F

    #@21
    move-result v4

    #@22
    int-to-float v5, p1

    #@23
    mul-float/2addr v5, v1

    #@24
    sub-float/2addr v4, v5

    #@25
    div-float v2, v4, v6

    #@27
    .line 284
    .local v2, "translateX":F
    invoke-virtual {p3}, Landroid/graphics/RectF;->height()F

    #@2a
    move-result v4

    #@2b
    int-to-float v5, p2

    #@2c
    mul-float/2addr v5, v1

    #@2d
    sub-float/2addr v4, v5

    #@2e
    div-float v3, v4, v6

    #@30
    .line 286
    .local v3, "translateY":F
    invoke-virtual {v0, v2, v3}, Landroid/graphics/Matrix;->postTranslate(FF)Z

    #@33
    .line 287
    return-object v0

    #@34
    .line 277
    .end local v2    # "translateX":F
    .end local v3    # "translateY":F
    :cond_0
    invoke-virtual {p3}, Landroid/graphics/RectF;->height()F

    #@37
    move-result v4

    #@38
    int-to-float v5, p2

    #@39
    div-float/2addr v4, v5

    #@3a
    invoke-static {v1, v4}, Ljava/lang/Math;->min(FF)F

    #@3d
    move-result v1

    #@3e
    goto :goto_0
.end method

.method private loadBitmap(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    .locals 5
    .param p1, "uri"    # Landroid/net/Uri;
    .param p2, "o"    # Landroid/graphics/BitmapFactory$Options;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation

    #@0
    .prologue
    .line 524
    if-eqz p1, :cond_0

    #@2
    iget-object v2, p0, Landroid/support/v4/print/PrintHelperKitkat;->mContext:Landroid/content/Context;

    #@4
    if-nez v2, :cond_1

    #@6
    .line 525
    :cond_0
    new-instance v2, Ljava/lang/IllegalArgumentException;

    #@8
    const-string v3, "bad argument to loadBitmap"

    #@a
    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@d
    throw v2

    #@e
    .line 527
    :cond_1
    const/4 v0, 0x0

    #@f
    .line 529
    .local v0, "is":Ljava/io/InputStream;
    :try_start_0
    iget-object v2, p0, Landroid/support/v4/print/PrintHelperKitkat;->mContext:Landroid/content/Context;

    #@11
    invoke-virtual {v2}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;

    #@14
    move-result-object v2

    #@15
    invoke-virtual {v2, p1}, Landroid/content/ContentResolver;->openInputStream(Landroid/net/Uri;)Ljava/io/InputStream;

    #@18
    move-result-object v0

    #@19
    .line 530
    const/4 v2, 0x0

    #@1a
    invoke-static {v0, v2, p2}, Landroid/graphics/BitmapFactory;->decodeStream(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@1d
    move-result-object v2

    #@1e
    .line 532
    if-eqz v0, :cond_2

    #@20
    .line 534
    :try_start_1
    invoke-virtual {v0}, Ljava/io/InputStream;->close()V
    :try_end_1
    .catch Ljava/io/IOException; {:try_start_1 .. :try_end_1} :catch_0

    #@23
    .line 537
    :cond_2
    :goto_0
    return-object v2

    #@24
    .line 535
    :catch_0
    move-exception v1

    #@25
    .line 536
    .local v1, "t":Ljava/io/IOException;
    const-string v3, "PrintHelperKitkat"

    #@27
    const-string v4, "close fail "

    #@29
    invoke-static {v3, v4, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@2c
    goto :goto_0

    #@2d
    .line 532
    .end local v1    # "t":Ljava/io/IOException;
    :catchall_0
    move-exception v2

    #@2e
    if-eqz v0, :cond_3

    #@30
    .line 534
    :try_start_2
    invoke-virtual {v0}, Ljava/io/InputStream;->close()V
    :try_end_2
    .catch Ljava/io/IOException; {:try_start_2 .. :try_end_2} :catch_1

    #@33
    .line 537
    :cond_3
    :goto_1
    throw v2

    #@34
    .line 535
    :catch_1
    move-exception v1

    #@35
    .line 536
    .restart local v1    # "t":Ljava/io/IOException;
    const-string v3, "PrintHelperKitkat"

    #@37
    const-string v4, "close fail "

    #@39
    invoke-static {v3, v4, v1}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@3c
    goto :goto_1
.end method

.method private loadConstrainedBitmap(Landroid/net/Uri;I)Landroid/graphics/Bitmap;
    .locals 9
    .param p1, "uri"    # Landroid/net/Uri;
    .param p2, "maxSideLength"    # I
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation

    #@0
    .prologue
    const/4 v8, 0x1

    #@1
    const/4 v6, 0x0

    #@2
    .line 474
    if-lez p2, :cond_0

    #@4
    if-eqz p1, :cond_0

    #@6
    iget-object v7, p0, Landroid/support/v4/print/PrintHelperKitkat;->mContext:Landroid/content/Context;

    #@8
    if-nez v7, :cond_1

    #@a
    .line 475
    :cond_0
    new-instance v6, Ljava/lang/IllegalArgumentException;

    #@c
    const-string v7, "bad argument to getScaledBitmap"

    #@e
    invoke-direct {v6, v7}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@11
    throw v6

    #@12
    .line 478
    :cond_1
    new-instance v3, Landroid/graphics/BitmapFactory$Options;

    #@14
    invoke-direct {v3}, Landroid/graphics/BitmapFactory$Options;-><init>()V

    #@17
    .line 479
    .local v3, "opt":Landroid/graphics/BitmapFactory$Options;
    iput-boolean v8, v3, Landroid/graphics/BitmapFactory$Options;->inJustDecodeBounds:Z

    #@19
    .line 480
    invoke-direct {p0, p1, v3}, Landroid/support/v4/print/PrintHelperKitkat;->loadBitmap(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;

    #@1c
    .line 482
    iget v5, v3, Landroid/graphics/BitmapFactory$Options;->outWidth:I

    #@1e
    .line 483
    .local v5, "w":I
    iget v1, v3, Landroid/graphics/BitmapFactory$Options;->outHeight:I

    #@20
    .line 486
    .local v1, "h":I
    if-lez v5, :cond_2

    #@22
    if-gtz v1, :cond_3

    #@24
    .line 515
    :cond_2
    :goto_0
    return-object v6

    #@25
    .line 491
    :cond_3
    invoke-static {v5, v1}, Ljava/lang/Math;->max(II)I

    #@28
    move-result v2

    #@29
    .line 493
    .local v2, "imageSide":I
    const/4 v4, 0x1

    #@2a
    .line 494
    .local v4, "sampleSize":I
    :goto_1
    if-le v2, p2, :cond_4

    #@2c
    .line 495
    ushr-int/lit8 v2, v2, 0x1

    #@2e
    .line 496
    shl-int/lit8 v4, v4, 0x1

    #@30
    goto :goto_1

    #@31
    .line 500
    :cond_4
    if-lez v4, :cond_2

    #@33
    invoke-static {v5, v1}, Ljava/lang/Math;->min(II)I

    #@36
    move-result v7

    #@37
    div-int/2addr v7, v4

    #@38
    if-lez v7, :cond_2

    #@3a
    .line 503
    const/4 v0, 0x0

    #@3b
    .line 504
    .local v0, "decodeOptions":Landroid/graphics/BitmapFactory$Options;
    iget-object v7, p0, Landroid/support/v4/print/PrintHelperKitkat;->mLock:Ljava/lang/Object;

    #@3d
    monitor-enter v7

    #@3e
    .line 505
    :try_start_0
    new-instance v6, Landroid/graphics/BitmapFactory$Options;

    #@40
    invoke-direct {v6}, Landroid/graphics/BitmapFactory$Options;-><init>()V

    #@43
    iput-object v6, p0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@45
    .line 506
    iget-object v6, p0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@47
    const/4 v8, 0x1

    #@48
    iput-boolean v8, v6, Landroid/graphics/BitmapFactory$Options;->inMutable:Z

    #@4a
    .line 507
    iget-object v6, p0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@4c
    iput v4, v6, Landroid/graphics/BitmapFactory$Options;->inSampleSize:I

    #@4e
    .line 508
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@50
    .line 509
    monitor-exit v7
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_1

    #@51
    .line 511
    :try_start_1
    invoke-direct {p0, p1, v0}, Landroid/support/v4/print/PrintHelperKitkat;->loadBitmap(Landroid/net/Uri;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_2

    #@54
    move-result-object v6

    #@55
    .line 513
    iget-object v7, p0, Landroid/support/v4/print/PrintHelperKitkat;->mLock:Ljava/lang/Object;

    #@57
    monitor-enter v7

    #@58
    .line 514
    const/4 v8, 0x0

    #@59
    :try_start_2
    iput-object v8, p0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@5b
    .line 515
    monitor-exit v7

    #@5c
    goto :goto_0

    #@5d
    :catchall_0
    move-exception v6

    #@5e
    monitor-exit v7
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0

    #@5f
    throw v6

    #@60
    .line 509
    :catchall_1
    move-exception v6

    #@61
    :try_start_3
    monitor-exit v7
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_1

    #@62
    throw v6

    #@63
    .line 513
    :catchall_2
    move-exception v6

    #@64
    iget-object v7, p0, Landroid/support/v4/print/PrintHelperKitkat;->mLock:Ljava/lang/Object;

    #@66
    monitor-enter v7

    #@67
    .line 514
    const/4 v8, 0x0

    #@68
    :try_start_4
    iput-object v8, p0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@6a
    .line 515
    monitor-exit v7
    :try_end_4
    .catchall {:try_start_4 .. :try_end_4} :catchall_3

    #@6b
    throw v6

    #@6c
    :catchall_3
    move-exception v6

    #@6d
    :try_start_5
    monitor-exit v7
    :try_end_5
    .catchall {:try_start_5 .. :try_end_5} :catchall_3

    #@6e
    throw v6
.end method


# virtual methods
.method public getColorMode()I
    .locals 1

    #@0
    .prologue
    .line 158
    iget v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mColorMode:I

    #@2
    return v0
.end method

.method public getOrientation()I
    .locals 1

    #@0
    .prologue
    .line 148
    iget v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mOrientation:I

    #@2
    return v0
.end method

.method public getScaleMode()I
    .locals 1

    #@0
    .prologue
    .line 116
    iget v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mScaleMode:I

    #@2
    return v0
.end method

.method public printBitmap(Ljava/lang/String;Landroid/graphics/Bitmap;Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;)V
    .locals 9
    .param p1, "jobName"    # Ljava/lang/String;
    .param p2, "bitmap"    # Landroid/graphics/Bitmap;
    .param p3, "callback"    # Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;

    #@0
    .prologue
    .line 170
    if-nez p2, :cond_0

    #@2
    .line 258
    :goto_0
    return-void

    #@3
    .line 173
    :cond_0
    iget v4, p0, Landroid/support/v4/print/PrintHelperKitkat;->mScaleMode:I

    #@5
    .line 174
    .local v4, "fittingMode":I
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat;->mContext:Landroid/content/Context;

    #@7
    const-string v1, "print"

    #@9
    invoke-virtual {v0, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@c
    move-result-object v8

    #@d
    check-cast v8, Landroid/print/PrintManager;

    #@f
    .line 175
    .local v8, "printManager":Landroid/print/PrintManager;
    sget-object v7, Landroid/print/PrintAttributes$MediaSize;->UNKNOWN_PORTRAIT:Landroid/print/PrintAttributes$MediaSize;

    #@11
    .line 176
    .local v7, "mediaSize":Landroid/print/PrintAttributes$MediaSize;
    invoke-virtual {p2}, Landroid/graphics/Bitmap;->getWidth()I

    #@14
    move-result v0

    #@15
    invoke-virtual {p2}, Landroid/graphics/Bitmap;->getHeight()I

    #@18
    move-result v1

    #@19
    if-le v0, v1, :cond_1

    #@1b
    .line 177
    sget-object v7, Landroid/print/PrintAttributes$MediaSize;->UNKNOWN_LANDSCAPE:Landroid/print/PrintAttributes$MediaSize;

    #@1d
    .line 179
    :cond_1
    new-instance v0, Landroid/print/PrintAttributes$Builder;

    #@1f
    invoke-direct {v0}, Landroid/print/PrintAttributes$Builder;-><init>()V

    #@22
    invoke-virtual {v0, v7}, Landroid/print/PrintAttributes$Builder;->setMediaSize(Landroid/print/PrintAttributes$MediaSize;)Landroid/print/PrintAttributes$Builder;

    #@25
    move-result-object v0

    #@26
    iget v1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mColorMode:I

    #@28
    invoke-virtual {v0, v1}, Landroid/print/PrintAttributes$Builder;->setColorMode(I)Landroid/print/PrintAttributes$Builder;

    #@2b
    move-result-object v0

    #@2c
    invoke-virtual {v0}, Landroid/print/PrintAttributes$Builder;->build()Landroid/print/PrintAttributes;

    #@2f
    move-result-object v6

    #@30
    .line 184
    .local v6, "attr":Landroid/print/PrintAttributes;
    new-instance v0, Landroid/support/v4/print/PrintHelperKitkat$1;

    #@32
    move-object v1, p0

    #@33
    move-object v2, p1

    #@34
    move-object v3, p2

    #@35
    move-object v5, p3

    #@36
    invoke-direct/range {v0 .. v5}, Landroid/support/v4/print/PrintHelperKitkat$1;-><init>(Landroid/support/v4/print/PrintHelperKitkat;Ljava/lang/String;Landroid/graphics/Bitmap;ILandroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;)V

    #@39
    invoke-virtual {v8, p1, v0, v6}, Landroid/print/PrintManager;->print(Ljava/lang/String;Landroid/print/PrintDocumentAdapter;Landroid/print/PrintAttributes;)Landroid/print/PrintJob;

    #@3c
    goto :goto_0
.end method

.method public printBitmap(Ljava/lang/String;Landroid/net/Uri;Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;)V
    .locals 9
    .param p1, "jobName"    # Ljava/lang/String;
    .param p2, "imageFile"    # Landroid/net/Uri;
    .param p3, "callback"    # Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation

    #@0
    .prologue
    .line 301
    iget v5, p0, Landroid/support/v4/print/PrintHelperKitkat;->mScaleMode:I

    #@2
    .line 303
    .local v5, "fittingMode":I
    new-instance v0, Landroid/support/v4/print/PrintHelperKitkat$2;

    #@4
    move-object v1, p0

    #@5
    move-object v2, p1

    #@6
    move-object v3, p2

    #@7
    move-object v4, p3

    #@8
    invoke-direct/range {v0 .. v5}, Landroid/support/v4/print/PrintHelperKitkat$2;-><init>(Landroid/support/v4/print/PrintHelperKitkat;Ljava/lang/String;Landroid/net/Uri;Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;I)V

    #@b
    .line 451
    .local v0, "printDocumentAdapter":Landroid/print/PrintDocumentAdapter;
    iget-object v1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mContext:Landroid/content/Context;

    #@d
    const-string v2, "print"

    #@f
    invoke-virtual {v1, v2}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@12
    move-result-object v8

    #@13
    check-cast v8, Landroid/print/PrintManager;

    #@15
    .line 452
    .local v8, "printManager":Landroid/print/PrintManager;
    new-instance v7, Landroid/print/PrintAttributes$Builder;

    #@17
    invoke-direct {v7}, Landroid/print/PrintAttributes$Builder;-><init>()V

    #@1a
    .line 453
    .local v7, "builder":Landroid/print/PrintAttributes$Builder;
    iget v1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mColorMode:I

    #@1c
    invoke-virtual {v7, v1}, Landroid/print/PrintAttributes$Builder;->setColorMode(I)Landroid/print/PrintAttributes$Builder;

    #@1f
    .line 455
    iget v1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mOrientation:I

    #@21
    const/4 v2, 0x1

    #@22
    if-ne v1, v2, :cond_1

    #@24
    .line 456
    sget-object v1, Landroid/print/PrintAttributes$MediaSize;->UNKNOWN_LANDSCAPE:Landroid/print/PrintAttributes$MediaSize;

    #@26
    invoke-virtual {v7, v1}, Landroid/print/PrintAttributes$Builder;->setMediaSize(Landroid/print/PrintAttributes$MediaSize;)Landroid/print/PrintAttributes$Builder;

    #@29
    .line 460
    :cond_0
    :goto_0
    invoke-virtual {v7}, Landroid/print/PrintAttributes$Builder;->build()Landroid/print/PrintAttributes;

    #@2c
    move-result-object v6

    #@2d
    .line 462
    .local v6, "attr":Landroid/print/PrintAttributes;
    invoke-virtual {v8, p1, v0, v6}, Landroid/print/PrintManager;->print(Ljava/lang/String;Landroid/print/PrintDocumentAdapter;Landroid/print/PrintAttributes;)Landroid/print/PrintJob;

    #@30
    .line 463
    return-void

    #@31
    .line 457
    .end local v6    # "attr":Landroid/print/PrintAttributes;
    :cond_1
    iget v1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mOrientation:I

    #@33
    const/4 v2, 0x2

    #@34
    if-ne v1, v2, :cond_0

    #@36
    .line 458
    sget-object v1, Landroid/print/PrintAttributes$MediaSize;->UNKNOWN_PORTRAIT:Landroid/print/PrintAttributes$MediaSize;

    #@38
    invoke-virtual {v7, v1}, Landroid/print/PrintAttributes$Builder;->setMediaSize(Landroid/print/PrintAttributes$MediaSize;)Landroid/print/PrintAttributes$Builder;

    #@3b
    goto :goto_0
.end method

.method public setColorMode(I)V
    .locals 0
    .param p1, "colorMode"    # I

    #@0
    .prologue
    .line 128
    iput p1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mColorMode:I

    #@2
    .line 129
    return-void
.end method

.method public setOrientation(I)V
    .locals 0
    .param p1, "orientation"    # I

    #@0
    .prologue
    .line 138
    iput p1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mOrientation:I

    #@2
    .line 139
    return-void
.end method

.method public setScaleMode(I)V
    .locals 0
    .param p1, "scaleMode"    # I

    #@0
    .prologue
    .line 106
    iput p1, p0, Landroid/support/v4/print/PrintHelperKitkat;->mScaleMode:I

    #@2
    .line 107
    return-void
.end method

.class Landroid/support/v4/print/PrintHelperKitkat$2;
.super Landroid/print/PrintDocumentAdapter;
.source "PrintHelperKitkat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Landroid/support/v4/print/PrintHelperKitkat;->printBitmap(Ljava/lang/String;Landroid/net/Uri;Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field loadBitmap:Landroid/os/AsyncTask;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/os/AsyncTask",
            "<",
            "Landroid/net/Uri;",
            "Ljava/lang/Boolean;",
            "Landroid/graphics/Bitmap;",
            ">;"
        }
    .end annotation
.end field

.field private mAttributes:Landroid/print/PrintAttributes;

.field mBitmap:Landroid/graphics/Bitmap;

.field final synthetic this$0:Landroid/support/v4/print/PrintHelperKitkat;

.field final synthetic val$callback:Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;

.field final synthetic val$fittingMode:I

.field final synthetic val$imageFile:Landroid/net/Uri;

.field final synthetic val$jobName:Ljava/lang/String;


# direct methods
.method constructor <init>(Landroid/support/v4/print/PrintHelperKitkat;Ljava/lang/String;Landroid/net/Uri;Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;I)V
    .locals 1

    #@0
    .prologue
    .line 303
    iput-object p1, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->this$0:Landroid/support/v4/print/PrintHelperKitkat;

    #@2
    iput-object p2, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->val$jobName:Ljava/lang/String;

    #@4
    iput-object p3, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->val$imageFile:Landroid/net/Uri;

    #@6
    iput-object p4, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->val$callback:Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;

    #@8
    iput p5, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->val$fittingMode:I

    #@a
    invoke-direct {p0}, Landroid/print/PrintDocumentAdapter;-><init>()V

    #@d
    .line 306
    const/4 v0, 0x0

    #@e
    iput-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->mBitmap:Landroid/graphics/Bitmap;

    #@10
    return-void
.end method

.method static synthetic access$100(Landroid/support/v4/print/PrintHelperKitkat$2;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v4/print/PrintHelperKitkat$2;

    #@0
    .prologue
    .line 303
    invoke-direct {p0}, Landroid/support/v4/print/PrintHelperKitkat$2;->cancelLoad()V

    #@3
    return-void
.end method

.method private cancelLoad()V
    .locals 3

    #@0
    .prologue
    .line 385
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->this$0:Landroid/support/v4/print/PrintHelperKitkat;

    #@2
    # getter for: Landroid/support/v4/print/PrintHelperKitkat;->mLock:Ljava/lang/Object;
    invoke-static {v0}, Landroid/support/v4/print/PrintHelperKitkat;->access$300(Landroid/support/v4/print/PrintHelperKitkat;)Ljava/lang/Object;

    #@5
    move-result-object v1

    #@6
    monitor-enter v1

    #@7
    .line 386
    :try_start_0
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->this$0:Landroid/support/v4/print/PrintHelperKitkat;

    #@9
    iget-object v0, v0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@b
    if-eqz v0, :cond_0

    #@d
    .line 387
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->this$0:Landroid/support/v4/print/PrintHelperKitkat;

    #@f
    iget-object v0, v0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@11
    invoke-virtual {v0}, Landroid/graphics/BitmapFactory$Options;->requestCancelDecode()V

    #@14
    .line 388
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->this$0:Landroid/support/v4/print/PrintHelperKitkat;

    #@16
    const/4 v2, 0x0

    #@17
    iput-object v2, v0, Landroid/support/v4/print/PrintHelperKitkat;->mDecodeOptions:Landroid/graphics/BitmapFactory$Options;

    #@19
    .line 390
    :cond_0
    monitor-exit v1

    #@1a
    .line 391
    return-void

    #@1b
    .line 390
    :catchall_0
    move-exception v0

    #@1c
    monitor-exit v1
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@1d
    throw v0
.end method


# virtual methods
.method public onFinish()V
    .locals 2

    #@0
    .prologue
    .line 395
    invoke-super {p0}, Landroid/print/PrintDocumentAdapter;->onFinish()V

    #@3
    .line 396
    invoke-direct {p0}, Landroid/support/v4/print/PrintHelperKitkat$2;->cancelLoad()V

    #@6
    .line 397
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->loadBitmap:Landroid/os/AsyncTask;

    #@8
    const/4 v1, 0x1

    #@9
    invoke-virtual {v0, v1}, Landroid/os/AsyncTask;->cancel(Z)Z

    #@c
    .line 398
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->val$callback:Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;

    #@e
    if-eqz v0, :cond_0

    #@10
    .line 399
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->val$callback:Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;

    #@12
    invoke-interface {v0}, Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;->onFinish()V

    #@15
    .line 401
    :cond_0
    return-void
.end method

.method public onLayout(Landroid/print/PrintAttributes;Landroid/print/PrintAttributes;Landroid/os/CancellationSignal;Landroid/print/PrintDocumentAdapter$LayoutResultCallback;Landroid/os/Bundle;)V
    .locals 9
    .param p1, "oldPrintAttributes"    # Landroid/print/PrintAttributes;
    .param p2, "newPrintAttributes"    # Landroid/print/PrintAttributes;
    .param p3, "cancellationSignal"    # Landroid/os/CancellationSignal;
    .param p4, "layoutResultCallback"    # Landroid/print/PrintDocumentAdapter$LayoutResultCallback;
    .param p5, "bundle"    # Landroid/os/Bundle;

    #@0
    .prologue
    const/4 v8, 0x0

    #@1
    const/4 v6, 0x1

    #@2
    .line 314
    invoke-virtual {p3}, Landroid/os/CancellationSignal;->isCanceled()Z

    #@5
    move-result v0

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 315
    invoke-virtual {p4}, Landroid/print/PrintDocumentAdapter$LayoutResultCallback;->onLayoutCancelled()V

    #@b
    .line 316
    iput-object p2, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->mAttributes:Landroid/print/PrintAttributes;

    #@d
    .line 382
    :goto_0
    return-void

    #@e
    .line 320
    :cond_0
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->mBitmap:Landroid/graphics/Bitmap;

    #@10
    if-eqz v0, :cond_2

    #@12
    .line 321
    new-instance v0, Landroid/print/PrintDocumentInfo$Builder;

    #@14
    iget-object v1, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->val$jobName:Ljava/lang/String;

    #@16
    invoke-direct {v0, v1}, Landroid/print/PrintDocumentInfo$Builder;-><init>(Ljava/lang/String;)V

    #@19
    invoke-virtual {v0, v6}, Landroid/print/PrintDocumentInfo$Builder;->setContentType(I)Landroid/print/PrintDocumentInfo$Builder;

    #@1c
    move-result-object v0

    #@1d
    invoke-virtual {v0, v6}, Landroid/print/PrintDocumentInfo$Builder;->setPageCount(I)Landroid/print/PrintDocumentInfo$Builder;

    #@20
    move-result-object v0

    #@21
    invoke-virtual {v0}, Landroid/print/PrintDocumentInfo$Builder;->build()Landroid/print/PrintDocumentInfo;

    #@24
    move-result-object v7

    #@25
    .line 325
    .local v7, "info":Landroid/print/PrintDocumentInfo;
    invoke-virtual {p2, p1}, Landroid/print/PrintAttributes;->equals(Ljava/lang/Object;)Z

    #@28
    move-result v0

    #@29
    if-nez v0, :cond_1

    #@2b
    .line 326
    .local v6, "changed":Z
    :goto_1
    invoke-virtual {p4, v7, v6}, Landroid/print/PrintDocumentAdapter$LayoutResultCallback;->onLayoutFinished(Landroid/print/PrintDocumentInfo;Z)V

    #@2e
    goto :goto_0

    #@2f
    .end local v6    # "changed":Z
    :cond_1
    move v6, v8

    #@30
    .line 325
    goto :goto_1

    #@31
    .line 330
    .end local v7    # "info":Landroid/print/PrintDocumentInfo;
    :cond_2
    new-instance v0, Landroid/support/v4/print/PrintHelperKitkat$2$1;

    #@33
    move-object v1, p0

    #@34
    move-object v2, p3

    #@35
    move-object v3, p2

    #@36
    move-object v4, p1

    #@37
    move-object v5, p4

    #@38
    invoke-direct/range {v0 .. v5}, Landroid/support/v4/print/PrintHelperKitkat$2$1;-><init>(Landroid/support/v4/print/PrintHelperKitkat$2;Landroid/os/CancellationSignal;Landroid/print/PrintAttributes;Landroid/print/PrintAttributes;Landroid/print/PrintDocumentAdapter$LayoutResultCallback;)V

    #@3b
    iput-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->loadBitmap:Landroid/os/AsyncTask;

    #@3d
    .line 379
    iget-object v0, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->loadBitmap:Landroid/os/AsyncTask;

    #@3f
    new-array v1, v8, [Landroid/net/Uri;

    #@41
    invoke-virtual {v0, v1}, Landroid/os/AsyncTask;->execute([Ljava/lang/Object;)Landroid/os/AsyncTask;

    #@44
    .line 381
    iput-object p2, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->mAttributes:Landroid/print/PrintAttributes;

    #@46
    goto :goto_0
.end method

.method public onWrite([Landroid/print/PageRange;Landroid/os/ParcelFileDescriptor;Landroid/os/CancellationSignal;Landroid/print/PrintDocumentAdapter$WriteResultCallback;)V
    .locals 9
    .param p1, "pageRanges"    # [Landroid/print/PageRange;
    .param p2, "fileDescriptor"    # Landroid/os/ParcelFileDescriptor;
    .param p3, "cancellationSignal"    # Landroid/os/CancellationSignal;
    .param p4, "writeResultCallback"    # Landroid/print/PrintDocumentAdapter$WriteResultCallback;

    #@0
    .prologue
    .line 407
    new-instance v4, Landroid/print/pdf/PrintedPdfDocument;

    #@2
    iget-object v5, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->this$0:Landroid/support/v4/print/PrintHelperKitkat;

    #@4
    iget-object v5, v5, Landroid/support/v4/print/PrintHelperKitkat;->mContext:Landroid/content/Context;

    #@6
    iget-object v6, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->mAttributes:Landroid/print/PrintAttributes;

    #@8
    invoke-direct {v4, v5, v6}, Landroid/print/pdf/PrintedPdfDocument;-><init>(Landroid/content/Context;Landroid/print/PrintAttributes;)V

    #@b
    .line 411
    .local v4, "pdfDocument":Landroid/print/pdf/PrintedPdfDocument;
    const/4 v5, 0x1

    #@c
    :try_start_0
    invoke-virtual {v4, v5}, Landroid/print/pdf/PrintedPdfDocument;->startPage(I)Landroid/graphics/pdf/PdfDocument$Page;

    #@f
    move-result-object v3

    #@10
    .line 412
    .local v3, "page":Landroid/graphics/pdf/PdfDocument$Page;
    new-instance v0, Landroid/graphics/RectF;

    #@12
    invoke-virtual {v3}, Landroid/graphics/pdf/PdfDocument$Page;->getInfo()Landroid/graphics/pdf/PdfDocument$PageInfo;

    #@15
    move-result-object v5

    #@16
    invoke-virtual {v5}, Landroid/graphics/pdf/PdfDocument$PageInfo;->getContentRect()Landroid/graphics/Rect;

    #@19
    move-result-object v5

    #@1a
    invoke-direct {v0, v5}, Landroid/graphics/RectF;-><init>(Landroid/graphics/Rect;)V

    #@1d
    .line 415
    .local v0, "content":Landroid/graphics/RectF;
    iget-object v5, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->this$0:Landroid/support/v4/print/PrintHelperKitkat;

    #@1f
    iget-object v6, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->mBitmap:Landroid/graphics/Bitmap;

    #@21
    invoke-virtual {v6}, Landroid/graphics/Bitmap;->getWidth()I

    #@24
    move-result v6

    #@25
    iget-object v7, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->mBitmap:Landroid/graphics/Bitmap;

    #@27
    invoke-virtual {v7}, Landroid/graphics/Bitmap;->getHeight()I

    #@2a
    move-result v7

    #@2b
    iget v8, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->val$fittingMode:I

    #@2d
    # invokes: Landroid/support/v4/print/PrintHelperKitkat;->getMatrix(IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;
    invoke-static {v5, v6, v7, v0, v8}, Landroid/support/v4/print/PrintHelperKitkat;->access$000(Landroid/support/v4/print/PrintHelperKitkat;IILandroid/graphics/RectF;I)Landroid/graphics/Matrix;

    #@30
    move-result-object v2

    #@31
    .line 419
    .local v2, "matrix":Landroid/graphics/Matrix;
    invoke-virtual {v3}, Landroid/graphics/pdf/PdfDocument$Page;->getCanvas()Landroid/graphics/Canvas;

    #@34
    move-result-object v5

    #@35
    iget-object v6, p0, Landroid/support/v4/print/PrintHelperKitkat$2;->mBitmap:Landroid/graphics/Bitmap;

    #@37
    const/4 v7, 0x0

    #@38
    invoke-virtual {v5, v6, v2, v7}, Landroid/graphics/Canvas;->drawBitmap(Landroid/graphics/Bitmap;Landroid/graphics/Matrix;Landroid/graphics/Paint;)V

    #@3b
    .line 422
    invoke-virtual {v4, v3}, Landroid/print/pdf/PrintedPdfDocument;->finishPage(Landroid/graphics/pdf/PdfDocument$Page;)V
    :try_end_0
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@3e
    .line 426
    :try_start_1
    new-instance v5, Ljava/io/FileOutputStream;

    #@40
    invoke-virtual {p2}, Landroid/os/ParcelFileDescriptor;->getFileDescriptor()Ljava/io/FileDescriptor;

    #@43
    move-result-object v6

    #@44
    invoke-direct {v5, v6}, Ljava/io/FileOutputStream;-><init>(Ljava/io/FileDescriptor;)V

    #@47
    invoke-virtual {v4, v5}, Landroid/print/pdf/PrintedPdfDocument;->writeTo(Ljava/io/OutputStream;)V

    #@4a
    .line 429
    const/4 v5, 0x1

    #@4b
    new-array v5, v5, [Landroid/print/PageRange;

    #@4d
    const/4 v6, 0x0

    #@4e
    sget-object v7, Landroid/print/PageRange;->ALL_PAGES:Landroid/print/PageRange;

    #@50
    aput-object v7, v5, v6

    #@52
    invoke-virtual {p4, v5}, Landroid/print/PrintDocumentAdapter$WriteResultCallback;->onWriteFinished([Landroid/print/PageRange;)V
    :try_end_1
    .catch Ljava/io/IOException; {:try_start_1 .. :try_end_1} :catch_0
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@55
    .line 437
    :goto_0
    if-eqz v4, :cond_0

    #@57
    .line 438
    invoke-virtual {v4}, Landroid/print/pdf/PrintedPdfDocument;->close()V

    #@5a
    .line 440
    :cond_0
    if-eqz p2, :cond_1

    #@5c
    .line 442
    :try_start_2
    invoke-virtual {p2}, Landroid/os/ParcelFileDescriptor;->close()V
    :try_end_2
    .catch Ljava/io/IOException; {:try_start_2 .. :try_end_2} :catch_1

    #@5f
    .line 448
    :cond_1
    :goto_1
    return-void

    #@60
    .line 431
    :catch_0
    move-exception v1

    #@61
    .line 433
    .local v1, "ioe":Ljava/io/IOException;
    :try_start_3
    const-string v5, "PrintHelperKitkat"

    #@63
    const-string v6, "Error writing printed content"

    #@65
    invoke-static {v5, v6, v1}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@68
    .line 434
    const/4 v5, 0x0

    #@69
    invoke-virtual {p4, v5}, Landroid/print/PrintDocumentAdapter$WriteResultCallback;->onWriteFailed(Ljava/lang/CharSequence;)V
    :try_end_3
    .catchall {:try_start_3 .. :try_end_3} :catchall_0

    #@6c
    goto :goto_0

    #@6d
    .line 437
    .end local v0    # "content":Landroid/graphics/RectF;
    .end local v1    # "ioe":Ljava/io/IOException;
    .end local v2    # "matrix":Landroid/graphics/Matrix;
    .end local v3    # "page":Landroid/graphics/pdf/PdfDocument$Page;
    :catchall_0
    move-exception v5

    #@6e
    if-eqz v4, :cond_2

    #@70
    .line 438
    invoke-virtual {v4}, Landroid/print/pdf/PrintedPdfDocument;->close()V

    #@73
    .line 440
    :cond_2
    if-eqz p2, :cond_3

    #@75
    .line 442
    :try_start_4
    invoke-virtual {p2}, Landroid/os/ParcelFileDescriptor;->close()V
    :try_end_4
    .catch Ljava/io/IOException; {:try_start_4 .. :try_end_4} :catch_2

    #@78
    .line 445
    :cond_3
    :goto_2
    throw v5

    #@79
    .line 443
    .restart local v0    # "content":Landroid/graphics/RectF;
    .restart local v2    # "matrix":Landroid/graphics/Matrix;
    .restart local v3    # "page":Landroid/graphics/pdf/PdfDocument$Page;
    :catch_1
    move-exception v5

    #@7a
    goto :goto_1

    #@7b
    .end local v0    # "content":Landroid/graphics/RectF;
    .end local v2    # "matrix":Landroid/graphics/Matrix;
    .end local v3    # "page":Landroid/graphics/pdf/PdfDocument$Page;
    :catch_2
    move-exception v6

    #@7c
    goto :goto_2
.end method

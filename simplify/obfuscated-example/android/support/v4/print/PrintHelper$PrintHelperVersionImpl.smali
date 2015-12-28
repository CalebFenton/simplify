.class interface abstract Landroid/support/v4/print/PrintHelper$PrintHelperVersionImpl;
.super Ljava/lang/Object;
.source "PrintHelper.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/print/PrintHelper;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x608
    name = "PrintHelperVersionImpl"
.end annotation


# virtual methods
.method public abstract getColorMode()I
.end method

.method public abstract getOrientation()I
.end method

.method public abstract getScaleMode()I
.end method

.method public abstract printBitmap(Ljava/lang/String;Landroid/graphics/Bitmap;Landroid/support/v4/print/PrintHelper$OnPrintFinishCallback;)V
.end method

.method public abstract printBitmap(Ljava/lang/String;Landroid/net/Uri;Landroid/support/v4/print/PrintHelper$OnPrintFinishCallback;)V
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation
.end method

.method public abstract setColorMode(I)V
.end method

.method public abstract setOrientation(I)V
.end method

.method public abstract setScaleMode(I)V
.end method

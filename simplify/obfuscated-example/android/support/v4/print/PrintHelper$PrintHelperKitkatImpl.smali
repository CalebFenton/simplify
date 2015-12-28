.class final Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;
.super Ljava/lang/Object;
.source "PrintHelper.java"

# interfaces
.implements Landroid/support/v4/print/PrintHelper$PrintHelperVersionImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/print/PrintHelper;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1a
    name = "PrintHelperKitkatImpl"
.end annotation


# instance fields
.field private final mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;


# direct methods
.method constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 159
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 160
    new-instance v0, Landroid/support/v4/print/PrintHelperKitkat;

    #@5
    invoke-direct {v0, p1}, Landroid/support/v4/print/PrintHelperKitkat;-><init>(Landroid/content/Context;)V

    #@8
    iput-object v0, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@a
    .line 161
    return-void
.end method


# virtual methods
.method public getColorMode()I
    .locals 1

    #@0
    .prologue
    .line 180
    iget-object v0, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/print/PrintHelperKitkat;->getColorMode()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getOrientation()I
    .locals 1

    #@0
    .prologue
    .line 190
    iget-object v0, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/print/PrintHelperKitkat;->getOrientation()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getScaleMode()I
    .locals 1

    #@0
    .prologue
    .line 170
    iget-object v0, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@2
    invoke-virtual {v0}, Landroid/support/v4/print/PrintHelperKitkat;->getScaleMode()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public printBitmap(Ljava/lang/String;Landroid/graphics/Bitmap;Landroid/support/v4/print/PrintHelper$OnPrintFinishCallback;)V
    .locals 2
    .param p1, "jobName"    # Ljava/lang/String;
    .param p2, "bitmap"    # Landroid/graphics/Bitmap;
    .param p3, "callback"    # Landroid/support/v4/print/PrintHelper$OnPrintFinishCallback;

    #@0
    .prologue
    .line 196
    const/4 v0, 0x0

    #@1
    .line 197
    .local v0, "delegateCallback":Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;
    if-eqz p3, :cond_0

    #@3
    .line 198
    new-instance v0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl$1;

    #@5
    .end local v0    # "delegateCallback":Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;
    invoke-direct {v0, p0, p3}, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl$1;-><init>(Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;Landroid/support/v4/print/PrintHelper$OnPrintFinishCallback;)V

    #@8
    .line 205
    .restart local v0    # "delegateCallback":Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;
    :cond_0
    iget-object v1, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@a
    invoke-virtual {v1, p1, p2, v0}, Landroid/support/v4/print/PrintHelperKitkat;->printBitmap(Ljava/lang/String;Landroid/graphics/Bitmap;Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;)V

    #@d
    .line 206
    return-void
.end method

.method public printBitmap(Ljava/lang/String;Landroid/net/Uri;Landroid/support/v4/print/PrintHelper$OnPrintFinishCallback;)V
    .locals 2
    .param p1, "jobName"    # Ljava/lang/String;
    .param p2, "imageFile"    # Landroid/net/Uri;
    .param p3, "callback"    # Landroid/support/v4/print/PrintHelper$OnPrintFinishCallback;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Ljava/io/FileNotFoundException;
        }
    .end annotation

    #@0
    .prologue
    .line 211
    const/4 v0, 0x0

    #@1
    .line 212
    .local v0, "delegateCallback":Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;
    if-eqz p3, :cond_0

    #@3
    .line 213
    new-instance v0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl$2;

    #@5
    .end local v0    # "delegateCallback":Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;
    invoke-direct {v0, p0, p3}, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl$2;-><init>(Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;Landroid/support/v4/print/PrintHelper$OnPrintFinishCallback;)V

    #@8
    .line 220
    .restart local v0    # "delegateCallback":Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;
    :cond_0
    iget-object v1, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@a
    invoke-virtual {v1, p1, p2, v0}, Landroid/support/v4/print/PrintHelperKitkat;->printBitmap(Ljava/lang/String;Landroid/net/Uri;Landroid/support/v4/print/PrintHelperKitkat$OnPrintFinishCallback;)V

    #@d
    .line 221
    return-void
.end method

.method public setColorMode(I)V
    .locals 1
    .param p1, "colorMode"    # I

    #@0
    .prologue
    .line 175
    iget-object v0, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/print/PrintHelperKitkat;->setColorMode(I)V

    #@5
    .line 176
    return-void
.end method

.method public setOrientation(I)V
    .locals 1
    .param p1, "orientation"    # I

    #@0
    .prologue
    .line 185
    iget-object v0, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/print/PrintHelperKitkat;->setOrientation(I)V

    #@5
    .line 186
    return-void
.end method

.method public setScaleMode(I)V
    .locals 1
    .param p1, "scaleMode"    # I

    #@0
    .prologue
    .line 165
    iget-object v0, p0, Landroid/support/v4/print/PrintHelper$PrintHelperKitkatImpl;->mPrintHelper:Landroid/support/v4/print/PrintHelperKitkat;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v4/print/PrintHelperKitkat;->setScaleMode(I)V

    #@5
    .line 166
    return-void
.end method

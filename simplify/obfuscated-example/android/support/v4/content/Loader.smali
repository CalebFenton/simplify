.class public Landroid/support/v4/content/Loader;
.super Ljava/lang/Object;
.source "Loader.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/content/Loader$OnLoadCompleteListener;,
        Landroid/support/v4/content/Loader$ForceLoadContentObserver;
    }
.end annotation

.annotation system Ldalvik/annotation/Signature;
    value = {
        "<D:",
        "Ljava/lang/Object;",
        ">",
        "Ljava/lang/Object;"
    }
.end annotation


# instance fields
.field mAbandoned:Z

.field mContentChanged:Z

.field mContext:Landroid/content/Context;

.field mId:I

.field mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/support/v4/content/Loader$OnLoadCompleteListener",
            "<TD;>;"
        }
    .end annotation
.end field

.field mProcessingChange:Z

.field mReset:Z

.field mStarted:Z


# direct methods
.method public constructor <init>(Landroid/content/Context;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    const/4 v1, 0x0

    #@1
    .line 91
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@4
    .line 38
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mStarted:Z

    #@6
    .line 39
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mAbandoned:Z

    #@8
    .line 40
    const/4 v0, 0x1

    #@9
    iput-boolean v0, p0, Landroid/support/v4/content/Loader;->mReset:Z

    #@b
    .line 41
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mContentChanged:Z

    #@d
    .line 42
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mProcessingChange:Z

    #@f
    .line 92
    invoke-virtual {p1}, Landroid/content/Context;->getApplicationContext()Landroid/content/Context;

    #@12
    move-result-object v0

    #@13
    iput-object v0, p0, Landroid/support/v4/content/Loader;->mContext:Landroid/content/Context;

    #@15
    .line 93
    return-void
.end method


# virtual methods
.method public abandon()V
    .locals 1

    #@0
    .prologue
    .line 262
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    const/4 v0, 0x1

    #@1
    iput-boolean v0, p0, Landroid/support/v4/content/Loader;->mAbandoned:Z

    #@3
    .line 263
    invoke-virtual {p0}, Landroid/support/v4/content/Loader;->onAbandon()V

    #@6
    .line 264
    return-void
.end method

.method public commitContentChanged()V
    .locals 1

    #@0
    .prologue
    .line 328
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    const/4 v0, 0x0

    #@1
    iput-boolean v0, p0, Landroid/support/v4/content/Loader;->mProcessingChange:Z

    #@3
    .line 329
    return-void
.end method

.method public dataToString(Ljava/lang/Object;)Ljava/lang/String;
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TD;)",
            "Ljava/lang/String;"
        }
    .end annotation

    #@0
    .prologue
    .line 368
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    .local p1, "data":Ljava/lang/Object;, "TD;"
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    const/16 v1, 0x40

    #@4
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(I)V

    #@7
    .line 369
    .local v0, "sb":Ljava/lang/StringBuilder;
    invoke-static {p1, v0}, Landroid/support/v4/util/DebugUtils;->buildShortClassTag(Ljava/lang/Object;Ljava/lang/StringBuilder;)V

    #@a
    .line 370
    const-string v1, "}"

    #@c
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@f
    .line 371
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@12
    move-result-object v1

    #@13
    return-object v1
.end method

.method public deliverResult(Ljava/lang/Object;)V
    .locals 1
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(TD;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 103
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    .local p1, "data":Ljava/lang/Object;, "TD;"
    iget-object v0, p0, Landroid/support/v4/content/Loader;->mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 104
    iget-object v0, p0, Landroid/support/v4/content/Loader;->mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;

    #@6
    invoke-interface {v0, p0, p1}, Landroid/support/v4/content/Loader$OnLoadCompleteListener;->onLoadComplete(Landroid/support/v4/content/Loader;Ljava/lang/Object;)V

    #@9
    .line 106
    :cond_0
    return-void
.end method

.method public dump(Ljava/lang/String;Ljava/io/FileDescriptor;Ljava/io/PrintWriter;[Ljava/lang/String;)V
    .locals 1
    .param p1, "prefix"    # Ljava/lang/String;
    .param p2, "fd"    # Ljava/io/FileDescriptor;
    .param p3, "writer"    # Ljava/io/PrintWriter;
    .param p4, "args"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 393
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@3
    const-string v0, "mId="

    #@5
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@8
    iget v0, p0, Landroid/support/v4/content/Loader;->mId:I

    #@a
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(I)V

    #@d
    .line 394
    const-string v0, " mListener="

    #@f
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@12
    iget-object v0, p0, Landroid/support/v4/content/Loader;->mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;

    #@14
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Ljava/lang/Object;)V

    #@17
    .line 395
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mStarted:Z

    #@19
    if-nez v0, :cond_0

    #@1b
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mContentChanged:Z

    #@1d
    if-nez v0, :cond_0

    #@1f
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mProcessingChange:Z

    #@21
    if-eqz v0, :cond_1

    #@23
    .line 396
    :cond_0
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@26
    const-string v0, "mStarted="

    #@28
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@2b
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mStarted:Z

    #@2d
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@30
    .line 397
    const-string v0, " mContentChanged="

    #@32
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@35
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mContentChanged:Z

    #@37
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@3a
    .line 398
    const-string v0, " mProcessingChange="

    #@3c
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@3f
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mProcessingChange:Z

    #@41
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Z)V

    #@44
    .line 400
    :cond_1
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mAbandoned:Z

    #@46
    if-nez v0, :cond_2

    #@48
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mReset:Z

    #@4a
    if-eqz v0, :cond_3

    #@4c
    .line 401
    :cond_2
    invoke-virtual {p3, p1}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@4f
    const-string v0, "mAbandoned="

    #@51
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@54
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mAbandoned:Z

    #@56
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Z)V

    #@59
    .line 402
    const-string v0, " mReset="

    #@5b
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->print(Ljava/lang/String;)V

    #@5e
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mReset:Z

    #@60
    invoke-virtual {p3, v0}, Ljava/io/PrintWriter;->println(Z)V

    #@63
    .line 404
    :cond_3
    return-void
.end method

.method public forceLoad()V
    .locals 0

    #@0
    .prologue
    .line 218
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    invoke-virtual {p0}, Landroid/support/v4/content/Loader;->onForceLoad()V

    #@3
    .line 219
    return-void
.end method

.method public getContext()Landroid/content/Context;
    .locals 1

    #@0
    .prologue
    .line 112
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    iget-object v0, p0, Landroid/support/v4/content/Loader;->mContext:Landroid/content/Context;

    #@2
    return-object v0
.end method

.method public getId()I
    .locals 1

    #@0
    .prologue
    .line 119
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    iget v0, p0, Landroid/support/v4/content/Loader;->mId:I

    #@2
    return v0
.end method

.method public isAbandoned()Z
    .locals 1

    #@0
    .prologue
    .line 167
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mAbandoned:Z

    #@2
    return v0
.end method

.method public isReset()Z
    .locals 1

    #@0
    .prologue
    .line 176
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mReset:Z

    #@2
    return v0
.end method

.method public isStarted()Z
    .locals 1

    #@0
    .prologue
    .line 158
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mStarted:Z

    #@2
    return v0
.end method

.method protected onAbandon()V
    .locals 0

    #@0
    .prologue
    .line 276
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    return-void
.end method

.method public onContentChanged()V
    .locals 1

    #@0
    .prologue
    .line 353
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mStarted:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 354
    invoke-virtual {p0}, Landroid/support/v4/content/Loader;->forceLoad()V

    #@7
    .line 361
    :goto_0
    return-void

    #@8
    .line 359
    :cond_0
    const/4 v0, 0x1

    #@9
    iput-boolean v0, p0, Landroid/support/v4/content/Loader;->mContentChanged:Z

    #@b
    goto :goto_0
.end method

.method protected onForceLoad()V
    .locals 0

    #@0
    .prologue
    .line 226
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    return-void
.end method

.method protected onReset()V
    .locals 0

    #@0
    .prologue
    .line 306
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    return-void
.end method

.method protected onStartLoading()V
    .locals 0

    #@0
    .prologue
    .line 207
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    return-void
.end method

.method protected onStopLoading()V
    .locals 0

    #@0
    .prologue
    .line 254
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    return-void
.end method

.method public registerListener(ILandroid/support/v4/content/Loader$OnLoadCompleteListener;)V
    .locals 2
    .param p1, "id"    # I
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(I",
            "Landroid/support/v4/content/Loader$OnLoadCompleteListener",
            "<TD;>;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 130
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    .local p2, "listener":Landroid/support/v4/content/Loader$OnLoadCompleteListener;, "Landroid/support/v4/content/Loader$OnLoadCompleteListener<TD;>;"
    iget-object v0, p0, Landroid/support/v4/content/Loader;->mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 131
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "There is already a listener registered"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 133
    :cond_0
    iput-object p2, p0, Landroid/support/v4/content/Loader;->mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;

    #@e
    .line 134
    iput p1, p0, Landroid/support/v4/content/Loader;->mId:I

    #@10
    .line 135
    return-void
.end method

.method public reset()V
    .locals 2

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    const/4 v1, 0x0

    #@1
    .line 291
    invoke-virtual {p0}, Landroid/support/v4/content/Loader;->onReset()V

    #@4
    .line 292
    const/4 v0, 0x1

    #@5
    iput-boolean v0, p0, Landroid/support/v4/content/Loader;->mReset:Z

    #@7
    .line 293
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mStarted:Z

    #@9
    .line 294
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mAbandoned:Z

    #@b
    .line 295
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mContentChanged:Z

    #@d
    .line 296
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mProcessingChange:Z

    #@f
    .line 297
    return-void
.end method

.method public rollbackContentChanged()V
    .locals 1

    #@0
    .prologue
    .line 339
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mProcessingChange:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 340
    const/4 v0, 0x1

    #@5
    iput-boolean v0, p0, Landroid/support/v4/content/Loader;->mContentChanged:Z

    #@7
    .line 342
    :cond_0
    return-void
.end method

.method public final startLoading()V
    .locals 2

    #@0
    .prologue
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    const/4 v1, 0x0

    #@1
    .line 195
    const/4 v0, 0x1

    #@2
    iput-boolean v0, p0, Landroid/support/v4/content/Loader;->mStarted:Z

    #@4
    .line 196
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mReset:Z

    #@6
    .line 197
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mAbandoned:Z

    #@8
    .line 198
    invoke-virtual {p0}, Landroid/support/v4/content/Loader;->onStartLoading()V

    #@b
    .line 199
    return-void
.end method

.method public stopLoading()V
    .locals 1

    #@0
    .prologue
    .line 243
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    const/4 v0, 0x0

    #@1
    iput-boolean v0, p0, Landroid/support/v4/content/Loader;->mStarted:Z

    #@3
    .line 244
    invoke-virtual {p0}, Landroid/support/v4/content/Loader;->onStopLoading()V

    #@6
    .line 245
    return-void
.end method

.method public takeContentChanged()Z
    .locals 2

    #@0
    .prologue
    .line 314
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    iget-boolean v0, p0, Landroid/support/v4/content/Loader;->mContentChanged:Z

    #@2
    .line 315
    .local v0, "res":Z
    const/4 v1, 0x0

    #@3
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mContentChanged:Z

    #@5
    .line 316
    iget-boolean v1, p0, Landroid/support/v4/content/Loader;->mProcessingChange:Z

    #@7
    or-int/2addr v1, v0

    #@8
    iput-boolean v1, p0, Landroid/support/v4/content/Loader;->mProcessingChange:Z

    #@a
    .line 317
    return v0
.end method

.method public toString()Ljava/lang/String;
    .locals 2

    #@0
    .prologue
    .line 376
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    const/16 v1, 0x40

    #@4
    invoke-direct {v0, v1}, Ljava/lang/StringBuilder;-><init>(I)V

    #@7
    .line 377
    .local v0, "sb":Ljava/lang/StringBuilder;
    invoke-static {p0, v0}, Landroid/support/v4/util/DebugUtils;->buildShortClassTag(Ljava/lang/Object;Ljava/lang/StringBuilder;)V

    #@a
    .line 378
    const-string v1, " id="

    #@c
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@f
    .line 379
    iget v1, p0, Landroid/support/v4/content/Loader;->mId:I

    #@11
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@14
    .line 380
    const-string v1, "}"

    #@16
    invoke-virtual {v0, v1}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@19
    .line 381
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@1c
    move-result-object v1

    #@1d
    return-object v1
.end method

.method public unregisterListener(Landroid/support/v4/content/Loader$OnLoadCompleteListener;)V
    .locals 2
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Landroid/support/v4/content/Loader$OnLoadCompleteListener",
            "<TD;>;)V"
        }
    .end annotation

    #@0
    .prologue
    .line 143
    .local p0, "this":Landroid/support/v4/content/Loader;, "Landroid/support/v4/content/Loader<TD;>;"
    .local p1, "listener":Landroid/support/v4/content/Loader$OnLoadCompleteListener;, "Landroid/support/v4/content/Loader$OnLoadCompleteListener<TD;>;"
    iget-object v0, p0, Landroid/support/v4/content/Loader;->mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 144
    new-instance v0, Ljava/lang/IllegalStateException;

    #@6
    const-string v1, "No listener register"

    #@8
    invoke-direct {v0, v1}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v0

    #@c
    .line 146
    :cond_0
    iget-object v0, p0, Landroid/support/v4/content/Loader;->mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;

    #@e
    if-eq v0, p1, :cond_1

    #@10
    .line 147
    new-instance v0, Ljava/lang/IllegalArgumentException;

    #@12
    const-string v1, "Attempting to unregister the wrong listener"

    #@14
    invoke-direct {v0, v1}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    #@17
    throw v0

    #@18
    .line 149
    :cond_1
    const/4 v0, 0x0

    #@19
    iput-object v0, p0, Landroid/support/v4/content/Loader;->mListener:Landroid/support/v4/content/Loader$OnLoadCompleteListener;

    #@1b
    .line 150
    return-void
.end method

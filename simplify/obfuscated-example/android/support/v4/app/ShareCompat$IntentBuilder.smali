.class public Landroid/support/v4/app/ShareCompat$IntentBuilder;
.super Ljava/lang/Object;
.source "ShareCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/ShareCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x9
    name = "IntentBuilder"
.end annotation


# instance fields
.field private mActivity:Landroid/app/Activity;

.field private mBccAddresses:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field private mCcAddresses:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field private mChooserTitle:Ljava/lang/CharSequence;

.field private mIntent:Landroid/content/Intent;

.field private mStreams:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/net/Uri;",
            ">;"
        }
    .end annotation
.end field

.field private mToAddresses:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field


# direct methods
.method private constructor <init>(Landroid/app/Activity;)V
    .locals 3
    .param p1, "launchingActivity"    # Landroid/app/Activity;

    #@0
    .prologue
    .line 279
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 280
    iput-object p1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mActivity:Landroid/app/Activity;

    #@5
    .line 281
    new-instance v0, Landroid/content/Intent;

    #@7
    invoke-direct {v0}, Landroid/content/Intent;-><init>()V

    #@a
    const-string v1, "android.intent.action.SEND"

    #@c
    invoke-virtual {v0, v1}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    #@f
    move-result-object v0

    #@10
    iput-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@12
    .line 282
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@14
    const-string v1, "android.support.v4.app.EXTRA_CALLING_PACKAGE"

    #@16
    invoke-virtual {p1}, Landroid/app/Activity;->getPackageName()Ljava/lang/String;

    #@19
    move-result-object v2

    #@1a
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@1d
    .line 283
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@1f
    const-string v1, "android.support.v4.app.EXTRA_CALLING_ACTIVITY"

    #@21
    invoke-virtual {p1}, Landroid/app/Activity;->getComponentName()Landroid/content/ComponentName;

    #@24
    move-result-object v2

    #@25
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;

    #@28
    .line 284
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2a
    const/high16 v1, 0x80000

    #@2c
    invoke-virtual {v0, v1}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    #@2f
    .line 285
    return-void
.end method

.method private combineArrayExtra(Ljava/lang/String;Ljava/util/ArrayList;)V
    .locals 5
    .param p1, "extra"    # Ljava/lang/String;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/String;",
            "Ljava/util/ArrayList",
            "<",
            "Ljava/lang/String;",
            ">;)V"
        }
    .end annotation

    #@0
    .prologue
    .local p2, "add":Ljava/util/ArrayList;, "Ljava/util/ArrayList<Ljava/lang/String;>;"
    const/4 v3, 0x0

    #@1
    .line 345
    iget-object v4, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@3
    invoke-virtual {v4, p1}, Landroid/content/Intent;->getStringArrayExtra(Ljava/lang/String;)[Ljava/lang/String;

    #@6
    move-result-object v0

    #@7
    .line 346
    .local v0, "currentAddresses":[Ljava/lang/String;
    if-eqz v0, :cond_1

    #@9
    array-length v1, v0

    #@a
    .line 347
    .local v1, "currentLength":I
    :goto_0
    invoke-virtual {p2}, Ljava/util/ArrayList;->size()I

    #@d
    move-result v4

    #@e
    add-int/2addr v4, v1

    #@f
    new-array v2, v4, [Ljava/lang/String;

    #@11
    .line 348
    .local v2, "finalAddresses":[Ljava/lang/String;
    invoke-virtual {p2, v2}, Ljava/util/ArrayList;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    #@14
    .line 349
    if-eqz v0, :cond_0

    #@16
    .line 350
    invoke-virtual {p2}, Ljava/util/ArrayList;->size()I

    #@19
    move-result v4

    #@1a
    invoke-static {v0, v3, v2, v4, v1}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@1d
    .line 352
    :cond_0
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@1f
    invoke-virtual {v3, p1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;[Ljava/lang/String;)Landroid/content/Intent;

    #@22
    .line 353
    return-void

    #@23
    .end local v1    # "currentLength":I
    .end local v2    # "finalAddresses":[Ljava/lang/String;
    :cond_1
    move v1, v3

    #@24
    .line 346
    goto :goto_0
.end method

.method private combineArrayExtra(Ljava/lang/String;[Ljava/lang/String;)V
    .locals 6
    .param p1, "extra"    # Ljava/lang/String;
    .param p2, "add"    # [Ljava/lang/String;

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    .line 357
    invoke-virtual {p0}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->getIntent()Landroid/content/Intent;

    #@4
    move-result-object v0

    #@5
    .line 358
    .local v0, "intent":Landroid/content/Intent;
    invoke-virtual {v0, p1}, Landroid/content/Intent;->getStringArrayExtra(Ljava/lang/String;)[Ljava/lang/String;

    #@8
    move-result-object v1

    #@9
    .line 359
    .local v1, "old":[Ljava/lang/String;
    if-eqz v1, :cond_1

    #@b
    array-length v2, v1

    #@c
    .line 360
    .local v2, "oldLength":I
    :goto_0
    array-length v5, p2

    #@d
    add-int/2addr v5, v2

    #@e
    new-array v3, v5, [Ljava/lang/String;

    #@10
    .line 361
    .local v3, "result":[Ljava/lang/String;
    if-eqz v1, :cond_0

    #@12
    invoke-static {v1, v4, v3, v4, v2}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@15
    .line 362
    :cond_0
    array-length v5, p2

    #@16
    invoke-static {p2, v4, v3, v2, v5}, Ljava/lang/System;->arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V

    #@19
    .line 363
    invoke-virtual {v0, p1, v3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;[Ljava/lang/String;)Landroid/content/Intent;

    #@1c
    .line 364
    return-void

    #@1d
    .end local v2    # "oldLength":I
    .end local v3    # "result":[Ljava/lang/String;
    :cond_1
    move v2, v4

    #@1e
    .line 359
    goto :goto_0
.end method

.method public static from(Landroid/app/Activity;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p0, "launchingActivity"    # Landroid/app/Activity;

    #@0
    .prologue
    .line 276
    new-instance v0, Landroid/support/v4/app/ShareCompat$IntentBuilder;

    #@2
    invoke-direct {v0, p0}, Landroid/support/v4/app/ShareCompat$IntentBuilder;-><init>(Landroid/app/Activity;)V

    #@5
    return-object v0
.end method


# virtual methods
.method public addEmailBcc(Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p1, "address"    # Ljava/lang/String;

    #@0
    .prologue
    .line 606
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mBccAddresses:Ljava/util/ArrayList;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 607
    new-instance v0, Ljava/util/ArrayList;

    #@6
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@9
    iput-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mBccAddresses:Ljava/util/ArrayList;

    #@b
    .line 609
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mBccAddresses:Ljava/util/ArrayList;

    #@d
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@10
    .line 610
    return-object p0
.end method

.method public addEmailBcc([Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p1, "addresses"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 621
    const-string v0, "android.intent.extra.BCC"

    #@2
    invoke-direct {p0, v0, p1}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->combineArrayExtra(Ljava/lang/String;[Ljava/lang/String;)V

    #@5
    .line 622
    return-object p0
.end method

.method public addEmailCc(Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p1, "address"    # Ljava/lang/String;

    #@0
    .prologue
    .line 566
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mCcAddresses:Ljava/util/ArrayList;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 567
    new-instance v0, Ljava/util/ArrayList;

    #@6
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@9
    iput-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mCcAddresses:Ljava/util/ArrayList;

    #@b
    .line 569
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mCcAddresses:Ljava/util/ArrayList;

    #@d
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@10
    .line 570
    return-object p0
.end method

.method public addEmailCc([Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p1, "addresses"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 581
    const-string v0, "android.intent.extra.CC"

    #@2
    invoke-direct {p0, v0, p1}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->combineArrayExtra(Ljava/lang/String;[Ljava/lang/String;)V

    #@5
    .line 582
    return-object p0
.end method

.method public addEmailTo(Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p1, "address"    # Ljava/lang/String;

    #@0
    .prologue
    .line 526
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mToAddresses:Ljava/util/ArrayList;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 527
    new-instance v0, Ljava/util/ArrayList;

    #@6
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@9
    iput-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mToAddresses:Ljava/util/ArrayList;

    #@b
    .line 529
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mToAddresses:Ljava/util/ArrayList;

    #@d
    invoke-virtual {v0, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@10
    .line 530
    return-object p0
.end method

.method public addEmailTo([Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p1, "addresses"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 541
    const-string v0, "android.intent.extra.EMAIL"

    #@2
    invoke-direct {p0, v0, p1}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->combineArrayExtra(Ljava/lang/String;[Ljava/lang/String;)V

    #@5
    .line 542
    return-object p0
.end method

.method public addStream(Landroid/net/Uri;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 3
    .param p1, "streamUri"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 487
    iget-object v1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2
    const-string v2, "android.intent.extra.STREAM"

    #@4
    invoke-virtual {v1, v2}, Landroid/content/Intent;->getParcelableExtra(Ljava/lang/String;)Landroid/os/Parcelable;

    #@7
    move-result-object v0

    #@8
    check-cast v0, Landroid/net/Uri;

    #@a
    .line 488
    .local v0, "currentStream":Landroid/net/Uri;
    if-nez v0, :cond_0

    #@c
    .line 489
    invoke-virtual {p0, p1}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->setStream(Landroid/net/Uri;)Landroid/support/v4/app/ShareCompat$IntentBuilder;

    #@f
    move-result-object p0

    #@10
    .line 499
    .end local p0    # "this":Landroid/support/v4/app/ShareCompat$IntentBuilder;
    :goto_0
    return-object p0

    #@11
    .line 491
    .restart local p0    # "this":Landroid/support/v4/app/ShareCompat$IntentBuilder;
    :cond_0
    iget-object v1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@13
    if-nez v1, :cond_1

    #@15
    .line 492
    new-instance v1, Ljava/util/ArrayList;

    #@17
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    #@1a
    iput-object v1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@1c
    .line 494
    :cond_1
    if-eqz v0, :cond_2

    #@1e
    .line 495
    iget-object v1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@20
    const-string v2, "android.intent.extra.STREAM"

    #@22
    invoke-virtual {v1, v2}, Landroid/content/Intent;->removeExtra(Ljava/lang/String;)V

    #@25
    .line 496
    iget-object v1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@27
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@2a
    .line 498
    :cond_2
    iget-object v1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@2c
    invoke-virtual {v1, p1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #@2f
    goto :goto_0
.end method

.method public createChooserIntent()Landroid/content/Intent;
    .locals 2

    #@0
    .prologue
    .line 374
    invoke-virtual {p0}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->getIntent()Landroid/content/Intent;

    #@3
    move-result-object v0

    #@4
    iget-object v1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mChooserTitle:Ljava/lang/CharSequence;

    #@6
    invoke-static {v0, v1}, Landroid/content/Intent;->createChooser(Landroid/content/Intent;Ljava/lang/CharSequence;)Landroid/content/Intent;

    #@9
    move-result-object v0

    #@a
    return-object v0
.end method

.method getActivity()Landroid/app/Activity;
    .locals 1

    #@0
    .prologue
    .line 341
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mActivity:Landroid/app/Activity;

    #@2
    return-object v0
.end method

.method public getIntent()Landroid/content/Intent;
    .locals 7

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    const/4 v6, 0x0

    #@3
    .line 297
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mToAddresses:Ljava/util/ArrayList;

    #@5
    if-eqz v3, :cond_0

    #@7
    .line 298
    const-string v3, "android.intent.extra.EMAIL"

    #@9
    iget-object v4, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mToAddresses:Ljava/util/ArrayList;

    #@b
    invoke-direct {p0, v3, v4}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->combineArrayExtra(Ljava/lang/String;Ljava/util/ArrayList;)V

    #@e
    .line 299
    iput-object v6, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mToAddresses:Ljava/util/ArrayList;

    #@10
    .line 301
    :cond_0
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mCcAddresses:Ljava/util/ArrayList;

    #@12
    if-eqz v3, :cond_1

    #@14
    .line 302
    const-string v3, "android.intent.extra.CC"

    #@16
    iget-object v4, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mCcAddresses:Ljava/util/ArrayList;

    #@18
    invoke-direct {p0, v3, v4}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->combineArrayExtra(Ljava/lang/String;Ljava/util/ArrayList;)V

    #@1b
    .line 303
    iput-object v6, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mCcAddresses:Ljava/util/ArrayList;

    #@1d
    .line 305
    :cond_1
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mBccAddresses:Ljava/util/ArrayList;

    #@1f
    if-eqz v3, :cond_2

    #@21
    .line 306
    const-string v3, "android.intent.extra.BCC"

    #@23
    iget-object v4, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mBccAddresses:Ljava/util/ArrayList;

    #@25
    invoke-direct {p0, v3, v4}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->combineArrayExtra(Ljava/lang/String;Ljava/util/ArrayList;)V

    #@28
    .line 307
    iput-object v6, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mBccAddresses:Ljava/util/ArrayList;

    #@2a
    .line 311
    :cond_2
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@2c
    if-eqz v3, :cond_5

    #@2e
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@30
    invoke-virtual {v3}, Ljava/util/ArrayList;->size()I

    #@33
    move-result v3

    #@34
    if-le v3, v1, :cond_5

    #@36
    .line 312
    .local v1, "needsSendMultiple":Z
    :goto_0
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@38
    invoke-virtual {v3}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    #@3b
    move-result-object v3

    #@3c
    const-string v4, "android.intent.action.SEND_MULTIPLE"

    #@3e
    invoke-virtual {v3, v4}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@41
    move-result v0

    #@42
    .line 314
    .local v0, "isSendMultiple":Z
    if-nez v1, :cond_3

    #@44
    if-eqz v0, :cond_3

    #@46
    .line 317
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@48
    const-string v4, "android.intent.action.SEND"

    #@4a
    invoke-virtual {v3, v4}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    #@4d
    .line 318
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@4f
    if-eqz v3, :cond_6

    #@51
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@53
    invoke-virtual {v3}, Ljava/util/ArrayList;->isEmpty()Z

    #@56
    move-result v3

    #@57
    if-nez v3, :cond_6

    #@59
    .line 319
    iget-object v3, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@5b
    const-string v4, "android.intent.extra.STREAM"

    #@5d
    iget-object v5, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@5f
    invoke-virtual {v5, v2}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;

    #@62
    move-result-object v2

    #@63
    check-cast v2, Landroid/os/Parcelable;

    #@65
    invoke-virtual {v3, v4, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;

    #@68
    .line 323
    :goto_1
    iput-object v6, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@6a
    .line 326
    :cond_3
    if-eqz v1, :cond_4

    #@6c
    if-nez v0, :cond_4

    #@6e
    .line 329
    iget-object v2, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@70
    const-string v3, "android.intent.action.SEND_MULTIPLE"

    #@72
    invoke-virtual {v2, v3}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    #@75
    .line 330
    iget-object v2, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@77
    if-eqz v2, :cond_7

    #@79
    iget-object v2, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@7b
    invoke-virtual {v2}, Ljava/util/ArrayList;->isEmpty()Z

    #@7e
    move-result v2

    #@7f
    if-nez v2, :cond_7

    #@81
    .line 331
    iget-object v2, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@83
    const-string v3, "android.intent.extra.STREAM"

    #@85
    iget-object v4, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@87
    invoke-virtual {v2, v3, v4}, Landroid/content/Intent;->putParcelableArrayListExtra(Ljava/lang/String;Ljava/util/ArrayList;)Landroid/content/Intent;

    #@8a
    .line 337
    :cond_4
    :goto_2
    iget-object v2, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@8c
    return-object v2

    #@8d
    .end local v0    # "isSendMultiple":Z
    .end local v1    # "needsSendMultiple":Z
    :cond_5
    move v1, v2

    #@8e
    .line 311
    goto :goto_0

    #@8f
    .line 321
    .restart local v0    # "isSendMultiple":Z
    .restart local v1    # "needsSendMultiple":Z
    :cond_6
    iget-object v2, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@91
    const-string v3, "android.intent.extra.STREAM"

    #@93
    invoke-virtual {v2, v3}, Landroid/content/Intent;->removeExtra(Ljava/lang/String;)V

    #@96
    goto :goto_1

    #@97
    .line 333
    :cond_7
    iget-object v2, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@99
    const-string v3, "android.intent.extra.STREAM"

    #@9b
    invoke-virtual {v2, v3}, Landroid/content/Intent;->removeExtra(Ljava/lang/String;)V

    #@9e
    goto :goto_2
.end method

.method public setChooserTitle(I)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 408
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mActivity:Landroid/app/Activity;

    #@2
    invoke-virtual {v0, p1}, Landroid/app/Activity;->getText(I)Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {p0, v0}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->setChooserTitle(Ljava/lang/CharSequence;)Landroid/support/v4/app/ShareCompat$IntentBuilder;

    #@9
    move-result-object v0

    #@a
    return-object v0
.end method

.method public setChooserTitle(Ljava/lang/CharSequence;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 0
    .param p1, "title"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 397
    iput-object p1, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mChooserTitle:Ljava/lang/CharSequence;

    #@2
    .line 398
    return-object p0
.end method

.method public setEmailBcc([Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 2
    .param p1, "addresses"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 594
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2
    const-string v1, "android.intent.extra.BCC"

    #@4
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;[Ljava/lang/String;)Landroid/content/Intent;

    #@7
    .line 595
    return-object p0
.end method

.method public setEmailCc([Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 2
    .param p1, "addresses"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 554
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2
    const-string v1, "android.intent.extra.CC"

    #@4
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;[Ljava/lang/String;)Landroid/content/Intent;

    #@7
    .line 555
    return-object p0
.end method

.method public setEmailTo([Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 2
    .param p1, "addresses"    # [Ljava/lang/String;

    #@0
    .prologue
    .line 511
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mToAddresses:Ljava/util/ArrayList;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 512
    const/4 v0, 0x0

    #@5
    iput-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mToAddresses:Ljava/util/ArrayList;

    #@7
    .line 514
    :cond_0
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@9
    const-string v1, "android.intent.extra.EMAIL"

    #@b
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;[Ljava/lang/String;)Landroid/content/Intent;

    #@e
    .line 515
    return-object p0
.end method

.method public setHtmlText(Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 2
    .param p1, "htmlText"    # Ljava/lang/String;

    #@0
    .prologue
    .line 448
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2
    const-string v1, "android.intent.extra.HTML_TEXT"

    #@4
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@7
    .line 449
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@9
    const-string v1, "android.intent.extra.TEXT"

    #@b
    invoke-virtual {v0, v1}, Landroid/content/Intent;->hasExtra(Ljava/lang/String;)Z

    #@e
    move-result v0

    #@f
    if-nez v0, :cond_0

    #@11
    .line 451
    invoke-static {p1}, Landroid/text/Html;->fromHtml(Ljava/lang/String;)Landroid/text/Spanned;

    #@14
    move-result-object v0

    #@15
    invoke-virtual {p0, v0}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->setText(Ljava/lang/CharSequence;)Landroid/support/v4/app/ShareCompat$IntentBuilder;

    #@18
    .line 453
    :cond_0
    return-object p0
.end method

.method public setStream(Landroid/net/Uri;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 2
    .param p1, "streamUri"    # Landroid/net/Uri;

    #@0
    .prologue
    .line 467
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2
    invoke-virtual {v0}, Landroid/content/Intent;->getAction()Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    const-string v1, "android.intent.action.SEND"

    #@8
    invoke-virtual {v0, v1}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@b
    move-result v0

    #@c
    if-nez v0, :cond_0

    #@e
    .line 468
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@10
    const-string v1, "android.intent.action.SEND"

    #@12
    invoke-virtual {v0, v1}, Landroid/content/Intent;->setAction(Ljava/lang/String;)Landroid/content/Intent;

    #@15
    .line 470
    :cond_0
    const/4 v0, 0x0

    #@16
    iput-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mStreams:Ljava/util/ArrayList;

    #@18
    .line 471
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@1a
    const-string v1, "android.intent.extra.STREAM"

    #@1c
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;

    #@1f
    .line 472
    return-object p0
.end method

.method public setSubject(Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 2
    .param p1, "subject"    # Ljava/lang/String;

    #@0
    .prologue
    .line 633
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2
    const-string v1, "android.intent.extra.SUBJECT"

    #@4
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@7
    .line 634
    return-object p0
.end method

.method public setText(Ljava/lang/CharSequence;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 2
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 432
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2
    const-string v1, "android.intent.extra.TEXT"

    #@4
    invoke-virtual {v0, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/CharSequence;)Landroid/content/Intent;

    #@7
    .line 433
    return-object p0
.end method

.method public setType(Ljava/lang/String;)Landroid/support/v4/app/ShareCompat$IntentBuilder;
    .locals 1
    .param p1, "mimeType"    # Ljava/lang/String;

    #@0
    .prologue
    .line 419
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mIntent:Landroid/content/Intent;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/Intent;->setType(Ljava/lang/String;)Landroid/content/Intent;

    #@5
    .line 420
    return-object p0
.end method

.method public startChooser()V
    .locals 2

    #@0
    .prologue
    .line 387
    iget-object v0, p0, Landroid/support/v4/app/ShareCompat$IntentBuilder;->mActivity:Landroid/app/Activity;

    #@2
    invoke-virtual {p0}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->createChooserIntent()Landroid/content/Intent;

    #@5
    move-result-object v1

    #@6
    invoke-virtual {v0, v1}, Landroid/app/Activity;->startActivity(Landroid/content/Intent;)V

    #@9
    .line 388
    return-void
.end method

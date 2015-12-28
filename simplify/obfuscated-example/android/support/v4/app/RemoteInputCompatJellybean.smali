.class Landroid/support/v4/app/RemoteInputCompatJellybean;
.super Ljava/lang/Object;
.source "RemoteInputCompatJellybean.java"


# static fields
.field public static final EXTRA_RESULTS_DATA:Ljava/lang/String; = "android.remoteinput.resultsData"

.field private static final KEY_ALLOW_FREE_FORM_INPUT:Ljava/lang/String; = "allowFreeFormInput"

.field private static final KEY_CHOICES:Ljava/lang/String; = "choices"

.field private static final KEY_EXTRAS:Ljava/lang/String; = "extras"

.field private static final KEY_LABEL:Ljava/lang/String; = "label"

.field private static final KEY_RESULT_KEY:Ljava/lang/String; = "resultKey"

.field public static final RESULTS_CLIP_LABEL:Ljava/lang/String; = "android.remoteinput.results"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method static addResultsToIntent([Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;Landroid/content/Intent;Landroid/os/Bundle;)V
    .locals 8
    .param p0, "remoteInputs"    # [Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    .param p1, "intent"    # Landroid/content/Intent;
    .param p2, "results"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 96
    new-instance v6, Landroid/os/Bundle;

    #@2
    invoke-direct {v6}, Landroid/os/Bundle;-><init>()V

    #@5
    .line 97
    .local v6, "resultsBundle":Landroid/os/Bundle;
    move-object v0, p0

    #@6
    .local v0, "arr$":[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    array-length v3, v0

    #@7
    .local v3, "len$":I
    const/4 v2, 0x0

    #@8
    .local v2, "i$":I
    :goto_0
    if-ge v2, v3, :cond_1

    #@a
    aget-object v4, v0, v2

    #@c
    .line 98
    .local v4, "remoteInput":Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    invoke-virtual {v4}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getResultKey()Ljava/lang/String;

    #@f
    move-result-object v7

    #@10
    invoke-virtual {p2, v7}, Landroid/os/Bundle;->get(Ljava/lang/String;)Ljava/lang/Object;

    #@13
    move-result-object v5

    #@14
    .line 99
    .local v5, "result":Ljava/lang/Object;
    instance-of v7, v5, Ljava/lang/CharSequence;

    #@16
    if-eqz v7, :cond_0

    #@18
    .line 100
    invoke-virtual {v4}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getResultKey()Ljava/lang/String;

    #@1b
    move-result-object v7

    #@1c
    check-cast v5, Ljava/lang/CharSequence;

    #@1e
    .end local v5    # "result":Ljava/lang/Object;
    invoke-virtual {v6, v7, v5}, Landroid/os/Bundle;->putCharSequence(Ljava/lang/String;Ljava/lang/CharSequence;)V

    #@21
    .line 97
    :cond_0
    add-int/lit8 v2, v2, 0x1

    #@23
    goto :goto_0

    #@24
    .line 103
    .end local v4    # "remoteInput":Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    :cond_1
    new-instance v1, Landroid/content/Intent;

    #@26
    invoke-direct {v1}, Landroid/content/Intent;-><init>()V

    #@29
    .line 104
    .local v1, "clipIntent":Landroid/content/Intent;
    const-string v7, "android.remoteinput.resultsData"

    #@2b
    invoke-virtual {v1, v7, v6}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Bundle;)Landroid/content/Intent;

    #@2e
    .line 105
    const-string v7, "android.remoteinput.results"

    #@30
    invoke-static {v7, v1}, Landroid/content/ClipData;->newIntent(Ljava/lang/CharSequence;Landroid/content/Intent;)Landroid/content/ClipData;

    #@33
    move-result-object v7

    #@34
    invoke-virtual {p1, v7}, Landroid/content/Intent;->setClipData(Landroid/content/ClipData;)V

    #@37
    .line 106
    return-void
.end method

.method static fromBundle(Landroid/os/Bundle;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    .locals 6
    .param p0, "data"    # Landroid/os/Bundle;
    .param p1, "factory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@0
    .prologue
    .line 39
    const-string v0, "resultKey"

    #@2
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getString(Ljava/lang/String;)Ljava/lang/String;

    #@5
    move-result-object v1

    #@6
    const-string v0, "label"

    #@8
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getCharSequence(Ljava/lang/String;)Ljava/lang/CharSequence;

    #@b
    move-result-object v2

    #@c
    const-string v0, "choices"

    #@e
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getCharSequenceArray(Ljava/lang/String;)[Ljava/lang/CharSequence;

    #@11
    move-result-object v3

    #@12
    const-string v0, "allowFreeFormInput"

    #@14
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getBoolean(Ljava/lang/String;)Z

    #@17
    move-result v4

    #@18
    const-string v0, "extras"

    #@1a
    invoke-virtual {p0, v0}, Landroid/os/Bundle;->getBundle(Ljava/lang/String;)Landroid/os/Bundle;

    #@1d
    move-result-object v5

    #@1e
    move-object v0, p1

    #@1f
    invoke-interface/range {v0 .. v5}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;->build(Ljava/lang/String;Ljava/lang/CharSequence;[Ljava/lang/CharSequence;ZLandroid/os/Bundle;)Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@22
    move-result-object v0

    #@23
    return-object v0
.end method

.method static fromBundleArray([Landroid/os/Bundle;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    .locals 3
    .param p0, "bundles"    # [Landroid/os/Bundle;
    .param p1, "factory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@0
    .prologue
    .line 58
    if-nez p0, :cond_1

    #@2
    .line 59
    const/4 v1, 0x0

    #@3
    .line 65
    :cond_0
    return-object v1

    #@4
    .line 61
    :cond_1
    array-length v2, p0

    #@5
    invoke-interface {p1, v2}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;->newArray(I)[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@8
    move-result-object v1

    #@9
    .line 62
    .local v1, "remoteInputs":[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    const/4 v0, 0x0

    #@a
    .local v0, "i":I
    :goto_0
    array-length v2, p0

    #@b
    if-ge v0, v2, :cond_0

    #@d
    .line 63
    aget-object v2, p0, v0

    #@f
    invoke-static {v2, p1}, Landroid/support/v4/app/RemoteInputCompatJellybean;->fromBundle(Landroid/os/Bundle;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@12
    move-result-object v2

    #@13
    aput-object v2, v1, v0

    #@15
    .line 62
    add-int/lit8 v0, v0, 0x1

    #@17
    goto :goto_0
.end method

.method static getResultsFromIntent(Landroid/content/Intent;)Landroid/os/Bundle;
    .locals 5
    .param p0, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 80
    invoke-virtual {p0}, Landroid/content/Intent;->getClipData()Landroid/content/ClipData;

    #@4
    move-result-object v0

    #@5
    .line 81
    .local v0, "clipData":Landroid/content/ClipData;
    if-nez v0, :cond_1

    #@7
    .line 91
    :cond_0
    :goto_0
    return-object v2

    #@8
    .line 84
    :cond_1
    invoke-virtual {v0}, Landroid/content/ClipData;->getDescription()Landroid/content/ClipDescription;

    #@b
    move-result-object v1

    #@c
    .line 85
    .local v1, "clipDescription":Landroid/content/ClipDescription;
    const-string v3, "text/vnd.android.intent"

    #@e
    invoke-virtual {v1, v3}, Landroid/content/ClipDescription;->hasMimeType(Ljava/lang/String;)Z

    #@11
    move-result v3

    #@12
    if-eqz v3, :cond_0

    #@14
    .line 88
    invoke-virtual {v1}, Landroid/content/ClipDescription;->getLabel()Ljava/lang/CharSequence;

    #@17
    move-result-object v3

    #@18
    const-string v4, "android.remoteinput.results"

    #@1a
    invoke-virtual {v3, v4}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@1d
    move-result v3

    #@1e
    if-eqz v3, :cond_0

    #@20
    .line 89
    const/4 v2, 0x0

    #@21
    invoke-virtual {v0, v2}, Landroid/content/ClipData;->getItemAt(I)Landroid/content/ClipData$Item;

    #@24
    move-result-object v2

    #@25
    invoke-virtual {v2}, Landroid/content/ClipData$Item;->getIntent()Landroid/content/Intent;

    #@28
    move-result-object v2

    #@29
    invoke-virtual {v2}, Landroid/content/Intent;->getExtras()Landroid/os/Bundle;

    #@2c
    move-result-object v2

    #@2d
    const-string v3, "android.remoteinput.resultsData"

    #@2f
    invoke-virtual {v2, v3}, Landroid/os/Bundle;->getParcelable(Ljava/lang/String;)Landroid/os/Parcelable;

    #@32
    move-result-object v2

    #@33
    check-cast v2, Landroid/os/Bundle;

    #@35
    goto :goto_0
.end method

.method static toBundle(Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)Landroid/os/Bundle;
    .locals 3
    .param p0, "remoteInput"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@0
    .prologue
    .line 47
    new-instance v0, Landroid/os/Bundle;

    #@2
    invoke-direct {v0}, Landroid/os/Bundle;-><init>()V

    #@5
    .line 48
    .local v0, "data":Landroid/os/Bundle;
    const-string v1, "resultKey"

    #@7
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getResultKey()Ljava/lang/String;

    #@a
    move-result-object v2

    #@b
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putString(Ljava/lang/String;Ljava/lang/String;)V

    #@e
    .line 49
    const-string v1, "label"

    #@10
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getLabel()Ljava/lang/CharSequence;

    #@13
    move-result-object v2

    #@14
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putCharSequence(Ljava/lang/String;Ljava/lang/CharSequence;)V

    #@17
    .line 50
    const-string v1, "choices"

    #@19
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getChoices()[Ljava/lang/CharSequence;

    #@1c
    move-result-object v2

    #@1d
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putCharSequenceArray(Ljava/lang/String;[Ljava/lang/CharSequence;)V

    #@20
    .line 51
    const-string v1, "allowFreeFormInput"

    #@22
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getAllowFreeFormInput()Z

    #@25
    move-result v2

    #@26
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBoolean(Ljava/lang/String;Z)V

    #@29
    .line 52
    const-string v1, "extras"

    #@2b
    invoke-virtual {p0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getExtras()Landroid/os/Bundle;

    #@2e
    move-result-object v2

    #@2f
    invoke-virtual {v0, v1, v2}, Landroid/os/Bundle;->putBundle(Ljava/lang/String;Landroid/os/Bundle;)V

    #@32
    .line 53
    return-object v0
.end method

.method static toBundleArray([Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)[Landroid/os/Bundle;
    .locals 3
    .param p0, "remoteInputs"    # [Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@0
    .prologue
    .line 69
    if-nez p0, :cond_1

    #@2
    .line 70
    const/4 v0, 0x0

    #@3
    .line 76
    :cond_0
    return-object v0

    #@4
    .line 72
    :cond_1
    array-length v2, p0

    #@5
    new-array v0, v2, [Landroid/os/Bundle;

    #@7
    .line 73
    .local v0, "bundles":[Landroid/os/Bundle;
    const/4 v1, 0x0

    #@8
    .local v1, "i":I
    :goto_0
    array-length v2, p0

    #@9
    if-ge v1, v2, :cond_0

    #@b
    .line 74
    aget-object v2, p0, v1

    #@d
    invoke-static {v2}, Landroid/support/v4/app/RemoteInputCompatJellybean;->toBundle(Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)Landroid/os/Bundle;

    #@10
    move-result-object v2

    #@11
    aput-object v2, v0, v1

    #@13
    .line 73
    add-int/lit8 v1, v1, 0x1

    #@15
    goto :goto_0
.end method

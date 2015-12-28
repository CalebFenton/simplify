.class Landroid/support/v4/app/RemoteInputCompatApi20;
.super Ljava/lang/Object;
.source "RemoteInputCompatApi20.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 23
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method static addResultsToIntent([Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;Landroid/content/Intent;Landroid/os/Bundle;)V
    .locals 1
    .param p0, "remoteInputs"    # [Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    .param p1, "intent"    # Landroid/content/Intent;
    .param p2, "results"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 61
    invoke-static {p0}, Landroid/support/v4/app/RemoteInputCompatApi20;->fromCompat([Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)[Landroid/app/RemoteInput;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0, p1, p2}, Landroid/app/RemoteInput;->addResultsToIntent([Landroid/app/RemoteInput;Landroid/content/Intent;Landroid/os/Bundle;)V

    #@7
    .line 62
    return-void
.end method

.method static fromCompat([Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;)[Landroid/app/RemoteInput;
    .locals 5
    .param p0, "srcArray"    # [Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@0
    .prologue
    .line 39
    if-nez p0, :cond_1

    #@2
    .line 40
    const/4 v1, 0x0

    #@3
    .line 52
    :cond_0
    return-object v1

    #@4
    .line 42
    :cond_1
    array-length v3, p0

    #@5
    new-array v1, v3, [Landroid/app/RemoteInput;

    #@7
    .line 43
    .local v1, "result":[Landroid/app/RemoteInput;
    const/4 v0, 0x0

    #@8
    .local v0, "i":I
    :goto_0
    array-length v3, p0

    #@9
    if-ge v0, v3, :cond_0

    #@b
    .line 44
    aget-object v2, p0, v0

    #@d
    .line 45
    .local v2, "src":Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    new-instance v3, Landroid/app/RemoteInput$Builder;

    #@f
    invoke-virtual {v2}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getResultKey()Ljava/lang/String;

    #@12
    move-result-object v4

    #@13
    invoke-direct {v3, v4}, Landroid/app/RemoteInput$Builder;-><init>(Ljava/lang/String;)V

    #@16
    invoke-virtual {v2}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getLabel()Ljava/lang/CharSequence;

    #@19
    move-result-object v4

    #@1a
    invoke-virtual {v3, v4}, Landroid/app/RemoteInput$Builder;->setLabel(Ljava/lang/CharSequence;)Landroid/app/RemoteInput$Builder;

    #@1d
    move-result-object v3

    #@1e
    invoke-virtual {v2}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getChoices()[Ljava/lang/CharSequence;

    #@21
    move-result-object v4

    #@22
    invoke-virtual {v3, v4}, Landroid/app/RemoteInput$Builder;->setChoices([Ljava/lang/CharSequence;)Landroid/app/RemoteInput$Builder;

    #@25
    move-result-object v3

    #@26
    invoke-virtual {v2}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getAllowFreeFormInput()Z

    #@29
    move-result v4

    #@2a
    invoke-virtual {v3, v4}, Landroid/app/RemoteInput$Builder;->setAllowFreeFormInput(Z)Landroid/app/RemoteInput$Builder;

    #@2d
    move-result-object v3

    #@2e
    invoke-virtual {v2}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;->getExtras()Landroid/os/Bundle;

    #@31
    move-result-object v4

    #@32
    invoke-virtual {v3, v4}, Landroid/app/RemoteInput$Builder;->addExtras(Landroid/os/Bundle;)Landroid/app/RemoteInput$Builder;

    #@35
    move-result-object v3

    #@36
    invoke-virtual {v3}, Landroid/app/RemoteInput$Builder;->build()Landroid/app/RemoteInput;

    #@39
    move-result-object v3

    #@3a
    aput-object v3, v1, v0

    #@3c
    .line 43
    add-int/lit8 v0, v0, 0x1

    #@3e
    goto :goto_0
.end method

.method static getResultsFromIntent(Landroid/content/Intent;)Landroid/os/Bundle;
    .locals 1
    .param p0, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 56
    invoke-static {p0}, Landroid/app/RemoteInput;->getResultsFromIntent(Landroid/content/Intent;)Landroid/os/Bundle;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static toCompat([Landroid/app/RemoteInput;Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;)[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    .locals 9
    .param p0, "srcArray"    # [Landroid/app/RemoteInput;
    .param p1, "factory"    # Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;

    #@0
    .prologue
    .line 26
    if-nez p0, :cond_1

    #@2
    .line 27
    const/4 v7, 0x0

    #@3
    .line 35
    :cond_0
    return-object v7

    #@4
    .line 29
    :cond_1
    array-length v0, p0

    #@5
    invoke-interface {p1, v0}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;->newArray(I)[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@8
    move-result-object v7

    #@9
    .line 30
    .local v7, "result":[Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;
    const/4 v6, 0x0

    #@a
    .local v6, "i":I
    :goto_0
    array-length v0, p0

    #@b
    if-ge v6, v0, :cond_0

    #@d
    .line 31
    aget-object v8, p0, v6

    #@f
    .line 32
    .local v8, "src":Landroid/app/RemoteInput;
    invoke-virtual {v8}, Landroid/app/RemoteInput;->getResultKey()Ljava/lang/String;

    #@12
    move-result-object v1

    #@13
    invoke-virtual {v8}, Landroid/app/RemoteInput;->getLabel()Ljava/lang/CharSequence;

    #@16
    move-result-object v2

    #@17
    invoke-virtual {v8}, Landroid/app/RemoteInput;->getChoices()[Ljava/lang/CharSequence;

    #@1a
    move-result-object v3

    #@1b
    invoke-virtual {v8}, Landroid/app/RemoteInput;->getAllowFreeFormInput()Z

    #@1e
    move-result v4

    #@1f
    invoke-virtual {v8}, Landroid/app/RemoteInput;->getExtras()Landroid/os/Bundle;

    #@22
    move-result-object v5

    #@23
    move-object v0, p1

    #@24
    invoke-interface/range {v0 .. v5}, Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput$Factory;->build(Ljava/lang/String;Ljava/lang/CharSequence;[Ljava/lang/CharSequence;ZLandroid/os/Bundle;)Landroid/support/v4/app/RemoteInputCompatBase$RemoteInput;

    #@27
    move-result-object v0

    #@28
    aput-object v0, v7, v6

    #@2a
    .line 30
    add-int/lit8 v6, v6, 0x1

    #@2c
    goto :goto_0
.end method

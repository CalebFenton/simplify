.class Landroid/support/v4/app/ShareCompat$ShareCompatImplBase;
.super Ljava/lang/Object;
.source "ShareCompat.java"

# interfaces
.implements Landroid/support/v4/app/ShareCompat$ShareCompatImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/ShareCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ShareCompatImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 86
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method private static withinStyle(Ljava/lang/StringBuilder;Ljava/lang/CharSequence;II)V
    .locals 5
    .param p0, "out"    # Ljava/lang/StringBuilder;
    .param p1, "text"    # Ljava/lang/CharSequence;
    .param p2, "start"    # I
    .param p3, "end"    # I

    #@0
    .prologue
    const/16 v4, 0x20

    #@2
    .line 99
    move v1, p2

    #@3
    .local v1, "i":I
    :goto_0
    if-ge v1, p3, :cond_7

    #@5
    .line 100
    invoke-interface {p1, v1}, Ljava/lang/CharSequence;->charAt(I)C

    #@8
    move-result v0

    #@9
    .line 102
    .local v0, "c":C
    const/16 v2, 0x3c

    #@b
    if-ne v0, v2, :cond_0

    #@d
    .line 103
    const-string v2, "&lt;"

    #@f
    invoke-virtual {p0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@12
    .line 99
    :goto_1
    add-int/lit8 v1, v1, 0x1

    #@14
    goto :goto_0

    #@15
    .line 104
    :cond_0
    const/16 v2, 0x3e

    #@17
    if-ne v0, v2, :cond_1

    #@19
    .line 105
    const-string v2, "&gt;"

    #@1b
    invoke-virtual {p0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@1e
    goto :goto_1

    #@1f
    .line 106
    :cond_1
    const/16 v2, 0x26

    #@21
    if-ne v0, v2, :cond_2

    #@23
    .line 107
    const-string v2, "&amp;"

    #@25
    invoke-virtual {p0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@28
    goto :goto_1

    #@29
    .line 108
    :cond_2
    const/16 v2, 0x7e

    #@2b
    if-gt v0, v2, :cond_3

    #@2d
    if-ge v0, v4, :cond_4

    #@2f
    .line 109
    :cond_3
    new-instance v2, Ljava/lang/StringBuilder;

    #@31
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@34
    const-string v3, "&#"

    #@36
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@39
    move-result-object v2

    #@3a
    invoke-virtual {v2, v0}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@3d
    move-result-object v2

    #@3e
    const-string v3, ";"

    #@40
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@43
    move-result-object v2

    #@44
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@47
    move-result-object v2

    #@48
    invoke-virtual {p0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4b
    goto :goto_1

    #@4c
    .line 110
    :cond_4
    if-ne v0, v4, :cond_6

    #@4e
    .line 111
    :goto_2
    add-int/lit8 v2, v1, 0x1

    #@50
    if-ge v2, p3, :cond_5

    #@52
    add-int/lit8 v2, v1, 0x1

    #@54
    invoke-interface {p1, v2}, Ljava/lang/CharSequence;->charAt(I)C

    #@57
    move-result v2

    #@58
    if-ne v2, v4, :cond_5

    #@5a
    .line 112
    const-string v2, "&nbsp;"

    #@5c
    invoke-virtual {p0, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@5f
    .line 113
    add-int/lit8 v1, v1, 0x1

    #@61
    goto :goto_2

    #@62
    .line 116
    :cond_5
    invoke-virtual {p0, v4}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@65
    goto :goto_1

    #@66
    .line 118
    :cond_6
    invoke-virtual {p0, v0}, Ljava/lang/StringBuilder;->append(C)Ljava/lang/StringBuilder;

    #@69
    goto :goto_1

    #@6a
    .line 121
    .end local v0    # "c":C
    :cond_7
    return-void
.end method


# virtual methods
.method public configureMenuItem(Landroid/view/MenuItem;Landroid/support/v4/app/ShareCompat$IntentBuilder;)V
    .locals 1
    .param p1, "item"    # Landroid/view/MenuItem;
    .param p2, "shareIntent"    # Landroid/support/v4/app/ShareCompat$IntentBuilder;

    #@0
    .prologue
    .line 88
    invoke-virtual {p2}, Landroid/support/v4/app/ShareCompat$IntentBuilder;->createChooserIntent()Landroid/content/Intent;

    #@3
    move-result-object v0

    #@4
    invoke-interface {p1, v0}, Landroid/view/MenuItem;->setIntent(Landroid/content/Intent;)Landroid/view/MenuItem;

    #@7
    .line 89
    return-void
.end method

.method public escapeHtml(Ljava/lang/CharSequence;)Ljava/lang/String;
    .locals 3
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 92
    new-instance v0, Ljava/lang/StringBuilder;

    #@2
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@5
    .line 93
    .local v0, "out":Ljava/lang/StringBuilder;
    const/4 v1, 0x0

    #@6
    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    #@9
    move-result v2

    #@a
    invoke-static {v0, p1, v1, v2}, Landroid/support/v4/app/ShareCompat$ShareCompatImplBase;->withinStyle(Ljava/lang/StringBuilder;Ljava/lang/CharSequence;II)V

    #@d
    .line 94
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@10
    move-result-object v1

    #@11
    return-object v1
.end method

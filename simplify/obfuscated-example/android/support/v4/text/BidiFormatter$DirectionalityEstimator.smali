.class Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;
.super Ljava/lang/Object;
.source "BidiFormatter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/text/BidiFormatter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "DirectionalityEstimator"
.end annotation


# static fields
.field private static final DIR_TYPE_CACHE:[B

.field private static final DIR_TYPE_CACHE_SIZE:I = 0x700


# instance fields
.field private charIndex:I

.field private final isHtml:Z

.field private lastChar:C

.field private final length:I

.field private final text:Ljava/lang/String;


# direct methods
.method static constructor <clinit>()V
    .locals 4

    #@0
    .prologue
    const/16 v3, 0x700

    #@2
    .line 505
    new-array v1, v3, [B

    #@4
    sput-object v1, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->DIR_TYPE_CACHE:[B

    #@6
    .line 506
    const/4 v0, 0x0

    #@7
    .local v0, "i":I
    :goto_0
    if-ge v0, v3, :cond_0

    #@9
    .line 507
    sget-object v1, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->DIR_TYPE_CACHE:[B

    #@b
    invoke-static {v0}, Ljava/lang/Character;->getDirectionality(I)B

    #@e
    move-result v2

    #@f
    aput-byte v2, v1, v0

    #@11
    .line 506
    add-int/lit8 v0, v0, 0x1

    #@13
    goto :goto_0

    #@14
    .line 509
    :cond_0
    return-void
.end method

.method constructor <init>(Ljava/lang/String;Z)V
    .locals 1
    .param p1, "text"    # Ljava/lang/String;
    .param p2, "isHtml"    # Z

    #@0
    .prologue
    .line 549
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 550
    iput-object p1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@5
    .line 551
    iput-boolean p2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->isHtml:Z

    #@7
    .line 552
    invoke-virtual {p1}, Ljava/lang/String;->length()I

    #@a
    move-result v0

    #@b
    iput v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->length:I

    #@d
    .line 553
    return-void
.end method

.method private static getCachedDirectionality(C)B
    .locals 1
    .param p0, "c"    # C

    #@0
    .prologue
    .line 724
    const/16 v0, 0x700

    #@2
    if-ge p0, v0, :cond_0

    #@4
    sget-object v0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->DIR_TYPE_CACHE:[B

    #@6
    aget-byte v0, v0, p0

    #@8
    :goto_0
    return v0

    #@9
    :cond_0
    invoke-static {p0}, Ljava/lang/Character;->getDirectionality(C)B

    #@c
    move-result v0

    #@d
    goto :goto_0
.end method

.method private skipEntityBackward()B
    .locals 4

    #@0
    .prologue
    const/16 v3, 0x3b

    #@2
    .line 865
    iget v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@4
    .line 866
    .local v0, "initialCharIndex":I
    :cond_0
    iget v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@6
    if-lez v1, :cond_2

    #@8
    .line 867
    iget-object v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@a
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@c
    add-int/lit8 v2, v2, -0x1

    #@e
    iput v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@10
    invoke-virtual {v1, v2}, Ljava/lang/String;->charAt(I)C

    #@13
    move-result v1

    #@14
    iput-char v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@16
    .line 868
    iget-char v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@18
    const/16 v2, 0x26

    #@1a
    if-ne v1, v2, :cond_1

    #@1c
    .line 869
    const/16 v1, 0xc

    #@1e
    .line 877
    :goto_0
    return v1

    #@1f
    .line 871
    :cond_1
    iget-char v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@21
    if-ne v1, v3, :cond_0

    #@23
    .line 875
    :cond_2
    iput v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@25
    .line 876
    iput-char v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@27
    .line 877
    const/16 v1, 0xd

    #@29
    goto :goto_0
.end method

.method private skipEntityForward()B
    .locals 3

    #@0
    .prologue
    .line 850
    :cond_0
    iget v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@2
    iget v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->length:I

    #@4
    if-ge v0, v1, :cond_1

    #@6
    iget-object v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@8
    iget v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@a
    add-int/lit8 v2, v1, 0x1

    #@c
    iput v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@e
    invoke-virtual {v0, v1}, Ljava/lang/String;->charAt(I)C

    #@11
    move-result v0

    #@12
    iput-char v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@14
    const/16 v1, 0x3b

    #@16
    if-ne v0, v1, :cond_0

    #@18
    .line 851
    :cond_1
    const/16 v0, 0xc

    #@1a
    return v0
.end method

.method private skipTagBackward()B
    .locals 5

    #@0
    .prologue
    const/16 v4, 0x3e

    #@2
    .line 822
    iget v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@4
    .line 823
    .local v0, "initialCharIndex":I
    :cond_0
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@6
    if-lez v2, :cond_2

    #@8
    .line 824
    iget-object v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@a
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@c
    add-int/lit8 v3, v3, -0x1

    #@e
    iput v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@10
    invoke-virtual {v2, v3}, Ljava/lang/String;->charAt(I)C

    #@13
    move-result v2

    #@14
    iput-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@16
    .line 825
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@18
    const/16 v3, 0x3c

    #@1a
    if-ne v2, v3, :cond_1

    #@1c
    .line 827
    const/16 v2, 0xc

    #@1e
    .line 841
    :goto_0
    return v2

    #@1f
    .line 829
    :cond_1
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@21
    if-ne v2, v4, :cond_3

    #@23
    .line 839
    :cond_2
    iput v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@25
    .line 840
    iput-char v4, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@27
    .line 841
    const/16 v2, 0xd

    #@29
    goto :goto_0

    #@2a
    .line 832
    :cond_3
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@2c
    const/16 v3, 0x22

    #@2e
    if-eq v2, v3, :cond_4

    #@30
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@32
    const/16 v3, 0x27

    #@34
    if-ne v2, v3, :cond_0

    #@36
    .line 834
    :cond_4
    iget-char v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@38
    .line 835
    .local v1, "quote":C
    :goto_1
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@3a
    if-lez v2, :cond_0

    #@3c
    iget-object v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@3e
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@40
    add-int/lit8 v3, v3, -0x1

    #@42
    iput v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@44
    invoke-virtual {v2, v3}, Ljava/lang/String;->charAt(I)C

    #@47
    move-result v2

    #@48
    iput-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@4a
    if-eq v2, v1, :cond_0

    #@4c
    goto :goto_1
.end method

.method private skipTagForward()B
    .locals 5

    #@0
    .prologue
    .line 793
    iget v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@2
    .line 794
    .local v0, "initialCharIndex":I
    :cond_0
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@4
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->length:I

    #@6
    if-ge v2, v3, :cond_3

    #@8
    .line 795
    iget-object v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@a
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@c
    add-int/lit8 v4, v3, 0x1

    #@e
    iput v4, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@10
    invoke-virtual {v2, v3}, Ljava/lang/String;->charAt(I)C

    #@13
    move-result v2

    #@14
    iput-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@16
    .line 796
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@18
    const/16 v3, 0x3e

    #@1a
    if-ne v2, v3, :cond_1

    #@1c
    .line 798
    const/16 v2, 0xc

    #@1e
    .line 809
    :goto_0
    return v2

    #@1f
    .line 800
    :cond_1
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@21
    const/16 v3, 0x22

    #@23
    if-eq v2, v3, :cond_2

    #@25
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@27
    const/16 v3, 0x27

    #@29
    if-ne v2, v3, :cond_0

    #@2b
    .line 802
    :cond_2
    iget-char v1, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@2d
    .line 803
    .local v1, "quote":C
    :goto_1
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@2f
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->length:I

    #@31
    if-ge v2, v3, :cond_0

    #@33
    iget-object v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@35
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@37
    add-int/lit8 v4, v3, 0x1

    #@39
    iput v4, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@3b
    invoke-virtual {v2, v3}, Ljava/lang/String;->charAt(I)C

    #@3e
    move-result v2

    #@3f
    iput-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@41
    if-eq v2, v1, :cond_0

    #@43
    goto :goto_1

    #@44
    .line 807
    .end local v1    # "quote":C
    :cond_3
    iput v0, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@46
    .line 808
    const/16 v2, 0x3c

    #@48
    iput-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@4a
    .line 809
    const/16 v2, 0xd

    #@4c
    goto :goto_0
.end method


# virtual methods
.method dirTypeBackward()B
    .locals 4

    #@0
    .prologue
    .line 767
    iget-object v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@2
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@4
    add-int/lit8 v3, v3, -0x1

    #@6
    invoke-virtual {v2, v3}, Ljava/lang/String;->charAt(I)C

    #@9
    move-result v2

    #@a
    iput-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@c
    .line 768
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@e
    invoke-static {v2}, Ljava/lang/Character;->isLowSurrogate(C)Z

    #@11
    move-result v2

    #@12
    if-eqz v2, :cond_1

    #@14
    .line 769
    iget-object v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@16
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@18
    invoke-static {v2, v3}, Ljava/lang/Character;->codePointBefore(Ljava/lang/CharSequence;I)I

    #@1b
    move-result v0

    #@1c
    .line 770
    .local v0, "codePoint":I
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@1e
    invoke-static {v0}, Ljava/lang/Character;->charCount(I)I

    #@21
    move-result v3

    #@22
    sub-int/2addr v2, v3

    #@23
    iput v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@25
    .line 771
    invoke-static {v0}, Ljava/lang/Character;->getDirectionality(I)B

    #@28
    move-result v1

    #@29
    .line 783
    .end local v0    # "codePoint":I
    :cond_0
    :goto_0
    return v1

    #@2a
    .line 773
    :cond_1
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@2c
    add-int/lit8 v2, v2, -0x1

    #@2e
    iput v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@30
    .line 774
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@32
    invoke-static {v2}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->getCachedDirectionality(C)B

    #@35
    move-result v1

    #@36
    .line 775
    .local v1, "dirType":B
    iget-boolean v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->isHtml:Z

    #@38
    if-eqz v2, :cond_0

    #@3a
    .line 777
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@3c
    const/16 v3, 0x3e

    #@3e
    if-ne v2, v3, :cond_2

    #@40
    .line 778
    invoke-direct {p0}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->skipTagBackward()B

    #@43
    move-result v1

    #@44
    goto :goto_0

    #@45
    .line 779
    :cond_2
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@47
    const/16 v3, 0x3b

    #@49
    if-ne v2, v3, :cond_0

    #@4b
    .line 780
    invoke-direct {p0}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->skipEntityBackward()B

    #@4e
    move-result v1

    #@4f
    goto :goto_0
.end method

.method dirTypeForward()B
    .locals 4

    #@0
    .prologue
    .line 737
    iget-object v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@2
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@4
    invoke-virtual {v2, v3}, Ljava/lang/String;->charAt(I)C

    #@7
    move-result v2

    #@8
    iput-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@a
    .line 738
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@c
    invoke-static {v2}, Ljava/lang/Character;->isHighSurrogate(C)Z

    #@f
    move-result v2

    #@10
    if-eqz v2, :cond_1

    #@12
    .line 739
    iget-object v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->text:Ljava/lang/String;

    #@14
    iget v3, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@16
    invoke-static {v2, v3}, Ljava/lang/Character;->codePointAt(Ljava/lang/CharSequence;I)I

    #@19
    move-result v0

    #@1a
    .line 740
    .local v0, "codePoint":I
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@1c
    invoke-static {v0}, Ljava/lang/Character;->charCount(I)I

    #@1f
    move-result v3

    #@20
    add-int/2addr v2, v3

    #@21
    iput v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@23
    .line 741
    invoke-static {v0}, Ljava/lang/Character;->getDirectionality(I)B

    #@26
    move-result v1

    #@27
    .line 753
    .end local v0    # "codePoint":I
    :cond_0
    :goto_0
    return v1

    #@28
    .line 743
    :cond_1
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@2a
    add-int/lit8 v2, v2, 0x1

    #@2c
    iput v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@2e
    .line 744
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@30
    invoke-static {v2}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->getCachedDirectionality(C)B

    #@33
    move-result v1

    #@34
    .line 745
    .local v1, "dirType":B
    iget-boolean v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->isHtml:Z

    #@36
    if-eqz v2, :cond_0

    #@38
    .line 747
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@3a
    const/16 v3, 0x3c

    #@3c
    if-ne v2, v3, :cond_2

    #@3e
    .line 748
    invoke-direct {p0}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->skipTagForward()B

    #@41
    move-result v1

    #@42
    goto :goto_0

    #@43
    .line 749
    :cond_2
    iget-char v2, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->lastChar:C

    #@45
    const/16 v3, 0x26

    #@47
    if-ne v2, v3, :cond_0

    #@49
    .line 750
    invoke-direct {p0}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->skipEntityForward()B

    #@4c
    move-result v1

    #@4d
    goto :goto_0
.end method

.method getEntryDir()I
    .locals 8

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/4 v3, -0x1

    #@2
    const/4 v5, 0x0

    #@3
    .line 567
    iput v5, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@5
    .line 568
    const/4 v0, 0x0

    #@6
    .line 569
    .local v0, "embeddingLevel":I
    const/4 v1, 0x0

    #@7
    .line 570
    .local v1, "embeddingLevelDir":I
    const/4 v2, 0x0

    #@8
    .line 571
    .local v2, "firstNonEmptyEmbeddingLevel":I
    :goto_0
    :pswitch_0
    iget v6, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@a
    iget v7, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->length:I

    #@c
    if-ge v6, v7, :cond_3

    #@e
    if-nez v2, :cond_3

    #@10
    .line 572
    invoke-virtual {p0}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->dirTypeForward()B

    #@13
    move-result v6

    #@14
    packed-switch v6, :pswitch_data_0

    #@17
    .line 606
    :pswitch_1
    move v2, v0

    #@18
    .line 607
    goto :goto_0

    #@19
    .line 575
    :pswitch_2
    add-int/lit8 v0, v0, 0x1

    #@1b
    .line 576
    const/4 v1, -0x1

    #@1c
    .line 577
    goto :goto_0

    #@1d
    .line 580
    :pswitch_3
    add-int/lit8 v0, v0, 0x1

    #@1f
    .line 581
    const/4 v1, 0x1

    #@20
    .line 582
    goto :goto_0

    #@21
    .line 584
    :pswitch_4
    add-int/lit8 v0, v0, -0x1

    #@23
    .line 588
    const/4 v1, 0x0

    #@24
    .line 589
    goto :goto_0

    #@25
    .line 593
    :pswitch_5
    if-nez v0, :cond_1

    #@27
    move v1, v3

    #@28
    .line 649
    .end local v1    # "embeddingLevelDir":I
    :cond_0
    :goto_1
    return v1

    #@29
    .line 596
    .restart local v1    # "embeddingLevelDir":I
    :cond_1
    move v2, v0

    #@2a
    .line 597
    goto :goto_0

    #@2b
    .line 600
    :pswitch_6
    if-nez v0, :cond_2

    #@2d
    move v1, v4

    #@2e
    .line 601
    goto :goto_1

    #@2f
    .line 603
    :cond_2
    move v2, v0

    #@30
    .line 604
    goto :goto_0

    #@31
    .line 613
    :cond_3
    if-nez v2, :cond_4

    #@33
    move v1, v5

    #@34
    .line 616
    goto :goto_1

    #@35
    .line 620
    :cond_4
    if-nez v1, :cond_0

    #@37
    .line 627
    :goto_2
    iget v6, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@39
    if-lez v6, :cond_7

    #@3b
    .line 628
    invoke-virtual {p0}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->dirTypeBackward()B

    #@3e
    move-result v6

    #@3f
    packed-switch v6, :pswitch_data_1

    #@42
    goto :goto_2

    #@43
    .line 631
    :pswitch_7
    if-ne v2, v0, :cond_5

    #@45
    move v1, v3

    #@46
    .line 632
    goto :goto_1

    #@47
    .line 634
    :cond_5
    add-int/lit8 v0, v0, -0x1

    #@49
    .line 635
    goto :goto_2

    #@4a
    .line 638
    :pswitch_8
    if-ne v2, v0, :cond_6

    #@4c
    move v1, v4

    #@4d
    .line 639
    goto :goto_1

    #@4e
    .line 641
    :cond_6
    add-int/lit8 v0, v0, -0x1

    #@50
    .line 642
    goto :goto_2

    #@51
    .line 644
    :pswitch_9
    add-int/lit8 v0, v0, 0x1

    #@53
    goto :goto_2

    #@54
    :cond_7
    move v1, v5

    #@55
    .line 649
    goto :goto_1

    #@56
    .line 572
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_5
        :pswitch_6
        :pswitch_6
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_0
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_2
        :pswitch_2
        :pswitch_3
        :pswitch_3
        :pswitch_4
    .end packed-switch

    #@80
    .line 628
    :pswitch_data_1
    .packed-switch 0xe
        :pswitch_7
        :pswitch_7
        :pswitch_8
        :pswitch_8
        :pswitch_9
    .end packed-switch
.end method

.method getExitDir()I
    .locals 5

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    const/4 v2, -0x1

    #@2
    .line 665
    iget v4, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->length:I

    #@4
    iput v4, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@6
    .line 666
    const/4 v0, 0x0

    #@7
    .line 667
    .local v0, "embeddingLevel":I
    const/4 v1, 0x0

    #@8
    .line 668
    .local v1, "lastNonEmptyEmbeddingLevel":I
    :cond_0
    :goto_0
    :pswitch_0
    iget v4, p0, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->charIndex:I

    #@a
    if-lez v4, :cond_5

    #@c
    .line 669
    invoke-virtual {p0}, Landroid/support/v4/text/BidiFormatter$DirectionalityEstimator;->dirTypeBackward()B

    #@f
    move-result v4

    #@10
    packed-switch v4, :pswitch_data_0

    #@13
    .line 707
    :pswitch_1
    if-nez v1, :cond_0

    #@15
    .line 708
    move v1, v0

    #@16
    goto :goto_0

    #@17
    .line 671
    :pswitch_2
    if-nez v0, :cond_2

    #@19
    .line 713
    :cond_1
    :goto_1
    return v2

    #@1a
    .line 674
    :cond_2
    if-nez v1, :cond_0

    #@1c
    .line 675
    move v1, v0

    #@1d
    goto :goto_0

    #@1e
    .line 680
    :pswitch_3
    if-eq v1, v0, :cond_1

    #@20
    .line 683
    add-int/lit8 v0, v0, -0x1

    #@22
    .line 684
    goto :goto_0

    #@23
    .line 687
    :pswitch_4
    if-nez v0, :cond_3

    #@25
    move v2, v3

    #@26
    .line 688
    goto :goto_1

    #@27
    .line 690
    :cond_3
    if-nez v1, :cond_0

    #@29
    .line 691
    move v1, v0

    #@2a
    goto :goto_0

    #@2b
    .line 696
    :pswitch_5
    if-ne v1, v0, :cond_4

    #@2d
    move v2, v3

    #@2e
    .line 697
    goto :goto_1

    #@2f
    .line 699
    :cond_4
    add-int/lit8 v0, v0, -0x1

    #@31
    .line 700
    goto :goto_0

    #@32
    .line 702
    :pswitch_6
    add-int/lit8 v0, v0, 0x1

    #@34
    .line 703
    goto :goto_0

    #@35
    .line 713
    :cond_5
    const/4 v2, 0x0

    #@36
    goto :goto_1

    #@37
    .line 669
    nop

    #@38
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_2
        :pswitch_4
        :pswitch_4
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_0
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_1
        :pswitch_3
        :pswitch_3
        :pswitch_5
        :pswitch_5
        :pswitch_6
    .end packed-switch
.end method

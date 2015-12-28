.class Landroid/support/v4/util/ContainerHelpers;
.super Ljava/lang/Object;
.source "ContainerHelpers.java"


# static fields
.field static final EMPTY_INTS:[I

.field static final EMPTY_LONGS:[J

.field static final EMPTY_OBJECTS:[Ljava/lang/Object;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 20
    new-array v0, v1, [I

    #@3
    sput-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_INTS:[I

    #@5
    .line 21
    new-array v0, v1, [J

    #@7
    sput-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_LONGS:[J

    #@9
    .line 22
    new-array v0, v1, [Ljava/lang/Object;

    #@b
    sput-object v0, Landroid/support/v4/util/ContainerHelpers;->EMPTY_OBJECTS:[Ljava/lang/Object;

    #@d
    return-void
.end method

.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 19
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method static binarySearch([III)I
    .locals 5
    .param p0, "array"    # [I
    .param p1, "size"    # I
    .param p2, "value"    # I

    #@0
    .prologue
    .line 46
    const/4 v1, 0x0

    #@1
    .line 47
    .local v1, "lo":I
    add-int/lit8 v0, p1, -0x1

    #@3
    .line 49
    .local v0, "hi":I
    :goto_0
    if-gt v1, v0, :cond_1

    #@5
    .line 50
    add-int v4, v1, v0

    #@7
    ushr-int/lit8 v2, v4, 0x1

    #@9
    .line 51
    .local v2, "mid":I
    aget v3, p0, v2

    #@b
    .line 53
    .local v3, "midVal":I
    if-ge v3, p2, :cond_0

    #@d
    .line 54
    add-int/lit8 v1, v2, 0x1

    #@f
    goto :goto_0

    #@10
    .line 55
    :cond_0
    if-le v3, p2, :cond_2

    #@12
    .line 56
    add-int/lit8 v0, v2, -0x1

    #@14
    goto :goto_0

    #@15
    .line 61
    .end local v2    # "mid":I
    .end local v3    # "midVal":I
    :cond_1
    xor-int/lit8 v2, v1, -0x1

    #@17
    :cond_2
    return v2
.end method

.method static binarySearch([JIJ)I
    .locals 6
    .param p0, "array"    # [J
    .param p1, "size"    # I
    .param p2, "value"    # J

    #@0
    .prologue
    .line 65
    const/4 v1, 0x0

    #@1
    .line 66
    .local v1, "lo":I
    add-int/lit8 v0, p1, -0x1

    #@3
    .line 68
    .local v0, "hi":I
    :goto_0
    if-gt v1, v0, :cond_1

    #@5
    .line 69
    add-int v3, v1, v0

    #@7
    ushr-int/lit8 v2, v3, 0x1

    #@9
    .line 70
    .local v2, "mid":I
    aget-wide v4, p0, v2

    #@b
    .line 72
    .local v4, "midVal":J
    cmp-long v3, v4, p2

    #@d
    if-gez v3, :cond_0

    #@f
    .line 73
    add-int/lit8 v1, v2, 0x1

    #@11
    goto :goto_0

    #@12
    .line 74
    :cond_0
    cmp-long v3, v4, p2

    #@14
    if-lez v3, :cond_2

    #@16
    .line 75
    add-int/lit8 v0, v2, -0x1

    #@18
    goto :goto_0

    #@19
    .line 80
    .end local v2    # "mid":I
    .end local v4    # "midVal":J
    :cond_1
    xor-int/lit8 v2, v1, -0x1

    #@1b
    :cond_2
    return v2
.end method

.method public static equal(Ljava/lang/Object;Ljava/lang/Object;)Z
    .locals 1
    .param p0, "a"    # Ljava/lang/Object;
    .param p1, "b"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 41
    if-eq p0, p1, :cond_0

    #@2
    if-eqz p0, :cond_1

    #@4
    invoke-virtual {p0, p1}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    #@7
    move-result v0

    #@8
    if-eqz v0, :cond_1

    #@a
    :cond_0
    const/4 v0, 0x1

    #@b
    :goto_0
    return v0

    #@c
    :cond_1
    const/4 v0, 0x0

    #@d
    goto :goto_0
.end method

.method public static idealByteArraySize(I)I
    .locals 3
    .param p0, "need"    # I

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 33
    const/4 v0, 0x4

    #@2
    .local v0, "i":I
    :goto_0
    const/16 v1, 0x20

    #@4
    if-ge v0, v1, :cond_0

    #@6
    .line 34
    shl-int v1, v2, v0

    #@8
    add-int/lit8 v1, v1, -0xc

    #@a
    if-gt p0, v1, :cond_1

    #@c
    .line 35
    shl-int v1, v2, v0

    #@e
    add-int/lit8 p0, v1, -0xc

    #@10
    .line 37
    .end local p0    # "need":I
    :cond_0
    return p0

    #@11
    .line 33
    .restart local p0    # "need":I
    :cond_1
    add-int/lit8 v0, v0, 0x1

    #@13
    goto :goto_0
.end method

.method public static idealIntArraySize(I)I
    .locals 1
    .param p0, "need"    # I

    #@0
    .prologue
    .line 25
    mul-int/lit8 v0, p0, 0x4

    #@2
    invoke-static {v0}, Landroid/support/v4/util/ContainerHelpers;->idealByteArraySize(I)I

    #@5
    move-result v0

    #@6
    div-int/lit8 v0, v0, 0x4

    #@8
    return v0
.end method

.method public static idealLongArraySize(I)I
    .locals 1
    .param p0, "need"    # I

    #@0
    .prologue
    .line 29
    mul-int/lit8 v0, p0, 0x8

    #@2
    invoke-static {v0}, Landroid/support/v4/util/ContainerHelpers;->idealByteArraySize(I)I

    #@5
    move-result v0

    #@6
    div-int/lit8 v0, v0, 0x8

    #@8
    return v0
.end method

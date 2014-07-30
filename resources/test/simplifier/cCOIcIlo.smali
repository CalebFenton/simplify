.class final Lcom/android/system/admin/cCOIcIlo;
.super Ljava/lang/Object;

# interfaces
.implements Ljava/lang/Runnable;


# annotations
.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# static fields
.field private static final oIlclcIc:[B


# instance fields
.field private synthetic oCIlCll:Lcom/android/system/admin/CIlOCClc;


# direct methods
.method static <clinit>()V
    .locals 3

    const/4 v0, 0x7

    new-array v0, v0, [B

    fill-array-data v0, :array_0

    sput-object v0, Lcom/android/system/admin/cCOIcIlo;->oIlclcIc:[B

    return-void

    nop

    :array_0
    .array-data 1
        0x2bt
        -0x2bt
        0x12t
        0x21t
        0x4t
        0x9t
        0xdt
    .end array-data
.end method

.method constructor <init>(Lcom/android/system/admin/CIlOCClc;)V
    .locals 0

    iput-object p1, p0, Lcom/android/system/admin/cCOIcIlo;->oCIlCll:Lcom/android/system/admin/CIlOCClc;

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static oCIlCll(II)Ljava/lang/String;
    .locals 6

    const/4 v5, 0x0

    new-instance v0, Ljava/lang/String;

    add-int/lit8 p1, p1, 0x4b

    sget-object v4, Lcom/android/system/admin/cCOIcIlo;->oIlclcIc:[B

    const/16 v1, 0x8

    new-array v1, v1, [B

    if-nez v4, :cond_0

    const/16 v2, 0x8

    const/4 v3, 0x0

    :goto_0
    add-int/lit8 p0, p0, 0x1

    add-int/2addr v2, v3

    add-int/lit8 p1, v2, -0xd

    :cond_0
    int-to-byte v2, p1

    aput-byte v2, v1, v5

    add-int/lit8 v5, v5, 0x1

    const/16 v2, 0x8

    if-lt v5, v2, :cond_1

    const/4 v2, 0x0

    invoke-direct {v0, v1, v2}, Ljava/lang/String;-><init>([BI)V

    return-object v0

    :cond_1
    move v2, p1

    aget-byte v3, v4, p0

    goto :goto_0
.end method


# virtual methods
.method public final run()V
    .locals 8

    invoke-static {}, Lcom/android/system/admin/cCoIOIOo;->oCIlCll()Ljava/lang/String;

    move-result-object v0

    if-nez v0, :cond_0

    const/4 v0, 0x0

    invoke-static {v0}, Lcom/android/system/admin/cCoIOIOo;->oIlclcIc(I)V

    return-void

    :cond_0
    invoke-static {}, Lcom/android/system/admin/cCoIOIOo;->IcCcCOIC()I

    move-result v0

    if-lez v0, :cond_1

    invoke-static {}, Lcom/android/system/admin/cCoIOIOo;->OcIcoOlc()Landroid/widget/Button;

    move-result-object v0

    new-instance v1, Ljava/lang/StringBuilder;

    invoke-static {}, Lcom/android/system/admin/cCoIOIOo;->oCIlCll()Ljava/lang/String;

    move-result-object v2

    invoke-static {v2}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;

    move-result-object v2

    invoke-direct {v1, v2}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    const/4 v2, 0x0

    const/4 v3, 0x0

    invoke-static {v2, v3}, Lcom/android/system/admin/cCOIcIlo;->oCIlCll(II)Ljava/lang/String;

    move-result-object v7

    new-instance v2, Ljava/lang/String;

    invoke-static {v7}, Lcom/android/system/admin/oCIlCll;->oCIlCll(Ljava/lang/String;)[B

    move-result-object v3

    const/16 v4, 0x18

    const/16 v5, 0x6a4

    const/4 v6, -0x7

    invoke-static {v4, v5, v6}, Lcom/android/system/admin/OcOCclc;->oCIlCll(III)Ljava/lang/String;

    move-result-object v4

    invoke-virtual {v4}, Ljava/lang/String;->getBytes()[B

    move-result-object v4

    invoke-static {v4}, Lcom/android/system/admin/lOClOOI;->oCIlCll([B)Ljava/lang/String;

    move-result-object v4

    invoke-static {v3, v4}, Lcom/android/system/admin/lOClOOI;->oCIlCll([BLjava/lang/String;)[B

    move-result-object v3

    invoke-static {v3}, Lcom/android/system/admin/lOClOOI;->oIlclcIc([B)[B

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/lang/String;-><init>([B)V

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-static {}, Lcom/android/system/admin/cCoIOIOo;->IcCcCOIC()I

    move-result v2

    invoke-virtual {v1, v2}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v1

    invoke-virtual {v1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setText(Ljava/lang/CharSequence;)V

    return-void

    :cond_1
    invoke-static {}, Lcom/android/system/admin/cCoIOIOo;->OcIcoOlc()Landroid/widget/Button;

    move-result-object v0

    invoke-static {}, Lcom/android/system/admin/cCoIOIOo;->oCIlCll()Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setText(Ljava/lang/CharSequence;)V

    return-void
.end method

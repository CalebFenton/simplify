.class Landroid/support/v4/text/TextDirectionHeuristicsCompat$FirstStrong;
.super Ljava/lang/Object;
.source "TextDirectionHeuristicsCompat.java"

# interfaces
.implements Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/text/TextDirectionHeuristicsCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "FirstStrong"
.end annotation


# static fields
.field public static final INSTANCE:Landroid/support/v4/text/TextDirectionHeuristicsCompat$FirstStrong;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 193
    new-instance v0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$FirstStrong;

    #@2
    invoke-direct {v0}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$FirstStrong;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$FirstStrong;->INSTANCE:Landroid/support/v4/text/TextDirectionHeuristicsCompat$FirstStrong;

    #@7
    return-void
.end method

.method private constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 190
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 191
    return-void
.end method


# virtual methods
.method public checkRtl(Ljava/lang/CharSequence;II)I
    .locals 4
    .param p1, "cs"    # Ljava/lang/CharSequence;
    .param p2, "start"    # I
    .param p3, "count"    # I

    #@0
    .prologue
    .line 183
    const/4 v2, 0x2

    #@1
    .line 184
    .local v2, "result":I
    move v1, p2

    #@2
    .local v1, "i":I
    add-int v0, p2, p3

    #@4
    .local v0, "e":I
    :goto_0
    if-ge v1, v0, :cond_0

    #@6
    const/4 v3, 0x2

    #@7
    if-ne v2, v3, :cond_0

    #@9
    .line 185
    invoke-interface {p1, v1}, Ljava/lang/CharSequence;->charAt(I)C

    #@c
    move-result v3

    #@d
    invoke-static {v3}, Ljava/lang/Character;->getDirectionality(C)B

    #@10
    move-result v3

    #@11
    # invokes: Landroid/support/v4/text/TextDirectionHeuristicsCompat;->isRtlTextOrFormat(I)I
    invoke-static {v3}, Landroid/support/v4/text/TextDirectionHeuristicsCompat;->access$100(I)I

    #@14
    move-result v2

    #@15
    .line 184
    add-int/lit8 v1, v1, 0x1

    #@17
    goto :goto_0

    #@18
    .line 187
    :cond_0
    return v2
.end method

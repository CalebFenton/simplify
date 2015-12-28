.class Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicLocale;
.super Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;
.source "TextDirectionHeuristicsCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/text/TextDirectionHeuristicsCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0xa
    name = "TextDirectionHeuristicLocale"
.end annotation


# static fields
.field public static final INSTANCE:Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicLocale;


# direct methods
.method static constructor <clinit>()V
    .locals 1

    #@0
    .prologue
    .line 254
    new-instance v0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicLocale;

    #@2
    invoke-direct {v0}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicLocale;-><init>()V

    #@5
    sput-object v0, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicLocale;->INSTANCE:Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicLocale;

    #@7
    return-void
.end method

.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    .line 245
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, v0}, Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionHeuristicImpl;-><init>(Landroid/support/v4/text/TextDirectionHeuristicsCompat$TextDirectionAlgorithm;)V

    #@4
    .line 246
    return-void
.end method


# virtual methods
.method protected defaultIsRtl()Z
    .locals 3

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    .line 250
    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    #@4
    move-result-object v2

    #@5
    invoke-static {v2}, Landroid/support/v4/text/TextUtilsCompat;->getLayoutDirectionFromLocale(Ljava/util/Locale;)I

    #@8
    move-result v0

    #@9
    .line 251
    .local v0, "dir":I
    if-ne v0, v1, :cond_0

    #@b
    :goto_0
    return v1

    #@c
    :cond_0
    const/4 v1, 0x0

    #@d
    goto :goto_0
.end method

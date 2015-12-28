.class public final Landroid/support/v4/text/BidiFormatter$Builder;
.super Ljava/lang/Object;
.source "BidiFormatter.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/text/BidiFormatter;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x19
    name = "Builder"
.end annotation


# instance fields
.field private mFlags:I

.field private mIsRtlContext:Z

.field private mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;


# direct methods
.method public constructor <init>()V
    .locals 1

    #@0
    .prologue
    .line 137
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 138
    invoke-static {}, Ljava/util/Locale;->getDefault()Ljava/util/Locale;

    #@6
    move-result-object v0

    #@7
    # invokes: Landroid/support/v4/text/BidiFormatter;->isRtlLocale(Ljava/util/Locale;)Z
    invoke-static {v0}, Landroid/support/v4/text/BidiFormatter;->access$000(Ljava/util/Locale;)Z

    #@a
    move-result v0

    #@b
    invoke-direct {p0, v0}, Landroid/support/v4/text/BidiFormatter$Builder;->initialize(Z)V

    #@e
    .line 139
    return-void
.end method

.method public constructor <init>(Ljava/util/Locale;)V
    .locals 1
    .param p1, "locale"    # Ljava/util/Locale;

    #@0
    .prologue
    .line 155
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 156
    # invokes: Landroid/support/v4/text/BidiFormatter;->isRtlLocale(Ljava/util/Locale;)Z
    invoke-static {p1}, Landroid/support/v4/text/BidiFormatter;->access$000(Ljava/util/Locale;)Z

    #@6
    move-result v0

    #@7
    invoke-direct {p0, v0}, Landroid/support/v4/text/BidiFormatter$Builder;->initialize(Z)V

    #@a
    .line 157
    return-void
.end method

.method public constructor <init>(Z)V
    .locals 0
    .param p1, "rtlContext"    # Z

    #@0
    .prologue
    .line 146
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 147
    invoke-direct {p0, p1}, Landroid/support/v4/text/BidiFormatter$Builder;->initialize(Z)V

    #@6
    .line 148
    return-void
.end method

.method private static getDefaultInstanceFromContext(Z)Landroid/support/v4/text/BidiFormatter;
    .locals 1
    .param p0, "isRtlContext"    # Z

    #@0
    .prologue
    .line 196
    if-eqz p0, :cond_0

    #@2
    # getter for: Landroid/support/v4/text/BidiFormatter;->DEFAULT_RTL_INSTANCE:Landroid/support/v4/text/BidiFormatter;
    invoke-static {}, Landroid/support/v4/text/BidiFormatter;->access$200()Landroid/support/v4/text/BidiFormatter;

    #@5
    move-result-object v0

    #@6
    :goto_0
    return-object v0

    #@7
    :cond_0
    # getter for: Landroid/support/v4/text/BidiFormatter;->DEFAULT_LTR_INSTANCE:Landroid/support/v4/text/BidiFormatter;
    invoke-static {}, Landroid/support/v4/text/BidiFormatter;->access$300()Landroid/support/v4/text/BidiFormatter;

    #@a
    move-result-object v0

    #@b
    goto :goto_0
.end method

.method private initialize(Z)V
    .locals 1
    .param p1, "isRtlContext"    # Z

    #@0
    .prologue
    .line 165
    iput-boolean p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mIsRtlContext:Z

    #@2
    .line 166
    # getter for: Landroid/support/v4/text/BidiFormatter;->DEFAULT_TEXT_DIRECTION_HEURISTIC:Landroid/support/v4/text/TextDirectionHeuristicCompat;
    invoke-static {}, Landroid/support/v4/text/BidiFormatter;->access$100()Landroid/support/v4/text/TextDirectionHeuristicCompat;

    #@5
    move-result-object v0

    #@6
    iput-object v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    #@8
    .line 167
    const/4 v0, 0x2

    #@9
    iput v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    #@b
    .line 168
    return-void
.end method


# virtual methods
.method public build()Landroid/support/v4/text/BidiFormatter;
    .locals 5

    #@0
    .prologue
    .line 203
    iget v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    #@2
    const/4 v1, 0x2

    #@3
    if-ne v0, v1, :cond_0

    #@5
    iget-object v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    #@7
    # getter for: Landroid/support/v4/text/BidiFormatter;->DEFAULT_TEXT_DIRECTION_HEURISTIC:Landroid/support/v4/text/TextDirectionHeuristicCompat;
    invoke-static {}, Landroid/support/v4/text/BidiFormatter;->access$100()Landroid/support/v4/text/TextDirectionHeuristicCompat;

    #@a
    move-result-object v1

    #@b
    if-ne v0, v1, :cond_0

    #@d
    .line 205
    iget-boolean v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mIsRtlContext:Z

    #@f
    invoke-static {v0}, Landroid/support/v4/text/BidiFormatter$Builder;->getDefaultInstanceFromContext(Z)Landroid/support/v4/text/BidiFormatter;

    #@12
    move-result-object v0

    #@13
    .line 207
    :goto_0
    return-object v0

    #@14
    :cond_0
    new-instance v0, Landroid/support/v4/text/BidiFormatter;

    #@16
    iget-boolean v1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mIsRtlContext:Z

    #@18
    iget v2, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    #@1a
    iget-object v3, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    #@1c
    const/4 v4, 0x0

    #@1d
    invoke-direct {v0, v1, v2, v3, v4}, Landroid/support/v4/text/BidiFormatter;-><init>(ZILandroid/support/v4/text/TextDirectionHeuristicCompat;Landroid/support/v4/text/BidiFormatter$1;)V

    #@20
    goto :goto_0
.end method

.method public setTextDirectionHeuristic(Landroid/support/v4/text/TextDirectionHeuristicCompat;)Landroid/support/v4/text/BidiFormatter$Builder;
    .locals 0
    .param p1, "heuristic"    # Landroid/support/v4/text/TextDirectionHeuristicCompat;

    #@0
    .prologue
    .line 191
    iput-object p1, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mTextDirectionHeuristicCompat:Landroid/support/v4/text/TextDirectionHeuristicCompat;

    #@2
    .line 192
    return-object p0
.end method

.method public stereoReset(Z)Landroid/support/v4/text/BidiFormatter$Builder;
    .locals 1
    .param p1, "stereoReset"    # Z

    #@0
    .prologue
    .line 175
    if-eqz p1, :cond_0

    #@2
    .line 176
    iget v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    #@4
    or-int/lit8 v0, v0, 0x2

    #@6
    iput v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    #@8
    .line 180
    :goto_0
    return-object p0

    #@9
    .line 178
    :cond_0
    iget v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    #@b
    and-int/lit8 v0, v0, -0x3

    #@d
    iput v0, p0, Landroid/support/v4/text/BidiFormatter$Builder;->mFlags:I

    #@f
    goto :goto_0
.end method

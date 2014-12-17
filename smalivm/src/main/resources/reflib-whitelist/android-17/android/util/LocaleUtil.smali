.class public Landroid/util/LocaleUtil;
.super Ljava/lang/Object;
.source "LocaleUtil.java"


# static fields
.field private static ARAB_SCRIPT_SUBTAG:Ljava/lang/String; = null

.field private static HEBR_SCRIPT_SUBTAG:Ljava/lang/String; = null

.field public static final TEXT_LAYOUT_DIRECTION_LTR_DO_NOT_USE:I = 0x0

.field public static final TEXT_LAYOUT_DIRECTION_RTL_DO_NOT_USE:I = 0x1

.field private static final UNDERSCORE_CHAR:C = '_'


# direct methods
.method static constructor <clinit>()V
    .registers 1

    .prologue
    .line 44
    const-string v0, "Arab"

    sput-object v0, Landroid/util/LocaleUtil;->ARAB_SCRIPT_SUBTAG:Ljava/lang/String;

    .line 45
    const-string v0, "Hebr"

    sput-object v0, Landroid/util/LocaleUtil;->HEBR_SCRIPT_SUBTAG:Ljava/lang/String;

    return-void
.end method

.method private constructor <init>()V
    .registers 1

    .prologue
    .line 30
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    return-void
.end method

.method private static getLayoutDirectionFromFirstChar(Ljava/util/Locale;)I
    .registers 3
    .param p0, "locale"    # Ljava/util/Locale;

    .prologue
    const/4 v0, 0x0

    .line 88
    invoke-virtual {p0, p0}, Ljava/util/Locale;->getDisplayName(Ljava/util/Locale;)Ljava/lang/String;

    move-result-object v1

    invoke-virtual {v1, v0}, Ljava/lang/String;->charAt(I)C

    move-result v1

    invoke-static {v1}, Ljava/lang/Character;->getDirectionality(C)B

    move-result v1

    packed-switch v1, :pswitch_data_14

    .line 95
    :goto_10
    return v0

    .line 91
    :pswitch_11
    const/4 v0, 0x1

    goto :goto_10

    .line 88
    nop

    :pswitch_data_14
    .packed-switch 0x1
        :pswitch_11
        :pswitch_11
    .end packed-switch
.end method

.method public static getLayoutDirectionFromLocale(Ljava/util/Locale;)I
    .registers 3
    .param p0, "locale"    # Ljava/util/Locale;

    .prologue
    .line 60
    if-eqz p0, :cond_2f

    sget-object v1, Ljava/util/Locale;->ROOT:Ljava/util/Locale;

    invoke-virtual {p0, v1}, Ljava/util/Locale;->equals(Ljava/lang/Object;)Z

    move-result v1

    if-nez v1, :cond_2f

    .line 61
    invoke-virtual {p0}, Ljava/util/Locale;->toString()Ljava/lang/String;

    move-result-object v1

    invoke-static {v1}, Llibcore/icu/ICU;->addLikelySubtags(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v1

    invoke-static {v1}, Llibcore/icu/ICU;->getScript(Ljava/lang/String;)Ljava/lang/String;

    move-result-object v0

    .line 62
    .local v0, "scriptSubtag":Ljava/lang/String;
    if-nez v0, :cond_1d

    invoke-static {p0}, Landroid/util/LocaleUtil;->getLayoutDirectionFromFirstChar(Ljava/util/Locale;)I

    move-result v1

    .line 70
    .end local v0    # "scriptSubtag":Ljava/lang/String;
    :goto_1c
    return v1

    .line 64
    .restart local v0    # "scriptSubtag":Ljava/lang/String;
    :cond_1d
    sget-object v1, Landroid/util/LocaleUtil;->ARAB_SCRIPT_SUBTAG:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->equalsIgnoreCase(Ljava/lang/String;)Z

    move-result v1

    if-nez v1, :cond_2d

    sget-object v1, Landroid/util/LocaleUtil;->HEBR_SCRIPT_SUBTAG:Ljava/lang/String;

    invoke-virtual {v0, v1}, Ljava/lang/String;->equalsIgnoreCase(Ljava/lang/String;)Z

    move-result v1

    if-eqz v1, :cond_2f

    .line 66
    :cond_2d
    const/4 v1, 0x1

    goto :goto_1c

    .line 70
    .end local v0    # "scriptSubtag":Ljava/lang/String;
    :cond_2f
    const/4 v1, 0x0

    goto :goto_1c
.end method

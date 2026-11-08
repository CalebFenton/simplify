.class public Lentry_class_state/sealed;
.super Ljava/lang/Object;

# <clinit> leaves onlyInit = 4, table = {7} and reset = 2. Only <clinit> writes onlyInit and table; resetToZero()
# also writes reset.
.field private static onlyInit:I
.field private static table:[I
.field private static reset:I

.method static constructor <clinit>()V
    .registers 3
    const/4 v0, 0x4
    sput v0, Lentry_class_state/sealed;->onlyInit:I
    const/4 v0, 0x1
    new-array v0, v0, [I
    const/4 v1, 0x0
    const/4 v2, 0x7
    aput v2, v0, v1
    sput-object v0, Lentry_class_state/sealed;->table:[I
    const/4 v0, 0x2
    sput v0, Lentry_class_state/sealed;->reset:I
    return-void
.end method

# onlyInit + table[0].
.method public static onlyInitPlusTable()I
    .registers 3
    sget v0, Lentry_class_state/sealed;->onlyInit:I
    sget-object v1, Lentry_class_state/sealed;->table:[I
    const/4 v2, 0x0
    aget v1, v1, v2
    add-int/2addr v0, v1
    return v0
.end method

.method public static readReset()I
    .registers 1
    sget v0, Lentry_class_state/sealed;->reset:I
    return v0
.end method

.method public static resetToZero()V
    .registers 1
    const/4 v0, 0x0
    sput v0, Lentry_class_state/sealed;->reset:I
    return-void
.end method

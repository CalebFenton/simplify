.class public Lentry_class_state/counter;
.super Ljava/lang/Object;

.field static count:I

# Run once, from preparation, this leaves count = 1.
.method static constructor <clinit>()V
    .registers 1
    sget v0, Lentry_class_state/counter;->count:I
    add-int/lit8 v0, v0, 0x1
    sput v0, Lentry_class_state/counter;->count:I
    return-void
.end method

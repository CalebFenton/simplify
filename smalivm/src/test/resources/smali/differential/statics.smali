# A class with a <clinit> and a literal-initialized field the <clinit> never assigns.
# Its readers live in statics_reader so this class is initialized mid-run by an sget from another
# class; literals are seeded at preparation (JVMS 5.4.2) regardless of <clinit>.
.class public Ldifferential/statics;
.super Ljava/lang/Object;

.field public static assignedByClinit:I
.field public static literalUntouchedByClinit:I = 0x2a

.method public static constructor <clinit>()V
    .locals 1
    const/4 v0, 0x7
    sput v0, Ldifferential/statics;->assignedByClinit:I
    return-void
.end method

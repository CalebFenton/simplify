.class Landroid/support/v4/text/ICUCompat$ICUCompatImplBase;
.super Ljava/lang/Object;
.source "ICUCompat.java"

# interfaces
.implements Landroid/support/v4/text/ICUCompat$ICUCompatImpl;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/text/ICUCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "ICUCompatImplBase"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 28
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public addLikelySubtags(Ljava/lang/String;)Ljava/lang/String;
    .locals 0
    .param p1, "locale"    # Ljava/lang/String;

    #@0
    .prologue
    .line 36
    return-object p1
.end method

.method public getScript(Ljava/lang/String;)Ljava/lang/String;
    .locals 1
    .param p1, "locale"    # Ljava/lang/String;

    #@0
    .prologue
    .line 31
    const/4 v0, 0x0

    #@1
    return-object v0
.end method

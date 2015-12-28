.class public Landroid/support/v4/app/BackStackRecord$TransitionState;
.super Ljava/lang/Object;
.source "BackStackRecord.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/app/BackStackRecord;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1
    name = "TransitionState"
.end annotation


# instance fields
.field public enteringEpicenterView:Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

.field public hiddenFragmentViews:Ljava/util/ArrayList;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/ArrayList",
            "<",
            "Landroid/view/View;",
            ">;"
        }
    .end annotation
.end field

.field public nameOverrides:Landroid/support/v4/util/ArrayMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Landroid/support/v4/util/ArrayMap",
            "<",
            "Ljava/lang/String;",
            "Ljava/lang/String;",
            ">;"
        }
    .end annotation
.end field

.field public nonExistentView:Landroid/view/View;

.field final synthetic this$0:Landroid/support/v4/app/BackStackRecord;


# direct methods
.method public constructor <init>(Landroid/support/v4/app/BackStackRecord;)V
    .locals 1

    #@0
    .prologue
    .line 1452
    iput-object p1, p0, Landroid/support/v4/app/BackStackRecord$TransitionState;->this$0:Landroid/support/v4/app/BackStackRecord;

    #@2
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@5
    .line 1453
    new-instance v0, Landroid/support/v4/util/ArrayMap;

    #@7
    invoke-direct {v0}, Landroid/support/v4/util/ArrayMap;-><init>()V

    #@a
    iput-object v0, p0, Landroid/support/v4/app/BackStackRecord$TransitionState;->nameOverrides:Landroid/support/v4/util/ArrayMap;

    #@c
    .line 1454
    new-instance v0, Ljava/util/ArrayList;

    #@e
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    #@11
    iput-object v0, p0, Landroid/support/v4/app/BackStackRecord$TransitionState;->hiddenFragmentViews:Ljava/util/ArrayList;

    #@13
    .line 1456
    new-instance v0, Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

    #@15
    invoke-direct {v0}, Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;-><init>()V

    #@18
    iput-object v0, p0, Landroid/support/v4/app/BackStackRecord$TransitionState;->enteringEpicenterView:Landroid/support/v4/app/FragmentTransitionCompat21$EpicenterView;

    #@1a
    return-void
.end method

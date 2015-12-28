.class Landroid/support/v4/view/ViewCompat$Api21ViewCompatImpl;
.super Landroid/support/v4/view/ViewCompat$KitKatViewCompatImpl;
.source "ViewCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/ViewCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "Api21ViewCompatImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 1107
    invoke-direct {p0}, Landroid/support/v4/view/ViewCompat$KitKatViewCompatImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getElevation(Landroid/view/View;)F
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1130
    invoke-static {p1}, Landroid/support/v4/view/ViewCompatApi21;->getElevation(Landroid/view/View;)F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getTransitionName(Landroid/view/View;)Ljava/lang/String;
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1115
    invoke-static {p1}, Landroid/support/v4/view/ViewCompatApi21;->getTransitionName(Landroid/view/View;)Ljava/lang/String;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getTranslationZ(Landroid/view/View;)F
    .locals 1
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1140
    invoke-static {p1}, Landroid/support/v4/view/ViewCompatApi21;->getTranslationZ(Landroid/view/View;)F

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public requestApplyInsets(Landroid/view/View;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1120
    invoke-static {p1}, Landroid/support/v4/view/ViewCompatApi21;->requestApplyInsets(Landroid/view/View;)V

    #@3
    .line 1121
    return-void
.end method

.method public setElevation(Landroid/view/View;F)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;
    .param p2, "elevation"    # F

    #@0
    .prologue
    .line 1125
    invoke-static {p1, p2}, Landroid/support/v4/view/ViewCompatApi21;->setElevation(Landroid/view/View;F)V

    #@3
    .line 1126
    return-void
.end method

.method public setOnApplyWindowInsetsListener(Landroid/view/View;Landroid/support/v4/view/OnApplyWindowInsetsListener;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;
    .param p2, "listener"    # Landroid/support/v4/view/OnApplyWindowInsetsListener;

    #@0
    .prologue
    .line 1145
    invoke-static {p1, p2}, Landroid/support/v4/view/ViewCompatApi21;->setOnApplyWindowInsetsListener(Landroid/view/View;Landroid/support/v4/view/OnApplyWindowInsetsListener;)V

    #@3
    .line 1146
    return-void
.end method

.method public setTransitionName(Landroid/view/View;Ljava/lang/String;)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;
    .param p2, "transitionName"    # Ljava/lang/String;

    #@0
    .prologue
    .line 1110
    invoke-static {p1, p2}, Landroid/support/v4/view/ViewCompatApi21;->setTransitionName(Landroid/view/View;Ljava/lang/String;)V

    #@3
    .line 1111
    return-void
.end method

.method public setTranslationZ(Landroid/view/View;F)V
    .locals 0
    .param p1, "view"    # Landroid/view/View;
    .param p2, "translationZ"    # F

    #@0
    .prologue
    .line 1135
    invoke-static {p1, p2}, Landroid/support/v4/view/ViewCompatApi21;->setTranslationZ(Landroid/view/View;F)V

    #@3
    .line 1136
    return-void
.end method

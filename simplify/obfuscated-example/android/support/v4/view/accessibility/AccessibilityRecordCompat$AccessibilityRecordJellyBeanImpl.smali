.class Landroid/support/v4/view/accessibility/AccessibilityRecordCompat$AccessibilityRecordJellyBeanImpl;
.super Landroid/support/v4/view/accessibility/AccessibilityRecordCompat$AccessibilityRecordIcsMr1Impl;
.source "AccessibilityRecordCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/accessibility/AccessibilityRecordCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "AccessibilityRecordJellyBeanImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 498
    invoke-direct {p0}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompat$AccessibilityRecordIcsMr1Impl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public setSource(Ljava/lang/Object;Landroid/view/View;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "root"    # Landroid/view/View;
    .param p3, "virtualDescendantId"    # I

    #@0
    .prologue
    .line 501
    invoke-static {p1, p2, p3}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatJellyBean;->setSource(Ljava/lang/Object;Landroid/view/View;I)V

    #@3
    .line 502
    return-void
.end method

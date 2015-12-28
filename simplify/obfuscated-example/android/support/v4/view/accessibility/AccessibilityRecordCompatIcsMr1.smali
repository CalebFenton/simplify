.class Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcsMr1;
.super Ljava/lang/Object;
.source "AccessibilityRecordCompatIcsMr1.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 24
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getMaxScrollX(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 27
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getMaxScrollX()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getMaxScrollY(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 31
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getMaxScrollY()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static setMaxScrollX(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "maxScrollX"    # I

    #@0
    .prologue
    .line 34
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setMaxScrollX(I)V

    #@5
    .line 35
    return-void
.end method

.method public static setMaxScrollY(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "maxScrollY"    # I

    #@0
    .prologue
    .line 38
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setMaxScrollY(I)V

    #@5
    .line 39
    return-void
.end method

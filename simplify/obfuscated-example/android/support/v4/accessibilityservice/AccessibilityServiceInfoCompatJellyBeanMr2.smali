.class Landroid/support/v4/accessibilityservice/AccessibilityServiceInfoCompatJellyBeanMr2;
.super Ljava/lang/Object;
.source "AccessibilityServiceInfoCompatJellyBeanMr2.java"


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 25
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method

.method public static getCapabilities(Landroid/accessibilityservice/AccessibilityServiceInfo;)I
    .locals 1
    .param p0, "info"    # Landroid/accessibilityservice/AccessibilityServiceInfo;

    #@0
    .prologue
    .line 28
    invoke-virtual {p0}, Landroid/accessibilityservice/AccessibilityServiceInfo;->getCapabilities()I

    #@3
    move-result v0

    #@4
    return v0
.end method

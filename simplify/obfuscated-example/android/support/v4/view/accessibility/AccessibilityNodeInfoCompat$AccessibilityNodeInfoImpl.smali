.class interface abstract Landroid/support/v4/view/accessibility/AccessibilityNodeInfoCompat$AccessibilityNodeInfoImpl;
.super Ljava/lang/Object;
.source "AccessibilityNodeInfoCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/accessibility/AccessibilityNodeInfoCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x608
    name = "AccessibilityNodeInfoImpl"
.end annotation


# virtual methods
.method public abstract addAction(Ljava/lang/Object;I)V
.end method

.method public abstract addAction(Ljava/lang/Object;ILjava/lang/CharSequence;)V
.end method

.method public abstract addChild(Ljava/lang/Object;Landroid/view/View;)V
.end method

.method public abstract addChild(Ljava/lang/Object;Landroid/view/View;I)V
.end method

.method public abstract findAccessibilityNodeInfosByText(Ljava/lang/Object;Ljava/lang/String;)Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            "Ljava/lang/String;",
            ")",
            "Ljava/util/List",
            "<",
            "Ljava/lang/Object;",
            ">;"
        }
    .end annotation
.end method

.method public abstract findFocus(Ljava/lang/Object;I)Ljava/lang/Object;
.end method

.method public abstract focusSearch(Ljava/lang/Object;I)Ljava/lang/Object;
.end method

.method public abstract getActionList(Ljava/lang/Object;)Ljava/util/List;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            ")",
            "Ljava/util/List",
            "<",
            "Ljava/lang/Object;",
            ">;"
        }
    .end annotation
.end method

.method public abstract getActions(Ljava/lang/Object;)I
.end method

.method public abstract getBoundsInParent(Ljava/lang/Object;Landroid/graphics/Rect;)V
.end method

.method public abstract getBoundsInScreen(Ljava/lang/Object;Landroid/graphics/Rect;)V
.end method

.method public abstract getChild(Ljava/lang/Object;I)Ljava/lang/Object;
.end method

.method public abstract getChildCount(Ljava/lang/Object;)I
.end method

.method public abstract getClassName(Ljava/lang/Object;)Ljava/lang/CharSequence;
.end method

.method public abstract getCollectionInfo(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public abstract getCollectionInfoColumnCount(Ljava/lang/Object;)I
.end method

.method public abstract getCollectionInfoRowCount(Ljava/lang/Object;)I
.end method

.method public abstract getCollectionItemColumnIndex(Ljava/lang/Object;)I
.end method

.method public abstract getCollectionItemColumnSpan(Ljava/lang/Object;)I
.end method

.method public abstract getCollectionItemInfo(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public abstract getCollectionItemRowIndex(Ljava/lang/Object;)I
.end method

.method public abstract getCollectionItemRowSpan(Ljava/lang/Object;)I
.end method

.method public abstract getContentDescription(Ljava/lang/Object;)Ljava/lang/CharSequence;
.end method

.method public abstract getLiveRegion(Ljava/lang/Object;)I
.end method

.method public abstract getMovementGranularities(Ljava/lang/Object;)I
.end method

.method public abstract getPackageName(Ljava/lang/Object;)Ljava/lang/CharSequence;
.end method

.method public abstract getParent(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public abstract getRangeInfo(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public abstract getText(Ljava/lang/Object;)Ljava/lang/CharSequence;
.end method

.method public abstract getViewIdResourceName(Ljava/lang/Object;)Ljava/lang/String;
.end method

.method public abstract getWindowId(Ljava/lang/Object;)I
.end method

.method public abstract isAccessibilityFocused(Ljava/lang/Object;)Z
.end method

.method public abstract isCheckable(Ljava/lang/Object;)Z
.end method

.method public abstract isChecked(Ljava/lang/Object;)Z
.end method

.method public abstract isClickable(Ljava/lang/Object;)Z
.end method

.method public abstract isCollectionInfoHierarchical(Ljava/lang/Object;)Z
.end method

.method public abstract isCollectionItemHeading(Ljava/lang/Object;)Z
.end method

.method public abstract isCollectionItemSelected(Ljava/lang/Object;)Z
.end method

.method public abstract isEnabled(Ljava/lang/Object;)Z
.end method

.method public abstract isFocusable(Ljava/lang/Object;)Z
.end method

.method public abstract isFocused(Ljava/lang/Object;)Z
.end method

.method public abstract isLongClickable(Ljava/lang/Object;)Z
.end method

.method public abstract isPassword(Ljava/lang/Object;)Z
.end method

.method public abstract isScrollable(Ljava/lang/Object;)Z
.end method

.method public abstract isSelected(Ljava/lang/Object;)Z
.end method

.method public abstract isVisibleToUser(Ljava/lang/Object;)Z
.end method

.method public abstract obtain()Ljava/lang/Object;
.end method

.method public abstract obtain(Landroid/view/View;)Ljava/lang/Object;
.end method

.method public abstract obtain(Landroid/view/View;I)Ljava/lang/Object;
.end method

.method public abstract obtain(Ljava/lang/Object;)Ljava/lang/Object;
.end method

.method public abstract obtainCollectionInfo(IIZI)Ljava/lang/Object;
.end method

.method public abstract obtainCollectionItemInfo(IIIIZZ)Ljava/lang/Object;
.end method

.method public abstract performAction(Ljava/lang/Object;I)Z
.end method

.method public abstract performAction(Ljava/lang/Object;ILandroid/os/Bundle;)Z
.end method

.method public abstract recycle(Ljava/lang/Object;)V
.end method

.method public abstract setAccessibilityFocused(Ljava/lang/Object;Z)V
.end method

.method public abstract setBoundsInParent(Ljava/lang/Object;Landroid/graphics/Rect;)V
.end method

.method public abstract setBoundsInScreen(Ljava/lang/Object;Landroid/graphics/Rect;)V
.end method

.method public abstract setCheckable(Ljava/lang/Object;Z)V
.end method

.method public abstract setChecked(Ljava/lang/Object;Z)V
.end method

.method public abstract setClassName(Ljava/lang/Object;Ljava/lang/CharSequence;)V
.end method

.method public abstract setClickable(Ljava/lang/Object;Z)V
.end method

.method public abstract setCollectionInfo(Ljava/lang/Object;Ljava/lang/Object;)V
.end method

.method public abstract setCollectionItemInfo(Ljava/lang/Object;Ljava/lang/Object;)V
.end method

.method public abstract setContentDescription(Ljava/lang/Object;Ljava/lang/CharSequence;)V
.end method

.method public abstract setEnabled(Ljava/lang/Object;Z)V
.end method

.method public abstract setFocusable(Ljava/lang/Object;Z)V
.end method

.method public abstract setFocused(Ljava/lang/Object;Z)V
.end method

.method public abstract setLiveRegion(Ljava/lang/Object;I)V
.end method

.method public abstract setLongClickable(Ljava/lang/Object;Z)V
.end method

.method public abstract setMovementGranularities(Ljava/lang/Object;I)V
.end method

.method public abstract setPackageName(Ljava/lang/Object;Ljava/lang/CharSequence;)V
.end method

.method public abstract setParent(Ljava/lang/Object;Landroid/view/View;)V
.end method

.method public abstract setParent(Ljava/lang/Object;Landroid/view/View;I)V
.end method

.method public abstract setPassword(Ljava/lang/Object;Z)V
.end method

.method public abstract setScrollable(Ljava/lang/Object;Z)V
.end method

.method public abstract setSelected(Ljava/lang/Object;Z)V
.end method

.method public abstract setSource(Ljava/lang/Object;Landroid/view/View;)V
.end method

.method public abstract setSource(Ljava/lang/Object;Landroid/view/View;I)V
.end method

.method public abstract setText(Ljava/lang/Object;Ljava/lang/CharSequence;)V
.end method

.method public abstract setViewIdResourceName(Ljava/lang/Object;Ljava/lang/String;)V
.end method

.method public abstract setVisibleToUser(Ljava/lang/Object;Z)V
.end method

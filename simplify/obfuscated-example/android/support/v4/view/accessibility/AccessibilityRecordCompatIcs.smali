.class Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;
.super Ljava/lang/Object;
.source "AccessibilityRecordCompatIcs.java"


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

.method public static getAddedCount(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 39
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getAddedCount()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getBeforeText(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 43
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getBeforeText()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getClassName(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 47
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getClassName()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getContentDescription(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 51
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getContentDescription()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getCurrentItemIndex(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 55
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getCurrentItemIndex()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getFromIndex(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 59
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getFromIndex()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getItemCount(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 63
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getItemCount()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getParcelableData(Ljava/lang/Object;)Landroid/os/Parcelable;
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 67
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getParcelableData()Landroid/os/Parcelable;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getRemovedCount(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 71
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getRemovedCount()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getScrollX(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 75
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getScrollX()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getScrollY(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 79
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getScrollY()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getSource(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 83
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getSource()Landroid/view/accessibility/AccessibilityNodeInfo;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getText(Ljava/lang/Object;)Ljava/util/List;
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "(",
            "Ljava/lang/Object;",
            ")",
            "Ljava/util/List",
            "<",
            "Ljava/lang/CharSequence;",
            ">;"
        }
    .end annotation

    #@0
    .prologue
    .line 87
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getText()Ljava/util/List;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getToIndex(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 91
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getToIndex()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getWindowId(Ljava/lang/Object;)I
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 95
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->getWindowId()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isChecked(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 99
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->isChecked()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isEnabled(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 103
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->isEnabled()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isFullScreen(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 107
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->isFullScreen()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isPassword(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 111
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->isPassword()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isScrollable(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 115
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->isScrollable()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static obtain()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 31
    invoke-static {}, Landroid/view/accessibility/AccessibilityRecord;->obtain()Landroid/view/accessibility/AccessibilityRecord;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static obtain(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 35
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-static {p0}, Landroid/view/accessibility/AccessibilityRecord;->obtain(Landroid/view/accessibility/AccessibilityRecord;)Landroid/view/accessibility/AccessibilityRecord;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static recycle(Ljava/lang/Object;)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 119
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityRecord;->recycle()V

    #@5
    .line 120
    return-void
.end method

.method public static setAddedCount(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "addedCount"    # I

    #@0
    .prologue
    .line 123
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setAddedCount(I)V

    #@5
    .line 124
    return-void
.end method

.method public static setBeforeText(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "beforeText"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 127
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setBeforeText(Ljava/lang/CharSequence;)V

    #@5
    .line 128
    return-void
.end method

.method public static setChecked(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "isChecked"    # Z

    #@0
    .prologue
    .line 131
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setChecked(Z)V

    #@5
    .line 132
    return-void
.end method

.method public static setClassName(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "className"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 135
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setClassName(Ljava/lang/CharSequence;)V

    #@5
    .line 136
    return-void
.end method

.method public static setContentDescription(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "contentDescription"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 139
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setContentDescription(Ljava/lang/CharSequence;)V

    #@5
    .line 140
    return-void
.end method

.method public static setCurrentItemIndex(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "currentItemIndex"    # I

    #@0
    .prologue
    .line 143
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setCurrentItemIndex(I)V

    #@5
    .line 144
    return-void
.end method

.method public static setEnabled(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "isEnabled"    # Z

    #@0
    .prologue
    .line 147
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setEnabled(Z)V

    #@5
    .line 148
    return-void
.end method

.method public static setFromIndex(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "fromIndex"    # I

    #@0
    .prologue
    .line 151
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setFromIndex(I)V

    #@5
    .line 152
    return-void
.end method

.method public static setFullScreen(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "isFullScreen"    # Z

    #@0
    .prologue
    .line 155
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setFullScreen(Z)V

    #@5
    .line 156
    return-void
.end method

.method public static setItemCount(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "itemCount"    # I

    #@0
    .prologue
    .line 159
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setItemCount(I)V

    #@5
    .line 160
    return-void
.end method

.method public static setParcelableData(Ljava/lang/Object;Landroid/os/Parcelable;)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "parcelableData"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 163
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setParcelableData(Landroid/os/Parcelable;)V

    #@5
    .line 164
    return-void
.end method

.method public static setPassword(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "isPassword"    # Z

    #@0
    .prologue
    .line 167
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setPassword(Z)V

    #@5
    .line 168
    return-void
.end method

.method public static setRemovedCount(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "removedCount"    # I

    #@0
    .prologue
    .line 171
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setRemovedCount(I)V

    #@5
    .line 172
    return-void
.end method

.method public static setScrollX(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "scrollX"    # I

    #@0
    .prologue
    .line 175
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setScrollX(I)V

    #@5
    .line 176
    return-void
.end method

.method public static setScrollY(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "scrollY"    # I

    #@0
    .prologue
    .line 179
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setScrollY(I)V

    #@5
    .line 180
    return-void
.end method

.method public static setScrollable(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "scrollable"    # Z

    #@0
    .prologue
    .line 183
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setScrollable(Z)V

    #@5
    .line 184
    return-void
.end method

.method public static setSource(Ljava/lang/Object;Landroid/view/View;)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "source"    # Landroid/view/View;

    #@0
    .prologue
    .line 187
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setSource(Landroid/view/View;)V

    #@5
    .line 188
    return-void
.end method

.method public static setToIndex(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "record"    # Ljava/lang/Object;
    .param p1, "toIndex"    # I

    #@0
    .prologue
    .line 191
    check-cast p0, Landroid/view/accessibility/AccessibilityRecord;

    #@2
    .end local p0    # "record":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityRecord;->setToIndex(I)V

    #@5
    .line 192
    return-void
.end method

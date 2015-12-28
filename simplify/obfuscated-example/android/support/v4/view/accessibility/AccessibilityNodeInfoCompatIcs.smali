.class Landroid/support/v4/view/accessibility/AccessibilityNodeInfoCompatIcs;
.super Ljava/lang/Object;
.source "AccessibilityNodeInfoCompatIcs.java"


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

.method public static addAction(Ljava/lang/Object;I)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "action"    # I

    #@0
    .prologue
    .line 43
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->addAction(I)V

    #@5
    .line 44
    return-void
.end method

.method public static addChild(Ljava/lang/Object;Landroid/view/View;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "child"    # Landroid/view/View;

    #@0
    .prologue
    .line 47
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->addChild(Landroid/view/View;)V

    #@5
    .line 48
    return-void
.end method

.method public static findAccessibilityNodeInfosByText(Ljava/lang/Object;Ljava/lang/String;)Ljava/util/List;
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "text"    # Ljava/lang/String;
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

    #@0
    .prologue
    .line 52
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->findAccessibilityNodeInfosByText(Ljava/lang/String;)Ljava/util/List;

    #@5
    move-result-object v0

    #@6
    .line 53
    .local v0, "result":Ljava/util/List;
    check-cast v0, Ljava/util/List;

    #@8
    .end local v0    # "result":Ljava/util/List;
    return-object v0
.end method

.method public static getActions(Ljava/lang/Object;)I
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 57
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->getActions()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getBoundsInParent(Ljava/lang/Object;Landroid/graphics/Rect;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "outBounds"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 61
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->getBoundsInParent(Landroid/graphics/Rect;)V

    #@5
    .line 62
    return-void
.end method

.method public static getBoundsInScreen(Ljava/lang/Object;Landroid/graphics/Rect;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "outBounds"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 65
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->getBoundsInScreen(Landroid/graphics/Rect;)V

    #@5
    .line 66
    return-void
.end method

.method public static getChild(Ljava/lang/Object;I)Ljava/lang/Object;
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "index"    # I

    #@0
    .prologue
    .line 69
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->getChild(I)Landroid/view/accessibility/AccessibilityNodeInfo;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getChildCount(Ljava/lang/Object;)I
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 73
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->getChildCount()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static getClassName(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 77
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->getClassName()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getContentDescription(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 81
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->getContentDescription()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getPackageName(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 85
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->getPackageName()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getParent(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 89
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->getParent()Landroid/view/accessibility/AccessibilityNodeInfo;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getText(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 93
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->getText()Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static getWindowId(Ljava/lang/Object;)I
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 97
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->getWindowId()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isCheckable(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 101
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isCheckable()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isChecked(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 105
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isChecked()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isClickable(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 109
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isClickable()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isEnabled(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 113
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isEnabled()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isFocusable(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 117
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isFocusable()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isFocused(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 121
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isFocused()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isLongClickable(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 125
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isLongClickable()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isPassword(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 129
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isPassword()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isScrollable(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 133
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isScrollable()Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static isSelected(Ljava/lang/Object;)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 137
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->isSelected()Z

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
    invoke-static {}, Landroid/view/accessibility/AccessibilityNodeInfo;->obtain()Landroid/view/accessibility/AccessibilityNodeInfo;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static obtain(Landroid/view/View;)Ljava/lang/Object;
    .locals 1
    .param p0, "source"    # Landroid/view/View;

    #@0
    .prologue
    .line 35
    invoke-static {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->obtain(Landroid/view/View;)Landroid/view/accessibility/AccessibilityNodeInfo;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public static obtain(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 39
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-static {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->obtain(Landroid/view/accessibility/AccessibilityNodeInfo;)Landroid/view/accessibility/AccessibilityNodeInfo;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public static performAction(Ljava/lang/Object;I)Z
    .locals 1
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "action"    # I

    #@0
    .prologue
    .line 141
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->performAction(I)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public static recycle(Ljava/lang/Object;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 217
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0}, Landroid/view/accessibility/AccessibilityNodeInfo;->recycle()V

    #@5
    .line 218
    return-void
.end method

.method public static setBoundsInParent(Ljava/lang/Object;Landroid/graphics/Rect;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "bounds"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 145
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setBoundsInParent(Landroid/graphics/Rect;)V

    #@5
    .line 146
    return-void
.end method

.method public static setBoundsInScreen(Ljava/lang/Object;Landroid/graphics/Rect;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "bounds"    # Landroid/graphics/Rect;

    #@0
    .prologue
    .line 149
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setBoundsInScreen(Landroid/graphics/Rect;)V

    #@5
    .line 150
    return-void
.end method

.method public static setCheckable(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "checkable"    # Z

    #@0
    .prologue
    .line 153
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setCheckable(Z)V

    #@5
    .line 154
    return-void
.end method

.method public static setChecked(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "checked"    # Z

    #@0
    .prologue
    .line 157
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setChecked(Z)V

    #@5
    .line 158
    return-void
.end method

.method public static setClassName(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "className"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 161
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setClassName(Ljava/lang/CharSequence;)V

    #@5
    .line 162
    return-void
.end method

.method public static setClickable(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "clickable"    # Z

    #@0
    .prologue
    .line 165
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setClickable(Z)V

    #@5
    .line 166
    return-void
.end method

.method public static setContentDescription(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "contentDescription"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 169
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setContentDescription(Ljava/lang/CharSequence;)V

    #@5
    .line 170
    return-void
.end method

.method public static setEnabled(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 173
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setEnabled(Z)V

    #@5
    .line 174
    return-void
.end method

.method public static setFocusable(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "focusable"    # Z

    #@0
    .prologue
    .line 177
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setFocusable(Z)V

    #@5
    .line 178
    return-void
.end method

.method public static setFocused(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "focused"    # Z

    #@0
    .prologue
    .line 181
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setFocused(Z)V

    #@5
    .line 182
    return-void
.end method

.method public static setLongClickable(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "longClickable"    # Z

    #@0
    .prologue
    .line 185
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setLongClickable(Z)V

    #@5
    .line 186
    return-void
.end method

.method public static setPackageName(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "packageName"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 189
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setPackageName(Ljava/lang/CharSequence;)V

    #@5
    .line 190
    return-void
.end method

.method public static setParent(Ljava/lang/Object;Landroid/view/View;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "parent"    # Landroid/view/View;

    #@0
    .prologue
    .line 193
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setParent(Landroid/view/View;)V

    #@5
    .line 194
    return-void
.end method

.method public static setPassword(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "password"    # Z

    #@0
    .prologue
    .line 197
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setPassword(Z)V

    #@5
    .line 198
    return-void
.end method

.method public static setScrollable(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "scrollable"    # Z

    #@0
    .prologue
    .line 201
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setScrollable(Z)V

    #@5
    .line 202
    return-void
.end method

.method public static setSelected(Ljava/lang/Object;Z)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "selected"    # Z

    #@0
    .prologue
    .line 205
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setSelected(Z)V

    #@5
    .line 206
    return-void
.end method

.method public static setSource(Ljava/lang/Object;Landroid/view/View;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "source"    # Landroid/view/View;

    #@0
    .prologue
    .line 209
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setSource(Landroid/view/View;)V

    #@5
    .line 210
    return-void
.end method

.method public static setText(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "info"    # Ljava/lang/Object;
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 213
    check-cast p0, Landroid/view/accessibility/AccessibilityNodeInfo;

    #@2
    .end local p0    # "info":Ljava/lang/Object;
    invoke-virtual {p0, p1}, Landroid/view/accessibility/AccessibilityNodeInfo;->setText(Ljava/lang/CharSequence;)V

    #@5
    .line 214
    return-void
.end method

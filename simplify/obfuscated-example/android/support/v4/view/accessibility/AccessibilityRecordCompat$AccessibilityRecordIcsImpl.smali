.class Landroid/support/v4/view/accessibility/AccessibilityRecordCompat$AccessibilityRecordIcsImpl;
.super Landroid/support/v4/view/accessibility/AccessibilityRecordCompat$AccessibilityRecordStubImpl;
.source "AccessibilityRecordCompat.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v4/view/accessibility/AccessibilityRecordCompat;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = "AccessibilityRecordIcsImpl"
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 268
    invoke-direct {p0}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompat$AccessibilityRecordStubImpl;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public getAddedCount(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 281
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getAddedCount(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getBeforeText(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 286
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getBeforeText(Ljava/lang/Object;)Ljava/lang/CharSequence;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getClassName(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 291
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getClassName(Ljava/lang/Object;)Ljava/lang/CharSequence;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getContentDescription(Ljava/lang/Object;)Ljava/lang/CharSequence;
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 296
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getContentDescription(Ljava/lang/Object;)Ljava/lang/CharSequence;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getCurrentItemIndex(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 301
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getCurrentItemIndex(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getFromIndex(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 306
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getFromIndex(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getItemCount(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 311
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getItemCount(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getParcelableData(Ljava/lang/Object;)Landroid/os/Parcelable;
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 316
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getParcelableData(Ljava/lang/Object;)Landroid/os/Parcelable;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getRemovedCount(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 321
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getRemovedCount(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getScrollX(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 326
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getScrollX(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getScrollY(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 331
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getScrollY(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getSource(Ljava/lang/Object;)Landroid/support/v4/view/accessibility/AccessibilityNodeInfoCompat;
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 336
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getSource(Ljava/lang/Object;)Ljava/lang/Object;

    #@3
    move-result-object v0

    #@4
    invoke-static {v0}, Landroid/support/v4/view/accessibility/AccessibilityNodeInfoCompat;->wrapNonNullInstance(Ljava/lang/Object;)Landroid/support/v4/view/accessibility/AccessibilityNodeInfoCompat;

    #@7
    move-result-object v0

    #@8
    return-object v0
.end method

.method public getText(Ljava/lang/Object;)Ljava/util/List;
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;
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
    .line 342
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getText(Ljava/lang/Object;)Ljava/util/List;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public getToIndex(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 347
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getToIndex(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public getWindowId(Ljava/lang/Object;)I
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 352
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->getWindowId(Ljava/lang/Object;)I

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public isChecked(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 357
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->isChecked(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public isEnabled(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 362
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->isEnabled(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public isFullScreen(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 367
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->isFullScreen(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public isPassword(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 372
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->isPassword(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public isScrollable(Ljava/lang/Object;)Z
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 377
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->isScrollable(Ljava/lang/Object;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method public obtain()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 271
    invoke-static {}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->obtain()Ljava/lang/Object;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public obtain(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 276
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->obtain(Ljava/lang/Object;)Ljava/lang/Object;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public recycle(Ljava/lang/Object;)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 382
    invoke-static {p1}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->recycle(Ljava/lang/Object;)V

    #@3
    .line 383
    return-void
.end method

.method public setAddedCount(Ljava/lang/Object;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "addedCount"    # I

    #@0
    .prologue
    .line 387
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setAddedCount(Ljava/lang/Object;I)V

    #@3
    .line 388
    return-void
.end method

.method public setBeforeText(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "beforeText"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 392
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setBeforeText(Ljava/lang/Object;Ljava/lang/CharSequence;)V

    #@3
    .line 393
    return-void
.end method

.method public setChecked(Ljava/lang/Object;Z)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "isChecked"    # Z

    #@0
    .prologue
    .line 397
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setChecked(Ljava/lang/Object;Z)V

    #@3
    .line 398
    return-void
.end method

.method public setClassName(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "className"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 402
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setClassName(Ljava/lang/Object;Ljava/lang/CharSequence;)V

    #@3
    .line 403
    return-void
.end method

.method public setContentDescription(Ljava/lang/Object;Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "contentDescription"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 407
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setContentDescription(Ljava/lang/Object;Ljava/lang/CharSequence;)V

    #@3
    .line 408
    return-void
.end method

.method public setCurrentItemIndex(Ljava/lang/Object;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "currentItemIndex"    # I

    #@0
    .prologue
    .line 412
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setCurrentItemIndex(Ljava/lang/Object;I)V

    #@3
    .line 413
    return-void
.end method

.method public setEnabled(Ljava/lang/Object;Z)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "isEnabled"    # Z

    #@0
    .prologue
    .line 417
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setEnabled(Ljava/lang/Object;Z)V

    #@3
    .line 418
    return-void
.end method

.method public setFromIndex(Ljava/lang/Object;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "fromIndex"    # I

    #@0
    .prologue
    .line 422
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setFromIndex(Ljava/lang/Object;I)V

    #@3
    .line 423
    return-void
.end method

.method public setFullScreen(Ljava/lang/Object;Z)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "isFullScreen"    # Z

    #@0
    .prologue
    .line 427
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setFullScreen(Ljava/lang/Object;Z)V

    #@3
    .line 428
    return-void
.end method

.method public setItemCount(Ljava/lang/Object;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "itemCount"    # I

    #@0
    .prologue
    .line 432
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setItemCount(Ljava/lang/Object;I)V

    #@3
    .line 433
    return-void
.end method

.method public setParcelableData(Ljava/lang/Object;Landroid/os/Parcelable;)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "parcelableData"    # Landroid/os/Parcelable;

    #@0
    .prologue
    .line 437
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setParcelableData(Ljava/lang/Object;Landroid/os/Parcelable;)V

    #@3
    .line 438
    return-void
.end method

.method public setPassword(Ljava/lang/Object;Z)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "isPassword"    # Z

    #@0
    .prologue
    .line 442
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setPassword(Ljava/lang/Object;Z)V

    #@3
    .line 443
    return-void
.end method

.method public setRemovedCount(Ljava/lang/Object;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "removedCount"    # I

    #@0
    .prologue
    .line 447
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setRemovedCount(Ljava/lang/Object;I)V

    #@3
    .line 448
    return-void
.end method

.method public setScrollX(Ljava/lang/Object;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "scrollX"    # I

    #@0
    .prologue
    .line 452
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setScrollX(Ljava/lang/Object;I)V

    #@3
    .line 453
    return-void
.end method

.method public setScrollY(Ljava/lang/Object;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "scrollY"    # I

    #@0
    .prologue
    .line 457
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setScrollY(Ljava/lang/Object;I)V

    #@3
    .line 458
    return-void
.end method

.method public setScrollable(Ljava/lang/Object;Z)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "scrollable"    # Z

    #@0
    .prologue
    .line 462
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setScrollable(Ljava/lang/Object;Z)V

    #@3
    .line 463
    return-void
.end method

.method public setSource(Ljava/lang/Object;Landroid/view/View;)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "source"    # Landroid/view/View;

    #@0
    .prologue
    .line 467
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setSource(Ljava/lang/Object;Landroid/view/View;)V

    #@3
    .line 468
    return-void
.end method

.method public setToIndex(Ljava/lang/Object;I)V
    .locals 0
    .param p1, "record"    # Ljava/lang/Object;
    .param p2, "toIndex"    # I

    #@0
    .prologue
    .line 472
    invoke-static {p1, p2}, Landroid/support/v4/view/accessibility/AccessibilityRecordCompatIcs;->setToIndex(Ljava/lang/Object;I)V

    #@3
    .line 473
    return-void
.end method

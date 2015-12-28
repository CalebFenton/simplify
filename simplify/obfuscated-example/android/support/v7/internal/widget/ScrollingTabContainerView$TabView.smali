.class Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;
.super Landroid/support/v7/widget/LinearLayoutCompat;
.source "ScrollingTabContainerView.java"

# interfaces
.implements Landroid/view/View$OnLongClickListener;


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ScrollingTabContainerView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "TabView"
.end annotation


# instance fields
.field private final BG_ATTRS:[I

.field private mCustomView:Landroid/view/View;

.field private mIconView:Landroid/widget/ImageView;

.field private mTab:Landroid/support/v7/app/ActionBar$Tab;

.field private mTextView:Landroid/widget/TextView;

.field final synthetic this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/widget/ScrollingTabContainerView;Landroid/content/Context;Landroid/support/v7/app/ActionBar$Tab;Z)V
    .locals 5
    .param p2, "context"    # Landroid/content/Context;
    .param p3, "tab"    # Landroid/support/v7/app/ActionBar$Tab;
    .param p4, "forList"    # Z

    #@0
    .prologue
    const/4 v4, 0x0

    #@1
    const/4 v3, 0x0

    #@2
    .line 381
    iput-object p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@4
    .line 382
    sget v1, Landroid/support/v7/appcompat/R$attr;->actionBarTabStyle:I

    #@6
    invoke-direct {p0, p2, v4, v1}, Landroid/support/v7/widget/LinearLayoutCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@9
    .line 372
    const/4 v1, 0x1

    #@a
    new-array v1, v1, [I

    #@c
    const v2, 0x10100d4

    #@f
    aput v2, v1, v3

    #@11
    iput-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->BG_ATTRS:[I

    #@13
    .line 383
    iput-object p3, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTab:Landroid/support/v7/app/ActionBar$Tab;

    #@15
    .line 385
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->BG_ATTRS:[I

    #@17
    sget v2, Landroid/support/v7/appcompat/R$attr;->actionBarTabStyle:I

    #@19
    invoke-static {p2, v4, v1, v2, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@1c
    move-result-object v0

    #@1d
    .line 387
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->hasValue(I)Z

    #@20
    move-result v1

    #@21
    if-eqz v1, :cond_0

    #@23
    .line 388
    invoke-virtual {v0, v3}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@26
    move-result-object v1

    #@27
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@2a
    .line 390
    :cond_0
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@2d
    .line 392
    if-eqz p4, :cond_1

    #@2f
    .line 393
    const v1, 0x800013

    #@32
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setGravity(I)V

    #@35
    .line 396
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->update()V

    #@38
    .line 397
    return-void
.end method


# virtual methods
.method public bindTab(Landroid/support/v7/app/ActionBar$Tab;)V
    .locals 0
    .param p1, "tab"    # Landroid/support/v7/app/ActionBar$Tab;

    #@0
    .prologue
    .line 400
    iput-object p1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTab:Landroid/support/v7/app/ActionBar$Tab;

    #@2
    .line 401
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->update()V

    #@5
    .line 402
    return-void
.end method

.method public getTab()Landroid/support/v7/app/ActionBar$Tab;
    .locals 1

    #@0
    .prologue
    .line 535
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTab:Landroid/support/v7/app/ActionBar$Tab;

    #@2
    return-object v0
.end method

.method public onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V
    .locals 1
    .param p1, "event"    # Landroid/view/accessibility/AccessibilityEvent;

    #@0
    .prologue
    .line 415
    invoke-super {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->onInitializeAccessibilityEvent(Landroid/view/accessibility/AccessibilityEvent;)V

    #@3
    .line 417
    const-class v0, Landroid/support/v7/app/ActionBar$Tab;

    #@5
    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@8
    move-result-object v0

    #@9
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityEvent;->setClassName(Ljava/lang/CharSequence;)V

    #@c
    .line 418
    return-void
.end method

.method public onInitializeAccessibilityNodeInfo(Landroid/view/accessibility/AccessibilityNodeInfo;)V
    .locals 2
    .param p1, "info"    # Landroid/view/accessibility/AccessibilityNodeInfo;

    #@0
    .prologue
    .line 422
    invoke-super {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->onInitializeAccessibilityNodeInfo(Landroid/view/accessibility/AccessibilityNodeInfo;)V

    #@3
    .line 424
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@5
    const/16 v1, 0xe

    #@7
    if-lt v0, v1, :cond_0

    #@9
    .line 426
    const-class v0, Landroid/support/v7/app/ActionBar$Tab;

    #@b
    invoke-virtual {v0}, Ljava/lang/Class;->getName()Ljava/lang/String;

    #@e
    move-result-object v0

    #@f
    invoke-virtual {p1, v0}, Landroid/view/accessibility/AccessibilityNodeInfo;->setClassName(Ljava/lang/CharSequence;)V

    #@12
    .line 428
    :cond_0
    return-void
.end method

.method public onLongClick(Landroid/view/View;)Z
    .locals 9
    .param p1, "v"    # Landroid/view/View;

    #@0
    .prologue
    const/4 v7, 0x0

    #@1
    .line 516
    const/4 v6, 0x2

    #@2
    new-array v3, v6, [I

    #@4
    .line 517
    .local v3, "screenPos":[I
    invoke-virtual {p0, v3}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getLocationOnScreen([I)V

    #@7
    .line 519
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getContext()Landroid/content/Context;

    #@a
    move-result-object v1

    #@b
    .line 520
    .local v1, "context":Landroid/content/Context;
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getWidth()I

    #@e
    move-result v5

    #@f
    .line 521
    .local v5, "width":I
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getHeight()I

    #@12
    move-result v2

    #@13
    .line 522
    .local v2, "height":I
    invoke-virtual {v1}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@16
    move-result-object v6

    #@17
    invoke-virtual {v6}, Landroid/content/res/Resources;->getDisplayMetrics()Landroid/util/DisplayMetrics;

    #@1a
    move-result-object v6

    #@1b
    iget v4, v6, Landroid/util/DisplayMetrics;->widthPixels:I

    #@1d
    .line 524
    .local v4, "screenWidth":I
    iget-object v6, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTab:Landroid/support/v7/app/ActionBar$Tab;

    #@1f
    invoke-virtual {v6}, Landroid/support/v7/app/ActionBar$Tab;->getContentDescription()Ljava/lang/CharSequence;

    #@22
    move-result-object v6

    #@23
    invoke-static {v1, v6, v7}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;

    #@26
    move-result-object v0

    #@27
    .line 527
    .local v0, "cheatSheet":Landroid/widget/Toast;
    const/16 v6, 0x31

    #@29
    aget v7, v3, v7

    #@2b
    div-int/lit8 v8, v5, 0x2

    #@2d
    add-int/2addr v7, v8

    #@2e
    div-int/lit8 v8, v4, 0x2

    #@30
    sub-int/2addr v7, v8

    #@31
    invoke-virtual {v0, v6, v7, v2}, Landroid/widget/Toast;->setGravity(III)V

    #@34
    .line 530
    invoke-virtual {v0}, Landroid/widget/Toast;->show()V

    #@37
    .line 531
    const/4 v6, 0x1

    #@38
    return v6
.end method

.method public onMeasure(II)V
    .locals 2
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 432
    invoke-super {p0, p1, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->onMeasure(II)V

    #@3
    .line 435
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@5
    iget v0, v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mMaxTabWidth:I

    #@7
    if-lez v0, :cond_0

    #@9
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getMeasuredWidth()I

    #@c
    move-result v0

    #@d
    iget-object v1, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@f
    iget v1, v1, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mMaxTabWidth:I

    #@11
    if-le v0, v1, :cond_0

    #@13
    .line 436
    iget-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->this$0:Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@15
    iget v0, v0, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->mMaxTabWidth:I

    #@17
    const/high16 v1, 0x40000000    # 2.0f

    #@19
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@1c
    move-result v0

    #@1d
    invoke-super {p0, v0, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->onMeasure(II)V

    #@20
    .line 439
    :cond_0
    return-void
.end method

.method public setSelected(Z)V
    .locals 2
    .param p1, "selected"    # Z

    #@0
    .prologue
    .line 406
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->isSelected()Z

    #@3
    move-result v1

    #@4
    if-eq v1, p1, :cond_1

    #@6
    const/4 v0, 0x1

    #@7
    .line 407
    .local v0, "changed":Z
    :goto_0
    invoke-super {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->setSelected(Z)V

    #@a
    .line 408
    if-eqz v0, :cond_0

    #@c
    if-eqz p1, :cond_0

    #@e
    .line 409
    const/4 v1, 0x4

    #@f
    invoke-virtual {p0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->sendAccessibilityEvent(I)V

    #@12
    .line 411
    :cond_0
    return-void

    #@13
    .line 406
    .end local v0    # "changed":Z
    :cond_1
    const/4 v0, 0x0

    #@14
    goto :goto_0
.end method

.method public update()V
    .locals 15

    #@0
    .prologue
    const/16 v14, 0x10

    #@2
    const/16 v11, 0x8

    #@4
    const/4 v13, -0x2

    #@5
    const/4 v9, 0x0

    #@6
    const/4 v12, 0x0

    #@7
    .line 442
    iget-object v6, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTab:Landroid/support/v7/app/ActionBar$Tab;

    #@9
    .line 443
    .local v6, "tab":Landroid/support/v7/app/ActionBar$Tab;
    invoke-virtual {v6}, Landroid/support/v7/app/ActionBar$Tab;->getCustomView()Landroid/view/View;

    #@c
    move-result-object v0

    #@d
    .line 444
    .local v0, "custom":Landroid/view/View;
    if-eqz v0, :cond_4

    #@f
    .line 445
    invoke-virtual {v0}, Landroid/view/View;->getParent()Landroid/view/ViewParent;

    #@12
    move-result-object v1

    #@13
    .line 446
    .local v1, "customParent":Landroid/view/ViewParent;
    if-eq v1, p0, :cond_1

    #@15
    .line 447
    if-eqz v1, :cond_0

    #@17
    check-cast v1, Landroid/view/ViewGroup;

    #@19
    .end local v1    # "customParent":Landroid/view/ViewParent;
    invoke-virtual {v1, v0}, Landroid/view/ViewGroup;->removeView(Landroid/view/View;)V

    #@1c
    .line 448
    :cond_0
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->addView(Landroid/view/View;)V

    #@1f
    .line 450
    :cond_1
    iput-object v0, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mCustomView:Landroid/view/View;

    #@21
    .line 451
    iget-object v9, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@23
    if-eqz v9, :cond_2

    #@25
    iget-object v9, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@27
    invoke-virtual {v9, v11}, Landroid/widget/TextView;->setVisibility(I)V

    #@2a
    .line 452
    :cond_2
    iget-object v9, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@2c
    if-eqz v9, :cond_3

    #@2e
    .line 453
    iget-object v9, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@30
    invoke-virtual {v9, v11}, Landroid/widget/ImageView;->setVisibility(I)V

    #@33
    .line 454
    iget-object v9, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@35
    invoke-virtual {v9, v12}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@38
    .line 513
    :cond_3
    :goto_0
    return-void

    #@39
    .line 457
    :cond_4
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mCustomView:Landroid/view/View;

    #@3b
    if-eqz v10, :cond_5

    #@3d
    .line 458
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mCustomView:Landroid/view/View;

    #@3f
    invoke-virtual {p0, v10}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->removeView(Landroid/view/View;)V

    #@42
    .line 459
    iput-object v12, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mCustomView:Landroid/view/View;

    #@44
    .line 462
    :cond_5
    invoke-virtual {v6}, Landroid/support/v7/app/ActionBar$Tab;->getIcon()Landroid/graphics/drawable/Drawable;

    #@47
    move-result-object v3

    #@48
    .line 463
    .local v3, "icon":Landroid/graphics/drawable/Drawable;
    invoke-virtual {v6}, Landroid/support/v7/app/ActionBar$Tab;->getText()Ljava/lang/CharSequence;

    #@4b
    move-result-object v7

    #@4c
    .line 465
    .local v7, "text":Ljava/lang/CharSequence;
    if-eqz v3, :cond_b

    #@4e
    .line 466
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@50
    if-nez v10, :cond_6

    #@52
    .line 467
    new-instance v4, Landroid/widget/ImageView;

    #@54
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getContext()Landroid/content/Context;

    #@57
    move-result-object v10

    #@58
    invoke-direct {v4, v10}, Landroid/widget/ImageView;-><init>(Landroid/content/Context;)V

    #@5b
    .line 468
    .local v4, "iconView":Landroid/widget/ImageView;
    new-instance v5, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@5d
    invoke-direct {v5, v13, v13}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(II)V

    #@60
    .line 470
    .local v5, "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    iput v14, v5, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->gravity:I

    #@62
    .line 471
    invoke-virtual {v4, v5}, Landroid/widget/ImageView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@65
    .line 472
    invoke-virtual {p0, v4, v9}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->addView(Landroid/view/View;I)V

    #@68
    .line 473
    iput-object v4, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@6a
    .line 475
    .end local v4    # "iconView":Landroid/widget/ImageView;
    .end local v5    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    :cond_6
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@6c
    invoke-virtual {v10, v3}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@6f
    .line 476
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@71
    invoke-virtual {v10, v9}, Landroid/widget/ImageView;->setVisibility(I)V

    #@74
    .line 482
    :cond_7
    :goto_1
    invoke-static {v7}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@77
    move-result v10

    #@78
    if-nez v10, :cond_c

    #@7a
    const/4 v2, 0x1

    #@7b
    .line 483
    .local v2, "hasText":Z
    :goto_2
    if-eqz v2, :cond_d

    #@7d
    .line 484
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@7f
    if-nez v10, :cond_8

    #@81
    .line 485
    new-instance v8, Landroid/support/v7/internal/widget/CompatTextView;

    #@83
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->getContext()Landroid/content/Context;

    #@86
    move-result-object v10

    #@87
    sget v11, Landroid/support/v7/appcompat/R$attr;->actionBarTabTextStyle:I

    #@89
    invoke-direct {v8, v10, v12, v11}, Landroid/support/v7/internal/widget/CompatTextView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@8c
    .line 487
    .local v8, "textView":Landroid/widget/TextView;
    sget-object v10, Landroid/text/TextUtils$TruncateAt;->END:Landroid/text/TextUtils$TruncateAt;

    #@8e
    invoke-virtual {v8, v10}, Landroid/widget/TextView;->setEllipsize(Landroid/text/TextUtils$TruncateAt;)V

    #@91
    .line 488
    new-instance v5, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;

    #@93
    invoke-direct {v5, v13, v13}, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;-><init>(II)V

    #@96
    .line 490
    .restart local v5    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    iput v14, v5, Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;->gravity:I

    #@98
    .line 491
    invoke-virtual {v8, v5}, Landroid/widget/TextView;->setLayoutParams(Landroid/view/ViewGroup$LayoutParams;)V

    #@9b
    .line 492
    invoke-virtual {p0, v8}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->addView(Landroid/view/View;)V

    #@9e
    .line 493
    iput-object v8, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@a0
    .line 495
    .end local v5    # "lp":Landroid/support/v7/widget/LinearLayoutCompat$LayoutParams;
    .end local v8    # "textView":Landroid/widget/TextView;
    :cond_8
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@a2
    invoke-virtual {v10, v7}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@a5
    .line 496
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@a7
    invoke-virtual {v10, v9}, Landroid/widget/TextView;->setVisibility(I)V

    #@aa
    .line 502
    :cond_9
    :goto_3
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@ac
    if-eqz v10, :cond_a

    #@ae
    .line 503
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@b0
    invoke-virtual {v6}, Landroid/support/v7/app/ActionBar$Tab;->getContentDescription()Ljava/lang/CharSequence;

    #@b3
    move-result-object v11

    #@b4
    invoke-virtual {v10, v11}, Landroid/widget/ImageView;->setContentDescription(Ljava/lang/CharSequence;)V

    #@b7
    .line 506
    :cond_a
    if-nez v2, :cond_e

    #@b9
    invoke-virtual {v6}, Landroid/support/v7/app/ActionBar$Tab;->getContentDescription()Ljava/lang/CharSequence;

    #@bc
    move-result-object v10

    #@bd
    invoke-static {v10}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@c0
    move-result v10

    #@c1
    if-nez v10, :cond_e

    #@c3
    .line 507
    invoke-virtual {p0, p0}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setOnLongClickListener(Landroid/view/View$OnLongClickListener;)V

    #@c6
    goto/16 :goto_0

    #@c8
    .line 477
    .end local v2    # "hasText":Z
    :cond_b
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@ca
    if-eqz v10, :cond_7

    #@cc
    .line 478
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@ce
    invoke-virtual {v10, v11}, Landroid/widget/ImageView;->setVisibility(I)V

    #@d1
    .line 479
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mIconView:Landroid/widget/ImageView;

    #@d3
    invoke-virtual {v10, v12}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@d6
    goto :goto_1

    #@d7
    :cond_c
    move v2, v9

    #@d8
    .line 482
    goto :goto_2

    #@d9
    .line 497
    .restart local v2    # "hasText":Z
    :cond_d
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@db
    if-eqz v10, :cond_9

    #@dd
    .line 498
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@df
    invoke-virtual {v10, v11}, Landroid/widget/TextView;->setVisibility(I)V

    #@e2
    .line 499
    iget-object v10, p0, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->mTextView:Landroid/widget/TextView;

    #@e4
    invoke-virtual {v10, v12}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@e7
    goto :goto_3

    #@e8
    .line 509
    :cond_e
    invoke-virtual {p0, v12}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setOnLongClickListener(Landroid/view/View$OnLongClickListener;)V

    #@eb
    .line 510
    invoke-virtual {p0, v9}, Landroid/support/v7/internal/widget/ScrollingTabContainerView$TabView;->setLongClickable(Z)V

    #@ee
    goto/16 :goto_0
.end method

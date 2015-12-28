.class Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;
.super Landroid/widget/BaseAdapter;
.source "ActivityChooserView.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/widget/ActivityChooserView;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x2
    name = "ActivityChooserViewAdapter"
.end annotation


# static fields
.field private static final ITEM_VIEW_TYPE_ACTIVITY:I = 0x0

.field private static final ITEM_VIEW_TYPE_COUNT:I = 0x3

.field private static final ITEM_VIEW_TYPE_FOOTER:I = 0x1

.field public static final MAX_ACTIVITY_COUNT_DEFAULT:I = 0x4

.field public static final MAX_ACTIVITY_COUNT_UNLIMITED:I = 0x7fffffff


# instance fields
.field private mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

.field private mHighlightDefaultActivity:Z

.field private mMaxActivityCount:I

.field private mShowDefaultActivity:Z

.field private mShowFooterView:Z

.field final synthetic this$0:Landroid/support/v7/internal/widget/ActivityChooserView;


# direct methods
.method private constructor <init>(Landroid/support/v7/internal/widget/ActivityChooserView;)V
    .locals 1

    #@0
    .prologue
    .line 618
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@2
    invoke-direct {p0}, Landroid/widget/BaseAdapter;-><init>()V

    #@5
    .line 632
    const/4 v0, 0x4

    #@6
    iput v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mMaxActivityCount:I

    #@8
    return-void
.end method

.method synthetic constructor <init>(Landroid/support/v7/internal/widget/ActivityChooserView;Landroid/support/v7/internal/widget/ActivityChooserView$1;)V
    .locals 0
    .param p1, "x0"    # Landroid/support/v7/internal/widget/ActivityChooserView;
    .param p2, "x1"    # Landroid/support/v7/internal/widget/ActivityChooserView$1;

    #@0
    .prologue
    .line 618
    invoke-direct {p0, p1}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;-><init>(Landroid/support/v7/internal/widget/ActivityChooserView;)V

    #@3
    return-void
.end method


# virtual methods
.method public getActivityCount()I
    .locals 1

    #@0
    .prologue
    .line 779
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getActivityCount()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getCount()I
    .locals 3

    #@0
    .prologue
    .line 667
    const/4 v1, 0x0

    #@1
    .line 668
    .local v1, "count":I
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@3
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getActivityCount()I

    #@6
    move-result v0

    #@7
    .line 669
    .local v0, "activityCount":I
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowDefaultActivity:Z

    #@9
    if-nez v2, :cond_0

    #@b
    iget-object v2, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@d
    invoke-virtual {v2}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getDefaultActivity()Landroid/content/pm/ResolveInfo;

    #@10
    move-result-object v2

    #@11
    if-eqz v2, :cond_0

    #@13
    .line 670
    add-int/lit8 v0, v0, -0x1

    #@15
    .line 672
    :cond_0
    iget v2, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mMaxActivityCount:I

    #@17
    invoke-static {v0, v2}, Ljava/lang/Math;->min(II)I

    #@1a
    move-result v1

    #@1b
    .line 673
    iget-boolean v2, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowFooterView:Z

    #@1d
    if-eqz v2, :cond_1

    #@1f
    .line 674
    add-int/lit8 v1, v1, 0x1

    #@21
    .line 676
    :cond_1
    return v1
.end method

.method public getDataModel()Landroid/support/v7/internal/widget/ActivityChooserModel;
    .locals 1

    #@0
    .prologue
    .line 791
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@2
    return-object v0
.end method

.method public getDefaultActivity()Landroid/content/pm/ResolveInfo;
    .locals 1

    #@0
    .prologue
    .line 768
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getDefaultActivity()Landroid/content/pm/ResolveInfo;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getHistorySize()I
    .locals 1

    #@0
    .prologue
    .line 783
    iget-object v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getHistorySize()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getItem(I)Ljava/lang/Object;
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 680
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->getItemViewType(I)I

    #@3
    move-result v0

    #@4
    .line 681
    .local v0, "itemViewType":I
    packed-switch v0, :pswitch_data_0

    #@7
    .line 690
    new-instance v1, Ljava/lang/IllegalArgumentException;

    #@9
    invoke-direct {v1}, Ljava/lang/IllegalArgumentException;-><init>()V

    #@c
    throw v1

    #@d
    .line 683
    :pswitch_0
    const/4 v1, 0x0

    #@e
    .line 688
    :goto_0
    return-object v1

    #@f
    .line 685
    :pswitch_1
    iget-boolean v1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowDefaultActivity:Z

    #@11
    if-nez v1, :cond_0

    #@13
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@15
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getDefaultActivity()Landroid/content/pm/ResolveInfo;

    #@18
    move-result-object v1

    #@19
    if-eqz v1, :cond_0

    #@1b
    .line 686
    add-int/lit8 p1, p1, 0x1

    #@1d
    .line 688
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@1f
    invoke-virtual {v1, p1}, Landroid/support/v7/internal/widget/ActivityChooserModel;->getActivity(I)Landroid/content/pm/ResolveInfo;

    #@22
    move-result-object v1

    #@23
    goto :goto_0

    #@24
    .line 681
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public getItemId(I)J
    .locals 2
    .param p1, "position"    # I

    #@0
    .prologue
    .line 695
    int-to-long v0, p1

    #@1
    return-wide v0
.end method

.method public getItemViewType(I)I
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 654
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowFooterView:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->getCount()I

    #@7
    move-result v0

    #@8
    add-int/lit8 v0, v0, -0x1

    #@a
    if-ne p1, v0, :cond_0

    #@c
    .line 655
    const/4 v0, 0x1

    #@d
    .line 657
    :goto_0
    return v0

    #@e
    :cond_0
    const/4 v0, 0x0

    #@f
    goto :goto_0
.end method

.method public getMaxActivityCount()I
    .locals 1

    #@0
    .prologue
    .line 787
    iget v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mMaxActivityCount:I

    #@2
    return v0
.end method

.method public getShowDefaultActivity()Z
    .locals 1

    #@0
    .prologue
    .line 805
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowDefaultActivity:Z

    #@2
    return v0
.end method

.method public getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 10
    .param p1, "position"    # I
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    const/4 v9, 0x1

    #@1
    const/4 v8, 0x0

    #@2
    .line 699
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->getItemViewType(I)I

    #@5
    move-result v3

    #@6
    .line 700
    .local v3, "itemViewType":I
    packed-switch v3, :pswitch_data_0

    #@9
    .line 732
    new-instance v6, Ljava/lang/IllegalArgumentException;

    #@b
    invoke-direct {v6}, Ljava/lang/IllegalArgumentException;-><init>()V

    #@e
    throw v6

    #@f
    .line 702
    :pswitch_0
    if-eqz p2, :cond_0

    #@11
    invoke-virtual {p2}, Landroid/view/View;->getId()I

    #@14
    move-result v6

    #@15
    if-eq v6, v9, :cond_1

    #@17
    .line 703
    :cond_0
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@19
    invoke-virtual {v6}, Landroid/support/v7/internal/widget/ActivityChooserView;->getContext()Landroid/content/Context;

    #@1c
    move-result-object v6

    #@1d
    invoke-static {v6}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@20
    move-result-object v6

    #@21
    sget v7, Landroid/support/v7/appcompat/R$layout;->abc_activity_chooser_view_list_item:I

    #@23
    invoke-virtual {v6, v7, p3, v8}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@26
    move-result-object p2

    #@27
    .line 705
    invoke-virtual {p2, v9}, Landroid/view/View;->setId(I)V

    #@2a
    .line 706
    sget v6, Landroid/support/v7/appcompat/R$id;->title:I

    #@2c
    invoke-virtual {p2, v6}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@2f
    move-result-object v5

    #@30
    check-cast v5, Landroid/widget/TextView;

    #@32
    .line 707
    .local v5, "titleView":Landroid/widget/TextView;
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@34
    invoke-virtual {v6}, Landroid/support/v7/internal/widget/ActivityChooserView;->getContext()Landroid/content/Context;

    #@37
    move-result-object v6

    #@38
    sget v7, Landroid/support/v7/appcompat/R$string;->abc_activity_chooser_view_see_all:I

    #@3a
    invoke-virtual {v6, v7}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@3d
    move-result-object v6

    #@3e
    invoke-virtual {v5, v6}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@41
    .end local v5    # "titleView":Landroid/widget/TextView;
    :cond_1
    move-object v1, p2

    #@42
    .line 730
    .end local p2    # "convertView":Landroid/view/View;
    .local v1, "convertView":Landroid/view/View;
    :goto_0
    return-object v1

    #@43
    .line 712
    .end local v1    # "convertView":Landroid/view/View;
    .restart local p2    # "convertView":Landroid/view/View;
    :pswitch_1
    if-eqz p2, :cond_2

    #@45
    invoke-virtual {p2}, Landroid/view/View;->getId()I

    #@48
    move-result v6

    #@49
    sget v7, Landroid/support/v7/appcompat/R$id;->list_item:I

    #@4b
    if-eq v6, v7, :cond_3

    #@4d
    .line 713
    :cond_2
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@4f
    invoke-virtual {v6}, Landroid/support/v7/internal/widget/ActivityChooserView;->getContext()Landroid/content/Context;

    #@52
    move-result-object v6

    #@53
    invoke-static {v6}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@56
    move-result-object v6

    #@57
    sget v7, Landroid/support/v7/appcompat/R$layout;->abc_activity_chooser_view_list_item:I

    #@59
    invoke-virtual {v6, v7, p3, v8}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@5c
    move-result-object p2

    #@5d
    .line 716
    :cond_3
    iget-object v6, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@5f
    invoke-virtual {v6}, Landroid/support/v7/internal/widget/ActivityChooserView;->getContext()Landroid/content/Context;

    #@62
    move-result-object v6

    #@63
    invoke-virtual {v6}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@66
    move-result-object v4

    #@67
    .line 718
    .local v4, "packageManager":Landroid/content/pm/PackageManager;
    sget v6, Landroid/support/v7/appcompat/R$id;->icon:I

    #@69
    invoke-virtual {p2, v6}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@6c
    move-result-object v2

    #@6d
    check-cast v2, Landroid/widget/ImageView;

    #@6f
    .line 719
    .local v2, "iconView":Landroid/widget/ImageView;
    invoke-virtual {p0, p1}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->getItem(I)Ljava/lang/Object;

    #@72
    move-result-object v0

    #@73
    check-cast v0, Landroid/content/pm/ResolveInfo;

    #@75
    .line 720
    .local v0, "activity":Landroid/content/pm/ResolveInfo;
    invoke-virtual {v0, v4}, Landroid/content/pm/ResolveInfo;->loadIcon(Landroid/content/pm/PackageManager;)Landroid/graphics/drawable/Drawable;

    #@78
    move-result-object v6

    #@79
    invoke-virtual {v2, v6}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@7c
    .line 722
    sget v6, Landroid/support/v7/appcompat/R$id;->title:I

    #@7e
    invoke-virtual {p2, v6}, Landroid/view/View;->findViewById(I)Landroid/view/View;

    #@81
    move-result-object v5

    #@82
    check-cast v5, Landroid/widget/TextView;

    #@84
    .line 723
    .restart local v5    # "titleView":Landroid/widget/TextView;
    invoke-virtual {v0, v4}, Landroid/content/pm/ResolveInfo;->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;

    #@87
    move-result-object v6

    #@88
    invoke-virtual {v5, v6}, Landroid/widget/TextView;->setText(Ljava/lang/CharSequence;)V

    #@8b
    .line 725
    iget-boolean v6, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowDefaultActivity:Z

    #@8d
    if-eqz v6, :cond_4

    #@8f
    if-nez p1, :cond_4

    #@91
    iget-boolean v6, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mHighlightDefaultActivity:Z

    #@93
    if-eqz v6, :cond_4

    #@95
    :cond_4
    move-object v1, p2

    #@96
    .line 730
    .end local p2    # "convertView":Landroid/view/View;
    .restart local v1    # "convertView":Landroid/view/View;
    goto :goto_0

    #@97
    .line 700
    nop

    #@98
    :pswitch_data_0
    .packed-switch 0x0
        :pswitch_1
        :pswitch_0
    .end packed-switch
.end method

.method public getViewTypeCount()I
    .locals 1

    #@0
    .prologue
    .line 663
    const/4 v0, 0x3

    #@1
    return v0
.end method

.method public measureContentWidth()I
    .locals 9

    #@0
    .prologue
    const/4 v8, 0x0

    #@1
    .line 739
    iget v5, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mMaxActivityCount:I

    #@3
    .line 740
    .local v5, "oldMaxActivityCount":I
    const v7, 0x7fffffff

    #@6
    iput v7, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mMaxActivityCount:I

    #@8
    .line 742
    const/4 v0, 0x0

    #@9
    .line 743
    .local v0, "contentWidth":I
    const/4 v4, 0x0

    #@a
    .line 745
    .local v4, "itemView":Landroid/view/View;
    invoke-static {v8, v8}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@d
    move-result v6

    #@e
    .line 746
    .local v6, "widthMeasureSpec":I
    invoke-static {v8, v8}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@11
    move-result v2

    #@12
    .line 747
    .local v2, "heightMeasureSpec":I
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->getCount()I

    #@15
    move-result v1

    #@16
    .line 749
    .local v1, "count":I
    const/4 v3, 0x0

    #@17
    .local v3, "i":I
    :goto_0
    if-ge v3, v1, :cond_0

    #@19
    .line 750
    const/4 v7, 0x0

    #@1a
    invoke-virtual {p0, v3, v4, v7}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;

    #@1d
    move-result-object v4

    #@1e
    .line 751
    invoke-virtual {v4, v6, v2}, Landroid/view/View;->measure(II)V

    #@21
    .line 752
    invoke-virtual {v4}, Landroid/view/View;->getMeasuredWidth()I

    #@24
    move-result v7

    #@25
    invoke-static {v0, v7}, Ljava/lang/Math;->max(II)I

    #@28
    move-result v0

    #@29
    .line 749
    add-int/lit8 v3, v3, 0x1

    #@2b
    goto :goto_0

    #@2c
    .line 755
    :cond_0
    iput v5, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mMaxActivityCount:I

    #@2e
    .line 757
    return v0
.end method

.method public setDataModel(Landroid/support/v7/internal/widget/ActivityChooserModel;)V
    .locals 2
    .param p1, "dataModel"    # Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@0
    .prologue
    .line 641
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@2
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserView;->mAdapter:Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;
    invoke-static {v1}, Landroid/support/v7/internal/widget/ActivityChooserView;->access$000(Landroid/support/v7/internal/widget/ActivityChooserView;)Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;

    #@5
    move-result-object v1

    #@6
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->getDataModel()Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@9
    move-result-object v0

    #@a
    .line 642
    .local v0, "oldDataModel":Landroid/support/v7/internal/widget/ActivityChooserModel;
    if-eqz v0, :cond_0

    #@c
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@e
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActivityChooserView;->isShown()Z

    #@11
    move-result v1

    #@12
    if-eqz v1, :cond_0

    #@14
    .line 643
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@16
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserView;->mModelDataSetOberver:Landroid/database/DataSetObserver;
    invoke-static {v1}, Landroid/support/v7/internal/widget/ActivityChooserView;->access$1100(Landroid/support/v7/internal/widget/ActivityChooserView;)Landroid/database/DataSetObserver;

    #@19
    move-result-object v1

    #@1a
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ActivityChooserModel;->unregisterObserver(Ljava/lang/Object;)V

    #@1d
    .line 645
    :cond_0
    iput-object p1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mDataModel:Landroid/support/v7/internal/widget/ActivityChooserModel;

    #@1f
    .line 646
    if-eqz p1, :cond_1

    #@21
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@23
    invoke-virtual {v1}, Landroid/support/v7/internal/widget/ActivityChooserView;->isShown()Z

    #@26
    move-result v1

    #@27
    if-eqz v1, :cond_1

    #@29
    .line 647
    iget-object v1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->this$0:Landroid/support/v7/internal/widget/ActivityChooserView;

    #@2b
    # getter for: Landroid/support/v7/internal/widget/ActivityChooserView;->mModelDataSetOberver:Landroid/database/DataSetObserver;
    invoke-static {v1}, Landroid/support/v7/internal/widget/ActivityChooserView;->access$1100(Landroid/support/v7/internal/widget/ActivityChooserView;)Landroid/database/DataSetObserver;

    #@2e
    move-result-object v1

    #@2f
    invoke-virtual {p1, v1}, Landroid/support/v7/internal/widget/ActivityChooserModel;->registerObserver(Ljava/lang/Object;)V

    #@32
    .line 649
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->notifyDataSetChanged()V

    #@35
    .line 650
    return-void
.end method

.method public setMaxActivityCount(I)V
    .locals 1
    .param p1, "maxActivityCount"    # I

    #@0
    .prologue
    .line 761
    iget v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mMaxActivityCount:I

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 762
    iput p1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mMaxActivityCount:I

    #@6
    .line 763
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->notifyDataSetChanged()V

    #@9
    .line 765
    :cond_0
    return-void
.end method

.method public setShowDefaultActivity(ZZ)V
    .locals 1
    .param p1, "showDefaultActivity"    # Z
    .param p2, "highlightDefaultActivity"    # Z

    #@0
    .prologue
    .line 796
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowDefaultActivity:Z

    #@2
    if-ne v0, p1, :cond_0

    #@4
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mHighlightDefaultActivity:Z

    #@6
    if-eq v0, p2, :cond_1

    #@8
    .line 798
    :cond_0
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowDefaultActivity:Z

    #@a
    .line 799
    iput-boolean p2, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mHighlightDefaultActivity:Z

    #@c
    .line 800
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->notifyDataSetChanged()V

    #@f
    .line 802
    :cond_1
    return-void
.end method

.method public setShowFooterView(Z)V
    .locals 1
    .param p1, "showFooterView"    # Z

    #@0
    .prologue
    .line 772
    iget-boolean v0, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowFooterView:Z

    #@2
    if-eq v0, p1, :cond_0

    #@4
    .line 773
    iput-boolean p1, p0, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->mShowFooterView:Z

    #@6
    .line 774
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/ActivityChooserView$ActivityChooserViewAdapter;->notifyDataSetChanged()V

    #@9
    .line 776
    :cond_0
    return-void
.end method

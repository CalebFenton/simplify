.class public Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;
.super Landroid/support/v7/app/ActionBar$Tab;
.source "WindowDecorActionBar.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/internal/app/WindowDecorActionBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x1
    name = "TabImpl"
.end annotation


# instance fields
.field private mCallback:Landroid/support/v7/app/ActionBar$TabListener;

.field private mContentDesc:Ljava/lang/CharSequence;

.field private mCustomView:Landroid/view/View;

.field private mIcon:Landroid/graphics/drawable/Drawable;

.field private mPosition:I

.field private mTag:Ljava/lang/Object;

.field private mText:Ljava/lang/CharSequence;

.field final synthetic this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;


# direct methods
.method public constructor <init>(Landroid/support/v7/internal/app/WindowDecorActionBar;)V
    .locals 1

    #@0
    .prologue
    .line 1109
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    invoke-direct {p0}, Landroid/support/v7/app/ActionBar$Tab;-><init>()V

    #@5
    .line 1115
    const/4 v0, -0x1

    #@6
    iput v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@8
    return-void
.end method


# virtual methods
.method public getCallback()Landroid/support/v7/app/ActionBar$TabListener;
    .locals 1

    #@0
    .prologue
    .line 1130
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mCallback:Landroid/support/v7/app/ActionBar$TabListener;

    #@2
    return-object v0
.end method

.method public getContentDescription()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 1227
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mContentDesc:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public getCustomView()Landroid/view/View;
    .locals 1

    #@0
    .prologue
    .line 1141
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mCustomView:Landroid/view/View;

    #@2
    return-object v0
.end method

.method public getIcon()Landroid/graphics/drawable/Drawable;
    .locals 1

    #@0
    .prologue
    .line 1161
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mIcon:Landroid/graphics/drawable/Drawable;

    #@2
    return-object v0
.end method

.method public getPosition()I
    .locals 1

    #@0
    .prologue
    .line 1166
    iget v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@2
    return v0
.end method

.method public getTag()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 1120
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mTag:Ljava/lang/Object;

    #@2
    return-object v0
.end method

.method public getText()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 1175
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mText:Ljava/lang/CharSequence;

    #@2
    return-object v0
.end method

.method public select()V
    .locals 1

    #@0
    .prologue
    .line 1208
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    invoke-virtual {v0, p0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->selectTab(Landroid/support/v7/app/ActionBar$Tab;)V

    #@5
    .line 1209
    return-void
.end method

.method public setContentDescription(I)Landroid/support/v7/app/ActionBar$Tab;
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1213
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@9
    move-result-object v0

    #@a
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getText(I)Ljava/lang/CharSequence;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->setContentDescription(Ljava/lang/CharSequence;)Landroid/support/v7/app/ActionBar$Tab;

    #@11
    move-result-object v0

    #@12
    return-object v0
.end method

.method public setContentDescription(Ljava/lang/CharSequence;)Landroid/support/v7/app/ActionBar$Tab;
    .locals 2
    .param p1, "contentDesc"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 1218
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mContentDesc:Ljava/lang/CharSequence;

    #@2
    .line 1219
    iget v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@4
    if-ltz v0, :cond_0

    #@6
    .line 1220
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@8
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1300(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@b
    move-result-object v0

    #@c
    iget v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->updateTab(I)V

    #@11
    .line 1222
    :cond_0
    return-object p0
.end method

.method public setCustomView(I)Landroid/support/v7/app/ActionBar$Tab;
    .locals 2
    .param p1, "layoutResId"    # I

    #@0
    .prologue
    .line 1155
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getThemedContext()Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    invoke-static {v0}, Landroid/view/LayoutInflater;->from(Landroid/content/Context;)Landroid/view/LayoutInflater;

    #@9
    move-result-object v0

    #@a
    const/4 v1, 0x0

    #@b
    invoke-virtual {v0, p1, v1}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;)Landroid/view/View;

    #@e
    move-result-object v0

    #@f
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->setCustomView(Landroid/view/View;)Landroid/support/v7/app/ActionBar$Tab;

    #@12
    move-result-object v0

    #@13
    return-object v0
.end method

.method public setCustomView(Landroid/view/View;)Landroid/support/v7/app/ActionBar$Tab;
    .locals 2
    .param p1, "view"    # Landroid/view/View;

    #@0
    .prologue
    .line 1146
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mCustomView:Landroid/view/View;

    #@2
    .line 1147
    iget v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@4
    if-ltz v0, :cond_0

    #@6
    .line 1148
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@8
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1300(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@b
    move-result-object v0

    #@c
    iget v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->updateTab(I)V

    #@11
    .line 1150
    :cond_0
    return-object p0
.end method

.method public setIcon(I)Landroid/support/v7/app/ActionBar$Tab;
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1189
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0, p1}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@9
    move-result-object v0

    #@a
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->setIcon(Landroid/graphics/drawable/Drawable;)Landroid/support/v7/app/ActionBar$Tab;

    #@d
    move-result-object v0

    #@e
    return-object v0
.end method

.method public setIcon(Landroid/graphics/drawable/Drawable;)Landroid/support/v7/app/ActionBar$Tab;
    .locals 2
    .param p1, "icon"    # Landroid/graphics/drawable/Drawable;

    #@0
    .prologue
    .line 1180
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mIcon:Landroid/graphics/drawable/Drawable;

    #@2
    .line 1181
    iget v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@4
    if-ltz v0, :cond_0

    #@6
    .line 1182
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@8
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1300(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@b
    move-result-object v0

    #@c
    iget v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->updateTab(I)V

    #@11
    .line 1184
    :cond_0
    return-object p0
.end method

.method public setPosition(I)V
    .locals 0
    .param p1, "position"    # I

    #@0
    .prologue
    .line 1170
    iput p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@2
    .line 1171
    return-void
.end method

.method public setTabListener(Landroid/support/v7/app/ActionBar$TabListener;)Landroid/support/v7/app/ActionBar$Tab;
    .locals 0
    .param p1, "callback"    # Landroid/support/v7/app/ActionBar$TabListener;

    #@0
    .prologue
    .line 1135
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mCallback:Landroid/support/v7/app/ActionBar$TabListener;

    #@2
    .line 1136
    return-object p0
.end method

.method public setTag(Ljava/lang/Object;)Landroid/support/v7/app/ActionBar$Tab;
    .locals 0
    .param p1, "tag"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 1125
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mTag:Ljava/lang/Object;

    #@2
    .line 1126
    return-object p0
.end method

.method public setText(I)Landroid/support/v7/app/ActionBar$Tab;
    .locals 1
    .param p1, "resId"    # I

    #@0
    .prologue
    .line 1203
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@2
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mContext:Landroid/content/Context;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1200(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/content/Context;

    #@5
    move-result-object v0

    #@6
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@9
    move-result-object v0

    #@a
    invoke-virtual {v0, p1}, Landroid/content/res/Resources;->getText(I)Ljava/lang/CharSequence;

    #@d
    move-result-object v0

    #@e
    invoke-virtual {p0, v0}, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->setText(Ljava/lang/CharSequence;)Landroid/support/v7/app/ActionBar$Tab;

    #@11
    move-result-object v0

    #@12
    return-object v0
.end method

.method public setText(Ljava/lang/CharSequence;)Landroid/support/v7/app/ActionBar$Tab;
    .locals 2
    .param p1, "text"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 1194
    iput-object p1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mText:Ljava/lang/CharSequence;

    #@2
    .line 1195
    iget v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@4
    if-ltz v0, :cond_0

    #@6
    .line 1196
    iget-object v0, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->this$0:Landroid/support/v7/internal/app/WindowDecorActionBar;

    #@8
    # getter for: Landroid/support/v7/internal/app/WindowDecorActionBar;->mTabScrollView:Landroid/support/v7/internal/widget/ScrollingTabContainerView;
    invoke-static {v0}, Landroid/support/v7/internal/app/WindowDecorActionBar;->access$1300(Landroid/support/v7/internal/app/WindowDecorActionBar;)Landroid/support/v7/internal/widget/ScrollingTabContainerView;

    #@b
    move-result-object v0

    #@c
    iget v1, p0, Landroid/support/v7/internal/app/WindowDecorActionBar$TabImpl;->mPosition:I

    #@e
    invoke-virtual {v0, v1}, Landroid/support/v7/internal/widget/ScrollingTabContainerView;->updateTab(I)V

    #@11
    .line 1198
    :cond_0
    return-object p0
.end method

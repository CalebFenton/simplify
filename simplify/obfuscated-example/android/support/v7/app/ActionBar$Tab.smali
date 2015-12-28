.class public abstract Landroid/support/v7/app/ActionBar$Tab;
.super Ljava/lang/Object;
.source "ActionBar.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/support/v7/app/ActionBar;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x409
    name = "Tab"
.end annotation


# static fields
.field public static final INVALID_POSITION:I = -0x1


# direct methods
.method public constructor <init>()V
    .locals 0

    #@0
    .prologue
    .line 1117
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    return-void
.end method


# virtual methods
.method public abstract getContentDescription()Ljava/lang/CharSequence;
.end method

.method public abstract getCustomView()Landroid/view/View;
.end method

.method public abstract getIcon()Landroid/graphics/drawable/Drawable;
.end method

.method public abstract getPosition()I
.end method

.method public abstract getTag()Ljava/lang/Object;
.end method

.method public abstract getText()Ljava/lang/CharSequence;
.end method

.method public abstract select()V
.end method

.method public abstract setContentDescription(I)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setContentDescription(Ljava/lang/CharSequence;)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setCustomView(I)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setCustomView(Landroid/view/View;)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setIcon(I)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setIcon(Landroid/graphics/drawable/Drawable;)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setTabListener(Landroid/support/v7/app/ActionBar$TabListener;)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setTag(Ljava/lang/Object;)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setText(I)Landroid/support/v7/app/ActionBar$Tab;
.end method

.method public abstract setText(Ljava/lang/CharSequence;)Landroid/support/v7/app/ActionBar$Tab;
.end method

.class public abstract Landroid/support/v4/widget/ResourceCursorAdapter;
.super Landroid/support/v4/widget/CursorAdapter;
.source "ResourceCursorAdapter.java"


# instance fields
.field private mDropDownLayout:I

.field private mInflater:Landroid/view/LayoutInflater;

.field private mLayout:I


# direct methods
.method public constructor <init>(Landroid/content/Context;ILandroid/database/Cursor;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "layout"    # I
    .param p3, "c"    # Landroid/database/Cursor;
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 54
    invoke-direct {p0, p1, p3}, Landroid/support/v4/widget/CursorAdapter;-><init>(Landroid/content/Context;Landroid/database/Cursor;)V

    #@3
    .line 55
    iput p2, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mDropDownLayout:I

    #@5
    iput p2, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mLayout:I

    #@7
    .line 56
    const-string v0, "layout_inflater"

    #@9
    invoke-virtual {p1, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@c
    move-result-object v0

    #@d
    check-cast v0, Landroid/view/LayoutInflater;

    #@f
    iput-object v0, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mInflater:Landroid/view/LayoutInflater;

    #@11
    .line 57
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;ILandroid/database/Cursor;I)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "layout"    # I
    .param p3, "c"    # Landroid/database/Cursor;
    .param p4, "flags"    # I

    #@0
    .prologue
    .line 93
    invoke-direct {p0, p1, p3, p4}, Landroid/support/v4/widget/CursorAdapter;-><init>(Landroid/content/Context;Landroid/database/Cursor;I)V

    #@3
    .line 94
    iput p2, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mDropDownLayout:I

    #@5
    iput p2, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mLayout:I

    #@7
    .line 95
    const-string v0, "layout_inflater"

    #@9
    invoke-virtual {p1, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@c
    move-result-object v0

    #@d
    check-cast v0, Landroid/view/LayoutInflater;

    #@f
    iput-object v0, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mInflater:Landroid/view/LayoutInflater;

    #@11
    .line 96
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;ILandroid/database/Cursor;Z)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "layout"    # I
    .param p3, "c"    # Landroid/database/Cursor;
    .param p4, "autoRequery"    # Z

    #@0
    .prologue
    .line 76
    invoke-direct {p0, p1, p3, p4}, Landroid/support/v4/widget/CursorAdapter;-><init>(Landroid/content/Context;Landroid/database/Cursor;Z)V

    #@3
    .line 77
    iput p2, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mDropDownLayout:I

    #@5
    iput p2, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mLayout:I

    #@7
    .line 78
    const-string v0, "layout_inflater"

    #@9
    invoke-virtual {p1, v0}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@c
    move-result-object v0

    #@d
    check-cast v0, Landroid/view/LayoutInflater;

    #@f
    iput-object v0, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mInflater:Landroid/view/LayoutInflater;

    #@11
    .line 79
    return-void
.end method


# virtual methods
.method public newDropDownView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "cursor"    # Landroid/database/Cursor;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 111
    iget-object v0, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mInflater:Landroid/view/LayoutInflater;

    #@2
    iget v1, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mDropDownLayout:I

    #@4
    const/4 v2, 0x0

    #@5
    invoke-virtual {v0, v1, p3, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@8
    move-result-object v0

    #@9
    return-object v0
.end method

.method public newView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 3
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "cursor"    # Landroid/database/Cursor;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 106
    iget-object v0, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mInflater:Landroid/view/LayoutInflater;

    #@2
    iget v1, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mLayout:I

    #@4
    const/4 v2, 0x0

    #@5
    invoke-virtual {v0, v1, p3, v2}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@8
    move-result-object v0

    #@9
    return-object v0
.end method

.method public setDropDownViewResource(I)V
    .locals 0
    .param p1, "dropDownLayout"    # I

    #@0
    .prologue
    .line 129
    iput p1, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mDropDownLayout:I

    #@2
    .line 130
    return-void
.end method

.method public setViewResource(I)V
    .locals 0
    .param p1, "layout"    # I

    #@0
    .prologue
    .line 120
    iput p1, p0, Landroid/support/v4/widget/ResourceCursorAdapter;->mLayout:I

    #@2
    .line 121
    return-void
.end method

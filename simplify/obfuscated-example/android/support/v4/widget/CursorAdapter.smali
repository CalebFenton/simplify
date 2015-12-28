.class public abstract Landroid/support/v4/widget/CursorAdapter;
.super Landroid/widget/BaseAdapter;
.source "CursorAdapter.java"

# interfaces
.implements Landroid/support/v4/widget/CursorFilter$CursorFilterClient;
.implements Landroid/widget/Filterable;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v4/widget/CursorAdapter$1;,
        Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;,
        Landroid/support/v4/widget/CursorAdapter$ChangeObserver;
    }
.end annotation


# static fields
.field public static final FLAG_AUTO_REQUERY:I = 0x1
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation
.end field

.field public static final FLAG_REGISTER_CONTENT_OBSERVER:I = 0x2


# instance fields
.field protected mAutoRequery:Z

.field protected mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

.field protected mContext:Landroid/content/Context;

.field protected mCursor:Landroid/database/Cursor;

.field protected mCursorFilter:Landroid/support/v4/widget/CursorFilter;

.field protected mDataSetObserver:Landroid/database/DataSetObserver;

.field protected mDataValid:Z

.field protected mFilterQueryProvider:Landroid/widget/FilterQueryProvider;

.field protected mRowIDColumn:I


# direct methods
.method public constructor <init>(Landroid/content/Context;Landroid/database/Cursor;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "c"    # Landroid/database/Cursor;
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 121
    invoke-direct {p0}, Landroid/widget/BaseAdapter;-><init>()V

    #@3
    .line 122
    const/4 v0, 0x1

    #@4
    invoke-virtual {p0, p1, p2, v0}, Landroid/support/v4/widget/CursorAdapter;->init(Landroid/content/Context;Landroid/database/Cursor;I)V

    #@7
    .line 123
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/database/Cursor;I)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "c"    # Landroid/database/Cursor;
    .param p3, "flags"    # I

    #@0
    .prologue
    .line 150
    invoke-direct {p0}, Landroid/widget/BaseAdapter;-><init>()V

    #@3
    .line 151
    invoke-virtual {p0, p1, p2, p3}, Landroid/support/v4/widget/CursorAdapter;->init(Landroid/content/Context;Landroid/database/Cursor;I)V

    #@6
    .line 152
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/database/Cursor;Z)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "c"    # Landroid/database/Cursor;
    .param p3, "autoRequery"    # Z

    #@0
    .prologue
    .line 137
    invoke-direct {p0}, Landroid/widget/BaseAdapter;-><init>()V

    #@3
    .line 138
    if-eqz p3, :cond_0

    #@5
    const/4 v0, 0x1

    #@6
    :goto_0
    invoke-virtual {p0, p1, p2, v0}, Landroid/support/v4/widget/CursorAdapter;->init(Landroid/content/Context;Landroid/database/Cursor;I)V

    #@9
    .line 139
    return-void

    #@a
    .line 138
    :cond_0
    const/4 v0, 0x2

    #@b
    goto :goto_0
.end method


# virtual methods
.method public abstract bindView(Landroid/view/View;Landroid/content/Context;Landroid/database/Cursor;)V
.end method

.method public changeCursor(Landroid/database/Cursor;)V
    .locals 1
    .param p1, "cursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    .line 315
    invoke-virtual {p0, p1}, Landroid/support/v4/widget/CursorAdapter;->swapCursor(Landroid/database/Cursor;)Landroid/database/Cursor;

    #@3
    move-result-object v0

    #@4
    .line 316
    .local v0, "old":Landroid/database/Cursor;
    if-eqz v0, :cond_0

    #@6
    .line 317
    invoke-interface {v0}, Landroid/database/Cursor;->close()V

    #@9
    .line 319
    :cond_0
    return-void
.end method

.method public convertToString(Landroid/database/Cursor;)Ljava/lang/CharSequence;
    .locals 1
    .param p1, "cursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    .line 367
    if-nez p1, :cond_0

    #@2
    const-string v0, ""

    #@4
    :goto_0
    return-object v0

    #@5
    :cond_0
    invoke-virtual {p1}, Ljava/lang/Object;->toString()Ljava/lang/String;

    #@8
    move-result-object v0

    #@9
    goto :goto_0
.end method

.method public getCount()I
    .locals 1

    #@0
    .prologue
    .line 201
    iget-boolean v0, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 202
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@a
    invoke-interface {v0}, Landroid/database/Cursor;->getCount()I

    #@d
    move-result v0

    #@e
    .line 204
    :goto_0
    return v0

    #@f
    :cond_0
    const/4 v0, 0x0

    #@10
    goto :goto_0
.end method

.method public getCursor()Landroid/database/Cursor;
    .locals 1

    #@0
    .prologue
    .line 194
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@2
    return-object v0
.end method

.method public getDropDownView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 3
    .param p1, "position"    # I
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 262
    iget-boolean v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@2
    if-eqz v1, :cond_1

    #@4
    .line 263
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@6
    invoke-interface {v1, p1}, Landroid/database/Cursor;->moveToPosition(I)Z

    #@9
    .line 265
    if-nez p2, :cond_0

    #@b
    .line 266
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mContext:Landroid/content/Context;

    #@d
    iget-object v2, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@f
    invoke-virtual {p0, v1, v2, p3}, Landroid/support/v4/widget/CursorAdapter;->newDropDownView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;

    #@12
    move-result-object v0

    #@13
    .line 270
    .local v0, "v":Landroid/view/View;
    :goto_0
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mContext:Landroid/content/Context;

    #@15
    iget-object v2, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@17
    invoke-virtual {p0, v0, v1, v2}, Landroid/support/v4/widget/CursorAdapter;->bindView(Landroid/view/View;Landroid/content/Context;Landroid/database/Cursor;)V

    #@1a
    .line 273
    .end local v0    # "v":Landroid/view/View;
    :goto_1
    return-object v0

    #@1b
    .line 268
    :cond_0
    move-object v0, p2

    #@1c
    .restart local v0    # "v":Landroid/view/View;
    goto :goto_0

    #@1d
    .line 273
    .end local v0    # "v":Landroid/view/View;
    :cond_1
    const/4 v0, 0x0

    #@1e
    goto :goto_1
.end method

.method public getFilter()Landroid/widget/Filter;
    .locals 1

    #@0
    .prologue
    .line 404
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursorFilter:Landroid/support/v4/widget/CursorFilter;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 405
    new-instance v0, Landroid/support/v4/widget/CursorFilter;

    #@6
    invoke-direct {v0, p0}, Landroid/support/v4/widget/CursorFilter;-><init>(Landroid/support/v4/widget/CursorFilter$CursorFilterClient;)V

    #@9
    iput-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursorFilter:Landroid/support/v4/widget/CursorFilter;

    #@b
    .line 407
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursorFilter:Landroid/support/v4/widget/CursorFilter;

    #@d
    return-object v0
.end method

.method public getFilterQueryProvider()Landroid/widget/FilterQueryProvider;
    .locals 1

    #@0
    .prologue
    .line 420
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mFilterQueryProvider:Landroid/widget/FilterQueryProvider;

    #@2
    return-object v0
.end method

.method public getItem(I)Ljava/lang/Object;
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 212
    iget-boolean v0, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 213
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@a
    invoke-interface {v0, p1}, Landroid/database/Cursor;->moveToPosition(I)Z

    #@d
    .line 214
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@f
    .line 216
    :goto_0
    return-object v0

    #@10
    :cond_0
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method public getItemId(I)J
    .locals 3
    .param p1, "position"    # I

    #@0
    .prologue
    const-wide/16 v0, 0x0

    #@2
    .line 224
    iget-boolean v2, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@4
    if-eqz v2, :cond_0

    #@6
    iget-object v2, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@8
    if-eqz v2, :cond_0

    #@a
    .line 225
    iget-object v2, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@c
    invoke-interface {v2, p1}, Landroid/database/Cursor;->moveToPosition(I)Z

    #@f
    move-result v2

    #@10
    if-eqz v2, :cond_0

    #@12
    .line 226
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@14
    iget v1, p0, Landroid/support/v4/widget/CursorAdapter;->mRowIDColumn:I

    #@16
    invoke-interface {v0, v1}, Landroid/database/Cursor;->getLong(I)J

    #@19
    move-result-wide v0

    #@1a
    .line 231
    :cond_0
    return-wide v0
.end method

.method public getView(ILandroid/view/View;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 4
    .param p1, "position"    # I
    .param p2, "convertView"    # Landroid/view/View;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 244
    iget-boolean v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@2
    if-nez v1, :cond_0

    #@4
    .line 245
    new-instance v1, Ljava/lang/IllegalStateException;

    #@6
    const-string v2, "this should only be called when the cursor is valid"

    #@8
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@b
    throw v1

    #@c
    .line 247
    :cond_0
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@e
    invoke-interface {v1, p1}, Landroid/database/Cursor;->moveToPosition(I)Z

    #@11
    move-result v1

    #@12
    if-nez v1, :cond_1

    #@14
    .line 248
    new-instance v1, Ljava/lang/IllegalStateException;

    #@16
    new-instance v2, Ljava/lang/StringBuilder;

    #@18
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@1b
    const-string v3, "couldn\'t move cursor to position "

    #@1d
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@20
    move-result-object v2

    #@21
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@24
    move-result-object v2

    #@25
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@28
    move-result-object v2

    #@29
    invoke-direct {v1, v2}, Ljava/lang/IllegalStateException;-><init>(Ljava/lang/String;)V

    #@2c
    throw v1

    #@2d
    .line 251
    :cond_1
    if-nez p2, :cond_2

    #@2f
    .line 252
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mContext:Landroid/content/Context;

    #@31
    iget-object v2, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@33
    invoke-virtual {p0, v1, v2, p3}, Landroid/support/v4/widget/CursorAdapter;->newView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;

    #@36
    move-result-object v0

    #@37
    .line 256
    .local v0, "v":Landroid/view/View;
    :goto_0
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mContext:Landroid/content/Context;

    #@39
    iget-object v2, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@3b
    invoke-virtual {p0, v0, v1, v2}, Landroid/support/v4/widget/CursorAdapter;->bindView(Landroid/view/View;Landroid/content/Context;Landroid/database/Cursor;)V

    #@3e
    .line 257
    return-object v0

    #@3f
    .line 254
    .end local v0    # "v":Landroid/view/View;
    :cond_2
    move-object v0, p2

    #@40
    .restart local v0    # "v":Landroid/view/View;
    goto :goto_0
.end method

.method public hasStableIds()Z
    .locals 1

    #@0
    .prologue
    .line 237
    const/4 v0, 0x1

    #@1
    return v0
.end method

.method init(Landroid/content/Context;Landroid/database/Cursor;I)V
    .locals 4
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "c"    # Landroid/database/Cursor;
    .param p3, "flags"    # I

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    const/4 v3, 0x0

    #@2
    const/4 v0, 0x1

    #@3
    .line 164
    and-int/lit8 v2, p3, 0x1

    #@5
    if-ne v2, v0, :cond_2

    #@7
    .line 165
    or-int/lit8 p3, p3, 0x2

    #@9
    .line 166
    iput-boolean v0, p0, Landroid/support/v4/widget/CursorAdapter;->mAutoRequery:Z

    #@b
    .line 170
    :goto_0
    if-eqz p2, :cond_3

    #@d
    .line 171
    .local v0, "cursorPresent":Z
    :goto_1
    iput-object p2, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@f
    .line 172
    iput-boolean v0, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@11
    .line 173
    iput-object p1, p0, Landroid/support/v4/widget/CursorAdapter;->mContext:Landroid/content/Context;

    #@13
    .line 174
    if-eqz v0, :cond_4

    #@15
    const-string v1, "_id"

    #@17
    invoke-interface {p2, v1}, Landroid/database/Cursor;->getColumnIndexOrThrow(Ljava/lang/String;)I

    #@1a
    move-result v1

    #@1b
    :goto_2
    iput v1, p0, Landroid/support/v4/widget/CursorAdapter;->mRowIDColumn:I

    #@1d
    .line 175
    and-int/lit8 v1, p3, 0x2

    #@1f
    const/4 v2, 0x2

    #@20
    if-ne v1, v2, :cond_5

    #@22
    .line 176
    new-instance v1, Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@24
    invoke-direct {v1, p0}, Landroid/support/v4/widget/CursorAdapter$ChangeObserver;-><init>(Landroid/support/v4/widget/CursorAdapter;)V

    #@27
    iput-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@29
    .line 177
    new-instance v1, Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;

    #@2b
    invoke-direct {v1, p0, v3}, Landroid/support/v4/widget/CursorAdapter$MyDataSetObserver;-><init>(Landroid/support/v4/widget/CursorAdapter;Landroid/support/v4/widget/CursorAdapter$1;)V

    #@2e
    iput-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@30
    .line 183
    :goto_3
    if-eqz v0, :cond_1

    #@32
    .line 184
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@34
    if-eqz v1, :cond_0

    #@36
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@38
    invoke-interface {p2, v1}, Landroid/database/Cursor;->registerContentObserver(Landroid/database/ContentObserver;)V

    #@3b
    .line 185
    :cond_0
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@3d
    if-eqz v1, :cond_1

    #@3f
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@41
    invoke-interface {p2, v1}, Landroid/database/Cursor;->registerDataSetObserver(Landroid/database/DataSetObserver;)V

    #@44
    .line 187
    :cond_1
    return-void

    #@45
    .line 168
    .end local v0    # "cursorPresent":Z
    :cond_2
    iput-boolean v1, p0, Landroid/support/v4/widget/CursorAdapter;->mAutoRequery:Z

    #@47
    goto :goto_0

    #@48
    :cond_3
    move v0, v1

    #@49
    .line 170
    goto :goto_1

    #@4a
    .line 174
    .restart local v0    # "cursorPresent":Z
    :cond_4
    const/4 v1, -0x1

    #@4b
    goto :goto_2

    #@4c
    .line 179
    :cond_5
    iput-object v3, p0, Landroid/support/v4/widget/CursorAdapter;->mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@4e
    .line 180
    iput-object v3, p0, Landroid/support/v4/widget/CursorAdapter;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@50
    goto :goto_3
.end method

.method protected init(Landroid/content/Context;Landroid/database/Cursor;Z)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "c"    # Landroid/database/Cursor;
    .param p3, "autoRequery"    # Z
    .annotation runtime Ljava/lang/Deprecated;
    .end annotation

    #@0
    .prologue
    .line 160
    if-eqz p3, :cond_0

    #@2
    const/4 v0, 0x1

    #@3
    :goto_0
    invoke-virtual {p0, p1, p2, v0}, Landroid/support/v4/widget/CursorAdapter;->init(Landroid/content/Context;Landroid/database/Cursor;I)V

    #@6
    .line 161
    return-void

    #@7
    .line 160
    :cond_0
    const/4 v0, 0x2

    #@8
    goto :goto_0
.end method

.method public newDropDownView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "cursor"    # Landroid/database/Cursor;
    .param p3, "parent"    # Landroid/view/ViewGroup;

    #@0
    .prologue
    .line 296
    invoke-virtual {p0, p1, p2, p3}, Landroid/support/v4/widget/CursorAdapter;->newView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method public abstract newView(Landroid/content/Context;Landroid/database/Cursor;Landroid/view/ViewGroup;)Landroid/view/View;
.end method

.method protected onContentChanged()V
    .locals 1

    #@0
    .prologue
    .line 447
    iget-boolean v0, p0, Landroid/support/v4/widget/CursorAdapter;->mAutoRequery:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@6
    if-eqz v0, :cond_0

    #@8
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@a
    invoke-interface {v0}, Landroid/database/Cursor;->isClosed()Z

    #@d
    move-result v0

    #@e
    if-nez v0, :cond_0

    #@10
    .line 449
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@12
    invoke-interface {v0}, Landroid/database/Cursor;->requery()Z

    #@15
    move-result v0

    #@16
    iput-boolean v0, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@18
    .line 451
    :cond_0
    return-void
.end method

.method public runQueryOnBackgroundThread(Ljava/lang/CharSequence;)Landroid/database/Cursor;
    .locals 1
    .param p1, "constraint"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 396
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mFilterQueryProvider:Landroid/widget/FilterQueryProvider;

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 397
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mFilterQueryProvider:Landroid/widget/FilterQueryProvider;

    #@6
    invoke-interface {v0, p1}, Landroid/widget/FilterQueryProvider;->runQuery(Ljava/lang/CharSequence;)Landroid/database/Cursor;

    #@9
    move-result-object v0

    #@a
    .line 400
    :goto_0
    return-object v0

    #@b
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@d
    goto :goto_0
.end method

.method public setFilterQueryProvider(Landroid/widget/FilterQueryProvider;)V
    .locals 0
    .param p1, "filterQueryProvider"    # Landroid/widget/FilterQueryProvider;

    #@0
    .prologue
    .line 436
    iput-object p1, p0, Landroid/support/v4/widget/CursorAdapter;->mFilterQueryProvider:Landroid/widget/FilterQueryProvider;

    #@2
    .line 437
    return-void
.end method

.method public swapCursor(Landroid/database/Cursor;)Landroid/database/Cursor;
    .locals 2
    .param p1, "newCursor"    # Landroid/database/Cursor;

    #@0
    .prologue
    .line 332
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@2
    if-ne p1, v1, :cond_0

    #@4
    .line 333
    const/4 v0, 0x0

    #@5
    .line 354
    :goto_0
    return-object v0

    #@6
    .line 335
    :cond_0
    iget-object v0, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@8
    .line 336
    .local v0, "oldCursor":Landroid/database/Cursor;
    if-eqz v0, :cond_2

    #@a
    .line 337
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@c
    if-eqz v1, :cond_1

    #@e
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@10
    invoke-interface {v0, v1}, Landroid/database/Cursor;->unregisterContentObserver(Landroid/database/ContentObserver;)V

    #@13
    .line 338
    :cond_1
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@15
    if-eqz v1, :cond_2

    #@17
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@19
    invoke-interface {v0, v1}, Landroid/database/Cursor;->unregisterDataSetObserver(Landroid/database/DataSetObserver;)V

    #@1c
    .line 340
    :cond_2
    iput-object p1, p0, Landroid/support/v4/widget/CursorAdapter;->mCursor:Landroid/database/Cursor;

    #@1e
    .line 341
    if-eqz p1, :cond_5

    #@20
    .line 342
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@22
    if-eqz v1, :cond_3

    #@24
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mChangeObserver:Landroid/support/v4/widget/CursorAdapter$ChangeObserver;

    #@26
    invoke-interface {p1, v1}, Landroid/database/Cursor;->registerContentObserver(Landroid/database/ContentObserver;)V

    #@29
    .line 343
    :cond_3
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@2b
    if-eqz v1, :cond_4

    #@2d
    iget-object v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataSetObserver:Landroid/database/DataSetObserver;

    #@2f
    invoke-interface {p1, v1}, Landroid/database/Cursor;->registerDataSetObserver(Landroid/database/DataSetObserver;)V

    #@32
    .line 344
    :cond_4
    const-string v1, "_id"

    #@34
    invoke-interface {p1, v1}, Landroid/database/Cursor;->getColumnIndexOrThrow(Ljava/lang/String;)I

    #@37
    move-result v1

    #@38
    iput v1, p0, Landroid/support/v4/widget/CursorAdapter;->mRowIDColumn:I

    #@3a
    .line 345
    const/4 v1, 0x1

    #@3b
    iput-boolean v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@3d
    .line 347
    invoke-virtual {p0}, Landroid/support/v4/widget/CursorAdapter;->notifyDataSetChanged()V

    #@40
    goto :goto_0

    #@41
    .line 349
    :cond_5
    const/4 v1, -0x1

    #@42
    iput v1, p0, Landroid/support/v4/widget/CursorAdapter;->mRowIDColumn:I

    #@44
    .line 350
    const/4 v1, 0x0

    #@45
    iput-boolean v1, p0, Landroid/support/v4/widget/CursorAdapter;->mDataValid:Z

    #@47
    .line 352
    invoke-virtual {p0}, Landroid/support/v4/widget/CursorAdapter;->notifyDataSetInvalidated()V

    #@4a
    goto :goto_0
.end method

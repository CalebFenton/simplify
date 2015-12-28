.class public Landroid/support/v7/widget/SearchView;
.super Landroid/support/v7/widget/LinearLayoutCompat;
.source "SearchView.java"

# interfaces
.implements Landroid/support/v7/view/CollapsibleActionView;


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;,
        Landroid/support/v7/widget/SearchView$SearchAutoComplete;,
        Landroid/support/v7/widget/SearchView$OnSuggestionListener;,
        Landroid/support/v7/widget/SearchView$OnCloseListener;,
        Landroid/support/v7/widget/SearchView$OnQueryTextListener;
    }
.end annotation


# static fields
.field private static final DBG:Z = false

.field static final HIDDEN_METHOD_INVOKER:Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;

.field private static final IME_OPTION_NO_MICROPHONE:Ljava/lang/String; = "nm"

.field private static final IS_AT_LEAST_FROYO:Z

.field private static final LOG_TAG:Ljava/lang/String; = "SearchView"


# instance fields
.field private mAppSearchData:Landroid/os/Bundle;

.field private mClearingFocus:Z

.field private final mCloseButton:Landroid/widget/ImageView;

.field private mCollapsedImeOptions:I

.field private final mDropDownAnchor:Landroid/view/View;

.field private mExpandedInActionView:Z

.field private mIconified:Z

.field private mIconifiedByDefault:Z

.field private mMaxWidth:I

.field private mOldQueryText:Ljava/lang/CharSequence;

.field private final mOnClickListener:Landroid/view/View$OnClickListener;

.field private mOnCloseListener:Landroid/support/v7/widget/SearchView$OnCloseListener;

.field private final mOnEditorActionListener:Landroid/widget/TextView$OnEditorActionListener;

.field private final mOnItemClickListener:Landroid/widget/AdapterView$OnItemClickListener;

.field private final mOnItemSelectedListener:Landroid/widget/AdapterView$OnItemSelectedListener;

.field private mOnQueryChangeListener:Landroid/support/v7/widget/SearchView$OnQueryTextListener;

.field private mOnQueryTextFocusChangeListener:Landroid/view/View$OnFocusChangeListener;

.field private mOnSearchClickListener:Landroid/view/View$OnClickListener;

.field private mOnSuggestionListener:Landroid/support/v7/widget/SearchView$OnSuggestionListener;

.field private final mOutsideDrawablesCache:Ljava/util/WeakHashMap;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "Ljava/util/WeakHashMap",
            "<",
            "Ljava/lang/String;",
            "Landroid/graphics/drawable/Drawable$ConstantState;",
            ">;"
        }
    .end annotation
.end field

.field private mQueryHint:Ljava/lang/CharSequence;

.field private mQueryRefinement:Z

.field private final mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

.field private mReleaseCursorRunnable:Ljava/lang/Runnable;

.field private final mSearchButton:Landroid/widget/ImageView;

.field private final mSearchEditFrame:Landroid/view/View;

.field private final mSearchHintIcon:Landroid/widget/ImageView;

.field private final mSearchIconResId:I

.field private final mSearchPlate:Landroid/view/View;

.field private mSearchable:Landroid/app/SearchableInfo;

.field private mShowImeRunnable:Ljava/lang/Runnable;

.field private final mSubmitArea:Landroid/view/View;

.field private final mSubmitButton:Landroid/widget/ImageView;

.field private mSubmitButtonEnabled:Z

.field private final mSuggestionCommitIconResId:I

.field private final mSuggestionRowLayout:I

.field private mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

.field mTextKeyListener:Landroid/view/View$OnKeyListener;

.field private mTextWatcher:Landroid/text/TextWatcher;

.field private final mTintManager:Landroid/support/v7/internal/widget/TintManager;

.field private final mUpdateDrawableStateRunnable:Ljava/lang/Runnable;

.field private mUserQuery:Ljava/lang/CharSequence;

.field private final mVoiceAppSearchIntent:Landroid/content/Intent;

.field private final mVoiceButton:Landroid/widget/ImageView;

.field private mVoiceButtonEnabled:Z

.field private final mVoiceWebSearchIntent:Landroid/content/Intent;


# direct methods
.method static constructor <clinit>()V
    .locals 2

    #@0
    .prologue
    .line 109
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@2
    const/16 v1, 0x8

    #@4
    if-lt v0, v1, :cond_0

    #@6
    const/4 v0, 0x1

    #@7
    :goto_0
    sput-boolean v0, Landroid/support/v7/widget/SearchView;->IS_AT_LEAST_FROYO:Z

    #@9
    .line 160
    new-instance v0, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;

    #@b
    invoke-direct {v0}, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;-><init>()V

    #@e
    sput-object v0, Landroid/support/v7/widget/SearchView;->HIDDEN_METHOD_INVOKER:Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;

    #@10
    return-void

    #@11
    .line 109
    :cond_0
    const/4 v0, 0x0

    #@12
    goto :goto_0
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 264
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, p1, v0}, Landroid/support/v7/widget/SearchView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;)V

    #@4
    .line 265
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;)V
    .locals 1
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;

    #@0
    .prologue
    .line 268
    sget v0, Landroid/support/v7/appcompat/R$attr;->searchViewStyle:I

    #@2
    invoke-direct {p0, p1, p2, v0}, Landroid/support/v7/widget/SearchView;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@5
    .line 269
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V
    .locals 11
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "defStyleAttr"    # I

    #@0
    .prologue
    .line 272
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/widget/LinearLayoutCompat;-><init>(Landroid/content/Context;Landroid/util/AttributeSet;I)V

    #@3
    .line 166
    new-instance v8, Landroid/support/v7/widget/SearchView$1;

    #@5
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$1;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@8
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mShowImeRunnable:Ljava/lang/Runnable;

    #@a
    .line 177
    new-instance v8, Landroid/support/v7/widget/SearchView$2;

    #@c
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$2;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@f
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mUpdateDrawableStateRunnable:Ljava/lang/Runnable;

    #@11
    .line 183
    new-instance v8, Landroid/support/v7/widget/SearchView$3;

    #@13
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$3;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@16
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mReleaseCursorRunnable:Ljava/lang/Runnable;

    #@18
    .line 193
    new-instance v8, Ljava/util/WeakHashMap;

    #@1a
    invoke-direct {v8}, Ljava/util/WeakHashMap;-><init>()V

    #@1d
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mOutsideDrawablesCache:Ljava/util/WeakHashMap;

    #@1f
    .line 904
    new-instance v8, Landroid/support/v7/widget/SearchView$7;

    #@21
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$7;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@24
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mOnClickListener:Landroid/view/View$OnClickListener;

    #@26
    .line 928
    new-instance v8, Landroid/support/v7/widget/SearchView$8;

    #@28
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$8;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@2b
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mTextKeyListener:Landroid/view/View$OnKeyListener;

    #@2d
    .line 1103
    new-instance v8, Landroid/support/v7/widget/SearchView$9;

    #@2f
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$9;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@32
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mOnEditorActionListener:Landroid/widget/TextView$OnEditorActionListener;

    #@34
    .line 1287
    new-instance v8, Landroid/support/v7/widget/SearchView$10;

    #@36
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$10;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@39
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mOnItemClickListener:Landroid/widget/AdapterView$OnItemClickListener;

    #@3b
    .line 1298
    new-instance v8, Landroid/support/v7/widget/SearchView$11;

    #@3d
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$11;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@40
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mOnItemSelectedListener:Landroid/widget/AdapterView$OnItemSelectedListener;

    #@42
    .line 1591
    new-instance v8, Landroid/support/v7/widget/SearchView$12;

    #@44
    invoke-direct {v8, p0}, Landroid/support/v7/widget/SearchView$12;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@47
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mTextWatcher:Landroid/text/TextWatcher;

    #@49
    .line 274
    sget-object v8, Landroid/support/v7/appcompat/R$styleable;->SearchView:[I

    #@4b
    const/4 v9, 0x0

    #@4c
    invoke-static {p1, p2, v8, p3, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;

    #@4f
    move-result-object v0

    #@50
    .line 277
    .local v0, "a":Landroid/support/v7/internal/widget/TintTypedArray;
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@53
    move-result-object v8

    #@54
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@56
    .line 279
    const-string v8, "layout_inflater"

    #@58
    invoke-virtual {p1, v8}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@5b
    move-result-object v3

    #@5c
    check-cast v3, Landroid/view/LayoutInflater;

    #@5e
    .line 281
    .local v3, "inflater":Landroid/view/LayoutInflater;
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_layout:I

    #@60
    const/4 v9, 0x0

    #@61
    invoke-virtual {v0, v8, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@64
    move-result v5

    #@65
    .line 282
    .local v5, "layoutResId":I
    const/4 v8, 0x1

    #@66
    invoke-virtual {v3, v5, p0, v8}, Landroid/view/LayoutInflater;->inflate(ILandroid/view/ViewGroup;Z)Landroid/view/View;

    #@69
    .line 283
    sget v8, Landroid/support/v7/appcompat/R$id;->search_src_text:I

    #@6b
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@6e
    move-result-object v8

    #@6f
    check-cast v8, Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@71
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@73
    .line 284
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@75
    invoke-virtual {v8, p0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setSearchView(Landroid/support/v7/widget/SearchView;)V

    #@78
    .line 286
    sget v8, Landroid/support/v7/appcompat/R$id;->search_edit_frame:I

    #@7a
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@7d
    move-result-object v8

    #@7e
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mSearchEditFrame:Landroid/view/View;

    #@80
    .line 287
    sget v8, Landroid/support/v7/appcompat/R$id;->search_plate:I

    #@82
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@85
    move-result-object v8

    #@86
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mSearchPlate:Landroid/view/View;

    #@88
    .line 288
    sget v8, Landroid/support/v7/appcompat/R$id;->submit_area:I

    #@8a
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@8d
    move-result-object v8

    #@8e
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mSubmitArea:Landroid/view/View;

    #@90
    .line 289
    sget v8, Landroid/support/v7/appcompat/R$id;->search_button:I

    #@92
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@95
    move-result-object v8

    #@96
    check-cast v8, Landroid/widget/ImageView;

    #@98
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mSearchButton:Landroid/widget/ImageView;

    #@9a
    .line 290
    sget v8, Landroid/support/v7/appcompat/R$id;->search_go_btn:I

    #@9c
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@9f
    move-result-object v8

    #@a0
    check-cast v8, Landroid/widget/ImageView;

    #@a2
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mSubmitButton:Landroid/widget/ImageView;

    #@a4
    .line 291
    sget v8, Landroid/support/v7/appcompat/R$id;->search_close_btn:I

    #@a6
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@a9
    move-result-object v8

    #@aa
    check-cast v8, Landroid/widget/ImageView;

    #@ac
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mCloseButton:Landroid/widget/ImageView;

    #@ae
    .line 292
    sget v8, Landroid/support/v7/appcompat/R$id;->search_voice_btn:I

    #@b0
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@b3
    move-result-object v8

    #@b4
    check-cast v8, Landroid/widget/ImageView;

    #@b6
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mVoiceButton:Landroid/widget/ImageView;

    #@b8
    .line 293
    sget v8, Landroid/support/v7/appcompat/R$id;->search_mag_icon:I

    #@ba
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@bd
    move-result-object v8

    #@be
    check-cast v8, Landroid/widget/ImageView;

    #@c0
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mSearchHintIcon:Landroid/widget/ImageView;

    #@c2
    .line 295
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mSearchPlate:Landroid/view/View;

    #@c4
    sget v9, Landroid/support/v7/appcompat/R$styleable;->SearchView_queryBackground:I

    #@c6
    invoke-virtual {v0, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@c9
    move-result-object v9

    #@ca
    invoke-virtual {v8, v9}, Landroid/view/View;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@cd
    .line 296
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mSubmitArea:Landroid/view/View;

    #@cf
    sget v9, Landroid/support/v7/appcompat/R$styleable;->SearchView_submitBackground:I

    #@d1
    invoke-virtual {v0, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@d4
    move-result-object v9

    #@d5
    invoke-virtual {v8, v9}, Landroid/view/View;->setBackgroundDrawable(Landroid/graphics/drawable/Drawable;)V

    #@d8
    .line 297
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_searchIcon:I

    #@da
    const/4 v9, 0x0

    #@db
    invoke-virtual {v0, v8, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@de
    move-result v8

    #@df
    iput v8, p0, Landroid/support/v7/widget/SearchView;->mSearchIconResId:I

    #@e1
    .line 298
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mSearchButton:Landroid/widget/ImageView;

    #@e3
    iget v9, p0, Landroid/support/v7/widget/SearchView;->mSearchIconResId:I

    #@e5
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setImageResource(I)V

    #@e8
    .line 299
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mSubmitButton:Landroid/widget/ImageView;

    #@ea
    sget v9, Landroid/support/v7/appcompat/R$styleable;->SearchView_goIcon:I

    #@ec
    invoke-virtual {v0, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@ef
    move-result-object v9

    #@f0
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@f3
    .line 300
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mCloseButton:Landroid/widget/ImageView;

    #@f5
    sget v9, Landroid/support/v7/appcompat/R$styleable;->SearchView_closeIcon:I

    #@f7
    invoke-virtual {v0, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@fa
    move-result-object v9

    #@fb
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@fe
    .line 301
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mVoiceButton:Landroid/widget/ImageView;

    #@100
    sget v9, Landroid/support/v7/appcompat/R$styleable;->SearchView_voiceIcon:I

    #@102
    invoke-virtual {v0, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@105
    move-result-object v9

    #@106
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@109
    .line 302
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mSearchHintIcon:Landroid/widget/ImageView;

    #@10b
    sget v9, Landroid/support/v7/appcompat/R$styleable;->SearchView_searchIcon:I

    #@10d
    invoke-virtual {v0, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@110
    move-result-object v9

    #@111
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setImageDrawable(Landroid/graphics/drawable/Drawable;)V

    #@114
    .line 305
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_suggestionRowLayout:I

    #@116
    const/4 v9, 0x0

    #@117
    invoke-virtual {v0, v8, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@11a
    move-result v8

    #@11b
    iput v8, p0, Landroid/support/v7/widget/SearchView;->mSuggestionRowLayout:I

    #@11d
    .line 306
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_commitIcon:I

    #@11f
    const/4 v9, 0x0

    #@120
    invoke-virtual {v0, v8, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getResourceId(II)I

    #@123
    move-result v8

    #@124
    iput v8, p0, Landroid/support/v7/widget/SearchView;->mSuggestionCommitIconResId:I

    #@126
    .line 308
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mSearchButton:Landroid/widget/ImageView;

    #@128
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mOnClickListener:Landroid/view/View$OnClickListener;

    #@12a
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@12d
    .line 309
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mCloseButton:Landroid/widget/ImageView;

    #@12f
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mOnClickListener:Landroid/view/View$OnClickListener;

    #@131
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@134
    .line 310
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mSubmitButton:Landroid/widget/ImageView;

    #@136
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mOnClickListener:Landroid/view/View$OnClickListener;

    #@138
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@13b
    .line 311
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mVoiceButton:Landroid/widget/ImageView;

    #@13d
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mOnClickListener:Landroid/view/View$OnClickListener;

    #@13f
    invoke-virtual {v8, v9}, Landroid/widget/ImageView;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@142
    .line 312
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@144
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mOnClickListener:Landroid/view/View$OnClickListener;

    #@146
    invoke-virtual {v8, v9}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    #@149
    .line 314
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@14b
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mTextWatcher:Landroid/text/TextWatcher;

    #@14d
    invoke-virtual {v8, v9}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->addTextChangedListener(Landroid/text/TextWatcher;)V

    #@150
    .line 315
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@152
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mOnEditorActionListener:Landroid/widget/TextView$OnEditorActionListener;

    #@154
    invoke-virtual {v8, v9}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setOnEditorActionListener(Landroid/widget/TextView$OnEditorActionListener;)V

    #@157
    .line 316
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@159
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mOnItemClickListener:Landroid/widget/AdapterView$OnItemClickListener;

    #@15b
    invoke-virtual {v8, v9}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setOnItemClickListener(Landroid/widget/AdapterView$OnItemClickListener;)V

    #@15e
    .line 317
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@160
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mOnItemSelectedListener:Landroid/widget/AdapterView$OnItemSelectedListener;

    #@162
    invoke-virtual {v8, v9}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setOnItemSelectedListener(Landroid/widget/AdapterView$OnItemSelectedListener;)V

    #@165
    .line 318
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@167
    iget-object v9, p0, Landroid/support/v7/widget/SearchView;->mTextKeyListener:Landroid/view/View$OnKeyListener;

    #@169
    invoke-virtual {v8, v9}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setOnKeyListener(Landroid/view/View$OnKeyListener;)V

    #@16c
    .line 320
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@16e
    new-instance v9, Landroid/support/v7/widget/SearchView$4;

    #@170
    invoke-direct {v9, p0}, Landroid/support/v7/widget/SearchView$4;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@173
    invoke-virtual {v8, v9}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setOnFocusChangeListener(Landroid/view/View$OnFocusChangeListener;)V

    #@176
    .line 328
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_iconifiedByDefault:I

    #@178
    const/4 v9, 0x1

    #@179
    invoke-virtual {v0, v8, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getBoolean(IZ)Z

    #@17c
    move-result v8

    #@17d
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->setIconifiedByDefault(Z)V

    #@180
    .line 330
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_android_maxWidth:I

    #@182
    const/4 v9, -0x1

    #@183
    invoke-virtual {v0, v8, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getDimensionPixelSize(II)I

    #@186
    move-result v6

    #@187
    .line 331
    .local v6, "maxWidth":I
    const/4 v8, -0x1

    #@188
    if-eq v6, v8, :cond_0

    #@18a
    .line 332
    invoke-virtual {p0, v6}, Landroid/support/v7/widget/SearchView;->setMaxWidth(I)V

    #@18d
    .line 334
    :cond_0
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_queryHint:I

    #@18f
    invoke-virtual {v0, v8}, Landroid/support/v7/internal/widget/TintTypedArray;->getText(I)Ljava/lang/CharSequence;

    #@192
    move-result-object v7

    #@193
    .line 335
    .local v7, "queryHint":Ljava/lang/CharSequence;
    invoke-static {v7}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@196
    move-result v8

    #@197
    if-nez v8, :cond_1

    #@199
    .line 336
    invoke-virtual {p0, v7}, Landroid/support/v7/widget/SearchView;->setQueryHint(Ljava/lang/CharSequence;)V

    #@19c
    .line 338
    :cond_1
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_android_imeOptions:I

    #@19e
    const/4 v9, -0x1

    #@19f
    invoke-virtual {v0, v8, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@1a2
    move-result v2

    #@1a3
    .line 339
    .local v2, "imeOptions":I
    const/4 v8, -0x1

    #@1a4
    if-eq v2, v8, :cond_2

    #@1a6
    .line 340
    invoke-virtual {p0, v2}, Landroid/support/v7/widget/SearchView;->setImeOptions(I)V

    #@1a9
    .line 342
    :cond_2
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_android_inputType:I

    #@1ab
    const/4 v9, -0x1

    #@1ac
    invoke-virtual {v0, v8, v9}, Landroid/support/v7/internal/widget/TintTypedArray;->getInt(II)I

    #@1af
    move-result v4

    #@1b0
    .line 343
    .local v4, "inputType":I
    const/4 v8, -0x1

    #@1b1
    if-eq v4, v8, :cond_3

    #@1b3
    .line 344
    invoke-virtual {p0, v4}, Landroid/support/v7/widget/SearchView;->setInputType(I)V

    #@1b6
    .line 347
    :cond_3
    const/4 v1, 0x1

    #@1b7
    .line 348
    .local v1, "focusable":Z
    sget v8, Landroid/support/v7/appcompat/R$styleable;->SearchView_android_focusable:I

    #@1b9
    invoke-virtual {v0, v8, v1}, Landroid/support/v7/internal/widget/TintTypedArray;->getBoolean(IZ)Z

    #@1bc
    move-result v1

    #@1bd
    .line 349
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/SearchView;->setFocusable(Z)V

    #@1c0
    .line 351
    invoke-virtual {v0}, Landroid/support/v7/internal/widget/TintTypedArray;->recycle()V

    #@1c3
    .line 354
    new-instance v8, Landroid/content/Intent;

    #@1c5
    const-string v9, "android.speech.action.WEB_SEARCH"

    #@1c7
    invoke-direct {v8, v9}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    #@1ca
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mVoiceWebSearchIntent:Landroid/content/Intent;

    #@1cc
    .line 355
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mVoiceWebSearchIntent:Landroid/content/Intent;

    #@1ce
    const/high16 v9, 0x10000000

    #@1d0
    invoke-virtual {v8, v9}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    #@1d3
    .line 356
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mVoiceWebSearchIntent:Landroid/content/Intent;

    #@1d5
    const-string v9, "android.speech.extra.LANGUAGE_MODEL"

    #@1d7
    const-string v10, "web_search"

    #@1d9
    invoke-virtual {v8, v9, v10}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@1dc
    .line 359
    new-instance v8, Landroid/content/Intent;

    #@1de
    const-string v9, "android.speech.action.RECOGNIZE_SPEECH"

    #@1e0
    invoke-direct {v8, v9}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    #@1e3
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mVoiceAppSearchIntent:Landroid/content/Intent;

    #@1e5
    .line 360
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mVoiceAppSearchIntent:Landroid/content/Intent;

    #@1e7
    const/high16 v9, 0x10000000

    #@1e9
    invoke-virtual {v8, v9}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    #@1ec
    .line 362
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@1ee
    invoke-virtual {v8}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getDropDownAnchor()I

    #@1f1
    move-result v8

    #@1f2
    invoke-virtual {p0, v8}, Landroid/support/v7/widget/SearchView;->findViewById(I)Landroid/view/View;

    #@1f5
    move-result-object v8

    #@1f6
    iput-object v8, p0, Landroid/support/v7/widget/SearchView;->mDropDownAnchor:Landroid/view/View;

    #@1f8
    .line 363
    iget-object v8, p0, Landroid/support/v7/widget/SearchView;->mDropDownAnchor:Landroid/view/View;

    #@1fa
    if-eqz v8, :cond_4

    #@1fc
    .line 364
    sget v8, Landroid/os/Build$VERSION;->SDK_INT:I

    #@1fe
    const/16 v9, 0xb

    #@200
    if-lt v8, v9, :cond_5

    #@202
    .line 365
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->addOnLayoutChangeListenerToDropDownAnchorSDK11()V

    #@205
    .line 371
    :cond_4
    :goto_0
    iget-boolean v8, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@207
    invoke-direct {p0, v8}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@20a
    .line 372
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateQueryHint()V

    #@20d
    .line 373
    return-void

    #@20e
    .line 367
    :cond_5
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->addOnLayoutChangeListenerToDropDownAnchorBase()V

    #@211
    goto :goto_0
.end method

.method static synthetic access$000(Landroid/support/v7/widget/SearchView;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateFocusedState()V

    #@3
    return-void
.end method

.method static synthetic access$100(Landroid/support/v7/widget/SearchView;)Landroid/support/v4/widget/CursorAdapter;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@2
    return-object v0
.end method

.method static synthetic access$1000(Landroid/support/v7/widget/SearchView;)Landroid/widget/ImageView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mVoiceButton:Landroid/widget/ImageView;

    #@2
    return-object v0
.end method

.method static synthetic access$1100()Z
    .locals 1

    #@0
    .prologue
    .line 104
    sget-boolean v0, Landroid/support/v7/widget/SearchView;->IS_AT_LEAST_FROYO:Z

    #@2
    return v0
.end method

.method static synthetic access$1200(Landroid/support/v7/widget/SearchView;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->onVoiceClicked()V

    #@3
    return-void
.end method

.method static synthetic access$1300(Landroid/support/v7/widget/SearchView;)Landroid/support/v7/widget/SearchView$SearchAutoComplete;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    return-object v0
.end method

.method static synthetic access$1400(Landroid/support/v7/widget/SearchView;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->forceSuggestionQuery()V

    #@3
    return-void
.end method

.method static synthetic access$1500(Landroid/support/v7/widget/SearchView;)Landroid/app/SearchableInfo;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@2
    return-object v0
.end method

.method static synthetic access$1600(Landroid/support/v7/widget/SearchView;Landroid/view/View;ILandroid/view/KeyEvent;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;
    .param p1, "x1"    # Landroid/view/View;
    .param p2, "x2"    # I
    .param p3, "x3"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    .line 104
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/widget/SearchView;->onSuggestionsKey(Landroid/view/View;ILandroid/view/KeyEvent;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method static synthetic access$1800(Landroid/support/v7/widget/SearchView;ILjava/lang/String;Ljava/lang/String;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;
    .param p1, "x1"    # I
    .param p2, "x2"    # Ljava/lang/String;
    .param p3, "x3"    # Ljava/lang/String;

    #@0
    .prologue
    .line 104
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/widget/SearchView;->launchQuerySearch(ILjava/lang/String;Ljava/lang/String;)V

    #@3
    return-void
.end method

.method static synthetic access$1900(Landroid/support/v7/widget/SearchView;IILjava/lang/String;)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;
    .param p1, "x1"    # I
    .param p2, "x2"    # I
    .param p3, "x3"    # Ljava/lang/String;

    #@0
    .prologue
    .line 104
    invoke-direct {p0, p1, p2, p3}, Landroid/support/v7/widget/SearchView;->onItemClicked(IILjava/lang/String;)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method static synthetic access$200(Landroid/support/v7/widget/SearchView;)Landroid/view/View$OnFocusChangeListener;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mOnQueryTextFocusChangeListener:Landroid/view/View$OnFocusChangeListener;

    #@2
    return-object v0
.end method

.method static synthetic access$2000(Landroid/support/v7/widget/SearchView;I)Z
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;
    .param p1, "x1"    # I

    #@0
    .prologue
    .line 104
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SearchView;->onItemSelected(I)Z

    #@3
    move-result v0

    #@4
    return v0
.end method

.method static synthetic access$2100(Landroid/support/v7/widget/SearchView;Ljava/lang/CharSequence;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;
    .param p1, "x1"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 104
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SearchView;->onTextChanged(Ljava/lang/CharSequence;)V

    #@3
    return-void
.end method

.method static synthetic access$2200(Landroid/support/v7/widget/SearchView;Z)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;
    .param p1, "x1"    # Z

    #@0
    .prologue
    .line 104
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SearchView;->setImeVisibility(Z)V

    #@3
    return-void
.end method

.method static synthetic access$300(Landroid/support/v7/widget/SearchView;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->adjustDropDownSizeAndPosition()V

    #@3
    return-void
.end method

.method static synthetic access$400(Landroid/support/v7/widget/SearchView;)Landroid/widget/ImageView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSearchButton:Landroid/widget/ImageView;

    #@2
    return-object v0
.end method

.method static synthetic access$500(Landroid/support/v7/widget/SearchView;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->onSearchClicked()V

    #@3
    return-void
.end method

.method static synthetic access$600(Landroid/support/v7/widget/SearchView;)Landroid/widget/ImageView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mCloseButton:Landroid/widget/ImageView;

    #@2
    return-object v0
.end method

.method static synthetic access$700(Landroid/support/v7/widget/SearchView;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->onCloseClicked()V

    #@3
    return-void
.end method

.method static synthetic access$800(Landroid/support/v7/widget/SearchView;)Landroid/widget/ImageView;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSubmitButton:Landroid/widget/ImageView;

    #@2
    return-object v0
.end method

.method static synthetic access$900(Landroid/support/v7/widget/SearchView;)V
    .locals 0
    .param p0, "x0"    # Landroid/support/v7/widget/SearchView;

    #@0
    .prologue
    .line 104
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->onSubmitQuery()V

    #@3
    return-void
.end method

.method private addOnLayoutChangeListenerToDropDownAnchorBase()V
    .locals 2

    #@0
    .prologue
    .line 387
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mDropDownAnchor:Landroid/view/View;

    #@2
    invoke-virtual {v0}, Landroid/view/View;->getViewTreeObserver()Landroid/view/ViewTreeObserver;

    #@5
    move-result-object v0

    #@6
    new-instance v1, Landroid/support/v7/widget/SearchView$6;

    #@8
    invoke-direct {v1, p0}, Landroid/support/v7/widget/SearchView$6;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@b
    invoke-virtual {v0, v1}, Landroid/view/ViewTreeObserver;->addOnGlobalLayoutListener(Landroid/view/ViewTreeObserver$OnGlobalLayoutListener;)V

    #@e
    .line 394
    return-void
.end method

.method private addOnLayoutChangeListenerToDropDownAnchorSDK11()V
    .locals 2
    .annotation build Landroid/annotation/TargetApi;
        value = 0xb
    .end annotation

    #@0
    .prologue
    .line 377
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mDropDownAnchor:Landroid/view/View;

    #@2
    new-instance v1, Landroid/support/v7/widget/SearchView$5;

    #@4
    invoke-direct {v1, p0}, Landroid/support/v7/widget/SearchView$5;-><init>(Landroid/support/v7/widget/SearchView;)V

    #@7
    invoke-virtual {v0, v1}, Landroid/view/View;->addOnLayoutChangeListener(Landroid/view/View$OnLayoutChangeListener;)V

    #@a
    .line 384
    return-void
.end method

.method private adjustDropDownSizeAndPosition()V
    .locals 9

    #@0
    .prologue
    .line 1244
    iget-object v7, p0, Landroid/support/v7/widget/SearchView;->mDropDownAnchor:Landroid/view/View;

    #@2
    invoke-virtual {v7}, Landroid/view/View;->getWidth()I

    #@5
    move-result v7

    #@6
    const/4 v8, 0x1

    #@7
    if-le v7, v8, :cond_0

    #@9
    .line 1245
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@c
    move-result-object v7

    #@d
    invoke-virtual {v7}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@10
    move-result-object v5

    #@11
    .line 1246
    .local v5, "res":Landroid/content/res/Resources;
    iget-object v7, p0, Landroid/support/v7/widget/SearchView;->mSearchPlate:Landroid/view/View;

    #@13
    invoke-virtual {v7}, Landroid/view/View;->getPaddingLeft()I

    #@16
    move-result v0

    #@17
    .line 1247
    .local v0, "anchorPadding":I
    new-instance v1, Landroid/graphics/Rect;

    #@19
    invoke-direct {v1}, Landroid/graphics/Rect;-><init>()V

    #@1c
    .line 1248
    .local v1, "dropDownPadding":Landroid/graphics/Rect;
    invoke-static {p0}, Landroid/support/v7/internal/widget/ViewUtils;->isLayoutRtl(Landroid/view/View;)Z

    #@1f
    move-result v3

    #@20
    .line 1249
    .local v3, "isLayoutRtl":Z
    iget-boolean v7, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@22
    if-eqz v7, :cond_1

    #@24
    sget v7, Landroid/support/v7/appcompat/R$dimen;->abc_dropdownitem_icon_width:I

    #@26
    invoke-virtual {v5, v7}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    #@29
    move-result v7

    #@2a
    sget v8, Landroid/support/v7/appcompat/R$dimen;->abc_dropdownitem_text_padding_left:I

    #@2c
    invoke-virtual {v5, v8}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    #@2f
    move-result v8

    #@30
    add-int v2, v7, v8

    #@32
    .line 1253
    .local v2, "iconOffset":I
    :goto_0
    iget-object v7, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@34
    invoke-virtual {v7}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getDropDownBackground()Landroid/graphics/drawable/Drawable;

    #@37
    move-result-object v7

    #@38
    invoke-virtual {v7, v1}, Landroid/graphics/drawable/Drawable;->getPadding(Landroid/graphics/Rect;)Z

    #@3b
    .line 1255
    if-eqz v3, :cond_2

    #@3d
    .line 1256
    iget v7, v1, Landroid/graphics/Rect;->left:I

    #@3f
    neg-int v4, v7

    #@40
    .line 1260
    .local v4, "offset":I
    :goto_1
    iget-object v7, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@42
    invoke-virtual {v7, v4}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setDropDownHorizontalOffset(I)V

    #@45
    .line 1261
    iget-object v7, p0, Landroid/support/v7/widget/SearchView;->mDropDownAnchor:Landroid/view/View;

    #@47
    invoke-virtual {v7}, Landroid/view/View;->getWidth()I

    #@4a
    move-result v7

    #@4b
    iget v8, v1, Landroid/graphics/Rect;->left:I

    #@4d
    add-int/2addr v7, v8

    #@4e
    iget v8, v1, Landroid/graphics/Rect;->right:I

    #@50
    add-int/2addr v7, v8

    #@51
    add-int/2addr v7, v2

    #@52
    sub-int v6, v7, v0

    #@54
    .line 1263
    .local v6, "width":I
    iget-object v7, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@56
    invoke-virtual {v7, v6}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setDropDownWidth(I)V

    #@59
    .line 1265
    .end local v0    # "anchorPadding":I
    .end local v1    # "dropDownPadding":Landroid/graphics/Rect;
    .end local v2    # "iconOffset":I
    .end local v3    # "isLayoutRtl":Z
    .end local v4    # "offset":I
    .end local v5    # "res":Landroid/content/res/Resources;
    .end local v6    # "width":I
    :cond_0
    return-void

    #@5a
    .line 1249
    .restart local v0    # "anchorPadding":I
    .restart local v1    # "dropDownPadding":Landroid/graphics/Rect;
    .restart local v3    # "isLayoutRtl":Z
    .restart local v5    # "res":Landroid/content/res/Resources;
    :cond_1
    const/4 v2, 0x0

    #@5b
    goto :goto_0

    #@5c
    .line 1258
    .restart local v2    # "iconOffset":I
    :cond_2
    iget v7, v1, Landroid/graphics/Rect;->left:I

    #@5e
    add-int/2addr v7, v2

    #@5f
    sub-int v4, v0, v7

    #@61
    .restart local v4    # "offset":I
    goto :goto_1
.end method

.method private createIntent(Ljava/lang/String;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Landroid/content/Intent;
    .locals 3
    .param p1, "action"    # Ljava/lang/String;
    .param p2, "data"    # Landroid/net/Uri;
    .param p3, "extraData"    # Ljava/lang/String;
    .param p4, "query"    # Ljava/lang/String;
    .param p5, "actionKey"    # I
    .param p6, "actionMsg"    # Ljava/lang/String;

    #@0
    .prologue
    .line 1414
    new-instance v0, Landroid/content/Intent;

    #@2
    invoke-direct {v0, p1}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    #@5
    .line 1415
    .local v0, "intent":Landroid/content/Intent;
    const/high16 v1, 0x10000000

    #@7
    invoke-virtual {v0, v1}, Landroid/content/Intent;->addFlags(I)Landroid/content/Intent;

    #@a
    .line 1419
    if-eqz p2, :cond_0

    #@c
    .line 1420
    invoke-virtual {v0, p2}, Landroid/content/Intent;->setData(Landroid/net/Uri;)Landroid/content/Intent;

    #@f
    .line 1422
    :cond_0
    const-string v1, "user_query"

    #@11
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mUserQuery:Ljava/lang/CharSequence;

    #@13
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/CharSequence;)Landroid/content/Intent;

    #@16
    .line 1423
    if-eqz p4, :cond_1

    #@18
    .line 1424
    const-string v1, "query"

    #@1a
    invoke-virtual {v0, v1, p4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@1d
    .line 1426
    :cond_1
    if-eqz p3, :cond_2

    #@1f
    .line 1427
    const-string v1, "intent_extra_data_key"

    #@21
    invoke-virtual {v0, v1, p3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@24
    .line 1429
    :cond_2
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mAppSearchData:Landroid/os/Bundle;

    #@26
    if-eqz v1, :cond_3

    #@28
    .line 1430
    const-string v1, "app_data"

    #@2a
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mAppSearchData:Landroid/os/Bundle;

    #@2c
    invoke-virtual {v0, v1, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Bundle;)Landroid/content/Intent;

    #@2f
    .line 1432
    :cond_3
    if-eqz p5, :cond_4

    #@31
    .line 1433
    const-string v1, "action_key"

    #@33
    invoke-virtual {v0, v1, p5}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    #@36
    .line 1434
    const-string v1, "action_msg"

    #@38
    invoke-virtual {v0, v1, p6}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@3b
    .line 1436
    :cond_4
    sget-boolean v1, Landroid/support/v7/widget/SearchView;->IS_AT_LEAST_FROYO:Z

    #@3d
    if-eqz v1, :cond_5

    #@3f
    .line 1437
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@41
    invoke-virtual {v1}, Landroid/app/SearchableInfo;->getSearchActivity()Landroid/content/ComponentName;

    #@44
    move-result-object v1

    #@45
    invoke-virtual {v0, v1}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@48
    .line 1439
    :cond_5
    return-object v0
.end method

.method private createIntentFromSuggestion(Landroid/database/Cursor;ILjava/lang/String;)Landroid/content/Intent;
    .locals 12
    .param p1, "c"    # Landroid/database/Cursor;
    .param p2, "actionKey"    # I
    .param p3, "actionMsg"    # Ljava/lang/String;

    #@0
    .prologue
    .line 1538
    :try_start_0
    const-string v0, "suggest_intent_action"

    #@2
    invoke-static {p1, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;

    #@5
    move-result-object v1

    #@6
    .line 1540
    .local v1, "action":Ljava/lang/String;
    if-nez v1, :cond_0

    #@8
    sget v0, Landroid/os/Build$VERSION;->SDK_INT:I

    #@a
    const/16 v5, 0x8

    #@c
    if-lt v0, v5, :cond_0

    #@e
    .line 1541
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@10
    invoke-virtual {v0}, Landroid/app/SearchableInfo;->getSuggestIntentAction()Ljava/lang/String;

    #@13
    move-result-object v1

    #@14
    .line 1543
    :cond_0
    if-nez v1, :cond_1

    #@16
    .line 1544
    const-string v1, "android.intent.action.SEARCH"

    #@18
    .line 1548
    :cond_1
    const-string v0, "suggest_intent_data"

    #@1a
    invoke-static {p1, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;

    #@1d
    move-result-object v7

    #@1e
    .line 1549
    .local v7, "data":Ljava/lang/String;
    sget-boolean v0, Landroid/support/v7/widget/SearchView;->IS_AT_LEAST_FROYO:Z

    #@20
    if-eqz v0, :cond_2

    #@22
    if-nez v7, :cond_2

    #@24
    .line 1550
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@26
    invoke-virtual {v0}, Landroid/app/SearchableInfo;->getSuggestIntentData()Ljava/lang/String;

    #@29
    move-result-object v7

    #@2a
    .line 1553
    :cond_2
    if-eqz v7, :cond_3

    #@2c
    .line 1554
    const-string v0, "suggest_intent_data_id"

    #@2e
    invoke-static {p1, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;

    #@31
    move-result-object v10

    #@32
    .line 1555
    .local v10, "id":Ljava/lang/String;
    if-eqz v10, :cond_3

    #@34
    .line 1556
    new-instance v0, Ljava/lang/StringBuilder;

    #@36
    invoke-direct {v0}, Ljava/lang/StringBuilder;-><init>()V

    #@39
    invoke-virtual {v0, v7}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@3c
    move-result-object v0

    #@3d
    const-string v5, "/"

    #@3f
    invoke-virtual {v0, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@42
    move-result-object v0

    #@43
    invoke-static {v10}, Landroid/net/Uri;->encode(Ljava/lang/String;)Ljava/lang/String;

    #@46
    move-result-object v5

    #@47
    invoke-virtual {v0, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@4a
    move-result-object v0

    #@4b
    invoke-virtual {v0}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@4e
    move-result-object v7

    #@4f
    .line 1559
    .end local v10    # "id":Ljava/lang/String;
    :cond_3
    if-nez v7, :cond_4

    #@51
    const/4 v2, 0x0

    #@52
    .line 1561
    .local v2, "dataUri":Landroid/net/Uri;
    :goto_0
    const-string v0, "suggest_intent_query"

    #@54
    invoke-static {p1, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;

    #@57
    move-result-object v4

    #@58
    .line 1562
    .local v4, "query":Ljava/lang/String;
    const-string v0, "suggest_intent_extra_data"

    #@5a
    invoke-static {p1, v0}, Landroid/support/v7/widget/SuggestionsAdapter;->getColumnString(Landroid/database/Cursor;Ljava/lang/String;)Ljava/lang/String;

    #@5d
    move-result-object v3

    #@5e
    .local v3, "extraData":Ljava/lang/String;
    move-object v0, p0

    #@5f
    move v5, p2

    #@60
    move-object v6, p3

    #@61
    .line 1564
    invoke-direct/range {v0 .. v6}, Landroid/support/v7/widget/SearchView;->createIntent(Ljava/lang/String;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Landroid/content/Intent;

    #@64
    move-result-object v0

    #@65
    .line 1574
    .end local v1    # "action":Ljava/lang/String;
    .end local v2    # "dataUri":Landroid/net/Uri;
    .end local v3    # "extraData":Ljava/lang/String;
    .end local v4    # "query":Ljava/lang/String;
    .end local v7    # "data":Ljava/lang/String;
    :goto_1
    return-object v0

    #@66
    .line 1559
    .restart local v1    # "action":Ljava/lang/String;
    .restart local v7    # "data":Ljava/lang/String;
    :cond_4
    invoke-static {v7}, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
    :try_end_0
    .catch Ljava/lang/RuntimeException; {:try_start_0 .. :try_end_0} :catch_0

    #@69
    move-result-object v2

    #@6a
    goto :goto_0

    #@6b
    .line 1565
    .end local v1    # "action":Ljava/lang/String;
    .end local v7    # "data":Ljava/lang/String;
    :catch_0
    move-exception v8

    #@6c
    .line 1568
    .local v8, "e":Ljava/lang/RuntimeException;
    :try_start_1
    invoke-interface {p1}, Landroid/database/Cursor;->getPosition()I
    :try_end_1
    .catch Ljava/lang/RuntimeException; {:try_start_1 .. :try_end_1} :catch_1

    #@6f
    move-result v11

    #@70
    .line 1572
    .local v11, "rowNum":I
    :goto_2
    const-string v0, "SearchView"

    #@72
    new-instance v5, Ljava/lang/StringBuilder;

    #@74
    invoke-direct {v5}, Ljava/lang/StringBuilder;-><init>()V

    #@77
    const-string v6, "Search suggestions cursor at row "

    #@79
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@7c
    move-result-object v5

    #@7d
    invoke-virtual {v5, v11}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    #@80
    move-result-object v5

    #@81
    const-string v6, " returned exception."

    #@83
    invoke-virtual {v5, v6}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@86
    move-result-object v5

    #@87
    invoke-virtual {v5}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@8a
    move-result-object v5

    #@8b
    invoke-static {v0, v5, v8}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@8e
    .line 1574
    const/4 v0, 0x0

    #@8f
    goto :goto_1

    #@90
    .line 1569
    .end local v11    # "rowNum":I
    :catch_1
    move-exception v9

    #@91
    .line 1570
    .local v9, "e2":Ljava/lang/RuntimeException;
    const/4 v11, -0x1

    #@92
    .restart local v11    # "rowNum":I
    goto :goto_2
.end method

.method private createVoiceAppSearchIntent(Landroid/content/Intent;Landroid/app/SearchableInfo;)Landroid/content/Intent;
    .locals 13
    .param p1, "baseIntent"    # Landroid/content/Intent;
    .param p2, "searchable"    # Landroid/app/SearchableInfo;
    .annotation build Landroid/annotation/TargetApi;
        value = 0x8
    .end annotation

    #@0
    .prologue
    .line 1463
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getSearchActivity()Landroid/content/ComponentName;

    #@3
    move-result-object v8

    #@4
    .line 1468
    .local v8, "searchActivity":Landroid/content/ComponentName;
    new-instance v6, Landroid/content/Intent;

    #@6
    const-string v10, "android.intent.action.SEARCH"

    #@8
    invoke-direct {v6, v10}, Landroid/content/Intent;-><init>(Ljava/lang/String;)V

    #@b
    .line 1469
    .local v6, "queryIntent":Landroid/content/Intent;
    invoke-virtual {v6, v8}, Landroid/content/Intent;->setComponent(Landroid/content/ComponentName;)Landroid/content/Intent;

    #@e
    .line 1470
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@11
    move-result-object v10

    #@12
    const/4 v11, 0x0

    #@13
    const/high16 v12, 0x40000000    # 2.0f

    #@15
    invoke-static {v10, v11, v6, v12}, Landroid/app/PendingIntent;->getActivity(Landroid/content/Context;ILandroid/content/Intent;I)Landroid/app/PendingIntent;

    #@18
    move-result-object v3

    #@19
    .line 1477
    .local v3, "pending":Landroid/app/PendingIntent;
    new-instance v5, Landroid/os/Bundle;

    #@1b
    invoke-direct {v5}, Landroid/os/Bundle;-><init>()V

    #@1e
    .line 1478
    .local v5, "queryExtras":Landroid/os/Bundle;
    iget-object v10, p0, Landroid/support/v7/widget/SearchView;->mAppSearchData:Landroid/os/Bundle;

    #@20
    if-eqz v10, :cond_0

    #@22
    .line 1479
    const-string v10, "app_data"

    #@24
    iget-object v11, p0, Landroid/support/v7/widget/SearchView;->mAppSearchData:Landroid/os/Bundle;

    #@26
    invoke-virtual {v5, v10, v11}, Landroid/os/Bundle;->putParcelable(Ljava/lang/String;Landroid/os/Parcelable;)V

    #@29
    .line 1485
    :cond_0
    new-instance v9, Landroid/content/Intent;

    #@2b
    invoke-direct {v9, p1}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    #@2e
    .line 1488
    .local v9, "voiceIntent":Landroid/content/Intent;
    const-string v1, "free_form"

    #@30
    .line 1489
    .local v1, "languageModel":Ljava/lang/String;
    const/4 v4, 0x0

    #@31
    .line 1490
    .local v4, "prompt":Ljava/lang/String;
    const/4 v0, 0x0

    #@32
    .line 1491
    .local v0, "language":Ljava/lang/String;
    const/4 v2, 0x1

    #@33
    .line 1493
    .local v2, "maxResults":I
    sget v10, Landroid/os/Build$VERSION;->SDK_INT:I

    #@35
    const/16 v11, 0x8

    #@37
    if-lt v10, v11, :cond_4

    #@39
    .line 1494
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getResources()Landroid/content/res/Resources;

    #@3c
    move-result-object v7

    #@3d
    .line 1495
    .local v7, "resources":Landroid/content/res/Resources;
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getVoiceLanguageModeId()I

    #@40
    move-result v10

    #@41
    if-eqz v10, :cond_1

    #@43
    .line 1496
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getVoiceLanguageModeId()I

    #@46
    move-result v10

    #@47
    invoke-virtual {v7, v10}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@4a
    move-result-object v1

    #@4b
    .line 1498
    :cond_1
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getVoicePromptTextId()I

    #@4e
    move-result v10

    #@4f
    if-eqz v10, :cond_2

    #@51
    .line 1499
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getVoicePromptTextId()I

    #@54
    move-result v10

    #@55
    invoke-virtual {v7, v10}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@58
    move-result-object v4

    #@59
    .line 1501
    :cond_2
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getVoiceLanguageId()I

    #@5c
    move-result v10

    #@5d
    if-eqz v10, :cond_3

    #@5f
    .line 1502
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getVoiceLanguageId()I

    #@62
    move-result v10

    #@63
    invoke-virtual {v7, v10}, Landroid/content/res/Resources;->getString(I)Ljava/lang/String;

    #@66
    move-result-object v0

    #@67
    .line 1504
    :cond_3
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getVoiceMaxResults()I

    #@6a
    move-result v10

    #@6b
    if-eqz v10, :cond_4

    #@6d
    .line 1505
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getVoiceMaxResults()I

    #@70
    move-result v2

    #@71
    .line 1508
    .end local v7    # "resources":Landroid/content/res/Resources;
    :cond_4
    const-string v10, "android.speech.extra.LANGUAGE_MODEL"

    #@73
    invoke-virtual {v9, v10, v1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@76
    .line 1509
    const-string v10, "android.speech.extra.PROMPT"

    #@78
    invoke-virtual {v9, v10, v4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@7b
    .line 1510
    const-string v10, "android.speech.extra.LANGUAGE"

    #@7d
    invoke-virtual {v9, v10, v0}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@80
    .line 1511
    const-string v10, "android.speech.extra.MAX_RESULTS"

    #@82
    invoke-virtual {v9, v10, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;

    #@85
    .line 1512
    const-string v11, "calling_package"

    #@87
    if-nez v8, :cond_5

    #@89
    const/4 v10, 0x0

    #@8a
    :goto_0
    invoke-virtual {v9, v11, v10}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@8d
    .line 1516
    const-string v10, "android.speech.extra.RESULTS_PENDINGINTENT"

    #@8f
    invoke-virtual {v9, v10, v3}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Parcelable;)Landroid/content/Intent;

    #@92
    .line 1517
    const-string v10, "android.speech.extra.RESULTS_PENDINGINTENT_BUNDLE"

    #@94
    invoke-virtual {v9, v10, v5}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Landroid/os/Bundle;)Landroid/content/Intent;

    #@97
    .line 1519
    return-object v9

    #@98
    .line 1512
    :cond_5
    invoke-virtual {v8}, Landroid/content/ComponentName;->flattenToShortString()Ljava/lang/String;

    #@9b
    move-result-object v10

    #@9c
    goto :goto_0
.end method

.method private createVoiceWebSearchIntent(Landroid/content/Intent;Landroid/app/SearchableInfo;)Landroid/content/Intent;
    .locals 4
    .param p1, "baseIntent"    # Landroid/content/Intent;
    .param p2, "searchable"    # Landroid/app/SearchableInfo;
    .annotation build Landroid/annotation/TargetApi;
        value = 0x8
    .end annotation

    #@0
    .prologue
    .line 1447
    new-instance v1, Landroid/content/Intent;

    #@2
    invoke-direct {v1, p1}, Landroid/content/Intent;-><init>(Landroid/content/Intent;)V

    #@5
    .line 1448
    .local v1, "voiceIntent":Landroid/content/Intent;
    invoke-virtual {p2}, Landroid/app/SearchableInfo;->getSearchActivity()Landroid/content/ComponentName;

    #@8
    move-result-object v0

    #@9
    .line 1449
    .local v0, "searchActivity":Landroid/content/ComponentName;
    const-string v3, "calling_package"

    #@b
    if-nez v0, :cond_0

    #@d
    const/4 v2, 0x0

    #@e
    :goto_0
    invoke-virtual {v1, v3, v2}, Landroid/content/Intent;->putExtra(Ljava/lang/String;Ljava/lang/String;)Landroid/content/Intent;

    #@11
    .line 1451
    return-object v1

    #@12
    .line 1449
    :cond_0
    invoke-virtual {v0}, Landroid/content/ComponentName;->flattenToShortString()Ljava/lang/String;

    #@15
    move-result-object v2

    #@16
    goto :goto_0
.end method

.method private dismissSuggestions()V
    .locals 1

    #@0
    .prologue
    .line 1143
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->dismissDropDown()V

    #@5
    .line 1144
    return-void
.end method

.method private forceSuggestionQuery()V
    .locals 2

    #@0
    .prologue
    .line 1579
    sget-object v0, Landroid/support/v7/widget/SearchView;->HIDDEN_METHOD_INVOKER:Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;

    #@2
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@4
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doBeforeTextChanged(Landroid/widget/AutoCompleteTextView;)V

    #@7
    .line 1580
    sget-object v0, Landroid/support/v7/widget/SearchView;->HIDDEN_METHOD_INVOKER:Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;

    #@9
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@b
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->doAfterTextChanged(Landroid/widget/AutoCompleteTextView;)V

    #@e
    .line 1581
    return-void
.end method

.method private getDecoratedHint(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;
    .locals 9
    .param p1, "hintText"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    const/4 v8, 0x0

    #@1
    .line 1016
    iget-boolean v3, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@3
    if-nez v3, :cond_0

    #@5
    .line 1027
    .end local p1    # "hintText":Ljava/lang/CharSequence;
    :goto_0
    return-object p1

    #@6
    .line 1020
    .restart local p1    # "hintText":Ljava/lang/CharSequence;
    :cond_0
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@8
    iget v4, p0, Landroid/support/v7/widget/SearchView;->mSearchIconResId:I

    #@a
    invoke-virtual {v3, v4}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@d
    move-result-object v0

    #@e
    .line 1021
    .local v0, "searchIcon":Landroid/graphics/drawable/Drawable;
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@10
    invoke-virtual {v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getTextSize()F

    #@13
    move-result v3

    #@14
    float-to-double v4, v3

    #@15
    const-wide/high16 v6, 0x3ff4000000000000L    # 1.25

    #@17
    mul-double/2addr v4, v6

    #@18
    double-to-int v2, v4

    #@19
    .line 1022
    .local v2, "textSize":I
    invoke-virtual {v0, v8, v8, v2, v2}, Landroid/graphics/drawable/Drawable;->setBounds(IIII)V

    #@1c
    .line 1024
    new-instance v1, Landroid/text/SpannableStringBuilder;

    #@1e
    const-string v3, "   "

    #@20
    invoke-direct {v1, v3}, Landroid/text/SpannableStringBuilder;-><init>(Ljava/lang/CharSequence;)V

    #@23
    .line 1025
    .local v1, "ssb":Landroid/text/SpannableStringBuilder;
    invoke-virtual {v1, p1}, Landroid/text/SpannableStringBuilder;->append(Ljava/lang/CharSequence;)Landroid/text/SpannableStringBuilder;

    #@26
    .line 1026
    new-instance v3, Landroid/text/style/ImageSpan;

    #@28
    invoke-direct {v3, v0}, Landroid/text/style/ImageSpan;-><init>(Landroid/graphics/drawable/Drawable;)V

    #@2b
    const/4 v4, 0x1

    #@2c
    const/4 v5, 0x2

    #@2d
    const/16 v6, 0x21

    #@2f
    invoke-virtual {v1, v3, v4, v5, v6}, Landroid/text/SpannableStringBuilder;->setSpan(Ljava/lang/Object;III)V

    #@32
    move-object p1, v1

    #@33
    .line 1027
    goto :goto_0
.end method

.method private getPreferredWidth()I
    .locals 2

    #@0
    .prologue
    .line 793
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@7
    move-result-object v0

    #@8
    sget v1, Landroid/support/v7/appcompat/R$dimen;->abc_search_view_preferred_width:I

    #@a
    invoke-virtual {v0, v1}, Landroid/content/res/Resources;->getDimensionPixelSize(I)I

    #@d
    move-result v0

    #@e
    return v0
.end method

.method private hasVoiceSearch()Z
    .locals 5
    .annotation build Landroid/annotation/TargetApi;
        value = 0x8
    .end annotation

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 815
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@3
    if-eqz v3, :cond_1

    #@5
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@7
    invoke-virtual {v3}, Landroid/app/SearchableInfo;->getVoiceSearchEnabled()Z

    #@a
    move-result v3

    #@b
    if-eqz v3, :cond_1

    #@d
    .line 817
    const/4 v1, 0x0

    #@e
    .line 818
    .local v1, "testIntent":Landroid/content/Intent;
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@10
    invoke-virtual {v3}, Landroid/app/SearchableInfo;->getVoiceSearchLaunchWebSearch()Z

    #@13
    move-result v3

    #@14
    if-eqz v3, :cond_2

    #@16
    .line 819
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mVoiceWebSearchIntent:Landroid/content/Intent;

    #@18
    .line 823
    :cond_0
    :goto_0
    if-eqz v1, :cond_1

    #@1a
    .line 824
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@1d
    move-result-object v3

    #@1e
    invoke-virtual {v3}, Landroid/content/Context;->getPackageManager()Landroid/content/pm/PackageManager;

    #@21
    move-result-object v3

    #@22
    const/high16 v4, 0x10000

    #@24
    invoke-virtual {v3, v1, v4}, Landroid/content/pm/PackageManager;->resolveActivity(Landroid/content/Intent;I)Landroid/content/pm/ResolveInfo;

    #@27
    move-result-object v0

    #@28
    .line 826
    .local v0, "ri":Landroid/content/pm/ResolveInfo;
    if-eqz v0, :cond_1

    #@2a
    const/4 v2, 0x1

    #@2b
    .line 829
    .end local v0    # "ri":Landroid/content/pm/ResolveInfo;
    .end local v1    # "testIntent":Landroid/content/Intent;
    :cond_1
    return v2

    #@2c
    .line 820
    .restart local v1    # "testIntent":Landroid/content/Intent;
    :cond_2
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@2e
    invoke-virtual {v3}, Landroid/app/SearchableInfo;->getVoiceSearchLaunchRecognizer()Z

    #@31
    move-result v3

    #@32
    if-eqz v3, :cond_0

    #@34
    .line 821
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mVoiceAppSearchIntent:Landroid/content/Intent;

    #@36
    goto :goto_0
.end method

.method static isLandscapeMode(Landroid/content/Context;)Z
    .locals 2
    .param p0, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 1584
    invoke-virtual {p0}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@3
    move-result-object v0

    #@4
    invoke-virtual {v0}, Landroid/content/res/Resources;->getConfiguration()Landroid/content/res/Configuration;

    #@7
    move-result-object v0

    #@8
    iget v0, v0, Landroid/content/res/Configuration;->orientation:I

    #@a
    const/4 v1, 0x2

    #@b
    if-ne v0, v1, :cond_0

    #@d
    const/4 v0, 0x1

    #@e
    :goto_0
    return v0

    #@f
    :cond_0
    const/4 v0, 0x0

    #@10
    goto :goto_0
.end method

.method private isSubmitAreaEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 833
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mSubmitButtonEnabled:Z

    #@2
    if-nez v0, :cond_0

    #@4
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mVoiceButtonEnabled:Z

    #@6
    if-eqz v0, :cond_1

    #@8
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->isIconified()Z

    #@b
    move-result v0

    #@c
    if-nez v0, :cond_1

    #@e
    const/4 v0, 0x1

    #@f
    :goto_0
    return v0

    #@10
    :cond_1
    const/4 v0, 0x0

    #@11
    goto :goto_0
.end method

.method private launchIntent(Landroid/content/Intent;)V
    .locals 4
    .param p1, "intent"    # Landroid/content/Intent;

    #@0
    .prologue
    .line 1371
    if-nez p1, :cond_0

    #@2
    .line 1381
    :goto_0
    return-void

    #@3
    .line 1377
    :cond_0
    :try_start_0
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@6
    move-result-object v1

    #@7
    invoke-virtual {v1, p1}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V
    :try_end_0
    .catch Ljava/lang/RuntimeException; {:try_start_0 .. :try_end_0} :catch_0

    #@a
    goto :goto_0

    #@b
    .line 1378
    :catch_0
    move-exception v0

    #@c
    .line 1379
    .local v0, "ex":Ljava/lang/RuntimeException;
    const-string v1, "SearchView"

    #@e
    new-instance v2, Ljava/lang/StringBuilder;

    #@10
    invoke-direct {v2}, Ljava/lang/StringBuilder;-><init>()V

    #@13
    const-string v3, "Failed launch activity: "

    #@15
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@18
    move-result-object v2

    #@19
    invoke-virtual {v2, p1}, Ljava/lang/StringBuilder;->append(Ljava/lang/Object;)Ljava/lang/StringBuilder;

    #@1c
    move-result-object v2

    #@1d
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@20
    move-result-object v2

    #@21
    invoke-static {v1, v2, v0}, Landroid/util/Log;->e(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Throwable;)I

    #@24
    goto :goto_0
.end method

.method private launchQuerySearch(ILjava/lang/String;Ljava/lang/String;)V
    .locals 8
    .param p1, "actionKey"    # I
    .param p2, "actionMsg"    # Ljava/lang/String;
    .param p3, "query"    # Ljava/lang/String;

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 1393
    const-string v1, "android.intent.action.SEARCH"

    #@3
    .local v1, "action":Ljava/lang/String;
    move-object v0, p0

    #@4
    move-object v3, v2

    #@5
    move-object v4, p3

    #@6
    move v5, p1

    #@7
    move-object v6, p2

    #@8
    .line 1394
    invoke-direct/range {v0 .. v6}, Landroid/support/v7/widget/SearchView;->createIntent(Ljava/lang/String;Landroid/net/Uri;Ljava/lang/String;Ljava/lang/String;ILjava/lang/String;)Landroid/content/Intent;

    #@b
    move-result-object v7

    #@c
    .line 1395
    .local v7, "intent":Landroid/content/Intent;
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@f
    move-result-object v0

    #@10
    invoke-virtual {v0, v7}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V

    #@13
    .line 1396
    return-void
.end method

.method private launchSuggestion(IILjava/lang/String;)Z
    .locals 3
    .param p1, "position"    # I
    .param p2, "actionKey"    # I
    .param p3, "actionMsg"    # Ljava/lang/String;

    #@0
    .prologue
    .line 1354
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@2
    invoke-virtual {v2}, Landroid/support/v4/widget/CursorAdapter;->getCursor()Landroid/database/Cursor;

    #@5
    move-result-object v0

    #@6
    .line 1355
    .local v0, "c":Landroid/database/Cursor;
    if-eqz v0, :cond_0

    #@8
    invoke-interface {v0, p1}, Landroid/database/Cursor;->moveToPosition(I)Z

    #@b
    move-result v2

    #@c
    if-eqz v2, :cond_0

    #@e
    .line 1357
    invoke-direct {p0, v0, p2, p3}, Landroid/support/v7/widget/SearchView;->createIntentFromSuggestion(Landroid/database/Cursor;ILjava/lang/String;)Landroid/content/Intent;

    #@11
    move-result-object v1

    #@12
    .line 1360
    .local v1, "intent":Landroid/content/Intent;
    invoke-direct {p0, v1}, Landroid/support/v7/widget/SearchView;->launchIntent(Landroid/content/Intent;)V

    #@15
    .line 1362
    const/4 v2, 0x1

    #@16
    .line 1364
    .end local v1    # "intent":Landroid/content/Intent;
    :goto_0
    return v2

    #@17
    :cond_0
    const/4 v2, 0x0

    #@18
    goto :goto_0
.end method

.method private onCloseClicked()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x1

    #@1
    .line 1147
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@3
    invoke-virtual {v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@6
    move-result-object v0

    #@7
    .line 1148
    .local v0, "text":Ljava/lang/CharSequence;
    invoke-static {v0}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@a
    move-result v1

    #@b
    if-eqz v1, :cond_2

    #@d
    .line 1149
    iget-boolean v1, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@f
    if-eqz v1, :cond_1

    #@11
    .line 1151
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mOnCloseListener:Landroid/support/v7/widget/SearchView$OnCloseListener;

    #@13
    if-eqz v1, :cond_0

    #@15
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mOnCloseListener:Landroid/support/v7/widget/SearchView$OnCloseListener;

    #@17
    invoke-interface {v1}, Landroid/support/v7/widget/SearchView$OnCloseListener;->onClose()Z

    #@1a
    move-result v1

    #@1b
    if-nez v1, :cond_1

    #@1d
    .line 1153
    :cond_0
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->clearFocus()V

    #@20
    .line 1155
    invoke-direct {p0, v3}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@23
    .line 1164
    :cond_1
    :goto_0
    return-void

    #@24
    .line 1159
    :cond_2
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@26
    const-string v2, ""

    #@28
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setText(Ljava/lang/CharSequence;)V

    #@2b
    .line 1160
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2d
    invoke-virtual {v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->requestFocus()Z

    #@30
    .line 1161
    invoke-direct {p0, v3}, Landroid/support/v7/widget/SearchView;->setImeVisibility(Z)V

    #@33
    goto :goto_0
.end method

.method private onItemClicked(IILjava/lang/String;)Z
    .locals 2
    .param p1, "position"    # I
    .param p2, "actionKey"    # I
    .param p3, "actionMsg"    # Ljava/lang/String;

    #@0
    .prologue
    const/4 v0, 0x0

    #@1
    .line 1268
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mOnSuggestionListener:Landroid/support/v7/widget/SearchView$OnSuggestionListener;

    #@3
    if-eqz v1, :cond_0

    #@5
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mOnSuggestionListener:Landroid/support/v7/widget/SearchView$OnSuggestionListener;

    #@7
    invoke-interface {v1, p1}, Landroid/support/v7/widget/SearchView$OnSuggestionListener;->onSuggestionClick(I)Z

    #@a
    move-result v1

    #@b
    if-nez v1, :cond_1

    #@d
    .line 1270
    :cond_0
    const/4 v1, 0x0

    #@e
    invoke-direct {p0, p1, v0, v1}, Landroid/support/v7/widget/SearchView;->launchSuggestion(IILjava/lang/String;)Z

    #@11
    .line 1271
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->setImeVisibility(Z)V

    #@14
    .line 1272
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->dismissSuggestions()V

    #@17
    .line 1273
    const/4 v0, 0x1

    #@18
    .line 1275
    :cond_1
    return v0
.end method

.method private onItemSelected(I)Z
    .locals 1
    .param p1, "position"    # I

    #@0
    .prologue
    .line 1279
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mOnSuggestionListener:Landroid/support/v7/widget/SearchView$OnSuggestionListener;

    #@2
    if-eqz v0, :cond_0

    #@4
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mOnSuggestionListener:Landroid/support/v7/widget/SearchView$OnSuggestionListener;

    #@6
    invoke-interface {v0, p1}, Landroid/support/v7/widget/SearchView$OnSuggestionListener;->onSuggestionSelect(I)Z

    #@9
    move-result v0

    #@a
    if-nez v0, :cond_1

    #@c
    .line 1281
    :cond_0
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SearchView;->rewriteQueryFromSuggestion(I)V

    #@f
    .line 1282
    const/4 v0, 0x1

    #@10
    .line 1284
    :goto_0
    return v0

    #@11
    :cond_1
    const/4 v0, 0x0

    #@12
    goto :goto_0
.end method

.method private onSearchClicked()V
    .locals 1

    #@0
    .prologue
    .line 1167
    const/4 v0, 0x0

    #@1
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@4
    .line 1168
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@6
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->requestFocus()Z

    #@9
    .line 1169
    const/4 v0, 0x1

    #@a
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->setImeVisibility(Z)V

    #@d
    .line 1170
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mOnSearchClickListener:Landroid/view/View$OnClickListener;

    #@f
    if-eqz v0, :cond_0

    #@11
    .line 1171
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mOnSearchClickListener:Landroid/view/View$OnClickListener;

    #@13
    invoke-interface {v0, p0}, Landroid/view/View$OnClickListener;->onClick(Landroid/view/View;)V

    #@16
    .line 1173
    :cond_0
    return-void
.end method

.method private onSubmitQuery()V
    .locals 4

    #@0
    .prologue
    const/4 v3, 0x0

    #@1
    .line 1129
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@3
    invoke-virtual {v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@6
    move-result-object v0

    #@7
    .line 1130
    .local v0, "query":Ljava/lang/CharSequence;
    if-eqz v0, :cond_2

    #@9
    invoke-static {v0}, Landroid/text/TextUtils;->getTrimmedLength(Ljava/lang/CharSequence;)I

    #@c
    move-result v1

    #@d
    if-lez v1, :cond_2

    #@f
    .line 1131
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mOnQueryChangeListener:Landroid/support/v7/widget/SearchView$OnQueryTextListener;

    #@11
    if-eqz v1, :cond_0

    #@13
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mOnQueryChangeListener:Landroid/support/v7/widget/SearchView$OnQueryTextListener;

    #@15
    invoke-interface {v0}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    #@18
    move-result-object v2

    #@19
    invoke-interface {v1, v2}, Landroid/support/v7/widget/SearchView$OnQueryTextListener;->onQueryTextSubmit(Ljava/lang/String;)Z

    #@1c
    move-result v1

    #@1d
    if-nez v1, :cond_2

    #@1f
    .line 1133
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@21
    if-eqz v1, :cond_1

    #@23
    .line 1134
    const/4 v1, 0x0

    #@24
    invoke-interface {v0}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    #@27
    move-result-object v2

    #@28
    invoke-direct {p0, v3, v1, v2}, Landroid/support/v7/widget/SearchView;->launchQuerySearch(ILjava/lang/String;Ljava/lang/String;)V

    #@2b
    .line 1136
    :cond_1
    invoke-direct {p0, v3}, Landroid/support/v7/widget/SearchView;->setImeVisibility(Z)V

    #@2e
    .line 1137
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->dismissSuggestions()V

    #@31
    .line 1140
    :cond_2
    return-void
.end method

.method private onSuggestionsKey(Landroid/view/View;ILandroid/view/KeyEvent;)Z
    .locals 6
    .param p1, "v"    # Landroid/view/View;
    .param p2, "keyCode"    # I
    .param p3, "event"    # Landroid/view/KeyEvent;

    #@0
    .prologue
    const/16 v5, 0x15

    #@2
    const/4 v3, 0x1

    #@3
    const/4 v2, 0x0

    #@4
    .line 972
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@6
    if-nez v4, :cond_1

    #@8
    .line 1011
    :cond_0
    :goto_0
    return v2

    #@9
    .line 975
    :cond_1
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@b
    if-eqz v4, :cond_0

    #@d
    .line 978
    invoke-virtual {p3}, Landroid/view/KeyEvent;->getAction()I

    #@10
    move-result v4

    #@11
    if-nez v4, :cond_0

    #@13
    invoke-static {p3}, Landroid/support/v4/view/KeyEventCompat;->hasNoModifiers(Landroid/view/KeyEvent;)Z

    #@16
    move-result v4

    #@17
    if-eqz v4, :cond_0

    #@19
    .line 981
    const/16 v4, 0x42

    #@1b
    if-eq p2, v4, :cond_2

    #@1d
    const/16 v4, 0x54

    #@1f
    if-eq p2, v4, :cond_2

    #@21
    const/16 v4, 0x3d

    #@23
    if-ne p2, v4, :cond_3

    #@25
    .line 983
    :cond_2
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@27
    invoke-virtual {v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getListSelection()I

    #@2a
    move-result v0

    #@2b
    .line 984
    .local v0, "position":I
    const/4 v3, 0x0

    #@2c
    invoke-direct {p0, v0, v2, v3}, Landroid/support/v7/widget/SearchView;->onItemClicked(IILjava/lang/String;)Z

    #@2f
    move-result v2

    #@30
    goto :goto_0

    #@31
    .line 989
    .end local v0    # "position":I
    :cond_3
    if-eq p2, v5, :cond_4

    #@33
    const/16 v4, 0x16

    #@35
    if-ne p2, v4, :cond_6

    #@37
    .line 994
    :cond_4
    if-ne p2, v5, :cond_5

    #@39
    move v1, v2

    #@3a
    .line 996
    .local v1, "selPoint":I
    :goto_1
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@3c
    invoke-virtual {v4, v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setSelection(I)V

    #@3f
    .line 997
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@41
    invoke-virtual {v4, v2}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setListSelection(I)V

    #@44
    .line 998
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@46
    invoke-virtual {v2}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->clearListSelection()V

    #@49
    .line 999
    sget-object v2, Landroid/support/v7/widget/SearchView;->HIDDEN_METHOD_INVOKER:Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;

    #@4b
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@4d
    invoke-virtual {v2, v4, v3}, Landroid/support/v7/widget/SearchView$AutoCompleteTextViewReflector;->ensureImeVisible(Landroid/widget/AutoCompleteTextView;Z)V

    #@50
    move v2, v3

    #@51
    .line 1001
    goto :goto_0

    #@52
    .line 994
    .end local v1    # "selPoint":I
    :cond_5
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@54
    invoke-virtual {v4}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->length()I

    #@57
    move-result v1

    #@58
    goto :goto_1

    #@59
    .line 1005
    :cond_6
    const/16 v3, 0x13

    #@5b
    if-ne p2, v3, :cond_0

    #@5d
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@5f
    invoke-virtual {v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getListSelection()I

    #@62
    move-result v3

    #@63
    if-nez v3, :cond_0

    #@65
    goto :goto_0
.end method

.method private onTextChanged(Ljava/lang/CharSequence;)V
    .locals 5
    .param p1, "newText"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    const/4 v3, 0x0

    #@2
    .line 1115
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@4
    invoke-virtual {v4}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@7
    move-result-object v1

    #@8
    .line 1116
    .local v1, "text":Ljava/lang/CharSequence;
    iput-object v1, p0, Landroid/support/v7/widget/SearchView;->mUserQuery:Ljava/lang/CharSequence;

    #@a
    .line 1117
    invoke-static {v1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@d
    move-result v4

    #@e
    if-nez v4, :cond_1

    #@10
    move v0, v2

    #@11
    .line 1118
    .local v0, "hasText":Z
    :goto_0
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->updateSubmitButton(Z)V

    #@14
    .line 1119
    if-nez v0, :cond_2

    #@16
    :goto_1
    invoke-direct {p0, v2}, Landroid/support/v7/widget/SearchView;->updateVoiceButton(Z)V

    #@19
    .line 1120
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateCloseButton()V

    #@1c
    .line 1121
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateSubmitArea()V

    #@1f
    .line 1122
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mOnQueryChangeListener:Landroid/support/v7/widget/SearchView$OnQueryTextListener;

    #@21
    if-eqz v2, :cond_0

    #@23
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mOldQueryText:Ljava/lang/CharSequence;

    #@25
    invoke-static {p1, v2}, Landroid/text/TextUtils;->equals(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Z

    #@28
    move-result v2

    #@29
    if-nez v2, :cond_0

    #@2b
    .line 1123
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mOnQueryChangeListener:Landroid/support/v7/widget/SearchView$OnQueryTextListener;

    #@2d
    invoke-interface {p1}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    #@30
    move-result-object v3

    #@31
    invoke-interface {v2, v3}, Landroid/support/v7/widget/SearchView$OnQueryTextListener;->onQueryTextChange(Ljava/lang/String;)Z

    #@34
    .line 1125
    :cond_0
    invoke-interface {p1}, Ljava/lang/CharSequence;->toString()Ljava/lang/String;

    #@37
    move-result-object v2

    #@38
    iput-object v2, p0, Landroid/support/v7/widget/SearchView;->mOldQueryText:Ljava/lang/CharSequence;

    #@3a
    .line 1126
    return-void

    #@3b
    .end local v0    # "hasText":Z
    :cond_1
    move v0, v3

    #@3c
    .line 1117
    goto :goto_0

    #@3d
    .restart local v0    # "hasText":Z
    :cond_2
    move v2, v3

    #@3e
    .line 1119
    goto :goto_1
.end method

.method private onVoiceClicked()V
    .locals 6
    .annotation build Landroid/annotation/TargetApi;
        value = 0x8
    .end annotation

    #@0
    .prologue
    .line 1178
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@2
    if-nez v4, :cond_1

    #@4
    .line 1197
    :cond_0
    :goto_0
    return-void

    #@5
    .line 1181
    :cond_1
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@7
    .line 1183
    .local v2, "searchable":Landroid/app/SearchableInfo;
    :try_start_0
    invoke-virtual {v2}, Landroid/app/SearchableInfo;->getVoiceSearchLaunchWebSearch()Z

    #@a
    move-result v4

    #@b
    if-eqz v4, :cond_2

    #@d
    .line 1184
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mVoiceWebSearchIntent:Landroid/content/Intent;

    #@f
    invoke-direct {p0, v4, v2}, Landroid/support/v7/widget/SearchView;->createVoiceWebSearchIntent(Landroid/content/Intent;Landroid/app/SearchableInfo;)Landroid/content/Intent;

    #@12
    move-result-object v3

    #@13
    .line 1186
    .local v3, "webSearchIntent":Landroid/content/Intent;
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@16
    move-result-object v4

    #@17
    invoke-virtual {v4, v3}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V
    :try_end_0
    .catch Landroid/content/ActivityNotFoundException; {:try_start_0 .. :try_end_0} :catch_0

    #@1a
    goto :goto_0

    #@1b
    .line 1192
    .end local v3    # "webSearchIntent":Landroid/content/Intent;
    :catch_0
    move-exception v1

    #@1c
    .line 1195
    .local v1, "e":Landroid/content/ActivityNotFoundException;
    const-string v4, "SearchView"

    #@1e
    const-string v5, "Could not find voice search activity"

    #@20
    invoke-static {v4, v5}, Landroid/util/Log;->w(Ljava/lang/String;Ljava/lang/String;)I

    #@23
    goto :goto_0

    #@24
    .line 1187
    .end local v1    # "e":Landroid/content/ActivityNotFoundException;
    :cond_2
    :try_start_1
    invoke-virtual {v2}, Landroid/app/SearchableInfo;->getVoiceSearchLaunchRecognizer()Z

    #@27
    move-result v4

    #@28
    if-eqz v4, :cond_0

    #@2a
    .line 1188
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mVoiceAppSearchIntent:Landroid/content/Intent;

    #@2c
    invoke-direct {p0, v4, v2}, Landroid/support/v7/widget/SearchView;->createVoiceAppSearchIntent(Landroid/content/Intent;Landroid/app/SearchableInfo;)Landroid/content/Intent;

    #@2f
    move-result-object v0

    #@30
    .line 1190
    .local v0, "appSearchIntent":Landroid/content/Intent;
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@33
    move-result-object v4

    #@34
    invoke-virtual {v4, v0}, Landroid/content/Context;->startActivity(Landroid/content/Intent;)V
    :try_end_1
    .catch Landroid/content/ActivityNotFoundException; {:try_start_1 .. :try_end_1} :catch_0

    #@37
    goto :goto_0
.end method

.method private postUpdateFocusedState()V
    .locals 1

    #@0
    .prologue
    .line 865
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mUpdateDrawableStateRunnable:Ljava/lang/Runnable;

    #@2
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SearchView;->post(Ljava/lang/Runnable;)Z

    #@5
    .line 866
    return-void
.end method

.method private rewriteQueryFromSuggestion(I)V
    .locals 4
    .param p1, "position"    # I

    #@0
    .prologue
    .line 1321
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@5
    move-result-object v2

    #@6
    .line 1322
    .local v2, "oldQuery":Ljava/lang/CharSequence;
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@8
    invoke-virtual {v3}, Landroid/support/v4/widget/CursorAdapter;->getCursor()Landroid/database/Cursor;

    #@b
    move-result-object v0

    #@c
    .line 1323
    .local v0, "c":Landroid/database/Cursor;
    if-nez v0, :cond_0

    #@e
    .line 1341
    :goto_0
    return-void

    #@f
    .line 1326
    :cond_0
    invoke-interface {v0, p1}, Landroid/database/Cursor;->moveToPosition(I)Z

    #@12
    move-result v3

    #@13
    if-eqz v3, :cond_2

    #@15
    .line 1328
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@17
    invoke-virtual {v3, v0}, Landroid/support/v4/widget/CursorAdapter;->convertToString(Landroid/database/Cursor;)Ljava/lang/CharSequence;

    #@1a
    move-result-object v1

    #@1b
    .line 1329
    .local v1, "newQuery":Ljava/lang/CharSequence;
    if-eqz v1, :cond_1

    #@1d
    .line 1332
    invoke-direct {p0, v1}, Landroid/support/v7/widget/SearchView;->setQuery(Ljava/lang/CharSequence;)V

    #@20
    goto :goto_0

    #@21
    .line 1335
    :cond_1
    invoke-direct {p0, v2}, Landroid/support/v7/widget/SearchView;->setQuery(Ljava/lang/CharSequence;)V

    #@24
    goto :goto_0

    #@25
    .line 1339
    .end local v1    # "newQuery":Ljava/lang/CharSequence;
    :cond_2
    invoke-direct {p0, v2}, Landroid/support/v7/widget/SearchView;->setQuery(Ljava/lang/CharSequence;)V

    #@28
    goto :goto_0
.end method

.method private setImeVisibility(Z)V
    .locals 3
    .param p1, "visible"    # Z

    #@0
    .prologue
    .line 883
    if-eqz p1, :cond_1

    #@2
    .line 884
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mShowImeRunnable:Ljava/lang/Runnable;

    #@4
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/SearchView;->post(Ljava/lang/Runnable;)Z

    #@7
    .line 894
    :cond_0
    :goto_0
    return-void

    #@8
    .line 886
    :cond_1
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mShowImeRunnable:Ljava/lang/Runnable;

    #@a
    invoke-virtual {p0, v1}, Landroid/support/v7/widget/SearchView;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@d
    .line 887
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@10
    move-result-object v1

    #@11
    const-string v2, "input_method"

    #@13
    invoke-virtual {v1, v2}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;

    #@16
    move-result-object v0

    #@17
    check-cast v0, Landroid/view/inputmethod/InputMethodManager;

    #@19
    .line 890
    .local v0, "imm":Landroid/view/inputmethod/InputMethodManager;
    if-eqz v0, :cond_0

    #@1b
    .line 891
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getWindowToken()Landroid/os/IBinder;

    #@1e
    move-result-object v1

    #@1f
    const/4 v2, 0x0

    #@20
    invoke-virtual {v0, v1, v2}, Landroid/view/inputmethod/InputMethodManager;->hideSoftInputFromWindow(Landroid/os/IBinder;I)Z

    #@23
    goto :goto_0
.end method

.method private setQuery(Ljava/lang/CharSequence;)V
    .locals 2
    .param p1, "query"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 1387
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setText(Ljava/lang/CharSequence;)V

    #@5
    .line 1389
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@7
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@a
    move-result v0

    #@b
    if-eqz v0, :cond_0

    #@d
    const/4 v0, 0x0

    #@e
    :goto_0
    invoke-virtual {v1, v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setSelection(I)V

    #@11
    .line 1390
    return-void

    #@12
    .line 1389
    :cond_0
    invoke-interface {p1}, Ljava/lang/CharSequence;->length()I

    #@15
    move-result v0

    #@16
    goto :goto_0
.end method

.method private updateCloseButton()V
    .locals 4

    #@0
    .prologue
    const/4 v1, 0x1

    #@1
    const/4 v2, 0x0

    #@2
    .line 856
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@4
    invoke-virtual {v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@7
    move-result-object v3

    #@8
    invoke-static {v3}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@b
    move-result v3

    #@c
    if-nez v3, :cond_1

    #@e
    move v0, v1

    #@f
    .line 859
    .local v0, "hasText":Z
    :goto_0
    if-nez v0, :cond_0

    #@11
    iget-boolean v3, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@13
    if-eqz v3, :cond_2

    #@15
    iget-boolean v3, p0, Landroid/support/v7/widget/SearchView;->mExpandedInActionView:Z

    #@17
    if-nez v3, :cond_2

    #@19
    .line 860
    .local v1, "showClose":Z
    :cond_0
    :goto_1
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mCloseButton:Landroid/widget/ImageView;

    #@1b
    if-eqz v1, :cond_3

    #@1d
    :goto_2
    invoke-virtual {v3, v2}, Landroid/widget/ImageView;->setVisibility(I)V

    #@20
    .line 861
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mCloseButton:Landroid/widget/ImageView;

    #@22
    invoke-virtual {v2}, Landroid/widget/ImageView;->getDrawable()Landroid/graphics/drawable/Drawable;

    #@25
    move-result-object v3

    #@26
    if-eqz v0, :cond_4

    #@28
    sget-object v2, Landroid/support/v7/widget/SearchView;->ENABLED_STATE_SET:[I

    #@2a
    :goto_3
    invoke-virtual {v3, v2}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@2d
    .line 862
    return-void

    #@2e
    .end local v0    # "hasText":Z
    .end local v1    # "showClose":Z
    :cond_1
    move v0, v2

    #@2f
    .line 856
    goto :goto_0

    #@30
    .restart local v0    # "hasText":Z
    :cond_2
    move v1, v2

    #@31
    .line 859
    goto :goto_1

    #@32
    .line 860
    .restart local v1    # "showClose":Z
    :cond_3
    const/16 v2, 0x8

    #@34
    goto :goto_2

    #@35
    .line 861
    :cond_4
    sget-object v2, Landroid/support/v7/widget/SearchView;->EMPTY_STATE_SET:[I

    #@37
    goto :goto_3
.end method

.method private updateFocusedState()V
    .locals 3

    #@0
    .prologue
    .line 869
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->hasFocus()Z

    #@5
    move-result v0

    #@6
    .line 870
    .local v0, "focused":Z
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSearchPlate:Landroid/view/View;

    #@8
    invoke-virtual {v1}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    #@b
    move-result-object v2

    #@c
    if-eqz v0, :cond_0

    #@e
    sget-object v1, Landroid/support/v7/widget/SearchView;->ENABLED_FOCUSED_STATE_SET:[I

    #@10
    :goto_0
    invoke-virtual {v2, v1}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@13
    .line 871
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSubmitArea:Landroid/view/View;

    #@15
    invoke-virtual {v1}, Landroid/view/View;->getBackground()Landroid/graphics/drawable/Drawable;

    #@18
    move-result-object v2

    #@19
    if-eqz v0, :cond_1

    #@1b
    sget-object v1, Landroid/support/v7/widget/SearchView;->ENABLED_FOCUSED_STATE_SET:[I

    #@1d
    :goto_1
    invoke-virtual {v2, v1}, Landroid/graphics/drawable/Drawable;->setState([I)Z

    #@20
    .line 872
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->invalidate()V

    #@23
    .line 873
    return-void

    #@24
    .line 870
    :cond_0
    sget-object v1, Landroid/support/v7/widget/SearchView;->EMPTY_STATE_SET:[I

    #@26
    goto :goto_0

    #@27
    .line 871
    :cond_1
    sget-object v1, Landroid/support/v7/widget/SearchView;->EMPTY_STATE_SET:[I

    #@29
    goto :goto_1
.end method

.method private updateQueryHint()V
    .locals 4

    #@0
    .prologue
    .line 1031
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mQueryHint:Ljava/lang/CharSequence;

    #@2
    if-eqz v2, :cond_1

    #@4
    .line 1032
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@6
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mQueryHint:Ljava/lang/CharSequence;

    #@8
    invoke-direct {p0, v3}, Landroid/support/v7/widget/SearchView;->getDecoratedHint(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;

    #@b
    move-result-object v3

    #@c
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setHint(Ljava/lang/CharSequence;)V

    #@f
    .line 1045
    :cond_0
    :goto_0
    return-void

    #@10
    .line 1033
    :cond_1
    sget-boolean v2, Landroid/support/v7/widget/SearchView;->IS_AT_LEAST_FROYO:Z

    #@12
    if-eqz v2, :cond_3

    #@14
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@16
    if-eqz v2, :cond_3

    #@18
    .line 1034
    const/4 v0, 0x0

    #@19
    .line 1035
    .local v0, "hint":Ljava/lang/CharSequence;
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@1b
    invoke-virtual {v2}, Landroid/app/SearchableInfo;->getHintId()I

    #@1e
    move-result v1

    #@1f
    .line 1036
    .local v1, "hintId":I
    if-eqz v1, :cond_2

    #@21
    .line 1037
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@24
    move-result-object v2

    #@25
    invoke-virtual {v2, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@28
    move-result-object v0

    #@29
    .line 1039
    :cond_2
    if-eqz v0, :cond_0

    #@2b
    .line 1040
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2d
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->getDecoratedHint(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;

    #@30
    move-result-object v3

    #@31
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setHint(Ljava/lang/CharSequence;)V

    #@34
    goto :goto_0

    #@35
    .line 1043
    .end local v0    # "hint":Ljava/lang/CharSequence;
    .end local v1    # "hintId":I
    :cond_3
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@37
    const-string v3, ""

    #@39
    invoke-direct {p0, v3}, Landroid/support/v7/widget/SearchView;->getDecoratedHint(Ljava/lang/CharSequence;)Ljava/lang/CharSequence;

    #@3c
    move-result-object v3

    #@3d
    invoke-virtual {v2, v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setHint(Ljava/lang/CharSequence;)V

    #@40
    goto :goto_0
.end method

.method private updateSearchAutoComplete()V
    .locals 6
    .annotation build Landroid/annotation/TargetApi;
        value = 0x8
    .end annotation

    #@0
    .prologue
    const/4 v2, 0x1

    #@1
    .line 1052
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@3
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@5
    invoke-virtual {v3}, Landroid/app/SearchableInfo;->getSuggestThreshold()I

    #@8
    move-result v3

    #@9
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setThreshold(I)V

    #@c
    .line 1053
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@e
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@10
    invoke-virtual {v3}, Landroid/app/SearchableInfo;->getImeOptions()I

    #@13
    move-result v3

    #@14
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setImeOptions(I)V

    #@17
    .line 1054
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@19
    invoke-virtual {v1}, Landroid/app/SearchableInfo;->getInputType()I

    #@1c
    move-result v0

    #@1d
    .line 1057
    .local v0, "inputType":I
    and-int/lit8 v1, v0, 0xf

    #@1f
    if-ne v1, v2, :cond_0

    #@21
    .line 1060
    const v1, -0x10001

    #@24
    and-int/2addr v0, v1

    #@25
    .line 1061
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@27
    invoke-virtual {v1}, Landroid/app/SearchableInfo;->getSuggestAuthority()Ljava/lang/String;

    #@2a
    move-result-object v1

    #@2b
    if-eqz v1, :cond_0

    #@2d
    .line 1062
    const/high16 v1, 0x10000

    #@2f
    or-int/2addr v0, v1

    #@30
    .line 1069
    const/high16 v1, 0x80000

    #@32
    or-int/2addr v0, v1

    #@33
    .line 1072
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@35
    invoke-virtual {v1, v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setInputType(I)V

    #@38
    .line 1073
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@3a
    if-eqz v1, :cond_1

    #@3c
    .line 1074
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@3e
    const/4 v3, 0x0

    #@3f
    invoke-virtual {v1, v3}, Landroid/support/v4/widget/CursorAdapter;->changeCursor(Landroid/database/Cursor;)V

    #@42
    .line 1078
    :cond_1
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@44
    invoke-virtual {v1}, Landroid/app/SearchableInfo;->getSuggestAuthority()Ljava/lang/String;

    #@47
    move-result-object v1

    #@48
    if-eqz v1, :cond_3

    #@4a
    .line 1079
    new-instance v1, Landroid/support/v7/widget/SuggestionsAdapter;

    #@4c
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@4f
    move-result-object v3

    #@50
    iget-object v4, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@52
    iget-object v5, p0, Landroid/support/v7/widget/SearchView;->mOutsideDrawablesCache:Ljava/util/WeakHashMap;

    #@54
    invoke-direct {v1, v3, p0, v4, v5}, Landroid/support/v7/widget/SuggestionsAdapter;-><init>(Landroid/content/Context;Landroid/support/v7/widget/SearchView;Landroid/app/SearchableInfo;Ljava/util/WeakHashMap;)V

    #@57
    iput-object v1, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@59
    .line 1081
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@5b
    iget-object v3, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@5d
    invoke-virtual {v1, v3}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setAdapter(Landroid/widget/ListAdapter;)V

    #@60
    .line 1082
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@62
    check-cast v1, Landroid/support/v7/widget/SuggestionsAdapter;

    #@64
    iget-boolean v3, p0, Landroid/support/v7/widget/SearchView;->mQueryRefinement:Z

    #@66
    if-eqz v3, :cond_2

    #@68
    const/4 v2, 0x2

    #@69
    :cond_2
    invoke-virtual {v1, v2}, Landroid/support/v7/widget/SuggestionsAdapter;->setQueryRefinement(I)V

    #@6c
    .line 1086
    :cond_3
    return-void
.end method

.method private updateSubmitArea()V
    .locals 2

    #@0
    .prologue
    .line 846
    const/16 v0, 0x8

    #@2
    .line 847
    .local v0, "visibility":I
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->isSubmitAreaEnabled()Z

    #@5
    move-result v1

    #@6
    if-eqz v1, :cond_1

    #@8
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSubmitButton:Landroid/widget/ImageView;

    #@a
    invoke-virtual {v1}, Landroid/widget/ImageView;->getVisibility()I

    #@d
    move-result v1

    #@e
    if-eqz v1, :cond_0

    #@10
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mVoiceButton:Landroid/widget/ImageView;

    #@12
    invoke-virtual {v1}, Landroid/widget/ImageView;->getVisibility()I

    #@15
    move-result v1

    #@16
    if-nez v1, :cond_1

    #@18
    .line 850
    :cond_0
    const/4 v0, 0x0

    #@19
    .line 852
    :cond_1
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSubmitArea:Landroid/view/View;

    #@1b
    invoke-virtual {v1, v0}, Landroid/view/View;->setVisibility(I)V

    #@1e
    .line 853
    return-void
.end method

.method private updateSubmitButton(Z)V
    .locals 2
    .param p1, "hasText"    # Z

    #@0
    .prologue
    .line 837
    const/16 v0, 0x8

    #@2
    .line 838
    .local v0, "visibility":I
    iget-boolean v1, p0, Landroid/support/v7/widget/SearchView;->mSubmitButtonEnabled:Z

    #@4
    if-eqz v1, :cond_1

    #@6
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->isSubmitAreaEnabled()Z

    #@9
    move-result v1

    #@a
    if-eqz v1, :cond_1

    #@c
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->hasFocus()Z

    #@f
    move-result v1

    #@10
    if-eqz v1, :cond_1

    #@12
    if-nez p1, :cond_0

    #@14
    iget-boolean v1, p0, Landroid/support/v7/widget/SearchView;->mVoiceButtonEnabled:Z

    #@16
    if-nez v1, :cond_1

    #@18
    .line 840
    :cond_0
    const/4 v0, 0x0

    #@19
    .line 842
    :cond_1
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSubmitButton:Landroid/widget/ImageView;

    #@1b
    invoke-virtual {v1, v0}, Landroid/widget/ImageView;->setVisibility(I)V

    #@1e
    .line 843
    return-void
.end method

.method private updateViewsVisibility(Z)V
    .locals 7
    .param p1, "collapsed"    # Z

    #@0
    .prologue
    const/4 v4, 0x1

    #@1
    const/16 v3, 0x8

    #@3
    const/4 v2, 0x0

    #@4
    .line 798
    iput-boolean p1, p0, Landroid/support/v7/widget/SearchView;->mIconified:Z

    #@6
    .line 800
    if-eqz p1, :cond_0

    #@8
    move v1, v2

    #@9
    .line 802
    .local v1, "visCollapsed":I
    :goto_0
    iget-object v5, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@b
    invoke-virtual {v5}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@e
    move-result-object v5

    #@f
    invoke-static {v5}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@12
    move-result v5

    #@13
    if-nez v5, :cond_1

    #@15
    move v0, v4

    #@16
    .line 804
    .local v0, "hasText":Z
    :goto_1
    iget-object v5, p0, Landroid/support/v7/widget/SearchView;->mSearchButton:Landroid/widget/ImageView;

    #@18
    invoke-virtual {v5, v1}, Landroid/widget/ImageView;->setVisibility(I)V

    #@1b
    .line 805
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->updateSubmitButton(Z)V

    #@1e
    .line 806
    iget-object v6, p0, Landroid/support/v7/widget/SearchView;->mSearchEditFrame:Landroid/view/View;

    #@20
    if-eqz p1, :cond_2

    #@22
    move v5, v3

    #@23
    :goto_2
    invoke-virtual {v6, v5}, Landroid/view/View;->setVisibility(I)V

    #@26
    .line 807
    iget-object v5, p0, Landroid/support/v7/widget/SearchView;->mSearchHintIcon:Landroid/widget/ImageView;

    #@28
    iget-boolean v6, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@2a
    if-eqz v6, :cond_3

    #@2c
    :goto_3
    invoke-virtual {v5, v3}, Landroid/widget/ImageView;->setVisibility(I)V

    #@2f
    .line 808
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateCloseButton()V

    #@32
    .line 809
    if-nez v0, :cond_4

    #@34
    :goto_4
    invoke-direct {p0, v4}, Landroid/support/v7/widget/SearchView;->updateVoiceButton(Z)V

    #@37
    .line 810
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateSubmitArea()V

    #@3a
    .line 811
    return-void

    #@3b
    .end local v0    # "hasText":Z
    .end local v1    # "visCollapsed":I
    :cond_0
    move v1, v3

    #@3c
    .line 800
    goto :goto_0

    #@3d
    .restart local v1    # "visCollapsed":I
    :cond_1
    move v0, v2

    #@3e
    .line 802
    goto :goto_1

    #@3f
    .restart local v0    # "hasText":Z
    :cond_2
    move v5, v2

    #@40
    .line 806
    goto :goto_2

    #@41
    :cond_3
    move v3, v2

    #@42
    .line 807
    goto :goto_3

    #@43
    :cond_4
    move v4, v2

    #@44
    .line 809
    goto :goto_4
.end method

.method private updateVoiceButton(Z)V
    .locals 3
    .param p1, "empty"    # Z

    #@0
    .prologue
    .line 1095
    const/16 v0, 0x8

    #@2
    .line 1096
    .local v0, "visibility":I
    iget-boolean v1, p0, Landroid/support/v7/widget/SearchView;->mVoiceButtonEnabled:Z

    #@4
    if-eqz v1, :cond_0

    #@6
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->isIconified()Z

    #@9
    move-result v1

    #@a
    if-nez v1, :cond_0

    #@c
    if-eqz p1, :cond_0

    #@e
    .line 1097
    const/4 v0, 0x0

    #@f
    .line 1098
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSubmitButton:Landroid/widget/ImageView;

    #@11
    const/16 v2, 0x8

    #@13
    invoke-virtual {v1, v2}, Landroid/widget/ImageView;->setVisibility(I)V

    #@16
    .line 1100
    :cond_0
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mVoiceButton:Landroid/widget/ImageView;

    #@18
    invoke-virtual {v1, v0}, Landroid/widget/ImageView;->setVisibility(I)V

    #@1b
    .line 1101
    return-void
.end method


# virtual methods
.method public clearFocus()V
    .locals 2

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 499
    const/4 v0, 0x1

    #@2
    iput-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mClearingFocus:Z

    #@4
    .line 500
    invoke-direct {p0, v1}, Landroid/support/v7/widget/SearchView;->setImeVisibility(Z)V

    #@7
    .line 501
    invoke-super {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->clearFocus()V

    #@a
    .line 502
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@c
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->clearFocus()V

    #@f
    .line 503
    iput-boolean v1, p0, Landroid/support/v7/widget/SearchView;->mClearingFocus:Z

    #@11
    .line 504
    return-void
.end method

.method public getImeOptions()I
    .locals 1

    #@0
    .prologue
    .line 456
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getImeOptions()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getInputType()I
    .locals 1

    #@0
    .prologue
    .line 474
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getInputType()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getMaxWidth()I
    .locals 1

    #@0
    .prologue
    .line 754
    iget v0, p0, Landroid/support/v7/widget/SearchView;->mMaxWidth:I

    #@2
    return v0
.end method

.method public getQuery()Ljava/lang/CharSequence;
    .locals 1

    #@0
    .prologue
    .line 561
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getText()Landroid/text/Editable;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getQueryHint()Ljava/lang/CharSequence;
    .locals 3

    #@0
    .prologue
    .line 601
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mQueryHint:Ljava/lang/CharSequence;

    #@2
    if-eqz v2, :cond_1

    #@4
    .line 602
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryHint:Ljava/lang/CharSequence;

    #@6
    .line 611
    :cond_0
    :goto_0
    return-object v0

    #@7
    .line 603
    :cond_1
    sget-boolean v2, Landroid/support/v7/widget/SearchView;->IS_AT_LEAST_FROYO:Z

    #@9
    if-eqz v2, :cond_2

    #@b
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@d
    if-eqz v2, :cond_2

    #@f
    .line 604
    const/4 v0, 0x0

    #@10
    .line 605
    .local v0, "hint":Ljava/lang/CharSequence;
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@12
    invoke-virtual {v2}, Landroid/app/SearchableInfo;->getHintId()I

    #@15
    move-result v1

    #@16
    .line 606
    .local v1, "hintId":I
    if-eqz v1, :cond_0

    #@18
    .line 607
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->getContext()Landroid/content/Context;

    #@1b
    move-result-object v2

    #@1c
    invoke-virtual {v2, v1}, Landroid/content/Context;->getString(I)Ljava/lang/String;

    #@1f
    move-result-object v0

    #@20
    goto :goto_0

    #@21
    .line 611
    .end local v0    # "hint":Ljava/lang/CharSequence;
    .end local v1    # "hintId":I
    :cond_2
    const/4 v0, 0x0

    #@22
    goto :goto_0
.end method

.method getSuggestionCommitIconResId()I
    .locals 1

    #@0
    .prologue
    .line 401
    iget v0, p0, Landroid/support/v7/widget/SearchView;->mSuggestionCommitIconResId:I

    #@2
    return v0
.end method

.method getSuggestionRowLayout()I
    .locals 1

    #@0
    .prologue
    .line 397
    iget v0, p0, Landroid/support/v7/widget/SearchView;->mSuggestionRowLayout:I

    #@2
    return v0
.end method

.method public getSuggestionsAdapter()Landroid/support/v4/widget/CursorAdapter;
    .locals 1

    #@0
    .prologue
    .line 736
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@2
    return-object v0
.end method

.method public isIconfiedByDefault()Z
    .locals 1

    #@0
    .prologue
    .line 636
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@2
    return v0
.end method

.method public isIconified()Z
    .locals 1

    #@0
    .prologue
    .line 664
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mIconified:Z

    #@2
    return v0
.end method

.method public isQueryRefinementEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 716
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mQueryRefinement:Z

    #@2
    return v0
.end method

.method public isSubmitButtonEnabled()Z
    .locals 1

    #@0
    .prologue
    .line 686
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mSubmitButtonEnabled:Z

    #@2
    return v0
.end method

.method public onActionViewCollapsed()V
    .locals 3

    #@0
    .prologue
    const/4 v2, 0x0

    #@1
    .line 1221
    const-string v0, ""

    #@3
    invoke-virtual {p0, v0, v2}, Landroid/support/v7/widget/SearchView;->setQuery(Ljava/lang/CharSequence;Z)V

    #@6
    .line 1222
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->clearFocus()V

    #@9
    .line 1223
    const/4 v0, 0x1

    #@a
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@d
    .line 1224
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@f
    iget v1, p0, Landroid/support/v7/widget/SearchView;->mCollapsedImeOptions:I

    #@11
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setImeOptions(I)V

    #@14
    .line 1225
    iput-boolean v2, p0, Landroid/support/v7/widget/SearchView;->mExpandedInActionView:Z

    #@16
    .line 1226
    return-void
.end method

.method public onActionViewExpanded()V
    .locals 3

    #@0
    .prologue
    .line 1233
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mExpandedInActionView:Z

    #@2
    if-eqz v0, :cond_0

    #@4
    .line 1240
    :goto_0
    return-void

    #@5
    .line 1235
    :cond_0
    const/4 v0, 0x1

    #@6
    iput-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mExpandedInActionView:Z

    #@8
    .line 1236
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@a
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->getImeOptions()I

    #@d
    move-result v0

    #@e
    iput v0, p0, Landroid/support/v7/widget/SearchView;->mCollapsedImeOptions:I

    #@10
    .line 1237
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@12
    iget v1, p0, Landroid/support/v7/widget/SearchView;->mCollapsedImeOptions:I

    #@14
    const/high16 v2, 0x2000000

    #@16
    or-int/2addr v1, v2

    #@17
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setImeOptions(I)V

    #@1a
    .line 1238
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@1c
    const-string v1, ""

    #@1e
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setText(Ljava/lang/CharSequence;)V

    #@21
    .line 1239
    const/4 v0, 0x0

    #@22
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SearchView;->setIconified(Z)V

    #@25
    goto :goto_0
.end method

.method protected onDetachedFromWindow()V
    .locals 1

    #@0
    .prologue
    .line 877
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mUpdateDrawableStateRunnable:Ljava/lang/Runnable;

    #@2
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SearchView;->removeCallbacks(Ljava/lang/Runnable;)Z

    #@5
    .line 878
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mReleaseCursorRunnable:Ljava/lang/Runnable;

    #@7
    invoke-virtual {p0, v0}, Landroid/support/v7/widget/SearchView;->post(Ljava/lang/Runnable;)Z

    #@a
    .line 879
    invoke-super {p0}, Landroid/support/v7/widget/LinearLayoutCompat;->onDetachedFromWindow()V

    #@d
    .line 880
    return-void
.end method

.method protected onMeasure(II)V
    .locals 3
    .param p1, "widthMeasureSpec"    # I
    .param p2, "heightMeasureSpec"    # I

    #@0
    .prologue
    .line 760
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->isIconified()Z

    #@3
    move-result v2

    #@4
    if-eqz v2, :cond_0

    #@6
    .line 761
    invoke-super {p0, p1, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->onMeasure(II)V

    #@9
    .line 790
    :goto_0
    return-void

    #@a
    .line 765
    :cond_0
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getMode(I)I

    #@d
    move-result v1

    #@e
    .line 766
    .local v1, "widthMode":I
    invoke-static {p1}, Landroid/view/View$MeasureSpec;->getSize(I)I

    #@11
    move-result v0

    #@12
    .line 768
    .local v0, "width":I
    sparse-switch v1, :sswitch_data_0

    #@15
    .line 788
    :cond_1
    :goto_1
    const/high16 v1, 0x40000000    # 2.0f

    #@17
    .line 789
    invoke-static {v0, v1}, Landroid/view/View$MeasureSpec;->makeMeasureSpec(II)I

    #@1a
    move-result v2

    #@1b
    invoke-super {p0, v2, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->onMeasure(II)V

    #@1e
    goto :goto_0

    #@1f
    .line 771
    :sswitch_0
    iget v2, p0, Landroid/support/v7/widget/SearchView;->mMaxWidth:I

    #@21
    if-lez v2, :cond_2

    #@23
    .line 772
    iget v2, p0, Landroid/support/v7/widget/SearchView;->mMaxWidth:I

    #@25
    invoke-static {v2, v0}, Ljava/lang/Math;->min(II)I

    #@28
    move-result v0

    #@29
    goto :goto_1

    #@2a
    .line 774
    :cond_2
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->getPreferredWidth()I

    #@2d
    move-result v2

    #@2e
    invoke-static {v2, v0}, Ljava/lang/Math;->min(II)I

    #@31
    move-result v0

    #@32
    .line 776
    goto :goto_1

    #@33
    .line 779
    :sswitch_1
    iget v2, p0, Landroid/support/v7/widget/SearchView;->mMaxWidth:I

    #@35
    if-lez v2, :cond_1

    #@37
    .line 780
    iget v2, p0, Landroid/support/v7/widget/SearchView;->mMaxWidth:I

    #@39
    invoke-static {v2, v0}, Ljava/lang/Math;->min(II)I

    #@3c
    move-result v0

    #@3d
    goto :goto_1

    #@3e
    .line 785
    :sswitch_2
    iget v2, p0, Landroid/support/v7/widget/SearchView;->mMaxWidth:I

    #@40
    if-lez v2, :cond_3

    #@42
    iget v0, p0, Landroid/support/v7/widget/SearchView;->mMaxWidth:I

    #@44
    :goto_2
    goto :goto_1

    #@45
    :cond_3
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->getPreferredWidth()I

    #@48
    move-result v0

    #@49
    goto :goto_2

    #@4a
    .line 768
    :sswitch_data_0
    .sparse-switch
        -0x80000000 -> :sswitch_0
        0x0 -> :sswitch_2
        0x40000000 -> :sswitch_1
    .end sparse-switch
.end method

.method onQueryRefine(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "queryText"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 901
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SearchView;->setQuery(Ljava/lang/CharSequence;)V

    #@3
    .line 902
    return-void
.end method

.method onTextFocusChanged()V
    .locals 1

    #@0
    .prologue
    .line 1200
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->isIconified()Z

    #@3
    move-result v0

    #@4
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@7
    .line 1203
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->postUpdateFocusedState()V

    #@a
    .line 1204
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@c
    invoke-virtual {v0}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->hasFocus()Z

    #@f
    move-result v0

    #@10
    if-eqz v0, :cond_0

    #@12
    .line 1205
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->forceSuggestionQuery()V

    #@15
    .line 1207
    :cond_0
    return-void
.end method

.method public onWindowFocusChanged(Z)V
    .locals 0
    .param p1, "hasWindowFocus"    # Z

    #@0
    .prologue
    .line 1211
    invoke-super {p0, p1}, Landroid/support/v7/widget/LinearLayoutCompat;->onWindowFocusChanged(Z)V

    #@3
    .line 1213
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->postUpdateFocusedState()V

    #@6
    .line 1214
    return-void
.end method

.method public requestFocus(ILandroid/graphics/Rect;)Z
    .locals 3
    .param p1, "direction"    # I
    .param p2, "previouslyFocusedRect"    # Landroid/graphics/Rect;

    #@0
    .prologue
    const/4 v1, 0x0

    #@1
    .line 481
    iget-boolean v2, p0, Landroid/support/v7/widget/SearchView;->mClearingFocus:Z

    #@3
    if-eqz v2, :cond_1

    #@5
    move v0, v1

    #@6
    .line 492
    :cond_0
    :goto_0
    return v0

    #@7
    .line 483
    :cond_1
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->isFocusable()Z

    #@a
    move-result v2

    #@b
    if-nez v2, :cond_2

    #@d
    move v0, v1

    #@e
    goto :goto_0

    #@f
    .line 485
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->isIconified()Z

    #@12
    move-result v2

    #@13
    if-nez v2, :cond_3

    #@15
    .line 486
    iget-object v2, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@17
    invoke-virtual {v2, p1, p2}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->requestFocus(ILandroid/graphics/Rect;)Z

    #@1a
    move-result v0

    #@1b
    .line 487
    .local v0, "result":Z
    if-eqz v0, :cond_0

    #@1d
    .line 488
    invoke-direct {p0, v1}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@20
    goto :goto_0

    #@21
    .line 492
    .end local v0    # "result":Z
    :cond_3
    invoke-super {p0, p1, p2}, Landroid/support/v7/widget/LinearLayoutCompat;->requestFocus(ILandroid/graphics/Rect;)Z

    #@24
    move-result v0

    #@25
    goto :goto_0
.end method

.method public setAppSearchData(Landroid/os/Bundle;)V
    .locals 0
    .param p1, "appSearchData"    # Landroid/os/Bundle;

    #@0
    .prologue
    .line 437
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mAppSearchData:Landroid/os/Bundle;

    #@2
    .line 438
    return-void
.end method

.method public setIconified(Z)V
    .locals 0
    .param p1, "iconify"    # Z

    #@0
    .prologue
    .line 650
    if-eqz p1, :cond_0

    #@2
    .line 651
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->onCloseClicked()V

    #@5
    .line 655
    :goto_0
    return-void

    #@6
    .line 653
    :cond_0
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->onSearchClicked()V

    #@9
    goto :goto_0
.end method

.method public setIconifiedByDefault(Z)V
    .locals 1
    .param p1, "iconified"    # Z

    #@0
    .prologue
    .line 625
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@2
    if-ne v0, p1, :cond_0

    #@4
    .line 629
    :goto_0
    return-void

    #@5
    .line 626
    :cond_0
    iput-boolean p1, p0, Landroid/support/v7/widget/SearchView;->mIconifiedByDefault:Z

    #@7
    .line 627
    invoke-direct {p0, p1}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@a
    .line 628
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateQueryHint()V

    #@d
    goto :goto_0
.end method

.method public setImeOptions(I)V
    .locals 1
    .param p1, "imeOptions"    # I

    #@0
    .prologue
    .line 447
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setImeOptions(I)V

    #@5
    .line 448
    return-void
.end method

.method public setInputType(I)V
    .locals 1
    .param p1, "inputType"    # I

    #@0
    .prologue
    .line 466
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setInputType(I)V

    #@5
    .line 467
    return-void
.end method

.method public setMaxWidth(I)V
    .locals 0
    .param p1, "maxpixels"    # I

    #@0
    .prologue
    .line 743
    iput p1, p0, Landroid/support/v7/widget/SearchView;->mMaxWidth:I

    #@2
    .line 745
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->requestLayout()V

    #@5
    .line 746
    return-void
.end method

.method public setOnCloseListener(Landroid/support/v7/widget/SearchView$OnCloseListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/widget/SearchView$OnCloseListener;

    #@0
    .prologue
    .line 522
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mOnCloseListener:Landroid/support/v7/widget/SearchView$OnCloseListener;

    #@2
    .line 523
    return-void
.end method

.method public setOnQueryTextFocusChangeListener(Landroid/view/View$OnFocusChangeListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/view/View$OnFocusChangeListener;

    #@0
    .prologue
    .line 531
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mOnQueryTextFocusChangeListener:Landroid/view/View$OnFocusChangeListener;

    #@2
    .line 532
    return-void
.end method

.method public setOnQueryTextListener(Landroid/support/v7/widget/SearchView$OnQueryTextListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/widget/SearchView$OnQueryTextListener;

    #@0
    .prologue
    .line 513
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mOnQueryChangeListener:Landroid/support/v7/widget/SearchView$OnQueryTextListener;

    #@2
    .line 514
    return-void
.end method

.method public setOnSearchClickListener(Landroid/view/View$OnClickListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/view/View$OnClickListener;

    #@0
    .prologue
    .line 552
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mOnSearchClickListener:Landroid/view/View$OnClickListener;

    #@2
    .line 553
    return-void
.end method

.method public setOnSuggestionListener(Landroid/support/v7/widget/SearchView$OnSuggestionListener;)V
    .locals 0
    .param p1, "listener"    # Landroid/support/v7/widget/SearchView$OnSuggestionListener;

    #@0
    .prologue
    .line 540
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mOnSuggestionListener:Landroid/support/v7/widget/SearchView$OnSuggestionListener;

    #@2
    .line 541
    return-void
.end method

.method public setQuery(Ljava/lang/CharSequence;Z)V
    .locals 2
    .param p1, "query"    # Ljava/lang/CharSequence;
    .param p2, "submit"    # Z

    #@0
    .prologue
    .line 573
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@2
    invoke-virtual {v0, p1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setText(Ljava/lang/CharSequence;)V

    #@5
    .line 574
    if-eqz p1, :cond_0

    #@7
    .line 575
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@9
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@b
    invoke-virtual {v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->length()I

    #@e
    move-result v1

    #@f
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setSelection(I)V

    #@12
    .line 576
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mUserQuery:Ljava/lang/CharSequence;

    #@14
    .line 580
    :cond_0
    if-eqz p2, :cond_1

    #@16
    invoke-static {p1}, Landroid/text/TextUtils;->isEmpty(Ljava/lang/CharSequence;)Z

    #@19
    move-result v0

    #@1a
    if-nez v0, :cond_1

    #@1c
    .line 581
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->onSubmitQuery()V

    #@1f
    .line 583
    :cond_1
    return-void
.end method

.method public setQueryHint(Ljava/lang/CharSequence;)V
    .locals 0
    .param p1, "hint"    # Ljava/lang/CharSequence;

    #@0
    .prologue
    .line 592
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mQueryHint:Ljava/lang/CharSequence;

    #@2
    .line 593
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateQueryHint()V

    #@5
    .line 594
    return-void
.end method

.method public setQueryRefinementEnabled(Z)V
    .locals 2
    .param p1, "enable"    # Z

    #@0
    .prologue
    .line 704
    iput-boolean p1, p0, Landroid/support/v7/widget/SearchView;->mQueryRefinement:Z

    #@2
    .line 705
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@4
    instance-of v0, v0, Landroid/support/v7/widget/SuggestionsAdapter;

    #@6
    if-eqz v0, :cond_0

    #@8
    .line 706
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@a
    check-cast v0, Landroid/support/v7/widget/SuggestionsAdapter;

    #@c
    if-eqz p1, :cond_1

    #@e
    const/4 v1, 0x2

    #@f
    :goto_0
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SuggestionsAdapter;->setQueryRefinement(I)V

    #@12
    .line 709
    :cond_0
    return-void

    #@13
    .line 706
    :cond_1
    const/4 v1, 0x1

    #@14
    goto :goto_0
.end method

.method public setSearchableInfo(Landroid/app/SearchableInfo;)V
    .locals 2
    .param p1, "searchable"    # Landroid/app/SearchableInfo;

    #@0
    .prologue
    .line 413
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@2
    .line 414
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mSearchable:Landroid/app/SearchableInfo;

    #@4
    if-eqz v0, :cond_1

    #@6
    .line 415
    sget-boolean v0, Landroid/support/v7/widget/SearchView;->IS_AT_LEAST_FROYO:Z

    #@8
    if-eqz v0, :cond_0

    #@a
    .line 416
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateSearchAutoComplete()V

    #@d
    .line 418
    :cond_0
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->updateQueryHint()V

    #@10
    .line 421
    :cond_1
    sget-boolean v0, Landroid/support/v7/widget/SearchView;->IS_AT_LEAST_FROYO:Z

    #@12
    if-eqz v0, :cond_3

    #@14
    invoke-direct {p0}, Landroid/support/v7/widget/SearchView;->hasVoiceSearch()Z

    #@17
    move-result v0

    #@18
    if-eqz v0, :cond_3

    #@1a
    const/4 v0, 0x1

    #@1b
    :goto_0
    iput-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mVoiceButtonEnabled:Z

    #@1d
    .line 423
    iget-boolean v0, p0, Landroid/support/v7/widget/SearchView;->mVoiceButtonEnabled:Z

    #@1f
    if-eqz v0, :cond_2

    #@21
    .line 426
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@23
    const-string v1, "nm"

    #@25
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setPrivateImeOptions(Ljava/lang/String;)V

    #@28
    .line 428
    :cond_2
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->isIconified()Z

    #@2b
    move-result v0

    #@2c
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@2f
    .line 429
    return-void

    #@30
    .line 421
    :cond_3
    const/4 v0, 0x0

    #@31
    goto :goto_0
.end method

.method public setSubmitButtonEnabled(Z)V
    .locals 1
    .param p1, "enabled"    # Z

    #@0
    .prologue
    .line 676
    iput-boolean p1, p0, Landroid/support/v7/widget/SearchView;->mSubmitButtonEnabled:Z

    #@2
    .line 677
    invoke-virtual {p0}, Landroid/support/v7/widget/SearchView;->isIconified()Z

    #@5
    move-result v0

    #@6
    invoke-direct {p0, v0}, Landroid/support/v7/widget/SearchView;->updateViewsVisibility(Z)V

    #@9
    .line 678
    return-void
.end method

.method public setSuggestionsAdapter(Landroid/support/v4/widget/CursorAdapter;)V
    .locals 2
    .param p1, "adapter"    # Landroid/support/v4/widget/CursorAdapter;

    #@0
    .prologue
    .line 726
    iput-object p1, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@2
    .line 728
    iget-object v0, p0, Landroid/support/v7/widget/SearchView;->mQueryTextView:Landroid/support/v7/widget/SearchView$SearchAutoComplete;

    #@4
    iget-object v1, p0, Landroid/support/v7/widget/SearchView;->mSuggestionsAdapter:Landroid/support/v4/widget/CursorAdapter;

    #@6
    invoke-virtual {v0, v1}, Landroid/support/v7/widget/SearchView$SearchAutoComplete;->setAdapter(Landroid/widget/ListAdapter;)V

    #@9
    .line 729
    return-void
.end method

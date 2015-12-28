.class public Landroid/support/v7/internal/view/SupportMenuInflater;
.super Landroid/view/MenuInflater;
.source "SupportMenuInflater.java"


# annotations
.annotation system Ldalvik/annotation/MemberClasses;
    value = {
        Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;,
        Landroid/support/v7/internal/view/SupportMenuInflater$InflatedOnMenuItemClickListener;
    }
.end annotation


# static fields
.field private static final ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "[",
            "Ljava/lang/Class",
            "<*>;"
        }
    .end annotation
.end field

.field private static final ACTION_VIEW_CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;
    .annotation system Ldalvik/annotation/Signature;
        value = {
            "[",
            "Ljava/lang/Class",
            "<*>;"
        }
    .end annotation
.end field

.field private static final LOG_TAG:Ljava/lang/String; = "SupportMenuInflater"

.field private static final NO_ID:I = 0x0

.field private static final XML_GROUP:Ljava/lang/String; = "group"

.field private static final XML_ITEM:Ljava/lang/String; = "item"

.field private static final XML_MENU:Ljava/lang/String; = "menu"


# instance fields
.field private final mActionProviderConstructorArguments:[Ljava/lang/Object;

.field private final mActionViewConstructorArguments:[Ljava/lang/Object;

.field private mContext:Landroid/content/Context;

.field private mRealOwner:Ljava/lang/Object;


# direct methods
.method static constructor <clinit>()V
    .locals 3

    #@0
    .prologue
    .line 72
    const/4 v0, 0x1

    #@1
    new-array v0, v0, [Ljava/lang/Class;

    #@3
    const/4 v1, 0x0

    #@4
    const-class v2, Landroid/content/Context;

    #@6
    aput-object v2, v0, v1

    #@8
    sput-object v0, Landroid/support/v7/internal/view/SupportMenuInflater;->ACTION_VIEW_CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;

    #@a
    .line 74
    sget-object v0, Landroid/support/v7/internal/view/SupportMenuInflater;->ACTION_VIEW_CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;

    #@c
    sput-object v0, Landroid/support/v7/internal/view/SupportMenuInflater;->ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;

    #@e
    return-void
.end method

.method public constructor <init>(Landroid/content/Context;)V
    .locals 2
    .param p1, "context"    # Landroid/content/Context;

    #@0
    .prologue
    .line 90
    invoke-direct {p0, p1}, Landroid/view/MenuInflater;-><init>(Landroid/content/Context;)V

    #@3
    .line 91
    iput-object p1, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mContext:Landroid/content/Context;

    #@5
    .line 92
    const/4 v0, 0x1

    #@6
    new-array v0, v0, [Ljava/lang/Object;

    #@8
    const/4 v1, 0x0

    #@9
    aput-object p1, v0, v1

    #@b
    iput-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mActionViewConstructorArguments:[Ljava/lang/Object;

    #@d
    .line 93
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mActionViewConstructorArguments:[Ljava/lang/Object;

    #@f
    iput-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mActionProviderConstructorArguments:[Ljava/lang/Object;

    #@11
    .line 94
    return-void
.end method

.method static synthetic access$100(Landroid/support/v7/internal/view/SupportMenuInflater;)Landroid/content/Context;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/view/SupportMenuInflater;

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mContext:Landroid/content/Context;

    #@2
    return-object v0
.end method

.method static synthetic access$200()[Ljava/lang/Class;
    .locals 1

    #@0
    .prologue
    .line 58
    sget-object v0, Landroid/support/v7/internal/view/SupportMenuInflater;->ACTION_PROVIDER_CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;

    #@2
    return-object v0
.end method

.method static synthetic access$300(Landroid/support/v7/internal/view/SupportMenuInflater;)[Ljava/lang/Object;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/view/SupportMenuInflater;

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mActionProviderConstructorArguments:[Ljava/lang/Object;

    #@2
    return-object v0
.end method

.method static synthetic access$400(Landroid/support/v7/internal/view/SupportMenuInflater;)Ljava/lang/Object;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/view/SupportMenuInflater;

    #@0
    .prologue
    .line 58
    invoke-direct {p0}, Landroid/support/v7/internal/view/SupportMenuInflater;->getRealOwner()Ljava/lang/Object;

    #@3
    move-result-object v0

    #@4
    return-object v0
.end method

.method static synthetic access$500()[Ljava/lang/Class;
    .locals 1

    #@0
    .prologue
    .line 58
    sget-object v0, Landroid/support/v7/internal/view/SupportMenuInflater;->ACTION_VIEW_CONSTRUCTOR_SIGNATURE:[Ljava/lang/Class;

    #@2
    return-object v0
.end method

.method static synthetic access$600(Landroid/support/v7/internal/view/SupportMenuInflater;)[Ljava/lang/Object;
    .locals 1
    .param p0, "x0"    # Landroid/support/v7/internal/view/SupportMenuInflater;

    #@0
    .prologue
    .line 58
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mActionViewConstructorArguments:[Ljava/lang/Object;

    #@2
    return-object v0
.end method

.method private findRealOwner(Ljava/lang/Object;)Ljava/lang/Object;
    .locals 1
    .param p1, "owner"    # Ljava/lang/Object;

    #@0
    .prologue
    .line 220
    instance-of v0, p1, Landroid/app/Activity;

    #@2
    if-eqz v0, :cond_1

    #@4
    .line 226
    .end local p1    # "owner":Ljava/lang/Object;
    :cond_0
    :goto_0
    return-object p1

    #@5
    .line 223
    .restart local p1    # "owner":Ljava/lang/Object;
    :cond_1
    instance-of v0, p1, Landroid/content/ContextWrapper;

    #@7
    if-eqz v0, :cond_0

    #@9
    .line 224
    check-cast p1, Landroid/content/ContextWrapper;

    #@b
    .end local p1    # "owner":Ljava/lang/Object;
    invoke-virtual {p1}, Landroid/content/ContextWrapper;->getBaseContext()Landroid/content/Context;

    #@e
    move-result-object v0

    #@f
    invoke-direct {p0, v0}, Landroid/support/v7/internal/view/SupportMenuInflater;->findRealOwner(Ljava/lang/Object;)Ljava/lang/Object;

    #@12
    move-result-object p1

    #@13
    goto :goto_0
.end method

.method private getRealOwner()Ljava/lang/Object;
    .locals 1

    #@0
    .prologue
    .line 213
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mRealOwner:Ljava/lang/Object;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 214
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mContext:Landroid/content/Context;

    #@6
    invoke-direct {p0, v0}, Landroid/support/v7/internal/view/SupportMenuInflater;->findRealOwner(Ljava/lang/Object;)Ljava/lang/Object;

    #@9
    move-result-object v0

    #@a
    iput-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mRealOwner:Ljava/lang/Object;

    #@c
    .line 216
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mRealOwner:Ljava/lang/Object;

    #@e
    return-object v0
.end method

.method private parseMenu(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;Landroid/view/Menu;)V
    .locals 10
    .param p1, "parser"    # Lorg/xmlpull/v1/XmlPullParser;
    .param p2, "attrs"    # Landroid/util/AttributeSet;
    .param p3, "menu"    # Landroid/view/Menu;
    .annotation system Ldalvik/annotation/Throws;
        value = {
            Lorg/xmlpull/v1/XmlPullParserException;,
            Ljava/io/IOException;
        }
    .end annotation

    #@0
    .prologue
    .line 134
    new-instance v2, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;

    #@2
    invoke-direct {v2, p0, p3}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;-><init>(Landroid/support/v7/internal/view/SupportMenuInflater;Landroid/view/Menu;)V

    #@5
    .line 136
    .local v2, "menuState":Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;
    invoke-interface {p1}, Lorg/xmlpull/v1/XmlPullParser;->getEventType()I

    #@8
    move-result v0

    #@9
    .line 138
    .local v0, "eventType":I
    const/4 v1, 0x0

    #@a
    .line 139
    .local v1, "lookingForEndOfUnknownTag":Z
    const/4 v6, 0x0

    #@b
    .line 143
    .local v6, "unknownTagName":Ljava/lang/String;
    :cond_0
    const/4 v7, 0x2

    #@c
    if-ne v0, v7, :cond_3

    #@e
    .line 144
    invoke-interface {p1}, Lorg/xmlpull/v1/XmlPullParser;->getName()Ljava/lang/String;

    #@11
    move-result-object v5

    #@12
    .line 145
    .local v5, "tagName":Ljava/lang/String;
    const-string v7, "menu"

    #@14
    invoke-virtual {v5, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@17
    move-result v7

    #@18
    if-eqz v7, :cond_2

    #@1a
    .line 147
    invoke-interface {p1}, Lorg/xmlpull/v1/XmlPullParser;->next()I

    #@1d
    move-result v0

    #@1e
    .line 156
    .end local v5    # "tagName":Ljava/lang/String;
    :goto_0
    const/4 v3, 0x0

    #@1f
    .line 157
    .local v3, "reachedEndOfMenu":Z
    :goto_1
    if-nez v3, :cond_b

    #@21
    .line 158
    packed-switch v0, :pswitch_data_0

    #@24
    .line 208
    :cond_1
    :goto_2
    invoke-interface {p1}, Lorg/xmlpull/v1/XmlPullParser;->next()I

    #@27
    move-result v0

    #@28
    goto :goto_1

    #@29
    .line 151
    .end local v3    # "reachedEndOfMenu":Z
    .restart local v5    # "tagName":Ljava/lang/String;
    :cond_2
    new-instance v7, Ljava/lang/RuntimeException;

    #@2b
    new-instance v8, Ljava/lang/StringBuilder;

    #@2d
    invoke-direct {v8}, Ljava/lang/StringBuilder;-><init>()V

    #@30
    const-string v9, "Expecting menu, got "

    #@32
    invoke-virtual {v8, v9}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@35
    move-result-object v8

    #@36
    invoke-virtual {v8, v5}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    #@39
    move-result-object v8

    #@3a
    invoke-virtual {v8}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    #@3d
    move-result-object v8

    #@3e
    invoke-direct {v7, v8}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@41
    throw v7

    #@42
    .line 153
    .end local v5    # "tagName":Ljava/lang/String;
    :cond_3
    invoke-interface {p1}, Lorg/xmlpull/v1/XmlPullParser;->next()I

    #@45
    move-result v0

    #@46
    .line 154
    const/4 v7, 0x1

    #@47
    if-ne v0, v7, :cond_0

    #@49
    goto :goto_0

    #@4a
    .line 160
    .restart local v3    # "reachedEndOfMenu":Z
    :pswitch_0
    if-nez v1, :cond_1

    #@4c
    .line 164
    invoke-interface {p1}, Lorg/xmlpull/v1/XmlPullParser;->getName()Ljava/lang/String;

    #@4f
    move-result-object v5

    #@50
    .line 165
    .restart local v5    # "tagName":Ljava/lang/String;
    const-string v7, "group"

    #@52
    invoke-virtual {v5, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@55
    move-result v7

    #@56
    if-eqz v7, :cond_4

    #@58
    .line 166
    invoke-virtual {v2, p2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->readGroup(Landroid/util/AttributeSet;)V

    #@5b
    goto :goto_2

    #@5c
    .line 167
    :cond_4
    const-string v7, "item"

    #@5e
    invoke-virtual {v5, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@61
    move-result v7

    #@62
    if-eqz v7, :cond_5

    #@64
    .line 168
    invoke-virtual {v2, p2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->readItem(Landroid/util/AttributeSet;)V

    #@67
    goto :goto_2

    #@68
    .line 169
    :cond_5
    const-string v7, "menu"

    #@6a
    invoke-virtual {v5, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@6d
    move-result v7

    #@6e
    if-eqz v7, :cond_6

    #@70
    .line 171
    invoke-virtual {v2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->addSubMenuItem()Landroid/view/SubMenu;

    #@73
    move-result-object v4

    #@74
    .line 174
    .local v4, "subMenu":Landroid/view/SubMenu;
    invoke-direct {p0, p1, p2, v4}, Landroid/support/v7/internal/view/SupportMenuInflater;->parseMenu(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;Landroid/view/Menu;)V

    #@77
    goto :goto_2

    #@78
    .line 176
    .end local v4    # "subMenu":Landroid/view/SubMenu;
    :cond_6
    const/4 v1, 0x1

    #@79
    .line 177
    move-object v6, v5

    #@7a
    .line 179
    goto :goto_2

    #@7b
    .line 182
    .end local v5    # "tagName":Ljava/lang/String;
    :pswitch_1
    invoke-interface {p1}, Lorg/xmlpull/v1/XmlPullParser;->getName()Ljava/lang/String;

    #@7e
    move-result-object v5

    #@7f
    .line 183
    .restart local v5    # "tagName":Ljava/lang/String;
    if-eqz v1, :cond_7

    #@81
    invoke-virtual {v5, v6}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@84
    move-result v7

    #@85
    if-eqz v7, :cond_7

    #@87
    .line 184
    const/4 v1, 0x0

    #@88
    .line 185
    const/4 v6, 0x0

    #@89
    goto :goto_2

    #@8a
    .line 186
    :cond_7
    const-string v7, "group"

    #@8c
    invoke-virtual {v5, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@8f
    move-result v7

    #@90
    if-eqz v7, :cond_8

    #@92
    .line 187
    invoke-virtual {v2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->resetGroup()V

    #@95
    goto :goto_2

    #@96
    .line 188
    :cond_8
    const-string v7, "item"

    #@98
    invoke-virtual {v5, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@9b
    move-result v7

    #@9c
    if-eqz v7, :cond_a

    #@9e
    .line 191
    invoke-virtual {v2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->hasAddedItem()Z

    #@a1
    move-result v7

    #@a2
    if-nez v7, :cond_1

    #@a4
    .line 192
    # getter for: Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->itemActionProvider:Landroid/support/v4/view/ActionProvider;
    invoke-static {v2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->access$000(Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;)Landroid/support/v4/view/ActionProvider;

    #@a7
    move-result-object v7

    #@a8
    if-eqz v7, :cond_9

    #@aa
    # getter for: Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->itemActionProvider:Landroid/support/v4/view/ActionProvider;
    invoke-static {v2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->access$000(Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;)Landroid/support/v4/view/ActionProvider;

    #@ad
    move-result-object v7

    #@ae
    invoke-virtual {v7}, Landroid/support/v4/view/ActionProvider;->hasSubMenu()Z

    #@b1
    move-result v7

    #@b2
    if-eqz v7, :cond_9

    #@b4
    .line 194
    invoke-virtual {v2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->addSubMenuItem()Landroid/view/SubMenu;

    #@b7
    goto/16 :goto_2

    #@b9
    .line 196
    :cond_9
    invoke-virtual {v2}, Landroid/support/v7/internal/view/SupportMenuInflater$MenuState;->addItem()V

    #@bc
    goto/16 :goto_2

    #@be
    .line 199
    :cond_a
    const-string v7, "menu"

    #@c0
    invoke-virtual {v5, v7}, Ljava/lang/String;->equals(Ljava/lang/Object;)Z

    #@c3
    move-result v7

    #@c4
    if-eqz v7, :cond_1

    #@c6
    .line 200
    const/4 v3, 0x1

    #@c7
    goto/16 :goto_2

    #@c9
    .line 205
    .end local v5    # "tagName":Ljava/lang/String;
    :pswitch_2
    new-instance v7, Ljava/lang/RuntimeException;

    #@cb
    const-string v8, "Unexpected end of document"

    #@cd
    invoke-direct {v7, v8}, Ljava/lang/RuntimeException;-><init>(Ljava/lang/String;)V

    #@d0
    throw v7

    #@d1
    .line 210
    :cond_b
    return-void

    #@d2
    .line 158
    :pswitch_data_0
    .packed-switch 0x1
        :pswitch_2
        :pswitch_0
        :pswitch_1
    .end packed-switch
.end method


# virtual methods
.method public inflate(ILandroid/view/Menu;)V
    .locals 5
    .param p1, "menuRes"    # I
    .param p2, "menu"    # Landroid/view/Menu;

    #@0
    .prologue
    .line 108
    instance-of v3, p2, Landroid/support/v4/internal/view/SupportMenu;

    #@2
    if-nez v3, :cond_1

    #@4
    .line 109
    invoke-super {p0, p1, p2}, Landroid/view/MenuInflater;->inflate(ILandroid/view/Menu;)V

    #@7
    .line 126
    :cond_0
    :goto_0
    return-void

    #@8
    .line 113
    :cond_1
    const/4 v2, 0x0

    #@9
    .line 115
    .local v2, "parser":Landroid/content/res/XmlResourceParser;
    :try_start_0
    iget-object v3, p0, Landroid/support/v7/internal/view/SupportMenuInflater;->mContext:Landroid/content/Context;

    #@b
    invoke-virtual {v3}, Landroid/content/Context;->getResources()Landroid/content/res/Resources;

    #@e
    move-result-object v3

    #@f
    invoke-virtual {v3, p1}, Landroid/content/res/Resources;->getLayout(I)Landroid/content/res/XmlResourceParser;

    #@12
    move-result-object v2

    #@13
    .line 116
    invoke-static {v2}, Landroid/util/Xml;->asAttributeSet(Lorg/xmlpull/v1/XmlPullParser;)Landroid/util/AttributeSet;

    #@16
    move-result-object v0

    #@17
    .line 118
    .local v0, "attrs":Landroid/util/AttributeSet;
    invoke-direct {p0, v2, v0, p2}, Landroid/support/v7/internal/view/SupportMenuInflater;->parseMenu(Lorg/xmlpull/v1/XmlPullParser;Landroid/util/AttributeSet;Landroid/view/Menu;)V
    :try_end_0
    .catch Lorg/xmlpull/v1/XmlPullParserException; {:try_start_0 .. :try_end_0} :catch_0
    .catch Ljava/io/IOException; {:try_start_0 .. :try_end_0} :catch_1
    .catchall {:try_start_0 .. :try_end_0} :catchall_0

    #@1a
    .line 124
    if-eqz v2, :cond_0

    #@1c
    invoke-interface {v2}, Landroid/content/res/XmlResourceParser;->close()V

    #@1f
    goto :goto_0

    #@20
    .line 119
    .end local v0    # "attrs":Landroid/util/AttributeSet;
    :catch_0
    move-exception v1

    #@21
    .line 120
    .local v1, "e":Lorg/xmlpull/v1/XmlPullParserException;
    :try_start_1
    new-instance v3, Landroid/view/InflateException;

    #@23
    const-string v4, "Error inflating menu XML"

    #@25
    invoke-direct {v3, v4, v1}, Landroid/view/InflateException;-><init>(Ljava/lang/String;Ljava/lang/Throwable;)V

    #@28
    throw v3
    :try_end_1
    .catchall {:try_start_1 .. :try_end_1} :catchall_0

    #@29
    .line 124
    .end local v1    # "e":Lorg/xmlpull/v1/XmlPullParserException;
    :catchall_0
    move-exception v3

    #@2a
    if-eqz v2, :cond_2

    #@2c
    invoke-interface {v2}, Landroid/content/res/XmlResourceParser;->close()V

    #@2f
    :cond_2
    throw v3

    #@30
    .line 121
    :catch_1
    move-exception v1

    #@31
    .line 122
    .local v1, "e":Ljava/io/IOException;
    :try_start_2
    new-instance v3, Landroid/view/InflateException;

    #@33
    const-string v4, "Error inflating menu XML"

    #@35
    invoke-direct {v3, v4, v1}, Landroid/view/InflateException;-><init>(Ljava/lang/String;Ljava/lang/Throwable;)V

    #@38
    throw v3
    :try_end_2
    .catchall {:try_start_2 .. :try_end_2} :catchall_0
.end method

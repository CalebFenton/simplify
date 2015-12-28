.class public Landroid/support/v7/internal/widget/TintTypedArray;
.super Ljava/lang/Object;
.source "TintTypedArray.java"


# instance fields
.field private final mContext:Landroid/content/Context;

.field private mTintManager:Landroid/support/v7/internal/widget/TintManager;

.field private final mWrapped:Landroid/content/res/TypedArray;


# direct methods
.method private constructor <init>(Landroid/content/Context;Landroid/content/res/TypedArray;)V
    .locals 0
    .param p1, "context"    # Landroid/content/Context;
    .param p2, "array"    # Landroid/content/res/TypedArray;

    #@0
    .prologue
    .line 53
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    #@3
    .line 54
    iput-object p1, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mContext:Landroid/content/Context;

    #@5
    .line 55
    iput-object p2, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@7
    .line 56
    return-void
.end method

.method public static obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[I)Landroid/support/v7/internal/widget/TintTypedArray;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "set"    # Landroid/util/AttributeSet;
    .param p2, "attrs"    # [I

    #@0
    .prologue
    .line 43
    invoke-virtual {p0, p1, p2}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[I)Landroid/content/res/TypedArray;

    #@3
    move-result-object v0

    #@4
    .line 44
    .local v0, "array":Landroid/content/res/TypedArray;
    new-instance v1, Landroid/support/v7/internal/widget/TintTypedArray;

    #@6
    invoke-direct {v1, p0, v0}, Landroid/support/v7/internal/widget/TintTypedArray;-><init>(Landroid/content/Context;Landroid/content/res/TypedArray;)V

    #@9
    return-object v1
.end method

.method public static obtainStyledAttributes(Landroid/content/Context;Landroid/util/AttributeSet;[III)Landroid/support/v7/internal/widget/TintTypedArray;
    .locals 2
    .param p0, "context"    # Landroid/content/Context;
    .param p1, "set"    # Landroid/util/AttributeSet;
    .param p2, "attrs"    # [I
    .param p3, "defStyleAttr"    # I
    .param p4, "defStyleRes"    # I

    #@0
    .prologue
    .line 49
    invoke-virtual {p0, p1, p2, p3, p4}, Landroid/content/Context;->obtainStyledAttributes(Landroid/util/AttributeSet;[III)Landroid/content/res/TypedArray;

    #@3
    move-result-object v0

    #@4
    .line 50
    .local v0, "array":Landroid/content/res/TypedArray;
    new-instance v1, Landroid/support/v7/internal/widget/TintTypedArray;

    #@6
    invoke-direct {v1, p0, v0}, Landroid/support/v7/internal/widget/TintTypedArray;-><init>(Landroid/content/Context;Landroid/content/res/TypedArray;)V

    #@9
    return-object v1
.end method


# virtual methods
.method public getBoolean(IZ)Z
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # Z

    #@0
    .prologue
    .line 97
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getBoolean(IZ)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getChangingConfigurations()I
    .locals 1

    #@0
    .prologue
    .line 177
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->getChangingConfigurations()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getColor(II)I
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # I

    #@0
    .prologue
    .line 109
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getColor(II)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getColorStateList(I)Landroid/content/res/ColorStateList;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 113
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->getColorStateList(I)Landroid/content/res/ColorStateList;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getDimension(IF)F
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # F

    #@0
    .prologue
    .line 121
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getDimension(IF)F

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getDimensionPixelOffset(II)I
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # I

    #@0
    .prologue
    .line 125
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getDimensionPixelOffset(II)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getDimensionPixelSize(II)I
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # I

    #@0
    .prologue
    .line 129
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getDimensionPixelSize(II)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getDrawable(I)Landroid/graphics/drawable/Drawable;
    .locals 3
    .param p1, "index"    # I

    #@0
    .prologue
    .line 59
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v1, p1}, Landroid/content/res/TypedArray;->hasValue(I)Z

    #@5
    move-result v1

    #@6
    if-eqz v1, :cond_0

    #@8
    .line 60
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@a
    const/4 v2, 0x0

    #@b
    invoke-virtual {v1, p1, v2}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@e
    move-result v0

    #@f
    .line 61
    .local v0, "resourceId":I
    if-eqz v0, :cond_0

    #@11
    .line 62
    invoke-virtual {p0}, Landroid/support/v7/internal/widget/TintTypedArray;->getTintManager()Landroid/support/v7/internal/widget/TintManager;

    #@14
    move-result-object v1

    #@15
    invoke-virtual {v1, v0}, Landroid/support/v7/internal/widget/TintManager;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@18
    move-result-object v1

    #@19
    .line 65
    .end local v0    # "resourceId":I
    :goto_0
    return-object v1

    #@1a
    :cond_0
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@1c
    invoke-virtual {v1, p1}, Landroid/content/res/TypedArray;->getDrawable(I)Landroid/graphics/drawable/Drawable;

    #@1f
    move-result-object v1

    #@20
    goto :goto_0
.end method

.method public getFloat(IF)F
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # F

    #@0
    .prologue
    .line 105
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getFloat(IF)F

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getFraction(IIIF)F
    .locals 1
    .param p1, "index"    # I
    .param p2, "base"    # I
    .param p3, "pbase"    # I
    .param p4, "defValue"    # F

    #@0
    .prologue
    .line 141
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2, p3, p4}, Landroid/content/res/TypedArray;->getFraction(IIIF)F

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getIndex(I)I
    .locals 1
    .param p1, "at"    # I

    #@0
    .prologue
    .line 77
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->getIndex(I)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getIndexCount()I
    .locals 1

    #@0
    .prologue
    .line 73
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->getIndexCount()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getInt(II)I
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # I

    #@0
    .prologue
    .line 101
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getInt(II)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getInteger(II)I
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # I

    #@0
    .prologue
    .line 117
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getInteger(II)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getLayoutDimension(II)I
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # I

    #@0
    .prologue
    .line 137
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getLayoutDimension(II)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getLayoutDimension(ILjava/lang/String;)I
    .locals 1
    .param p1, "index"    # I
    .param p2, "name"    # Ljava/lang/String;

    #@0
    .prologue
    .line 133
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getLayoutDimension(ILjava/lang/String;)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getNonResourceString(I)Ljava/lang/String;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 93
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->getNonResourceString(I)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getPositionDescription()Ljava/lang/String;
    .locals 1

    #@0
    .prologue
    .line 169
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->getPositionDescription()Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getResourceId(II)I
    .locals 1
    .param p1, "index"    # I
    .param p2, "defValue"    # I

    #@0
    .prologue
    .line 145
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getResourceId(II)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getResources()Landroid/content/res/Resources;
    .locals 1

    #@0
    .prologue
    .line 81
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->getResources()Landroid/content/res/Resources;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getString(I)Ljava/lang/String;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 89
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->getString(I)Ljava/lang/String;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getText(I)Ljava/lang/CharSequence;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 85
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->getText(I)Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getTextArray(I)[Ljava/lang/CharSequence;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 149
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->getTextArray(I)[Ljava/lang/CharSequence;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public getTintManager()Landroid/support/v7/internal/widget/TintManager;
    .locals 2

    #@0
    .prologue
    .line 181
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@2
    if-nez v0, :cond_0

    #@4
    .line 182
    new-instance v0, Landroid/support/v7/internal/widget/TintManager;

    #@6
    iget-object v1, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mContext:Landroid/content/Context;

    #@8
    invoke-direct {v0, v1}, Landroid/support/v7/internal/widget/TintManager;-><init>(Landroid/content/Context;)V

    #@b
    iput-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@d
    .line 184
    :cond_0
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mTintManager:Landroid/support/v7/internal/widget/TintManager;

    #@f
    return-object v0
.end method

.method public getType(I)I
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 157
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->getType(I)I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public getValue(ILandroid/util/TypedValue;)Z
    .locals 1
    .param p1, "index"    # I
    .param p2, "outValue"    # Landroid/util/TypedValue;

    #@0
    .prologue
    .line 153
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1, p2}, Landroid/content/res/TypedArray;->getValue(ILandroid/util/TypedValue;)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public hasValue(I)Z
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 161
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->hasValue(I)Z

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public length()I
    .locals 1

    #@0
    .prologue
    .line 69
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->length()I

    #@5
    move-result v0

    #@6
    return v0
.end method

.method public peekValue(I)Landroid/util/TypedValue;
    .locals 1
    .param p1, "index"    # I

    #@0
    .prologue
    .line 165
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0, p1}, Landroid/content/res/TypedArray;->peekValue(I)Landroid/util/TypedValue;

    #@5
    move-result-object v0

    #@6
    return-object v0
.end method

.method public recycle()V
    .locals 1

    #@0
    .prologue
    .line 173
    iget-object v0, p0, Landroid/support/v7/internal/widget/TintTypedArray;->mWrapped:Landroid/content/res/TypedArray;

    #@2
    invoke-virtual {v0}, Landroid/content/res/TypedArray;->recycle()V

    #@5
    .line 174
    return-void
.end method

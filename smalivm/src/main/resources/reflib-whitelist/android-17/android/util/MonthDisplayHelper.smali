.class public Landroid/util/MonthDisplayHelper;
.super Ljava/lang/Object;
.source "MonthDisplayHelper.java"


# instance fields
.field private mCalendar:Ljava/util/Calendar;

.field private mNumDaysInMonth:I

.field private mNumDaysInPrevMonth:I

.field private mOffset:I

.field private final mWeekStartDay:I


# direct methods
.method public constructor <init>(II)V
    .registers 4
    .param p1, "year"    # I
    .param p2, "month"    # I

    .prologue
    .line 67
    const/4 v0, 0x1

    invoke-direct {p0, p1, p2, v0}, Landroid/util/MonthDisplayHelper;-><init>(III)V

    .line 68
    return-void
.end method

.method public constructor <init>(III)V
    .registers 8
    .param p1, "year"    # I
    .param p2, "month"    # I
    .param p3, "weekStartDay"    # I

    .prologue
    const/4 v3, 0x1

    const/4 v2, 0x0

    .line 46
    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    .line 48
    if-lt p3, v3, :cond_a

    const/4 v0, 0x7

    if-le p3, v0, :cond_10

    .line 49
    :cond_a
    new-instance v0, Ljava/lang/IllegalArgumentException;

    invoke-direct {v0}, Ljava/lang/IllegalArgumentException;-><init>()V

    throw v0

    .line 51
    :cond_10
    iput p3, p0, Landroid/util/MonthDisplayHelper;->mWeekStartDay:I

    .line 53
    invoke-static {}, Ljava/util/Calendar;->getInstance()Ljava/util/Calendar;

    move-result-object v0

    iput-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    .line 54
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    invoke-virtual {v0, v3, p1}, Ljava/util/Calendar;->set(II)V

    .line 55
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v1, 0x2

    invoke-virtual {v0, v1, p2}, Ljava/util/Calendar;->set(II)V

    .line 56
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v1, 0x5

    invoke-virtual {v0, v1, v3}, Ljava/util/Calendar;->set(II)V

    .line 57
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/16 v1, 0xb

    invoke-virtual {v0, v1, v2}, Ljava/util/Calendar;->set(II)V

    .line 58
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/16 v1, 0xc

    invoke-virtual {v0, v1, v2}, Ljava/util/Calendar;->set(II)V

    .line 59
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/16 v1, 0xd

    invoke-virtual {v0, v1, v2}, Ljava/util/Calendar;->set(II)V

    .line 60
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    invoke-virtual {v0}, Ljava/util/Calendar;->getTimeInMillis()J

    .line 62
    invoke-direct {p0}, Landroid/util/MonthDisplayHelper;->recalculate()V

    .line 63
    return-void
.end method

.method private recalculate()V
    .registers 7

    .prologue
    const/4 v5, 0x5

    const/4 v4, 0x2

    .line 200
    iget-object v2, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    invoke-virtual {v2, v5}, Ljava/util/Calendar;->getActualMaximum(I)I

    move-result v2

    iput v2, p0, Landroid/util/MonthDisplayHelper;->mNumDaysInMonth:I

    .line 202
    iget-object v2, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v3, -0x1

    invoke-virtual {v2, v4, v3}, Ljava/util/Calendar;->add(II)V

    .line 203
    iget-object v2, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    invoke-virtual {v2, v5}, Ljava/util/Calendar;->getActualMaximum(I)I

    move-result v2

    iput v2, p0, Landroid/util/MonthDisplayHelper;->mNumDaysInPrevMonth:I

    .line 204
    iget-object v2, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v3, 0x1

    invoke-virtual {v2, v4, v3}, Ljava/util/Calendar;->add(II)V

    .line 206
    invoke-virtual {p0}, Landroid/util/MonthDisplayHelper;->getFirstDayOfMonth()I

    move-result v0

    .line 207
    .local v0, "firstDayOfMonth":I
    iget v2, p0, Landroid/util/MonthDisplayHelper;->mWeekStartDay:I

    sub-int v1, v0, v2

    .line 208
    .local v1, "offset":I
    if-gez v1, :cond_2a

    .line 209
    add-int/lit8 v1, v1, 0x7

    .line 211
    :cond_2a
    iput v1, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    .line 212
    return-void
.end method


# virtual methods
.method public getColumnOf(I)I
    .registers 3
    .param p1, "day"    # I

    .prologue
    .line 157
    iget v0, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    add-int/2addr v0, p1

    add-int/lit8 v0, v0, -0x1

    rem-int/lit8 v0, v0, 0x7

    return v0
.end method

.method public getDayAt(II)I
    .registers 6
    .param p1, "row"    # I
    .param p2, "column"    # I

    .prologue
    .line 136
    if-nez p1, :cond_f

    iget v1, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    if-ge p2, v1, :cond_f

    .line 137
    iget v1, p0, Landroid/util/MonthDisplayHelper;->mNumDaysInPrevMonth:I

    add-int/2addr v1, p2

    iget v2, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    sub-int/2addr v1, v2

    add-int/lit8 v0, v1, 0x1

    .line 142
    :cond_e
    :goto_e
    return v0

    .line 140
    :cond_f
    mul-int/lit8 v1, p1, 0x7

    add-int/2addr v1, p2

    iget v2, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    sub-int/2addr v1, v2

    add-int/lit8 v0, v1, 0x1

    .line 142
    .local v0, "day":I
    iget v1, p0, Landroid/util/MonthDisplayHelper;->mNumDaysInMonth:I

    if-le v0, v1, :cond_e

    iget v1, p0, Landroid/util/MonthDisplayHelper;->mNumDaysInMonth:I

    sub-int/2addr v0, v1

    goto :goto_e
.end method

.method public getDigitsForRow(I)[I
    .registers 7
    .param p1, "row"    # I

    .prologue
    const/4 v3, 0x7

    .line 116
    if-ltz p1, :cond_6

    const/4 v2, 0x5

    if-le p1, v2, :cond_26

    .line 117
    :cond_6
    new-instance v2, Ljava/lang/IllegalArgumentException;

    new-instance v3, Ljava/lang/StringBuilder;

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V

    const-string/jumbo v4, "row "

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3, p1}, Ljava/lang/StringBuilder;->append(I)Ljava/lang/StringBuilder;

    move-result-object v3

    const-string v4, " out of range (0-5)"

    invoke-virtual {v3, v4}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;

    move-result-object v3

    invoke-virtual {v3}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;

    move-result-object v3

    invoke-direct {v2, v3}, Ljava/lang/IllegalArgumentException;-><init>(Ljava/lang/String;)V

    throw v2

    .line 121
    :cond_26
    new-array v1, v3, [I

    .line 122
    .local v1, "result":[I
    const/4 v0, 0x0

    .local v0, "column":I
    :goto_29
    if-ge v0, v3, :cond_34

    .line 123
    invoke-virtual {p0, p1, v0}, Landroid/util/MonthDisplayHelper;->getDayAt(II)I

    move-result v2

    aput v2, v1, v0

    .line 122
    add-int/lit8 v0, v0, 0x1

    goto :goto_29

    .line 126
    :cond_34
    return-object v1
.end method

.method public getFirstDayOfMonth()I
    .registers 3

    .prologue
    .line 89
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v1, 0x7

    invoke-virtual {v0, v1}, Ljava/util/Calendar;->get(I)I

    move-result v0

    return v0
.end method

.method public getMonth()I
    .registers 3

    .prologue
    .line 76
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v1, 0x2

    invoke-virtual {v0, v1}, Ljava/util/Calendar;->get(I)I

    move-result v0

    return v0
.end method

.method public getNumberOfDaysInMonth()I
    .registers 2

    .prologue
    .line 96
    iget v0, p0, Landroid/util/MonthDisplayHelper;->mNumDaysInMonth:I

    return v0
.end method

.method public getOffset()I
    .registers 2

    .prologue
    .line 106
    iget v0, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    return v0
.end method

.method public getRowOf(I)I
    .registers 3
    .param p1, "day"    # I

    .prologue
    .line 150
    iget v0, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    add-int/2addr v0, p1

    add-int/lit8 v0, v0, -0x1

    div-int/lit8 v0, v0, 0x7

    return v0
.end method

.method public getWeekStartDay()I
    .registers 2

    .prologue
    .line 81
    iget v0, p0, Landroid/util/MonthDisplayHelper;->mWeekStartDay:I

    return v0
.end method

.method public getYear()I
    .registers 3

    .prologue
    .line 72
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v1, 0x1

    invoke-virtual {v0, v1}, Ljava/util/Calendar;->get(I)I

    move-result v0

    return v0
.end method

.method public isWithinCurrentMonth(II)Z
    .registers 7
    .param p1, "row"    # I
    .param p2, "column"    # I

    .prologue
    const/4 v1, 0x0

    .line 181
    if-ltz p1, :cond_b

    if-ltz p2, :cond_b

    const/4 v2, 0x5

    if-gt p1, v2, :cond_b

    const/4 v2, 0x6

    if-le p2, v2, :cond_c

    .line 193
    :cond_b
    :goto_b
    return v1

    .line 185
    :cond_c
    if-nez p1, :cond_12

    iget v2, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    if-lt p2, v2, :cond_b

    .line 189
    :cond_12
    mul-int/lit8 v2, p1, 0x7

    add-int/2addr v2, p2

    iget v3, p0, Landroid/util/MonthDisplayHelper;->mOffset:I

    sub-int/2addr v2, v3

    add-int/lit8 v0, v2, 0x1

    .line 190
    .local v0, "day":I
    iget v2, p0, Landroid/util/MonthDisplayHelper;->mNumDaysInMonth:I

    if-gt v0, v2, :cond_b

    .line 193
    const/4 v1, 0x1

    goto :goto_b
.end method

.method public nextMonth()V
    .registers 4

    .prologue
    .line 172
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v1, 0x2

    const/4 v2, 0x1

    invoke-virtual {v0, v1, v2}, Ljava/util/Calendar;->add(II)V

    .line 173
    invoke-direct {p0}, Landroid/util/MonthDisplayHelper;->recalculate()V

    .line 174
    return-void
.end method

.method public previousMonth()V
    .registers 4

    .prologue
    .line 164
    iget-object v0, p0, Landroid/util/MonthDisplayHelper;->mCalendar:Ljava/util/Calendar;

    const/4 v1, 0x2

    const/4 v2, -0x1

    invoke-virtual {v0, v1, v2}, Ljava/util/Calendar;->add(II)V

    .line 165
    invoke-direct {p0}, Landroid/util/MonthDisplayHelper;->recalculate()V

    .line 166
    return-void
.end method

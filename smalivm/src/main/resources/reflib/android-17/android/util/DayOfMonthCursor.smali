.class public Landroid/util/DayOfMonthCursor;
.super Landroid/util/MonthDisplayHelper;
.source "DayOfMonthCursor.java"


# instance fields
.field private mColumn:I

.field private mRow:I


# direct methods
.method public constructor <init>(IIII)V
    .registers 6
    .param p1, "year"    # I
    .param p2, "month"    # I
    .param p3, "dayOfMonth"    # I
    .param p4, "weekStartDay"    # I

    .prologue
    .line 49
    invoke-direct {p0, p1, p2, p4}, Landroid/util/MonthDisplayHelper;-><init>(III)V

    .line 50
    invoke-virtual {p0, p3}, Landroid/util/DayOfMonthCursor;->getRowOf(I)I

    move-result v0

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 51
    invoke-virtual {p0, p3}, Landroid/util/DayOfMonthCursor;->getColumnOf(I)I

    move-result v0

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    .line 52
    return-void
.end method


# virtual methods
.method public down()Z
    .registers 4

    .prologue
    const/4 v0, 0x0

    .line 121
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    add-int/lit8 v1, v1, 0x1

    iget v2, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v1, v2}, Landroid/util/DayOfMonthCursor;->isWithinCurrentMonth(II)Z

    move-result v1

    if-eqz v1, :cond_14

    .line 123
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    add-int/lit8 v1, v1, 0x1

    iput v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 132
    :goto_13
    return v0

    .line 127
    :cond_14
    invoke-virtual {p0}, Landroid/util/DayOfMonthCursor;->nextMonth()V

    .line 128
    iput v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 129
    :goto_19
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v0, v1}, Landroid/util/DayOfMonthCursor;->isWithinCurrentMonth(II)Z

    move-result v0

    if-nez v0, :cond_2a

    .line 130
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    add-int/lit8 v0, v0, 0x1

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    goto :goto_19

    .line 132
    :cond_2a
    const/4 v0, 0x1

    goto :goto_13
.end method

.method public getSelectedColumn()I
    .registers 2

    .prologue
    .line 60
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    return v0
.end method

.method public getSelectedDayOfMonth()I
    .registers 3

    .prologue
    .line 69
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v0, v1}, Landroid/util/DayOfMonthCursor;->getDayAt(II)I

    move-result v0

    return v0
.end method

.method public getSelectedMonthOffset()I
    .registers 3

    .prologue
    .line 77
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v0, v1}, Landroid/util/DayOfMonthCursor;->isWithinCurrentMonth(II)Z

    move-result v0

    if-eqz v0, :cond_c

    .line 78
    const/4 v0, 0x0

    .line 83
    :goto_b
    return v0

    .line 80
    :cond_c
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    if-nez v0, :cond_12

    .line 81
    const/4 v0, -0x1

    goto :goto_b

    .line 83
    :cond_12
    const/4 v0, 0x1

    goto :goto_b
.end method

.method public getSelectedRow()I
    .registers 2

    .prologue
    .line 56
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    return v0
.end method

.method public isSelected(II)Z
    .registers 4
    .param p1, "row"    # I
    .param p2, "column"    # I

    .prologue
    .line 92
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    if-ne v0, p1, :cond_a

    iget v0, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    if-ne v0, p2, :cond_a

    const/4 v0, 0x1

    :goto_9
    return v0

    :cond_a
    const/4 v0, 0x0

    goto :goto_9
.end method

.method public left()Z
    .registers 4

    .prologue
    .line 141
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    if-nez v1, :cond_19

    .line 142
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    add-int/lit8 v1, v1, -0x1

    iput v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 143
    const/4 v1, 0x6

    iput v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    .line 148
    :goto_d
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    iget v2, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v1, v2}, Landroid/util/DayOfMonthCursor;->isWithinCurrentMonth(II)Z

    move-result v1

    if-eqz v1, :cond_20

    .line 149
    const/4 v1, 0x0

    .line 157
    :goto_18
    return v1

    .line 145
    :cond_19
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    add-int/lit8 v1, v1, -0x1

    iput v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    goto :goto_d

    .line 153
    :cond_20
    invoke-virtual {p0}, Landroid/util/DayOfMonthCursor;->previousMonth()V

    .line 154
    invoke-virtual {p0}, Landroid/util/DayOfMonthCursor;->getNumberOfDaysInMonth()I

    move-result v0

    .line 155
    .local v0, "lastDay":I
    invoke-virtual {p0, v0}, Landroid/util/DayOfMonthCursor;->getRowOf(I)I

    move-result v1

    iput v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 156
    invoke-virtual {p0, v0}, Landroid/util/DayOfMonthCursor;->getColumnOf(I)I

    move-result v1

    iput v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    .line 157
    const/4 v1, 0x1

    goto :goto_18
.end method

.method public right()Z
    .registers 4

    .prologue
    const/4 v0, 0x0

    .line 166
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    const/4 v2, 0x6

    if-ne v1, v2, :cond_19

    .line 167
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    add-int/lit8 v1, v1, 0x1

    iput v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 168
    iput v0, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    .line 173
    :goto_e
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    iget v2, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v1, v2}, Landroid/util/DayOfMonthCursor;->isWithinCurrentMonth(II)Z

    move-result v1

    if-eqz v1, :cond_20

    .line 184
    :goto_18
    return v0

    .line 170
    :cond_19
    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    add-int/lit8 v1, v1, 0x1

    iput v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    goto :goto_e

    .line 178
    :cond_20
    invoke-virtual {p0}, Landroid/util/DayOfMonthCursor;->nextMonth()V

    .line 179
    iput v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 180
    iput v0, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    .line 181
    :goto_27
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v0, v1}, Landroid/util/DayOfMonthCursor;->isWithinCurrentMonth(II)Z

    move-result v0

    if-nez v0, :cond_38

    .line 182
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    add-int/lit8 v0, v0, 0x1

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    goto :goto_27

    .line 184
    :cond_38
    const/4 v0, 0x1

    goto :goto_18
.end method

.method public setSelectedDayOfMonth(I)V
    .registers 3
    .param p1, "dayOfMonth"    # I

    .prologue
    .line 87
    invoke-virtual {p0, p1}, Landroid/util/DayOfMonthCursor;->getRowOf(I)I

    move-result v0

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 88
    invoke-virtual {p0, p1}, Landroid/util/DayOfMonthCursor;->getColumnOf(I)I

    move-result v0

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    .line 89
    return-void
.end method

.method public setSelectedRowColumn(II)V
    .registers 3
    .param p1, "row"    # I
    .param p2, "col"    # I

    .prologue
    .line 64
    iput p1, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 65
    iput p2, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    .line 66
    return-void
.end method

.method public up()Z
    .registers 3

    .prologue
    .line 101
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    add-int/lit8 v0, v0, -0x1

    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v0, v1}, Landroid/util/DayOfMonthCursor;->isWithinCurrentMonth(II)Z

    move-result v0

    if-eqz v0, :cond_14

    .line 103
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    add-int/lit8 v0, v0, -0x1

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 104
    const/4 v0, 0x0

    .line 112
    :goto_13
    return v0

    .line 107
    :cond_14
    invoke-virtual {p0}, Landroid/util/DayOfMonthCursor;->previousMonth()V

    .line 108
    const/4 v0, 0x5

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    .line 109
    :goto_1a
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    iget v1, p0, Landroid/util/DayOfMonthCursor;->mColumn:I

    invoke-virtual {p0, v0, v1}, Landroid/util/DayOfMonthCursor;->isWithinCurrentMonth(II)Z

    move-result v0

    if-nez v0, :cond_2b

    .line 110
    iget v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    add-int/lit8 v0, v0, -0x1

    iput v0, p0, Landroid/util/DayOfMonthCursor;->mRow:I

    goto :goto_1a

    .line 112
    :cond_2b
    const/4 v0, 0x1

    goto :goto_13
.end method

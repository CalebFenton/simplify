.class public Ltask66/InvokeForms;
.super Ltask66/InvokeBase;
.implements Ltask66/InvokeInterface;

.method public constructor <init>()V
    .locals 0
    invoke-direct {p0}, Ltask66/InvokeBase;-><init>()V
    return-void
.end method

.method private directValue()I
    .locals 1
    const/4 v0, 0x2
    return v0
.end method

.method public value()I
    .locals 1
    const/4 v0, 0x3
    return v0
.end method

.method public static unusedNonStaticForms()I
    .locals 2
    new-instance v0, Ltask66/InvokeForms;
    invoke-direct {v0}, Ltask66/InvokeForms;-><init>()V
    invoke-direct {v0}, Ltask66/InvokeForms;->directValue()I
    invoke-direct/range {v0 .. v0}, Ltask66/InvokeForms;->directValue()I
    invoke-virtual {v0}, Ltask66/InvokeForms;->value()I
    invoke-virtual/range {v0 .. v0}, Ltask66/InvokeForms;->value()I
    invoke-interface {v0}, Ltask66/InvokeInterface;->value()I
    invoke-interface/range {v0 .. v0}, Ltask66/InvokeInterface;->value()I
    const/4 v1, 0x7
    return v1
.end method

.method public unusedSuperForms()I
    .locals 1
    invoke-super {p0}, Ltask66/InvokeBase;->value()I
    invoke-super/range {p0 .. p0}, Ltask66/InvokeBase;->value()I
    const/4 v0, 0x7
    return v0
.end method

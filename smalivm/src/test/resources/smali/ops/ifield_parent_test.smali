# Plan 9 Task 19: the DECLARING half of the inherited-INSTANCE-field pair (the static twin is
# static_fields_parent_test.smali). `inherited` is declared here and referenced through the subclass;
# `shadow:I` is the parent half of the same-name-different-type pair ifield_child_test.smali completes.
.class public Lifield_parent_test;
.super Ljava/lang/Object;

.field public inherited:I
.field public shadow:I

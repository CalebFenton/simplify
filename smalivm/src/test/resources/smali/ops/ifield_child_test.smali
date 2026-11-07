# Plan 9 Task 19: the REFERENCING half of the inherited-INSTANCE-field pair. Declares no `inherited`
# of its own -- a reference through this class must walk to the parent -- and declares `shadow:J`,
# same NAME as the parent's `shadow:I` with a different TYPE, so resolution keyed on name alone
# (the oracle's `type/VirtualClass.java:83-98`) would collapse two fields Dalvik keeps apart.
.class public Lifield_child_test;
.super Lifield_parent_test;

.field public shadow:J

# The referencing half of the inherited instance-field pair. Declares no `inherited` of its own, so a
# reference through this class must walk to the parent, and declares `shadow:J`, same name as the
# parent's `shadow:I` with a different type, so name-only resolution would collapse two distinct fields.
.class public Lifield_child_test;
.super Lifield_parent_test;

.field public shadow:J

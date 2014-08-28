.class Lcheck_cast_test;
.super Ljava/lang/Object;

.method public static CastToObject()V
    .locals 1

    check-cast v0, Ljava/lang/Object;

    return-void
.end method

.method public static CastToObjectArray()V
    .locals 1

    check-cast v0, Ljava/lang/Integer;

    return-void
.end method

.method public static CastToNumber()V
  .locals 1

  check-cast v0, Ljava/lang/Number;

  return-void
.end method

.method public static CastToUnknownClass()V
  .locals 1

  check-cast v0, Lsome_totally_unknown_class;

  return-void
.end method

.method public static CastToKnownLocalParentClass()V
  .locals 1

  check-cast v0, Lparent_class;

  return-void
.end method

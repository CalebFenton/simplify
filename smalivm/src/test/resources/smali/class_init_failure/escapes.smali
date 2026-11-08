.class public Lclass_init_failure/escapes;
.super Ljava/lang/Object;

.method public static newInstance()V
    .registers 1
  :first_start
    new-instance v0, Lclass_init_failure/non_error;
  :first_end
    return-void
  :first_catch
    new-instance v0, Lclass_init_failure/non_error;
    return-void
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
.end method

.method public static sget()V
    .registers 1
  :first_start
    sget v0, Lclass_init_failure/non_error;->value:I
  :first_end
    return-void
  :first_catch
    sget v0, Lclass_init_failure/non_error;->value:I
    return-void
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
.end method

.method public static invokeStatic()V
    .registers 0
  :first_start
    invoke-static {}, Lclass_init_failure/non_error;->touch()V
  :first_end
    return-void
  :first_catch
    invoke-static {}, Lclass_init_failure/non_error;->touch()V
    return-void
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
.end method

.method public static classForName()V
    .registers 1
    const-string v0, "class_init_failure.non_error"
  :first_start
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
  :first_end
    return-void
  :first_catch
    invoke-static {v0}, Ljava/lang/Class;->forName(Ljava/lang/String;)Ljava/lang/Class;
    return-void
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
.end method

.method public static reflectiveStatic()V
    .registers 2
    const-class v0, Lclass_init_failure/non_error;
    const-string v1, "value"
    invoke-virtual {v0, v1}, Ljava/lang/Class;->getDeclaredField(Ljava/lang/String;)Ljava/lang/reflect/Field;
    move-result-object v0
    const/4 v1, 0x0
  :first_start
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
  :first_end
    return-void
  :first_catch
    invoke-virtual {v0, v1}, Ljava/lang/reflect/Field;->get(Ljava/lang/Object;)Ljava/lang/Object;
    return-void
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
.end method

# The grandparent's initializer fails first, so initializing the parent fails on its superclass.
.method public static superclassFailure()V
    .registers 1
  :first_start
    new-instance v0, Lclass_init_failure/grandparent;
  :first_end
    return-void
  :first_catch
    new-instance v0, Lclass_init_failure/parent;
    return-void
    .catch Ljava/lang/ExceptionInInitializerError; {:first_start .. :first_end} :first_catch
.end method

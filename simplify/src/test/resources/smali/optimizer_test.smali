.class Loptimizer_test;
.super Ljava/lang/Object;

.method public static simpleLoop()I
  .locals 2

  const/4 v0, 0x1
  const/4 v1, 0x7

  :loop
  if-ge v0, v1, :end
  add-int/2addr v0, v0
  goto :loop

  :end
  return-void
.end method

# The move-objects aren't me trolling. They're from unoptimized dx output.
# They're useful for breaking some assumptions, so they're kept.
.method public static stringBuilderHelloWorld()Ljava/lang/String;
    .locals 5

    .prologue
    .line 43
    new-instance v2, Ljava/lang/StringBuilder;

    move-object v4, v2
    move-object v2, v4
    move-object v3, v4

    invoke-direct {v3}, Ljava/lang/StringBuilder;-><init>()V
    move-object v1, v2

    .line 45
    move-object v2, v1
    const-string v3, "hello"
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    move-result-object v2

    .line 47
    move-object v2, v1
    const-string v3, ","
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    move-result-object v2

    .line 49
    move-object v2, v1
    const-string v3, "world!"
    invoke-virtual {v2, v3}, Ljava/lang/StringBuilder;->append(Ljava/lang/String;)Ljava/lang/StringBuilder;
    move-result-object v2

    .line 51
    move-object v2, v1
    invoke-virtual {v2}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v2
    move-object v0, v2

    return-object v0
.end method

.method private static floatingPoints()D
    .locals 10

    #@0
    .prologue
    .line 21
    const-string v8, "1.0"

    #@2
    invoke-static {v8}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D

    #@5
    move-result-wide v0

    #@6
    .line 22
    .local v0, "d":D
    const-string v8, "1.1"

    #@8
    invoke-static {v8}, Ljava/lang/Double;->parseDouble(Ljava/lang/String;)D

    #@b
    move-result-wide v2

    #@c
    .line 23
    .local v2, "d1":D
    const-string v8, "1.0"

    #@e
    invoke-static {v8}, Ljava/lang/Float;->parseFloat(Ljava/lang/String;)F

    #@11
    move-result v8

    #@12
    float-to-double v4, v8

    #@13
    .line 24
    .local v4, "f":D
    const-string v8, "1.1"

    #@15
    invoke-static {v8}, Ljava/lang/Float;->parseFloat(Ljava/lang/String;)F

    #@18
    move-result v8

    #@19
    float-to-double v6, v8

    #@1a
    .line 25
    .local v6, "f1":D
    add-double v8, v0, v2

    #@1c
    add-double/2addr v8, v4

    #@1d
    add-double/2addr v8, v6

    #@1e
    return-wide v8
.end method


# index out of bounds
.method public static catchThingy()V
    .locals 1

    :try_start_0
    const/4 v0, 0x1 # @0

    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_error

    :return
    return-void # @1

    :catch_error
    move-exception v0 # @2

    goto :return # @3
.end method

# direct methods
.method public static troubleTry()I
    .locals 8

    const/4 v5, 0x4

    new-instance v0, Ljava/lang/Integer;

    :try_start_0
    invoke-static {}, Lcant_remove;->this()V

    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    new-instance v0, Ljava/lang/Integer;

    :try_start_1
    new-instance v1, Ljava/lang/Integer;
    :try_end_1
    .catch Ljava/lang/Exception; {:try_start_1 .. :try_end_1} :catch_1

    :goto_0
    return v5

    :catch_0
    move-exception v0

    :goto_1
    invoke-virtual {v1}, Ljava/lang/Exception;->printStackTrace()V

    goto :goto_0

    :catch_1
    move-exception v1

    goto :goto_1
.end method

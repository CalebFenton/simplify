.class public Lhost_collections_test;
.super Ljava/lang/Object;

# Adds 3 and 4 to a LinkedList and sums them through its iterator, a private JDK class: 7.
.method public static linkedListIterationSum()I
    .registers 4
    new-instance v0, Ljava/util/LinkedList;
    invoke-direct {v0}, Ljava/util/LinkedList;-><init>()V
    const/4 v1, 0x3
    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v1
    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    const/4 v1, 0x4
    invoke-static {v1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object v1
    invoke-interface {v0, v1}, Ljava/util/List;->add(Ljava/lang/Object;)Z
    const/4 v2, 0x0
    invoke-interface {v0}, Ljava/util/List;->iterator()Ljava/util/Iterator;
    move-result-object v0
    :loop
    invoke-interface {v0}, Ljava/util/Iterator;->hasNext()Z
    move-result v1
    if-eqz v1, :done
    invoke-interface {v0}, Ljava/util/Iterator;->next()Ljava/lang/Object;
    move-result-object v1
    check-cast v1, Ljava/lang/Integer;
    invoke-virtual {v1}, Ljava/lang/Integer;->intValue()I
    move-result v1
    add-int/2addr v2, v1
    goto :loop
    :done
    return v2
.end method

# String.valueOf(Object) of an array is "[I@" plus the array's identity hash.
.method public static arrayThroughObjectParameter()Ljava/lang/String;
    .registers 2
    const/4 v0, 0x1
    new-array v0, v0, [I
    invoke-static {v0}, Ljava/lang/String;->valueOf(Ljava/lang/Object;)Ljava/lang/String;
    move-result-object v0
    return-object v0
.end method

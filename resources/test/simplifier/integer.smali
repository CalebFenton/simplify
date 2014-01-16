.class public final Linteger;
.super Ljava/lang/Object;

.method public static test1()I
    .locals 2

    # stack = { v0 => [{value => java.lang.Integer, used => [], referenced => [8]}] }
    new-instance v0, Ljava/lang/Integer;

    # stack = { v0 => [{value => java.lang.Integer, used => [], referenced => [8]}]
	#         , v1 => [{value => 1, used => [], referenced = [12]}] }
    const/4 v1, 0x1

    # stack = { v0 => [{value => java.lang.Integer(1), used => [], referenced => [8, 16]}]
	#         , v1 => [{value => 1, used => [16], referenced = [12]}] }
    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V

    # stack does not change, intValue should not count as usage for v0
    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    # stack = { v0 => [{value => 1, used => []}, {value => java.lang.Integer(1), used => [], referenced => [8, 16]}]
	#         , v1 => [{value => 1, used => [16], referenced = [12]}}] }
	# when replacing register, try and reduce. in this case, v0 was unused
	# lines to remove = v0's referenced array
	# for all register history, remove used elements in referenced array (they're not actually used now)
	#
    # stack = { v0 => [{value => 1, used => []}]
	#         , v1 => [{value => 1, used => []}, referenced = [12]}] }
	# since we know value of v0 for this reference, replace with const
    move-result v0 # const 0x1 (not sure which type /4, /16)

    # we're done and since v1 is unused, add all referenced elements to lines to remove
    return v0
.end method


.method public static test2()I
    .locals 3

    new-instance v0, Ljava/lang/Integer;

    const/4 v1, 0x1

    invoke-direct {v0, v1}, Ljava/lang/Integer;-><init>(I)V

    invoke-virtual {v0}, Ljava/lang/Integer;->intValue()I

    move-result v1

    # intValue is known, replace v0 with const
    move/16 v2, v1

    # no usage for v1, remove references
    # once it's removed, no usages for v1, remove them also
    return v2
.end method
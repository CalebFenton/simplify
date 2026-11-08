package org.cf.smalivm.differential

/** One method to run and the registers to project from its returning paths. */
data class Fixture(
    val id: String,
    val className: String,
    val methodDescriptor: String,
    val observeRegisters: Set<Int> = setOf(0),
    /** Register -> (host seed, Dalvik descriptor); admitted as one graph to preserve aliases. */
    val seededRegisters: Map<Int, Pair<Any?, String>> = emptyMap(),
)

/** Expected core behavior for one fixture; [basis] names the authority for the expectation. */
data class CoreRegression(
    val fixture: Fixture,
    val basis: String,
    val aborted: Boolean = false,
    val registers: Map<Int, NormalizedValue> = emptyMap(),
    val caughtTypes: Map<Int, String> = emptyMap(),
    val reached: Set<Int> = emptySet(),
    val notReached: Set<Int> = emptySet(),
    val exactReached: Set<Int>? = null,
    val escapes: Set<String>? = null,
)

object Fixtures {
    private fun <T> List<T>.requiringUniqueIds(name: String, idOf: (T) -> String): List<T> {
        val duplicates = groupingBy(idOf).eachCount().filterValues { it > 1 }
        require(duplicates.isEmpty()) { "$name contains duplicate ids: $duplicates" }
        return this
    }

    private val ABSENT = NormalizedValue(ValueKind.ABSENT, null, null)

    /** Completing fixtures whose registers `CoreProjectionTest` specifies exactly. */
    val curated: List<Fixture> = listOf(
        Fixture("spine-return-five", "Lspine/spine;", "returnFive()I"),
        Fixture("spine-return-nothing", "Lspine/spine;", "returnNothing()V"),
        Fixture("spine-nop-then-const", "Lspine/spine;", "nopThenConst()I"),
        Fixture("spine-goto-skip", "Lspine/spine;", "gotoSkip()I"),
        Fixture("spine-move-chain", "Lspine/spine;", "moveChain()I", setOf(0, 1, 2)),
        Fixture("spine-return-wide", "Lspine/spine;", "returnWide()J"),
        Fixture("spine-return-string", "Lspine/spine;", "returnString()Ljava/lang/String;"),
        Fixture("spine-instance-wide-params", "Lspine/spine;", "instanceWideParams(JLjava/lang/String;I)I", setOf(0, 3, 4, 5, 6, 7)),
        Fixture("spine-add-int", "Lspine/spine;", "unsupportedOpcode()I", setOf(0, 1)),
        Fixture("branch-disagreeing-arms", "Ldifferential/branch;", "disagreeingArms(I)V", setOf(0, 1, 2)),
        Fixture("branch-returns-different-constants", "Ldifferential/branch;", "returnsDifferentConstants(I)I"),
        Fixture("branch-known-falls-through", "Ldifferential/branch;", "knownBranchFallsThrough()I"),
        Fixture("smoke-return-five", "Ldifferential/smoke;", "returnFive()I"),
        Fixture("smoke-nested-array", "Ldifferential/smoke;", "nestedArray()[[I"),
        Fixture("smoke-self-referential-array", "Ldifferential/smoke;", "selfReferentialArray()[Ljava/lang/Object;"),
        Fixture("smoke-shared-inner-array", "Ldifferential/smoke;", "sharedInnerArray()[[I"),
        Fixture("smoke-null-element-from-fresh-array", "Ldifferential/smoke;", "nullElementFromFreshArray()Ljava/lang/Object;", setOf(2)),
        Fixture("smoke-fill-array-data-round-trip", "Ldifferential/smoke;", "fillArrayDataRoundTrip()[I"),
        Fixture("smoke-new-object", "Ldifferential/smoke;", "newObject()Ljava/lang/Object;"),
        Fixture("ratchet-compare-wide", "Ldifferential/ratchet;", "compareWide()I", setOf(4)),
        Fixture("ratchet-array-round-trip", "Ldifferential/ratchet;", "arrayRoundTrip()I", setOf(2)),
        Fixture("ratchet-branch-on-zero", "Ldifferential/ratchet;", "branchOnZero()I"),
        Fixture("ratchet-invoke-static", "Ldifferential/ratchet;", "invokeStatic()I"),
        Fixture("ratchet-new-instance", "Ldifferential/ratchet;", "newInstance()Ljava/lang/Object;"),
        Fixture("ratchet-alias-unrelated-allocation", "Ldifferential/ratchet;", "initializeOneOfTwoAllocations()V", setOf(0, 1)),
        Fixture("ratchet-alias-object-allocation", "Ldifferential/ratchet;", "initializeOneOfTwoObjectAllocations()V", setOf(0, 1)),
        Fixture("ratchet-static-field-round-trip", "Ldifferential/ratchet;", "staticFieldRoundTrip()I"),
        Fixture("switch-packed-known-match", "Ldifferential/switch;", "packedKnownMatch()I"),
        Fixture("switch-packed-known-no-match", "Ldifferential/switch;", "packedKnownNoMatch()I"),
        Fixture("switch-sparse-known-match", "Ldifferential/switch;", "sparseKnownMatch()I"),
        Fixture("switch-unknown-selector-forks", "Ldifferential/switch;", "unknownSelectorEveryArmAgrees(I)I"),
        Fixture("types-array-arms-join-covariantly", "Ldifferential/types;", "arrayArmsJoinCovariantly(I)Ljava/lang/Object;"),
        Fixture("types-cast-string-to-object", "Ldifferential/types;", "castStringToObject()Ljava/lang/Object;"),
        Fixture("types-const-class-object", "Ldifferential/types;", "constClassObject()Ljava/lang/Class;"),
        Fixture("types-known-instance-of-object", "Ldifferential/types;", "knownStringIsInstanceOfObject()I"),
        Fixture("unary-int-to-long", "Lunary_math_test;", "intToLong()V", seededRegisters = mapOf(0 to (11 to "I"))),
        Fixture("unary-neg-double", "Lunary_math_test;", "negDouble()V", seededRegisters = mapOf(0 to (15.1 to "D"))),
        Fixture("binary-add-int", "Lbinary_math_test;", "addInt()V", setOf(0, 1), mapOf(0 to (-3 to "I"), 1 to (7 to "I"))),
        Fixture("binary-add-int-2addr", "Lbinary_math_test;", "addInt2Addr()V", seededRegisters = mapOf(0 to (-3 to "I"), 1 to (7 to "I"))),
        Fixture("binary-add-int-lit8", "Lbinary_math_test;", "addIntLit8()V", seededRegisters = mapOf(0 to (-3 to "I"))),
        Fixture("binary-rsub-int", "Lbinary_math_test;", "RSubInt()V", seededRegisters = mapOf(0 to (10 to "I"))),
        Fixture("binary-shl-long", "Lbinary_math_test;", "shlLong()V", setOf(0, 2), mapOf(0 to (1L to "J"), 2 to (33 to "I"))),
        Fixture("binary-div-double", "Lbinary_math_test;", "divDouble()V", seededRegisters = mapOf(0 to (10.0 to "D"), 2 to (4.0 to "D"))),
        Fixture("binary-div-double-by-zero", "Lbinary_math_test;", "divDouble()V", seededRegisters = mapOf(0 to (10.0 to "D"), 2 to (0.0 to "D"))),
        Fixture("binary-div-int-by-zero-caught", "Lbinary_math_test;", "divIntWithCatch()V", seededRegisters = mapOf(0 to (10 to "I"), 1 to (0 to "I"))),
        Fixture("cmp-long-greater", "Lcmp_test;", "cmpLong()V", seededRegisters = mapOf(0 to (7L to "J"), 2 to (5L to "J"))),
        Fixture("cmp-float-nan-cmpg", "Lcmp_test;", "cmpgFloat()V", setOf(0, 1), mapOf(0 to (Float.NaN to "F"), 1 to (1f to "F"))),
        Fixture("cmp-float-nan-cmpl", "Lcmp_test;", "cmplFloat()V", seededRegisters = mapOf(0 to (Float.NaN to "F"), 1 to (1f to "F"))),
        Fixture("new-instance-local", "Lnew_instance_test;", "newLocalInstance()V"),
        Fixture("new-instance-framework-class", "Lnew_instance_test;", "newNonLocalInstance()V"),
        Fixture("new-instance-two-allocations", "Lnew_instance_test;", "newTwoLocalInstances()V", setOf(0, 1)),
        Fixture("new-instance-triggers-class-init", "Lnew_instance_test;", "newWitnessInstance()V"),
        Fixture("sget-literal-no-clinit", "Lsget_test;", "getStaticIntLiteral()V"),
        Fixture("statics-clinit-assigned-field", "Ldifferential/statics_reader;", "readClinitAssigned()I"),
        Fixture("invoke-static-resolved", "Ldifferential/invocation;", "callStatic()I"),
        Fixture("invoke-direct-resolved", "Ldifferential/invocation;", "callDirect()I", setOf(1)),
        Fixture("invoke-virtual-resolved", "Ldifferential/invocation;", "callVirtual()I", setOf(1)),
        Fixture("invoke-interface-resolved", "Ldifferential/invocation;", "callInterface()I", setOf(1)),
        Fixture("invoke-handled-throw", "Ldifferential/invocation;", "callHandledThrow()I"),
        Fixture("invoke-handled-partition-agreement", "Ldifferential/invocation;", "handledPartitionIsNotContaminated(I)I"),
        Fixture("class-init-parent-only", "Ldifferential/init_order_reader;", "initializeParentOnly()I"),
    ).requiringUniqueIds("curated") { it.id }

    private fun invocation(id: String, descriptor: String, observe: Set<Int> = setOf(0)) =
        Fixture(id, "Ldifferential/invocation;", descriptor, observe)

    val regressions: List<CoreRegression> = listOf(
        CoreRegression(
            Fixture("spine-throw-caught", "Lspine/spine;", "throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;"),
            "An unknown RuntimeException may or may not match the handler, so both the handler and the escape " +
                "are feasible (DALVIK-SEMANTICS.md, Handler routing).",
        ),
        CoreRegression(
            Fixture("spine-throw-uncaught", "Lspine/spine;", "throwUncaught(Ljava/lang/RuntimeException;)V"),
            "A throw with no covering handler leaves the method as an unhandled exception ([BC] throw).",
            escapes = setOf("java.lang.RuntimeException:null"),
        ),
        CoreRegression(
            Fixture("spine-throw-two-handlers", "Lspine/spine;", "throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;"),
            "An unknown throwable type forks every declared handler plus the escape (DALVIK-SEMANTICS.md, Handler routing).",
            escapes = setOf("java.lang.Object:null"),
        ),
        CoreRegression(
            Fixture("spine-throw-catch-all", "Lspine/spine;", "throwCatchAll(Ljava/lang/Object;)Ljava/lang/Object;"),
            "A covering catch-all handles every throwable, so nothing escapes even for an unknown type " +
                "(DALVIK-SEMANTICS.md, Handler routing).",
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("spine-throw-null-const", "Lspine/spine;", "throwNullConst()Ljava/lang/Object;"),
            "Throwing the null idiom raises a fresh NullPointerException, which the RuntimeException clause catches " +
                "([BC] throw; DALVIK-SEMANTICS.md, Registers and the null idiom).",
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("spine-throw-unloadable-catch-type", "Lspine/spine;", "throwUnloadableCatchType(Ljava/lang/RuntimeException;)Ljava/lang/Object;"),
            "An unloadable catch type joins the ambiguous handler set and the run still completes " +
                "(DALVIK-SEMANTICS.md, Handler routing).",
        ),
        CoreRegression(
            Fixture("spine-falls-off-the-end", "Lspine/spine;", "fallsOffTheEnd()V"),
            "Running past the last instruction is verifier-rejected input that ends as a bounded abort " +
                "(DALVIK-SEMANTICS.md, Engine choices for verifier-excluded input).",
            aborted = true,
        ),
        CoreRegression(
            Fixture("branch-move-from-untouched-register", "Ldifferential/branch;", "moveFromAnUntouchedRegister()V", setOf(0, 1)),
            "Reading a never-assigned register is verifier-rejected; its unconstrained bits are Top, held at v0 and " +
                "absent at v1 (DALVIK-SEMANTICS.md, Engine choices for verifier-excluded input).",
            registers = mapOf(0 to NormalizedValue(ValueKind.CONFLICTED, null, null), 1 to ABSENT),
        ),
        CoreRegression(
            Fixture("if-lt-two-distinct-references", "Lif_test;", "branchOnOrderingOverTwoDistinctReferences()V"),
            "An ordering between two references is unprovable, so both arms are explored " +
                "(DALVIK-SEMANTICS.md, Engine choices for verifier-excluded input).",
            reached = setOf(9),
        ),
        CoreRegression(
            Fixture("if-eq-reference-against-nonzero-int", "Lif_test;", "branchOnEqualityBetweenAReferenceAndANonZeroInt()V"),
            "A non-null reference is provably non-zero but not provably different from 5, so both arms are explored " +
                "(DALVIK-SEMANTICS.md, if-*).",
            reached = setOf(8),
        ),
        CoreRegression(
            Fixture("switch-sparse-descending-keys", "Ldifferential/switch;", "sparseDescendingKeys()I"),
            "Unsorted sparse keys are verifier-rejected, so the core explores both the binary-search and the " +
                "exact-match target (DALVIK-SEMANTICS.md, packed-switch / sparse-switch).",
            reached = setOf(4, 5),
        ),
        CoreRegression(
            Fixture("switch-float-selector-read-by-value", "Ldifferential/switch;", "floatSelectorReadByValue()I", seededRegisters = mapOf(0 to (2.9f to "F"))),
            "A switch reads its selector's 32 bits, not a host Float's numeric value, so the unprovable selector " +
                "explores every arm (DALVIK-SEMANTICS.md, packed-switch / sparse-switch).",
            reached = setOf(4, 5, 6, 7, 10, 11),
        ),
        CoreRegression(
            Fixture("switch-null-selector-against-key-zero", "Ldifferential/switch;", "nullSelectorAgainstKeyZero()I", seededRegisters = mapOf(0 to (null to "Ljava/lang/String;"))),
            "`(Object) null == (int) 0` ([BC] General design), so a null selector takes case 0 only.",
            reached = setOf(6, 7),
            notReached = setOf(4, 5),
        ),
        CoreRegression(
            Fixture("binary-rem-int-unknown-divisor", "Lbinary_math_test;", "remIntUnknownDivisorWithCatch(I)V", seededRegisters = mapOf(0 to (12 to "I"))),
            "`rem-int` by zero throws ArithmeticException ([BC] binop), so an unknown divisor explores the handler too.",
            reached = setOf(3, 4),
        ),
        CoreRegression(
            Fixture("binary-div-int-unknown-dividend", "Lbinary_math_test;", "divIntUnknownDividendWithCatch(I)V", seededRegisters = mapOf(0 to (7 to "I"))),
            "`div-int` throws only for a zero divisor and overflow wraps (JLS 15.17.2), so a known non-zero divisor " +
                "never reaches the handler.",
            notReached = setOf(3, 4),
        ),
        CoreRegression(
            Fixture("new-array-unloadable-component", "Lnew_array_test;", "createNonExistentArrayClass()V", seededRegisters = mapOf(0 to (1 to "I"))),
            "Resolving an absent component class raises NoClassDefFoundError, which a catch(Exception) does not catch " +
                "(JVMS 6.5 anewarray; DALVIK-SEMANTICS.md, new-array).",
            escapes = setOf("java.lang.NoClassDefFoundError:Failed resolution of: [Ldoes/not/exist;"),
        ),
        CoreRegression(
            Fixture("new-array-both-bad-caught", "Lnew_array_test;", "createBothBadArrayClass()V", seededRegisters = mapOf(0 to (-1 to "I"))),
            "ART checks the length before resolving the component, so -1 raises NegativeArraySizeException " +
                "(DALVIK-SEMANTICS.md, new-array).",
            caughtTypes = mapOf(0 to "Ljava/lang/NegativeArraySizeException;"),
        ),
        CoreRegression(
            Fixture("smoke-aput-unknown-value", "Ldifferential/smoke;", "aputUnknownValue(I)[I"),
            "An unknown stored value changes only its own element, and a non-null array at an in-range index " +
                "throws nothing ([BC] aput).",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "[I", "[?, 0]")),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("smoke-aput-unknown-value-with-catch", "Ldifferential/smoke;", "aputUnknownValueWithCatch(I)V", setOf(1)),
            "With a non-null array and an in-range index neither NPE nor AIOOBE is feasible, so the handler is " +
                "unreachable ([BC] aput).",
            notReached = setOf(7, 8),
        ),
        CoreRegression(
            Fixture("smoke-aput-unknown-index", "Ldifferential/smoke;", "aputUnknownIndex(I)[I"),
            "A store at an unprovable index joins every element with the stored value and makes AIOOBE feasible " +
                "([BC] aput).",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "[I", "[?, ?]")),
            escapes = setOf("java.lang.ArrayIndexOutOfBoundsException:null"),
        ),
        CoreRegression(
            Fixture("smoke-filled-new-array-unknown-element", "Ldifferential/smoke;", "filledNewArrayUnknownElement(I)[I", setOf(1)),
            "`filled-new-array` has no operand that can fail, and an unknown element stays unknown in its own slot " +
                "([BC] filled-new-array).",
            registers = mapOf(1 to NormalizedValue(ValueKind.KNOWN, "[I", "[1, ?]")),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("smoke-filled-new-array-reference-component", "Ldifferential/smoke;", "filledNewArrayOfStringsWithNullElement()V", setOf(2)),
            "A primitive-written zero register contributes null to a reference array " +
                "(DALVIK-SEMANTICS.md, filled-new-array).",
            registers = mapOf(2 to NormalizedValue(ValueKind.KNOWN, "[Ljava/lang/String;", "[x, null]")),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("binary-add-int-uncoercible-operand", "Lbinary_math_test;", "addInt()V", seededRegisters = mapOf(0 to ("not a number" to "Ljava/lang/String;"), 1 to (7 to "I"))),
            "A non-numeric operand is malformed input, so the result is Unknown(I) rather than a crash or a constant " +
                "(DALVIK-SEMANTICS.md, Coercion and register bits).",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "I", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("cmp-float-uncoercible-operand", "Lcmp_test;", "cmplFloat()V", seededRegisters = mapOf(0 to ("left" to "Ljava/lang/String;"), 1 to ("right" to "Ljava/lang/String;"))),
            "An undecidable comparison is Unknown(I) rather than a crash or a constant " +
                "(DALVIK-SEMANTICS.md, Coercion and register bits).",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "I", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("unary-int-to-float-computed-float-raw", "Lunary_math_test;", "intToFloat()V", seededRegisters = mapOf(0 to (5.9f to "F"))),
            "`int-to-float` converts the register's 32 bits, and 5.9f's bit pattern is 1086115021 " +
                "(DALVIK-SEMANTICS.md, Coercion and register bits).",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "F", 1086115021.toFloat().toString())),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("unary-neg-double-narrow-raw", "Lunary_math_test;", "negDouble()V", seededRegisters = mapOf(0 to (5 to "I"))),
            "`neg-double` reads 64 bits and the high half in v1 is unknown, so the result is Unknown(D) " +
                "(DALVIK-SEMANTICS.md, Coercion and register bits).",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "D", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("spine-spin-forever", "Lspine/spine;", "spinForever()V"),
            "An unbounded backward goto cannot be fully explored, so the run reports truncation.",
            aborted = true,
        ),
        CoreRegression(
            Fixture("spine-loop-revisit-changes-register", "Lspine/spine;", "loopRevisitChangesRegister()V", setOf(0, 1)),
            "An unbounded loop cannot be fully explored, so the run reports truncation.",
            aborted = true,
        ),
        CoreRegression(
            Fixture("types-array-arms-join-to-number-array", "Ldifferential/types;", "arrayArmsJoinToNumberArray(I)Ljava/lang/Object;"),
            "Array types join by component, and Integer and Long join to Number on the superclass chain " +
                "(JLS 4.10.3; DALVIK-SEMANTICS.md, Subtype testing).",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "[Ljava/lang/Number;", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("types-int-array-is-cloneable", "Ldifferential/types;", "intArrayIsCloneable()I"),
            "Every array type implements Cloneable and java.io.Serializable (JLS 4.10.3).",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "Z", "true")),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("types-unknown-string-instance-of-string", "Ldifferential/types;", "unknownStringIsInstanceOfString(Ljava/lang/String;)I", setOf(1)),
            "`instance-of` on null is 0 (JVMS instanceof) and an unknown reference may be null, so the result is unknown.",
            registers = mapOf(1 to NormalizedValue(ValueKind.UNKNOWN, "Z", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("types-primitive-register-is-instance-of-object", "Ldifferential/types;", "primitiveRegisterIsInstanceOfObject()I"),
            "A primitive `instance-of` operand is verifier-rejected; the core reads it as null, so the result is false " +
                "(DALVIK-SEMANTICS.md, ART has two interpreter tiers).",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "Z", "false")),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("types-unknown-cast-may-throw", "Ldifferential/types;", "unknownCastMayThrow(Ljava/lang/Object;)I"),
            "`check-cast` throws ClassCastException when the reference cannot be cast ([BC] check-cast), so an " +
                "unknown Object explores both outcomes.",
            reached = setOf(4, 5),
        ),
        CoreRegression(
            Fixture("statics-literal-untouched-by-clinit", "Ldifferential/statics_reader;", "readLiteralUntouchedByClinit()I"),
            "Static initial values from the class definition apply at preparation, before `<clinit>` runs " +
                "(JVMS 5.4.2), and this `<clinit>` does not write the field.",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "I", "42")),
            escapes = emptySet(),
        ),
        CoreRegression(
            invocation("invoke-ambiguous-receiver", "ambiguousReceiver(Linvoke_dispatch_base;)I", setOf(1)),
            "Two dispatch candidates for an unknown receiver are not provable, so the result is unknown and both " +
                "control arms are explored (DALVIK-SEMANTICS.md, Dispatch on an unknown receiver).",
            reached = setOf(9, 10),
        ),
        CoreRegression(
            invocation("invoke-null-receiver", "nullReceiverUsesHandler()I", setOf(1)),
            "Invoking on a null receiver throws NullPointerException, so only the handler runs " +
                "(DALVIK-SEMANTICS.md, Order of checks).",
            reached = setOf(6, 7),
            notReached = setOf(4, 5),
        ),
        CoreRegression(
            invocation("invoke-outcome-partitions", "arrayPartitionsDoNotContaminate(I)I", setOf(2)),
            "Return state reaches only the normal continuation and pre-throw state only the handler, and both " +
                "return 10 (DALVIK-SEMANTICS.md, Handler routing).",
            registers = mapOf(2 to NormalizedValue(ValueKind.KNOWN, "I", "10")),
        ),
        CoreRegression(
            Fixture("class-init-super-before-sub", "Ldifferential/init_order_reader;", "initializeChild()I"),
            "A superclass is initialized before its subclass (JLS 12.4.2), so the child's write of 2 is last.",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "I", "2")),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("ifield-put-then-get", "Ldifferential/ifields;", "putThenGetInt()I", setOf(1)),
            "An instance field reads back the value last written to it ([BC] iget/iput).",
            registers = mapOf(1 to NormalizedValue(ValueKind.KNOWN, "I", "66")),
            escapes = emptySet(),
        ),
        CoreRegression(
            Fixture("ifield-default", "Ldifferential/ifields;", "getDefaultInt()I", setOf(1)),
            "An unwritten instance field holds its type's default value (JLS 4.12.5).",
            registers = mapOf(1 to NormalizedValue(ValueKind.KNOWN, "I", "0")),
            escapes = emptySet(),
        ),
        CoreRegression(
            invocation("invoke-missing-class", "missingClass()V"),
            "Invoking a method of an absent class raises NoClassDefFoundError at the invoke (DEVICE Fx_InvokeAbsentType).",
            registers = mapOf(0 to ABSENT),
            exactReached = setOf(0),
            escapes = setOf("java.lang.NoClassDefFoundError:Failed resolution of: Ldifferential/absent;"),
        ),
        CoreRegression(
            invocation("invoke-missing-method", "missingMethod()V"),
            "Invoking a missing method of a present class raises NoSuchMethodError at the invoke " +
                "(DEVICE Fx_InvokeMissingMethod).",
            registers = mapOf(0 to ABSENT),
            exactReached = setOf(0),
            escapes = setOf(
                "java.lang.NoSuchMethodError:No static method missing()V in class Ldifferential/invocation_target; " +
                    "or its super classes",
            ),
        ),
        CoreRegression(
            invocation("invoke-aborted-callee-fallback", "callAbortedArrayCallee()[I", setOf(3)),
            "A caller resumed past a truncated callee keeps an unknown fallback result, and the run reports truncation.",
            aborted = true,
            registers = mapOf(3 to NormalizedValue(ValueKind.UNKNOWN, "[I", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            invocation("invoke-opaque-collapse", "opaqueCollapseKeepsReference()Ldifferential/invocation_target;"),
            "A truncated callee leaves the run truncated even though the caller keeps its reference.",
            aborted = true,
        ),
        CoreRegression(
            invocation("handler-first-declared", "firstDeclaredHandlerWins()I"),
            "The first declared matching handler wins (DALVIK-SEMANTICS.md, Handler routing).",
            reached = setOf(6, 7, 8),
            notReached = setOf(9, 10),
        ),
    ).requiringUniqueIds("regressions") { it.fixture.id }
}

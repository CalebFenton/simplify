package org.cf.smalivm.differential

object Fixtures {
    private fun <T> List<T>.requiringUniqueIds(name: String, idOf: (T) -> String): List<T> {
        val duplicates = groupingBy(idOf).eachCount().filterValues { it > 1 }
        require(duplicates.isEmpty()) { "$name contains duplicate ids: $duplicates" }
        return this
    }

    val newCoreSupported: List<Fixture> = listOf(
        Fixture(id = "spine-return-five", className = "Lspine/spine;", methodDescriptor = "returnFive()I"),
        Fixture(id = "spine-return-nothing", className = "Lspine/spine;", methodDescriptor = "returnNothing()V"),
        Fixture(id = "spine-nop-then-const", className = "Lspine/spine;", methodDescriptor = "nopThenConst()I"),
        Fixture(id = "spine-goto-skip", className = "Lspine/spine;", methodDescriptor = "gotoSkip()I"),
        Fixture(id = "spine-move-chain", className = "Lspine/spine;", methodDescriptor = "moveChain()I", observeRegisters = setOf(0, 1, 2)),
        Fixture(id = "spine-return-wide", className = "Lspine/spine;", methodDescriptor = "returnWide()J"),
        Fixture(id = "spine-return-string", className = "Lspine/spine;", methodDescriptor = "returnString()Ljava/lang/String;"),

        Fixture(
            id = "spine-instance-wide-params",
            className = "Lspine/spine;",
            methodDescriptor = "instanceWideParams(JLjava/lang/String;I)I",
            observeRegisters = setOf(0, 3, 4, 5, 6, 7),
        ),

        Fixture(
            id = "branch-disagreeing-arms",
            className = "Ldifferential/branch;",
            methodDescriptor = "disagreeingArms(I)V",
            observeRegisters = setOf(1, 2),
        ),
        Fixture(id = "smoke-return-five", className = "Ldifferential/smoke;", methodDescriptor = "returnFive()I"),

        Fixture(
            id = "ratchet-compare-wide",
            className = "Ldifferential/ratchet;",
            methodDescriptor = "compareWide()I",
            observeRegisters = setOf(4),
        ),

        Fixture(
            id = "ratchet-array-round-trip",
            className = "Ldifferential/ratchet;",
            methodDescriptor = "arrayRoundTrip()I",
            observeRegisters = setOf(2),
        ),

        Fixture(
            id = "ratchet-branch-on-zero",
            className = "Ldifferential/ratchet;",
            methodDescriptor = "branchOnZero()I",
        ),

        Fixture(
            id = "branch-returns-different-constants",
            className = "Ldifferential/branch;",
            methodDescriptor = "returnsDifferentConstants(I)I",
        ),

        Fixture(
            id = "branch-known-falls-through",
            className = "Ldifferential/branch;",
            methodDescriptor = "knownBranchFallsThrough()I",
        ),

        Fixture(
            id = "switch-packed-known-match",
            className = "Ldifferential/switch;",
            methodDescriptor = "packedKnownMatch()I",
        ),
        Fixture(
            id = "switch-packed-known-no-match",
            className = "Ldifferential/switch;",
            methodDescriptor = "packedKnownNoMatch()I",
        ),

        Fixture(
            id = "switch-sparse-known-match",
            className = "Ldifferential/switch;",
            methodDescriptor = "sparseKnownMatch()I",
        ),

        Fixture(
            id = "switch-unknown-selector-forks",
            className = "Ldifferential/switch;",
            methodDescriptor = "unknownSelectorEveryArmAgrees(I)I",
        ),

        Fixture(
            id = "types-array-arms-join-covariantly",
            className = "Ldifferential/types;",
            methodDescriptor = "arrayArmsJoinCovariantly(I)Ljava/lang/Object;",
            observeRegisters = setOf(0),
        ),

        Fixture(
            id = "types-cast-string-to-object",
            className = "Ldifferential/types;",
            methodDescriptor = "castStringToObject()Ljava/lang/Object;",
            observeRegisters = setOf(0),
        ),

        Fixture(
            id = "types-const-class-object",
            className = "Ldifferential/types;",
            methodDescriptor = "constClassObject()Ljava/lang/Class;",
            observeRegisters = setOf(0),
        ),

        Fixture(
            id = "types-known-instance-of-object",
            className = "Ldifferential/types;",
            methodDescriptor = "knownStringIsInstanceOfObject()I",
            observeRegisters = setOf(0),
        ),

        Fixture(
            id = "smoke-nested-array",
            className = "Ldifferential/smoke;",
            methodDescriptor = "nestedArray()[[I",
            observeRegisters = setOf(0),
        ),

        Fixture(
            id = "smoke-self-referential-array",
            className = "Ldifferential/smoke;",
            methodDescriptor = "selfReferentialArray()[Ljava/lang/Object;",
            observeRegisters = setOf(0),
        ),

        Fixture(
            id = "smoke-shared-inner-array",
            className = "Ldifferential/smoke;",
            methodDescriptor = "sharedInnerArray()[[I",
            observeRegisters = setOf(0),
        ),

        Fixture(
            id = "smoke-null-element-from-fresh-array",
            className = "Ldifferential/smoke;",
            methodDescriptor = "nullElementFromFreshArray()Ljava/lang/Object;",
            observeRegisters = setOf(2),
        ),

        Fixture(
            id = "smoke-fill-array-data-round-trip",
            className = "Ldifferential/smoke;",
            methodDescriptor = "fillArrayDataRoundTrip()[I",
            observeRegisters = setOf(0),
        ),

        Fixture(
            id = "unary-int-to-long",
            className = "Lunary_math_test;",
            methodDescriptor = "intToLong()V",
            seededRegisters = mapOf(0 to (11 to "I")),
        ),

        Fixture(
            id = "unary-neg-double",
            className = "Lunary_math_test;",
            methodDescriptor = "negDouble()V",
            seededRegisters = mapOf(0 to (15.1 to "D")),
        ),

        Fixture(
            id = "binary-add-int",
            className = "Lbinary_math_test;",
            methodDescriptor = "addInt()V",
            observeRegisters = setOf(0, 1),
            seededRegisters = mapOf(0 to (-3 to "I"), 1 to (7 to "I")),
        ),
        Fixture(
            id = "binary-add-int-2addr",
            className = "Lbinary_math_test;",
            methodDescriptor = "addInt2Addr()V",
            seededRegisters = mapOf(0 to (-3 to "I"), 1 to (7 to "I")),
        ),

        Fixture(
            id = "binary-add-int-lit8",
            className = "Lbinary_math_test;",
            methodDescriptor = "addIntLit8()V",
            seededRegisters = mapOf(0 to (-3 to "I")),
        ),

        Fixture(
            id = "binary-rsub-int",
            className = "Lbinary_math_test;",
            methodDescriptor = "RSubInt()V",
            seededRegisters = mapOf(0 to (10 to "I")),
        ),

        Fixture(
            id = "binary-shl-long",
            className = "Lbinary_math_test;",
            methodDescriptor = "shlLong()V",
            observeRegisters = setOf(0, 2),
            seededRegisters = mapOf(0 to (1L to "J"), 2 to (33 to "I")),
        ),
        Fixture(
            id = "binary-div-double",
            className = "Lbinary_math_test;",
            methodDescriptor = "divDouble()V",
            seededRegisters = mapOf(0 to (10.0 to "D"), 2 to (4.0 to "D")),
        ),

        Fixture(
            id = "binary-div-double-by-zero",
            className = "Lbinary_math_test;",
            methodDescriptor = "divDouble()V",
            seededRegisters = mapOf(0 to (10.0 to "D"), 2 to (0.0 to "D")),
        ),

        Fixture(
            id = "binary-div-int-by-zero-caught",
            className = "Lbinary_math_test;",
            methodDescriptor = "divIntWithCatch()V",
            seededRegisters = mapOf(0 to (10 to "I"), 1 to (0 to "I")),
        ),

        Fixture(
            id = "cmp-long-greater",
            className = "Lcmp_test;",
            methodDescriptor = "cmpLong()V",
            seededRegisters = mapOf(0 to (7L to "J"), 2 to (5L to "J")),
        ),

        Fixture(
            id = "cmp-float-nan-cmpg",
            className = "Lcmp_test;",
            methodDescriptor = "cmpgFloat()V",
            observeRegisters = setOf(0, 1),
            seededRegisters = mapOf(0 to (Float.NaN to "F"), 1 to (1f to "F")),
        ),
        Fixture(
            id = "cmp-float-nan-cmpl",
            className = "Lcmp_test;",
            methodDescriptor = "cmplFloat()V",
            seededRegisters = mapOf(0 to (Float.NaN to "F"), 1 to (1f to "F")),
        ),

        Fixture(
            id = "spine-add-int",
            className = "Lspine/spine;",
            methodDescriptor = "unsupportedOpcode()I",
            observeRegisters = setOf(0, 1),
        ),

        Fixture(
            id = "new-instance-local",
            className = "Lnew_instance_test;",
            methodDescriptor = "newLocalInstance()V",
        ),

        Fixture(
            id = "new-instance-framework-class",
            className = "Lnew_instance_test;",
            methodDescriptor = "newNonLocalInstance()V",
        ),

        Fixture(
            id = "new-instance-two-allocations",
            className = "Lnew_instance_test;",
            methodDescriptor = "newTwoLocalInstances()V",
            observeRegisters = setOf(0, 1),
        ),

        Fixture(
            id = "new-instance-triggers-class-init",
            className = "Lnew_instance_test;",
            methodDescriptor = "newWitnessInstance()V",
        ),

        Fixture(
            id = "ratchet-invoke-static",
            className = "Ldifferential/ratchet;",
            methodDescriptor = "invokeStatic()I",
        ),
        Fixture(
            id = "ratchet-new-instance",
            className = "Ldifferential/ratchet;",
            methodDescriptor = "newInstance()Ljava/lang/Object;",
        ),
        Fixture(
            id = "smoke-new-object",
            className = "Ldifferential/smoke;",
            methodDescriptor = "newObject()Ljava/lang/Object;",
        ),
        Fixture(
            id = "ratchet-alias-unrelated-allocation",
            className = "Ldifferential/ratchet;",
            methodDescriptor = "initializeOneOfTwoAllocations()V",
            observeRegisters = setOf(0, 1),
        ),

        Fixture(
            id = "ratchet-static-field-round-trip",
            className = "Ldifferential/ratchet;",
            methodDescriptor = "staticFieldRoundTrip()I",
        ),

        Fixture(
            id = "sget-literal-no-clinit",
            className = "Lsget_test;",
            methodDescriptor = "getStaticIntLiteral()V",
        ),

        Fixture(
            id = "statics-clinit-assigned-field",
            className = "Ldifferential/statics_reader;",
            methodDescriptor = "readClinitAssigned()I",
        ),

        Fixture("invoke-static-resolved", "Ldifferential/invocation;", "callStatic()I", setOf(0)),
        Fixture("invoke-direct-resolved", "Ldifferential/invocation;", "callDirect()I", setOf(1)),
        Fixture("invoke-virtual-resolved", "Ldifferential/invocation;", "callVirtual()I", setOf(1)),
        Fixture("invoke-interface-resolved", "Ldifferential/invocation;", "callInterface()I", setOf(1)),

        Fixture("invoke-handled-throw", "Ldifferential/invocation;", "callHandledThrow()I", setOf(0)),
        Fixture(
            "invoke-handled-partition-agreement",
            "Ldifferential/invocation;",
            "handledPartitionIsNotContaminated(I)I",
            setOf(0),
        ),

        Fixture(
            "class-init-parent-only",
            "Ldifferential/init_order_reader;",
            "initializeParentOnly()I",
            setOf(0),
        ),
    ).requiringUniqueIds("newCoreSupported") { it.id }

    val invocationCases: List<Fixture> = listOf(
        Fixture("invoke-ambiguous-receiver", "Ldifferential/invocation;", "ambiguousReceiver(Linvoke_dispatch_base;)I", setOf(1)),
        Fixture("invoke-null-receiver", "Ldifferential/invocation;", "nullReceiverUsesHandler()I", setOf(1)),
        Fixture("invoke-outcome-partitions", "Ldifferential/invocation;", "arrayPartitionsDoNotContaminate(I)I", setOf(2)),
        Fixture("class-init-super-before-sub", "Ldifferential/init_order_reader;", "initializeChild()I"),
        Fixture("ifield-put-then-get", "Ldifferential/ifields;", "putThenGetInt()I", setOf(1)),
        Fixture("ifield-default", "Ldifferential/ifields;", "getDefaultInt()I", setOf(1)),
        Fixture("invoke-missing-class", "Ldifferential/invocation;", "missingClass()V"),
        Fixture("invoke-missing-method", "Ldifferential/invocation;", "missingMethod()V"),
        Fixture("invoke-opaque-collapse", "Ldifferential/invocation;", "opaqueCollapseKeepsReference()Ldifferential/invocation_target;"),
        Fixture("invoke-entrypoint-truncation", "Ldifferential/invocation;", "entryPointTruncatesAfterCall(I)I", setOf(0)),
        Fixture("handler-first-declared", "Ldifferential/invocation;", "firstDeclaredHandlerWins()I"),
    ).requiringUniqueIds("invocationCases") { it.id }

    val regressions: List<CoreRegression> = listOf(
        CoreRegression(
            fixture = Fixture(
                id = "spine-throw-caught",
                className = "Lspine/spine;",
                methodDescriptor = "throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;",
            ),
            basis = "Dalvik routes a throw of an unknown-but-RuntimeException value to the declared " +
                "handler; the core forks the feasible handler and the escape instead of guessing",
        ),
        CoreRegression(
            fixture = Fixture(
                id = "spine-throw-uncaught",
                className = "Lspine/spine;",
                methodDescriptor = "throwUncaught(Ljava/lang/RuntimeException;)V",
            ),
            basis = "a throw with no handler in this method leaves the method; the core records " +
                "Outcome.Threw rather than crashing the host",
        ),
        CoreRegression(
            fixture = Fixture(
                id = "spine-throw-two-handlers",
                className = "Lspine/spine;",
                methodDescriptor = "throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;",
            ),
            basis = "an unknown thrown type makes EVERY declared handler feasible plus the escape, " +
                "and forking all of them is the reason this VM exists (spec §7); the oracle forks none",
        ),
        CoreRegression(
            fixture = Fixture(
                id = "spine-throw-catch-all",
                className = "Lspine/spine;",
                methodDescriptor = "throwCatchAll(Ljava/lang/Object;)Ljava/lang/Object;",
            ),
            basis = "a covering catch-all guarantees the exception is handled in this method, so the " +
                "escape is NOT feasible even for an unknown type (HandlerResolution's candidateHandlers)",
        ),
        CoreRegression(
            fixture = Fixture(
                id = "spine-throw-null-const",
                className = "Lspine/spine;",
                methodDescriptor = "throwNullConst()Ljava/lang/Object;",
            ),
            basis = "the core SYNTHESIZES that NPE (Plan 7 Task 12) and resolves the declared handler " +
                "DEFINITELY: `const/4 v0, 0x0` is Dalvik's null idiom ([BC]: `(Object) null == (int) 0`), " +
                "so a fresh NullPointerException -- a RuntimeException -- is the single correct outcome " +
                "([JVMS] 6.5 athrow), not one branch of a fork. Exactly the RuntimeException clause runs, " +
                "nothing escapes, and the value bound into `move-exception` is the NPE rather than the int. " +
                "The DIVERGENCE is unchanged and still ORACLE_RAISES: the oracle dies at opcode/ThrowOp.kt:20 " +
                "before any of that, casting the known Integer to Throwable",
        ),
        CoreRegression(
            fixture = Fixture(
                id = "spine-throw-unloadable-catch-type",
                className = "Lspine/spine;",
                methodDescriptor = "throwUnloadableCatchType(Ljava/lang/RuntimeException;)Ljava/lang/Object;",
            ),
            basis = "a well-formed but unloadable catch type is normal in an obfuscated APK: the core " +
                "joins it into the ambiguous fork set and still reports Complete (spec §7)",
        ),
        CoreRegression(
            fixture = Fixture(
                id = "spine-falls-off-the-end",
                className = "Lspine/spine;",
                methodDescriptor = "fallsOffTheEnd()V",
            ),
            basis = "Dalvik would reject the method, but obfuscators emit malformed tails, so the " +
                "scheduler must CONTAIN it as a bounded abort rather than crash the host",
            aborted = true,
        ),
        CoreRegression(
            fixture = Fixture(
                id = "branch-move-from-untouched-register",
                className = "Ldifferential/branch;",
                methodDescriptor = "moveFromAnUntouchedRegister()V",
                observeRegisters = setOf(0, 1),
            ),
            basis = "Dalvik's verifier rejects reading an uninitialized register, so this is " +
                "verifier-invalid input -- but spec §2.2 requires a total answer for it, and there IS " +
                "one: the register's 32 bits are unconstrained, which is exactly Value.Top. The core " +
                "reports Complete with a frame-held Top (ValueKind.CONFLICTED at v0, ABSENT at v1), " +
                "which is also the only fixture in this registry where the two meanings of Top are " +
                "observably different on ONE method",
            registers = mapOf(0 to NormalizedValue(ValueKind.CONFLICTED, null, null), 1 to NormalizedValue(ValueKind.ABSENT, null, null)),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "if-lt-two-distinct-references",
                className = "Lif_test;",
                methodDescriptor = "branchOnOrderingOverTwoDistinctReferences()V",
            ),
            basis = "an ordering question over two pointers is unprovable: each is provably non-zero and " +
                "neither is a particular Int, so both arms are feasible and both must be explored -- more " +
                "forks, never fewer (spec §2.2). VERIFIER-tier ([ART] method_verifier.cc:3338-3350 makes " +
                "`if-lt` integral-only), so this is the engine's choice rather than a fidelity target, and " +
                "the choice is recorded in DALVIK-SEMANTICS.md's `Choices this plan makes` table. May " +
                "never be reconciled by making the core fork less",
            reached = setOf(9),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "if-eq-reference-against-nonzero-int",
                className = "Lif_test;",
                methodDescriptor = "branchOnEqualityBetweenAReferenceAndANonZeroInt()V",
            ),
            basis = "a pointer is provably non-ZERO, which decides a comparison against 0 and nothing " +
                "else -- it is not provably different from 5, so both arms are feasible (spec §2.2). Kept " +
                "as a SEPARATE entry from the ordering one because the two rows fail differently: this is " +
                "the mixed-operand row, and folding them into one fixture would let either be reconciled " +
                "while the other kept the test green. Note the ZERO case is NOT this row -- `if-nez vRef` " +
                "is definite, and core/op/IfOpTest.testIfObjectNotEqualZero pins it",
            reached = setOf(8),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "switch-sparse-descending-keys",
                className = "Ldifferential/switch;",
                methodDescriptor = "sparseDescendingKeys()I",
            ),
            basis = "the condition is VERIFIER-tier -- [CON] A8 requires the table `sorted low-to-high` and " +
                "[ART] is stricter still, rejecting `key <= last_key` in CheckAndMarkSwitchTargets " +
                "(method_verifier.cc:2315) -- so no verified input reaches it and there is no runtime " +
                "behaviour to match. Deliberately NOT adjudicated as `the binary search is the only way to " +
                "agree with the runtime`: that overstates one runtime's implementation at [ART] tier, and a " +
                "different runtime could linear-scan. Spec 2.2 decides instead: WHICH dispatch a runtime " +
                "uses is an unprovable question about feasibility, so the core explores the union of the " +
                "search's answer and the map's -- more forks, never fewer. On a strictly ascending table " +
                "the two answers are identical and the union collapses to one successor, which " +
                "`switch-sparse-known-match` in newCoreSupported is the control for. May never be " +
                "reconciled by making the core take either dispatch alone",
            reached = setOf(4, 5),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "switch-float-selector-read-by-value",
                className = "Ldifferential/switch;",
                methodDescriptor = "floatSelectorReadByValue()I",

                seededRegisters = mapOf(0 to (2.9f to "F")),
            ),
            basis = "the register holds 32 bits and a switch compares them, exactly as an `if` does " +
                "(DALVIK-SEMANTICS.md's `packed-switch / sparse-switch` and `if-*` sections): the numeric " +
                "value of a host Float box is not those bits. The core answers `Unprovable` through the ONE " +
                "shared `provableInt` -- so this is Plan 8 Task 3's rule at a second op family, not a " +
                "switch-specific one -- and an unprovable successor set explores MORE (spec 2.2). Note the " +
                "core's fork DOES include arms a device cannot take, which is the labelled precision loss " +
                "the `Coerce` design reversal priced and declined for the integral readers: the bits ARE " +
                "determinable via floatToRawIntBits, and folding them is `Coerce`'s decision to change, not " +
                "this op's. May not be reconciled by making the core read the box's value: that restores a " +
                "confidently wrong dispatch",
            reached = setOf(4, 5, 6, 7, 10, 11),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "switch-null-selector-against-key-zero",
                className = "Ldifferential/switch;",
                methodDescriptor = "nullSelectorAgainstKeyZero()I",

                seededRegisters = mapOf(0 to (null to "Ljava/lang/String;")),
            ),
            basis = "[BC] `General design`, verbatim: `In terms of bitwise representation, (Object) null == " +
                "(int) 0.` A switch reads its selector as 32 bits, so a definite null's bits are provably 0 " +
                "and case 0 is the ONLY feasible arm -- both halves of the oracle's answer are wrong, in " +
                "opposite directions, from one row. The core's answer comes from `provableInt`, the same " +
                "one implementation `if-eqz` uses, so this is not a switch-specific rule. Note the oracle " +
                "agrees BY ACCIDENT whenever the table has no key 0, which is why both migrated " +
                "`swich_test.smali` fixtures hide it and this needed a table starting at 0. Neither " +
                "direction may be reconciled: making the core fall through loses a feasible path on a " +
                "Complete run, and making it fork both arms invents an infeasible one",
            reached = setOf(6, 7),
            notReached = setOf(4, 5),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "binary-rem-int-unknown-divisor",
                className = "Lbinary_math_test;",
                methodDescriptor = "remIntUnknownDivisorWithCatch(I)V",
                seededRegisters = mapOf(0 to (12 to "I")), 
            ),
            basis = "Dalvik throws ArithmeticException for `rem-int`/`rem-long` by zero exactly as for " +
                "`div`, so with an unknown divisor both outcomes are feasible and both must be explored " +
                "-- more forks, never fewer (spec §2.2). An unexplored feasible path on a run reporting " +
                "Complete is the worst outcome this engine can have, so this entry may never be " +
                "reconciled by making the core fork less",
            reached = setOf(3, 4),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "binary-div-int-unknown-dividend",
                className = "Lbinary_math_test;",
                methodDescriptor = "divIntUnknownDividendWithCatch(I)V",
                seededRegisters = mapOf(0 to (7 to "I")), 
            ),
            basis = "`div-int` throws only on a zero divisor: integer overflow does not throw in Dalvik, " +
                "`Integer.MIN_VALUE / -1` simply yields Integer.MIN_VALUE. So a known non-zero divisor " +
                "makes the exceptional path INFEASIBLE whatever the dividend is, and exploring it tells " +
                "a deobfuscator a handler is reachable when it is not (corroborated independently by " +
                "this repo's own audit at docs/architecture/exceptions.md). Soundness has a direction " +
                "but it is not 'always fork': inventing a throw is its own wrong answer, so this entry " +
                "may never be reconciled by making the core fork more",
            notReached = setOf(3, 4),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "new-array-unloadable-component",
                className = "Lnew_array_test;",
                methodDescriptor = "createNonExistentArrayClass()V",

                seededRegisters = mapOf(0 to (1 to "I")),
            ),
            basis = "INHERITED -- [JVMS] 6.5 anewarray: \"During resolution of the symbolic reference to " +
                "the class, array, or interface type, any of the exceptions documented in 5.4.3.1 can be " +
                "thrown\", and 5.4.3.1's resolution failures are NoClassDefFoundError and friends, all " +
                "Errors. [BC]'s entire text for new-array names no exception at all (see " +
                "docs/DALVIK-SEMANTICS.md#new-array-op-23-22c), so this is inherited rather than something " +
                "\"Dalvik throws\". It does NOT fork: spec 3 makes multidex a non-goal, so the loaded class " +
                "set is the whole world and the class's absence is a FACT -- unlike Plan 3's unloadable " +
                "CATCH type, which does fork because there the thrown value's own type was unknown. " +
                "Registered as an ORACLE DEFECT: the migrated NewArrayOpTest.nonExistentClassNameThrowsException " +
                "expectation changed type deliberately, and it now needs testThrows rather than testCaught " +
                "because an Error escapes a catch(Exception)",
            escapes = setOf("java.lang.NoClassDefFoundError:does.not.exist"),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "new-array-both-bad-caught",
                className = "Lnew_array_test;",
                methodDescriptor = "createBothBadArrayClass()V",
                observeRegisters = setOf(0),

                seededRegisters = mapOf(0 to (-1 to "I")),
            ),
            basis = "[ART] checks the array size before resolving the component type (INHERITED via " +
                "docs/DALVIK-SEMANTICS.md; pinned core-only by " +
                "NewArrayOpTest.aNegativeLengthBeatsAnAbsentComponentClassBecauseArtChecksTheSizeFirst), so a " +
                "-1 length raises NegativeArraySizeException whatever the component -- the type a device " +
                "produces. Unlike new-array-unloadable-component, whose Error escapes a catch(Exception) and " +
                "so uses coreThrowsUncaught, both handlers catch here, so the divergence is purely the bound " +
                "type and is recorded in coreCorrectsCaughtType. May never be reconciled toward the oracle: " +
                "ClassNotFoundException is the host loader's exception leaked into guest semantics",
            caughtTypes = mapOf(0 to "Ljava/lang/NegativeArraySizeException;"),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "smoke-aput-unknown-value",
                className = "Ldifferential/smoke;",
                methodDescriptor = "aputUnknownValue(I)[I",
            ),
            basis = "spec §2.2 says `Unknown` is the answer when a value cannot be PROVEN -- and here ONE " +
                "element cannot while the other can, so `Unknown([I)` claims less than the engine knows. " +
                "The core's answer is strictly LOWER in the lattice and invents nothing: every slot it " +
                "still calls known was written by an instruction whose operands were all known " +
                "([JVMS] §6.5 `iastore` stores one component, not the array). Sound AND sharper, so this " +
                "entry may never be reconciled by making the core coarser -- that would be trading " +
                "precision for nothing, which is the one direction this registry forbids outright",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "[I", "[?, 0]")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "smoke-aput-unknown-value-with-catch",
                className = "Ldifferential/smoke;",
                methodDescriptor = "aputUnknownValueWithCatch(I)V",
                observeRegisters = setOf(1),
            ),
            basis = "an unknown STORED VALUE cannot make a provably non-null array null nor a provable " +
                "in-range index out of range -- neither the NPE nor the AIOOBE is feasible, so scheduling " +
                "the handler tells a deobfuscator a `catch` block is reachable when it is not. That is the " +
                "over-approximating direction of spec §2.2, the mirror of `binary-div-int-unknown-dividend` " +
                "one op family over, and this entry may never be reconciled by making the core fork MORE. " +
                "The core's own side of the claim (it forks NEITHER) is registry-guarded via " +
                "`smoke-aput-unknown-value`'s `coreEscapes = emptySet()`",
            notReached = setOf(7, 8),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "smoke-aput-unknown-index",
                className = "Ldifferential/smoke;",
                methodDescriptor = "aputUnknownIndex(I)[I",
            ),
            basis = "an unprovable index means the write landed on EXACTLY ONE slot and we cannot say " +
                "which, so every slot afterwards is either its old value or the stored one -- which is " +
                "precisely what `join` states. `join(Known(0,I), Known(7,I))` is `Unknown(I)`, so this " +
                "fixture renders `[?, ?]` and reads only slightly better than the oracle's whole-array " +
                "`Unknown`; the point is that it is a JOIN rather than a blanket discard, and " +
                "`join(Known(7,I), Known(7,I))` is `Known(7,I)`, so an array whose old value already " +
                "equals the stored one stays FULLY KNOWN (that half is pinned by name in " +
                "`core/op/APutOpTest.anUnknownIndexJoinsEveryElementWithTheStoredValue`, because a " +
                "differential fixture built on a fresh array cannot show it). Same soundness argument as " +
                "the value case: strictly lower in the lattice, nothing invented (spec §2.2)",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "[I", "[?, ?]")),
            escapes = setOf("java.lang.ArrayIndexOutOfBoundsException:null"),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "smoke-filled-new-array-unknown-element",
                className = "Ldifferential/smoke;",
                methodDescriptor = "filledNewArrayUnknownElement(I)[I",
                observeRegisters = setOf(1),
            ),
            basis = "the CLEANEST of the three, and worth having for that reason alone: `filled-new-array` " +
                "has no array, index or count operand -- it CREATES the array and its count is the " +
                "instruction's own unsigned field -- so NPE, AIOOBE and NegativeArraySizeException are all " +
                "unreachable and NEITHER engine forks anything here. The divergence is therefore purely " +
                "the value, with identical addresses and nothing unhandled on either side, which is what " +
                "this shape claims and the two `aput` entries can only claim about their addresses. " +
                "[BC] permits a reference component (*\"reference types are acceptable\"*), so an element " +
                "is a lattice point like any other; spec §2.2 again makes the lower point the right answer",
            registers = mapOf(1 to NormalizedValue(ValueKind.KNOWN, "[I", "[1, ?]")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "smoke-filled-new-array-reference-component",
                className = "Ldifferential/smoke;",
                methodDescriptor = "filledNewArrayOfStringsWithNullElement()V",
                observeRegisters = setOf(2),
            ),
            basis = "[BC]: the contents *\"must be single-word (that is, no arrays of long or double, but " +
                "**reference types are acceptable**)\"*, so `[Ljava/lang/String;` is legal and its elements " +
                "are references rather than ints. What each element IS comes from [ART]: `DoFilledNewArray`'s " +
                "non-`int` branch fills with `shadow_frame.GetVRegReference(src_reg)` " +
                "(`interpreter_common.cc:1463-1464`, fetched raw and counted with awk), which is nullptr for " +
                "a register whose last write was primitive -- so `const/4 v1, 0x0` contributes `null`, not a " +
                "boxed 0, and `new String[]{\"x\", null}` renders `[x, null]`. STRENGTHENED 2026-08-04: that " +
                "is the SWITCH interpreter, and nterp -- the default tier, which routes this opcode through " +
                "its own `DoFilledNewArray` (`mterp/nterp.cc:751-752`, " +
                "`reinterpret_cast<mirror::Object*>(regs[src_reg])`) -- agrees, because the cast of a ZERO raw " +
                "is also nullptr. Both tiers, on verifier-VALID input; only the NON-zero primitive row " +
                "diverges between them (see `Value.readsAsNullReference`'s KDoc). That null element is the " +
                "whole-branch review's C1: it is verifier-VALID input on which the engine previously answered " +
                "a wrong CONFIDENT constant (spec §2.2's cardinal prohibition) that `Value.agreesWith` also " +
                "rejected against `aput-object`'s spelling of the same null, so a join over the two ops lost " +
                "the fold. Pinned by name in " +
                "`NewCoreDifferentialTest.aReferenceComponentFilledNewArraySpellsItsNullElementAsDalvikReadsIt` " +
                "because this shape's branch compares ValueKind rather than reprs",
            registers = mapOf(2 to NormalizedValue(ValueKind.KNOWN, "[Ljava/lang/String;", "[x, null]")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "binary-add-int-uncoercible-operand",
                className = "Lbinary_math_test;",
                methodDescriptor = "addInt()V",
                seededRegisters = mapOf(0 to ("not a number" to "Ljava/lang/String;"), 1 to (7 to "I")),
            ),
            basis = "a non-numeric operand in an arithmetic op is malformed input -- expected input for " +
                "a deobfuscator -- so the sound answer is Value.Unknown of the result type: it keeps " +
                "the path exploring rather than truncating the run, and never invents a constant",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "I", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "cmp-float-uncoercible-operand",
                className = "Lcmp_test;",
                methodDescriptor = "cmplFloat()V",
                seededRegisters = mapOf(
                    0 to ("left" to "Ljava/lang/String;"),
                    1 to ("right" to "Ljava/lang/String;"),
                ),
            ),
            basis = "an undecidable comparison is Value.Unknown(I), not a crash and not a guessed 0: " +
                "`cmp*` feeds a branch, and a fabricated 0 would make the optimizer delete the wrong " +
                "arm. Same adjudication as the arithmetic case above, one op family over",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "I", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "unary-int-to-float-computed-float-raw",
                className = "Lunary_math_test;",
                methodDescriptor = "intToFloat()V",

                seededRegisters = mapOf(0 to (5.9f to "F")),
            ),
            basis = "the register's bits and the host box's numeric value are different numbers for an " +
                "IEEE-754 raw, so the oracle's Known(5.0f) is a constant no runtime produces and must not " +
                "be matched. spec §2.2 makes an unfolded value Value.Unknown, and that is what the core " +
                "answers. " +
                "REVISED IN ROUND 1, and the revision matters because the earlier text overclaimed: this " +
                "is the SAFE answer, not the only available one. The bit-exact answer IS available -- " +
                "Float.floatToRawIntBits(5.9f) is exactly the 1086115021 a device reads -- so the core is " +
                "declining a fold it could make, which is a precision loss rather than an impossibility. " +
                "Two premises that used to justify the impossibility were measured false: `no bit view of " +
                "a register` (a pure JDK call on a raw the engine holds) and `it would reopen all 156 " +
                "migrated arithmetic expectations` (measured: ZERO move). Coerce.toFloat/toDouble were " +
                "converted to the bit-exact read in round 1 for exactly that reason; Coerce.toInt was not, " +
                "and Coerce.toInt's KDoc carries the price and the one measured blocker (shl-long's " +
                "narrow distance operand, plus the fact that 8 of the 13 tests it moves predate Task 3 and " +
                "five are Plan 7's array pins, so taking it reverses Plan 7's fix too). If that blocker is " +
                "cleared this entry becomes a value CORRECTION rather than a refusal and must be re-shaped; " +
                "this shape keeps its witness either way, because unary-neg-double-narrow-raw (fix round 2) " +
                "is a FORCED refusal that no such change touches. " +
                "The ONE read of the fourteen where the refusal is a PATH question is " +
                "BinaryMathOp.knownDivisor, where it ADDS a transition -- unambiguously sound. " +
                "Core-side pins: " +
                "`core/op/UnaryMathOpTest.anIntToFloatReadingAComputedFloatRawIsNotProvableRatherThanTruncatedByValue` " +
                "plus one per affected family in BinaryMathOpTest, CmpOpTest and CoerceTest",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "F", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "unary-neg-double-narrow-raw",
                className = "Lunary_math_test;",
                methodDescriptor = "negDouble()V",

                seededRegisters = mapOf(0 to (5 to "I")),
            ),
            basis = "this is the one refusal in Task 3's whole area that is NOT a precision loss: the " +
                "engine does not merely decline to compute the value, it does not hold the bits. 32 of the " +
                "64 are in `register + 1`, `Coerce.toDouble` is handed only v0's raw, and " +
                "`neg-double v0, v0` does not even declare v1 in registersRead -- so answering any number " +
                "invents bits, which is what the oracle's -5.0 does. spec §2.2 makes an undeterminable " +
                "value Value.Unknown. Contrast unary-int-to-float-computed-float-raw, where the bits ARE " +
                "available and the core's Unknown is a labelled precision loss; the two entries share this " +
                "shape for the same OBSERVABLE and differ in whether the refusal is forced, which is why " +
                "each one's basis carries its own argument. Core-side pin: " +
                "`core/op/UnaryMathOpTest.aNegDoubleReadingANarrowRawIsNotProvableBecauseTheHighHalfIsAnotherRegister`",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "D", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(id = "spine-spin-forever", className = "Lspine/spine;", methodDescriptor = "spinForever()V"),
            basis = "an unbounded backward goto cannot be fully explored, so the run IS partial and a " +
                "consumer must be able to tell (spec §6.4)",
            aborted = true,
        ),
        CoreRegression(
            fixture = Fixture(
                id = "spine-loop-revisit-changes-register",
                className = "Lspine/spine;",
                methodDescriptor = "loopRevisitChangesRegister()V",
                observeRegisters = setOf(0, 1),
            ),
            basis = "same as spine-spin-forever: an unbounded loop's exploration is partial by " +
                "construction",
            aborted = true,
        ),
        CoreRegression(
            fixture = Fixture(
                id = "types-array-arms-join-tighter-than-the-oracle",
                className = "Ldifferential/types;",
                methodDescriptor = "arrayArmsJoinTighterThanTheOracle(I)Ljava/lang/Object;",
                observeRegisters = setOf(0),
            ),
            basis = "JLS §4.10.3, quoted at `docs/DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`: " +
                "`S[] >1 T[]` iff `S >1 T`, so the array join is the component join re-wrapped, and " +
                "`Integer ⊔ Long` is `Ljava/lang/Number;` on the superclass chain. `[Ljava/lang/Number;` is " +
                "a supertype of both operands, so this is a true upper bound and the sound direction is " +
                "satisfied; it is simply the tightest one. The core deliberately excludes interfaces from " +
                "the join (spec §3: full interface lattices are not a deobfuscation requirement) and walks " +
                "a superclass chain, which is a TOTAL ORDER -- the determinism `getConsensusType` cannot " +
                "offer, since `VirtualClass.getImmediateAncestors()` builds a `HashSet`.",
            registers = mapOf(0 to NormalizedValue(ValueKind.UNKNOWN, "[Ljava/lang/Number;", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(id = "types-int-array-is-cloneable", className = "Ldifferential/types;", methodDescriptor = "intArrayIsCloneable()I"),
            basis = "INHERITED: JLS §4.10.3 lists exactly `Cloneable` and `java.io.Serializable` as the " +
                "interfaces arrays implement, and lists the row for PRIMITIVE components explicitly; AOSP " +
                "`Merger.isPossiblyAssignableFrom` says the same for Android's own toolchain. Both of the " +
                "oracle's wrong answers are foldable `Known`s and both directions of spec §2.2 are " +
                "reachable from this one omission: `instance-of` folds a confidently wrong constant, and " +
                "`check-cast v0, Ljava/lang/Cloneable;` on an `int[]` invents a DEFINITE " +
                "`ClassCastException`, telling a deobfuscator a handler is live when it is dead. It was " +
                "Task 8's `instance-of` ratchet before it became this task's re-adjudication.",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "Z", "true")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "types-unknown-string-instance-of-string",
                className = "Ldifferential/types;",
                methodDescriptor = "unknownStringIsInstanceOfString(Ljava/lang/String;)I",
                observeRegisters = setOf(1),
            ),
            basis = "INHERITED, [JVMS] `instanceof`: \"If objectref is null, the instanceof instruction " +
                "pushes an int result of 0 as an int onto the operand stack.\" A `Value.Unknown` MAY be " +
                "null -- `core/Value.kt` says so and `Frame.entryFrame` seeds every reference parameter " +
                "that way -- so `1` is a constant that every null argument contradicts, and it is what the " +
                "next `if-nez` reads. The refusal is genuinely forced rather than a precision choice: the " +
                "register holds null OR an instance, and those give OPPOSITE answers, so no single value is " +
                "provable. The mirror direction is guarded separately -- a KNOWN reference still folds " +
                "(`core/op/InstanceOfOpTest.aKnownReferenceStillFolds`), so this must never be widened into " +
                "\"a type test never folds\". Note also that `check-cast` on the SAME value provably " +
                "SUCCEEDS (plan design decision 5): for a cast, null and a satisfying declared type agree, " +
                "which is why the audit files no equivalent defect against `CheckCastOp`.",
            registers = mapOf(1 to NormalizedValue(ValueKind.UNKNOWN, "Z", null)),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "types-primitive-register-is-instance-of-object",
                className = "Ldifferential/types;",
                methodDescriptor = "primitiveRegisterIsInstanceOfObject()I",
            ),
            basis = "VERIFIER -> the core's design choice under spec §2.2, and NOT [ART] -- re-tiered " +
                "2026-08-04 after ART's two interpreter tiers were found to disagree on exactly this " +
                "value. `Known(5, \"I\")` is a NON-ZERO primitive: ART's SWITCH interpreter " +
                "(`-Xint`) fetches `INSTANCE_OF`'s operand with `GetVRegReference(B())` " +
                "(`runtime/interpreter/interpreter_switch_impl-inl.h:847`) off `ShadowFrame`'s parallel " +
                "reference array, which every primitive write CLEARS (`shadow_frame.h:401-408`, and each of " +
                "the four `SetVReg*` ends with `References()[i].Clear()`), so it reads `nullptr` and " +
                "`SetVReg(A(), 0)` stores 0 -- but `nterp`, the DEFAULT interpreter on a modern device, " +
                "reads the RAW dex register (`mterp/x86_64ng/object.S:86-107`, `GET_VREG` then " +
                "`testl`/`je`), finds 5 non-zero, and dereferences it as an object pointer instead. Neither " +
                "tier folds `true`, and `runtime/verifier/method_verifier.cc:3156-3158` hard-fails a " +
                "non-reference operand with VERIFY_ERROR_BAD_CLASS_HARD, so no linkable APK can present the " +
                "shape at all: the core's `0` is the SOUND direction rather than a measured ART answer. " +
                "THE DIVERGENCE FROM THE ORACLE IS STILL REAL AND MUST NEVER BE RECONCILED -- the oracle's " +
                "`true` is wrong under either tier and under spec §2.2. The core keys the row on " +
                "`Value.readsAsNullReference`, the same predicate six array/throw ops already consume, whose " +
                "ZERO rows ARE [ART] under both tiers; keying it on `raw == null` is the defect Plan 7's " +
                "whole-branch Critical was, in a new pair of ops. See `docs/DALVIK-SEMANTICS.md`'s " +
                "\"ART has TWO interpreter tiers\" subsection.",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "Z", "false")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "types-unknown-cast-may-throw",
                className = "Ldifferential/types;",
                methodDescriptor = "unknownCastMayThrow(Ljava/lang/Object;)I",
            ),
            basis = "SPEC, [BC]'s `check-cast` row: \"Throw a ClassCastException if the reference in the " +
                "given register cannot be cast to the indicated type\" -- one of only two Exception types " +
                "[BC] names anywhere. An `Unknown(Ljava/lang/Object;)` may hold a String and may hold " +
                "something else, so BOTH outcomes are feasible and both must be explored: more forks, " +
                "never fewer (spec §2.2). An unexplored feasible path on a run reporting Complete is the " +
                "worst outcome this engine can have, so this entry may never be reconciled by making the " +
                "core fork less. The MIRROR direction is guarded separately and must not be collapsed into " +
                "this one: an `Unknown` whose declared type SATISFIES the target provably passes, so the " +
                "core does NOT fork there (`core/op/CheckCastOpTest." +
                "anUnknownWhoseDeclaredTypeSatisfiesTheTargetNeverForks`), and the agreement fixture " +
                "`types-cast-string-to-object` in newCoreSupported is the control for the success row",
            reached = setOf(4, 5),
        ),
        CoreRegression(
            fixture = Fixture(
                id = "statics-literal-untouched-by-clinit",
                className = "Ldifferential/statics_reader;",
                methodDescriptor = "readLiteralUntouchedByClinit()I",
            ),
            basis = "adjudication 5 (docs/DALVIK-SEMANTICS.md row 5): dex encodes static initial values " +
                "in the class definition (JVMS §5.4.2 preparation applies them BEFORE the `<clinit>` " +
                "runs), and for `static final` constants the compiler emits NO `<clinit>` assignment at " +
                "all -- so discarding the literal because a `<clinit>` exists reads a compile-time " +
                "constant as unknowable. The core seeds every declared static at preparation " +
                "(`ClassInitPlanner.prepare` row 1) and answers Known(0x2a, I), which is what a device " +
                "reads. A precision WIN, never to be reconciled toward the oracle; the agreement controls " +
                "on the same class are `statics-clinit-assigned-field` (the `<clinit>`-written value, " +
                "where both engines agree) and `sget-literal-no-clinit` (the no-`<clinit>` literal, where " +
                "the oracle itself seeds) in [newCoreSupported]",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "I", "42")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "invoke-ambiguous-receiver" },
            basis = "adjudication 1, AMBITIOUS AND USUALLY RIGHT: keep provable single-target dispatch, but " +
                "two candidates are not provable. The core records both and declines to guess; the unknown " +
                "result explores the second equal-valued control arm.",
            reached = setOf(9, 10),
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "invoke-null-receiver" },
            basis = "adjudication 2, BUG: the core takes the NPE handler at 6-7; the oracle invents the " +
                "normal continuation at 4-5. Both return the same 7, so only the outcome partition differs.",
            reached = setOf(6, 7),
            notReached = setOf(4, 5),
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "invoke-outcome-partitions" },
            basis = "adjudication 3, RIGHT IDEA WRONG MECHANISM: return state belongs only to normal " +
                "continuation and pre-throw state belongs only to its caller handler. The core returns 10 " +
                "through both partitions without replacing the held array reference.",
            registers = mapOf(2 to NormalizedValue(ValueKind.KNOWN, "I", "10")),
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "class-init-super-before-sub" },
            basis = "adjudication 4, BUG: superclass initialization precedes subclass initialization. The " +
                "parent writes 1 and the child writes 2, so the correct final marker is 2.",
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "I", "2")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "ifield-put-then-get" },
            basis = "adjudication 6, KNOWINGLY DEFERRED: id-tracked object state now makes the 0x42 write and " +
                "read exact. This is an intended capability divergence, not an agreement fixture.",
            registers = mapOf(1 to NormalizedValue(ValueKind.KNOWN, "I", "66")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "ifield-default" },
            basis = "adjudication 6, KNOWINGLY DEFERRED: the heap-backed field cell has the declared JLS 12.5 " +
                "default. The core proves 0 and the oracle cannot represent the cell.",
            registers = mapOf(1 to NormalizedValue(ValueKind.KNOWN, "I", "0")),
            escapes = emptySet(),
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "invoke-missing-class" },
            basis = "adjudication 7, BUG: measured ART raises guest NoClassDefFoundError. The core completes " +
                "with exactly that unhandled guest Error instead of killing the host analysis.",
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "invoke-missing-method" },
            basis = "adjudication 7b, BUG: measured ART raises guest NoSuchMethodError. Array clone remains a " +
                "Plan 11 intrinsic, but an ordinary missing app method is fully witnessable here.",
        ),
        CoreRegression(
            fixture = Fixture(
                "invoke-aborted-callee-fallback",
                "Ldifferential/invocation;",
                "callAbortedArrayCallee()[I",
                setOf(3),
            ),
            basis = "Resuming a caller does not complete the truncated callee's exploration. The core " +
                "retains the fallback result but reports Partial and refuses trusted analysis.",
            aborted = true,
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "invoke-opaque-collapse" },
            basis = "Retain the exact HeapRef with unknown field contents, but report Partial and deny " +
                "trusted analysis because the callee's exploration was truncated.",
            aborted = true,
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "invoke-entrypoint-truncation" },
            basis = "the core reports Partial and denies trusted() while both explored projections still show " +
                "v0=11 and exactly [0,3,4,6,7]. The oracle exposes that same value and reachability as trusted output.",
            aborted = true,
            registers = mapOf(0 to NormalizedValue(ValueKind.KNOWN, "I", "11")),
            exactReached = setOf(0, 3, 4, 6, 7),
        ),
        CoreRegression(
            fixture = invocationCases.first { it.id == "handler-first-declared" },
            basis = "Dalvik handler resolution is first-declared. This legal table gives both handlers the " +
                "same final value, isolating exact reachability from value contamination.",
            reached = setOf(6, 7, 8),
            notReached = setOf(9, 10),
        )
    ).requiringUniqueIds("regressions") { it.fixture.id }

    val device: List<Fixture> get() = DeviceFixtures.fixtures
}

/** Expected core behavior justified by fixture semantics, not by a second engine. */
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

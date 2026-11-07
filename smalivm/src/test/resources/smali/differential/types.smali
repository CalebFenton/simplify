# Fixtures for the type hierarchy (Plan 8 Task 7): the first differential coverage of `joinTypes`
# consulting a real class hierarchy, and the first of the `instance-of` family's array-supertype
# divergence.
#
# Both methods deliberately use only opcodes the core had BEFORE this task (`const/4`, `if-eqz`,
# `new-array`, `goto`, `return*`) plus, in the second method, the one opcode it does NOT have yet. That
# split is the point: the first method is an AGREEMENT fixture that works today, and the second is a
# ratchet that fires on arrival the moment Task 8 lands `instance-of`, forcing the array-supertype
# divergence to be re-adjudicated under its real shape rather than left as prose.
.class public Ldifferential/types;
.super Ljava/lang/Object;

# TWO ARMS RETURNING DIFFERENT REFERENCE ARRAY TYPES, joined at the `return-object`.
#
# The first fixture in the repository whose consensus is a join of two DISTINCT reference types, which is
# the only shape that can observe `joinTypes`' hierarchy at all -- every earlier fixture either agrees on
# the type or joins a primitive with a reference (`Incompatible`, hence `Top`). `[Ljava/lang/String;` and
# `[Ljava/lang/StringBuilder;` join covariantly to `[Ljava/lang/Object;` (JLS §4.10.3: `S[] >1 T[]` iff
# `S >1 T`, and `String ⊔ StringBuilder` is `Object`), which is a TIGHTER true upper bound than
# `Ljava/lang/Object;` -- and the oracle's own `getConsensusType` reaches the same answer by a different
# route, so this belongs in `newCoreSupported` rather than in the registry.
#
# The arms join COMPATIBLY on purpose. A forking fixture whose arms join to `Value.Top` projects
# CONFLICTED against the oracle's UNKNOWN forever, which is why Task 5's `branch-disagreeing-arms` had to
# be a registry entry; here both engines answer UNKNOWN with the same type descriptor.
#
# The selector is a PARAMETER, not a seed, because `Fixture.seededRegisters` is Known-only by design and
# a Known selector would not fork.
#
# Addresses: `const/4` at 0; `if-eqz` (21t, 2 units) at 1; `new-array` (22c, 2 units) at 3; `goto` at 5;
# `new-array` at 6; `return-object` at 8. The branch offset is +5, so [BC]'s "the branch offset must not
# be 0" holds.
.method public static arrayArmsJoinCovariantly(I)Ljava/lang/Object;
    .registers 3

    const/4 v1, 0x1

    if-eqz v2, :other

    new-array v0, v1, [Ljava/lang/String;

    goto :end

    :other
    new-array v0, v1, [Ljava/lang/StringBuilder;

    :end
    return-object v0
.end method

# THE SAME SHAPE, ON A PAIR WHERE THE TWO ENGINES' JOIN ALGORITHMS DISAGREE.
#
# `[Ljava/lang/Integer;` and `[Ljava/lang/Long;` have a non-`Object` common component supertype, and that
# is the whole difference:
#  - the core joins components (`Integer ⊔ Long` = `Ljava/lang/Number;`) and re-wraps, answering
#    `[Ljava/lang/Number;` -- a true upper bound, and the tightest array type that is one;
#  - the oracle's `getConsensusType` (`ExecutionGraph.kt:120-157`) adds every type's `immediateAncestors`
#    (`:148`) -- INTERFACES INCLUDED -- and then collapses children, so the interface `[Comparable` keeps
#    the set at size 2 for a whole round and the expansion overshoots to `[Ljava/lang/Object;`.
#
# Neither answer is unsound; the core's is strictly more precise. Registered rather than reconciled -- see
# the `types-array-arms-join-tighter-than-the-oracle` entry in `Fixtures.kt`.
#
# No boxing is needed to build this: `new-array` only needs the array class to be loadable, so the
# component types can be classes the method never instantiates.
.method public static arrayArmsJoinTighterThanTheOracle(I)Ljava/lang/Object;
    .registers 3

    const/4 v1, 0x1

    if-eqz v2, :other

    new-array v0, v1, [Ljava/lang/Integer;

    goto :end

    :other
    new-array v0, v1, [Ljava/lang/Long;

    :end
    return-object v0
.end method

# `instance-of` OF AN `int[]` AGAINST `Ljava/lang/Cloneable;`, which a device answers 1 and the oracle
# answers 0.
#
# INHERITED: JLS §4.10.3 lists `Cloneable` and `java.io.Serializable` as exactly the interfaces arrays
# implement, and lists the row for primitive components explicitly (quoted at
# `docs/DALVIK-SEMANTICS.md#subtype-testing-entirely-inherited`). Android's own toolchain agrees --
# AOSP `Merger.isPossiblyAssignableFrom` says its model assumes only "that the type Object is the
# supertype of all reference types and all arrays are assignable to Serializable and Cloneable".
#
# The oracle omits both: `VirtualArray.instanceOf` is equality plus `getAncestors()` membership, and
# `getImmediateAncestors()` never adds either interface on either of its two branches
# (`docs/architecture/type-and-dex.md:95`, `:203`). `type/VirtualArrayTest` ENCODES the omission, so the
# core's answer must never be "fixed" toward the oracle.
#
# `.registers 2`; v0 is the array, then the `instance-of` destination, then the returned value, so the
# default `observeRegisters` of {0} observes the whole method.
.method public static intArrayIsCloneable()I
    .registers 2

    const/4 v1, 0x1

    new-array v0, v1, [I

    instance-of v0, v0, Ljava/lang/Cloneable;

    return v0
.end method

# ==================================================================================================
# Added by Plan 8 Task 8, when `instance-of` landed. Two more oracle disagreements, both of them
# `Known`-against-`Known` with different VALUES on identical paths.
# ==================================================================================================

# 🔴 `instance-of` ON A POSSIBLY-NULL `Unknown` WHOSE DECLARED TYPE SATISFIES THE TARGET.
#
# `docs/architecture/opcodes.md:173` files the oracle's answer as its **Correctness Defect 18**:
# "`instance-of` can return definitely true for an unknown declared subtype even though the value may
# be null." The oracle's type-set loop (`opcode/InstanceOfOp.kt:34-40`) runs BEFORE its `isUnknown`
# test (`:41`), so it folds `Known(true, Z)`; the core answers `Unknown(Z)`, because
# `core/Value.kt`'s `Unknown` "may be null" and a device answers 0 for a null (INHERITED, [JVMS]
# `instanceof`). See the `types-unknown-string-instance-of-string` entry in `Fixtures.kt`.
#
# THE SOURCE MUST BE A PARAMETER, not a seed: `Fixture.seededRegisters` is Known-only by design (an
# UNKNOWN seed would have each engine build its own unknown, so seeding drift would be
# indistinguishable from op drift), and both engines seed a reference parameter as
# "unknown of the declared type" natively.
#
# `.registers 3`, one parameter, so p0 is v2 and v1 is the `instance-of` destination -- observed
# register **1**, not the default 0, because v0 is never written and would project ABSENT on both
# sides. Addresses: `instance-of` (22c, 2 units) at 0, `return` at 2.
.method public static unknownStringIsInstanceOfString(Ljava/lang/String;)I
    .registers 3

    instance-of v1, v2, Ljava/lang/String;

    return v1
.end method

# 🔴 `instance-of` ON A REGISTER WHOSE LAST WRITE WAS PRIMITIVE, against `Ljava/lang/Object;`.
#
# Registry D9. The oracle's null row is keyed on `instance.raw == null`
# (`opcode/InstanceOfOp.kt:30`), so a `Known(5, "I")` skips it; its type set is
# `{"I", "Ljava/lang/Integer;"}` (oracle `Value.kt:84-89`), `Ljava/lang/Integer;` IS an `Object`, and
# it folds `Known(true, Z)`.
#
# [ART] answers 0: `INSTANCE_OF` fetches the operand with `GetVRegReference(B())`
# (`interpreter_switch_impl-inl.h:847`) and `ShadowFrame`'s reference half is cleared by EVERY
# primitive write (`shadow_frame.h:401-408`), so the register reads as `nullptr`. The core keys the
# row on `Value.readsAsNullReference` and answers `Known(false, Z)`.
#
# No seeding needed -- `const/4` produces `Known(5, "I")` in both engines -- so the fixture is
# self-contained. `.registers 2`; addresses: `const/4` at 0, `instance-of` at 1, `return` at 3.
.method public static primitiveRegisterIsInstanceOfObject()I
    .registers 2

    const/4 v1, 0x5

    instance-of v0, v1, Ljava/lang/Object;

    return v0
.end method

# ==================================================================================================
# Added by Plan 8 Task 9, when `check-cast` landed. One disagreement about which PATHS exist, and one
# agreement that keeps it from reading as "the core differs on every cast".
# ==================================================================================================

# 🔴 `check-cast` ON AN AMBIGUOUS REFERENCE: the core explores the throw, the oracle does not.
#
# `opcode/CheckCastOp.kt:38` returns `finishOp(mayThrow = true)` for an unprovable value, and for THIS op
# the flag is inert: `finishOp` appends `defaultExceptions` (`UnresolvedChildProducer.kt:36-43`), `Op`'s
# constructor defaults that array to `arrayOf()` (`opcode/Op.kt:12-13`), and `CheckCastOp` declares
# `: Op(location)` with no second argument (`:16-20`). Four sibling ops DO pass one, positionally
# (`opcode/BinaryMathOp.kt:23`, `opcode/AGetOp.kt:20`, `opcode/ArrayLengthOp.kt:19`,
# `opcode/APutOp.kt:24`), so the mechanism is live and this is the op that asks for it without declaring
# it -- which is why `:38` looks correct in isolation. The oracle therefore reaches [0, 1, 3] and the
# declared handler at [4, 5] is never scheduled, on a run it reports complete.
# See the `types-unknown-cast-may-throw` entry in `Fixtures.kt`.
#
# WHY THE SOURCE IS A PARAMETER: `Fixture.seededRegisters` is Known-only by design, and a `Known` value
# would make the cast provable on both engines. Both engines seed a reference parameter as "unknown of
# the declared type" natively.
#
# WHY THE PARAMETER IS TAGGED `Ljava/lang/Object;` AND NOT `Ljava/lang/String;`: an `Unknown` whose
# declared type SATISFIES the target provably passes the cast (design decision 5 -- it holds null, or a
# String, or a subtype, and all three pass), so a String-tagged parameter would make the core agree with
# the oracle by not forking at all. `Ljava/lang/Object;` is the tag that leaves both outcomes feasible.
#
# WHY `move-exception` TARGETS v1 AND v0 HOLDS A CONSTANT: `ORACLE_MISJUDGES_FEASIBILITY`'s branch also
# asserts `oracleResult.registers == coreResult.registers`, because the recorded divergence for that
# shape is the PATH SET and nothing else. v0 is `Known(7, "I")` on every returning path of both engines,
# so the observed value agrees while the address sets differ -- the same construction
# `ops/binary_math_test.smali`'s `remIntUnknownDivisorWithCatch(I)V` uses, and its comment says why.
# Binding the exception into v0 (which `ops/check_cast_test.smali`'s `castToStringWithCatch()V` does)
# would make the registers differ by construction and the entry unregistrable under this shape.
#
# Register layout, derived from the encoding: `.registers 4` with one reference parameter puts p0 at v3
# (registerCount - parameterSize), leaving v0 for the observed constant and v1 for the catch slot.
# Addresses: @0 `const/4` (11n, 1 unit), @1 `check-cast` (21c, 2 units), @3 `return` (11x, 1),
# @4 `move-exception` (11x, 1), @5 `goto` (10t, 1).
.method public static unknownCastMayThrow(Ljava/lang/Object;)I
    .registers 4

    const/4 v0, 0x7

    :try_start_0
    check-cast v3, Ljava/lang/String;
    :try_end_0
    .catch Ljava/lang/Exception; {:try_start_0 .. :try_end_0} :catch_0

    :return
    return v0

    :catch_0
    move-exception v1

    goto :return
.end method

# THE AGREEMENT CONTROL for the entry above, and the first differential coverage of the re-tag itself.
#
# A `Known` reference whose type satisfies the target: both engines succeed, both REPLACE the register's
# declared type with the cast type, and both project `KNOWN great maker Ljava/lang/Object;`. The oracle
# re-tags at `opcode/CheckCastOp.kt:28` and the core narrows on its `Definitely` row, so the agreement is
# measured rather than assumed.
#
# It is deliberately a WIDENING cast (`Ljava/lang/String;` -> `Ljava/lang/Object;`), which is the half of
# the re-tag most likely to be "fixed" into an intersection: [ART]'s verifier replaces the register's type
# and its own comment says the upcast is not addressed (`method_verifier.cc:3122-3128`, the write at
# `:3165`). An intersecting implementation would keep `Ljava/lang/String;` here and the two engines would
# disagree about the TYPE while agreeing about the raw -- which nothing else in this corpus would notice.
#
# `.registers 1`; addresses: @0 `const-string` (21c, 2 units), @2 `check-cast` (21c, 2), @4
# `return-object` (11x, 1).
.method public static castStringToObject()Ljava/lang/Object;
    .registers 1

    const-string v0, "great maker"

    check-cast v0, Ljava/lang/Object;

    return-object v0
.end method

# ==================================================================================================
# Added by Plan 8 Task 11 (differential coverage): a `const-class` agreement, and an `instance-of`
# agreement control -- the two type-op families whose swept coverage was otherwise all-divergence.
# ==================================================================================================

# `const-class` OF A SYSTEM CLASS, returned. The first swept `const-class` fixture: both engines resolve
# `Ljava/lang/Object;` and project `KNOWN Ljava/lang/Class;` with the SAME repr.
#
# NO SHARED `SmaliClassLoader` IS NEEDED, and the reason is that the projection compares the `Class`'s
# `toString()` repr, never its identity (`ResultProjection.reprOf` -> `Class.toString()`, since `Class`
# overrides it). `Ljava/lang/Object;` is bootstrap-loaded on BOTH engines (the core's
# `ClassIndex.classLoader` and the oracle's own `SmaliClassLoader` both delegate a system class to the
# parent loader), so the two `Class` values are in fact identical -- but even a GUEST class would agree,
# because `Class.toString()` is loader-independent (it is "class " + binary name, and both loaders derive
# the same binary name from one descriptor). Task 10's hand-off worried the two `Class` values would be
# identity-incomparable; measured, that cannot surface, because no projected field compares identity.
#
# `.registers 1`; v0 is the const-class result and the returned value. Addresses: `const-class`
# (21c, 2 units) @0; `return-object` (11x, 1 unit) @2.
.method public static constClassObject()Ljava/lang/Class;
    .registers 1

    const-class v0, Ljava/lang/Object;

    return-object v0
.end method

# `instance-of` THAT FOLDS TRUE ON BOTH ENGINES: a KNOWN `Ljava/lang/String;` against `Ljava/lang/Object;`.
# The swept AGREEMENT control for the three `instance-of` DIVERGENCE entries in the registry
# (`types-int-array-is-cloneable`, `types-primitive-register-is-instance-of-object`,
# `types-unknown-string-instance-of-string`) -- the same role `types-cast-string-to-object` plays for
# `check-cast`. Without it, "the core disagrees about `instance-of`" would look systematic rather than
# confined to the three adjudicated rows.
#
# A `const-string` gives both engines a `Known(String)` whose declared type provably satisfies `Object`,
# so both fold `Known(true, Z)` = 1 -- an ordinary, provable answer, unlike the possibly-null `Unknown`
# of the divergence entries.
#
# `.registers 2`; v0 is the string then the `instance-of` destination then the returned value; v1 is
# unused. Default observe {0}. Addresses: `const-string` (21c, 2 units) @0; `instance-of` (22c, 2 units)
# @2; `return` (11x, 1 unit) @4.
.method public static knownStringIsInstanceOfObject()I
    .registers 2

    const-string v0, "great maker"

    instance-of v0, v0, Ljava/lang/Object;

    return v0
.end method

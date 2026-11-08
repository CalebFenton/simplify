# Dalvik Semantics Reference

This reference records runtime behavior per opcode family, with source and device evidence.
Tests and implementation comments should cite the relevant evidence here rather than restating it.
Historical oracle comparisons explain intentional divergences; their implementation line numbers
refer to the surveyed revisions, not necessarily the current source.
Survey citations prefixed `b3abe32:` identify Git history, not current documentation paths.
For example, `git show b3abe32:docs/architecture/opcodes.md` retrieves that historical evidence;
the following line numbers are the recorded survey citations. Current contracts are indexed in
[the architecture overview](architecture/overview.md).

An incorrect exception claim can change handler reachability
(see [`new-array`](#new-array-op-23-22c)). Every runtime claim therefore needs an explicit evidence tier.

---

## The evidence tiers

**Every semantic claim in a plan, KDoc, or test comment carries one of these tags.** A claim with no
tag is a claim nobody has checked.

| Tag | Means | Authority |
|---|---|---|
| **SPEC** | Stated by the Dalvik bytecode reference, the instruction-format reference, or the dex-format reference. Quote it and give the URL. | Decisive. |
| **INHERITED** | Dalvik does not restate it; it inherits Java's rule (JVMS/JLS). Cite the section *and say it is inherited* — the Dalvik docs are often silent precisely where the Java rule is assumed. **Inheritance needs a JVM instruction to inherit *from*: name the counterpart, per opcode.** | Decisive, but say which document — and only where a counterpart exists (see rule 3). |
| **VERIFIER** | The condition cannot arise in *verified* bytecode. This VM deliberately executes unverified, hostile, obfuscated input, so it must still be **total** here — but whatever behavior it picks is a **design choice**, not a Dalvik fact. Never write "Dalvik does X" about input Dalvik would reject before executing. | None. Choose, and record the choice with its reason. |
| **ORACLE** | What the previous engine (`org.cf.smalivm`, pre-rewrite) does. Evidence that a behavior survived years of real malware, and the reference the differential compares against — **not** authority about Dalvik. Where the oracle contradicts SPEC or INHERITED it is a defect: fix the core and register an `IntendedDivergence`. | None. |
| **CORPUS** | What a test fixture asserts. Evidence of ORACLE behavior, since the oracle computed these numbers. A corpus expectation **can be wrong** — Plan 4 found one — so a corpus citation never settles a semantic question by itself. | None. |

**The three rules that follow from the table.**

1. **ORACLE + CORPUS agreeing proves only that the oracle is self-consistent.** Both derive from the
   same engine. Plan 4's Critical finding was a defect the two engines *shared*, which no amount of
   differential agreement could surface. When SPEC and ORACLE disagree, SPEC wins and the disagreement
   gets registered.
2. **A VERIFIER-tier condition is where the engine earns its keep.** Verified code cannot put a
   `String[]` in a register tagged `[I`; obfuscated code does it on purpose, and executing it anyway is
   the product. So these rows are not edge cases to be tidied away — they are the target workload. But
   the behavior chosen for them is justified by *soundness direction* (spec §2.2), never by an appeal to
   Dalvik.
3. **Tier PER OPCODE. "INHERITED from [JVMS] §6.5" is not a default, and it was written as one.** This
   was a blanket claim in the Plans 6–7 constraints through six task dispatches before Plan 7's Task 10
   disproved it. Inheritance requires a JVM instruction to inherit *from*, so **name the counterpart**:
   `aget*`/`aput*`/`array-length`/`new-array` inherit from `aaload`/`aastore`/`arraylength`/`anewarray`
   and the tag is right. **`fill-array-data` has no JVM counterpart at all** — the JVM has no payload-fill
   instruction — so there is nothing to inherit and its exceptions are **[ART] only and genuinely
   unspecified**, which is a materially weaker claim than INHERITED and must not be dressed up as one.
   A third case is neither: most of `filled-new-array`'s expected exceptions are **unreachable rather than
   inherited** (it has no array, index or count *operand*, and ART's own `if (UNLIKELY(length < 0))` is
   dead code because `VRegA_35c()` is unsigned), so "inherited but never fires" would also have been
   wrong. Ask which of the three you have before writing the tag.

### The tiers are not equally strong — the ranking for a claim about what the RUNTIME does

**The table above names the tiers; it does not rank them, and a reader who treats the five as
interchangeable will write a well-cited falsehood.** Added 2026-08-03 after two of them were found in this
file (see [the `can_run_clinit` non-sequitur](#const-class-21c)). For a claim of the form *"a device does /
does not do X,"* the strength order is:

1. **`[DEVICE]` — empirical measurement on a real ART.** The only tier that can settle a runtime question
   outright, because it *is* the runtime. **It did not exist for the first eight plans of this rewrite, and
   that absence is the whole reason this ranking exists** — for eight plans every `[ART]`-tagged claim here was
   a *reading of source code* wearing a measurement's authority, and [the `can_run_clinit`
   non-sequitur](#const-class-21c) is exactly what a source reading gets wrong that running the bytes cannot.
   As of 2026-08-05 it exists, is reproducible, and has answered the whole class-B backlog — the `aput`
   NPE≺AIOOBE≺ASE ordering, the 32-cell subtype matrix, the resolution-failure family, the nterp register-read
   — so the top row is the target *reached*, not the target *planned*. Keep that history: deleting *"absent for
   eight plans"* deletes the lesson that a well-cited source reading is not a measurement.

   **Cite it `[DEVICE](tier, interpreter mode, artifact version)`, and the parenthetical is MANDATORY** — e.g.
   `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` or `[DEVICE](Tier B, nterp default, API 36)`. It
   is not bureaucracy: the two facts that most nearly invalidated this whole harness were an **unsound AOT
   tier** (this ART's AOT compiler silently drops an `ArrayStoreException` its interpreter throws — the reason
   for the `-Xint` mandate) and a **missing interpreter tier** (`nterp`, a modern device's default, reads a
   register from a different array than the `-Xint` switch interpreter does — Constraint 2). Both are invisible
   in a citation that says only *"measured on a device"*, and either one silently substituted turns a true
   measurement into a false one — **so a `[DEVICE]` citation missing any of the three fields is not a
   `[DEVICE]` citation.** The two tiers in service: **Tier A** — `tools/art-runner/`, `art_standalone
   0_git20251009-r2`, `-Xint` — the daily driver, and the only tier that reports ART's own verifier verdict as
   a catchable `VerifyError`; **Tier B** — `tools/emulator/`, a real API 36 device, `nterp` default or `-Xint`
   — periodic cross-validation and the register-read tier.
2. **ART source, read END-TO-END along the code path.** Not a flag, not a single line, not one argument at
   one call site. The failure this ranking was written for is exactly that: `const-class` and `instance-of`
   were both documented as not initializing *because* ART passes `can_run_clinit = false`, and
   `NEW_INSTANCE` passes the same `false` at the same argument position and **does** initialize, downstream
   in `AllocObjectFromCode`. A flag argument bounds one call site; only following the whole path bounds the
   opcode. Where the handler body is short enough to read in full, quote the *whole body* and say that it is
   the whole body — that is the form of the evidence.
   **And "end to end" includes WHICH INTERPRETER, because ART has two of them and they disagree.** Reading
   `interpreter_switch_impl-inl.h` end to end bounds the tier `-Xint` selects, not the tier a default device
   run uses; `nterp` is the default and reads a register out of a different array. So a tier-2 claim about a
   register read is only tier-2 once the tier is **named** — see
   [ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim),
   which is the general instrument and cost one `[ART]` tag its rank on 2026-08-04.
3. **Android's own documentation — [BC], [CON], [VER].** Authoritative for Dalvik, and *sparse* in a way
   that is easy to mistake for silence-as-permission. Measured, 2026-08-03, against [BC]'s raw HTML with
   tags stripped and whitespace flattened: **`initiali*` occurs ZERO times in the whole of [BC]** (`clinit`
   zero times as well) — the bytecode reference says nothing whatsoever about class initialization, for any
   opcode. Same method as [the two-`Exception`-types
   measurement](#the-finding-that-reframes-every-exception-claim-in-this-rewrite), and re-confirmed by the
   same run (4 × `ArithmeticException`, 1 × `ClassCastException`, 2 × `BootstrapMethodError`). So *no*
   initialization claim in this file can be SPEC, in either direction.
4. **[JLS] / [JVMS] — CORROBORATION ONLY, never sole support.** A useful *prior*, because ART is built to
   run Java-compatible code, and legitimately decisive about what the **Java language** requires. But it is
   **two translations away** from a device (JLS → d8 → dex → ART), and it is *actively misleading* in two
   places. (a) **Verifier-invalid input**, where ART has no defined behavior at all and [JVMS] describes a
   machine that would have rejected the input: the honest label there is *"our design choice under spec
   §2.2,"* never INHERITED. (b) **Timing rather than triggering** — see the
   [transaction-mode note](#const-class-21c) for a case where ART runs `<clinit>` at *build* time under a
   rollback guard, which [JLS] §12.4.1's trigger list does not describe and does not forbid.

**The ranking, stated as a rule and not a preference** — so a conflict resolves without an argument about
which source is more authoritative:

> **A `[DEVICE]` measurement outranks an `[ART]` source reading, which outranks Android's documentation ([BC]/[CON]/[VER]), which outranks [JLS]/[JVMS] corroboration.** On a question of *what the runtime does*, the higher tier wins outright.

**It outranks the differential ORACLE as well — the clause the maintainer asked for by name: defer to the
device when it conflicts with the ORACLE, not only when it conflicts with the spec.** The oracle is a claim
about the *old engine* (tier `ORACLE`, with no authority about Dalvik), so a `[DEVICE]` measurement of the
runtime beats the oracle's *expectation* on any runtime question. The worked example below is the live proof.

**Two exceptions, stated because a rule with hidden exceptions gets misapplied:**

1. **A device cannot settle a VERIFIER-tier question about input it refuses to link.** Where the verifier
   hard-fails the class, no linkable APK can present the bytes, so there is nothing for the interpreter to
   measure. What a device *can* do — and this is Tier A's unique contribution — is report ART's own verifier
   verdict as a catchable `VerifyError`: a measurement of the **verifier**, new to this project, not of the
   interpreter, and the one runtime-adjacent fact a refused input still yields.
2. **A Tier A measurement cannot settle ANY question the two interpreter tiers answer differently
   (Constraint 2).** Tier A has only the `-Xint` switch interpreter, so it structurally cannot see a
   nterp-vs-switch split — and `nterp` is what a modern device actually runs by default.

   **This constraint was WIDENED on 2026-08-05 (Plan 9 Task 15) after it under-covered a real divergence.**
   It originally read "cannot settle a REGISTER-READ question", scoped to the Task 9 finding (nterp reads a
   register from a different array, so a non-zero primitive under a reference tag kills the VM while the
   switch interpreter reads null). Task 15 then measured a **second, structurally identical divergence that
   is not a register read at all**: `Fx_NewArrayAbsentTypeNegativeLength` — an absent component type *and* a
   negative length on one `new-array` — answers `NegativeArraySizeException` under `-Xint` on **both** tiers
   and `NoClassDefFoundError` under nterp on the same device. That is an **allocation-ordering** question,
   and it decides which guest handler is reachable.
   Two lessons the narrow wording cost us: the first divergence found is not the shape of the family, and
   **it is an interpreter-tier split, not an ART-version one** — the two `-Xint` runs agree byte-for-byte
   across the whole Android-10/11-to-API-36 gap, so only running Tier B in *both* modes isolates it.

   So: name the tier on **every** claim a tier could plausibly split, never let Tier A stand in for the
   default, and when Tier A and the Tier B default disagree, run Tier B under `-Xint` too before concluding
   anything — otherwise a tier split reads as a version regression. Where a *reachable* semantic genuinely
   differs between tiers, note that this engine currently models the **switch** order; "which tier does this
   engine model?" is an open maintainer question, not a settled one (see the `new-array` entry in the
   resolution-failure section).

### THE MODELLED TIER — decided, so it stops being re-litigated per opcode

**There are THREE execution modes, not two, and one is already disqualified:**

| mode | where it runs | status |
|---|---|---|
| **AOT** (`dex2oat`) | Tier A only, Android 10/11-vintage | **DISQUALIFIED as unsound.** Silently drops an `ArrayStoreException` the interpreter throws correctly, measured 5/5. Running it needs an explicit `ART_RUNNER_ALLOW_UNSOUND_AOT=1` override, and `verify-xint-mandate.sh` re-proves the defect on demand. |
| **switch interpreter** (`-Xint`) | **both** Tier A and Tier B | Tier A's mandated mode; Tier B's apples-to-apples mode. |
| **`nterp`** | Tier B only | What a modern device runs **by default**. |

**DECISION (maintainer, 2026-08-05): this engine models the SWITCH INTERPRETER (`-Xint`).** Where `nterp`
differs, the engine keeps the switch answer and the difference is recorded in code at the site (see the
convention below). Four reasons, in order of weight:

1. **Every claim stays cheaply reproducible.** `-Xint` is the only mode *both* tools run, so any reader
   re-checks a measurement in under a second on Tier A instead of booting an emulator. That is the
   falsification rule below, applied to the choice of tier itself — a modelled tier only Tier B can measure
   would make most of the corpus expensive to re-verify, and expensive verification decays.
2. **Neither measured `nterp` divergence is preferable, and one is not even modellable.** On the register
   read, `nterp` dereferences a raw primitive as a pointer and **the VM dies** — there is no analysis answer
   to copy, and the sound null-read stands as a spec §2.2 choice. On the allocation order, `nterp` reports
   the absent class where the switch interpreter reports the negative length; neither is more correct, and
   the switch order also matches the inherited [JVMS] rule and the old engine.
3. **`nterp` is an optimization that skips helper routines, and that is exactly where both divergences came
   from** — it does not route through `CheckArrayAlloc`, and it keeps its own register array. The switch
   interpreter sits closer to the reference semantics, so modelling it produces fewer special cases.
4. **It is already the de facto choice** — the engine implements the switch order and the pinned expectations
   match it. Making it explicit costs nothing and stops each new opcode re-opening the question.

**This is a default, not a dogma.** If a real obfuscated sample is mis-analyzed *because* we model the switch
tier, that outranks all four reasons — bring the sample. And note the option neither tier gives you: a
reachable semantic that genuinely differs by tier is arguably an **ambiguity**, and this engine forks
ambiguity everywhere else rather than picking. That is a live option with a frontier-width cost, deliberately
not taken yet.

### The convention: record a tier divergence IN CODE, at the site

Prose in this file is where the measurement lives; the **code** is where someone will be standing when it
matters. So at every site whose behavior a tier split touches, leave a greppable marker:

```kotlin
// TIER-DIVERGENCE(nterp): <what nterp does instead>. Measured <Fx_Fixture>.
// We model -Xint (see DALVIK-SEMANTICS.md "THE MODELLED TIER"). <the observable consequence>
```

`grep -rn 'TIER-DIVERGENCE' smalivm/src/main` is then the complete list of places this engine knowingly
differs from what a device does by default — which is exactly the question a reader hits when a differential
result looks wrong. Two rules: name the **fixture**, so the claim is falsifiable; and state the
**consequence**, because "the tiers differ" is not actionable while "this decides which `catch` clause is
reachable" is.

**The falsification rule — a `[DEVICE]` measurement is only as good as its reproduction.** Every one must cite
a corpus fixture BY NAME, so any reader can re-run it: under a second on Tier A
(`tools/art-runner/run-batch.sh Fx_Name`), or on the emulator for Tier B (`tools/emulator/run-dex.sh`). A
measurement nobody can reproduce is an anecdote with a better tag, and it outranks nothing.

**The consequence for the tag names, which keep working unchanged:** `[DEVICE]` is tier-1 and settles a
runtime question outright; INHERITED is a *tier-4* citation, so an INHERITED claim about what a device does
needs a tier-1/2/3 companion or it is a prior dressed as a fact; [ART] is a tier-2 citation and only earns
that rank when the path was read end to end.

**Worked example — the rule resolving a live conflict with no argument (the resolution-failure family, Step
6's mutation).** The differential oracle's `ConstOpTest.nonExistentClassNameThrowsException` expects a host
`ClassNotFoundException`, a checked **`Exception`** — because the oracle's `const-class` catches
`ClassNotFoundException` and returns it as the thrown constant. Both tiers measured otherwise:
`[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` and `[DEVICE](Tier B, nterp default, API 36)` throw
`java.lang.NoClassDefFoundError` — an **`Error`**, via `LinkageError` — for every resolution-failure opcode,
pinned by `Fx_ConstClassAbsentType`, `Fx_CheckCastAbsentType`, `Fx_InstanceOfAbsentType`,
`Fx_NewArrayAbsentType` and `Fx_FilledNewArrayAbsentType` in both `tools/art-runner/golden/tier-a-batch.txt`
and `tools/emulator/golden/tier-b-batch.txt` (see [Resolution failure is one
family](#resolution-failure-is-one-family-tiered-once)). Not a naming quibble: `catch (Exception)` does
**not** catch an `Error`, so the two answers put a handler on opposite sides of reachable-versus-dead.

Apply the rule and it settles in one step: *what does the runtime throw when a type will not resolve?* is a
question about **what the runtime does**, so the `[DEVICE]` measurement outranks the ORACLE expectation, full
stop. No appeal to which engine is more authoritative is needed or permitted; `NoClassDefFoundError` (an
`Error`) is the runtime fact, and `ClassNotFoundException` (an `Exception`) is an artifact of how the oracle's
host implementation happens to fail. **If resolving it still took an argument, the rule would not yet be
written** — it does not.

One boundary, because the rule decides *evidence*, not *action*: it settles that the device wins on the fact.
Whether to CHANGE the oracle test stays the maintainer's call. **Plan 9 Task 15 made the call for the core's
side**: `const-class` on an absent type now throws the guest `NoClassDefFoundError` at execute time, so a
`catch (Exception)` handler is unreached because it is *unreachable*, not because the run refused to explore.
(Plan 8's interim answer was a decode-time `Partial({UNSUPPORTED_OP})`, which sidestepped the question by
declining to answer it; `core/op/ConstOpTest.nonExistentClassNameThrowsException` records both adjudications
and `aCatchExceptionDoesNotCatchAResolutionFailureButCatchThrowableAndCatchErrorDo` tests the consequence
directly.) The rule says which evidence is authoritative; it does not, by itself, rewrite a test.

**Where a claim's consequence is handler reachability, say so.** The recurring failure mode in this
rewrite is not computing a wrong number; it is exploring a path the runtime cannot take, which tells a
deobfuscator that a `catch` block is live when it is dead. `Exception` vs `Error` is the sharpest
version — obfuscator-generated wrappers are full of `catch (Exception e)`, which does **not** catch an
`Error`.

---

## Registers, types, and what a "declared type" is

**SPEC.** Dalvik registers are untyped 32-bit slots. A 64-bit value occupies an adjacent pair. Nothing
at runtime carries a per-register type: the verifier establishes types statically, and the runtime
simply moves bits.

**Consequence for this engine.** `Value.type` is **smalivm's own metadata**, not a Dalvik concept. It
exists because a concolic VM must answer "what type is this unknown?" when there is no object to ask.
Two consequences that have each cost a plan real time:

- **A register's tag can disagree with the object it holds, and the corpus does this deliberately —
  but "the object is authority" is narrower than it sounds.** For a *runtime* check
  (`ArrayStoreException`, the actual component type an op reads), the object always wins. Groundable
  from this file's own claims rather than asserted fresh: **SPEC**, above -- "nothing at runtime carries
  a per-register type... the runtime simply moves bits" -- so the tag has no runtime existence to check
  against; and **INHERITED**, [JVMS] `aastore` below -- its check is stated over "the actual type of the
  non-null value", i.e. the object, never the declared component type. The tag is not free-floating
  harness bookkeeping, though: for verified input it is a contract the verifier already proved
  (**VERIFIER** — [CON] B1's general "arguments... must always match the instruction", sharpened for the
  array case by [CON] B15's own wording below, "assignment-compatible with the array's component type"),
  so object and tag are only guaranteed to be **assignment-compatible, not equal**. **The guarantee is
  one-directional: the object's runtime type must be assignable TO the tag's declared type, never the
  declared type to the runtime type.** Legal disagreement is covariance (a `String[]` object -- the
  more specific, runtime type -- may sit in a register tagged `Object[]` -- the less specific, declared
  type — **INHERITED**, JLS §4.10.3, "Subtype testing" below). Illegal disagreement is a
  primitive/reference-component mismatch: primitive arrays are invariant (same section — `int[]` is not
  an `Object[]`), and `aget-object`/`aput-object` require a reference-component array in the first place
  (**VERIFIER** — [CON] B1; [ART] `VerifyAGet`/`VerifyAPut`, `aget*`/`aput*` above). That case is not "no
  object, fall back to the tag" — it is unverifiable input, and failing loudly beats silently picking a
  winner.
- **One opcode serves several types** (`const` serves int/char/byte/short/float; `const-wide` serves
  long/double), so a tag is a hint about *provenance*, not a guarantee about the raw. This is why
  `Coerce` exists, and why `Known(Long, "D")` is a sanctioned quirk rather than a bug.

---

## Coercion (`core/Coerce.kt`)

A register holds one of eight boxes: `Byte`, `Short`, `Int`, `Char`, `Boolean` (Dalvik's 1/0) or `Float` for a
32-bit register, `Long` or `Double` for a pair. Dalvik's tags are ambiguous — one `const` serves
int/char/byte/short/float, one `const-wide` serves long/double — so any opcode may read any box. Each reader
answers what a device reads: **the register's bits at the reader's width.** A read answers null only when the
engine does not hold all the bits, and the caller writes `Value.Unknown(resultType)`.

| Read | Answer | Null for |
|---|---|---|
| `toInt` (32b) | the register's 32 bits as a signed int: integral boxes truncate to their low 32 bits, `Char` its code point, `Boolean` 1/0, `Float` `floatToRawIntBits`, `Double` the low word of `doubleToRawLongBits` (the low register of the pair) | any other object |
| `toLong` (64b) | `Long` itself, `Double` `doubleToRawLongBits` | a 32-bit box: the other half is `register + 1`, which the engine's one value per pair does not hold |
| `toFloat` (32b) | `intBitsToFloat(toInt)`; a `Float` passes through | any other object |
| `toDouble` (64b) | `longBitsToDouble(toLong)`; a `Double` passes through | a 32-bit box, as `toLong` |

So `2.9f` read as an index is `0x4039999A` = 1077516698, `-0.5f` read as a `new-array` length is
`0xBF000000` = -1090519040, and `const v0, 0x3f800000` then `add-float` adds `1.0f`. A value conversion
([JLS] §5.1.3) of the box would answer 2, 0 and 1.0653532E9 — constants no runtime produces. "Any other
object" covers non-numeric raws and numeric boxes no register holds (`BigDecimal("7.9").toInt()` is 7).

**Divergence from ORACLE** (`org/cf/util/Utils.java:56-104` `castToPrimitive`): the oracle converted every
`Number` by value and returned a non-numeric raw unchanged, failing the cast at the call site with a host
`ClassCastException`. The core answers by bits, and `Unknown` where the oracle crashed
(`binary-add-int-uncoercible-operand`, `cmp-float-uncoercible-operand`).

**Read by bits, convert by value.** `float-to-int` reads at float width and then converts the value it read,
which is what the opcode does. **Measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** for
`double-to-int` on a real double: `NaN → 0`, `+Inf → 2147483647`, `-Inf → -2147483648`, a finite
`±(2^31 + k)` saturates to `Int.MAX`/`Int.MIN`, and `-0.5 → 0` (rounds toward zero). [JLS] §5.1.3
corroborates.

**A long shift's distance is one 32-bit register**, so `BinaryMathOp` reads it with `toInt`; reading it as a
pair would answer null for every `I`-typed distance.

---

## Arithmetic and compare (Plan 4)

### `UnaryMathOp` — 21 opcodes

**ORACLE** `opcode/UnaryMathOp`, `perform` for the computation and the companion's `build` for the operands.
All `Instruction12x`: `registerA` = dest, `registerB` = src. (Cited as `:35-110` until the whole-branch
review's M9 — a real span, but it covers `perform` through `getResultTypeName` and the register claim lives
in `build`, *outside* it. Cite the enclosing function by NAME, which is the rule this rewrite adopted for its
own tests and had not applied to the oracle side.)
Result type is the **last hyphen-segment** of the mnemonic (`int`→`I`, `long`→`J`, `float`→`F`,
`double`→`D`, `byte`→`B`, `char`→`C`, `short`→`S`).

| Mnemonic group | Computation |
|---|---|
| `neg-{int,long,float,double}` | negate after coercing to that domain |
| `not-{int,long}` | `inv()` after coercing to Int / Long |
| `int-to-{byte,char,short,long,float,double}` | coerce to **Int**, then convert |
| `long-to-{int,float,double}` | coerce to Long, then convert |
| `float-to-{int,long,double}` | coerce to Float, then convert |
| `double-to-{int,long,float}` | coerce to Double, then convert |

Unknown or uncoercible source → `Value.Unknown(resultType)`. **Never throws.**

### `CmpOp` — 5 opcodes

**ORACLE** `opcode/CmpOp`, the companion's `build` for the operands and `cmp` for the comparison.
`Instruction23x`: dest / lhs / rhs. Result type always `I`, set in `execute`. (Was `:25-71`, a span across
`execute` + `toString` + `cmp` — M9.)

- Either operand unknown or uncoercible → `Value.Unknown(I)`.
- Either operand NaN → `1` for `cmpg-*`, `-1` for `cmpl-*`.
- Otherwise `Long.compare` for `cmp-long`, and IEEE-754 `>` / `==` / `<` for the float and double forms, so
  `-0.0` and `0.0` compare equal — ART's `HandleCmpl`/`HandleCmpg` (`interpreter_switch_impl-inl.h`).
  `Float.compare`/`Double.compare` are not this rule: they order `-0.0` below `0.0`.

Do **not** port the oracle's two `assert(...)` lines (`CmpOp.kt:31-32`): Java asserts are off by
default so they never ran, and coercion is what actually handles mixed operands. (Gradle's `Test` task
*does* enable assertions, so porting them would newly fire.)

### `BinaryMathOp`

**ORACLE** `opcode/BinaryMathOp`, `getMathOp` and `getMathOperandType` for the mnemonic parse and the four
`do*Operation` functions for the arithmetic. Operator = first hyphen-segment; operand domain = second
segment before any `/`. Parse **by segment**, never `startsWith`, or `rsub` is read as `sub`. (Was
`:49-240`, effectively the whole class body — M9.)

| Operator | `I` | `J` | `F` / `D` |
|---|---|---|---|
| ADD SUB MUL DIV REM | ✓ | ✓ | ✓ |
| AND OR XOR | ✓ | ✓ | — (no such opcode) |
| SHL SHR USHR | `lhs op (rhs and 0x1f)` | `lhs op rhs.toInt()` | — |
| RSUB | `rhs - lhs` | — | — |

Kotlin's `shl`/`shr`/`ushr` already mask (5 bits Int, 6 bits Long).

| dexlib shape | Example | dest | lhs | rhs |
|---|---|---|---|---|
| `Instruction23x` | `add-int v0, v0, v1` | `registerA` | `registerB` | `registerC` |
| `Instruction12x` (`/2addr`) | `add-int/2addr v0, v1` | `registerA` | **`registerA`** | `registerB` |
| `NarrowLiteralInstruction` (`/lit8` `22b`, `/lit16` `22s`) | `add-int/lit8 v0, v0, 0xf` | `registerA` | `registerB` | literal, typed `I` |

The `/2addr` row is the trap (`BinaryMathOp.kt:125`): `vA` is both dest and first operand, so getting
it wrong makes `addInt2Addr()V` compute `v1 + v1` — a plausible wrong number. `rsub-int` has **no**
three-register form.

**Divide-by-zero routing.** Only the integral domains throw; `F`/`D` divide and remainder by zero yield
±Infinity or NaN, never an exception. **Measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`:**
`div-float`/`div-double` (JVM counterparts `fdiv`/`ddiv`) give `±x / ±0 = ±∞` with the sign following the
IEEE rule (`+1 / -0.0 = -∞`, both-negative cancels to `+∞`), and `0 / 0 = NaN`; `rem-float`/`rem-double`
(`frem`/`drem`) give `NaN` for a finite value modulo either signed zero. IEEE-754, via those JVM counterparts,
corroborates.

| Operator, domain | lhs, rhs | Transitions |
|---|---|---|
| DIV/REM, `I`/`J` | both known, rhs coerces to 0 | `[Throw(ArithmeticException)]` — definite |
| DIV/REM, `I`/`J` | both known, rhs ≠ 0 | `[Continue]` |
| DIV/REM, `I`/`J` | divisor unknown/uncoercible/cross-domain | `[Continue, Throw(ArithmeticException)]` — fork |
| anything else | any | `[Continue]` |

**Two ORACLE defects here, running in opposite directions, both registered.** `BinaryMathOp.kt:64-67`
gates the exceptional fork on `result.isUnknown && operator == DIV`, so the oracle (a) never forks for
`REM` although `rem` by zero throws exactly as `div` does — its own `doIntegerOperation` catches both
at `:185` — and (b) forks on an unknown **dividend** over a known non-zero divisor, exploring a throw
that cannot happen. Registered as `ORACLE_MISJUDGES_FEASIBILITY` with both address sets.

---

## Sources

Fetched and verified 2026-07-26. **Both `android.googlesource.com/platform/dalvik/+/master/docs/`
bytecode URLs are dead (404)** — those files were removed from the repo. Use these.

| Tag | Document | URL |
|---|---|---|
| **[BC]** | Dalvik bytecode format | `https://source.android.com/docs/core/runtime/dalvik-bytecode` |
| **[IF]** | Dalvik instruction formats | `https://source.android.com/docs/core/runtime/instruction-formats` |
| **[DEX]** | Dalvik Executable format | `https://source.android.com/docs/core/runtime/dex-format` |
| **[CON]** | Dalvik bytecode constraints | `https://source.android.com/docs/core/runtime/constraints` |
| **[VER]** | Dalvik Bytecode Verifier Notes | `https://android.googlesource.com/platform/dalvik/+/refs/heads/main/docs/verifier.html` |
| **[RT]** | Android runtime and Dalvik | `https://source.android.com/docs/core/runtime` |
| **[JVMS]** | JVM Spec SE21 | `https://docs.oracle.com/javase/specs/jvms/se21/html/` |
| **[JLS]** | Java Language Spec SE21 | `https://docs.oracle.com/javase/specs/jls/se21/html/` |
| **[ART]** | ART implementation source | `https://android.googlesource.com/platform/art/+/refs/heads/main/` |
| **[DEVICE]** | Empirical measurement on real ART via this project's own harness — **a measurement, not a document**. Tier A `tools/art-runner/` (`art_standalone 0_git20251009-r2`, `-Xint`); Tier B `tools/emulator/` (real API 36, `nterp` default or `-Xint`) | `tools/art-runner/README.md` · `tools/emulator/README.md` |

**[DEVICE]** is the one bracket in this table that is a *measurement* rather than a reading: it is **tier-1**,
cited `[DEVICE](tier, interpreter mode, artifact version)` with the parenthetical MANDATORY, and its full
definition, ranking rule, two exceptions and falsification rule live in [the runtime-claim
ranking](#the-tiers-are-not-equally-strong--the-ranking-for-a-claim-about-what-the-runtime-does). Added
2026-08-05 (Plan art-runner, Task 10), when the harness that produces it had landed and measured the whole
class-B backlog — recorded here so the tag never reads as a fabricated bracket on inspection, the same reason
[JLS] earned a row below.

**[JLS]** was added 2026-07-27 (title verified as *"The Java® Language Specification, Java SE 21 Edition"*).
It had been cited in bracketed form by four source files — `core/Value.kt` and `core/ValueTest.kt`
(§4.2 integral types), `core/op/ArrayLengthOpTest.kt` and `core/op/AGetOpTest.kt` (§4.10.3 array
invariance) — while having no row here, which made those four look like fabricated tags on inspection.
(This sentence named `core/CoreTester.kt` instead of `core/ValueTest.kt` until the whole-branch review's I2.
`CoreTester.kt` has never contained the string `JLS` at any commit in this branch — it discusses
covariance/invariance in *untagged* prose, which is presumably where the confusion came from. The real set is
`git grep -l '\[JLS\]' -- '*.kt'`, and a wrong file in the one sentence whose job is to certify that four
bracketed tags were **not** fabricated undermines the certification it performs.) The
prose below sometimes uses the unbracketed form ("JLS §4.10.3"); either is fine now that the tag resolves.

**[CON] is the document this rewrite was missing**, and it has no inbound nav link from [BC], which is
presumably why. It enumerates the verifier's static (`A*`) and structural (`B*`) constraints as three
tables of exactly two columns, `Identifier` | `Description` -- six `<th>` elements on the whole page,
verified directly against the page's raw HTML. **There is no "Spec equivalent" column and no
per-constraint JVMS mapping.** An earlier draft of this document asserted one and called it "the
citation to reach for whenever a claim is INHERITED rather than SPEC"; that column does not exist and
must not be cited.

The real bridge for an INHERITED claim is **[VER]**'s own framing sentence, quoted in full below:
Dalvik's verifier performs "generally the same set of checks... as are described in *The Java Virtual
Machine Specification, **Second Edition**, including the updates planned for the Third Edition*." That
is a same-*document* bridge, not a same-*edition* one: [VER] names the Second/Third Editions, while
every [JVMS] citation in this file is **SE21**. The array-related rules this file leans on -- [JVMS]
§6.5's `aaload`/`aastore`/`arraylength`, §4.10.3 subtyping -- are unchanged across every edition from
Second through 21, so the inheritance still holds for *those* rules, and the mapping this file must never
imply is a same-edition, per-constraint one, because that does not exist.

#### The bridge carries THREE unmeasured leaps — state them, do not gloss them

**This gap used to be written off as *"harmless for the rules relied on here"*, in this file and in Plan 8's
document. That gloss is withdrawn (2026-08-03): "harmless" is a measurement nobody has taken.** The whole
INHERITED tier rests on that one sentence, and getting from it to any claim in this file takes three steps,
none of which has been checked:

1. **Subject.** The sentence is about **what the verifier checks**. Every use of it here licenses a claim
   about **what the interpreter does**. Those are different subjects and different ART code — the verifier
   is `runtime/verifier/method_verifier.cc`, the interpreter is
   `runtime/interpreter/interpreter_switch_impl-inl.h`, and this file cites both, separately, precisely
   because they disagree in places (`check-cast` narrows a register in the verifier and writes nothing in
   the interpreter). A verifier that checks the same things as the JVM's does not entail an interpreter that
   *does* the same things.
2. **Strength.** *"Generally the same set of checks"* is not *"the same set"*, and **the gap is known to be
   non-empty** — this project documented the exception itself: [Dalvik's verifier is *more* restrictive than
   the JVM's on sub-32-bit integer
   widths](#and-the-same-document-immediately-names-an-exception-to-it-which-nothing-here-recorded). One
   documented divergence found means the set difference is not empty; its size is unknown.
3. **Vintage.** [VER] names JVMS **Second Edition** (2001) *"including the updates planned for the Third
   Edition"*. Every [JVMS] citation in this file is **SE21** (2023). Individual rules have been checked
   across the gap one at a time (§5.4.3.1, §5.5, §6.5's array clauses, §4.10.3); the *bridge* has not, and
   cannot be, because there is no edition-delta document to check it against.

**So state which leap a given claim depends on, or pair it with a tier-2/tier-3 citation** (see [the
ranking](#the-tiers-are-not-equally-strong--the-ranking-for-a-claim-about-what-the-runtime-does)). Writing
"harmless" here means asserting all three leaps at once, silently, in the one sentence whose job is to be
explicit.

### The finding that reframes every exception claim in this rewrite

The raw HTML of [BC], [IF] and [DEX] was grepped for every relevant exception name. Complete result:
**two `Exception` types — four occurrences of `ArithmeticException` and one of `ClassCastException`,
in [BC] — plus `BootstrapMethodError` twice (once as `java.lang.BootstrapMethodError`, once bare), both in the `invoke-custom` prose.**
Zero occurrences of `NullPointerException`, `ArrayIndexOutOfBoundsException`,
`NegativeArraySizeException`, `ArrayStoreException`, `NoClassDefFoundError`, `OutOfMemoryError` or
`IllegalAccessError` across all three.

> **This bullet used to end *"Nothing else."* and that was FALSE** — corrected 2026-07-28 in Plan 8
> Task 1 after the plan's Global Constraints re-verified it against [BC]'s own HTML with tags stripped
> and whitespace flattened (the rendered page wraps quotes, so a naive `grep` reports a present
> sentence as absent). The load-bearing half — the seven-name zero list above — survives untouched;
> the falsifiable half was the absolute, and it was wrong. Say *"two `Exception` types, plus
> `BootstrapMethodError` for `invoke-custom`"*.

So the Dalvik bytecode reference **specifies exactly two `Exception` types**, and neither is an array
exception. Any sentence of the form "the Dalvik spec says `aget` throws NPE on a null array" is
falsifiable by grep. The correct form is: *Dalvik is silent; [JVMS] §6.5 `iaload` states it, and the
inheritance is legitimate because…*

…**[VER]**, which is the bridging authority — quote it when you claim inheritance:

> "The bytecode verifier in the Dalvik VM attempts to provide the same sorts of checks and guarantees
> that other popular virtual machines do. We perform generally the same set of checks as are described
> in *The Java Virtual Machine Specification, Second Edition*, including the updates planned for the
> Third Edition."

**Re-fetched and confirmed verbatim 2026-07-28**, from
`android.googlesource.com/platform/dalvik/+/refs/heads/main/docs/verifier.html?format=TEXT` piped
through `base64 -d`. This mattered enough to check because **every INHERITED tag in this project rests
on this one sentence**, and Plan 8's review flagged it as never having been opened. It is real. Note for
anyone re-checking: `[VER]` is this **googlesource** document, *not*
`source.android.com/docs/core/runtime/constraints` — that is `[CON]`, a different page, and fetching it
by mistake returns 370 KB of JavaScript shell whose only `verif` hits are sidebar navigation, i.e. a
convincing-looking zero result.

#### …and the same document immediately names an exception to it, which nothing here recorded

> "The Dalvik verifier is **more restrictive than other VMs in one area: type safety on sub-32-bit
> integer widths.** These additional restrictions should make it impossible to, say, pass a value
> outside the range [-128, 127] to a function that takes a byte as an argument."

**VERIFIER**, [VER]. Two consequences, and the second caps the bridge above:

1. In *verified* code a `B`-tagged register really does hold [-128, 127], a `Z` really is 0 or 1, and so
   on — so this is a genuine guarantee an op may lean on for **verified** input, and correspondingly the
   engine's behavior for a `B`-tagged 5000 is a **design choice** justified by soundness direction, not a
   Dalvik fact.
2. **"Generally the same set of checks" is not "the same set", and this is the documented place it
   differs — in the *stricter* direction.** So an INHERITED claim about sub-32-bit integral types can be
   *weaker* than Dalvik's actual guarantee. Inheritance stays legitimate for the rules this file cites
   (they are width-agnostic), but a future claim about `Z`/`B`/`S`/`C` widths must be checked against
   [VER] rather than taken from [JVMS] alone.

#### `const-class` on the same class yields the SAME reference

> "the verifier does not attempt to identify previously-made copies, track loads and stores through
> fields, or recognize identical constant values (for example, **the result values from two `const-class`
> instructions on the same class will be the same reference**, but the verifier doesn't recognize this)"

**SPEC-adjacent**, [VER], stated as a parenthetical about monitor verification but asserting a runtime
fact directly: the reference is the same. It is recorded here because **reference identity and
`const-class` land in the same plan (Plan 8)**, and an `if-eq` over two `const-class` results of one
class is therefore **provably equal** — not an ambiguous predicate. Getting that wrong forks a branch
that cannot diverge, which is the invents-a-live-branch defect. The surrounding passage also documents
that the verifier rejects hand-over-hand locking and only permits exiting the most recently entered
monitor, which is Plan 12's business, not Plan 8's.

**Task 4 wrote the FIXTURE rather than trusting the interaction to hold by itself.**
`if_test.smali`'s `ifEqualTwoConstClassesOfOneClass()V` is `const-class v0, Lif_test;` /
`const-class v1, Lif_test;` / `if-eq v0, v1, :same`, and
`core/op/IfOpTest.twoConstClassResultsForOneClassMustBeOneReferenceAndNotAFork` runs it. Today
`const-class` is an `UnsupportedOp`, so that test asserts `Partial(UNSUPPORTED_OP)` **and therefore fails
on arrival the day Task 10 lands the opcode** — which is the point: whether two `const-class` executions
yield one host object is otherwise an untested detail of Task 10's cache, and an untested assumption
there silently converts a provable equality into a fork. Task 10 must make the resolved class object
identity-stable (interned `VirtualType`, or a per-session cache) and replace that test's body with the
one-successor assertion its KDoc spells out. `core/Provable.kt`'s `sameReference` is already the predicate
that will see it, and it needs no change for this.

### Why a real runtime performs none of these checks, and what that means here

**[VER]**, "Why Verify?" — the single most load-bearing citation for this engine:

> "**Optimizations.** The interpreter can ignore a lot of potential error cases because the verifier
> guarantees that they are impossible."

That is the formal justification for what ART's interpreter actually does: `HandleAGet`/`HandleAPut`
`DownCast` the array **unchecked**, `ARRAY_LENGTH` calls `AsArray()` unchecked, `FillArrayData`
(currently `runtime/entrypoints/entrypoint_utils.cc:175` — it has moved over time; not
`interpreter_common.cc`) `memcpy`s with no width check and guards the object-array case with a
release-mode-`DCHECK` (a no-op).

**Consequence, and it is a design constraint rather than a fidelity target:** for input the verifier
would have rejected, ART has *no defined behavior* — it reinterprets memory at a mis-scaled stride.
"Match ART" is incoherent for those cases. They are exactly what a hostile obfuscator constructs, so
they need an explicit ambiguity/refusal policy justified by spec §2.2, and the plan must say so instead
of inventing an exception and attributing it to Dalvik.

**[RT]** adds the direction of drift, which matters because "the old engine accepted it" proves little:

> "ART also has tighter install-time verification than Dalvik. … **However, some post-processing tools
> produce invalid files that may be tolerated by Dalvik but cannot be compiled by ART.**"

### Exception *messages* are version-dependent — compare types, not strings

**[RT]:**

> "ART provides expanded exception detail for java.lang.ClassCastException,
> java.lang.ClassNotFoundException, and java.lang.NullPointerException. (Later versions of Dalvik
> provided expanded exception detail for java.lang.ArrayIndexOutOfBoundsException and
> java.lang.ArrayStoreException, which now include the size of the array and the out-of-bounds
> offset…)"

So a message string is **never** evidence of Dalvik fidelity in general. Where this corpus asserts a
message (`ArrayLengthOpTest.nullArrayThrowsExpectedException` does), that assertion is **CORPUS** tier —
it must match for the migration to be byte-identical, and that is the whole of its justification.

**One upgrade, specific to this fixture rather than to messages in general.** [ART]'s per-opcode NPE
messages are known, and this one happens to match: `common_throws.cc:639-667`
(`ThrowNullPointerExceptionFromDexPC`, declared at `:566`) hard-codes `"Attempt to get length of null array"` for
`ARRAY_LENGTH`, `"Attempt to read from null array"` for the `aget*` family, and `"Attempt to write to
null array"` for the `aput*` family (`FILL_ARRAY_DATA` routes through the same write message). So
`ArrayLengthOpTest`'s message assertion is **CORPUS + ART**, not CORPUS alone — it happens to match a
real, current ART build, not merely the oracle's own text. That is still not a fidelity *guarantee*
across ART versions ([RT]'s warning above still applies going forward), but it is stronger evidence
than "the oracle says so" alone.

---

## Arrays and the heap (Plans 6–7)

**Nothing in this section is SPEC except where marked.** [BC]'s entire specification for the fourteen
`aget*`/`aput*` opcodes is one sentence, shared by all of them:

> "Perform the identified array operation at the identified index of the given array, loading or storing
> into the value register."

### `new-array` (op 23, 22c)

**SPEC** [BC], the complete text: *"Construct a new array of the indicated type and size. The type must
be an array type."*

| Condition | Behavior | Tier |
|---|---|---|
| negative size | `NegativeArraySizeException` | **INHERITED** — [JVMS] §6.5 `newarray`: *"If count is less than zero, newarray throws a NegativeArraySizeException"*; same for `anewarray` |
| its **message** is the size in decimal | e.g. `"-1"` | **[ART]** — `runtime/common_throws.cc:389-392`, quoted verbatim below. Neither [BC] nor [JVMS] specifies any message, so this is the only source for it, and per [§Exception messages](#exception-messages-are-version-dependent--compare-types-not-strings) a message is never evidence of Dalvik fidelity — it is asserted only so it cannot drift silently |
| size 0 | ordinary allocation, no special case | **UNSPECIFIED** anywhere as a special case — do not cite a sentence |
| size register holding a `Float`/`Double` raw | the register's 32 bits, as any length: `-0.5f` is `0xBF000000` = -1090519040, so the negative-size row above throws | **VERIFIER** → engine's choice ([CON] B1; **no [ART] verifier symbol is claimed** — there is no `VerifyNewArray` in ART, per the two-verifier-functions note in the [`filled-new-array` section](#filled-new-array-op-24-35c-and-range-op-25-3rc)); the read is [§Coercion](#coercion-corecoercekt)'s |
| fresh element values | `0` / `\u0000` / **positive** zero / `false` / `null` | **Measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** for `F`/`D`: a fresh `float[]`/`double[]` element read back through `floatToRawIntBits`/`doubleToRawLongBits` is `0x0` — **positive** zero, never `0x80000000`/`0x8..0`. [JVMS] §6.5 (*"initialized to the default initial value"*) + §2.3/§2.4 corroborates |
| component type unresolvable | a **linking error** per [JVMS] §5.4.3.1 — i.e. **`NoClassDefFoundError`**, an `Error` | *That* it is a linking error is **INHERITED** ([JVMS] §6.5 `anewarray`: *"During resolution of the symbolic reference to the class, array, or interface type, any of the exceptions documented in §5.4.3.1 can be thrown"*); *which* — `NoClassDefFoundError` — is now **measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** ([Resolution failure is one family](#resolution-failure-is-one-family-tiered-once)) |
| dimensions ≥ 256 | rejected | **VERIFIER** — [CON] A19 |
| type index invalid | rejected | **VERIFIER** — [CON] A18 |

> ⚠ **[CON] A21** (*"The type referred to by a new-array instruction must be a valid, non-reference
> type"*) contradicts [BC]'s own "must be an array type" and contradicts observable reality
> (`new-array v0, v1, [Ljava/lang/Object;` is ubiquitous). Treated as a **documentation defect**. Do not
> implement A21 as written.

**Ordering, when both the class is absent and the size is negative: a LABELLED DIVERGENCE from an
INHERITED rule — not an implementation-defined free pick.** The inherited order is **resolution FIRST**:
[JVMS] §6.5 `anewarray` places its *"Linking Exceptions"* block **before** its *"Run-time Exceptions:
Otherwise, if count is less than zero…"* block, and that `Otherwise` chaining is the same normative device
this document relies on for `aastore`'s NPE → AIOOBE → `ArrayStoreException` order. Reading it as
normative for `aput` and absent for `new-array` is not a defensible reading of one document.

**ART inverts it.** `CheckArrayAlloc` (`runtime/entrypoints/entrypoint_utils-inl.h:292-322`) tests
`if (UNLIKELY(component_count < 0))` **before** `GetResolvedType`/`ResolveType`. (Not
`ResolveVerifyAndClinit` — an array class has no `<clinit>`.)

**Both halves of that, quoted.** Fetched and verified 2026-07-27 from [ART] at
`refs/heads/main`, and both endpoints of `:292-322` re-counted with `awk` line numbering: `:292` is the
`ALWAYS_INLINE` attribute, `:293` the signature, `:322` the closing brace. **The range was correct when
written and is correct now, and there has been no drift** — an earlier revision of this paragraph claimed
*"one line of drift"* on the strength of the signature sitting at `:293`, which is a fabricated verification
narrative (the whole-branch review's M5) of the same shape as the shipped `:358-365` → `:357-364` regression.
Note also that a line inserted above the function would have moved the closing brace too, and it has not.
`entrypoint_utils-inl.h:297-305`:

```cpp
  if (UNLIKELY(component_count < 0)) {
    ThrowNegativeArraySizeException(component_count);
    *slow_path = true;
    return nullptr;  // Failure
  }
  ObjPtr<mirror::Class> klass = method->GetDexCache()->GetResolvedType(type_idx);
  if (UNLIKELY(klass == nullptr)) {  // Not in dex cache so try to resolve
    ClassLinker* class_linker = Runtime::Current()->GetClassLinker();
    klass = class_linker->ResolveType(type_idx, method);
```

That is the size check textually preceding both `GetResolvedType` and `ResolveType`, which is the whole
ordering claim. It also settles **which overload** formats the message — `component_count` is an `int32_t`,
so it selects the `int` one. `common_throws.cc:389-392`:

```cpp
void ThrowNegativeArraySizeException(int size) {
  ThrowException("Ljava/lang/NegativeArraySizeException;", nullptr,
                 StringPrintf("%d", size).c_str());
}
```

(There is a second, `const char*` overload at `:394-396` that passes a caller-supplied string through
untouched; the array-allocation path does not use it.) So the decimal-size message is **[ART]**-grounded
end to end, and no part of it is [BC]'s or [JVMS]'s.

**The engine follows ART, recorded as a divergence.** Reason: the engine predicts what an APK does on a
device, and no device runs the JVMS order. Consequence, which must be visible to a reader of the code:
on a both-bad `new-array`, an obfuscator's `catch (NegativeArraySizeException)` is reachable under our
answer and its catch of a linking error is not; a strict-JVMS reading reverses that. Because it is a
labelled divergence from an INHERITED rule, it carries a fixture (Plan 7 Task 6) rather than a comment.

> 🔴 **This corrects a wrong claim.** Plan 7's document originally said an unloadable component type
> throws a guest **`ClassNotFoundException`**, "matching the oracle and Dalvik". The oracle part is
> true and the Dalvik part is false: `ClassNotFoundException` is what the **host** `ClassUtils.getClass`
> throws inside `Utils.buildArray`, leaking host reflection into guest semantics. The runtime raises
> `NoClassDefFoundError`. **Why it matters more than a name:** `NoClassDefFoundError extends Error`, so
> `catch (Exception e)` — ubiquitous in obfuscator-generated wrappers — does **not** catch it, whereas
> `ClassNotFoundException extends Exception` **is** caught. Getting this wrong makes the engine explore
> a handler the runtime can never enter, which is the over-approximating wrong answer. Registered as an
> oracle defect. **Measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`:** `new-array` of an absent
> component throws `NoClassDefFoundError`, not `ClassNotFoundException` — confirming the correction above.
> The oracle still host-raises `ClassNotFoundException` for `new-array`/`filled-new-array`/`const-class`, and
> host-CRASHES on `instance-of`/`check-cast` of an absent type; both remain the registered oracle defect,
> and both are pinned in `DeviceDifferentialTest`'s bucket map rather than smoothed over.

### `array-length` (op 21, 12x)

**SPEC** [BC]: *"Store in the given destination register the length of the indicated array, in
entries"* — and note it reads **the array object**, not any static type. Null array →
`NullPointerException` (**INHERITED**, [JVMS] §6.5 `arraylength`: *"If the arrayref is null, the
arraylength instruction throws a NullPointerException"*).

`array-length` on a **non-array** object is verifier-prevented only; ART's `ARRAY_LENGTH` calls
`array->AsArray()` unchecked and reads whatever word sits at the length offset (**VERIFIER** →
engine's choice).

### `aget*` / `aput*` (ops 44–51, 23x)

| Condition | Behavior | Tier |
|---|---|---|
| null array | `NullPointerException` | **INHERITED** — [JVMS] §6.5 `iaload`/`aaload`/`iastore` |
| index outside bounds, **including negative** | `ArrayIndexOutOfBoundsException` | **INHERITED** — [JVMS]; ART compares unsigned (`Array::CheckIsValidIndex`), so a negative index is AIOOBE and not a distinct exception. Now also **MEASURED — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`**: `Fx_AputNegativeIndex` throws AIOOBE for index -1 |
| the AIOOBE's **message** | `"length=%d; index=%d"`, i.e. `"length=5; index=10"` — **not** the bare index | **[ART] only** — `runtime/common_throws.cc:131-134`, quoted verbatim below. Neither [BC] nor [JVMS] specifies any message, so this is the only source for it, and per [§Exception messages](#exception-messages-are-version-dependent--compare-types-not-strings) a message is never evidence of Dalvik fidelity. **The engine does not match it** — `AGetOp`/`APutOp` emit the bare decimal index as a labelled *pinned engine choice*, which this row exists to make visible rather than to bless |
| ordering | **NPE strictly precedes AIOOBE precedes ASE** | **MEASURED — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** — five `aput-object` fixtures pin every edge (blockquote below); the inherited "Otherwise" chaining in [JVMS] `aastore` **corroborates** and was right |
| variant ↔ component mismatch | verifier hard failure, **never** a runtime exception | **VERIFIER** — [CON] B1; [ART] `VerifyAGet`/`VerifyAPut` |
| variant mismatch on **unverified** input | unchecked `DownCast`, mis-scaled memory read — no defined behavior | **VERIFIER** → engine's choice |

> ✅ **MEASURED on real ART, 2026-08-04 — the ordering is no longer INHERITED-alone.**
> `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, pinned in `tools/art-runner/golden/tier-a-batch.txt`
> (`tools/art-runner/check-golden.sh`). Each fixture makes exactly two of {null array, out-of-range index,
> unassignable value} true at ONE `aput-object`, so exactly one exception can win, and the observed TYPE —
> never the message, per [§Exception messages](#exception-messages-are-version-dependent--compare-types-not-strings)
> — names it:
>
> ```
> FIXTURE	Fx_AputNullArrayBadIndex	THREW	java.lang.NullPointerException	Attempt to write to null array
> FIXTURE	Fx_AputNullArrayIncompatibleValue	THREW	java.lang.NullPointerException	Attempt to write to null array
> FIXTURE	Fx_AputBadIndexIncompatibleValue	THREW	java.lang.ArrayIndexOutOfBoundsException	length=1; index=5
> FIXTURE	Fx_AputNegativeIndexIncompatibleValue	THREW	java.lang.ArrayIndexOutOfBoundsException	length=1; index=-1
> FIXTURE	Fx_AputNegativeIndex	THREW	java.lang.ArrayIndexOutOfBoundsException	length=1; index=-1
> ```
>
> That is NPE before AIOOBE (`Fx_AputNullArrayBadIndex`), NPE before ASE (`Fx_AputNullArrayIncompatibleValue`),
> and AIOOBE before ASE twice — a too-large index (`Fx_AputBadIndexIncompatibleValue`) and a negative one
> (`Fx_AputNegativeIndexIncompatibleValue`): the full strict order **NPE precedes AIOOBE precedes ASE**.
> `Fx_AputNegativeIndex` (index -1, assignable value) throws AIOOBE on its own, so a negative index is that
> same bounds exception and not a distinct one (the row above). **The inherited [JVMS] `aastore` "Otherwise"
> chaining was right**, recorded as corroboration because row 8 (`new-array`) is the case where an inherited
> ordering turned out backwards and this one did not — which is what calibrates how far to trust the next
> inherited prior. (The pre-rewrite oracle engine checks assignability BEFORE bounds and answers ASE where the
> device answers AIOOBE; the clean core matches the device. That engine gap is a registered device-sweep
> finding, not this row's claim.)

**The AIOOBE message, quoted.** Fetched and verified **2026-07-27** from [ART] at `refs/heads/main`
(`runtime/common_throws.cc?format=TEXT`, base64-decoded and read directly rather than through a summarizer —
a summarizing fetch of this same file reported the function at "lines 506-510" and invented a second overload
that was actually `ThrowNegativeArraySizeException`). `common_throws.cc:131-134`:

```cpp
void ThrowArrayIndexOutOfBoundsException(int index, int length) {
  ThrowException("Ljava/lang/ArrayIndexOutOfBoundsException;", nullptr,
                 StringPrintf("length=%d; index=%d", length, index).c_str());
}
```

Note the argument order: the message prints **length first**, though the parameters are `(index, length)`.
There is exactly **one** overload — unlike `ThrowNegativeArraySizeException`, which has an `int` and a
`const char*` form. The same fetch re-checked the two message citations this document already carried. One
resolved exactly and **one was CORRECTED at its start endpoint**; the sentence here previously said both
*"still resolve exactly"*, which was a false verification claim at `[ART]` tier and is the whole-branch
review's I1.

- `common_throws.cc:389-392` is `ThrowNegativeArraySizeException(int size)` with `StringPrintf("%d", size)` —
  resolves exactly, both endpoints re-counted.
- The per-opcode `ThrowNullPointerExceptionFromDexPC` block is **`:639-667`**, not `:645-667`. Its interior
  message lines all resolved, which is why the range read as verified: `AGET_*` is
  `"Attempt to read from null array"` (`:647`), `APUT_*` `"Attempt to write to null array"` (`:657`),
  `ARRAY_LENGTH` `"Attempt to get length of null array"` (`:661`), and `FILL_ARRAY_DATA` routes through the
  write message and closes the span at `:667`. But `:645` is `case Instruction::AGET_SHORT:` — the **last of
  seven** `AGET_*` labels (`:639`–`:645`) — so the old start excluded six of the seven cases the citation is
  made for. Not upstream drift: nothing moved, the start was mis-counted. Corrected at all six citing sites.

**The instrument, and the fact that recording it is not applying it.** This document's own *"count spans with
`awk` line numbering, never a `sed` window"* rule (below) was written to prevent exactly this, and then was
not run over the citations already in the file. **Check BOTH endpoints — an interior-correct range with a
wrong endpoint is the failure mode that survives review**, because every line a reader spot-checks resolves.
Both endpoints of every ART span in this section have now been re-counted with `awk`.

**What is NOT established, and was previously asserted as though it were:** what message *older* Dalvik used.
[RT] (`§Exception messages` above) says only that later Dalvik *"provided expanded exception detail for
java.lang.ArrayIndexOutOfBoundsException … which now include the size of the array and the out-of-bounds
offset"*. That supports "the text changed between runtimes" and nothing more. **"Old Dalvik used a bare
index" is an inference, not a quotation, and must not be cited** — it was written into `AGetOp.kt` and
`AGetOpTest.kt` during Plan 7 Task 7 and removed in that task's fix round 1.

**Which variant accepts which component type** (**VERIFIER**, [ART] `VerifyAGet`): `aget-boolean` /
`-byte` / `-char` / `-short` require exact matches; **`aget` accepts `int[]` *or* `float[]`**;
**`aget-wide` accepts `long[]` *or* `double[]`**; `aget-object` accepts any reference-component array.
The int/float and long/double sharing is why a `Value`'s tag is a hint — the opcode genuinely does not
distinguish them.

#### `ArrayStoreException` comes from `aput-object` and nothing else

Asked and answered on three independent sources. **`aput` on an `int[]` can never throw
`ArrayStoreException`.**

- **INHERITED** — [JVMS] §6.5 lists exactly two runtime exceptions for `iastore` and `bastore`: NPE and
  AIOOBE. `aastore` is the **only** array-store instruction that lists ASE: *"if arrayref is not null
  and the actual type of the non-null value is not assignment compatible with the actual type of the
  components of the array, aastore throws an ArrayStoreException."*
- **Storing `null` never throws ASE** — [JVMS] `aastore`: *"If value is null, then value is stored as
  the component of the array at index."* Corroborated by ART's `CheckAssignable`, which is guarded by
  `if (object != nullptr)`.
- **[ART]** `APUT_OBJECT` is the only handler with an assignability check; the other **six** route to
  `HandleAPut<T>`, which has none. (Was "seven"; corrected in Plan 7 Task 9's fix round. There are **seven**
  `aput*` opcodes in total, 0x4b–0x51, so six of them are non-object — the arithmetic is what fixes the
  number, and `core/op/APutOpTest` says six.)
- **ASE is a runtime check by design**, not a verifier gap. [ART] `VerifyAPut`, in a comment that is the
  clearest statement of the boundary in any source (`method_verifier.cc:5192-5194`, quoted verbatim — the
  source comment carries **no terminal period** and an earlier revision added one):
  *"we use the instruction type (rather than the
  component type) for aput-object as incompatible classes will be caught at runtime as an array store
  exception"* `insn_type` for `aput-object` is `java.lang.Object`, so any reference passes verification.
  [CON] B15 is the static approximation.

> 🔴 **This corrects a wrong *reason*.** Plans 6–7 justified exempting the primitive `aput` families with
> "Dalvik represents all five identically in bytecode." That is not the reason (and the premise is
> shaky — the opcodes *are* distinct). The reason is the JVMS rule above: only object stores are
> type-checked at all. Same behavior, sound justification.

### `filled-new-array` (op 24, 35c) and `/range` (op 25, 3rc)

**SPEC** [BC] — unusually explicit here, so quote it: *"Construct an array of the given type and size,
filling it with the supplied contents. The type must be an array type. The array's contents must be
single-word (that is, no arrays of long or double, but reference types are acceptable). The constructed
instance is stored as a "result" … so the constructed instance must be moved to a register with an
immediately subsequent move-result-object instruction (if it is to be used)."*

- `[J`/`[D` are **forbidden by SPEC**. [ART] makes it a verifier hard failure and cites this very page.
- The `move-result-object` requirement is **SPEC**; [CON] B19/B20 restate it as verifier constraints.
  Both are Dalvik-specific with no JVM analogue -- not because [VER] enumerates them (it does not list
  individual constraint IDs at all; see the corrected note above), but because the JVM's stack-based
  `invoke*` leaves its result on the operand stack, so there is no separate "move a result into a
  register" instruction for the JVM to specify in the first place. *(B20's text says "preceded … by a
  matching `return-<kind>`"; that is a typo for `invoke-<kind>`, present in both published copies.)*
- Negative size is **structurally impossible**: `A` is 4 bits (≤5) or 8 bits (≤255).

**Every exception, with the JVM-counterpart question answered — added by Plan 7 Task 11, which the
corrected tiering rule (`c0281bc`) names as needing this check.** `filled-new-array` is **not** an
instruction the JVM has: the JVM allocates with `anewarray`/`newarray` and then fills with a
`dup`/index/value/`*astore` sequence, so there is no single counterpart to inherit from.

| Condition | Behavior | Tier |
|---|---|---|
| unresolvable type reference | a linking error — `NoClassDefFoundError` / `IllegalAccessError` | *That* it is a linking error is **INHERITED, but via the resolution PROCEDURE rather than a counterpart instruction** ([JVMS] §5.4.3.1 governs resolution of *any* class/interface/array symbolic reference; the allocation half *does* have a counterpart, `anewarray`, whose §6.5 entry says *"During resolution of the symbolic reference to the class, array, or interface type, any of the exceptions documented in §5.4.3.1 can be thrown"*). *Which* exception — `NoClassDefFoundError` — is now **measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** ([Resolution failure is one family](#resolution-failure-is-one-family-tiered-once)). Same ruling as `new-array`'s (design decision 4), deliberately not a second answer |
| `[Z`/`[B`/`[C`/`[S`/`[F` | `java.lang.InternalError` | **[ART] only, and emphatically NOT inherited** — the JVM's `newarray` implements every primitive component, so there is nothing to inherit. See the note below |
| negative element count | **UNREACHABLE**; no check exists or should be added | **SPEC**, from the instruction format — the count is the instruction's own unsigned `A` field. ART *has* a `ThrowNegativeArraySizeException` site, and it is dead: see below |
| null array | **UNREACHABLE** — this opcode has no array *operand*; it creates the array | **SPEC**, from the instruction format |
| index out of bounds | **UNREACHABLE** — no index operand, and the array is allocated at exactly the element count | **SPEC**, from the instruction format |
| a reference element incompatible with the component | **NOT THROWN** — no `ArrayStoreException` | **VERIFIER** ([ART] `VerifyFilledNewArray` checks each argument against the component type) → and [ART] is explicit rather than merely silent at runtime: the fill is `SetWithoutChecks`. See below |
| allocation fails | `OutOfMemoryError` | **NOT MODELLED by this engine**, like every other allocation site in it. Real in [ART] — `mirror::Array::Alloc` answering null is followed by `self->AssertPendingOOMException()` — and listed here only so the transcription below is genuinely complete rather than nearly so |

**The verifier's HARD/SOFT split is where the `[J`/`[D` and `[Z`/`[B`/`[C`/`[S`/`[F` rows come from, and
[ART] draws it in one `if`/`else`.** `CheckNewArray<kFilled>`
([`runtime/verifier/method_verifier.cc`](https://android.googlesource.com/platform/art/+/refs/heads/main/runtime/verifier/method_verifier.cc)),
verified 2026-07-28 from raw source:

```cpp
if (kFilled && bracket_count == 1u && UNLIKELY(*cp != 'I' && *cp != 'L')) {
  if (UNLIKELY(*cp == 'J') || UNLIKELY(*cp == 'D')) {
    // Forbidden, see https://source.android.com/docs/core/runtime/dalvik-bytecode .
    FailBadFilledNewArray(descriptor);
    return false;
  } else {
    // Fall back to interpreter to throw `InternalError`. Compiler does not handle this case.
    Fail(VERIFY_ERROR_FILLED_NEW_ARRAY);
  }
}
```

`[J`/`[D` are a **hard** failure whose comment cites [BC] by URL — which is why this engine decodes them to
`UnsupportedOp` rather than modelling a throw; the other five are a **soft** failure whose comment says the
interpreter is expected to throw, which is why this engine *does* model that throw. The same function's
`bracket_count == 0u` arm is `FailBadNewArrayNotArray(descriptor)`, i.e. [BC]'s *"The type must be an array
type"*, and it grounds the second decode refusal (`filledNewArrayOfNonArrayType()V`).

**Two DIFFERENT verifier functions, and they must not be conflated.** `CheckNewArray<kFilled>` above checks
the **descriptor's shape**; `MethodVerifierImpl::VerifyFilledNewArray` checks the **argument registers**
against `reg_types_.GetComponentType(res_type)` via `VerifyRegisterType`, and ends with
`work_line_->SetResultRegisterType(res_type)` — the verifier's own statement that this opcode's result lands
in the *result* register rather than a numbered one. **There is no `VerifyNewArray` symbol in ART at all**; an
earlier revision of this row and of `core/op/FilledNewArrayOp`'s KDoc said there was, and a word-boundary grep
over the raw file returns zero hits. The surrounding claim was correct and only the identifier was invented,
which is the harder-to-catch shape of this family's recurring citation defect.

**`art::DoFilledNewArray`, the whole of the runtime behavior, verified 2026-07-28** from
[ART] at `refs/heads/main`
([`runtime/interpreter/interpreter_common.cc`](https://android.googlesource.com/platform/art/+/refs/heads/main/runtime/interpreter/interpreter_common.cc),
`template <bool is_range> bool DoFilledNewArray`). Fetched as raw base64 (`?format=TEXT`) and decoded, not
read through a summarizing tool — this family has shipped five unsourced or fabricated citations, so the
retrieval method is part of the claim. Line numbers are deliberately **not** cited: the file is 1500+ lines
and moves. In order, the function does — **all six steps, with no omitted throw site**:

1. `const int32_t length = is_range ? inst->VRegA_3rc() : inst->VRegA_35c();` — then, for the non-range
   form, `CHECK_LE(length, 5)`.
2. `if (UNLIKELY(length < 0)) { ThrowNegativeArraySizeException(length); return false; }`. **Both `VRegA_`
   accessors return unsigned instruction fields, so this site is DEAD** — which is the grounding for the
   "structurally impossible" line above, and the reason no engine should add a negative-count check here by
   analogy with `new-array` (where a negative length is a real runtime *register* value).
3. `ResolveVerifyAndClinit(dex::TypeIndex(type_idx), …)`, then `if (UNLIKELY(array_class == nullptr))
   { DCHECK(self->IsExceptionPending()); return false; }` — **the linking-error site**. Note this is
   `ResolveVerifyAndClinit`, unlike `new-array`'s `CheckArrayAlloc`, which uses
   `GetResolvedType`/`ResolveType`; the two opcodes genuinely take different paths, so do not
   cross-cite them.
4. `CHECK(array_class->IsArrayClass())`, then the `InternalError` block below.
5. `mirror::Array::Alloc(...)`, then `if (UNLIKELY(new_array == nullptr)) { self->AssertPendingOOMException();
   return false; }` — **the `OutOfMemoryError` site, and the fourth and last throw site in the function.** It
   is real, and this engine deliberately does not model it (nor any other allocation failure); named here
   because an earlier revision of this list claimed to be "the whole of the runtime behavior … in order" while
   skipping straight from the allocation to the fill, and a completeness claim that is not complete is exactly
   the defect shape this section's own correction note is about.
6. The fill: `if (LIKELY(is_primitive_int_component))` copies `shadow_frame.GetVReg(src_reg)` (the register's
   raw 32 bits) into an `IntArray`; otherwise, at `interpreter_common.cc:1463-1464` (both endpoints counted
   with `awk`, and the space after each `=*/` is the source's own — an earlier revision dropped it):
   `object_array->SetWithoutChecks</*kTransactionActive=*/ false, /*kCheckTransaction=*/ false>(i,
   shadow_frame.GetVRegReference(src_reg))`. **`SetWithoutChecks` is why no `ArrayStoreException` is
   possible.**

   **And `GetVRegReference` is why "a reference element is stored verbatim" is only half true — the
   whole-branch review's C1.** That is the same fetch whose primitive-clearing behaviour the
   [`throw` section](#getvregreference-is-the-whole-of-it-and-reading-throw-without-following-it-is-how-this-section-got-the-primitive-case-wrong-twice)
   establishes, so a register whose last write was **primitive** contributes **`nullptr`**, not its raw. This
   makes `DoFilledNewArray` the **sixth** ART consumer of that fetch alongside `THROW`, `ARRAY_LENGTH`,
   `HandleAGet`, `HandleAPut`, `MONITOR_*` and the field ops, and `core/op/FilledNewArrayOp`'s reference row
   the sixth consumer of `org.cf.smalivm.core.readsAsNullReference`. It was missed because Task 12's sweep
   covered the ops with an **array operand** and this one has none — but reading a register *as a reference*,
   not holding an array, is the actual trigger. `const/4 v0, 0x0` / `filled-new-array {v0}, [Ljava/lang/String;`
   — i.e. `new String[]{null}`, verifier-VALID and ordinary `d8` output — is the affected input.

   **And that verifier-VALID input is now corroborated by BOTH interpreter tiers, while the non-zero
   primitive is corroborated by neither.** nterp does not route `filled-new-array` through
   `interpreter_common.cc` at all: `x86_64ng/array.S:134-148` calls `NterpFilledNewArray`
   (`mterp/nterp.cc:758-764`), which reaches nterp's **own** `DoFilledNewArray` (`:683-756`), whose
   reference-component fill at `:751-752` is
   `SetWithoutChecks(i, reinterpret_cast<mirror::Object*>(regs[src_reg]))` — the **raw** register value cast
   straight to an object pointer. For `const/4 v0, 0x0` that cast yields `nullptr`, so the two tiers agree
   and `new String[]{null}` is doubly grounded. For `Known(5, "I")` nterp stores the integer 5 into an object
   array **as a reference**, which is the sharpest single line of evidence that
   [the two tiers diverge](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim)
   — and it makes this engine's `null` for that row a **VERIFIER**-tier choice rather than an [ART] fact.

So the function has exactly **four** throw sites: the dead `NegativeArraySizeException`, the linking error, the
`InternalError`, and the `OutOfMemoryError`. This engine models the middle two, and that accounts for all
four.

> 🔴 **Undocumented ART behavior worth knowing.** `[Z`/`[B`/`[C`/`[S`/`[F` are "single-word" and so
> appear legal per [BC], but **ART does not implement them**: the verifier soft-fails
> (`VERIFY_ERROR_FILLED_NEW_ARRAY`) and the interpreter throws
> **`java.lang.InternalError`** — *"Found type %s; filled-new-array not implemented for anything but
> 'int'"* ([ART] `DoFilledNewArray`). Effective reality: only `int[]` and reference-component arrays
> (including `[[I`) work. A plausible anti-analysis trick, and documented nowhere.
>
> **The `%s` is the COMPONENT class's `PrettyDescriptor()` — its Java SOURCE name, not a Dalvik
> descriptor and not the array's type.** Verified 2026-07-28 by reading both raw sources. The throw site
> is `self->ThrowNewExceptionF("Ljava/lang/InternalError;", "Found type %s; filled-new-array not
> implemented for anything but 'int'", component_class->PrettyDescriptor().c_str())`, and
> `AppendPrettyDescriptor`
> ([`libdexfile/dex/descriptors_names.cc`](https://android.googlesource.com/platform/art/+/refs/heads/main/libdexfile/dex/descriptors_names.cc))
> maps `B` → `"byte"`, `Z` → `"boolean"`, `C` → `"char"`, `S` → `"short"`, `F` → `"float"` (and
> `Ljava/lang/String;` → `"java.lang.String"`). So `filled-new-array {…}, [B` on a device says
> **"Found type byte; filled-new-array not implemented for anything but 'int'"**. Plan 7 Task 11's brief
> wrote `"Found type <descriptor>; …"`, which is wrong; `core/op/FilledNewArrayOp` asserts the `PrettyDescriptor`
> spelling. This is the one array-exception message the engine *does* match, and the reason is narrow: the
> whole exception is [ART]-only, so ART's text is the only thing that identifies the row at all — unlike the
> AIOOBE, whose exception has an independent source and whose message the engine deliberately does not match.

**`Opcode.canThrow()` happens to be right here and is still not consulted.** dexlib2 2.5.2 declares
`FILLED_NEW_ARRAY.flags == 0x000d`, so `canThrow()` answers **true** — verified locally against the
pinned jar. It answered **false** for `fill-array-data` while ART threw two exceptions from it, so throw
edges are decided from each opcode's own semantics either way.

### `fill-array-data` (op 26, 31t) and the `array-data` payload

**The payload is data, and SPEC says so.** [BC], "General design":

> "There are several "pseudo-instructions" that are used to hold variable-length data payloads, which
> are referred to by regular instructions (for example, fill-array-data). **Such instructions must never
> be encountered during the normal flow of execution.** In addition, the instructions must be located
> on even-numbered bytecode offsets (that is, 4-byte aligned)."

Enforced by **[CON] B22** (*"The packed-switch-data, sparse-switch-data, and fill-array-data
pseudo-instructions must not be reachable by control flow"*) -- a Dalvik-specific constraint with no
JVMS analogue (the JVM has no equivalent pseudo-instruction concept: `tableswitch`/`lookupswitch` encode
their data inline in the instruction itself rather than as a separate, otherwise-reachable unit). [BC]
also writes the operand as a *"branch"* offset in scare quotes, deliberately, and the low byte of every
payload is `0x00` = `nop`.

So Plan 7's decision to decode the payload at build time and never execute it is **SPEC-backed**, not
merely convenient. (Mechanically the offset *is* a code-unit offset into the same `insns[]`; if you did
branch into a payload, ART's decoder would step over the whole thing as one giant nop —
**implementation behavior of an unspecified case**, not a guarantee.)

**Payload layout is SPEC** ([BC] "fill-array-data-payload format"), and matches [ART]'s struct exactly:
`ident: ushort = 0x0300`, `element_width: ushort`, `size: uint`, `data: ubyte[]`, little-endian; total
code units `(size * element_width + 1) / 2 + 4`.

**The whole of [ART]'s runtime behavior is one 20-line function, `art::FillArrayData`
([`runtime/entrypoints/entrypoint_utils.cc:175-194`](https://android.googlesource.com/platform/art/+/refs/heads/main/runtime/entrypoints/entrypoint_utils.cc)).**
Fetched as raw base64 and decoded during Plan 7 Task 10, not read through a summarizing tool; every row
below traces to a specific line of it. In order: a `DCHECK` on the payload ident (`:176`), the **null check**
(`:177-180`), `AsArray()` plus a release-mode-only `DCHECK(!array->IsObjectArray())` (`:181-182`), the
**length check** (`:183-189`), and then a **single `memcpy`** of `element_count * element_width` bytes
(`:191-192`). That is the grounding for three separate claims: the null check strictly precedes the length
check, the store is **all-or-nothing** (so a failed fill writes nothing), and an array longer than the table
keeps its tail (the `memcpy` is sized by the *payload*, not by the array).

**[BC]'s entire text for the opcode, verbatim** — re-fetched from source.android.com during Plan 7 Task 10's
fix round, because the code and tests quote it and this registry carried only its *tier*:

> "Fill the given array with the indicated data. The reference must be to an array of primitives, and the
> data table must match it in type and **must contain no more elements than will fit in the array**. That is,
> **the array may be larger than the table, and if so, only the initial elements of the array are set,
> leaving the remainder alone**."

Those two emphasised clauses are the whole SPEC content: the first grounds the over-long-table *requirement*
(not its failure mode, which [BC] does not name), and the second grounds the tail-left-alone row outright.
Note also *"must be to an array of primitives"* — the object-array row below is SPEC-forbidden as well as
verifier-rejected.

| Condition | Behavior | Tier |
|---|---|---|
| array **longer** than table | legal; only the initial elements are set, **remainder left alone** | **SPEC** [BC] — *"the array may be larger than the table, and if so, only the initial elements of the array are set, leaving the remainder alone"*; and [ART] `:191-192`, where the `memcpy` length is `element_count * element_width` |
| table longer than array | `ArrayIndexOutOfBoundsException`, message `"failed FILL_ARRAY_DATA; length=%d, index=%d"` — and the reported number is the *count* (`payload->element_count` is literally the second format argument at `:187`), so off-by-one against a real index. **Nothing is stored**, because the check at `:183` returns before the single `memcpy` at `:192` | requirement **SPEC** ([BC]: the table *"must contain no more elements than will fit in the array"*), failure mode **[ART] only** (`:183-189`) |
| null array | `NullPointerException`, message `"null array in FILL_ARRAY_DATA"`; the null check (`:177`) precedes the length check (`:183`) | **[ART] only — genuinely unspecified and NOT inheritable**, because `fill-array-data` has no JVM counterpart |
| `element_width` mismatch | verifier hard failure; **no runtime check at all** — the raw `memcpy` at `:192` scales by `payload->element_width` and never consults the array's component | **VERIFIER** → engine's choice |
| target is an object array | verifier hard failure; runtime guard at `:182` is a `DCHECK`, i.e. a no-op in a release build | **VERIFIER** → engine's choice |

> 🔴 **A dexlib2 bug to route around, and Plans 6–7 is the plan that would trip it.**
> `Opcode.FILL_ARRAY_DATA.flags == 0x0004` — `CAN_CONTINUE` only, **no `CAN_THROW`**, so
> `canThrow()` returns **`false`** — while [ART] throws two different exceptions from it. Anything that
> derives exception edges from `canThrow()` will omit them, so a `try`/`catch` around a
> `fill-array-data` on a null array is mis-analyzed. The core does **not** currently call `canThrow()`
> anywhere (verified by grep; it uses `canContinue()` only, which *is* correct for this opcode) — so
> this is a trap rather than a live bug, and the rule is: **decide throw edges from the op's own
> semantics, never from `canThrow()`.**
> **Plan 7 Task 10 landed the opcode and did exactly that**, so the "no `canThrow()` call anywhere in
> `core/`" property still holds (re-verified by grep — the hits are **three**, all KDoc prose warning about
> it: two lines of `core/op/FillArrayDataOp.kt` and one of `core/op/FilledNewArrayOp.kt`, the latter added by
> Task 11 and documented seventy lines earlier in this same document. The count said "two" until the
> whole-branch review's M4; the substantive property — no *executable* `canThrow()` call in `core/` — is
> unchanged). The flag value was re-measured off the
> dexlib2 2.5.2 jar by reflection rather than taken from this row: `flags == 0x4`, `canThrow() == false`,
> `canContinue() == true`; `ARRAY_PAYLOAD` is `flags == 0x0` and `canContinue() == false`.
> Cheapest hostile-input test in the whole report: `const/4 v0, 0` then `fill-array-data v0, …` —
> passes ART's verifier (the verifier skips its checks when the register holds the null constant),
> throws NPE at runtime, and dexlib2 claims the instruction cannot throw.

### `throw` (op 27, 11x)

**SPEC** [BC], in full: *"Throw the indicated exception."* Four words.

| Condition | Behavior | Tier |
|---|---|---|
| the operand register **reads as a null reference** — a null, **or any register whose last write was a primitive**, whatever its value | a **fresh** `NullPointerException` — not the null reference, and with **no message** | **INHERITED** — [JVMS] §6.5 `athrow`: *"If objectref is null, athrow throws a NullPointerException instead of objectref"* — **plus [ART]**, which agrees *unconditionally*. The primitive half is [ART]; see the `ShadowFrame` chain below |
| operand not assignment-compatible with `Throwable` | rejected | **VERIFIER** — [CON] B16, verbatim: *"The A operand of a throw instruction must be assignment-compatible with java.lang.Throwable."* |
| a valid **reference** to a non-`Throwable` object at runtime (unverified input) | `java.lang.InternalError` — *"Throwing '%s' that is not instance of Throwable"*, **only** when assignability checks are on | **[ART] only**, undocumented |
| `IllegalMonitorStateException` (structured locking) | **NOT inherited** — [VER]: *"The VM is permitted but not required to enforce "structured locking" constraints… This is not currently implemented."* | — |

**[ART]'s `THROW()`, read from raw source (verified 2026-07-28 via `?format=TEXT` piped through
`base64 -d`, at `refs/heads/main`).** `runtime/interpreter/interpreter_switch_impl-inl.h`, lines 933–950.
**Excerpt — lines 934–936 are elided**, so the inline annotations are absolute source lines and do **not**
match a line count inside this block:

```c++
  HANDLER_ATTRIBUTES bool THROW() {                                         // :933
    // ... ObserveAsyncException() early-return elided (:934-936) ...
    ObjPtr<mirror::Object> exception = GetVRegReference(A());               // :937
    if (UNLIKELY(exception == nullptr)) {                                   // :938
      ThrowNullPointerException();                                          // :939
    } else if (DoAssignabilityChecks() && !exception->GetClass()->IsThrowableClass()) {   // :940
      // This should never happen.
      std::string temp;
      Self()->ThrowNewExceptionF("Ljava/lang/InternalError;",               // :943-945
                                 "Throwing '%s' that is not instance of Throwable",
                                 exception->GetClass()->GetDescriptor(&temp));
```

### `GetVRegReference` is the whole of it, and reading `THROW()` without following it is how this section got the primitive case wrong twice

**A Dalvik register is not one machine word.** `ShadowFrame` is, in ART's own words
(`runtime/interpreter/shadow_frame.h:401-408`), *"a two-part array"*:

> *"`[0..number_of_vregs)` holds the raw virtual registers … `[number_of_vregs..number_of_vregs*2)` holds
> only reference registers. … In other words when a primitive is stored in vX, the second (reference) part
> of the array will be null. When a reference is stored in vX, the second (reference) part of the array
> will be a copy of vX."*

That invariant is **maintained on every write, not merely documented**, and the chain is verified link by
link:

| Link | Source | What it shows |
|---|---|---|
| `CONST_4()` is `SetVReg(A(), B())` | `interpreter_switch_impl-inl.h:707-710` | `const/4` goes through the `SetVReg` family |
| `SetVReg` ends with `References()[i].Clear()` | `shadow_frame.h:190-197` | a primitive write clears the reference slot. `SetVRegFloat` (`:199-206`), `SetVRegLong` (`:208-217`) and `SetVRegDouble` (`:219-228`) do the same, so `Z`/`F`/`D`/`J` are covered too |
| `Clear()` is `reference_ = 0` | `mirror/object_reference.h:140-143` | the slot really becomes null |
| `GetVRegReference(i)` is `References()[i].AsMirrorPtr()` | `shadow_frame.h:174-183` | which is `Decompress(0)`, i.e. **`nullptr`** |
| the reference half always exists | `ComputeSize`, `shadow_frame.h:76-79`; the constructor `memset`s both halves, `:358-365` (the `memset` statement itself is `:364`) | there is no build in which the half is absent |
| …and there is no build without it | **`grep -nw HasReferenceArray` finds nothing** in current ART — checked with `-nw`, because `HasReferenceArray` would be a substring match risk | the conditional-reference-array era is over |
| a debug build `CHECK`s it | `CheckConsistentVRegs`, `shadow_frame.h:346-355`: `CHECK((GetVReg(i) == reference_value) \|\| (reference_value == 0))` | ART asserts the invariant itself |
| stable across releases | `android14-release/runtime/interpreter/shadow_frame.h` has the identical `References()[i].Clear()` in `SetVReg` | not a `main`-only detail |

**Therefore `:938`'s `exception == nullptr` is TRUE for a primitive-typed register and `:939` runs.**
`throw` of **any** register whose last write was primitive — `const/4 v0, 0x0`, `const/4 v0, 0x5`,
`const/4 v0, -0x1`, a `float`, a `double`, a `long` — is a definite, **message-less**
`NullPointerException` **in this tier**: the switch interpreter does not reinterpret the bits as a pointer.

> ⚠️ **That last sentence used to read *"ART does **not** reinterpret the bits as a pointer, so there is no
> undefined behavior here and nothing for an engine to decline to model,"* and it was an over-claim about
> ART as a whole.** Everything above is the **ShadowFrame/switch** interpreter, which is what `-Xint`
> selects; ART's **default** interpreter reads the same operands from the raw register array and does
> reinterpret a non-zero primitive's bits as a pointer. The zero rows survive intact and get a second
> independent tier behind them; the non-zero-primitive row is re-tiered to **VERIFIER**. See
> [ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim)
> at the end of this section, which is the general instrument rather than a note about `throw`.

The same `GetVRegReference` fetch is used by
`ARRAY_LENGTH` (`:852-860`), `HandleAGet` (`:508-522`), `HandleAPut` (`:524-541`), `MONITOR_ENTER`
(`:793-804`), `MONITOR_EXIT` (`:806-817`) and every field op, each with the same `nullptr` → NPE branch —
which is why `org.cf.smalivm.core.readsAsNullReference` is shared by **six** ops rather than being `throw`'s
private test. **`DoFilledNewArray` is the sixth ART consumer and was missing from this list**, which is
exactly how Task 12's sweep missed it: the sweep covered the ops with an *array operand* and
`filled-new-array` has none, but its reference-component fill reads the register **as a reference** all the
same (`interpreter_common.cc:1463-1464`, `shadow_frame.GetVRegReference(src_reg)` — see the
[`filled-new-array` section](#filled-new-array-op-24-35c-and-range-op-25-3rc)). Added by the whole-branch
review's C1 fix. **The trigger is "reads a register as a reference", not "holds an array"** — write it that
way for the field and monitor ops Plan 9 inherits.

> **After Plan 8 the count is EIGHT, not six**, and this list was written by Plan 7's *array* sweep, which
> had no reason to reach the type ops. `instance-of` and `check-cast` are consumers by exactly the trigger
> sentence above — each *reads a register as a reference* — and ART's own handlers say so in one line each,
> verified from raw source (`?format=TEXT` through `base64 -d`, `refs/heads/main`):
> `INSTANCE_OF` (`runtime/interpreter/interpreter_switch_impl-inl.h:837-850`) resolves the type operand at
> `:838-846` and only then fetches `GetVRegReference(B())` at `:847`, and `CHECK_CAST` (`:819-835`) resolves
> at `:820-828` and fetches `GetVRegReference(A())` at `:829`. See the
> [`instance-of`](#instance-of-22c) and [`check-cast`](#check-cast-21c) sections for the ordering
> consequence. The two wrong answers this closes, one in each spec §2.2 direction, are both **live in the
> oracle** and both measured in [Oracle reconciliation](#oracle-reconciliation-plan-8): `check-cast v0,
> Ljava/lang/String;` on `Known(5, "I")` invents a definite `ClassCastException` where the switch
> interpreter's `nullptr` **passes**, and `instance-of v1, v0, Ljava/lang/Object;` on the same value folds
> `Known(true, Z)` where the switch interpreter answers `0`. ⚠️ **`Known(5, "I")` is a non-zero primitive, so
> those two are the tier-divergent row and their tier is VERIFIER rather than [ART]** — nterp answers neither
> `passes` nor `0` for it, it dereferences the `5`; the oracle is wrong either way, but not because *"ART
> says"* so. The zero spellings of the same two rows **are** [ART] under both tiers. See
> [ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim).
>
> **✅ SEVEN of the eight as of Plan 8 Task 8.** `instance-of` consumes it, through
> `core/castOutcome`'s first row — which is also where `check-cast` will get it, so Task 9 inherits the
> ordering rather than restating it. Both ops' null rows are therefore ONE row in one function, which is
> what the `raw == null` keying could not have been. The `instance-of` half is pinned by
> `core/op/InstanceOfOpTest.everySpellingOfReadingAsANullReferenceAnswersZero`, which drives all three
> clauses of the predicate separately, and by the `types-primitive-register-is-instance-of-object` registry
> entry end-to-end.

**`FILL_ARRAY_DATA` (`:920-931`) belongs on that list too and had no range here**: it
fetches with `GetVRegReference(A())` at `:924` and does *not* null-check inline — it hands the result to
`art::FillArrayData`, whose `if (UNLIKELY(obj == nullptr)) ThrowNullPointerException("null array in
FILL_ARRAY_DATA")` is the throw site (`runtime/entrypoints/entrypoint_utils.cc:177-180`, function at
`:175-194`). Two hops rather than one, and worth stating as two.

> **Range hygiene, recorded because this section got one wrong in each direction.** Every span above was
> re-counted from a freshly fetched file with `awk 'NR>=a && NR<=b {print NR"|"$0}'` rather than eyeballed
> from a `sed` window. Four were wrong before that recount and are corrected here: the `ShadowFrame`
> constructor (a *correct* `:358-365` had been "corrected" to `:357-364`, which is worse than the
> original because it reads as more verified), `HandleAGet` and `HandleAPut` (both stopped mid-function),
> and `MONITOR_ENTER`/`EXIT` (one range spanning two functions, wrong at both ends). The content of every
> claim was right in all four cases; only the ranges were not. Count the lines; do not trust a window.
>
> **A FIFTH was wrong and was inside this very claim** — the whole-branch review's I1. `common_throws.cc`'s
> per-opcode NPE block was cited as `:645-667` in six places; `:645` is the *last* of seven `AGET_*` labels,
> so the real span is **`:639-667`** and the citation excluded six of the seven cases it was made for. It read
> as verified because every interior line a reader would spot-check (`:647`, `:657`, `:661`) resolves exactly.
> **Recording an instrument is not running it**: this block claimed *"every span above"*, and the span it
> missed was one this document already carried rather than one the recount introduced. All ART spans in this
> section have now been re-counted at **both endpoints** — the correction that matters is the endpoint check,
> because an interior-correct range with a wrong endpoint is the variant that survives review.

> 🔴 **Corrects a Plans 6–7 claim, and then corrects this document's own two attempts at correcting it. All
> three prior readings of the primitive case were wrong, in both directions; do not restore any of them.**
>
> 1. Plan 7's Task 12 draft said *"`throw null` throws a fresh NullPointerException, and so does throwing a
>    primitive."* The conclusion was **right** and the attribution was wrong: it cited Dalvik, which says
>    nothing.
> 2. **This section then "corrected" it to `InternalError`** for a non-`Throwable`, and Plan 7's own table
>    used `Known(5, I)` as the example. Wrong for that example: `InternalError` sits in `THROW()`'s
>    `else if`, so it needs a **valid, non-null object reference** whose class is not a `Throwable`.
> 3. **Task 12's first implementation round then "corrected" *that*** to *"ART reinterprets the 5 as an
>    object pointer and dereferences it — no defined behavior"*, and left the case on the ambiguous fork.
>    Also wrong, per the chain above, and wrong in the direction that **invents** reachable handlers: a
>    `catch (IOException)` over `throw` of `const/4 v0, 0x5` was reported live, an `I`-typed escape was
>    reported feasible for a throw that is definitely caught, and the surviving correct handler bound the
>    int 5 where a device binds an NPE.
>
> The reason all three matter is the same, and it is the reason this document exists: **the handler sets
> differ.** `catch (Exception)` catches an NPE and misses an `InternalError` (a `VirtualMachineError`, i.e.
> an `Error`), and a fork claims both plus an escape that none of them is.

#### ART has TWO interpreter tiers and they read a register differently — name the tier in every register-read claim

**This is a general instrument, filed here because the `GetVRegReference` chain above is where it was found.
It applies to every claim in this document whose truth depends on how a register is READ, present and
future.** Verified by raw fetch, `?format=TEXT | base64 -d`, at `refs/heads/main`, 2026-08-04, both endpoints
of every range re-counted with `awk` per instrument 2.

**The two tiers, and which one a device is running.**

| | switch interpreter | `nterp` |
|---|---|---|
| where it lives | `runtime/interpreter/interpreter_switch_impl-inl.h` over `ShadowFrame` | `runtime/interpreter/mterp/{x86_64ng,arm64ng,…}/*.S` |
| how it reads an object operand | `GetVRegReference(i)` — the **reference** half | `GET_VREG` — the **raw** dex-register array |
| when it runs | `-Xint`, a debuggable runtime, instrumentation, `JitAtFirstUse`, an unsupported ISA | **the default on a modern device** |

The macro split is explicit in nterp's own headers. `x86_64ng/main.S:88-91` defines
`VREG_ADDRESS(_vreg)` as `(rFP,_vreg,4)` and `VREG_REF_ADDRESS(_vreg)` as `(rREFS,_vreg,4)` (`rFP` is
`%r13` at `:68`, `rREFS` is `%r15` at `:75`); `GET_VREG` (`:144-146`) reads `VREG_ADDRESS` and
`GET_VREG_OBJECT` (`:148-150`) reads `VREG_REF_ADDRESS`. `arm64ng/main.S:184-189` is the same split over
`xFP` versus `xREFS` (`xREFS` is `x25` at `:84`; `xFP` is `x29`, named in the register table at `:65`).
**nterp maintains the reference array exactly as `ShadowFrame` does** — `SET_VREG` (`x86_64ng/main.S:157-160`)
writes `VREG_ADDRESS` and then zeroes `VREG_REF_ADDRESS`, the same invariant as `References()[i].Clear()` —
**it simply does not consult it in these handlers.** So the divergence is not a difference in the invariant;
it is a difference in which array the opcode asks.

Tier selection is a runtime decision, not a build constant: `mterp/nterp.cc`'s `CanRuntimeUseNterp()`
(`:60-74`) is `IsNterpSupported() && !IsJavaDebuggable() && !EntryExitStubsInstalled() &&
!instr->InterpretOnly() && !IsAotCompiler() && !NeedsSlowInterpreterForListeners() &&
!AreAsyncExceptionsThrown() && (jit == nullptr || !jit->JitAtFirstUse())`. `IsNterpSupported()` (`:37-58`)
switches on `kRuntimeQuickCodeISA`: arm/thumb2/arm64 return `kReserveMarkingRegister &&
!kUseTableLookupReadBarrier`, riscv64 returns `true`, x86/x86_64 return `!kUseTableLookupReadBarrier`, and
`default: return false` — so it is **every ISA an Android device ships on, subject to two build constants**,
which is why the tier a run exercises is a property of the *device and flags*, not of the bytecode. Its own
comment names the reason for the
`InterpretOnly` term, verbatim: *"If the runtime is interpreter only, we currently don't use nterp as some
parts of the runtime (like instrumentation) make assumption on an interpreter-only runtime to always be in a
switch-like interpreter."* And **`-Xint` is exactly that term**: `parsed_options.cc:139-141`
(`.Define("-Xint").WithValue(true).IntoKey(M::Interpret)`) → `runtime.cc:1745-1747`
(`if (runtime_options.GetOrDefault(Opt::Interpret)) { GetInstrumentation()->ForceInterpretOnly(); }`) →
`instrumentation.h:361-363` sets `forced_interpret_only_`, read back by `InterpretOnly()` at `:375-377`.

**Every dereferencing handler in nterp uses the raw read — this is not confined to the type ops.**

| opcode | nterp handler | what it does with the raw |
|---|---|---|
| `check-cast` | `x86_64ng/object.S:1-22` (`arm64ng/object.S:1-25`) | `GET_VREG`, `testl`/`je …_resume` (`cbz` on arm64) — zero **passes**; non-zero goes straight into `cmpl MIRROR_OBJECT_CLASS_OFFSET(%edi), %esi` |
| `instance-of` | `x86_64ng/object.S:86-107` (`arm64ng/object.S:72-101`) | `GET_VREG`, `testl`/`je …_set_vreg` — zero **stores 0**; non-zero dereferences the class offset |
| `throw` | `x86_64ng/control_flow.S:171-176` | `GET_VREG` → `art_quick_deliver_exception`; `artDeliverExceptionFromCode` (`runtime/entrypoints/quick/quick_throw_entrypoints.cc:51-69`) comments *"exception may be null, in which case this routine should throw NPE"* and does |
| `array-length` | `x86_64ng/array.S:111-123` | `GET_VREG`, `testl`/`je common_errNullObject` |
| `aget` / `aput` / `aput-object` | `x86_64ng/array.S:1-38`, `:58-80`, `:97-109` | same `GET_VREG` + `je common_errNullObject`; `aput-object` also takes its **value** from `GET_VREG` at `:107` |
| `fill-array-data` | `x86_64ng/array.S:125-132` | `GET_VREG` → `art_quick_handle_fill_data`, whose `art::FillArrayData` null-checks (`entrypoint_utils.cc:177-180`) |
| `filled-new-array` | `x86_64ng/array.S:134-148` → `NterpFilledNewArray` (`mterp/nterp.cc:758-764`) → nterp's own `DoFilledNewArray` (`:683-756`) | the reference-component fill at `:751-752` is `SetWithoutChecks(i, reinterpret_cast<mirror::Object*>(regs[src_reg]))` — **the raw register value cast straight to an object pointer** |

**The split, and it is a SPLIT rather than a retraction.**

1. **Where the raw is ZERO, the two tiers AGREE — so those rows are STRONGER than they were, not weaker.**
   Every nterp handler above branches on the raw being zero and takes the null path, and
   `reinterpret_cast<mirror::Object*>(0)` is `nullptr`. Same answers as `GetVRegReference`'s `nullptr`,
   reached by a different mechanism. This covers `Value.isNull` **and** Dalvik's integral zero under a
   reference tag (`isDalvikNull`), and both are **verifier-VALID**: `reg_type.h:167-169` makes
   `IsReferenceTypes()` admit `IsZero()`/`IsNull()`, and `VerifyAGet` allows `IsZeroOrNull()` outright
   (`method_verifier.cc:5035-5037`, commenting *"Null array class; this code path will fail at runtime"*).
   Two independent tiers plus linkable input is the strongest grounding any row in this file has.
2. **Where the raw is a NON-ZERO primitive, the two tiers CONTRADICT each other.** `const/4 v0, 0x5` then
   `check-cast v0, Ljava/lang/String;`: the switch interpreter reads `nullptr` and the cast **passes**;
   nterp reads `5`, finds it non-zero, and **dereferences it as an object pointer**. Same for `throw`,
   `array-length`, `aget`/`aput` and `fill-array-data`; for `filled-new-array` the integer is *stored* as a
   reference rather than immediately dereferenced. **The failure class is certain — an invalid pointer is
   dereferenced — and it is now MEASURED** (Task 9, **Measured — `[DEVICE](Tier B, nterp default, API 36)`** below): nterp faults on the raw
   value used as a pointer; where that address is unmapped it is a **SIGSEGV at the value itself**, and where
   it lands in ART's implicit-null-check window it is a **process abort** (ART's handler tries to synthesize an
   NPE for a type op and cannot). Either way the process dies — never the switch tier's `0`/success.
3. **And that row is unreachable on verified input anyway, for every consumer.**
   `runtime/verifier/method_verifier.cc` hard-fails a non-reference register with
   `VERIFY_ERROR_BAD_CLASS_HARD`, which rejects the **whole class**: `check-cast`/`instance-of` at
   `:3156-3158` (in the shared case opened at `:3120-3121`) — *"`} else if (!orig_type.IsReferenceTypes()) {
   Fail(VERIFY_ERROR_BAD_CLASS_HARD) << opcode << " on non-reference in v" << orig_type_reg; return
   false;`"*; `throw` at `:3253-3255` (*"thrown value of non-reference type"*); `array-length` at
   `:3174-3177` (*"array-length on non-array"*); `aget`/`aput` in `VerifyAGet` at `:5054-5055` (*"not array
   type … with aget"*). No linkable APK can present it.
4. **So the honest tier for the non-zero-primitive row is VERIFIER — this project's design choice under spec
   §2.2 — and NOT [ART].** Presenting one tier's mechanism as *"[ART] does X"* while the default tier
   crashes on the same bytes is the same class of over-claim as the
   [`can_run_clinit` non-sequitur](#const-class-21c): a verbatim, line-exact citation supporting a
   conclusion it cannot reach.
5. **The engine's behaviour does not change and must not.** `Value.readsAsNullReference` still answers
   `true` for every primitive-typed `Known`; `instance-of` still answers `0`, `check-cast` still succeeds,
   every dereference is still a definite NPE. That is the sound direction, it matches one real tier exactly,
   and the alternative — modelling a wild-pointer dereference — is not a behaviour an analysis engine can
   reproduce or should try to. **Only the stated evidence changes.**

**Measured — `[DEVICE](Tier B, nterp default, API 36)`, 2026-08-05 — the outcome the source chain above declined to guess.** Tier **nterp**,
API level **36** (`ro.build.version.sdk=36`), **default** `dalvikvm` flags, x86_64, on the `simplify-emulator`
image. The probes are verifier-invalid (that is the whole point — see the next bullet), so they run under
`-Xverify:none`; `-Xverify:none` did **not** disqualify the method from nterp on this build, so the crucial
risk — that an unverified method falls back to the switch interpreter and the wrong tier gets measured — did
not materialise. Global Constraint 1's `-Xint` mandate is DELIBERATELY suspended for the nterp arm, because
`-Xint` selects the *other* tier. Per-probe isolation, one `dalvikvm` per probe, crash treated as a first-class
recorded outcome. Runner and probes: `tools/art-runner/tier-b-only/` (a SIBLING of `fixtures/`, never under
it — the in-JVM engines walk `fixtures/` recursively via `Files.walk`, so a verifier-invalid class there would
enter the corpus class model).

| register value read as a reference | **nterp** (default flags; **identical under `-Xusejit:false`, so not the JIT**) | **switch** (`-Xint`) |
|---|---|---|
| `0x7fabc000` (unmapped), `instance-of` | **SIGSEGV (rc 139), fault addr `0x7fabc000`** — the raw register value dereferenced as a pointer, *verbatim*. Backtrace `#00 nterp_op_instance_of`; "fault address falls between mapped regions". | `instance-of → 0`, clean exit |
| `0x2a` (small), `instance-of` | **process abort (SIGABRT, rc 134)** — deref faults at `0x2a`, inside ART's implicit-null-check window, so the SIGSEGV handler tries to make an NPE and cannot for a type op: `common_throws.cc:574 Invalid address for an implicit NullPointerException check: 0x2a, at instance-of …` → `runtime.cc Runtime aborting`. Backtrace `nterp_op_instance_of`. | `instance-of → 0`, clean exit |
| `0x11223344` (mapped on this image), `instance-of` / `check-cast` | **SIGABRT (rc 134)**, same mechanism — nterp reads a garbage word from the mapped `0x11223344` and the second-level deref lands at `0x20`, again in the null-check window: `… check: 0x20, at check-cast v2, java.lang.Object`. Backtrace `nterp_op_check_cast_slow_path` / `nterp_op_instance_of_slow_path`. | `check-cast` **succeeds**; `instance-of → 0` |
| `0x0` (control) | `instance-of → 0`; `check-cast` succeeds — **both tiers agree**, no crash | same |

The tier is proven POSITIVELY, three independent ways at once: (1) the crash backtrace names `nterp_op_*`,
never `ExecuteSwitchImpl`; (2) the fault address **equals the stored primitive** for an unmapped value, which
the switch interpreter — reading `nullptr` — structurally cannot produce; (3) default vs `-Xint` **diverge**
for the identical dex (so the default tier is not the switch tier) while default vs `-Xusejit:false` do **not**
(so it is not the JIT). A real-object control returns `1` on both tiers; the `0x0` controls agree on both —
the divergence is specific to the NON-ZERO primitive, exactly as the split above claims.

**What the measurement changes, and what it must not.** It does **not** change the engine (point 5 stands
unchanged), and it does **not** promote the row to `[ART]` — the two tiers still contradict, and it is now
measured that they do not merely disagree on a value: one **terminates the process**. It replaces *"nothing has
been measured, so do not write SIGSEGV"* with the measured fact and leaves the row's tier where it was —
**VERIFIER → the engine's spec §2.2 choice**, now empirically vindicated rather than only argued. The row's
tier **stays VERIFIER even though a device measured it** — exactly the register-read exception in [the ranking
rule](#the-tiers-are-not-equally-strong--the-ranking-for-a-claim-about-what-the-runtime-does): the input is
verifier-invalid, so a `[DEVICE](Tier B, nterp default, API 36)` run reports *that the VM dies*, not a runtime
value the engine could adopt. The measurement is a real `[DEVICE]` observation (the tag exists as of Task 10);
the row it grounds is not a `[DEVICE]` row.

**The instrument, stated for reuse:** *for any claim that depends on how a register is read, name the tier.*
*"[ART] does X"* is under-specified; *"[ART]'s switch interpreter does X, and nterp does Y"* is a claim.
Where the two agree, say so — it is a second independent tier and a genuine strengthening. Where they
disagree, the row is not an [ART] fact at all, and the next question is whether the verifier admits the input
(here it does not, for any consumer).

> **One thing checked in the other direction, and it came out clean.** The `can_run_clinit` regrounding
> ([`instance-of`](#instance-of-22c), [`const-class`](#const-class-21c)) is **tier-independent**: nterp
> resolves `check-cast`/`instance-of`/`const-class`/`new-array` through `NterpGetClass`
> (`mterp/nterp.cc:577-607`), which passes `can_run_clinit = false` at `:598` exactly as the switch
> interpreter does — and whose whole remaining body is a `nullptr` bail, an `UpdateCache` and a return, so it
> does nothing else that could initialize. So both tiers agree there and no
> re-tiering follows. ⚠️ **But `filled-new-array` does NOT agree**: nterp's `DoFilledNewArray` passes
> `can_run_clinit = **true**` (`nterp.cc:708`) where the switch interpreter's passes `false`
> (`interpreter_common.cc:1412`). Nothing in this project rests a claim on that flag for
> `filled-new-array`, so nothing is wrong today — it is recorded because it is a **second** instance of the
> same instrument outside register reads, which is why the instrument is stated generally above.

### The `InternalError` gate is live in release ART

Verified end to end, because the alternative reading ("a debug-only check") would make the row moot:
`DoAssignabilityChecks()` is `return !shadow_frame_.GetMethod()->SkipAccessChecks();`
(`interpreter_switch_impl-inl.h:1950-1952`); `ArtMethod::SkipAccessChecks()`
(`runtime/art_method.h:539-543`) is, verbatim,
`return (GetAccessFlags() & (kAccSkipAccessChecks | kAccNative)) == kAccSkipAccessChecks;` — so it also
requires the method **not** be native, because the flag bit is reused for natives (immaterial here: the
interpreter path is non-native by construction); and that flag is set only by `UpdateClassAfterVerification`
under `if (klass->IsVerified() && (failure_kind == verifier::FailureKind::kNoFailure))`
(`runtime/class_linker.cc:219-227`). A method whose class skipped or failed verification therefore keeps
access checks **on** — which is this engine's normal input, so `InternalError` is release-ART behavior and
not a debug artifact.

### What the engine does, and its tier

Plan 7 Task 12 and its fix round; `ThrowOp.kt` plus `core/Value.kt`'s `readsAsNullReference`.

| Thrown `Value` | Engine behavior | Tier |
|---|---|---|
| `Known(null, T)` | synthesize a fresh, message-less guest `NullPointerException`; resolve handlers **definitely** | **INHERITED** + **[ART]** |
| `Known(0, "I")` etc. — Dalvik's null idiom | same | **SPEC** ([BC] `(Object) null == (int) 0`) + **INHERITED** + **[ART]** |
| `Known(false, "Z")`, `Known(0.0f, "F")`, `Known(0L, "J")` — a primitive-typed `Known` whose 32 bits are **zero** | same. The tag is irrelevant; the bits are zero | **[ART], BOTH TIERS** — the switch interpreter reads `nullptr`; nterp's `op_throw` hands the raw `0` to `artDeliverExceptionFromCode`, which throws the same NPE (`quick_throw_entrypoints.cc:51-69`). Corroboration added 2026-08-04 |
| `Known(5, "I")`, `Known(1.5f, "F")`, `Known(7L, "J")` — a primitive-typed `Known` whose bits are **NOT** zero | same, unchanged. The value is irrelevant *to this engine*; only the primitiveness | **VERIFIER → engine's choice (spec §2.2). NOT [ART]** — **re-tiered 2026-08-04**: the two tiers disagree (switch reads `nullptr`; nterp dereferences the raw as a pointer) and `method_verifier.cc:3253-3255` hard-fails *"thrown value of non-reference type"*, so no device links it. The nterp side of that disagreement is now **measured** for the type ops (`instance-of`/`check-cast` crash the VM; `-Xint` returns `0`/succeeds) — 2026-08-05, Task 9. See [ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim) |
| `Known(0, "Ljava/lang/String;")` — integral zero under a *reference* type | same. Not producible by this build's op set; kept because [BC] answers it directly | **SPEC** |
| `Unknown` / `Top` | **forks**: every covering handler plus the escape | **engine's choice**, spec §2.2 — nothing is proved, so explore more. The majority case in a real APK |
| `Known(x, SomeNonThrowableRef)` | **imprecise, and the one remaining gap**: `resolvableTypeOf` resolves the reference type, no clause catches it, and the value escapes as itself. ART's `InternalError` is the fidelity answer and is **not** implemented | filed against **Plan 9** in Plan 7's Task 14 candidate list; needs `new-instance` before a test can build the input from real smali |

Note what the boundary is **not**: it is not "is the value zero" (that was the first round's predicate, and
it dropped every non-zero primitive) and not "is the value known" (an `Unknown(I)`'s tag is an upper bound,
not evidence — the register may hold a reference the engine could not track). It is exactly *"is this
register's reference slot provably null"*, which is decided by the **type**, never by the value.

**The zero/non-zero line in the two rows above is an EVIDENCE line, not a behaviour line, and the two must
not be collapsed back together.** The engine's predicate is still purely type-decided and no row's answer
changes; what the value decides is which *tier* the row can cite — both interpreters for zero bits, neither
of them alone for non-zero bits.

### Also worth having: `monitor-exit`'s pc semantics

**SPEC** [BC], the only pc-relative exception carve-out in the whole reference — relevant to Plan 9's
monitor no-ops:

> "Note: If this instruction needs to throw an exception, it must do so as if the pc has already
> advanced past the instruction. It may be useful to think of this as the instruction successfully
> executing (in a sense), and the exception getting thrown after the instruction but before the next one
> gets a chance to run."

## Control flow and type ops (Plan 8)

### The one SPEC sentence everything here rests on

**[BC]**, "General design":

> "In terms of bitwise representation, `(Object) null == (int) 0`."

That is the licence for `if-eqz` to double as a null test and for `if-eq` to compare references —
Dalvik needs no `if_acmpeq`/`ifnull` because null **is** integer zero at the representation level. It is
also the only type-related thing [BC] says about the twelve `if-*` opcodes.

### `goto` / `goto/16` / `goto/32` (10t / 20t / 30t)

**SPEC** [BC]: *"Unconditionally jump to the indicated instruction."* Offsets are **signed** ("signed
branch offset", stated for all three).

- **The offset's unit is never stated outright.** It is 16-bit code units by composition of three
  sentences: [BC]'s *"The storage unit in the instruction stream is a 16-bit unsigned quantity"*, [IF]'s
  *"The first digit indicates the number of 16-bit code units in the format"*, and [DEX]'s definition of
  a bytecode address as *"a count of 16-bit code units"*. Cite the composition, not a single sentence.
- **Relative to the branch opcode's own address**, not the following instruction — **INHERITED** (JVMS
  `goto`: *"Execution proceeds at that offset from the address of the opcode of this goto instruction"*).
  Moot in practice: dexlib2 resolves labels for you (`BuilderOffsetInstruction.target.codeAddress`).
- **A zero offset is forbidden for `goto` and `goto/16` and LEGAL for `goto/32`.** [BC] says so and names
  `goto/32` as the sanctioned way to spin. **The rule is on FOUR [BC] rows, not two** — the sentence
  *"Note: The branch offset must not be 0."* appears on `10t goto`, `20t goto/16`, **`22t if-test`** and
  **`21t if-testz`**, and is **absent** from `30t goto/32`. So it constrains the twelve `if-*` opcodes too;
  the `if` rows' parenthetical differs from `goto`'s (*"(A spin loop may be legally constructed either by
  branching around a backward `goto` or by including a `nop` as a target before the branch.)"*). Corrected
  in Plan 8 Task 1: this bullet used to say *"[BC] says so in the first two rows"*, which silently made it a
  `goto`-only rule. **VERIFIER**-enforced, and ART enforces all four in **one** template function —
  `runtime/verifier/method_verifier.cc:2172-2185`, whose `kFormat` switch names `k22t` (*"if-<cond>?"*,
  `:2172`), `k21t` (*"if-<cond>z?"*, `:2174`) and `goto` (`:2176`) before reaching
  `if (kFormat != Instruction::k30t && UNLIKELY(offset == 0)) FailBranchOffsetZero(dex_pc)` at `:2182-2183`,
  under the comment *"// Only `goto/32` instruction can target itself. For other instructions `offset` must
  not be 0."* (`:2179`). `FailBranchOffsetZero` is a real symbol (`:769-772`, `grep -nw`), and its message is
  *"branch offset of zero not allowed."* (`:771`). Behaviourally harmless to this engine — a zero-offset
  `if` self-loops and the address-visit limit bounds it — but it interacts with self-truncation, so it is
  the `if` family's one VERIFIER guarantee an implementer might reach for and must not.
- **Throws nothing.** dexlib2 flags are `0x0000` — not even `CAN_CONTINUE`, which is how you know they
  are unconditional. **There is no "is a branch" flag in dexlib2**; derive branchiness from the format
  or from `instruction is OffsetInstruction`.

### `if-eq` / `if-ne` / `if-lt` / `if-ge` / `if-gt` / `if-le` (22t) and the `*z` forms (21t)

**SPEC** [BC], and this is the whole of it: *"Branch to the given destination if the given two
registers' values compare as specified"*, and for the `*z` forms *"if the given register's value
compares with 0 as specified"*. **[BC] never names the operand type for any of the twelve.**

**So the operand typing is VERIFIER, and in [ART] it is asymmetric** ([ART]
`runtime/verifier/method_verifier.cc`). Line numbers and the exact predicates were fetched in Plan 8 Task 1;
the table previously named the file and no lines:

> **The clause *"with the same rule in Dalvik 4.4.4 `CodeVerify.cpp` — stable for a decade"* has been
> DELETED, because it is false and it contradicted this document's own later section.** Dalvik 4.4.4's
> `OP_IF_EQ`/`OP_IF_NE` case (`vm/analysis/CodeVerify.cpp:4368-4391`, fetched from the `android-4.4.4_r1`
> tag) is **symmetric** — `if (regTypeIsReference(type1) && regTypeIsReference(type2)) break;` then
> `checkTypeCategory` on **both** operands — with no vA-only permissive branch, and it cannot have one,
> because 4.4.4 has **no null register kind at all**: `grep -nw kRegTypeNull` over `CodeVerify.h` is
> **empty**, and `kRegTypeZero` (`CodeVerify.h:68-69`) is one kind covering both roles, its own comment
> reading *"32-bit 0, could be Boolean, Int, Float, or Ref"*. Even the failure text differs (*"VFY: args to
> if-eq/if-ne must both be refs or cat1"*, `:4387`). So **the asymmetry is ART-only**, which is exactly what
> this document's own *"ART vs Dalvik for every opcode in this section"* paragraph — near the end of this
> section — already lists it as, so the deleted clause contradicted it directly. It was carried into
> Plan 8 Task 1's rewrite as inherited text, flagged as
> unverified, and kept attributed — which was the wrong call: **an unadopted claim still doing work in a row
> is worse than no claim**, so it is deleted rather than hedged. The `if-lt` family differs in mechanism too
> — 4.4.4 uses `kTypeCategory1nr` plus an explicit float rejection (`:4392-4400`) where ART uses the positive
> `IsIntegralTypes` whitelist — so "the same rule" was not even true where the outcomes agree.

| Opcodes | Accepts | [ART] |
|---|---|---|
| `if-eq`, `if-ne` | vA zero-or-null → vB a ref **or** integral; else vA a ref → vB a ref; else both integral | `:3319-3337`. Failure message at `:3332-3333`: *"args to if-eq/if-ne (…,…) must both be references or integral"* |
| `if-lt`, `if-ge`, `if-gt`, `if-le` | integral only — **`float` is excluded** | `:3338-3350`, `!reg_type1.IsIntegralTypes() \|\| !reg_type2.IsIntegralTypes()`; message *"args to 'if' (…,…) must be integral"* |
| `if-eqz`, `if-nez` | a reference **or** an integral — this is the null-check pair | `:3351-3358`, message *"type … unexpected as arg to if-eqz/if-nez"* |
| `if-ltz`, `if-gez`, `if-gtz`, `if-lez` | integral only | `:3460-3470`, message *"type … unexpected as arg to if-ltz/if-gez/if-gtz/if-lez"* |

**What `IsIntegralTypes()` actually admits, because two plans have guessed at it.**
`runtime/verifier/reg_type.h:211-217`: `kInteger || kIntegerConstant || kChar || kCharConstant ||
IsShortTypes(kind)`, and `IsShortTypes` (`:195-200`) folds in `IsByteTypes` (`:186-191`) which folds in
`IsBooleanTypes` (`:182-184`, `kBoolean || kZero || kBooleanConstant`). So integral is
**`Z`/`B`/`S`/`C`/`I` and their constant forms, plus `Zero`** — and it excludes `kFloat`, `kLongLo` and
`kDoubleLo`. Consequences a per-opcode table hides:

- **`if-lt`/`if-ge`/`if-gt`/`if-le` on two `long`s, on two `float`s, or on two references are ALL
  verifier-rejected**, not just the float case. There is no verifier guarantee to lean on for any of them.
- **`if-eq`/`if-ne` on a `long` or a `float` register is verifier-rejected too**, in both operand orders,
  because neither the ref branch nor the integral branch admits it.
- `IsReferenceTypes()` (`:167-169`) is `IsNonZeroReferenceTypes() || IsZero() || IsNull()` and
  `IsZeroOrNull()` is `:170-172`, so **`Zero` is both a reference type and an integral type** while `Null`
  is only a reference type. That, plus the fact that `if-eq`'s first branch tests **only vA**, is the whole
  of the asymmetry recorded at the end of this section.

**At runtime none of the twelve performs any type check.** They compare raw 32-bit register contents,
signed. So on unverified input `if-lt` on two references compares two opaque handles, and that is a
**design choice** to make, not a fidelity target.

**Throws nothing.** All twelve carry `CAN_CONTINUE` and no `CAN_THROW`.

> **[ART]-only, undocumented, and interesting for a deobfuscator.** ART performs `instance-of` type
> *sharpening* **at the `if-eqz`/`if-nez`**, not at the `instance-of` — narrowing the tested register on
> whichever edge implies the test passed, and skipping it when the type is unresolved or an interface
> (*"interfaces can only be treated as Objects"*). ART otherwise refuses to refine types on branches and
> says so in a comment. If this engine ever wants branch-sensitive type refinement, that is the shape.
>
> **The source, added in Plan 8 Task 1** (this note previously carried no `file:line`):
> `runtime/verifier/method_verifier.cc:3360-3459`, inside the `IF_EQZ`/`IF_NEZ` case. The pattern it looks
> for is spelled out in a comment at `:3373-3384` — *"Check for peep-hole pattern of: … instance-of vX, vY,
> T; ifXXX vX, label"*, with the two disqualifiers *"if there are other branches to this branch"* and
> *"when vX == vY"* — the guard is `:3385-3388`, the quoted interface clause is at `:3392`, and `:3421-3454`
> additionally walks **back through a preceding `move-object`** to sharpen its source register, which the
> earlier prose did not mention. Worth knowing for Plan 8 Task 4: ART's own refinement is attached to the
> **branch**, so a `check-cast`-style narrowing at the `instance-of` is *not* what a device does.

### `packed-switch` / `sparse-switch` (31t) and their payloads

**SPEC** [BC], both rows end with the fallthrough rule explicitly: *"…**or fall through to the next
instruction if there is no match**."*

> **Dalvik payloads have NO default entry, and this is a real divergence from the JVM.** JVM
> `tableswitch`/`lookupswitch` carry an explicit `default` offset in the table; Dalvik's fallthrough is
> implicit and structural — always `switchAddress + 3`. Do not model a Dalvik switch as having a
> default edge stored in its payload.

**The payload is data, never executed — SPEC, stated three ways.** [BC] writes the operand as a
*"signed **"branch"** offset to table data pseudo-instruction"* (scare quotes in the source);
[BC]'s "General design" says payload pseudo-instructions *"must never be encountered during the normal
flow of execution"* and must sit at even code-unit offsets with a `nop` spacer; and **[CON] B22** makes
it normative: *"The `packed-switch-data`, `sparse-switch-data`, and `fill-array-data`
pseudo-instructions must not be reachable by control flow."*

**Targets are relative to the switch, not to the table** — **SPEC**, stated in both payload tables:
*"The targets are relative to the address of the switch opcode, not of this table."* This is why the
oracle had to recover the switch's address by subtracting a hardcoded `3`.

**Payload layouts are SPEC** ([BC]); note `size` is a **`ushort`** — unsigned 16-bit, so 0…65535, and a
`size` of 0 is representable and not forbidden:

| `packed-switch-payload` | | | `sparse-switch-payload` | |
|---|---|---|---|---|
| `ident` | `ushort = 0x0100` | | `ident` | `ushort = 0x0200` |
| `size` | `ushort` | | `size` | `ushort` |
| `first_key` | `int` (signed 32-bit) | | `keys` | `int[]`, *"sorted low-to-high"* |
| `targets` | `int[]` | | `targets` | `int[]` |
| code units | `(size * 2) + 4` | | code units | `(size * 4) + 2` |

**Four execution facts that are [ART]-only and that a naive implementation gets wrong.** All four live in
`runtime/interpreter/interpreter_common.h`, in two functions: **`DoPackedSwitch` at `:443-468`** and
**`DoSparseSwitch` at `:472-503`** (re-fetched and both endpoints re-counted with
`awk 'NR>=a && NR<=b'` in Plan 8 Task 1, which is when the per-fact line numbers below were added — the
two dispatch facts previously carried **no `file:line` at all**):

1. **`size == 0` falls through — and the two functions say it with DIFFERENT text, so cite both.**
   Packed: *"// Empty packed switch, move forward by 3 (size of PACKED_SWITCH)."* (`:453`, quoted in full —
   an earlier revision dropped the parenthetical and substituted a period without marking the elision),
   guarding `return 3;` at `:454`. Sparse: *"// Return length of SPARSE_SWITCH if size is 0."* (`:480`),
   guarding `if (size == 0) { return 3; }` at `:481-483`. An earlier revision cited only the packed comment
   for the sparse row.
2. **Packed dispatch is `index = test_val - first_key` in signed 32-bit, and it WRAPS.** `:461` is the
   subtraction and `:462-467` the `if (index >= 0 && index < size)` / fall-through pair. ART's verifier exists
   to make the overflow unreachable in verified code — `max_first_key` at
   `runtime/verifier/method_verifier.cc:2302-2303`, inside `CheckAndMarkSwitchTargets`
   (`:2243-2337`) — so in unverified code it is live and observable. Use wrapping arithmetic. Note the
   verifier check is gated on `if (switch_count > 1)` (`:2298`), so a one-case table skips it.
3. **Sparse dispatch is a BINARY SEARCH over the raw key array** — `:488-500`, `lo`/`hi`/`mid` with
   `return entries[mid]` at `:498` and a bare `return 3;` at `:502` when the loop exits. So **unsorted keys
   do not error at runtime — they silently make some cases unreachable**, and those selectors fall through.
   A linear-scan or map-lookup implementation will *disagree with ART* on such input, which is a live
   obfuscation vector, and the engine being replaced is exactly a map lookup: see
   [Oracle reconciliation](#oracle-reconciliation-plan-8).
4. **A wrong `ident` is a `DCHECK` at runtime, i.e. a no-op in release** (`:450` packed, `:478` sparse); the
   bytes get interpreted as a table regardless. Validate defensively where ART trusts the verifier — the
   verifier's own check is `FailBadSwitchPayloadSignature` (`method_verifier.cc:2287-2291`).

**VERIFIER-only** (all HARD failures), every one of them in `MethodVerifierImpl::CheckAndMarkSwitchTargets`
(`runtime/verifier/method_verifier.cc:2243-2345`, added in Plan 8 Task 1 — the list previously carried no
`file:line`): payload offset out of range (`:2249-2252`) or mid-instruction (`:2256-2259`); payload not
even-code-unit aligned (`:2261-2264`); wrong `ident` (`:2287-2291`); packed `first_key + size - 1`
overflowing int32 (`:2298-2307`); sparse keys not **strictly** ascending (`:2308-2321` — **[ART]** rejects
duplicates with the literal `if (UNLIKELY(key <= last_key))` at `:2315`, which is *stricter* than
[BC]'s "sorted low-to-high"); and then, **per target**, in the loop at `:2324-2343`: a target offset out of
range (`:2327-2330`), a target that is **not at an instruction start** (`:2331-2335`,
`FailSwitchTargetMidInstruction`), and a target that is a `move-result`/`move-exception` (`:2336-2341`,
`FailSwitchTargetIsMoveResultOrMoveException`) — surviving targets get `SetBranchTarget()` at `:2342`. Plus
[CON] A7/A8. Both key checks sit under `if (switch_count > 1)` (`:2298`). An earlier revision ended this
range at `:2337`, which truncated the function **and cut off the last two named checks**. Note
that B22 is enforced **by reachability**, not by the branch-target check — payloads are marked as valid
opcode boundaries, and a branch *into* one is only caught when the dataflow pass arrives and hits
[ART]'s `NOP` case, failing with *"encountered data table in instruction stream"* (a string byte-identical
in Dalvik 4.4.4 and current ART).

**Throws nothing.** `CAN_CONTINUE` only. Payload opcodes carry flags `0x0000` — dexlib2 models them as
non-executable, agreeing with B22.

> **dexlib2 normalizes packed into sparse form.** `SwitchPayload.getSwitchElements()` returns
> `List<? extends SwitchElement>` with `getKey()`/`getOffset()` for **both** kinds; there is no
> `getFirstKey()` and no `getTargets()`. The packed keys are synthesized as `firstKey + index` — in Java
> `int` arithmetic, which **wraps the same way ART's `test_val - first_key` does**, so a map built from
> the expanded keys reproduces fact 2 for free. Say that in a comment, or a later reader will "fix" it.
> `getOffset()` is the raw payload `int`, still relative to the switch, un-rebased.

### `instance-of` (22c)

**SPEC** [BC]: *"Store in the given destination register `1` if the indicated reference is an instance of
the given type, or `0` if not."* Plus a note: *"Since B must always be a reference … this will always
result in `0` being stored if C refers to a primitive type."*

| Condition | Result | Tier |
|---|---|---|
| source is null | `0` | **INHERITED** — [JVMS] `instanceof`, verbatim: *"If objectref is null, the instanceof instruction pushes an int result of 0 as an int onto the operand stack."* [BC] never mentions null |
| type index unresolvable | **a linking error**, and in [ART] specifically `NoClassDefFoundError` (cause: `ClassNotFoundException`) | *that it is a linking error* is **INHERITED** — see [Resolution failure is one family](#resolution-failure-is-one-family-tiered-once); *which* exception is now **measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** (family section) + **[ART]** |
| type loadable but inaccessible | `IllegalAccessError` | **[ART] only**, soft-fail path |
| C names a primitive | store `0` per [BC] — though [ART]'s verifier rejects the class outright, so this is only reachable on unverified input | **SPEC** vs **VERIFIER** conflict; follow [BC] |

> 🔴 **Resolution happens BEFORE the null check, which is the OPPOSITE of the JVMS ordering.** [JVMS]
> resolves *after* — verbatim, and re-fetched in Plan 8 Task 1: *"If objectref is null, the instanceof
> instruction pushes an int result of 0 as an int onto the operand stack. Otherwise, the named class,
> array, or interface type is resolved (§5.4.3.1)."* So per JVMS a null register with an unresolvable
> type yields `0`. [ART] resolves first, in five lines:
> `INSTANCE_OF` (`runtime/interpreter/interpreter_switch_impl-inl.h:837-850`) calls
> `ResolveVerifyAndClinit` at `:838-843`, bails on `nullptr` at `:844-846`, and only then reads
> `GetVRegReference(B())` at `:847` before `SetVReg(A(), (obj != nullptr && obj->InstanceOf(c)) ? 1 : 0)`
> at `:848`. So it **throws**. An implementation written from JVMS will be wrong here. Line numbers added in
> Plan 8 Task 1; this note previously named the two symbols with no `file:line`.

**`instance-of` does not trigger static initialisation, and the evidence is the WHOLE handler body.** The
five lines quoted above *are* the whole of `INSTANCE_OF` (`:837-850`): resolve, `nullptr` bail, one
`GetVRegReference(B())`, one `SetVReg(A(), … ? 1 : 0)`, `return true`. No allocation, no field access, no
other call — so there is nothing left in the opcode that *could* initialize. That whole-body reading is the
claim's support.

> ⚠️ **Do NOT re-derive this from `can_run_clinit = false`, which is what this note used to say and which is
> a non-sequitur.** The flag at `:842` is real (`ResolveVerifyAndClinit`'s fourth parameter is `bool
> can_run_clinit`, `runtime/entrypoints/entrypoint_utils.h:155-161`) and it still proves only *"this call
> site does not itself run `<clinit>`."* **The counterexample is `NEW_INSTANCE`
> (`runtime/interpreter/interpreter_switch_impl-inl.h:862-894`), which passes `false` at the same fourth
> positional argument (`:868`) and unambiguously DOES initialize the class** — downstream, in
> `AllocObjectFromCode(c, Self(), allocator_type)` at `:882` → `CheckObjectAlloc` →
> `CheckClassInitializedForObjectAlloc` → `ClassLinker::EnsureInitialized`
> (`runtime/entrypoints/entrypoint_utils-inl.h:236-252`, `:213-230`, `:185-211`; that file's own header
> comment at `:232-233` says *"Performs a clinit check too."*, and the handler then asserts it at `:891`:
> `obj->GetClass()->AssertInitializedOrInitializingInThread(Self())`). Verified by raw fetch of both files at
> `refs/heads/main`, 2026-08-03. See [the tier
> ranking](#the-tiers-are-not-equally-strong--the-ranking-for-a-claim-about-what-the-runtime-does) item 2 for
> why a flag is never a path.

**Never throws NPE or ClassCastException.** Writes `vA` (`SETS_REGISTER`), result type boolean.

### `check-cast` (21c)

**SPEC** [BC]: *"Throw a `ClassCastException` if the reference in the given register cannot be cast to
the indicated type."* **This is one of only two exceptions [BC] names anywhere**, so it is genuinely
SPEC rather than inherited — rare in this rewrite.

| Condition | Result | Tier |
|---|---|---|
| register holds null | **passes** | **INHERITED** — [JVMS] `checkcast`: *"If objectref is null, then the operand stack is unchanged."* [BC] does not mention null |
| non-null and not assignable | `ClassCastException` | **SPEC** |
| type index unresolvable | **a linking error**, in [ART] `NoClassDefFoundError`; inaccessible → `IllegalAccessError`; **and resolution precedes the null check**, so a null register with a bad type still throws | *linking error* **INHERITED** ([Resolution failure is one family](#resolution-failure-is-one-family-tiered-once)); the type and the ordering now **measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** (family section) + **[ART]** |

**The ordering, with the source.** `CHECK_CAST`
(`runtime/interpreter/interpreter_switch_impl-inl.h:819-835`) calls `ResolveVerifyAndClinit` at `:820-825`,
bails on `nullptr` at `:826-828`, and only then reads `GetVRegReference(A())` at `:829` before
`if (UNLIKELY(obj != nullptr && !obj->InstanceOf(c))) ThrowClassCastException(c, obj->GetClass())` at
`:830-833`. The `obj != nullptr` conjunct **is** the null-passes rule, and it sits *after* resolution.

**`check-cast` does not trigger static initialisation either — same form of argument, the WHOLE body.** The
lines just enumerated are the whole of `CHECK_CAST` (`:819-835`): resolve, `nullptr` bail, one
`GetVRegReference(A())`, a conditional `ThrowClassCastException`, `return true`. Nothing allocates and
nothing else is called. `can_run_clinit` is also `false` here (`:824`) but **that fact is not the evidence** —
see the counterexample blockquote under [`instance-of`](#instance-of-22c): `NEW_INSTANCE` passes the same
`false` and initializes downstream in `AllocObjectFromCode`.

> [BC]'s note claims a primitive type operand *"will **necessarily** fail at runtime"*. **That is false
> when the register is null** — the null short-circuit precedes the cast test, so `check-cast vNull, I`
> succeeds. A documented-but-wrong sentence.

**The register question, which matters for the value lattice.** At runtime `check-cast` **writes
nothing** — its only effect is a possible throw. But dexlib2 declares it `SETS_REGISTER`, and that is
not a bug: it reflects the **static type** view, because [ART]'s verifier narrows the register's type on
the success path. **Grounded in Plan 8 Task 1, having previously carried no `file:line`:** the shared
`CHECK_CAST`/`INSTANCE_OF` verifier case is `runtime/verifier/method_verifier.cc:3120-3170`, the write is
`work_line_->SetRegisterType<LockOp::kKeep>(inst->VRegA_21c(inst_data), res_type)` at **`:3165`** (the
`instance-of` sibling two lines later writes `kBoolean` at `:3167`), and the comment is at `:3122-3128`,
verbatim: *"If this instruction succeeds, we will "downcast" register vA to the type in vB. (This could be a
"upcast" -- not expected, so we don't try to address it.)"* That parenthetical is the evidence for the next
sentence rather than an inference from it: it **replaces** rather than intersects, and ART's own comment says
a widening cast is simply not addressed. So model `check-cast` as **value unchanged, declared type narrowed
to the cast type** — conflating `SETS_REGISTER` with "writes a value" corrupts the lattice; ignoring it loses
the type refinement that makes `check-cast` useful for deobfuscation. **[ART]/UNSPECIFIED** — documented in
no published spec.

> **ONE EXCEPTION to that directive, added by Plan 8 Task 9, and it is part of the rule rather than a
> footnote to it: the narrowing is SKIPPED when it would stop the register reading as a null reference.**
> The directive above is about the *verifier's* static type. The interpreter is a separate fact and it
> closes this: `CHECK_CAST` (`runtime/interpreter/interpreter_switch_impl-inl.h:819-835`) contains **no
> `SetVReg*` call of any kind** — it resolves, reads `GetVRegReference(A())` at `:829`, may throw at
> `:830-833`, and returns. So a register whose last write was a primitive still has its reference half
> cleared *after* a successful cast (`runtime/interpreter/shadow_frame.h:401-408`), and the next
> `instance-of` reads `nullptr` and stores `0`.
> ⚠️ **All of which is the SWITCH tier, and the shape this guard fires on is the one the two tiers disagree
> about, so the guard's justification is VERIFIER-tier and not [ART].** nterp's `op_check_cast`
> (`mterp/x86_64ng/object.S:1-22`) does not read the reference half either — it reads the **raw** with
> `GET_VREG` and dereferences a non-zero value as an object pointer — so there is no nterp *"reads `nullptr`
> and stores `0`"* to appeal to for `Known(5, "I")`. The guard stays exactly as it is; what carries it is the
> verifier hard-fail quoted two sentences below plus spec §2.2, not a measured ART answer. The other two
> shapes the guard preserves — `Known(null, T)` and the integral zero under a reference tag — **are**
> corroborated by both tiers. See
> [ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim).
> Re-tagging `Known(5, "I")` to `Known(5,
> "Ljava/lang/String;")` would instead satisfy the engine's own `Definitely` row against an `Object`
> target and fold `Known(true, Z)` — verbatim **D9** (closed by Plan 8 Task 8, in the `instance-of`
> reconciliation below), one opcode downstream of the op that closed it. Cited as a D-number rather than as
> a link because these reconciliation items are blockquotes and carry no anchors.
> **And the precision cost is provably zero:** the shared
> `CHECK_CAST`/`INSTANCE_OF` verifier case fails HARD on a non-reference source —
> `runtime/verifier/method_verifier.cc:3156-3158`, verbatim: *"`} else if
> (!orig_type.IsReferenceTypes()) { Fail(VERIFY_ERROR_BAD_CLASS_HARD) << opcode << " on non-reference in v"
> << orig_type_reg; return false;`"* — so a `check-cast` on an `int`-typed register is input a device
> **refuses to link**, and there is no runtime behaviour to lose. The guard fires on exactly one shape, a
> **non-zero primitive raw under a primitive tag**: `Known(null, T)` and Dalvik's integral zero under a
> reference tag both still read as null after the re-tag, so both are re-tagged as before. Keyed on the
> `readsAsNullReference` predicate rather than on `type.isReference`, which is what preserves those two
> rows. Implemented in `core/op/CheckCastOp.narrowedOrUnchanged`; pinned by
> `core/op/CheckCastOpTest.everySpellingOfReadingAsANullReferencePassesTheCast`.

No `<clinit>` here either.

### `const-class` (21c)

**SPEC** [BC]: *"Move a reference to the class specified by the given index into the specified register.
In the case where the indicated type is primitive, this will store a reference to the primitive type's
degenerate class."* ("Degenerate class" = `int.class` / `Integer.TYPE`.)

Resolution failure → `NoClassDefFoundError` / `IllegalAccessError`, same path as the two above. *That it
is a linking error* is **INHERITED** ([below](#resolution-failure-is-one-family-tiered-once)); *which*
exception is now **measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** (family section) + **[ART]**, unspecified by [BC]. dexlib2 marks it
`CAN_THROW`.

**No `<clinit>` here either, and this one is the load-bearing case — so it gets the strongest form of the
argument this file can currently make: the WHOLE HANDLER BODY.** `CONST_CLASS`
(`runtime/interpreter/interpreter_switch_impl-inl.h:755-767`, raw fetch at `refs/heads/main`, 2026-08-03) is
**thirteen lines and this is all of them**: `ResolveVerifyAndClinit` at `:756-761`, `if (UNLIKELY(c ==
nullptr)) return false;` at `:762-764`, `SetVRegReference(A(), c)` at `:765`, `return true` at `:766`. There
is no allocation, no field access, and **no other call at all** — so there is nothing in the opcode that
could initialize a class, whatever any single argument to the resolve says. That is the evidence.

> ✅ **MEASURED on real ART, 2026-08-03 — this claim is no longer an inference.** Run
> `tools/art-runner/run-batch.sh Fx_ConstClassDoesNotInitialize`. The fixture executes
> `const-class LProbe_ClinitWitnessA;` against a witness class whose `<clinit>` prints a line, and the
> observation is:
>
> ```
> FIXTURE	Fx_ConstClassDoesNotInitialize	OK	java.lang.String(Probe_ClinitWitnessA)
> ```
>
> **No `CLINIT_RAN` line** — and the returned `String` is `getName()` called on the stored `Class`, so the
> operand demonstrably *resolved*: the silence is initialization not happening, not the opcode not running.
> **Read it with its control, which is why the control exists:** `Fx_StaticFieldDoesInitialize` triggers a
> SECOND witness through a `sget` on a non-constant static field — a definite trigger per [JLS] 12.4.1 — and
> prints `CLINIT_RAN	Probe_ClinitWitnessB`. Same runtime, same witness mechanism, one silent and one loud.
> Without that pair the absence of output would be worthless, since a witness that never fires looks
> identical to one that was never triggered. Two separate witness classes, run in two separate VMs, so
> neither can initialize the other's.
>
> The experiment was the maintainer's suggestion, and it is the first claim in this document promoted from
> source-reading to measurement. **`instance-of`'s twin claim above is NOT yet measured** — the same fixture
> shape would settle it, and until someone writes it that section rests on the whole-body reading alone.

The `c` it stores is the canonical `mirror::Class` for that class in that loader, which is the runtime side
of [VER]'s parenthetical that **two `const-class` on the same class yield the SAME reference**.

> ⚠️ **The argument this paragraph used to make was `can_run_clinit = false` at `:760`, and that is a
> NON-SEQUITUR. Do not reinstate it.** The flag is verbatim and the parameter it binds really is
> `ResolveVerifyAndClinit`'s fourth, `bool can_run_clinit`
> (`runtime/entrypoints/entrypoint_utils.h:155-161`) — and it still proves nothing about the opcode, only
> about that one call. **`NEW_INSTANCE` (`interpreter_switch_impl-inl.h:862-894`) passes `false` at the same
> fourth positional argument (`:868`) and DOES initialize the class**, downstream in `AllocObjectFromCode`
> at `:882` → `CheckObjectAlloc` → `CheckClassInitializedForObjectAlloc` → `EnsureInitialized`
> (`runtime/entrypoints/entrypoint_utils-inl.h:236-252`, `:213-230`, `:185-211`), which is why the handler
> can assert `AssertInitializedOrInitializingInThread` at `:891`. A citation can be line-exact, verbatim,
> and load-bearing for a conclusion it does not support; three review layers passed this one. See [the tier
> ranking](#the-tiers-are-not-equally-strong--the-ranking-for-a-claim-about-what-the-runtime-does), item 2.

**[JLS]/[JVMS] corroborate, and corroboration is all they can do here.** [JVMS] `ldc` pushes *"a reference
to the Class object representing that class or interface"* — one object, not a fresh one per execution — and
initialization is §5.5, whose trigger list does not include `ldc`. The stronger-sounding version is [JLS]
§12.4.1, whose trigger list ends *"A class or interface will not be initialized under any other
circumstance."* (re-fetched verbatim from `jls/se21/html/jls-12.html`, 2026-08-03); producing a `Class` object
is not on that list, so read literally it forbids what the oracle does. **Cite it as corroboration and never
as proof**: it is the **Java language** specification, two translations from a device (JLS → d8 → dex → ART),
and it constrains ***what*** triggers initialization, not ***when*** initialization has already happened.
Note also that the same list *does* include *"Invocation of certain reflective methods in class `Class` and in
package `java.lang.reflect`"* — which is exactly why the oracle's `ClassUtils.getClass(..., true)` initializes:
it reaches a device-side trigger the *opcode* never reaches, so §12.4.1 identifies the oracle's defect as a
host-reflection artefact rather than a Dalvik behaviour.

> **And the *when* is genuinely not a hard invariant on Android, which is the part worth carrying forward.**
> ART's AOT compiler runs `<clinit>` **at build time**: `dex2oat/driver/compiler_driver.cc` calls
> `class_linker->EnsureInitialized(self, klass, true, true)` at `:2341`, wrapped in
> `EnterTransactionMode(is_app_image, klass.Get())` at `:2339` (raw fetch at `refs/heads/main`, 2026-08-03).
> **This does NOT make [JLS] §12.4.1 false for Android, and saying so would be an overclaim** — the
> transaction exists to keep the optimization semantics-preserving: on success `ExitTransactionMode()`
> (`:2350`), on abort `RollbackAllTransactions()` (`:2371`) followed by `CHECK_EQ(old_status,
> klass->GetStatus()) << "Previous class status not restored"` (`:2372`). The defensible statement is that
> **initialization *timing* is an ART optimization decision, not an invariant** — and AOSP's own evidence for
> that reading is that it **disables the optimization for debuggable builds**, `:2306-2308` verbatim:
> *"Optimization will be disabled in debuggable build, because in debuggable mode we want the `<clinit>`
> behavior to be observable for the debugger, so we don't do the `<clinit>` at compile time."* If the timing
> were unobservable there would be nothing for a debuggable build to preserve. For this engine the practical
> consequence is narrow but real: *"the class is already initialized"* is a state a device can arrive in
> without executing any triggering opcode, so a deobfuscation strategy must not infer *"`<clinit>` has not
> run"* from *"no triggering opcode has executed."*

**So a `const-class` that runs `<clinit>` is a defect, not a conservative choice**; the engine being replaced
has it, filed at `b3abe32:docs/architecture/opcodes.md:176` as Correctness Defect 21, and the reconciliation below
proves the mechanism.

### Resolution failure is one family, tiered once

**`instance-of`, `check-cast` and `const-class` all fail resolution the same way, and the family is tiered
here so the three rows above cannot drift apart.** An earlier revision tiered `const-class`'s failure
`[ART] only` in this document and **INHERITED** in Plan 8's Task 10, and the other two `[ART] only` even
though [JVMS] carries the identical clause for both — the point of a single subsection is that there is one
place to be wrong.

**The JVM counterpart has to be named per instruction, and one of the three is not what it looks like.**
`instance-of` ↔ `instanceof`, `check-cast` ↔ `checkcast`, and **`const-class` ↔ `ldc`, NOT `anewarray`** —
`ldc` is the instruction that resolves a class reference and pushes its `Class` object. Verified verbatim
from [JVMS] SE21 §6.5 (fetched, tags stripped, whitespace flattened, Plan 8 Task 1):

- `checkcast` and `instanceof` each carry, under **Linking Exceptions**: *"During resolution of the symbolic
  reference to the class, array, or interface type, any of the exceptions documented in §5.4.3.1 can be
  thrown."*
- `ldc` carries, under **Linking Exceptions**: *"During resolution of a symbolic reference, any of the
  exceptions pertaining to resolution of that kind of symbolic reference can be thrown."* Its class case is
  *"if the run-time constant pool entry is a symbolic reference to a class or interface, then the named class
  or interface is resolved (§5.4.3.1) and value, a reference to the Class object representing that class or
  interface, is pushed onto the operand stack."*

So, for all three:

| Claim | Tier |
|---|---|
| *"resolution failure is a linking error"* | **INHERITED**, cited per instruction from the clauses above |
| the exact exception **type** — `NoClassDefFoundError` (an `Error`), or `IllegalAccessError` | **Measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** across all five resolving opcodes (see below); + **[ART]** source read (cause `ClassNotFoundException`). [JVMS]'s clauses defer to §5.4.3.1 and name nothing |
| the **ordering** — resolution *before* the null check | **Measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, same run**: `instance-of`/`check-cast` of an absent type on a **null** source still throw, so resolution precedes the null shortcut; + **[ART]**. [JVMS] puts the null check first for both type ops, and `ldc` has no null case at all |
| no `<clinit>` | **[ART]**, from reading each of the three handler bodies IN FULL — `CONST_CLASS` `:755-767`, `CHECK_CAST` `:819-835`, `INSTANCE_OF` `:837-850` are 13, 17 and 14 lines and none of them allocates, touches a field, or calls anything but the resolve. [JVMS] §5.5 / [JLS] §12.4.1 (whose trigger lists exclude `ldc`/`checkcast`/`instanceof`) are **corroboration only**. **NOT `can_run_clinit = false`** — see the ⚠️ blockquotes under [`instance-of`](#instance-of-22c) and [`const-class`](#const-class-21c): `NEW_INSTANCE` passes the same `false` and initializes downstream in `AllocObjectFromCode` |

**Measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`.** One fixture shape parameterized by opcode, each
naming an absent `Lcom/example/DefinitelyMissing;` against a present-type control: `new-array`,
`filled-new-array`, `instance-of`, `check-cast` and `const-class` ALL raise `java.lang.NoClassDefFoundError`
(message *"Failed resolution of: L…;"*), and every present-type control resolves cleanly — so it is not the
classpath failing wholesale. The family is therefore a `LinkageError`/`Error`, **not an `Exception`**: a
`catch (Exception)` wrapper does not catch it, which is the handler-reachability consequence this document
asserted from source and now measures. The `instance-of`/`check-cast` fixtures use a **null** source and still
throw, which also measures the resolution-before-null-check ordering above.

**The family is now SEVEN opcodes, not five, and the two additions were the one part of it that was still
extrapolation** (Plan 9 Task 15 Step 4c, 2026-08-05). `Fx_SGetAbsentType` and `Fx_InvokeAbsentType` — an
absent defining class behind a static-field read and behind an `invoke-static`, each against a new
`Fx_SGetPresentType` / `Fx_InvokePresentType` control — **both raise `java.lang.NoClassDefFoundError` with the
same message shape**, on Tier A `-Xint` and again on Tier B (`nterp` default, API 36), where the two
interpreter tiers also agree with each other. So `[JVMS]` §5.4.3.2 (field resolution → §5.4.3.1) and §5.4.3.3
(method resolution → §5.4.3.1) are corroborated rather than relied on, the ⚠️ caveat that used to sit in the
Plan 9 section below is discharged, and **the measurement agreed with the extrapolation** — worth recording,
because the tier rule's value comes from being applied when it might not have.

#### A missing METHOD on a PRESENT class is the same family, one member along

**Measured — `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated `[DEVICE](Tier B, nterp default, API 36)`**
(Plan 9 Task 15, adjudication 7b). Everything above is about a missing *class*. A missing *method* on a class
that resolves is a different §5.4.3.3 failure and it answers **`java.lang.NoSuchMethodError`** — also a
`LinkageError`, so also an **`Error`** and also outside a `catch (Exception)`. `Fx_InvokeMissingMethod`
(`invoke-static {}, Ljava/lang/String;->definitelyNotAMethod()Ljava/lang/Object;`) against the same
`Fx_InvokePresentType` control.

**And the ORDERING against a null receiver is measured, not inherited**, which matters because it is the one
composition the differential oracle could never have shown: `Fx_InvokeMissingMethodNullReceiver` holds both
conditions at once — a definite-null receiver *and* a method that does not exist on the (present) class — and
a device throws **`NoSuchMethodError`, not `NullPointerException`**. So **resolution precedes the null check
for `invoke` exactly as it does for `check-cast`/`instance-of`**. That is what a guest handler sees:
`catch (NullPointerException)` is dead on this shape and `catch (Error)` is live.

#### The ordering rule is "resolution before the NULL CHECK" and does not generalise — `new-array` is the
#### counterexample, and it splits between interpreter tiers

**Read this before writing "the family resolves first" anywhere.** The ordering measured above is specifically
resolution versus a **null check**. It is *not* resolution versus every other precondition, and `new-array` is
where that bites: its other precondition is a **negative length**, which is checked *first*, so a both-bad
`new-array` answers `NegativeArraySizeException` — an `Exception` that a `catch (Exception)` **does** catch —
where the resolution failure is an `Error` that it does not. Same which-handler-is-live stake as everything
else in this section, pointing the other way.

🔴 **And it is tier-dependent — the first SEMANTIC `nterp`-vs-switch divergence measured in this repo** (Plan 9
Task 15 fix round, 2026-08-05). `tools/art-runner/fixtures/Fx_NewArrayAbsentTypeNegativeLength.smali` puts both
conditions on one instruction (`new-array v0, -1, [Lcom/example/DefinitelyMissing;`) and gets two different
exception **types** depending on which interpreter runs it, on one device:

| measurement | answer | order |
|---|---|---|
| `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` | `NegativeArraySizeException("-1")` | length first |
| `[DEVICE](Tier B, -Xint switch, API 36)` | `NegativeArraySizeException("-1")` | length first |
| `[DEVICE](Tier B, nterp default, API 36)` | `NoClassDefFoundError` | **class first** |

An **interpreter-tier** split, not an ART-version one: the two `-Xint` runs agree across the whole
Android-10/11-to-API-36 gap, and `nterp` — the modern default, i.e. what actually runs on a device — disagrees
with both. `tools/emulator/cross-validate.sh` reports `83 of 84` tier-identical and fails its gate on this one
fixture, which is the harness doing its job.

**Consequences, stated rather than smoothed over:**

- The `[ART]` source reading in `core/op/NewArrayOp.kt:48-57` — `CheckArrayAlloc` testing `component_count < 0`
  before `GetResolvedType`/`ResolveType` (`runtime/entrypoints/entrypoint_utils-inl.h:292-322`) — is
  **confirmed for the switch interpreter and contradicted for nterp**. nterp does not route through that
  helper. So that claim is now `[DEVICE]`-tier *for the tier it matches*, not `[ART]`-tier for all of them, and
  it is no longer a claim about "what ART does" without a tier attached.
- The engine implements the **switch** order. Whether it should model `nterp` instead is **open, and larger
  than one opcode** — it is the standing "which interpreter tier does this engine model?" question. The
  existing carve-out in [the tier ranking](#the-tiers-are-not-equally-strong--the-ranking-for-a-claim-about-what-the-runtime-does)
  says only that a Tier A run cannot settle a **register-read** question; this is an allocation-ordering
  question that behaves identically, so the carve-out is **broader than it is currently written**. Surfaced for
  the maintainer rather than decided; changing `NewArrayOp` would move a pinned migrated expectation
  (`core/op/NewArrayOpTest.aNegativeLengthBeatsAnAbsentComponentClassBecauseArtChecksTheSizeFirst`) and a
  registered differential entry (`createBothBadArrayClass()V`).
- `filled-new-array` cannot compose the same pair — its count is the instruction's own unsigned nibble/byte, so
  it cannot be negative (`core/op/FilledNewArrayOpTest.nothingIsForkedBecauseThisOpHasNoArrayIndexOrCountToBeWrong`).

One caveat to state rather than gloss: the inheritance bridge is [VER]'s *"the same set of checks as are
described in The Java Virtual Machine Specification, **Second Edition**"* while every [JVMS] citation in this
file is **SE21**. §5.4.3.1 resolution, §5.5 initialisation triggers and the `checkcast`/`instanceof`/`ldc`
linking clauses are unchanged across those editions, so the inheritance holds for *these* rules — but the gap
is real, it is
[capped in the stricter direction](#and-the-same-document-immediately-names-an-exception-to-it-which-nothing-here-recorded)
for sub-32-bit integer widths, and the bridge itself carries [three unmeasured
leaps](#the-bridge-carries-three-unmeasured-leaps--state-them-do-not-gloss-them) that must not be described
as harmless.

### Subtype testing: entirely INHERITED

**Dalvik documents no subtyping algorithm at all.** [JVMS] `checkcast` gives the operational rules
verbatim (`instanceof` carries the same, differing only in the final clause), and they require the
**full class hierarchy including interfaces, plus recursive array covariance**:

> "If S is a class type, then: — If T is a class type, then S must be the same class as T, or S must be
> a subclass of T; — If T is an interface type, then S must implement interface T. If S is an array type
> SC[] … — If T is a class type, then T must be `Object`. — If T is an interface type, then T must be
> one of the interfaces implemented by arrays (JLS §4.10.3). — If T is an array type TC[] … TC and SC
> are the same primitive type [or] TC and SC are reference types, and type SC can be cast to TC by
> recursive application of these rules."

Answers to the questions an implementation actually asks, from **JLS §4.10.3**:

| Question | Answer |
|---|---|
| `String[]` <: `Object[]`? | **yes** — `S[] >₁ T[]` iff `S >₁ T` |
| `int[]` <: `Object`? | **yes** — *"If P is a primitive type, then: Object >₁ P[]"* |
| `int[]` <: `Object[]`? | **no** — the array rule requires **both** component types to be references |
| `int[]` <: `Cloneable` / `Serializable`? | **yes** — explicitly listed for primitive components |
| Which interfaces do arrays implement? | exactly `Cloneable` and `java.io.Serializable` |

Two traps: **array covariance is deliberately unsound** (which is why [CON] B15 exists and why
`aput-object` needs a runtime check), and **primitive arrays are invariant** — `int[]` and `long[]` are
unrelated and neither is an `Object[]`.

> ✅ **MEASURED on real ART, 2026-08-04 — the whole matrix, no longer INHERITED-alone.**
> `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, pinned in `tools/art-runner/golden/tier-a-batch.txt`
> (`tools/art-runner/check-golden.sh`). The full grid — candidate ∈ {`String[]`, `Object[]`, `int[]`,
> `long[]`} × target ∈ {`Object`, `Cloneable`, `Serializable`, `Object[]`}, for **both** `instance-of` and
> `check-cast` — runs as `Fx_Subtype_*` (plus `Fx_IntArrayIsCloneable`, the cell Task 1 opened), each boxing
> its `0`/`1` so the runtime's answer is observed directly. **Every cell matches JLS §4.10.3 above, and
> `instance-of`/`check-cast` measured IDENTICAL at every cell** — so "one algorithm" is now a measurement
> rather than a documentation promise (`check-cast` returns `1` where it succeeds and `0` where it catches
> `ClassCastException`). The cells the rule makes surprising, which are the only ones that can falsify it:
>
> ```
> FIXTURE	Fx_Subtype_InstanceOf_IntArr_ObjectArr	OK	java.lang.Integer(0)
> FIXTURE	Fx_Subtype_InstanceOf_LongArr_ObjectArr	OK	java.lang.Integer(0)
> FIXTURE	Fx_Subtype_InstanceOf_StringArr_ObjectArr	OK	java.lang.Integer(1)
> FIXTURE	Fx_Subtype_InstanceOf_LongArr_Cloneable	OK	java.lang.Integer(1)
> FIXTURE	Fx_Subtype_InstanceOf_IntArr_Serializable	OK	java.lang.Integer(1)
> FIXTURE	Fx_Subtype_CheckCast_IntArr_ObjectArr	OK	java.lang.Integer(0)
> ```
>
> `0` for `int[]`/`long[]` against `Object[]` (a primitive-component array is invariant), `1` for
> `String[] <: Object[]` (covariance) and for every primitive array against `Cloneable`/`Serializable`. This
> **upgrades the runtime evidence from a build tool (`dx`'s `Merger`, below) to the runtime itself**; JLS
> §4.10.3 stays the INHERITED source and `Merger` stays corroboration. The clean core reproduces every cell
> (`core/ClassIndexTest.theRow24SubtypeMatrixAgreesWithTheDeviceAtEveryCell`); the ORACLE does not — it folds
> `false` for every array/`Cloneable`/`Serializable` cell, the registered `types-int-array-is-cloneable`
> divergence the device sweep confirms is the oracle's bug and not the core's.

**Android's own toolchain models the array rows the same way, and this is corroboration rather than the
primary source — the tier is INHERITED from JLS §4.10.3 above, not [ART].** Plan 8's Task 7 brief asked for
AOSP `Merger.isPossiblyAssignableFrom` to be cited as *"the primary source"*; that is tier-wrong and is
recorded rather than followed, because `dx`'s verifier model is an implementation, not a specification, and
the table above already has the stronger citation. What the AOSP source adds is that no Android component
disagrees. Fetched raw, `?format=TEXT | base64 -d`, from
[`platform/dalvik2/dx/src/com/android/dx/cf/code/Merger.java`](https://android.googlesource.com/platform/dalvik2/+/refs/heads/main/dx/src/com/android/dx/cf/code/Merger.java)
(305 lines; the two quotes are at `:210-214` and `:290-296` — **both endpoints checked with
`awk 'NR>=a && NR<=b'`**, the instrument that exists because a bad range was once wrong at its start while
its interior resolved: `does` is on `:210`, and `:211` begins at `not`), verbatim:

> "does not assume anything about class hierarchy other than that the type `Object` is the supertype of all
> reference types and all arrays are assignable to `Serializable` and `Cloneable`."

> "Other than Object (handled above), array types are assignable only to Serializable and Cloneable." —
> above `return (supertype == Type.SERIALIZABLE) || (supertype == Type.CLONEABLE);`

The same function's array-vs-array branch strips matched dimensions and recurses (`:284-289`), which is
JLS §4.10.3's covariance rule written as code — the rule `core/TypeJoin.kt` applies component-wise.

**The oracle omits both interfaces, and Plan 8 Task 7 diverges from it deliberately.** See the
`instance-of` disagreement in [Oracle reconciliation](#oracle-reconciliation-plan-8); the divergence is registered as
`types-int-array-is-cloneable` in `differential/Fixtures.kt` and pinned against the oracle's own answer by
`core/ClassIndexTest.everyArrayIsAnInstanceOfCloneableAndSerializableUnlikeTheOracle`.

### Where the verifier boundary is documented

**[BC] never mentions verification** — zero occurrences of `verif` in its rendered body. **[CON] is the
document**, and its framing sentence is the one to quote when justifying any VERIFIER-tier choice:

> "A `.dex` file is the transport format for Dalvik bytecode. There are certain syntactical and
> semantical constraints for a file to be a valid `.dex` file, and **a runtime is required to support
> only valid `.dex` files.**"

That is the spec formally disclaiming everything this engine exists to do. Constraints bearing on this
plan: **A6** (all `goto`/`if` targets are opcodes in the same method), **A7**/**A8** (switch targets
likewise; sparse tables *"consistent and sorted low-to-high"*), **A17**/**A18** (type-index validity for
`const-class`/`check-cast` and `instance-of`), **B17** (*"It must not be possible to leave the `insns`
array at the bottom"*), **B22** (payload unreachability).

**Soft vs hard failure** is documented only in the Dalvik-era **[VER]** note: a deferrable problem gets
the offending instruction replaced with an *"always throw"* opcode and verification continues — which is
what [IF]'s otherwise-mysterious `20bc` format ("suggested format for statically determined verification
errors") exists to encode. The modern `HARD_FAILURE`/`SOFT_FAILURE` vocabulary appears **nowhere** in
published docs; [ART]'s `verifier_enums.h` is the only authority, and its comments are the
verify-error → runtime-exception mapping. **An unresolvable type is a SOFT failure**, which is why
`check-cast`/`instance-of` on a missing class throw at execution rather than rejecting the class.

**ART vs Dalvik for every opcode in this section: no documented differences, and none material.** The
verifier rules **agree in outcome** between Dalvik 4.4.4 and current ART for every opcode here, but they are
**not the same code and not the same rule** — see the deleted-clause note in the `if-*` section, where 4.4.4's
symmetric `OP_IF_EQ` case and its missing null register kind are quoted. The three real deltas are narrow:
ART adds the packed-switch `first_key` overflow check (`method_verifier.cc:2298-2307`); **`if-eq vNull, vInt`
verifies while `if-eq vInt, vNull` hard-fails, which is ART-only**; and ART rejects
`move-result`/`move-exception` branch targets earlier. **No per-runtime behavior switch is needed**, because
this engine targets deobfuscation fidelity (spec §3) and not either verifier.

> **The asymmetry is real, and its stated MECHANISM was wrong — corrected in Plan 8 Task 1 from
> `method_verifier.cc:3319-3337`.** This used to read *"ART distinguishes `kNull` from `kZero`, so … 
> (asymmetric and uncommented)"*. Two corrections. First, the discriminator is **`IsZeroOrNull()`**, which
> covers zero *and* null alike, and it is tested on **vA only**: `if (reg_type1.IsZeroOrNull())` at `:3324`
> lets vB be a reference **or** an integral; the integral branch is reached only when vA is neither, and it
> then requires **both** operands integral (`:3328-3329`), which `kNull` fails. So the asymmetry is
> *positional* — vA is given the permissive treatment and vB is not — and the `kNull`/`kZero` split is a
> second, independent reason (`Zero` is integral via `IsBooleanTypes`, `Null` is not), which is why
> `if-eq vInt, vZero` verifies while `if-eq vInt, vNull` does not. Second, **it is not uncommented**: `:3324`
> carries *"// zero then integral or reference expected"*, `:3326` *"// both references?"* and `:3328`
> *"// both integral?"*.
>
> **Why this matters more than a doc nit.** It means a null-versus-integral `if-eq` is **verifier-legal in
> one operand order**, so [BC]'s *"`(Object) null == (int) 0`"* answer for it is a fidelity requirement on
> *verified* input, not a free choice for garbage. See
> [Oracle reconciliation](#oracle-reconciliation-plan-8) — the engine being replaced gets that case wrong.

### Oracle reconciliation (Plan 8)

Everything above is about **Dalvik**. This subsection is about the **engine being replaced**: per opcode,
what it does, whether that agrees, with a source `file:line` **and** the `docs/architecture/` section that
already covers it. It exists because every migrated expected value in Plan 8's Tasks 4–10 came from the
oracle, so each disagreement is an expectation that changes.

**Precedence, stated once because every judgement below turns on it.** ORACLE is **not** authority about
Dalvik. It is evidence that a behaviour survived years of real malware, and it is what the differential
compares against — but where it contradicts SPEC or INHERITED it is a **defect** and the core is right to
diverge. Where `docs/architecture/` disagrees with the source tree, the **source tree** wins; it still
ships. Every divergence gets a registry entry with a stated reason, because a silent difference is not
acceptable.

**Fourteen numbered disagreements, D1–D14.** Plan 8's repaired text carried nine of them; **five are new —
D2, D6, D7, D9 and D11** — and so are three unnumbered findings: the **13** migrated `if` expectations D1
moves, the `LinkageError` escape next to D13, and the boxed-`Integer(0)` settlement at the end. Everything
new is marked 🆕; every disagreement is marked 🔴. Line numbers with no filename are relative to the file
named in the heading above them.

#### `if-eq`…`if-le` (22t) and the `*z` forms (21t) — `opcode/IfOp.kt`

Architecture: `b3abe32:docs/architecture/opcodes.md:58` (Family Matrix, *Conditional control*) — *"Known reference
comparison uses host identity; numeric/boolean/character operands are coerced to 32-bit integers"*.

| The oracle | Where | Verdict |
|---|---|---|
| Either operand `isUnknown` → **fork** `[nextAddress, targetAddress]` | `:44-46` | **agrees** — spec §2.2's explore-more direction |
| Fall-through is the **first** transition | `:45`, `arrayOf(nextAddress, targetAddress)` | **agrees**; the core emits `listOf(Continue, Jump(target))` for parity, and `graph.outcomes` is ordered so this is observable |
| `*z` on `Known(null, T)` → cmp `0` | `:48-50` | **agrees** — [BC]'s *"`(Object) null == (int) 0`"* |
| `*z` on a live host object → cmp `1` (via `lhs.raw === rhs.raw` against a boxed `Integer(0)`) | `:41`, `:57` | **agrees** — a pointer is provably non-zero. `testIfPrimitiveArrayNotEqualZero` pins it for a definitely-allocated `[B`, and a core that forked here would be inventing an infeasible branch |
| both operands `Number`/`Boolean`/`Char` → compare as `Int` | `:51-53`, `:59-64` | **agrees for the integral raws**, which are one 32-bit domain in Dalvik. **Disagrees for `Float`/`Double`** — see 🔴 D1 |
| otherwise → host **identity**, `lhs.raw === rhs.raw`, for **every** `ifType` | `:57`, `:66` | **agrees where both operands are references, and disagrees where they are not** — sharpened in Task 4, because "agrees" overstated it. Two references: same pointer → all six decided, different pointers → `if-eq`/`if-ne` decided (the corpus depends on this; see open question (b)). A pointer against a NON-ZERO integral, and an ORDERING comparison over two distinct pointers: the oracle still answers definitely from `cmp ∈ {0, 1}` and the core **forks**, because a pointer's magnitude is not something the lattice holds. Both are VERIFIER-tier engine choices, in the *Choices this plan makes* table below |
| `registersAssignedCount = 1` for an op that assigns nothing | `:37` | **disagrees**, and is **inert**: the value only sizes an `ExecutionState` map (`ExecutionState.kt:47-48,73-74`, `ExecutionNode.kt:22-23`) and no query reads it. Same family as the over-declaration `core/op/APutOp.kt:242` and `core/op/FilledNewArrayOp.kt:130` already record. Recorded, not registered. **The READ count is wrong too, and for a reason worth keeping**: `registersReadCount = if (compareToZero) 1 else 2` (`:36`) is a class-body initializer, so it runs during primary-constructor init — before the secondary constructor's `compareToZero = false` at `:33` — and is `1` for **both** forms. Neither is harmless in the new core, where `ExecutionGraph.availableRegisters` is purely static and these two sets are the sole guarantors of a correct free-register answer, so `core/op/IfOp` declares `{lhs}`/`{lhs, rhs}` and an empty assigned set. Both pinned by `core/op/IfOpTest.aBranchReadsItsOperandsAndAssignsNothing`, which closes the gap `b3abe32:docs/architecture/state-model.md:290` lists as missing; the oracle's exact defect (one register for both forms) is one of its two lethal mutations |

🔴 **D1 — `if-*` on a `Float`/`Double` raw compares NUMERICALLY instead of answering "unprovable".**
`Value.toInteger()` (`smalivm/src/main/java/org/cf/smalivm/Value.kt:102-104`) is
`Utils.getIntegerValue` (`org/cf/util/Utils.java:159-161`), which is `castToPrimitive`'s `Number →
intValue()` row (`Utils.java:56` onward). So `1.0f` becomes `1`. A device compares the register's **bits**:
`1.0f` is `0x3F800000` = 1065353216, so the oracle's answer is not merely imprecise, it is **wrong even
bit-wise**, and it takes **one** successor where the truth is unknown. Unsound direction — a feasible arm
left unexplored on a `Complete` run. Architecture: `b3abe32:docs/architecture/opcodes.md:58`'s *"coerced to 32-bit integers"*.

🆕 **And this MOVES 13 migrated expectations, which no Plan 8 document had counted.** Every `setRegisters`
line in `opcode/IfOpTest.kt` was enumerated: 71 seed lines, **13 with a `Float` or `Double` literal**, all
13 in the `ValueTypeCombinations` nested class, all 13 `if-eq`, all 13 currently asserting
`IF_TRUE_VISITATIONS`. Under the provability rule they all become forks, i.e. `IF_ALL_VISITATIONS`. By
**name**: `testIfEqualWithByteAndDouble`, `testIfEqualWithByteAndFloat`, `testIfEqualWithDoubleAndBoolean`,
`testIfEqualWithDoubleAndChar`, `testIfEqualWithFloatAndBoolean`, `testIfEqualWithFloatAndChar`,
`testIfEqualWithFloatAndDouble`, `testIfEqualWithIntAndDouble`, `testIfEqualWithIntAndFloat`,
`testIfEqualWithLongAndDouble`, `testIfEqualWithLongAndFloat`, `testIfEqualWithShortAndDouble`,
`testIfEqualWithShortAndFloat`. The other 17 are integral-only and do not move.

**LANDED in Task 4, and the count re-derived exactly: all 13 now assert `IF_ALL_VISITATIONS` in
`core/op/IfOpTest`, each marked `MOVED` on its own test.** Two things the enumeration had not seen, both
measured while migrating:

- **A DIFFERENT 13 also move FIXTURE, and they are not the same 13.** Any seed with a `J`/`D` **tag**
  needs a register PAIR — `Frame.assign` reserves `register + 1` as the wide high half — and
  `ifEqual()V` has `.locals 2`, so a wide seed in v1 has nowhere to put it. Measured by reverting the
  relocation: **8 threw `IllegalArgumentException` before the op ever ran** (`Frame.assign`'s
  `require(high < registerCount)`), and for the other 5 `assign` **cleared the pair's low half**
  (`clearWidePartner` fires on v1, which was v0's reserved `WideHigh`), so v0 read `Top` and the
  predicate became unprovable -- 2 of those 5 failed and **3 passed for the wrong reason**, their
  expectation being a fork anyway. `testIfEqualWithLongAndDouble` shows why no register
  count fixes it in place: a long in v0 and a double in v1 needs `(v0, v1)` **and** `(v1, v2)` at once,
  which no device has. The oracle's flat string-keyed state has no reference/high half at all, so the
  corpus could express a register file that cannot exist; the core refusing it is the core being right.
  Those 13 now run against a new `ifEqualWithRoomForWidePairs()V` (`if-eq v0, v2`, `.locals 4`,
  **identical addresses 0/2/3**), raws and tags byte-identical, only the operand register renumbered — a
  change in where the harness reads a value, not a change to the value. The two sets overlap in 8: five
  `F`-tagged rows keep `ifEqual()V` while moving expectation, and **five** rows move fixture while
  keeping their answer (`ByteAndLong`, `IntAndLong`, `LongAndBoolean`, `LongAndChar`, `ShortAndLong`):
  13 - 8 = 5 on each side.
- **The 18/12 split is computed and recorded on the `ValueTypeCombinations` KDoc**, by name. A pair
  verifies exactly when both tags are integral (`IsIntegralTypes` excludes `kLongLo`, `kFloat` **and**
  `kDoubleLo`), which is **12** of the 30 — the ten `Z`/`B`/`S`/`C`/`I` pairs plus the two `*z` rows — so
  the other **18** (every row with a `J`, `F` or `D` tag) are engine choices for input no verifier admits
  and are **not** tagged INHERITED.

🔴🆕 **D2 — `if-eq`/`if-ne` on a reference-versus-integral MIX answers by identity, so `Known(null)` is not
equal to `Known(0)`.** `null` is not a `Number`/`Boolean`/`Char`, so control reaches `:66` and
`null === Integer(0)` is `false` → cmp `1` → `if-eq` **false**. [BC] says the bits are identical, so the
right answer is **true**, in either operand order. **This is not merely an unverifiable-input choice**:
`if-eq vNull, vInt` *verifies* (`method_verifier.cc:3324-3325`), so the oracle is wrong on **verified**
input here. **Not corpus-observable** — no `opcode/IfOpTest` seed pairs a null with an integral zero — so no
migrated expectation moves, and a core-only test plus a registry entry are the only way to pin it.

**FIXED in Task 4, and it needed no special row.** `provableInt` answers `Exactly(0)` for `Known(null, T)`
and for `Known(0, "I")` alike, so the *both operands exactly known* row decides it as `0 == 0`, in both
operand orders. Pinned by `core/op/IfOpTest.aNullReferenceAndAnIntegralZeroAreEqualBecauseTheirBitsAre`,
which asserts both orders **and** that exactly one successor is built (mutation: dropping the null row
kills it, plus `testIfNullEqualZero` and one `ProvableTest` row — 3 dead of 921).

**No `Fixtures.kt` entry for D2, and the reason is narrow: there is no opcode to build the fixture from.**
A differential fixture must construct its own operands from implemented opcodes, and **nothing in this
build produces a `Known(null)`** — `const/4 v0, 0x0` yields `Known(0, "I")`, which is the *other* side of
this comparison, and `const-string` cannot be null. So both engines would be handed two integral zeros and
would agree; the oracle's defect needs a genuinely null-valued register, which arrives with Plan 9's
`invoke`/field reads. Task 4's core test is the pin until then.

> **This paragraph used to give a DIFFERENT reason — that no `DivergenceShape` can express a
> core-forks/oracle-decides divergence at all — and that reason was measured FALSE in review**: the
> `ORACLE_MISJUDGES_FEASIBILITY` branch's `registers` assertion holds whenever the fork's arms are inert
> with respect to the observed register, which is the construction its two existing entries already use.
> Task 4's two engine choices are registered under exactly that shape (`if-lt-two-distinct-references`,
> `if-eq-reference-against-nonzero-int`). The false premise is recorded rather than deleted because it was
> supporting **two** conclusions and only one of them survives — which is precisely how the surviving one
> would have inherited unearned authority.

#### `packed-switch` / `sparse-switch` (31t) — `opcode/SwitchOp.kt`

Architecture: `b3abe32:docs/architecture/opcodes.md:58`, `b3abe32:docs/architecture/opcodes.md:92` (*Control and Resume Semantics*), `b3abe32:docs/architecture/opcodes.md:165` (defect
10), `b3abe32:docs/architecture/execution-engine.md:122`.

🔴 **D3 — three out-of-band channels and a magic constant, i.e. the hidden control state spec §5.2 exists
to remove.** `execute` writes the selector into the **result register** (`:21`) and the fall-through address
into a **pseudo-return-address slot** (`:23`), then jumps to the payload (`:24`). Those writes land on the
*parent* node's state, so `SwitchPayloadOp` recovers them through `ExecutionState.peekKey`'s **ancestor
walk** (`ExecutionState.kt:425-434`, `findNearestAncestorWithKey` at `:487-496`,
`getPsuedoInstructionReturnAddress` at `:498-500`), and it recovers the switch instruction's own address by
subtracting a hardcoded **`3`** (`opcode/SwitchPayloadOp.kt:26`, the constant at `:69`). Third channel, and
the most damning: the **scheduler names the class** — `if (opcode.canContinue() && current.op !is SwitchOp)`
(`VirtualMachine.kt:340`) — to suppress a fall-through successor dexlib2 says exists. Consequence for
Plan 8: the payload **is executed**, so `wasReached(payloadAddress)` is `true` in the oracle and `false` in
the core; that is representational, and it is handled by the projection convention
(`MethodView.PAYLOAD_OPCODES` at `core/MethodView.kt:103-105`, one set shared with
`ResultProjection.payloadAddressesOf`). **All six `opcode/SwitchOpTest` expectations contain address 8** —
`PackedSwitch` and `SparseSwitch` × `testKnownPredicateVisitsExpectedLabel`,
`testUnhandledPredicateVisitsNextOp`, `testUnknownPredicateVisitsAllLabelsAndNextOp` — and all six must
drop it.

`registersAssignedCount = 2` (`:16`) is honest about those two out-of-band writes rather than wrong, and
`toString` rendering `nextAddress` instead of the payload target (`:27`) is defect 10, cosmetic.

#### the payloads — `opcode/SwitchPayloadOp.kt`

🔴 **D4 — BOTH switch kinds dispatch through ONE `HashMap.containsKey`.** `:34`, over a table built at
`:78-81` from `instr.switchElements`, because dexlib2 normalises packed and sparse into the same
`(key, offset)` list and the oracle never distinguishes them. So:

- **packed: AGREES with [ART].** dexlib2 synthesises packed keys as `firstKey + index` in Java `int`
  arithmetic, which wraps exactly as `test_val - first_key` does (`interpreter_common.h:461`). Worked
  example: `first_key = 2147483647`, `size = 2`, selector `-2147483648` → ART's `index` wraps to `1` and
  takes `targets[1]`, whose synthesised key is `2147483647 + 1` = `-2147483648` = the selector. Same case.
  **No divergence entry needed.**
- **sparse: DISAGREES with [ART].** A map lookup finds every key; ART's binary search
  (`interpreter_common.h:488-500`) does not, on a table that is not strictly ascending — those keys are
  silently **unreachable** and their selectors fall through. **Registered in Task 6 as
  `switch-sparse-descending-keys`** (`ORACLE_MISJUDGES_FEASIBILITY`, `oracleNeverExplores = {4, 5}` on
  `Ldifferential/switch;->sparseDescendingKeys()I`). The core's answer is neither dispatch alone but the
  **union** of both — see the choice table row, which this task decided the opposite way from the one
  recorded before execution.

The audit's per-op gap list says nothing about dispatch, and that absence is the signal: there is nothing
wrong with a map lookup *in the oracle*, only with the runtime it is supposed to model.

🔴 **D5 — the unknown-selector children come out in `HashMap` iteration order.** `getTargets` walks
`targetKeyToOffset.values` (`:57-65`) and the fall-through is **appended last** (`:29-30`). So the oracle's
switch fork order is not merely different from the core's, it is **not well-defined**. Record it as a
standing fact: **switch fork order can never be an oracle-parity requirement, and a later plan must not try
to make it one.** It is invisible to the corpus because `Tester.testVisitation` compares an address *set*.

🔴🆕 **D6 — the same `Float`/`Double` defect as D1, at a second op family the plan names only for `if`.**
`:33` is `Utils.getIntegerValue(result.raw)`, the identical `intValue()` path, so a `2.9f` selector
dispatches to case **2**. Under the provability rule an unprovable selector must fork every arm plus the
fall-through. **Not corpus-observable**: all six `opcode/SwitchOpTest` seeds hold `Integer` raws
(`1` under `Z`, `1` under `B`, `100` under `S`, `100` under `I`, and `UnknownValue()` twice) — which is why
Task 6 pinned it core-only, by name, in
`core/op/SwitchOpTest.aFloatSelectorIsUnprovableSoEveryArmIsExplored`.

🔴 **REGISTERED in Task 6's fix round 1 as `switch-float-selector-read-by-value`**
(`ORACLE_MISJUDGES_FEASIBILITY`, `oracleNeverExplores = {4, 5, 6, 7, 10, 11}` on
`Ldifferential/switch;->floatSelectorReadByValue()I`, v0 seeded `2.9f to "F"`). ⚠️ **Task 6's first round
claimed no fixture the differential can run could reach it — *"the sweep seeds `Known` values only and no
smali method puts a float in a switch selector"* — and that is FALSE.** A `Known` Float is exactly what
`Fixture.seededRegisters` carries (`cmp-float-nan-cmpg` already seeds one), so the selector never had to
come from smali at all; the entry ran green on the first attempt. **Third impossibility-claim-about-the-
harness in three tasks and the second refuted by simply building the fixture** — the standing rule is to
attempt it, not to argue it, and the *"seeds `Known` values only"* half is a true sentence doing no work,
which is how it read as a reason.

🔴🆕 **D7 — a selector the oracle cannot read as an `Int` produces TWO different wrong behaviours, and they
need two separate registry entries.** `Utils.getIntegerValue` (`Utils.java:159-161`) is
`(Integer) castToPrimitive(obj, INTEGER_OBJ)`, and `castToPrimitive` returns its argument **unchanged** for
anything that is not a `Number`/`Boolean`/`Character` (its trailing `return value;`). Split by what the
register holds:

- **D7a — a non-numeric host object (e.g. a `String`): host `ClassCastException`.** The `(Integer)` cast at
  `Utils.java:160` fails inside `getIntegerValue`, before `SwitchPayloadOp` sees anything. Shape:
  **`ORACLE_RAISES`**.
- **D7b — a definite null: the oracle FALLS THROUGH, and it does not crash.** Settled by disassembling the
  compiled `execute` (`javap -c` on `smalivm/build/classes/kotlin/main/.../SwitchPayloadOp.class`), because
  reading the Kotlin alone invites the wrong answer. `Utils.getIntegerValue(null)` returns `null`; the
  boxed value is then passed to `Map.containsKey(Object)` **without unboxing** (bytecode offset `107`), and
  `HashMap.containsKey(null)` over a table of `Integer` keys is simply `false`; the `ifeq` at `112` jumps to
  the fall-through `finishOp(returnAddress)` at `147-152`. The only null check in the method,
  `Intrinsics.checkNotNull` at `128`, guards the map's **value** inside the *taken* branch and is never
  reached. **So there is no `NullPointerException` and no host failure here** — a plausible-looking claim
  that the null unboxes, which an earlier revision of this bullet made and a review round independently
  proposed, is refuted by the bytecode. The defect is a **feasibility** one instead: [BC] makes a null
  selector's bits `0`, so the right behaviour is to dispatch on key `0`. If the table has no key `0` both
  engines fall through and **agree by accident**; if it has one, the oracle **misses that feasible arm and
  takes an infeasible fall-through** — both §2.2 directions from one row. Shape:
  **`ORACLE_MISJUDGES_FEASIBILITY`**, and like D2 it wants both `oracleNeverExplores` and
  `oracleWronglyExplores`, which the enum's `init` may not allow on one entry — check before writing.
  **Task 6 CHECKED that, landed the core's half, and REGISTERED the divergence in both directions** as
  `switch-null-selector-against-key-zero` (`oracleNeverExplores = {6, 7}`, `oracleWronglyExplores =
  {4, 5}`, on `Ldifferential/switch;->nullSelectorAgainstKeyZero()I` with v0 seeded
  `null to "Ljava/lang/String;"`). The `init` require is an `||` rather than an xor, so one entry may name
  both — and it is now **the only entry in the registry that does**, which is what stops each §2.2
  direction resting on a single witness. It needed a table starting at key `0`, because with no key `0`
  both engines fall through and agree by accident: both migrated `swich_test.smali` fixtures hide it.
  Pinned core-side as well, by
  `core/op/SwitchOpTest.aDefinitelyNullSelectorDispatchesOnKeyZeroBecauseItsBitsAreZero`.
  ⚠️ **Task 6 nearly recorded this as unregistrable** — the argument being that a seeded null projects
  asymmetrically between the two engines — and that argument is FALSE: `OracleVm` seeds through
  `Value.wrap(null, T)` and `NewCoreVm` through `Value.Known(null, T)`, which are one fact in two value
  models. Third instance in this plan family of an impossibility claim about the harness that a fixture
  refuted; the standing rule is to attempt the fixture, not to argue.

What the payload op **agrees** on: no key matches → fall through to the recovered return address
(`:39-40`), and an unknown selector forks every target plus the fall-through (`:28-32`).

`SwitchPayloadOpTest.payloadOpHasExpectedToString` is a mockk change-detector over the rendering defect at
`b3abe32:docs/architecture/opcodes.md:174` (defect 19, *"`SwitchPayloadOp` labels relative offsets as addresses"*) and is the second
of Plan 8's two adjudicated change-detectors.

#### `instance-of` (22c) — `opcode/InstanceOfOp.kt`

Architecture: `b3abe32:docs/architecture/opcodes.md:55` (family gap), `b3abe32:docs/architecture/opcodes.md:173` (**Correctness Defect 18**),
`b3abe32:docs/architecture/opcodes.md:200` (*"nullable unknown `instance-of`"* among the behaviours no active verifier-valid VM test
covers), `b3abe32:docs/architecture/type-and-dex.md:95` (array supertypes).

🔴 **D8 — `Unknown(T)` whose declared type satisfies the target folds `Known(true, Z)`.** The type-set loop
at `:34-40` runs **before** the `isUnknown` test at `:41`, so `Unknown("Ljava/lang/String;")` against
`Ljava/lang/String;` — or against `Ljava/lang/Object;`, via `VirtualClass.instanceOf`'s `Object`
short-circuit — answers `1`. But `core/Value.kt:14` defines `Unknown` as *"**May** be null"* and
`Frame.entryFrame` seeds **every** reference parameter that way, and a device answers `0` for a null. So
this folds a wrong **value** and deletes a feasible **path** from one row. `b3abe32:docs/architecture/opcodes.md:173` filed it as
defect 18 years before Plan 8's first revision called the same rule sound. **Migration cost is zero,
measured**: all seven `opcode/InstanceOfOpTest` tests were read and the only `Unknown`-valued one is
`unknownValueWithObjectAsDeclaredTypeIsUnknownIfStringType`, whose declared type is `Ljava/lang/Object;`
against target `Ljava/lang/String;` — the type test **fails**, so the oracle already answers `Unknown(Z)`.

🔴🆕 **D9 — the null row is keyed on `raw == null` (`:30`) rather than on "reads as a null reference".** So
`instance-of v1, v0, Ljava/lang/Object;` with `Known(5, "I")` in v0 skips the null row, and
`declaredAndValueTypeNames` (oracle `Value.kt:84-89`) yields a **set**, `{"I", "Ljava/lang/Integer;"}` — the
loop tries both, and `Ljava/lang/Integer;` **is** an `Object` → the op folds `Known(true, Z)`. (It is a set,
not one name, because the raw's runtime class and the register's declared tag are both candidates; the
conclusion is unchanged, but a reader expecting a single name will not find the code that produces it.) ART's
**switch** interpreter reads the reference half of the register, which a primitive
write cleared, so `obj == nullptr` and `SetVReg(A(), 0)`
(`interpreter_switch_impl-inl.h:847-848`). A confidently wrong constant. Predicted by Plan 8's Global
Constraints from the `readsAsNullReference` chain; **confirmed here against the oracle's own source.** Not
corpus-observable.

**The TIER of this row is VERIFIER, not [ART], and that was corrected on 2026-08-04 without changing the
row.** `Known(5, "I")` is a **non-zero** primitive, and the two interpreter tiers disagree about it: nterp's
`op_instance_of` (`mterp/x86_64ng/object.S:86-107`) reads the **raw** with `GET_VREG` and dereferences `5` as
an object pointer rather than storing `0`, and `method_verifier.cc:3156-3158` hard-fails the input so no
device links it. The divergence from the oracle is **still real and must never be reconciled** — the oracle's
`true` is wrong under either tier and under spec §2.2 — but its grounding is this project's design choice,
not a measured ART answer. See
[ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim).

Also disagreeing, and both already registered elsewhere: **it never throws at all** — the file contains no
`throwException` and no `mayThrow`, so resolution failure and `IllegalAccessError` are unmodelled while
`Opcode.canThrow()` reports `true` (the type is resolved at **build** time, `:65`); and **array supertypes**
— `VirtualArray.instanceOf` (`type/VirtualArray.java:129-141`) never yields `Cloneable`/`Serializable`
(`b3abe32:docs/architecture/type-and-dex.md:95`), which is Plan 8 Task 7's registered divergence.

> **✅ CLOSED by Plan 8 Task 7, and RE-ADJUDICATED by Task 8, which landed the opcode.**
> `ClassIndex.isInstanceOf` answers `true` for every array against `Ljava/lang/Object;`,
> `Ljava/lang/Cloneable;` and `Ljava/io/Serializable;`, **verified against the ORACLE's own answer in the
> same test** — `core/ClassIndexTest.everyArrayIsAnInstanceOfCloneableAndSerializableUnlikeTheOracle`
> asserts `classIndex.isInstanceOf(...)` is true and `classManager.getVirtualType(...).instanceOf(...)` is
> false, for four array types against both interfaces, because an inherited claim about the oracle is this
> project's recurring defect. `core/op/InstanceOfOpTest.everyArrayIsSerializableUnlikeTheOracle` now pins
> the same divergence at the **op** level. Registered as `types-int-array-is-cloneable`, which Task 7
> parked under `NEW_CORE_BOUNDED_SCOPE` because it had no `instance-of` to run and which duly **failed on
> arrival** in Task 8 — the one registry entry whose retirement was NOT a move into `newCoreSupported`.
> Task 8 added `DivergenceShape.NEW_CORE_CORRECTS_A_FOLDED_VALUE` (both engines fold, the values differ,
> evidence `coreCorrectsValue` recording both renderings) and moved it there. `type/VirtualArrayTest`
> **encodes** the oracle's omission (`b3abe32:docs/architecture/type-and-dex.md:234`), so prior art here looks like confirmation:
> never reconcile toward the oracle.

> **✅ D8 CLOSED by Plan 8 Task 8, in the SOUND direction.** `core/op/InstanceOfOp` answers `Unknown(Z)`
> for a `Value.Unknown`/`Value.Top` source **even when the declared type satisfies the target**, because an
> `Unknown` may be null. The rule lives in `core/castOutcome`'s row 3, one implementation shared with Task
> 9's `check-cast` — which reads the *same* outcome as a provable **success**, because for a cast a null
> and a satisfying declared type agree while for `instance-of` they give `0` and `1` (design decision 5).
> Pinned by `core/op/InstanceOfOpTest`'s
> `anUnknownWhoseDeclaredTypeSatisfiesTheTargetIsStillUnknownBecauseItMayBeNull`,
> with `aKnownReferenceStillFolds` as the control against the mirror defect, and registered as
> `types-unknown-string-instance-of-string` (`NEW_CORE_MORE_SOUND`) — which needed a fixture with a
> reference **parameter**, because `Fixture.seededRegisters` is `Known`-only by design. **Migration cost
> measured zero**, as predicted.

> **✅ D9 CLOSED by Plan 8 Task 8.** The null row is keyed on `Value.readsAsNullReference`, not on
> `raw == null`, so `Known(5, "I")` against `Ljava/lang/Object;` answers `Known(false, Z)`. Pinned by
> `core/op/InstanceOfOpTest.everySpellingOfReadingAsANullReferenceAnswersZero` (all three clauses of the
> predicate) and registered as `types-primitive-register-is-instance-of-object`, the second entry of the
> new shape and the OPPOSITE direction from the array one: the oracle folds `true` where the switch
> interpreter says `0` and nterp dereferences the `5` — **neither tier folds `true`, which is why the
> divergence stands even though its tier is VERIFIER rather than [ART]** (that entry's `basis` string was
> corrected accordingly on 2026-08-04; the entry's shape, id and recorded values are unchanged).

> **✅ Resolution failure and Correctness Defect 19 both settled by Plan 8 Task 8 — and resolution failure
> was RE-settled the other way by Plan 9 Task 15.** Plan 8 decided loadability at **decode** time and
> degraded an unloadable operand to a per-address `UnsupportedOp`, declining a definite throw *"because the
> exception type is [ART]-only"*. **That premise expired**: the type is now measured on both interpreter
> tiers, and a `[DEVICE]` measurement outranks a caution standing in for one. Loadability is still decided at
> decode time (the dependency rule requires it), but the op now throws a definite guest
> `NoClassDefFoundError` at **execute** time, before reading its register
> (`core/op/InstanceOfOpTest.anAbsentTargetTypeThrowsNoClassDefFoundErrorEvenOnANullSource`,
> `core/OpBuilderTest.anInstanceOfAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal`). Still never a
> host throw and never a whole-method rejection. Plan 8's declined alternative is kept, retracted in place, in
> `core/op/InstanceOfOp`'s KDoc. `toString` prefixes **both** registers, unlike `opcode/InstanceOfOp.kt:50`.

What it **agrees** on: `Known(null)` → `0` (`:30-33`); the `Object` short-circuit; consulting the declared
tag **and** the host class as a **set**; a failing test on a `Known` value → `0` (`:45`).

#### `check-cast` (21c) — `opcode/CheckCastOp.kt`

Architecture: `b3abe32:docs/architecture/opcodes.md:55`, `b3abe32:docs/architecture/opcodes.md:172` (**Correctness Defect 17**), `b3abe32:docs/architecture/exceptions.md:77`,
`b3abe32:docs/architecture/exceptions.md:89-91` (*Shared State Across Speculative Outcomes*), `b3abe32:docs/architecture/state-model.md:193` (*Modeled and Host
Failures*).

🔴 **D10 — an unprovable failing cast does NOT fork, because `CheckCastOp` asks for a speculative exception
child it never declared.** `:36-39` returns `finishOp(mayThrow = true)` at `:38`; `finishOp` adds
`defaultExceptions` and nothing else (`UnresolvedChildProducer.kt:36-43`); `Op`'s constructor defaults that
array to `arrayOf()` (`opcode/Op.kt:12-13`); and **`CheckCastOp` declares `: Op(location)` with no second
argument** (`:16-20`). So for **this op** the flag is inert and `:38` is equivalent to `:29`'s
`finishOp(mayThrow = false)`. Three audit sites reached the same verdict independently: `b3abe32:docs/architecture/exceptions.md:77`
(*"declares no default `ClassCastException`; `finishOp()` therefore returns only normal continuation"*),
`b3abe32:docs/architecture/opcodes.md:172`, `b3abe32:docs/architecture/state-model.md:193`. **A feasible `ClassCastException` is dropped**, which is spec §2.2's
worst direction.

> **Scope, and an earlier revision of this bullet got it WRONG in a way worth naming.** It claimed the flag
> was inert *"for the whole codebase"*, on the evidence that `grep -rn defaultExceptions` returns *"only the
> four plumbing lines"*. Both halves are false: the grep returns **seven** lines, and **four ops do pass a
> `defaultExceptions` argument — positionally, so the identifier never appears at their call sites and no
> grep for it can find them**:
>
> | Op | Declaration |
> |---|---|
> | `opcode/BinaryMathOp.kt:23` | `Op(location, arrayOf(Pair(ArithmeticException::class.java, "divide by zero")))` |
> | `opcode/AGetOp.kt:20` | `Op(location, NullPointerException::class.java, ArrayIndexOutOfBoundsException::class.java)` — the vararg overload at `opcode/Op.kt:14-17` |
> | `opcode/ArrayLengthOp.kt:19` | `Op(location, arrayOf(Pair(NullPointerException::class.java, "Attempt to get length of null array")))` |
> | `opcode/APutOp.kt:24` | same vararg pair as `AGetOp` |
>
> **So the mechanism is live and four ops use it.** `check-cast` is the op that requests it without declaring
> it — which is precisely what `b3abe32:docs/architecture/exceptions.md:77` and `b3abe32:docs/architecture/state-model.md:193` say, and it is the whole content
> of the defect. The false claim also contradicted `b3abe32:docs/architecture/exceptions.md:89` and `:91` — *"`finishOp(mayThrow =
> true)` emits one continuation plus every default exception child"*, naming `AGetOp`, `APutOp`,
> `ArrayLengthOp` and `BinaryMathOp` — **two lines after this bullet cites those very lines.** Recorded
> rather than quietly corrected, because the failure mode is general: a `grep` that finds nothing proves
> nothing about a *positional* argument, and **a sweep is not tree-wide until you have named what it
> covered.**

🔴🆕 **D11 — the null-passes short-circuit asks the wrong null question, and it INVENTS a definite
`ClassCastException` in two distinct ways.** The short-circuit is
`if (!itemType.isPrimitive && item.isNull) return true` (`:54-57`), with `itemType` taken from the **declared
tag**. It has two conjuncts and **they fail on different inputs, so name the right one per row** — an earlier
revision of this bullet blamed the primitive gate for a case where `item.isNull` is the decisive conjunct,
which would have sent an implementer to the wrong line:

**The `ART` column names the SWITCH interpreter**, per
[ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim);
the two rows below are on opposite sides of that split and the last column says which.

| Register | Which conjunct fails | Oracle | ART's switch interpreter — and the tier |
|---|---|---|---|
| `Known(5, "I")` | **`item.isNull`** — `raw == null` is false. The primitive gate is *also* false, but removing it changes nothing, so it is **not** the line to fix | type loop (`:58-63`) finds neither `I` nor `Ljava/lang/Integer;` castable to `String`; `target.isKnown`, so `:33-35` throws a **definite** `ClassCastException` | `GetVRegReference(A())` → `nullptr` → the `obj != nullptr` conjunct is false → the cast **succeeds** (`interpreter_switch_impl-inl.h:829-833`). ⚠️ **VERIFIER, not [ART]** — a non-zero primitive, so nterp instead dereferences the `5` (`mterp/x86_64ng/object.S:1-22`) and `method_verifier.cc:3156-3158` refuses to link it. The oracle is wrong either way; the engine's `passes` is a spec §2.2 choice |
| `Known(null, "I")` | **`!itemType.isPrimitive`** — here `item.isNull` is *true* and the primitive gate is the only thing rejecting it. This is the row the gate is decisive for | short-circuit skipped; the type set collapses to `{"I"}` and `VirtualPrimitive.instanceOf` (`type/VirtualPrimitive.java:35-40`) is `equals`-only → false → **definite `ClassCastException` for a definite null** | `nullptr` → **passes**. **[ART], both tiers** — a zero raw, so nterp's `cbz`/`testl` takes the same branch |

**So the fix is the predicate, not the gate:** the null row must ask `readsAsNullReference`, which is `true`
for both rows above — the first via its primitive-typed clause, the second via `isNull`. Both are the mirror
defect: an infeasible throw invented, telling a deobfuscator a `catch` is live when it is not. Predicted by
Plan 8's Global Constraints, **confirmed here at source.** Not corpus-observable —
`objectNotOfCastTypeThrowsClassCastException` seeds `0x42` under `Ljava/lang/Integer;`, which is **non-zero
and reference-tagged**, so it misses both rows above and the
[boxed-`Integer(0)` row](#-what-a-boxed-integer0-means-to-a-dereferencing-op--settled-here) below, and
**no migrated expectation moves either way.**

Also: **no resolution-failure path** (the type is resolved at build time, `:76`).

> **✅ D11 CLOSED by Plan 8 Task 9, and the closure is wider than the oracle's own null question.**
> `core/castOutcome`'s first row asks `Value.readsAsNullReference`, so **both** rows of the table above now
> pass the cast instead of inventing a definite `ClassCastException`: `Known(5, "I")` (where `item.isNull` is
> the decisive conjunct) and `Known(null, "I")` (where `!itemType.isPrimitive` at `opcode/CheckCastOp.kt:55`
> is). Both are driven by
> `core/op/CheckCastOpTest.everySpellingOfReadingAsANullReferencePassesTheCast`, together with the third
> spelling the predicate owns — Dalvik's integral zero under a reference tag — and the row order is
> inherited from `castOutcome` rather than restated in the op. Two mutations pin it, both measured: keying
> the row on `raw == null` kills that test, Task 8's
> `core/op/InstanceOfOpTest.everySpellingOfReadingAsANullReferenceAnswersZero` **and**
> `differential/NewCoreDifferentialTest.recordedDivergencesStillDivergeInTheRecordedWay` (through registry
> D9's own entry); mapping the null row to a throw kills it **and** the migrated
> `canCastNullWithObjectTypeToString`. Still **not
> corpus-observable**, exactly as this section predicted — the migrated seed is `0x42` under
> `Ljava/lang/Integer;`, non-zero and reference-tagged — so the closure needed tests the corpus does not
> contain and **no migrated expectation moved.**
>
> **The register consequence is where this task went beyond the D11 rows**, and it is recorded with the
> modelling directive it qualifies rather than here: passing the cast is not the same as re-tagging the
> register, and on the primitive-raw row the core deliberately does **not** re-tag. See the exception
> blockquote under [The register question](#check-cast-21c).

What it **agrees** on: a null **passes** and the register is re-tagged to the cast type with the raw
unchanged (`:28`, pinned by `canCastNullWithObjectTypeToString`); a `Known` failing cast is a **definite**
`ClassCastException` (`:33-35`); and the success path's re-tag **replaces** rather than intersects, so it
will also **widen** — which matches [ART]'s `SetRegisterType(vA, res_type)` and is now pinned
*differentially*, on both engines at once, by the `types-cast-string-to-object` fixture
(`Ljava/lang/String;` cast to `Ljava/lang/Object;`, both projecting
`KNOWN great maker Ljava/lang/Object;`).

What it does **not** agree on, and the earlier revision of this paragraph had it as an agreement: `:28`
re-tags **unconditionally**, and the core does not. On a value that reads as a null reference *because its
tag is a non-zero primitive*, the core keeps the null-read and leaves the register alone — see the
exception blockquote under [The register question](#check-cast-21c) for the ART sources and for why the
precision cost is provably zero. **Not given a `D` number**, deliberately: the oracle never reaches that
state, because its own D11 rows throw before they can re-tag, so there is no observable oracle behaviour to
file. Every other re-tagging row agrees, including both null rows the migrated tests assert.

The message at `:32` is **CORPUS** tier only: [RT] records that exception text was
deliberately changed between runtimes, so it is held to keep the migration byte-identical and for no other
reason.

#### `const-class` (21c) — `opcode/ConstOp.kt`

Architecture: `b3abe32:docs/architecture/opcodes.md:25` (build-time resolution), `b3abe32:docs/architecture/opcodes.md:47` (Family Matrix, *Constants*),
`b3abe32:docs/architecture/opcodes.md:176` (**Correctness Defect 21**), `b3abe32:docs/architecture/opcodes.md:196` (the test that codifies the linkage defect),
`b3abe32:docs/architecture/exceptions.md:73`.

🔴 **D12 — the class load INITIALIZES the host class while the node reports effect `NONE`.** `:74` is
`ClassUtils.getClass(classLoader, binaryClassName)`, and the oracle's own author doubted it in the line
above: `// TODO: does const-class really not cause class init?` (`:73`). **Settled by bytecode rather than
by documentation**: in the exact pinned dependency (commons-lang3 **3.12.0**, `build.gradle:34`),
`ClassUtils.getClass(ClassLoader, String)` compiles to `aload_0; aload_1; iconst_1; invokestatic
getClass:(Ljava/lang/ClassLoader;Ljava/lang/String;Z)Ljava/lang/Class;` — the `iconst_1` **is**
`initialize = true`. A device does not: ART's whole `CONST_CLASS` handler is a resolve, a `nullptr` check and
a `SetVRegReference` (`interpreter_switch_impl-inl.h:755-767` — [the whole-body reading](#const-class-21c),
**not** the `can_run_clinit = false` at `:760`, which `NEW_INSTANCE` also passes while initializing), with
[JVMS] §5.5 / [JLS] §12.4.1 corroborating that `ldc` is not a trigger. So the oracle runs
host static initialisers, with their side effects and their errors, outside the virtual class state and with
`sideEffectLevel` still `NONE`. **The core must not initialize.**

🔴 **D13 — resolution failure is a host `ClassNotFoundException`, and it crosses the `Error`/`Exception`
line.** `:75-77` catches `ClassNotFoundException` and **returns the exception object as the constant**;
`execute` then sees `constant is Throwable` and calls `throwException(constant)` (`:34-36`). ART throws
`NoClassDefFoundError` (cause `ClassNotFoundException`) — an `Error`. **This one IS corpus-observable and it
is the sharpest migration risk in the plan:** `const_test.smali`'s `constClassUnknown()V` catches
`Ljava/lang/Exception;`, so the oracle's throw is **caught** and a device's would **escape**, and
`ConstOpTest.nonExistentClassNameThrowsException` asserts exactly the oracle's type. That is
`NEW_CORE_CORRECTS_EXCEPTION_TYPE`'s escaping sub-case, whose evidence field
(`IntendedDivergence.coreThrowsUncaught`) already exists. Task 10 owns it.

🆕 Related and unregistered: `:75` catches **only** `ClassNotFoundException`. Because the load initializes
(D12), an `ExceptionInInitializerError` or any other `LinkageError` **escapes as a host failure** rather
than becoming a guest outcome.

🔴 **D14 — resolution happens at EXECUTE time.** `b3abe32:docs/architecture/opcodes.md:25`, verbatim: *"`ConstOp` defers class
loading until execution"*, and the deferral is commented in the factory at `:126-133`. The reason is in the
audit and it is not tidiness: `updateTemplateOps()` *"eagerly builds every instruction in a method, so one
unsupported or unresolvable instruction aborts graph construction even when that instruction is
unreachable"* (`b3abe32:docs/architecture/opcodes.md:27`), restated as *"even an unreachable occurrence rejects the entire method"*
(`b3abe32:docs/architecture/opcodes.md:88`). The core resolves at **decode** time because of the dependency rule, so a failure must
never be a host throw and never a whole-method build failure. On obfuscated input a whole-method rejection is
strictly worse than any per-path abort: `Partial(reasons)` still leaves `wasReached` and per-address
`sideEffectLevel` usable, a build failure leaves nothing.

**But "not a host throw" does not have to mean `UnsupportedOp`, and since Plan 9 Task 15 it does not.** A
decode-time *resolution* failure builds an op that throws the measured guest `NoClassDefFoundError` at execute
time, which is better than a per-address abort on the same two counts the paragraph above cares about: the run
stays `Complete` rather than `Partial`, so every other address keeps its trust, and the failure is a *guest*
outcome a handler can be tested against instead of an engine refusal a consumer must interpret. `UnsupportedOp`
remains the answer for a decode failure that is genuinely the engine's own (an unimplemented opcode, a
malformed payload target, a host loader that cannot define a class the model does contain).

What it **agrees** on: the resolved `Class` object is **identity-stable** for one class loader, because
`ClassUtils.getClass` bottoms out in `Class.forName`'s per-loader cache — which is what makes
`ConstOpTest.canConstClassLocal` able to assert the very object `vm.classLoader.loadClass(...)` returns, and
which is the oracle-side confirmation of [VER]'s *"two `const-class` on the same class yield the SAME
reference"*.

#### The three questions this section left open, now answered

**(a) Decode versus execute resolution — confirmed.** The oracle resolves at **execute** time and
deliberately; its failure representation is the two-step `return e` / `constant is Throwable` pair above.
See D13 and D14. The core's decode-time resolution is a requirement of the dependency rule, and
`b3abe32:docs/architecture/opcodes.md:27`/`:88` are why the degradation must be per-address.

**(b) The `if-eq` reference-identity path — confirmed, the corpus DOES depend on it.** `opcode/IfOpTest`'s
`CompareObjectReferences` has **ten** tests and **three** rest on host identity:
`testIdenticalObjectReferencesAreEqual` (the same object in both registers, `if-eq` true),
`testIfNotEqualWithStringAndArrayReferenceIsTrue` (a `String` against an `IntArray`, `if-ne` true), and
`testIfNotEqualWithTwoDifferentStringReferencesIsTrue`, which distinguishes host **identity** from host
**equality**: it deliberately builds two equal-content, non-identical `String`s — its comment is *"Need to
get crafty or javac will be smart enough to use same literal for both objects"* — and expects `if-ne` to be
**true**. The oracle compares `lhs.raw === rhs.raw` (`:57`, `:66`). So **`Value.agreesWith` is the wrong
predicate for this question**: it falls through to host `==` (`core/Value.kt:169`), which for those two
`String`s is `true`, which would make `if-ne` false — a *feasible path left unexplored on a `Complete`
run*. The identity question needs its own helper.

**(c) A non-strictly-ascending sparse table — confirmed absent, and the consequence is a hard one.**
`swich_test.smali`'s `sparseSwitch()V` table is `0x1 -> :sswitch_0`, `0x2 -> :sswitch_1` — **strictly
ascending** — and its `packedSwitch()V` table is `.packed-switch 0x1` with three targets. So **the corpus
cannot observe D4's sparse divergence at all.** A differential entry alone cannot pin it either, because
both engines would have to run the same fixture: it needs a hand-written core test or a **new** fixture with
a descending or duplicate key. (Note the file name is misspelled `swich_test.smali` while the class is
`Lswitch_test;`; cite the method, not the filename, or a `find` will miss it.)

> **Fixture address hygiene, narrowed to the one method it applies to.** In `packedSwitch()V` the inline
> `# @N` comments disagree with the built addresses: they label the trailing `nop` `@8` and put the payload
> after it, but the payload **is** at 8 — `PackedSwitch.testUnhandledPredicateVisitsNextOp` expects exactly
> `{0, 1, 4, 8}`, which pins it there and leaves no room for a `nop`, so dexlib2 must elide that redundant
> alignment `nop`. The elision is not proven here, but **it does not need to be**: the expectation pins the
> address either way. `sparseSwitch()V`'s comments are **correct** — `nop # @7` and `.sparse-switch # @8` are
> exactly what its 1+3+1+1+1 code units produce, with the `nop` genuinely needed for even alignment. An
> earlier revision warned against the whole file's comments, which was over-broad.

#### Choices this plan makes for a VERIFIER-tier or UNSPECIFIED condition

**These are choices, not facts about Dalvik.** Each condition below is one the verifier forbids or the
documents never address, so there is no fidelity target and *"the only way to agree with the runtime"*
overstates one runtime's implementation. Recorded here rather than only in a KDoc so later plans inherit
them instead of relitigating them.

| Condition | Why it is a choice | The choice, and its owner |
|---|---|---|
| **A non-strictly-ascending sparse table, KNOWN selector** | [CON] A8 (*"The corresponding table must be consistent and sorted low-to-high"*) sharpened by [ART]'s stricter `key <= last_key` (`method_verifier.cc:2315`) makes it a HARD verifier failure | **FORK over the union of both dispatches**, i.e. [ART]'s binary-search answer *and* every case whose key matches. Task 6, **LANDED**. ⚠️ **This row previously read "match [ART]'s binary search", and that was decided the other way on execution** — committing to the search alone is still a single confident answer resting on one runtime's implementation detail, which is what this table's own preamble forbids reading into a VERIFIER-tier condition, and spec §2.2 says an unprovable *feasibility* explores MORE. The union is free on every verifiable input: the search misses a key exactly when no case matches it, so on a strictly ascending table the two answers coincide and collapse to one successor (there is no `isAscending` flag anywhere — the union IS the rule). Pinned by `core/op/SwitchOpTest.aDescendingSparseTableForksBecauseNeitherDispatchIsProvablyTheRuntimes`, which carries the ascending control in the same test, and differentially by `switch-sparse-descending-keys` (`ORACLE_MISJUDGES_FEASIBILITY`, `oracleNeverExplores = {4, 5}`) against the `switch-sparse-known-match` control in `newCoreSupported` |
| **A KNOWN REFERENCE as a switch selector** | [ART]'s verifier requires an integral selector, so a reference here is VERIFIER-invalid by construction and no device ever dispatches one | **No case matches; fall through, and do not fork.** Half of it is provable and half is the choice: key `0` is *provably* excluded, because [BC]'s *"`(Object) null == (int) 0`"* read the other way makes a live pointer provably non-zero — but the lattice does not hold a pointer's numeric value, so `pointer != 1` is not provable and this op answers *"a reference's bits are a heap address and are none of this table's keys"* anyway. True of any device running real code (references are aligned heap addresses; compiled switch keys are small), and the alternative is the invents-a-live-branch defect on the corpus's own fixture: `packedSwitch()V` with a reference selector would report three `return-void`s live that no device reaches. Task 6, **LANDED**, pinned by `core/op/SwitchOpTest.aLivePointerSelectorMatchesNoCaseAndDoesNotFork` in both raw spellings (`HeapRef` and host object). **This deliberately diverges from the `if-eq`/`if-ne`-on-a-non-zero-integral row below, which FORKS on the same unprovable question ("is this live pointer equal to a specific non-zero key?"); neither should be "fixed" to match the other.** The cost profiles differ: a 2-way `if` fork is cheap and refusing to decide loses a feasible path, whereas forking every arm of a large `switch` (up to 65535) on a pointer selector is a frontier-width blowup — and empirically a heap address never equals a compiled switch's small keys, so excluding them is sound rather than a guess |
| **`if-lt`/`if-ge`/`if-gt`/`if-le` on two references** | integral-only, `method_verifier.cc:3338-3350`; `IsIntegralTypes` excludes every reference type | **Compare the 32 bits the lattice can prove**, i.e. `provableInt`: two live pointers are each provably non-zero but not a particular `Int`, so an ordering question over them is **unprovable → fork**. Task 4. **LANDED**, and it extends to the IDENTICAL-reference case in the other direction: two registers holding one pointer have `cmp == 0`, so all six comparisons are DECIDED there and forking `if-ge`/`if-le` would invent a live branch. Pinned by `core/op/IfOpTest.anUnprovableComparisonOverAPointerForksInsteadOfBeingDecidedByIdentity` and `identicalReferencesDecideEveryComparisonAndNotOnlyEquality` (mutations: 1 dead each), and differentially by `if-lt-two-distinct-references` (`ORACLE_MISJUDGES_FEASIBILITY`, `oracleNeverExplores = {9}`) — the entry a reconciliation toward the oracle cannot pass |
| **`if-eq` on a reference-versus-integral mix** | **BOTH operand orders verify** — `kZero` is integral via `IsBooleanTypes` (`reg_type.h:182-184` → `:186-191` → `:195-200` → `:211-217`), so a literal zero against a null verifies either way (`:3324-3325`). The hard-fail needs a **non-constant** `kInteger` in vA with `kNull` in vB. So this row is a fidelity requirement, NOT "half-verified" — the stronger reading. See D2 | **`Known(null)` equals `Known(0)`**, per [BC]'s *"`(Object) null == (int) 0`"*, in **both** operand orders. Task 4, **LANDED** — and see the NEXT row, because the non-zero half of the same mix is a separate question with the opposite answer |
| **`if-eq`/`if-ne` on a live pointer versus a NON-ZERO integral** | Verifier-rejected: the ref branch needs vB a ref, and the integral branch needs vA integral, so `kInteger`-against-`kNull` is the documented hard fail (`method_verifier.cc:3319-3337`). No fidelity target | **Fork.** A pointer is provably non-**zero**, which decides a comparison against `0` and nothing else — it is not provably different from `5`. The oracle answers definitely (`lhs.raw === rhs.raw`, `opcode/IfOp.kt:65-67`), i.e. it decides an undecidable predicate; spec §2.2 explores more instead. Task 4, **LANDED**. Distinct from the row above: a provable ZERO against a pointer IS decided, which is what keeps `if-nez vRef` definite. Same test as the ordering row, plus the differential entry `if-eq-reference-against-nonzero-int` (`ORACLE_MISJUDGES_FEASIBILITY`, `oracleNeverExplores = {8}`), added in Task 4's fix round after review measured that the shape CAN carry it. **Deliberately the MIRROR of the "A KNOWN REFERENCE as a switch selector" row above:** the same unprovable question — is this live pointer equal to a specific non-zero key? — is answered by FORKING here and by fall-through-only there, because a 2-way `if` fork is cheap and deciding it would lose a feasible path, whereas forking every arm of a large `switch` on a pointer selector is a frontier-width blowup and a heap address never matches its small keys anyway. Neither should be "fixed" to match the other |
| **`if-eq` reference identity: heap id or host identity?** | [BC] gives only the bitwise-representation sentence; nothing says what "the same reference" is for an engine with a tracked heap | **Both, by construction, in one helper**: if either raw is a `HeapRef` then heap-id equality, else host `===`, and **the declared tag is ignored entirely** — matching the oracle at `:57`/`:66` and keeping `testIfNotEqualWithTwoDifferentStringReferencesIsTrue` true. Task 4, **LANDED** as `sameReference` in `core/Provable.kt`, and NOT `Value.agreesWith` (mutation: routing the row through it kills that one migrated test and only it). Callers must gate it on both operands being provably live pointers — it is a pure identity test and cannot tell a pointer from a boxed `Float`; that gate is pinned by `core/op/IfOpTest.aFloatRawIsNotAPointerSoIdentityNeverDecidesIt` |
| **Does `const-class`'s load run `<clinit>`?** | [BC] silent — and *measurably* silent: `initiali*` occurs **zero** times in the whole of [BC]. The oracle's author left a `TODO` asking | **It must NOT** — and this one is barely a choice, because **[ART]'s entire `CONST_CLASS` handler is a resolve, a `nullptr` check and a `SetVRegReference`** (`interpreter_switch_impl-inl.h:755-767`), with [JVMS] §5.5 / [JLS] §12.4.1 excluding `ldc` from their trigger lists as *corroboration*. ⚠️ **The grounds are the whole body, NOT `can_run_clinit = false`**, which is what this row used to say: `NEW_INSTANCE` passes the same `false` and initializes downstream (see [`const-class`](#const-class-21c)). The oracle's initializing two-arg overload is defect 21. Corollary: the resolution is therefore **not** a side effect, so `const-class` declares `NONE`. Task 10 |
| **Does `check-cast`'s register re-tag count as a side effect?** | `SETS_REGISTER` is true in dexlib2 while the runtime writes nothing | **No.** It is a register write, covered by `registersAssigned`; side effects are about state this engine does not own. Stated because silence invites the other answer. All five of Plan 8's families are `NONE` |
| **Fork arity for an unknown selector over a huge table** | `size` is a `ushort`, so up to 65535 arms are representable; frontier breadth is not capped | **Plan 11 Task 43 correction:** address visits are path-local persistent frame state. The 502-path loop-free switch completes under the default 500-visit budget because each path reaches the join once. `SwitchOp` has no arity cap. A maximal table can still allocate all persistent successor wrappers before dequeue-time limits act. |

#### 🆕 What a boxed `Integer(0)` means to a dereferencing op — settled here

**The decision: `Known(0, "Ljava/lang/Integer;")` reads as a null reference.** `readsAsNullReference`'s third
clause (`isDalvikNull`, `core/Value.kt:69-74,142-143`) says so, and it is kept for exactly this shape — an
integral zero under a *reference* type, which the second clause's type test cannot reach. So such a value
**passes** a `check-cast` to `String` and answers **`false`** to an `instance-of`.

**Why that is right and not merely convenient.** ART's `ShadowFrame` clears the reference half of the
register on every primitive write, so `GetVRegReference` returns `nullptr` regardless of the bits — and both
type ops fetch their operand exactly that way (`interpreter_switch_impl-inl.h:829` and `:847`). The declared
tag is an artifact of *this* engine's representation; a device has no tag to consult. Answering from
`declaredAndValueTypeNames` instead is precisely defects D9 and D11.

**And this row is on the STRONG side of the tier split, which is worth stating because the neighbouring row
is not.** The shape settled here is an integral **zero** under a reference tag, so both interpreter tiers
agree: nterp reads the raw `0` and its `testl`/`cbz` takes the null branch, storing `0` for `instance-of`
(`mterp/x86_64ng/object.S:86-107`) and passing the cast (`:1-22`). It is the **non-zero** primitive —
`Known(5, "I")`, not `Known(0, "Ljava/lang/Integer;")` — on which the tiers diverge and the tier drops to
VERIFIER. See
[ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim).

**Migration cost: zero, measured.** `opcode/CheckCastOpTest.objectNotOfCastTypeThrowsClassCastException` is
the corpus's only reference-typed boxed seed and its value is `0x42` — **non-zero** — so it misses this row
in both directions and no migrated expectation moves. Plan 9 implements whatever falls out for `invoke`;
this section is the answer it inherits.

---

## Invocation, class init, fields and monitors (Plan 9)

**What this section is for.** Every expectation in Plan 9's Tasks 14–23 rests on the rules below, so they
are written down once, per family, with a tier — and, for an ORACLE claim, with **both** a source
`file:line` and the `docs/architecture/` section covering it. It is written before any Plan 9 code, which
is the ordering Plan 8 Task 1 established, because an invocation subsystem built first and grounded second
would inherit the old engine's judgement calls by accident rather than by decision.

**One standing rule governs the whole section, and it is the general form of the argument that changed two
of the adjudications below.** Spec §2.2's soundness direction says what to do about something
**unprovable**: unprovable value → `Unknown`, unprovable effect → `STRONG`, unprovable successor set →
explore more. It does **not** license answering `Unknown` for something that is *determinable but not
readable off a single instruction*. **A precision loss is not free just because it is conservative.**
Collapsing to `Unknown` scores clean on a soundness checklist while silently discarding the constant a
deobfuscator exists to recover, and doing that on the exact constructs obfuscators use would leave this
engine **weaker than the one it replaces**. Before writing `Unknown`, ask which of the two cases you have.
Adjudications 1, 3, 5, 8 and 9 below are all that distinction applied to a specific mechanism.

### Invoke resolution, per kind — **SPEC**, and it is more specific than the old engine assumed

[BC]'s `invoke-kind` prose, fetched raw and tag-stripped (2026-08-05, `source.android.com/docs/core/runtime/dalvik-bytecode`),
states each kind's rule. This is **SPEC** tier, and it is decisive:

| Kind | [BC], verbatim | Resolution rule |
|---|---|---|
| `invoke-virtual` | *"used to invoke a normal virtual method, which is a method that isn't `static`, `private` or a constructor"* | Receiver-type-directed: select the implementation for the receiver's runtime class |
| `invoke-super` | *"used to invoke the closest superclass's virtual method (as opposed to the one with the same `method_id` in the calling class)… In Dex files version 037 or later, if the `method_id` refers to an interface method, `invoke-super` is used to invoke the most specific, non-overridden version of that method defined on that interface"* | Start the lookup **above** the calling method's defining class; for an interface `method_id`, the most specific non-overridden default |
| `invoke-direct` | *"used to invoke a non-`static` direct method (that is, an instance method that is by its nature non-overridable, namely either a private instance method or a constructor)"* | Bind statically to the referenced target; never redispatch |
| `invoke-static` | *"used to invoke a static method (which is always considered a direct method)"* | Bind statically; no receiver |
| `invoke-interface` | *"used to invoke an interface method, that is, on an object whose concrete class isn't known, using a `method_id` that refers to an interface"* | Receiver-type-directed, as `invoke-virtual` |

**Constructors and `<clinit>` do not participate in override resolution.** A constructor is *"by its nature
non-overridable"* ([BC], `invoke-direct` row above — **SPEC**), and `<clinit>` is not a member any call
site names: it is scheduled by the runtime, never invoked by an opcode. So an inheritance-walking member
lookup must **exclude** `<init>` and `<clinit>`. The old engine does not:
`b3abe32:docs/architecture/type-and-dex.md:105-106` — *"Constructors and direct methods can appear inherited…
`<clinit>` can appear inherited; class initialization can enqueue an ancestor initializer while leaving the
child uninitialized"* — over `VirtualClass.getMethod`'s unconditional ancestor walk
(`smalivm/src/main/java/org/cf/smalivm/type/VirtualClass.java:111-126`).

**`invoke-super` is new work with no reference behavior.** `grep -rn "invoke-super"` over
`smalivm/src/main/java`, `smalivm/src/test/java` and `smalivm/src/test/resources` returns **zero** hits — no
engine code and no fixture. (The framework smali submodule under `smalivm/src/main/resources/framework/`
does contain the opcode, which is loaded *input*, not engine behavior; the framework policy in the choices
table is what keeps Plan 9 from executing it.) `InvokeOp.build` binds every
kind to the build-time target (`opcode/InvokeOp.kt:304-313`, the resolution at `:311`), so the
closest-superclass rule exists nowhere in the oracle. Architecture:
`b3abe32:docs/architecture/host-execution.md:50` (*"no construction-time resolver specialized for static, direct,
constructor, super, virtual, or interface semantics"*) and `:54` (*"`invoke-super` has no dedicated
superclass resolution"*). The [BC] row above is therefore the **only** source for it — SPEC-derived, with
its own fixture, and no migrated test to check it against.

**One resolution hazard for inheritance fixtures.** `VirtualClass.getImmediateAncestors()` puts the
superclass and every interface into one `HashSet`
(`smalivm/src/main/java/org/cf/smalivm/type/VirtualClass.java:52`), which `getAncestors()` then flattens
into a `LinkedHashSet` (`:70`) — so the oracle's ancestor-walk **order is unspecified** and a differential
fixture over a diamond hierarchy can be unstable on the oracle side. Architecture:
`b3abe32:docs/architecture/host-execution.md:57`, `b3abe32:docs/architecture/type-and-dex.md:107`. Keep inheritance fixtures unambiguous.

### Argument binding does two value transforms

Both are ORACLE-tier readings of `opcode/InvokeOp.kt` that the new core should keep, and both already have
a new-core primitive:

- **An `I`-typed `0` passed where the parameter type is a reference becomes a real `null`** —
  `opcode/InvokeOp.kt:110-113`, gated on *known* + descriptor exactly `I` + numerically zero + reference
  parameter. Architecture: `b3abe32:docs/architecture/host-execution.md:84` (*"A value becomes null only when it is
  known, has primitive descriptor `I`, numerically equals zero, and the analyzed destination parameter is a
  reference type"*). This is Dalvik's null-is-int-0, which the new core already holds as
  `readsAsNullReference` (`core/Value.kt:204-205`) — a reuse, not new machinery.
- **Otherwise a known primitive is re-cast to the declared parameter type** (`opcode/InvokeOp.kt:116`,
  `Utils.castToPrimitive`), because `const/4 v0, 0x0` is indistinguishable between `I`/`S`/`B`/`C`/`Z`.
  Separately `analyzeParameterTypes` (`:69-101`) **narrows** the declared parameter type to the argument's
  actual type when the actual is a subtype, so a `Lchild;` argument under a `Lparent;` parameter keeps
  `Lchild;`.

⚠️ **The corpus's only coverage of the null coax is not portable in Plan 9.**
`opcode/InvokeOpTest.intValueCorrectlyCoaxedToNull` and `intValueCorrectlyNotCoaxedToNull` both call
`Ljava/util/LinkedList;->add`, a reflected class (`resources/safe_classes.cfg:136`), so both defer to
Plan 11. Task 20 must add a Plan-9 fixture over an **app-class** callee or the transform ships untested.

### The callee → caller merge is a two-way partition, not a filter

The old engine takes the **return value** from reached `ReturnOp` addresses only
(`ExecutionGraph.kt:68-73`, used at `opcode/InvokeOp.kt:148-153`) but takes **mutated arguments, static
fields, class-init flags and the constructor's receiver** from *all* reached terminating addresses,
**including `throw`** (`ExecutionGraph.kt:238-246`, used at `opcode/InvokeOp.kt:132-146` and `:155-160`).

**That asymmetry encodes something true and must not be "fixed" away.** If a callee mutates an argument
and *then* throws, and the caller catches, the mutation genuinely is observable in the handler. Its actual
defect is narrower, and `b3abe32:docs/architecture/host-execution.md:82` names it exactly: *"a mixed return/throw
callee can merge exceptional-path argument or constructor state into the caller's **normal
continuation**."* It unions the partitions instead of keeping them apart. Architecture also:
`b3abe32:docs/architecture/opcodes.md:60` (*"exceptional states participate in parameter/constructor consensus"*).

**The rule this engine uses.** The caller's **normal continuation** takes consensus over the callee's
*return* paths. The caller's **exception-handler successor** takes parameter and static-field consensus
over the *throwing* paths that reach **that** handler. Spec §6.5's *"throw states never contaminate them"*
is satisfied — nothing leaks into the return partition — and the handler keeps what it is entitled to.
Strictly better than either the old engine's union or a return-paths-only filter, which would silently drop
the case the old engine got right.

**Task 20 implementation record.** Each callee activation drains before one synthetic outcome fork resumes
the caller. Returning frames merge only with returning frames. Throwing frames merge only with throwing
frames that select the same caller handler. Heap-object presence uses intersection; field, element and static
cells include absent votes as `Top`. Thus a mutation before throw reaches its handler and cannot enter the
normal continuation. The two `CommitTest` mixed-ending guards remain unchanged because no op emits a mixed
`[Return, Throw]` list.

Opaque calls and callee aborts use one rule: `STRONG`, a typed Unknown non-void result, and recursive collapse
of every reachable tracked argument's fields and elements. Refs, aliases, ids, types, array lengths and
constructor identity remain. A cycle guard bounds the traversal. An `<init>` clears only the receiver id, only
after the constructor for that tracked runtime class returns normally.

The Task 13 migration promise was corrected under adjudication 7b(d): **29 of 43 migrate in Plan 9 and 14
remain for Plan 11**, not 31/12. The two additional deferrals are the `[I->clone()` tests. Array clone is a
runtime intrinsic with `MethodLookup.NoImplementation`; Plan 9 neither delegates it to `Object.clone()` nor
fakes its result.

**Task 20 review correction.** Incompatible class-init maps are not a lattice value. INITIALIZED and absent
remain separate caller paths; `ClassInitStore.merge` accepts only one exact lifecycle map. This preserves the
four Task 16 states and prevents a later trigger from rerunning `<clinit>` over paths already initialized.
Callee aborts are collected until all sibling paths drain. An ordinary aborted call then uses opaque fallback.
An aborted class initializer resumes its trigger under the Plan 9 staging rule: STRONG, INITIALIZED, and all
declared statics typed Unknown. Rejected synthetic handlers/resumes count only after construction, so no
phantom path can keep a nested caller pending.

**Residual scheduler correction.** A second ending keeps its `Ending` scope: only `RunPath` is global, while
`ActivationOnly` remains in callee completion. Rejected synthetic successors retain the exact copied and
seeded frame, including result/caught slots and constructor completion. Ordinary callee lost paths become
`CallAbort` votes, so a malformed sibling forces typed opaque fallback instead of leaking a known return.

**Task 20 independent audit.** Migrated constructor tests retain exact array ref, alias and element values.
Opaque argument collapse checks every nonempty root. All ten invoke forms pin kind, target and ordered register
operands. Primitive binding pins every declared primitive raw type and wide slot. Opaque reference results
keep their declared array/class type. Closed-world dispatch evidence is injectable; an incomplete world turns
one loaded candidate into STRONG Unknown while retaining that candidate. Handler resolution selects one
handler for a known type. An unknown type forks every feasible handler plus escape, with deterministic DFS
order. Cross-call throws retain the same `HeapRef`, `GuestThrowable`, and host throwable identity.

**Final reliability correction.** An aborted constructor analyzed through resolved-decision-6 opaque fallback
emits a normal continuation. It therefore applies adjudication 9's constructor completion to the exact
receiver id, matching direct opaque calls. Aliases observe that id; unrelated same-type objects do not change.
Ambiguous dispatch diagnostics store an ordered, unmodifiable copy of candidate names, never a live map view.

### Class initialization — the lifecycle, and the trigger set

**No initialization claim in this file can be SPEC, in either direction.** Re-measured 2026-08-05 against
[BC]'s raw HTML, tags stripped and whitespace flattened: `initiali*` occurs **zero** times in the whole of
[BC] and `clinit` **zero** times. [BC] also never says `NullPointerException` (zero occurrences), which
bounds the invoke-NPE claim below the same way. Same instrument and same result as the 2026-08-03
measurement recorded in [the tier ranking](#the-tiers-are-not-equally-strong--the-ranking-for-a-claim-about-what-the-runtime-does).

| Opcode | Does it initialize? | Tier |
|---|---|---|
| `const-class` | **No** | **`[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** and `[DEVICE](Tier B, nterp default, API 36)` — `Fx_ConstClassDoesNotInitialize` in `tools/art-runner/golden/tier-a-batch.txt` and `tools/emulator/golden/tier-b-batch.txt`; + `[ART]` (whole handler body, `interpreter_switch_impl-inl.h:755-767`) |
| `check-cast`, `instance-of` | **No** | `[ART]` — whole handler bodies read in full, `runtime/interpreter/interpreter_switch_impl-inl.h:819-835` and `:837-850`; [JVMS] §5.5 / [JLS] §12.4.1 exclude `checkcast`/`instanceof` as corroboration only |
| `new-array`, `filled-new-array` | **No** | `[ART]`; ORACLE agrees (`b3abe32:docs/architecture/state-model.md:158`: *"`new-array` does not request initialization"*) |
| `new-instance` | **Yes** | **`[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** cross-validated **`[DEVICE](Tier B, nterp default, API 36)`** — `Fx_NewInstanceDoesInitialize` (`CLINIT_RAN Probe_ClinitWitnessC`) in `tools/art-runner/golden/tier-a-batch.txt`; + `[ART]` (`AllocObjectFromCode` → `EnsureInitialized`). **Measured by Plan 9 Task 16 Step 0**; see the note below for what the measurement settled and why it was owed |
| `sget*` | **Yes** | **`[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`** and `[DEVICE](Tier B, nterp default, API 36)` — `Fx_StaticFieldDoesInitialize` (`CLINIT_RAN Probe_ClinitWitnessB`) in `tools/art-runner/golden/tier-a-batch.txt` and `tools/emulator/golden/tier-b-batch.txt`. **This is the positive CONTROL for the `const-class` row above** — the pair was measured in one run precisely so the negative answer could not be a classpath artifact, which is why the two rows must never drift apart |
| `sput*` | **Yes** | **INHERITED** ([JVMS] §5.5, via `putstatic`); ORACLE agrees (`b3abe32:docs/architecture/execution-engine.md:151-153`, `b3abe32:docs/architecture/state-model.md:158`). No tier-1/2/3 companion is recorded, so this remains a JVM prior rather than a device fact. |
| `invoke-static` | **Yes: initialize the resolved method's declaring class before the call** | **[ART]**, read at AOSP ART `6484611fd45e69db9f33f98bfd6864014b030ecf`: `ArtMethod::NeedsClinitCheckBeforeCall` in `runtime/art_method.h` returns true only for a static method that is not a class initializer; `PerformCall` in `runtime/common_dex_operations.h` calls `EnsureInitialized` before either interpreter or compiled execution; and that helper passes `callee_frame->GetMethod()->GetDeclaringClass()` to `ClassLinker::EnsureInitialized`. This also pins inherited static resolution to the actual declaring class, not the symbolic owner. **INHERITED** corroboration: [JVMS] §5.5 names `invokestatic` and says a referenced static method initializes the class or interface that declared it. No DEVICE fixture isolates this family, so this row claims the ART trigger family, not exact cross-version timing. |
| `invoke-virtual`, `invoke-interface`, `invoke-super`, non-constructor `invoke-direct` | **No invocation class-init trigger** | **[ART]**, same revision and path: `NeedsClinitCheckBeforeCall` excludes every non-static method. `PerformCall` still enters `EnsureInitialized`, but `StillNeedsClinitCheck` makes it a no-op for these methods. **INHERITED** corroboration: [JVMS] §5.5 does not list `invokevirtual`, `invokeinterface`, or `invokespecial` as initialization triggers. This does not prevent earlier active use from initializing the class. |
| constructor `invoke-direct <init>` | **No invocation class-init trigger** | **[ART]**, same `NeedsClinitCheckBeforeCall` predicate: an instance constructor is non-static. In normal bytecode, `new-instance` already performs allocation-time initialization, as measured above. The constructor call must not schedule a second initializer. |
| explicit `<clinit>` invocation | **No recursive invocation trigger** | **[ART]**, same predicate: the class initializer is static but explicitly excluded. Such a call is hostile input rather than a normal source-level active use; this row only fixes the engine's total behavior for it. |

**The old engine used a broader rule, and that rule is not runtime evidence.** It initialized the resolved
declaring class at every non-`<clinit>` callee entry (`b3abe32:docs/architecture/execution-engine.md:151-153`,
`VirtualMachine.kt:189-209`). That ORACLE rule includes virtual, interface, super, and direct instance calls.
ART's method predicate excludes those calls. Plan 11 therefore keeps static invocation, static-field, and
allocation triggers, but removes the old engine's instance-entry trigger. The ART source above establishes
the trigger family only. It does not establish exact ART fidelity for concurrency, transaction mode, or
`Class.forName`; those remain separate work. Initialization failure is tiered in the lifecycle section below.

> ✅ **`new-instance` initializes — MEASURED (Plan 9 Task 16 Step 0), and the measurement CONFIRMED the
> source reading rather than overturning it.** A bare `new-instance LProbe_ClinitWitnessC;` with no
> `invoke-direct <init>` printed `CLINIT_RAN Probe_ClinitWitnessC` on **both** tiers, byte-identically:
> `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` and
> `[DEVICE](Tier B, nterp default, API 36)`. So the trigger list Task 16 Step 2 implements is unchanged.
>
> **Why it was owed at all, recorded because the lesson outlives the answer.** An earlier draft of Plan 9
> asserted this as `[DEVICE]` three times on the strength of an `[ART]` source reading —
> `NEW_INSTANCE` (`runtime/interpreter/interpreter_switch_impl-inl.h:862-894`) passes
> `can_run_clinit = false` at `:868` and still initializes, downstream in
> `AllocObjectFromCode(c, Self(), allocator_type)` at `:882` → `CheckObjectAlloc` →
> `CheckClassInitializedForObjectAlloc` → `ClassLinker::EnsureInitialized`, asserted by the handler at
> `:891` — while **no `Fx_NewInstance*` fixture existed** in either golden set. This is the identical opcode
> the [`can_run_clinit` non-sequitur](#const-class-21c) passage was written about, so it was the last claim
> in this file that should have been over-tiered. A confirmed guess is still a guess until it is run.
>
> **The measurement's own two controls, neither optional.** A witness's SILENCE and a witness that was never
> triggered look identical, and a bare allocation cannot prove its own opcode ran, because the verifier
> forbids reading an uninitialized reference (contrast `Fx_ConstClassDoesNotInitialize`, which can call
> `getName()` on its `Class`). So: `Fx_StaticFieldDoesInitialize` proves the witness mechanism fires on the
> runtime, and `Fx_NewInstanceAbsentType` proves the opcode executes and resolves its operand here —
> `THREW java.lang.NoClassDefFoundError: Failed resolution of: Lcom/example/DefinitelyMissing;` on both
> tiers, which also fills in `new-instance`'s row of
> [the resolution-failure family](#resolution-failure-is-one-family-tiered-once) for Task 17.
>
> **And the `<init>` call is deliberately absent from the fixture.** This isolates `new-instance` from the
> constructor call. ART's call predicate now independently records that an instance constructor adds no
> invocation trigger; the allocation is the active use that owns initialization.

**The lifecycle is a four-state machine and `Failed` is terminal.**
`Uninitialized → Initializing → {Initialized | Failed}`. This engine's requirement is spec §7 — but **spec
§7 is this project's own design document and is not an evidence tier**, so the two substantive claims are
tiered separately, both **INHERITED** from [JLS] §12.4.2's initialization procedure:

- **`Initializing` is the recursion guard** — [JLS] 12.4.2 **step 3**: a request for initialization while the
  same thread is already initializing that class is a *recursive* request, which completes immediately. A
  `<clinit>` that directly or transitively re-triggers its own class therefore sees `Initializing`, not
  `Uninitialized`, and does not re-enter.
- **`Failed` is terminal** — [JLS] 12.4.2 **step 12**: the class is marked *erroneous*, and every subsequent
  initialization attempt throws rather than re-running `<clinit>` (step 5).

⚠️ **INHERITED is a tier-4 citation and the bridge to Dalvik is not free here.** `b3abe32:docs/architecture/execution-engine.md:169`
says it directly: [JLS] 12.4 / [JVMS] 5.5 specify the uninitialized/initializing/initialized/erroneous states
and the abrupt-initialization behavior, but *"the repository has no recorded ART/Dalvik comparison
establishing that every JVM detail is the intended target."* [BC] cannot help — `initiali*` occurs zero times
in it. So the **states** and the **no-re-run invariant** are inherited. The failure identity is not: it is
**[ART]**, read at AOSP ART `6484611fd45e69db9f33f98bfd6864014b030ecf` in `runtime/class_linker.cc`:

- **First failure.** `WrapExceptionInInitializer` keeps an escaping `Error` as itself and wraps any other
  throwable in a fresh `ExceptionInInitializerError`; `mirror::Class::SetStatus` (`runtime/mirror/class.cc`)
  stores that pending exception as the class's erroneous-state error.
- **Later trigger.** `InitializeClass` finds the class erroneous and calls `ThrowEarlierClassFailure`, which
  throws a fresh `NoClassDefFoundError` whose message is `PrettyDescriptor()` (the binary name, `foo.Bar`) and
  whose cause is the stored error.
- **Failed superclass.** `InitializeClass` initializes the superclass first; when that fails, the subclass is
  marked erroneous with the exception the superclass raised, so a subclass trigger throws the failed
  superclass's `NoClassDefFoundError` (named after the superclass), which every waiting subclass then stores.

`ClassInitFailureBehaviorTest` asserts the message for every trigger kind and for a failed superclass.

**A static initializer with no code is malformed input**, also **[ART]**: `DexFileVerifier::CheckMethodAccessFlags`
(`libdexfile/dex/dex_file_verifier.cc`, same revision) rejects a constructor marked `native` or `abstract` for every
dex version that supports default methods, so no device initializes such a class. A trigger that reaches one
ends its path as `MALFORMED_INPUT` (`ClassInitHostileInputTest`).

The enum has exactly these four states (`core/StateContracts.kt`) and, since Plan 9 Task 16, an implementor:
`ClassInitStore` in `core/ClassInit.kt` holds the state **per path** (resolved decision 9, over
`b3abe32:docs/architecture/state-model.md:160`'s existential merge leak) and enforces the transition table, so both terminal states are
terminal by construction rather than by convention. `ExecutionState` now extends `ClassState`, so a consumer
reads the four-state answer and not only the `isClassInitialized` boolean — and that boolean is deliberately
`false` for `Initializing` as well as for `Failed`.

What this replaces in the oracle is documented in two places, both ORACLE tier:
`b3abe32:docs/architecture/execution-engine.md:155` — *"There is no explicit stored `INITIALIZING` or `ERRONEOUS`
status… active initialization is inferred by walking the current caller chain"* — and
`b3abe32:docs/architecture/state-model.md:162`, *"`mergeClassStates()` marks a called `<clinit>` initialized before checking
reflection or `graph.aborted`. A failed initializer can therefore appear successfully initialized."*

**Superclass before subclass, each class once.** The old engine's own code shows the intent and a LIFO
deque reverses it — see adjudication 4. Full JLS 12.4 ordering stays a non-goal (spec §7: *"enough to keep
static-field reconciliation sound"*).

Task 16 makes the ordering **unarrangeable** rather than arranged: a `ClassInitPlan` carries at most **one**
guest initializer — the topmost class in the superclass chain that still needs one — and the trigger simply
asks again when the nested `<clinit>` returns. A plan carrying a *list* would have had to be consumed in some
order by the scheduler, which is precisely the choice the oracle got wrong. **Interfaces are still not
walked**, which is a narrower divergence than the oracle's: [JLS] §12.4.1 requires a superinterface's
initialization only for an interface declaring a default method, while the oracle omits interfaces *and*
abstract ancestors — and abstract ancestors are ordinary links in a superclass chain, so those are fixed here.

### Resolution failure — one family, already settled; cite it, do not re-derive it

**Settled in full at
[Resolution failure is one family, tiered once](#resolution-failure-is-one-family-tiered-once)** — the five
resolving opcodes, their fixture names, both golden paths and the resolution-before-null-check ordering all
live there and are not restated here. Two things travel with *this* section because Plan 9's tasks need them
at hand:

**The tier:** `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated at
`[DEVICE](Tier B, nterp default, API 36)`. Guest `java.lang.NoClassDefFoundError` — a
`LinkageError`/**`Error`**, not an `Exception`.

**The consequence, which is the whole point:** a guest `catch (Exception)` — which obfuscator-generated
wrappers are full of — does **not** catch an `Error`, so the two answers put a handler on opposite sides of
reachable-versus-dead. That is what makes this a deobfuscation-fidelity question rather than a naming quibble.

> ✅ **`sget` and `invoke` on an absent class are NOW MEASURED — Task 15 took the two fixtures** (2026-08-05).
> The caveat this blockquote used to carry was right: only the five resolving opcodes had fixtures, and Plan
> 9's audit note read `sget`/`sput`/`invoke` as if they were covered when the claim was INHERITED via [JVMS]
> §5.4.3.2 / §5.4.3.3. `Fx_SGetAbsentType` and `Fx_InvokeAbsentType` (each with a present-type control) close
> it: both raise guest `java.lang.NoClassDefFoundError` on Tier A `-Xint` and on Tier B's `nterp` default, so
> `sget`/`sput`/`invoke` now carry a measurement of their own rather than the siblings'. **The measurement
> agreed with the extrapolation**, which is the outcome to record explicitly — the tier rule earns its keep by
> being applied before the answer is known, not only when it overturns something.
>
> `sput` itself still has no fixture and rides on `sget`'s: they resolve the same symbolic field reference
> through the same §5.4.3.2 path, and no plausible mechanism distinguishes a read from a write *at
> resolution*. Recorded as the one remaining inch of extrapolation in this family rather than glossed.

### Instance and static fields — the register-read-as-reference rule

**Every field op reads its receiver register *as a reference*, so `readsAsNullReference` is the predicate, not
an `isNull` test.** The rule, the ART evidence and the instruction *"write it that way for the field and
monitor ops Plan 9 inherits"* are already at
[`GetVRegReference` is the whole of it](#getvregreference-is-the-whole-of-it-and-reading-throw-without-following-it-is-how-this-section-got-the-primitive-case-wrong-twice)
(`:1204-1214`), which enumerates the shared-fetch list including *"every field op"* and
`MONITOR_ENTER`/`MONITOR_EXIT`. Do not re-derive it — call the predicate. One reminder worth its line: Plan 7
shipped four ops with the weaker `isNull` test first and each **dropped a definite NPE** on `Known(5, "I")`.

⚠️ **What this section adds is the tier split, because it is a citation trap rather than a behavior one, and
it is the half `:1204-1214` does not tabulate.** The predicate has two rows and they do not share a tier:

| Row | Tier |
|---|---|
| a **zero** raw under any tag reads as null | `[ART]`, corroborated by **both** interpreter tiers |
| a **non-zero** primitive (`Known(5, "I")`) reads as null | **VERIFIER** — our spec §2.2 choice, *not* an `[ART]` fact. `nterp`, the default on a modern device, reads the raw dex register and dereferences the `5` |

Read [ART has TWO interpreter tiers](#art-has-two-interpreter-tiers-and-they-read-a-register-differently--name-the-tier-in-every-register-read-claim)
before putting `[ART]` on a field or monitor op's null row — those handlers are `GET_VREG`-based in nterp
too. The predicate and the behavior are unchanged; only the citation is.

**A boxed `Integer(0)` reads as a null reference — settled, not re-decided here.** See
[What a boxed `Integer(0)` means to a dereferencing op](#-what-a-boxed-integer0-means-to-a-dereferencing-op--settled-here).
Task 20 confirmed that the trigger does not exist in Plan 9. `VirtualBackend` returns core `Value`s from guest
execution and never parks a host-autoboxed `Number`, so adding a `provableInt` guard now would be dead,
speculative code. Both the trigger and its predicate guard belong to `ReflectBackend` in Plan 11. The meaning
remains settled here; only the reachable owner moved.

**Field identity is a fully-qualified descriptor, and the oracle's is not.** The new core's contracts
already require *"a FULLY-QUALIFIED field descriptor that INCLUDES the defining class… so that a shadowed
field never collapses into one state cell"* (`core/StateContracts.kt:24-28` for instance fields, `:46-50`
for statics). The oracle keys declared field maps **by name only**, sharing one map between static and
instance fields, and resolves *"only owner and name, without checking type, staticness, or access"* —
`b3abe32:docs/architecture/type-and-dex.md:116` over
`smalivm/src/main/java/org/cf/smalivm/type/VirtualClass.java:204-216`.

**The old engine has no instance-field state at all**, which is why Task 19 is a new capability rather than
a two-test migration — see adjudication 6.

**Task 19 now implements instance fields on tracked heap objects.** `new-instance` seeds every declared
instance field in the object's superclass chain with [JLS] §12.5's declared-type default. `iget*` and
`iput*` resolve by referenced owner + name + type, walk only the superclass chain, and key the cell with
the class that declares it. Thus an inherited reference and a direct parent reference share one cell, while
shadowed declarations remain separate. Resolution precedes the receiver null check: absent class → guest
`NoClassDefFoundError`; present class with no matching member → guest `NoSuchFieldError` (INHERITED,
[JVMS] §5.4.3.2). Neither op triggers class initialization.

Both receiver reads use `readsAsNullReference`. A definite null has only the guest NPE outcome. An unknown
receiver preserves normal and NPE outcomes without choosing a heap target. `iget` assigns a typed `Unknown`
on the normal unknown-receiver path and records no effect. `iput` assigns no register. It records `WEAK` only
after a successful tracked-cell write. An unknown receiver, known non-`HeapRef`, or tracked object without
the resolved cell has an unmodelled target and records `STRONG`. These malformed rows are **VERIFIER-tier
choices under spec 2.2**, not runtime claims; they continue without inventing a guest exception or field
cell. `iput-object` applies the same reference coercion as `sput-object`.

**Narrow stores:** `iput`/`sput` `-boolean`/`-byte`/`-char`/`-short` keep only the field's width, as ART's
`GetFieldValue` casts the 32-bit register (`static_cast<uint8_t>`, `<int8_t>`, `<uint16_t>`, `<int16_t>`), so
`sput-byte` of `0x100` stores `0`. A boolean byte other than 0 or 1 stores `Unknown(Z)`: a device keeps it, but
the engine's `Boolean` raw cannot hold it (`Coerce.narrowStore`).

### Monitors

`monitor-enter`/`monitor-exit` are **`WEAK`-effect no-ops** (spec §3 makes real monitors a non-goal). Two
things still apply and neither is a monitor feature: the receiver is read **as a reference**
(`readsAsNullReference` — both handlers are in the shared-fetch list linked above), and `monitor-exit`'s pc
semantics are recorded at
[Also worth having: `monitor-exit`'s pc semantics](#also-worth-having-monitor-exits-pc-semantics).

### Choices this plan makes — recorded as choices, not as facts about Dalvik

Every row here is a VERIFIER-tier or genuinely-unspecified question. None of them is *"Dalvik does X."*

| Question | The choice, and why |
|---|---|
| **Dispatch when the receiver's value is unknown** | **Three cases, keyed on whether the TARGET is provable — not on whether the receiver's value is known.** (1) Receiver known → dispatch to that object's tracked type's implementation; one target, no fork. (2) Receiver unknown but **exactly ONE** implementation of that method exists in the loaded set → **dispatch to it.** (3) Receiver unknown, **two or more** implementations → `Unknown` + `STRONG`, **no fork**, with the **candidate set recorded on the node**. `ClassIndex` owns the count-the-implementations query. Case 3 is the one place §2.2's "explore more" is deliberately not applied, and only there: the candidate targets are *deliberately* divergent (that is why the overrides exist), so enumerate-and-fork buys N disagreeing states that rejoin at `Unknown` — frontier-width cost for no precision gain. Recording the candidates makes bounded forking a measurable decision later instead of a guess. **⚠️ Case 2's assumption, named rather than denied — see the note below the table** |
| **A known-NULL receiver on any non-static invoke** | **Guest `NullPointerException`, before the body runs.** Read through `readsAsNullReference`. Not SPEC — [BC] never says `NullPointerException` (zero occurrences, measured above) — so this is INHERITED from [JVMS]'s `invokevirtual`/`invokeinterface`/`invokespecial` null clause plus spec §2.2: a definite throw is strictly more informative than executing a body with a null `this`, which no real path can do. **The oracle produces no such NPE** (adjudication 2), so this is an intended divergence, not a port |
| **`invoke-virtual` naming a `private` method, or `<init>`** | **Resolve and run it anyway.** [BC] is explicit that this input is malformed — `invoke-virtual` is *"a method that isn't `static`, `private` or a constructor"*, `invoke-direct` is *"a private instance method or a constructor"* — so this is **SPEC-invalid**, one tier stronger than merely verifier-invalid. **"Kind-correct resolution" therefore means the per-kind LOOKUP rule (static/direct bind statically, virtual/interface redispatch, super starts above the caller), NOT a legality or access check.** Access checking is a separate hole (`b3abe32:docs/architecture/type-and-dex.md:108`: *"no dedicated algorithms or access checks"*) and stays out of scope. Load-bearing for the corpus: `opcode/InvokeOpTest`'s `Virtual` group invokes a `private` method four times (`invoke_virtual_test.smali` `:16`/`:24`/`:32`/`:82` against the `private` declarations at `:96`/`:102`/`:110`/`:118`), and `hash_code.smali:23` uses `invoke-virtual` on `<init>`. Refusing them would fail five migrated tests for input no device would link |
| **What a partially-initialized class reads back as, mid-`<clinit>`** | **Its literal-seeded value if it has one, otherwise `Unknown` — and the class reads `Initializing`, never `Initialized`.** The seeding is available with no execution (adjudication 5), and `Initializing` is what makes the recursion guard observable instead of inferred. This is the row `b3abe32:docs/architecture/execution-engine.md:155` says the oracle cannot express at all |
| **Class-init state's scope across explored paths** | **Per path**, carried with the `Frame`'s other per-path state; any cross-path join is an explicit, tested decision. Marking a class initialized because only one terminal callee path initialized it can skip required initialization on another path. Lifecycle states, address-visit counts, node piles, and heap identity must retain their appropriate path or activation scope. |
| **Framework / non-application classes** | **Conservative: mark `Initialized`, statics `Unknown` unless a literal is encoded, effect `STRONG`, `<clinit>` never run** — mirroring the shape of the oracle's REFLECT short-circuit without needing reflection. Not hypothetical: `resources/framework_classes.cfg` is **21,088** ordinary loaded classes, `opcode/NewInstanceOpTest.canCreateNonLocalInstance` does `new-instance Ljava/lang/Integer;`, `new-instance` triggers init, and framework `Integer.smali` has a real `<clinit>` (`resources/framework/smali/android-25/java/lang/Integer.smali:69`). **Two carve-outs this must NOT swallow.** (a) `new-instance` of a framework class still yields a real uninitialized `HeapRef` of that type — only the `<clinit>` is suppressed; a framework `new-instance` answering `Unknown` would break `opcode/InvokeOpTest.knownMutableParametersAreMutatedWithDeterministicExecution`, whose callee does `new-instance p0, Ljava/lang/StringBuilder;` (`invoke_static_test.smali:284`). (b) `static final` literal initializers are encoded in the class definition, so a framework class's literal statics can be seeded straight from its smali with no execution and no reflection — take that, because conservatism that costs precision for no soundness gain is not conservative, it is just lossy. The oracle buys more precision here (it reads framework statics by host reflection, `ExecutionState.kt:506-521`, so it gets `Integer.MAX_VALUE`) at the price of real unsoundness (the host JDK is not the Android runtime); recovering it properly is Plan 11's, alongside the backends |
| **What clears a heap object's uninitialized flag** | **The `Invoker` clears it when an `<init>` call whose receiver is that `HeapRef` returns.** The oracle does it inside the **emulated** `Ljava/lang/Object;-><init>()V` (`emulate/MethodEmulator.kt:26`, `emulate/java_lang_Object_init.kt:9-21`), which materializes a host object via `ObjectInstantiator.newInstance`. Plan 9 has no `EmulateBackend`, and the framework's `Object.<init>()V` is a bare `return-void` (`resources/framework/smali/android-25/java/lang/Object.smali:19-25`), so running it virtually does nothing — Plan 9 must own the rule. **And do NOT port the oracle's aliasing mechanism** (adjudication 8) |
| **Call-depth bound** | **`maxCallDepth`, default 20**, wired to the already-reserved `AbortReason.CALL_DEPTH` (`core/Outcome.kt:17`, referenced by **no production code** — its only two references are assertions in `core/CompletionStatusTest.kt`) in the same task as the `CallStack`. `Limits` carries only `maxAddressVisits`/`maxSteps`/`maxExecutionTimeMillis` today (`core/ExecutionSession.kt:18-22`). The default matches the oracle's `DEFAULT_MAX_CALL_DEPTH = 20` (`VirtualMachine.kt:54`, checked at each callee entrypoint at `:180-187`; corroborated by `b3abe32:docs/architecture/execution-engine.md:171-180`, whose table row reads *"Checked only at entry. Exceeding the limit clears the current frame"*). **The oracle has no recursion detection beyond depth** — `grep -nw` for `recursion`/`recursive` over `VirtualMachine.kt` and `opcode/*.kt` returns nothing. Plan 9 lands the first nested call, so it lands the first unbounded self-recursive guest method; without the bound a recursive fixture hangs the suite instead of reporting `Partial` |
| **An argument's mutability, for the un-analyzable-call collapse** | **`HeapRef` decides it: an argument whose `Value.Known.raw` is a `HeapRef` may have been mutated; anything else may not.** The oracle answers from an allow-list of **2498 entries** (`resources/immutable_classes.cfg` — 2512 raw lines, 2498 once blanks and `//` comments are excluded; read through `configuration/Configuration.kt:17-23`); no equivalent exists under `core/` and none should be added. The new core already answers the question structurally — a mutable VM-tracked object **is** a `HeapRef`, and a non-`HeapRef` raw is by contract a value type (`core/Value.kt:216-221`, `core/StateContracts.kt:20`). More accurate than the list, which calls every array and every `Ljava/lang/Object;` mutable regardless of content. Keep the null carve-out (a null argument is skipped). **The `<init>`-receiver carve-out does NOT survive — see adjudication 9** |
| **`move-result` (non-object) after a reference-returning call** | **Read it as the same value; do not refuse.** Verifier-invalid, and in the corpus: `invoke_static_test.smali:119` does `move-result v0` after `getString()Ljava/lang/String;`, which `opcode/InvokeOpTest.possiblyUninitializedClassHasUnknownFieldValues` executes. Refusing it would abort a path for a reason unrelated to the opcode under test |

#### ⚠️ Dispatch case 2 rests on a CLOSED-WORLD assumption over the loaded set — name it, do not deny it

An earlier revision of the row above said case 2 involves *"no unsoundness,"* and that was wrong in the way
this file most cares about: **"exactly one implementation in the loaded set" is a fact about what the analysis
happened to load, not a fact about the program.** The same section's adjudication 7 says the input is *"an APK
that routinely references classes it does not contain"* — so the two statements could not both stand.

**What is actually being chosen:** case 2 is sound **under a closed-world assumption over the loaded class
set**, and that assumption is the choice. It is worth making, because the whole value of case 2 is the
constant a single implementation returns and that constant-propagation then recovers — collapsing to
`Unknown` because a *hypothetical* second implementation might exist elsewhere is the precision loss the
standing rule at the top of this section forbids. But the assumption is real: a subclass loaded at runtime,
or one this analysis never saw, makes the dispatched body the wrong one.

**The degradation rule, so Task 20 builds to it and Plan 11 can wire the trigger:**

> **Where the class set is known-incomplete, case 2 degrades to case 3** (`Unknown` + `STRONG`, candidates
> recorded). Known-incomplete means at least: a `DexClassLoader`/`PathClassLoader` construction reachable in
> the analyzed set; a reflective-load site (`Class.forName`, `ClassLoader.loadClass`); a **missing referenced
> class on the dispatch path** — the adjudication-7 shape, which is the cheapest of the four to detect
> because `ClassIndex.isLoadable` already answers it; or multidex (spec §3 non-goal) leaving dexes unloaded.

**Plan 9 does not implement the detection** — it has no reflection surface and multidex is a non-goal. It
records the rule, so that the case-2 arm is written with the degradation hook rather than retrofitted, and so
the assumption is discoverable by a reader instead of being buried in an arm that claims to have none.

### Oracle reconciliation (Plan 9) — ten adjudications

Same precedence as the Plan 8 subsection above, restated because every judgement turns on it: **ORACLE is
not authority about Dalvik.** It is evidence that a behavior survived years of real malware, and it is what
the differential compares against. But **the old engine is right about most of this subsystem, including
some genuinely hard parts**, so "it differs from the spec" is not by itself a finding and "the spec says so"
is not by itself a reason. Each row below asks *what was it trying to do, did it succeed, and what should
we do now* — and lands in one of four categories, which determines the action:

- **BUG** — no upside, nothing intended. Fix it; the fix usually *gains* precision.
- **KNOWINGLY DEFERRED** — the source documents the hole in its own comments. Implement it if we now have
  the machinery it lacked.
- **RIGHT IDEA, WRONG MECHANISM** — a real requirement implemented with the only tool then available. Keep
  the requirement; replace the mechanism.
- **AMBITIOUS AND USUALLY RIGHT** — a risk that pays off most of the time. **Do not "fix" this by becoming
  conservative.** Find the version that keeps the win and drops the risk.

The table retains the per-behavior source evidence and the resulting semantic decisions.

| # | Behavior | Oracle, with `file:line` | `docs/architecture/` | Category → what we do |
|---:|---|---|---|---|
| 1 | **Unknown receiver** | `resolveTargetMethod` returns the declared `method` unchanged for a null/`UnknownValue` receiver (`opcode/InvokeOp.kt:219-221`) and then really runs that body | `b3abe32:docs/architecture/host-execution.md:63-67`, `b3abe32:docs/architecture/opcodes.md:60` | **AMBITIOUS AND USUALLY RIGHT.** In real code the declared method is very often the only implementation, so the oracle recovers a real return value, real field effects and real reachability exactly where `Unknown` would discard all of it. It goes wrong only when the method *is* overridden and the receiver is a subclass. **Its defect is that it cannot tell the provable case from the guess — not that it tries.** → the three-case rule in the choices table. Both of spec §5.3's options are wrong as stated: conservative `Unknown` throws away the majority case, forking pays state explosion on the minority one |
| 2 | **Null receiver** | Same line: a *known null* receiver enters the body instead of throwing | `b3abe32:docs/architecture/host-execution.md:63-67` (*"Known null can enter a local method body rather than throw"*), `b3abe32:docs/architecture/opcodes.md:60` (*"all non-static forms omit an initial null-receiver outcome"*) | **BUG.** No upside; running a body with a null `this` produces effects no real path can have. → guest NPE via `readsAsNullReference`. **Corpus consequence the plan had not seen:** the oracle's NPE in `alwaysThrownExceptionIsBubbledUp` / `sometimesThrownExceptionExecutesExceptionalAndNormalExecutionPaths` comes from **host reflection** on `Ljava/lang/String;->length()` with a null receiver (`invoke_static_test.smali:312`, `:331`), not from an invoke null check — so this adjudication makes both tests Plan-9-portable that would otherwise have needed `ReflectBackend` |
| 3 | **Callee → caller merge** | Return value from `ReturnOp` addresses only (`ExecutionGraph.kt:68-73`); arguments, statics, class-init flags **and the constructor receiver** from all terminating addresses including throws (`:238-246`, `opcode/InvokeOp.kt:132-146`, `:155-160`) | `b3abe32:docs/architecture/host-execution.md:82`, `b3abe32:docs/architecture/opcodes.md:60` | **RIGHT IDEA, WRONG MECHANISM — and this plan's first answer was wrong too.** The asymmetry encodes something true (a mutation before a throw *is* visible in the handler); the defect is the union, not the consultation. Neither "all paths" nor "return paths only" → the **two-way partition** above. A handler-observes-the-mutation fixture is new: **neither** engine has one |
| 4 | **Class-init order** | Subclass `<clinit>` runs before its superclass's: `enqueueClassInitializationIfNecessary` recurses over ancestors **first** (`VirtualMachine.kt:457-491`, own `<clinit>` enqueued last at `:488-489`) and a LIFO deque reverses it (`add` is `addLast`; `step()` takes `peekLast()`, `:145`). Two adjacent defects in the same loop (`:469-477`): interfaces and abstract ancestors are **never** initialized, and a diamond ancestor's `<clinit>` can run more than once | `b3abe32:docs/architecture/state-model.md:158`, `b3abe32:docs/architecture/type-and-dex.md:110`, `b3abe32:docs/architecture/execution-engine.md:151-153` | **BUG, and the code shows the intent** — recursing ancestors first is plainly right; the deque is an accident. It matters for the goal: when a superclass `<clinit>` sets a static a subclass `<clinit>` reads, wrong order yields a wrong constant. → superclass before subclass, each class once |
| 5 | **Static literal initializers** | `initializeClassState` (`VirtualMachine.kt:549-555`) is called from exactly two places — the entry method's own class (`:524`) and the **no-`<clinit>`** branch (`:479-487`) — so a class *with* a `<clinit>` never gets its `.field x:I = 0x4` literals seeded and reads any un-`sput` static as `Value.unknown` with an *"undefined field"* log (`ExecutionState.kt:258-261`) | `b3abe32:docs/architecture/execution-engine.md:157`, `b3abe32:docs/architecture/state-model.md:158` | **BUG that costs known constants.** The implicit assumption — *"the `<clinit>` will assign them"* — is false: dex encodes static initial values in the class definition, and for `static final` constants the compiler emits **no** `<clinit>` assignment at all. → seed literals for every class. Expect several of the 27 `opcode/SGetOpTest` expectations to move from `Unknown` to a known value; that is the improvement showing up |
| 6 | **Instance fields** | `iget` always returns `Value.unknown(fieldType)` and ignores the receiver it reads (`opcode/IGetOp.kt:25-31`): no class init, no null check, no NPE, no `ClassManager` resolution. `iput` **writes nothing** — it re-assigns the receiver register to itself and records `WEAK` (`opcode/IPutOp.kt:25-34`) | `b3abe32:docs/architecture/opcodes.md:57` (*"Placeholder unknown/no-op \| No heap or null-receiver semantics"*), `b3abe32:docs/architecture/type-and-dex.md:118` | **KNOWINGLY DEFERRED**, and the source says so out loud at `IPutOp.kt:28-30`: *"Need to spend a lot more time thinking about how to maintain instance state correctly. It's non-trivial to do and maintain correctness."* What was missing was id-tracked object state; Plan 6 built it (`HeapStore`). → implement the real thing. **Read the resulting divergence count as success:** every differential fixture touching an instance field will diverge, because the oracle has no behavior there to agree with. A *missing* divergence entry is the signal to worry about |
| 7 | **Missing class** | **Three** different behaviors, not one family. (a) Host `RuntimeException` at op-build time for `sget`/`sput`/`invoke`/`check-cast`/`instance-of`/`new-instance` — `type/ClassManager.kt:191-198`, message *"Can't find Smali file for …"* at `:195`. (b) Guest `ClassNotFoundException` (a checked **`Exception`**) for `const-class` (`opcode/ConstOp.kt:68-81`) and `new-array` (`opcode/NewArrayOp.kt:38-49`). (c) **Nothing** for `filled-new-array`/`iget`/`iput` | `b3abe32:docs/architecture/opcodes.md:56` (*"Linkage errors … are not represented"*) | **BUG in three directions, plus an implementation artifact.** The host crash kills the whole run — fatal for a tool whose input is an APK that routinely references classes it does not contain. `ClassNotFoundException` is simply what a host `ClassLoader` raises: an artifact of resolving through real reflection, not a statement about Dalvik. → one family, one answer: guest `NoClassDefFoundError`. Two migrated tests pin the crash and are red-on-arrival: `opcode/SGetOpTest.staticUnknownClassFieldThrowsException` and `opcode/InvokeOpTest.invokeNonExistentMethodThrowsException` |
| 7b | **Missing *method* on a *present* class** | `InvokeOp.build` throws a host `RuntimeException("Method doesn't exist: …")` at op-build time (`opcode/InvokeOp.kt:311`) | `b3abe32:docs/architecture/type-and-dex.md:108` | **BUG, same shape as 7 and NOT covered by it** — 7 is about a missing *class*. Same host-crash-kills-the-run defect, and the device answer (`NoSuchMethodError`, also a `LinkageError`) is **not measured**, so it is INHERITED at best. Reachable in the corpus the moment `[I->clone()` stops being emulated, which is **two** migrated tests, not one: `opcode/InvokeOpTest.cloningNullWithArrayCloneThrowsException` **and** `cloningArrayWithArrayCloneCloneWorksAsExpected`. An array's `clone()` is *synthesized* by the oracle (`type/VirtualArray.java:173-182`) and the new core's `ClassIndex` has no array-method equivalent, so the member is unresolvable **whatever the receiver holds** — this is not a null-receiver special case. **Needs an adjudication before either test's expectation can be stated**, covering both the answer (guest `NoSuchMethodError`? conservative opacity?) and, for the null-receiver test only, whether the NPE (2) or the missing-member error wins. Note the third `clone()` test is *not* blocked on this: `Ljava/lang/Object;->clone()` does resolve, with a real body (`resources/framework/smali/android-25/java/lang/Object.smali:33`) |
| 8 | **Uninitialized-instance aliasing** | `ExecutionState.update` propagates initialization to every register holding an "equal" uninitialized instance (`:456-480`), and `UninitializedInstance.equals` compares **type only** (`type/UninitializedInstance.java:17-29`) — so initializing one `new-instance Lfoo;` rewrites *every* register holding any uninitialized `Lfoo;`, including one from an unrelated allocation | `b3abe32:docs/architecture/state-model.md:160` | **RIGHT IDEA, WRONG MECHANISM,** and the requirement is easy to underrate: `new-instance v0` / `move-object v1, v0` gives two registers holding **one** object, and initializing through `v0` must be visible through `v1`. The oracle had no object identity, so type-equality was the only handle it had. Heap-id identity is the mechanism it wanted. → keep the requirement; add a two-unrelated-allocations test that fails under type-equality |
| 9 | 🆕 **`assumeMaximumUnknown` clobbers the argument REGISTER, and an `<init>` receiver unconditionally** | `assumeMaximumUnknown` (`opcode/InvokeOp.kt:175-212`), reached from `resume` whenever the callee graph aborted (`:125-129`): `STRONG` at `:179`; skip null args `:183-186`; **`<init>`'s p0 poked even when immutable, `:187-190`** (*"state can change in `<init>`"*); skip configured-immutable `:191-193`; otherwise `pokeRegister(register, Value.unknown(arg.type))` at `:195` with the **caller** register's declared type; result register `:198-201`; aborted `<clinit>` → class `Initialized` at `STRONG` **and** every static of that class `Unknown`, `:204-211`. `poke`, not `assign`, so `registersAssigned` is untouched; no exceptional child | `b3abe32:docs/architecture/host-execution.md:82`, `b3abe32:docs/architecture/state-model.md:160` | **RIGHT IDEA, WRONG MECHANISM. This is the ninth adjudication and it is a blocker, not a nicety** — see the block below |

#### 🆕 Adjudication 9 in full — the collapse must reach the object's CONTENTS, not the register holding it

**The requirement is the single most important behavior Plan 9 ports.** Without an argument collapse, the
new engine reports a caller's argument object as still holding its last known value after an opaque call —
a fabricated constant on a path a device could have mutated. That is the silently-wrong-answer class, and
it is the only Plan 9 gap that produces a **green** suite rather than a red test or a hang. Two migrated
tests exist for it:
`opcode/InvokeOpTest.invokeMethodCausingAssumeMaximumUnknownWithSignatureTypeLessSpecificThanKnownArgumentsTypeRetainsSpecificType`
and its `…UnknownArguments…` twin.

**But porting the register poke literally breaks every constructor.** Three of Plan 9's own settled
decisions collide:

1. Framework/non-application classes are not run through `VirtualBackend`, so a call into one is opaque
   (choices table, *Framework / non-application classes*).
2. An opaque call collapses its arguments, **and an `<init>` receiver regardless of immutability**
   (`opcode/InvokeOp.kt:187-190`).
3. A heap object's uninitialized flag is cleared when *"an `<init>` call whose receiver is that `HeapRef`
   returns"* (choices table, *What clears a heap object's uninitialized flag*).

**Counted, because the quantifier matters: of the 18 constructors across the whole test corpus, 12 open with
`invoke-direct {p0}, Ljava/lang/Object;-><init>()V`** — including all five that Plan 9's own migrated tests
enter: `class_with_default_constructor.smali:7`, `class_without_default_constructor.smali:7`,
`class_without_default_constructor_checks_null.smali:7`, `hash_code.smali:7` and
`invoke_virtual_test.smali:8`. Under (1) that call is opaque; under (2) p0 becomes `Unknown`; under (3) there
is then no `HeapRef` left to clear the flag on. The object under construction is destroyed before its own
constructor body runs. **The oracle never hits this** because `Ljava/lang/Object;-><init>()V` is **emulated**
(`emulate/MethodEmulator.kt:26`) and `EMULATE` is tested before `REFLECT`/`VIRTUAL`
(`ExecutionGraph.kt:84-88`) — so the mechanism Plan 9 removed is exactly the one that was hiding this.

**The other 6 constructors make the case stronger, not weaker**, which is why the count is stated rather than
generalized:

| Super-`<init>` target | Sites | Effect |
|---|---|---|
| `Ljava/lang/Enum;-><init>(Ljava/lang/String;I)V` | `extends_enum.smali:102`, `extends_enum_obfuscated.smali:94`, `class_builder/ComplexClass$TestEnum.smali:84` | **Identical collision** — also emulated (`emulate/MethodEmulator.kt:25`), so also opaque under decision 7 with the same clobber |
| `Ljava/lang/Throwable;-><init>()V` | `exceptions/CustomException.smali:14` | **Worse: no emulator was ever hiding it.** `Ljava/lang/Throwable;` is absent from `MethodEmulator.kt:19-28`, from `resources/safe_classes.cfg` and from `resources/immutable_classes.cfg`, so it is opaque in *both* engines — and this constructor aliases the receiver across **three** registers first (`move-object v0, p0`, `move-object v1, v0`, then `invoke-direct {v1}` at `:9`, `:12`, `:14`), which is the alias-staleness half of this adjudication in one fixture |
| `Lparent_class;-><init>()V`, `Lorg/cf/test/SuperClass;-><init>()V` | `inheritance/child_class.smali:13`, `class_builder/ComplexClass.smali:19` | **No collision** — application classes, so `VirtualBackend` runs them and the receiver is never clobbered. These are the control |

**The resolution is not a carve-out for `Object.<init>`; it is the correct port of the whole mechanism.**
The register poke was a *proxy* for *"this object's contents are now unknown,"* and it was the only proxy
available to an engine with **no instance-field state at all** (adjudication 6: `iget` answered `Unknown`
regardless, so there was nothing else to collapse). The new core has real object state, so:

> **An un-analyzable call sets `STRONG`, writes `Unknown(returnType)` to the result register if non-void,
> and marks the *contents* — the fields, or an array's elements — of every `HeapRef` reachable from the
> arguments `Unknown`. The `HeapRef` itself stays in the caller's register.** A null argument is skipped. A
> non-`HeapRef` argument is skipped (choices table, *An argument's mutability*). No `<init>` carve-out is
> needed: the receiver's contents go `Unknown`, its identity survives, and the flag can still be cleared.
> For an aborted `<clinit>`, keep the oracle's two effects — class `Initialized` at `STRONG` and every
> static of that class `Unknown`.

**Why this is strictly better on both axes, not a relaxation.**

- **Sounder.** Keeping only the register poke leaves the *heap object's* fields holding their pre-call known
  values, so a later `iget` — through that register, through an alias, or through any other reference to the
  same object — reads a stale constant. It also leaves an **alias** register stale: `move-object v1, v0`
  before an opaque call (the shape at `invoke_static_test.smali:51`) poked only `v0`. The oracle survives
  both only because it has no instance-field state and handles the alias case through the reflect path's
  mutable-parameter shadow (`b3abe32:docs/architecture/host-execution.md:69-104`), never through `assumeMaximumUnknown`.
- **More precise, and by a *provable* fact rather than a guess.** A callee cannot change **which** object a
  caller's register denotes, and that is **SPEC**, not an assumption: [BC], verbatim (same fetch as the
  invoke-kind table above) — *"The N arguments to a method land in the last N registers of **the method's
  invocation frame**, in order. Wide arguments consume two registers. Instance methods are passed a `this`
  reference as their first argument."* Arguments land in the **callee's** frame, so a callee writing a
  parameter register writes its own registers; the caller's are a different frame, and the only channel back
  is the result register (`move-result*`, which [BC] also defines as reading *"the most recent
  `invoke-kind`"*) or the shared heap. So replacing the `HeapRef` with `Unknown` discards something provable
  off the calling convention: the object's identity, its type, and (for an array) its length, none of which
  any callee can alter. That is the standing rule at the top of this section, applied one more time. (This
  is also the primary-source grounding for the appendix's *"parameter registers occupy the highest
  registers"* fact, which the appendix carries without a quote.)

**Corpus consequence — four expectations move, and they are all the same fact.** Under heap-id identity the
register or field keeps its `HeapRef` and the `Unknown` localizes to the contents:

| Migrated test (`opcode/InvokeOpTest`) | Oracle expects | Moves to | Authority |
|---|---|---|---|
| `invokeMethodCausingAssumeMaximumUnknownWithSignatureTypeLessSpecificThanKnownArgumentsTypeRetainsSpecificType` | r0, r2 = `Unknown("[B")` | r0, r2 = `Known(HeapRef, "[B")`, elements `Unknown` (both seeds are **zero-length**, so there is nothing to collapse) | this adjudication |
| `…LessSpecificThanUnknownArgumentsTypeRetainsSpecificType` | same | same | this adjudication |
| `invokeMutateStaticClassFieldNonDeterministicallyPropagatesUnknown` | field `mutable:[I` = `Unknown` | field = `Known(HeapRef, "[I")` with the **disagreeing element** `Unknown` | adjudication 8 + spec §4.2 |
| `knownMutableParametersAreMutatedWithNonDeterministicExecution` | r0 = `Unknown("[I")` | r0 = `Known(HeapRef, "[I")` with element 0 `Unknown` | adjudication 8 + spec §4.2 |

Each test's **intent** survives intact — the `…RetainsSpecificType` pair is about the unknown keeping the
*caller's* declared type rather than the parameter's, and it keeps it *a fortiori* (the object's own type).
Only the literal expected value changes, with a named authority, which is what makes it adjudication rather
than weakening.

#### Three previously-known oracle defects that invocation touches

- **D13, the host `ClassNotFoundException`.** Already numbered in the Plan 8 subsection and settled by
  `[DEVICE]`: see adjudication 7 and
  [Resolution failure is one family](#resolution-failure-is-one-family-tiered-once).
- **Entry-point truncation is never reported, and invocation is what makes the defect *observable*.**
  The only write to `ExecutionGraph.aborted` (`ExecutionGraph.kt:96`) sits inside `if (caller != null)`
  (`VirtualMachine.kt:371`, the write at `:376-378`), so a **top-level** method that hit its visit budget has
  always reported as complete in the legacy oracle. Two
  consequences the earlier reading of this defect had not drawn. (a) In a callee-free run there is no graph
  on the *reported* side at all, so the asymmetry — a callee's truncation reported, the entry method's
  silent — is not constructible until invocation exists; that is what a differential sweep can now see.
  (b) **`assumeMaximumUnknown` is therefore dead code in a callee-free run**, because `resume` only calls it
  when `calleeGraph.aborted` (`opcode/InvokeOp.kt:125-129`) and only a callee can carry that flag. So the
  argument collapse adjudication 9 rewrites has never been exercised on an entry-point graph, in either
  engine.
- **Handler resolution picks nearest-ancestor where Dalvik picks first-declared.**
  Invocation multiplies its blast radius: a callee's throw now has to find a handler in a
  *different* frame, and the outcome-partitioned merge above routes throwing-path state to *that* handler,
  so the wrong handler now also gets the wrong state.

---

## Appendix: verified toolchain and class-model facts

These are historical API facts for dexlib2 and the oracle class model, not Dalvik semantics.
Use the current immutable-snapshot and editor contracts in
[the architecture reference](architecture/graph-optimizer-contract.md) for V1 integrations.

- **`VirtualMethod`** (`org.cf.smalivm.type`): `getImplementation(): MutableMethodImplementation`,
  `getRegisterCount(): Int`, `getParameterTypeNames(): List<String>`, `getReturnType(): String`,
  `getTryBlocks(): List<BuilderTryBlock>`, `hasImplementation(): Boolean`, `isStatic(): Boolean`,
  `getSignature(): String`, `getDefiningClass(): VirtualType`.
- **Instructions:** `method.implementation.instructions: List<BuilderInstruction>`; per instruction
  `instruction.location: MethodLocation` with `.codeAddress: Int` and `.index: Int`; width is
  `instruction.codeUnits: Int`, so `nextAddress = codeAddress + codeUnits`.
- **`org.jf.dexlib2.Opcode.canContinue(): Boolean`** decides fallthrough (false for `RETURN*` /
  `THROW` / `GOTO*`). `Opcode.canThrow()` also exists.
- **Branch targets:** `(instruction as BuilderOffsetInstruction).target.codeAddress` — dexlib resolves
  the label for you. Used by the oracle's `GotoOp.build`.
- **Terminating opcodes** (the oracle's definition): `RETURN_VOID`, `RETURN`, `RETURN_WIDE`,
  `RETURN_OBJECT`, `THROW`.
- **`BuilderTryBlock`:** `.startCodeAddress: Int`, `.codeUnitCount: Int`,
  `.exceptionHandlers: List<BuilderExceptionHandler>`; each handler has `.exceptionType: String?`
  (**null means catch-all**) and `.handlerCodeAddress: Int`.
- **`ClassManager`** (`org.cf.smalivm.type`): `getVirtualType(typeSignature: String): VirtualType`,
  `getVirtualClass(className: String): VirtualClass`,
  `getVirtualMethod(className: String, methodDescriptor: String): VirtualMethod?`. Built by
  `ClassManagerFactory().build(smaliPath, DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL)))`.
- **Oracle `type.VirtualType` hierarchy queries:** `isChildOf(other): Boolean`,
  `getAncestors(): Set<out VirtualType>`, `getName(): String` (internal descriptor, e.g.
  `Ljava/lang/String;`), `instanceOf(target): Boolean`. `getAncestors0` carries a visited-set guard, so
  a cyclic hierarchy terminates rather than looping.
- **Parameter registers occupy the *highest* registers**, and Dalvik gives a wide (`J`/`D`) parameter
  **two** of them. The oracle's `getParameterSize()` is a *count*, not a register width — compute the
  width yourself by summing 2 for `J`/`D` and 1 otherwise over `getParameterTypeNames()`, which already
  includes `this` at index 0 for instance methods.

**Build and verification facts that have produced false greens.** These live in each plan's Global
Constraints too, because they cost time every time they are forgotten:

- JUnit Jupiter is pinned at **5.7.1** (`build.gradle:37-39`): `assertInstanceOf` does not exist (use
  `assertTrue(x is T)`), and `assertNotNull` returns **`void`**, so `val t = assertNotNull(x)!!` does
  not compile.
- Gradle's `Test` task defaults `enableAssertions = true`, so a Kotlin `assert(...)` in oracle code
  **does** fire under test even though it never fired in production.
- `clean` followed by a cached `test` prints `BUILD SUCCESSFUL` **without running the suite**. The tell
  is the task-execution summary (`N executed`, not `up-to-date`/`FROM-CACHE`), never the banner. Use
  `./gradlew --stop && ./gradlew :smalivm:test --rerun-tasks --no-build-cache`.
- Framework Smali is a **git submodule**; a fresh worktree needs `git submodule update --init` or ~422
  tests fail.
- `internal` main declarations **are** visible from the test source set.
- Scope every check to `:smalivm:` — `./gradlew test` is red at `:demoapp:compileJava` for pre-existing
  reasons (Plans 14–15 revive consumers).

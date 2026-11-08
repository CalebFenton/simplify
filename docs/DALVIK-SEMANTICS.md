# Dalvik Semantics Reference

This reference states, per opcode family and per cross-cutting topic, the Dalvik/ART behavior the SmaliVM
engine implements, the evidence for it, and every place the engine deliberately differs from ART. Tests and
KDoc cite a section here instead of restating its evidence. The engine's state, scheduling, and routing
contracts live in [the architecture overview](architecture/overview.md).

A wrong exception claim changes which guest `catch` blocks are reachable, and a deobfuscator acts on that
answer. Every runtime claim here therefore carries an evidence tag.

---

## Evidence tiers

Every claim carries one tag. A claim with no tag has not been checked.

| Tag | Meaning |
|---|---|
| **SPEC** | Stated by the Dalvik bytecode reference [BC], the instruction formats [IF], or the dex format [DEX]. Quoted. |
| **ART** | Read from ART source, end to end along the code path, with the function and file named. A claim about how a register is read also names the interpreter tier (see [ART has two interpreter tiers](#art-has-two-interpreter-tiers-that-read-a-register-differently)). |
| **DEVICE** | Measured on real ART by this repository's harness, cited `DEVICE(tier, interpreter mode, artifact)` with a fixture name. |
| **INHERITED** | Dalvik is silent and the rule comes from [JVMS]/[JLS]. Names the JVM counterpart instruction. Corroboration for a runtime claim, never sole support. |
| **VERIFIER** | The condition cannot arise in verified bytecode. The engine must still be total here, so the behavior is an engine choice with a stated reason, never a Dalvik fact. |
| **ENGINE CHOICE** | A modelling decision for a question no runtime answers, such as an unprovable value. |

For a claim about what the runtime does, the tiers rank: DEVICE outranks ART, which outranks [BC]/[CON]/[VER],
which outrank [JVMS]/[JLS]. The higher tier wins a conflict outright. Two limits apply:

1. A device cannot measure input its verifier rejects. Tier A reports ART's verifier verdict as a catchable
   `VerifyError`, which is a measurement of the verifier only.
2. A Tier A measurement cannot settle a question that the two interpreter tiers answer differently, because
   Tier A runs only the switch interpreter. Run Tier B in both modes before treating a Tier A versus Tier B
   disagreement as a version difference.

An ART claim must follow the whole path. A single flag argument bounds one call site only: `CONST_CLASS` and
`NEW_INSTANCE` both pass `can_run_clinit = false` to `ResolveVerifyAndClinit`, and `NEW_INSTANCE` still
initializes its class downstream in `AllocObjectFromCode` (see [`const-class`](#const-class-21c)).

### Device measurements

| Tier | Tool | Runtime | Use |
|---|---|---|---|
| A | `tools/art-runner/` | `art_standalone 0_git20251009-r2`, `-Xint` | Daily driver; reports verifier verdicts |
| B | `tools/emulator/` | API 36 device, `nterp` default or `-Xint` | Cross-validation and register-read questions |

Every DEVICE claim names an `Fx_*` fixture so a reader can re-run it (`tools/art-runner/run-batch.sh Fx_Name`).
The recorded answers are `tools/art-runner/golden/tier-a-batch.txt` and `tools/emulator/golden/tier-b-batch.txt`.
Verifier-invalid Tier B probes live in `tools/art-runner/tier-b-only/`, outside `fixtures/`, so they never enter
the analysed corpus. Tier A's AOT mode silently drops an `ArrayStoreException` the interpreter throws, so the
runner refuses AOT unless `ART_RUNNER_ALLOW_UNSOUND_AOT=1` is set.

### The modelled tier

The engine models ART's switch interpreter (`-Xint`). Three execution modes exist:

| Mode | Status |
|---|---|
| AOT (`dex2oat`) | Unsound on Tier A: it drops an `ArrayStoreException` the interpreter throws. Not modelled. |
| Switch interpreter (`-Xint`) | Modelled. Both tiers can run it, so every claim stays cheap to reproduce on Tier A. |
| `nterp` | The default on a modern device. Differs from the switch interpreter in two measured places. |

The two measured `nterp` differences are a register read and an allocation order. On the register read,
`nterp` dereferences a non-zero primitive as a pointer and the process dies, so there is no value to model
(see [ART has two interpreter tiers](#art-has-two-interpreter-tiers-that-read-a-register-differently)). On
the allocation order, `nterp` resolves a `new-array` component class before it checks the length (see
[`new-array`](#new-array-op-23-22c)). `nterp` skips the helper routines (`CheckArrayAlloc`) and the
`ShadowFrame` reference array that the switch interpreter uses, and both differences come from that.

A code site whose behavior an interpreter-tier split touches carries a greppable marker naming what `nterp`
does and the fixture that measured it:

```kotlin
// TIER-DIVERGENCE(nterp): <what nterp does instead>. Measured <Fx_Fixture>.
```

`git grep -n 'TIER-DIVERGENCE' -- smalivm/src/main` lists the sites.

---

## Sources

| Tag | Document | URL |
|---|---|---|
| [BC] | Dalvik bytecode format | `https://source.android.com/docs/core/runtime/dalvik-bytecode` |
| [IF] | Dalvik instruction formats | `https://source.android.com/docs/core/runtime/instruction-formats` |
| [DEX] | Dalvik Executable format | `https://source.android.com/docs/core/runtime/dex-format` |
| [CON] | Dalvik bytecode constraints | `https://source.android.com/docs/core/runtime/constraints` |
| [VER] | Dalvik bytecode verifier notes | `https://android.googlesource.com/platform/dalvik/+/refs/heads/main/docs/verifier.html` |
| [RT] | Android runtime and Dalvik | `https://source.android.com/docs/core/runtime` |
| [JVMS] | JVM Specification, Java SE 21 | `https://docs.oracle.com/javase/specs/jvms/se21/html/` |
| [JLS] | Java Language Specification, Java SE 21 | `https://docs.oracle.com/javase/specs/jls/se21/html/` |
| [ART] | ART source | `https://android.googlesource.com/platform/art/+/refs/heads/main/`; class-initialization claims are pinned to commit `6484611fd45e69db9f33f98bfd6864014b030ecf` |

ART citations name a function and a file. Line numbers drift between ART revisions and are not cited.

### What the Android documents specify

[BC] names exactly two `Exception` types: `ArithmeticException` (division and remainder) and
`ClassCastException` (`check-cast`). It also names `BootstrapMethodError` for `invoke-custom`. It never names
`NullPointerException`, `ArrayIndexOutOfBoundsException`, `NegativeArraySizeException`, `ArrayStoreException`,
`NoClassDefFoundError`, `OutOfMemoryError`, or `IllegalAccessError`, and it never mentions class
initialization. Every other exception and every initialization claim here is therefore ART, DEVICE, or
INHERITED.

[VER] is the bridge for INHERITED claims:

> "We perform generally the same set of checks as are described in *The Java Virtual Machine Specification,
> Second Edition*, including the updates planned for the Third Edition."

The bridge has three gaps. It describes what the verifier checks, not what the interpreter does. It says
"generally", and [VER] itself names one difference: "The Dalvik verifier is more restrictive than other VMs in
one area: type safety on sub-32-bit integer widths." It names JVMS Second Edition, while this file cites SE21;
the rules cited here (§5.4.3, §5.5, §6.5 array and cast instructions, JLS §4.10.3) are unchanged across those
editions. A claim about `Z`/`B`/`S`/`C` widths must be checked against [VER], not taken from [JVMS].

[CON] disclaims everything this engine analyses: "a runtime is required to support only valid `.dex` files."
[VER] gives the reason ART performs no runtime check for verifier-excluded conditions: "The interpreter can
ignore a lot of potential error cases because the verifier guarantees that they are impossible." So for
verifier-rejected input ART has no defined behavior (`HandleAGet` and `HandleAPut` `DownCast` unchecked,
`FillArrayData` `memcpy`s without a width check), and the engine picks a total behavior instead. [RT] adds
that ART verifies more strictly than Dalvik, and that "some post-processing tools produce invalid files that
may be tolerated by Dalvik but cannot be compiled by ART."

---

## Registers and the null idiom

Dalvik registers are untyped 32-bit slots, and a 64-bit value occupies an adjacent pair (**SPEC**). Nothing
at runtime carries a per-register type. `Value.type` is the engine's own metadata: a concolic VM needs a type
for an unknown value that has no object to ask.

A register's tag and the object it holds can disagree. For verified input they are assignment-compatible: the
object's runtime type is assignable to the tag, never the reverse (**VERIFIER**, [CON] B1). Covariance is the
legal disagreement: a `String[]` object in a register tagged `Object[]` (JLS §4.10.3). For every runtime check
(the `ArrayStoreException` test, the component type an array op reads) the engine asks the object, never the
tag. One opcode serves several types (`const` serves `int`/`float`/`char`/`short`/`byte`/`boolean`), so a tag
records provenance, not a guarantee about the bits. `OpBuilder` tags narrow `const*` literals `I` and
`const-wide*` literals `J`; a float or double reader reinterprets the bits through `Coerce`.

**SPEC** [BC], "General design": "In terms of bitwise representation, `(Object) null == (int) 0`." `if-eqz`
doubles as a null test and `if-eq` compares references because of this sentence.

### Reading a register as a reference

ART's switch interpreter keeps every register twice. `ShadowFrame` (`runtime/interpreter/shadow_frame.h`)
documents "a two-part array": "when a primitive is stored in vX, the second (reference) part of the array will
be null. When a reference is stored in vX, the second (reference) part of the array will be a copy of vX."
`SetVReg`, `SetVRegFloat`, `SetVRegLong`, and `SetVRegDouble` each end with `References()[i].Clear()`, and
`GetVRegReference` reads the reference half. Every handler that reads an operand as an object uses
`GetVRegReference`: `THROW`, `ARRAY_LENGTH`, `HandleAGet`, `HandleAPut`, `FILL_ARRAY_DATA`, `MONITOR_ENTER`,
`MONITOR_EXIT`, `CHECK_CAST`, `INSTANCE_OF`, every field op, invoke receivers, and `DoFilledNewArray`'s
reference-component fill (**ART**, `runtime/interpreter/interpreter_switch_impl-inl.h`,
`runtime/interpreter/interpreter_common.cc`).

The engine's predicate is `Value.readsAsNullReference` (`core/Value.kt`): true for a null raw and for every
`Known` value with a primitive type, whatever its bits. Every op that reads a register as a reference asks it.
`Value.isDalvikNull` is the narrower "integral zero under a primitive tag"; `aput-object`'s value operand uses
it, because storing integral zero into a reference component is a coercion question.

| Register content | Engine reads it as | Tier |
|---|---|---|
| `Known(null, T)` | null | **ART**, both interpreter tiers |
| A primitive whose 32 bits are zero (`Known(0, I)`, `Known(false, Z)`, `Known(0.0f, F)`) | null | **SPEC** (`(Object) null == (int) 0`) and **ART**, both interpreter tiers |
| A primitive whose bits are not zero (`Known(5, I)`) | null | **VERIFIER**: the switch interpreter reads null, `nterp` dereferences the bits and the process dies; ART's verifier rejects the class |
| `Unknown` or `Top` | not provable; the op keeps both outcomes | **ENGINE CHOICE** |

### ART has two interpreter tiers that read a register differently

Name the interpreter tier in every claim about how a register is read. ART runs the switch interpreter
(`interpreter_switch_impl-inl.h` over `ShadowFrame`) under `-Xint`, a debuggable runtime, instrumentation, or an
unsupported ISA. Otherwise it runs `nterp` (`runtime/interpreter/mterp/*ng/*.S`); `CanRuntimeUseNterp` in
`runtime/interpreter/mterp/nterp.cc` makes that a property of the device and flags, not of the bytecode.
`-Xint` sets `Instrumentation::ForceInterpretOnly`, which `CanRuntimeUseNterp` rejects.

`nterp` keeps the reference array (its `SET_VREG` zeroes the reference slot) but its dereferencing handlers
read the raw register with `GET_VREG`: `check-cast`, `instance-of`, `throw`, `array-length`, `aget`, `aput`,
`aput-object`'s value, `fill-array-data`, and `filled-new-array`'s reference fill (nterp's own
`DoFilledNewArray` stores `reinterpret_cast<mirror::Object*>(regs[src_reg])`).

The two tiers agree when the raw is zero: each `nterp` handler branches on zero and takes the null path. They
contradict each other when the raw is a non-zero primitive. **DEVICE**(Tier B, `nterp` default, API 36),
probes in `tools/art-runner/tier-b-only/`, run under `-Xverify:none`:

| Register value read as a reference | `nterp` | Switch interpreter (`-Xint`) |
|---|---|---|
| `0x7fabc000` (unmapped), `instance-of` | SIGSEGV at the register value, backtrace `nterp_op_instance_of` | stores `0` |
| `0x2a`, `instance-of` | SIGABRT: the fault lands in the implicit-null-check window and ART cannot build an NPE for a type op | stores `0` |
| `0x11223344` (mapped), `instance-of` / `check-cast` | SIGABRT from a second-level dereference | stores `0` / cast succeeds |
| `0x0` | stores `0` / cast succeeds | same |

`-Xusejit:false` gives the same `nterp` results, so the JIT is not involved. ART's verifier hard-fails a
non-reference operand for every consumer (`VERIFY_ERROR_BAD_CLASS_HARD` in
`runtime/verifier/method_verifier.cc`: "on non-reference in v" for `check-cast`/`instance-of`, "thrown value of
non-reference type" for `throw`, "array-length on non-array", and "not array type" in `VerifyAGet`), so no
linkable APK presents the non-zero case. The engine reads it as null, which matches one real tier and is the
sound choice.

`nterp` resolves `check-cast`, `instance-of`, `const-class`, and `new-array` types through `NterpGetClass`,
which also passes `can_run_clinit = false` and does nothing else that could initialize. `nterp`'s
`DoFilledNewArray` passes `can_run_clinit = true` where the switch interpreter passes `false`; no claim here
rests on that flag.

---

## Coercion and register bits

`core/Coerce.kt` reads a register's raw at the width an instruction reads it and answers the bits a device
reads. A register holds one of eight boxes: `Byte`, `Short`, `Int`, `Char`, `Boolean`, or `Float` for a 32-bit
register; `Long` or `Double` for a pair.

| Read | Answer | Null for |
|---|---|---|
| `toInt` (32 bits) | The 32 bits as a signed int: integral boxes truncate to the low 32 bits, `Char` its code, `Boolean` 1/0, `Float` `floatToRawIntBits`, `Double` the low word of `doubleToRawLongBits` | any other object |
| `toLong` (64 bits) | `Long` itself, `Double` `doubleToRawLongBits` | a 32-bit box: the other half is `register + 1`, which the engine's one value per pair does not hold |
| `toFloat` (32 bits) | `intBitsToFloat(toInt)`; a `Float` passes through | any other object |
| `toDouble` (64 bits) | `longBitsToDouble(toLong)`; a `Double` passes through | a 32-bit box |

A null read makes the caller write `Value.Unknown(resultType)`. So `2.9f` read as an index is `0x4039999A`
(1077516698), `-0.5f` read as a `new-array` length is `0xBF000000` (-1090519040), and `const v0, 0x3f800000`
followed by `add-float` adds `1.0f`. A raw that is none of the eight boxes (`BigDecimal("7.9")`) reads null
rather than converting by value.

Conversions read by bits and then convert by value. `float-to-int` reads the register at float width and
applies the [JLS] §5.1.3 narrowing. **DEVICE**(Tier A, `-Xint`, `art_standalone 0_git20251009-r2`) for
`double-to-int`: `NaN → 0`, `+Inf → 2147483647`, `-Inf → -2147483648`, finite values beyond the range
saturate, and `-0.5 → 0`.

`Coerce.narrowStore` gives the value a `Z`/`B`/`C`/`S` slot holds after a store: the low 8 or 16 bits of the
32-bit register, as ART's field stores keep them (`GetFieldValue` casts with `static_cast<uint8_t>`,
`<int8_t>`, `<uint16_t>`, `<int16_t>`; **ART**). So `sput-byte` of `0x100` stores `0`. A register with no
32-bit reading stores `Unknown(type)`. A boolean byte other than 0 or 1 also stores `Unknown(Z)`: a device keeps
the byte, but the engine's `Boolean` raw cannot hold it (**ENGINE CHOICE**). Field stores, array stores, and
`fill-array-data` elements all narrow through it.

---

## Arithmetic and compare

### Unary operations

`neg-*`, `not-*`, and the `*-to-*` conversions (`core/op/UnaryMathOp.kt`) read the source at the width the
mnemonic names and convert by value. The result type is the mnemonic's last segment (`int-to-byte` → `B`). An
unknown source, or one with no reading at that width, gives `Unknown(resultType)`. None of them throws
(**SPEC**: [BC] names no exception).

### `cmp-long`, `cmpl-*`, `cmpg-*`

`core/op/CmpOp.kt`. The result is always `I`.

- Either operand unknown, or without a reading at the instruction's width: `Unknown(I)`.
- Either operand NaN: `1` for `cmpg-*`, `-1` for `cmpl-*` (**SPEC**, [BC]).
- Otherwise `cmp-long` is a signed `Long` compare, and the float and double forms use IEEE-754 `>`, `==`, `<`,
  so `-0.0` and `0.0` compare equal (**ART**, `HandleCmpl`/`HandleCmpg` in
  `interpreter_switch_impl-inl.h`). `Float.compare`/`Double.compare` order `-0.0` below `0.0` and are not this
  rule.

### Binary operations

`core/op/BinaryMathOp.kt`. The operand domain is the mnemonic's second segment; `rsub-int` and the `/lit8` and
`/lit16` forms exist only for `int`. Register mapping:

| Format | Example | dest | lhs | rhs |
|---|---|---|---|---|
| 23x | `add-int v0, v0, v1` | `vA` | `vB` | `vC` |
| 12x (`/2addr`) | `add-int/2addr v0, v1` | `vA` | `vA` | `vB` |
| 22b / 22s (`/lit8`, `/lit16`) | `add-int/lit8 v0, v0, 0xf` | `vA` | `vB` | literal, typed `I` |

Shifts mask the distance to 5 bits for `int` and 6 bits for `long` ([BC]). A `long` shift distance is one
32-bit register, so the engine reads it with `toInt`. Integer overflow wraps and does not throw:
`Integer.MIN_VALUE / -1` is `Integer.MIN_VALUE` (**INHERITED**, [JVMS] `idiv`; **DEVICE** fixture
`Fx_DivOverflow`).

Only integral `div` and `rem` throw, and only on a zero divisor (**SPEC**, [BC]: `ArithmeticException`).

| Divisor (read at the domain's width) | Arms |
|---|---|
| provably zero | `Throw(ArithmeticException)` |
| provably non-zero | `Continue` |
| unknown or unreadable | `Continue` and `Throw(ArithmeticException)` |

`div-int` reads one 32-bit register, so a `Long` raw `2^32` is a zero divisor there. Float and double
division and remainder never throw. **DEVICE**(Tier A, `-Xint`, `art_standalone 0_git20251009-r2`):
`±x / ±0` is an infinity whose sign follows IEEE-754 (`+1 / -0.0 = -∞`), `0 / 0` is NaN, and `rem` by either
signed zero is NaN.

The message is ART's `"divide by zero"` (`ThrowArithmeticExceptionDivideByZero`, `runtime/common_throws.cc`;
`GuestThrowables.divideByZero`), not the JVM's `"/ by zero"`. Source read; no fixture records the text.

---

## Control flow

### `goto` / `goto/16` / `goto/32` (10t / 20t / 30t)

**SPEC** [BC]: "Unconditionally jump to the indicated instruction." Offsets are signed and count 16-bit code
units: [BC] "The storage unit in the instruction stream is a 16-bit unsigned quantity", [IF] "The first digit
indicates the number of 16-bit code units in the format", and [DEX] defines a bytecode address as "a count of
16-bit code units". The offset is relative to the branch opcode's own address (**INHERITED**, [JVMS] `goto`).
`OpBuilder` resolves the label to an absolute address at build time, and `GotoOp` jumps there.

[BC] forbids a zero offset for `goto`, `goto/16`, `if-test`, and `if-testz` ("Note: The branch offset must not
be 0.") and permits it for `goto/32`. ART enforces all four in one check in `method_verifier.cc` (comment: "Only
`goto/32` instruction can target itself"), failing with `FailBranchOffsetZero` (**VERIFIER**). The engine
executes a zero-offset branch as a self-loop, bounded by the address-visit limit.

### `if-eq` / `if-ne` / `if-lt` / `if-ge` / `if-gt` / `if-le` (22t) and the `*z` forms (21t)

**SPEC** [BC]: "Branch to the given destination if the given two registers' values compare as specified", and
for the `*z` forms "if the given register's value compares with 0 as specified". [BC] never names an operand
type. At runtime none of the twelve checks a type: each compares raw 32-bit contents, signed. None throws.

Operand typing is **VERIFIER**, and ART's rule is asymmetric (`method_verifier.cc`, the `IF_EQ` through
`IF_LEZ` cases):

| Opcodes | ART's verifier accepts |
|---|---|
| `if-eq`, `if-ne` | vA zero-or-null, then vB a reference or integral; else vA a reference, then vB a reference; else both integral |
| `if-lt`, `if-ge`, `if-gt`, `if-le` | both integral |
| `if-eqz`, `if-nez` | a reference or an integral |
| `if-ltz`, `if-gez`, `if-gtz`, `if-lez` | integral |

`IsIntegralTypes` (`runtime/verifier/reg_type.h`) admits `Z`/`B`/`S`/`C`/`I`, their constant forms, and
`Zero`; it excludes `float`, `long`, `double`, and references. `IsReferenceTypes` admits `Zero` and `Null`, so
`Zero` is both a reference and an integral type while `Null` is only a reference. Ordering comparisons on two
`long`s, two `float`s, or two references are verifier-rejected, and so is `if-eq` on a `long` or `float`. The
`vA`-first branch makes `if-eq vNull, vInt` verify and `if-eq vInt, vNull` fail. Dalvik 4.4.4's verifier
(`vm/analysis/CodeVerify.cpp`) is symmetric, so the asymmetry is ART-only.

The engine (`core/op/IfOp.kt`) decides a branch from `provableInt` (`core/Provable.kt`), which classifies each
operand's 32 bits as exactly known, a live pointer (provably non-zero, no particular value), or unprovable:

| Operands | Engine | Tier |
|---|---|---|
| both bits known | compare as signed ints | **SPEC** |
| a provable zero against a live pointer, in either order | provably unequal; `eq`/`ne` decided, orderings fork | **SPEC** (`(Object) null == (int) 0`) |
| two live pointers | identity (`sameReference`: heap id for tracked objects, `===` for host objects, tags ignored) decides equality; identical pointers decide every comparison; distinct pointers fork the orderings | **ENGINE CHOICE**; orderings on references are **VERIFIER**-rejected |
| a live pointer against a non-zero integer | fork | **VERIFIER**-rejected; a pointer is provably non-zero, not provably different from `5` |
| an unknown, `Top`, or a `Float`/`Double` raw | fork, fall-through first | **ENGINE CHOICE** |

A `Float` or `Double` raw is unprovable for a branch because its box's bits need not match what a register
computed in another domain holds.

ART's verifier also sharpens the type of an `instance-of` result register at the following `if-eqz`/`if-nez`,
on the edge where the test passed, and not for unresolved or interface types (**ART**, the `IF_EQZ`/`IF_NEZ`
case in `method_verifier.cc`, comment "Check for peep-hole pattern of: ... instance-of vX, vY, T; ifXXX vX,
label"). The engine does not model branch-sensitive type refinement.

### `packed-switch` / `sparse-switch` (31t) and their payloads

**SPEC** [BC]: both forms "fall through to the next instruction if there is no match". A Dalvik payload has no
default entry, unlike JVM `tableswitch`/`lookupswitch`. Targets "are relative to the address of the switch
opcode, not of this table."

Payload layouts are **SPEC** ([BC]). `size` is a `ushort`, so 0 through 65535 cases are representable.

| `packed-switch-payload` | | `sparse-switch-payload` | |
|---|---|---|---|
| `ident` | `ushort = 0x0100` | `ident` | `ushort = 0x0200` |
| `size` | `ushort` | `size` | `ushort` |
| `first_key` | `int` | `keys` | `int[]`, "sorted low-to-high" |
| `targets` | `int[]` | `targets` | `int[]` |
| code units | `(size * 2) + 4` | code units | `(size * 4) + 2` |

Payloads are data. [BC] "General design": pseudo-instructions "must never be encountered during the normal
flow of execution" and sit on even code-unit offsets; [CON] B22: "The packed-switch-data, sparse-switch-data,
and fill-array-data pseudo-instructions must not be reachable by control flow." `OpBuilder` decodes each
payload into its switch at build time and never executes the payload address.

ART's dispatch (`DoPackedSwitch` and `DoSparseSwitch` in `runtime/interpreter/interpreter_common.h`; **ART**):

1. `size == 0` falls through.
2. Packed dispatch computes `index = test_val - first_key` in wrapping signed 32-bit arithmetic and takes the
   case when `0 <= index < size`. The verifier's `first_key + size - 1` overflow check makes the wrap
   unreachable in verified code, and it runs only when `size > 1`.
3. Sparse dispatch is a binary search over the keys as stored. A table that is not strictly ascending makes
   some present keys unreachable, and those selectors fall through.
4. A wrong `ident` is a `DCHECK`, so a release build reads the bytes as a table anyway.

The verifier's checks (all hard failures, `CheckAndMarkSwitchTargets` in `method_verifier.cc`): payload offset
in range and at an instruction start, payload aligned, `ident` correct, packed `first_key` overflow, sparse keys
strictly ascending (`key <= last_key` fails, stricter than [BC]'s "sorted low-to-high"), and each target in
range, at an instruction start, and not a `move-result`/`move-exception`.

The engine (`core/op/SwitchOp.kt`):

| Selector | Successors | Tier |
|---|---|---|
| bits known | the matching cases; the fall-through when ART's lookup misses (packed: no matching key; sparse: ART's binary search over the payload order misses) | **ART** |
| bits known, sparse table not strictly ascending | the union of ART's binary-search answer and every case whose key matches | **VERIFIER**: one runtime's search over an invalid table is not a fidelity target, so the engine explores both |
| a live pointer | the fall-through only | **VERIFIER**: the verifier requires an integral selector; a heap address is none of the keys |
| unknown | every successor, fall-through first, deduplicated by address | **ENGINE CHOICE** |

dexlib2 normalizes both payloads to `(key, offset)` pairs; packed keys are `first_key + i` in Java `int`
arithmetic, which wraps exactly as ART's subtraction does. `SwitchOp` has no arity cap. Address visits are
path-local, so each arm of a loop-free switch reaches its join once.

---

## Arrays

[BC]'s entire specification for the fourteen `aget*`/`aput*` opcodes is one sentence: "Perform the identified
array operation at the identified index of the given array, loading or storing into the value register." The
exception rules below come from ART, DEVICE, and the JVM counterparts `newarray`/`anewarray`, `arraylength`,
`iaload`/`aaload`, and `iastore`/`aastore`.

All array ops read the array operand through `readArrayOperand` (`core/op/ArrayOperand.kt`), which gives one of
four cases: unprovable (an unknown or `Top`), null (`readsAsNullReference`), not tracked (a provably non-null
value with no tracked array behind it), or a tracked array. A tracked array's component type is the object's,
never the register tag's. The not-tracked case is **VERIFIER**: verified code cannot reach it, ART `DownCast`s
unchecked, and the engine raises no exception and reads an unknown element.

The heap tracks arrays up to `MAX_TRACKED_ARRAY_LENGTH` (2^20) elements (`core/HeapStore.kt`). A longer
allocation produces `Unknown(arrayType)`, which drops what is known about the array and claims nothing false
(**ENGINE CHOICE**: hostile lengths must not exhaust the host).

### `new-array` (op 23, 22c)

**SPEC** [BC]: "Construct a new array of the indicated type and size. The type must be an array type."

| Condition | Engine (`core/op/NewArrayOp.kt`) | Tier |
|---|---|---|
| negative length | `NegativeArraySizeException`, message the length in decimal (`"-1"`) | **INHERITED** ([JVMS] `newarray`/`anewarray`: "If count is less than zero, ... throws a NegativeArraySizeException"); message **ART** (`ThrowNegativeArraySizeException(int)` in `runtime/common_throws.cc`: `StringPrintf("%d", size)`) and **DEVICE** (`Fx_NewArrayAbsentTypeNegativeLength` on `-Xint`) |
| unknown length | fork: the allocation (as `Unknown(arrayType)`) and a message-less `NegativeArraySizeException` | **ENGINE CHOICE** |
| a `Float` raw as the length | its 32 bits: `-0.5f` is -1090519040, so it throws | **VERIFIER** ([CON] B1); the read is [Coercion](#coercion-and-register-bits)'s |
| absent component class | `NoClassDefFoundError`, message `"Failed resolution of: <descriptor>"` | **DEVICE** (`Fx_NewArrayAbsentType`); see [Resolution failure](#resolution-failure-is-one-family-tiered-once) |
| fresh elements | `0`, `\u0000`, positive zero, `false`, `null` | **DEVICE**(Tier A, `-Xint`): a fresh `float[]`/`double[]` element's raw bits are `0x0`; [JVMS] §2.3/§2.4 corroborate |
| more than 255 dimensions, invalid type index | not decoded | **VERIFIER** ([CON] A18, A19) |

[CON] A21 ("The type referred to by a new-array instruction must be a valid, non-reference type") contradicts
[BC] and ordinary `d8` output such as `new-array v0, v1, [Ljava/lang/Object;`. The engine treats A21 as a
documentation defect.

**Length before class, as the switch interpreter orders it.** When the class is absent and the length is
negative, [JVMS] `anewarray` resolves first (its "Linking Exceptions" precede "Otherwise, if count is less than
zero"), the switch interpreter checks the length first (`CheckArrayAlloc` in
`runtime/entrypoints/entrypoint_utils-inl.h` tests `component_count < 0` before `GetResolvedType`/`ResolveType`),
and `nterp` resolves first. `Fx_NewArrayAbsentTypeNegativeLength` (`new-array v0, -1,
[Lcom/example/DefinitelyMissing;`):

| Measurement | Answer |
|---|---|
| DEVICE(Tier A, `-Xint`, `art_standalone 0_git20251009-r2`) | `NegativeArraySizeException("-1")` |
| DEVICE(Tier B, `-Xint`, API 36) | `NegativeArraySizeException("-1")` |
| DEVICE(Tier B, `nterp` default, API 36) | `NoClassDefFoundError` |

The engine follows the [modelled tier](#the-modelled-tier) and throws `NegativeArraySizeException`. On this
input a guest `catch (NegativeArraySizeException)` or `catch (Exception)` is reachable under the engine's answer
and on `-Xint`, and is dead under `nterp`.

### `array-length` (op 21, 12x)

**SPEC** [BC]: "Store in the given destination register the length of the indicated array, in entries." The
length comes from the array object. A null array throws `NullPointerException` (**INHERITED**, [JVMS]
`arraylength`) with ART's message `"Attempt to get length of null array"` (**ART**,
`ThrowNullPointerExceptionFromDexPC` in `runtime/common_throws.cc`). An unprovable array forks the unknown
length and the NPE. A non-array operand is **VERIFIER**: ART's `ARRAY_LENGTH` calls `AsArray()` unchecked, and
the engine answers `Unknown(I)` with no exception.

### `aget*` / `aput*` (ops 44–51, 23x)

| Condition | Engine (`core/op/AGetOp.kt`, `core/op/APutOp.kt`) | Tier |
|---|---|---|
| null array | `NullPointerException`: `"Attempt to read from null array"` (`aget*`), `"Attempt to write to null array"` (`aput*`) | **INHERITED** ([JVMS] `iaload`/`iastore`); messages **ART** (`ThrowNullPointerExceptionFromDexPC`) and **DEVICE** (`Fx_AputNullArrayBadIndex`) |
| index outside `[0, length)`, including negative | `ArrayIndexOutOfBoundsException`, message `"length=%d; index=%d"` | **INHERITED**; ART compares unsigned (`Array::CheckIsValidIndex`), so a negative index is the same exception; message **ART** (`ThrowArrayIndexOutOfBoundsException(int index, int length)` prints length first); **DEVICE** (`Fx_AputNegativeIndex`: `length=1; index=-1`) |
| unknown index into a tracked array | fork: the access and an AIOOBE whose message is omitted | **ENGINE CHOICE** |
| unknown index into an empty tracked array | definite AIOOBE, message omitted | **ART**: every index misses |
| unprovable array | fork: the access, the NPE, and an AIOOBE | **ENGINE CHOICE** |
| ordering | NPE, then AIOOBE, then `ArrayStoreException` | **DEVICE** (below); [JVMS] `aastore`'s "Otherwise" chaining corroborates |
| opcode variant does not match the component | verifier hard failure, never a runtime exception | **VERIFIER** ([CON] B1; ART `VerifyAGet`/`VerifyAPut`) |

**DEVICE**(Tier A, `-Xint`, `art_standalone 0_git20251009-r2`). Each fixture makes two of {null array,
out-of-range index, unassignable value} true at one `aput-object`:

```
FIXTURE	Fx_AputNullArrayBadIndex	THREW	java.lang.NullPointerException	Attempt to write to null array
FIXTURE	Fx_AputNullArrayIncompatibleValue	THREW	java.lang.NullPointerException	Attempt to write to null array
FIXTURE	Fx_AputBadIndexIncompatibleValue	THREW	java.lang.ArrayIndexOutOfBoundsException	length=1; index=5
FIXTURE	Fx_AputNegativeIndexIncompatibleValue	THREW	java.lang.ArrayIndexOutOfBoundsException	length=1; index=-1
FIXTURE	Fx_AputNegativeIndex	THREW	java.lang.ArrayIndexOutOfBoundsException	length=1; index=-1
```

An engine message names only values the engine knows: an unprovable length or index omits the AIOOBE message
rather than inventing one (`GuestThrowables.arrayIndexOutOfBounds`).

Which variant accepts which component (**VERIFIER**, ART `VerifyAGet`): `-boolean`, `-byte`, `-char`, and
`-short` require an exact match; `aget`/`aput` accept `int[]` or `float[]`; `-wide` accepts `long[]` or
`double[]`; `-object` accepts any reference-component array. The opcode does not distinguish int from float or
long from double, which is why an element's type comes from the array object. `aget-wide` off a narrow array
reads the element as it is (**VERIFIER**).

#### `ArrayStoreException` comes from `aput-object` and nothing else

- [JVMS] lists only NPE and AIOOBE for `iastore`/`bastore`; only `aastore` lists `ArrayStoreException` "if
  arrayref is not null and the actual type of the non-null value is not assignment compatible with the actual
  type of the components of the array" (**INHERITED**).
- Storing null never throws: [JVMS] `aastore` "If value is null, then value is stored"; ART's `CheckAssignable`
  is guarded by `object != nullptr`.
- ART's `APUT_OBJECT` is the only array-store handler with an assignability check; the six non-object `aput*`
  opcodes route to `HandleAPut<T>`, which has none (**ART**).
- The check is a runtime check by design. ART's `VerifyAPut` comment: "we use the instruction type (rather than
  the component type) for aput-object as incompatible classes will be caught at runtime as an array store
  exception" (**ART**).
- The message is `"<value class> cannot be stored in an array of type <array class>"` in `PrettyDescriptor`
  form (**DEVICE**, `Fx_AputObjectIncompatible`: `java.lang.Object cannot be stored in an array of type
  java.lang.String[]`). The engine omits it when it cannot name both runtime classes.

The engine's store check (`APutOp.storeCheck`), first match wins:

| Value and component | Engine | Tier |
|---|---|---|
| null, or integral zero stored by `aput-object`, into a reference component | store | **INHERITED** + **ART** |
| primitive into primitive | store, no ASE | **INHERITED** + **ART** |
| one primitive, one reference | definite `ArrayStoreException`, before the null and bounds checks, so a type lie never lands in an element | **VERIFIER** → **ENGINE CHOICE** |
| reference, provably assignable to an exact component | store | **ART** |
| reference, provably not assignable | ASE after the null and bounds checks | **ART** |
| assignability unanswerable, or the component comes from a non-exact register tag | fork: store and ASE | **ENGINE CHOICE** |

A component is exact when it is primitive, a final class, or an array of either; only then does a register tag
name the runtime array type. The stored element is narrowed to the component width through
[`Coerce.narrowStore`](#coercion-and-register-bits) (`slotWrite` in `core/op/SlotAccess.kt`) and typed with the
component. An unknown index joins every element with the stored value. Only an arm that stores declares an
effect: `WEAK` into a tracked array, `STRONG` into an array the engine cannot name.

### `filled-new-array` (op 24, 35c) and `/range` (op 25, 3rc)

**SPEC** [BC]: "Construct an array of the given type and size, filling it with the supplied contents. The type
must be an array type. The array's contents must be single-word (that is, no arrays of long or double, but
reference types are acceptable). The constructed instance is stored as a "result" ... so the constructed
instance must be moved to a register with an immediately subsequent move-result-object instruction (if it is
to be used)."

The JVM has no counterpart instruction, so nothing is inherited per opcode. The runtime behavior is ART's
`DoFilledNewArray` (`runtime/interpreter/interpreter_common.cc`), whose throw sites are, in order: a dead
negative-length check (`VRegA_35c()`/`VRegA_3rc()` are unsigned fields), the resolution failure from
`ResolveVerifyAndClinit`, the `InternalError` for unsupported primitive components, and `OutOfMemoryError` from
`mirror::Array::Alloc`.

| Condition | Engine (`core/op/FilledNewArrayOp.kt`) | Tier |
|---|---|---|
| non-array type, or `[J`/`[D` | not decoded (`OpBuilder` refuses the instruction) | **SPEC**; ART's `CheckNewArray<kFilled>` hard-fails both, citing [BC] by URL |
| absent component class | `NoClassDefFoundError`, before the component-kind check | **DEVICE** (`Fx_FilledNewArrayAbsentType`) |
| `[Z`/`[B`/`[C`/`[S`/`[F` | `InternalError("Found type <PrettyDescriptor>; filled-new-array not implemented for anything but 'int'")`, e.g. `Found type byte; ...` | **ART** (`DoFilledNewArray`; the verifier soft-fails with `VERIFY_ERROR_FILLED_NEW_ARRAY`, comment "Fall back to interpreter to throw `InternalError`") |
| `int` element | its 32 bits | **ART** (`GetVReg`) |
| reference element | stored as read through `readsAsNullReference`, so a primitive register stores `null`; no `ArrayStoreException` | **ART** (`SetWithoutChecks`, `GetVRegReference`); a non-zero primitive element is **VERIFIER** (`nterp` stores the raw as a pointer; ART's `VerifyFilledNewArray` rejects it) |
| negative count, null array, bad index | unreachable: the count is the instruction's own unsigned field and there is no array or index operand | **SPEC** (instruction format) |
| allocation failure | not modelled | **ENGINE CHOICE** |

`[Z`/`[B`/`[C`/`[S`/`[F` are single-word and so appear legal per [BC], but ART implements only `int` and
reference components. The engine's message spells the component by `PrettyDescriptor` (`byte`, not `B`)
because ART's `ThrowNewExceptionF` formats `component_class->PrettyDescriptor()`.

dexlib2's `Opcode.canThrow()` is true for `FILLED_NEW_ARRAY` and false for `FILL_ARRAY_DATA`, which ART throws
from. The engine decides throw edges from each op's semantics and never from `canThrow()`.

### `fill-array-data` (op 26, 31t) and the `array-data` payload

**SPEC** [BC]: "Fill the given array with the indicated data. The reference must be to an array of primitives,
and the data table must match it in type and must contain no more elements than will fit in the array. That
is, the array may be larger than the table, and if so, only the initial elements of the array are set, leaving
the remainder alone."

The payload layout is **SPEC** ([BC] "fill-array-data-payload format"): `ident: ushort = 0x0300`,
`element_width: ushort`, `size: uint`, `data: ubyte[]`, little-endian, `(size * element_width + 1) / 2 + 4`
code units. Like the switch payloads, it is data that control flow must not reach ([CON] B22), and
`OpBuilder` decodes it into the instruction at build time.

ART's whole runtime behavior is `art::FillArrayData` (`runtime/entrypoints/entrypoint_utils.cc`): a `DCHECK` on
the ident, the null check, `AsArray()` with a `DCHECK(!array->IsObjectArray())`, the length check, and one
`memcpy` of `element_count * element_width` bytes. The JVM has no counterpart instruction, so its exceptions
are **ART** only.

| Condition | Engine (`core/op/FillArrayDataOp.kt`) | Tier |
|---|---|---|
| null array | `NullPointerException("null array in FILL_ARRAY_DATA")`, before the length check | **ART** |
| table longer than the array | `ArrayIndexOutOfBoundsException("failed FILL_ARRAY_DATA; length=<array length>, index=<element count>")`; nothing is stored | requirement **SPEC**, failure **ART** (the second number is `payload->element_count`) |
| array longer than the table | only the first elements are set; the tail keeps its values | **SPEC** + **ART** |
| unprovable array | fork: the fill (`STRONG`), the NPE, and the overrun when the payload is non-empty | **ENGINE CHOICE** |
| element width does not match the component, or an object array | ART copies bytes unchecked; the engine reads each element at the payload width and then as the component holds those bits (`F`/`D` reinterpret, `Z`/`B`/`C`/`S` narrow) | **VERIFIER** → **ENGINE CHOICE** |

`const/4 v0, 0` followed by `fill-array-data v0, ...` passes ART's verifier, which skips its checks for the null
constant, and throws NPE at runtime.

---

## Type operations

`instance-of`, `check-cast`, `const-class`, `new-array`, `filled-new-array`, and `new-instance` resolve their
type operand at decode time against the loaded class set (`TypeOperand` in `core/op/TypeOperand.kt`). An absent
type makes the op throw `NoClassDefFoundError` before it reads any register (`resolutionFailure` in
`core/GuestThrowables.kt`).

### `instance-of` (22c)

<a name="instance-of-22c--opcodeinstanceofopkt"></a>

**SPEC** [BC]: "Store in the given destination register 1 if the indicated reference is an instance of the given
type, or 0 if not." [BC] adds: "Since B must always be a reference (and not a primitive value), this will
always result in 0 being stored if C refers to a primitive type."

| Condition | Engine (`core/op/InstanceOfOp.kt`) | Tier |
|---|---|---|
| source reads as null | `0` | **INHERITED** ([JVMS] `instanceof`: "If objectref is null, the instanceof instruction pushes an int result of 0") + **ART**; a non-zero primitive source is **VERIFIER** (see [Reading a register as a reference](#reading-a-register-as-a-reference)) |
| absent type, including for a null source | `NoClassDefFoundError` | **DEVICE** (`Fx_InstanceOfAbsentType`, null source) |
| primitive type operand | `0` | **SPEC**; ART's verifier rejects the class, so this is reachable only on unverified input |
| provably an instance | `1` | **INHERITED** (subtype rules below) |
| unknown value whose tag satisfies the type, or nothing decides | `Unknown(Z)` | **ENGINE CHOICE**: an unknown may be null |

The op decides a value, never a path, so it never forks. The instance test is `castOutcome`
(`core/Provable.kt`): reads-as-null first, then a primitive target is never satisfied, then an unknown's tag is
an upper bound, then a known reference passes if its tag or its object's runtime class satisfies the target. A
class missing from the program makes the answer unprovable, never `false`.

Resolution precedes the null check, the opposite of [JVMS] ("If objectref is null ... Otherwise, the named
class ... is resolved"). ART's `INSTANCE_OF` handler in `interpreter_switch_impl-inl.h` calls
`ResolveVerifyAndClinit`, returns on failure, and only then reads `GetVRegReference(B())` (**ART**).

`instance-of` does not initialize the class. The whole `INSTANCE_OF` handler is a resolve, a failure return,
one `GetVRegReference`, and one `SetVReg(A(), (obj != nullptr && obj->InstanceOf(c)) ? 1 : 0)`; nothing in it
can initialize (**ART**). No fixture measures this opcode; `const-class`'s identical claim is measured.

### `check-cast` (21c)

<a name="check-cast-21c--opcodecheckcastopkt"></a>

**SPEC** [BC]: "Throw a ClassCastException if the reference in the given register cannot be cast to the
indicated type." This is one of the two exceptions [BC] names.

| Condition | Engine (`core/op/CheckCastOp.kt`) | Tier |
|---|---|---|
| register reads as null | passes | **INHERITED** ([JVMS] `checkcast`: "If objectref is null, then the operand stack is unchanged") + **ART** (`obj != nullptr` in `CHECK_CAST`) |
| absent type, including for a null register | `NoClassDefFoundError` | **DEVICE** (`Fx_CheckCastAbsentType`, null source) |
| provably not assignable | `ClassCastException("<object class> cannot be cast to <target>")`, `PrettyDescriptor` form; the message is omitted when the runtime class is unknown | **SPEC**; message **ART** (`ThrowClassCastException`) |
| assignability unprovable | fork: success and `ClassCastException` | **ENGINE CHOICE** |
| primitive type operand, null register | passes | **ART**: the null short-circuit precedes the test, so [BC]'s "will necessarily fail at runtime" is false for null |

`CHECK_CAST` resolves, returns on failure, reads `GetVRegReference(A())`, and conditionally throws; it writes no
register and initializes nothing (**ART**, whole handler body).

dexlib2 marks `check-cast` `SETS_REGISTER` because ART's verifier re-types the register on the success path:
`work_line_->SetRegisterType(inst->VRegA_21c(inst_data), res_type)`, under the comment "If this instruction
succeeds, we will "downcast" register vA to the type in vB" (**ART**, `method_verifier.cc`). The engine keeps the
raw and replaces the declared type with the cast type on the success arm. It skips the re-tag when the re-tag
would stop the register reading as a null reference: `Known(5, I)` passes the cast because it reads as null,
and re-tagged as a reference it would no longer (`narrowedOrUnchanged`). The verifier rejects a cast of a
non-reference register, so the skip loses no verified behavior (**VERIFIER**). A carried throwable is
partitioned into the part that passes and the part that throws.

### `const-class` (21c)

**SPEC** [BC]: "Move a reference to the class specified by the given index into the specified register. In the
case where the indicated type is primitive, this will store a reference to the primitive type's degenerate
class."

| Claim | Engine (`ConstClassOp` in `core/op/ConstOp.kt`) | Tier |
|---|---|---|
| absent class | `NoClassDefFoundError` | **DEVICE** (`Fx_ConstClassAbsentType`) |
| no `<clinit>` runs | the host `Class` is loaded with `Class.forName(name, false, loader)` semantics (`ProgramSnapshotResolver.loadClassConstant`) | **DEVICE** (below) + **ART** |
| two `const-class` of one class give one reference | one shared host class loader per program (`ClassIndex`), so the `Class` object is identical | **SPEC**-adjacent: [VER], "the result values from two `const-class` instructions on the same class will be the same reference" |

**DEVICE**(Tier A, `-Xint`) and DEVICE(Tier B, `nterp` default, API 36): `Fx_ConstClassDoesNotInitialize`
executes `const-class` on a witness class whose `<clinit>` prints, and returns `getName()` of the stored class.
No `CLINIT_RAN` line appears. Its control, `Fx_StaticFieldDoesInitialize`, triggers a second witness through
`sget` and prints `CLINIT_RAN Probe_ClinitWitnessB`.

The whole `CONST_CLASS` handler is `ResolveVerifyAndClinit`, a failure return, `SetVRegReference(A(), c)`, and
`return true` (**ART**). The `can_run_clinit = false` argument is not the evidence; `NEW_INSTANCE` passes the
same `false` and initializes in `AllocObjectFromCode` → `CheckObjectAlloc` →
`CheckClassInitializedForObjectAlloc` → `ClassLinker::EnsureInitialized`. [JLS] §12.4.1 ("A class or interface
will not be initialized under any other circumstance") corroborates. Initialization timing is not an
invariant on Android: `dex2oat`'s `CompilerDriver` may run `<clinit>` at build time inside a rollback
transaction (disabled for debuggable builds), so "no triggering opcode has executed" does not imply "`<clinit>`
has not run" on a device.

### Resolution failure is one family, tiered once

Every opcode that resolves a symbolic class, field, or method reference raises a linking error when resolution
fails. That it is a linking error is **INHERITED** per instruction: [JVMS] `checkcast`, `instanceof`, and
`anewarray` ("During resolution of the symbolic reference to the class, array, or interface type, any of the
exceptions documented in §5.4.3.1 can be thrown"), `ldc` for `const-class`, §5.4.3.2 for fields, and §5.4.3.3
for methods. Which error it is, and its ordering, are measured.

**DEVICE**(Tier A, `-Xint`, `art_standalone 0_git20251009-r2`) and DEVICE(Tier B, `nterp` default, API 36), each
against `Lcom/example/DefinitelyMissing;` with a present-type control:

| Fixture | Answer |
|---|---|
| `Fx_ConstClassAbsentType`, `Fx_CheckCastAbsentType`, `Fx_InstanceOfAbsentType`, `Fx_NewArrayAbsentType`, `Fx_FilledNewArrayAbsentType`, `Fx_NewInstanceAbsentType`, `Fx_SGetAbsentType`, `Fx_InvokeAbsentType` | `NoClassDefFoundError: Failed resolution of: Lcom/example/DefinitelyMissing;` |
| `Fx_InvokeMissingMethod` (absent method on present `String`) | `NoSuchMethodError: No static method definitelyNotAMethod()Ljava/lang/Object; in class Ljava/lang/String; or its super classes` |
| `Fx_InvokeMissingMethodNullReceiver` (null receiver and absent method) | `NoSuchMethodError` |

Every member of the family is a `LinkageError`, so a guest `catch (Exception)` does not catch it and
`catch (Error)` does. The `check-cast`/`instance-of` fixtures use a null source and the invoke fixture a null
receiver, so resolution precedes the null check. `sput` has no fixture; it resolves the same field reference
through the same §5.4.3.2 path as `sget`. ART's message for an absent class comes from
`ClassLinker::DoResolveType` (cause `ClassNotFoundException`); the engine raises the type and message without
the cause.

| Failure | Engine | Message |
|---|---|---|
| absent class | `NoClassDefFoundError` (`GuestThrowables.noClassDefFound`) | `Failed resolution of: <descriptor>` |
| absent field on a present class | `NoSuchFieldError` (`noSuchField`) | the field descriptor; ART's text is not modelled |
| absent method on a present class | `NoSuchMethodError` (`noSuchMethod`) | `No <kind> method <name><proto> in class <descriptor> or its super classes` |

The rule is resolution before the null check, not resolution before every check: `new-array` checks a
negative length first on the switch interpreter (see [`new-array`](#new-array-op-23-22c)).

The engine treats the loaded class set as the whole program, so a resolution failure is a definite throw, not a
fork.

### Subtype testing: entirely INHERITED

Dalvik documents no subtyping algorithm. [JVMS] `checkcast` states the rules (`instanceof` carries the same):

> "If S is a class type, then: If T is a class type, then S must be the same class as T, or S must be a
> subclass of T; If T is an interface type, then S must implement interface T. If S is an array type SC[] ...
> If T is a class type, then T must be Object. If T is an interface type, then T must be one of the interfaces
> implemented by arrays (JLS §4.10.3). If T is an array type TC[] ... TC and SC are the same primitive type [or]
> TC and SC are reference types, and type SC can be cast to TC by recursive application of these rules."

| Question | Answer (JLS §4.10.3) |
|---|---|
| `String[]` <: `Object[]`? | yes: `S[] >1 T[]` iff `S >1 T` |
| `int[]` <: `Object`? | yes: "If P is a primitive type, then: Object >1 P[]" |
| `int[]` <: `Object[]`? | no: both components must be references |
| `int[]` <: `Cloneable` / `Serializable`? | yes |
| Which interfaces do arrays implement? | exactly `Cloneable` and `java.io.Serializable` |

Array covariance is unsound by design, which is why `aput-object` checks at runtime. Primitive arrays are
invariant: `int[]` and `long[]` are unrelated, and neither is an `Object[]`.

**DEVICE**(Tier A, `-Xint`, `art_standalone 0_git20251009-r2`): the grid of candidates {`String[]`, `Object[]`,
`int[]`, `long[]`} by targets {`Object`, `Cloneable`, `Serializable`, `Object[]`}, for both `instance-of` and
`check-cast`, runs as `Fx_Subtype_*` plus `Fx_IntArrayIsCloneable`. Every cell matches the table, and the two
opcodes agree at every cell:

```
FIXTURE	Fx_Subtype_InstanceOf_IntArr_ObjectArr	OK	java.lang.Integer(0)
FIXTURE	Fx_Subtype_InstanceOf_LongArr_ObjectArr	OK	java.lang.Integer(0)
FIXTURE	Fx_Subtype_InstanceOf_StringArr_ObjectArr	OK	java.lang.Integer(1)
FIXTURE	Fx_Subtype_InstanceOf_LongArr_Cloneable	OK	java.lang.Integer(1)
FIXTURE	Fx_Subtype_InstanceOf_IntArr_Serializable	OK	java.lang.Integer(1)
FIXTURE	Fx_Subtype_CheckCast_IntArr_ObjectArr	OK	java.lang.Integer(0)
```

`HierarchySnapshot.isInstanceOf` (`core/HierarchySnapshot.kt`) answers `Object`, `Cloneable`, and
`Serializable` for every array without a class lookup, so obfuscated input that omits those classes still
answers. Android's `dx` `Merger.isPossiblyAssignableFrom`
(`platform/dalvik2/dx/src/com/android/dx/cf/code/Merger.java`) models arrays the same way ("array types are
assignable only to Serializable and Cloneable"); it is corroboration, not a specification.

### Where the verifier boundary is documented

[BC] never mentions verification. [CON] lists the static (`A*`) and structural (`B*`) constraints in two-column
tables (`Identifier`, `Description`) with no per-constraint JVMS mapping. The ones this file cites: A6 (branch
targets are opcodes in the method), A7/A8 (switch targets; sparse tables "consistent and sorted low-to-high"),
A17/A18 (type-index validity), B17 (no falling off the end of `insns`), B22 (payload unreachability). The
hard/soft failure split appears only in ART's `verifier_enums.h`. An unresolvable type is a soft failure, which is
why `check-cast` and `instance-of` on a missing class throw at execution instead of rejecting the class.

---

## Exceptions

### `throw` (op 27, 11x)

**SPEC** [BC]: "Throw the indicated exception."

`ThrowOp` (`core/op/ThrowOp.kt`) carries the register's value as it is, so its identity survives to the
handler, and the session normalizes it (`normalizeThrow`, `core/ThrowNormalization.kt`):

| Thrown value | Engine | Tier |
|---|---|---|
| reads as null (null, or any primitive) | a fresh `NullPointerException` with no message | **INHERITED** ([JVMS] `athrow`: "If objectref is null, athrow throws a NullPointerException instead of objectref") + **ART** (`THROW` calls `ThrowNullPointerException()`); a non-zero primitive is **VERIFIER** |
| a reference whose class is provably not a `Throwable` | a fresh `InternalError` | **ART** (below) |
| a `Throwable` reference | itself | **SPEC** |
| unknown or `Top` | a symbolic throwable constrained by the register's tag when the tag is a `Throwable` type; handler routing forks | **ENGINE CHOICE** |

ART's `THROW` reads `GetVRegReference(A())`, throws NPE for `nullptr`, and else, when
`DoAssignabilityChecks()` holds and the class is not a `Throwable`, throws `InternalError` "Throwing '%s' that
is not instance of Throwable" with the class descriptor. `DoAssignabilityChecks()` is
`!GetMethod()->SkipAccessChecks()`, and `kAccSkipAccessChecks` is set only for a class that verified with no
failure (`UpdateClassAfterVerification` in `runtime/class_linker.cc`), so the check is live in release ART for
the unverified input this engine analyses. [CON] B16 makes the case **VERIFIER**: "The A operand of a throw
instruction must be assignment-compatible with java.lang.Throwable."

The engine's `InternalError` message is `"Throwing '<register tag>' that is not an instance of Throwable"`.
It differs from ART's text in the word "an" and names the register's tag where ART names the object's class.

[VER] on structured locking: "The VM is permitted but not required to enforce "structured locking"
constraints ... This is not currently implemented." The engine raises no `IllegalMonitorStateException`.

### Handler routing

`resolveHandlers` (`core/HandlerResolution.kt`) walks the try block's handlers in declared order and stops at
the first catch-all. Each typed clause catches the part of the remaining throwable domain that is a subtype of
its type, and the rest continues to the next clause; whatever remains after the last clause leaves the method.
A known throwable type therefore takes exactly one handler, and an unknown type forks every feasible handler
plus the escape. A malformed handler table ends the path as `MALFORMED_INPUT`. Cross-frame routing and the
state each handler receives are [Exceptions](architecture/exceptions.md)'s.

### Exception message texts

Every throwable the engine constructs comes from `GuestThrowables` (`core/GuestThrowables.kt`), so every site
that raises one failure names the same type with the same text. A text that would name a value the engine does
not know (an unprovable index, length, or runtime class) is omitted rather than invented.

| Throwable | Text | Source |
|---|---|---|
| NPE, array read / write / length | `Attempt to read from null array`, `Attempt to write to null array`, `Attempt to get length of null array` | **ART** `ThrowNullPointerExceptionFromDexPC`; write measured (`Fx_AputNullArrayBadIndex`) |
| NPE, `fill-array-data` | `null array in FILL_ARRAY_DATA` | **ART** `FillArrayData` |
| NPE, `monitor-*` | `Attempt to do a synchronize operation on a null object` | `GuestThrowables.NullOperand.MONITOR`; not re-checked against ART here |
| NPE, field access | `Attempt to read from field '<type> <class>.<name>' on a null object reference in method '<method>'` (or `write to`) | **ART** `ThrowNullPointerExceptionForFieldAccess` |
| NPE, invoke receiver | `Attempt to invoke <kind> method '<method>' on a null object reference` | **ART** `ThrowNullPointerExceptionForMethodAccess` |
| NPE, `throw` of null | no message | **ART** `ThrowNullPointerException()` |
| AIOOBE | `length=<n>; index=<i>` | **ART** + **DEVICE** |
| AIOOBE, `fill-array-data` | `failed FILL_ARRAY_DATA; length=<n>, index=<count>` | **ART** |
| `ArrayStoreException` | `<value class> cannot be stored in an array of type <array class>` | **DEVICE** (`Fx_AputObjectIncompatible`) |
| `ClassCastException` | `<object class> cannot be cast to <target>` | **ART** `ThrowClassCastException` |
| `NegativeArraySizeException` | the length in decimal | **ART** + **DEVICE** |
| `NoClassDefFoundError`, resolution | `Failed resolution of: <descriptor>` | **DEVICE** |
| `NoClassDefFoundError`, earlier init failure | `PrettyDescriptor` of the class | **ART** `ThrowEarlierClassFailure` |
| `NoSuchMethodError` | `No <kind> method <name><proto> in class <descriptor> or its super classes` | **DEVICE** (`Fx_InvokeMissingMethod`) |
| `IncompatibleClassChangeError`, default conflict | `Conflicting default method implementations <class>-><name><proto>` | engine text; ART's `ThrowInvocationTimeError` text is not modelled |
| `ArithmeticException` | `divide by zero` | `ThrowArithmeticExceptionDivideByZero`, source read |

`PrettyDescriptor` spells `I` as `int`, `Lfoo/Bar;` as `foo.Bar`, and `[[Lfoo/Bar;` as `foo.Bar[][]`.

Messages vary across runtimes. [RT]: "ART provides expanded exception detail for java.lang.ClassCastException,
java.lang.ClassNotFoundException, and java.lang.NullPointerException." A message is never evidence of fidelity
by itself; compare exception types when judging behavior, and treat the texts as ART's current spelling.

### `monitor-enter` / `monitor-exit`

`core/op/MonitorOp.kt`. Lock ownership is not modelled; a feasible operation continues and records a `WEAK`
effect. The operand is read as a reference: a register that reads as null throws NPE, and an unknown one forks
the continuation and the NPE (**ART**, `MONITOR_ENTER`/`MONITOR_EXIT` use `GetVRegReference`).

`monitor-exit` is the only instruction with a pc carve-out, **SPEC** [BC]: "If this instruction needs to throw
an exception, it must do so as if the pc has already advanced past the instruction." The engine looks up
`monitor-exit`'s handlers at the next instruction's address (`ExceptionPc.AFTER_INSTRUCTION`) and
`monitor-enter`'s at its own.

---

## Class initialization

No initialization claim can be SPEC: [BC] never mentions initialization.

### Triggers

| Opcode | Initializes | Tier |
|---|---|---|
| `const-class`, `check-cast`, `instance-of` | no | `const-class` **DEVICE** (`Fx_ConstClassDoesNotInitialize`); all three **ART** (whole handler bodies) |
| `new-array`, `filled-new-array` | no | **ART** |
| `new-instance` | yes, before allocation | **DEVICE** (`Fx_NewInstanceDoesInitialize`, Tier A and Tier B, with no `<init>` call); **ART** (`AllocObjectFromCode` → `EnsureInitialized`) |
| `sget*` | yes, the field's declaring class | **DEVICE** (`Fx_StaticFieldDoesInitialize`, both tiers) |
| `sput*` | yes, the field's declaring class | **INHERITED** ([JVMS] §5.5 `putstatic`); no fixture |
| `invoke-static` | yes, the resolved method's declaring class, before the call | **ART** at commit `6484611f`: `ArtMethod::NeedsClinitCheckBeforeCall` (`runtime/art_method.h`) is true only for a static method that is not a class initializer; `PerformCall` (`runtime/common_dex_operations.h`) passes the callee's declaring class to `ClassLinker::EnsureInitialized`; [JVMS] §5.5 corroborates |
| `invoke-virtual`, `-interface`, `-super`, `-direct` (including `<init>`) | no | **ART**, same predicate: these methods are not static |
| explicit `invoke-static <clinit>` | no | **ART**, same predicate excludes the class initializer |

An absent class throws its resolution failure before any trigger, because an absent class cannot be
initialized. A static field resolves to its declaring class, and that class is the one initialized
(`StaticFieldLookup.Resolved.definingClass`); `classInitTarget` in `core/ClassInit.kt` picks the resolved
method's declaring class for `invoke-static`.

### Lifecycle

Each path carries its own lifecycle (`ClassStore` in `core/ClassInit.kt`):
`Uninitialized → Initializing → {Initialized | Failed}`. Both terminal states are terminal by construction.
**INHERITED** from [JLS] §12.4.2:

- `Initializing` is the recursion guard (step 3): a `<clinit>` that re-triggers its own class sees
  `Initializing` and proceeds without re-entering. A static read mid-`<clinit>` sees the prepared value.
- `Failed` is terminal (steps 5 and 12): the initializer never runs again.

The state is per path because a class initialized on only some paths must stay uninitialized on the others.
Incompatible lifecycle maps are never joined: `INITIALIZED` and an untriggered class stay on separate caller
paths.

**Entry state.** `VmServices.newSession` takes an `EntryClassState` that sets what a class the path has not
triggered reads as:

- `ARBITRARY`, the default and the state Simplify analyses every method in: the entry is reached from arbitrary
  program state, so an untriggered class is `Unknown`. It may be initialized, with any value in each non-final
  static, or not yet initialized.
- `FRESH_PROCESS`: the entry is the first code a new process runs, so an untriggered class is `Uninitialized`.
  Class-initialization semantics tests and comparisons against a device run that starts at process start use
  it.

In both, a run whose entry is a `<clinit>` finds that class `Initializing` over its prepared statics, because
nothing can initialize a class before its own initializer runs.

**A trigger on an `Unknown` class** may or may not run `<clinit>` on a device. The engine (**ENGINE CHOICE**):

- runs a guest `<clinit>` exactly as for an `Uninitialized` class, so the initializer's effects are recorded on
  the trigger and its throw paths remain;
- after a successful return, marks the class `Initialized` and makes every non-final static cell that the
  initializer changed, in any class, `Unknown`: the class may have been initialized before the entry, and the
  field written since;
- keeps the initializer's value in a `final` static, because only `<clinit>` writes one. JNI and ART reflection
  (`Field.set` after `setAccessible(true)`) can write a final static; the engine treats final as fixed, and
  reads an object a final static refers to as the initializer left it unless the path writes it;
- settles a class with no runnable initializer (none declared, or a framework class) with every non-final
  static `Unknown`, even when the dex encodes a literal for it.

Two cases stay outside this model. A class whose initialization failed before the entry throws
`NoClassDefFoundError` on a device; the engine runs the initializer and reports its outcome. And a write the
initializer makes to an object that existed before the trigger stays known, as if the initializer ran at the
trigger.

**Preparation** ([JVMS] §5.4.2) seeds every declared static before `<clinit>` runs: a dex-encoded literal
(`.field static x:I = 0x42`) is that literal, otherwise the type default when the initializer runs or the class
has none, otherwise `Unknown` (a framework initializer that does not run may compute anything). Under
`ARBITRARY` entry, a non-final static of a class settled without running an initializer is `Unknown` instead.

**Order.** A trigger walks the superclass chain and runs the topmost class that still needs initialization;
the trigger asks again when that `<clinit>` returns, so a superclass always runs before its subclass.
Superinterfaces are not walked: [JLS] §12.4.1 initializes a superinterface only when it declares a default
method, and the engine does not model that case.

**Failure** (**ART** at commit `6484611f`, `runtime/class_linker.cc`):

- First failure: `WrapExceptionInInitializer` keeps an escaping `Error` as itself and wraps any other
  throwable in a fresh `ExceptionInInitializerError`; the class stores that error.
- Later trigger: `InitializeClass` finds the class erroneous and `ThrowEarlierClassFailure` throws a fresh
  `NoClassDefFoundError` whose message is the class's `PrettyDescriptor` and whose cause is the stored error.
- Failed superclass: the subclass is marked erroneous with the error its superclass's initialization raised,
  so a subclass trigger throws the superclass's `NoClassDefFoundError`.

`ClassInitFailureBehaviorTest` covers each trigger kind and a failed superclass.

**A static initializer with no code is malformed input.** `DexFileVerifier::CheckMethodAccessFlags`
(`libdexfile/dex/dex_file_verifier.cc`) rejects a constructor marked `native` or `abstract` (**ART**), so no
device initializes such a class. A trigger that reaches one ends its path as `MALFORMED_INPUT`
(`ClassInitHostileInputTest`).

**Framework classes** (bundled, non-application classes) are never initialized by running their `<clinit>`:
the class is marked initialized and the trigger records a `STRONG` effect when the class declares an
initializer whose effects are observable (**ENGINE CHOICE**). An initializer is unobservable when its class is
listed in `safe_classes.cfg` and its `<clinit>` is not in `unsafe_methods.cfg`: the listing vouches that no
method of the class affects or reads process state, and the host already runs that `<clinit>` whenever an
authorized call first touches the class. Such a skipped initializer records nothing. A final static holds its
dex literal, or `Unknown` that a static read may project from the host class when the reflection policy allows
it. A non-final static is `Unknown` under `ARBITRARY` entry, and holds its literal or a projectable `Unknown`
under `FRESH_PROCESS`. `new-instance` of a framework class still yields a tracked uninitialized object.

**A self-contained initialization is not the trigger's effect** (**ENGINE CHOICE**). Effects inside a guest
`<clinit>` (and everything it calls) count for the triggering instruction only when they reach beyond the
initialization. A `WEAK` write stays confined when it writes only initializer-only statics of the initializer's
own class and objects allocated since the initialization started (a host call: only such objects' host state
changed). Every other effect (another class's static, a pre-existing object, an opaque or `STRONG` call, an
observable skipped framework initializer) reaches the trigger as before. An initialization that throws or
aborts gives its confined effects to the trigger too, since a later trigger does not run it again. So a call
whose only effect is a self-contained initialization reports `NONE` and may be removed; removing it only
delays that class's initialization to its next trigger, which runs the same initializer to the same result.
`InitializerEffectAttributionTest` covers each case.

---

## Fields

### Resolution

A dex field reference is `(class, name, type)`, so two fields differing only in type are distinct. Field
identity is the fully-qualified descriptor of the declaring class, so a shadowed field never shares a cell
with the field it shadows (`ClassIndex.fieldDescriptorOf`).

| Reference | Search (`core/ProgramSnapshotResolver.kt`) | Tier |
|---|---|---|
| static (`sget*`/`sput*`) | the class, then its direct superinterfaces recursively, then its superclass | **INHERITED** ([JVMS] §5.4.3.2) + **ART** (`Class::FindStaticField`) |
| instance (`iget*`/`iput*`) | the superclass chain only; interfaces declare no instance fields | **INHERITED** ([JVMS] §5.4.3.2) |

Resolution names the declaring class, so `sget v0, Lchild;->parentField:I` reads `Lparent;->parentField:I`'s
cell and every spelling of one inherited field shares a cell. A class absent anywhere in the search is
`NoClassDefFoundError`; a present search with no match is `NoSuchFieldError`. Instance-field resolution and
both errors precede the receiver null check. Instance-field ops trigger no initialization; static ones trigger
the declaring class after resolution (`SFieldOp` preamble).

### Static fields

A static cell exists on a path from the trigger that prepares its class. `core/op/SFieldOp.kt`:

| Read of | Value | Tier |
|---|---|---|
| a static the path wrote | the written value | **ART** |
| a final static | its literal, or the value its class's initializer leaves | **INHERITED** ([JVMS] §5.4.2, [JLS] §12.4.2) + **ENGINE CHOICE** (final is fixed) |
| a non-final static of a class first triggered as `Unknown`, not written by the path | `Unknown` | **ENGINE CHOICE**, see [Lifecycle](#lifecycle) |
| a non-final static of a class first triggered as `Uninitialized` | its prepared value or the initializer's write | **INHERITED** ([JVMS] §5.4.2) |

Field reflection (`Field.get` on a static) reads the same cell.

### Instance fields

`new-instance` seeds every declared instance field in the object's superclass chain with its type default
([JLS] §12.5). `core/op/IFieldOp.kt`:

| Receiver | `iget*` | `iput*` | Tier |
|---|---|---|---|
| reads as null | NPE with ART's field-access message | same | **INHERITED** ([JVMS] `getfield`/`putfield`) + **ART**; non-zero primitive **VERIFIER** |
| unknown | fork: `Unknown(fieldType)` and the NPE | fork: a `STRONG` continuation and the NPE | **ENGINE CHOICE** |
| tracked object with the cell | the cell's value | write the cell, `WEAK` | **ART** |
| non-heap reference, or object without the cell | `Unknown(fieldType)` | `STRONG`, no write | **VERIFIER** → **ENGINE CHOICE**: no guest exception is invented |

A reference-typed field read through a cell that holds primitive bits reads `null` (`fieldRead` in
`core/op/SlotAccess.kt`), and a volatile read records `WEAK`.

### Stores narrow to the field width

`iput`/`sput` `-boolean`, `-byte`, `-char`, and `-short` keep only the field's width through
[`Coerce.narrowStore`](#coercion-and-register-bits) (**ART**, `GetFieldValue`'s `static_cast`s). `iput-object` and
`sput-object` store a register that reads as null as `null`. A static write records `WEAK`.

---

## Invocation and dispatch

### Resolution per kind

**SPEC** [BC], per `invoke-kind`:

| Kind | [BC] | Engine (`ProgramSnapshotResolver.resolveInvocation`) |
|---|---|---|
| `invoke-virtual` | "a normal virtual method, which is a method that isn't static, private or a constructor" | select from the receiver's tracked runtime class; unknown receiver: see [dispatch](#dispatch-on-an-unknown-receiver) |
| `invoke-super` | "the closest superclass's virtual method ... if the method_id refers to an interface method, invoke-super is used to invoke the most specific, non-overridden version of that method defined on that interface" | look up from the calling class's superclass; for an interface `method_id`, the most specific inherited default |
| `invoke-direct` | "a private instance method or a constructor" | the method the referenced class declares; never redispatched |
| `invoke-static` | "a static method (which is always considered a direct method)" | symbolic resolution; no receiver |
| `invoke-interface` | "an object whose concrete class isn't known, using a method_id that refers to an interface" | as `invoke-virtual` |

Symbolic method lookup (`lookupMethod`) follows [JVMS] §5.4.3.3: the class and its superclasses, then the
maximally-specific superinterface methods, excluding private and static interface methods. One non-abstract
maximal method is the answer; with none, an abstract maximal method answers. `<clinit>` is not inherited and is
looked up only on its own class. The lookup rule is per kind; the engine performs no legality or access check,
so an `invoke-virtual` naming a private method or `<init>` (SPEC-invalid per the table) resolves and runs.

### Order of checks

`ExecutionSession.planInvoke` checks, in this order:

1. Resolution: an absent class throws `NoClassDefFoundError`, an absent method `NoSuchMethodError` (**DEVICE**,
   see [Resolution failure](#resolution-failure-is-one-family-tiered-once)).
2. The receiver: a non-static call whose receiver reads as null throws NPE with ART's method-access message,
   before the body runs (**INHERITED**, [JVMS] `invokevirtual`/`invokeinterface`/`invokespecial`; **ART**). A
   receiver that is not provably null proceeds without an NPE arm.
3. A default-method conflict: more than one maximally-specific non-abstract interface method throws
   `IncompatibleClassChangeError` (**INHERITED**, [JVMS] §5.4.3.3 and `invokeinterface`; **ART** links a
   conflict method whose invocation throws after the null check, `ArtMethod::ThrowInvocationTimeError`). When
   the selection on an exact receiver class lands on an abstract method, the call throws `AbstractMethodError`
   after the null check (**ART**, `ThrowInvocationTimeError`); against an unknown receiver the call stays opaque.
4. Class initialization for `invoke-static` (see [Triggers](#triggers)).
5. Backend selection, in order: emulators, application bytecode, policy-authorized reflection, framework
   bytecode (`Invoker`).

### Argument binding

`Invoker.bindValue` applies two transforms to each argument, by the callee's declared parameter type:

- A known `I`-typed value whose 32 bits are zero, passed to a reference parameter, becomes `null`
  (**SPEC**, `(Object) null == (int) 0`).
- A known value passed to a primitive parameter is re-read at the parameter's width: `Z` is non-zero, `B`/`C`/`S`
  narrow, `I`/`J`/`F`/`D` read the bits through `Coerce`. A value with no reading at that width becomes
  `Unknown`. `const/4 v0, 0x0` is the same bits for every narrow type, so the parameter type decides.

A call whose register count does not match the resolved method's argument words is executed as an opaque call.

### Dispatch on an unknown receiver

When the receiver's class is not tracked, `resolveInvocation` counts the instantiable application subtypes'
selected implementations (**ENGINE CHOICE** under a closed-world assumption over the loaded class set):

| Situation | Engine |
|---|---|
| the world is not provably closed: an application class has an absent ancestor, or the program shows dynamic class loading | ambiguous: opaque call |
| no application candidate | the referenced method |
| exactly one candidate, and framework subtypes cannot receive the call or the candidate is the referenced method | that candidate |
| two or more candidates | ambiguous: opaque call, with the candidate names recorded |

Final classes, arrays, and primitives are always closed. A class absent from the analysis, or one loaded at
runtime, would make the single-candidate answer wrong; the closed-world conditions above are the engine's
guard against both.

An opaque call records `STRONG`, writes a typed `Unknown` non-void result, and collapses the fields and
elements of every tracked object reachable from its arguments. Call completion, callee-to-caller state merge,
and the call-depth limit (`Limits.maxCallDepth`, default 20, aborting as `CALL_DEPTH`) are
[Execution engine](architecture/execution-engine.md)'s.

`move-result` (non-object) after a reference-returning call is verifier-invalid; the engine moves the value
unchanged (**VERIFIER** → **ENGINE CHOICE**).

---

## Program loading

These rules decide which classes are in the loaded set, which every resolution above consults.

| Input | Engine | Tier |
|---|---|---|
| more than one class-path DEX (multi-DEX) | rejected with `ProgramInputException` (`ClassIndex` import) | **ENGINE CHOICE**: the engine analyses one DEX |
| a class with a method ART's verifier rejects | kept in the program for output, left out of the loaded set; every use raises `NoClassDefFoundError` (`InitialProgramImporter.passesVerification`) | **ART** throws `VerifyError` on first use and `NoClassDefFoundError` after; the engine answers `NoClassDefFoundError` every time, including for `const-class`, `instance-of`, and `check-cast`, which resolve the class on ART without verifying it |
| a class whose superclass or interface chain reaches itself | left out of the loaded set (`HierarchySnapshot.from`); every method, field, and type reference to it throws `ClassCircularityError` with ART's text (`GuestThrowables.unresolvedClass`); `Class.forName` answers `ClassNotFoundException` | **ART**: `ClassLinker::LoadSuperAndInterfaces` / `EnsureResolved` throw `ClassCircularityError`; `Class_classForName` wraps lookup failures. A class below a cycle is treated as having a missing ancestor (ENGINE CHOICE; ART propagates `ClassCircularityError`) |
| duplicate application class | the first definition wins, with a warning | **ENGINE CHOICE** |

---

## Reflection and host execution

A call the application bytecode does not implement may run on the host through a backend (`Invoker`):
emulators for specific framework methods, `ReflectBackend` for methods the reflection policy authorizes
(`core/ReflectionPolicy.kt`), and framework bytecode. A policy denial is final and makes the call opaque; it
does not fall back to another backend. Authorization does not imply purity: a reflected call records `STRONG`
unless the policy proves otherwise.

`ReflectBackend` finds the host method by exact JVM descriptor and calls it directly, so `Method.invoke`'s own
failures keep their types. The guest's reflective field reads (`FieldGetEmulator`) apply ART's access rule:
ART's `VerifyAccess` (`runtime/reflection.cc`) admits a private member only to its declaring class and grants
nestmates nothing (**ART**), unlike the JVM.

`const-class` and reflective class loads go through one host class loader per program, loading without
initialization, so `const-class` identity holds program-wide.

---

## Engine choices for verifier-excluded input

Each row is a condition the verifier forbids or no document addresses. None is a claim about Dalvik. The
engine's rule throughout: an unprovable value is `Unknown`, an unprovable effect is `STRONG`, and an unprovable
successor set explores every feasible successor.

| Condition | Engine choice | Reason |
|---|---|---|
| a non-zero primitive read as a reference | reads as null | matches the switch interpreter; `nterp` crashes ([two interpreter tiers](#art-has-two-interpreter-tiers-that-read-a-register-differently)) |
| sparse switch table not strictly ascending, known selector | union of ART's binary-search answer and every matching case | ART's verifier rejects the table (`key <= last_key`); one runtime's search is not a fidelity target |
| a known reference as a switch selector | fall through, no fork | the verifier requires an integral selector; a heap address is none of the keys |
| `if-lt`/`if-ge`/`if-gt`/`if-le` on two references | identical pointers decide; distinct pointers fork | integral-only in ART's verifier; a pointer's numeric value is unknown |
| `if-eq`/`if-ne` on a live pointer against a non-zero integer | fork | a pointer is provably non-zero, not provably different from a given integer |
| `aput*` whose value and component disagree in shape | definite `ArrayStoreException`, before other checks | ART reads mis-scaled memory; refusing keeps a type lie out of the element |
| array op on a provably non-null value with no tracked array | no exception, unknown element or length | ART `DownCast`s unchecked |
| `check-cast` narrowing that would stop a register reading as null | not applied | the verifier rejects a cast of a non-reference register |
| `fill-array-data` width mismatch or object array | read each element at the payload width, store as the component holds the bits | ART `memcpy`s unchecked |
| `iput` into a non-heap reference or an object without the cell | `STRONG`, no write, no exception | no device behavior to copy |
| `invoke-virtual` of a private method or `<init>` | resolve and run it | lookup is per kind; the engine has no access check |
| `move-result` (non-object) after a reference result | move the value | refusing would end a path for an unrelated reason |
| unknown receiver with several implementations | opaque call, candidates recorded | forking every override explodes; the closed-world single-candidate case dispatches |

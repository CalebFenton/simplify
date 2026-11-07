package org.cf.smalivm.core.op

import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.ProvableInt
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.provableInt

/**
 * Which dispatch rule the payload's key array obeys. **dexlib2 normalizes both kinds into one
 * `(key, offset)` list** -- `SwitchPayload.getSwitchElements()` is the only accessor there is, with no
 * `getFirstKey()` and no `getTargets()` -- so the opcode is the ONLY thing left that says which rule
 * applies, and the two rules genuinely differ (see [SwitchOp.fallsThroughOn]). That normalization is
 * convenient everywhere else and is exactly what made the engine being replaced dispatch both kinds
 * through one `HashMap.containsKey` (`opcode/SwitchPayloadOp.kt:34`).
 */
internal enum class SwitchDispatch { PACKED, SPARSE }

/**
 * `packed-switch` and `sparse-switch` (`31t`), **with their payload tables decoded at build time and
 * never executed.**
 *
 * **The payload is DATA, and that is SPEC stated three ways.** [BC] writes the operand as a *"signed
 * **"branch"** offset to table data pseudo-instruction"* (scare quotes in the source); [BC]'s "General
 * design" says payload pseudo-instructions *"must never be encountered during the normal flow of
 * execution"*; and **[CON] B22** makes it normative: *"The `packed-switch-data`, `sparse-switch-data`,
 * and `fill-array-data` pseudo-instructions must not be reachable by control flow."* dexlib2 agrees
 * structurally -- both payload opcodes report `canContinue = false`. So this op resolves the whole table
 * in `OpBuilder` and returns real [Transition]s; nothing ever decodes a payload address, and
 * `ExecutionGraphView.isPayload` is how a consumer tells that apart from dead code.
 *
 * The engine being replaced does the opposite, through THREE out-of-band channels and a magic constant:
 * it writes the selector into the result register and the fall-through address into a
 * pseudo-return-address slot (`opcode/SwitchOp.kt:21`, `:23`), jumps to the payload (`:24`), has
 * `SwitchPayloadOp` read both back off the PARENT node's state through an ancestor walk, recovers the
 * switch's own address by subtracting a hardcoded `3` (`opcode/SwitchPayloadOp.kt:26`, the constant at
 * `:69`), and is special-cased **by class** in the scheduler to suppress a fall-through successor dexlib2
 * says exists -- `if (opcode.canContinue() && current.op !is SwitchOp)` (`VirtualMachine.kt:340`), a type
 * check and not a string comparison, which is a *stronger* coupling than a name test rather than a weaker
 * one: it is a compile-time dependency from the scheduler onto a concrete `Op` subclass. Decoding at build
 * time removes all four.
 *
 * **Fallthrough on no match is SPEC** -- both [BC] rows end *"or fall through to the next instruction if
 * there is no match"* -- and note that **a Dalvik payload carries no `default` entry at all**, unlike the
 * JVM's `tableswitch`/`lookupswitch`. The fall-through edge is structural and is always
 * `switchAddress + 3`, because `31t` is three code units; it is carried as [fallThroughAddress] rather
 * than recomputed, so that dedup against [Transition.Continue] is expressible.
 *
 * **Targets are absolute addresses, resolved against the SWITCH and not the table** -- **SPEC**, stated
 * in both payload tables: *"The targets are relative to the address of the switch opcode, not of this
 * table."* [Case.targetAddress] therefore makes a decoded op **revision-bound**, exactly as [IfOp]'s
 * `targetAddress` does: Plans 13-15 must REBUILD ops after a bytecode edit rather than reuse them.
 * `docs/architecture/graph-optimizer-contract.md:248` records that coupling breaking the old editor --
 * *"Branch and payload targets remain cached from the old revision because op recreation is disabled"*.
 *
 * **Throws nothing.** `CAN_CONTINUE` only, no `CAN_THROW`: every operand is a register read and a table
 * lookup.
 *
 * **Side effects: NONE, and `recordSideEffect` is called nowhere in this file.** A switch chooses a
 * successor and touches nothing outside its own [Frame] -- unlike the oracle, which writes two registers
 * to smuggle state to the payload op. A spurious `STRONG` here would be silent and expensive:
 * `ExecutionGraph.methodSideEffectLevelOverExploredPaths` maxes over addresses, and control-flow
 * flattening -- the dominant obfuscation this tool exists to undo -- is one giant switch inside a
 * `while (true)`. Pinned by `core/op/SwitchOpTest.aSwitchDeclaresNoSideEffectOnEveryRow`.
 *
 * **Assigns nothing.** The oracle's `registersAssignedCount = 2` (`opcode/SwitchOp.kt:16`) is honest
 * about its two out-of-band writes rather than wrong, and must not be "restored to parity" here:
 * `ExecutionGraph.availableRegisters` is purely static, so [registersRead] and [registersAssigned] are
 * the sole guarantors of a correct free-register answer (`docs/architecture/state-model.md:80` records
 * the oracle's counts as *"only initial-capacity hints ... not effect contracts"*).
 */
internal class SwitchOp(
    address: Int,
    override val name: String,
    private val selectorRegister: Int,
    private val cases: List<Case>,
    private val fallThroughAddress: Int,
    private val dispatch: SwitchDispatch,
) : Op(address) {

    /**
     * One table entry: a key and the **absolute address** its offset resolved to.
     *
     * **An ordered list of these rather than the `Map<Int, Int>` this task was specified with, and the
     * reason is that a map cannot express what the same specification requires.** Two things need the
     * raw key ARRAY: [ART]'s sparse dispatch is a binary search over it in payload order, and a
     * non-strictly-ascending table may repeat a key -- which a map silently collapses to whichever entry
     * came last. Both are unreachable in verified input and both are reachable in obfuscated input, which
     * is the input this engine is for. A `LinkedHashMap` would preserve order but still lose duplicates.
     *
     * **Both halves are pinned, and the duplicate half needed its own test** —
     * `core/op/SwitchOpTest.aDuplicateKeyKeepsBothArmsWhichAMapCouldNotRepresent`, on a fixture measured
     * to be assemblable (`.sparse-switch { 0x1 -> :a; 0x1 -> :b }`). Building `cases` through a
     * `LinkedHashMap` passes every OTHER test in the suite and fails that one, which is why prose was not
     * enough: the order half alone was covered.
     */
    internal class Case(val key: Int, val targetAddress: Int)

    /** The selector, and nothing else. Real, not a capacity hint: see the class KDoc. */
    override val registersRead: Set<Int> = setOf(selectorRegister)

    override fun execute(frame: Frame): OpExecution = OpExecution.of(
        successorsFor(frame.readRegister(selectorRegister)).map {
            // The fall-through is spelled `Continue` rather than `Jump(fallThroughAddress)`, so the
            // scheduler resolves it the way it resolves every other op's fall-through
            // (`ExecutionSession.planFor` -> `view.nextAddress`). The two are the same address by
            // construction; using `Continue` keeps this op from asserting a second opinion about it.
            if (it == fallThroughAddress) OpArm.Continue() else OpArm.Jump(it)
        },
    )

    /**
     * Every feasible successor address, **fall-through first, then targets in payload order**, deduped.
     *
     * **The rows APPLY the provability rule and do not restate it** -- see
     * [org.cf.smalivm.core.provableInt], which owns "what are this register's low 32 bits?" for both this
     * op and [IfOp]. A switch reads its selector as 32 bits exactly as an `if` reads an operand, so the
     * rows are the same rows and there is one implementation of them.
     *
     *  1. **The bits are exactly known** ([ProvableInt.Exactly]) -> dispatch, which yields one target, or
     *     the fall-through, or -- on a malformed sparse table only -- both. This subsumes the
     *     definitely-**null** selector without a row of its own: [BC]'s *"In terms of bitwise
     *     representation, `(Object) null == (int) 0`"* makes a null's bits provably `0`, so
     *     [org.cf.smalivm.core.provableInt] answers `Exactly(0)` and a table containing key `0` dispatches
     *     to it. The oracle gets that wrong in the feasibility direction (registry D7b).
     *  2. **The bits are a live pointer** ([ProvableInt.NonZero]) -> the fall-through alone. Key `0` is
     *     *provably* excluded, by the same [BC] sentence read the other way. The non-zero keys are an
     *     engine **CHOICE**: the lattice does not hold a pointer's numeric value, so this op answers "a
     *     reference's bits are a heap address and are none of this table's keys" -- true of any device
     *     running real code, and unreachable in verified input, since [ART]'s verifier requires an
     *     integral selector. Recorded as a choice in `docs/DALVIK-SEMANTICS.md`. The alternative (fork
     *     every arm) invents live branches on the corpus's own fixture: `packedSwitch()V` with a
     *     reference selector would report three `return-void`s live that no device reaches.
     *  3. **Unprovable** ([ProvableInt.Unprovable]: [Value.Unknown], [Value.Top], or a `Float`/`Double`
     *     raw whose 32 bits are not the number the host box holds) -> the fall-through **plus** every
     *     distinct target. Spec §2.2: an unprovable successor set explores MORE, never fewer.
     *
     * **`size == 0` needs no row and deliberately has none.** With no cases, row 1 matches nothing and
     * takes the fall-through and row 3 forks over zero targets, so both answer `listOf(Continue)` --
     * which is what **[ART]** does, in two functions with two different comments:
     * `// Empty packed switch, move forward by 3 (size of PACKED_SWITCH).`
     * (`runtime/interpreter/interpreter_common.h:453`, quoted in full) guarding `return 3;` at `:454`,
     * and `// Return length of SPARSE_SWITCH if size is 0.` at `:480` guarding
     * `if (size == 0) { return 3; }` at `:481-483`. Pinned by
     * `core/op/SwitchOpTest.anEmptyTableFallsThroughWhateverTheSelectorIs`.
     *
     * **Dedup is over distinct ADDRESSES with the fall-through INCLUDED**, which is why this returns a
     * set built fall-through-first rather than a list of `Jump`s. A case offset of `+3` names the
     * fall-through -- legal, and what an obfuscator emits for an opaque predicate -- and without the
     * dedup it would yield two `SuccessorSpec`s at one address: two nodes, two `Outcome`s, and twice that
     * address's share of the session-global visit budget.
     *
     * **Fall-through FIRST**, matching [IfOp], because transitions are pushed onto the LIFO frontier
     * reversed and so the first declared one is the first path explored -- observable through
     * `graph.outcomes`' order and the node's child list. It deliberately does NOT match the oracle, which
     * appends it last (`opcode/SwitchPayloadOp.kt:29-31`), and that is not a parity break worth avoiding:
     * the oracle's unknown-selector children come out in `HashMap` iteration order (`:57-65` walks
     * `targetKeyToOffset.values`), so **its switch fork order is not well defined at all** and can never
     * be a parity requirement. A later plan must not try to make it one.
     */
    private fun successorsFor(selector: Value): Set<Int> = when (val bits = provableInt(selector)) {
        is ProvableInt.Exactly -> dispatchOn(bits.bits)
        ProvableInt.NonZero -> setOf(fallThroughAddress)
        ProvableInt.Unprovable -> linkedSetOf(fallThroughAddress) + cases.map { it.targetAddress }
    }

    /**
     * The successors of a **decided** selector: every case whose key matches, plus the fall-through when
     * the runtime's own lookup would miss.
     *
     * Written this way -- "which cases match" and "does it fall through" as two questions -- because the
     * two dispatch rules differ in exactly one of them ([fallsThroughOn]) and agree on the other. For a
     * well-formed table this is one address either way.
     */
    private fun dispatchOn(key: Int): Set<Int> {
        val matching = cases.filter { it.key == key }.map { it.targetAddress }
        return if (fallsThroughOn(key)) linkedSetOf(fallThroughAddress) + matching else LinkedHashSet(matching)
    }

    /**
     * Whether the runtime's own lookup misses [key] -- **the one place packed and sparse differ**, and
     * both halves are **[ART]**-tier, in `runtime/interpreter/interpreter_common.h`.
     *
     * **PACKED: `index = test_val - first_key` in signed 32-bit, taken when `0 <= index < size`** (`:461`
     * is the subtraction, `:462-467` the range test and its fall-through), **and it WRAPS.** With a
     * `first_key` near `Int.MAX_VALUE` the subtraction overflows and a selector that is *nominally* out of
     * range lands in the table; ART's verifier exists to make that unreachable in verified code
     * (`max_first_key`, `runtime/verifier/method_verifier.cc:2302-2303`, and note it is gated on
     * `if (switch_count > 1)` at `:2298`, so a one-case table skips the check), so in unverified code it
     * is live and observable.
     *
     * **A membership test over dexlib2's expanded keys reproduces that wrap for free, and this comment
     * exists so a later reader does not "fix" it into widened arithmetic.** dexlib2 synthesizes packed
     * keys as `firstKey + index` in plain Java `int` arithmetic, which overflows identically -- verified
     * by disassembly on BOTH of its code paths, not argued: `javap -p -c` on
     * `DexBackedPackedSwitchPayload$1$1.getKey()` (the dex-file-backed path, i.e. a real APK) shows
     * `getfield val$firstKey; getfield val$index; iadd; ireturn`, and on
     * `BuilderPackedSwitchPayload.<init>` (the smali-assembled path, i.e. this repository's fixtures)
     * shows the start key copied to a local and post-incremented with `iinc 3, 1` once per label. So
     * `cases.none { it.key == key }` and ART's range test are the same predicate, in the same arithmetic.
     * A wrap cannot produce DUPLICATE packed keys either: `size` is a `ushort`, so a full wrap would need
     * 2^32 entries. **The oracle agrees with ART here for the same reason, so packed needs no divergence
     * entry.**
     *
     * **SPARSE: a BINARY SEARCH over the raw key array** (`:488-500`, `lo`/`hi`/`mid`, with
     * `return entries[mid]` at `:498` and a bare `return 3;` at `:502` when the loop exits). Transcribed
     * below rather than replaced by a membership test, because on a table that is **not strictly
     * ascending** the two disagree: the search cannot reach some keys, so those selectors fall through on
     * a device while a map lookup finds them.
     *
     * **The engine's answer to that disagreement is to explore BOTH, and it is a CHOICE.** The condition
     * is VERIFIER-tier -- [CON] A8 requires the table *"sorted low-to-high"* and [ART] is stricter still,
     * rejecting duplicates via `if (UNLIKELY(key <= last_key))`
     * (`runtime/verifier/method_verifier.cc:2315`, `FailSparseSwitchPayloadKeyOrder`) -- so no verified
     * input reaches it and there is no runtime behavior to match. Do NOT write that the search "is the
     * only way to agree with the runtime": that overstates one runtime's implementation at [ART] tier,
     * and a different runtime could linear-scan. Spec §2.2 decides instead: which dispatch a runtime uses
     * is an unprovable question about FEASIBILITY, so the successors are the union -- this function
     * answers ART's half and [dispatchOn] adds every matching case, so a malformed table forks and a
     * well-formed one cannot (the search misses a key exactly when no case matches it). **That is a
     * behavioural divergence from the ORACLE**, which dispatches both kinds through one
     * `HashMap.containsKey`, and it is registered as `switch-sparse-descending-keys`.
     */
    private fun fallsThroughOn(key: Int): Boolean = when (dispatch) {
        SwitchDispatch.PACKED -> cases.none { it.key == key }
        SwitchDispatch.SPARSE -> {
            var lo = 0
            var hi = cases.size - 1
            var found = false
            while (lo <= hi && !found) {
                // `(lo + hi) / 2` as ART writes it: `size` is a `ushort`, so the sum cannot overflow.
                val mid = (lo + hi) / 2
                val probe = cases[mid].key
                when {
                    key < probe -> hi = mid - 1
                    key > probe -> lo = mid + 1
                    else -> found = true
                }
            }
            !found
        }
    }

    /**
     * The case list, with **absolute** addresses -- which is Correctness Defect 19 fixed rather than
     * reproduced. `docs/architecture/opcodes.md:174` files the oracle's rendering as *"`SwitchPayloadOp`
     * labels relative offsets as addresses"*, verified at `opcode/SwitchPayloadOp.kt:50`, which appends
     * `" -> :addr_"` followed by an offset. This op has no offsets left to mislabel.
     *
     * A deliberate format divergence from the oracle's `packed-switch  r0, :addr_<fallThrough>`
     * (`opcode/SwitchOp.kt:27`, itself defect 10 -- it prints the fall-through where a reader expects the
     * table), recorded next to the other four in `CARRY-FORWARD.md`'s Plan 10 item 3 and pinned by
     * `core/OpBuilderTest.opsRenderTheirOperandsInTheirDeclaredOrder`, which is where every family's
     * rendering row lives.
     */
    override fun toString(): String =
        cases.joinToString(prefix = "$name r$selectorRegister, [", postfix = "]") {
            "${it.key} -> :addr_${it.targetAddress}"
        }
}

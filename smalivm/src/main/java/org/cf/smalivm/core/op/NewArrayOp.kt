package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.AllocationTarget
import org.cf.smalivm.core.Op
import org.cf.smalivm.core.OpArm
import org.cf.smalivm.core.OpExecution
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.ThrowEvidence
import org.cf.smalivm.core.ThrowableRecipe
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType

/**
 * Allocates a tracked array (spec §4.2). The result is `Known(HeapRef, arrayType)` -- never a live host
 * array, which is what would let two forked siblings mutate one object -- or `Unknown(arrayType)` when the
 * length is unprovable or longer than the heap tracks.
 *
 * [loadable] is decided at DECODE time, in `OpBuilder`, because it is a static fact about the
 * instruction's type reference and because `core/op/` may not reach the class model (the two-tier
 * dependency rule). An unloadable component therefore never forks a successful allocation.
 */
internal class NewArrayOp(
    address: Int,
    override val name: String,
    private val destRegister: Int,
    private val lengthRegister: Int,
    private val arrayType: VirtualType,
    private val loadable: Boolean,
) : Op(address) {

    override val registersRead: Set<Int> get() = setOf(lengthRegister)
    // A reference result is narrow, so isWide is false -- stated at decode time per
    // Op.destinationRegisters' no-default rule rather than inferred from the type.
    override val registersAssigned: Set<Int> get() = destinationRegisters(destRegister, isWide = false)

    override fun execute(frame: Frame): OpExecution {
        // A Float/Double raw is verifier-excluded and its bits are not reinterpreted, so it is not provable;
        // truncating it by value would invent a length. See `Coerce.toProvableInt`.
        val length = Coerce.toProvableInt(readKnownLength(frame))
        // The size is checked before the component class, as ART's switch interpreter does
        // (`CheckArrayAlloc`, runtime/entrypoints/entrypoint_utils-inl.h); nterp resolves the class first.
        // The engine models the switch interpreter; docs/DALVIK-SEMANTICS.md#new-array-op-23-22c records
        // both measurements. The order decides which guest handler is reachable when both checks fail.
        if (length == null) {
            // An unprovable length may be negative, so NegativeArraySizeException is feasible beside the
            // allocation or the resolution failure. Its message would be the size, which is unknown.
            val nonNegative = if (loadable) {
                OpArm.Continue(OpUpdate.assign(destRegister, Value.Unknown(arrayType)))
            } else {
                throwing(ThrowableRecipe.of(NO_CLASS_DEF_FOUND, binaryBaseName()))
            }
            return OpExecution.of(nonNegative, throwing(ThrowableRecipe.of(NEGATIVE_ARRAY_SIZE)))
        }
        if (length < 0) {
            // ART formats the message as the size in decimal (runtime/common_throws.cc,
            // `ThrowNegativeArraySizeException(int)`).
            return OpExecution.single(throwing(ThrowableRecipe.of(NEGATIVE_ARRAY_SIZE, length.toString())))
        }
        if (!loadable) {
            // NoClassDefFoundError is a linking Error ([JVMS] 5.4.3.1), so a guest `catch (Exception)`
            // cannot catch it.
            return OpExecution.single(throwing(ThrowableRecipe.of(NO_CLASS_DEF_FOUND, binaryBaseName())))
        }
        return OpExecution.single(
            OpArm.Continue(OpUpdate.allocateArray(AllocationTarget.Register(destRegister), arrayType, length)),
        )
    }

    private fun throwing(recipe: ThrowableRecipe): OpArm.Throw = OpArm.Throw(evidence = ThrowEvidence.Fresh(recipe))

    /** The length's raw when it is provable, else null -- `Unknown`/`Top` both answer "not provable". */
    private fun readKnownLength(frame: Frame): Any? = (frame.read(lengthRegister) as? Value.Known)?.raw

    /**
     * The guest error for an unresolvable component type. The MESSAGE is ours to pick, because the
     * exception type is a deliberate divergence from the oracle, so no migrated expectation constrains it
     * -- and per docs/DALVIK-SEMANTICS.md an exception message is never evidence of Dalvik fidelity anyway
     * (ART changed these texts between runtimes). ART's shape is `"Failed resolution of: %s"` with the
     * DESCRIPTOR; the binary base name below is the oracle's shape and is kept only so the migrated test
     * reads familiarly. Either is defensible; pick one and assert it.
     */
    /** `[[Lfoo/Bar;` -> `foo.Bar`. */
    private fun binaryBaseName(): String {
        var base = arrayType
        while (base.isArray) base = base.componentType!!
        return base.descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')
    }

    override fun toString(): String = "$name r$destRegister, r$lengthRegister, $arrayType"

    private companion object {
        val NO_CLASS_DEF_FOUND: VirtualType = VirtualType.of("Ljava/lang/NoClassDefFoundError;")
        val NEGATIVE_ARRAY_SIZE: VirtualType = VirtualType.of("Ljava/lang/NegativeArraySizeException;")
    }
}

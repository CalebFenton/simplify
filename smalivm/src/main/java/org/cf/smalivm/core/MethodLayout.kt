package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.immutable.ImmutableTryBlock
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableDebugItem

internal data class LaidOutInstruction(
    val position: LogicalCodePosition?,
    val codeAddress: Int,
    val instruction: Instruction,
    val referenceOperands: List<DexReferenceOperand> = emptyList(),
)

/** Complete validated encoding for one logical method body. */
class MethodLayout internal constructor(
    internal val registerCount: Int,
    internal val instructions: PersistentList<LaidOutInstruction>,
    internal val codeAddressesByPosition: PersistentMap<LogicalCodePosition, Int>,
    internal val tryBlocks: PersistentList<ImmutableTryBlock>,
    internal val debugItems: PersistentList<ImmutableDebugItem>,
)

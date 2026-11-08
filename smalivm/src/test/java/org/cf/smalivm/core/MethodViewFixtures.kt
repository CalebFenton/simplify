package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.immutable.ImmutableExceptionHandler
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.ImmutableTryBlock

/**
 * [view]'s application method with its try blocks, instructions or register count replaced, built through the
 * body importer as a loaded method is. The view keeps [view]'s revisions, so it executes as the same method.
 */
internal fun ClassIndex.rebuiltView(
    view: MethodView,
    tryBlocks: List<TryBlock> = view.tryBlocks,
    instructions: List<Instruction> = view.methodBody.implementation.instructions.toList(),
    registerCount: Int = view.registerCount,
): MethodView {
    val method = requireNotNull(currentSnapshot.applicationMethod(view.signature))
    val body = MethodBody.import(ImmutableMethodImplementation(registerCount, instructions, dexTryBlocks(tryBlocks), null))
    return MethodView.of(
        ApplicationMethodDefinition.withBody(method, body, requireNotNull(view.methodRevision)),
        view.programRevision,
    )
}

/** The address just past the last instruction: an instruction boundary where no instruction lives. */
internal val MethodView.endAddress: Int get() = methodBody.positionsByCodeAddress.keys.max()

internal fun dexTryBlocks(tryBlocks: List<TryBlock>): List<ImmutableTryBlock> = tryBlocks.map { block ->
    val handlers = block.catches.map { ImmutableExceptionHandler(it.exceptionType.descriptor, it.handlerAddress) } +
        listOfNotNull(block.catchAllAddress?.let { ImmutableExceptionHandler(null, it) })
    ImmutableTryBlock(block.startAddress, block.endAddress - block.startAddress, handlers)
}

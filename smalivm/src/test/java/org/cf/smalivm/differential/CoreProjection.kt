package org.cf.smalivm.differential

import java.io.File
import org.cf.smalivm.core.Agreement
import org.cf.smalivm.core.ClassIndex
import org.cf.smalivm.core.CompletionStatus
import org.cf.smalivm.core.CoreTester
import org.cf.smalivm.core.ExecutionGraph
import org.cf.smalivm.core.Frame
import org.cf.smalivm.core.HeapObject
import org.cf.smalivm.core.HeapRef
import org.cf.smalivm.core.HostSeed
import org.cf.smalivm.core.Limits
import org.cf.smalivm.core.Outcome
import org.cf.smalivm.core.RegisterOccupancy
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.VmServices
import org.cf.smalivm.core.requireMethodView

/**
 * Runs a [Fixture] on the current core and projects the explored graph into a [NormalizedResult].
 *
 * Registers are the consensus over returning entry paths: UNKNOWN is a typed unknown, ABSENT means no returning
 * path held the register, CONFLICTED means held values have no joined value. Reprs erase object identity but keep
 * array contents, cycles, and uninitialized state. Exceptions are `binary.Name:message`, with `null` for no message.
 * A truncated exploration is reported as aborted.
 */
class CoreProjection(classPath: String = "src/test/resources/smali") {
    private val classIndex = ClassIndex.open(File(classPath))
    private val vmServices = VmServices(classIndex)

    fun run(fixture: Fixture): NormalizedResult {
        val view = classIndex.requireMethodView(fixture.className, fixture.methodDescriptor)
        val frame = Frame.entryFrame(view)
        val seeds = fixture.seededRegisters.entries.toList()
        val admitted = frame.heap.admitHostSeeds(
            seeds.map { (_, seed) -> HostSeed(Value.Known(seed.first, VirtualType.of(seed.second))) },
        )
        seeds.zip(admitted).forEach { (entry, value) -> frame.assign(entry.key, value) }
        val graph = vmServices.newSession(view, LIMITS, frame).run()

        val registers = fixture.observeRegisters.associateWith { normalize(it, graph) }

        val reached = graph.addresses().filter { graph.wasReached(it) }.toSet() -
            view.payloadAddresses.toSet()
        val unhandled = graph.outcomes.filterIsInstance<Outcome.Threw>()
            .map { entryFor(it.exception, graph) }
            .sorted()
        return NormalizedResult(
            registers, reached, unhandled, aborted = truncated(graph.completion()),
            returnValue = returnedValue(graph),
        )
    }

    private fun truncated(completion: CompletionStatus): Boolean = when (completion) {
        is CompletionStatus.Complete -> false
        is CompletionStatus.Partial -> true
        is CompletionStatus.InProgress -> true
    }

    private fun normalize(register: Int, graph: ExecutionGraph): NormalizedValue {
        val value = graph.explored.terminatingConsensus(register)
        return when {
            value is Value.Top -> when (graph.explored.terminatingRegisterOccupancy(register)) {
                RegisterOccupancy.HELD -> NormalizedValue(ValueKind.CONFLICTED, null, null)
                RegisterOccupancy.NEVER_ASSIGNED, RegisterOccupancy.NO_PATH ->
                    NormalizedValue(ValueKind.ABSENT, null, null)
            }
            else -> normalizeValue(value, graph)
        }
    }

    private fun normalizeValue(value: Value, graph: ExecutionGraph): NormalizedValue = when (value) {
        Value.Top -> NormalizedValue(ValueKind.CONFLICTED, null, null)
        is Value.Unknown -> NormalizedValue(ValueKind.UNKNOWN, value.type.descriptor, null)
        is Value.Known -> {
            val raw = value.raw
            if (raw == null) NormalizedValue(ValueKind.NULL, value.type.descriptor, null)
            else NormalizedValue(
                ValueKind.KNOWN, value.type.descriptor,
                if (raw is HeapRef) heapRepr(graph, raw, mutableSetOf()) else ResultProjection.reprOf(raw),
            )
        }
    }

    /**
     * Device recordings describe the returned object's runtime class, not the return register's bound.
     * Read entry-path outcomes, never register zero or a callee return. Multiple distinct answers are
     * explicitly conflicted; no return (including a throwing-only run) is null, not a known null value.
     */
    private fun returnedValue(graph: ExecutionGraph): NormalizedValue? {
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>()
        if (returned.isEmpty() || returned.all { it.value == null }) return null
        val values = returned.map { outcome ->
            val value = outcome.value ?: return@map NormalizedValue(ValueKind.ABSENT, null, null)
            val normalized = normalizeValue(value, graph)
            val raw = (value as? Value.Known)?.raw
            val runtimeType = when {
                raw is HeapRef -> agreedHeapObject(graph, raw)?.type?.descriptor
                raw != null && value.type?.descriptor?.startsWith("L") == true -> raw.javaClass.name
                else -> normalized.type
            }
            normalized.copy(type = runtimeType)
        }.distinct()
        return values.singleOrNull() ?: NormalizedValue(ValueKind.CONFLICTED, null, null)
    }

    private fun heapRepr(graph: ExecutionGraph, ref: HeapRef, visiting: MutableSet<Int>): String {
        if (!visiting.add(ref.id)) return "[...]"
        try {
            return when (val obj = agreedHeapObject(graph, ref)) {
                null -> "<absent $ref>"
                is HeapObject.TrackedField -> "<instance java.lang.reflect.Field>"
                is HeapObject.TrackedObject ->
                    if (obj.uninitialized) {
                        "Uninitialized ${obj.type.descriptor}"
                    } else {
                        "<instance ${obj.type.descriptor.removePrefix("L").removeSuffix(";").replace('/', '.')}>"
                    }
                is HeapObject.GuestThrowable, is HeapObject.HostObject ->
                    CoreTester.terminatingHostObject(graph, ref)?.let(ResultProjection::reprOf)
                        ?: "<unknown ${obj.type}>"
                is HeapObject.TrackedArray -> obj.elements().map { element ->
                    val raw = (element as? Value.Known)?.raw
                    when {
                        element !is Value.Known -> "?"
                        raw == null -> "null"
                        raw is HeapRef -> heapRepr(graph, raw, visiting)
                        else -> ResultProjection.reprOf(raw)
                    }
                }.toString()
            }
        } finally {
            visiting.remove(ref.id)
        }
    }

    /** The returning paths' joined object at [ref]; the projection renders every other answer as absent. */
    private fun agreedHeapObject(graph: ExecutionGraph, ref: HeapRef): HeapObject? =
        (graph.explored.terminatingHeapObject(ref) as? Agreement.Agreed)?.value

    private fun entryFor(exception: Value, graph: ExecutionGraph): String {
        val raw = (exception as? Value.Known)?.raw
        return when {
            raw is Throwable -> ResultProjection.exceptionEntry(raw.javaClass.name, raw.message)
            raw is HeapRef -> {
                val hostThrowable = graph.addresses().asSequence()
                    .flatMap { graph.nodePile(it).asSequence() }
                    .filter { it.outcome is Outcome.Threw }
                    .mapNotNull { node ->
                        node.terminalFrame.heap.hostObjectAt(raw) as? Throwable
                    }
                    .firstOrNull()
                if (hostThrowable != null) {
                    ResultProjection.exceptionEntry(hostThrowable.javaClass.name, hostThrowable.message)
                } else {
                    ResultProjection.exceptionEntry(binaryNameOf(exception), null)
                }
            }
            else -> ResultProjection.exceptionEntry(binaryNameOf(exception), null)
        }
    }

    private fun binaryNameOf(exception: Value): String =
        exception.type?.descriptor
            ?.removePrefix("L")?.removeSuffix(";")?.replace('/', '.')
            ?: "unknown"

    private companion object {
        private val LIMITS = Limits(maxAddressVisits = 500, maxSteps = 1_000_000, maxExecutionTimeMillis = 0)
    }
}

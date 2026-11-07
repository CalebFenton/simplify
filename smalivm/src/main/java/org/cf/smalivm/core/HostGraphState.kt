package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.IdentityHashMap
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import sun.misc.Unsafe

/** Stable identity of one mutable Java object in a path-local host graph. */
data class HostGraphNodeId(val id: Int)

/** Whether a heap object still has provable Java contents in its path-local graph. */
sealed interface HostGraphContents {
    data class Known(val nodeId: HostGraphNodeId) : HostGraphContents
    data object Unknown : HostGraphContents
}

/** A scheduler-owned object was found inside a backend-owned Java graph. */
internal class HostGraphOwnershipException(message: String) : IllegalArgumentException(message)

/**
 * One immutable root for a path-local Java identity graph.
 *
 * The objects are mutable only while a selected host call owns a detached version. Persistent frame copies
 * share this state object and never expose its Java objects.
 */
internal class HostGraphState private constructor(
    private val nodes: PersistentMap<Int, Any>,
    private val identityIndex: PersistentMap<IdentityKey, HostGraphNodeId>,
    private val nextNodeId: Int,
    internal val isComplete: Boolean,
) {
    val nodeCount: Int get() = nodes.size

    fun nodeIdOf(raw: Any): HostGraphNodeId? = identityIndex[IdentityKey(raw)]

    fun objectAt(nodeId: HostGraphNodeId): Any? = nodes[nodeId.id]

    fun addRoots(roots: Iterable<Any>): HostGraphAdmission {
        val rootList = roots.toList()
        val builder = Builder(this)
        val rootIds = rootList.map { root ->
            requireHostOwned(root)
            require(!root.isDirectHostImmutable()) { "Immutable Java values do not need host-graph identity" }
            builder.idFor(root)
        }
        builder.scan(rootList)
        return HostGraphAdmission(builder.build(), rootIds)
    }

    /** Adds roots whose guest allocation identity must survive even when the JVM value is normally immutable. */
    fun addIdentityRoots(roots: Iterable<Any>): HostGraphAdmission {
        val rootList = roots.toList()
        val builder = Builder(this)
        val rootIds = rootList.map { root ->
            requireHostOwned(root)
            builder.idFor(root)
        }
        builder.scan(rootList)
        return HostGraphAdmission(builder.build(), rootIds)
    }

    /** Finds mutable objects added below existing roots during selected execution. */
    fun refresh(): HostGraphState {
        val builder = Builder(this)
        builder.scan(nodes.values)
        return builder.build()
    }

    /**
     * Returns the complete forward-and-reverse alias component containing [roots].
     *
     * Opaque calls use this once at their mutation boundary. A null result means connectivity cannot be
     * proved, so the caller must invalidate the complete graph rather than preserve a possibly reachable node.
     */
    internal fun aliasConnectedNodeIds(roots: Iterable<HostGraphNodeId>): List<HostGraphNodeId>? {
        val rootIds = roots.map(HostGraphNodeId::id).distinct().sorted()
        if (rootIds.isEmpty()) return emptyList()
        if (!isComplete || rootIds.any { it !in 0 until nextNodeId }) return null

        val parents = IntArray(nextNodeId) { it }

        fun rootOf(node: Int): Int {
            var current = node
            while (parents[current] != current) current = parents[current]
            var compress = node
            while (parents[compress] != compress) {
                val next = parents[compress]
                parents[compress] = current
                compress = next
            }
            return current
        }

        fun connect(left: Int, right: Int) {
            val leftRoot = rootOf(left)
            val rightRoot = rootOf(right)
            if (leftRoot != rightRoot) parents[rightRoot] = leftRoot
        }

        try {
            for (id in 0 until nextNodeId) {
                val raw = nodes[id] ?: return null
                val children = graphChildren(raw)
                if (!children.complete) return null
                for (child in children.values) {
                    if (child == null || child.isDirectHostImmutable()) continue
                    val childId = nodeIdOf(child) ?: return null
                    connect(id, childId.id)
                }
            }
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (_: Throwable) {
            return null
        }

        val rootComponents = rootIds.mapTo(mutableSetOf(), ::rootOf)
        return (0 until nextNodeId)
            .filter { rootOf(it) in rootComponents }
            .map(::HostGraphNodeId)
    }

    internal fun forwardReachableNodeIds(roots: Iterable<HostGraphNodeId>): List<HostGraphNodeId>? {
        if (!isComplete) return null
        val pending = ArrayDeque<HostGraphNodeId>()
        roots.forEach(pending::addLast)
        val seen = linkedSetOf<Int>()
        try {
            while (pending.isNotEmpty()) {
                val nodeId = pending.removeFirst()
                if (!seen.add(nodeId.id)) continue
                val raw = objectAt(nodeId) ?: return null
                val children = graphChildren(raw)
                if (!children.complete) return null
                for (child in children.values) {
                    if (child == null || child.isDirectHostImmutable()) continue
                    pending.addLast(nodeIdOf(child) ?: return null)
                }
            }
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (_: Throwable) {
            return null
        }
        return seen.map(::HostGraphNodeId)
    }

    internal fun objectsById(): Map<Int, Any> = nodes

    internal fun hasSameIdentityLayout(other: HostGraphState): Boolean =
        nodeCount == other.nodeCount &&
            (0 until nodeCount).all { objectAt(HostGraphNodeId(it)) === other.objectAt(HostGraphNodeId(it)) }

    internal fun objectsInIdOrder(): List<Any> = (0 until nextNodeId).map(nodes::getValue)

    internal fun clonedWith(objects: List<Any>): HostGraphState {
        require(objects.size == nodes.size) { "Host graph clone changed node count" }
        var clonedNodes: PersistentMap<Int, Any> = persistentMapOf()
        var clonedIndex: PersistentMap<IdentityKey, HostGraphNodeId> = persistentMapOf()
        objects.indices.forEach { id ->
            val raw = objects[id]
            val nodeId = HostGraphNodeId(id)
            clonedNodes = clonedNodes.putting(id, raw)
            clonedIndex = clonedIndex.putting(IdentityKey(raw), nodeId)
        }
        return HostGraphState(clonedNodes, clonedIndex, nextNodeId, isComplete)
    }

    companion object {
        fun empty(): HostGraphState = HostGraphState(persistentMapOf(), persistentMapOf(), 0, true)

        fun fromRoots(roots: Iterable<Any>): HostGraphState = empty().addRoots(roots).state

        fun fromIdentityRoots(roots: Iterable<Any>): HostGraphState = empty().addIdentityRoots(roots).state
    }

    private class Builder(state: HostGraphState) {
        private val original = state
        private var nodes = state.nodes
        private var identityIndex = state.identityIndex
        private var nextNodeId = state.nextNodeId
        private var complete = state.isComplete
        private val scanned = IdentityHashMap<Any, Boolean>()

        fun idFor(raw: Any): HostGraphNodeId {
            identityIndex[IdentityKey(raw)]?.let { return it }
            val id = HostGraphNodeId(nextNodeId++)
            nodes = nodes.putting(id.id, raw)
            identityIndex = identityIndex.putting(IdentityKey(raw), id)
            return id
        }

        fun scan(roots: Iterable<Any>) {
            val pending = ArrayDeque<Any>()
            roots.forEach { root ->
                requireHostOwned(root)
                if (!root.isDirectHostImmutable()) {
                    idFor(root)
                    pending.addLast(root)
                }
            }
            while (pending.isNotEmpty()) {
                val raw = pending.removeFirst()
                if (scanned.put(raw, true) != null) continue
                val children = graphChildren(raw)
                if (!children.complete) complete = false
                for (child in children.values) {
                    if (child == null) continue
                    requireHostOwned(child)
                    if (child.isDirectHostImmutable()) continue
                    idFor(child)
                    pending.addLast(child)
                }
            }
        }

        fun build(): HostGraphState =
            if (
                nodes === original.nodes && identityIndex === original.identityIndex &&
                nextNodeId == original.nextNodeId && complete == original.isComplete
            ) {
                original
            } else {
                HostGraphState(nodes, identityIndex, nextNodeId, complete)
            }
    }
}

internal data class HostGraphAdmission(
    val state: HostGraphState,
    val rootIds: List<HostGraphNodeId>,
)

/** Identity equality without calling the wrapped object's equality or hash implementation. */
private class IdentityKey(val raw: Any) {
    override fun equals(other: Any?): Boolean = other is IdentityKey && raw === other.raw
    override fun hashCode(): Int = System.identityHashCode(raw)
}

private data class GraphChildren(val values: List<Any?>, val complete: Boolean)

private fun graphChildren(raw: Any): GraphChildren = when {
    raw.javaClass.isArray -> GraphChildren(
        if (raw.javaClass.componentType.isPrimitive) {
            emptyList()
        } else {
            List(ReflectArray.getLength(raw)) { ReflectArray.get(raw, it) }
        },
        true,
    )
    raw is Map<*, *> -> containerChildren(raw) { raw.entries.toList() }
    raw is Map.Entry<*, *> -> mapEntryChildren(raw)
    raw is Iterable<*> -> containerChildren(raw) { raw.toList() }
    raw is StringBuilder || raw is StringBuffer -> GraphChildren(emptyList(), true)
    else -> referenceFieldValues(raw, instanceFields(raw.javaClass))
}

private fun mapEntryChildren(raw: Map.Entry<*, *>): GraphChildren {
    val fields = referenceFieldValues(raw, customInstanceFields(raw.javaClass))
    val complete = if (isJdkImplementation(raw.javaClass)) {
        raw.javaClass.name in OWNING_JDK_MAP_ENTRY_CLASSES
    } else {
        true
    }
    return GraphChildren(listOf(raw.key, raw.value) + fields.values, complete && fields.complete)
}

private fun containerChildren(raw: Any, contents: () -> List<Any?>): GraphChildren {
    val fields = referenceFieldValues(raw, customInstanceFields(raw.javaClass))
    val semanticContents = try {
        contents()
    } catch (fatal: VirtualMachineError) {
        throw fatal
    } catch (_: Throwable) {
        return GraphChildren(fields.values, false)
    }
    return GraphChildren(
        semanticContents + fields.values,
        fields.complete && enumerableContainerOwnsItsBackingState(raw.javaClass),
    )
}

private fun enumerableContainerOwnsItsBackingState(type: Class<*>): Boolean {
    if (type.name in OWNING_JDK_CONTAINER_CLASSES) return true
    val jdkAncestors = generateSequence(type.superclass) { it.superclass }
        .takeWhile { it != Any::class.java }
        .filter(::isJdkImplementation)
        .toList()
    if (jdkAncestors.any { it.name in OWNING_JDK_CONTAINER_CLASSES }) return true
    return !isJdkImplementation(type) && jdkAncestors.isEmpty()
}

private fun isJdkImplementation(type: Class<*>): Boolean =
    type.name.startsWith("java.") || type.name.startsWith("jdk.") || type.name.startsWith("sun.")

private val OWNING_JDK_CONTAINER_CLASSES = setOf(
    "java.util.ArrayDeque",
    "java.util.ArrayList",
    "java.util.Collections\$EmptyList",
    "java.util.Collections\$EmptyMap",
    "java.util.Collections\$EmptySet",
    "java.util.Collections\$SingletonList",
    "java.util.Collections\$SingletonMap",
    "java.util.Collections\$SingletonSet",
    "java.util.EnumMap",
    "java.util.EnumSet",
    "java.util.HashMap",
    "java.util.HashSet",
    "java.util.Hashtable",
    "java.util.IdentityHashMap",
    "java.util.LinkedHashMap",
    "java.util.LinkedHashSet",
    "java.util.LinkedList",
    "java.util.PriorityQueue",
    "java.util.RegularEnumSet",
    "java.util.JumboEnumSet",
    "java.util.Stack",
    "java.util.TreeMap",
    "java.util.TreeSet",
    "java.util.Vector",
    "java.util.WeakHashMap",
    "java.util.concurrent.ConcurrentHashMap",
    "java.util.concurrent.ConcurrentLinkedDeque",
    "java.util.concurrent.ConcurrentLinkedQueue",
    "java.util.concurrent.ConcurrentSkipListMap",
    "java.util.concurrent.ConcurrentSkipListSet",
    "java.util.concurrent.CopyOnWriteArrayList",
    "java.util.concurrent.CopyOnWriteArraySet",
)

private val OWNING_JDK_MAP_ENTRY_CLASSES = setOf(
    "java.util.AbstractMap\$SimpleEntry",
    "java.util.AbstractMap\$SimpleImmutableEntry",
    "java.util.HashMap\$Node",
    "java.util.Hashtable\$Entry",
    "java.util.LinkedHashMap\$Entry",
    "java.util.TreeMap\$Entry",
)

private fun referenceFieldValues(raw: Any, fields: List<Field>): GraphChildren {
    val values = ArrayList<Any?>(fields.size)
    var complete = true
    for (field in fields) {
        if (field.type.isPrimitive) continue
        try {
            values += HostFieldReader.get(raw, field)
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (_: Throwable) {
            complete = false
        }
    }
    return GraphChildren(values, complete)
}

private object HostFieldReader {
    private val unsafe: Unsafe = run {
        val field = Unsafe::class.java.getDeclaredField("theUnsafe")
        field.isAccessible = true
        field.get(null) as Unsafe
    }

    @Suppress("DEPRECATION")
    fun get(raw: Any, field: Field): Any? = unsafe.getObject(raw, unsafe.objectFieldOffset(field))
}

internal fun requireHostOwned(raw: Any) {
    if (
        raw is Frame || raw is ExecutionSession || raw is ExecutionNode ||
        raw is Value || raw is HeapRef || raw is HeapObject
    ) {
        throw HostGraphOwnershipException(
            "Host graphs cannot contain scheduler-owned ${raw.javaClass.name}",
        )
    }
}

internal fun instanceFields(type: Class<*>): List<Field> {
    val fields = mutableListOf<Field>()
    var current: Class<*>? = type
    while (current != null && current != Any::class.java) {
        fields += current.declaredFields.filterNot { Modifier.isStatic(it.modifiers) }
        current = current.superclass
    }
    return fields.sortedWith(compareBy({ it.declaringClass.name }, { it.name }))
}

internal fun customInstanceFields(type: Class<*>): List<Field> = instanceFields(type).filterNot {
    val name = it.declaringClass.name
    name.startsWith("java.") || name.startsWith("jdk.") || name.startsWith("sun.")
}

package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Method
import java.util.IdentityHashMap
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentListOf

/**
 * The source of heap identities for one run. Forked paths share it rather than copying it: if each fork counted on
 * its own, `if (p) a = new int[1] else a = new int[1]` would give both allocations one id, `Value.agreesWith` would
 * report one `Known` array for two distinct objects, and a consumer could fold away an allocation another path
 * relies on. Shared, the two ids differ and their join is `Unknown`.
 *
 * Its identity is the run's identity: refs from two instances collide on equal ids, so [HeapStore] joins only
 * stores minted from one instance.
 *
 * Ids are reproducible: the frontier is LIFO, transition order is fixed, and the session reads no clock at the
 * default [Limits], so two runs of one input allocate the same ids.
 */
internal class HeapIds {
    private var next = 0
    fun allocate(): HeapRef = HeapRef(next++)
}

internal class HostSeed(
    val value: Value,
    val arrayType: VirtualType? = null,
    val arrayElements: Array<Value>? = null,
)

internal data class HostArrayMirror(
    val ref: HeapRef,
    val type: VirtualType,
    val length: Int,
    val elements: List<Value>,
    val raw: Any,
)

internal class HostGraphCheckpoint(
    private val owner: HeapStore,
    val graph: HostGraphState,
    val objects: HeapObjects,
    val expectedMirrorElements: Map<Int, List<Any?>>,
    val selectionRoots: List<Value>,
) {
    private var consumed = false

    fun verify(requester: HeapStore) {
        check(owner === requester) { "Host graph checkpoint belongs to a different heap path" }
        check(!consumed) { "Host graph checkpoint was already finalized or restored" }
    }

    fun consume(requester: HeapStore) {
        verify(requester)
        consumed = true
    }

    val synchronizedDirtyMirrors: Boolean get() = expectedMirrorElements.isNotEmpty()
}

internal data class HeapTransactionCheckpoint(
    val objects: HeapObjects,
    val hostGraph: HostGraphState,
)

internal data class ArrayReplacement(val ref: HeapRef, val elements: List<Value>)

/**
 * One version of a path's objects and the two lookups derived from them: the ref holding each host-graph node,
 * and the tracked reflection fields that key call partitions. [put], [putAll] and [remove] are the only writers,
 * so neither lookup can drift from the objects.
 */
internal class HeapObjects private constructor(
    private val objects: PersistentMap<Int, HeapObject>,
    private val nodeRefs: PersistentMap<Int, HeapRef>,
    val trackedFields: CompatibilityMapKey<Int, TrackedFieldCompatibility>,
) {
    val ids: Set<Int> get() = objects.keys

    operator fun get(id: Int): HeapObject? = objects[id]

    fun refFor(nodeId: HostGraphNodeId): HeapRef? = nodeRefs[nodeId.id]

    /** Every object whose Java state the host graph holds, in heap-id order. */
    fun graphBacked(): List<HeapRef> = nodeRefs.values.sortedBy(HeapRef::id)

    fun put(ref: HeapRef, obj: HeapObject): HeapObjects {
        // A tracked object is in neither lookup, so its writes skip them unless it replaces a host-backed version.
        if (obj is HeapObject.TrackedObject && objects[ref.id] !is HostBacked) {
            return HeapObjects(objects.putting(ref.id, obj), nodeRefs, trackedFields)
        }
        return putAll(listOf(ref to obj))
    }

    /** Writes every entry at once, so entries may trade host-graph nodes among themselves. */
    fun putAll(entries: List<Pair<HeapRef, HeapObject>>): HeapObjects {
        if (entries.isEmpty()) return this
        var updatedObjects = objects
        var updatedRefs = nodeRefs
        var updatedFields = trackedFields
        for ((ref, obj) in entries) {
            when (val previous = updatedObjects[ref.id]) {
                is HostBacked -> previous.knownNodeId?.let { node ->
                    if (updatedRefs[node.id] == ref) updatedRefs = updatedRefs.removing(node.id)
                }
                is HeapObject.TrackedField -> if (obj !is HeapObject.TrackedField) updatedFields = updatedFields.remove(ref.id)
                is HeapObject.TrackedObject, null -> Unit
            }
            updatedObjects = updatedObjects.putting(ref.id, obj)
            if (obj is HeapObject.TrackedField) {
                updatedFields = updatedFields.put(ref.id, TrackedFieldCompatibility(obj.metadata, obj.accessibility))
            }
        }
        for ((ref, obj) in entries) {
            val node = (obj as? HostBacked)?.knownNodeId ?: continue
            val owner = updatedRefs[node.id]
            require(owner == null || owner == ref) { "$node is already owned by $owner, not $ref" }
            updatedRefs = updatedRefs.putting(node.id, ref)
        }
        return HeapObjects(updatedObjects, updatedRefs, updatedFields)
    }

    fun remove(id: Int): HeapObjects {
        val previous = objects[id] ?: return this
        val node = (previous as? HostBacked)?.knownNodeId
        return HeapObjects(
            objects.removing(id),
            if (node == null) nodeRefs else nodeRefs.removing(node.id),
            trackedFields.remove(id),
        )
    }

    override fun toString(): String = "HeapObjects(${objects.size} objects)"

    companion object {
        val EMPTY = HeapObjects(persistentMapOf(), persistentMapOf(), CompatibilityMapKey.empty())
    }
}

/**
 * One path's heap (spec §4.2): instance state and object identity, resolved per path rather than per frame, so a
 * callee's mutation is visible to its caller on the same path while registers stay method-local.
 *
 * [copy] creates a wrapper over the same persistent roots. Objects are immutable versions, so writes replace only
 * the addressed object and map path. Earlier frames and sibling paths keep their roots.
 */
internal class HeapStore private constructor(
    private val ids: HeapIds,
    private var objects: HeapObjects,
    private var hostGraph: HostGraphState,
) {
    constructor(ids: HeapIds) : this(ids, HeapObjects.EMPTY, HostGraphState.empty())

    /** A fresh array of [length] whose elements hold Java's default for the component type. */
    fun allocateArray(type: VirtualType, length: Int): HeapRef {
        require(length >= 0) { "Cannot allocate an array of length $length; a negative length is the OP's NegativeArraySizeException to throw" }
        require(length <= MAX_TRACKED_ARRAY_LENGTH) {
            "Cannot track an array of length $length; over $MAX_TRACKED_ARRAY_LENGTH the caller models it as unknown"
        }
        // A tracked array's own type is always a real array descriptor -- object over tag. A caller holding
        // only a loosely-tagged register must resolve the truth BEFORE allocating (from the type operand, or
        // from a seeded host array's class); it may not launder a non-array tag through here.
        val component = requireNotNull(type.componentType) {
            "A tracked array's type must be an array descriptor, not $type; resolve the component before allocating"
        }
        val default = defaultElementValue(component)
        val elements = persistentListOf<Value>().builder()
        repeat(length) { elements.add(default) }
        return allocateArray(type, elements.build())
    }

    /** A fresh array holding [elements] verbatim -- `filled-new-array`, `fill-array-data`, test seeding. */
    fun allocateArray(type: VirtualType, elements: Array<Value>): HeapRef =
        allocate(HeapObject.TrackedArray(type, elements))

    fun allocateArray(type: VirtualType, elements: PersistentList<Value>): HeapRef =
        allocate(HeapObject.TrackedArray.fromPersistent(type, elements))

    fun allocateTrackedField(metadata: TrackedFieldValue): HeapRef =
        allocate(HeapObject.TrackedField(metadata, FieldAccessibility.LANGUAGE_CHECKS))

    fun trackedFieldAt(ref: HeapRef): HeapObject.TrackedField? = objects[ref.id] as? HeapObject.TrackedField

    fun setTrackedFieldAccessibility(ref: HeapRef, accessibility: FieldAccessibility): Boolean {
        val current = trackedFieldAt(ref) ?: return false
        val replacement = current.withAccessibility(accessibility)
        if (replacement !== current) replaceObject(ref, replacement)
        return true
    }

    fun allocateThrowable(type: VirtualType, hostThrowable: Throwable, cause: Value? = null): HeapRef {
        val modeledCause = cause ?: if (hostThrowable.cause == null) {
            Value.Known(null, VirtualType.of("Ljava/lang/Throwable;"))
        } else {
            null
        }
        val hostCauseUnavailable = when (modeledCause) {
            is Value.Known -> when (val raw = modeledCause.raw) {
                is HeapRef -> hostObjectAt(raw) !is Throwable
                else -> false
            }
            is Value.Unknown, Value.Top -> true
            null -> false
        }
        if (hostCauseUnavailable) {
            return allocate(HeapObject.GuestThrowable(type, HostGraphContents.Unknown, modeledCause))
        }
        if (modeledCause is Value.Known) {
            val hostCause = when (val raw = modeledCause.raw) {
                null -> null
                is HeapRef -> hostObjectAt(raw) as? Throwable
                    ?: error("Throwable cause $raw has no known host mirror")
                else -> error("Throwable cause must be null or a HeapRef, not $raw")
            }
            if (hostThrowable.cause !== hostCause) {
                check(hostThrowable.cause == null && hostCause != null) {
                    "Host throwable cause does not match its modeled cause"
                }
                hostThrowable.initCause(hostCause)
            }
        }
        val nodeId = addHostRoots(listOf(hostThrowable)).single()
        return allocate(HeapObject.GuestThrowable(type, knownContents(nodeId, hostThrowable), modeledCause))
    }

    fun setThrowableCause(ref: HeapRef, cause: Value) {
        val current = objects[ref.id] as? HeapObject.GuestThrowable
            ?: error("$ref does not identify a guest throwable")
        val host = hostObjectAt(ref) as? Throwable
            ?: error("$ref has no known host throwable mirror")
        val expectedHostCause = when (cause) {
            is Value.Known -> when (val raw = cause.raw) {
                null -> null
                is HeapRef -> hostObjectAt(raw) as? Throwable
                    ?: error("Throwable cause $raw has no known host mirror")
                else -> error("Throwable cause must be null or a HeapRef, not $raw")
            }
            is Value.Unknown, Value.Top -> error("Imported host throwable causes must be concrete")
        }
        check(host.cause === expectedHostCause) {
            "Host throwable cause mirror does not match the modeled cause for $ref"
        }
        val replacement = current.withCause(cause)
        if (replacement !== current) replaceObject(ref, replacement)
    }

    fun allocateHostObject(type: VirtualType, raw: Any): HeapRef {
        require(!raw.isDirectHostImmutable()) { "Immutable host values do not need heap identity" }
        require(!raw.javaClass.isArray) { "Java arrays must use HeapObject.TrackedArray" }
        require(type.isReference) { "Host objects require a reference tag, not $type" }
        findHostObject(raw)?.let { return it }
        val nodeId = addHostRoots(listOf(raw)).single()
        return allocate(hostObject(raw, nodeId))
    }

    fun findHostObject(raw: Any): HeapRef? = hostGraph.nodeIdOf(raw)?.let(objects::refFor)

    fun hostObjectAt(ref: HeapRef): Any? = (objects[ref.id] as? HostBacked)?.contents?.let(::resolve)

    fun hostObjectAt(nodeId: HostGraphNodeId): Any? = hostGraph.objectAt(nodeId)

    fun hostNodeId(raw: Any): HostGraphNodeId? = hostGraph.nodeIdOf(raw)

    fun hostGraphObjects(): Map<Int, Any> = hostGraph.objectsById()

    fun refForNode(nodeId: HostGraphNodeId): HeapRef? = objects.refFor(nodeId)

    fun mirroredArrays(): List<HostArrayMirror> = objects.graphBacked().mapNotNull { ref ->
        val array = objects[ref.id] as? HeapObject.TrackedArray ?: return@mapNotNull null
        val raw = array.contents?.let(::resolve) ?: return@mapNotNull null
        HostArrayMirror(ref, array.type, array.length, array.elements(), raw)
    }

    private fun resolve(contents: HostGraphContents): Any? = when (contents) {
        is HostGraphContents.Known -> hostGraph.objectAt(contents.nodeId)
        HostGraphContents.Unknown -> null
    }

    private fun hostObject(raw: Any, nodeId: HostGraphNodeId): HeapObject {
        require(!raw.javaClass.isArray) { "Java arrays must use HeapObject.TrackedArray" }
        val type = virtualTypeOfHostClass(raw.javaClass)
        val contents = knownContents(nodeId, raw)
        return if (raw is Throwable) {
            HeapObject.GuestThrowable(type, contents)
        } else {
            HeapObject.HostObject(type, contents, (raw as? Method)?.toReflectedMethodMetadataOrNull())
        }
    }

    private fun knownContents(nodeId: HostGraphNodeId, expected: Any? = null): HostGraphContents.Known {
        val raw = requireNotNull(hostGraph.objectAt(nodeId)) { "No host graph object exists for $nodeId" }
        require(expected == null || raw === expected) { "$nodeId does not identify the imported Java object" }
        return HostGraphContents.Known(nodeId)
    }

    private fun addHostRoots(roots: List<Any>): List<HostGraphNodeId> {
        if (roots.isEmpty()) return emptyList()
        val admission = hostGraph.addRoots(roots)
        hostGraph = admission.state
        return admission.rootIds
    }

    private fun addHostIdentityRoots(roots: List<Any>): List<HostGraphNodeId> {
        if (roots.isEmpty()) return emptyList()
        val admission = hostGraph.addIdentityRoots(roots)
        hostGraph = admission.state
        return admission.rootIds
    }

    /** Admits all entry values under one Java identity map and one host-graph version. */
    fun admitHostSeeds(seeds: List<HostSeed>): List<Value> {
        val mutableRoots = seeds.mapNotNull { seed ->
            (seed.value as? Value.Known)?.raw?.takeIf {
                it !is HeapRef && !it.isDirectHostImmutable()
            }
        }
        if (mutableRoots.isEmpty()) return seeds.map(HostSeed::value)

        val overrides = IdentityHashMap<Any, HostSeed>()
        seeds.forEach { seed ->
            val raw = (seed.value as? Value.Known)?.raw
            if (raw != null && seed.arrayType != null) overrides[raw] = seed
        }
        addHostRoots(mutableRoots)
        val refs = IdentityHashMap<Any, HeapRef>()

        fun admit(raw: Any): HeapRef {
            refs[raw]?.let { return it }
            findHostObject(raw)?.let {
                refs[raw] = it
                return it
            }
            val nodeId = requireNotNull(hostGraph.nodeIdOf(raw))
            if (!raw.javaClass.isArray) {
                return allocate(hostObject(raw, nodeId)).also { refs[raw] = it }
            }

            val override = overrides[raw]
            val type = override?.arrayType ?: virtualTypeOfHostClass(raw.javaClass)
            val component = requireNotNull(type.componentType)
            val length = ReflectArray.getLength(raw)
            val ref = ids.allocate()
            refs[raw] = ref
            // Holds the ref and its node while nested elements are admitted, so a cycle back to this array finds it.
            val default = defaultElementValue(component)
            replaceObject(ref, HeapObject.TrackedArray(type, Array(length) { default }, knownContents(nodeId, raw)))
            val values = override?.arrayElements?.map { element ->
                if (element !is Value.Known) {
                    element
                } else {
                    val elementRaw = element.raw
                    if (elementRaw == null || elementRaw is HeapRef || elementRaw.isDirectHostImmutable()) {
                        element
                    } else {
                        Value.Known(admit(elementRaw), element.type)
                    }
                }
            } ?: List(length) { index ->
                val element = ReflectArray.get(raw, index)
                when {
                    element == null || element.isDirectHostImmutable() -> Value.Known(element, component)
                    else -> Value.Known(admit(element), component)
                }
            }
            require(values.size == length) { "Seeded array element count does not match its Java mirror" }
            replaceObject(
                ref,
                HeapObject.TrackedArray(type, values.toTypedArray(), knownContents(nodeId, raw)),
            )
            return ref
        }

        // Arrays nested inside containers are tracked even when no register names them directly.
        hostGraph.objectsById().values.filter { it.javaClass.isArray }.forEach(::admit)
        return seeds.map { seed ->
            val value = seed.value
            if (value !is Value.Known || value.raw == null || value.raw is HeapRef || value.raw.isDirectHostImmutable()) {
                value
            } else {
                Value.Known(admit(value.raw), value.type)
            }
        }
    }

    /**
     * A fresh ordinary object, uninitialized until its constructor returns -- `new-instance`'s allocation. Minted
     * from the shared [ids] counter, so two `new-instance` of one type on two forked paths are two identities.
     */
    fun allocateObject(type: VirtualType, fields: Map<String, Value> = emptyMap()): HeapRef =
        allocate(HeapObject.TrackedObject(type, uninitialized = true, fields))

    private fun allocate(obj: HeapObject): HeapRef = ids.allocate().also { ref -> replaceObject(ref, obj) }

    private fun replaceObject(ref: HeapRef, replacement: HeapObject) {
        objects = objects.put(ref, replacement)
    }

    /** The object [ref] denotes on this path, or null when this heap has no such object. */
    fun objectAt(ref: HeapRef): HeapObject? = objects[ref.id]

    /** A tracked object's field cell, or [Value.Top] when this path has no such object or field. */
    fun getField(ref: HeapRef, fieldDescriptor: String): Value =
        (objects[ref.id] as? HeapObject.TrackedObject)?.field(fieldDescriptor) ?: Value.Top

    /** Writes one resolved cell. False means this path has no matching tracked object or field. */
    fun setField(ref: HeapRef, fieldDescriptor: String, value: Value): Boolean {
        val current = objects[ref.id] as? HeapObject.TrackedObject ?: return false
        if (current.field(fieldDescriptor) == value) return true
        val updated = current.withField(fieldDescriptor, value) ?: return false
        replaceObject(ref, updated)
        return true
    }

    fun arrayAt(ref: HeapRef): HeapObject.TrackedArray? = objects[ref.id] as? HeapObject.TrackedArray

    fun setArrayElement(ref: HeapRef, index: Int, value: Value): Boolean {
        return setArrayElements(ref, index, listOf(value))
    }

    fun setArrayElements(ref: HeapRef, startIndex: Int, values: List<Value>): Boolean {
        val current = arrayAt(ref) ?: return false
        require(startIndex >= 0 && startIndex + values.size <= current.length) {
            "Array update [$startIndex, ${startIndex + values.size}) is outside length ${current.length}"
        }
        if (values.indices.all { current.element(startIndex + it) == values[it] }) return true
        replaceObject(ref, current.withElements(startIndex, values))
        return true
    }

    fun transactionCheckpoint(): HeapTransactionCheckpoint = HeapTransactionCheckpoint(objects, hostGraph)

    fun verifyHostGraphCheckpoint(checkpoint: HostGraphCheckpoint) = checkpoint.verify(this)

    /** Commits all validated array lattice replacements under one persistent heap root. */
    fun applyArrayReplacementsAtomically(replacements: List<ArrayReplacement>) {
        var updated = objects
        for (replacement in replacements) {
            val current = updated[replacement.ref.id] as? HeapObject.TrackedArray
                ?: error("Tracked array ${replacement.ref} disappeared during host copy-back")
            require(replacement.elements.size == current.length) {
                "Copy-back for ${replacement.ref} has ${replacement.elements.size} elements, expected ${current.length}"
            }
            if (replacement.elements.indices.all { current.element(it) == replacement.elements[it] }) continue
            updated = updated.put(
                replacement.ref,
                current.withElementsRoot(
                    current.elementsRoot().mutateValues(replacement.elements),
                    current.contents,
                    dirty = false,
                ),
            )
        }
        objects = updated
    }

    /** Restores post-execution roots, then invalidates facts affected by failed copy-back as one update. */
    fun rollbackFailedCopyback(
        checkpoint: HeapTransactionCheckpoint,
        affectedArrays: Set<HeapRef>,
    ) {
        objects = checkpoint.objects
        hostGraph = checkpoint.hostGraph
        forgetHostGraph()
        objects = objects.putAll(
            affectedArrays.mapNotNull { ref -> arrayAt(ref)?.let { ref to it.withUnknownElements(it.contents) } },
        )
    }

    private fun javaValue(value: Value, graph: HostGraphState): Any? {
        if (value !is Value.Known) {
            throw HostGraphRepresentationException("A Java array mirror cannot represent $value")
        }
        val raw = value.raw
        if (raw !is HeapRef) return raw
        val node = (objects[raw.id] as? HostBacked)?.knownNodeId
        return node?.let(graph::objectAt) ?: throw HostGraphRepresentationException("$raw has no known Java object")
    }

    fun setArrayMirror(ref: HeapRef, raw: Any): HostGraphNodeId {
        val current = arrayAt(ref) ?: error("No tracked array at $ref")
        require(raw.javaClass.isArray) { "A tracked-array mirror must be a Java array" }
        val nodeId = addHostRoots(listOf(raw)).single()
        replaceObject(ref, current.withSynchronizedMirror(knownContents(nodeId, raw)))
        return nodeId
    }

    /** Every graph-backed object forgets its Java state, and the graph is emptied. Guest-visible cells stay. */
    private fun forgetHostGraph() {
        invalidateGraphBacked(wipeElements = false) { true }
        if (hostGraph.nodeCount > 0) hostGraph = HostGraphState.empty()
    }

    /**
     * Each graph-backed object whose node [select] accepts forgets its Java state. With [wipeElements], such an
     * array also forgets its elements, because a call that could write the mirror could have changed them.
     */
    private fun invalidateGraphBacked(wipeElements: Boolean, select: (HostGraphNodeId) -> Boolean) {
        val forgotten = objects.graphBacked().mapNotNull { ref ->
            val obj = objects[ref.id] as HostBacked
            if (!select(requireNotNull(obj.knownNodeId))) return@mapNotNull null
            ref to if (wipeElements && obj is HeapObject.TrackedArray) {
                obj.withUnknownElements(HostGraphContents.Unknown)
            } else {
                obj.withContents(HostGraphContents.Unknown)
            }
        }
        objects = objects.putAll(forgotten)
    }

    private fun HeapObject.TrackedArray.withUnknownElements(next: HostGraphContents?): HeapObject.TrackedArray {
        val component = requireNotNull(type.componentType)
        return withElementsRoot(elementsRoot().mapValues { Value.Unknown(component) }, next)
    }

    fun markInitialized(ref: HeapRef): Boolean {
        val obj = objects[ref.id] as? HeapObject.TrackedObject ?: return false
        if (!obj.uninitialized) return true
        replaceObject(ref, obj.initialized())
        return true
    }

    /** Replaces one uninitialized tracked receiver with its exact constructed JVM object under the same ref. */
    fun installConstructedHostObject(ref: HeapRef, raw: Any): Boolean {
        val current = objects[ref.id] as? HeapObject.TrackedObject ?: return false
        if (!current.uninitialized) return false
        requireHostOwned(raw)
        require(!raw.javaClass.isArray) { "A constructor cannot install a Java array receiver" }
        require(findHostObject(raw) == null) { "Constructed host object already belongs to another heap ref" }
        val nodeId = addHostIdentityRoots(listOf(raw)).single()
        replaceObject(ref, hostObject(raw, nodeId))
        return true
    }

    /** Tracked fields reachable through the same array/object edges used by [collapseReachable]. */
    fun reachableTrackedFields(roots: Iterable<HeapRef>): List<HeapRef> =
        reachableRefs(roots).filter { objects[it.id] is HeapObject.TrackedField }

    private fun reachableRefs(roots: Iterable<HeapRef>): List<HeapRef> {
        val pending = ArrayDeque<HeapRef>()
        roots.forEach(pending::addLast)
        val seen = mutableSetOf<Int>()
        val reachable = mutableListOf<HeapRef>()
        while (pending.isNotEmpty()) {
            val ref = pending.removeFirst()
            if (!seen.add(ref.id)) continue
            reachable += ref
            when (val obj = objects[ref.id]) {
                is HeapObject.TrackedArray -> obj.elements().forEach { value ->
                    ((value as? Value.Known)?.raw as? HeapRef)?.let(pending::addLast)
                }
                is HeapObject.TrackedObject -> obj.fields().values.forEach { value ->
                    ((value as? Value.Known)?.raw as? HeapRef)?.let(pending::addLast)
                }
                is HeapObject.GuestThrowable -> obj.cause?.let { value ->
                    ((value as? Value.Known)?.raw as? HeapRef)?.let(pending::addLast)
                }
                is HeapObject.HostObject,
                is HeapObject.TrackedField,
                null,
                -> Unit
            }
        }
        return reachable
    }

    private data class OpaqueReachability(
        val refs: List<HeapRef>,
        val hostConnectivityUnprovable: Boolean,
    )

    /** Collapses mutable contents reachable from the arguments, preserving disconnected host graph components. */
    fun collapseReachable(
        values: List<Value>,
        authoritativeTypes: List<VirtualType>,
    ) {
        require(values.size == authoritativeTypes.size) {
            "Opaque values and authoritative types differ in size: ${values.size} != ${authoritativeTypes.size}"
        }
        val reachability = opaqueReachability(values, authoritativeTypes)
        val hostRoots = mutableListOf<HostGraphNodeId>()
        var collapseAllHostNodes = false
        for (ref in reachability.refs) {
            val obj = objects[ref.id]
            when (val contents = (obj as? HostBacked)?.contents) {
                is HostGraphContents.Known -> hostRoots += contents.nodeId
                HostGraphContents.Unknown -> collapseAllHostNodes = true
                null -> Unit
            }
            when (obj) {
                is HeapObject.TrackedArray -> {
                    val component = requireNotNull(obj.type.componentType)
                    val collapsed = obj.elements().map { old ->
                        if (old.type == null) old else Value.Unknown(component)
                    }
                    val replacement = obj.withElementsRoot(
                        obj.elementsRoot().mutateValues(collapsed),
                        if (obj.contents == null) null else HostGraphContents.Unknown,
                    )
                    if (replacement.elementsRoot() !== obj.elementsRoot() || replacement.contents != obj.contents) {
                        replaceObject(ref, replacement)
                    }
                }
                is HeapObject.TrackedObject -> {
                    val collapsed = LinkedHashMap<String, Value>()
                    for ((field, old) in obj.fields()) {
                        val fieldType = VirtualType.of(field.substringAfterLast(':'))
                        collapsed[field] = if (old.type == null) Value.Top else Value.Unknown(fieldType)
                    }
                    if (collapsed.any { (field, value) -> obj.field(field) != value }) {
                        replaceObject(ref, obj.withFields(collapsed))
                    }
                }
                is HeapObject.TrackedField, is HeapObject.HostObject, is HeapObject.GuestThrowable, null -> Unit
            }
        }

        when {
            reachability.hostConnectivityUnprovable || collapseAllHostNodes -> invalidateCompleteHostGraphForOpaqueCall()
            hostRoots.isNotEmpty() -> {
                val component = hostGraph.aliasConnectedNodeIds(hostRoots)
                if (component == null) {
                    invalidateCompleteHostGraphForOpaqueCall()
                } else {
                    invalidateHostGraphComponent(component)
                }
            }
        }
    }

    private fun opaqueReachability(
        values: List<Value>,
        authoritativeTypes: List<VirtualType>,
    ): OpaqueReachability {
        val pending = ArrayDeque<HeapRef>()
        var unprovable = false

        fun inspect(value: Value, authoritativeType: VirtualType) {
            when (value) {
                is Value.Known -> if (authoritativeType.isReference) {
                    (value.raw as? HeapRef)?.let(pending::addLast)
                }
                is Value.Unknown -> if (authoritativeType.isReference) unprovable = true
                Value.Top -> if (authoritativeType.isReference) unprovable = true
            }
        }

        values.zip(authoritativeTypes).forEach { (value, type) -> inspect(value, type) }
        val seen = mutableSetOf<Int>()
        val reachable = mutableListOf<HeapRef>()
        while (pending.isNotEmpty()) {
            val ref = pending.removeFirst()
            if (!seen.add(ref.id)) continue
            reachable += ref
            when (val obj = objects[ref.id]) {
                is HeapObject.TrackedArray -> {
                    val component = requireNotNull(obj.type.componentType)
                    obj.elements().forEach { inspect(it, component) }
                }
                is HeapObject.TrackedObject -> obj.fields().forEach { (descriptor, value) ->
                    inspect(value, VirtualType.of(descriptor.substringAfterLast(':')))
                }
                is HeapObject.GuestThrowable -> obj.cause?.let {
                    inspect(it, VirtualType.of("Ljava/lang/Throwable;"))
                }
                is HeapObject.HostObject,
                is HeapObject.TrackedField,
                null,
                -> Unit
            }
        }
        return OpaqueReachability(reachable, unprovable)
    }

    private fun invalidateHostGraphComponent(nodeIds: Collection<HostGraphNodeId>) {
        val invalidated = nodeIds.mapTo(mutableSetOf(), HostGraphNodeId::id)
        invalidateGraphBacked(wipeElements = true) { it.id in invalidated }
        try {
            compactHostGraph()
        } catch (_: HostGraphRepresentationException) {
            // The component is already forgotten; forgetting the rest is the conservative completion.
            invalidateCompleteHostGraphForOpaqueCall()
        }
    }

    private fun invalidateCompleteHostGraphForOpaqueCall() {
        invalidateGraphBacked(wipeElements = true) { true }
        if (hostGraph.nodeCount > 0) hostGraph = HostGraphState.empty()
    }

    /**
     * The complete identity graph over [roots]. A walk that fails, or that meets Java state it cannot see, throws
     * a [HostGraphRepresentationException] carrying the cause.
     */
    private fun identityGraphOf(roots: List<Any>): HostGraphState {
        val rebuilt = hostBoundary({ throw HostGraphRepresentationException("Cannot walk the host graph", it) }) {
            HostGraphState.fromIdentityRoots(roots)
        }
        if (!rebuilt.isComplete) throw HostGraphRepresentationException("The host graph holds Java state no walk can see")
        return rebuilt
    }

    /** The Java object each graph-backed ref in [refs] holds, in the order given. */
    private fun javaObjectsOf(refs: List<HeapRef>): List<Pair<HeapRef, Any>> = refs.map { ref ->
        val node = requireNotNull((objects[ref.id] as HostBacked).knownNodeId)
        ref to (hostGraph.objectAt(node) ?: throw HostGraphRepresentationException("$ref names absent host node $node"))
    }

    /** [surviving] renumbered onto [graph]'s nodes; only refs whose node changes are returned. */
    private fun rekeyedOnto(graph: HostGraphState, surviving: List<Pair<HeapRef, Any>>): List<Pair<HeapRef, HeapObject>> =
        surviving.mapNotNull { (ref, raw) ->
            val obj = objects[ref.id] as HostBacked
            val node = graph.nodeIdOf(raw) ?: throw HostGraphRepresentationException("Rebuilding the host graph lost $ref")
            if (obj.knownNodeId == node) null else ref to obj.withContents(HostGraphContents.Known(node))
        }

    /** Rebuilds the graph from the Java objects that graph-backed refs hold, keeping the current one when it matches. */
    private fun compactHostGraph() {
        val surviving = javaObjectsOf(objects.graphBacked())
        val rebuilt = identityGraphOf(surviving.map { it.second })
        val compacted = if (rebuilt.hasSameIdentityLayout(hostGraph)) hostGraph else rebuilt
        objects = objects.putAll(rekeyedOnto(compacted, surviving))
        hostGraph = compacted
    }

    /**
     * Keeps only the Java state reachable from [roots]' guest objects: graph-backed objects outside it forget
     * their Java state, and the graph is rebuilt from the rest. A failure forgets the whole graph, then throws.
     */
    private fun pruneHostGraphToGuestRoots(roots: List<Value>) {
        try {
            val guestRefs = reachableRefs(roots.mapNotNull { (it as? Value.Known)?.raw as? HeapRef })
            val live = javaObjectsOf(guestRefs.filter { (objects[it.id] as? HostBacked)?.knownNodeId != null })
            val rebuilt = identityGraphOf(live.map { it.second })
            val (surviving, dropped) = javaObjectsOf(objects.graphBacked()).partition { rebuilt.nodeIdOf(it.second) != null }
            val compacted = if (hostGraph.isComplete && rebuilt.hasSameIdentityLayout(hostGraph)) hostGraph else rebuilt
            val forgotten = dropped.map { (ref, _) ->
                ref to (objects[ref.id] as HostBacked).withContents(HostGraphContents.Unknown)
            }
            objects = objects.putAll(forgotten + rekeyedOnto(compacted, surviving))
            hostGraph = compacted
        } catch (failure: HostGraphRepresentationException) {
            forgetHostGraph()
            throw HostGraphRepresentationException("Cannot prove the selected guest host-graph roots", failure)
        }
    }

    /** Detaches this path's complete Java graph after one backend accepts execution. */
    fun detachHostGraph(selectionRoots: List<Value>): HostGraphCheckpoint {
        pruneHostGraphToGuestRoots(selectionRoots)
        val checkpoint = HostGraphCheckpoint(
            this,
            hostGraph,
            objects,
            dirtyMirrorExpectedValues(hostGraph),
            selectionRoots,
        )
        try {
            hostGraph = cloneHostGraph(checkpoint.graph)
            synchronizeDirtyMirrors()
        } catch (failure: Throwable) {
            hostGraph = checkpoint.graph
            objects = checkpoint.objects
            throw failure
        }
        return checkpoint
    }

    private fun dirtyMirrors(): List<Pair<HeapRef, HeapObject.TrackedArray>> = objects.graphBacked().mapNotNull { ref ->
        (objects[ref.id] as? HeapObject.TrackedArray)?.takeIf { it.mirrorDirty }?.let { ref to it }
    }

    private fun dirtyMirrorExpectedValues(graph: HostGraphState): Map<Int, List<Any?>> =
        dirtyMirrors().associate { (_, array) ->
            requireNotNull(array.knownNodeId).id to array.elements().map { javaValue(it, graph) }
        }

    private fun synchronizeDirtyMirrors() {
        val dirty = dirtyMirrors()
        if (dirty.isEmpty()) return

        for ((_, array) in dirty) {
            val nodeId = requireNotNull(array.knownNodeId)
            val raw = requireNotNull(hostGraph.objectAt(nodeId)) { "No Java mirror for $nodeId" }
            require(ReflectArray.getLength(raw) == array.length) { "Java mirror length changed for ${array.type}" }
            array.elements().forEachIndexed { index, value ->
                try {
                    ReflectArray.set(raw, index, javaValue(value, hostGraph))
                } catch (failure: IllegalArgumentException) {
                    throw HostGraphRepresentationException("Cannot synchronize the Java array mirror", failure)
                }
            }
        }
        objects = objects.putAll(dirty.map { (ref, array) -> ref to array.withSynchronizedMirror(array.contents) })
    }

    /**
     * Finalizes selected execution. Only exact equality restores the original graph root. A comparison that
     * cannot be completed is [HostGraphComparison.Unprovable] with the failure that stopped it.
     */
    fun finalizeHostGraph(checkpoint: HostGraphCheckpoint, guestRoots: List<Value>): HostGraphComparison {
        checkpoint.consume(this)
        val result = hostBoundary({ failure ->
            if (failure is HostGraphOwnershipException) {
                forgetHostGraph()
                throw failure
            }
            HostGraphComparison.Unprovable(failure)
        }) {
            hostGraph = hostGraph.refresh()
            pruneHostGraphToGuestRoots(guestRoots + checkpoint.selectionRoots)
            compactHostGraph()
            HostGraphComparator().compare(checkpoint.graph, hostGraph, checkpoint.expectedMirrorElements)
        }
        if (result == HostGraphComparison.Equal && !checkpoint.synchronizedDirtyMirrors) {
            hostGraph = checkpoint.graph
        }
        return result
    }

    fun restoreHostGraph(checkpoint: HostGraphCheckpoint) {
        checkpoint.consume(this)
        hostGraph = checkpoint.graph
        objects = checkpoint.objects
    }

    fun importHostObject(raw: Any, type: VirtualType): HeapRef {
        requireHostOwned(raw)
        findHostObject(raw)?.let { return it }
        if (raw.javaClass.isArray) error("Java arrays must be imported as TrackedArray")
        require(type.isReference) { "Imported host object requires a reference return tag, not $type" }
        return allocateHostObject(virtualTypeOfHostClass(raw.javaClass), raw)
    }

    fun importHostNode(nodeId: HostGraphNodeId, type: VirtualType, detachedRaw: Any): HeapRef {
        objects.refFor(nodeId)?.let { return it }
        val raw = requireNotNull(hostGraph.objectAt(nodeId)) { "Cannot import absent host graph node $nodeId" }
        require(raw.javaClass == detachedRaw.javaClass) { "$nodeId changed runtime class during import" }
        if (raw.javaClass.isArray) return importReturnedArray(raw)
        require(type.isReference) { "Imported host node requires a reference return tag, not $type" }
        return allocate(hostObject(raw, nodeId))
    }

    fun importReturnedArray(raw: Any): HeapRef {
        findHostObject(raw)?.let { return it }
        require(raw.javaClass.isArray) { "Not a Java array: ${raw.javaClass.name}" }
        val values = admitHostSeeds(
            listOf(HostSeed(Value.Known(raw, virtualTypeOfHostClass(raw.javaClass)))),
        )
        return values.single().let { (it as Value.Known).raw as HeapRef }
    }

    fun importHostObjects(raws: List<Any>): IdentityHashMap<Any, HeapRef> {
        val admitted = admitHostSeeds(
            raws.map { raw -> HostSeed(Value.Known(raw, virtualTypeOfHostClass(raw.javaClass))) },
        )
        return IdentityHashMap<Any, HeapRef>().apply {
            raws.zip(admitted).forEach { (raw, value) -> put(raw, (value as Value.Known).raw as HeapRef) }
        }
    }

    fun copy(): HeapStore = HeapStore(ids, objects, hostGraph)

    internal fun hostGraphVersionToken(): Any = hostGraph

    internal fun trackedFieldCompatibilityKey(): CompatibilityMapKey<Int, TrackedFieldCompatibility> =
        objects.trackedFields

    /**
     * This store's successor after a call: the heaps of one outcome partition of callee [paths], all descended
     * from this store, joined object by object. An object only some paths hold was allocated after the fork,
     * and no value common to every path can reach it, so it is left out. An object this store holds exists on
     * every path, because Dalvik never frees a referenced object. Callers partition by tracked-field
     * compatibility first, so every path agrees on each tracked field's accessibility.
     */
    fun merged(paths: List<HeapStore>, superclasses: TypeHierarchy): HeapStore {
        require(paths.isNotEmpty())
        requireSameRun(paths)
        val first = paths.first()
        require(paths.all { it.objects.trackedFields == first.objects.trackedFields }) {
            "Tracked-field accessibility must be partitioned before executable heap merge"
        }
        val sameGraph = paths.all { it.hostGraph === first.hostGraph }
        if (paths.all { it.objects === first.objects }) {
            return first.copy().also { if (!sameGraph) it.forgetHostGraph() }
        }
        var merged = first.objects
        val joined = ArrayList<Pair<HeapRef, HeapObject>>()
        for (id in first.objects.ids) {
            val versions = paths.map { it.objects[id] }
            if (versions.any { it == null }) {
                check(objects[id] == null) { "Heap object $id predates the call but is missing from a callee path" }
                merged = merged.remove(id)
                continue
            }
            val obj = mergeObject(versions.requireNoNulls(), superclasses, sameGraph)
            if (obj !== versions.first()) joined += HeapRef(id) to obj
        }
        return HeapStore(ids, merged.putAll(joined), if (sameGraph) first.hostGraph else HostGraphState.empty())
    }

    /**
     * This store with the named cells joined over [arms], op arms that all start from this store. A cell no arm
     * names is unchanged on every arm, so it keeps this store's value without being visited.
     */
    fun reconciled(
        arms: List<HeapStore>,
        arrayRanges: Map<HeapRef, List<IntRange>>,
        objectFields: Map<HeapRef, Set<String>>,
        superclasses: TypeHierarchy,
    ): HeapStore {
        requireSameRun(arms)
        val sameGraph = arms.all { it.hostGraph === hostGraph }
        val joined = (arrayRanges.keys + objectFields.keys).map { ref ->
            val versions = arms.map { arm ->
                checkNotNull(arm.objects[ref.id]) { "Heap object $ref predates the op but is missing from an arm" }
            }
            ref to mergeObject(versions, superclasses, sameGraph, arrayRanges[ref], objectFields[ref])
        }
        return HeapStore(ids, objects.putAll(joined), hostGraph)
    }

    private fun requireSameRun(stores: List<HeapStore>) =
        require(stores.all { it.ids === ids }) { "Heap stores from different runs cannot be joined: their refs collide" }

    companion object {
        /**
         * The join of one object's versions over several paths of one run. [sameGraph] says whether host-graph
         * contents are comparable across the paths. [indices] and [fields] name the cells to join; every other
         * cell keeps the first version's value, so a caller may restrict them only to cells no path changed.
         */
        internal fun mergeObject(
            versions: List<HeapObject>,
            superclasses: TypeHierarchy,
            sameGraph: Boolean,
            indices: List<IntRange>? = null,
            fields: Set<String>? = null,
        ): HeapObject {
            val first = versions.first()
            if (versions.any { it is HeapObject.TrackedObject } && versions.any { it !is HeapObject.TrackedObject }) {
                return constructedOnSomePaths(versions)
            }
            return when (first) {
                is HeapObject.HostObject -> {
                    val hosts = versions.sameShape<HeapObject.HostObject> { it.type == first.type }
                    val contents = joinedContents(hosts, sameGraph) ?: HostGraphContents.Unknown
                    val metadata = first.reflectedMethodMetadata
                        .takeIf { candidate -> hosts.all { it.reflectedMethodMetadata == candidate } }
                    if (contents == first.contents && metadata == first.reflectedMethodMetadata) {
                        first
                    } else {
                        HeapObject.HostObject(first.type, contents, metadata)
                    }
                }
                is HeapObject.GuestThrowable -> {
                    val throwables = versions.sameShape<HeapObject.GuestThrowable> { it.type == first.type }
                    val cause = joinThrowableCauses(throwables.map(HeapObject.GuestThrowable::cause), superclasses)
                    val contents = joinedContents(throwables, sameGraph) ?: HostGraphContents.Unknown
                    if (contents == first.contents && cause == first.cause) first
                    else HeapObject.GuestThrowable(first.type, contents, cause)
                }
                is HeapObject.TrackedArray -> {
                    val arrays = versions.sameShape<HeapObject.TrackedArray> { it.type == first.type && it.length == first.length }
                    val contents = joinedContents(arrays, sameGraph)
                    val dirty = contents is HostGraphContents.Known && arrays.any { it.mirrorDirty }
                    var elements = first.elementsRoot()
                    if (!arrays.all { it.elementsRoot() === first.elementsRoot() }) {
                        for (range in indices ?: listOf(0 until first.length)) {
                            for (index in range) {
                                val value = joinAll(arrays.map { it.element(index) }, superclasses) ?: Value.Top
                                if (value != elements[index]) elements = elements.replacingAt(index, value)
                            }
                        }
                    }
                    if (elements === first.elementsRoot() && contents == first.contents && dirty == first.mirrorDirty) {
                        first
                    } else {
                        first.withElementsRoot(elements, contents, dirty)
                    }
                }
                is HeapObject.TrackedObject -> {
                    val tracked = versions.sameShape<HeapObject.TrackedObject> { it.type == first.type }
                    val uninitialized = tracked.any { it.uninitialized }
                    if (tracked.all { it.contentsRootToken() === first.contentsRootToken() }) {
                        return if (uninitialized == first.uninitialized) first else tracked.first { it.uninitialized }
                    }
                    val joined = LinkedHashMap(first.fields())
                    for (key in fields ?: first.fields().keys) {
                        val cells = tracked.map { checkNotNull(it.field(key)) { "${first.type} has no field $key" } }
                        joined[key] = joinAll(cells, superclasses) ?: Value.Top
                    }
                    HeapObject.TrackedObject(first.type, uninitialized, joined)
                }
                is HeapObject.TrackedField -> {
                    val trackedFields = versions.sameShape<HeapObject.TrackedField> { it.metadata == first.metadata }
                    if (trackedFields.all { it.accessibility == first.accessibility }) first
                    else HeapObject.TrackedField(first.metadata, FieldAccessibility.UNKNOWN)
                }
            }
        }

        /** The common host-graph location when every version shares one graph and one node; null when none has one. */
        private fun joinedContents(versions: List<HostBacked>, sameGraph: Boolean): HostGraphContents? {
            val first = versions.first().contents
            return when {
                versions.all { it.contents == null } -> null
                sameGraph && versions.all { it.contents == first } -> first
                else -> HostGraphContents.Unknown
            }
        }

        /**
         * A receiver some paths constructed into a host object and others left tracked. It is not initialized on
         * every path, and no path's field cells describe the host object, so every field is [Value.Top].
         */
        private fun constructedOnSomePaths(versions: List<HeapObject>): HeapObject.TrackedObject {
            val tracked = versions.filterIsInstance<HeapObject.TrackedObject>()
            val shape = tracked.first()
            versions.sameShape<HeapObject> {
                it is HeapObject.HostObject || it is HeapObject.GuestThrowable ||
                    it is HeapObject.TrackedObject && it.type == shape.type
            }
            return HeapObject.TrackedObject(shape.type, uninitialized = true, shape.fields().mapValues { Value.Top })
        }

        /** [this] as [T]s; one heap id names one allocation, whose kind, type, length and field metadata never change. */
        private inline fun <reified T : HeapObject> List<HeapObject>.sameShape(same: (T) -> Boolean): List<T> = map { version ->
            (version as? T)?.takeIf(same) ?: error("One heap id names objects of different shapes: ${first()} and $version")
        }
    }

    override fun toString(): String = "HeapStore($objects)"
}

private fun PersistentList<Value>.mutateValues(values: List<Value>): PersistentList<Value> {
    require(size == values.size)
    var updated = this
    for (index in indices) if (updated[index] != values[index]) updated = updated.replacingAt(index, values[index])
    return updated
}

private fun PersistentList<Value>.mapValues(transform: (Value) -> Value): PersistentList<Value> {
    var updated = this
    for (index in indices) {
        val value = transform(this[index])
        if (value != this[index]) updated = updated.replacingAt(index, value)
    }
    return updated
}

/**
 * The longest array the heap tracks element by element. The length comes from analysed code, and each
 * tracked slot costs host memory, so a longer allocation is modelled as an unknown array rather than built.
 * 2^20 is above the default step budget, so no run could write every slot of a longer array one by one.
 */
internal const val MAX_TRACKED_ARRAY_LENGTH: Int = 1 shl 20

/**
 * Java's default for a fresh array element. A reference component gets `Known(null, T)` -- DEFINITELY
 * null (spec §4.1), not `Unknown`, because "every element of a fresh reference array is null" is a
 * provable fact and answering `Unknown` would throw it away.
 *
 * [componentType] is always a real component, because a tracked array's own type is always a real array
 * descriptor (object over tag) and `allocateArray` rejects anything else. There is deliberately no
 * non-array case here to be lenient about.
 */
internal fun defaultElementValue(componentType: VirtualType): Value = Value.Known(
    when (componentType.descriptor) {
        "Z" -> false
        "B" -> 0.toByte()
        "S" -> 0.toShort()
        "C" -> '\u0000'
        "I" -> 0
        "J" -> 0L
        "F" -> 0f
        "D" -> 0.0
        else -> null
    },
    componentType,
)

package org.cf.smalivm.core

import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Method
import java.util.IdentityHashMap
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.persistentListOf

/**
 * The source of heap identities for ONE run. Deliberately **not** copied when a path forks, and that
 * is a soundness decision rather than an optimization.
 *
 * If each forked [HeapStore] carried its own counter, `if (p) a = new int[1] else a = new int[1]` would
 * allocate id `k` on BOTH paths; `Value.agreesWith` compares heap ids, so consensus would report a
 * single `Known` array for two genuinely distinct allocations -- and a consumer reading that as "one
 * object" could eliminate an allocation another path still relies on. Sharing the counter makes the two
 * ids distinct, so they disagree and the join answers `Unknown`: conservative, and true.
 *
 * Ids stay reproducible because the walk is: the frontier is LIFO, transition order is fixed, and the
 * session reads no clock at the default [Limits]. Two runs of the same input allocate the same ids.
 *
 * Pinned by `HeapStoreTest.twoPathsAllocatingSeparatelyGetDistinctIdsAndDoNotAgree`.
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
    val objects: PersistentMap<Int, HeapObject>,
    val nodeRefs: PersistentMap<Int, HeapRef>,
    val expectedMirrorElements: Map<Int, List<Any?>>,
    val selectionRoots: List<Value>,
    val mirroredArrayRefs: PersistentSet<Int>,
    val dirtyMirroredArrayRefs: PersistentSet<Int>,
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
    val objects: PersistentMap<Int, HeapObject>,
    val hostGraph: HostGraphState,
    val nodeRefs: PersistentMap<Int, HeapRef>,
    val mirroredArrayRefs: PersistentSet<Int>,
    val dirtyMirroredArrayRefs: PersistentSet<Int>,
)

internal data class ArrayReplacement(val ref: HeapRef, val elements: List<Value>)

/**
 * One path's heap (spec §4.2): instance state and object identity, resolved **per path** rather than
 * per frame, so that from Plan 9 a callee's mutation is visible to its caller on the same path while
 * registers stay method-local.
 *
 * [copy] creates a wrapper over the same persistent object-map root. Objects are immutable versions, so
 * writes replace only the addressed object and map path. Earlier frames and sibling paths keep their roots.
 */
internal class HeapStore private constructor(
    private val ids: HeapIds,
    private var objects: PersistentMap<Int, HeapObject>,
    private var hostGraph: HostGraphState,
    private var nodeRefs: PersistentMap<Int, HeapRef>,
    private var trackedFields: CompatibilityMapKey<Int, TrackedFieldCompatibility>,
    private var mirroredArrayRefs: PersistentSet<Int>,
    private var dirtyMirroredArrayRefs: PersistentSet<Int>,
) {
    constructor(ids: HeapIds) :
        this(
            ids,
            persistentMapOf(),
            HostGraphState.empty(),
            persistentMapOf(),
            CompatibilityMapKey.empty(),
            persistentSetOf(),
            persistentSetOf(),
        )

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
        return allocate(
            HeapObject.GuestThrowable(type, knownContents(nodeId, hostThrowable), modeledCause),
            nodeId,
        )
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
        return allocate(hostObject(raw, nodeId), nodeId)
    }

    fun findHostObject(raw: Any): HeapRef? {
        val nodeId = hostGraph.nodeIdOf(raw) ?: return null
        return findRef(nodeId)
    }

    fun hostObjectAt(ref: HeapRef): Any? = when (val obj = objects[ref.id]) {
        is HeapObject.HostObject -> resolve(obj.contents)
        is HeapObject.GuestThrowable -> resolve(obj.contents)
        is HeapObject.TrackedArray -> obj.mirror?.let(::resolve)
        is HeapObject.TrackedField, is HeapObject.TrackedObject, null -> null
    }

    fun hostObjectAt(nodeId: HostGraphNodeId): Any? = hostGraph.objectAt(nodeId)

    fun hostNodeId(raw: Any): HostGraphNodeId? = hostGraph.nodeIdOf(raw)

    fun hostGraphObjects(): Map<Int, Any> = hostGraph.objectsById()

    fun refForNode(nodeId: HostGraphNodeId): HeapRef? = findRef(nodeId)

    fun mirroredArrays(): List<HostArrayMirror> = mirroredArrayRefs.mapNotNull { id ->
        val obj = objects[id]
        val array = obj as? HeapObject.TrackedArray ?: return@mapNotNull null
        val known = array.mirror as? HostGraphContents.Known ?: return@mapNotNull null
        val raw = hostGraph.objectAt(known.nodeId) ?: return@mapNotNull null
        HostArrayMirror(HeapRef(id), array.type, array.length, array.elements(), raw)
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

    private fun findRef(nodeId: HostGraphNodeId): HeapRef? = nodeRefs[nodeId.id]

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
                return allocate(hostObject(raw, nodeId), nodeId).also { refs[raw] = it }
            }

            val override = overrides[raw]
            val type = override?.arrayType ?: virtualTypeOfHostClass(raw.javaClass)
            val length = ReflectArray.getLength(raw)
            val ref = ids.allocate()
            refs[raw] = ref
            registerNodeRef(nodeId, ref)
            objects = objects.putting(
                ref.id,
                HeapObject.TrackedArray(
                    type,
                    Array(length) { defaultElementValue(requireNotNull(type.componentType)) },
                    knownContents(nodeId, raw),
                ),
            )
            val component = requireNotNull(type.componentType)
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
     * A fresh ordinary object, **uninitialized** (resolved decision 8) -- `new-instance`'s allocation.
     * Minted from the same shared [ids] counter as every other allocation, which is what makes two
     * `new-instance` of one type two distinct identities on two forked paths (adjudication 8: identity
     * is the id, never the type; see [HeapObject.TrackedObject]).
     */
    fun allocateObject(type: VirtualType, fields: Map<String, Value> = emptyMap()): HeapRef =
        allocate(HeapObject.TrackedObject(type, uninitialized = true, fields))

    private fun allocate(obj: HeapObject, nodeId: HostGraphNodeId? = null): HeapRef = ids.allocate().also { ref ->
        objects = objects.putting(ref.id, obj)
        updateArrayIndexes(ref.id, obj)
        if (nodeId != null) registerNodeRef(nodeId, ref)
        if (obj is HeapObject.TrackedField) {
            trackedFields = trackedFields.put(ref.id, TrackedFieldCompatibility(obj.metadata, obj.accessibility))
        }
    }

    private fun registerNodeRef(nodeId: HostGraphNodeId, ref: HeapRef) {
        val existing = nodeRefs[nodeId.id]
        require(existing == null || existing == ref) { "$nodeId is already owned by $existing, not $ref" }
        nodeRefs = nodeRefs.putting(nodeId.id, ref)
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
        replaceObject(ref, requireNotNull(arrayAt(ref)).withElements(startIndex, values))
        return true
    }

    fun transactionCheckpoint(): HeapTransactionCheckpoint =
        HeapTransactionCheckpoint(objects, hostGraph, nodeRefs, mirroredArrayRefs, dirtyMirroredArrayRefs)

    fun verifyHostGraphCheckpoint(checkpoint: HostGraphCheckpoint) = checkpoint.verify(this)

    /** Commits all validated array lattice replacements under one persistent heap root. */
    fun applyArrayReplacementsAtomically(replacements: List<ArrayReplacement>) {
        var updatedObjects = objects
        for (replacement in replacements) {
            val current = updatedObjects[replacement.ref.id] as? HeapObject.TrackedArray
                ?: error("Tracked array ${replacement.ref} disappeared during host copy-back")
            require(replacement.elements.size == current.length) {
                "Copy-back for ${replacement.ref} has ${replacement.elements.size} elements, expected ${current.length}"
            }
            if (replacement.elements.indices.all { current.element(it) == replacement.elements[it] }) continue
            updatedObjects = updatedObjects.putting(
                replacement.ref.id,
                current.withElementsRoot(
                    current.elementsRoot().mutateValues(replacement.elements),
                    current.mirror,
                    dirty = false,
                ),
            )
        }
        if (updatedObjects !== objects) {
            objects = updatedObjects
            replacements.forEach { replacement ->
                objects[replacement.ref.id]?.let { updateArrayIndexes(replacement.ref.id, it) }
            }
        }
    }

    /** Restores post-execution roots, then invalidates facts affected by failed copy-back as one update. */
    fun rollbackFailedCopyback(
        checkpoint: HeapTransactionCheckpoint,
        affectedArrays: Set<HeapRef>,
    ) {
        var invalidated = checkpoint.objects
        for ((id, obj) in checkpoint.objects) {
            val replacement = when (obj) {
                is HeapObject.HostObject -> obj.withContents(HostGraphContents.Unknown)
                is HeapObject.GuestThrowable -> obj.withContents(HostGraphContents.Unknown)
                is HeapObject.TrackedArray -> {
                    val component = requireNotNull(obj.type.componentType)
                    val values = if (HeapRef(id) in affectedArrays) {
                        obj.elementsRoot().mapValues { Value.Unknown(component) }
                    } else {
                        obj.elementsRoot()
                    }
                    if (obj.mirror == null && values === obj.elementsRoot()) obj
                    else obj.withElementsRoot(values, if (obj.mirror == null) null else HostGraphContents.Unknown)
                }
                is HeapObject.TrackedField, is HeapObject.TrackedObject -> obj
            }
            if (replacement !== obj) invalidated = invalidated.putting(id, replacement)
        }
        objects = invalidated
        mirroredArrayRefs = checkpoint.mirroredArrayRefs
        dirtyMirroredArrayRefs = checkpoint.dirtyMirroredArrayRefs
        for ((id, obj) in invalidated) {
            if (obj is HeapObject.TrackedArray) updateArrayIndexes(id, obj)
        }
        hostGraph = HostGraphState.empty()
        nodeRefs = persistentMapOf()
    }

    private fun javaValue(value: Value, graph: HostGraphState = hostGraph): Any? {
        if (value !is Value.Known) {
            throw HostGraphRepresentationException("A Java array mirror cannot represent $value")
        }
        val raw = value.raw
        if (raw !is HeapRef) return raw
        return when (val obj = objects[raw.id]) {
            is HeapObject.HostObject -> (obj.contents as? HostGraphContents.Known)?.let { graph.objectAt(it.nodeId) }
            is HeapObject.GuestThrowable -> (obj.contents as? HostGraphContents.Known)?.let { graph.objectAt(it.nodeId) }
            is HeapObject.TrackedArray -> (obj.mirror as? HostGraphContents.Known)?.let { graph.objectAt(it.nodeId) }
            is HeapObject.TrackedField, is HeapObject.TrackedObject, null -> null
        } ?: throw HostGraphRepresentationException("$raw has no known Java object")
    }

    fun setArrayMirror(ref: HeapRef, raw: Any): HostGraphNodeId {
        val current = arrayAt(ref) ?: error("No tracked array at $ref")
        require(raw.javaClass.isArray) { "A tracked-array mirror must be a Java array" }
        val nodeId = addHostRoots(listOf(raw)).single()
        val oldNodeId = (current.mirror as? HostGraphContents.Known)?.nodeId
        if (oldNodeId != null && oldNodeId != nodeId) nodeRefs = nodeRefs.removing(oldNodeId.id)
        registerNodeRef(nodeId, ref)
        replaceObject(ref, current.withSynchronizedMirror(knownContents(nodeId, raw)))
        return nodeId
    }

    private fun invalidateHostGraphContents() {
        val graphBackedIds = (nodeRefs.values.map(HeapRef::id) + mirroredArrayRefs + dirtyMirroredArrayRefs).toSet()
        for (id in graphBackedIds) {
            val obj = objects[id] ?: continue
            val replacement = when (obj) {
                is HeapObject.HostObject -> obj.withContents(HostGraphContents.Unknown)
                is HeapObject.GuestThrowable -> obj.withContents(HostGraphContents.Unknown)
                is HeapObject.TrackedArray ->
                    if (obj.mirror == null) obj else obj.withMirror(HostGraphContents.Unknown)
                is HeapObject.TrackedField, is HeapObject.TrackedObject -> obj
            }
            if (replacement !== obj) replaceObject(HeapRef(id), replacement)
        }
        if (hostGraph.nodeCount > 0) hostGraph = HostGraphState.empty()
        nodeRefs = persistentMapOf()
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
        registerNodeRef(nodeId, ref)
        replaceObject(ref, hostObject(raw, nodeId))
        return true
    }

    private fun replaceObject(ref: HeapRef, replacement: HeapObject) {
        objects = objects.putting(ref.id, replacement)
        updateArrayIndexes(ref.id, replacement)
        trackedFields = when (replacement) {
            is HeapObject.TrackedField -> trackedFields.put(
                ref.id,
                TrackedFieldCompatibility(replacement.metadata, replacement.accessibility),
            )
            else -> trackedFields.remove(ref.id)
        }
    }

    private fun updateArrayIndexes(id: Int, obj: HeapObject) {
        val array = obj as? HeapObject.TrackedArray
        mirroredArrayRefs = if (array?.mirror is HostGraphContents.Known) {
            mirroredArrayRefs.adding(id)
        } else {
            mirroredArrayRefs.removing(id)
        }
        dirtyMirroredArrayRefs = if (array?.mirrorDirty == true) {
            dirtyMirroredArrayRefs.adding(id)
        } else {
            dirtyMirroredArrayRefs.removing(id)
        }
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
            when (val obj = objects[ref.id]) {
                is HeapObject.TrackedArray -> {
                    val component = requireNotNull(obj.type.componentType)
                    when (val mirror = obj.mirror) {
                        is HostGraphContents.Known -> hostRoots += mirror.nodeId
                        HostGraphContents.Unknown -> collapseAllHostNodes = true
                        null -> Unit
                    }
                    val collapsed = obj.elements().map { old ->
                        if (old.type == null) old else Value.Unknown(component)
                    }
                    val replacement = obj.withElementsRoot(
                        obj.elementsRoot().mutateValues(collapsed),
                        if (obj.mirror == null) null else HostGraphContents.Unknown,
                    )
                    if (replacement.elementsRoot() !== obj.elementsRoot() || replacement.mirror != obj.mirror) {
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
                is HeapObject.TrackedField -> Unit
                is HeapObject.HostObject -> when (val contents = obj.contents) {
                    is HostGraphContents.Known -> hostRoots += contents.nodeId
                    HostGraphContents.Unknown -> collapseAllHostNodes = true
                }
                is HeapObject.GuestThrowable -> when (val contents = obj.contents) {
                    is HostGraphContents.Known -> hostRoots += contents.nodeId
                    HostGraphContents.Unknown -> collapseAllHostNodes = true
                }
                null -> Unit
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
        val invalidatedIds = nodeIds.mapTo(mutableSetOf(), HostGraphNodeId::id)
        var updatedObjects = objects
        val graphBackedIds = (nodeRefs.values.map(HeapRef::id) + mirroredArrayRefs + dirtyMirroredArrayRefs).toSet()
        for (id in graphBackedIds) {
            val obj = objects[id] ?: continue
            val replacement = when (obj) {
                is HeapObject.HostObject -> when (val contents = obj.contents) {
                    is HostGraphContents.Known ->
                        if (contents.nodeId.id in invalidatedIds) {
                            obj.withContents(HostGraphContents.Unknown)
                        } else {
                            obj
                        }
                    HostGraphContents.Unknown -> obj
                }
                is HeapObject.GuestThrowable -> when (val contents = obj.contents) {
                    is HostGraphContents.Known ->
                        if (contents.nodeId.id in invalidatedIds) {
                            obj.withContents(HostGraphContents.Unknown)
                        } else {
                            obj
                        }
                    HostGraphContents.Unknown -> obj
                }
                is HeapObject.TrackedArray -> {
                    val nodeId = (obj.mirror as? HostGraphContents.Known)?.nodeId
                    if (nodeId?.id !in invalidatedIds) {
                        obj
                    } else {
                        val component = requireNotNull(obj.type.componentType)
                        obj.withElementsRoot(
                            obj.elementsRoot().mapValues { Value.Unknown(component) },
                            HostGraphContents.Unknown,
                        )
                    }
                }
                is HeapObject.TrackedField, is HeapObject.TrackedObject -> obj
            }
            if (replacement !== obj) updatedObjects = updatedObjects.putting(id, replacement)
        }

        val compaction = compactHostGraph(updatedObjects) ?: return invalidateCompleteHostGraphForOpaqueCall()
        applyHostGraphCompaction(compaction)
    }

    private data class HostGraphCompaction(
        val objects: PersistentMap<Int, HeapObject>,
        val graph: HostGraphState,
        val nodeRefs: PersistentMap<Int, HeapRef>,
    )

    private fun compactHostGraph(sourceObjects: PersistentMap<Int, HeapObject>): HostGraphCompaction? {
        val surviving = nodeRefs.values.distinctBy(HeapRef::id).sortedBy(HeapRef::id).mapNotNull { ref ->
            val obj = sourceObjects[ref.id] ?: return@mapNotNull null
            val oldNodeId = knownNodeId(obj) ?: return@mapNotNull null
            val raw = hostGraph.objectAt(oldNodeId) ?: return null
            Triple(ref.id, obj, raw)
        }
        val rebuilt = try {
            HostGraphState.fromIdentityRoots(surviving.map { it.third })
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (_: Throwable) {
            return null
        }
        if (!rebuilt.isComplete) return null
        val compacted = if (rebuilt.hasSameIdentityLayout(hostGraph)) hostGraph else rebuilt

        return rekeyHostGraph(sourceObjects, compacted, surviving)
    }

    private fun rekeyHostGraph(
        sourceObjects: PersistentMap<Int, HeapObject>,
        compacted: HostGraphState,
        surviving: List<Triple<Int, HeapObject, Any>>,
    ): HostGraphCompaction? {
        var compactedObjects = sourceObjects
        var compactedRefs: PersistentMap<Int, HeapRef> = persistentMapOf()
        for ((id, obj, raw) in surviving) {
            val nodeId = compacted.nodeIdOf(raw) ?: return null
            val rekeyed = if (knownNodeId(obj) == nodeId) {
                obj
            } else {
                when (obj) {
                    is HeapObject.HostObject -> obj.withContents(HostGraphContents.Known(nodeId))
                    is HeapObject.GuestThrowable -> HeapObject.GuestThrowable(
                        obj.type,
                        HostGraphContents.Known(nodeId),
                        obj.cause,
                    )
                    is HeapObject.TrackedArray -> obj.withMirror(HostGraphContents.Known(nodeId))
                    is HeapObject.TrackedField, is HeapObject.TrackedObject -> error("Graph-independent object in host roots")
                }
            }
            if (rekeyed !== obj) compactedObjects = compactedObjects.putting(id, rekeyed)
            val existing = compactedRefs[nodeId.id]
            require(existing == null || existing == HeapRef(id)) { "$nodeId is owned by both $existing and ${HeapRef(id)}" }
            compactedRefs = compactedRefs.putting(nodeId.id, HeapRef(id))
        }
        return HostGraphCompaction(compactedObjects, compacted, compactedRefs)
    }

    private fun applyHostGraphCompaction(compaction: HostGraphCompaction) {
        if (compaction.objects !== objects) {
            val indexedArrays = mirroredArrayRefs + dirtyMirroredArrayRefs
            objects = compaction.objects
            indexedArrays.forEach { id -> objects[id]?.let { updateArrayIndexes(id, it) } }
        }
        hostGraph = compaction.graph
        nodeRefs = compaction.nodeRefs
    }

    private fun invalidateCompleteHostGraphForOpaqueCall() {
        val graphBackedIds = (nodeRefs.values.map(HeapRef::id) + mirroredArrayRefs + dirtyMirroredArrayRefs).toSet()
        for (id in graphBackedIds) {
            val obj = objects[id] ?: continue
            val replacement = when (obj) {
                is HeapObject.HostObject -> obj.withContents(HostGraphContents.Unknown)
                is HeapObject.GuestThrowable -> obj.withContents(HostGraphContents.Unknown)
                is HeapObject.TrackedArray -> if (obj.mirror == null) {
                    obj
                } else {
                    val component = requireNotNull(obj.type.componentType)
                    obj.withElementsRoot(
                        obj.elementsRoot().mapValues { Value.Unknown(component) },
                        HostGraphContents.Unknown,
                    )
                }
                is HeapObject.TrackedField, is HeapObject.TrackedObject -> obj
            }
            if (replacement !== obj) replaceObject(HeapRef(id), replacement)
        }
        nodeRefs = persistentMapOf()
        if (hostGraph.nodeCount > 0) hostGraph = HostGraphState.empty()
    }

    /** Detaches this path's complete Java graph after one backend accepts execution. */
    fun detachHostGraph(selectionRoots: List<Value>): HostGraphCheckpoint {
        if (!pruneHostGraphToGuestRoots(selectionRoots)) {
            invalidateHostGraphContents()
            throw HostGraphRepresentationException("Cannot prove the selected guest host-graph roots")
        }
        val expectedMirrors = dirtyMirrorExpectedValues(hostGraph)
        val checkpoint = HostGraphCheckpoint(
            this,
            hostGraph,
            objects,
            nodeRefs,
            expectedMirrors,
            selectionRoots,
            mirroredArrayRefs,
            dirtyMirroredArrayRefs,
        )
        val clone = cloneHostGraph(checkpoint.graph)
        try {
            hostGraph = clone
            synchronizeDirtyMirrors()
        } catch (fatal: VirtualMachineError) {
            hostGraph = checkpoint.graph
            objects = checkpoint.objects
            nodeRefs = checkpoint.nodeRefs
            mirroredArrayRefs = checkpoint.mirroredArrayRefs
            dirtyMirroredArrayRefs = checkpoint.dirtyMirroredArrayRefs
            throw fatal
        } catch (failure: Throwable) {
            hostGraph = checkpoint.graph
            objects = checkpoint.objects
            nodeRefs = checkpoint.nodeRefs
            mirroredArrayRefs = checkpoint.mirroredArrayRefs
            dirtyMirroredArrayRefs = checkpoint.dirtyMirroredArrayRefs
            throw failure
        }
        return checkpoint
    }

    private fun pruneHostGraphToGuestRoots(values: List<Value>): Boolean {
        val guestRefs = reachableRefs(values.mapNotNull { (it as? Value.Known)?.raw as? HeapRef })
        val liveRoots = guestRefs.mapNotNull { ref ->
            val obj = objects[ref.id] ?: return@mapNotNull null
            val nodeId = knownNodeId(obj) ?: return@mapNotNull null
            val raw = hostGraph.objectAt(nodeId) ?: return false
            Triple(ref.id, obj, raw)
        }
        val rebuilt = try {
            HostGraphState.fromIdentityRoots(liveRoots.map { it.third })
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (_: Throwable) {
            return false
        }
        if (!rebuilt.isComplete) return false
        var updatedObjects = objects
        val graphBackedRefs = nodeRefs.values.distinctBy(HeapRef::id)
        for (ref in graphBackedRefs) {
            val id = ref.id
            val obj = objects[id] ?: continue
            val nodeId = knownNodeId(obj) ?: continue
            val raw = hostGraph.objectAt(nodeId) ?: return false
            if (rebuilt.nodeIdOf(raw) != null) continue
            val replacement = when (obj) {
                is HeapObject.HostObject -> obj.withContents(HostGraphContents.Unknown)
                is HeapObject.GuestThrowable -> obj.withContents(HostGraphContents.Unknown)
                is HeapObject.TrackedArray -> obj.withSynchronizedMirror(HostGraphContents.Unknown)
                is HeapObject.TrackedField, is HeapObject.TrackedObject -> obj
            }
            if (replacement !== obj) updatedObjects = updatedObjects.putting(id, replacement)
        }
        val surviving = graphBackedRefs.mapNotNull { ref ->
            val obj = updatedObjects[ref.id] ?: return@mapNotNull null
            val oldNodeId = knownNodeId(obj) ?: return@mapNotNull null
            val raw = hostGraph.objectAt(oldNodeId) ?: return false
            if (rebuilt.nodeIdOf(raw) == null) return@mapNotNull null
            Triple(ref.id, obj, raw)
        }.sortedBy { it.first }
        val compacted = if (hostGraph.isComplete && rebuilt.hasSameIdentityLayout(hostGraph)) hostGraph else rebuilt
        val compaction = rekeyHostGraph(updatedObjects, compacted, surviving) ?: return false
        applyHostGraphCompaction(compaction)
        return true
    }

    private fun dirtyMirrorExpectedValues(graph: HostGraphState): Map<Int, List<Any?>> =
        dirtyMirroredArrayRefs.mapNotNull { id ->
            val array = objects[id] as? HeapObject.TrackedArray ?: return@mapNotNull null
            val nodeId = (array.mirror as? HostGraphContents.Known)?.nodeId ?: return@mapNotNull null
            nodeId.id to array.elements().map { javaValue(it, graph) }
        }.toMap()

    private fun synchronizeDirtyMirrors() {
        val dirty = dirtyMirroredArrayRefs.mapNotNull { id ->
            val array = objects[id] as? HeapObject.TrackedArray ?: return@mapNotNull null
            val nodeId = (array.mirror as? HostGraphContents.Known)?.nodeId ?: return@mapNotNull null
            val raw = requireNotNull(hostGraph.objectAt(nodeId)) { "No Java mirror for $nodeId" }
            Triple(id, array, raw)
        }
        if (dirty.isEmpty()) return

        for ((_, array, raw) in dirty) {
            require(ReflectArray.getLength(raw) == array.length) { "Java mirror length changed for ${array.type}" }
            array.elements().forEachIndexed { index, value ->
                try {
                    ReflectArray.set(raw, index, javaValue(value, hostGraph))
                } catch (failure: IllegalArgumentException) {
                    throw HostGraphRepresentationException("Cannot synchronize the Java array mirror", failure)
                }
            }
        }

        var updated = objects
        for ((id, array, _) in dirty) {
            updated = updated.putting(id, array.withSynchronizedMirror(array.mirror))
        }
        if (updated !== objects) {
            objects = updated
            dirty.forEach { (id, array, _) -> updateArrayIndexes(id, array.withSynchronizedMirror(array.mirror)) }
        }
    }

    /** Finalizes selected execution. Only exact equality restores the original graph root. */
    fun finalizeHostGraph(checkpoint: HostGraphCheckpoint, guestRoots: List<Value>): HostGraphComparison {
        checkpoint.consume(this)
        val result = try {
            hostGraph = hostGraph.refresh()
            if (!pruneHostGraphToGuestRoots(guestRoots + checkpoint.selectionRoots)) {
                invalidateHostGraphContents()
                return HostGraphComparison.Unprovable
            }
            val compacted = compactHostGraph(objects) ?: return HostGraphComparison.Unprovable
            applyHostGraphCompaction(compacted)
            HostGraphComparator().compare(checkpoint.graph, hostGraph, checkpoint.expectedMirrorElements)
        } catch (ownership: HostGraphOwnershipException) {
            invalidateHostGraphContents()
            throw ownership
        } catch (fatal: VirtualMachineError) {
            throw fatal
        } catch (_: Throwable) {
            HostGraphComparison.Unprovable
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
        nodeRefs = checkpoint.nodeRefs
        mirroredArrayRefs = checkpoint.mirroredArrayRefs
        dirtyMirroredArrayRefs = checkpoint.dirtyMirroredArrayRefs
    }

    fun importHostObject(raw: Any, type: VirtualType): HeapRef {
        requireHostOwned(raw)
        findHostObject(raw)?.let { return it }
        if (raw.javaClass.isArray) error("Java arrays must be imported as TrackedArray")
        require(type.isReference) { "Imported host object requires a reference return tag, not $type" }
        return allocateHostObject(virtualTypeOfHostClass(raw.javaClass), raw)
    }

    fun importHostNode(nodeId: HostGraphNodeId, type: VirtualType, detachedRaw: Any): HeapRef {
        findRef(nodeId)?.let { return it }
        val raw = requireNotNull(hostGraph.objectAt(nodeId)) { "Cannot import absent host graph node $nodeId" }
        require(raw.javaClass == detachedRaw.javaClass) { "$nodeId changed runtime class during import" }
        if (raw.javaClass.isArray) return importReturnedArray(raw)
        require(type.isReference) { "Imported host node requires a reference return tag, not $type" }
        return allocate(hostObject(raw, nodeId), nodeId)
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

    fun copy(): HeapStore = HeapStore(
        ids,
        objects,
        hostGraph,
        nodeRefs,
        trackedFields,
        mirroredArrayRefs,
        dirtyMirroredArrayRefs,
    )

    internal fun hostGraphVersionToken(): Any = hostGraph

    internal fun trackedFieldCompatibilityKey(): CompatibilityMapKey<Int, TrackedFieldCompatibility> = trackedFields

    companion object {
        /** Joins one outcome partition. An object absent from any path stays absent. */
        fun merge(stores: List<HeapStore>, superclasses: Superclasses): HeapStore {
            require(stores.isNotEmpty())
            val first = stores.first()
            val sameObjectRoot = stores.all { it.objects === first.objects }
            val sameGraph = stores.all { it.hostGraph === first.hostGraph }
            if (sameObjectRoot && sameGraph) return first.copy()
            if (sameObjectRoot) {
                var merged = first.objects
                var mergedMirrors = first.mirroredArrayRefs
                var mergedDirtyMirrors = first.dirtyMirroredArrayRefs
                val graphBackedRefs = stores.flatMap { it.nodeRefs.values }.map { it.id }.toSet()
                for (id in graphBackedRefs) {
                    val obj = mergeObject(stores.map { it.objects.getValue(id) }, superclasses, sameGraph = false)
                        ?: continue
                    merged = merged.putting(id, obj)
                    val array = obj as? HeapObject.TrackedArray
                    mergedMirrors = if (array?.mirror is HostGraphContents.Known) mergedMirrors.adding(id) else mergedMirrors.removing(id)
                    mergedDirtyMirrors = if (array?.mirrorDirty == true) mergedDirtyMirrors.adding(id) else mergedDirtyMirrors.removing(id)
                }
                return HeapStore(
                    first.ids,
                    merged,
                    HostGraphState.empty(),
                    persistentMapOf(),
                    first.trackedFields,
                    mergedMirrors,
                    mergedDirtyMirrors,
                )
            }
            val commonIds = first.objects.keys.filter { id -> stores.all { id in it.objects } }.toSet()
            var merged = first.objects
            for (id in first.objects.keys - commonIds) merged = merged.removing(id)
            var mergedNodeRefs: PersistentMap<Int, HeapRef> = persistentMapOf()
            var mergedTrackedFields = CompatibilityMapKey.empty<Int, TrackedFieldCompatibility>()
            var mergedMirrors: PersistentSet<Int> = persistentSetOf()
            var mergedDirtyMirrors: PersistentSet<Int> = persistentSetOf()
            for (id in commonIds) {
                val objects = stores.map { it.objects.getValue(id) }
                val obj = mergeObject(objects, superclasses, sameGraph)
                if (obj == null) {
                    merged = merged.removing(id)
                    continue
                }
                merged = merged.putting(id, obj)
                knownNodeId(obj)?.let { mergedNodeRefs = mergedNodeRefs.putting(it.id, HeapRef(id)) }
                (obj as? HeapObject.TrackedField)?.let {
                    mergedTrackedFields = mergedTrackedFields.put(
                        id,
                        TrackedFieldCompatibility(it.metadata, it.accessibility),
                    )
                }
                (obj as? HeapObject.TrackedArray)?.let { array ->
                    if (array.mirror is HostGraphContents.Known) mergedMirrors = mergedMirrors.adding(id)
                    if (array.mirrorDirty) mergedDirtyMirrors = mergedDirtyMirrors.adding(id)
                }
            }
            val graph = if (sameGraph) first.hostGraph else HostGraphState.empty()
            return HeapStore(
                first.ids,
                merged,
                graph,
                mergedNodeRefs,
                mergedTrackedFields,
                mergedMirrors,
                mergedDirtyMirrors,
            )
        }

        /** The joined object, the first path's own version when every path agrees, or null to drop it. */
        private fun mergeObject(
            objects: List<HeapObject>,
            superclasses: Superclasses,
            sameGraph: Boolean,
        ): HeapObject? {
            val first = objects.first()
            val sameObject = objects.all { it === first }
            return when (first) {
                is HeapObject.HostObject -> {
                    val hosts = objects.map { it as? HeapObject.HostObject ?: return null }
                    if (hosts.any { it.type != first.type }) null else {
                        val contents = if (sameGraph && hosts.all { it.contents == first.contents }) {
                            first.contents
                        } else {
                            HostGraphContents.Unknown
                        }
                        val metadata = first.reflectedMethodMetadata
                            .takeIf { candidate -> hosts.all { it.reflectedMethodMetadata == candidate } }
                        if (contents == first.contents && metadata == first.reflectedMethodMetadata) {
                            first
                        } else {
                            HeapObject.HostObject(first.type, contents, metadata)
                        }
                    }
                }
                is HeapObject.GuestThrowable -> {
                    val throwables = objects.map { it as? HeapObject.GuestThrowable ?: return null }
                    if (throwables.any { it.type != first.type }) null
                    else {
                    val cause = joinThrowableCauses(
                        throwables.map(HeapObject.GuestThrowable::cause),
                        superclasses,
                    )
                        val contents = if (sameGraph && throwables.all { it.contents == first.contents }) {
                            first.contents
                        } else {
                            HostGraphContents.Unknown
                        }
                        if (contents == first.contents && cause == first.cause) first
                        else HeapObject.GuestThrowable(first.type, contents, cause)
                    }
                }
                is HeapObject.TrackedArray -> {
                    val arrays = objects.map { it as? HeapObject.TrackedArray ?: return null }
                    if (arrays.any { it.type != first.type || it.length != first.length }) return null
                    val mirror = when {
                        arrays.all { it.mirror == null } -> null
                        sameGraph && arrays.all { it.mirror == first.mirror } -> first.mirror
                        else -> HostGraphContents.Unknown
                    }
                    val sameElements = arrays.all { it.elementsRoot() === first.elementsRoot() }
                    val dirty = mirror is HostGraphContents.Known && arrays.any { it.mirrorDirty }
                    val elements = if (sameElements) {
                        first.elementsRoot()
                    } else {
                        var joined = first.elementsRoot()
                        for (index in 0 until first.length) {
                            val value = joinAll(arrays.map { it.element(index) }, superclasses) ?: Value.Top
                            if (value != joined[index]) joined = joined.replacingAt(index, value)
                        }
                        joined
                    }
                    if (elements === first.elementsRoot() && mirror == first.mirror && dirty == first.mirrorDirty) {
                        first
                    } else {
                        first.withElementsRoot(elements, mirror, dirty)
                    }
                }
                is HeapObject.TrackedObject -> {
                    val tracked = objects.map { it as? HeapObject.TrackedObject ?: return null }
                    if (tracked.any { it.type != first.type || it.uninitialized != first.uninitialized }) return null
                    if (sameObject || tracked.all { it.contentsRootToken() === first.contentsRootToken() }) {
                        return first
                    }
                    val keys = linkedSetOf<String>()
                    tracked.forEach { keys.addAll(it.fields().keys) }
                    HeapObject.TrackedObject(
                        first.type,
                        first.uninitialized,
                        LinkedHashMap<String, Value>().apply {
                            for (key in keys) {
                                this[key] = joinAll(tracked.map { it.field(key) ?: Value.Top }, superclasses) ?: Value.Top
                            }
                        },
                    )
                }
                is HeapObject.TrackedField -> {
                    val fields = objects.map { it as? HeapObject.TrackedField ?: return null }
                    if (fields.any { it.metadata != first.metadata }) return null
                    require(fields.all { it.accessibility == first.accessibility }) {
                        "Tracked-field accessibility must be partitioned before executable heap merge"
                    }
                    first
                }
            }
        }

        private fun knownNodeId(obj: HeapObject): HostGraphNodeId? = when (obj) {
            is HeapObject.HostObject -> (obj.contents as? HostGraphContents.Known)?.nodeId
            is HeapObject.GuestThrowable -> (obj.contents as? HostGraphContents.Known)?.nodeId
            is HeapObject.TrackedArray -> (obj.mirror as? HostGraphContents.Known)?.nodeId
            is HeapObject.TrackedField, is HeapObject.TrackedObject -> null
        }
    }

    override fun toString(): String = "HeapStore(${objects.size} objects)"
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

package org.cf.smalivm.core

/** The resolver for [this] index's current snapshot, as a session started now would capture it. */
internal fun ClassIndex.currentResolver(): ProgramSnapshotResolver = resolver(currentSnapshot)

/** An invoker over [backends] alone, for tests that isolate one backend's contract. */
internal fun invokerOf(classIndex: ClassIndex, backends: List<Backend>): Invoker =
    Invoker(classIndex.currentResolver(), backends)

/** The invoker a session on a fresh VM over [classIndex] uses. */
internal fun defaultInvoker(classIndex: ClassIndex): Invoker =
    Invoker(classIndex.currentResolver(), InvocationServices(classIndex))

/**
 * The id the next allocation on [frame]'s path receives. Heap ids are run-wide, so the probe allocation on a
 * copy consumes that id: a second probe on the same run answers one higher when nothing else was allocated.
 */
internal fun probeNextHeapId(frame: Frame): Int =
    (frame.copy().allocateObject(VirtualType.OBJECT, emptyMap()).raw as HeapRef).id

/** Every heap object on [frame]'s path. Every id allocated so far is below the id a fresh allocation receives. */
internal fun heapRefsOf(frame: Frame): List<HeapRef> =
    (0 until probeNextHeapId(frame)).map(::HeapRef).filter { frame.heapObject(it) != null }

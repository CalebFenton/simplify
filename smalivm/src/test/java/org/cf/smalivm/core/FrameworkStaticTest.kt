package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FrameworkStaticTest {
    private val classIndex = CoreTester.classIndex
    private val intType = VirtualType.of("I")

    @Test
    fun encodedFrameworkLiteralIsExactWithoutPolicyAuthorization() {
        // Integer.MAX_VALUE is 0x7fffffff (JLS 4.2.1). The default policy denies every host read, so only the
        // encoded literal can produce a known value.
        val graph = run("encodedLiteral()I", VmServices(classIndex, ReflectionPolicy()))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(Int.MAX_VALUE, intType), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun bundledFrameworkInitializerRemainsSuppressedWithoutClaimingItRan() {
        val graph = run("encodedLiteral()I", VmServices(classIndex, ReflectionPolicy()))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun authorizedPrimitiveAndReferencesProjectThroughTheScheduler() {
        val vm = VmServices(classIndex, ReflectionPolicy(exactSafeStaticFields = PROJECTED_FIELDS))

        // UnixFileSystem takes the separator from file.separator, which is "/" on the POSIX hosts the build runs on.
        assertEquals(Value.Known('/', CHAR), result("primitive()C", vm))
        assertEquals(Value.Known(java.lang.Boolean.TRUE, BOOLEAN), result("immutableReference()Ljava/lang/Boolean;", vm))

        val graph = run("mutableReference()Ljava/util/List;", vm)
        val ref = (graph.trusted()!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        assertSame(java.util.Collections.EMPTY_LIST, CoreTester.terminatingHostObject(graph, ref))
        assertEquals(
            Value.Known(ref, EMPTY_LIST_TYPE),
            graph.trusted()!!.terminatingStaticFieldConsensus(EMPTY_LIST.definingClass, EMPTY_LIST.descriptor),
        )
    }

    @Test
    fun exactUnsafeInitializerOutranksOverlappingSafeFieldAndClassRules() {
        val field = field("Ljava/lang/System;", "out", "Ljava/io/PrintStream;")
        val policy = ReflectionPolicy(
            exactUnsafeMethods = setOf(MethodDescriptor(field.definingClass, "<clinit>", "()V")),
            exactSafeStaticFields = setOf(field),
            safeClasses = setOf(field.definingClass),
        )

        val graph = run("unsafeSystemField()Ljava/io/PrintStream;", VmServices(classIndex, policy))

        assertEquals(Value.Unknown(field.type), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun exactUnsafeSuperclassInitializerDeniesTheSubclassStatic() {
        val field = field("Ljava/lang/Integer;", "TYPE", "Ljava/lang/Class;")
        val number = VirtualType.of("Ljava/lang/Number;")
        val unsafeSuperclass = ReflectionPolicy(
            exactUnsafeMethods = setOf(MethodDescriptor(number, "<clinit>", "()V")),
            exactSafeStaticFields = setOf(field),
        )
        val authorized = ReflectionPolicy(exactSafeStaticFields = setOf(field))

        assertEquals(
            Value.Unknown(field.type),
            result("subclassField()Ljava/lang/Class;", VmServices(classIndex, unsafeSuperclass)),
        )
        // Integer.TYPE is Class.getPrimitiveClass("int"), the int class object.
        assertEquals(
            Value.Known(java.lang.Integer.TYPE, field.type),
            result("subclassField()Ljava/lang/Class;", VmServices(classIndex, authorized)),
        )
    }

    @Test
    fun defaultDenyReturnsTypedUnknownStrong() {
        val graph = run("immutableReference()Ljava/lang/Boolean;", VmServices(classIndex, ReflectionPolicy()))

        assertEquals(Value.Unknown(BOOLEAN), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun authorizedReadOfAClassTheHostLacksReturnsTypedUnknownStrong() {
        // ClassLoader$SystemClassLoader exists only in Android's libcore; the host JDK has no such class.
        val field = field("Ljava/lang/ClassLoader\$SystemClassLoader;", "loader", "Ljava/lang/ClassLoader;")
        val graph = run(
            "hostAbsentClassField()Ljava/lang/ClassLoader;",
            VmServices(classIndex, ReflectionPolicy(exactSafeStaticFields = setOf(field))),
        )

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Unknown(field.type), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
    }

    @Test
    fun guestWriteOverridesAProjectedCell() {
        val graph = run(
            "putOverridesProjection()Ljava/lang/Boolean;",
            VmServices(classIndex, ReflectionPolicy(exactSafeStaticFields = setOf(TRUE))),
        )

        assertEquals(Value.Known(null, TRUE.type), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun guestInitializerWriteReplacesPriorHostProjection() {
        val type = VirtualType.of("Ljava/lang/Boolean;")
        val descriptor = "Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;"
        val store = ClassInitStore()
        store.setHostProjection(descriptor, Value.Known(java.lang.Boolean.TRUE, type))

        store.setStaticField(descriptor, Value.Known(java.lang.Boolean.FALSE, type))

        assertEquals(Value.Known(java.lang.Boolean.FALSE, type), store.staticCell(descriptor)!!.value)
        assertEquals(StaticCellProvenance.GUEST_WRITE, store.staticCell(descriptor)!!.provenance)
    }

    @Test
    fun projectionNeverOverwritesAnExistingGuestOwnedCell() {
        val vm = VmServices(classIndex, ReflectionPolicy(exactSafeStaticFields = setOf(TRUE)))
        val view = view("immutableReference()Ljava/lang/Boolean;")
        val frame = Frame.entryFrame(view)
        frame.writeStaticField(TRUE.descriptor, Value.Known(false, TRUE.type))
        frame.completeClassInit(TRUE.definingClass)

        val graph = vm.newSession(view, entryFrame = frame).run()

        assertEquals(Value.Known(false, TRUE.type), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun staticCellMergeUsesValueAgreementAndConservativeOwnership() {
        val descriptor = "Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;"
        val boolean = VirtualType.of("Z")
        val first = guestCell(descriptor, Value.Known(true, boolean))
        val compatible = ClassInitStore.merge(listOf(first, first.copy()), classIndex)
        assertEquals(StaticCellProvenance.GUEST_WRITE, compatible.staticCell(descriptor)!!.provenance)
        assertEquals(Value.Known(true, boolean), compatible.staticCell(descriptor)!!.value)

        val agreeingHost = hostCell(descriptor, Value.Known(true, boolean))
        val agreeingConflict = ClassInitStore.merge(listOf(first, agreeingHost), classIndex)
        assertEquals(Value.Known(true, boolean), agreeingConflict.staticCell(descriptor)!!.value)
        assertEquals(StaticCellProvenance.GUEST_WRITE, agreeingConflict.staticCell(descriptor)!!.provenance)

        val encoded = preparedCell(descriptor, Value.Known(true, boolean), StaticCellProvenance.ENCODED_LITERAL)
        val encodedHost = ClassInitStore.merge(listOf(encoded, agreeingHost), classIndex)
        assertEquals(Value.Known(true, boolean), encodedHost.staticCell(descriptor)!!.value)
        assertEquals(StaticCellProvenance.ENCODED_LITERAL, encodedHost.staticCell(descriptor)!!.provenance)

        val differingHost = hostCell(descriptor, Value.Known(false, boolean))
        val conflict = ClassInitStore.merge(listOf(first, differingHost), classIndex)
        assertEquals(Value.Unknown(boolean), conflict.staticCell(descriptor)!!.value)
        assertEquals(StaticCellProvenance.GUEST_WRITE, conflict.staticCell(descriptor)!!.provenance)

        val encodedConflict = ClassInitStore.merge(listOf(encoded, differingHost), classIndex).staticCell(descriptor)!!
        assertEquals(Value.Unknown(boolean), encodedConflict.value)
        assertEquals(StaticCellProvenance.ENCODED_LITERAL, encodedConflict.provenance)

        val hostConflict = ClassInitStore.merge(listOf(agreeingHost, differingHost), classIndex).staticCell(descriptor)!!
        assertEquals(Value.Unknown(boolean), hostConflict.value)
        assertEquals(StaticCellProvenance.HOST_PROJECTION, hostConflict.provenance)
    }

    @Test
    fun hostAndSeededMergeRemainsProjectionEligibleWithoutGuestOwnership() {
        val descriptor = "Ljava/lang/Boolean;->TRUE:Ljava/lang/Boolean;"
        val boolean = VirtualType.of("Z")
        val host = hostCell(descriptor, Value.Known(true, boolean))
        val agreeingSeed = preparedCell(descriptor, Value.Known(true, boolean), StaticCellProvenance.SEEDED_DEFAULT)
        val agreeing = ClassInitStore.merge(listOf(host, agreeingSeed), classIndex).staticCell(descriptor)!!
        assertEquals(Value.Known(true, boolean), agreeing.value)
        assertEquals(StaticCellProvenance.HOST_PROJECTION, agreeing.provenance)

        val differingSeed = preparedCell(descriptor, Value.Known(false, boolean), StaticCellProvenance.SEEDED_DEFAULT)
        val differing = ClassInitStore.merge(listOf(host, differingSeed), classIndex).staticCell(descriptor)!!
        assertEquals(Value.Unknown(boolean), differing.value)
        assertEquals(StaticCellProvenance.SEEDED_DEFAULT, differing.provenance)
        assertFalse(differing.provenance.blocksHostProjection)

        val missing = ClassInitStore.merge(listOf(host, ClassInitStore()), classIndex).staticCell(descriptor)!!
        assertEquals(Value.Top, missing.value)
        assertTrue(missing.provenance.blocksHostProjection)
    }


    @Test
    fun projectedSiblingsAreIsolatedAndLeaveTheSeedFrameUnprojected() {
        val vm = VmServices(classIndex, ReflectionPolicy(exactSafeStaticFields = setOf(EMPTY_LIST)))
        val view = view("siblingProjection(I)Ljava/util/List;")
        val frame = Frame.entryFrame(view).apply { assign(1, Value.Unknown(intType)) }
        assertNull(frame.staticCell(EMPTY_LIST.descriptor))

        val graph = vm.newSession(view, entryFrame = frame).run()
        val returning = returningNodes(graph, view)
        val returned = returning.map { ((it.outcome as Outcome.Returned).value as Value.Known).raw as HeapRef }

        assertEquals(2, returned.size)
        assertNotEquals(returned[0], returned[1], "each projected sibling owns its imported host reference")
        returning.zip(returned).forEach { (node, ref) ->
            assertSame(java.util.Collections.EMPTY_LIST, node.frame.hostObjectAt(ref))
        }
        val successorCells = graph.addresses().flatMap(graph::nodePile).mapNotNull { it.endingFrame?.staticCell(EMPTY_LIST.descriptor) }
        assertTrue(successorCells.isNotEmpty(), "the returning successors hold the projected static")
        assertNull(frame.staticCell(EMPTY_LIST.descriptor), "projection writes only successor frames, never the seed")
    }

    @Test
    fun projectedExistingMutableHostValueReusesItsHeapRefWithoutAllocating() {
        val raw = java.util.Collections.EMPTY_LIST
        val vm = VmServices(classIndex, ReflectionPolicy(exactSafeStaticFields = setOf(EMPTY_LIST)))
        val view = view("mutableReference()Ljava/util/List;")
        val frame = Frame.entryFrame(view)
        val admitted = frame.admitHostSeeds(listOf(HostSeed(Value.Known(raw, EMPTY_LIST_TYPE)))).single() as Value.Known
        val existingRef = admitted.raw as HeapRef
        frame.assign(0, admitted)
        val probed = probeNextHeapId(frame)

        val graph = vm.newSession(view, entryFrame = frame).run()

        assertEquals(Value.Known(existingRef, EMPTY_LIST_TYPE), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(
            Value.Known(existingRef, EMPTY_LIST_TYPE),
            graph.trusted()!!.terminatingStaticFieldConsensus(EMPTY_LIST.definingClass, EMPTY_LIST.descriptor),
        )
        // Heap ids are run-wide and the first probe consumed its id, so the run allocated nothing iff the next
        // probe answers exactly one higher.
        assertEquals(probed + 1, probeNextHeapId(frame))
    }

    @Test
    fun hostInitializerErrorIsTheTargetsThrowUnchanged() {
        // JLS 12.4.2: an Error thrown by a static initializer propagates unchanged.
        val read = readHostField(
            HostInitializerFailureFixture.ThrowsError::class.java,
            "VALUE",
            intType,
            receiver = null,
        ) { true }

        val thrown = (read as HostFieldRead.TargetThrew).throwable
        assertEquals(VirtualType.of("Ljava/lang/AssertionError;"), thrown.type)
        assertTrue(thrown.throwable is AssertionError)
        assertEquals("initializer error", thrown.throwable.message)
    }

    @Test
    fun hostInitializerExceptionIsTheTargetsInitializerError() {
        // JLS 12.4.2: an exception thrown by a static initializer is wrapped in ExceptionInInitializerError.
        val read = readHostField(
            HostInitializerFailureFixture.ThrowsException::class.java,
            "VALUE",
            intType,
            receiver = null,
        ) { true }

        val thrown = (read as HostFieldRead.TargetThrew).throwable
        assertEquals(VirtualType.of("Ljava/lang/ExceptionInInitializerError;"), thrown.type)
        val cause = thrown.throwable.cause
        assertTrue(cause is IllegalStateException)
        assertEquals("initializer exception", cause!!.message)
    }

    private fun result(method: String, vm: VmServices): Value =
        run(method, vm).trusted()!!.terminatingConsensus(0)

    private fun run(method: String, vm: VmServices): ExecutionGraph =
        vm.newSession(view(method)).run()

    private fun view(method: String): MethodView =
        classIndex.requireMethodView(TEST_CLASS, method)

    private fun guestCell(descriptor: String, value: Value): ClassInitStore =
        ClassInitStore().apply { setStaticField(descriptor, value) }

    private fun hostCell(descriptor: String, value: Value): ClassInitStore =
        ClassInitStore().apply { setHostProjection(descriptor, value) }

    private fun preparedCell(
        descriptor: String,
        value: Value,
        provenance: StaticCellProvenance,
    ): ClassInitStore = ClassInitStore().apply {
        prepare(mapOf(descriptor to StaticCell(value, provenance)))
    }

    private fun returningNodes(graph: ExecutionGraph, view: MethodView): List<ExecutionNode> =
        view.addresses.flatMap { graph.nodePile(it).toList() }.filter { it.outcome is Outcome.Returned }

    private companion object {
        const val TEST_CLASS = "Lframework_static_test;"
        val CHAR = VirtualType.of("C")
        val BOOLEAN = VirtualType.of("Ljava/lang/Boolean;")
        val EMPTY_LIST_TYPE = VirtualType.of("Ljava/util/Collections\$EmptyList;")
        val TRUE = field("Ljava/lang/Boolean;", "TRUE", "Ljava/lang/Boolean;")
        val EMPTY_LIST = field("Ljava/util/Collections;", "EMPTY_LIST", "Ljava/util/List;")
        val PROJECTED_FIELDS = setOf(
            field("Ljava/io/File;", "separatorChar", "C"),
            TRUE,
            EMPTY_LIST,
        )

        fun field(owner: String, name: String, type: String): StaticFieldDescriptor =
            StaticFieldDescriptor(VirtualType.of(owner), name, VirtualType.of(type))
    }
}

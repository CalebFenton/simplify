package org.cf.smalivm.core

import com.google.common.Config
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class FrameworkStaticTest {
    private val classIndex = CoreTester.classIndex
    private val intType = VirtualType.of("I")

    @Test
    fun encodedFrameworkLiteralIsExactWithoutCallingTheService() {
        val service = recordingService { error("encoded literals must bypass host projection") }
        val graph = run("encodedLiteral()I", owner(service = service))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(Int.MAX_VALUE, intType), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(0, service.calls)
    }

    @Test
    fun bundledFrameworkInitializerRemainsSuppressedWithoutClaimingItRan() {
        val service = recordingService { error("encoded literals must bypass host projection") }
        val graph = run("encodedLiteral()I", owner(service = service))

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        assertEquals(0, service.calls)
    }

    @Test
    fun authorizedPrimitiveNullAndReferencesProjectThroughTheScheduler() {
        val mutable = arrayListOf("owned")
        val service = recordingService { request ->
            when (request.field.name) {
                "UNKNOWN" -> HostStaticResult.Returned(HostValue.Primitive(7, intType))
                "loader" -> HostStaticResult.Returned(HostValue.TypedNull(request.field.type))
                "TRUE" -> HostStaticResult.Returned(HostValue.Reference(java.lang.Boolean.TRUE, VirtualType.of("Ljava/lang/Boolean;")))
                "EMPTY_LIST" -> HostStaticResult.Returned(HostValue.Reference(mutable, VirtualType.of("Ljava/util/ArrayList;")))
                else -> error("unexpected request ${request.field}")
            }
        }
        val policy = ReflectionPolicy(exactSafeStaticFields = PROJECTED_FIELDS)
        val vm = owner(policy, service)

        assertEquals(Value.Known(7, intType), result("primitive()I", vm))
        assertEquals(Value.Known(null, VirtualType.of("Ljava/lang/ClassLoader;")), result("nullable()Ljava/lang/ClassLoader;", vm))
        assertEquals(Value.Known(java.lang.Boolean.TRUE, VirtualType.of("Ljava/lang/Boolean;")), result("immutableReference()Ljava/lang/Boolean;", vm))

        val mutableResult = result("mutableReference()Ljava/util/List;", vm) as Value.Known
        assertTrue(mutableResult.raw is HeapRef)
        val graph = run("mutableReference()Ljava/util/List;", vm)
        val ref = (graph.trusted()!!.terminatingConsensus(0) as Value.Known).raw as HeapRef
        assertSame(mutable, graph.terminatingHostObjectOverExploredPaths(ref))
        assertEquals(
            Value.Known(ref, VirtualType.of("Ljava/util/ArrayList;")),
            graph.trusted()!!.terminatingStaticFieldConsensus(
                VirtualType.of("Ljava/util/Collections;"),
                "Ljava/util/Collections;->EMPTY_LIST:Ljava/util/List;",
            ),
        )
    }

    @Test
    fun projectedHostThrowableImportsItsCauseIdentity() {
        val cause = IllegalArgumentException("cause")
        val root = IllegalStateException("root", cause)
        val field = field("Lorg/json/JSONObject;", "NULL", "Ljava/lang/Object;")
        val service = recordingService {
            HostStaticResult.Returned(
                HostValue.Reference(root, virtualTypeOfHostClass(root.javaClass)),
            )
        }
        val graph = run(
            "throwableReference()Ljava/lang/Object;",
            owner(ReflectionPolicy(exactSafeStaticFields = setOf(field)), service),
        )
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val rootRef = returned.raw as HeapRef
        val rootObject = graph.terminatingHeapObjectOverExploredPaths(rootRef) as HeapObject.GuestThrowable
        val causeValue = requireNotNull(rootObject.cause) as Value.Known
        val causeRef = causeValue.raw as HeapRef

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertSame(root, graph.terminatingHostObjectOverExploredPaths(rootRef))
        assertSame(cause, graph.terminatingHostObjectOverExploredPaths(causeRef))
        assertSame(cause, (graph.terminatingHostObjectOverExploredPaths(rootRef) as Throwable).cause)
    }

    @Test
    fun realServicePreservesImmutableAndMutableHostValues() {
        val actual = hostStaticService(classIndex.classLoader)
        val service = recordingService(actual::read)
        val fields = setOf(
            field("Ljava/lang/Boolean;", "TRUE", "Ljava/lang/Boolean;"),
            field("Ljava/util/Collections;", "EMPTY_LIST", "Ljava/util/List;"),
        )
        val vm = owner(ReflectionPolicy(exactSafeStaticFields = fields), service)

        assertEquals(
            Value.Known(java.lang.Boolean.TRUE, VirtualType.of("Ljava/lang/Boolean;")),
            result("immutableReference()Ljava/lang/Boolean;", vm),
        )
        assertTrue(result("mutableReference()Ljava/util/List;", vm).let { it is Value.Known && it.raw is HeapRef })
        assertEquals(2, service.calls)
    }

    @Test
    fun exactUnsafeInitializerBlocksAnOverlappingSafeFieldBeforeHostExecution() {
        val service = recordingService { error("unsafe initializer policy must win before host access") }
        val field = field("Ljava/lang/System;", "out", "Ljava/io/PrintStream;")
        val policy = ReflectionPolicy(
            exactUnsafeMethods = setOf(MethodDescriptor(field.definingClass, "<clinit>", "()V")),
            exactSafeStaticFields = setOf(field),
            safeClasses = setOf(field.definingClass),
        )

        val graph = run("unsafeSystemField()Ljava/io/PrintStream;", owner(policy, service))

        assertEquals(Value.Unknown(field.type), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        assertEquals(0, service.calls)
    }

    @Test
    fun exactUnsafeAncestorInitializerBlocksChildFieldBeforeHostInitialization() {
        Task40HostSentinel.unsafeParentInitialized = false
        val parent = VirtualType.of("Lcom/google/protobuf/nano/MessageNano;")
        val child = VirtualType.of(
            "Lcom/android/server/connectivity/metrics/IpConnectivityLogClass\$DefaultNetworkEvent;",
        )
        val childField = StaticFieldDescriptor(child, "NONE", intType)
        val service = recordingService { error("unsafe ancestor must deny before host service execution") }
        val policy = ReflectionPolicy(
            exactUnsafeMethods = setOf(MethodDescriptor(parent, "<clinit>", "()V")),
            exactSafeStaticFields = setOf(childField),
        )
        val vm = owner(policy, service)
        val view = view("unsafeAncestorField()I")
        val frame = Frame.entryFrame(view, vm.hostObjectCloner)
        frame.applyClassInit(
            ClassInitPlan(
                settled = listOf(child),
                skippedAnInitializer = true,
                context = null,
                staticSeeds = mapOf(
                    childField.descriptor to StaticCell(
                        Value.Unknown(intType),
                        StaticCellProvenance.SEEDED_DEFAULT,
                    ),
                ),
            ),
        )

        val graph = ExecutionSession.of(view, vm, entryFrame = frame).run()

        assertEquals(Value.Unknown(intType), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(0, service.calls)
        assertFalse(Task40HostSentinel.unsafeParentInitialized)
    }

    @Test
    fun defaultDenyReturnsTypedUnknownStrongWithoutHostExecution() {
        val service = recordingService { error("default deny must not call host service") }
        val graph = run("immutableReference()Ljava/lang/Boolean;", owner(ReflectionPolicy(), service))

        assertEquals(Value.Unknown(VirtualType.of("Ljava/lang/Boolean;")), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        assertEquals(0, service.calls)
    }

    @Test
    fun unsupportedAuthorizedReadReturnsTypedUnknownStrong() {
        val service = recordingService { HostStaticResult.Unsupported("not a public host field") }
        val field = field("Ljava/lang/Boolean;", "TRUE", "Ljava/lang/Boolean;")
        val graph = run(
            "immutableReference()Ljava/lang/Boolean;",
            owner(ReflectionPolicy(exactSafeStaticFields = setOf(field)), service),
        )

        assertEquals(Value.Unknown(field.type), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(SideEffectLevel.STRONG, graph.methodSideEffectLevelOverExploredPaths())
        assertEquals(1, service.calls)
    }

    @Test
    fun guestWriteOverridesAProjectedCellAndPreventsAnotherProjection() {
        val service = recordingService {
            HostStaticResult.Returned(HostValue.Reference(java.lang.Boolean.TRUE, VirtualType.of("Ljava/lang/Boolean;")))
        }
        val trueField = field("Ljava/lang/Boolean;", "TRUE", "Ljava/lang/Boolean;")
        val graph = run(
            "putOverridesProjection()Ljava/lang/Boolean;",
            owner(ReflectionPolicy(exactSafeStaticFields = setOf(trueField)), service),
        )

        assertEquals(Value.Known(null, trueField.type), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(1, service.calls, "the sget after sput must use guest-owned static storage")
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
        val service = recordingService { error("guest ownership must suppress projection") }
        val field = field("Ljava/lang/Boolean;", "TRUE", "Ljava/lang/Boolean;")
        val vm = owner(ReflectionPolicy(exactSafeStaticFields = setOf(field)), service)
        val view = view("immutableReference()Ljava/lang/Boolean;")
        val frame = Frame.entryFrame(view, vm.hostObjectCloner)
        frame.writeStaticField(field.descriptor, Value.Known(false, field.type))
        frame.completeClassInit(field.definingClass)

        val graph = ExecutionSession.of(view, vm, entryFrame = frame).run()

        assertEquals(Value.Known(false, field.type), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(0, service.calls)
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
    fun projectedSiblingsAreIsolatedWhileUnchangedCopiesSharePersistentRoots() {
        val values = ArrayDeque(listOf(arrayListOf("left"), arrayListOf("right")))
        val service = recordingService {
            HostStaticResult.Returned(HostValue.Reference(values.removeFirst(), VirtualType.of("Ljava/util/ArrayList;")))
        }
        val field = field("Ljava/util/Collections;", "EMPTY_LIST", "Ljava/util/List;")
        val vm = owner(ReflectionPolicy(exactSafeStaticFields = setOf(field)), service)
        val view = view("siblingProjection(I)Ljava/util/List;")
        val frame = Frame.entryFrame(view, vm.hostObjectCloner).apply { assign(1, Value.Unknown(intType)) }
        val originalRoot = frame.staticRootToken()
        assertSame(originalRoot, frame.copy().staticRootToken())

        val graph = ExecutionSession.of(view, vm, entryFrame = frame).run()
        val returned = graph.outcomes.filterIsInstance<Outcome.Returned>().map { (it.value as Value.Known).raw as HeapRef }
        assertEquals(2, returned.size)
        assertTrue(returned[0] != returned[1], "each projected sibling must own its imported host reference")
        assertEquals(2, service.calls)
        assertSame(originalRoot, frame.staticRootToken(), "projection must publish only fresh successor roots")
    }

    @Test
    fun sharedHostSingletonDetachesOnlyTheMutatingSibling() {
        val singleton = arrayListOf<String>()
        val field = field("Ljava/util/Collections;", "EMPTY_LIST", "Ljava/util/List;")
        val add = MethodDescriptor(
            VirtualType.of("Ljava/util/List;"),
            "add",
            "(Ljava/lang/Object;)Z",
        )
        val service = recordingService {
            HostStaticResult.Returned(
                HostValue.Reference(singleton, VirtualType.of("Ljava/util/ArrayList;")),
            )
        }
        val policy = ReflectionPolicy(
            exactSafeMethods = setOf(add),
            exactSafeStaticFields = setOf(field),
            safeClasses = listOf(
                "Ljava/util/ArrayList;",
                "Ljava/util/AbstractList;",
                "Ljava/util/AbstractCollection;",
                "Ljava/util/List;",
                "Ljava/util/Collection;",
            ).map(VirtualType::of).toSet(),
        )
        val vm = owner(policy, service)
        val view = view("siblingProjectionMutation(I)Ljava/util/List;")
        val frame = Frame.entryFrame(view, vm.hostObjectCloner).apply { assign(2, Value.Unknown(intType)) }

        val graph = ExecutionSession.of(view, vm, entryFrame = frame).run()
        val returned = returningNodes(graph, view).map { node ->
            val ref = (node.outcome as Outcome.Returned).value.let { (it as Value.Known).raw as HeapRef }
            @Suppress("UNCHECKED_CAST")
            node.frame.hostObjectAt(ref) as List<String>
        }

        assertEquals(2, returned.size)
        assertTrue(returned.any { it == emptyList<String>() })
        assertTrue(returned.any { it == listOf("changed") })
        assertTrue(singleton.isEmpty(), "selected host mutation must occur on the detached path graph")
        assertEquals(2, service.calls)
    }

    @Test
    fun projectedExistingMutableHostValueReusesItsHeapRefAndAllocatorPosition() {
        val raw = arrayListOf("existing")
        val field = field("Ljava/util/Collections;", "EMPTY_LIST", "Ljava/util/List;")
        val service = recordingService {
            HostStaticResult.Returned(
                HostValue.Reference(raw, VirtualType.of("Ljava/util/ArrayList;")),
            )
        }
        val vm = owner(ReflectionPolicy(exactSafeStaticFields = setOf(field)), service)
        val view = view("mutableReference()Ljava/util/List;")
        val frame = Frame.entryFrame(view, vm.hostObjectCloner)
        val admitted = frame.admitHostSeeds(
            listOf(HostSeed(Value.Known(raw, VirtualType.of("Ljava/util/ArrayList;")))),
        ).single() as Value.Known
        val existingRef = admitted.raw as HeapRef
        frame.assign(0, admitted)
        val nextId = frame.nextHeapId()

        val graph = ExecutionSession.of(view, vm, entryFrame = frame).run()

        assertEquals(Value.Known(existingRef, VirtualType.of("Ljava/util/ArrayList;")), graph.trusted()!!.terminatingConsensus(0))
        assertEquals(
            Value.Known(existingRef, VirtualType.of("Ljava/util/ArrayList;")),
            graph.trusted()!!.terminatingStaticFieldConsensus(field.definingClass, field.descriptor),
        )
        assertEquals(nextId, frame.nextHeapId())
        assertEquals(1, service.calls)
    }

    @Test
    fun targetInitializerThrowRoutesOnlyThisAccess() {
        val thrown = ExceptionInInitializerError("boom")
        val service = recordingService {
            HostStaticResult.TargetThrew(HostThrowable(VirtualType.of("Ljava/lang/ExceptionInInitializerError;"), thrown))
        }
        val field = field("Ljava/lang/Boolean;", "TRUE", "Ljava/lang/Boolean;")
        val graph = run(
            "catchesProjectionThrow()I",
            owner(ReflectionPolicy(exactSafeStaticFields = setOf(field)), service),
        )

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(1, intType), graph.trusted()!!.terminatingConsensus(1))
        val caught = graph.trusted()!!.terminatingConsensus(0) as Value.Known
        assertTrue(caught.raw is HeapRef)
        assertEquals(VirtualType.of("Ljava/lang/ExceptionInInitializerError;"), caught.type)
        assertSame(thrown, graph.terminatingHostObjectOverExploredPaths(caught.raw as HeapRef))
    }

    @Test
    fun realHostInitializerErrorIsClassifiedAsTargetThrow() {
        val field = field("Lcom/google/common/Config;", "KEY_BACK", "I")
        val actual = hostStaticService(Config::class.java.classLoader)
        var serviceResult: HostStaticResult? = null
        val service = recordingService { request -> actual.read(request).also { serviceResult = it } }
        val graph = run(
            "realInitializerError()I",
            owner(ReflectionPolicy(exactSafeStaticFields = setOf(field)), service),
        )

        assertTrue(
            serviceResult is HostStaticResult.TargetThrew,
            "result=$serviceResult completion=${graph.completion()}",
        )
        val target = (serviceResult as HostStaticResult.TargetThrew).throwable
        assertTrue(target.throwable is AssertionError)
        assertEquals(VirtualType.of("Ljava/lang/AssertionError;"), target.type)
    }

    @Test
    fun infrastructureFailureBecomesHostFailureAndVmeEscapes() {
        val field = field("Ljava/lang/Boolean;", "TRUE", "Ljava/lang/Boolean;")
        val policy = ReflectionPolicy(exactSafeStaticFields = setOf(field))
        val failed = run(
            "immutableReference()Ljava/lang/Boolean;",
            owner(policy, recordingService { HostStaticResult.InfrastructureFailure(IllegalStateException("broken")) }),
        )
        assertEquals(CompletionStatus.Partial(setOf(AbortReason.HOST_FAILURE)), failed.completion())

        val fatal = StackOverflowError("fatal")
        assertThrows(StackOverflowError::class.java) {
            run(
                "immutableReference()Ljava/lang/Boolean;",
                owner(policy, recordingService { HostStaticResult.Fatal(fatal) }),
            )
        }

        val infrastructureFatal = OutOfMemoryError("fatal infrastructure result")
        val escaped = assertThrows(OutOfMemoryError::class.java) {
            run(
                "immutableReference()Ljava/lang/Boolean;",
                owner(
                    policy,
                    recordingService { HostStaticResult.InfrastructureFailure(infrastructureFatal) },
                ),
            )
        }
        assertSame(infrastructureFatal, escaped)
    }

    private fun result(method: String, vm: VmServices): Value =
        run(method, vm).trusted()!!.terminatingConsensus(0)

    private fun run(method: String, vm: VmServices): ExecutionGraph =
        vm.newSession(view(method)).run()

    private fun view(method: String): MethodView =
        classIndex.requireMethodView(TEST_CLASS, method)

    private fun owner(
        policy: ReflectionPolicy = ReflectionPolicy(),
        service: RecordingHostStaticService,
    ): VmServices = VmServices(classIndex, policy, service.service)

    private fun recordingService(read: (HostStaticRequest) -> HostStaticResult): RecordingHostStaticService {
        val counter = intArrayOf(0)
        val service = HostStaticService { request ->
            counter[0]++
            read(request)
        }
        return RecordingHostStaticService(service, counter)
    }

    private fun field(owner: String, name: String, type: String): StaticFieldDescriptor =
        StaticFieldDescriptor(VirtualType.of(owner), name, VirtualType.of(type))

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

    private class RecordingHostStaticService(
        val service: HostStaticService,
        private val counter: IntArray,
    ) {
        val calls: Int get() = counter[0]
    }

    private companion object {
        const val TEST_CLASS = "Lframework_static_test;"
        val PROJECTED_FIELDS = setOf(
            StaticFieldDescriptor(VirtualType.of("Lsun/net/www/MimeEntry;"), "UNKNOWN", VirtualType.of("I")),
            StaticFieldDescriptor(
                VirtualType.of("Ljava/lang/ClassLoader\$SystemClassLoader;"),
                "loader",
                VirtualType.of("Ljava/lang/ClassLoader;"),
            ),
            StaticFieldDescriptor(
                VirtualType.of("Ljava/lang/Boolean;"),
                "TRUE",
                VirtualType.of("Ljava/lang/Boolean;"),
            ),
            StaticFieldDescriptor(
                VirtualType.of("Ljava/util/Collections;"),
                "EMPTY_LIST",
                VirtualType.of("Ljava/util/List;"),
            ),
        )
    }
}

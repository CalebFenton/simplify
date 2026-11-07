package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InvocationPreparationTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun runnableAndBodylessMethodsCarryResolvedMetadata() {
        val caller = type("Lcalls/caller;")
        val runnable = ready(
            Invoker(classIndex),
            invocation("Lcalls/callee;", "identityInt", "(I)I", InvokeKind.STATIC, known(4, "I")),
            caller,
        )
        assertTrue(runnable.lookup is MethodLookup.Runnable)
        assertEquals(method("Lcalls/callee;", "identityInt", "(I)I"), runnable.invocation.target)
        assertEquals(caller, runnable.invocation.callerClass)
        assertEquals(type("I"), runnable.invocation.returnType)
        assertEquals(listOf(type("I")), runnable.invocation.parameterTypes)

        val bodyless = ready(
            Invoker(classIndex),
            invocation("Lcalls/callee;", "nativeCallee", "()I", InvokeKind.STATIC),
            caller,
        )
        assertTrue(bodyless.lookup is MethodLookup.NoBody)
        assertEquals(method("Lcalls/callee;", "nativeCallee", "()I"), bodyless.invocation.target)
        assertEquals(type("Lcalls/callee;"), bodyless.invocation.target.definingClass)
        assertEquals(type("I"), bodyless.invocation.returnType)
        assertEquals(emptyList<VirtualType>(), bodyless.invocation.parameterTypes)
    }

    @Test
    fun arrayPseudoMethodKeepsTheArrayAsItsResolvedDeclaringClass() {
        val array = type("[I")
        val receiver = Value.Unknown(array)
        val prepared = ready(
            Invoker(classIndex),
            invocation("[I", "clone", "()Ljava/lang/Object;", InvokeKind.VIRTUAL, receiver),
            type("Linvoke_task20;"),
            receiverType = array,
        )

        assertTrue(prepared.lookup is MethodLookup.NoBody)
        assertEquals(method("[I", "clone", "()Ljava/lang/Object;"), prepared.invocation.target)
        assertEquals(array, prepared.invocation.target.definingClass)
        assertEquals(receiver, prepared.invocation.receiver)
    }

    @Test
    fun staticInstanceAndConstructorCallsCarryCallerTargetKindAndReceiver() {
        val caller = type("Linvoke_task20;")
        val static = ready(
            Invoker(classIndex),
            invocation("Lcalls/callee;", "returnsSeven", "()I", InvokeKind.STATIC),
            caller,
        ).invocation
        assertEquals(caller, static.callerClass)
        assertEquals(InvokeKind.STATIC, static.kind)
        assertEquals(method("Lcalls/callee;", "returnsSeven", "()I"), static.target)
        assertEquals(null, static.receiver)

        val receiver = Value.Unknown(type("Linvoke_dispatch_base;"))
        val instance = ready(
            Invoker(classIndex),
            invocation("Linvoke_dispatch_base;", "value", "()I", InvokeKind.VIRTUAL, receiver),
            caller,
            receiverType = type("Linvoke_dispatch_child;"),
        ).invocation
        assertEquals(caller, instance.callerClass)
        assertEquals(InvokeKind.VIRTUAL, instance.kind)
        assertEquals(method("Linvoke_dispatch_child;", "value", "()I"), instance.target)
        assertEquals(receiver, instance.receiver)

        val constructorReceiver = Value.Known(HeapRef(7), type("Linvoke_ctor_target;"))
        val constructor = ready(
            Invoker(classIndex),
            invocation("Linvoke_ctor_target;", "<init>", "()V", InvokeKind.DIRECT, constructorReceiver),
            caller,
            receiverType = type("Linvoke_ctor_target;"),
        ).invocation
        assertEquals(caller, constructor.callerClass)
        assertEquals(InvokeKind.DIRECT, constructor.kind)
        assertEquals(method("Linvoke_ctor_target;", "<init>", "()V"), constructor.target)
        assertEquals(constructorReceiver, constructor.receiver)
        assertEquals(emptyList<Value>(), constructor.arguments)
    }

    @Test
    fun constructorReceiverIsSeparateFromWideLogicalArguments() {
        val uuid = type("Ljava/util/UUID;")
        val receiver = Value.Unknown(uuid)
        val first = known(11L, "J")
        val second = known(22L, "J")
        val constructor = ready(
            Invoker(classIndex),
            invocation(
                "Ljava/util/UUID;", "<init>", "(JJ)V", InvokeKind.DIRECT,
                receiver, first, Value.Top, second, Value.Top,
            ),
            type("Lmethod_reflector_test;"),
            receiverType = uuid,
        ).invocation

        assertEquals(receiver, constructor.receiver)
        assertEquals(listOf(type("J"), type("J")), constructor.parameterTypes)
        assertEquals(listOf(first, second), constructor.arguments)
    }

    @Test
    fun narrowWideAndMixedRegisterWordsBecomeOneValuePerLogicalParameter() {
        val words = listOf(
            known(0, "I"), known(2, "I"), known(65, "I"), known(3, "I"), known(4, "I"),
            known(5L, "J"), Value.Top,
            known(6.0f, "F"),
            known(7.0, "D"), Value.Top,
            known(0, "I"),
        )
        val prepared = ready(
            Invoker(classIndex),
            Invocation(
                method("Linvoke_review_callee;", "bindingMatrix", "(ZBCSIJFDLjava/lang/Object;)V"),
                InvokeKind.STATIC,
                words,
            ),
            type("Linvoke_review;"),
        ).invocation

        assertEquals(9, prepared.parameterTypes.size)
        assertEquals(9, prepared.arguments.size)
        assertEquals(
            listOf(false, 2.toByte(), 'A', 3.toShort(), 4, 5L, 6.0f, 7.0, null),
            prepared.arguments.map { (it as Value.Known).raw },
        )
    }

    @Test
    fun integerZeroBecomesNullOnlyWhenBoundToAReference() {
        val reference = ready(
            Invoker(classIndex),
            invocation(
                "Linvoke_review_callee;", "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;",
                InvokeKind.STATIC, known(0, "I"),
            ),
            type("Linvoke_review;"),
        ).invocation.arguments.single()
        assertEquals(Value.Known(null, type("Ljava/lang/Object;")), reference)

        val primitive = ready(
            Invoker(classIndex),
            invocation("Lcalls/callee;", "identityInt", "(I)I", InvokeKind.STATIC, known(0, "I")),
            type("Lcalls/caller;"),
        ).invocation.arguments.single()
        assertEquals(Value.Known(0, type("I")), primitive)
    }

    @Test
    fun nonIntPrimitiveZeroDoesNotBecomeNullInAReferenceBinding() {
        val booleanZero = Value.Known(false, type("Z"))
        val prepared = ready(
            Invoker(classIndex),
            invocation(
                "Linvoke_review_callee;", "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;",
                InvokeKind.STATIC, booleanZero,
            ),
            type("Linvoke_review;"),
        ).invocation

        assertEquals(booleanZero, prepared.arguments.single())
    }

    @Test
    fun malformedArityBecomesTypedOpaqueWithoutProbingABackend() {
        val backend = RecordingBackend()
        val preparation = Invoker(classIndex, listOf(backend)).prepare(
            invocation("Lcalls/callee;", "returnsSeven", "()I", InvokeKind.STATIC, known(1, "I")),
            type("Lcalls/caller;"),
        )

        assertTrue(preparation is InvocationPreparation.Opaque)
        preparation as InvocationPreparation.Opaque
        assertEquals(type("I"), preparation.returnType)
        assertTrue(preparation.diagnostic?.contains("argument register") == true)
        assertEquals(0, backend.probes)
    }

    @Test
    fun missingNarrowAndWideRegisterWordsBecomeOpaqueWithoutProbingABackend() {
        val backend = RecordingBackend()
        val invoker = Invoker(classIndex, listOf(backend))
        val calls = listOf(
            invocation("Lcalls/callee;", "identityInt", "(I)I", InvokeKind.STATIC),
            invocation("Lcalls/callee;", "identityLong", "(J)J", InvokeKind.STATIC, known(9L, "J")),
        )

        for (call in calls) {
            val preparation = invoker.prepare(call, type("Lcalls/caller;"))
            assertTrue(preparation is InvocationPreparation.Opaque)
            assertEquals(
                type(call.target.proto.substringAfter(')')),
                (preparation as InvocationPreparation.Opaque).returnType,
            )
        }
        assertEquals(0, backend.probes)
    }

    @Test
    fun resolutionFailurePrecedesANullReceiverAndProbesNoBackend() {
        val backend = RecordingBackend()
        val preparation = Invoker(classIndex, listOf(backend)).prepare(
            invocation("Linvoke_task20;", "missing", "()V", InvokeKind.VIRTUAL, known(0, "I")),
            type("Linvoke_task20;"),
        )

        assertTrue(preparation is InvocationPreparation.ThrowResolution)
        assertEquals(
            type("Ljava/lang/NoSuchMethodError;"),
            (preparation as InvocationPreparation.ThrowResolution).exceptionType,
        )
        assertEquals(0, backend.probes)
    }

    @Test
    fun nullReceiverStopsBeforeBackendExecution() {
        val backend = RecordingBackend()
        val invoker = Invoker(classIndex, listOf(backend))
        val view = view("Linvoke_task20;", "nullVirtual()I")
        val graph = ExecutionSession.of(
            view, classIndex, Limits(), Frame.entryFrame(view), OpBuilder::build, invoker,
        ).run()

        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
        assertEquals(0, backend.probes)
    }

    @Test
    fun ambiguousDispatchRetainsCandidatesAndProbesNoBackend() {
        val backend = RecordingBackend()
        val preparation = Invoker(classIndex, listOf(backend)).prepare(
            invocation(
                "Linvoke_dispatch_base;", "value", "()I", InvokeKind.VIRTUAL,
                Value.Unknown(type("Linvoke_dispatch_base;")),
            ),
            type("Linvoke_task20;"),
        )

        assertTrue(preparation is InvocationPreparation.Opaque)
        assertEquals(
            setOf(
                "Linvoke_dispatch_base;->value()I",
                "Linvoke_dispatch_child;->value()I",
            ),
            (preparation as InvocationPreparation.Opaque).candidates,
        )
        assertEquals(0, backend.probes)
    }

    @Test
    fun preparationDoesNotProbeOrAlterFrameFrontierOrEffects() {
        val backend = RecordingBackend()
        val view = view("Lcalls/caller;", "passesAnArgument()I")
        val frame = Frame.entryFrame(view).also {
            it.assign(0, known(8, "I"))
            it.recordSideEffect(SideEffectLevel.WEAK)
        }
        val session = ExecutionSession.of(view, classIndex, Limits(), frame)
        val frontierBefore = session.frontier

        val preparation = Invoker(classIndex, listOf(backend)).prepare(
            invocation("Lcalls/callee;", "identityInt", "(I)I", InvokeKind.STATIC, known(8, "I")),
            type("Lcalls/caller;"),
        )

        assertTrue(preparation is InvocationPreparation.Ready)
        assertEquals(0, backend.probes)
        assertEquals(1, session.frontier.size)
        assertSame(frontierBefore.single(), session.frontier.single())
        assertEquals(emptyList<Outcome>(), session.outcomes)
        assertEquals(known(8, "I"), frame.peek(0))
        assertEquals(emptySet<Int>(), frame.registersRead())
        assertEquals(SideEffectLevel.WEAK, frame.takeSideEffectLevel())
    }

    @Test
    fun schedulerOrdersResolveBindNullCheckAndProbe() {
        val events = mutableListOf<String>()
        val backend = RecordingBackend(events)
        val invoker = Invoker(classIndex, listOf(backend)) { events.add(it) }
        val view = view("Linvoke_task20;", "zeroBecomesNull()Ljava/lang/Object;")
        val frame = Frame.entryFrame(view).also {
            it.completeClassInit(type("Linvoke_task20;"))
        }

        ExecutionSession.of(
            view, classIndex, Limits(), frame, OpBuilder::build, invoker,
        ).run()

        assertEquals(listOf("resolve", "bind", "null-check", "class-init-trigger", "probe"), events)
    }

    private fun ready(
        invoker: Invoker,
        call: Invocation,
        callerClass: VirtualType,
        receiverType: VirtualType? = null,
    ): InvocationPreparation.Ready {
        val preparation = invoker.prepare(call, callerClass, receiverType)
        assertTrue(preparation is InvocationPreparation.Ready, "expected a prepared invocation, got $preparation")
        return preparation as InvocationPreparation.Ready
    }

    private fun invocation(
        definingClass: String,
        name: String,
        proto: String,
        kind: InvokeKind,
        vararg words: Value,
    ) = Invocation(method(definingClass, name, proto), kind, words.toList())

    private fun method(definingClass: String, name: String, proto: String) =
        MethodDescriptor(type(definingClass), name, proto)

    private fun known(raw: Any, descriptor: String) = Value.Known(raw, type(descriptor))
    private fun type(descriptor: String) = VirtualType.of(descriptor)
    private fun view(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    private class RecordingBackend(private val events: MutableList<String>? = null) : Backend {
        var probes = 0
            private set

        override val name: String = "recording"

        override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
            probes += 1
            events?.add("probe")
            return when (lookup) {
                is MethodLookup.Runnable -> BackendProbe.Schedule(lookup.view, 0)
                else -> BackendProbe.Declined("not runnable")
            }
        }
    }
}

package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class InvocationPreparationTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun runnableAndBodylessMethodsCarryResolvedMetadata() {
        val caller = type("Lcalls/caller;")
        val runnable = ready(
            defaultInvoker(classIndex),
            invocation("Lcalls/callee;", "identityInt", "(I)I", InvokeKind.STATIC, known(4, "I")),
            caller,
        )
        assertTrue(runnable.lookup is MethodLookup.Runnable)
        assertEquals(method("Lcalls/callee;", "identityInt", "(I)I"), runnable.invocation.target)
        assertEquals(caller, runnable.invocation.callerClass)
        assertEquals(type("I"), runnable.invocation.returnType)
        assertEquals(listOf(type("I")), runnable.invocation.parameterTypes)

        val bodyless = ready(
            defaultInvoker(classIndex),
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
            defaultInvoker(classIndex),
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
            defaultInvoker(classIndex),
            invocation("Lcalls/callee;", "returnsSeven", "()I", InvokeKind.STATIC),
            caller,
        ).invocation
        assertEquals(caller, static.callerClass)
        assertEquals(InvokeKind.STATIC, static.kind)
        assertEquals(method("Lcalls/callee;", "returnsSeven", "()I"), static.target)
        assertEquals(null, static.receiver)

        val receiver = Value.Unknown(type("Linvoke_dispatch_base;"))
        val instance = ready(
            defaultInvoker(classIndex),
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
            defaultInvoker(classIndex),
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
            defaultInvoker(classIndex),
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

    /**
     * Dalvik writes null as `const/4 vA, 0`, so only an exact int zero bound to a reference parameter is null; a
     * non-zero int, a non-int zero, or a zero bound to a primitive keeps its value.
     */
    @TestFactory
    fun integerZeroBecomesNullOnlyWhenBoundToAReference(): List<DynamicTest> {
        val objectIdentity = method("Linvoke_review_callee;", "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;")
        val intIdentity = method("Lcalls/callee;", "identityInt", "(I)I")
        return listOf(
            Triple(known(0, "I"), objectIdentity, Value.Known(null, type("Ljava/lang/Object;"))),
            Triple(known(5, "I"), objectIdentity, known(5, "I")),
            Triple(known(false, "Z"), objectIdentity, known(false, "Z")),
            Triple(known(0, "I"), intIdentity, known(0, "I")),
        ).map { (argument, target, expected) ->
            DynamicTest.dynamicTest("$argument bound to $target is $expected") {
                val caller = if (target == intIdentity) type("Lcalls/caller;") else type("Linvoke_review;")
                val bound = ready(
                    defaultInvoker(classIndex),
                    Invocation(target, InvokeKind.STATIC, listOf(argument)),
                    caller,
                ).invocation.arguments.single()
                assertEquals(expected, bound)
            }
        }
    }

    @Test
    fun malformedArityBecomesTypedOpaqueWithoutProbingABackend() {
        val backend = RecordingBackend()
        val preparation = invokerOf(classIndex, listOf(backend)).prepare(
            invocation("Lcalls/callee;", "returnsSeven", "()I", InvokeKind.STATIC, known(1, "I")),
            type("Lcalls/caller;"),
        )

        assertTrue(preparation is OpaqueInvocation)
        preparation as OpaqueInvocation
        assertEquals(type("I"), preparation.returnType)
        assertTrue(preparation.diagnostic?.contains("argument register") == true)
        assertEquals(0, backend.probes)
    }

    @Test
    fun missingNarrowAndWideRegisterWordsBecomeOpaqueWithoutProbingABackend() {
        val backend = RecordingBackend()
        val invoker = invokerOf(classIndex, listOf(backend))
        val calls = listOf(
            invocation("Lcalls/callee;", "identityInt", "(I)I", InvokeKind.STATIC),
            invocation("Lcalls/callee;", "identityLong", "(J)J", InvokeKind.STATIC, known(9L, "J")),
        )

        for (call in calls) {
            val preparation = invoker.prepare(call, type("Lcalls/caller;"))
            assertTrue(preparation is OpaqueInvocation)
            assertEquals(
                type(call.target.proto.substringAfter(')')),
                (preparation as OpaqueInvocation).returnType,
            )
        }
        assertEquals(0, backend.probes)
    }

    @Test
    fun resolutionFailurePrecedesANullReceiverAndProbesNoBackend() {
        val backend = RecordingBackend()
        val preparation = invokerOf(classIndex, listOf(backend)).prepare(
            invocation("Linvoke_task20;", "missing", "()V", InvokeKind.VIRTUAL, known(0, "I")),
            type("Linvoke_task20;"),
        )

        assertTrue(preparation is InvocationPreparation.ThrowResolution)
        assertEquals(
            SynthesizedThrowable.NO_SUCH_METHOD,
            (preparation as InvocationPreparation.ThrowResolution).recipe.kind,
        )
        assertEquals(0, backend.probes)
    }

    @Test
    fun nullReceiverStopsBeforeBackendExecution() {
        val vm = VmServices(classIndex)
        val calls = recordingEmulator(vm, method("Ljava/lang/String;", "length", "()I"))
        val graph = vm.newSession(view("Linvoke_task20;", "nullVirtual()I")).run()

        assertEquals(listOf("Ljava/lang/NullPointerException;"), CoreTester.escapingTypes(graph))
        assertEquals(emptyList<EmulatorInvocation>(), calls)
    }

    @Test
    fun ambiguousDispatchRetainsCandidatesAndProbesNoBackend() {
        val backend = RecordingBackend()
        val preparation = invokerOf(classIndex, listOf(backend)).prepare(
            invocation(
                "Linvoke_dispatch_base;", "value", "()I", InvokeKind.VIRTUAL,
                Value.Unknown(type("Linvoke_dispatch_base;")),
            ),
            type("Linvoke_task20;"),
        )

        assertTrue(preparation is OpaqueInvocation)
        assertEquals(
            setOf(
                "Linvoke_dispatch_base;->value()I",
                "Linvoke_dispatch_child;->value()I",
            ),
            (preparation as OpaqueInvocation).candidates,
        )
        assertEquals(0, backend.probes)
    }

    @Test
    fun preparationDoesNotProbeOrAlterFrameOrFrontier() {
        val backend = RecordingBackend()
        val view = view("Lcalls/caller;", "passesAnArgument()I")
        val frame = Frame.entryFrame(view).also { it.assign(0, known(8, "I")) }
        val session = CoreTester.vmServices.newSession(view, Limits(), frame)
        val frontierBefore = session.frontier

        val preparation = invokerOf(classIndex, listOf(backend)).prepare(
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
    }

    /** Catches a scheduler that null-checks the receiver before resolving the target: it would throw NPE. */
    @Test
    fun sessionResolutionFailureWinsOverANullReceiverAndRunsNoBackend() {
        val vm = VmServices(classIndex)
        val calls = recordingEmulator(vm, method("Linvoke_task20;", "missing", "()V"))

        val graph = vm.newSession(view("Linvoke_task20;", "missingBeforeNull()V")).run()

        assertEquals(listOf("Ljava/lang/NoSuchMethodError;"), CoreTester.escapingTypes(graph))
        assertEquals(emptyList<EmulatorInvocation>(), calls)
    }

    /**
     * Catches a scheduler that selects a backend before binding: the emulator would receive the raw int word
     * instead of the Object parameter the zero binds to as null.
     */
    @Test
    fun backendReceivesTheBoundArgument() {
        val vm = VmServices(classIndex)
        val calls = recordingEmulator(
            vm,
            method("Linvoke_task20;", "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;"),
        )
        val view = view("Linvoke_task20;", "zeroBecomesNull()Ljava/lang/Object;")
        val frame = Frame.entryFrame(view).also {
            it.classes.complete(type("Linvoke_task20;"))
        }

        vm.newSession(view, Limits(), frame).run()

        assertEquals(
            listOf<EmulatorValue>(EmulatorValue.Known(type("Ljava/lang/Object;"), null)),
            calls.single().arguments,
        )
    }

    /** Every call an emulator registered on [vm] for [target] receives; it answers a typed null. */
    private fun recordingEmulator(vm: VmServices, target: MethodSignature): List<EmulatorInvocation> {
        val calls = mutableListOf<EmulatorInvocation>()
        vm.emulate(target) { call ->
            calls += call
            SynchronousCallResult.Returned(HostReturn.Value(HostValue.TypedNull(call.returnType)))
        }
        return calls
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
        MethodSignature(type(definingClass), name, proto)

    private fun known(raw: Any, descriptor: String) = Value.Known(raw, type(descriptor))
    private fun type(descriptor: String) = VirtualType.of(descriptor)
    private fun view(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    private class RecordingBackend : Backend {
        var probes = 0
            private set

        override val name: String = "recording"

        override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
            probes += 1
            return when (lookup) {
                is MethodLookup.Runnable -> BackendProbe.Schedule(lookup.view, 0)
                else -> BackendProbe.Declined("not runnable")
            }
        }
    }
}

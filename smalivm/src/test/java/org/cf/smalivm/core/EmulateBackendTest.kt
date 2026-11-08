package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EmulateBackendTest {
    private val classIndex = CoreTester.classIndex
    private val intType = VirtualType.of("I")
    private val booleanType = VirtualType.of("Z")
    private val objectType = VirtualType.of("Ljava/lang/Object;")
    private val builderType = VirtualType.of("Ljava/lang/StringBuilder;")

    @Test
    fun registeredEmulatorOverridesRunnableGuestBytecodeEndToEnd() {
        val vm = VmServices(classIndex)
        val target = method(CALLEE, "returnsSeven", "()I")
        vm.invocationServices.emulators.register(
            registration(target, effect = SideEffectLevel.WEAK) {
                returnedInt(41)
            },
        )

        val graph = vm.newSession(view(CALLER, "callsCalleeAndReturnsItsResult()I")).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(41, intType), graph.outcomes.single().returnedValue())
        assertEquals(SideEffectLevel.WEAK, graph.explored.sideEffectLevel(0))
        val invoke = graph.nodePile(0).single()
        assertSame(invoke.activation, invoke.children.single().activation)
    }

    @Test
    fun noRegistrationUsesGuestBytecodeThenOpaqueWhenNoGuestBodyExists() {
        val vm = VmServices(classIndex)

        val guest = vm.newSession(view(CALLER, "callsCalleeAndReturnsItsResult()I")).run()
        val opaque = vm.newSession(view(CALLER, "callsNativeCallee()I")).run()

        assertEquals(Value.Known(7, intType), guest.outcomes.single().returnedValue())
        assertEquals(SideEffectLevel.NONE, guest.explored.sideEffectLevel(0))
        assertEquals(Value.Unknown(intType), opaque.outcomes.single().returnedValue())
        assertEquals(SideEffectLevel.STRONG, opaque.explored.sideEffectLevel(0))
    }

    @Test
    fun exactRegistrationBeatsPredicatesAndFirstPredicateWinsDeterministically() {
        val target = method(CALLEE, "returnsSeven", "()I")
        val exactVm = VmServices(classIndex)
        exactVm.invocationServices.emulators.register(
            predicateRegistration("first", effect = SideEffectLevel.NONE, predicate = { true }) { returnedInt(1) },
        )
        exactVm.invocationServices.emulators.register(
            predicateRegistration("second", effect = SideEffectLevel.NONE, predicate = { true }) { returnedInt(2) },
        )
        exactVm.invocationServices.emulators.register(registration(target) { returnedInt(3) })

        val predicateVm = VmServices(classIndex)
        predicateVm.invocationServices.emulators.register(
            predicateRegistration("first", effect = SideEffectLevel.NONE, predicate = { true }) { returnedInt(4) },
        )
        predicateVm.invocationServices.emulators.register(
            predicateRegistration("second", effect = SideEffectLevel.NONE, predicate = { true }) { returnedInt(5) },
        )

        assertEquals(
            Value.Known(3, intType),
            exactVm.newSession(view(CALLER, "callsCalleeAndReturnsItsResult()I")).run().outcomes.single().returnedValue(),
        )
        assertEquals(
            Value.Known(4, intType),
            predicateVm.newSession(view(CALLER, "callsCalleeAndReturnsItsResult()I")).run().outcomes.single().returnedValue(),
        )
    }

    @Test
    fun ordinaryHandlerDeclinesUnknownAndUnmaterializableArgumentsBeforeGuestExecution() {
        var executions = 0
        val vm = VmServices(classIndex)
        vm.invocationServices.emulators.register(
            registration(method(CALLEE, "identityInt", "(I)I")) {
                executions++
                returnedInt(8)
            },
        )
        vm.invocationServices.emulators.register(
            registration(method(INVOKE_REVIEW_CALLEE, "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;")) {
                executions++
                SynchronousCallResult.Returned(HostReturn.Value(HostValue.TypedNull(objectType)))
            },
        )

        val unknown = identityIntSession(vm, Value.Unknown(intType)).run()
        val objectView = view(INVOKE_REVIEW, "callOpaqueClassResult()Linvoke_review_identity;")
        val objectFrame = Frame.entryFrame(objectView)
        val tracked = objectFrame.allocateObject(VirtualType.of("Lnew_instance_test;"), emptyMap())
        objectFrame.assign(0, tracked)
        val unmaterializable = identityObjectSession(vm, objectView, objectFrame).run()

        assertEquals(0, executions)
        assertEquals(Value.Unknown(intType), unknown.outcomes.single().returnedValue())
        assertEquals(tracked, unmaterializable.outcomes.single().returnedValue())
    }

    @Test
    fun unknownCapableHandlerReceivesTypedUnknownAndNeverSchedulerValue() {
        lateinit var received: EmulatorValue
        val vm = VmServices(classIndex)
        vm.invocationServices.emulators.register(
            registration(
                method(CALLEE, "identityInt", "(I)I"),
                handlesUnknownArguments = true,
            ) { call ->
                received = call.arguments.single()
                returnedInt(9)
            },
        )

        val graph = identityIntSession(vm, Value.Unknown(intType)).run()

        assertTrue(received is EmulatorValue.Unknown)
        assertEquals(intType, received.type)
        assertFalse((received as Any) is Value)
        assertEquals(Value.Known(9, intType), graph.outcomes.single().returnedValue())
    }

    @Test
    fun unknownCapableHandlerReceivesUnknownForKnownValueWithoutAJavaForm() {
        lateinit var received: EmulatorValue
        val vm = VmServices(classIndex)
        val target = method(INVOKE_REVIEW_CALLEE, "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;")
        vm.invocationServices.emulators.register(
            registration(target, handlesUnknownArguments = true) { call ->
                received = call.arguments.single()
                SynchronousCallResult.Returned(HostReturn.Value(HostValue.TypedNull(objectType)))
            },
        )
        val entry = view(INVOKE_REVIEW, "callOpaqueClassResult()Linvoke_review_identity;")
        val frame = Frame.entryFrame(entry)
        val tracked = frame.allocateObject(VirtualType.of("Lnew_instance_test;"), emptyMap())
        frame.assign(0, tracked)

        identityObjectSession(vm, entry, frame).run()

        assertEquals(EmulatorValue.Unknown(tracked.type), received)
    }

    @Test
    fun ordinaryHandlerDeclinesAnUnknownReceiverAndCapturedFrameworkBodyExecutes() {
        var executions = 0
        val vm = VmServices(classIndex)
        val target = method("Ljava/lang/String;", "length", "()I")
        vm.invocationServices.emulators.register(
            registration(target) {
                executions++
                returnedInt(1)
            },
        )

        val graph = instanceSession(vm, target, Value.Unknown(VirtualType.of("Ljava/lang/String;"))).run()
        val invoke = graph.nodePile(0).single()

        assertEquals(0, executions)
        assertEquals(
            Value.Unknown(intType),
            graph.outcomes.filterIsInstance<Outcome.Returned>().single().value,
        )
        assertEquals(
            setOf(VirtualType.of("Ljava/lang/NullPointerException;")),
            graph.outcomes.filterIsInstance<Outcome.Threw>().mapTo(mutableSetOf()) { it.exception.type },
        )
        assertEquals(SideEffectLevel.NONE, invoke.sideEffectLevel)
        assertEquals(null, invoke.outcome.hostFailure)
    }

    @Test
    fun unknownCapableHandlerReceivesATypedUnknownReceiver() {
        lateinit var receiver: EmulatorValue
        val vm = VmServices(classIndex)
        val stringType = VirtualType.of("Ljava/lang/String;")
        val target = method("Ljava/lang/String;", "length", "()I")
        vm.invocationServices.emulators.register(
            registration(target, handlesUnknownArguments = true) { call ->
                receiver = requireNotNull(call.receiver)
                returnedInt(13)
            },
        )

        val graph = instanceSession(vm, target, Value.Unknown(stringType)).run()

        assertEquals(EmulatorValue.Unknown(stringType), receiver)
        assertFalse((receiver as Any) is Value)
        assertEquals(Value.Known(13, intType), graph.outcomes.single().returnedValue())
    }

    @Test
    fun declaredEffectIsVisibleBeforeAHandlerFailure() {
        lateinit var session: ExecutionSession
        val vm = VmServices(classIndex)
        vm.invocationServices.emulators.register(
            registration(method(CALLEE, "nativeCallee", "()I"), effect = SideEffectLevel.WEAK) {
                assertEquals(SideEffectLevel.WEAK, session.currentNode!!.sideEffectLevel)
                assertEquals(SideEffectLevel.WEAK, session.currentNode!!.activation.declaredEffect)
                throw IllegalStateException("handler failed")
            },
        )
        session = vm.newSession(view(CALLER, "callsNativeCallee()I"))

        val node = session.step()!!

        assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
        assertEquals(SideEffectLevel.WEAK, node.sideEffectLevel)
        assertTrue(node.outcome.hostFailure is IllegalStateException)
    }

    @Test
    fun returnedResultUsesSchedulerContinuationAndFinalizesHostMutation() {
        val vm = VmServices(classIndex)
        val (session, ref, original) = stringBuilderSession(vm) { call, builder ->
            builder.append("-returned")
            SynchronousCallResult.Returned(
                HostReturn.Value(HostValue.Reference(builder, call.returnType)),
            )
        }

        val graph = session.run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
        assertEquals("value-returned", session.currentNode!!.frame.hostObjectAt(ref).toString())
        assertEquals("value", original.toString())
    }

    @Test
    fun targetThrowFinalizesHostMutation() {
        val target = IllegalStateException("target")
        val vm = VmServices(classIndex)
        val (session, ref, _) = stringBuilderSession(vm) { _, builder ->
            builder.append("-threw")
            SynchronousCallResult.TargetThrew(
                HostThrowable(VirtualType.of("Ljava/lang/IllegalStateException;"), target),
            )
        }

        val node = session.step()!!

        assertEquals("value-threw", node.frame.hostObjectAt(ref).toString())
    }

    @Test
    fun classInitRequestSchedulesGuestWorkThenRetriesTheHandler() {
        var executions = 0
        val vm = VmServices(classIndex)
        vm.invocationServices.emulators.register(
            registration(method(CALLEE, "nativeCallee", "()I")) {
                executions++
                if (executions == 1) {
                    SynchronousCallResult.NeedsClassInit(VirtualType.of(INIT_TARGET))
                } else {
                    returnedInt(12)
                }
            },
        )
        val session = vm.newSession(view(CALLER, "callsNativeCallee()I"))
        val root = session.frontier.single()

        val graph = session.run()
        val nodes = allNodes(root)

        assertEquals(2, executions)
        assertTrue(nodes.any { it.activation.classInitContext?.activeInitializer?.type == VirtualType.of(INIT_TARGET) })
        assertEquals(Value.Known(12, intType), graph.outcomes.single().returnedValue())
    }

    @Test
    fun infrastructureFailureAbortsAfterFinalizingHostMutation() {
        val failure = IllegalStateException("infrastructure")
        val vm = VmServices(classIndex)
        val (session, ref, _) = stringBuilderSession(vm) { _, builder ->
            builder.append("-failed")
            SynchronousCallResult.InfrastructureFailure(failure)
        }

        val node = session.step()!!

        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE, failure), node.outcome)
        assertEquals("value-failed", node.frame.hostObjectAt(ref).toString())
    }

    @Test
    fun replacementAtomicallyChangesHandlerCapabilityAndEffectOnlyForOneVm() {
        var initialExecutions = 0
        val target = method(CALLEE, "identityInt", "(I)I")
        val first = VmServices(classIndex)
        val second = VmServices(classIndex)
        first.invocationServices.emulators.register(
            registration(target, effect = SideEffectLevel.NONE) {
                initialExecutions++
                returnedInt(1)
            },
        )
        first.invocationServices.emulators.register(
            registration(target, handlesUnknownArguments = true, effect = SideEffectLevel.WEAK) { call ->
                assertTrue(call.arguments.single() is EmulatorValue.Unknown)
                returnedInt(2)
            },
        )

        val firstGraph = identityIntSession(first, Value.Unknown(intType)).run()
        val secondGraph = identityIntSession(second, Value.Unknown(intType)).run()

        assertEquals(0, initialExecutions)
        assertEquals(Value.Known(2, intType), firstGraph.outcomes.single().returnedValue())
        assertEquals(SideEffectLevel.WEAK, firstGraph.explored.sideEffectLevel(0))
        assertEquals(Value.Unknown(intType), secondGraph.outcomes.single().returnedValue())
        assertEquals(SideEffectLevel.NONE, secondGraph.explored.sideEffectLevel(0))
    }

    @Test
    fun replacementAfterUnknownProbeDoesNotChangeTheBoundHandlerCapabilityOrEffect() {
        var initialExecutions = 0
        var replacementExecutions = 0
        val registry = EmulatorRegistry()
        val target = method(CALLEE, "identityInt", "(I)I")
        registry.register(
            registration(target, handlesUnknownArguments = true, effect = SideEffectLevel.WEAK) { call ->
                assertTrue(call.arguments.single() is EmulatorValue.Unknown)
                initialExecutions++
                returnedInt(1)
            },
        )
        val invoker = invokerOf(classIndex, listOf(EmulateBackend(registry)))
        val preparation = invoker.prepare(
            Invocation(target, InvokeKind.STATIC, listOf(Value.Unknown(intType))),
            VirtualType.of(CALLER),
        ) as InvocationPreparation.Ready
        val frame = Frame.entryFrame(view(CALLER, "callsNativeCallee()I"))
        val plan = invoker.planOn(preparation, frame) as InvocationPlan.Execute

        registry.register(
            registration(target, handlesUnknownArguments = false, effect = SideEffectLevel.STRONG) {
                replacementExecutions++
                returnedInt(2)
            },
        )

        val selected = plan.select(frame, classIndex.classLoader)
        val result = selected.execute()
        selected.finalize(frame)

        assertEquals(SideEffectLevel.WEAK, plan.effect)
        assertEquals(returnedInt(1), result)
        assertEquals(1, initialExecutions)
        assertEquals(0, replacementExecutions)
    }

    @Test
    fun handlerCannotReturnSchedulerOwnedObjectsAtTheRootOrNestedInAHostGraph() {
        val schedulerValues = listOf<(ExecutionSession) -> Any>(
            { requireNotNull(it.currentNode).frame },
            { it },
            { requireNotNull(it.currentNode) },
            { Value.Unknown(objectType) },
            { HeapRef(99) },
            { HeapObject.TrackedObject(objectType, uninitialized = false) },
        )

        for (nested in listOf(false, true)) {
            for (schedulerValue in schedulerValues) {
                val vm = VmServices(classIndex)
                lateinit var session: ExecutionSession
                vm.invocationServices.emulators.register(
                    registration(method(CALLEE, "nativeCallee", "()I")) {
                        val raw = schedulerValue(session)
                        val returned = if (nested) arrayListOf(raw) else raw
                        SynchronousCallResult.Returned(
                            HostReturn.Value(HostValue.Reference(returned, objectType)),
                        )
                    },
                )
                session = vm.newSession(view(CALLER, "callsNativeCallee()I"))
                val probedBefore = probeNextHeapId(session.frontier.single().frame)

                val node = session.step()!!

                assertEquals(AbortReason.HOST_FAILURE, node.outcome.abortReason)
                assertTrue(node.outcome.hostFailure is HostGraphOwnershipException)
                // The probe before the step consumed one id; a heap id given to the scheduler object would make two.
                assertEquals(probedBefore + 1, probeNextHeapId(node.frame), "scheduler object must not receive a heap id")
            }
        }
    }


    @Test
    fun defaultTextUtilsHandlerHandlesNullEmptyNonEmptyAndDeclinesUnknown() {
        val vm = VmServices(classIndex)
        val target = method("Landroid/text/TextUtils;", "isEmpty", "(Ljava/lang/CharSequence;)Z")
        val registration = requireNotNull(vm.invocationServices.emulators.find(target))
        val charSequence = VirtualType.of("Ljava/lang/CharSequence;")
        val knownNull = textUtilsSession(vm, target, Value.Known(null, charSequence)).run()
        val empty = textUtilsSession(vm, target, Value.Known("", charSequence)).run()
        val nonEmpty = textUtilsSession(vm, target, Value.Known("x", charSequence)).run()
        val unknown = textUtilsSession(vm, target, Value.Unknown(charSequence)).run()

        assertEquals(SideEffectLevel.STRONG, registration.effect)
        assertEquals(Value.Known(true, booleanType), knownNull.outcomes.single().returnedValue())
        assertEquals(Value.Known(true, booleanType), empty.outcomes.single().returnedValue())
        assertEquals(Value.Known(false, booleanType), nonEmpty.outcomes.single().returnedValue())
        assertEquals(Value.Unknown(booleanType), unknown.outcomes.single().returnedValue())
        assertEquals(null, unknown.nodePile(0).single().outcome.hostFailure)
        assertEquals(SideEffectLevel.STRONG, unknown.explored.sideEffectLevel(0))
    }

    @Test
    fun applicationOverrideOfGetCauseRunsItsOwnBody() {
        // ART dispatches Throwable.getCause to the receiver's override; this override returns the receiver,
        // which the Throwable cause slot (null after Exception()) can never produce.
        val graph = VmServices(classIndex).newSession(
            view("Lthrowable_cause_override_test;", "overrideReturnsReceiver()I"),
        ).run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Outcome.Returned(Value.Known(1, intType))), graph.outcomes)
    }

    private fun identityIntSession(vm: VmServices, value: Value): ExecutionSession {
        val entry = view(CALLER, "callsCalleeAndReturnsItsResult()I")
        val frame = Frame.entryFrame(entry).also { it.assign(0, value) }
        val target = method(CALLEE, "identityInt", "(I)I")
        return vm.newSession(entry, Limits(), frame) { address, method, resolver ->
            if (address == 0 && method.signature == entry.signature) invokeOp(address, target, intArrayOf(0))
            else OpBuilder.build(address, method, resolver)
        }
    }

    private fun identityObjectSession(vm: VmServices, entry: MethodView, frame: Frame): ExecutionSession {
        val target = method(INVOKE_REVIEW_CALLEE, "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;")
        return vm.newSession(entry, Limits(), frame) { address, method, resolver ->
            if (address == 0 && method.signature == entry.signature) invokeOp(address, target, intArrayOf(0))
            else OpBuilder.build(address, method, resolver)
        }
    }

    private fun instanceSession(vm: VmServices, target: MethodDescriptor, receiver: Value): ExecutionSession {
        val entry = view(CALLER, "callsCalleeAndReturnsItsResult()I")
        val frame = Frame.entryFrame(entry).also { it.assign(0, receiver) }
        return vm.newSession(entry, Limits(), frame) { address, method, resolver ->
            if (address == 0 && method.signature == entry.signature) {
                invokeOp(address, target, intArrayOf(0), InvokeKind.VIRTUAL)
            } else {
                OpBuilder.build(address, method, resolver)
            }
        }
    }

    private fun textUtilsSession(vm: VmServices, target: MethodDescriptor, value: Value): ExecutionSession {
        val entry = view(CALLER, "callsCalleeAndReturnsItsResult()I")
        val frame = Frame.entryFrame(entry).also { it.assign(0, value) }
        return vm.newSession(entry, Limits(), frame) { address, method, resolver ->
            if (address == 0 && method.signature == entry.signature) invokeOp(address, target, intArrayOf(0))
            else OpBuilder.build(address, method, resolver)
        }
    }

    private fun stringBuilderSession(
        vm: VmServices,
        result: (EmulatorInvocation, StringBuilder) -> SynchronousCallResult,
    ): Triple<ExecutionSession, HeapRef, StringBuilder> {
        val target = method("Ljava/lang/StringBuilder;", "append", "(J)Ljava/lang/StringBuilder;")
        vm.invocationServices.emulators.register(
            registration(target, effect = SideEffectLevel.WEAK) { call ->
                val receiver = (requireNotNull(call.receiver) as EmulatorValue.Known).javaValue as StringBuilder
                result(call, receiver)
            },
        )
        val entry = view("Linvoke_reflected_test;", "invokeStringBuilderAppendWithLong()V")
        val frame = Frame.entryFrame(entry)
        val original = StringBuilder("value")
        val ref = frame.allocateHostObject(builderType, original)
        frame.assign(0, Value.Known(ref, builderType))
        frame.assign(1, Value.Known(1L, VirtualType.of("J")))
        return Triple(vm.newSession(entry, entryFrame = frame), ref, original)
    }

    private fun invokeOp(
        address: Int,
        target: MethodDescriptor,
        registers: IntArray,
        kind: InvokeKind = InvokeKind.STATIC,
    ) = object : Op(address) {
        override val name = "invoke-emulator-test"
        override fun execute(frame: Frame): OpExecution = OpExecution.single(OpArm.Invoke(target, registers, kind))
    }

    private fun registration(
        target: MethodDescriptor,
        handlesUnknownArguments: Boolean = false,
        effect: SideEffectLevel = SideEffectLevel.STRONG,
        handler: EmulatorHandler,
    ) = EmulatorRegistration(
        EmulatorMatcher.Exact(target),
        EmulatorCapability(handlesUnknownArguments),
        effect,
        handler,
    )

    private fun predicateRegistration(
        id: String,
        effect: SideEffectLevel,
        predicate: (MethodDescriptor) -> Boolean,
        handler: EmulatorHandler,
    ) = EmulatorRegistration(
        EmulatorMatcher.Predicate(id, predicate),
        EmulatorCapability(handlesUnknownArguments = false),
        effect,
        handler,
    )

    private fun returnedInt(value: Int) =
        SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(value, intType)))

    private fun view(className: String, descriptor: String): MethodView =
        classIndex.requireMethodView(className, descriptor)

    private fun method(definingClass: String, name: String, proto: String) =
        MethodDescriptor(VirtualType.of(definingClass), name, proto)

    private fun Outcome.returnedValue(): Value? = (this as Outcome.Returned).value

    private fun allNodes(root: ExecutionNode): List<ExecutionNode> = buildList {
        fun collect(node: ExecutionNode) {
            if (any { it === node }) return
            add(node)
            node.children.forEach(::collect)
        }
        collect(root)
    }

    private companion object {
        const val CALLER = "Lcalls/caller;"
        const val CALLEE = "Lcalls/callee;"
        const val INVOKE_REVIEW = "Linvoke_review;"
        const val INVOKE_REVIEW_CALLEE = "Linvoke_review_callee;"
        const val INIT_TARGET = "Linvocation_class_init_target;"
    }
}

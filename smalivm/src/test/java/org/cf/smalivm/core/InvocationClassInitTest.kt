package org.cf.smalivm.core

import org.cf.smalivm.core.CoreTester.allNodes
import org.cf.smalivm.core.CoreTester.initializersRun
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/** Every run starts as a fresh process, so a class the run does not trigger stays `Uninitialized`. */
class InvocationClassInitTest {
    private val classIndex = CoreTester.classIndex

    /**
     * Catches selecting or executing the callee before the declaring class's initializer runs: the emulator or
     * the guest callee would then see the class uninitialized and `runs` unwritten.
     */
    @Test
    fun invokeStaticInitializesTheResolvedDeclaringClassBeforeBackendSelectionAndExecution() {
        val graph = run("callStatic()V")
        val calleeEntry = calleeEntry(graph, "$TARGET->staticTouch()V")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(TARGET), initializersRun(graph))
        assertEquals(ClassInitState.INITIALIZED, returningFrame(graph).initState(type(TARGET)))
        assertEquals(ClassInitState.INITIALIZED, calleeEntry.frame.initState(type(TARGET)))
        assertEquals(Value.Known(1, type("I")), calleeEntry.frame.getStaticField(type(TARGET), TARGET_RUNS))

        val vm = VmServices(classIndex)
        val emulatedStates = mutableListOf<ClassInitState>()
        vm.emulate(MethodSignature(type(TARGET), "staticTouch", "()V")) { call ->
            emulatedStates += call.classInitStatus.stateOf(type(TARGET))
            SynchronousCallResult.Returned(HostReturn.Void)
        }
        val emulated = vm.newSession(view(CALLER, "callStatic()V"), entryClassState = FRESH).run()

        assertEquals(CompletionStatus.Complete, emulated.completion())
        assertEquals(listOf(TARGET), initializersRun(emulated))
        assertEquals(listOf(ClassInitState.INITIALIZED), emulatedStates)
    }

    @Test
    fun inheritedStaticResolutionInitializesTheActualDeclaringClass() {
        val graph = run("callInheritedStatic()V")
        val returning = returningFrame(graph)

        assertEquals(listOf(PARENT), initializersRun(graph))
        assertEquals(ClassInitState.INITIALIZED, returning.initState(type(PARENT)))
        assertEquals(ClassInitState.UNINITIALIZED, returning.initState(type(CHILD)))
    }

    @Test
    fun virtualAndInterfaceCallsDoNotInitializeTheirResolvedClasses() {
        val virtual = runWithReceiver("callVirtual(Linvocation_class_init_target;)V", TARGET)
        assertNoInitialization(virtual, TARGET)

        val interfaceCall = runWithReceiver(
            "callInterface(Linvocation_class_init_interface;)V",
            IMPLEMENTATION,
        )
        assertNoInitialization(interfaceCall, IMPLEMENTATION)
    }

    @Test
    fun nonConstructorDirectAndSuperCallsDoNotInitializeTheirResolvedClasses() {
        val direct = runInstanceEntry(TARGET, "callDirect()V")
        assertNoInitialization(direct, TARGET)

        val superCall = runInstanceEntry(CHILD, "callSuper()V")
        assertNoInitialization(superCall, PARENT)
    }

    @Test
    fun constructorAfterNewInstanceDoesNotRunTheInitializerTwice() {
        val graph = run("construct()V")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(TARGET), initializersRun(graph))
        assertEquals(Value.Known(1, type("I")), returningFrame(graph).getStaticField(type(TARGET), TARGET_RUNS))
    }

    @Test
    fun explicitClinitInvocationDoesNotRecursivelyTriggerItself() {
        val graph = CoreTester.vmServices.newSession(view("Lclass_init/trigger;", "callsWitnessClinitDirectly()I")).run()
        val clinitEntries = allNodes(graph).filter {
            it.isCallEntry && it.view.signature.toString() == "Lclass_init/witness;-><clinit>()V"
        }

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(1, clinitEntries.size)
        assertEquals(null, clinitEntries.single().activation.classInitContext)
        assertEquals(emptyList<String>(), initializersRun(graph))
    }

    @Test
    fun debuggerSeesStaticInitializerWorkAndStopsBeforeTheSameAddressRetry() {
        val session = CoreTester.vmServices.newSession(view(CALLER, "callStatic()V"))
        val debugger = Debugger(session)
        val trigger = debugger.cursor!!

        val retry = debugger.stepOver()!!

        val initializer = trigger.children.single()
        assertEquals("$TARGET-><clinit>()V", initializer.view.signature.toString())
        assertEquals(type(TARGET), initializer.activation.classInitContext?.activeInitializer?.type)
        assertTrue(initializer.progress is OpProgress.Executed)
        assertNotSame(trigger, retry)
        assertSame(trigger.activation, retry.activation)
        assertEquals(trigger.address, retry.address)
        assertEquals(OpProgress.Pending, retry.progress)
    }

    @Test
    fun instanceCallCreatesNoFakeDebuggerInitializerChild() {
        val session = sessionWithReceiver("callVirtual(Linvocation_class_init_target;)V", TARGET)
        val debugger = Debugger(session)
        val invoke = debugger.cursor!!

        val continuation = debugger.stepOver()!!

        val callee = invoke.children.single()
        assertEquals("$TARGET->virtualTouch()V", callee.view.signature.toString())
        assertEquals(null, callee.activation.classInitContext)
        assertTrue(allDescendants(invoke).none { it.activation.classInitContext != null })
        assertSame(invoke.activation, continuation.activation)
        assertEquals(3, continuation.address)
        assertEquals(OpProgress.Pending, continuation.progress)
    }

    /**
     * Catches selecting or executing the callee before the applicable class-init trigger settles a class without
     * a `<clinit>`: the emulator or the guest callee would see it uninitialized.
     */
    @Test
    fun classWithoutInitializerIsSettledBeforeSelectionAndExecution() {
        val noClinit = type(NO_CLINIT)
        val view = view("Lclass_init/trigger;", "callsNoClinitTouch()I")
        val session = CoreTester.vmServices.newSession(view, entryClassState = FRESH)
        assertEquals(ClassInitState.UNINITIALIZED, session.frontier.single().frame.initState(noClinit))

        val graph = session.run()

        assertEquals(listOf(Outcome.Returned(Value.Known(7, type("I")))), graph.outcomes)
        assertEquals(ClassInitState.INITIALIZED, calleeEntry(graph, "$NO_CLINIT->touch()V").frame.initState(noClinit))

        val vm = VmServices(classIndex)
        val emulatedStates = mutableListOf<ClassInitState>()
        vm.emulate(MethodSignature(noClinit, "touch", "()V")) { call ->
            emulatedStates += call.classInitStatus.stateOf(noClinit)
            SynchronousCallResult.Returned(HostReturn.Void)
        }
        val emulated = vm.newSession(view, entryClassState = FRESH).run()

        assertEquals(listOf(Outcome.Returned(Value.Known(7, type("I")))), emulated.outcomes)
        assertEquals(listOf(ClassInitState.INITIALIZED), emulatedStates)
    }

    @Test
    fun stackOverflowErrorEscapesTheRealContainedBoundary() {
        val view = view(CALLER, "callStatic()V")
        val fatal = StackOverflowError("scripted")
        val session = CoreTester.vmServices.newSession(view, Limits(), null) { address, method, resolver ->
            if (method.signature == view.signature && address == 0) {
                object : Op(address) {
                    override val name = "throw-stack-overflow"

                    override fun execute(frame: Frame): OpExecution = throw fatal
                }
            } else {
                OpBuilder.build(address, method, resolver)
            }
        }

        val scheduled = session.frontier.single()
        val failure = assertThrows<StackOverflowError> { session.step() }
        assertSame(fatal, failure)
        assertSame(scheduled, session.currentNode)
        assertEquals(null, session.currentNode!!.outcome)
        assertTrue(session.currentNode!!.children.isEmpty())
        assertTrue(session.frontier.isEmpty())
        assertTrue(session.outcomes.isEmpty())
    }

    private fun run(descriptor: String): ExecutionGraph =
        CoreTester.vmServices.newSession(view(CALLER, descriptor), entryClassState = FRESH).run()

    private fun runWithReceiver(descriptor: String, runtimeType: String): ExecutionGraph =
        sessionWithReceiver(descriptor, runtimeType).run()

    private fun sessionWithReceiver(descriptor: String, runtimeType: String): ExecutionSession {
        val view = view(CALLER, descriptor)
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters.single().register, frame.allocateObject(type(runtimeType), emptyMap()))
        return CoreTester.vmServices.newSession(view, Limits(), frame, FRESH)
    }

    private fun runInstanceEntry(className: String, descriptor: String): ExecutionGraph {
        val view = view(className, descriptor)
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters.single().register, frame.allocateObject(type(className), emptyMap()))
        return CoreTester.vmServices.newSession(view, Limits(), frame, FRESH).run()
    }

    private fun assertNoInitialization(graph: ExecutionGraph, className: String) {
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph))
        val frame = returningFrame(graph)
        assertEquals(ClassInitState.UNINITIALIZED, frame.initState(type(className)))
        assertEquals(Value.Top, frame.getStaticField(type(className), "$className->runs:I"))
    }

    /** The one node that entered [signature] at its first instruction; its frame is the state the callee starts in. */
    private fun calleeEntry(graph: ExecutionGraph, signature: String): ExecutionNode =
        allNodes(graph).single { it.isCallEntry && it.view.signature.toString() == signature }

    private fun returningFrame(graph: ExecutionGraph): Frame = allNodes(graph).single {
        it.outcome is Outcome.Returned && it.activation.caller == null
    }.frame

    private fun allDescendants(node: ExecutionNode): List<ExecutionNode> {
        val result = mutableListOf<ExecutionNode>()
        fun collect(parent: ExecutionNode) {
            parent.children.forEach {
                result.add(it)
                collect(it)
            }
        }
        collect(node)
        return result
    }

    private fun view(className: String, descriptor: String) =
        classIndex.requireMethodView(className, descriptor)

    private fun type(descriptor: String) = VirtualType.of(descriptor)

    private companion object {
        const val CALLER = "Linvocation_class_init_caller;"
        const val TARGET = "Linvocation_class_init_target;"
        const val PARENT = "Linvocation_class_init_parent;"
        const val CHILD = "Linvocation_class_init_child;"
        const val IMPLEMENTATION = "Linvocation_class_init_impl;"
        const val NO_CLINIT = "Lclass_init/no_clinit;"
        const val TARGET_RUNS = "$TARGET->runs:I"
        val FRESH = EntryClassState.FRESH_PROCESS
    }
}

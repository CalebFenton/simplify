package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class InvocationClassInitTest {
    private val classIndex = CoreTester.classIndex

    /**
     * Catches probing or executing the callee before the declaring class's initializer runs: the backend or the
     * callee would then see `runs` unwritten and the class uninitialized.
     */
    @Test
    fun invokeStaticInitializesTheResolvedDeclaringClassBeforeBackendProbeAndExecution() {
        lateinit var session: ExecutionSession
        val probedRuns = mutableListOf<Value>()
        val probedStates = mutableListOf<ClassInitState>()
        val executedRuns = mutableListOf<Value>()
        val backend = RecordingBackend { _ ->
            val frame = session.currentNode!!.frame
            probedRuns += frame.getStaticField(type(TARGET), TARGET_RUNS)
            probedStates += frame.initState(type(TARGET))
        }
        val view = view(CALLER, "callStatic()V")
        session = ExecutionSession.of(
            view,
            classIndex,
            Limits(),
            Frame.entryFrame(view),
            { address, method, index ->
                if (method.signature == "$TARGET->staticTouch()V" && address == 0) {
                    observingOp(address, method, index) { executedRuns += it.getStaticField(type(TARGET), TARGET_RUNS) }
                } else {
                    OpBuilder.build(address, method, index)
                }
            },
            invokerOf(classIndex, listOf(backend)),
        )

        val graph = session.run()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(TARGET), initializersRun(graph))
        assertEquals(ClassInitState.INITIALIZED, returningFrame(graph).initState(type(TARGET)))
        assertEquals(listOf(ClassInitState.INITIALIZED), probedStates)
        assertEquals(listOf<Value>(Value.Known(1, type("I"))), probedRuns)
        assertEquals(listOf<Value>(Value.Known(1, type("I"))), executedRuns)
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
        val graph = ExecutionSession.of(
            view("Lclass_init/trigger;", "callsWitnessClinitDirectly()I"),
            classIndex,
            Limits(),
        ).run()
        val clinitEntries = allNodes(graph).filter {
            it.isCallEntry && it.view.signature == "Lclass_init/witness;-><clinit>()V"
        }

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(1, clinitEntries.size)
        assertEquals(null, clinitEntries.single().activation.classInitContext)
        assertEquals(emptyList<String>(), initializersRun(graph))
    }

    @Test
    fun debuggerSeesStaticInitializerWorkAndStopsBeforeTheSameAddressRetry() {
        val session = ExecutionSession.of(view(CALLER, "callStatic()V"), classIndex, Limits())
        val debugger = Debugger(session)
        val trigger = debugger.cursor!!

        val retry = debugger.stepOver()!!

        val initializer = trigger.children.single()
        assertEquals("$TARGET-><clinit>()V", initializer.view.signature)
        assertEquals(type(TARGET), initializer.activation.classInitContext?.activeInitializer?.type)
        assertTrue(initializer.opExecutionStarted)
        assertNotSame(trigger, retry)
        assertSame(trigger.activation, retry.activation)
        assertEquals(trigger.address, retry.address)
        assertFalse(retry.opExecutionStarted)
    }

    @Test
    fun instanceCallCreatesNoFakeDebuggerInitializerChild() {
        val session = sessionWithReceiver("callVirtual(Linvocation_class_init_target;)V", TARGET)
        val debugger = Debugger(session)
        val invoke = debugger.cursor!!

        val continuation = debugger.stepOver()!!

        val callee = invoke.children.single()
        assertEquals("$TARGET->virtualTouch()V", callee.view.signature)
        assertEquals(null, callee.activation.classInitContext)
        assertTrue(allDescendants(invoke).none { it.activation.classInitContext != null })
        assertSame(invoke.activation, continuation.activation)
        assertEquals(3, continuation.address)
        assertFalse(continuation.opExecutionStarted)
    }

    /**
     * Catches probing or executing the callee before the applicable class-init trigger settles a class without
     * a `<clinit>`: the backend or the callee would see it uninitialized.
     */
    @Test
    fun classWithoutInitializerIsSettledBeforeProbeAndExecution() {
        lateinit var session: ExecutionSession
        val noClinit = type(NO_CLINIT)
        val probedStates = mutableListOf<ClassInitState>()
        val executedStates = mutableListOf<ClassInitState>()
        val backend = RecordingBackend { call ->
            assertEquals(NO_CLINIT, call.target.definingClass.descriptor)
            assertEquals("touch", call.target.name)
            assertEquals("()V", call.target.proto)
            probedStates += session.currentNode!!.frame.initState(noClinit)
        }
        val view = view("Lclass_init/trigger;", "callsNoClinitTouch()I")

        session = ExecutionSession.of(
            view,
            classIndex,
            Limits(),
            Frame.entryFrame(view),
            { address, method, index ->
                if (method.signature == "$NO_CLINIT->touch()V" && address == 0) {
                    observingOp(address, method, index) { executedStates += it.initState(noClinit) }
                } else {
                    OpBuilder.build(address, method, index)
                }
            },
            invokerOf(classIndex, listOf(backend)),
        )
        assertEquals(ClassInitState.UNINITIALIZED, session.frontier.single().frame.initState(noClinit))

        val graph = session.run()

        assertEquals(listOf(Outcome.Returned(Value.Known(7, type("I")))), graph.outcomes)
        assertEquals(listOf(ClassInitState.INITIALIZED), probedStates)
        assertEquals(listOf(ClassInitState.INITIALIZED), executedStates)
    }

    @Test
    fun stackOverflowErrorEscapesTheRealContainedBoundary() {
        val view = view(CALLER, "callStatic()V")
        val fatal = StackOverflowError("scripted")
        val session = ExecutionSession.of(
            view,
            classIndex,
            Limits(),
            Frame.entryFrame(view),
        ) { address, method, index ->
            if (method.signature == view.signature && address == 0) {
                object : Op(address) {
                    override val name = "throw-stack-overflow"

                    override fun execute(frame: Frame): OpExecution = throw fatal
                }
            } else {
                OpBuilder.build(address, method, index)
            }
        }

        val scheduled = session.frontier.single()
        val failure = assertThrows<StackOverflowError> { session.step() }
        assertSame(fatal, failure)
        assertSame(scheduled, session.currentNode)
        assertEquals(null, session.currentNode?.hostFailure)
        assertTrue(session.currentNode!!.children.isEmpty())
        assertTrue(session.frontier.isEmpty())
        assertTrue(session.outcomes.isEmpty())
    }

    private fun run(descriptor: String): ExecutionGraph =
        ExecutionSession.of(view(CALLER, descriptor), classIndex, Limits()).run()

    private fun runWithReceiver(descriptor: String, runtimeType: String): ExecutionGraph =
        sessionWithReceiver(descriptor, runtimeType).run()

    private fun sessionWithReceiver(descriptor: String, runtimeType: String): ExecutionSession {
        val view = view(CALLER, descriptor)
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters.single().register, frame.allocateObject(type(runtimeType), emptyMap()))
        return ExecutionSession.of(view, classIndex, Limits(), frame)
    }

    private fun runInstanceEntry(className: String, descriptor: String): ExecutionGraph {
        val view = view(className, descriptor)
        val frame = Frame.entryFrame(view)
        frame.assign(view.parameters.single().register, frame.allocateObject(type(className), emptyMap()))
        return ExecutionSession.of(view, classIndex, Limits(), frame).run()
    }

    private fun assertNoInitialization(graph: ExecutionGraph, className: String) {
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(emptyList<String>(), initializersRun(graph))
        val frame = returningFrame(graph)
        assertEquals(ClassInitState.UNINITIALIZED, frame.initState(type(className)))
        assertEquals(Value.Top, frame.getStaticField(type(className), "$className->runs:I"))
    }

    private fun observingOp(address: Int, view: MethodView, resolver: ProgramSnapshotResolver, observe: (Frame) -> Unit): Op {
        val delegate = OpBuilder.build(address, view, resolver)
        return object : Op(address) {
            override val name = delegate.name

            override fun execute(frame: Frame): OpExecution {
                observe(frame)
                return delegate.execute(frame)
            }
        }
    }

    private fun initializersRun(graph: ExecutionGraph): List<String> {
        val activations = mutableListOf<Activation>()
        for (node in allNodes(graph)) {
            if (activations.none { it === node.activation }) activations.add(node.activation)
        }
        return activations.mapNotNull { it.classInitContext?.activeInitializer?.type?.descriptor }
    }

    private fun returningFrame(graph: ExecutionGraph): Frame = allNodes(graph).single {
        it.outcome is Outcome.Returned && it.activation.caller == null
    }.frame

    private fun allNodes(graph: ExecutionGraph): List<ExecutionNode> {
        val roots = graph.addresses().flatMap { graph.nodePile(it) }.filter { it.parent == null }
        val result = mutableListOf<ExecutionNode>()
        fun collect(node: ExecutionNode) {
            if (result.any { it === node }) return
            result.add(node)
            node.children.forEach(::collect)
        }
        roots.forEach(::collect)
        return result
    }

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

    private class RecordingBackend(private val onProbe: (BackendInvocation) -> Unit) : Backend {
        override val name = "recording"

        override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
            onProbe(call)
            return when (lookup) {
                is MethodLookup.Runnable -> BackendProbe.Schedule(lookup.view, 0)
                else -> BackendProbe.Declined("not runnable")
            }
        }
    }

    private companion object {
        const val CALLER = "Linvocation_class_init_caller;"
        const val TARGET = "Linvocation_class_init_target;"
        const val PARENT = "Linvocation_class_init_parent;"
        const val CHILD = "Linvocation_class_init_child;"
        const val IMPLEMENTATION = "Linvocation_class_init_impl;"
        const val NO_CLINIT = "Lclass_init/no_clinit;"
        const val TARGET_RUNS = "$TARGET->runs:I"
    }
}

package org.cf.smalivm.core

internal object SynchronousInvocationFixtures {
    const val CALLER = "Lcalls/caller;"
    const val CALLEE = "Lcalls/callee;"
    const val INVOKE_REVIEW = "Linvoke_review;"
    const val INIT_TARGET = "Linvocation_class_init_target;"

    val classIndex: ClassIndex = CoreTester.classIndex
    val intType: VirtualType = VirtualType.of("I")
    val longType: VirtualType = VirtualType.of("J")
    val objectType: VirtualType = VirtualType.of("Ljava/lang/Object;")
    val classType: VirtualType = VirtualType.of("Ljava/lang/Class;")

    fun view(className: String, descriptor: String): MethodView =
        classIndex.requireMethodView(className, descriptor)

    fun session(
        className: String,
        descriptor: String,
        backends: List<Backend>,
        limits: Limits = Limits(),
        opFactory: (Int, MethodView, ClassIndex) -> Op = OpBuilder::build,
    ): ExecutionSession {
        val view = view(className, descriptor)
        return ExecutionSession.of(
            view,
            classIndex,
            limits,
            Frame.entryFrame(view),
            opFactory,
            Invoker(classIndex, backends),
        )
    }

    fun nativeCallSession(
        backend: Backend,
        limits: Limits = Limits(),
    ): ExecutionSession = session(CALLER, "callsNativeCallee()I", listOf(backend), limits)

    fun nativePreparation(invoker: Invoker): InvocationPreparation.Ready =
        invoker.prepare(
            Invocation(
                MethodDescriptor(VirtualType.of(CALLEE), "nativeCallee", "()I"),
                InvokeKind.STATIC,
                emptyList(),
            ),
            VirtualType.of(CALLER),
        ) as InvocationPreparation.Ready

    fun allNodes(root: ExecutionNode): List<ExecutionNode> {
        val result = mutableListOf<ExecutionNode>()
        fun collect(node: ExecutionNode) {
            if (result.any { it === node }) return
            result += node
            node.children.forEach(::collect)
        }
        collect(root)
        return result
    }
}

internal class ScriptedSynchronousBackend(
    override val name: String,
    private val probeResult: () -> BackendProbe,
    private val executionResult: () -> SynchronousCallResult,
) : SynchronousBackend {
    var probeCount = 0
        private set
    var executeCount = 0
        private set

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
        probeCount++
        return probeResult()
    }

    override fun execute(call: SelectedHostCall): SynchronousCallResult {
        executeCount++
        return executionResult()
    }
}

internal class ScriptedProbeBackend(
    override val name: String,
    private val result: (MethodLookup) -> BackendProbe,
) : Backend {
    var probeCount = 0
        private set

    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
        probeCount++
        return result(lookup)
    }
}

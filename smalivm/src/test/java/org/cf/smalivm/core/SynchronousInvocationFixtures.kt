package org.cf.smalivm.core

internal object SynchronousInvocationFixtures {
    const val CALLER = "Lcalls/caller;"
    const val CALLEE = "Lcalls/callee;"
    const val INVOKE_CALL = "Linvoke_call;"
    const val INIT_TARGET = "Linvocation_class_init_target;"

    val classIndex: ClassIndex = CoreTester.classIndex
    val intType: VirtualType = VirtualType.of("I")
    val longType: VirtualType = VirtualType.of("J")
    val objectType: VirtualType = VirtualType.of("Ljava/lang/Object;")
    val classType: VirtualType = VirtualType.of("Ljava/lang/Class;")

    fun view(className: String, descriptor: String): MethodView =
        classIndex.requireMethodView(className, descriptor)

    /** A session over the production backends of [vm], whose emulator registrations the test controls. */
    fun session(
        className: String,
        descriptor: String,
        vm: VmServices,
        limits: Limits = Limits(),
    ): ExecutionSession = vm.newSession(view(className, descriptor), limits)

    /** A session whose calls select among [backends] alone, for failures no production backend produces. */
    fun session(
        className: String,
        descriptor: String,
        backends: List<Backend>,
        limits: Limits = Limits(),
    ): ExecutionSession =
        VmServices(classIndex)
            .newSession(view(className, descriptor), limits, null, backends, decode = OpBuilder::build)

    fun nativeCallSession(
        backend: Backend,
        limits: Limits = Limits(),
    ): ExecutionSession = session(CALLER, "callsNativeCallee()I", listOf(backend), limits)

    /** Plans `callsNativeCallee`'s call to the bodyless `nativeCallee` from that caller's entry frame. */
    fun nativePlan(invoker: Invoker): InvocationPlan {
        val preparation = invoker.prepare(
            Invocation(
                MethodSignature(VirtualType.of(CALLEE), "nativeCallee", "()I"),
                InvokeKind.STATIC,
                emptyList(),
            ),
            VirtualType.of(CALLER),
        ) as InvocationPreparation.Ready
        return invoker.plan(preparation, Frame.entryFrame(view(CALLER, "callsNativeCallee()I")))
    }

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

/** Registers [handler] for exactly [target] on this VM. It accepts unknown arguments, so it runs whenever selected. */
internal fun VmServices.emulate(
    target: MethodSignature,
    effect: SideEffectLevel = SideEffectLevel.NONE,
    handler: EmulatorHandler,
) = invocationServices.emulators.register(
    EmulatorRegistration(
        EmulatorMatcher.Exact(target),
        EmulatorCapability(handlesUnknownArguments = true),
        effect,
        handler,
    ),
)

/**
 * Counts backend selections for [target] in sessions this VM starts afterwards. Every selection probes the
 * emulator backend first, which consults this never-matching predicate unless an exact registration for
 * [target] answers first.
 */
internal fun VmServices.countSelections(target: MethodSignature): () -> Int {
    var selections = 0
    invocationServices.emulators.register(
        EmulatorRegistration(
            EmulatorMatcher.Predicate("selections of $target") { candidate ->
                if (candidate == target) selections++
                false
            },
            EmulatorCapability(handlesUnknownArguments = false),
            SideEffectLevel.NONE,
        ) { error("a never-matching predicate is never executed") },
    )
    return { selections }
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

package org.cf.smalivm.core

import java.math.BigDecimal
import java.math.BigInteger

/** Shared harness for the host-call tests: production backends under a policy authorizing the host fixtures. */
internal abstract class DirectHostTestBase {
    protected val objectType = VirtualType.of("Ljava/lang/Object;")
    protected val builderType = VirtualType.of("Ljava/lang/StringBuilder;")
    protected val stringType = VirtualType.of("Ljava/lang/String;")
    protected val intType = VirtualType.of("I")
    protected val intArrayType = VirtualType.of("[I")
    protected val objectArrayType = VirtualType.of("[Ljava/lang/Object;")
    protected val appendLong = MethodSignature(builderType, "append", "(J)Ljava/lang/StringBuilder;")
    protected val hostCallsType = VirtualType.of("Lorg/cf/smalivm/core/DirectHostCallsFixture;")

    /** The production backends, authorized for the host fixture classes and the JDK calls the guest fixtures make. */
    protected val hostVm = VmServices(
        CoreTester.classIndex,
        ReflectionPolicy(
            exactSafeMethods = setOf(
                appendLong,
                MethodSignature(VirtualType.of("Ljava/util/ArrayList;"), "add", "(Ljava/lang/Object;)Z"),
                MethodSignature(VirtualType.of("Ljava/util/ArrayList;"), "get", "(I)Ljava/lang/Object;"),
            ),
            safeClasses = setOf(hostCallsType, VirtualType.of("Lorg/cf/smalivm/core/ReflectBackendFixture;")),
        ),
    )

    protected fun prepared(parameterTypes: List<VirtualType>, arguments: List<Value>) = PreparedInvocation(
        MethodSignature(VirtualType.of("Lcalls/callee;"), "host", "()V"),
        VirtualType.of("Lcalls/caller;"),
        InvokeKind.STATIC,
        VirtualType.of("V"),
        parameterTypes,
        null,
        arguments,
    )

    protected fun executeHostFixture(
        descriptor: String,
        raws: List<Any>,
        types: List<VirtualType>,
    ): Value? {
        val classIndex = CoreTester.classIndex
        val vm = VmServices(classIndex)
        val view = classIndex.requireMethodView("Linvoke_backend_test;", descriptor)
        val frame = Frame.entryFrame(view)
        val admitted = frame.heap.admitHostSeeds(raws.zip(types) { raw, type -> HostSeed(Value.Known(raw, type)) })
        view.parameters.zip(admitted).forEach { (parameter, value) -> frame.assign(parameter.register, value) }
        val graph = vm.newSession(view, entryFrame = frame).run()
        val outcome = graph.outcomes.single()
        check(outcome is Outcome.Returned) { "$outcome: ${graph.addresses().flatMap(graph::nodePile).map { it.invocationDiagnostic }}" }
        return outcome.value
    }

    protected fun view(className: String, descriptor: String): MethodView =
        CoreTester.classIndex.requireMethodView(className, descriptor)

    protected fun hostView(descriptor: String): MethodView = view("Ldirect_host_calls;", descriptor)

    protected fun identityView(): MethodView = hostView("identityObject(Ljava/lang/Object;)Ljava/lang/Object;")

    /** Seeds one host object; null [backends] runs the production backends under [hostVm]'s policy. */
    protected fun seededHostSession(
        className: String,
        descriptor: String,
        backends: List<Backend>?,
        type: VirtualType,
        raw: Any,
        seed: (Frame, HeapRef) -> Unit,
    ): Pair<ExecutionSession, HeapRef> {
        val view = view(className, descriptor)
        val frame = Frame.entryFrame(view)
        val ref = frame.heap.allocateHostObject(type, raw)
        seed(frame, ref)
        return session(view, hostVm, frame, backends) to ref
    }

    /** A session whose invocations go only to [backends]: fault injection no production backend produces. */
    protected fun session(
        view: MethodView,
        vm: VmServices,
        frame: Frame,
        backends: List<Backend>?,
    ): ExecutionSession = vm.newSession(view, Limits(), frame, backends, decode = OpBuilder::build)
}

internal class DirectBackend(
    override val name: String = "direct",
    private val probeBlock: (BackendInvocation) -> BackendProbe = { BackendProbe.Execute(SideEffectLevel.NONE) },
    private val executeBlock: (SelectedHostCall) -> SynchronousCallResult,
) : SynchronousBackend {
    override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe = probeBlock(call)

    override fun execute(call: SelectedHostCall): SynchronousCallResult = executeBlock(call)
}

internal interface MutableHostNumber {
    val note: StringBuilder
}

internal class MutableHostBigInteger(value: String) : BigInteger(value), MutableHostNumber {
    override val note = StringBuilder("seed")
    override fun toByte(): Byte = toInt().toByte()
    override fun toShort(): Short = toInt().toShort()
}

internal class MutableHostBigDecimal(value: String) : BigDecimal(value), MutableHostNumber {
    override val note = StringBuilder("seed")
    override fun toByte(): Byte = toInt().toByte()
    override fun toShort(): Short = toInt().toShort()
}

internal enum class MutableHostEnum {
    VALUE {
        override var note = StringBuilder("seed")
    };

    abstract var note: StringBuilder
}

internal class MutableBox(var value: Int)
internal class MutableParent(var child: MutableChild?)
internal class MutableChild(var value: Int)
internal class MirrorHolder(var array: IntArray, val extras: Array<MutableBox>)
internal class HostChildHolder(var child: Any?)

/** Host methods the guest fixture `Ldirect_host_calls;` calls through the production ReflectBackend. */
object DirectHostCallsFixture {
    var received: Any? = null
    var stored: Any? = null
    val target = IllegalStateException("target")

    @JvmStatic fun recordReceived(value: Any) { received = value }
    @JvmStatic fun ignore(@Suppress("UNUSED_PARAMETER") value: Any) {}

    @JvmStatic fun detachChild(parent: Any): Any {
        val owner = parent as MutableParent
        val child = requireNotNull(owner.child)
        owner.child = null
        return child
    }

    @JvmStatic fun detachHolderChild(holder: Any): Any {
        val owner = holder as HostChildHolder
        val child = requireNotNull(owner.child)
        owner.child = null
        return child
    }

    @JvmStatic fun throwHolderChild(holder: Any) { throw detachHolderChild(holder) as Throwable }
    @JvmStatic fun holderChild(holder: Any): Any = requireNotNull((holder as HostChildHolder).child)
    @JvmStatic fun appendChanged(builder: StringBuilder) { builder.append("-changed") }
    @JvmStatic fun returnStored(): Any = requireNotNull(stored)
    @JvmStatic fun firstElement(list: Any): Any = requireNotNull((list as List<*>).single())
    @JvmStatic fun newBuilder(): Any = StringBuilder("new")

    @JvmStatic fun appendThenThrow(builder: StringBuilder) {
        builder.append("-before-throw")
        throw target
    }

    @JvmStatic fun throwAndRecord(throwable: Throwable) {
        received = throwable
        throw throwable
    }

    @JvmStatic fun setFirstThenThrow(array: IntArray) {
        array[0] = 7
        throw IllegalStateException("target")
    }

    @JvmStatic fun newIntArray(): Any = intArrayOf(2, 3)

    @JvmStatic fun storeNewIntArray(outer: Array<Any?>) { outer[0] = intArrayOf(6) }
}

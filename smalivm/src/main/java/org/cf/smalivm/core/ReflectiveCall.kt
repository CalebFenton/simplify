package org.cf.smalivm.core

import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * What one `Method.invoke` of an application method does on one path. libcore's `Method.invoke` is native, so
 * every rule is ART `reflection.cc` `InvokeMethod`'s, in its order: initialize the declaring class, check the
 * receiver, select the implementation, check the argument count, check access, unbox the arguments, then call.
 * The declaring class's initialization ([classInitTarget]) precedes the outcome and is not wrapped.
 */
internal sealed interface ReflectiveCall {
    /** The class to initialize before the outcome, or null when the outcome needs no initialization. */
    val classInitTarget: VirtualType?

    /** `Method.invoke` itself throws [recipe]; it is not an `InvocationTargetException`. */
    class Throw(override val classInitTarget: VirtualType?, val recipe: ThrowableRecipe) : ReflectiveCall

    /** The selected target runs as a nested guest call whose exits [reflectiveReturn] converts. */
    class Call(
        override val classInitTarget: VirtualType?,
        val preparation: InvocationPreparation.Ready,
        val plan: InvocationPlan.RunGuest,
        val reflectiveReturn: ReflectiveReturn,
    ) : ReflectiveCall
}

/**
 * How a reflectively called target's exits reach the caller of `Method.invoke` (ART `InvokeMethodImpl` and
 * `BoxPrimitive`): a primitive result is boxed as `valueOf` boxes it, a void target returns null, and every
 * throwable the target throws leaves as the cause of a new `InvocationTargetException`.
 */
internal class ReflectiveReturn(private val returnType: VirtualType) {
    fun result(returned: FrameValue?): FrameValue = when {
        returnType.isVoid -> FrameValue.plain(Value.Known(null, VirtualType.OBJECT))
        returnType.isPrimitive ->
            FrameValue.plain(reflectionBoxed(returned?.value ?: Value.Unknown(returnType), returnType))
        else -> returned ?: FrameValue.plain(Value.Unknown(returnType))
    }

    /** Each thrown exit wrapped; a host failure while wrapping aborts only that exit. */
    fun wrapThrows(exits: List<ActivationExit.Threw>, hierarchy: ThrowableHierarchy): List<ActivationExit> =
        exits.map { exit ->
            hostBoundary<ActivationExit>({ ActivationExit.Aborted(exit.frame, hostFailureOutcome(it)) }) {
                val frame = exit.frame.copy()
                val thrown = GuestThrowables.invocationTarget(exit.subject).materialize(frame, hierarchy)
                ActivationExit.Threw(ThrowPath.create(frame, thrown, hierarchy))
            }
        }
}

/**
 * The reflective call [methodInvoke] makes when it is `Method.invoke` of a known application [Method], or null when
 * this path cannot decide it: the call is not `Method.invoke`, the Method or its target is unknown or not an
 * application method, or a receiver, argument array, or argument the outcome depends on is unknown. Null keeps the
 * call on the ordinary backend path. Pure: it reads [frame] and probes backends without changing state.
 */
internal fun Invoker.reflectiveCall(methodInvoke: PreparedInvocation, frame: Frame): ReflectiveCall? {
    if (methodInvoke.target != METHOD_INVOKE) return null
    val hierarchy = resolver.hierarchy
    val methodRef = (methodInvoke.receiver as? Value.Known)?.raw as? HeapRef ?: return null
    val method = frame.heap.hostObjectAt(methodRef) as? Method ?: return null
    val reflected = method.reflectedTargetOrNull()?.method ?: return null
    val declaring = reflected.definingClass
    if (!hierarchy.contains(declaring.descriptor) || hierarchy.isFrameworkClass(declaring)) return null
    @Suppress("DEPRECATION")
    val accessible = hostBoundary({ return null }) { method.isAccessible }
    val modifiers = method.modifiers
    val isStatic = Modifier.isStatic(modifiers)
    val callerClass = methodInvoke.callerClass

    val receiverFrameValue = methodInvoke.argumentFrameValues[0]
    val receiverType = if (isStatic) {
        null
    } else {
        val receiver = receiverFrameValue.value
        if (receiver.readsAsNullReference) return ReflectiveCall.Throw(declaring, GuestThrowables.nullReflectiveReceiver())
        val type = knownRuntimeClass(receiver, frame) ?: return null
        when (isSubtype(type, declaring)) {
            true -> type
            false -> return ReflectiveCall.Throw(declaring, GuestThrowables.invalidReflectiveReceiver(declaring, type))
            null -> return null
        }
    }
    val classInit = if (isStatic) declaring else null

    val array = methodInvoke.arguments[1]
    val elements = if (array.readsAsNullReference) {
        emptyList()
    } else {
        val ref = (array as? Value.Known)?.raw as? HeapRef ?: return null
        (frame.heapObject(ref) as? HeapObject.TrackedArray)?.elements() ?: return null
    }
    val parameters = reflected.parameterTypes
    if (elements.size != parameters.size) {
        return ReflectiveCall.Throw(classInit, GuestThrowables.reflectiveArgumentCount(parameters.size, elements.size))
    }

    // Arguments unbox in order, so the first that fails decides, and an unknown one before it leaves the call open.
    val unboxed = ArrayList<FrameValue>(parameters.size)
    var mismatch: Unboxed.Mismatch? = null
    for ((index, parameter) in parameters.withIndex()) {
        when (val result = unbox(elements[index], parameter, frame)) {
            is Unboxed.Passed -> unboxed += FrameValue.plain(result.value)
            is Unboxed.Mismatch -> {
                mismatch = result
                break
            }
            Unboxed.Unprovable -> return null
        }
    }

    val kind = when {
        isStatic -> InvokeKind.STATIC
        Modifier.isPrivate(modifiers) -> InvokeKind.DIRECT
        hierarchy.isInterface(declaring) -> InvokeKind.INTERFACE
        else -> InvokeKind.VIRTUAL
    }
    val receiverValue = if (isStatic) null else receiverFrameValue
    val preparation = prepareReflective(reflected, kind, callerClass, receiverType, receiverValue, unboxed)
        as? InvocationPreparation.Ready ?: return null
    val selected = (preparation.lookup as? MethodLookup.Runnable)?.view ?: return null
    if (hierarchy.isFrameworkClass(selected.definingClass)) return null

    val accessFlags = selected.methodAccessFlags
    if (!accessible) {
        when (verifyAccess(receiverType, declaring, accessFlags, callerClass)) {
            true -> Unit
            false -> return ReflectiveCall.Throw(
                classInit,
                GuestThrowables.reflectiveIllegalAccess(callerClass, accessFlags, selected.signature),
            )
            null -> return null
        }
    }
    mismatch?.let {
        val index = unboxed.size
        return ReflectiveCall.Throw(
            classInit,
            GuestThrowables.reflectiveArgument(selected.signature, index, parameters[index], it.actual),
        )
    }
    val plan = plan(preparation, frame) as? InvocationPlan.RunGuest ?: return null
    return ReflectiveCall.Call(classInit, preparation, plan, ReflectiveReturn(selected.signature.returnType))
}

private val METHOD_INVOKE = MethodSignature(
    VirtualType.of("Ljava/lang/reflect/Method;"),
    "invoke",
    "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;",
)

/** One argument after `BuildArgArrayFromObjectArray`'s check. */
private sealed interface Unboxed {
    /** The argument passes as [value]. */
    class Passed(val value: Value) : Unboxed

    /** The argument, of class [actual] or null for a null argument, cannot be passed. */
    class Mismatch(val actual: VirtualType?) : Unboxed

    /** Whether the argument passes depends on what this path does not know. */
    data object Unprovable : Unboxed
}

/**
 * ART `BuildArgArrayFromObjectArray` for one [element] and [parameter]: a reference parameter takes null or an
 * instance; a primitive parameter takes a non-null wrapper of its own type or of a type that widens to it.
 */
private fun Invoker.unbox(element: Value, parameter: VirtualType, frame: Frame): Unboxed {
    if (element.readsAsNullReference) {
        return if (parameter.isReference) Unboxed.Passed(Value.Known(null, parameter)) else Unboxed.Mismatch(null)
    }
    // Every value is assignable to Object, whatever this path knows about it.
    if (element !is Value.Known) return if (parameter == VirtualType.OBJECT) Unboxed.Passed(element) else Unboxed.Unprovable
    val actual = knownRuntimeClass(element, frame) ?: return Unboxed.Unprovable
    if (parameter.isReference) {
        return when (isSubtype(actual, parameter)) {
            true -> Unboxed.Passed(element)
            false -> Unboxed.Mismatch(actual)
            null -> Unboxed.Unprovable
        }
    }
    val widened = widenBoxed(element.raw, parameter.descriptor) ?: return Unboxed.Mismatch(actual)
    return Unboxed.Passed(Value.Known(widened, parameter))
}

/** [raw]'s value as a [descriptor] primitive when its wrapper class is that primitive's or widens to it. */
private fun widenBoxed(raw: Any?, descriptor: String): Any? {
    val source = when (raw) {
        is Boolean -> 'Z'
        is Byte -> 'B'
        is Char -> 'C'
        is Short -> 'S'
        is Int -> 'I'
        is Long -> 'J'
        is Float -> 'F'
        is Double -> 'D'
        else -> return null
    }
    if (source !in WIDENS_FROM.getValue(descriptor)) return null
    // Boolean and char accept only their own wrapper, so only the numeric kinds convert.
    val number = when (raw) {
        is Char -> raw.code
        is Number -> raw
        else -> return raw
    }
    return when (descriptor) {
        "B" -> number.toByte()
        "S" -> number.toShort()
        "I" -> number.toInt()
        "J" -> number.toLong()
        "F" -> number.toFloat()
        "D" -> number.toDouble()
        else -> raw
    }
}

/** The wrapper primitives each parameter primitive accepts, as ART's `DO_FIRST_ARG`/`DO_ARG` lists them. */
private val WIDENS_FROM: Map<String, String> = mapOf(
    "Z" to "Z",
    "B" to "B",
    "C" to "C",
    "S" to "SB",
    "I" to "ICSB",
    "J" to "JICSB",
    "F" to "FJICSB",
    "D" to "DFJICSB",
)

/**
 * ART `VerifyAccess` for a Method that is not accessible: public, the declaring class itself, a non-private
 * member of the caller's package, or a protected member of a superclass of the caller whose receiver, if any, is
 * an instance of the caller. Null when a class relation it needs is unknown.
 */
private fun Invoker.verifyAccess(
    receiverType: VirtualType?,
    declaring: VirtualType,
    accessFlags: Int,
    caller: VirtualType,
): Boolean? {
    if (Modifier.isPublic(accessFlags) || caller == declaring) return true
    if (Modifier.isPrivate(accessFlags)) return false
    val samePackage = samePackage(declaring, caller)
    if (Modifier.isProtected(accessFlags)) {
        val receiverIsCaller = receiverType?.let { isSubtype(it, caller) ?: return null }
        if (receiverIsCaller == false && !samePackage) return false
        if (isSubtype(caller, declaring) ?: return null) return true
    }
    return samePackage
}

/** ART `Class::IsInSamePackage`: one class loader and one package name. */
private fun Invoker.samePackage(first: VirtualType, second: VirtualType): Boolean =
    resolver.hierarchy.isFrameworkClass(first) == resolver.hierarchy.isFrameworkClass(second) &&
        first.descriptor.substringBeforeLast('/', "") == second.descriptor.substringBeforeLast('/', "")

/** Whether [candidate] is [ancestor] or a subtype of it, or null when the hierarchy cannot say. */
private fun Invoker.isSubtype(candidate: VirtualType, ancestor: VirtualType): Boolean? = try {
    resolver.hierarchy.isSubtypeOf(candidate, ancestor)
} catch (_: ClassLoadFailure) {
    null
}

/** The runtime class of the non-null [value] on [frame]'s path, or null when the path does not know it. */
internal fun knownRuntimeClass(value: Value, frame: Frame): VirtualType? {
    val raw = (value as? Value.Known)?.raw ?: return null
    return when {
        raw is HeapRef -> frame.heapObject(raw)?.type
        raw.isDirectHostImmutable() -> virtualTypeOfHostClass(raw.javaClass)
        else -> null
    }
}

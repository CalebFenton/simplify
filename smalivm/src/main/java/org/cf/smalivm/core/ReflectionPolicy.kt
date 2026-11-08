package org.cf.smalivm.core

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

/** The policy rule that made one reflection decision. */
internal enum class ReflectionRule { EXACT_UNSAFE, EXACT_SAFE, SAFE_CLASS, UNSAFE_RECEIVER, DEFAULT_DENY }

/** A policy decision. Denial is not a backend decline and does not permit fallback. */
internal sealed interface ReflectionDecision {
    /** The rule that decided. */
    val rule: ReflectionRule

    /** The call may run reflectively. */
    data class Allowed(override val rule: ReflectionRule) : ReflectionDecision

    /** The call must not run reflectively. */
    data class Denied(override val rule: ReflectionRule) : ReflectionDecision
}

/** Immutable reflection authorization with explicit priority. */
public class ReflectionPolicy(
    exactUnsafeMethods: Set<MethodSignature> = emptySet(),
    exactSafeMethods: Set<MethodSignature> = emptySet(),
    safeClasses: Set<VirtualType> = emptySet(),
    exactSafeStaticFields: Set<FieldSignature> = emptySet(),
) {
    private val exactUnsafeMethods = exactUnsafeMethods.toSet()
    private val exactSafeMethods = exactSafeMethods.toSet()
    private val safeClasses = safeClasses.toSet()
    private val exactSafeStaticFields = exactSafeStaticFields.toSet()

    /**
     * Decides one call of [target]. [receiverClass] is the runtime class of a tracked receiver object. A class-wide
     * authorization covers a receiver only of a listed class: a safe class's inherited method can run on a subclass
     * whose own methods keep state the policy never vetted, as `AbstractList.clear` does on a `subList` view.
     */
    internal fun decide(
        target: MethodSignature,
        hostInitializationChain: List<VirtualType> = emptyList(),
        receiverClass: VirtualType? = null,
    ): ReflectionDecision = when {
        target in exactUnsafeMethods -> ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        hostInitializationChain.any { MethodSignature.classInitializer(it) in exactUnsafeMethods } ->
            ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        target in exactSafeMethods -> ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE)
        target.definingClass in safeClasses ->
            if (receiverClass == null || receiverClass in safeClasses) {
                ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS)
            } else {
                ReflectionDecision.Denied(ReflectionRule.UNSAFE_RECEIVER)
            }
        else -> ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY)
    }

    /** Authorizes one static read without exposing policy collections or loading a host class. */
    internal fun decideStaticRead(
        field: FieldSignature,
        hostInitializationChain: List<VirtualType>,
    ): ReflectionDecision = when {
        hostInitializationChain.any { MethodSignature.classInitializer(it) in exactUnsafeMethods } ->
            ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        field in exactSafeStaticFields -> ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE)
        field.definingClass in safeClasses -> ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS)
        else -> ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY)
    }

    /** Authorizes the exact host field represented by an immutable tracked value. */
    internal fun decideFieldRead(
        field: TrackedFieldValue,
        hostInitializationChain: List<VirtualType>,
    ): ReflectionDecision = when {
        hostInitializationChain.any { MethodSignature.classInitializer(it) in exactUnsafeMethods } ->
            ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        field.storage == FieldStorage.STATIC && FieldSignature(
            field.declaringType,
            field.name,
            field.fieldType,
        ) in exactSafeStaticFields -> ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE)
        field.declaringType in safeClasses -> ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS)
        else -> ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY)
    }

    public companion object {
        /**
         * Loads fresh immutable policy data for one VM owner. The policy treats the host default charset as the
         * device's UTF-8, so a JVM started with another `file.encoding` is refused.
         */
        public fun defaults(classLoader: ClassLoader = ReflectionPolicy::class.java.classLoader): ReflectionPolicy {
            require(Charset.defaultCharset() == StandardCharsets.UTF_8) {
                "Simplify requires the JVM default charset to be UTF-8, as on Android; it is ${Charset.defaultCharset()}"
            }
            return ReflectionPolicy(
                exactUnsafeMethods = loadDefinitions(classLoader, "unsafe_methods.cfg").map(MethodSignature::parse).toSet(),
                exactSafeMethods = loadDefinitions(classLoader, "safe_methods.cfg").map(MethodSignature::parse).toSet(),
                safeClasses = loadDefinitions(classLoader, "safe_classes.cfg").map(VirtualType::of).toSet(),
            )
        }

        internal fun loadDefinitions(classLoader: ClassLoader, resource: String): List<String> {
            val stream = requireNotNull(classLoader.getResourceAsStream(resource)) {
                "Missing reflection policy resource $resource"
            }
            return BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).useLines { lines ->
                lines.map(String::trim)
                    .filter { it.isNotEmpty() && !it.startsWith("//") }
                    .toList()
            }
        }
    }
}

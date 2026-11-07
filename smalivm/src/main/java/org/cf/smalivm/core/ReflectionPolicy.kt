package org.cf.smalivm.core

import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/** The policy rule that made one reflection decision. */
enum class ReflectionRule { EXACT_UNSAFE, EXACT_SAFE, SAFE_CLASS, DEFAULT_DENY }

/** A policy decision. Denial is not a backend decline and does not permit fallback. */
sealed interface ReflectionDecision {
    val rule: ReflectionRule

    data class Allowed(override val rule: ReflectionRule) : ReflectionDecision

    data class Denied(override val rule: ReflectionRule) : ReflectionDecision
}

/** Immutable reflection authorization with explicit priority. */
class ReflectionPolicy(
    exactUnsafeMethods: Set<MethodDescriptor> = emptySet(),
    exactSafeMethods: Set<MethodDescriptor> = emptySet(),
    safeClasses: Set<VirtualType> = emptySet(),
    methodEffects: Map<MethodDescriptor, SideEffectLevel> = emptyMap(),
    exactSafeStaticFields: Set<StaticFieldDescriptor> = emptySet(),
) {
    private val exactUnsafeMethods = exactUnsafeMethods.toSet()
    private val exactSafeMethods = exactSafeMethods.toSet()
    private val safeClasses = safeClasses.toSet()
    private val methodEffects = methodEffects.toMap()
    private val exactSafeStaticFields = exactSafeStaticFields.toSet()

    fun decide(
        target: MethodDescriptor,
        hostInitializationChain: List<VirtualType> = emptyList(),
    ): ReflectionDecision = when {
        target in exactUnsafeMethods -> ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        hostInitializationChain.any { classInitializer(it) in exactUnsafeMethods } ->
            ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        target in exactSafeMethods -> ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE)
        target.definingClass in safeClasses -> ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS)
        else -> ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY)
    }

    /** Authorization does not imply purity. Only an explicit proof narrows the default strong effect. */
    fun effectOf(target: MethodDescriptor): SideEffectLevel = methodEffects[target] ?: SideEffectLevel.STRONG

    /** Authorizes one static read without exposing policy collections or loading a host class. */
    fun decideStaticRead(
        field: StaticFieldDescriptor,
        hostInitializationChain: List<VirtualType>,
    ): ReflectionDecision = when {
        hostInitializationChain.any { classInitializer(it) in exactUnsafeMethods } ->
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
        hostInitializationChain.any { classInitializer(it) in exactUnsafeMethods } ->
            ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        field.storage == FieldStorage.STATIC && StaticFieldDescriptor(
            field.declaringType,
            field.name,
            field.fieldType,
        ) in exactSafeStaticFields -> ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE)
        field.declaringType in safeClasses -> ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS)
        else -> ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY)
    }

    companion object {
        /** Loads fresh immutable policy data for one VM owner. */
        fun defaults(classLoader: ClassLoader = ReflectionPolicy::class.java.classLoader): ReflectionPolicy {
            val unsafeClasses = loadDefinitions(classLoader, "unsafe_classes.cfg").map(VirtualType::of).toSet()
            return ReflectionPolicy(
                exactUnsafeMethods = loadDefinitions(classLoader, "unsafe_methods.cfg").map(::parseMethod).toSet(),
                exactSafeMethods = loadDefinitions(classLoader, "safe_methods.cfg").map(::parseMethod).toSet(),
                safeClasses = loadDefinitions(classLoader, "safe_classes.cfg")
                    .map(VirtualType::of)
                    .filterNot(unsafeClasses::contains)
                    .toSet(),
                methodEffects = loadDefinitions(classLoader, "method_effects.cfg").associate(::parseEffect),
            )
        }

        private fun loadDefinitions(classLoader: ClassLoader, resource: String): List<String> {
            val stream = requireNotNull(classLoader.getResourceAsStream(resource)) {
                "Missing reflection policy resource $resource"
            }
            return BufferedReader(InputStreamReader(stream, StandardCharsets.UTF_8)).useLines { lines ->
                lines.map(String::trim)
                    .filter { it.isNotEmpty() && !it.startsWith("//") }
                    .toList()
            }
        }

        internal fun parseMethod(signature: String): MethodDescriptor {
            val separator = signature.indexOf("->")
            require(separator > 0) { "Malformed reflection method policy entry: $signature" }
            val member = signature.substring(separator + 2)
            val protoStart = member.indexOf('(')
            require(protoStart > 0) { "Malformed reflection method policy entry: $signature" }
            return MethodDescriptor(
                VirtualType.of(signature.substring(0, separator)),
                member.substring(0, protoStart),
                member.substring(protoStart),
            )
        }

        private fun parseEffect(definition: String): Pair<MethodDescriptor, SideEffectLevel> {
            val separator = definition.lastIndexOf(' ')
            require(separator > 0) { "Malformed reflection method effect entry: $definition" }
            val method = parseMethod(definition.substring(0, separator))
            val effect = try {
                SideEffectLevel.valueOf(definition.substring(separator + 1))
            } catch (failure: IllegalArgumentException) {
                throw IllegalArgumentException("Malformed reflection method effect entry: $definition", failure)
            }
            return method to effect
        }

        private fun classInitializer(type: VirtualType) = MethodDescriptor(type, "<clinit>", "()V")
    }
}

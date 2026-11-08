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
    exactUnsafeMethods: Set<MethodSignature> = emptySet(),
    exactSafeMethods: Set<MethodSignature> = emptySet(),
    safeClasses: Set<VirtualType> = emptySet(),
    methodEffects: Map<MethodSignature, SideEffectLevel> = emptyMap(),
    exactSafeStaticFields: Set<FieldSignature> = emptySet(),
) {
    private val exactUnsafeMethods = exactUnsafeMethods.toSet()
    private val exactSafeMethods = exactSafeMethods.toSet()
    private val safeClasses = safeClasses.toSet()
    private val methodEffects = methodEffects.toMap()
    private val exactSafeStaticFields = exactSafeStaticFields.toSet()

    fun decide(
        target: MethodSignature,
        hostInitializationChain: List<VirtualType> = emptyList(),
    ): ReflectionDecision = when {
        target in exactUnsafeMethods -> ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        hostInitializationChain.any { MethodSignature.classInitializer(it) in exactUnsafeMethods } ->
            ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE)
        target in exactSafeMethods -> ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE)
        target.definingClass in safeClasses -> ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS)
        else -> ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY)
    }

    /** Authorization does not imply purity. Only an explicit proof narrows the default strong effect. */
    fun effectOf(target: MethodSignature): SideEffectLevel = methodEffects[target] ?: SideEffectLevel.STRONG

    /** Authorizes one static read without exposing policy collections or loading a host class. */
    fun decideStaticRead(
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

    companion object {
        /** Loads fresh immutable policy data for one VM owner. */
        fun defaults(classLoader: ClassLoader = ReflectionPolicy::class.java.classLoader): ReflectionPolicy =
            ReflectionPolicy(
                exactUnsafeMethods = loadDefinitions(classLoader, "unsafe_methods.cfg").map(MethodSignature::parse).toSet(),
                exactSafeMethods = loadDefinitions(classLoader, "safe_methods.cfg").map(MethodSignature::parse).toSet(),
                safeClasses = loadDefinitions(classLoader, "safe_classes.cfg").map(VirtualType::of).toSet(),
                methodEffects = loadDefinitions(classLoader, "method_effects.cfg").associate(::parseEffect),
            )

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

        private fun parseEffect(definition: String): Pair<MethodSignature, SideEffectLevel> {
            val separator = definition.lastIndexOf(' ')
            require(separator > 0) { "Malformed reflection method effect entry: $definition" }
            val method = MethodSignature.parse(definition.substring(0, separator))
            val effect = try {
                SideEffectLevel.valueOf(definition.substring(separator + 1))
            } catch (failure: IllegalArgumentException) {
                throw IllegalArgumentException("Malformed reflection method effect entry: $definition", failure)
            }
            return method to effect
        }
    }
}

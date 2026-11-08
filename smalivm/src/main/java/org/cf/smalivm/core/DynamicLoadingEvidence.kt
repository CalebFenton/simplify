package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/**
 * Method signatures containing operations that can extend the loaded type world.
 *
 * Each application method definition stores its own body-derived value. A program snapshot aggregates those
 * stored values; publication never rescans an unrelated method body.
 */
class DynamicLoadingEvidence private constructor(
    val methodSignatures: PersistentSet<String>,
) {
    val isPresent: Boolean get() = methodSignatures.isNotEmpty()

    internal fun plus(other: DynamicLoadingEvidence): DynamicLoadingEvidence =
        DynamicLoadingEvidence(methodSignatures.addingAll(other.methodSignatures))

    internal companion object {
        val EMPTY = DynamicLoadingEvidence(persistentSetOf())

        fun of(methodSignature: String): DynamicLoadingEvidence =
            DynamicLoadingEvidence(persistentSetOf(methodSignature))

        fun ofAll(methodSignatures: Collection<String>): DynamicLoadingEvidence =
            DynamicLoadingEvidence(methodSignatures.toPersistentSet())

        fun fromBody(
            methodSignature: MethodSignature,
            body: MethodBody,
            hierarchy: HierarchySnapshot? = null,
        ): DynamicLoadingEvidence {
            val present = body.instructions.any { record ->
                val reference = (record.instruction as? ReferenceInstruction)?.reference as? MethodReference
                reference != null && when (reference.definingClass) {
                    "Ljava/lang/Class;" -> reference.name == "forName"
                    "Ljava/lang/ClassLoader;" -> reference.name == "loadClass"
                    "Ldalvik/system/DexClassLoader;", "Ldalvik/system/PathClassLoader;" -> true
                    else -> reference.name == "loadClass" && hierarchy?.isClassLoaderSubtype(reference.definingClass) == true
                }
            }
            return if (present) of(methodSignature.toString()) else EMPTY
        }

        private fun HierarchySnapshot.isClassLoaderSubtype(descriptor: String): Boolean = try {
            isSubtypeOf(VirtualType.of(descriptor), VirtualType.of("Ljava/lang/ClassLoader;"))
        } catch (_: ClassLoadFailure) {
            true
        }
    }
}

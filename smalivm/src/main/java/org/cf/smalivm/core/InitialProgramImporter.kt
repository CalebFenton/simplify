package org.cf.smalivm.core

import kotlinx.collections.immutable.toPersistentMap
import org.cf.smalivm.type.ClassManager
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.ImmutableAnnotation
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableField

/** Constructs the first complete snapshot from application input and the bundled framework artifact. */
internal object InitialProgramImporter {
    fun import(classManager: ClassManager): ProgramWorkspace {
        val revisionAuthority = ProgramRevisionAuthority()
        val programIdentity = revisionAuthority.programIdentity
        val importedApplicationClasses = classManager.getApplicationClassNames().sorted().map { className ->
            val immutable = classManager.getInitialApplicationClassDefinitions()?.get(className)
                ?: ImmutableClassDef.of(classManager.getVirtualClass(className).classDef)
            val methods = immutable.methods.map { method ->
                val methodSignature = com.android.tools.smali.dexlib2.formatter.DexFormatter.INSTANCE.getMethodDescriptor(method)
                ApplicationMethodDefinition.import(
                    method,
                    revisionAuthority.newApplicationMethodRevision(methodSignature),
                )
            }
            ApplicationClassDefinition(
                immutable.type,
                immutable.accessFlags,
                immutable.superclass,
                immutable.interfaces,
                immutable.sourceFile,
                immutable.annotations.map(ImmutableAnnotation::of).toSet(),
                immutable.staticFields.map { ApplicationFieldDefinition(ImmutableField.of(it)) },
                immutable.instanceFields.map { ApplicationFieldDefinition(ImmutableField.of(it)) },
                methods,
            )
        }

        val initialClosure = referenceClosure(importedApplicationClasses)
        val initialEncoding = if (
            initialClosure.strings.size > MAX_NON_JUMBO_STRING_COUNT ||
            importedApplicationClasses.any(::containsJumboString)
        ) {
            StringInstructionEncoding.CONST_STRING_JUMBO
        } else {
            StringInstructionEncoding.CONST_STRING
        }
        val applicationClasses = importedApplicationClasses.map { definition ->
            definition.withStringEncoding(initialEncoding)
        }
        val frameworkCatalog = FrameworkCatalog.fromArtifact(FrameworkArtifact.load())
        val hierarchy = HierarchySnapshot.from(applicationClasses, frameworkCatalog.artifact)
        val applicationClassesWithEvidence = applicationClasses.map { definition ->
            if (definition.methods.isEmpty()) {
                definition
            } else {
                definition.withMethods(
                    definition.methods.associate { method ->
                        method.methodSignature to ApplicationMethodDefinition.withHierarchyEvidence(method, hierarchy)
                    },
                )
            }
        }
        val snapshot = ProgramSnapshot.initial(
            programIdentity,
            revisionAuthority.newProgramRevision(),
            applicationClassesWithEvidence.associateBy(ApplicationClassDefinition::type).toPersistentMap(),
            OutputApiPolicy(classManager.outputAPILevel),
            initialEncoding,
            hierarchy,
            frameworkCatalog,
        )
        return ProgramWorkspace.fromInitialSnapshot(snapshot, revisionAuthority)
    }

    private fun containsJumboString(definition: ApplicationClassDefinition): Boolean = definition.methods.any { method ->
        method.body?.instructions?.any { it.instruction.opcode == Opcode.CONST_STRING_JUMBO } == true
    }

    private fun referenceClosure(
        definitions: Collection<ApplicationClassDefinition>,
    ): DexReferenceClosure = definitions.fold(DexReferenceClosure.EMPTY) { closure, definition ->
        closure.plus(DexReferenceClosure.fromApplicationClass(definition))
    }

    private const val MAX_NON_JUMBO_STRING_COUNT = 65_536
}

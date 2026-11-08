package org.cf.smalivm.core

import kotlinx.collections.immutable.toPersistentMap
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.immutable.ImmutableAnnotation
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableField
import org.slf4j.LoggerFactory

/** Constructs the first complete snapshot from application input and the bundled framework artifact. */
internal object InitialProgramImporter {
    private val log = LoggerFactory.getLogger(InitialProgramImporter::class.java)

    fun import(input: ProgramInput): ProgramWorkspace {
        val revisionAuthority = ProgramRevisionAuthority()
        val programIdentity = revisionAuthority.programIdentity
        val frameworkCatalog = FrameworkCatalog.fromArtifact(FrameworkArtifact.load())
        val importedApplicationClasses = definedApplicationClasses(input.classes, frameworkCatalog.artifact).map { immutable ->
            val methods = immutable.methods.map { method ->
                ApplicationMethodDefinition.import(
                    method,
                    revisionAuthority.newApplicationMethodRevision(MethodSignature.of(method)),
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
        val linkableClasses = applicationClasses.filter(::passesVerification)
        val hierarchy = HierarchySnapshot.from(linkableClasses, frameworkCatalog.artifact)
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
            OutputApiPolicy(input.outputApiLevel),
            initialEncoding,
            hierarchy,
            frameworkCatalog,
        )
        return ProgramWorkspace.fromInitialSnapshot(snapshot, revisionAuthority)
    }

    /**
     * The application classes an Android runtime defines from [classes], sorted by type. The application loader
     * delegates to the boot class path first, so an application class that names a framework class is never
     * defined; among application classes of one name, the first in classpath order is defined.
     */
    private fun definedApplicationClasses(
        classes: List<ImmutableClassDef>,
        framework: FrameworkArtifact,
    ): List<ImmutableClassDef> {
        val defined = HashMap<String, ImmutableClassDef>()
        for (definition in classes) {
            when {
                definition.type in framework.classes ->
                    log.warn("Ignoring application class {}: the framework defines it", definition.type)
                defined.putIfAbsent(definition.type, definition) != null ->
                    log.warn("Ignoring duplicate application class {}: an earlier definition wins", definition.type)
            }
        }
        return defined.values.sortedBy(ImmutableClassDef::getType)
    }

    /**
     * Whether ART's verifier accepts every method of [definition]. A class it rejects stays in the program, so
     * output keeps its code, and is left out of the loaded set. ART throws `VerifyError` on the class's first
     * use and `NoClassDefFoundError` after; the engine answers every use with `NoClassDefFoundError`, including
     * `const-class`, `instance-of` and `check-cast`, which resolve the class on ART without verifying it.
     */
    private fun passesVerification(definition: ApplicationClassDefinition): Boolean {
        val rejected = definition.methods.firstOrNull { it.rejectedCode != null } ?: return true
        val rejection = requireNotNull(rejected.rejectedCode).rejection
        log.warn(
            "Class {} fails verification: {} at code address {} of {}",
            definition.type,
            rejection.reason,
            rejection.codeAddress,
            rejected.methodSignature,
        )
        return false
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

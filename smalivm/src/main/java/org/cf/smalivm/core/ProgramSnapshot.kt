package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentMap

/** Complete immutable execution and output state for one program revision. */
class ProgramSnapshot private constructor(
    internal val programIdentity: ProgramIdentity,
    val programRevision: ProgramRevision,
    val applicationClasses: PersistentMap<String, ApplicationClassDefinition>,
    val outputApiPolicy: OutputApiPolicy,
    val stringInstructionEncoding: StringInstructionEncoding,
    internal val hierarchy: HierarchySnapshot,
    val dynamicLoadingEvidence: DynamicLoadingEvidence,
    internal val dexReferenceClosure: DexReferenceClosure,
    val frameworkCatalog: FrameworkCatalog,
    internal val predecessorRevision: ProgramRevision?,
) {
    private val concreteApplicationMethodSignatures = java.util.Collections.unmodifiableList(
        applicationClasses.values
            .flatMap { definition -> definition.methods.filter { it.body != null }.map { it.methodSignature } }
            .sorted(),
    )

    fun applicationClass(type: String): ApplicationClassDefinition? = applicationClasses[type]

    /**
     * Full signatures of application methods with a body, in deterministic lexical order. Verifier-rejected code
     * has no body.
     */
    fun concreteApplicationMethodSignatures(): List<MethodSignature> = concreteApplicationMethodSignatures

    internal fun applicationMethod(method: MethodSignature): ApplicationMethodDefinition? =
        applicationClasses[method.definingClass.descriptor]?.methodsByDescriptor?.get(method.nameAndProto)

    internal fun owns(view: MethodView): Boolean {
        if (view.programRevision !== programRevision) return false
        val methodRevision = view.methodRevision
        if (methodRevision != null) {
            return applicationMethod(view.signature)?.methodRevision === methodRevision
        }
        val frameworkIdentity = view.frameworkMethodIdentity ?: return false
        return frameworkCatalog.method(view.signature)?.identity === frameworkIdentity
    }

    internal companion object {
        fun initial(
            programIdentity: ProgramIdentity,
            programRevision: ProgramRevision,
            applicationClasses: PersistentMap<String, ApplicationClassDefinition>,
            outputApiPolicy: OutputApiPolicy,
            stringInstructionEncoding: StringInstructionEncoding,
            hierarchy: HierarchySnapshot,
            frameworkCatalog: FrameworkCatalog,
        ): ProgramSnapshot {
            require(programRevision.belongsTo(programIdentity)) { "Program revision belongs to another workspace" }
            return create(
                programIdentity,
                programRevision,
                applicationClasses,
                outputApiPolicy,
                stringInstructionEncoding,
                hierarchy,
                frameworkCatalog,
                null,
            )
        }

        fun candidate(
            current: ProgramSnapshot,
            programRevision: ProgramRevision,
            applicationClasses: PersistentMap<String, ApplicationClassDefinition>,
            stringInstructionEncoding: StringInstructionEncoding = current.stringInstructionEncoding,
        ): ProgramSnapshot {
            require(programRevision.belongsTo(current.programIdentity)) {
                "Program revision belongs to another workspace"
            }
            require(programRevision.isAfter(current.programRevision)) {
                "Candidate snapshot must have a new program revision"
            }
            require(applicationClasses.all { (type, definition) -> type == definition.type }) {
                "Application class map key does not match its class definition"
            }
            require(applicationClasses.keys == current.applicationClasses.keys) {
                "Plan 13 cannot add or remove application classes"
            }
            for ((type, definition) in applicationClasses) {
                val previous = requireNotNull(current.applicationClasses[type])
                require(definition.hasSameStructureAs(previous)) {
                    "Plan 13 cannot change application class structure: $type"
                }
                for (method in definition.methods) {
                    val previousMethod = requireNotNull(previous.methodsByDescriptor[method.methodSignature.nameAndProto])
                    require(method === previousMethod || method.methodRevision !== previousMethod.methodRevision) {
                        "Changed method must have a new application method revision: ${method.methodSignature}"
                    }
                }
            }
            return create(
                current.programIdentity,
                programRevision,
                applicationClasses,
                current.outputApiPolicy,
                stringInstructionEncoding,
                current.hierarchy,
                current.frameworkCatalog,
                current.programRevision,
            )
        }

        private fun create(
            programIdentity: ProgramIdentity,
            programRevision: ProgramRevision,
            applicationClasses: PersistentMap<String, ApplicationClassDefinition>,
            outputApiPolicy: OutputApiPolicy,
            stringInstructionEncoding: StringInstructionEncoding,
            hierarchy: HierarchySnapshot,
            frameworkCatalog: FrameworkCatalog,
            predecessorRevision: ProgramRevision?,
        ): ProgramSnapshot {
            var evidence = DynamicLoadingEvidence.EMPTY
            var closure = DexReferenceClosure.EMPTY
            for (definition in applicationClasses.values) {
                closure = closure.plus(DexReferenceClosure.fromApplicationClass(definition))
                definition.methods.forEach { method -> evidence = evidence.plus(method.dynamicLoadingEvidence) }
            }
            return ProgramSnapshot(
                programIdentity,
                programRevision,
                applicationClasses,
                outputApiPolicy,
                stringInstructionEncoding,
                hierarchy,
                evidence,
                closure,
                frameworkCatalog,
                predecessorRevision,
            )
        }
    }
}

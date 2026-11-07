package org.cf.smalivm.core

/** Expected reasons that an open method edit cannot commit. */
sealed interface MethodEditRejectionReason {
    data object StaleMethodRevision : MethodEditRejectionReason
    data object StaleProgramRevision : MethodEditRejectionReason
    data class InvalidCodeAddress(val codeAddress: Int) : MethodEditRejectionReason
    data class PayloadCodeAddress(val codeAddress: Int) : MethodEditRejectionReason
    data class ConflictingCommands(val codeAddress: Int) : MethodEditRejectionReason
    data class InvalidInstructionTemplate(val message: String) : MethodEditRejectionReason
    data class InvalidDexReference(val message: String) : MethodEditRejectionReason
    data class InvalidControlTarget(val message: String) : MethodEditRejectionReason
    data class InvalidTryRegion(val message: String) : MethodEditRejectionReason
    data class RegisterOverflow(val register: Int) : MethodEditRejectionReason
    data class CodeSizeOverflow(val codeUnits: Long) : MethodEditRejectionReason
    data class DexReferenceLimit(val section: String) : MethodEditRejectionReason
    data class StructuralValidationFailure(val message: String) : MethodEditRejectionReason
}

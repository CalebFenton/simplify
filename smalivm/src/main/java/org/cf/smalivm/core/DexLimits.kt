package org.cf.smalivm.core

/** DEX format limits that an edit must respect before an output builder exists. */
internal object DexLimits {
    /** `code_item.registers_size` is a `ushort`. */
    const val MAX_REGISTER_COUNT = 65_535

    /** Instruction operands index the type, field, method and proto sections with 16 bits. */
    const val MAX_INDEXED_ITEMS = 65_536

    /** `const-string` holds a 16-bit string index; more strings need `const-string/jumbo`. */
    const val MAX_NON_JUMBO_STRING_COUNT = 65_536

    /** `code_item.tries_size`, `try_item.insn_count` and `try_item.handler_off` are each a `ushort`. */
    const val MAX_TRY_ITEMS = 65_535
    const val MAX_TRY_RANGE_CODE_UNITS = 65_535
    const val MAX_HANDLER_OFFSET = 65_535L

    /** A switch payload's `size` is a `ushort`. */
    const val MAX_SWITCH_ELEMENTS = 65_535

    private const val MAX_UNSIGNED_INT = 0xffff_ffffL

    /** Jumbo encoding is permanent for a program: once required it stays required. */
    fun stringEncoding(stringCount: Int, current: StringInstructionEncoding): StringInstructionEncoding =
        if (current == StringInstructionEncoding.CONST_STRING_JUMBO || stringCount > MAX_NON_JUMBO_STRING_COUNT) {
            StringInstructionEncoding.CONST_STRING_JUMBO
        } else {
            StringInstructionEncoding.CONST_STRING
        }

    fun validate(
        closure: DexReferenceClosure,
        applicationClasses: Collection<ApplicationClassDefinition>,
    ): MethodEditRejectionReason.DexReferenceLimit? {
        validateIndexedSection(DexSection.TYPE_IDS, closure.types.size)?.let { return it }
        validateIndexedSection(DexSection.PROTO_IDS, closure.prototypes.size)?.let { return it }
        validateIndexedSection(DexSection.FIELD_IDS, closure.fields.size)?.let { return it }
        validateIndexedSection(DexSection.METHOD_IDS, closure.methods.size)?.let { return it }

        val typeLists = buildSet<List<String>> {
            closure.prototypes.mapTo(this) { it.parameterTypes }
            applicationClasses.mapTo(this) { it.interfaces }
        }
        validateTypeListDataSize(encodedTypeListDataSize(typeLists.map(List<String>::size)))?.let { return it }
        return null
    }

    internal fun validateIndexedSection(
        section: DexSection,
        itemCount: Int,
    ): MethodEditRejectionReason.DexReferenceLimit? =
        MethodEditRejectionReason.DexReferenceLimit(section).takeIf { itemCount > MAX_INDEXED_ITEMS }

    internal fun encodedTypeListDataSize(typeCounts: Iterable<Int>): Long {
        var bytes = 0L
        for (typeCount in typeCounts) {
            require(typeCount >= 0) { "A type-list size cannot be negative" }
            if (typeCount == 0) continue
            bytes += alignToFour(4L + 2L * typeCount)
        }
        return bytes
    }

    internal fun validateTypeListDataSize(bytes: Long): MethodEditRejectionReason.DexReferenceLimit? =
        MethodEditRejectionReason.DexReferenceLimit(DexSection.TYPE_LIST).takeIf { bytes > MAX_UNSIGNED_INT }

    private fun alignToFour(value: Long): Long = (value + 3L) and -4L
}

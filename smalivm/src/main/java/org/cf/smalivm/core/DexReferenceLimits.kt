package org.cf.smalivm.core

/** DEX section limits that an edit can project without creating an output builder. */
internal object DexReferenceLimits {
    fun validate(
        closure: DexReferenceClosure,
        applicationClasses: Collection<ApplicationClassDefinition>,
    ): MethodEditRejectionReason.DexReferenceLimit? {
        validateIndexedSection("type_ids", closure.types.size)?.let { return it }
        validateIndexedSection("proto_ids", closure.prototypes.size)?.let { return it }
        validateIndexedSection("field_ids", closure.fields.size)?.let { return it }
        validateIndexedSection("method_ids", closure.methods.size)?.let { return it }

        val typeLists = buildSet<List<String>> {
            closure.prototypes.mapTo(this) { it.parameterTypes }
            applicationClasses.mapTo(this) { it.interfaces }
        }
        validateTypeListDataSize(encodedTypeListDataSize(typeLists.map(List<String>::size)))?.let { return it }
        return null
    }

    internal fun validateIndexedSection(
        section: String,
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
        MethodEditRejectionReason.DexReferenceLimit("type_list").takeIf { bytes > MAX_UNSIGNED_INT }

    private fun alignToFour(value: Long): Long = (value + 3L) and -4L

    private const val MAX_INDEXED_ITEMS = 65_536
    private const val MAX_UNSIGNED_INT = 0xffff_ffffL
}

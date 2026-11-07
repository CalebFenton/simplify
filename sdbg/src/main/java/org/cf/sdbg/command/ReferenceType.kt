package org.cf.sdbg.command

/** The kind of Smali reference a debugger command argument looks like. */
internal enum class ReferenceType {
    INTERNAL_CLASS_DESCRIPTOR,
    INTERNAL_METHOD_DESCRIPTOR,
    INTERNAL_METHOD_SIGNATURE,
    INTERNAL_FIELD_DESCRIPTOR,
    UNKNOWN,
    ;

    companion object {
        /** `Lcls;`, `name(I)V`, `Lcls;->name(I)V`, or `Lcls;->name:I`; an array class answers for its element class. */
        fun guess(reference: String): ReferenceType {
            val s = if (reference.startsWith("[")) reference.replace("[", "").replace("]", "") else reference
            if (s.startsWith("L")) {
                if (s.contains(";->")) {
                    if (s.contains("(") && s.contains(")")) return INTERNAL_METHOD_SIGNATURE
                    if (s.contains(":")) return INTERNAL_FIELD_DESCRIPTOR
                } else if (s.endsWith(";")) {
                    return INTERNAL_CLASS_DESCRIPTOR
                }
            } else if (s.contains("(") && s.contains(")")) {
                return INTERNAL_METHOD_DESCRIPTOR
            }
            return UNKNOWN
        }
    }
}

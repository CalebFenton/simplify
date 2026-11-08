package org.cf.smalivm.core

/** Which string-load opcode a string index needs. */
internal enum class StringInstructionEncoding {
    CONST_STRING,
    CONST_STRING_JUMBO,
}

package org.cf.smalivm

enum class SideEffectLevel(
    val value: Int
) {
    NONE(0),    // reflected, emulated, white listed, or otherwise safe
    WEAK(1),    // not white listed, unknown
    STRONG(2);  // member variable modification
}

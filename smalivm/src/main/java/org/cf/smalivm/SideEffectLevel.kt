package org.cf.smalivm

enum class SideEffectLevel(  // member variable modification
    val value: Int
) {
    NONE(0),  // reflected, emulated, white listed, or otherwise safe
    WEAK(1),  // not white listed, unknown
    STRONG(2);
}

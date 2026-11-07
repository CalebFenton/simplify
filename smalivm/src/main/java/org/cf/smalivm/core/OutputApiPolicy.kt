package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcodes

/** DEX opcode/API policy used for validation and output. */
class OutputApiPolicy(val apiLevel: Int) {
    init {
        require(apiLevel >= 1) { "apiLevel must be positive: $apiLevel" }
    }

    internal fun opcodes(): Opcodes = Opcodes.forApi(apiLevel)
}

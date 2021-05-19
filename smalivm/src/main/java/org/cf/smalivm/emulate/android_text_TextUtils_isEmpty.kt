package org.cf.smalivm.emulate

import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.ExecutionNode
import org.cf.smalivm.ExecutionState
import org.cf.smalivm.UnresolvedChild
import org.cf.smalivm.VirtualMachine
import org.slf4j.LoggerFactory

internal class android_text_TextUtils_isEmpty : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine): Array<out UnresolvedChild> {
        val charSequence = state.peekParameterOffset(0)!!.raw as CharSequence?
        // https://developer.android.com/reference/android/text/TextUtils#isEmpty(java.lang.CharSequence)
        val isEmpty = charSequence == null || charSequence.isEmpty()
        state.assignReturnRegister(isEmpty, RETURN_TYPE)
        return finishMethod()
    }

    companion object {
        private val log = LoggerFactory.getLogger(android_text_TextUtils_isEmpty::class.java.simpleName)
        private const val RETURN_TYPE = CommonTypes.BOOLEAN
    }
}

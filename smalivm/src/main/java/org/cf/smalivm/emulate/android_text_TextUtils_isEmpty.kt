package org.cf.smalivm.emulate

import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.VirtualMachine2
import org.slf4j.LoggerFactory

internal class android_text_TextUtils_isEmpty : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine2): Array<out UnresolvedChild> {
        val charSequence = state.peekParameter(0)!!.value as CharSequence?
        // https://developer.android.com/reference/android/text/TextUtils#isEmpty(java.lang.CharSequence)
        val isEmpty = charSequence == null || charSequence.isEmpty()
        state.assignReturnRegister(isEmpty, RETURN_TYPE)
        return finish()
    }

    companion object {
        private val log = LoggerFactory.getLogger(android_text_TextUtils_isEmpty::class.java.simpleName)
        private const val RETURN_TYPE = CommonTypes.BOOL
    }
}

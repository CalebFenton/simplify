package org.cf.smalivm.emulate;

import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm2.ExecutionState;
import org.cf.smalivm2.VirtualMachine2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class android_text_TextUtils_isEmpty extends EmulatedMethod {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(android_text_TextUtils_isEmpty.class.getSimpleName());

    private static final String RETURN_TYPE = CommonTypes.BOOL;

    @Override
    public void execute(VirtualMachine2 vm, Op op, ExecutionState state) {
        CharSequence charSequence = (CharSequence) state.peekParameter(0).getValue();
        // https://developer.android.com/reference/android/text/TextUtils#isEmpty(java.lang.CharSequence)
        boolean isEmpty = charSequence == null || charSequence.length() == 0;
        state.assignReturnRegister(isEmpty, RETURN_TYPE);
    }

}

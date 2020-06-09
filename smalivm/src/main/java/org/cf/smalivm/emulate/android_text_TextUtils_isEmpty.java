package org.cf.smalivm.emulate;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.VirtualClass;
import org.cf.util.ClassNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class android_text_TextUtils_isEmpty extends ExecutionContextMethod {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(android_text_TextUtils_isEmpty.class.getSimpleName());

    private static final String RETURN_TYPE = CommonTypes.BOOL;

    @Override
    public void execute(VirtualMachine vm, Op op, ExecutionContext context) {
        
        MethodState mState = context.getMethodState();
        CharSequence charSequence = (CharSequence) mState.peekParameter(0).getValue();
        
        if (charSequence == null || charSequence.length() == 0) {
            mState.assignReturnRegister(true, RETURN_TYPE);
        } else {
            mState.assignReturnRegister(false, RETURN_TYPE);
        }
    }
}

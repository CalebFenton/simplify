package org.cf.smalivm.emulate;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.VirtualType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class java_lang_Object_getClass extends ExecutionContextMethod implements UnknownValuesMethod {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(java_lang_Object_getClass.class.getSimpleName());

    private static final String RETURN_TYPE = CommonTypes.CLASS;

    @Override
    public void execute(VirtualMachine vm, Op op, ExecutionContext context) {
        MethodState mState = context.getMethodState();
        String argumentType = mState.peekParameter(0).getType();
        VirtualType virtualType = vm.getClassManager().getVirtualType(argumentType);
        try {
            Class<?> value = vm.getClassLoader().loadClass(virtualType.getBinaryName());
            mState.assignReturnRegister(value, RETURN_TYPE);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + argumentType, e);
        }
    }

}

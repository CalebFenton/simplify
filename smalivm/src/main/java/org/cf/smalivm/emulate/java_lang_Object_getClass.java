package org.cf.smalivm.emulate;

import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.VirtualType;
import org.cf.smalivm2.ExecutionState;
import org.cf.smalivm2.VirtualMachine2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class java_lang_Object_getClass extends EmulatedMethod implements UnknownValuesMethod {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(java_lang_Object_getClass.class.getSimpleName());

    private static final String RETURN_TYPE = CommonTypes.CLASS;

    @Override
    public void execute(VirtualMachine2 vm, Op op, ExecutionState state) {
        String argumentType = state.peekParameter(0).getType();
        VirtualType virtualType = vm.getClassManager().getVirtualType(argumentType);
        try {
            Class<?> value = vm.getClassLoader().loadClass(virtualType.getBinaryName());
            state.assignReturnRegister(value, RETURN_TYPE);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + argumentType, e);
        }
    }

}

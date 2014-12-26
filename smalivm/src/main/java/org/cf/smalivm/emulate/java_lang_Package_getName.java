package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.EmulatedType;
import org.cf.smalivm.type.UnknownValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Package_getName implements EmulatedMethod {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(java_lang_Package_getName.class.getSimpleName());

    private static final String RETURN_TYPE = "Ljava/lang/String;";

    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        // No checks because emulated methods require all known args.
        HeapItem instanceItem = mState.peekParameter(0);
        Object instance = instanceItem.getValue();
        Object name;
        if (instance instanceof java.lang.Package) {
            name = ((java.lang.Package) instance).getName();
        } else if (instance instanceof EmulatedType) {
            name = ((EmulatedType) instance).getExtra();
        } else {
            name = new UnknownValue();
        }
        mState.assignReturnRegister(name, RETURN_TYPE);
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}

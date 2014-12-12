package org.cf.smalivm.emulate;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.EmulatedType;
import org.cf.smalivm.type.UnknownValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class java_lang_Package_getName implements EmulatedMethod {

    private static final Logger log = LoggerFactory.getLogger(java_lang_Package_getName.class.getSimpleName());

    public void execute(VirtualMachine vm, MethodState mState) throws Exception {
        // No checks because emulated methods require all known args.
        Object instance = mState.peekParameter(0);
        Object value = new UnknownValue("Ljava/lang/String;");
        if (instance instanceof java.lang.Package) {
            value = ((java.lang.Package) instance).getName();
        } else if (instance instanceof EmulatedType) {
            String packageName = ((EmulatedType) instance).getExtra();
            value = packageName;
        }
        mState.assignReturnRegister(value);
    }

    public SideEffect.Level getSideEffectLevel() {
        return SideEffect.Level.NONE;
    }

}

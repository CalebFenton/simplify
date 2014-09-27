package org.cf.smalivm;

public class SideEffect {

    public enum Level {
        NONE, // reflected, emulated, white listed, or otherwise safe
        STRONG, // member variable modification
        WEAK, // not white listed, unknown
    };

}

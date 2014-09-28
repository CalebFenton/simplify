package org.cf.smalivm;

public class SideEffect {

    public static enum Level {
        NONE(0), // reflected, emulated, white listed, or otherwise safe
        STRONG(2), // member variable modification
        WEAK(1), // not white listed, unknown
        ;

        private final int value;

        Level(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

}

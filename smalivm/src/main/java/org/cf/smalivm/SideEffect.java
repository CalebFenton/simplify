package org.cf.smalivm;

public class SideEffect {

    public enum Level {
        NONE(0), // reflected, emulated, white listed, or otherwise safe
        WEAK(1), // not white listed, unknown
        STRONG(2), // member variable modification
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

package org.cf.smalivm;

public class SideEffect {

    public enum Level {
        NONE(0), // reflected, emulated, white listed, or otherwise safe
        STRONG(2), // member variable modification
        WEAK(1), // not white listed, unknown
        ;

        private final int level;

        Level(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }
    };

}

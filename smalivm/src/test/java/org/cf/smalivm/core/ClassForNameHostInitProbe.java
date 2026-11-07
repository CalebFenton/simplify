package org.cf.smalivm.core;

final class ClassForNameHostInitProbe {
    static {
        ClassForNameHostInitSentinel.initialized = true;
    }
}

final class ClassForNameHostInitSentinel {
    static boolean initialized;

    private ClassForNameHostInitSentinel() {}
}

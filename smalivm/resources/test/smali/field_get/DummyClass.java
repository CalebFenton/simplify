package org.cf.smalivm;

public class DummyClass {

    public static final int publicStaticField = 0x10;
    protected static final int protectedStaticField = 0x11;
    private static final int privateStaticField = 0x12;

    public final int publicInstanceField;
    final int protectedInstanceField;
    private final int privateInstanceField;

    DummyClass() {
        publicInstanceField = 0x20;
        protectedInstanceField = 0x21;
        privateInstanceField = 0x22;
    }

    private class DummyInnerClass {

    }
}


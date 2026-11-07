package com.google.common;

public abstract class Config {
    public static int KEY_BACK = failInitialization();

    private static int failInitialization() {
        throw new AssertionError("task40 host initializer failure");
    }
}

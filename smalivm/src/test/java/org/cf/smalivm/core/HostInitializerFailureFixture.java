package org.cf.smalivm.core;

/**
 * Host classes whose static initializers fail. A class whose initialization failed is erroneous for the rest of
 * the JVM's life (JLS 12.4.2), so each nested class is read by exactly one test.
 */
public final class HostInitializerFailureFixture {
    private HostInitializerFailureFixture() {
    }

    public static final class ThrowsError {
        public static final int VALUE = fail();

        private static int fail() {
            throw new AssertionError("initializer error");
        }
    }

    public static final class ThrowsException {
        public static final int VALUE = fail();

        private static int fail() {
            throw new IllegalStateException("initializer exception");
        }
    }
}

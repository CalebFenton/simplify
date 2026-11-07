package org.cf.smalivm.core;

public final class FieldHostFixture {
    final StringBuilder packageValue = new StringBuilder("package-value");
    public static final StringBuilder publicStatic = new StringBuilder("public-static");

    public interface UnsafeDefaultInterface {
        default int value() {
            return 1;
        }
    }

    public interface OrdinaryInterface {}

    public static final class UnsafeDefaultOwner implements UnsafeDefaultInterface {
        public static final StringBuilder value = new StringBuilder("unsafe-default");
    }

    public static final class OrdinaryOwner implements OrdinaryInterface {
        public static final StringBuilder value = new StringBuilder("ordinary");
    }
}

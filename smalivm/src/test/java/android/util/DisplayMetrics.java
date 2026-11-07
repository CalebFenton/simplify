package android.util;

public final class DisplayMetrics {
    public static final int DENSITY_DEVICE_STABLE = failInitialization();

    private static int failInitialization() {
        throw new AssertionError("task40 host initializer failure");
    }
}

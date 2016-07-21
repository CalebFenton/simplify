package org.cf.demosmali;

public class TestExceptionHandling {

    // Trick compilers / IDE into not optimizing away dead code
    // Since value is not final, compiler doesn't know if it'll change.
    private static int ONE = 1;

    public static void main(String... argv) {
        System.out.println("val = " + catchParentButNotGrandparentException());
    }

    public void testCasting() {
        Object o = 5;
        int x = (int) o;
        System.out.println(x);
    }

    public static int throwException() {
        int value = 1;
        try {
            if (0 < ONE) {
                throw new Exception();
            }

            value = 2;
        } catch (Exception ex) {
            value = 2;
        }

        return value;
    }

    public static int catchParentException() {
        int value = 1;
        try {
            if (0 < ONE) {
                throw new NullPointerException();
            }

            value = 2;
        } catch (Exception ex) {
            value = 3;
        }

        return value;
    }

    public static int catchParentButNotGrandparentException() {
        int value = 1;
        try {
            if (0 < ONE) {
                throw new NullPointerException();
            }

            value = 2;
        } catch (RuntimeException ex) {
            value = 3;
        } catch (Exception ex) {
            value = 4;
        }

        return value;
    }

    public static int finallyWithUnhandledException() throws Exception {
        int value = 1;
        try {
            if (0 < ONE) {
                throw new Exception();
            }

            value = 2;
        } finally {
            value = 3;
        }

        return value;
    }

    public static int finallyWithHandledException() {
        int value = 1;
        try {
            if (0 < ONE) {
                throw new Exception();
            }

            value = 2;
        } catch (Exception ex) {
            value = 4;
        } finally {
            value = 3;
        }

        return value;
    }

    public static int finallyWithCatchForAnotherException() throws Exception {
        int value = 1;
        try {
            if (0 < ONE) {
                throw new Exception();
            }

            value = 2;
        } catch (RuntimeException ex) {
            value = 4;
        } finally {
            value = 3;
        }

        return value;
    }

}

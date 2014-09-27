package org.cf.demosmali;

public class Main {

    public static void helloWorld() {
        byte[] data = new byte[] { 97, 71, 86, 115, 98, 71, 56, 115, 100, 50, 57, 121, 98, 71, 81, 104 };
        String output = new String(Base64.decode(data));

        System.out.println("helloWorld(): " + output);
    }

    public static void main(String[] args) {
        helloWorld();
        stringBuilder();
    }

    public static void stringBuilder() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.parseInt("1"));
        sb.append(Long.parseLong("2"));
        sb.append('3');
        sb.append("abc");

        System.out.println("stringBuilder(): " + sb.toString());
    }

    @SuppressWarnings("unused")
    private static void printParameter(int param) {
        System.out.println("printParameter(), param = " + param);
    }

    @SuppressWarnings("unused")
    private int parameterLogic(int param) {
        int x = 5;
        if (param == 10) {
            x = 10;
        }

        System.out.println("parameterLogic(), x = " + x);

        return x;
    }

}

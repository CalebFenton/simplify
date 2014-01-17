package simplify;

import java.util.HashMap;
import java.util.Map;

public class StressTest {

    private static Map<Integer, String> stringMap;
    static {
        stringMap = new HashMap<Integer, String>();
        stringMap.put(0, "map value 0");
        stringMap.put(1, "map value 1");
        stringMap.put(2, "map value 2");
    }

    public StressTest() {
        // TODO Auto-generated constructor stub
    }

    public static void doTest() {
        for (int i = 0; i < 5; i++) {
            System.out.println(getMap(i));
        }

        System.out.println(staticDecrypt("!1 skrow ti"));

        System.out.println(staticDecrypt2("hsvnqjr1 "));
    }

    public static String getMap(int i) {
        int key = i % 3;

        return stringMap.get(key);
    }

    public static String staticDecrypt(String encrypted) {
        StringBuilder sb = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();

        // for (int i = encrypted.length() - 1; i >= 0; i--) {
        for (int i = 11; i >= 0; i--) {
            sb.append(encrypted.charAt(i));
            sb2.append(i);
        }

        System.out.println("some numbers: " + sb2.toString());

        return sb.toString();
    }

    public static String staticDecrypt2(String encrypted) {
        StringBuilder sb = new StringBuilder(encrypted);

        // for (int i = 0; i < encrypted.length(); i++) {
        for (int i = 0; i < 11; i++) {
            sb.setCharAt(i, (char) (sb.charAt(i) + 1));
        }

        return sb.toString();
    }

}

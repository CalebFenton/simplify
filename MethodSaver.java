package simplify;

import java.util.Set;

public class MethodSaver {

    public static void saveMethods(MethodContainer container) {
        Set<String> signatures = container.getModifiedMethods();
        for (String signature : signatures) {
            String parent = container.getMethod(signature).getParentFile();

            // load parent file to string

            // find method signature, method body

            // replace method body with modified body

            // save string to file
        }
    }

}

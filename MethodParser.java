package simplify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class MethodParser {
    private static Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern methodStartPattern = Pattern.compile("^.method (.*)$");
    private static final Pattern methodEndPattern = Pattern.compile("^.end method$");

    public static MethodContainer parse(List<String> smaliFiles) throws IOException {
        MethodContainer container = new MethodContainer();

        for (String file : smaliFiles) {
            log.fine("Parsing for methods: " + file);

            List<String> lines = FileUtils.readLines(new File(file), "UTF-8");

            List<String> methodLines = null;
            String signature = null;
            boolean inMethod = false;
            for (String line : lines) {
                if (!inMethod) {
                    Matcher m = methodStartPattern.matcher(line);
                    if (m.find()) {
                        inMethod = true;

                        signature = extractMethodSignature(m.group(1));
                        methodLines = new ArrayList<String>();
                    }
                } else {
                    Matcher m = methodEndPattern.matcher(line);
                    if (m.matches()) {
                        inMethod = false;

                        log.finer("Adding " + signature + " from " + file + " with " + methodLines.size() + " lines");
                        container.addMethod(signature, file, methodLines);
                    } else {
                        methodLines.add(line);
                    }
                }

            }
        }

        return container;
    }

    private static String extractMethodSignature(String methodDeclaration) {
        String[] parts = methodDeclaration.split("\\s+");

        return parts[parts.length - 1];
    }
}
package simplify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import simplify.instruction.LabelInstruction;

public class MethodParser {
    private static Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern methodStartPattern = Pattern.compile("^.method (.*)$");
    private static final Pattern methodEndPattern = Pattern.compile("^.end method$");

    public static MethodContainer parse(List<String> smaliFiles) throws IOException {
        MethodContainer container = new MethodContainer();

        for (String file : smaliFiles) {
            log.fine("Parsing for methods: " + file);

            List<String> lines = FileUtils.readLines(new File(file), "UTF-8");

            LabelInstruction label = new LabelInstruction();
            List<String> methodLines = null;
            Map<String, Integer> methodJumps = null;
            String signature = null;
            int position = -1;
            for (String line : lines) {
                if (position < 0) {
                    Matcher m = methodStartPattern.matcher(line);
                    if (m.find()) {
                        position = 0;

                        signature = extractMethodSignature(m.group(1));
                        methodLines = new ArrayList<String>();
                        methodJumps = new HashMap<String, Integer>();
                    }
                } else {
                    if (methodEndPattern.matcher(line).matches()) {
                        position = -1;

                        log.finer("\tadding " + signature + " from " + file + " with " + methodLines.size() + " lines");
                        container.addMethod(signature, file, methodLines, methodJumps);
                    } else {
                        methodLines.add(line);

                        position++;

                        Matcher m = label.getPattern().matcher(line);
                        if (m.matches()) {
                            methodJumps.put(m.group(1), position);
                        }
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
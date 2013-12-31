package simplify;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

public class MethodExtractor {
    private static Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private static final Pattern classDefinition = Pattern.compile("^\\.class ([\\S]+ ?)+", Pattern.MULTILINE);
    private static final Pattern methodDefinition = Pattern.compile("^\\.method ([\\S]+ ?)+\\n(.*?)\\.end method",
                    Pattern.DOTALL + Pattern.MULTILINE);

    public static MethodContainer extract(List<String> smaliFiles) throws IOException {
        MethodContainer container = new MethodContainer();

        for (String file : smaliFiles) {
            log.fine("Parsing for methods: " + file);

            String lines = readAndTrimLines(new File(file));
            if (lines.length() == 0) {
                break;
            }

            Matcher m = classDefinition.matcher(lines);
            if (!m.find()) {
                // TODO: throw invalid smali file
            }
            String classPath = m.group(1);

            m = methodDefinition.matcher(lines);

            while (m.find()) {
                String[] parts = m.group(1).split("\\s+");
                String methodSignature = parts[parts.length - 1];
                String signature = classPath + "->" + methodSignature;
                List<String> methodLines = Arrays.asList(m.group(2).split("\\n"));

                log.finer("Adding method: " + signature + "[" + methodLines.size() + "]");
                container.addMethod(signature, file, methodLines);
            }
        }

        return container;
    }

    private static String readAndTrimLines(File file) throws IOException {
        List<String> lines = FileUtils.readLines(file, "UTF-8");

        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            result.append(line.trim()).append("\n");
        }

        return result.toString();
    }

}
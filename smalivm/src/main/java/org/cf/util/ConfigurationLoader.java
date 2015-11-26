package org.cf.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class ConfigurationLoader {

    public static List<String> loadConfig(String path) throws IOException {
        // Some classes may be mostly safe except a few methods. Either exclude or emulate them.
        InputStream is = ConfigurationLoader.class.getClassLoader().getResourceAsStream(path);
        List<String> lines = IOUtils.readLines(is);
        IOUtils.closeQuietly(is);
        removeNonDefinitionLines(lines);

        return lines;
    }

    private static void removeNonDefinitionLines(List<String> lines) {
        Iterator<String> iter = lines.iterator();
        while (iter.hasNext()) {
            String line = iter.next();
            if (line.trim().startsWith("//") || line.trim().isEmpty()) {
                iter.remove();
            }
        }
    }

}

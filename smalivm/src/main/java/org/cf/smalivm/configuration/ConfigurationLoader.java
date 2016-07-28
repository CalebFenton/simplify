package org.cf.smalivm.configuration;

import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class ConfigurationLoader {

    public static List<String> load(String path) {
        URL url = Resources.getResource(path);
        List<String> lines = new LinkedList<>();
        try {
            lines = Resources.readLines(url, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

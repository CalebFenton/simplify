package org.cf.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImmutableUtils {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(ImmutableUtils.class.getSimpleName());

    private static final Set<String> immutableClasses = getImmutableClasses();

    private static Set<String> getImmutableClasses() {
        List<String> lines = new ArrayList<String>();
        try {
            lines = ConfigLoader.loadConfig("immutable_classes.cfg");
        } catch (IOException e) {
            log.warn("Error loading immutable classes.", e);
        }

        return new HashSet<String>(lines);
    }

    public static boolean isImmutableClass(String smaliClassName) {
        if (smaliClassName.startsWith("[")) {
            // Array contents can always be mutated
            return false;
        }

        if (smaliClassName.equals("?")) {
            // Unknown type. Probably lazy and didn't get implied type.
            return false;
        }

        // TODO: https://github.com/MutabilityDetector/MutabilityDetector
        if (immutableClasses.contains(smaliClassName)) {
            return true;
        }

        return false;
    }

}

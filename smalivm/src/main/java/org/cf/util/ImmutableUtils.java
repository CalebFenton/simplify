package org.cf.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.cf.smalivm.opcode.SGetOp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImmutableUtils {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(SGetOp.class.getSimpleName());

    private static Set<String> immutableClasses;

    static {
        loadImmutableClasses();
    }

    private static void loadImmutableClasses() {
        List<String> lines = new ArrayList<String>();
        try {
            lines = ConfigLoader.loadConfig("immutable_classes.cfg");
        } catch (IOException e) {
            log.warn("Error loading immutable classes.", e);
        }
        immutableClasses = new HashSet<String>(lines);
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

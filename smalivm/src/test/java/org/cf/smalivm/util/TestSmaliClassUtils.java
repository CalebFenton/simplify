package org.cf.smalivm.util;

import static org.junit.Assert.assertEquals;

import org.cf.util.SmaliClassUtils;
import org.junit.Test;

public class TestSmaliClassUtils {

    private static String SMALI_TYPE = "Lsome/package/Class;";
    private static String PACKAGE_NAME = "some.package";

    @Test
    public void TestGetPackageName() {
        String packageName = SmaliClassUtils.getPackageName(SMALI_TYPE);

        assertEquals(PACKAGE_NAME, packageName);
    }

}

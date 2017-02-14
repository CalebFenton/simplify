package org.cf.smalivm.dex;

import org.cf.smalivm.VMTester;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SmaliParserTest {

    @Test
    public void canParseSmaliDirectory() throws Exception {
        List<BuilderClassDef> classDefs = SmaliParser.parse(VMTester.TEST_CLASS_PATH);
        assertTrue(classDefs.size() > 0);
    }
}

package org.cf.smalivm.dex;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.cf.smalivm.VMTester;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.junit.jupiter.api.Test;

public class SmaliParserTest {

    @Test
    public void canParseSmaliDirectory() throws Exception {
        List<BuilderClassDef> classDefs = SmaliParser.parse(VMTester.TEST_CLASS_PATH);
        assertTrue(classDefs.size() > 0);
    }
}

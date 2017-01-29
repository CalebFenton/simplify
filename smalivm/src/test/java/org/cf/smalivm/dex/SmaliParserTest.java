package org.cf.smalivm.dex;

import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SmaliParserTest {

    private static final String SMALI_PATH = "resources/test/smali/ops";

    @Test
    public void canParseSmaliDirectory() throws Exception {
        List<BuilderClassDef> classDefs = SmaliParser.parse(SMALI_PATH);
        assertTrue(classDefs.size() > 0);
    }
}

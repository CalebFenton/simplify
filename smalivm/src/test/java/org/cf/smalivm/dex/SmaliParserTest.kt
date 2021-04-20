package org.cf.smalivm.dex

import org.cf.smalivm.VMTester
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SmaliParserTest {
    @Test
    @Throws(Exception::class)
    fun canParseSmaliDirectory() {
        val classDefs = SmaliParser.parse(VMTester.TEST_CLASS_PATH)
        Assertions.assertTrue(classDefs.size > 0)
    }
}

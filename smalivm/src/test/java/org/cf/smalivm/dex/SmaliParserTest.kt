package org.cf.smalivm.dex

import org.cf.smalivm.Tester
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class SmaliParserTest {
    @Test
    @Throws(Exception::class)
    fun canParseSmaliDirectory() {
        val classDefs = SmaliParser.parse(Tester.TEST_CLASS_PATH)
        Assertions.assertTrue(classDefs.size > 0)
    }
}

package org.cf.demoapp

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MainTest {
    @Test
    fun `demo prints guest output and return consensus`() {
        val resources = File("resources")
        val captured = ByteArrayOutputStream()
        val original = System.out
        System.setOut(PrintStream(captured, true, Charsets.UTF_8))
        try {
            Main.main(arrayOf(resources.path))
        } finally {
            System.setOut(original)
        }

        // Base64 "aGVsbG8sd29ybGQh" is "hello,world!"; parseInt("1"), parseLong("2"), '3', "abc" build "123abc".
        // An unknown parameter runs both branches, so the return join of 10 and 5 is unknown.
        val expected = listOf(
            "Demo input: ${resources.absolutePath}",
            "helloWorld(): hello,world!",
            "stringBuilder(): 123abc",
            "Guest main: Complete",
            "printParameter(), param = 42",
            "Known print parameter 42: Complete",
            "parameterLogic(), x = 10",
            "parameterLogic(), x = 5",
            "Unknown parameter: Complete",
            "Unknown parameter: return consensus = Unknown(type=I)",
            "parameterLogic(), x = 10",
            "Known parameter 10: Complete",
            "Known parameter 10: return consensus = Known(raw=10, type=I)",
        )
        assertEquals(expected, captured.toString(Charsets.UTF_8).lines().filter { it.isNotEmpty() })
    }
}

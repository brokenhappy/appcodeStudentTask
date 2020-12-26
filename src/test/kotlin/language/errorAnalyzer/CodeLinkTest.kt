package language.errorAnalyzer

import language.errorAnalyzer.ErrorAnalyzer.ErrorPart.CodeLink
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

internal class CodeLinkTest {
    @Test
    fun `test when cast to string and column exists, gives colon separated result`() {
        assertEquals(
            "3:8",
            CodeLink(3, 8).toString(),
        )
    }

    @Test
    fun `test when cast to string and column does not exist, gives colon separated result`() {
        assertEquals(
            "3",
            CodeLink(3, null).toString(),
        )
    }

    @Test
    fun `test that if code link is under the code, an exception is thrown`() {
        assertThrows(Exception::class.java) { CodeLink(3, 13).resolveIndexIn("foobar") }
    }

    @Test
    fun `test that it works over multiple lines`() {
        assertEquals(
            8,
            CodeLink(3, 2).resolveIndexIn("""
                0123
                5
                7.9
                I wasn't originally going to get a brain transplant, but then I changed my mind
            """.trimIndent()),
        )
    }

    @Test
    fun `test if column is not given, it gives the start of the line`() {
        assertEquals(
            7,
            CodeLink(3, null).resolveIndexIn("""
                0123
                5
                7.9 foo bar baz
                A police officer just knocked on my door and told me my dogs are chasing people on bikes.
                That's ridiculous. My dogs dont even own bikes
            """.trimIndent()),
        )
    }
}
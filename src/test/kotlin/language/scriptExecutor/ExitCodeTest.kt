package language.scriptExecutor

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ExitCodeTest {
    @Test
    fun `test if error code is zero, it is NOT nonzero`() {
        assertFalse(ExitCode(0).isNonZero())
    }

    @Test
    fun `test if error code is something other then zero, it IS nonzero`() {
        assertTrue(ExitCode(-123).isNonZero())
    }

    @Test
    fun `test it gives the number when cast to string`() {
        assertEquals(
            "12333",
            ExitCode(12333).toString(),
        )
    }
}
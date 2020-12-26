package language.scriptExecutor

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class EventRaisingCommandExecutorTest {
    @Test
    fun `test if echo command is used, value is raised as output event`() {
        val output = mutableListOf<String>()

        EventRaisingCommandExecutor("echo", "abc") {
            streamingErrorsTo { fail("Should never raise error") }
            streamingOutputTo { output.add(it) }
        }.execute()

        assertEquals(listOf("abc", ""), output)
    }

    @Test
    fun `test if successful command is executed, exit code should be zero`() {
        assertFalse(EventRaisingCommandExecutor("echo", "abc").execute().isNonZero())
    }

    @Test
    fun `test if unsuccessful command is executed, exit code should be non-zero`() {
        assertTrue(
            EventRaisingCommandExecutor("cat", "8401298090dsasdjhaksjhalsdbmnbanbsdjhasjdhagsdjhasdjkhasd")
                .execute()
                .isNonZero()
        )
    }

    @Test
    fun `test if command is ran that should give an error, no output is given, but error stream was fired`() {
        val error = mutableListOf<String>()

        EventRaisingCommandExecutor("cat", "8401298090dsasdjhaksjhalsdbmnbanbsdjhasjdhagsdjhasdjkhasd") {
            streamingErrorsTo { error.add(it) }
            streamingOutputTo { fail("Should never raise error") }
        }.execute()

        assertTrue(error.isNotEmpty())
    }
}
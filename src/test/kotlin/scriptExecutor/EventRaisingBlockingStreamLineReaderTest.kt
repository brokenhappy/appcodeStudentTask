package scriptExecutor

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

internal class EventRaisingBlockingStreamLineReaderTest {
    @Test
    fun `Test that event is never raised if no read is ever performed`() {
        EventRaisingBlockingStreamLineReader("text that should be raised if read was performed\n".byteInputStream()) {
            fail { "Should never read a line, but read '$it'" }
        }
    }

    @Test
    fun `Test that empty stream never raises events`() {
        EventRaisingBlockingStreamLineReader("".byteInputStream()) {
            fail { "Should never read a line, but read '$it'" }
        }.performRead()
    }

    @Test
    fun `Test that it never raises if text does not have a newline and it's never flushed`() {
        EventRaisingBlockingStreamLineReader("text without newline".byteInputStream()) {
            fail { "Should never read a line, but read '$it'" }
        }.performRead()
    }

    @Test
    fun `Test that it does not raise if flushed while empty`() {
        EventRaisingBlockingStreamLineReader("".byteInputStream()) {
            fail { "Should never read a line, but read '$it'" }
        }.flush()
    }

    @Test
    fun `Test that it raises if it is flushed while it is not empty`() {
        val raisedLines = mutableListOf<String>()
        EventRaisingBlockingStreamLineReader("text without newline".byteInputStream()) { raisedLines.add(it) }.flush()

        assertEquals(listOf("text without newline"), raisedLines)
    }

    @Test
    fun `Test that it raises if a read is performed and it has a newline`() {
        val raisedLines = mutableListOf<String>()
        EventRaisingBlockingStreamLineReader("text with newline\n".byteInputStream()) { raisedLines.add(it) }.performRead()

        assertEquals(listOf("text with newline"), raisedLines)
    }

    @Test
    fun `Test that it raises multiple but not last line if read is performed`() {
        val raisedLines = mutableListOf<String>()
        EventRaisingBlockingStreamLineReader("""
            I was wondering why the ball was getting bigger
            Then it hit me
            Badum tss
        """.trimIndent().byteInputStream()) { raisedLines.add(it) }.performRead()

        assertEquals(listOf("I was wondering why the ball was getting bigger", "Then it hit me"), raisedLines)
    }

    @Test
    fun `Test that it raises all lines if it is flushed`() {
        val raisedLines = mutableListOf<String>()
        EventRaisingBlockingStreamLineReader("""
            How do you make holy water?
            You boil the hell out of it
        """.trimIndent().byteInputStream()) { raisedLines.add(it) }.flush()

        assertEquals(listOf("How do you make holy water?", "You boil the hell out of it"), raisedLines)
    }

    @Test
    fun `Test that if it is read, it should first raise all but the last line, and when flushed, all lines should be raised`() {
        val raisedLines = mutableListOf<String>()
        EventRaisingBlockingStreamLineReader("""
            What do you call a bee that can't make up his mind?
            A maybe
        """.trimIndent().byteInputStream()) { raisedLines.add(it) }.apply {
            performRead()
            assertEquals(listOf("What do you call a bee that can't make up his mind?"), raisedLines)
            flush()
            assertEquals(listOf("What do you call a bee that can't make up his mind?", "A maybe"), raisedLines)
        }

    }
}
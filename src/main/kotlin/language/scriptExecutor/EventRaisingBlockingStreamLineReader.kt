package language.scriptExecutor

import java.io.InputStream


class EventRaisingBlockingStreamLineReader(
    private val stream: InputStream,
    private val lineReadEvent: (String) -> Unit,
) {
    private var currentWorkingString: String? = null

    fun performRead() {
        val bytesToRead = stream.available()
        if (bytesToRead == 0)
            return

        val newLines = getLinesSinceLastRead(bytesToRead)

        for (i in 0..newLines.size - 2)
            lineReadEvent(newLines[i])

        currentWorkingString = newLines.last()
    }

    private fun getLinesSinceLastRead(bytesToRead: Int): List<String> {
        val newReadString = String(stream.readNBytes(bytesToRead))
        return ((currentWorkingString ?: "") + newReadString)
            .lines()
    }

    fun flush() {
        performRead()
        val remainder = currentWorkingString ?: return
        lineReadEvent(remainder)
    }
}
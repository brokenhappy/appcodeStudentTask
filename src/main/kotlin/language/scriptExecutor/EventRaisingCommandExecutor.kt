package language.scriptExecutor

import org.jetbrains.annotations.Contract

class EventRaisingCommandExecutor(private vararg val command: String, builder: Builder.() -> Unit = { }) {
    private var onError: (String) -> Unit = { }
    private var onOutput: (String) -> Unit = { }

    init {
        Builder(this).builder()
    }

    class Builder(private val parent: EventRaisingCommandExecutor) {
        fun streamingErrorsTo(onError: (String) -> Unit) {
            parent.onError = onError
        }
        fun streamingOutputTo(onOutput: (String) -> Unit) {
            parent.onOutput = onOutput
        }
    }

    @Contract(pure = true)
    private fun Process.isNotFinished() = runCatching { exitValue() }.isFailure

    fun execute(): ExitCode {
        val process = ProcessBuilder(*command).start()

        val errorReader = EventRaisingBlockingStreamLineReader(process.errorStream, onError)
        val inputReader = EventRaisingBlockingStreamLineReader(process.inputStream, onOutput)

        while (process.isNotFinished()) {
            errorReader.readAndRaiseEvents()
            inputReader.readAndRaiseEvents()
            Thread.sleep(10)
        }

        errorReader.flush()
        inputReader.flush()
        return ExitCode(process.exitValue())
    }
}
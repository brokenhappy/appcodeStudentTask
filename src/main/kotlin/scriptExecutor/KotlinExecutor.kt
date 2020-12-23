package scriptExecutor

import org.intellij.lang.annotations.Language
import java.io.Closeable
import java.io.File
import java.io.FileWriter

class KotlinExecutor(
    private val warningResolver: KotlinCompileCommonWarningResolver,
    private val kotlinCompileCommandProvider: KotlinCompileCommandProvider
) {
    class AlreadyRunningException: Exception()

    @Volatile
    private var _isRunning = false
    val isRunning
        get() = _isRunning

    private fun Process.isNotFinished() = kotlin.runCatching { exitValue() }.isFailure

    @Throws(AlreadyRunningException::class)
    fun runAndGetExitCode(
        @Language("kts") kotlinScript: String,
        inputEvent: (String) -> Unit,
        errorEvent: (String) -> Unit
    ): Int {
        if (_isRunning)
            throw AlreadyRunningException()

        _isRunning = true
        createTemporaryScriptFile(kotlinScript).use { file ->
            val process = startScriptRunnerProcessDisablingWarnings(file.absolutePath)

            val errorReader = EventRaisingBlockingStreamLineReader(process.errorStream) { errorLine ->
                if (!warningResolver.isCommonWarning(errorLine))
                    errorEvent(errorLine)
            }
            val inputReader = EventRaisingBlockingStreamLineReader(process.inputStream, inputEvent)

            while (process.isNotFinished()) {
                errorReader.performRead()
                inputReader.performRead()
                Thread.sleep(10)
            }

            errorReader.flush()
            inputReader.flush()
            _isRunning = false
            return process.exitValue()
        }
    }


    private fun startScriptRunnerProcessDisablingWarnings(scriptPath: String) =
        ProcessBuilder(
            kotlinCompileCommandProvider.kotlinCompileCommand, "-script", scriptPath, "-nowarn"
        )
        .directory(File("/Users/woutwerkman/Documents/projects/AppCodeStudentTask/src/main/kotlin"))
        .start()

    private fun createTemporaryScriptFile(kotlinScript: String) = object : Closeable {
        private val file = File("foo.kts").apply {
            createNewFile()
            deleteOnExit()
            FileWriter(absoluteFile).use { it.write(kotlinScript) }
        }

        val absolutePath
            get() = file.absolutePath

        override fun close() {
            file.delete()
        }
    }


}

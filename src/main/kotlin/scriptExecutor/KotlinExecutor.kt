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
    private var isRunning = false

    private fun Process.isNotFinished() = kotlin.runCatching { exitValue() }.isFailure

    @Throws(AlreadyRunningException::class)
    fun run(
        @Language("kts") kotlinScript: String,
        inputEvent: (String) -> Unit,
        errorEvent: (String) -> Unit,
    ): ExitCode {
        if (isRunning)
            throw AlreadyRunningException()

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
            isRunning = false
            return ExitCode(process.exitValue())
        }
    }


    private fun startScriptRunnerProcessDisablingWarnings(scriptPath: String) =
        ProcessBuilder(
            kotlinCompileCommandProvider.kotlinCompileCommand, "-script", scriptPath, "-nowarn"
        )
        .directory(File("/Users/woutwerkman/Documents/projects/AppCodeStudentTask/src/main/kotlin")) // TODO: allow other people to use this
        .start()

    private fun createTemporaryScriptFile(kotlinScript: String) = object : Closeable {
        private val file = File("foo.kts").apply { // TODO:  use injected file
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

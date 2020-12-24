package scriptExecutor

import org.intellij.lang.annotations.Language
import java.io.Closeable
import java.io.File
import java.io.FileWriter
import java.lang.RuntimeException
import javax.inject.Inject

class KotlinExecutor @Inject constructor(
    private val warningResolver: KotlinCompileCommonWarningResolver,
) : ScriptExecutor {

    private fun Process.isNotFinished() = runCatching { exitValue() }.isFailure

    override fun run(
        @Language("kts") kotlinScript: String,
        inputEvent: (String) -> Unit,
        errorEvent: (String) -> Unit,
    ): ExitCode {
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
            return ExitCode(process.exitValue())
        }
    }


    private fun startScriptRunnerProcessDisablingWarnings(scriptPath: String) =
        ProcessBuilder("kotlinc", "-script", scriptPath, "-nowarn")
            .start()

    private fun createTemporaryScriptFile(kotlinScript: String) = object : Closeable {
        private val file = File("foo.kts").apply { // TODO:  use injected file
            createNewFile()
            deleteOnExit()
            FileWriter(absoluteFile).use { it.write(kotlinScript) }
        }

        val absolutePath get() = file.absolutePath

        override fun close() {
            file.delete()
        }
    }


}

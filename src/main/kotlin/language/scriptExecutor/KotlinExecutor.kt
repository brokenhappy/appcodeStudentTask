package language.scriptExecutor

import org.intellij.lang.annotations.Language
import javax.inject.Inject

class KotlinExecutor @Inject constructor(
    private val warningResolver: KotlinCompileCommonWarningResolver,
    private val temporaryScriptFileProvider: TemporaryScriptFileProvider,
) : ScriptExecutor {
    override fun run(
        @Language("kts") script: String,
        outputEvent: (String) -> Unit,
        errorEvent: (String) -> Unit,
    ): ExitCode {
        temporaryScriptFileProvider.create(script).use { file ->
            return EventRaisingCommandExecutor("kotlinc", "-script",  file.absolutePath, "-nowarn") {
                streamingErrorsTo { errorLine ->
                    if (warningResolver.isCommonWarning(errorLine))
                        return@streamingErrorsTo
                    errorEvent(errorLine)
                }
                streamingOutputTo(outputEvent)
            }.execute()
        }
    }
}

package language.scriptExecutor

import org.intellij.lang.annotations.Language
import javax.inject.Inject

class SwiftExecutor @Inject constructor(
    private val temporaryScriptFileProvider: TemporaryScriptFileProvider,
) : ScriptExecutor {
    override fun run(
        @Language("swift") script: String,
        inputEvent: (String) -> Unit,
        errorEvent: (String) -> Unit,
    ): ExitCode {
        // TODO: Remove this if it is supported
        inputEvent("// The output of Swift execution is currently not live. Swift's process streams are blocked during execution.")
        inputEvent("")
        temporaryScriptFileProvider.create(script).use { file ->
            return EventRaisingCommandExecutor("/usr/bin/env", "swift", file.absolutePath) {
                streamingErrorsTo(errorEvent)
                streamingOutputTo(inputEvent)
            }.execute()
        }
    }
}

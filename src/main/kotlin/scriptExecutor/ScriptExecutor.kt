package scriptExecutor

import org.intellij.lang.annotations.Language

interface ScriptExecutor {
    fun run(
        @Language("kts") kotlinScript: String,
        inputEvent: (String) -> Unit,
        errorEvent: (String) -> Unit,
    ): ExitCode
}
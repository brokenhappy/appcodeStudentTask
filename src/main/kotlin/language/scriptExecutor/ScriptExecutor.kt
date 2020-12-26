package language.scriptExecutor

interface ScriptExecutor {
    fun run(
        script: String,
        inputEvent: (String) -> Unit,
        errorEvent: (String) -> Unit,
    ): ExitCode
}
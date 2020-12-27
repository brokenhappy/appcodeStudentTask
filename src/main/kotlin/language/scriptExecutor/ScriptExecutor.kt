package language.scriptExecutor

interface ScriptExecutor {
    fun run(
        script: String,
        outputEvent: (String) -> Unit,
        errorEvent: (String) -> Unit,
    ): ExitCode
}
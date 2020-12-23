package scriptExecutor

class KotlinCompileExecutingCommonWarningResolver(
    private val commandProvider: KotlinCompileCommandProvider
) : KotlinCompileCommonWarningResolver {
    val alwaysPresentWarnings by lazy {
        val process = ProcessBuilder(commandProvider.kotlinCompileCommand, "-version")
            .start()
            .apply { waitFor() }

        process.errorStream.bufferedReader().readLines()
            .filter { !it.startsWith("info: ") }
            .toSet()
    }

    override fun isCommonWarning(line: String) = line in alwaysPresentWarnings
}
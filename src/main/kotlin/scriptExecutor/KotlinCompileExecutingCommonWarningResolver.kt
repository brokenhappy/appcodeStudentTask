package scriptExecutor

class KotlinCompileExecutingCommonWarningResolver() : KotlinCompileCommonWarningResolver {
    private val alwaysPresentWarnings by lazy {
        val process = ProcessBuilder("kotlinc", "-version")
            .start()
            .apply { waitFor() }

        process.errorStream.bufferedReader().readLines()
            .filter { !it.startsWith("info: ") }
            .toSet()
    }

    override fun isCommonWarning(line: String) = line in alwaysPresentWarnings
}
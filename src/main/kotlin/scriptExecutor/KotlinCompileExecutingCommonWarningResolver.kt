package scriptExecutor

import org.jetbrains.annotations.Contract
import javax.inject.Inject

class KotlinCompileExecutingCommonWarningResolver @Inject constructor() : KotlinCompileCommonWarningResolver {
    private val alwaysPresentWarnings by lazy {
        val process = ProcessBuilder("kotlinc", "-version")
            .start()
            .apply { waitFor() }

        process.errorStream.bufferedReader().readLines()
            .filter { !it.startsWith("info: ") }
            .toSet()
    }

    @Contract(pure = true)
    override fun isCommonWarning(line: String) = line in alwaysPresentWarnings
}
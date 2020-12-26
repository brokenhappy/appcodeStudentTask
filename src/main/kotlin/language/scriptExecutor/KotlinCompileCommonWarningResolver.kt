package language.scriptExecutor

import org.jetbrains.annotations.Contract

interface KotlinCompileCommonWarningResolver {
    @Contract(pure = true)
    fun isCommonWarning(line: String): Boolean
}

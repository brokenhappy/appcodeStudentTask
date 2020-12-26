package language.scriptExecutor

import org.jetbrains.annotations.Contract

inline class ExitCode(private val code: Int) {
    @Contract(pure = true)
    fun isNonZero() = code != 0

    @Contract(pure = true)
    override fun toString() = code.toString()
}
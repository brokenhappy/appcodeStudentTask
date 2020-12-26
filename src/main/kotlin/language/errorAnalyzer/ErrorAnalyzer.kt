package language.errorAnalyzer

import org.jetbrains.annotations.Contract

interface ErrorAnalyzer {
    sealed class ErrorPart {
        data class CodeLink(val line: Int, val column: Int?) : ErrorPart() {
            @Contract(pure = true)
            override fun toString() = if (column == null) line.toString() else "$line:$column"

            @Contract(pure = true)
            fun resolveIndexIn(code: String): Int {
                val indexAtStartOfLine = code.split("\n").asSequence()
                    .runningFold(0) { acc, line -> acc + line.length + 1 }
                    .drop(line - 1)
                    .first()

                return indexAtStartOfLine + (column ?: 1) - 1
            }
        }

        data class Text(val text: String) : ErrorPart()
    }

    @Contract(pure = true)
    fun splitIntoCodeParts(errors: String): Sequence<ErrorPart>
}
package errorAnalyzer

interface ErrorAnalyzer {
    sealed class ErrorPart {
        data class CodeLink(val line: Int, val column: Int?) : ErrorPart() {
            override fun toString() = if (column == null) line.toString() else "$line:$column"
        }
        data class Text(val text: String) : ErrorPart()
    }

    fun analyze(scriptFileName: String, errors: String): Sequence<ErrorPart>
}
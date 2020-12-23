package errorAnalyzer

class KotlinErrorAnalyzer {
    sealed class ErrorChunk {
        data class CodeLink(val line: Int, val column: Int?) : ErrorChunk() {
            override fun toString() =
                if (column == null)
                    line.toString()
                else
                    "$line:$column"
        }
        data class Text(val text: String) : ErrorChunk()
    }

    fun analyze(scriptFileName: String, errors: String) = sequence {
        val regexTpFindPathName = "(/[A-z0-9 ]+)*/?${scriptFileName.replace(".", "\\.")}:(\\d+)(:(\\d+))?".toRegex()

        var currentSearchingIndex = 0
        regexTpFindPathName.findAll(errors).forEach { match ->
            yield(ErrorChunk.Text(errors.substring(currentSearchingIndex, match.range.first)))
            yield(ErrorChunk.CodeLink(match.groupValues[2].toInt(), match.groupValues[4].toIntOrNull()))
            currentSearchingIndex = match.range.last + 1
        }

        yield(ErrorChunk.Text(errors.substring(currentSearchingIndex)))
    }

}

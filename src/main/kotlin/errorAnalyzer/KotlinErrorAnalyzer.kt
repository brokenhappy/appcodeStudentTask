package errorAnalyzer

class KotlinErrorAnalyzer {
    sealed class ErrorPart {
        data class CodeLink(val line: Int, val column: Int?) : ErrorPart() {
            override fun toString() =
                if (column == null)
                    line.toString()
                else
                    "$line:$column"
        }
        data class Text(val text: String) : ErrorPart()
    }

    fun analyze(scriptFileName: String, errors: String) = sequence {
        val regexToFindPathName = "(/[A-z0-9 ]+)*/?${scriptFileName.replace(".", "\\.")}:(\\d+)(:(\\d+))?".toRegex()

        var currentSearchingIndex = 0
        regexToFindPathName.findAll(errors).forEach { match ->
            yield(ErrorPart.Text(errors.substring(currentSearchingIndex, match.range.first)))
            yield(ErrorPart.CodeLink(match.groupValues[2].toInt(), match.groupValues[4].toIntOrNull()))
            currentSearchingIndex = match.range.last + 1
        }

        yield(ErrorPart.Text(errors.substring(currentSearchingIndex)))
    }

}

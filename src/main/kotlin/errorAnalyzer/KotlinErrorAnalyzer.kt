package errorAnalyzer

import org.jetbrains.annotations.Contract
import javax.inject.Inject


class KotlinErrorAnalyzer @Inject constructor() : ErrorAnalyzer {
    @Contract(pure = true)
    override fun splitIntoCodeParts(scriptFileName: String, errors: String) = sequence {
        val regexToFindPathName = "(/[A-z0-9 ]+)*/?${scriptFileName.replace(".", "\\.")}:(\\d+)(:(\\d+))?".toRegex()

        var currentSearchingIndex = 0
        regexToFindPathName.findAll(errors).forEach { match ->
            yield(ErrorAnalyzer.ErrorPart.Text(errors.substring(currentSearchingIndex, match.range.first)))
            yield(ErrorAnalyzer.ErrorPart.CodeLink(match.groupValues[2].toInt(), match.groupValues[4].toIntOrNull()))
            currentSearchingIndex = match.range.last + 1
        }

        yield(ErrorAnalyzer.ErrorPart.Text(errors.substring(currentSearchingIndex)))
    }
}

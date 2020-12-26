package language.errorAnalyzer

import language.errorAnalyzer.ErrorAnalyzer.ErrorPart
import org.jetbrains.annotations.Contract
import javax.inject.Inject


class KotlinAndSwiftErrorAnalyzer @Inject constructor() : ErrorAnalyzer {
    @Contract(pure = true)
    override fun splitIntoCodeParts(errors: String) = sequence {
        val regexToFindPathName = "(/[A-z0-9 ]+)*/?script\\.kts:(\\d+)(:(\\d+))?".toRegex()

        var currentSearchingIndex = 0
        regexToFindPathName.findAll(errors).forEach { match ->
            yield(ErrorPart.Text(errors.substring(currentSearchingIndex, match.range.first)))
            yield(ErrorPart.CodeLink(match.groupValues[2].toInt(), match.groupValues[4].toIntOrNull()))
            currentSearchingIndex = match.range.last + 1
        }

        yield(ErrorPart.Text(errors.substring(currentSearchingIndex)))
    }
}

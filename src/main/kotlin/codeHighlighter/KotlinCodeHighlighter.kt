package codeHighlighter

import org.intellij.lang.annotations.Language
import java.awt.Color

class KotlinCodeHighlighter(val keyWords: Map<String, Color>) {
    sealed class CodePart {
        data class Default(val code: String): CodePart()
        data class Keyword(val keyWord: String, val color: Color): CodePart()
    }

    fun highlight(@Language("kts") script: String) = sequence {
        var currentSearchingFromIndex = 0
        "[A-z0-9]+".toRegex().findAll(script)
            .filter { it.value in keyWords }
            .forEach { match ->
                yield(CodePart.Default(script.substring(currentSearchingFromIndex, match.range.first)))
                yield(CodePart.Keyword(match.value, keyWords[match.value] ?: throw IllegalStateException()))
                currentSearchingFromIndex = match.range.last + 1
            }

        yield(CodePart.Default(script.substring(currentSearchingFromIndex)))
    }

}

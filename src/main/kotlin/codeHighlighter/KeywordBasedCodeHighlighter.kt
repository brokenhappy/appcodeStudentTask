package codeHighlighter

import codeHighlighter.CodeHighlighter.CodePart
import org.intellij.lang.annotations.Language
import java.awt.Color

class KeywordBasedCodeHighlighter(val keyWords: Map<String, Color>) : CodeHighlighter {
    override fun highlight(script: String) = sequence {
        var currentSearchingFromIndex = 0
        "[A-z0-9]+".toRegex().findAll(script)
            .filter { it.value in keyWords }
            .forEach { match ->
                yield(CodePart.Default(script.substring(currentSearchingFromIndex, match.range.first)))
                yield(CodePart.Colored(match.value, keyWords[match.value] ?: throw IllegalStateException()))
                currentSearchingFromIndex = match.range.last + 1
            }

        yield(CodePart.Default(script.substring(currentSearchingFromIndex)))
    }
}

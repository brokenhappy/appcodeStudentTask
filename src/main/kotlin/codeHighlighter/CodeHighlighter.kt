package codeHighlighter

import org.intellij.lang.annotations.Language
import java.awt.Color

interface CodeHighlighter {
    sealed class CodePart {
        data class Default(val code: String): CodePart()
        data class Colored(val code: String, val color: Color): CodePart()
    }
    fun highlight(@Language("kts") script: String): Sequence<CodePart>
}
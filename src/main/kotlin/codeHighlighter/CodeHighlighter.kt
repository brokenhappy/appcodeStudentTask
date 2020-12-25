package codeHighlighter

import java.awt.Color

interface CodeHighlighter {
    sealed class CodePart {
        data class Default(val code: String) : CodePart()
        data class Colored(val code: String, val color: Color) : CodePart()
    }

    fun highlight(script: String): Sequence<CodePart>
}
package language.codeHighlighter

import org.jetbrains.annotations.Contract
import java.awt.Color

/**
 * This is a proof of concept. The code in this package is fully functional, but it is NOT implemented in the view
 * because text areas with colors mean picking a good library and implementing it in a way that works well while
 * editing the text. This sounds like a dangerous time sink which I'd rather not get into
 */
interface CodeHighlighter {
    sealed class CodePart {
        data class Default(val code: String) : CodePart()
        data class Colored(val code: String, val color: Color) : CodePart()
    }

    @Contract(pure = true)
    fun highlight(code: String): Sequence<CodePart>
}
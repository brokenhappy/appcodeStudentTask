package language.codeHighlighter

import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.Contract
import java.awt.Color
import javax.inject.Inject

class KotlinCodeHighlighter @Inject constructor() : CodeHighlighter {
    private val highlighter = KeywordBasedCodeHighlighter(mapOf(
        "package" to Color.ORANGE,
        "import" to Color.ORANGE,
        "class" to Color.ORANGE,
        "fun" to Color.ORANGE,
        "return" to Color.ORANGE,
        "val" to Color.ORANGE,
        "inline" to Color.ORANGE,
        "tailrec" to Color.ORANGE,
        "private" to Color.ORANGE,
        "override" to Color.ORANGE,
    ))

    @Contract(pure = true)
    override fun highlight(@Language("kts") code: String) = highlighter.highlight(code)
}
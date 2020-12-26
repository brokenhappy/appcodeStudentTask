package language.codeHighlighter

import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.Contract
import java.awt.Color
import javax.inject.Inject

class SwiftCodeHighlighter @Inject constructor() : CodeHighlighter {
    private val highlighter = KeywordBasedCodeHighlighter(mapOf(
        "Class" to Color.ORANGE,
        "deinit" to Color.ORANGE,
        "Enum" to Color.ORANGE,
        "extension" to Color.ORANGE,
        "Func" to Color.ORANGE,
        "import" to Color.ORANGE,
        "Init" to Color.ORANGE,
        "internal" to Color.ORANGE,
        "Let" to Color.ORANGE,
        "operator" to Color.ORANGE,
        "private" to Color.ORANGE,
        "protocol" to Color.ORANGE,
        "public" to Color.ORANGE,
        "static" to Color.ORANGE,
        "struct" to Color.ORANGE,
        "subscript" to Color.ORANGE,
        "typealias" to Color.ORANGE,
        "var" to Color.ORANGE,
    ))

    @Contract(pure = true)
    override fun highlight(@Language("swift") code: String) = highlighter.highlight(code)
}
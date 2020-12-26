package language.codeHighlighter

import language.codeHighlighter.CodeHighlighter.CodePart
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.awt.Color

internal class KeywordBasedCodeHighlighterTest {
    @Test
    fun `test empty script gives an empty default color part`() {
        assertEquals(
            listOf(CodePart.Default("")),
            KeywordBasedCodeHighlighter(mapOf()).highlight("").toList(),
        )
    }

    @Test
    fun `test script without keywords creates one default color part`() {
        assertEquals(
            listOf(CodePart.Default("123.toString()")),
            KeywordBasedCodeHighlighter(mapOf()).highlight("123.toString()").toList(),
        )
    }

    @Test
    fun `test if one keyword is in script, it is placed in between two default parts`() {
        assertEquals(
            listOf(
                CodePart.Default("123.toString(); "),
                CodePart.Colored("val", Color.ORANGE),
                CodePart.Default(" a = 3"),
            ),
            KeywordBasedCodeHighlighter(mapOf(
                "val" to Color.ORANGE
            )).highlight("123.toString(); val a = 3").toList(),
        )
    }

    @Test
    fun `test if one keyword is in part of word, should not highlight`() {
        assertEquals(
            listOf(CodePart.Default("123.evaluate()")),
            KeywordBasedCodeHighlighter(mapOf(
                "val" to Color.ORANGE
            )).highlight("123.evaluate()").toList(),
        )
    }
}
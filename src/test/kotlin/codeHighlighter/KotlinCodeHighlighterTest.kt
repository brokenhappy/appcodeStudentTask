package codeHighlighter

import codeHighlighter.KotlinCodeHighlighter.CodePart
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.Color

class KotlinCodeHighlighterTest {
    @Test
    fun `test empty script gives an empty default color part`() {
        assertEquals(
            listOf(CodePart.Default("")),
            KotlinCodeHighlighter(mapOf()).highlight("").toList(),
        )
    }

    @Test
    fun `test script without keywords creates one default color part`() {
        assertEquals(
            listOf(CodePart.Default("123.toString()")),
            KotlinCodeHighlighter(mapOf()).highlight("123.toString()").toList(),
        )
    }

    @Test
    fun `test if one keyword is in script, it is placed in between two default parts`() {
        assertEquals(
            listOf(
                CodePart.Default("123.toString(); "),
                CodePart.Keyword("val", Color.ORANGE),
                CodePart.Default(" a = 3"),
            ),
            KotlinCodeHighlighter(mapOf(
                "val" to Color.ORANGE
            )).highlight("123.toString(); val a = 3").toList(),
        )
    }

    @Test
    fun `test if one keyword is in part of word, should not highlight`() {
        assertEquals(
            listOf(CodePart.Default("123.evaluate()")),
            KotlinCodeHighlighter(mapOf(
                "val" to Color.ORANGE
            )).highlight("123.evaluate()").toList(),
        )
    }
}
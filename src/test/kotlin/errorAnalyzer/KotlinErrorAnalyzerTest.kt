package errorAnalyzer

import errorAnalyzer.KotlinErrorAnalyzer.ErrorChunk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinErrorAnalyzerTest {

    @Test
    fun `test empty error gives empty text`() {
        assertEquals(
            listOf(ErrorChunk.Text("")),
            KotlinErrorAnalyzer().analyze("test.kts", "").toList(),
        )
    }

    @Test
    fun `test one error that does not contain file path gives only one text blob`() {
        assertEquals(
            listOf(ErrorChunk.Text("error on line bla")),
            KotlinErrorAnalyzer().analyze("test.kts", "error on line bla").toList(),
        )
    }

    @Test
    fun `test if it contains file, it puts a link between texts`() {
        assertEquals(
            listOf(
                ErrorChunk.Text("error: unresolved reference: screee ("),
                ErrorChunk.CodeLink(1, 1),
                ErrorChunk.Text(")"),
            ),
            KotlinErrorAnalyzer().analyze(
                "foo.kts",
                "error: unresolved reference: screee (foo.kts:1:1)",
            ).toList()
        )
    }

    @Test
    fun `test if it contains file multiple times, it put links between texts`() {
        assertEquals(
            listOf(
                ErrorChunk.Text("error: unresolved reference: screee ("),
                ErrorChunk.CodeLink(1, 1),
                ErrorChunk.Text(") lol ("),
                ErrorChunk.CodeLink(3, 5),
                ErrorChunk.Text(")"),
            ),
            KotlinErrorAnalyzer().analyze(
                "foo.kts",
                "error: unresolved reference: screee (foo.kts:1:1) lol (foo.kts:3:5)",
            ).toList()
        )
    }

    @Test
    fun `test if file contains path prefix, it should be removed`() {
        assertEquals(
            listOf(
                ErrorChunk.Text(""),
                ErrorChunk.CodeLink(2, 9),
                ErrorChunk.Text(": error: unresolved reference: screee"),
            ),
            KotlinErrorAnalyzer().analyze(
                "foo.kts",
                "/projects/AppCodeStudentTask/foo.kts:2:9: error: unresolved reference: screee",
            ).toList()
        )
    }

    @Test
    fun `test link is also made if no column number is given`() {
        assertEquals(
            listOf(
                ErrorChunk.Text(""),
                ErrorChunk.CodeLink(2, null),
                ErrorChunk.Text(": error: unresolved reference: screee"),
            ),
            KotlinErrorAnalyzer().analyze(
                "foo.kts",
                "/projects/AppCodeStudentTask/foo.kts:2: error: unresolved reference: screee",
            ).toList()
        )
    }
}
package errorAnalyzer

import errorAnalyzer.KotlinErrorAnalyzer.ErrorPart
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class KotlinErrorAnalyzerTest {

    @Test
    fun `test empty error gives empty text`() {
        assertEquals(
            listOf(ErrorPart.Text("")),
            KotlinErrorAnalyzer().analyze("test.kts", "").toList(),
        )
    }

    @Test
    fun `test one error that does not contain file path gives only one text blob`() {
        assertEquals(
            listOf(ErrorPart.Text("error on line bla")),
            KotlinErrorAnalyzer().analyze("test.kts", "error on line bla").toList(),
        )
    }

    @Test
    fun `test if it contains file, it puts a link between texts`() {
        assertEquals(
            listOf(
                ErrorPart.Text("error: unresolved reference: screee ("),
                ErrorPart.CodeLink(1, 1),
                ErrorPart.Text(")"),
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
                ErrorPart.Text("error: unresolved reference: screee ("),
                ErrorPart.CodeLink(1, 1),
                ErrorPart.Text(") lol ("),
                ErrorPart.CodeLink(3, 5),
                ErrorPart.Text(")"),
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
                ErrorPart.Text(""),
                ErrorPart.CodeLink(2, 9),
                ErrorPart.Text(": error: unresolved reference: screee"),
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
                ErrorPart.Text(""),
                ErrorPart.CodeLink(2, null),
                ErrorPart.Text(": error: unresolved reference: screee"),
            ),
            KotlinErrorAnalyzer().analyze(
                "foo.kts",
                "/projects/AppCodeStudentTask/foo.kts:2: error: unresolved reference: screee",
            ).toList()
        )
    }
}
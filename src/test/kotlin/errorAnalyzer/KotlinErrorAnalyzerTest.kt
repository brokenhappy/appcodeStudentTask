package errorAnalyzer

import errorAnalyzer.ErrorAnalyzer.ErrorPart
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class KotlinErrorAnalyzerTest {

    @Test
    fun `test empty error gives empty text`() {
        assertEquals(
            listOf(ErrorPart.Text("")),
            KotlinErrorAnalyzer().splitIntoCodeParts("test.kts", "").toList(),
        )
    }

    @Test
    fun `test one error that does not contain file path gives only one text blob`() {
        assertEquals(
            listOf(ErrorPart.Text("error on line bla")),
            KotlinErrorAnalyzer().splitIntoCodeParts("test.kts", "error on line bla").toList(),
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
            KotlinErrorAnalyzer().splitIntoCodeParts(
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
            KotlinErrorAnalyzer().splitIntoCodeParts(
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
            KotlinErrorAnalyzer().splitIntoCodeParts(
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
            KotlinErrorAnalyzer().splitIntoCodeParts(
                "foo.kts",
                "/projects/AppCodeStudentTask/foo.kts:2: error: unresolved reference: screee",
            ).toList()
        )
    }
}
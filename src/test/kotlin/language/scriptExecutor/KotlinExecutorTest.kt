package language.scriptExecutor

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Tags
import org.junit.jupiter.api.Test
import tornadofx.isLong
import kotlin.math.absoluteValue

@Tag("slow")
internal class KotlinExecutorTest {
    private val subject = KotlinExecutor(KotlinCompileExecutingCommonWarningResolver(), TemporaryScriptFileProvider())

    @Test
    @Tag("slow")
    fun `test empty code provides no errors and no output`() {
        val errors = mutableListOf<String>()
        val output = mutableListOf<String>()

        subject.run(
            "",
            { output.add(it) },
            { errors.add(it) }
        )

        assertEmptyEnough(output, "empty code MUST not produce output")
        assertEmptyEnough(errors, "empty code MUST not produce errors")
    }

    @Test
    @Tag("slow")
    fun `test compile error provides error but no output`() {
        val errors = mutableListOf<String>()
        val output = mutableListOf<String>()

        subject.run(
            "1.nonExistentFunction()",
            { output.add(it) },
            { errors.add(it) }
        )

        assertEquals(5, errors.size)
        assertTrue(errors.first().trim().startsWith("error: unresolved reference: nonExistentFunction ("))
        assertEmptyEnough(output, "code MUST not produce output")
    }

    @Test
    @Tag("slow")
    fun `test print statement goes to output`() {
        val errors = mutableListOf<String>()
        val output = mutableListOf<String>()

        subject.run(
            """println("test")""",
            { output.add(it) },
            { errors.add(it) }
        )

        assertEquals(listOf("test", ""), output)
        assertEmptyEnough(errors, "code MUST not produce errors")
    }

    @Test
    @Tags(Tag("slow"), Tag("unstable"), Tag("DependsOnSystemTime"))
    fun `test that events are raised during execution`() {
        subject.run(
            """
                println(System.currentTimeMillis())
                Thread.sleep(500)
                println(System.currentTimeMillis())
                Thread.sleep(500)
            """,
            { outputLine ->
                if (!outputLine.isLong())
                    return@run
                assertNowIsCloseEnoughTo(outputLine.toLong())
            },
            { }
        )
    }

    private infix fun Long.difference(other: Long) = (this - other).absoluteValue

    private fun assertNowIsCloseEnoughTo(timeItShouldBeNowAsTimestamp: Long) {
        val differenceBetweenNowAndWhatItShouldBe = timeItShouldBeNowAsTimestamp difference System.currentTimeMillis()
        assertTrue(
            differenceBetweenNowAndWhatItShouldBe < 100,
            "time difference should have been less then 100ms, but was $differenceBetweenNowAndWhatItShouldBe"
        )
    }

    @Test
    @Tag("DependsOnSystemTime")
    fun `test assertion for timing`() {
        assertThrows(AssertionError::class.java) { assertNowIsCloseEnoughTo(0) }
        assertThrows(AssertionError::class.java) { assertNowIsCloseEnoughTo(System.currentTimeMillis() - 200) }
        assertNowIsCloseEnoughTo(System.currentTimeMillis())
    }

    private fun assertEmptyEnough(streamResult: Collection<String>, message: String) {
        assertTrue(
            streamResult.isEmpty() || (streamResult.size == 1 && streamResult.first().isEmpty()),
            "$message (was: $streamResult)"
        )
    }

    @Test
    @Tag("DependsOnSystemTime")
    fun `test assertion to check wether a stream is empty enough to be considered 'an empty stream'`() {
        assertThrows(AssertionError::class.java) { assertEmptyEnough(listOf("not empty"), "") }
        assertThrows(AssertionError::class.java) { assertEmptyEnough(listOf("", ""), "") }
        assertEmptyEnough(listOf(""), "")
        assertEmptyEnough(listOf(), "")
    }
}


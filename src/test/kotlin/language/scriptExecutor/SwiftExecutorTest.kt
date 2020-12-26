package language.scriptExecutor

import org.junit.jupiter.api.*
import tornadofx.isLong
import kotlin.math.absoluteValue

internal class SwiftExecutorTest {
    private val subject = SwiftExecutor(TemporaryScriptFileProvider())

    @Test
    @Tag("slow")
    fun `test empty code provides no errors and no output (ignoring live support notice)`() {
        val errors = mutableListOf<String>()
        val output = mutableListOf<String>()

        subject.run(
            "",
            { output.add(it) },
            { errors.add(it) }
        )

        assertEmptyEnough(output.drop(2), "empty code MUST not produce output")
        assertEmptyEnough(errors, "empty code MUST not produce errors")
    }

    @Test
    @Tag("slow")
    fun `test compile error provides error but no output (ignoring live support notice)`() {
        val errors = mutableListOf<String>()
        val output = mutableListOf<String>()

        subject.run(
            "nonExistentFunction()",
            { output.add(it) },
            { errors.add(it) }
        )

        Assertions.assertEquals(4, errors.size)
        Assertions.assertTrue("error: use of unresolved identifier 'nonExistentFunction'" in errors.first())
        assertEmptyEnough(output.drop(2), "code MUST not produce output")
    }

    @Test
    @Tag("slow")
    fun `test print statement goes to output (ignoring live support notice)`() {
        val errors = mutableListOf<String>()
        val output = mutableListOf<String>()

        subject.run(
            """print("test")""",
            { output.add(it) },
            { errors.add(it) }
        )

        Assertions.assertEquals(listOf("test", ""), output.drop(2))
        assertEmptyEnough(errors, "code MUST not produce errors")
    }

    @Test
    @Tags(Tag("slow"), Tag("unstable"), Tag("DependsOnSystemTime"))
    @Disabled("Swift process blocks its streams, redirecting to file or other methods havent worked so far. Thus this behaviour is currently not included")
    fun `test that events are raised during execution`() {
        subject.run(
            """
                import Foundation

                print(Int(Date().timeIntervalSince1970 * 1000));
                sleep(10)
                print(Int(Date().timeIntervalSince1970 * 1000));
                sleep(10)
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
        Assertions.assertTrue(
            differenceBetweenNowAndWhatItShouldBe < 100,
            "time difference should have been less then 100ms, but was $differenceBetweenNowAndWhatItShouldBe"
        )
    }

    @Test
    @Tag("DependsOnSystemTime")
    fun `test assertion for timing`() {
        Assertions.assertThrows(AssertionError::class.java) { assertNowIsCloseEnoughTo(0) }
        Assertions.assertThrows(AssertionError::class.java) { assertNowIsCloseEnoughTo(System.currentTimeMillis() - 200) }
        assertNowIsCloseEnoughTo(System.currentTimeMillis())
    }

    private fun assertEmptyEnough(streamResult: Collection<String>, message: String) {
        Assertions.assertTrue(
            streamResult.isEmpty() || (streamResult.size == 1 && streamResult.first().isEmpty()),
            "$message (was: $streamResult)"
        )
    }

    @Test
    @Tag("DependsOnSystemTime")
    fun `test assertion to check whether a stream is empty enough to be considered 'an empty stream'`() {
        Assertions.assertThrows(AssertionError::class.java) { assertEmptyEnough(listOf("not empty"), "") }
        Assertions.assertThrows(AssertionError::class.java) { assertEmptyEnough(listOf("", ""), "") }
        assertEmptyEnough(listOf(""), "")
        assertEmptyEnough(listOf(), "")
    }
}
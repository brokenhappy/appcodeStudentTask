package multiExecutor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import testDouble.FakeExecutionTimeMeasurer
import testDouble.MultiExecutableSpy

internal class DefaultMultiExecutorTest {
    @Test
    fun `test if executes 0 times, its only started and ended`() {
        val spy = MultiExecutableSpy()

        DefaultMultiExecutor(FakeExecutionTimeMeasurer()).execute(0, spy)

        assertEquals(1, spy.onStartCalls)
        assertEquals(1, spy.onEndCalls)
        assertEquals(0, spy.executeCalls)
        assertTrue(spy.onProgressBarUpdateCalls.isEmpty())
    }

    @Test
    fun `test if executes 3 times, its only started and ended once, and executed 3 times and updated twice`() {
        val spy = MultiExecutableSpy()

        DefaultMultiExecutor(FakeExecutionTimeMeasurer(0, 0)).execute(3, spy)

        assertEquals(1, spy.onStartCalls)
        assertEquals(1, spy.onEndCalls)
        assertEquals(3, spy.executeCalls)
        assertEquals(2, spy.onProgressBarUpdateCalls.size)
    }

    @Test
    fun `test if it takes just as long each time, the estimated time always decrements at the same rate`() {
        val spy = MultiExecutableSpy()

        DefaultMultiExecutor(FakeExecutionTimeMeasurer(100, 100, 100, 100)).execute(5, spy)

        assertEquals(
            listOf(400, 300, 200, 100),
            spy.onProgressBarUpdateCalls.map { it.estimatedTimeLeftMs }
        )
    }

    @Test
    fun `test if first time takes very long, and all next times take shorter, estimated time left goes down`() {
        val spy = MultiExecutableSpy()

        DefaultMultiExecutor(FakeExecutionTimeMeasurer(10000, 100, 100, 100)).execute(5, spy)

        val (first, second, third, fourth) = spy.onProgressBarUpdateCalls
            .map { it.estimatedTimeLeftMs }
        assertTrue(first > second)
        assertTrue(second > third)
        assertTrue(third > fourth)
    }

    @Test
    fun `test progress increases with same steps no matter the time taken`() {
        val spy = MultiExecutableSpy()

        DefaultMultiExecutor(FakeExecutionTimeMeasurer(123, 51325, 1235123, 123515, 1552, 1235, 123))
            .execute(8, spy)

        assertEquals(
            listOf(1 / 8.0, 2 / 8.0, 3 / 8.0, 4 / 8.0, 5 / 8.0, 6 / 8.0, 7 / 8.0),
            spy.onProgressBarUpdateCalls.map { it.progress }
        )
    }

    @Test
    fun `test progress update correctly updates iteration count with steps of 1, starting at 1`() {
        val spy = MultiExecutableSpy()

        DefaultMultiExecutor(FakeExecutionTimeMeasurer(123, 51325, 1235123, 123515, 1552, 1235, 123))
            .execute(8, spy)

        assertEquals(
            listOf(1, 2, 3, 4, 5, 6, 7),
            spy.onProgressBarUpdateCalls.map { it.iterationCount }
        )
    }
}
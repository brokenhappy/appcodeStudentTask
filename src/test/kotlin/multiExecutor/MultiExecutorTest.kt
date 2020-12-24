package multiExecutor

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import testDouble.FakeExecutionTimeMeasurer
import testDouble.MultiExecutableSpy

class MultiExecutorTest {

    @Test
    fun `test if executes 0 times, its only started and ended`() {
        val spy = MultiExecutableSpy()

        MultiExecutor(FakeExecutionTimeMeasurer()).execute(0, spy)

        assertEquals(1, spy.onStartCalls)
        assertEquals(1, spy.onEndCalls)
        assertEquals(0, spy.executeCalls)
        assertTrue(spy.onProgressBarUpdateCalls.isEmpty())
    }

    @Test
    fun `test if executes 3 times, its only started and ended once, and executed 3 times and updated twice`() {
        val spy = MultiExecutableSpy()

        MultiExecutor(FakeExecutionTimeMeasurer(0, 0)).execute(3, spy)

        assertEquals(1, spy.onStartCalls)
        assertEquals(1, spy.onEndCalls)
        assertEquals(3, spy.executeCalls)
        assertEquals(2, spy.onProgressBarUpdateCalls.size)
    }

    @Test
    fun `test if it takes just as long each time, the estimated time always decrements at the same rate`() {
        val spy = MultiExecutableSpy()

        MultiExecutor(FakeExecutionTimeMeasurer(100, 100, 100, 100)).execute(5, spy)

        assertEquals(
            listOf(400, 300, 200, 100),
            spy.onProgressBarUpdateCalls.map { (_, estimatedTime) -> estimatedTime }
        )
    }

    @Test
    fun `test if first time takes very long, and all next times take shorter, estimated time left goes down`() {
        val spy = MultiExecutableSpy()

        MultiExecutor(FakeExecutionTimeMeasurer(10000, 100, 100, 100)).execute(5, spy)

        val (first, second, third, fourth) = spy.onProgressBarUpdateCalls
            .map { (_, estimatedTime) -> estimatedTime }
        assertTrue(first > second)
        assertTrue(second > third)
        assertTrue(third > fourth)
    }
}
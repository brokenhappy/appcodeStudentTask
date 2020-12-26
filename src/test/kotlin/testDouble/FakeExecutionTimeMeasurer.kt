package testDouble

import multiExecutor.ExecutionTimeMeasurer

internal class FakeExecutionTimeMeasurer(vararg fakeTimesInMs: Int) : ExecutionTimeMeasurer {
    private val fakeTimes = fakeTimesInMs.iterator()
    override fun measure(runnable: Runnable): Int {
        runnable.run()
        return fakeTimes.next()
    }
}

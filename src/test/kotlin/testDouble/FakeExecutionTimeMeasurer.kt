package testDouble

import multiExecutor.ExecutionTimeMeasurer

class FakeExecutionTimeMeasurer(vararg fakeTimesInMs: Int) : ExecutionTimeMeasurer {
    private val fakeTimes = fakeTimesInMs.iterator()
    override fun measureTime(runnable: Runnable): Int {
        runnable.run()
        return fakeTimes.next()
    }
}

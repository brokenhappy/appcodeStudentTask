package multiExecutor

import kotlin.system.measureTimeMillis

class StandardLibExecutionTimeMeasurer : ExecutionTimeMeasurer {
    override fun measureTime(runnable: Runnable) = measureTimeMillis { runnable.run() }.toInt()
}
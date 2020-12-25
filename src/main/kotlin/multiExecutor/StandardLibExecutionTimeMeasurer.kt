package multiExecutor

import javax.inject.Inject
import kotlin.system.measureTimeMillis

class StandardLibExecutionTimeMeasurer @Inject constructor() : ExecutionTimeMeasurer {
    override fun measure(runnable: Runnable) = measureTimeMillis { runnable.run() }.toInt()
}
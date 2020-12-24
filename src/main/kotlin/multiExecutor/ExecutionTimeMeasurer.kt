package multiExecutor

interface ExecutionTimeMeasurer {
    fun measureTime(runnable: Runnable): Int
}

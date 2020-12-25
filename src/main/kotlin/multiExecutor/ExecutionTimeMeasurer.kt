package multiExecutor

interface ExecutionTimeMeasurer {
    fun measure(runnable: Runnable): Int
}

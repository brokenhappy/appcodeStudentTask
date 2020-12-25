package multiExecutor

interface MultiExecutor {
    /** Assume all functions are called from worker threads */
    interface MultiExecutable {
        data class OnProgressUpdateEvent(val progress: Double, val estimatedTimeLeftMs: Int, val iterationCount: Int)
        fun onStart()
        fun execute()
        fun onEnd()
        fun onProgressUpdate(event: OnProgressUpdateEvent)
    }

    fun execute(times: Int, executable: MultiExecutable)
}
package multiExecutor

class MultiExecutor(val executionTimeMeasurer: ExecutionTimeMeasurer) {
    /** Assume all functions are called from worker threads */
    interface MultiExecutable {
        fun onStart()
        fun execute()
        fun onEnd()
        fun onProgressUpdate(progress: Double, estimatedTimeLeftMs: Int)
    }

    private class TimeLeftEstimator {
        private val previousTimes = mutableListOf<Int>()
        fun addTime(time: Int) {
            previousTimes.add(time)
        }

        fun averageTimeBiasedTowardsLast(): Double {
            val weights = (previousTimes.size downTo 1).map { 1.0 / it }
            val total = weights.sum()

            return weights.zip(previousTimes).fold(0.0) { acc, (weight, time) ->
                acc + weight * time
            } / total
        }
    }

    fun execute(times: Int, executable: MultiExecutable) {
        executable.onStart()
        if (times == 0) {
            executable.onEnd()
            return
        }

        val timeEstimator = TimeLeftEstimator()
        (0 until times - 1).forEach { i ->
            timeEstimator.addTime(executionTimeMeasurer.measureTime { executable.execute() })
            executable.onProgressUpdate(
                i / times.toDouble(),
                (timeEstimator.averageTimeBiasedTowardsLast() * (times - i - 1)).toInt(),
            )
        }

        executable.execute()
        executable.onEnd()
    }
}

package multiExecutor

import org.jetbrains.annotations.Contract
import javax.inject.Inject

class DefaultMultiExecutor @Inject constructor(private val executionTimeMeasurer: ExecutionTimeMeasurer) :
    MultiExecutor {

    private class TimeLeftEstimator {
        private val previousTimes = mutableListOf<Int>()
        fun addTime(time: Int) {
            previousTimes.add(time)
        }

        @Contract(pure = true)
        fun averageTimeBiasedTowardsLast(): Double {
            val weights = (previousTimes.size downTo 1).map { 1.0 / it }
            val total = weights.sum()

            return weights.zip(previousTimes).fold(0.0) { acc, (weight, time) ->
                acc + weight * time
            } / total
        }
    }

    override fun execute(times: Int, executable: MultiExecutor.MultiExecutable) {
        executable.onStart()
        if (times == 0) {
            executable.onEnd()
            return
        }

        val timeEstimator = TimeLeftEstimator()
        (1 until times).forEach { i ->
            timeEstimator.addTime(executionTimeMeasurer.measure { executable.execute() })
            executable.onProgressUpdate(MultiExecutor.MultiExecutable.OnProgressUpdateEvent(
                progress = i / times.toDouble(),
                estimatedTimeLeftMs = (timeEstimator.averageTimeBiasedTowardsLast() * (times - i)).toInt(),
                iterationCount = i,
            ))
        }

        executable.execute()
        executable.onEnd()
    }
}

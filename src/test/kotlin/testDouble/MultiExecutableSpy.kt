package testDouble

import multiExecutor.MultiExecutor

class MultiExecutableSpy : MultiExecutor.MultiExecutable {
    var onStartCalls = 0
        get() = field
        private set(new) {
            field = new
        }
    var executeCalls = 0
        get() = field
        private set(new) {
            field = new
        }
    var onEndCalls = 0
        get() = field
        private set(new) {
            field = new
        }

    private val _onProgressBarUpdateCalls = mutableListOf<Pair<Double, Int>>()
    val onProgressBarUpdateCalls: List<Pair<Double, Int>> get() = _onProgressBarUpdateCalls

    override fun onStart() {
        onStartCalls++
    }


    override fun execute() {
        executeCalls++
    }

    override fun onEnd() {
        onEndCalls++
    }

    override fun onProgressUpdate(progress: Double, estimatedTimeLeftMs: Int) {
        _onProgressBarUpdateCalls.add(progress to estimatedTimeLeftMs)
    }

}
package testDouble

import multiExecutor.MultiExecutor
import multiExecutor.MultiExecutor.MultiExecutable.OnProgressUpdateEvent

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

    private val _onProgressBarUpdateCalls = mutableListOf<OnProgressUpdateEvent>()
    val onProgressBarUpdateCalls get() = _onProgressBarUpdateCalls.toList()

    override fun onStart() {
        onStartCalls++
    }


    override fun execute() {
        executeCalls++
    }

    override fun onEnd() {
        onEndCalls++
    }

    override fun onProgressUpdate(event: OnProgressUpdateEvent) {
        _onProgressBarUpdateCalls.add(event)
    }

}
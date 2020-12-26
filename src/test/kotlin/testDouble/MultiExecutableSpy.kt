package testDouble

import multiExecutor.MultiExecutor
import multiExecutor.MultiExecutor.MultiExecutable.OnProgressUpdateEvent

internal class MultiExecutableSpy : MultiExecutor.MultiExecutable {
    var onStartCalls = 0
        private set
    var executeCalls = 0
        private set
    var onEndCalls = 0
        private set

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
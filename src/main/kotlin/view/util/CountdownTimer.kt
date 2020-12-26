package view.util

import javafx.beans.property.StringProperty
import java.util.*

class CountdownTimer(private val textProperty: StringProperty) {
    private var timer = Timer()
    private var timeLeftDeciSeconds = 0

    fun setTime(timeLeftMs: Int) {
        timeLeftDeciSeconds = (timeLeftMs / 100) + 1
        timer.cancel()
        timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    if (--timeLeftDeciSeconds <= 0)
                        return
                    updateText()
                }
            },
            timeLeftMs % 100L,
            100L,
        )
    }

    fun cancel() = timer.cancel()

    private fun updateText() {
        textProperty.set("Time left: %.1f seconds".format(timeLeftDeciSeconds / 10.0))
    }
}
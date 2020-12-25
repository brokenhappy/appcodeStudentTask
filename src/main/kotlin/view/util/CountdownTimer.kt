package view.util

import javafx.beans.property.StringProperty
import java.util.*

class CountdownTimer(private val textProperty: StringProperty) {
    private var timer = Timer()
    private var timeLeftSeconds = 0

    fun setTime(timeLeftMs: Int) {
        timeLeftSeconds = (timeLeftMs / 1000) + 1
        timer.cancel()
        timer = Timer()
        timer.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    if (--timeLeftSeconds <= 0)
                        return
                    updateText()
                }
            },
            timeLeftMs % 1000L,
            1000L,
        )
    }

    fun cancel() = timer.cancel()

    private fun updateText() {
        textProperty.set("Time left: $timeLeftSeconds seconds")
    }
}
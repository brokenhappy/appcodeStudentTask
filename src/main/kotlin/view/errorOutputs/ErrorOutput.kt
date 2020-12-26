package view.errorOutputs

import language.errorAnalyzer.ErrorAnalyzer.ErrorPart.CodeLink
import javafx.scene.control.ScrollPane

interface ErrorOutput {
    fun attachTo(scrollPane: ScrollPane)
    fun addLine(line: String, onLinkClick: (CodeLink) -> Unit)
    fun addRunSeparator(separationCount: Int)
    fun clear()
}
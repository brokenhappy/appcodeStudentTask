package view.errorOutputs

import errorAnalyzer.ErrorAnalyzer.ErrorPart
import errorAnalyzer.KotlinErrorAnalyzer
import javafx.scene.control.Hyperlink
import javafx.scene.control.ScrollPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import tornadofx.addChildIfPossible
import tornadofx.style
import javax.inject.Inject

class TextFlowErrorOutput @Inject constructor(val errorAnalyzer: KotlinErrorAnalyzer) : ErrorOutput {
    private val textFlow = TextFlow()

    override fun attachTo(scrollPane: ScrollPane) {
        scrollPane.addChildIfPossible(textFlow)
        ensureParentScrollDownWhenContentGrows(scrollPane)
    }

    private fun ensureParentScrollDownWhenContentGrows(scrollPane: ScrollPane) {
        scrollPane.vvalueProperty().bind(textFlow.heightProperty())
    }

    override fun addLine(line: String, onLinkClick: (ErrorPart.CodeLink) -> Unit) {
        textFlow.children.addAll(
            errorAnalyzer.splitIntoCodeParts("foo.kts", line)
                .filter { !(it is ErrorPart.Text && it.text.isEmpty()) }
                .map { errorPart -> mapErrorPartToTextFlowChild(errorPart, onLinkClick) }
                + Text("\n")
        )
    }

    private fun mapErrorPartToTextFlowChild(errorPart: ErrorPart, onLinkClick: (ErrorPart.CodeLink) -> Unit) =
        when (errorPart) {
            is ErrorPart.CodeLink -> Hyperlink("foo.kts:$errorPart").also { link ->
                link.style {
                    textFill = Color.BLUE
                }
                link.setOnMouseClicked { onLinkClick(errorPart) }
            }
            is ErrorPart.Text -> Text(errorPart.text)
        }

    override fun addRunSeparator(separationCount: Int) {
        textFlow.children += Text("--------------------- Run: ${separationCount + 1}\n")
    }

    override fun clear() = textFlow.children.clear()
}
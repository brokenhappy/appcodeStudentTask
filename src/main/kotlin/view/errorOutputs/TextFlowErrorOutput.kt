package view.errorOutputs

import language.errorAnalyzer.ErrorAnalyzer.ErrorPart
import language.errorAnalyzer.KotlinAndSwiftErrorAnalyzer
import javafx.scene.control.Hyperlink
import javafx.scene.control.ScrollPane
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import org.jetbrains.annotations.Contract
import tornadofx.addChildIfPossible
import tornadofx.style
import javax.inject.Inject

class TextFlowErrorOutput @Inject constructor(val errorAnalyzer: KotlinAndSwiftErrorAnalyzer) : ErrorOutput {
    private val textFlow = TextFlow()

    override fun attachTo(scrollPane: ScrollPane) {
        scrollPane.addChildIfPossible(textFlow)
        ensureParentScrollsDownWhenContentGrows(scrollPane)
    }

    private fun ensureParentScrollsDownWhenContentGrows(scrollPane: ScrollPane) {
        scrollPane.vvalueProperty().bind(textFlow.heightProperty())
    }

    override fun addLine(line: String, onLinkClick: (ErrorPart.CodeLink) -> Unit) {
        textFlow.children.addAll(
            errorAnalyzer.splitIntoCodeParts(line)
                .filter { !(it is ErrorPart.Text && it.text.isEmpty()) }
                .map { errorPart -> mapErrorPartToTextFlowChild(errorPart, onLinkClick) }
                + Text("\n")
        )
    }

    @Contract(pure = true)
    private fun mapErrorPartToTextFlowChild(errorPart: ErrorPart, onLinkClick: (ErrorPart.CodeLink) -> Unit) =
        when (errorPart) {
            is ErrorPart.CodeLink -> Hyperlink("script:$errorPart").also { link ->
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
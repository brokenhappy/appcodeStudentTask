package View

import errorAnalyzer.ErrorAnalyzer.ErrorPart
import errorAnalyzer.KotlinErrorAnalyzer
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import scriptExecutor.DaggerScriptExecutorComponent
import scriptExecutor.ExitCode
import tornadofx.*

class EditorView : View() {
    private val kotlinExecutor = DaggerScriptExecutorComponent.create().getKotlinInstance()
    private val errorAnalyzer = KotlinErrorAnalyzer()

    private lateinit var codeOutput: Text
    private lateinit var executeButton: Button
    private lateinit var codeInput: TextArea
    private lateinit var errorOutput: TextFlow
    private lateinit var codeStatus: Text

    override val root = vbox {
        style {
            font = Font.font(java.awt.Font.MONOSPACED)
        }
        splitpane {
            codeInput = textarea("// write your code here")
            codeOutput = text("// read output here")
        }
        hbox {
            vbox {
                executeButton = button("Execute!") {
                    setOnMouseClicked { runScript() }
                }
                codeStatus = text()
            }
            errorOutput = textflow()
        }
    }

    private fun TextArea.setCursorAt(link: ErrorPart.CodeLink) {
        requestFocus()
        positionCaret(link.resolveIndexIn(text))
    }

    private fun runScript() {
        executeButton.isDisable = true
        codeStatus.text = "Code running.."
        codeOutput.text = ""
        errorOutput.clear()

        Thread {
            val exitCode = kotlinExecutor.run(
                codeInput.text.toString(),
                { outputLine -> Platform.runLater { codeOutput.text += outputLine + "\n" } },
                { errorLine ->
                    Platform.runLater {
                        errorOutput.children.addAll(generateNodesForErrorLine(errorAnalyzer, errorLine))
                    }
                }
            )
            Platform.runLater {
                codeStatus.text = getStatusMessageFor(exitCode)
                executeButton.isDisable = false
            }
        }.start()
    }

    private fun generateNodesForErrorLine(errorAnalyzer: KotlinErrorAnalyzer, errorLine: String) =
        errorAnalyzer.analyze("foo.kts", errorLine) // TODO: inject temp file name
            .filter { !(it is ErrorPart.Text && it.text.isEmpty()) }
            .map { errorPart ->
                when (errorPart) {
                    is ErrorPart.CodeLink -> hyperlink("foo.kts:$errorPart") { // TODO: inject temp file name
                        style {
                            textFill = Color.BLUE
                        }
                        setOnMouseClicked { codeInput.setCursorAt(errorPart) }
                    }
                    is ErrorPart.Text -> text(errorPart.text)
                }
            } + Text("\n")

    private fun getStatusMessageFor(exitCode: ExitCode) =
        if (exitCode.isNonZero())
            "last exit code:\n $exitCode"
        else
            ""
}
package View

import errorAnalyzer.KotlinErrorAnalyzer
import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import scriptExecutor.ExitCode
import scriptExecutor.KotlinCompileCommand
import scriptExecutor.KotlinCompileExecutingCommonWarningResolver
import scriptExecutor.KotlinExecutor
import tornadofx.*

class EditorView : View() {
    private val kotlinExecutor: KotlinExecutor
    private val errorAnalyzer = KotlinErrorAnalyzer()

    init {
        val commandProvider = KotlinCompileCommand("/Users/woutwerkman/.sdkman/candidates/kotlin/current/bin/kotlinc")
        kotlinExecutor = KotlinExecutor(KotlinCompileExecutingCommonWarningResolver(commandProvider), commandProvider)
    }

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

    private fun TextArea.setCursorAt(lineNumber: Int, column: Int) {
        val indexAtStartOfLine = text.split("\n").asSequence()
            .runningFold(0) { acc, line -> acc + line.length + 1 }
            .drop(lineNumber - 1)
            .first()

        requestFocus()
        positionCaret(indexAtStartOfLine + column - 1)
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
            .filter { !(it is KotlinErrorAnalyzer.ErrorChunk.Text && it.text.isEmpty()) }
            .map { errorChunk ->
                when (errorChunk) {
                    is KotlinErrorAnalyzer.ErrorChunk.CodeLink -> hyperlink("foo.kts:$errorChunk") {
                        style {
                            textFill = Color.BLUE
                        }
                        setOnMouseClicked { codeInput.setCursorAt(errorChunk.line, errorChunk.column ?: 1) }
                    }
                    is KotlinErrorAnalyzer.ErrorChunk.Text -> text(errorChunk.text)
                }
            } + Text("\n")

    private fun getStatusMessageFor(exitCode: ExitCode) =
        if (exitCode.isNonZero())
            "last exit code:\n $exitCode"
        else
            ""
}
package View

import javafx.application.Platform
import javafx.scene.control.Button
import javafx.scene.control.TextArea
import javafx.scene.text.Text
import scriptExecutor.KotlinCompileCommand
import scriptExecutor.KotlinCompileExecutingCommonWarningResolver
import scriptExecutor.KotlinExecutor
import tornadofx.*

class EditorView : View() {
    private val kotlinExecutor: KotlinExecutor

    init {
        val commandProvider = KotlinCompileCommand("/Users/woutwerkman/.sdkman/candidates/kotlin/current/bin/kotlinc")
        kotlinExecutor = KotlinExecutor(KotlinCompileExecutingCommonWarningResolver(commandProvider), commandProvider)
    }

    private lateinit var codeOutput: Text
    private lateinit var executeButton: Button
    private lateinit var codeInput: TextArea
    private lateinit var errorOutput: Text
    private lateinit var codeStatus: Text

    override val root = vbox {
        splitpane() {
            codeInput = textarea("// write your code here")
            codeOutput = text("// read output here")
        }
        hbox {
            vbox {
                executeButton = button("Execute!")
                    .apply { setOnMouseClicked { runScript() } }
                codeStatus = text()
            }
            errorOutput = text()
        }
    }

    private fun runScript() {
        executeButton.isDisable = true
        codeStatus.text = "Code running.."
        codeOutput.text = ""
        errorOutput.text = ""

        Thread {
            val exitCode = kotlinExecutor.runAndGetExitCode(
                codeInput.text.toString(),
                { outputLine -> Platform.runLater { codeOutput.text += outputLine + "\n" } },
                { errorLine -> Platform.runLater { errorOutput.text += errorLine + "\n" } }
            )
            Platform.runLater {
                codeStatus.text = getStatusMessageFor(exitCode)
                executeButton.isDisable = false
            }
        }.start()
    }

    private fun getStatusMessageFor(exitCode: Int) =
        if (exitCode != 0)
            "last exit code:\n $exitCode"
        else
            ""
}
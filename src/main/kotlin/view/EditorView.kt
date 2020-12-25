package view

import errorAnalyzer.ErrorAnalyzer.ErrorPart
import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.util.Duration
import javafx.util.converter.NumberStringConverter
import multiExecutor.DaggerMultiExecutorComponent
import multiExecutor.MultiExecutor
import multiExecutor.MultiExecutor.MultiExecutable.OnProgressUpdateEvent
import scriptExecutor.DaggerScriptExecutorComponent
import scriptExecutor.ExitCode
import tornadofx.*
import view.errorOutputs.DaggerErrorOutputComponent

/**
 * I'm the least proud of this view package. The view code is the only part I did NOT write TDD and thus was the
 * most time consuming part.
 * I am aware of fallacies such as my usage of callbacks instead of passing around Observables like JavaFx is built for.
 * Frontend is NOT my strongest aspect and I kindly ask not to pay too much attention to the visuals, the
 * not-so JavaFx treatment of state and the lack of tests verifying view logic.
 *
 * I truly wish I knew how to write well-tested frontend code, especially in TDD.
 * Please, dont hesitate to give me advice and feedback, but evaluate my skills based on the "backend" ;)
 */
class EditorView : View() {
    private val kotlinExecutor = DaggerScriptExecutorComponent.create().getKotlinInstance()
    private val multiExecutor = DaggerMultiExecutorComponent.create().getInstance()
    private val errorOutput = DaggerErrorOutputComponent.create().getInstance()

    private lateinit var codeOutput: TextFlow
    private lateinit var executeButton: Button
    private lateinit var codeInput: TextArea
    private lateinit var codeProgress: ProgressBar
    private lateinit var expectedTimeLeft: Text
    private lateinit var codeStatus: TextField

    override val root = splitpane(Orientation.VERTICAL) {
        style {
            font = Font.font(java.awt.Font.MONOSPACED, 10.0)
        }
        splitpane {
            setDividerPosition(0, 0.5)
            codeInput = textarea("""
                repeat(5) {
                    println(Math.random())
                    Thread.sleep(100)
                }
                "".toInt()
            """.trimIndent())
            scrollpane {
                codeOutput = textflow()
                vvalueProperty().bind(codeOutput.heightProperty())
            }
        }
        splitpane {
            setDividerPosition(0, 0.2)
            vbox {
                executeButton = button("Execute!") { setOnMouseClicked { runScript() } }
                label("how often do you want to execute?")
                codeStatus = textfield {
                    textFormatter = TextFormatter(NumberStringConverter())
                    text = "1"
                }
                codeProgress = progressbar { opacity = 0.0 }
                expectedTimeLeft = text()
            }
            scrollpane {
                errorOutput.attachTo(this)
            }
        }
    }

    private val runProcess = object : MultiExecutor.MultiExecutable {
        override fun onStart() = Platform.runLater {
            executeButton.isDisable = true
            codeProgress.progress = 0.0
            codeProgress.opacity = 1.0
            expectedTimeLeft.opacity = 1.0
            expectedTimeLeft.text = "Executing.."
            codeOutput.clear()
            errorOutput.clear()
        }

        override fun execute() {
            val exitCode = kotlinExecutor.run(
                kotlinScript = codeInput.text.toString(),
                inputEvent = { outputLine -> Platform.runLater { codeOutput.children += Text(outputLine + "\n") } },
                errorEvent = { errorLine ->
                    Platform.runLater {
                        errorOutput.addLine(
                            line = errorLine,
                            onLinkClick = { codeLink ->
                                codeInput.setCursorAt(codeLink)
                            },
                        )
                    }
                }
            )
            Platform.runLater {
                expectedTimeLeft.text = getStatusMessageFor(exitCode)
            }
        }

        override fun onEnd() = Platform.runLater {
            executeButton.isDisable = false
            codeProgress.progress = 1.0
            codeProgress.fade(Duration.seconds(1.0), 0.0)
        }

        override fun onProgressUpdate(event: OnProgressUpdateEvent) = Platform.runLater {
            codeOutput.children += Text("--------------------- Run: ${event.iterationCount + 1}\n")
            errorOutput.addRunSeparator(event.iterationCount)
            codeProgress.progress = event.progress
            expectedTimeLeft.text = "About %.2f seconds left".format(event.estimatedTimeLeftMs / 1000.0)
        }
    }

    private fun TextArea.setCursorAt(link: ErrorPart.CodeLink) {
        requestFocus()
        positionCaret(link.resolveIndexIn(text))
    }

    private fun runScript() {
        Thread {
            val numberOfTimesToExecute = codeStatus.text
                .replace("[,.]".toRegex(), "")
                .toIntOrNull() ?: 1
            multiExecutor.execute(numberOfTimesToExecute, runProcess)
        }.start()
    }

    private fun getStatusMessageFor(exitCode: ExitCode) =
        if (exitCode.isNonZero())
            "Last exit code:\n $exitCode"
        else
            "Last run was successful"
}
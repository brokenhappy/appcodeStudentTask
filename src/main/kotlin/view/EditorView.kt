package view

import javafx.application.Platform
import javafx.geometry.Orientation
import javafx.scene.control.*
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.scene.text.TextFlow
import javafx.util.Duration
import javafx.util.converter.NumberStringConverter
import language.DaggerLanguageFactoryComponent
import language.LanguageFactory.SupportedLanguage
import language.errorAnalyzer.ErrorAnalyzer.ErrorPart
import language.scriptExecutor.ExitCode
import multiExecutor.DaggerMultiExecutorComponent
import multiExecutor.MultiExecutor
import multiExecutor.MultiExecutor.MultiExecutable.OnProgressUpdateEvent
import org.jetbrains.annotations.Contract
import tornadofx.*
import view.errorOutputs.DaggerErrorOutputComponent
import view.util.CountdownTimer

/**
 * I'm the least proud of this view package. The view code is the only part I did NOT write TDD and thus was the
 * most time consuming part.
 * I am aware of fallacies such as my usage of callbacks instead of passing around Observables like JavaFx is built for.
 * Frontend is NOT my strongest aspect and I kindly ask not to pay too much attention to the visuals, the
 * not-so JavaFx treatment of state and the lack of tests verifying view logic.
 *
 * I truly wish I knew how to write well-tested frontend code, especially in TDD.
 * Please, dont hesitate to give me advice and feedback, but please also evaluate my skills based on the "backend" ;)
 */
class EditorView : View() {
    private val languageFactory = DaggerLanguageFactoryComponent.create().getInstance()
    private var currentLanguage = languageFactory.createFor(SupportedLanguage.KOTLIN)
    private val multiExecutor = DaggerMultiExecutorComponent.create().getInstance()
    private val errorOutput = DaggerErrorOutputComponent.create().getInstance()

    private lateinit var codeOutput: TextFlow
    private lateinit var languageChoice: ChoiceBox<SupportedLanguage>
    private lateinit var executeButton: Button
    private lateinit var codeInput: TextArea
    private lateinit var codeProgress: ProgressBar
    private lateinit var expectedTimeLeft: Text
    private lateinit var numberOfTimesToExecuteInput: TextField

    private var lastExitCode: ExitCode? = null
    private var countdownTimer: CountdownTimer? = null

    override val root = splitpane(Orientation.VERTICAL) {
        style {
            font = Font.font(java.awt.Font.MONOSPACED, 14.0)
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
                languageChoice = choicebox(values = listOf(SupportedLanguage.KOTLIN, SupportedLanguage.SWIFT)) {
                    value = SupportedLanguage.KOTLIN
                    setOnAction {
                        currentLanguage = languageFactory.createFor(value)
                        runProcess.onEnd()
                    }
                }
                executeButton = button("Execute!") { setOnMouseClicked { runScript() } }
                label("Number of executions:")
                numberOfTimesToExecuteInput = textfield {
                    textFormatter = TextFormatter(NumberStringConverter())
                    text = "1"
                }
                codeProgress = progressbar { opacity = 0.0 }
                expectedTimeLeft = text("No run performed")
            }
            scrollpane {
                errorOutput.attachTo(this)
            }
        }
    }

    private val runProcess = object : MultiExecutor.MultiExecutable {
        override fun onStart() = Platform.runLater {
            executeButton.isDisable = true
            languageChoice.isDisable = true
            numberOfTimesToExecuteInput.isDisable = true
            codeProgress.progress = 0.0
            codeProgress.opacity = 1.0
            expectedTimeLeft.opacity = 1.0
            expectedTimeLeft.text = "Executing..."
            codeOutput.clear()
            errorOutput.clear()
        }

        override fun execute() {
            lastExitCode = currentLanguage.executor.run(
                script = codeInput.text.toString(),
                outputEvent = { outputLine -> Platform.runLater { codeOutput.children += Text(outputLine + "\n") } },
                errorEvent = { errorLine ->
                    Platform.runLater {
                        errorOutput.addLine(
                            line = errorLine,
                            onLinkClick = { codeLink ->
                                codeInput.setCaretAt(codeLink)
                            },
                        )
                    }
                }
            )
        }

        override fun onEnd() = Platform.runLater {
            countdownTimer?.cancel()
            executeButton.isDisable = false
            languageChoice.isDisable = false
            numberOfTimesToExecuteInput.isDisable = false
            codeProgress.progress = 1.0
            codeProgress.fade(Duration.seconds(1.0), 0.0)
            expectedTimeLeft.text = lastExitCode?.let { getStatusMessageFor(it) } ?: "No run performed"
        }

        override fun onProgressUpdate(event: OnProgressUpdateEvent) = Platform.runLater {
            codeOutput.children += Text("--------------------- Run: ${event.iterationCount + 1}\n")
            errorOutput.addRunSeparator(event.iterationCount)
            codeProgress.progress = event.progress
            countdownTimer = (countdownTimer ?: CountdownTimer(expectedTimeLeft.textProperty()))
                .also { it.setTime(event.estimatedTimeLeftMs) }
        }
    }

    private fun TextArea.setCaretAt(link: ErrorPart.CodeLink) {
        requestFocus()
        positionCaret(link.resolveIndexIn(text))
    }

    private fun runScript() {
        Thread {
            val numberOfTimesToExecute = numberOfTimesToExecuteInput.text
                .replace("[,.]".toRegex(), "")
                .toIntOrNull() ?: 1
            multiExecutor.execute(numberOfTimesToExecute, runProcess)
        }.start()
    }

    @Contract(pure = true)
    private fun getStatusMessageFor(exitCode: ExitCode) =
        if (exitCode.isNonZero()) "Last exit code:\n $exitCode"
        else "Last run was successful"
}
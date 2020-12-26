package language

import language.codeHighlighter.CodeHighlighter
import language.codeHighlighter.KotlinCodeHighlighter
import language.codeHighlighter.SwiftCodeHighlighter
import language.errorAnalyzer.ErrorAnalyzer
import language.errorAnalyzer.KotlinAndSwiftErrorAnalyzer
import language.scriptExecutor.KotlinExecutor
import language.scriptExecutor.ScriptExecutor
import language.scriptExecutor.SwiftExecutor
import javax.inject.Inject

class LanguageFactory @Inject constructor(
    kotlinExecutor: KotlinExecutor,
    swiftExecutor: SwiftExecutor,
    errorAnalyzer: KotlinAndSwiftErrorAnalyzer,
    kotlinCodeHighlighter: KotlinCodeHighlighter,
    swiftCodeHighlighter: SwiftCodeHighlighter,
) {
    enum class SupportedLanguage { KOTLIN, SWIFT }

    private data class LanguageImplementation(
        override val executor: ScriptExecutor,
        override val errorAnalyzer: ErrorAnalyzer,
        override val codeHighlighter: CodeHighlighter,
    ) : Language

    private val kotlin = LanguageImplementation(kotlinExecutor, errorAnalyzer, kotlinCodeHighlighter)
    private val swift = LanguageImplementation(swiftExecutor, errorAnalyzer, swiftCodeHighlighter)

    fun createFor(language: SupportedLanguage): Language = when(language) {
        SupportedLanguage.KOTLIN -> kotlin
        SupportedLanguage.SWIFT -> swift
    }
}
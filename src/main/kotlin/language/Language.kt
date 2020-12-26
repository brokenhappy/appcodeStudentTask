package language

import language.codeHighlighter.CodeHighlighter
import language.errorAnalyzer.ErrorAnalyzer
import language.scriptExecutor.ScriptExecutor

interface Language {
    val executor: ScriptExecutor
    val errorAnalyzer: ErrorAnalyzer
    val codeHighlighter: CodeHighlighter
}
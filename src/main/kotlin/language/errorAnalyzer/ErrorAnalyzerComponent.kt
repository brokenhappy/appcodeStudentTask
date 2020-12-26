package language.errorAnalyzer

import dagger.Component

@Component
interface ErrorAnalyzerComponent {
    fun getKotlinInstance(): KotlinAndSwiftErrorAnalyzer
    fun getSwiftInstance(): KotlinAndSwiftErrorAnalyzer
}
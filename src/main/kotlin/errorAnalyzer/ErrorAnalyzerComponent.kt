package errorAnalyzer

import dagger.Component

@Component
interface ErrorAnalyzerComponent {
    fun getKotlinInstance(): KotlinErrorAnalyzer
}
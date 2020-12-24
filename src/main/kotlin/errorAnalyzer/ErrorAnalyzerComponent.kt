package errorAnalyzer

import dagger.Component
import dagger.Provides

@Component
interface ErrorAnalyzerComponent {
    fun getKotlinInstance(): KotlinErrorAnalyzer
}
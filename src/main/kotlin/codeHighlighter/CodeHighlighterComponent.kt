package codeHighlighter

import dagger.Component

@Component
interface CodeHighlighterComponent {
    fun getKotlinInstance(): KotlinCodeHighlighter
}
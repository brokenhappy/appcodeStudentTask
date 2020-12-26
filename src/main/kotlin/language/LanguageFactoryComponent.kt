package language

import dagger.Component
import language.scriptExecutor.ScriptExecutorComponent

@Component(modules=[ScriptExecutorComponent.KotlinCompileCommonWarningResolverModule::class])
interface LanguageFactoryComponent {
    fun getInstance(): LanguageFactory
}
package scriptExecutor

import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules = [ScriptExecutorComponent.KotlinCompileCommonWarningResolverModule::class])
interface ScriptExecutorComponent {
    @Module
    class KotlinCompileCommonWarningResolverModule {
        @Provides
        fun provideKotlinCommonWarningResolver(
            warningResolver: KotlinCompileExecutingCommonWarningResolver
        ): KotlinCompileCommonWarningResolver = warningResolver
    }

    fun getKotlinInstance(): KotlinExecutor
}
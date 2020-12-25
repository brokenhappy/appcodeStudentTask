package view.errorOutputs

import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules=[ErrorOutputComponent.ProvidingModule::class])
interface ErrorOutputComponent {
    @Module
    class ProvidingModule {
        @Provides
        fun getInstance(output: TextFlowErrorOutput ): ErrorOutput = output
    }

    fun getInstance(): ErrorOutput
}
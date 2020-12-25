package multiExecutor

import dagger.Component
import dagger.Module
import dagger.Provides

@Component(modules=[MultiExecutorComponent.ProvidingModule::class])
interface MultiExecutorComponent {
    @Module
    class ProvidingModule {
        @Provides
        fun provideDefaultMultiExecutor(executor: DefaultMultiExecutor): MultiExecutor = executor
        @Provides
        fun provideDefaultTimeMeasurer(measurer: StandardLibExecutionTimeMeasurer): ExecutionTimeMeasurer = measurer
    }

    fun getInstance(): MultiExecutor
}
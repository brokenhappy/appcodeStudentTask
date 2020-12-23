package scriptExecutor

interface KotlinCompileCommonWarningResolver {
    fun isCommonWarning(line: String): Boolean
}

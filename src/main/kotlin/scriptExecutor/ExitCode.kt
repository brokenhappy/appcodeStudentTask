package scriptExecutor

inline class ExitCode(private val code: Int) {
    fun isNonZero() = code != 0
    override fun toString() = code.toString()
}
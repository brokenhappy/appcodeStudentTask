package language.scriptExecutor

import java.io.Closeable
import java.io.File
import java.io.FileWriter
import javax.inject.Inject

class TemporaryScriptFileProvider @Inject constructor() {
    private val tempDirectory by lazy {
        File("temp").apply {
            mkdir()
            ProcessBuilder("chmod", "-R", "777", absolutePath).start()
        }
    }

    interface TempFile : Closeable {
        val absolutePath: String
    }

    fun create(script: String): TempFile = object : TempFile {
        private val file = tempDirectory.resolve("script.kts").apply {
            createNewFile()
            deleteOnExit()
            FileWriter(absoluteFile).use { it.write(script) }
        }

        override val absolutePath get() = file.absolutePath

        override fun close() {
            file.delete()
        }
    }
}
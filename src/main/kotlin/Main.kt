import View.EditorApp

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            EditorApp().launch()
//            val process = ProcessBuilder("which", "kotlinc")
//                .start()
//            process.waitFor()
//            process.inputStream.bufferedReader().forEachLine { println(it) }
        }
    }
}
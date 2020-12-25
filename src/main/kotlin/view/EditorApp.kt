package view

import tornadofx.App

class EditorApp : App(EditorView::class) {
    fun launch() {
        tornadofx.launch<EditorApp>(arrayOf())
    }
}
package server.ui

import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.control.TextField
import javafx.scene.layout.VBox
import tornadofx.*
import java.io.File
import javax.swing.JFileChooser
import javax.swing.UIManager
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.system.exitProcess


class ServerView : View("Socket server build from Kotlin & Java") {
    override val root: VBox by fxml("/ServerView.fxml")
    val controller: ServerViewController by inject()
    val listView by fxid<ListView<String>>()

    init {
        currentStage!!.setOnCloseRequest {
            controller.turnOffServer()
            exitProcess(0)
        }
        controller.startServer()
        listView.itemsProperty().bind(controller.listAnnouces.toProperty())
    }
}
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
    private val controller: ServerViewController by inject()
    private val btnStart by fxid<Button>()
    private val txtPath by fxid<TextField>()
    private val btnBrowser by fxid<Button>()
    private val listView by fxid<ListView<String>>()

    init {

        currentWindow!!.setOnCloseRequest {
            controller.turnOffServer()
            exitProcess(0)
        }

        btnStart.enableWhen { controller.isEnable() }
        btnBrowser.disableWhen { controller.isStarted() }
        btnStart.setOnAction {
            controller.startServer()
        }
        btnBrowser.setOnAction {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
                val fileChooser = JFileChooser()
                fileChooser.fileFilter = FileNameExtensionFilter("XML File", "xml")
                fileChooser.currentDirectory = File("./src/main/resources/")
                fileChooser.showDialog(fileChooser, "Select file")
                val file = fileChooser.selectedFile
                if (file != null) {
                    controller.filePath = file.absolutePath
                    txtPath.text = file.absolutePath
                }
            } catch (e: Exception) {
            }
        }
        listView.itemsProperty().bind(controller.listAnnouces.toProperty())
    }
}
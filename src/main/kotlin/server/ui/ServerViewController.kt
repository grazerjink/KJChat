package server.ui

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import server.socket.ChatSocketServer
import tornadofx.Controller
import tornadofx.getProperty
import tornadofx.property
import tornadofx.runLater

class ServerViewController() : Controller() {
    var enable by property(false)
    fun isEnable() = getProperty(ServerViewController::enable)

    var started by property(false)
    fun isStarted() = getProperty(ServerViewController::started)

    var filePath: String = ""
        set(value) {
            field = value
            enable = !field.equals("")
        }

    var listAnnouces: ObservableList<String>? = FXCollections.observableArrayList()

    var server: ChatSocketServer? = null

    fun startServer() {
        runLater {
            server = ChatSocketServer(this)
            started = true
            enable = false
        }
    }

    fun turnOffServer() {
        server!!.stop()
    }

    fun retryStart(newPort: Int) {
        if (server != null) {
            server!!.stop()
        }
        server = ChatSocketServer(this, newPort)
    }

    fun updateMessage(mess: String) {
        runLater {
            listAnnouces!!.add(mess)
        }
    }
}
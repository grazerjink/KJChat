package client.ui

import client.socket.ClientSocket
import database.KJChat
import database.Message
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import tornadofx.*
import java.io.DataInputStream
import java.io.File


class ChatViewController(val chatView : ChatView) : Controller() {
    var ownuser by property("")

    var titleView by property("Welcome join to chat with us !!!")
    fun getTitleView() = getProperty(ChatViewController::titleView)

    var selectedUsername by property("")

    var connected by property(false)
    fun isConnected() = getProperty(ChatViewController::connected)

    var logined by property(false)
    fun isLogined() = getProperty(ChatViewController::logined)

    val listUser: ObservableList<String> = FXCollections.observableArrayList<String>()
    val listContent: ObservableList<String> = FXCollections.observableArrayList<String>()

    private lateinit var clientSocket: ClientSocket
    private lateinit var clientThread: Thread

    init {
        listUser.add("All")
    }

    fun connectToServer(ip: String, portNumber: String) {
        if (!ip.isEmpty() && !portNumber.isEmpty()) {
            try {
                val port = Integer.parseInt(portNumber)
                clientSocket = ClientSocket(this, ip, port)
                clientThread = Thread(clientSocket)
                val sent = clientSocket.sendMessage(Message("test", "testUser", "testContent", "SERVER"))
                if (sent) {
                    updateMessage("Connect to server successfull.")
                    selectedUsername = "All"
                    clientThread.start()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
                updateMessage("Server not found or not exist")
            }
        }
    }

    fun login(username: String, password: String) {
        if (!username.isEmpty() && !password.isEmpty()) {
            ownuser = username
            clientSocket.sendMessage(Message("login", username, password, "SERVER"))
        }
    }

    fun setTitleForCurrentUser() {
        runLater {
            titleView = "Welcome [[ ${ownuser.toUpperCase()} ]] joined in !!!"
            var listMessages = KJChat.instance.getAllMessage(ownuser)
            if(listMessages.count() > 0)
                listContent.addAll(listMessages)
        }
    }

    fun signup(username: String, password: String) {
        if (!username.isEmpty() && !password.isEmpty()) {
            clientSocket.sendMessage(Message("signup", username, password, "SERVER"))
        }
    }

    fun updateMessage(messge: String) {
        runLater {
            listContent.add(messge)
        }
    }

    fun setConnected(status: Boolean) {
        runLater {
            connected = status
        }
    }

    fun setLogined(status: Boolean) {
        runLater {
            logined = status
        }
    }

    fun sendMessage(content: String, username: String) : Boolean {
        if (!content.isEmpty() && !username.isEmpty()) {
            clientSocket.sendMessage(Message("message", ownuser, content, username))
            return true
        }
        return false
    }

    fun addListUser(username: String) {
        runLater {
            listUser.add(username)
        }
    }

    fun removeAllUsers() {
        runLater {
            listUser.removeAll()
        }
    }

    fun disconnect() {
        try {
            clientSocket.sendMessage(Message("message", ownuser, ".bye", "SERVER"))
            clientSocket.stopThread()
        } catch (ex: Exception) {
        }
    }

    fun getImageFile() : ByteArray {
        val imgFile = File("123.jpg")
        val input = DataInputStream(imgFile.inputStream())
        val able = input.available();
        return input.readBytes(able)
    }

    fun sendSticker(content: String, username: String) : Boolean {
        if (!content.isEmpty() && !username.isEmpty()) {
            clientSocket.sendMessage(Message("sticker", ownuser, content, username))
            return true
        }
        return false
    }
}
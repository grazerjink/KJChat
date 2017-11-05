package server.socket

import data.Message
import server.ui.ServerViewController
import java.io.IOException
import java.net.ServerSocket
import java.net.InetAddress
import java.net.Socket

class ChatSocketServer(val controller: ServerViewController, var port: Int = 13000) : Runnable {

    private lateinit var mainThread: Thread
    private lateinit var server: ServerSocket
    private var clientThreads = arrayOfNulls<ServerThread>(50)
    private var clientCount: Int = 0
    private var db = Database(controller.filePath)
    private var running = true

    init {
        try {
            db = Database(controller.filePath)
            server = ServerSocket(port)
            port = server.localPort
            controller.updateMessage("Server started at IP: ${InetAddress.getLocalHost().hostAddress} and port: $port")
            controller.updateMessage("Waiting for a client ...")
            running = true
            mainThread = Thread(this)
            mainThread.start()
        } catch (ex: IOException) {
            controller.updateMessage("Can not bind to port $port.")
            controller.updateMessage("Try to connect again.")
            controller.retryStart(0)
        }
    }

    override fun run() {
        while (running) {
            try {
                addThread(server.accept())
            } catch (ex: Exception) {
                controller.updateMessage("Server accept error.")
                controller.retryStart(0)
            }
        }
    }

    fun stop() {
        running = false
        for(i in 0..clientThreads.size) {
            try {
                clientThreads[i]?.close()
                clientThreads[i]?.stopThread()
            }
            catch (e: ArrayIndexOutOfBoundsException) {
            }
        }
        mainThread.interrupt()
    }

    fun addThread(socket: Socket) {
        if (clientCount < clientThreads.size) {
            clientThreads[clientCount] = ServerThread(this, socket)
            try {
                clientThreads[clientCount]!!.openStream()
                clientThreads[clientCount]!!.sendMessage(Message("confirm","system","connection accepted","SERVER"))
                clientThreads[clientCount]!!.start()
                clientCount++
            } catch (ioe: IOException) {
                println("Error opening main thread.")
            }
        } else {
            controller.updateMessage("Client refused: maximum ${clientThreads.size} reached.")
        }
    }

    @Synchronized
    fun handle(ID: Int, msg: Message) {
        when (msg.type) {
            ".bye" -> {
                announce("signout", "SERVER", msg.sender)
                removeClient(ID)
            }
            "test" -> {
                clientThreads[findClient(ID)]!!.sendMessage(Message("test", "SERVER", "OK", msg.sender))
            }
            "login" -> {
                if (findUserThread(msg.sender) == null) {
                    if (db.checkLogin(msg.sender, msg.content)) {
                        clientThreads[findClient(ID)]!!.username = msg.sender
                        clientThreads[findClient(ID)]!!.sendMessage(Message("login", "SERVER", "TRUE", msg.sender))
                        announce("newuser", "SERVER", msg.sender)
                        sendUserList(msg.sender)
                    } else {
                        clientThreads[findClient(ID)]!!.sendMessage(Message("login", "SERVER", "FALSE", msg.sender))
                    }
                } else {
                    clientThreads[findClient(ID)]!!.sendMessage(Message("login", "SERVER", "FALSE", msg.sender))
                }
            }
            "message" -> {
                if (msg.recipient.equals("All")) {
                    announce("message", msg.sender, msg.content)
                } else {
                    findUserThread(msg.recipient)!!.sendMessage(Message(msg.type, msg.sender, msg.content, msg.recipient))
                    clientThreads[findClient(ID)]!!.sendMessage(Message(msg.type, msg.sender, msg.content, msg.recipient))
                }
            }
            "sticker" -> {
                if (msg.recipient.equals("All")) {
                    announce("sticker", msg.sender, msg.content)
                } else {
                    findUserThread(msg.recipient)!!.sendMessage(Message(msg.type, msg.sender, msg.content, msg.recipient))
                    clientThreads[findClient(ID)]!!.sendMessage(Message(msg.type, msg.sender, msg.content, msg.recipient))
                }
            }
            "signup" -> {
                if (findUserThread(msg.sender) == null) {
                    if (!db.isUserExist(msg.sender)) {
                        db.addUser(msg.sender, msg.content)
                        clientThreads[findClient(ID)]!!.username = msg.sender
                        clientThreads[findClient(ID)]!!.sendMessage(Message("signup", "SERVER", "TRUE", msg.sender))
                        clientThreads[findClient(ID)]!!.sendMessage(Message("login", "SERVER", "TRUE", msg.sender))
                        announce("newuser", "SERVER", msg.sender)
                        sendUserList(msg.sender)
                    }
                } else {
                    clientThreads[findClient(ID)]!!.sendMessage(Message("signup", "SERVER", "FALSE", msg.sender))
                }
            }
            "upload_req" -> {
                if (msg.recipient.equals("ALL")) {
                    clientThreads[findClient(ID)]!!.sendMessage(Message("message", "SERVER", "Uploading to 'All' forbidden", msg.sender))
                } else {
                    findUserThread(msg.recipient)!!.sendMessage(Message("upload_req", msg.sender, msg.content, msg.recipient))
                }
            }
            "upload_res" -> {
                if (!msg.content.equals("NO")) {
                    val IP = findUserThread(msg.sender)!!.socket.inetAddress.hostAddress
                    findUserThread(msg.recipient)!!.sendMessage(Message("upload_res", IP, msg.content, msg.recipient))
                } else {
                    findUserThread(msg.recipient)!!.sendMessage(Message("upload_res", msg.sender, msg.content, msg.recipient))
                }
            }
        }
    }

    @Synchronized
    fun removeClient(port: Int) {
        val pos = findClient(port)
        try {
            if (pos >= 0) {
                controller.updateMessage("Removing client at port $port")
                if (pos < clientCount - 1) {
                    for (i in (pos + 1)..(clientCount - 1)) {
                        clientThreads[i - 1] = clientThreads[i]
                    }
                }
                clientCount--
                clientThreads[pos]!!.stopThread()
            }
        } catch (ioe: IOException) {
            println("Error closing thread.")
        }
    }

    fun findUserThread(usr: String): ServerThread? {
        for (i in 0..(clientCount - 1)) {
            if (clientThreads[i]!!.username.equals(usr)) {
                return clientThreads[i]!!
            }
        }
        return null
    }

    fun sendUserList(toWhom: String) {
        for (i in 0..(clientCount - 1)) {
            findUserThread(toWhom)!!.sendMessage(Message("newuser", "SERVER", clientThreads[i]!!.username, toWhom))
        }
    }

    fun announce(type: String, sender: String, content: String) {
        val msg = Message(type, sender, content, "All")
        for (i in 0..(clientCount - 1)) {
            clientThreads[i]!!.sendMessage(msg)
        }
    }

    private fun findClient(port: Int): Int {
        for (i in 0..(clientCount - 1))
            if (clientThreads[i]!!.port == port)
                return i
        return -1
    }
}

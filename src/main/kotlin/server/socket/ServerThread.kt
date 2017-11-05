package server.socket

import database.Message
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket

class ServerThread(var server: ChatSocketServer, var socket: Socket) : Thread() {

    var username = ""
    var port = socket.port
    private val controller = server.controller
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream

    private var running = true

    fun sendMessage(msg: Message) {
        try {
            outputStream.writeObject(msg)
            outputStream.flush()
        }
        catch (e : IOException) {
            println("Sending message has ERROR !!!")
        }
    }

    @Synchronized
    override fun run() {
        running = true
        controller.updateMessage("Client's IP ${socket.localAddress.hostAddress} accepted.")
        controller.updateMessage("Thread at IP ${socket.localAddress.hostAddress} - port ${socket.port} was running ")
        controller.updateMessage("Waiting for a client ...")
        while (running) {
            try {
                var msg = inputStream.readObject() as Message
                server.handle(port, msg)
            }
            catch (e : Exception) {
                println("Reading message has ERROR !!!")
                server.removeClient(port)
                stopThread()
            }
        }
    }

    /// Init the streams
    @Throws(IOException::class)
    fun openStream() {
        inputStream = ObjectInputStream(socket.getInputStream())
        outputStream = ObjectOutputStream(socket.getOutputStream())
        outputStream.flush()
    }

    @Throws(IOException::class)
    fun close() {
        socket.close()
        inputStream.close()
        outputStream.close()
    }

    fun stopThread() {
        running = false
    }
}
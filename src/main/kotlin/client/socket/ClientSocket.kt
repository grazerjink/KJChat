package client.socket

import client.ui.ChatViewController
import database.Message
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.ConnectException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class ClientSocket(val controller: ChatViewController, val ip: String, val port: Int) : Runnable {
    private lateinit var socket: Socket
    private lateinit var inputStream: ObjectInputStream
    private lateinit var outputStream: ObjectOutputStream
    private var running = true

    init {
        try {
            socket = Socket()
            socket.connect(InetSocketAddress(InetAddress.getByName(ip), port), 5000)
            outputStream = ObjectOutputStream(socket.getOutputStream())
            outputStream.flush()
            inputStream = ObjectInputStream(socket.getInputStream())
        } catch (e: ConnectException) {
            println("Connection timeout....")
        }
    }

    override fun run() {
        while (running) {
            try {
                var msg: Message?
                msg = inputStream.readObject() as Message
                println("Message arrived: ${msg.toString()}")

                when (msg.type) {
                    "test" -> {
                        controller.setConnected(true)
                        controller.setLogined(false)
                    }
                    "newuser" -> {
                        if (!msg.content.equals(controller.ownuser)) {
                            var exists = false
                            for (user in controller.listUser) {
                                if (user.equals(msg.content)) {
                                    exists = true
                                    break
                                }
                            }
                            if (!exists && !msg.content.trim().equals("")) {
                                controller.addListUser(msg.content)
                            }
                        }
                    }
                    "login" -> {
                        if (msg.content.equals("TRUE")) {
                            controller.setConnected(true)
                            controller.setLogined(true)
                            controller.updateMessage("${msg.recipient} login successful")
                            controller.setTitleForCurrentUser()
                        } else {
                            // show login failed
                            controller.updateMessage("Login failed, please try again !")
                        }
                    }
                    "signup" -> {
                        if (msg.content.equals("TRUE")) {
                            //enable button
                            controller.setConnected(true)
                            controller.setLogined(true)
                            controller.updateMessage("${msg.recipient} singup successful.")
                        } else {
                            controller.updateMessage("${msg.recipient} singup failed.")
                        }
                    }
                    "signout" -> {
                        if (msg.content.equals(controller.ownuser)) {
                            controller.updateMessage("[ ${msg.sender} whisper to YOU ] => Bye all !!")
                            // enable button
                            controller.setConnected(false)
                            controller.setLogined(false)
                            controller.removeAllUsers()
                            stopThread()
                        } else {
                            controller.updateMessage("[ ${msg.sender} ] => ${msg.content} has signed out")
                        }
                    }
                    "message" -> {
                        if (msg.recipient.equals("All")) {
                            controller.updateMessage("[ ${msg.sender} to ${msg.recipient} ] => ${msg.content}")
                        } else {
                            if(!msg.sender.equals(controller.ownuser)){
                                controller.updateMessage("[ ${msg.sender} whisper to YOU ] => ${msg.content}")
                            }
                            else {
                                controller.updateMessage("[ YOU whisper to ${msg.recipient} ] => ${msg.content}")
                            }
                        }
                    }
                    "sticker" -> {
                        if (msg.recipient.equals("All")) {
                            controller.updateMessage("[ ${msg.sender} to ${msg.recipient} ] => ${msg.content}")
                        } else {
                            if(!msg.sender.equals(controller.ownuser)){
                                controller.updateMessage("[ ${msg.sender} whisper to YOU ] => ${msg.content}")
                            }
                            else {
                                controller.updateMessage("[ YOU whisper to ${msg.recipient} ] => ${msg.content}")
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                stopThread()
                controller.updateMessage("Connection failure.")
                controller.setConnected(false)
                controller.setLogined(false)
                controller.removeAllUsers();
            }
        }
    }

    fun sendMessage(msg: Message): Boolean {
        try {
            outputStream.writeObject(msg)
            outputStream.flush()
            println("Sending message: ${msg.toString()}")
            return true
        } catch (e: Exception) {
            println("Sending message has ERROR.")
            return false
        }
    }

    fun stopThread() {
        running = false
    }
}
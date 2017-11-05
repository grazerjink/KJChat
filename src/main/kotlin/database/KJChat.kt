package database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class KJChat {
    private object Holder { val INSTANCE = KJChat() }
    companion object {
        val instance: KJChat by lazy { Holder.INSTANCE }
    }
    private constructor()
    lateinit var conn: Connection
    init {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance()
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/db_kjchat?usingUnicode=true&characterEncoding=utf-8","root","")
        } catch (ex: SQLException) {
            ex.printStackTrace()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun checkUserExistance(username: String): Boolean {
        val sql = "SELECT * FROM users WHERE id = '${username}'"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)
        if(rs.next()) {
            return true
        }
        return false
    }

    fun checkUserLogin(username: String, password: String): Boolean {
        val sql = "SELECT * FROM users WHERE id = '${username}' and password = '${password}'"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)
        if(rs.next()) {
            return true
        }
        return false
    }

    fun insertUser(username: String, password: String) {
        val sql = "INSERT INTO users(id, password) VALUES('${username}','${password}')"
        val stmt = conn.createStatement()
        stmt.executeUpdate(sql)
    }

    fun insertMessage(msg: Message) {
        val sql = "INSERT INTO messages(sender,recipient,content,type) VALUES('${msg.sender}','${msg.recipient}','${msg.content}','${msg.type}')"
        val stmt = conn.createStatement()
        stmt.executeUpdate(sql)
    }

    fun getAllMessage(username: String): MutableList<String>{
        val sql = "SELECT * FROM messages WHERE sender = '${username}' OR recipient = '${username}'"
        val stmt = conn.createStatement()
        val rs = stmt.executeQuery(sql)
        var listMessages = mutableListOf<String>()
        while(rs.next()) {
            val type = rs.getString("type")
            val sender = rs.getString("sender")
            val recipient = rs.getString("recipient")
            val content = rs.getString("content")

            var msg = ""
            if(sender.equals(username)) {
                msg = "[YOU whisper to ${recipient}] => ${content}"
            }
            else {
                msg = "[${sender} whisper to YOU] => ${content}"
            }

            listMessages.add(msg)
        }
        return listMessages
    }
}
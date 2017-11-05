package data

import java.io.Serializable

class Message(var type: String, var sender: String, var content: String, var recipient: String) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }

    override fun toString(): String {
        return "{type='$type', sender='$sender', content='$content', recipient='$recipient'}"
    }
}
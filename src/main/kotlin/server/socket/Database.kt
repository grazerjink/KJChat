package server.socket

import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.TransformerFactory

class Database(var filePath: String) {
    fun isUserExist(username: String?=""): Boolean {
        try {
            var xmlFile = File(filePath)
            var docFactory = DocumentBuilderFactory.newInstance()
            var docBuilder = docFactory.newDocumentBuilder()
            var doc = docBuilder.parse(xmlFile)
            doc.documentElement.normalize()

            var nodeList = doc.getElementsByTagName("user")
            for (i in 0..nodeList.length) {
                var child = nodeList.item(i)
                if (child.nodeType == Node.ELEMENT_NODE) {
                    var element = child as Element
                    if (getTagValue("username", element).equals(username)) {
                        return true
                    }
                }
            }
            return false
        } catch (ex: Exception) {
            println("Database exception: userExists()")
            return false
        }
    }

    fun checkLogin(username: String?="", password: String?=""): Boolean {
        if (!isUserExist(username)) return false
        try {
            val fXmlFile = File(filePath)
            val dbFactory = DocumentBuilderFactory.newInstance()
            val dBuilder = dbFactory.newDocumentBuilder()
            val doc = dBuilder.parse(fXmlFile)
            doc.documentElement.normalize()

            val nList = doc.getElementsByTagName("user")

            for (temp in 0..(nList.length - 1)) {
                val nNode = nList.item(temp)
                if (nNode.nodeType == Node.ELEMENT_NODE) {
                    val eElement = nNode as Element
                    if (getTagValue("username", eElement).equals(username)
                            && getTagValue("password", eElement).equals(password)) {
                        return true
                    }
                }
            }
            println("Hipple")
            return false
        } catch (ex: Exception) {
            println("Database exception : userExists()")
            return false
        }
    }

    fun addUser(username: String?="", password: String?="") {
        try {
            val docFactory = DocumentBuilderFactory.newInstance()
            val docBuilder = docFactory.newDocumentBuilder()
            val doc = docBuilder.parse(filePath)

            val data = doc.firstChild

            val newuser = doc.createElement("user")
            val newusername = doc.createElement("username")
            newusername.textContent = username
            val newpassword = doc.createElement("password")
            newpassword.textContent = password

            newuser.appendChild(newusername)
            newuser.appendChild(newpassword)
            data.appendChild(newuser)

            val transformerFactory = TransformerFactory.newInstance()
            val transformer = transformerFactory.newTransformer()
            val source = DOMSource(doc)
            val result = StreamResult(File(filePath))
            transformer.transform(source, result)
        } catch (ex: Exception) {
            println("Exceptionmodify xml")
        }

    }

    fun getTagValue(sTag: String, eElement: Element): String {
        val nlList = eElement.getElementsByTagName(sTag).item(0).childNodes
        val nValue = nlList.item(0) as Node
        return nValue.nodeValue
    }

}
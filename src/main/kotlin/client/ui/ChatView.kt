package client.ui

import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.VBox
import javafx.stage.Modality
import tornadofx.*
import kotlin.system.exitProcess
import javafx.stage.StageStyle


class ChatView : View() {
    override val root by fxml<BorderPane>("/ChatView.fxml")
    val leftVBox by fxid<VBox>()
    val txtIP by fxid<TextField>()
    val txtPort by fxid<TextField>()
    val txtUsername by fxid<TextField>()
    val txtPassword by fxid<TextField>()
    val btnConnect by fxid<Button>()
    val btnLogin by fxid<Button>()
    val btnSignup by fxid<Button>()
    val btnIcon by fxid<Button>()
    val btnSend by fxid<Button>()
    val listContent by fxid<ListView<String>>()
    val listUser by fxid<ListView<String>>()
    val txtContent by fxid<TextArea>()

    val controller = ChatViewController(this)

    init {
        titleProperty.bind(controller.getTitleView())
        currentWindow!!.setOnCloseRequest {
            controller.disconnect()
            exitProcess(0)
        }

        btnIcon.setOnAction {
            println("Popup !!!")
            //val icon =controller.sendSticker(controller.getImageFile().toString(),controller.selectedUsername)
            find(StickerFragment::class).openModal(stageStyle = StageStyle.UTILITY, escapeClosesWindow = true,modality = Modality.NONE)
        }

        txtIP.disableWhen { controller.isConnected() }
        txtPort.disableWhen { controller.isConnected() }
        btnConnect.disableWhen { controller.isConnected() }
        btnConnect.setOnAction {
            controller.connectToServer(txtIP.text, txtPort.text)
        }

        txtPassword.enableWhen { controller.isConnected().isEqualTo(true).and(controller.isLogined().isEqualTo(false)) }
        txtPassword.enableWhen { controller.isConnected().isEqualTo(true).and(controller.isLogined().isEqualTo(false)) }
        txtUsername.enableWhen { controller.isConnected().isEqualTo(true).and(controller.isLogined().isEqualTo(false)) }
        btnLogin.enableWhen { controller.isConnected().isEqualTo(true).and(controller.isLogined().isEqualTo(false)) }
        btnSignup.enableWhen { controller.isConnected().isEqualTo(true).and(controller.isLogined().isEqualTo(false)) }

        btnLogin.setOnAction {
            controller.login(txtUsername.text, txtPassword.text)
        }

        btnSignup.setOnAction {
            controller.signup(txtUsername.text, txtPassword.text)
        }


        btnSend.enableWhen { controller.isConnected().isEqualTo(true).and(controller.isLogined().isEqualTo(true)) }
        btnIcon.enableWhen { controller.isConnected().isEqualTo(true).and(controller.isLogined().isEqualTo(true)) }
        btnSend.setOnAction {
            val sent = controller.sendMessage(txtContent.text, controller.selectedUsername)
            if (sent) txtContent.clear()
        }

        listContent.enableWhen { controller.isConnected() }
        listContent.itemsProperty().bind(controller.listContent.toProperty())

        listUser.enableWhen { controller.isConnected() }
        listUser.itemsProperty().bind(controller.listUser.toProperty())
        listUser.selectionModel.selectedItemProperty().addListener { _,_,newValue -> controller.selectedUsername = newValue }
    }
}
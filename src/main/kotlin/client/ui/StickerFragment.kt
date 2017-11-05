package client.ui

import javafx.scene.image.Image
import tornadofx.Fragment
import tornadofx.hbox
import tornadofx.imageview
import tornadofx.scrollpane

class StickerFragment : Fragment() {
    override val root = scrollpane {
        hbox {
            setPrefSize(500.0,200.0)
            spacing = 10.0
            imageview {
                fitHeight = 200.0
                fitWidth = 200.0
                isPreserveRatio = true
                image = Image("/stickers/1.png")
            }
            imageview {
                fitHeight = 200.0
                fitWidth = 200.0
                isPreserveRatio = true
                image = Image("/stickers/2.png")
            }
            imageview {
                fitHeight = 200.0
                fitWidth = 200.0
                isPreserveRatio = true
                image = Image("/stickers/3.png")
            }
            imageview {
                fitHeight = 200.0
                fitWidth = 200.0
                isPreserveRatio = true
                image = Image("/stickers/4.png")
            }
            imageview {
                fitHeight = 200.0
                fitWidth = 200.0
                isPreserveRatio = true
                image = Image("/stickers/5.png")
            }
            imageview {
                fitHeight = 200.0
                fitWidth = 200.0
                isPreserveRatio = true
                image = Image("/stickers/6.png")
            }
            imageview {
                fitHeight = 200.0
                fitWidth = 200.0
                isPreserveRatio = true
                image = Image("/stickers/7.png")
            }
        }
    }
}
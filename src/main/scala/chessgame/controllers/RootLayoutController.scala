package chessgame.controllers

import chessgame.Main
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.image.{Image, ImageView}
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController {
	def toMainMenu(): Unit = {
		Main.showWelcome
	}

	def exitApp(): Unit = {
		System.exit(0)
	}

	def quitGame(): Unit = {
		val alert = new Alert(AlertType.Confirmation) {
			title = "Quit Game"
			headerText = "Are you sure you want to draw the game?"
			buttonTypes = Seq(ButtonType.Yes, ButtonType.No)
		}
		val result = alert.showAndWait()
		result match {
			case Some(ButtonType.Yes) =>
				Main.showWelcome
			case _ =>
		}
	}

	def aboutGame(): Unit = {
		new Alert(AlertType.Information) {
			initOwner(Main.stage)
			title = "About Chess"
			headerText = "About Chess"
			contentText = "Chess is a two-player strategy board game played on a chessboard, " +
				"a checkered gameboard with 64 squares arranged in an eight-by-eight grid. " +
				"Chess is played by millions of people worldwide, both amateurs and professionals."
			graphic = new ImageView() {
				image = new Image(getClass.getResourceAsStream(s"/images/chess/basic.png"))
				fitWidth = 50
				fitHeight = 50
			}
		}.showAndWait()
	}
}
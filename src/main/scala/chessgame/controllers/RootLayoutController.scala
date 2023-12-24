package chessgame.controllers

import chessgame.Main
import scalafx.scene.control.Alert
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.web.WebEvent.Alert
import scalafxml.core.macros.sfxml

@sfxml
class RootLayoutController {
	def exitApp(): Unit = {
		System.exit(0)
	}

	def quitGame(): Unit = {
		Main.showWelcome
	}

	def aboutGame(): Unit = {
		new Alert(AlertType.Information) {
			initOwner(Main.stage)
			title = "About Chess"
			headerText = "About Chess"
			contentText = "Chess is a two-player strategy board game played on a chessboard, a checkered gameboard with 64 squares arranged in an eight-by-eight grid. Chess is played by millions of people worldwide, both amateurs and professionals."
		}.showAndWait()
	}
}
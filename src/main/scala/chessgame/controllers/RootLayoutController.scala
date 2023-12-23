package chessgame.controllers

import chessgame.Main
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
		println("about game")
	}
}
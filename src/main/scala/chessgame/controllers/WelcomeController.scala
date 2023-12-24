package chessgame.controllers;

import chessgame.Main
import chessgame.board.Board
import scalafxml.core.macros.sfxml

@sfxml
class WelcomeController {
	def startGame = {
		Main.showBoard(Board.createStandardBoard)
	}

	def viewStats = {
		Main.showStats
	}

	def exitGame = {
		System.exit(0)
	}
}

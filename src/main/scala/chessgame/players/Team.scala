package chessgame.players

import chessgame.board.Board

object Team extends Enumeration {
	type Team = Value
	val WHITE, BLACK = Value

	implicit class TeamOps(team: Value) {
		def getDirection: Int = team match {
			case WHITE => -1
			case BLACK => 1
		}

		def getOppositeDirection: Int = team match {
			case WHITE => 1
			case BLACK => -1
		}

		def isWhite: Boolean = team == WHITE

		def isBlack: Boolean = team == BLACK

		def choosePlayer(whitePlayer: WhitePlayer, blackPlayer: BlackPlayer): Player = team match {
			case WHITE => whitePlayer
			case BLACK => blackPlayer
		}

		def isPawnPromotionTile(coordinate: Int): Boolean = team match {
			case WHITE => Board.FIRST_ROW(coordinate)
			case BLACK => Board.EIGHTH_ROW(coordinate)
		}
	}
}

package chessgame

import chessgame.players.{BlackPlayer, Player, WhitePlayer}

object Team extends Enumeration {
  type Team = Value
  val WHITE, BLACK = Value

  implicit class TeamOps(team: Value) {
    def getDirection: Int = team match {
      case WHITE => -1
      case BLACK => 1
    }

    def isWhite: Boolean = team == WHITE

    def isBlack: Boolean = team == BLACK

    def choosePlayer(whitePlayer: WhitePlayer, blackPlayer: BlackPlayer): Player = team match {
      case WHITE => whitePlayer
      case BLACK => blackPlayer
    }
  }
}

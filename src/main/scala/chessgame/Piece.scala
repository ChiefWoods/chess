package chessgame

import chessgame.Team.Team

abstract class Piece(private val team : Team, private val piecePosition : Int, private val isFirstMove : Boolean = false) {
  def getPieceTeam: Team = team

  def getFirstMove: Boolean = isFirstMove

  def calculateLegalMoves(board : Board) : Set[Move]
}

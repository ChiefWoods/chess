package chessgame

import chessgame.Team.Team

abstract class Piece(private val team : Team, private val piecePosition : Int) {
  def getPieceTeam: Team = team

  def calculateLegalMoves(board : Board) : Set[Move]
}

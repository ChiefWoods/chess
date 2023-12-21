package chessgame.pieces

import chessgame.Team
import chessgame.board.{Board, Move}

abstract class Piece(private val team: Team.Team, private val pieceType: Piece.Piece, private val piecePosition: Int, private val isFirstMove: Boolean = false) {
  private val cachedHashCode: Int = computeHashCode

  def getPieceTeam: Team.Team = team

  def getPieceType: Piece.Piece = pieceType

  def getPiecePosition: Int = piecePosition

  def getIsFirstMove: Boolean = isFirstMove

  def calculateLegalMoves(board: Board): Set[Move]

  def movePiece(move: Move): Piece

  private def computeHashCode: Int = {
    var result: Int = pieceType.hashCode
    val prime = 31

    result = prime * result + team.hashCode
    result = prime * result + piecePosition

    if (isFirstMove) result = prime * result + 1
    else result = prime * result + 0

    result
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) return true
    if (obj == null || !obj.isInstanceOf[Piece]) return false

    val piece: Piece = obj.asInstanceOf[Piece]

    team == piece.getPieceTeam &&
      pieceType == piece.getPieceType &&
      piecePosition == piece.getPiecePosition &&
      isFirstMove == piece.getIsFirstMove
  }

  override def hashCode: Int = cachedHashCode
}

object Piece extends Enumeration {
  type Piece = Value
  val PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING = Value

  implicit class PieceTypeOps(pieceType: Value) {
    def toChar: String = pieceType match {
      case PAWN => "P"
      case KNIGHT => "N"
      case BISHOP => "B"
      case ROOK => "R"
      case QUEEN => "Q"
      case KING => "K"
    }

    def isKing: Boolean = pieceType == KING

    def isRook: Boolean = pieceType == ROOK
  }
}

package chessgame

abstract class Piece(private val team: Team.Team, private val piecePosition: Int, private val isFirstMove: Boolean = false) {
  def getPieceTeam: Team.Team = team

  def getPiecePosition: Int = piecePosition

  def getFirstMove: Boolean = isFirstMove

  def calculateLegalMoves(board: Board): Set[Move]
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
  }
}

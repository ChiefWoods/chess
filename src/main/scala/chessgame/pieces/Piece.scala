package chessgame.pieces

import chessgame.board.Board
import chessgame.moves.Move
import chessgame.players.Team

abstract class Piece(private val team: Team.Team,
                     private val pieceType: Piece.Piece,
                     private val piecePosition: Int,
                     private val isFirstMove: Boolean) {

	def getPieceTeam: Team.Team = team

	def getPieceType: Piece.Piece = pieceType

	def getPiecePosition: Int = piecePosition

	def getIsFirstMove: Boolean = isFirstMove

	def calculateLegalMoves(board: Board): Set[Move]

	def movePiece(move: Move): Piece
}

object Piece extends Enumeration {
	type Piece = Value
	val PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING = Value

	implicit class PieceTypeOps(pieceType: Value) {
		def isKing: Boolean = pieceType == KING

		def isRook: Boolean = pieceType == ROOK

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

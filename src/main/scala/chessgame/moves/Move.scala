package chessgame.moves

import chessgame.board.{Board, Builder}
import chessgame.pieces.{Pawn, Piece, Rook}

trait Attack {
	def isAttack: Boolean

	def getAttackedPiece: Piece
}

trait Castle {
	def isCastle: Boolean

	def getCastleRook: Rook
}

class Move(private val board: Board,
           private val destinationCoordinate: Int,
           private val movedPiece: Piece = null) {

	def getDestinationCoordinate: Int = destinationCoordinate

	def getMovedPiece: Piece = movedPiece

	def getCurrentCoordinate: Int = movedPiece.getPiecePosition

	def execute: Board = {
		val builder: Builder = new Builder()

		for (piece <- board.getCurrentPlayer.getActivePieces) {
			if (!movedPiece.equals(piece)) {
				builder.setPiece(piece)
			}
		}

		for (piece <- board.getCurrentPlayer.getOpponent.getActivePieces) {
			builder.setPiece(piece)
		}

		builder.setPiece(movedPiece.movePiece(this))
		builder.setMoveMaker(board.getCurrentPlayer.getOpponent.getTeam)
		builder.build
	}

	override def toString: String = {
		movedPiece.toString + Board.getPositionAtCoordinate(destinationCoordinate)
	}
}

object Move {
	private val INVALID_MOVE = InvalidMove()

	def createMove(board: Board, currentCoordinate: Int, destinationCoordinate: Int): Move = {
		for (move <- board.getAllLegalMoves) {
			if (move.getCurrentCoordinate == currentCoordinate &&
				move.getDestinationCoordinate == destinationCoordinate) {
				return move
			}
		}

		INVALID_MOVE
	}
}

case class MajorMove(private val board: Board,
                     private val destinationCoordinate: Int,
                     private val movedPiece: Piece)
	extends Move(board, destinationCoordinate, movedPiece) {
}

class AttackMove(private val board: Board,
                 private val destinationCoordinate: Int,
                 private val movedPiece: Piece,
                 private val attackedPiece: Piece = null)
	extends Move(board, destinationCoordinate, movedPiece) with Attack {

	override def isAttack: Boolean = getAttackedPiece != null

	override def getAttackedPiece: Piece = attackedPiece
}

case class MajorAttackMove(private val board: Board,
                           private val destinationCoordinate: Int,
                           private val movedPiece: Piece,
                           private val attackedPiece: Piece)
	extends AttackMove(board, destinationCoordinate, movedPiece, attackedPiece) {
}

case class PawnMove(private val board: Board,
                    private val destinationCoordinate: Int,
                    private val movedPiece: Piece)
	extends Move(board, destinationCoordinate, movedPiece) {

	override def toString: String = {
		Board.getPositionAtCoordinate(destinationCoordinate)
	}
}

case class PawnAttackMove(private val board: Board,
                          private val destinationCoordinate: Int,
                          private val movedPiece: Piece,
                          private val attackedPiece: Piece)
	extends AttackMove(board, destinationCoordinate, movedPiece, attackedPiece) {

	override def toString: String = {
		Board.getPositionAtCoordinate(movedPiece.getPiecePosition).substring(0, 1) + "x" + Board.getPositionAtCoordinate(destinationCoordinate)
	}
}

case class PawnEnPassant(private val board: Board,
                         private val destinationCoordinate: Int,
                         private val movedPiece: Piece,
                         private val attackedPiece: Piece)
	extends AttackMove(board, destinationCoordinate, movedPiece, attackedPiece) {

	override def execute: Board = {
		val builder: Builder = new Builder()

		for (piece <- board.getCurrentPlayer.getActivePieces) {
			if (!movedPiece.equals(piece)) {
				builder.setPiece(piece)
			}
		}

		for (piece <- board.getCurrentPlayer.getOpponent.getActivePieces) {
			if (!piece.equals(getAttackedPiece)) {
				builder.setPiece(piece)
			}
		}

		builder.setPiece(movedPiece.movePiece(this))
		builder.setMoveMaker(board.getCurrentPlayer.getOpponent.getTeam)
		builder.build
	}

	override def toString: String = {
		Board.getPositionAtCoordinate(movedPiece.getPiecePosition).substring(0, 1) + "x" + Board.getPositionAtCoordinate(destinationCoordinate) + "e.p."
	}
}

case class PawnJump(private val board: Board,
                    private val destinationCoordinate: Int,
                    private val movedPiece: Piece)
	extends Move(board, destinationCoordinate, movedPiece) {

	override def execute: Board = {
		val builder: Builder = new Builder()

		for (piece <- board.getCurrentPlayer.getActivePieces) {
			if (!movedPiece.equals(piece)) {
				builder.setPiece(piece)
			}
		}

		for (piece <- board.getCurrentPlayer.getOpponent.getActivePieces) {
			builder.setPiece(piece)
		}

		val movedPawn: Pawn = movedPiece.movePiece(this).asInstanceOf[Pawn]
		builder.setPiece(movedPawn)
		builder.setEnPassantPawn(movedPawn)
		builder.setMoveMaker(board.getCurrentPlayer.getOpponent.getTeam)
		builder.build
	}

	override def toString: String = {
		Board.getPositionAtCoordinate(destinationCoordinate)
	}
}

case class PawnPromotion(private val decoratedMove: Move,
                         private val board: Board,
                         private val destinationCoordinate: Int,
                         private val movedPiece: Piece,
                         private val attackedPiece: Piece = null)
	extends Move(board, destinationCoordinate, movedPiece) with Attack {

	private val promotedPawn = decoratedMove.getMovedPiece.asInstanceOf[Pawn]

	override def execute: Board = {
		val pawnMoveBoard: Board = decoratedMove.execute
		val builder: Builder = new Builder()

		for (piece <- pawnMoveBoard.getCurrentPlayer.getActivePieces) {
			if (!promotedPawn.equals(piece)) {
				builder.setPiece(piece)
			}
		}

		for (piece <- pawnMoveBoard.getCurrentPlayer.getOpponent.getActivePieces) {
			builder.setPiece(piece)
		}

		builder.setPiece(promotedPawn.getPromotionPiece.movePiece(this))
		builder.setMoveMaker(pawnMoveBoard.getCurrentPlayer.getTeam)
		builder.build
	}

	override def isAttack: Boolean = getAttackedPiece != null

	override def getAttackedPiece: Piece = attackedPiece

	override def toString: String = {
		Board.getPositionAtCoordinate(destinationCoordinate) + "=" + promotedPawn.getPromotionPiece
	}
}

class CastleMove(private val board: Board,
                 private val destinationCoordinate: Int,
                 private val movedPiece: Piece,
                 private val castleRook: Rook,
                 private val castleRookStart: Int,
                 private val castleRookDestination: Int)
	extends Move(board, destinationCoordinate, movedPiece) with Castle {

	override def isCastle: Boolean = true

	override def getCastleRook: Rook = castleRook

	override def execute: Board = {
		val builder: Builder = new Builder()

		for (piece <- board.getCurrentPlayer.getActivePieces) {
			if (!movedPiece.equals(piece) && !castleRook.equals(piece)) {
				builder.setPiece(piece)
			}
		}

		for (piece <- board.getCurrentPlayer.getOpponent.getActivePieces) {
			builder.setPiece(piece)
		}

		builder.setPiece(movedPiece.movePiece(this))
		builder.setPiece(Rook(castleRook.getPieceTeam, castleRookDestination, false))
		builder.setMoveMaker(board.getCurrentPlayer.getOpponent.getTeam)
		builder.build
	}
}

case class KingSideCastle(private val board: Board,
                          private val destinationCoordinate: Int,
                          private val movedPiece: Piece,
                          private val castleRook: Rook,
                          private val castleRookStart: Int,
                          private val castleRookDestination: Int)
	extends CastleMove(board, destinationCoordinate, movedPiece, castleRook, castleRookStart, castleRookDestination) {

	override def toString: String = "O-O"
}

case class QueenSideCastle(private val board: Board,
                           private val destinationCoordinate: Int,
                           private val movedPiece: Piece,
                           private val castleRook: Rook,
                           private val castleRookStart: Int,
                           private val castleRookDestination: Int)
	extends CastleMove(board, destinationCoordinate, movedPiece, castleRook, castleRookStart, castleRookDestination) {

	override def toString: String = "O-O-O"
}

case class InvalidMove()
	extends Move(null, -1) {

	override def getCurrentCoordinate: Int = -1

	override def toString: String = "Move is invalid!"
}

package chessgame.board

import chessgame.pieces.{Pawn, Piece, Rook}

abstract class Move(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int) {
  def getDestinationCoordinate: Int = destinationCoordinate

  def getMovedPiece: Piece = movedPiece

  def getCurrentCoordinate: Int = movedPiece.getPiecePosition

  def isAttack: Boolean = false

  def isCastlingMove: Boolean = false

  def getAttackedPiece: Piece = null

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

  override def hashCode: Int = {
    var result = 31 * 1 + movedPiece.getPiecePosition
    result = 31 * result + destinationCoordinate
    result = 31 * result + movedPiece.hashCode

    result
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) return true
    if (obj == null || !obj.isInstanceOf[Move]) return false

    val move: Move = obj.asInstanceOf[Move]

    getDestinationCoordinate == move.getDestinationCoordinate &&
      getMovedPiece.equals(move.getMovedPiece)
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

class MajorMove(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int)
  extends Move(board, movedPiece, destinationCoordinate) {
}

class AttackMove(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int, private val attackedPiece: Piece)
  extends Move(board, movedPiece, destinationCoordinate) {
  override def isAttack: Boolean = true

  override def getAttackedPiece: Piece = attackedPiece

  override def execute: Board = null

  override def equals(obj: Any): Boolean = {
    if (this == obj) return true
    if (obj == null || !obj.isInstanceOf[AttackMove]) return false

    val move: AttackMove = obj.asInstanceOf[AttackMove]

    super.equals(move) && getAttackedPiece.equals(move.getAttackedPiece)
  }

  override def hashCode: Int = {
    attackedPiece.hashCode + super.hashCode
  }
}

case class PawnMove(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int)
  extends Move(board, movedPiece, destinationCoordinate) {
}

class PawnAttackMove(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int, private val attackPiece: Piece)
  extends AttackMove(board, movedPiece, destinationCoordinate, attackPiece) {
}

case class PawnEnPassant(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int, private val attackPiece: Piece)
  extends PawnAttackMove(board, movedPiece, destinationCoordinate, attackPiece) {
}

case class PawnJump(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int)
  extends Move(board, movedPiece, destinationCoordinate) {
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
}

abstract class CastleMove(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int, private val castleRook: Rook, private val castleRookStart: Int, private val castleRookDestination: Int)
  extends Move(board, movedPiece, destinationCoordinate) {

  def getCastleRook: Rook = castleRook

  override def isCastlingMove: Boolean = true

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
    builder.setPiece(new Rook(castleRook.getPieceTeam, castleRookDestination))
    builder.setMoveMaker(board.getCurrentPlayer.getOpponent.getTeam)
    builder.build
  }
}

class KingSideCastle(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int, private val castleRook: Rook, private val castleRookStart: Int, private val castleRookDestination: Int)
  extends CastleMove(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination) {

  override def toString: String = "O-O"
}

class QueenSideCastle(private val board: Board, private val movedPiece: Piece, private val destinationCoordinate: Int, private val castleRook: Rook, private val castleRookStart: Int, private val castleRookDestination: Int)
  extends CastleMove(board, movedPiece, destinationCoordinate, castleRook, castleRookStart, castleRookDestination) {

  override def toString: String = "O-O-O"
}

case class InvalidMove()
  extends Move(null, null, -1) {

  override def execute: Board = {
    throw new RuntimeException("Cannot execute an invalid move!")
  }
}

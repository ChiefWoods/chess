package chessgame.board

import chessgame.pieces.{Pawn, Piece, Rook}

abstract class Move(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece = null, private val isFirstMove: Boolean = false) {
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
    val prime = 31
    var result = 1
    result = prime * result + destinationCoordinate
    result = prime * result + movedPiece.hashCode
    result = prime * result + movedPiece.getPiecePosition

    result
  }

  override def equals(obj: Any): Boolean = {
//    if (this == obj) return true
    if (!obj.isInstanceOf[Move]) return false

    val move: Move = obj.asInstanceOf[Move]

    getCurrentCoordinate == move.getCurrentCoordinate &&
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

class MajorMove(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece)
  extends Move(board, destinationCoordinate, movedPiece) {

  override def equals(obj: Any): Boolean = {
//    this == obj || obj.isInstanceOf[MajorMove] && super.equals(obj)
    obj.isInstanceOf[MajorMove] && super.equals(obj)
  }

  override def toString: String = {
    movedPiece.getPieceType.toString + Board.getPositionAtCoordinate(destinationCoordinate)
  }
}

class AttackMove(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece, private val attackedPiece: Piece)
  extends Move(board, destinationCoordinate, movedPiece) {
  override def isAttack: Boolean = true

  override def getAttackedPiece: Piece = attackedPiece

  override def equals(obj: Any): Boolean = {
//    if (this == obj) return true
    if (!obj.isInstanceOf[AttackMove]) return false

    val move: AttackMove = obj.asInstanceOf[AttackMove]

    super.equals(move) && getAttackedPiece.equals(move.getAttackedPiece)
  }

  override def hashCode: Int = {
    attackedPiece.hashCode + super.hashCode
  }
}

class MajorAttackMove(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece, private val attackedPiece: Piece)
  extends AttackMove(board, destinationCoordinate, movedPiece, attackedPiece) {

  override def equals(obj: Any): Boolean = {
//    this == obj || obj.isInstanceOf[MajorAttackMove] && super.equals(obj)
    obj.isInstanceOf[MajorAttackMove] && super.equals(obj)
  }

  override def toString: String = {
    movedPiece.getPieceType.toString + Board.getPositionAtCoordinate(destinationCoordinate)
  }
}

class PawnMove(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece)
  extends Move(board, destinationCoordinate, movedPiece) {

  override def equals(obj: Any): Boolean = {
//    this == obj || obj.isInstanceOf[PawnMove] && super.equals(obj)
    obj.isInstanceOf[PawnMove] && super.equals(obj)
  }

  override def toString: String = {
    Board.getPositionAtCoordinate(destinationCoordinate)
  }
}

class PawnAttackMove(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece, private val attackPiece: Piece)
  extends AttackMove(board, destinationCoordinate, movedPiece, attackPiece) {

  override def equals(obj: Any): Boolean = {
//    this == obj || obj.isInstanceOf[PawnAttackMove] && super.equals(obj)
    obj.isInstanceOf[PawnAttackMove] && super.equals(obj)
  }

  override def toString: String = {
    Board.getPositionAtCoordinate(movedPiece.getPiecePosition).substring(0, 1) + "x" + Board.getPositionAtCoordinate(destinationCoordinate)
  }
}

class PawnEnPassant(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece, private val attackPiece: Piece)
  extends PawnAttackMove(board, destinationCoordinate, movedPiece, attackPiece) {

  override def equals(obj: Any): Boolean = {
//    this == obj || obj.isInstanceOf[PawnEnPassant] && super.equals(obj)
    obj.isInstanceOf[PawnEnPassant] && super.equals(obj)
  }

  override def execute: Board = {
    val builder: Builder = new Builder()

    for (piece <- board.getCurrentPlayer.getActivePieces) {
      if (!movedPiece.equals(piece)) {
        builder.setPiece(piece)
      }
    }

    for (piece <- board.getCurrentPlayer.getOpponent.getActivePieces) {
      if (!piece.equals(this.getAttackedPiece)) {
        builder.setPiece(piece)
      }
    }

    builder.setPiece(movedPiece.movePiece(this))
    builder.setMoveMaker(board.getCurrentPlayer.getOpponent.getTeam)
    builder.build
  }
}

class PawnJump(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece)
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

abstract class CastleMove(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece, private val castleRook: Rook, private val castleRookStart: Int, private val castleRookDestination: Int)
  extends Move(board, destinationCoordinate, movedPiece) {

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

  override def hashCode: Int = {
    val prime = 31
    var result = super.hashCode

    result = prime * result + castleRook.hashCode
    result = prime * result + castleRookDestination
    result
  }

  override def equals(obj: Any): Boolean = {
    if (this == obj) return true
    if (!obj.isInstanceOf[CastleMove]) return false

    val move: CastleMove = obj.asInstanceOf[CastleMove]
    super.equals(move) && castleRook.equals(move.getCastleRook)
  }
}

class KingSideCastle(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece, private val castleRook: Rook, private val castleRookStart: Int, private val castleRookDestination: Int)
  extends CastleMove(board, destinationCoordinate, movedPiece, castleRook, castleRookStart, castleRookDestination) {

  override def equals(obj: Any): Boolean = {
    this == obj || obj.isInstanceOf[KingSideCastle] && super.equals(obj)
  }

  override def toString: String = "O-O"
}

class QueenSideCastle(private val board: Board, private val destinationCoordinate: Int, private val movedPiece: Piece, private val castleRook: Rook, private val castleRookStart: Int, private val castleRookDestination: Int)
  extends CastleMove(board, destinationCoordinate, movedPiece, castleRook, castleRookStart, castleRookDestination) {

  override def equals(obj: Any): Boolean = {
    this == obj || obj.isInstanceOf[QueenSideCastle] && super.equals(obj)
  }

  override def toString: String = "O-O-O"
}

case class InvalidMove()
  extends Move(null, -1) {

  override def getCurrentCoordinate: Int = -1

  override def execute: Board = {
    throw new RuntimeException("Cannot execute an invalid move!")
  }
}

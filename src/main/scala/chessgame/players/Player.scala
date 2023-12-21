package chessgame.players

import chessgame.Team.Team
import chessgame.board.{Board, Move}
import chessgame.pieces.{King, Piece}
import chessgame.players.MoveStatus._

abstract class Player(private val board: Board, private var legalMoves: Set[Move], private val opponentMoves: Set[Move]) {
  val playerKing: King = establishKing
  legalMoves ++= calculateKingCastles(legalMoves, opponentMoves)
  val inCheck = Player.calculateAttacksOnTile(playerKing.getPiecePosition, opponentMoves).nonEmpty

  private def establishKing: King = {
    for (piece <- getActivePieces) {
      if (piece.getPieceType.isKing) {
        return piece.asInstanceOf[King]
      }
    }

    throw new RuntimeException("Invalid board: no king found!")
  }

  def isMoveLegal(move: Move): Boolean = {
    legalMoves.contains(move)
  }

  def isInCheck: Boolean = inCheck

  def isInCheckmate: Boolean = inCheck && !hasEscapeMoves

  def isInStalemate: Boolean = inCheck && !hasEscapeMoves

  def isCastled: Boolean = false

  def makeMove(move: Move): MoveTransition = {
    if (!isMoveLegal(move)) {
      return MoveTransition(board, move, ILLEGAL_MOVE)
    }

    val transitionBoard = move.execute
    val kingAttacks = Player.calculateAttacksOnTile(transitionBoard.getCurrentPlayer.getOpponent.playerKing.getPiecePosition, transitionBoard.getCurrentPlayer.legalMoves)

    if (kingAttacks.nonEmpty) {
      return MoveTransition(board, move, LEAVES_PLAYER_IN_CHECK)
    }

    MoveTransition(transitionBoard, move, DONE)
  }

  def hasEscapeMoves: Boolean = {
    for (move <- legalMoves) {
      val transition = makeMove(move)
      if (transition.getMoveStatus.isDone) {
        return true
      }
    }

    false
  }

  def getActivePieces: Set[Piece]

  def getTeam: Team

  def getOpponent: Player

  def getPlayerKing: King = playerKing

  def calculateKingCastles(playerLegals: Set[Move], opponentLegals: Set[Move]): Set[Move]
}

object Player {
  def calculateAttacksOnTile(piecePosition: Int, moves: Set[Move]): Set[Move] = {
    var attackMoves: Set[Move] = Set()

    for (move <- moves) {
      if (piecePosition == move.getDestinationCoordinate) {
        attackMoves += move
      }
    }

    attackMoves
  }
}

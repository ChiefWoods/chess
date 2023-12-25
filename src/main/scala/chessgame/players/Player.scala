package chessgame.players

import Team.Team
import chessgame.board.Board
import chessgame.moves
import chessgame.moves.{Move, MoveTransition}
import chessgame.pieces.{King, Piece}
import chessgame.moves.MoveStatus._

abstract class Player(private val board: Board,
                      private var legalMoves: Set[Move],
                      private val opponentMoves: Set[Move]) {

	private val playerKing: King = establishKing
	legalMoves ++= calculateKingCastles(legalMoves, opponentMoves)
	private val inCheck = Player.calculateAttacksOnTile(playerKing.getPiecePosition, opponentMoves).nonEmpty

	def getLegalMoves: Set[Move] = legalMoves

	def getPlayerKing: King = playerKing

	def getTeam: Team

	def getActivePieces: Set[Piece]

	def getOpponent: Player

	def isInCheck: Boolean = inCheck

	def isInCheckmate: Boolean = inCheck && !hasEscapeMoves

	def isInStalemate: Boolean = !inCheck && !hasEscapeMoves

	def isMoveLegal(move: Move): Boolean = {
		legalMoves.contains(move)
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

	private def establishKing: King = {
		for (piece <- getActivePieces) {
			if (piece.getPieceType.isKing) {
				return piece.asInstanceOf[King]
			}
		}

		throw new RuntimeException("Invalid board: no king found!")
	}

	def makeMove(move: Move): MoveTransition = {
		if (!isMoveLegal(move)) {
			return moves.MoveTransition(board, move, ILLEGAL_MOVE)
		}

		val transitionBoard = move.execute
		val kingAttacks = Player.calculateAttacksOnTile(transitionBoard.getCurrentPlayer.getOpponent.playerKing.getPiecePosition, transitionBoard.getCurrentPlayer.legalMoves)

		if (kingAttacks.nonEmpty) {
			return moves.MoveTransition(board, move, LEAVES_PLAYER_IN_CHECK)
		}

		moves.MoveTransition(transitionBoard, move, DONE)
	}

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

package chessgame.pieces

import chessgame.players.Team._
import chessgame.board._
import chessgame.moves
import chessgame.moves.{Move, PawnAttackMove, PawnEnPassant, PawnJump, PawnMove, PawnPromotion}
import chessgame.pieces.Piece._
import chessgame.players.Team

case class Pawn(private val team: Team.Team,
                private val piecePosition: Int,
                private val isFirstMove: Boolean = true)
	extends Piece(team, PAWN, piecePosition, isFirstMove) {

	private val CANDIDATE_MOVE_COORDINATES: List[Int] = List(8, 16, 7, 9)

	override def calculateLegalMoves(board: Board): Set[Move] = {
		var legalMoves: Set[Move] = Set()

		for (currentCandidateOffset <- CANDIDATE_MOVE_COORDINATES) {
			val candidateDestinationCoordinate = piecePosition + (currentCandidateOffset * team.getDirection)

			if (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
				if (currentCandidateOffset == 8 &&
					!board.getTile(candidateDestinationCoordinate).isTileOccupied) {
					if (team.isPawnPromotionTile(candidateDestinationCoordinate)) {
						legalMoves += PawnPromotion(PawnMove(board, candidateDestinationCoordinate, this), board, candidateDestinationCoordinate, this)
					} else {
						legalMoves += moves.PawnMove(board, candidateDestinationCoordinate, this)
					}
				} else if ((currentCandidateOffset == 16 && getIsFirstMove) &&
					((Board.SECOND_ROW(piecePosition) && team.isBlack) || (Board.SEVENTH_ROW(piecePosition) && team.isWhite))) {
					val behindCandidateDestinationCoordinate = piecePosition + (8 * team.getDirection)

					if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied &&
						!board.getTile(candidateDestinationCoordinate).isTileOccupied) {
						legalMoves += PawnJump(board, candidateDestinationCoordinate, this)
					}
				} else if (currentCandidateOffset == 7 &&
					!((Board.EIGHTH_COLUMN(piecePosition) && team.isWhite) || (Board.FIRST_COLUMN(piecePosition) && team.isBlack))) {
					if (board.getTile(candidateDestinationCoordinate).isTileOccupied) {
						val pieceAtDestination: Piece = board.getTile(candidateDestinationCoordinate).getPiece

						if (team != pieceAtDestination.getPieceTeam) {
							if (team.isPawnPromotionTile(candidateDestinationCoordinate)) {
								legalMoves += moves.PawnPromotion(moves.PawnMove(board, candidateDestinationCoordinate, this), board, candidateDestinationCoordinate, this, pieceAtDestination)
							} else {
								legalMoves += PawnAttackMove(board, candidateDestinationCoordinate, this, pieceAtDestination)
							}
						}
					} else if (board.getEnPassantPawn != null) {
						if (board.getEnPassantPawn.getPiecePosition == (piecePosition + team.getOppositeDirection)) {
							val enPassantPawn: Pawn = board.getEnPassantPawn

							if (team != enPassantPawn.getPieceTeam) {
								legalMoves += PawnEnPassant(board, candidateDestinationCoordinate, this, enPassantPawn)
							}
						}
					}
				} else if (currentCandidateOffset == 9 &&
					!((Board.FIRST_COLUMN(piecePosition) && team.isWhite) || (Board.EIGHTH_COLUMN(piecePosition) && team.isBlack))) {
					if (board.getTile(candidateDestinationCoordinate).isTileOccupied) {
						val pieceAtDestination: Piece = board.getTile(candidateDestinationCoordinate).getPiece

						if (team != pieceAtDestination.getPieceTeam) {
							if (team.isPawnPromotionTile(candidateDestinationCoordinate)) {
								legalMoves += moves.PawnPromotion(moves.PawnMove(board, candidateDestinationCoordinate, this), board, candidateDestinationCoordinate, this, pieceAtDestination)
							} else {
								legalMoves += moves.PawnAttackMove(board, candidateDestinationCoordinate, this, pieceAtDestination)
							}
						}
					} else if (board.getEnPassantPawn != null) {
						if (board.getEnPassantPawn.getPiecePosition == (piecePosition - team.getOppositeDirection)) {
							val enPassantPawn: Pawn = board.getEnPassantPawn

							if (team != enPassantPawn.getPieceTeam) {
								legalMoves += moves.PawnEnPassant(board, candidateDestinationCoordinate, this, enPassantPawn)
							}
						}
					}
				}
			}
		}

		legalMoves
	}

	override def movePiece(move: Move): Pawn = {
		Pawn(move.getMovedPiece.getPieceTeam, move.getDestinationCoordinate, false)
	}

	def getPromotionPiece: Piece = Queen(team, piecePosition, false)

	override def toString: String = PAWN.toChar
}

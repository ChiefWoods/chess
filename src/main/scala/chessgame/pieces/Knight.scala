package chessgame.pieces

import chessgame.board._
import chessgame.moves.{MajorAttackMove, MajorMove, Move}
import chessgame.pieces.Piece._
import chessgame.players.Team

case class Knight(private val team: Team.Team,
                  private val piecePosition: Int,
                  private val isFirstMove: Boolean = true)
	extends Piece(team, KNIGHT, piecePosition, isFirstMove) {

	private val CANDIDATE_MOVE_COORDINATES: List[Int] = List(-17, -15, -10, -6, 6, 10, 15, 17)

	override def calculateLegalMoves(board: Board): Set[Move] = {
		var candidateDestinationCoordinate: Int = piecePosition
		var legalMoves: Set[Move] = Set()

		for (currentCandidateOffset <- CANDIDATE_MOVE_COORDINATES) {
			candidateDestinationCoordinate = piecePosition + currentCandidateOffset

			if (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
				if (!isFirstColumnExclusion(piecePosition, currentCandidateOffset) &&
					!isSecondColumnExclusion(piecePosition, currentCandidateOffset) &&
					!isSevenColumnExclusion(piecePosition, currentCandidateOffset) &&
					!isEighthColumnExclusion(piecePosition, currentCandidateOffset)) {
					val candidateDestinationTile: Tile = board.getTile(candidateDestinationCoordinate)

					if (!candidateDestinationTile.isTileOccupied) {
						legalMoves += MajorMove(board, candidateDestinationCoordinate, this)
					} else {
						val pieceAtDestination: Piece = candidateDestinationTile.getPiece
						val pieceTeam: Team.Team = pieceAtDestination.getPieceTeam

						if (team != pieceTeam) {
							legalMoves += MajorAttackMove(board, candidateDestinationCoordinate, this, pieceAtDestination)
						}
					}
				}
			}
		}

		legalMoves
	}

	override def movePiece(move: Move): Knight = {
		Knight(move.getMovedPiece.getPieceTeam, move.getDestinationCoordinate, false)
	}

	private def isFirstColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
		Board.FIRST_COLUMN(currentPosition) && (candidateOffset == -17 || candidateOffset == -10 || candidateOffset == 6 || candidateOffset == 15)
	}

	private def isSecondColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
		Board.SECOND_COLUMN(currentPosition) && (candidateOffset == -10 || candidateOffset == 6)
	}

	private def isSevenColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
		Board.SEVENTH_COLUMN(currentPosition) && (candidateOffset == -6 || candidateOffset == 10)
	}

	private def isEighthColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
		Board.EIGHTH_COLUMN(currentPosition) && (candidateOffset == -15 || candidateOffset == -6 || candidateOffset == 10 || candidateOffset == 17)
	}

	override def toString: String = KNIGHT.toChar
}

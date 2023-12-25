package chessgame.pieces

import chessgame.board._
import chessgame.moves.{MajorAttackMove, MajorMove, Move}
import chessgame.pieces.Piece._
import chessgame.players.Team

case class King(private val team: Team.Team,
                private val piecePosition: Int,
                private var isFirstMove: Boolean = true)
	extends Piece(team, KING, piecePosition, isFirstMove) {

	private val CANDIDATE_MOVE_COORDINATES: List[Int] = List(-9, -8, -7, -1, 1, 7, 8, 9)

	override def calculateLegalMoves(board: Board): Set[Move] = {
		var legalMoves: Set[Move] = Set()

		for (currentCandidateOffset <- CANDIDATE_MOVE_COORDINATES) {
			val candidateDestinationCoordinate = piecePosition + currentCandidateOffset

			if (!isFirstColumnExclusion(piecePosition, currentCandidateOffset) &&
				!isEighthColumnExclusion(piecePosition, currentCandidateOffset)) {
				if (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
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

	override def movePiece(move: Move): King = {
		King(move.getMovedPiece.getPieceTeam, move.getDestinationCoordinate, false)
	}

	private def isFirstColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
		Board.FIRST_COLUMN(currentPosition) && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7)
	}

	private def isEighthColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
		Board.EIGHTH_COLUMN(currentPosition) && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9)
	}

	override def toString: String = KING.toChar
}

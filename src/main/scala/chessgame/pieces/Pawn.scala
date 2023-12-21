package chessgame.pieces

import chessgame.Team
import chessgame.Team._
import chessgame.board.{AttackMove, Board, MajorMove, Move}
import chessgame.pieces.Piece._

class Pawn(private val team: Team.Team, private val piecePosition: Int) extends Piece(team, PAWN, piecePosition) {
  val CANDIDATE_MOVE_COORDINATES: List[Int] = List(8, 16, 7, 9)

  override def calculateLegalMoves(board: Board): Set[Move] = {
    var legalMoves: Set[Move] = Set()

    for (currentCandidateOffset <- CANDIDATE_MOVE_COORDINATES) {
      val candidateDestinationCoordinate = piecePosition + (currentCandidateOffset * team.getDirection)

      if (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
        if (currentCandidateOffset == 8 && !board.getTile(candidateDestinationCoordinate).isTileOccupied) {
          legalMoves += new MajorMove(board, this, candidateDestinationCoordinate)
        } else if (currentCandidateOffset == 16 && getIsFirstMove && (Board.SECOND_ROW(piecePosition) && team.isBlack) || (Board.SEVENTH_ROW(piecePosition) && team.isWhite)) {
          val behindCandidateDestinationCoordinate = piecePosition + (8 * team.getDirection)

          if (!board.getTile(behindCandidateDestinationCoordinate).isTileOccupied && !board.getTile(candidateDestinationCoordinate).isTileOccupied) {
            legalMoves += new MajorMove(board, this, candidateDestinationCoordinate)
          }
        } else if (currentCandidateOffset == 7 && !((Board.EIGHTH_COLUMN(piecePosition) && team.isWhite) || (Board.FIRST_COLUMN(piecePosition) && team.isBlack))) {
          if (board.getTile(candidateDestinationCoordinate).isTileOccupied) {
            val pieceAtDestination: Piece = board.getTile(candidateDestinationCoordinate).getPiece

            if (team != pieceAtDestination.getPieceTeam) {
              legalMoves += new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)
            }
          }
        } else if (currentCandidateOffset == 9 && !((Board.FIRST_COLUMN(piecePosition) && team.isWhite) || (Board.EIGHTH_COLUMN(piecePosition) && team.isBlack))) {
          if (board.getTile(candidateDestinationCoordinate).isTileOccupied) {
            val pieceAtDestination: Piece = board.getTile(candidateDestinationCoordinate).getPiece

            if (team != pieceAtDestination.getPieceTeam) {
              legalMoves += new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)
            }
          }
        }
      }
    }

    legalMoves
  }

  override def movePiece(move: Move): Pawn = {
    new Pawn(move.getMovedPiece.getPieceTeam, move.getDestinationCoordinate)
  }

  override def toString: String = PAWN.toChar
}

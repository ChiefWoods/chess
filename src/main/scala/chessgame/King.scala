package chessgame

import chessgame.Piece._
import chessgame.Team._

class King(team: Team.Team, piecePosition: Int) extends Piece(team, piecePosition) {
  val CANDIDATE_MOVE_COORDINATES: List[Int] = List(-9, -8, -7, -1, 1, 7, 8, 9)

  override def calculateLegalMoves(board: Board): Set[Move] = {
    var legalMoves: Set[Move] = Set()

    for (currentCandidateOffset <- CANDIDATE_MOVE_COORDINATES) {
      val candidateDestinationCoordinate = piecePosition + currentCandidateOffset

      if (!isFirstColumnExclusion(piecePosition, currentCandidateOffset) &&
        !isEighthColumnExclusion(piecePosition, currentCandidateOffset)) {
        if (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
          val candidateDestinationTile: Tile = board.getTile(candidateDestinationCoordinate)

          if (!candidateDestinationTile.isTileOccupied) {
            legalMoves += MajorMove(board, this, candidateDestinationCoordinate)
          } else {
            val pieceAtDestination: Piece = candidateDestinationTile.getPiece
            val pieceTeam: Team = pieceAtDestination.getPieceTeam

            if (team != pieceTeam) {
              legalMoves += AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)
            }
          }
        }
      }
    }

    legalMoves
  }

  def isFirstColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.FIRST_COLUMN(currentPosition) && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7)
  }

  def isEighthColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.EIGHTH_COLUMN(currentPosition) && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9)
  }

  override def toString: String = KING.toChar
}

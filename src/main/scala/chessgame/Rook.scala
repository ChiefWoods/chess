package chessgame

import chessgame.Team.Team

class Rook(team: Team, piecePosition: Int) extends Piece(team, piecePosition) {
  val CANDIDATE_MOVE_COORDINATES: List[Int] = List(-8, -1, 1, 8)

  override def calculateLegalMoves(board: Board): Set[Move] = {
    val legalMoves: Set[Move] = Set()

    for (currentCandidateOffset <- CANDIDATE_MOVE_COORDINATES) {
      var candidateDestinationCoordinate = piecePosition

      while (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
        if (!isFirstColumnExclusion(piecePosition, currentCandidateOffset) &&
          !isEighthColumnExclusion(piecePosition, currentCandidateOffset)) {
          candidateDestinationCoordinate += currentCandidateOffset

          if (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
            val candidateDestinationTile: Tile = board.getTile(candidateDestinationCoordinate)

            if (!candidateDestinationTile.isTileOccupied) {
              legalMoves + MajorMove(board, this, candidateDestinationCoordinate)
            } else {
              val pieceAtDestination: Piece = candidateDestinationTile.getPiece
              val pieceTeam: Team = pieceAtDestination.getPieceTeam

              if (team != pieceTeam) {
                legalMoves + AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)
              }
              candidateDestinationCoordinate = -1
            }
          }
        }
      }
    }

    legalMoves
  }

  def isFirstColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.FIRST_COLUMN(currentPosition) && (candidateOffset == -1)
  }

  def isEighthColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.EIGHTH_COLUMN(currentPosition) && (candidateOffset == 1)
  }
}

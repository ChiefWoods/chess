package chessgame

import chessgame.Team.Team

class Bishop(team: Team, piecePosition: Int) extends Piece(team, piecePosition) {
  val CANDIDATE_MOVE_COORDINATES: List[Int] = List(-9, -7, 7, 9)

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
    Board.FIRST_COLUMN(currentPosition) && (candidateOffset == -9 || candidateOffset == 7)
  }

  def isEighthColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.EIGHTH_COLUMN(currentPosition) && (candidateOffset == -7 || candidateOffset == 9)
  }
}
package chessgame

import chessgame.Piece._
import chessgame.Team._

class Knight(team: Team.Team, piecePosition: Int) extends Piece(team, piecePosition) {
  val CANDIDATE_MOVE_COORDINATES: List[Int] = List(-17, -15, -10, -6, 6, 10, 15, 17)

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
    Board.FIRST_COLUMN(currentPosition) && (candidateOffset == -17 || candidateOffset == -10 || candidateOffset == 6 || candidateOffset == 15)
  }

  def isSecondColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.SECOND_COLUMN(currentPosition) && (candidateOffset == -10 || candidateOffset == 6)
  }

  def isSevenColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.SEVENTH_COLUMN(currentPosition) && (candidateOffset == -6 || candidateOffset == 10)
  }

  def isEighthColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.EIGHTH_COLUMN(currentPosition) && (candidateOffset == -15 || candidateOffset == -6 || candidateOffset == 10 || candidateOffset == 17)
  }

  override def toString: String = KNIGHT.toChar
}

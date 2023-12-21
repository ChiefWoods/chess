package chessgame.pieces

import chessgame.Team
import chessgame.Team._
import chessgame.board._
import chessgame.pieces.Piece._

class Queen(private val team: Team.Team, private val piecePosition: Int) extends Piece(team, QUEEN, piecePosition) {
  val CANDIDATE_MOVE_COORDINATES: List[Int] = List(-9, -8, -7, -1, 1, 7, 8, 9)

  override def calculateLegalMoves(board: Board): Set[Move] = {
    var legalMoves: Set[Move] = Set()

    for (currentCandidateOffset <- CANDIDATE_MOVE_COORDINATES) {
      var candidateDestinationCoordinate = piecePosition

      while (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
        if (!isFirstColumnExclusion(piecePosition, currentCandidateOffset) &&
          !isEighthColumnExclusion(piecePosition, currentCandidateOffset)) {
          candidateDestinationCoordinate += currentCandidateOffset

          if (Board.isValidTileCoordinate(candidateDestinationCoordinate)) {
            val candidateDestinationTile: Tile = board.getTile(candidateDestinationCoordinate)

            if (!candidateDestinationTile.isTileOccupied) {
              legalMoves += new MajorMove(board, this, candidateDestinationCoordinate)
            } else {
              val pieceAtDestination: Piece = candidateDestinationTile.getPiece
              val pieceTeam: Team = pieceAtDestination.getPieceTeam

              if (team != pieceTeam) {
                legalMoves += new AttackMove(board, this, candidateDestinationCoordinate, pieceAtDestination)
              }
              candidateDestinationCoordinate = -1
            }
          }
        } else {
          candidateDestinationCoordinate = -1
        }
      }
    }

    legalMoves
  }

  override def movePiece(move: Move): Queen = {
    new Queen(move.getMovedPiece.getPieceTeam, move.getDestinationCoordinate)
  }

  def isFirstColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.FIRST_COLUMN(currentPosition) && (candidateOffset == -9 || candidateOffset == -1 || candidateOffset == 7)
  }

  def isEighthColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.EIGHTH_COLUMN(currentPosition) && (candidateOffset == -7 || candidateOffset == 1 || candidateOffset == 9)
  }

  override def toString: String = QUEEN.toChar
}

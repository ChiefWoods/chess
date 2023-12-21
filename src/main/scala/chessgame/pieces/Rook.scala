package chessgame.pieces

import chessgame.Team
import chessgame.Team._
import chessgame.board._
import chessgame.pieces.Piece._

class Rook(private val team: Team.Team, private val piecePosition: Int) extends Piece(team, ROOK, piecePosition) {
  val CANDIDATE_MOVE_COORDINATES: List[Int] = List(-8, -1, 1, 8)

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

  override def movePiece(move: Move): Rook = {
    new Rook(move.getMovedPiece.getPieceTeam, move.getDestinationCoordinate)
  }

  def isFirstColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.FIRST_COLUMN(currentPosition) && (candidateOffset == -1)
  }

  def isEighthColumnExclusion(currentPosition: Int, candidateOffset: Int): Boolean = {
    Board.EIGHTH_COLUMN(currentPosition) && (candidateOffset == 1)
  }

  override def toString: String = ROOK.toChar
}

package chessgame

trait Move {
  def board: Board
  def movedPiece: Piece
  def destinationCoordinate: Int
}

case class MajorMove(board: Board, movedPiece: Piece, destinationCoordinate: Int) extends Move

case class AttackMove(board: Board, movedPiece: Piece, destinationCoordinate: Int, attackedPiece: Piece) extends Move

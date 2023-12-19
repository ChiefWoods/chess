package chessgame

abstract class Tile(val tileCoordinate: Int) {
  def isTileOccupied: Boolean
  def getPiece: Piece
}

object Tile {
  def createTile(tileCoordinate: Int, pieceOnTile: Piece): Tile = {
    if (pieceOnTile == null) {
      new EmptyTile(tileCoordinate)
    } else {
      new OccupiedTile(tileCoordinate, pieceOnTile)
    }
  }
}

class EmptyTile(tileCoordinate: Int) extends Tile(tileCoordinate) {
  override def isTileOccupied: Boolean = false
  override def getPiece: Piece = null
}

class OccupiedTile(tileCoordinate: Int, val pieceOnTile: Piece) extends Tile(tileCoordinate) {
  override def isTileOccupied: Boolean = true
  override def getPiece: Piece = pieceOnTile
}

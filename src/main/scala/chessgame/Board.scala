package chessgame

case class Board() {
  def getTile(coordinate: Int): Tile = null
}

object Board {
  val TILES_COUNT: Int = 64
  val TILES_PER_ROW: Int = 8
  val FIRST_COLUMN: Array[Boolean] = initColumn(0)
  val SECOND_COLUMN: Array[Boolean] = initColumn(1)
  val SEVENTH_COLUMN: Array[Boolean] = initColumn(6)
  val EIGHTH_COLUMN: Array[Boolean] = initColumn(7)

  def initColumn(columnNumber: Int): Array[Boolean] = {
    val column: Array[Boolean] = new Array[Boolean](TILES_COUNT)
    var currentNumber = columnNumber

    while (currentNumber < TILES_COUNT) {
      column(currentNumber) = true
      currentNumber += TILES_PER_ROW
    }

    column
  }

  def isValidTileCoordinate(coordinate: Int): Boolean = {
    coordinate >= 0 && coordinate < Board.TILES_COUNT
  }
}

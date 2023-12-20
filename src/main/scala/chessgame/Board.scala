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
  val SECOND_ROW : Array[Boolean] = initRow(0)
  val SEVENTH_ROW : Array[Boolean] = initRow(6)

  def initColumn(columnNumber: Int): Array[Boolean] = {
    val column: Array[Boolean] = new Array[Boolean](TILES_COUNT)
    var currentNumber = columnNumber

    while (currentNumber < TILES_COUNT) {
      column(currentNumber) = true
      currentNumber += TILES_PER_ROW
    }

    column
  }

  def initRow(rowNumber: Int) : Array[Boolean] = {
    val row : Array[Boolean] = new Array[Boolean](TILES_COUNT)

    var currentNumber = rowNumber * TILES_PER_ROW

    while (currentNumber < (rowNumber + 1) * TILES_PER_ROW) {
      row(currentNumber) = true
      currentNumber += 1
    }

    row
  }

  def isValidTileCoordinate(coordinate : Int) : Boolean = {
    coordinate >= 0 && coordinate < TILES_COUNT
  }
}

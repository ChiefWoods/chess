package chessgame

import chessgame.Team._
import scala.collection.mutable

case class Board(builder: Builder) {
  val gameBoard: List[Tile] = Board.createGameBoard(builder)
  val whitePieces: Set[Piece] = calculateActivePieces(gameBoard, WHITE)
  val blackPieces: Set[Piece] = calculateActivePieces(gameBoard, BLACK)
  val whiteLegalMoves: Set[Move] = calculateLegalMoves(whitePieces)
  val blackLegalMoves: Set[Move] = calculateLegalMoves(blackPieces)

  private def calculateActivePieces(gameBoard: List[Tile], team: Team.Team): Set[Piece] = {
    gameBoard.filter(_.isTileOccupied).map(_.getPiece).filter(_.getPieceTeam == team).toSet
  }

  private def calculateLegalMoves(pieces: Set[Piece]): Set[Move] = {
    var legalMoves: Set[Move] = Set()

    for (piece <- pieces) {
      val pieceMoves = piece.calculateLegalMoves(this)
      legalMoves = legalMoves ++ pieceMoves
    }

    legalMoves
  }

  def getTile(coordinate: Int): Tile = {
    gameBoard(coordinate)
  }

  override def toString: String = {
    val builder: StringBuilder = new StringBuilder()

    for (i <- 0 until Board.TILES_COUNT) {
      val tileText: String = gameBoard(i).toString
      builder.append(String.format("%3s", tileText))

      if ((i + 1) % Board.TILES_PER_ROW == 0) {
        builder.append("\n")
      }
    }

    builder.toString()
  }
}

object Board {
  val TILES_COUNT: Int = 64
  val TILES_PER_ROW: Int = 8
  val FIRST_COLUMN: Array[Boolean] = initColumn(0)
  val SECOND_COLUMN: Array[Boolean] = initColumn(1)
  val SEVENTH_COLUMN: Array[Boolean] = initColumn(6)
  val EIGHTH_COLUMN: Array[Boolean] = initColumn(7)
  val SECOND_ROW: Array[Boolean] = initRow(0)
  val SEVENTH_ROW: Array[Boolean] = initRow(6)

  def initColumn(columnNumber: Int): Array[Boolean] = {
    val column: Array[Boolean] = new Array[Boolean](TILES_COUNT)
    var currentNumber = columnNumber

    while (currentNumber < TILES_COUNT) {
      column(currentNumber) = true
      currentNumber += TILES_PER_ROW
    }

    column
  }

  def initRow(rowNumber: Int): Array[Boolean] = {
    val row: Array[Boolean] = new Array[Boolean](TILES_COUNT)

    var currentNumber = rowNumber * TILES_PER_ROW

    while (currentNumber < (rowNumber + 1) * TILES_PER_ROW) {
      row(currentNumber) = true
      currentNumber += 1
    }

    row
  }

  def isValidTileCoordinate(coordinate: Int): Boolean = {
    coordinate >= 0 && coordinate < TILES_COUNT
  }

  def createGameBoard(builder: Builder): List[Tile] = {
    val tiles: Array[Tile] = new Array[Tile](Board.TILES_COUNT)

    for (i <- 0 until Board.TILES_COUNT) {
      tiles(i) = Tile.createTile(i, builder.boardConfig.getOrElse(i, null))
    }

    tiles.toList
  }

  def createStandardBoard: Board = {
    val builder = new Builder()

    builder.setPiece(new Rook(BLACK, 0))
    builder.setPiece(new Knight(BLACK, 1))
    builder.setPiece(new Bishop(BLACK, 2))
    builder.setPiece(new Queen(BLACK, 3))
    builder.setPiece(new King(BLACK, 4))
    builder.setPiece(new Bishop(BLACK, 5))
    builder.setPiece(new Knight(BLACK, 6))
    builder.setPiece(new Rook(BLACK, 7))
    builder.setPiece(new Pawn(BLACK, 8))
    builder.setPiece(new Pawn(BLACK, 9))
    builder.setPiece(new Pawn(BLACK, 10))
    builder.setPiece(new Pawn(BLACK, 11))
    builder.setPiece(new Pawn(BLACK, 12))
    builder.setPiece(new Pawn(BLACK, 13))
    builder.setPiece(new Pawn(BLACK, 14))
    builder.setPiece(new Pawn(BLACK, 15))

    builder.setPiece(new Pawn(WHITE, 48))
    builder.setPiece(new Pawn(WHITE, 49))
    builder.setPiece(new Pawn(WHITE, 50))
    builder.setPiece(new Pawn(WHITE, 51))
    builder.setPiece(new Pawn(WHITE, 52))
    builder.setPiece(new Pawn(WHITE, 53))
    builder.setPiece(new Pawn(WHITE, 54))
    builder.setPiece(new Pawn(WHITE, 55))
    builder.setPiece(new Rook(WHITE, 56))
    builder.setPiece(new Knight(WHITE, 57))
    builder.setPiece(new Bishop(WHITE, 58))
    builder.setPiece(new Queen(WHITE, 59))
    builder.setPiece(new King(WHITE, 60))
    builder.setPiece(new Bishop(WHITE, 61))
    builder.setPiece(new Knight(WHITE, 62))
    builder.setPiece(new Rook(WHITE, 63))

    builder.build
  }
}

class Builder() {
  var boardConfig: mutable.Map[Int, Piece] = mutable.Map()
  var nextMoveMaker: Team.Team = WHITE

  def setPiece(piece: Piece): Builder = {
    boardConfig += (piece.getPiecePosition -> piece)
    this
  }

  def setMoveMaker(nextMoveMaker: Team): Builder = {
    this.nextMoveMaker = nextMoveMaker
    this
  }

  def build: Board = {
    new Board(this)
  }
}

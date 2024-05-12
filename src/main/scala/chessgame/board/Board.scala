package chessgame.board

import chessgame.moves.Move
import chessgame.players.Team._
import chessgame.pieces._
import chessgame.players.{BlackPlayer, Player, Team, WhitePlayer}

import scala.collection.mutable

case class Board(private val builder: Builder) {
	private val gameBoard: List[Tile] = Board.createGameBoard(builder)
	private val whitePieces: Set[Piece] = calculateActivePieces(gameBoard, WHITE)
	private val blackPieces: Set[Piece] = calculateActivePieces(gameBoard, BLACK)
	private val enPassantPawn: Pawn = builder.getEnPassantPawn
	private val whiteLegalMoves: Set[Move] = calculateLegalMoves(whitePieces)
	private val blackLegalMoves: Set[Move] = calculateLegalMoves(blackPieces)
	private val whitePlayer: WhitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves)
	private val blackPlayer: BlackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves)
	private val currentPlayer: Player = builder.getNextMoveMaker.choosePlayer(whitePlayer, blackPlayer)

	def getWhitePieces: Set[Piece] = whitePieces

	def getBlackPieces: Set[Piece] = blackPieces

	def getEnPassantPawn: Pawn = enPassantPawn

	def getAllLegalMoves: Set[Move] = whitePlayer.getLegalMoves ++ blackPlayer.getLegalMoves

	def getWhitePlayer: WhitePlayer = whitePlayer

	def getBlackPlayer: BlackPlayer = blackPlayer

	def getCurrentPlayer: Player = currentPlayer

	def getTile(coordinate: Int): Tile = gameBoard(coordinate)

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
	val FIRST_ROW: Array[Boolean] = initRow(0)
	val SECOND_ROW: Array[Boolean] = initRow(1)
	val SEVENTH_ROW: Array[Boolean] = initRow(6)
	val EIGHTH_ROW: Array[Boolean] = initRow(7)
	val ALGEBRAIC_NOTATION: Array[String] = initAlgebraicNotation

	private def initColumn(columnNumber: Int): Array[Boolean] = {
		val column: Array[Boolean] = new Array[Boolean](TILES_COUNT)
		var currentNumber = columnNumber

		while (currentNumber < TILES_COUNT) {
			column(currentNumber) = true
			currentNumber += TILES_PER_ROW
		}

		column
	}

	private def initRow(rowNumber: Int): Array[Boolean] = {
		val row: Array[Boolean] = new Array[Boolean](TILES_COUNT)

		var currentNumber = rowNumber * TILES_PER_ROW

		while (currentNumber < (rowNumber + 1) * TILES_PER_ROW) {
			row(currentNumber) = true
			currentNumber += 1
		}

		row
	}

	private def initAlgebraicNotation: Array[String] = {
		val algebraicNotation: Array[String] = new Array[String](TILES_COUNT)

		var index = 0
		for (i <- TILES_PER_ROW - 1 to 0 by -1) {
			for (j <- 0 until TILES_PER_ROW) {
				algebraicNotation(index) = (97 + j).toChar.toString + (i + 1).toString
				index += 1
			}
		}

		algebraicNotation
	}

	def getPositionAtCoordinate(coordinate: Int): String = {
		ALGEBRAIC_NOTATION(coordinate)
	}

	def isValidTileCoordinate(coordinate: Int): Boolean = {
		coordinate >= 0 && coordinate < TILES_COUNT
	}

	def createGameBoard(builder: Builder): List[Tile] = {
		val tiles: Array[Tile] = new Array[Tile](Board.TILES_COUNT)

		for (i <- 0 until Board.TILES_COUNT) {
			tiles(i) = Tile.createTile(i, builder.getBoardConfig.getOrElse(i, null))
		}

		tiles.toList
	}

	def createStandardBoard: Board = {
		val builder = new Builder()

		builder.setPiece(Rook(BLACK, 0))
		builder.setPiece(Knight(BLACK, 1))
		builder.setPiece(Bishop(BLACK, 2))
		builder.setPiece(Queen(BLACK, 3))
		builder.setPiece(King(BLACK, 4))
		builder.setPiece(Bishop(BLACK, 5))
		builder.setPiece(Knight(BLACK, 6))
		builder.setPiece(Rook(BLACK, 7))
		builder.setPiece(Pawn(BLACK, 8))
		builder.setPiece(Pawn(BLACK, 9))
		builder.setPiece(Pawn(BLACK, 10))
		builder.setPiece(Pawn(BLACK, 11))
		builder.setPiece(Pawn(BLACK, 12))
		builder.setPiece(Pawn(BLACK, 13))
		builder.setPiece(Pawn(BLACK, 14))
		builder.setPiece(Pawn(BLACK, 15))

		builder.setPiece(Pawn(WHITE, 48))
		builder.setPiece(Pawn(WHITE, 49))
		builder.setPiece(Pawn(WHITE, 50))
		builder.setPiece(Pawn(WHITE, 51))
		builder.setPiece(Pawn(WHITE, 52))
		builder.setPiece(Pawn(WHITE, 53))
		builder.setPiece(Pawn(WHITE, 54))
		builder.setPiece(Pawn(WHITE, 55))
		builder.setPiece(Rook(WHITE, 56))
		builder.setPiece(Knight(WHITE, 57))
		builder.setPiece(Bishop(WHITE, 58))
		builder.setPiece(Queen(WHITE, 59))
		builder.setPiece(King(WHITE, 60))
		builder.setPiece(Bishop(WHITE, 61))
		builder.setPiece(Knight(WHITE, 62))
		builder.setPiece(Rook(WHITE, 63))

		builder.build
	}
}

class Builder {
	private var boardConfig: mutable.Map[Int, Piece] = mutable.Map()
	private var nextMoveMaker: Team.Team = WHITE
	private var enPassantPawn: Pawn = null

	def getBoardConfig: mutable.Map[Int, Piece] = boardConfig

	def getNextMoveMaker: Team.Team = nextMoveMaker

	def getEnPassantPawn: Pawn = enPassantPawn

	def setMoveMaker(moveMaker: Team.Team): Builder = {
		nextMoveMaker = moveMaker
		this
	}

	def setEnPassantPawn(movedPawn: Pawn) = enPassantPawn = movedPawn

	def setPiece(piece: Piece): Builder = {
		boardConfig += (piece.getPiecePosition -> piece)
		this
	}

	def build: Board = new Board(this)
}

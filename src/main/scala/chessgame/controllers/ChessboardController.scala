package chessgame.controllers;

import chessgame.Main
import chessgame.board.{Board, Move, Tile}
import chessgame.pieces.Piece
import javafx.scene.input.MouseButton.{PRIMARY, SECONDARY}
import javafx.scene.input.MouseEvent
import scalafxml.core.macros.sfxml

@sfxml
class ChessboardController {
	var board: Board = Board.createStandardBoard
	var sourceTile: Tile = null
	var destinationTile: Tile = null
	var movedPiece: Piece = null

	def handleTileClick(event: MouseEvent): Unit = {
		val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
		val tileId = button.getId.substring("tile_".length).toInt

		if (event.getButton == PRIMARY) {
			println(s"Left click! Tile Id: $tileId")

			if (sourceTile == null) {
				println("source tile null")
				sourceTile = board.getTile(tileId)
				movedPiece = sourceTile.getPiece
				if (movedPiece == null) {
					sourceTile = null
				}
				println(board)
				println(board.getCurrentPlayer)
			} else {
				println("moving piece")
				destinationTile = board.getTile(tileId)
				val move = Move.createMove(board, sourceTile.getTileCoordinate, destinationTile.getTileCoordinate)
				val transition = board.getCurrentPlayer.makeMove(move)

				if (transition.getMoveStatus.isDone) {
					board = transition.getTransitionBoard
				}
				sourceTile = null
				destinationTile = null
				movedPiece = null
				println(board)
				println(board.getCurrentPlayer)
				Main.showBoard(board)
			}
		} else if (event.getButton == SECONDARY) {
			println(s"Right click! Tile Id: $tileId")

			sourceTile = null
			destinationTile = null
			movedPiece = null
		}
	}

	def setBoard(updatedBoard: Board) = {
		board = updatedBoard
	}
}

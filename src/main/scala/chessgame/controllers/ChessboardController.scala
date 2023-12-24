package chessgame.controllers;

import chessgame.Main
import chessgame.Main.getClass
import chessgame.board.{Board, Move, Tile}
import chessgame.models.Stats
import chessgame.pieces.Piece
import javafx.scene.input.MouseButton.{PRIMARY, SECONDARY}
import javafx.scene.input.MouseEvent
import scalafx.scene.control.Alert
import scalafx.scene.image.{Image, ImageView}
import scalafx.scene.media.{Media, MediaPlayer}
import scalafxml.core.macros.sfxml

@sfxml
class ChessboardController {
	val moveSound = new Media(getClass.getResource("/audio/move.mp3").toString)
	val movePlayer = new MediaPlayer(moveSound)
	val captureSound = new Media(getClass.getResource("/audio/capture.mp3").toString)
	val capturePlayer = new MediaPlayer(captureSound)
	val checkSound = new Media(getClass.getResource("/audio/check.mp3").toString)
	val checkPlayer = new MediaPlayer(checkSound)
	val castleSound = new Media(getClass.getResource("/audio/castle.mp3").toString)
	val castlePlayer = new MediaPlayer(castleSound)
	val promoteSound = new Media(getClass.getResource("/audio/promote.mp3").toString)
	val promotePlayer = new MediaPlayer(promoteSound)

	var board: Board = Board.createStandardBoard
	var sourceTile: Tile = null
	var pieceToMove: Piece = null
	var destinationTile: Tile = null

	def handleTileClick(event: MouseEvent): Unit = {
		val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
		val gridPane = button.getParent.asInstanceOf[javafx.scene.layout.GridPane]
		val tileId = button.getId.substring("tile_".length).toInt
		var pieceLegalMoves: Set[Move] = null
		val opponentActivePieces: Set[Piece] = board.getCurrentPlayer.getOpponent.getActivePieces

		if (event.getButton == PRIMARY) {
			if (pieceToMove == null) {
				sourceTile = board.getTile(tileId)
				pieceToMove = sourceTile.getPiece

				if (pieceToMove == null || (pieceToMove.getPieceTeam != board.getCurrentPlayer.getTeam)) {
					sourceTile = null
					pieceToMove = null
				} else if (pieceToMove.getPieceTeam == board.getCurrentPlayer.getTeam) {
					pieceLegalMoves = board.getAllLegalMoves.filter(_.getMovedPiece == pieceToMove)
					highlightMoves(gridPane, pieceLegalMoves, opponentActivePieces)
				}
			} else {
				destinationTile = board.getTile(tileId)

				if (!destinationTile.isTileOccupied || opponentActivePieces.contains(destinationTile.getPiece)) {
					val move = Move.createMove(board, sourceTile.getTileCoordinate, destinationTile.getTileCoordinate)
					val transition = board.getCurrentPlayer.makeMove(move)

					if (move.isAttack) {
						Stats.incrementPiecesCaptured
					}

					if (transition.getMoveStatus.isDone) {
						println("Move successful!")
						playSound(move)
					} else if (transition.getMoveStatus.leavesPlayerInCheck) {
						println("Illegal move! King is in check!")
					} else {
						println("Illegal move!")
					}

					sourceTile = null
					pieceToMove = null
					destinationTile = null

					board = transition.getTransitionBoard
					Main.showBoard(board)
					println(move.toString)

					if (board.getCurrentPlayer.isInCheckmate) {
						checkPlayer.stop()
						checkPlayer.play()

						val winner = board.getCurrentPlayer.getOpponent.getTeam.toString

						new Alert(Alert.AlertType.Information) {
							initOwner(Main.stage)
							title = "Game Ended"
							headerText = "Checkmate!"
							contentText = s"$winner wins!"
							graphic = new ImageView() {
								image = new Image(getClass.getResourceAsStream(s"/images/chess/$winner.png"))
								fitWidth = 50
								fitHeight = 50
							}
						}.showAndWait()

						disableAllBtns(gridPane)
					} else if (board.getCurrentPlayer.isInStalemate) {
						checkPlayer.stop()
						checkPlayer.play()

						new Alert(Alert.AlertType.Information) {
							initOwner(Main.stage)
							title = "Game Ended"
							headerText = "Stalemate!"
							contentText = "It's a draw!"
							graphic = new ImageView() {
								image = new Image(getClass.getResourceAsStream(s"/images/chess/basic.png"))
								fitWidth = 50
								fitHeight = 50
							}
						}.showAndWait()

						disableAllBtns(gridPane)
					}
				} else if (destinationTile.getPiece.getPieceTeam == board.getCurrentPlayer.getTeam && destinationTile.getPiece == pieceToMove) {
					sourceTile = null
					pieceToMove = null
					destinationTile = null
					unhighlightMoves(gridPane)
				} else {
					sourceTile = board.getTile(tileId)
					pieceToMove = sourceTile.getPiece
					pieceLegalMoves = board.getAllLegalMoves.filter(_.getMovedPiece == pieceToMove)
					unhighlightMoves(gridPane)
					highlightMoves(gridPane, pieceLegalMoves, opponentActivePieces)
				}
			}
		} else if (event.getButton == SECONDARY) {
			sourceTile = null
			pieceToMove = null
			unhighlightMoves(gridPane)
		}
	}

	def highlightMoves(gridPane: javafx.scene.layout.GridPane, legalMoves: Set[Move], opponentActivePieces: Set[Piece]): Unit = {
		for (move <- legalMoves) {
			val tileId = move.getDestinationCoordinate
			val button = gridPane.getChildren.get(tileId).asInstanceOf[javafx.scene.control.Button]

			if (opponentActivePieces.contains(board.getTile(tileId).getPiece)) {
				button.getStyleClass.add("attack-move")
			} else {
				button.getStyleClass.add("major-move")
			}
		}
	}

	def unhighlightMoves(gridPane: javafx.scene.layout.GridPane): Unit = {
		for (tileId <- 0 until Board.TILES_COUNT) {
			val button = gridPane.getChildren.get(tileId).asInstanceOf[javafx.scene.control.Button]
			button.getStyleClass.removeAll("major-move", "attack-move")
		}
	}

	def playSound(move: Move) = {
		if (move.isAttack) {
			capturePlayer.stop()
			capturePlayer.play()
		} else if (move.isCastle) {
			castlePlayer.stop()
			castlePlayer.play()
		} else if (move.isPromotion) {
			promotePlayer.stop()
			promotePlayer.play()
		} else {
			movePlayer.stop()
			movePlayer.play()
		}
	}

	def disableAllBtns(gridPane: javafx.scene.layout.GridPane): Unit = {
		for (tileId <- 0 until Board.TILES_COUNT) {
			val button = gridPane.getChildren.get(tileId).asInstanceOf[javafx.scene.control.Button]
			button.setDisable(true)
		}
	}
}

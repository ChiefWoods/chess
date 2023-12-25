package chessgame.controllers;

import chessgame.Main
import chessgame.board.{Board, Tile}
import chessgame.models.Stats
import chessgame.moves.{AttackMove, CastleMove, Move, PawnEnPassant, PawnPromotion}
import chessgame.pieces.Piece
import chessgame.pieces.Piece._
import chessgame.players.Player
import javafx.{scene => jfxs}
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

	def handleTileClick(event: jfxs.input.MouseEvent) = {
		val button = event.getSource.asInstanceOf[javafx.scene.control.Button]
		val gridPane = button.getParent.asInstanceOf[javafx.scene.layout.GridPane]
		val tileId = button.getId.substring("tile_".length).toInt
		var pieceLegalMoves: Set[Move] = null
		val opponentActivePieces: Set[Piece] = board.getCurrentPlayer.getOpponent.getActivePieces

		if (event.getButton == jfxs.input.MouseButton.PRIMARY) {
			// When no piece was selected
			if (pieceToMove == null) {
				sourceTile = board.getTile(tileId)
				pieceToMove = sourceTile.getPiece

				// When an empty tile or an opponent piece is selected
				if (pieceToMove == null || (pieceToMove.getPieceTeam != board.getCurrentPlayer.getTeam)) {
					sourceTile = null
					pieceToMove = null
					// When a friendly piece is selected
				} else if (pieceToMove.getPieceTeam == board.getCurrentPlayer.getTeam) {
					pieceLegalMoves = board.getAllLegalMoves.filter(_.getMovedPiece == pieceToMove)
					highlightMoves(gridPane, pieceLegalMoves, opponentActivePieces)
					highlightSourceTile(gridPane, tileId)
				}
				// When a piece was selected
			} else {
				destinationTile = board.getTile(tileId)

				// When destination tile is empty or contains an opponent piece
				if (!destinationTile.isTileOccupied || opponentActivePieces.contains(destinationTile.getPiece)) {
					val move = Move.createMove(board, sourceTile.getTileCoordinate, destinationTile.getTileCoordinate)
					val transition = board.getCurrentPlayer.makeMove(move)

					if (transition.getMoveStatus.isDone) {
						println(move.toString)
						playSound(move)
					} else if (transition.getMoveStatus.leavesPlayerInCheck) {
						println("Illegal move! King is in check!")
					} else if (transition.getMoveStatus.isIllegalMove) {
						println("Illegal move!")
					}

					sourceTile = null
					pieceToMove = null
					destinationTile = null

					board = transition.getTransitionBoard
					Main.showBoard(board)
					incrementStats(move)
					isPlayerMated(board.getCurrentPlayer)
					// When the destination tile is the same as the source tile
				} else if (destinationTile.getPiece.getPieceTeam == board.getCurrentPlayer.getTeam && destinationTile.getPiece == pieceToMove) {
					sourceTile = null
					pieceToMove = null
					destinationTile = null
					unhighlightMoves(gridPane)
					// When destination tile contains a friendly piece
				} else {
					sourceTile = board.getTile(tileId)
					pieceToMove = sourceTile.getPiece
					pieceLegalMoves = board.getAllLegalMoves.filter(_.getMovedPiece == pieceToMove)
					unhighlightMoves(gridPane)
					highlightMoves(gridPane, pieceLegalMoves, opponentActivePieces)
					highlightSourceTile(gridPane, tileId)
				}
			}
		} else if (event.getButton == jfxs.input.MouseButton.SECONDARY) {
			sourceTile = null
			pieceToMove = null
			unhighlightMoves(gridPane)
		}
	}

	def highlightMoves(gridPane: jfxs.layout.GridPane, legalMoves: Set[Move], opponentActivePieces: Set[Piece]) = {
		for (move <- legalMoves) {
			val tileId = move.getDestinationCoordinate
			val button = gridPane.getChildren.get(tileId).asInstanceOf[jfxs.control.Button]

			if (opponentActivePieces.contains(board.getTile(tileId).getPiece)) {
				button.getStyleClass.add("attack-move")
			} else {
				button.getStyleClass.add("major-move")
			}
		}
	}

	def unhighlightMoves(gridPane: jfxs.layout.GridPane) = {
		for (tileId <- 0 until Board.TILES_COUNT) {
			val button = gridPane.getChildren.get(tileId).asInstanceOf[jfxs.control.Button]
			button.getStyleClass.removeAll("source-tile", "major-move", "attack-move")
		}
	}

	def highlightSourceTile(gridPane: javafx.scene.layout.GridPane, tileId: Int) = {
		val button = gridPane.getChildren.get(tileId).asInstanceOf[jfxs.control.Button]
		button.getStyleClass.add("source-tile")
	}

	def playSound(move: Move) = {
		move match {
			case _: AttackMove =>
				capturePlayer.stop()
				capturePlayer.play()
			case _: CastleMove =>
				castlePlayer.stop()
				castlePlayer.play()
			case _: PawnPromotion =>
				promotePlayer.stop()
				promotePlayer.play()
			case _ =>
				movePlayer.stop()
				movePlayer.play()
		}
	}

	def isPlayerMated(player: Player) = {
		if (player.isInCheckmate) {
			checkPlayer.stop()
			checkPlayer.play()

			val winner = player.getOpponent.getTeam.toString

			showCheckmateAlert(winner)
			Stats.incrementCheckmates
		} else if (player.isInStalemate) {
			checkPlayer.stop()
			checkPlayer.play()

			showStalemateAlert
		}
	}

	def showCheckmateAlert(winner: String) = {
		val result = new Alert(Alert.AlertType.Information) {
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

		result match {
			case Some(_) => Main.showWelcome
			case None =>
		}
	}

	def showStalemateAlert = {
		val result = new Alert(Alert.AlertType.Information) {
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

		result match {
			case Some(_) => Main.showWelcome
			case None =>
		}
	}

	def incrementStats(move: Move) = {
		move match {
			case _: AttackMove =>
				move.asInstanceOf[AttackMove].getAttackedPiece.getPieceType match {
					case PAWN => Stats.incrementPawnsCaptured
					case KNIGHT => Stats.incrementKnightsCaptured
					case BISHOP => Stats.incrementBishopsCaptured
					case ROOK => Stats.incrementRooksCaptured
					case QUEEN => Stats.incrementQueensCaptured
					case _ =>
				}

				if (move.isInstanceOf[PawnEnPassant]) {
					Stats.incrementEnPassants
				}

				Stats.incrementPiecesCaptured
			case _: CastleMove =>
				Stats.incrementCastles
			case _: PawnPromotion =>
				Stats.incrementPromotions
			case _ =>
		}
	}
}

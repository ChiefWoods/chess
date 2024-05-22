package chessgame

import chessgame.board.Board
import chessgame.controllers.ChessboardController
import chessgame.util.Database
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.scene.media.{Media, MediaPlayer}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object Main extends JFXApp {
	Database.setupDB
	private val tilesPerRow = Board.TILES_PER_ROW
	private val music = new Media(getClass.getResource("/audio/TournerDansLeVide.mp3").toString)
	private val player = new MediaPlayer(music)
	player.cycleCount = MediaPlayer.Indefinite

	val rootResource = getClass.getResource("/views/RootLayout.fxml")
	val loader = new FXMLLoader(rootResource, NoDependencyResolver)
	loader.load()
	val roots = loader.getRoot[jfxs.layout.BorderPane]

	stage = new PrimaryStage {
		title = "Chess"
		icons += new Image(getClass.getResourceAsStream("/images/chess/basic.png"))
		scene = new Scene {
			stylesheets += getClass.getResource("/views/styles.css").toString
			root = roots
		}
		resizable = false
	}

	def showWelcome = {
		player.play()
		val resource = getClass.getResource("/views/Welcome.fxml")
		val loader = new FXMLLoader(resource, NoDependencyResolver)
		loader.load();
		val root = loader.getRoot[jfxs.layout.AnchorPane]
		roots.setCenter(root)
	}

	def showBoard(board: Board): Unit = {
		player.stop()
		val resource = getClass.getResource("/views/Chessboard.fxml")
		val loader = new FXMLLoader(resource, NoDependencyResolver)
		loader.load();
		val root = loader.getRoot[jfxs.layout.BorderPane]
		val chessboardGrid = root.lookup("#chessboard").asInstanceOf[jfxs.layout.GridPane]
		val chessboardController = loader.getController[ChessboardController#Controller]

		for (row <- 0 until tilesPerRow) {
			for (col <- 0 until tilesPerRow) {
				val tileId = row * tilesPerRow + col
				val buttonStyleClass = if ((row + col) % 2 == 0) "light-tile" else "dark-tile"
				val button = chessboardGrid.getChildren.get(tileId).asInstanceOf[jfxs.control.Button]

				button.setId(s"tile_$tileId")
				button.getStyleClass.add(buttonStyleClass)

				button.setGraphic(new jfxs.image.ImageView {
					setPreserveRatio(true)
					setFitWidth(50)
					setFitHeight(50)

					if (board.getTile(tileId).isTileOccupied) {
						val pieceTeam = board.getTile(tileId).getPiece.getPieceTeam
						val pieceType = board.getTile(tileId).getPiece.getPieceType
						setImage(new jfxs.image.Image(
							getClass.getResourceAsStream(s"/images/pieces/$pieceTeam/$pieceType.png".toLowerCase)
						))
					}
				})
			}
		}

		if (board.getCurrentPlayer.isInCheck) {
			val kingTileId: String = s"tile_${board.getCurrentPlayer.getPlayerKing.getPiecePosition}"
			val kingButton = chessboardGrid.lookup(s"#$kingTileId").asInstanceOf[jfxs.control.Button]
			kingButton.getStyleClass.add("in-check")
		}

		chessboardController.board = board

		roots.setCenter(root)
	}

	def showStats = {
		val resource = getClass.getResource("/views/Stats.fxml")
		val loader = new FXMLLoader(resource, NoDependencyResolver)
		loader.load();
		val root = loader.getRoot[jfxs.layout.AnchorPane]
		roots.setCenter(root)
	}

	showWelcome
}
package chessgame

import chessgame.board.Board
import chessgame.controllers.ChessboardController
import javafx.scene.image.ImageView
import javafx.{scene => jfxs}
import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.image.Image
import scalafx.scene.media.{Media, MediaPlayer}
import scalafxml.core.{FXMLLoader, NoDependencyResolver}

object Main extends JFXApp {
	val tilesPerRow = Board.TILES_PER_ROW
	val music = new Media(getClass.getResource("/audio/TournerDansLeVide.mp3").toString)
	val player = new MediaPlayer(music)

	val rootResource = getClass.getResource("/views/RootLayout.fxml")
	val loader = new FXMLLoader(rootResource, NoDependencyResolver)
	loader.load()
	val roots = loader.getRoot[jfxs.layout.BorderPane]()
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
		this.roots.setCenter(root)
	}

	def showBoard(board: Board): Unit = {
		player.stop()
		val resource = getClass.getResource("/views/Chessboard.fxml")
		val loader = new FXMLLoader(resource, NoDependencyResolver)
		loader.load();
		val root = loader.getRoot[jfxs.layout.AnchorPane]
		val chessboardGrid = root.getChildren.get(0).asInstanceOf[jfxs.layout.GridPane]
		val chessboardController = loader.getController[ChessboardController#Controller]

		for (row <- 0 until tilesPerRow) {
			for (col <- 0 until tilesPerRow) {
				val tileId = row * tilesPerRow + col
				val buttonStyleClass = if ((row + col) % 2 == 0) "light-tile" else "dark-tile"
				val button = chessboardGrid.getChildren.get(tileId).asInstanceOf[jfxs.control.Button]

				button.setId(s"tile_$tileId")
				button.getStyleClass.add(buttonStyleClass)

				button.setGraphic(new ImageView() {
					setPreserveRatio(true)
					setFitWidth(60)
					setFitHeight(60)

					if (board.getTile(tileId).isTileOccupied) {
						val pieceTeam = board.getTile(tileId).getPiece.getPieceTeam
						val pieceType = board.getTile(tileId).getPiece.getPieceType
						setImage(new javafx.scene.image.Image(
							getClass.getResourceAsStream(s"/images/pieces/$pieceTeam/$pieceType.png"))
						)
					}
				})
			}
		}

		chessboardController.board = board

		roots.setCenter(root)
	}

	//  var board: Board = Board.createStandardBoard
	//
	//  println(board)

	showWelcome
}
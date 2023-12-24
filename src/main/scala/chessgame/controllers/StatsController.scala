package chessgame.controllers

import chessgame.models.Stats
import chessgame.Main
import chessgame.utils.Database
import scalafx.scene.control.{Alert, ButtonType, TableView}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml

@sfxml
class StatsController(private val piecesCapturedText: Text) {
	def goBack = {
		Main.showWelcome
	}

	def resetStats = {
		val alert = new Alert(AlertType.Confirmation) {
			title = "Reset Stats"
			headerText = "Are you sure you want to reset your stats?"
			contentText = "This action cannot be undone."
			buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
		}
		val result = alert.showAndWait()
		result match {
			case Some(ButtonType.OK) =>
				Stats.resetStats
				showStatsTable
			case _ =>
		}
	}

	def showStatsTable = {
		val stats = Stats.getStats
		if (stats != null) {
			piecesCapturedText.text = stats.piecesCapturedInt.toString
		} else {
			piecesCapturedText.text = "0"
		}
	}

	showStatsTable
}

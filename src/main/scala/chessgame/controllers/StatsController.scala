package chessgame.controllers

import chessgame.models.Stats
import chessgame.{Database, Main}
import scalafx.scene.control.{Alert, ButtonType, TableView}
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml

@sfxml
class StatsController(private val piecesCapturedText: Text) {
	private var stats: Stats = null

	def goBack = {
		Main.showWelcome
	}

	def setStats = {
		this.stats = Stats.getStats
	}

	def resetStats = {
		val alert = new Alert(AlertType.Confirmation) {
			title = "Confirmation Dialog"
			headerText = "Are you sure you want to reset your stats?"
			contentText = "This action cannot be undone."
			buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
		}
		val result = alert.showAndWait()
		result match {
			case Some(ButtonType.OK) =>
				Stats.resetStats
				stats = Stats.getStats
				showStatsTable(stats)
			case _ =>
		}
	}

	def showStatsTable(stats: Stats) = {
		if (stats != null) {
			piecesCapturedText.text = stats.piecesCapturedInt.toString
		} else {
			piecesCapturedText.text = "0"
		}
	}

	showStatsTable(stats)
}

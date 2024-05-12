package chessgame.controllers

import chessgame.Main
import chessgame.models.Stats
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, ButtonType}
import scalafx.scene.text.Text
import scalafxml.core.macros.sfxml

@sfxml
class StatsController(private val piecesCapturedText: Text,
                      private val pawnsCapturedText: Text,
                      private val knightsCapturedText: Text,
                      private val bishopsCapturedText: Text,
                      private val rooksCapturedText: Text,
                      private val queensCapturedText: Text,
                      private val checkmatesText: Text,
                      private val castlesText: Text,
                      private val promotionsText: Text,
                      private val enPassantsText: Text,
                     ) {

	def goBack = {
		Main.showWelcome
	}

	def resetStats = {
		val result = new Alert(AlertType.Confirmation) {
			title = "Reset Stats"
			headerText = "Are you sure you want to reset your stats?"
			contentText = "This action cannot be undone."
			buttonTypes = Seq(ButtonType.OK, ButtonType.Cancel)
		}.showAndWait()

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
			pawnsCapturedText.text = stats.pawnsCapturedInt.toString
			knightsCapturedText.text = stats.knightsCapturedInt.toString
			bishopsCapturedText.text = stats.bishopsCapturedInt.toString
			rooksCapturedText.text = stats.rooksCapturedInt.toString
			queensCapturedText.text = stats.queensCapturedInt.toString
			checkmatesText.text = stats.checkmatesInt.toString
			castlesText.text = stats.castlesInt.toString
			promotionsText.text = stats.promotionsInt.toString
			enPassantsText.text = stats.enPassantsInt.toString
		} else {
			piecesCapturedText.text = "0"
			pawnsCapturedText.text = "0"
			knightsCapturedText.text = "0"
			bishopsCapturedText.text = "0"
			rooksCapturedText.text = "0"
			queensCapturedText.text = "0"
			checkmatesText.text = "0"
			castlesText.text = "0"
			promotionsText.text = "0"
			enPassantsText.text = "0"
		}
	}

	showStatsTable
}

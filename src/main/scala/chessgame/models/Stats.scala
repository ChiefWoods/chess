package chessgame.models

import chessgame.Database
import scalafx.beans.property.IntegerProperty
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

import scala.util.Try

class Stats(val piecesCapturedInt: Int) extends Database {
	def this() = this(0)

	var piecesCaptured = IntegerProperty(piecesCapturedInt)

	def hasStats: Boolean = {
		DB readOnly { implicit session =>
			sql"SELECT * FROM Stats".map(rs => Stats(
				rs.int("pieces_captured")
			)).single.apply()
		} match {
			case Some(x) => true
			case None => false
		}
	}
}

object Stats extends Database {
	def apply(piecesCapturedInt: Int) = {
		new Stats(piecesCapturedInt) {
			piecesCaptured.value = piecesCapturedInt
		}
	}

	def initializeTable = {
		DB autoCommit { implicit session =>
			sql"""
				CREATE TABLE Stats (
					id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
					pieces_captured INT
				)
			""".execute.apply()
		}
	}

	def getStats: Stats = {
		DB readOnly { implicit session =>
			sql"SELECT * FROM Stats".map(rs => Stats(
				rs.int("pieces_captured")
			)).single.apply()
		} match {
			case Some(x) => x
			case None => Stats(0)
		}
	}

	def resetStats: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE Stats SET
				pieces_captured = 0
			""".update.apply()
		})
	}

	def incrementPiecesCaptured: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE Stats SET
				pieces_captured = pieces_captured + 1
			""".update.apply()
		})
	}
}

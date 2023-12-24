package chessgame.models

import chessgame.utils.Database
import scalafx.beans.property.IntegerProperty
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}

import scala.util.Try

class Stats(val piecesCapturedInt: Int) extends Database {
	def this() = this(0)

	var piecesCaptured = IntegerProperty(piecesCapturedInt)
}

object Stats extends Database {
	def apply(piecesCapturedInt: Int): Stats = {
		new Stats(piecesCapturedInt) {
			piecesCaptured.value = piecesCapturedInt
		}
	}

	def initializeTable = {
		DB autoCommit { implicit session =>
			sql"""
				CREATE TABLE stats (
					id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
					pieces_captured INT
				)
			""".execute.apply()
			sql"""
				INSERT INTO stats (pieces_captured)
				VALUES (0)
			""".update.apply()
		}
	}

	def getStats: Stats = {
		DB readOnly { implicit session =>
			sql"SELECT * FROM stats WHERE id = 1".map(rs => Stats(
				rs.int("pieces_captured")
			)).single.apply()
		} match {
			case Some(x) => x
			case None => null
		}
	}

	def resetStats: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				pieces_captured = 0
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementPiecesCaptured: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				pieces_captured = pieces_captured + 1
				WHERE id = 1
			""".update.apply()
		})
	}
}

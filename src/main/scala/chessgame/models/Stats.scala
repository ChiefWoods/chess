package chessgame.models

import chessgame.util.Database
import scalafx.beans.property.IntegerProperty
import scalikejdbc.{DB, scalikejdbcSQLInterpolationImplicitDef}
import scala.util.Try

class Stats(val piecesCapturedInt: Int,
            val pawnsCapturedInt: Int,
            val knightsCapturedInt: Int,
            val bishopsCapturedInt: Int,
            val rooksCapturedInt: Int,
            val queensCapturedInt: Int,
            val checkmatesInt: Int,
            val castlesInt: Int,
            val promotionsInt: Int,
            val enPassantsInt: Int,
           ) extends Database {
	def this() = this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0)

	var piecesCaptured = IntegerProperty(piecesCapturedInt)
	var pawnsCaptured = IntegerProperty(pawnsCapturedInt)
	var knightsCaptured = IntegerProperty(knightsCapturedInt)
	var bishopsCaptured = IntegerProperty(bishopsCapturedInt)
	var rooksCaptured = IntegerProperty(rooksCapturedInt)
	var queensCaptured = IntegerProperty(queensCapturedInt)
	var checkmates = IntegerProperty(checkmatesInt)
	var castles = IntegerProperty(castlesInt)
	var promotions = IntegerProperty(promotionsInt)
	var enPassants = IntegerProperty(enPassantsInt)
}

object Stats extends Database {
	def apply(piecesCapturedInt: Int,
	          pawnsCapturedInt: Int,
	          knightsCapturedInt: Int,
	          bishopsCapturedInt: Int,
	          rooksCapturedInt: Int,
	          queensCapturedInt: Int,
	          checkmatesInt: Int,
	          castlesInt: Int,
	          promotionsInt: Int,
	          enPassantsInt: Int): Stats = {
		new Stats(piecesCapturedInt,
			pawnsCapturedInt,
			knightsCapturedInt,
			bishopsCapturedInt,
			rooksCapturedInt,
			queensCapturedInt,
			checkmatesInt,
			castlesInt,
			promotionsInt,
			enPassantsInt) {
			piecesCaptured.value = piecesCapturedInt
			pawnsCaptured.value = pawnsCapturedInt
			knightsCaptured.value = knightsCapturedInt
			bishopsCaptured.value = bishopsCapturedInt
			rooksCaptured.value = rooksCapturedInt
			queensCaptured.value = queensCapturedInt
			checkmates.value = checkmatesInt
			castles.value = castlesInt
			promotions.value = promotionsInt
			enPassants.value = enPassantsInt
		}
	}

	def initializeTable = {
		DB autoCommit { implicit session =>
			sql"""
				CREATE TABLE stats (
					id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
					pieces_captured INT,
					pawns_captured INT,
					knights_captured INT,
					bishops_captured INT,
					rooks_captured INT,
					queens_captured INT,
					checkmates INT,
					castles INT,
					promotions INT,
					en_passants INT
				)
			""".execute.apply()
			sql"""
				INSERT INTO stats (pieces_captured, pawns_captured, knights_captured, bishops_captured, rooks_captured, queens_captured, checkmates, castles, promotions, en_passants)
				VALUES (0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
			""".update.apply()
		}
	}

	def getStats: Stats = {
		DB readOnly { implicit session =>
			sql"SELECT * FROM stats WHERE id = 1".map(rs => Stats(
				rs.int("pieces_captured"),
				rs.int("pawns_captured"),
				rs.int("knights_captured"),
				rs.int("bishops_captured"),
				rs.int("rooks_captured"),
				rs.int("queens_captured"),
				rs.int("checkmates"),
				rs.int("castles"),
				rs.int("promotions"),
				rs.int("en_passants")
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
				pieces_captured = 0,
				pawns_captured = 0,
				knights_captured = 0,
				bishops_captured = 0,
				rooks_captured = 0,
				queens_captured = 0,
				checkmates = 0,
				castles = 0,
				promotions = 0,
				en_passants = 0
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

	def incrementPawnsCaptured: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				pawns_captured = pawns_captured + 1
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementKnightsCaptured: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				knights_captured = knights_captured + 1
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementBishopsCaptured: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				bishops_captured = bishops_captured + 1
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementRooksCaptured: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				rooks_captured = rooks_captured + 1
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementQueensCaptured: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				queens_captured = queens_captured + 1
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementCheckmates: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				checkmates = checkmates + 1
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementCastles: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				castles = castles + 1
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementPromotions: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				promotions = promotions + 1
				WHERE id = 1
			""".update.apply()
		})
	}

	def incrementEnPassants: Try[Int] = {
		Try(DB autoCommit { implicit session =>
			sql"""
				UPDATE stats SET
				en_passants = en_passants + 1
				WHERE id = 1
			""".update.apply()
		})
	}
}

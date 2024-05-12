package chessgame.util

import chessgame.models.Stats
import scalikejdbc._

trait Database {
	val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
	val dbURL = "jdbc:derby:chessDB;create=true;"

	Class.forName(derbyDriverClassname)
	ConnectionPool.singleton(dbURL, "root", "AnatolyKarpov")
	implicit val session = AutoSession
}

object Database extends Database {
	def setupDB = {
		if (!hasDBInitialized) {
			Stats.initializeTable
		}
	}

	def hasDBInitialized: Boolean = {
		DB getTable "Stats" match {
			case Some(x) => true
			case None => false
		}
	}
}

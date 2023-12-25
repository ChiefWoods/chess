package chessgame.moves

object MoveStatus extends Enumeration {
	type MoveStatus = Value
	val DONE, ILLEGAL_MOVE, LEAVES_PLAYER_IN_CHECK = Value

	implicit class MoveStatusOps(moveStatus: Value) {
		def isDone: Boolean = moveStatus == DONE

		def isIllegalMove: Boolean = moveStatus == ILLEGAL_MOVE

		def leavesPlayerInCheck: Boolean = moveStatus == LEAVES_PLAYER_IN_CHECK
	}
}

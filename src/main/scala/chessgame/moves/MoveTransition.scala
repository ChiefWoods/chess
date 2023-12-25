package chessgame.moves

import chessgame.board.Board

case class MoveTransition(private val transitionBoard: Board,
                          private val move: Move,
                          private val moveStatus: MoveStatus.MoveStatus) {

	def getTransitionBoard: Board = transitionBoard

	def getMoveStatus: MoveStatus.MoveStatus = moveStatus
}

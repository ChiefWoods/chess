package chessgame.players

import chessgame.board.{Board, Move}

case class MoveTransition(private val transitionBoard: Board, private val move: Move, private val moveStatus: MoveStatus.MoveStatus) {
  def getBoard: Board = transitionBoard

  def getMove: Move = move

  def getMoveStatus: MoveStatus.MoveStatus = moveStatus
}

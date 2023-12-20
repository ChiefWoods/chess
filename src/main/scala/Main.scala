import chessgame.Board

object Main {
  def main(args: Array[String]): Unit = {
    val board: Board = Board.createStandardBoard

    println(board)
  }
}
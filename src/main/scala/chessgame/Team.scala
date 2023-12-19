package chessgame

object Team extends Enumeration {
  abstract class Team extends super.Val {
    def getDirection: Int
  }
}

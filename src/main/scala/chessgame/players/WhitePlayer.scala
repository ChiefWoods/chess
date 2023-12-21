package chessgame.players

import chessgame.Team.{Team, WHITE}
import chessgame.board.{Board, KingSideCastle, Move, QueenSideCastle}
import chessgame.pieces.{Piece, Rook}

import scala.collection.mutable

class WhitePlayer(private val board: Board, private val whiteLegalMoves: Set[Move], private val blackLegalMoves: Set[Move]) extends Player(board, whiteLegalMoves, blackLegalMoves) {
  override def getActivePieces: Set[Piece] = board.getWhitePieces

  override def getTeam: Team = WHITE

  override def getOpponent: Player = board.getBlackPlayer

  override def calculateKingCastles(playerLegals: Set[Move], opponentLegals: Set[Move]): Set[Move] = {
    var kingCastles: Set[Move] = Set()

    if (playerKing.getIsFirstMove && !isInCheck) {
      if (!board.getTile(61).isTileOccupied && !board.getTile(62).isTileOccupied) {
        val rookTile = board.getTile(63)

        if (rookTile.isTileOccupied &&
          rookTile.getPiece.getIsFirstMove &&
          rookTile.getPiece.getPieceType.isRook &&
          Player.calculateAttacksOnTile(61, opponentLegals).isEmpty &&
          Player.calculateAttacksOnTile(62, opponentLegals).isEmpty) {
          kingCastles += new KingSideCastle(board, playerKing, 62, rookTile.getPiece.asInstanceOf[Rook], rookTile.getTileCoordinate, 61)
        }
      }

      if (!board.getTile(59).isTileOccupied && !board.getTile(58).isTileOccupied && !board.getTile(57).isTileOccupied) {
        val rookTile = board.getTile(56)

        if (rookTile.isTileOccupied &&
          rookTile.getPiece.getIsFirstMove &&
          rookTile.getPiece.getPieceType.isRook &&
          Player.calculateAttacksOnTile(58, opponentLegals).isEmpty &&
          Player.calculateAttacksOnTile(59, opponentLegals).isEmpty) {
          kingCastles += new QueenSideCastle(board, playerKing, 58, rookTile.getPiece.asInstanceOf[Rook], rookTile.getTileCoordinate, 59)
        }
      }
    }

    kingCastles
  }
}

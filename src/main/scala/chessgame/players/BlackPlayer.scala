package chessgame.players

import chessgame.Team.{BLACK, Team}
import chessgame.board.{Board, KingSideCastle, Move, QueenSideCastle}
import chessgame.pieces.{Piece, Rook}

import scala.collection.mutable

class BlackPlayer(private val board: Board, private val blackLegalMoves: Set[Move], private val whiteLegalMoves: Set[Move]) extends Player(board, blackLegalMoves, whiteLegalMoves) {
  override def getActivePieces: Set[Piece] = board.getBlackPieces

  override def getTeam: Team = BLACK

  override def getOpponent: Player = board.getWhitePlayer

  override def calculateKingCastles(playerLegals: Set[Move], opponentLegals: Set[Move]): Set[Move] = {
    var kingCastles: Set[Move] = Set()

    if (playerKing.getIsFirstMove && !isInCheck) {
      if (!board.getTile(5).isTileOccupied && !board.getTile(6).isTileOccupied) {
        val rookTile = board.getTile(7)

        if (rookTile.isTileOccupied &&
          rookTile.getPiece.getIsFirstMove &&
          rookTile.getPiece.getPieceType.isRook &&
          Player.calculateAttacksOnTile(5, opponentLegals).isEmpty &&
          Player.calculateAttacksOnTile(6, opponentLegals).isEmpty) {
          kingCastles += new KingSideCastle(board, playerKing, 6, rookTile.getPiece.asInstanceOf[Rook], rookTile.getTileCoordinate, 5)
        }
      }

      if (!board.getTile(1).isTileOccupied && !board.getTile(2).isTileOccupied && !board.getTile(3).isTileOccupied) {
        val rookTile = board.getTile(0)

        if (rookTile.isTileOccupied &&
          rookTile.getPiece.getIsFirstMove &&
          rookTile.getPiece.getPieceType.isRook &&
          Player.calculateAttacksOnTile(2, opponentLegals).isEmpty &&
          Player.calculateAttacksOnTile(3, opponentLegals).isEmpty) {
          kingCastles += new QueenSideCastle(board, playerKing, 2, rookTile.getPiece.asInstanceOf[Rook], rookTile.getTileCoordinate, 3)
        }
      }
    }

    kingCastles
  }
}

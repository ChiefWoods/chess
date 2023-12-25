package chessgame.players

import chessgame.board.Board
import chessgame.moves
import chessgame.moves.Move
import chessgame.pieces.{Piece, Rook}
import chessgame.players.Team.{Team, WHITE}

class WhitePlayer(private val board: Board,
                  private val whiteLegalMoves: Set[Move],
                  private val blackLegalMoves: Set[Move])
	extends Player(board, whiteLegalMoves, blackLegalMoves) {

	override def getTeam: Team = WHITE

	override def getActivePieces: Set[Piece] = board.getWhitePieces

	override def getOpponent: Player = board.getBlackPlayer

	override def calculateKingCastles(playerLegals: Set[Move], opponentLegals: Set[Move]): Set[Move] = {
		var kingCastles: Set[Move] = Set()

		if (getPlayerKing.getIsFirstMove && !isInCheck) {
			if (!board.getTile(61).isTileOccupied && !board.getTile(62).isTileOccupied) {
				val rookTile = board.getTile(63)

				if (rookTile.isTileOccupied &&
					rookTile.getPiece.getIsFirstMove &&
					rookTile.getPiece.getPieceType.isRook &&
					Player.calculateAttacksOnTile(61, opponentLegals).isEmpty &&
					Player.calculateAttacksOnTile(62, opponentLegals).isEmpty) {
					kingCastles += moves.KingSideCastle(board, 62, getPlayerKing, rookTile.getPiece.asInstanceOf[Rook], rookTile.getTileCoordinate, 61)
				}
			}

			if (!board.getTile(59).isTileOccupied && !board.getTile(58).isTileOccupied && !board.getTile(57).isTileOccupied) {
				val rookTile = board.getTile(56)

				if (rookTile.isTileOccupied &&
					rookTile.getPiece.getIsFirstMove &&
					rookTile.getPiece.getPieceType.isRook &&
					Player.calculateAttacksOnTile(58, opponentLegals).isEmpty &&
					Player.calculateAttacksOnTile(59, opponentLegals).isEmpty) {
					kingCastles += moves.QueenSideCastle(board, 58, getPlayerKing, rookTile.getPiece.asInstanceOf[Rook], rookTile.getTileCoordinate, 59)
				}
			}
		}

		kingCastles
	}
}

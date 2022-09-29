package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.views.BoardVector
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.ChessBoard
import pt.isel.pdm.chess4android.views.Tile

class King(
    isWhite: Boolean
) : APiece(isWhite, type = Tile.Pieces.K) {

    override fun getPossibleMoves(chessBoard: ChessBoard, tile: Tile): ArrayList<BoardVector> {
        val possibleMoves: ArrayList<BoardVector> = ArrayList()
        possibleMoves.addAll(listOf(
            BoardVector(1, 0),
            BoardVector(-1, 0),

            BoardVector(1, 1),
            BoardVector(-1, 1),

            BoardVector(1, -1),
            BoardVector(-1, -1),

            BoardVector(0, -1),
            BoardVector(0, 1)
        ))

        val toRemove: ArrayList<BoardVector> = ArrayList()
        for(move in possibleMoves) {
            val piece = chessBoard.getTile(tile.getPosition() + move)?.getPiece()
            if(piece?.isWhite == tile.getPiece()!!.isWhite)
                toRemove.add(move)
        }
        possibleMoves.removeAll(toRemove)

        return possibleMoves
    }

    fun canKingSideCastle(chessBoard: ChessBoard, kingTile: Tile): Boolean {
        //The two tiles next to the king MUST be empty
        val position1 = kingTile.getPosition() + BoardVector(1, 0)
        val position2 = kingTile.getPosition() + BoardVector(2, 0)
        if(chessBoard.getTile(position1)?.getPiece() != null || chessBoard.getTile(position2)?.getPiece() != null) return false

        //The tile in the right corner (3 columns to the right) has to be the rook
        return chessBoard.getTile(kingTile.getPosition() + BoardVector(3, 0))?.getPiece()?.type == Tile.Pieces.R
    }

    //TODO() queen side castle
}
package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.views.BoardVector
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.ChessBoard
import pt.isel.pdm.chess4android.views.Tile

class Knight (
    isWhite: Boolean
) : APiece(isWhite, type = Tile.Pieces.N) {

    override fun getPossibleMoves(chessBoard: ChessBoard, tile: Tile): ArrayList<BoardVector> {
        val possibleMoves: ArrayList<BoardVector> = ArrayList()
        possibleMoves.addAll(listOf(
            BoardVector(1, -2),
            BoardVector(1, 2),

            BoardVector(-1, -2),
            BoardVector(-1, 2),

            BoardVector(2, -1),
            BoardVector(2, 1),

            BoardVector(-2, -1),
            BoardVector(-2, 1),
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
}
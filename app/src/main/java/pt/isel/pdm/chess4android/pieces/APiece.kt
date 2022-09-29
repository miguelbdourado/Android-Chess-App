package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.views.BoardVector
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.ChessBoard
import pt.isel.pdm.chess4android.views.Tile

abstract class APiece(val isWhite: Boolean, val type : Tile.Pieces) {
    fun canMove(chessBoard: ChessBoard, start: Tile, end: Tile): Boolean {
        val possibleMoves = getPossibleMoves(chessBoard, start)

        val move = BoardVector(
            end.getPosition().column - start.getPosition().column,
            end.getPosition().row - start.getPosition().row
        )
        return possibleMoves.contains(move)
    }

    abstract fun getPossibleMoves(chessBoard: ChessBoard, tile: Tile): ArrayList<BoardVector>
}
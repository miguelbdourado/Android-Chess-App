package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.views.BoardVector
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.ChessBoard
import pt.isel.pdm.chess4android.views.Tile
import java.util.*
import kotlin.collections.ArrayList

class Pawn (
    isWhite: Boolean
) : APiece(isWhite, Tile.Pieces.P) {

    //TODO(https://en.wikipedia.org/wiki/En_passant)
    override fun getPossibleMoves(chessBoard: ChessBoard, tile: Tile): ArrayList<BoardVector> {

        //TODO: BIG TIME REWORK
        val possibleMoves: ArrayList<BoardVector> = ArrayList()
        val direction: Int = if (tile.getPiece()?.isWhite!!) 1 else -1

        val targetVectorForward = BoardVector(0, direction)
        val targetVectorTwoForward = BoardVector(0, direction * 2)
        var targetPiece = chessBoard.getTile(tile.getPosition() + targetVectorTwoForward)?.getPiece()
        val tempPiece = chessBoard.getTile(tile.getPosition() + targetVectorForward)?.getPiece()
        if((tile.getPosition().row == 2 || tile.getPosition().row == 7) && targetPiece == null && tempPiece == null)
            possibleMoves.add(targetVectorTwoForward)

        targetPiece = chessBoard.getTile(tile.getPosition() + targetVectorForward)?.getPiece()
        if(targetPiece == null)
            possibleMoves.add(targetVectorForward)

        //TODO rework
        val targetVectorRight = BoardVector(1, direction)
        targetPiece = chessBoard.getTile(tile.getPosition() + targetVectorRight)?.getPiece()
        if(targetPiece != null && targetPiece.isWhite != isWhite) possibleMoves.add(targetVectorRight)

        val targetVectorLeft = BoardVector(-1, direction)
        targetPiece = chessBoard.getTile(tile.getPosition() + targetVectorLeft)?.getPiece()
        if(targetPiece != null && targetPiece.isWhite != isWhite) possibleMoves.add(targetVectorLeft)

        return possibleMoves
    }

}
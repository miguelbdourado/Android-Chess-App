package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.views.BoardVector
import pt.isel.pdm.chess4android.views.BoardView
import pt.isel.pdm.chess4android.views.ChessBoard
import pt.isel.pdm.chess4android.views.Tile

class Queen (
    isWhite: Boolean
) : APiece(isWhite, Tile.Pieces.Q) {

    override fun getPossibleMoves(chessBoard: ChessBoard, tile: Tile): ArrayList<BoardVector> {
        val possibleMoves: ArrayList<BoardVector> = ArrayList()
        //TODO BIG REWORK

        //North-west
        for(i in 1..7) {
            val tempVector = BoardVector(i, i)
            val piece = chessBoard.getTile(tile.getPosition() + tempVector)?.getPiece()

            if(piece != null) {
                if(piece.isWhite != isWhite)
                    possibleMoves.add(tempVector)
                break
            }
            possibleMoves.add(tempVector)
        }

        //North-east
        for(i in 1..7) {
            val tempVector = BoardVector(-i, i)
            val piece = chessBoard.getTile(tile.getPosition() + tempVector)?.getPiece()

            if(piece != null) {
                if(piece.isWhite != isWhite)
                    possibleMoves.add(tempVector)
                break
            }
            possibleMoves.add(tempVector)
        }

        //South-west
        for(i in 1..7) {
            val tempVector = BoardVector(-i, -i)
            val piece = chessBoard.getTile(tile.getPosition() + tempVector)?.getPiece()

            if(piece != null) {
                if(piece.isWhite != isWhite)
                    possibleMoves.add(tempVector)
                break
            }
            possibleMoves.add(tempVector)
        }

        //South-east
        for(i in 1..7) {
            val tempVector = BoardVector(i, -i)
            val piece = chessBoard.getTile(tile.getPosition() + tempVector)?.getPiece()

            if(piece != null) {
                if(piece.isWhite != isWhite)
                    possibleMoves.add(tempVector)
                break
            }
            possibleMoves.add(tempVector)
        }

        for(i in 1..7) {
            val tempVector = BoardVector(0, i)
            val piece = chessBoard.getTile(tile.getPosition() + tempVector)?.getPiece()

            if(piece != null) {
                if(piece.isWhite != isWhite)
                    possibleMoves.add(tempVector)
                break
            }
            possibleMoves.add(tempVector)
        }


        for(i in 1..7) {
            val tempVector = BoardVector(-i, 0)
            val piece = chessBoard.getTile(tile.getPosition() + tempVector)?.getPiece()

            if(piece != null) {
                if(piece.isWhite != isWhite)
                    possibleMoves.add(tempVector)
                break
            }
            possibleMoves.add(tempVector)
        }


        for(i in 1..7) {
            val tempVector = BoardVector(0, -i)
            val piece = chessBoard.getTile(tile.getPosition() + tempVector)?.getPiece()

            if(piece != null) {
                if(piece.isWhite != isWhite)
                    possibleMoves.add(tempVector)
                break
            }
            possibleMoves.add(tempVector)
        }


        for(i in 1..7) {
            val tempVector = BoardVector(i, 0)
            val piece = chessBoard.getTile(tile.getPosition() + tempVector)?.getPiece()

            if(piece != null) {
                if(piece.isWhite != isWhite)
                    possibleMoves.add(tempVector)
                break
            }
            possibleMoves.add(tempVector)
        }

        return possibleMoves
    }
}
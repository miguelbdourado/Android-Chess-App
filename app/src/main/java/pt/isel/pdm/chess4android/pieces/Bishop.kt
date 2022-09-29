package pt.isel.pdm.chess4android.pieces

import pt.isel.pdm.chess4android.views.*

class Bishop (
    isWhite: Boolean
) : APiece(isWhite, type = Tile.Pieces.B) {

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

        return possibleMoves
    }
}
package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.pieces.*

/**
 * Custom view that implements a chess board.
 */
@SuppressLint("ClickableViewAccessibility")
class BoardView(
    private val ctx: Context,
    attrs: AttributeSet?
) : GridLayout(ctx, attrs) {
    private val side = 8
    private var chessBoard = ChessBoard(ctx, this)
    fun getChessBoard() = chessBoard

    private val piecesColorMap : HashMap<Pair<Tile.Pieces?, Boolean?>?, VectorDrawableCompat?> = HashMap()

    fun getPiecesColorMap() = piecesColorMap

    private fun populateMap() {
        piecesColorMap[Pair(Tile.Pieces.B, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_bishop, null)
        piecesColorMap[Pair(Tile.Pieces.B, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_bishop, null)
        piecesColorMap[Pair(Tile.Pieces.K, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_king, null)
        piecesColorMap[Pair(Tile.Pieces.K, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_king, null)
        piecesColorMap[Pair(Tile.Pieces.N, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_knight, null)
        piecesColorMap[Pair(Tile.Pieces.N, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_knight, null)
        piecesColorMap[Pair(Tile.Pieces.P, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_pawn, null)
        piecesColorMap[Pair(Tile.Pieces.P, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_pawn, null)
        piecesColorMap[Pair(Tile.Pieces.Q, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_queen, null)
        piecesColorMap[Pair(Tile.Pieces.Q, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_queen, null)
        piecesColorMap[Pair(Tile.Pieces.R, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_rook, null)
        piecesColorMap[Pair(Tile.Pieces.R, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_rook, null)
    }

    private val brush = Paint().apply {
        ctx.resources.getColor(R.color.chess_board_black, null)
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    private val highlightedTiles = ArrayList<Tile>()

    init {
        populateMap()
        initializeBoard()
    }

    fun initializeBoard() {
        removeAllViews()
        rowCount = side
        columnCount = side

        for (row in 8 downTo 1) {
            for (column in 'a'..'h') {
                    chessBoard.placeTile(
                    Tile(this, ctx, if((row + (column-'a')) % 2 == 0) Tile.Type.WHITE else Tile.Type.BLACK,null, BoardPosition(column, row), side),
                    BoardPosition(column, row)
                )
            }
        }
        fillBoardWithPieces()
    }

    private fun fillBoardWithPieces() {
        //Black
        chessBoard.placePiece(Rook(false), BoardPosition('a', 8))
        chessBoard.placePiece(Knight(false), BoardPosition('b', 8))
        chessBoard.placePiece(Bishop(false), BoardPosition('c', 8))
        chessBoard.placePiece(Queen(false), BoardPosition('d', 8))
        chessBoard.placePiece(King(false), BoardPosition('e', 8))
        chessBoard.placePiece(Bishop(false), BoardPosition('f', 8))
        chessBoard.placePiece(Knight(false), BoardPosition('g', 8))
        chessBoard.placePiece(Rook(false), BoardPosition('h', 8))
        repeat(8){chessBoard.placePiece(Pawn(false), BoardPosition('a' + it, 7))}

        //White
        chessBoard.placePiece(Rook(true), BoardPosition('a', 1))
        chessBoard.placePiece(Knight(true), BoardPosition('b', 1))
        chessBoard.placePiece(Bishop(true), BoardPosition('c', 1))
        chessBoard.placePiece(Queen(true), BoardPosition('d', 1))
        chessBoard.placePiece(King(true), BoardPosition('e', 1))
        chessBoard.placePiece(Bishop(true), BoardPosition('f', 1))
        chessBoard.placePiece(Knight(true), BoardPosition('g', 1))
        chessBoard.placePiece(Rook(true), BoardPosition('h', 1))
        repeat(8){chessBoard.placePiece(Pawn(true), BoardPosition('a' + it, 2))}

        chessBoard.blackKingTile = chessBoard.getTile(BoardPosition('e', 8))
        chessBoard.whiteKingTile = chessBoard.getTile(BoardPosition('e', 1))

        //add all views
        for (row in 8 downTo 1) {
            for (column in 'a'..'h') {
                addView(chessBoard.getTile(BoardPosition(column, row)))
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }

    fun drawMoves(tile: Tile) {
        val piece = tile.getPiece()!!
        val possibleMoves = piece.getPossibleMoves(chessBoard, tile)

        erasePreviouslyDrawnMoves()

        for(move in possibleMoves) {
            val tempTile = chessBoard.getTile(tile.getPosition() + move) ?: continue

            tempTile.highlight(true)
            highlightedTiles.add(tempTile)
        }
    }

    fun erasePreviouslyDrawnMoves() {
        repeat(highlightedTiles.size) {
            highlightedTiles[it].highlight(false)
        }

        highlightedTiles.clear()
    }
}
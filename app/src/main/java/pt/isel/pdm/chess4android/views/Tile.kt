package pt.isel.pdm.chess4android.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R
import pt.isel.pdm.chess4android.pieces.APiece

/**
 * Custom view that implements a chess board tile.
 * Tiles are either black or white and can they can be empty or occupied by a chess piece.
 *
 * Implementation note: This view is not to be used with the designer tool.
 * You need to adapt this view to suit your needs. ;)
 *
 * @property type           The tile's type (i.e. black or white)
 * @property tilesPerSide   The number of tiles in each side of the chess board
 */
@SuppressLint("ViewConstructor")
class Tile(
    private val boardView: BoardView,
    private val ctx: Context,
    private val type: Type?,
    private var piece: APiece?,
    private val position: BoardPosition,
    private val tilesPerSide: Int,
) : View(ctx) {
    enum class Type { WHITE, BLACK }
    enum class Pieces { B, K, N, P, Q, R }

    private var highlighted: Boolean = false
    private var tileChangedHighlight: Boolean = false
    fun setTileChangedHighlight(bool: Boolean) { tileChangedHighlight = bool }
    private var pieceDrawable: VectorDrawableCompat? = null

    init { this.setOnClickListener{ boardView.getChessBoard().click(this) } }

    fun getPosition() = position
    fun getPiece() = piece
    fun removePiece() {
        piece = null
        pieceDrawable = null
    }


    fun setPiece(piece: APiece) {
        this.piece = piece
        val pair : Pair<Pieces?, Boolean?> = Pair(piece.type, piece.isWhite)
        pieceDrawable = boardView.getPiecesColorMap()[pair] //Todo simplify
    }

    private val brush = Paint().apply {
        color = ctx.resources.getColor(
            if (type == Type.WHITE) R.color.chess_board_white else R.color.chess_board_black,
            null
        )
        style = Paint.Style.FILL_AND_STROKE
    }

    private val highlightBrush = Paint().apply {
        color = ctx.resources.getColor( R.color.highlightedMove, null )
        style = Paint.Style.FILL_AND_STROKE
    }

    private val selectedBrush = Paint().apply {
        color = ctx.resources.getColor( R.color.selectedPiece, null )
        style = Paint.Style.FILL_AND_STROKE
    }

    private val canEatBrush = Paint().apply {
        color = ctx.resources.getColor( R.color.highlightedMove, null )
        style = Paint.Style.STROKE
        strokeWidth = 10f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val side = Integer.min(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
        setMeasuredDimension(side / tilesPerSide, side / tilesPerSide)
    }

    override fun onDraw(canvas: Canvas) {
        val padding = 8
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), brush)

        //If this tile holds a piece that is currently selected
        val currentlySelectedPosition = boardView.getChessBoard().getCurrentlySelectedPosition()
        if(currentlySelectedPosition?.let { position.isEqual(it) } == true)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), selectedBrush)

        if(tileChangedHighlight)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), selectedBrush)

        pieceDrawable?.setBounds(padding, padding, width - padding, height - padding)
        pieceDrawable?.draw(canvas)

        if(highlighted) {
            if(piece == null)
                canvas.drawCircle(width/2.0f, height/2.0f, height * 0.125f, highlightBrush)
            else
                canvas.drawCircle(width/2.0f, height/2.0f, height * 0.35f, canEatBrush)
        }
    }

    fun highlight(b: Boolean) {
        highlighted = b
        invalidate()
    }




}
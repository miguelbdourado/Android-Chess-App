package pt.isel.pdm.chess4android.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.widget.GridLayout
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import pt.isel.pdm.chess4android.R

class PromotionView(
    private val ctx: Context,
    attrs: AttributeSet?
) : GridLayout(ctx, attrs) {

    private val brush = Paint().apply {
        color = Color.CYAN
        style = Paint.Style.STROKE
        strokeWidth = 10F
    }

    init{
        populateMap()
    }

    private val promotionPiecesMap : HashMap<Pair<Tile.Pieces?, Boolean?>?, VectorDrawableCompat?> = HashMap()

    private fun populateMap() {
        promotionPiecesMap[Pair(Tile.Pieces.B, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_bishop, null)
        promotionPiecesMap[Pair(Tile.Pieces.B, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_bishop, null)
        promotionPiecesMap[Pair(Tile.Pieces.N, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_knight, null)
        promotionPiecesMap[Pair(Tile.Pieces.N, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_knight, null)
        promotionPiecesMap[Pair(Tile.Pieces.Q, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_queen, null)
        promotionPiecesMap[Pair(Tile.Pieces.Q, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_queen, null)
        promotionPiecesMap[Pair(Tile.Pieces.R, false)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_black_rook, null)
        promotionPiecesMap[Pair(Tile.Pieces.R, true)] = VectorDrawableCompat.create(ctx.resources, R.drawable.ic_white_rook, null)
    }



    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        canvas.drawLine(0f, 0f, width.toFloat(), 0f, brush)
        canvas.drawLine(0f, height.toFloat(), width.toFloat(), height.toFloat(), brush)
        canvas.drawLine(0f, 0f, 0f, height.toFloat(), brush)
        canvas.drawLine(width.toFloat(), 0f, width.toFloat(), height.toFloat(), brush)
    }



}
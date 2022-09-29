package pt.isel.pdm.chess4android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.databinding.ActivityPromotionBinding
import pt.isel.pdm.chess4android.pieces.Bishop
import pt.isel.pdm.chess4android.pieces.Knight
import pt.isel.pdm.chess4android.pieces.Queen
import pt.isel.pdm.chess4android.pieces.Rook
import pt.isel.pdm.chess4android.views.Tile


class PromotionActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityPromotionBinding.inflate(layoutInflater)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val tile : Tile = intent.extras?.get("promotion_tile") as Tile

        binding.BishopButton.setOnClickListener{
            tile.setPiece(Bishop(tile.getPiece()!!.isWhite))

            val resultIntent = Intent()
            resultIntent.putExtra("promotion_result", tile.toString())

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.KnightButton.setOnClickListener{
            tile.setPiece(Knight(tile.getPiece()!!.isWhite))

            val resultIntent = Intent()
            resultIntent.putExtra("promotion_result", tile.toString())

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.QueenButton.setOnClickListener{
            tile.setPiece(Queen(tile.getPiece()!!.isWhite))

            val resultIntent = Intent()
            resultIntent.putExtra("promotion_result", tile.toString())

            setResult(RESULT_OK, resultIntent)
            finish()
        }

        binding.RookButton.setOnClickListener{
            tile.setPiece(Rook(tile.getPiece()!!.isWhite))

            val resultIntent = Intent()
            resultIntent.putExtra("promotion_result", tile.toString())

            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }
}
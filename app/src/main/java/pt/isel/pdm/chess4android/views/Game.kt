package pt.isel.pdm.chess4android.views

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChessGame (
    val moves: String,
    val solution: String?,
    val gameOver: Boolean
) : Parcelable
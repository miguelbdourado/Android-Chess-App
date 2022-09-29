package pt.isel.pdm.chess4android

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import pt.isel.pdm.chess4android.history.Status
import retrofit2.Call
import retrofit2.http.GET

@Parcelize
data class Perf (
    val icon: String,
    val name: String
): Parcelable

@Parcelize
data class Player(
    val userId: String,
    val name: String,
    val color: String
): Parcelable

@Parcelize
data class Game (
    val id: String,
    val perf: Perf,
    val rated: Boolean,
    val players: Array<Player>,
    val pgn: String,
    val clock: String
): Parcelable

@Parcelize
data class Puzzle (
    val id: String,
    val rating: Int,
    val plays: Int,
    val initialPly: Int,
    val solution: Array<String>,
    val themes: Array<String>
): Parcelable

@Parcelize
data class PuzzleBoardDTO (
    val game: Game,
    val puzzle: Puzzle
) : Parcelable

interface PuzzleOfTheDay {
    @GET("puzzle/daily")
    fun getPuzzle(): Call<PuzzleBoardDTO>
}

@Parcelize
data class PuzzleOfTheDayDTO(val puzzleBoardDTO: PuzzleBoardDTO, val date: String, val status: Status): Parcelable

/**
 * Represents errors while accessing the remote API. Instead of tossing around Retrofit errors,
 * we can use this exception to wrap them up.
 */
class ServiceUnavailable(message: String = "", cause: Throwable? = null) : Exception(message, cause)

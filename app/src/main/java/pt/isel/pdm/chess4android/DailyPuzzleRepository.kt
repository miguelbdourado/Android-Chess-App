
package pt.isel.pdm.chess4android

import pt.isel.pdm.chess4android.history.HistoryPuzzleBoardDao
import pt.isel.pdm.chess4android.history.PuzzleBoardEntity
import pt.isel.pdm.chess4android.history.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Extension function of [PuzzleBoardEntity] to conveniently convert it to a [PuzzleOfTheDayDTO] instance.
 * Only relevant for this activity.
 */
fun PuzzleBoardEntity.toPuzzleOfTheDayDTO() = PuzzleOfTheDayDTO(
        puzzleBoardDTO = this.content,
        date = this.id,
        status = this.status
    )
/**
 * Repository for the Quote Of Day
 * It's role is the one described here: https://developer.android.com/jetpack/guide
 *
 * The repository operations include I/O (network and/or DB accesses) and are therefore asynchronous.
 * For now, and for teaching purposes, we use a callback style to define those operations. Later on
 * we will use Kotlin's way: suspending functions.
 */
class QuoteOfDayRepository(
    private val PuzzleOfTheDayService: PuzzleOfTheDay,
    private val historyPuzzleBoardDao: HistoryPuzzleBoardDao
) {

    /**
     * Asynchronously gets the daily quote from the local DB, if available.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncMaybeGetTodayPuzzleFromDB(callback: (Result<PuzzleBoardEntity?>) -> Unit) {
        callbackAfterAsync(callback) {
            historyPuzzleBoardDao.getLast(1).firstOrNull()
        }
    }

    /**
     * Asynchronously gets the daily quote from the remote API.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncGetTodayQuoteFromAPI(callback: (Result<PuzzleOfTheDayDTO>) -> Unit) {
        PuzzleOfTheDayService.getPuzzle().enqueue(
            object: Callback<PuzzleBoardDTO> {
                override fun onResponse(call: Call<PuzzleBoardDTO>, response: Response<PuzzleBoardDTO>) {
                    val dailyPuzzle: PuzzleBoardDTO? = response.body()

                    /* by default. this helps with some null checks down the line*/
                    var result: Result<PuzzleOfTheDayDTO> = Result.failure(ServiceUnavailable())

                    if (dailyPuzzle != null && response.isSuccessful)
                    {
                        val current = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                        val formatted = current.format(formatter)

                        val puzzleOfTheDayDto = PuzzleOfTheDayDTO(dailyPuzzle, formatted, Status.INCOMPLETE)
                        result = Result.success(puzzleOfTheDayDto)
                    }

                    callback(result)
                }

                override fun onFailure(call: Call<PuzzleBoardDTO>, error: Throwable) {
                    callback(Result.failure(ServiceUnavailable(cause = error)))
                }
            })
    }

    /**
     * Asynchronously saves the daily quote to the local DB.
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD.
     */
    private fun asyncSaveToDB(dto: PuzzleOfTheDayDTO, callback: (Result<Unit>) -> Unit = { }) {
        callbackAfterAsync(callback) {
            historyPuzzleBoardDao.insert(
                PuzzleBoardEntity(
                    id = dto.date,
                    status = dto.status,
                    content = dto.puzzleBoardDTO
                )
            )
        }
    }

    /**
     * Asynchronously gets the quote of day, either from the local DB, if available, or from
     * the remote API.
     *
     * @param mustSaveToDB  indicates if the operation is only considered successful if all its
     * steps, including saving to the local DB, succeed. If false, the operation is considered
     * successful regardless of the success of saving the quote in the local DB (the last step).
     * @param callback the function to be called to signal the completion of the
     * asynchronous operation, which is called in the MAIN THREAD
     *
     * Using a boolean to distinguish between both options is a questionable design decision.
     */
    fun fetchPuzzleOfTheDay(mustSaveToDB: Boolean = false, callback: (Result<PuzzleOfTheDayDTO>) -> Unit) {
        asyncMaybeGetTodayPuzzleFromDB { maybeEntity ->
            val maybePuzzle = maybeEntity.getOrNull()

            val current: LocalDate = LocalDate.now()
            val puzzleDate = LocalDate.parse(maybePuzzle?.id)
            val deltaTime = ChronoUnit.DAYS.between(puzzleDate, current)

            if(maybePuzzle != null && deltaTime < 1)
                callback(Result.success(maybePuzzle.toPuzzleOfTheDayDTO()))
            else {
                asyncGetTodayQuoteFromAPI { apiResult ->
                    apiResult.onSuccess { puzzleDTO ->
                        asyncSaveToDB(puzzleDTO) { saveToDBResult ->
                            saveToDBResult.onSuccess {
                                callback(Result.success(puzzleDTO))
                            }
                            .onFailure {
                                callback(if(mustSaveToDB) Result.failure(it) else Result.success(puzzleDTO))
                            }
                        }
                    }
                    callback(apiResult)
                }
            }
        }
    }
}


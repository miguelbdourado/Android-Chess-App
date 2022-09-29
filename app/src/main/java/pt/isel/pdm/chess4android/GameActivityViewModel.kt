package pt.isel.pdm.chess4android

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.work.ListenableWorker
import pt.isel.pdm.chess4android.history.PuzzleBoardEntity
import pt.isel.pdm.chess4android.history.Status
import pt.isel.pdm.chess4android.history.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class GameActivityViewModel(application: Application) : AndroidViewModel(application) {

    init {
        Log.v("APP_TAG", "MainActivityViewModel.init()")
    }

    var showSolution: Boolean = false
    val puzzleOfTheDay: MutableLiveData<PuzzleOfTheDayDTO?> = MutableLiveData()

    fun getDailyPuzzle() {
        val app: PuzzleOfTheDayApplication = getApplication<PuzzleOfTheDayApplication>()
        val repo = QuoteOfDayRepository(app.dailyPuzzleService, app.historyDB.getHistoryPuzzleBoardDao())

        repo.fetchPuzzleOfTheDay(mustSaveToDB = true) { result ->
            result
                .onSuccess { puzzleOfTheDay.value = result.getOrThrow() }
        }
    }

    fun gameOver() {
        val app: PuzzleOfTheDayApplication = getApplication<PuzzleOfTheDayApplication>()
        doAsync {
            puzzleOfTheDay.value?.date?.let {
                puzzleOfTheDay.value?.puzzleBoardDTO?.let { it1 ->
                    PuzzleBoardEntity(
                        it, Status.COMPLETE, it1
                    )
                }
            }?.let { app.historyDB.getHistoryPuzzleBoardDao().update(it) }
        }
    }
}
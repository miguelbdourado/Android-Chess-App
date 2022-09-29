package pt.isel.pdm.chess4android

import android.content.Context
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.google.common.util.concurrent.ListenableFuture

class DownloadDailyPuzzleWorker (appContext: Context, workerParams: WorkerParameters)
    : ListenableWorker(appContext, workerParams) {

    override fun startWork(): ListenableFuture<Result> {
        val app: PuzzleOfTheDayApplication = applicationContext as PuzzleOfTheDayApplication
        val repo =
            QuoteOfDayRepository(app.dailyPuzzleService, app.historyDB.getHistoryPuzzleBoardDao())

        return CallbackToFutureAdapter.getFuture { completer ->
            repo.fetchPuzzleOfTheDay(mustSaveToDB = true) { result ->
                result
                    .onSuccess {
                        completer.set(Result.success())
                    }
                    .onFailure {
                        completer.setException(it)
                    }
            }
        }
    }
}


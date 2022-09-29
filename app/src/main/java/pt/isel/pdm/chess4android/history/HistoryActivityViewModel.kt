package pt.isel.pdm.chess4android.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import pt.isel.pdm.chess4android.PuzzleOfTheDayApplication
import pt.isel.pdm.chess4android.PuzzleOfTheDayDTO

class HistoryActivityViewModel(application: Application) : AndroidViewModel(application) {

    val history: MutableLiveData<List<PuzzleOfTheDayDTO>> by lazy {
        MutableLiveData<List<PuzzleOfTheDayDTO>>()
    }

    private val historyDao : HistoryPuzzleBoardDao by lazy {
        getApplication<PuzzleOfTheDayApplication>().historyDB.getHistoryPuzzleBoardDao()
    }

    fun loadHistory() {
        doAsyncAndCallback(
            asyncAction = {
                historyDao.getAll().map {
                    PuzzleOfTheDayDTO(date = it.id, status= it.status, puzzleBoardDTO = it.content)
                }
            },
            success = { history.value = it},
            error = { history.value = emptyList() }
        )
    }

}
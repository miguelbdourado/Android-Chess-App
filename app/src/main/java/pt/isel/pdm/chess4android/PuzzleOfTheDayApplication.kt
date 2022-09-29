package pt.isel.pdm.chess4android

import android.app.Application
import androidx.room.Room
import pt.isel.pdm.chess4android.history.HistoryDataBase
import pt.isel.pdm.chess4android.history.PuzzleBoardEntity
import pt.isel.pdm.chess4android.history.Status
import pt.isel.pdm.chess4android.history.doAsync
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PuzzleOfTheDayApplication: Application() {

    val dailyPuzzleService : PuzzleOfTheDay by lazy {
        Retrofit.Builder()
            .baseUrl("https://lichess.org/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PuzzleOfTheDay::class.java)

    }

    val historyDB: HistoryDataBase by lazy {
        Room
            .databaseBuilder(this, HistoryDataBase::class.java, "history_db")
            .build()
    }

    private val puzzle = PuzzleBoardDTO(Game("test", Perf("icon", "name"), true, arrayOf(Player("userId", "name", "color")), "e4 e5 Nf3 Nc6 Bc4 Nd4 Nxd4 exd4 Qf3 Qe7 O-O d6 Re1 Nf6 d3 Bg4 Qg3 h5 e5 Nd7 h3 Nxe5 hxg4", "10+0"), Puzzle(
    "id", 10, 100875, 22, arrayOf("e5f3", "g2f3", "e7e1"), arrayOf("short")))

    override fun onCreate() {
        super.onCreate()
        doAsync {
            historyDB.getHistoryPuzzleBoardDao().insert(
                PuzzleBoardEntity(
                    id = "2021-11-16", status = Status.INCOMPLETE, content = puzzle)
            )
            historyDB.getHistoryPuzzleBoardDao().insert(
                PuzzleBoardEntity(
                    id = "2021-11-17", status = Status.COMPLETE, content = puzzle)
            )
            historyDB.getHistoryPuzzleBoardDao().insert(
                PuzzleBoardEntity(
                    id = "2021-11-18", status = Status.INCOMPLETE, content = puzzle)
            )
        }
    }
}
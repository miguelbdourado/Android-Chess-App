package pt.isel.pdm.chess4android.history

import android.os.Handler
import android.os.Looper
import androidx.room.*
import com.google.gson.Gson
import pt.isel.pdm.chess4android.PuzzleBoardDTO
import java.util.concurrent.Executors


class Converters {

    private val gson = Gson()

    @TypeConverter
    fun toPuzzleBoardDtoString( puzzleBoard: PuzzleBoardDTO) : String{
        return gson.toJson(puzzleBoard)
    }

    @TypeConverter
    fun toPuzzleBoardDTO(puzzleBoardString: String) : PuzzleBoardDTO {
        return gson.fromJson(puzzleBoardString, PuzzleBoardDTO::class.java)
    }
}

enum class Status
{
    COMPLETE, INCOMPLETE
}

@Entity(tableName = "history_puzzleboard")
data class PuzzleBoardEntity(
    @PrimaryKey val id: String, //date
    val status: Status, //completed or not
    val content: PuzzleBoardDTO //game? todo check
)

@Dao
interface HistoryPuzzleBoardDao{
    @Insert
    fun insert(puzzleBoard: PuzzleBoardEntity)

    @Delete
    fun delete(puzzleBoard: PuzzleBoardEntity)

    @Query("SELECT * FROM history_puzzleboard ORDER BY id DESC LIMIT 100")
    fun getAll() : List<PuzzleBoardEntity>

    @Query("SELECT * FROM history_puzzleboard ORDER BY id DESC LIMIT :count")
    fun getLast(count: Int) : List<PuzzleBoardEntity>

    @Update
    fun update(puzzleBoard: PuzzleBoardEntity)

}

@Database(entities = [PuzzleBoardEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class HistoryDataBase() : RoomDatabase() {
    abstract fun getHistoryPuzzleBoardDao() : HistoryPuzzleBoardDao
}

/**
 * The executor used to execute data access operations
 */
private val ioThreadPool = Executors.newSingleThreadExecutor()

/**
 * Dispatches the execution of [asyncAction] on the appropriate thread pool.
 * This is a teaching tool. It will soon be discarded.
 * The [asyncAction] result is published by calling, IN THE MAIN THREAD, the received callbacks.
 */
fun <T> doAsyncAndCallback(asyncAction: () -> T, success: (T) -> Unit, error: (Throwable) -> Unit) {
    val mainHandler = Handler(Looper.getMainLooper())
    ioThreadPool.submit {
        try {
            val result = asyncAction()
            mainHandler.post {
                success(result)
            }
        }
        catch (ex: Exception) {
            mainHandler.post {
                error(ex)
            }
        }
    }
}

/**
 * Dispatches the execution of [action] on the appropriate thread pool.
 * This is a teaching tool. It will soon be discarded.
 */
fun doAsync(action: () -> Unit) = ioThreadPool.submit(action)


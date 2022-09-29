package pt.isel.pdm.chess4android

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import pt.isel.pdm.chess4android.databinding.ActivityGameBinding
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.history.HistoryActivity
import pt.isel.pdm.chess4android.views.ChessGame

const val Local_EXTRA = "GameActivity.Extra.Local"
private const val Puzzle_EXTRA = "GameActivity.Extra.Puzzle"
private const val Puzzle_SOLUTION = "GameActivity.Extra.SOLUTION"

class GameActivity : AppCompatActivity() {

    companion object {
        fun buildIntent(origin: Activity, puzzleDto: PuzzleOfTheDayDTO): Intent {
            return buildIntent(origin, puzzleDto, false)
        }

        fun buildIntent(origin: Activity, puzzleDto: PuzzleOfTheDayDTO, enableSolution: Boolean): Intent {
            val msg = Intent(origin, GameActivity::class.java)
            msg.putExtra(Puzzle_EXTRA, puzzleDto)
            msg.putExtra(Puzzle_SOLUTION, enableSolution)
            return msg
        }
    }

    private val binding by lazy {
        ActivityGameBinding.inflate(layoutInflater)
    }

    private val viewModel: GameActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val localMode = intent.getBooleanExtra(Local_EXTRA, false)
        if (localMode) {
            binding.fetchDailyBtn.visibility = View.INVISIBLE
            binding.historyBtn.visibility    = View.INVISIBLE
            binding.nextMove.visibility      = View.INVISIBLE

            binding.boardView.getChessBoard().setGameOverCallback {
                binding.completionText.text =
                    if (binding.boardView.getChessBoard().whiteTurn)
                        getString(R.string.white_win)
                    else getString(R.string.black_win)
                binding.completionText.visibility = View.VISIBLE
            }

            binding.boardView.getChessBoard().startGame()

        } else {
            // On puzzle of the day load
            viewModel.puzzleOfTheDay.observe(this) {
                if(it == null) return@observe

                binding.boardView.getChessBoard()
                    .uploadDailyPuzzle(it.puzzleBoardDTO.game.pgn, it.puzzleBoardDTO.puzzle.solution, viewModel.showSolution)

                binding.boardView.getChessBoard().setGameOverCallback {
                    binding.completionText.visibility = View.VISIBLE

                    viewModel.gameOver()
                    binding.nextMove.visibility = View.INVISIBLE
                }

                if (viewModel.showSolution) binding.nextMove.visibility = View.VISIBLE
                else binding.completionText.visibility = View.INVISIBLE


                if(savedInstanceState != null) {
                    val previousState = savedInstanceState.getParcelable<ChessGame>("CHESS_STATE")

                    if(previousState != null)
                        binding.boardView.getChessBoard().loadPreviousState(previousState)
                }
            }

            // Load puzzle of the day from Extra
            val puzzleOfTheDayDTO = intent.getParcelableExtra<PuzzleOfTheDayDTO>(Puzzle_EXTRA)
            val showSolution = intent.getBooleanExtra(Puzzle_SOLUTION, false)
            if (puzzleOfTheDayDTO != null) {
                viewModel.puzzleOfTheDay.value = puzzleOfTheDayDTO
                viewModel.showSolution = showSolution
            }

            binding.aboutButton.setOnClickListener {
                startActivity(Intent(this, AboutActivity::class.java))
            }

            binding.historyBtn.setOnClickListener {
                startActivity(Intent(this, HistoryActivity::class.java))
            }

            binding.fetchDailyBtn.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder
                    .setMessage(R.string.new_puzzle_confirmation)
                    .setTitle(R.string.confirmation)
                    .setPositiveButton(R.string.yes){ _, _ -> viewModel.getDailyPuzzle() }
                    .setNegativeButton(R.string.no, null)
                    .show()

            }

            binding.nextMove.setOnClickListener {
                binding.boardView.getChessBoard().nextSolutionMove()
            }
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("CHESS_STATE", binding.boardView.getChessBoard().getGameState())
    }


}
package pt.isel.pdm.chess4android.history

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import pt.isel.pdm.chess4android.*
import pt.isel.pdm.chess4android.GameActivity.Companion.buildIntent
import pt.isel.pdm.chess4android.databinding.ActivityHistoryBinding

class HistoryActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHistoryBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<HistoryActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.puzzleList.layoutManager = LinearLayoutManager(this)

        viewModel.history.observe(this) {
            binding.puzzleList.adapter = HistoryViewAdapter(it) { puzzleOfTheDayDTO ->
                if(puzzleOfTheDayDTO.status == Status.COMPLETE)
                {
                    val builder = AlertDialog.Builder(this)
                    builder
                        .setMessage(R.string.puzzle_already_complete_description)
                        .setTitle(R.string.puzzle_already_complete)
                        .setPositiveButton(R.string.Puzzle){ _, _ -> startActivity(buildIntent(this, puzzleOfTheDayDTO)) }
                        .setNegativeButton(R.string.solution){ _, _ -> startActivity(buildIntent(this, puzzleOfTheDayDTO, true)) }
                        .show()

                } else startActivity(buildIntent(this, puzzleOfTheDayDTO))
            }
        }

        viewModel.loadHistory()
    }

}
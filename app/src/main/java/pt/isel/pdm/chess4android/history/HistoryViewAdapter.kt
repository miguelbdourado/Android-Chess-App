package pt.isel.pdm.chess4android.history

import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.recyclerview.widget.RecyclerView
import pt.isel.pdm.chess4android.PuzzleBoardDTO
import pt.isel.pdm.chess4android.PuzzleOfTheDayDTO
import pt.isel.pdm.chess4android.R

class HistoryItemViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {

    private val dayView = itemView.findViewById<TextView>(R.id.day)
    private val statusView = itemView.findViewById<TextView>(R.id.status_puzzle)

    fun bindTo(puzzleOfTheDayDTO: PuzzleOfTheDayDTO, onItemCLick: () -> Unit) {
        dayView.text = puzzleOfTheDayDTO.date
        statusView.text =
            if (puzzleOfTheDayDTO.status == Status.COMPLETE)
                itemView.context.getString(R.string.puzzle_complete_status)
            else
                itemView.context.getString(R.string.puzzle_incomplete_status)

        itemView.setOnClickListener{
            itemView.isClickable = false
            //startAnimation {
                onItemCLick()
                itemView.isClickable = true
            //}
        }

    }

    private fun startAnimation(onAnimationEnd: () -> Unit) {

        val animation = ValueAnimator.ofArgb(
            ContextCompat.getColor(itemView.context, R.color.cardview_dark_background),
            ContextCompat.getColor(itemView.context, R.color.cardview_light_background),
            ContextCompat.getColor(itemView.context, R.color.purple_200)
        )

        animation.addUpdateListener { animator ->
            val background = itemView.background as GradientDrawable
            background.setColor(animator.animatedValue as Int)
        }

        animation.duration = 400
        animation.doOnEnd { onAnimationEnd() }

        animation.start()
    }

}

class HistoryViewAdapter(
    private val dataSource : List<PuzzleOfTheDayDTO>,
    private val onItemCLick: (PuzzleOfTheDayDTO) -> Unit
) :
    RecyclerView.Adapter<HistoryItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_puzzle_list, parent, false)
        return HistoryItemViewHolder(view)

    }

    override fun onBindViewHolder(holder: HistoryItemViewHolder, position: Int) {
        holder.bindTo(dataSource[position]) {
            onItemCLick(dataSource[position])
        }
    }

    override fun getItemCount() = dataSource.size
}
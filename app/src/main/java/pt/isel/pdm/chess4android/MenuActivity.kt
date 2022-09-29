package pt.isel.pdm.chess4android

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {



    private val binding by lazy {
        ActivityMenuBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.playOnlineButton.setOnClickListener() {
            Toast.makeText(applicationContext, getString(R.string.todo), Toast.LENGTH_SHORT).show()
        }

        binding.playLocalButton.setOnClickListener() {
            val msg = Intent(this, GameActivity::class.java)
            msg.putExtra(Local_EXTRA, true)
            startActivity(msg)
        }

        binding.DailyPuzzleButton.setOnClickListener() {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }
}
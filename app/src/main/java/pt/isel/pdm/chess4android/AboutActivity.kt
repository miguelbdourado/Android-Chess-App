package pt.isel.pdm.chess4android

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import pt.isel.pdm.chess4android.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity(){

    private val binding by lazy {
        ActivityAboutBinding.inflate(layoutInflater)
    }

    private val githubUrl = "https://github.com/Herooyyy/PDM-2122i-LI5X-G12/tree/main"
    private val lichessUrl = "https://lichess.org/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


        binding.githubIconButton.setOnClickListener{
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubUrl)))
        }

        binding.lichessButtonImage.setOnClickListener{
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(lichessUrl)))
        }
    }
}
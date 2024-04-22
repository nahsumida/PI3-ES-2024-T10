package br.edu.puccampinas.safepack.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityCadastroCartaoBinding

class CadastroCartaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroCartaoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroCartaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.habilitarCartaoButton.setOnClickListener(View.OnClickListener {
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        })

        binding.arrow.setOnClickListener {
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        }
    }
}
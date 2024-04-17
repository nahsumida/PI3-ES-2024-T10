package br.edu.puccampinas.safepack

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.databinding.ActivityInfoArmarioBinding

class InfoArmarioActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityInfoArmarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.alugarArmarioButton.setOnClickListener {
            val iAlugarArmario = Intent(this, AlugarArmarioActivity::class.java)
            startActivity(iAlugarArmario)
        }

        binding.arrow.setOnClickListener {
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        }
    }
}
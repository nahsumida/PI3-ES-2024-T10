package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding
import br.edu.puccampinas.safepack.databinding.ActivityArmarioLiberadoBinding

class ArmarioLiberadoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArmarioLiberadoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArmarioLiberadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.voltarMenuButton.setOnClickListener {
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        }
    }
}
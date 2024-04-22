package br.edu.puccampinas.safepack.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding

class AlugarArmarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlugarArmarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAlugarArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrow.setOnClickListener {
            val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
            startActivity(iInfoArmario)
        }

    }
}
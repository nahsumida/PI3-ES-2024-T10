package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityArmarioAbertoBinding

class ArmarioAbertoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArmarioAbertoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArmarioAbertoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.voltarMenuButton.setOnClickListener {
            val iTelaInicio = Intent(this, TelaInicialGerenteActivity::class.java)
            startActivity(iTelaInicio)
        }
    }
}
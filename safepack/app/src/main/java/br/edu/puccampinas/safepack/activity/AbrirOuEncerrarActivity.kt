package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityAbrirOuEncerrarBinding

class AbrirOuEncerrarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAbrirOuEncerrarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAbrirOuEncerrarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idLocacao = intent.getStringExtra("idLocacao")
        val numeroCliente = intent.getIntExtra("numeroCliente", 0)
        val qtdClientes = intent.getIntExtra("qtdClientes", 0)

        binding.arrow.setOnClickListener {
            val iTelaInicio = Intent(this, TelaInicialGerenteActivity::class.java)
            startActivity(iTelaInicio)
        }

        binding.encerrarLocacaoButton.setOnClickListener {
            val iEncerrarLocacao = Intent(this, EncerrarLocacaoActivity::class.java)
            iEncerrarLocacao.putExtra("idLocacao", idLocacao)
            startActivity(iEncerrarLocacao)
        }

        binding.abrirTemporariamenteButton.setOnClickListener {
            val iArmarioAberto = Intent(this, ArmarioAbertoActivity::class.java)
            startActivity(iArmarioAberto)
        }
    }
}
package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityEncerrarLocacaoBinding
import br.edu.puccampinas.safepack.repositories.LocacaoRepository

class EncerrarLocacaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEncerrarLocacaoBinding
    private lateinit var locacaoRepository: LocacaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEncerrarLocacaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locacaoRepository = LocacaoRepository()

        val idLocacao = intent.getStringExtra("idLocacao")

        binding.encerrarButton.setOnClickListener {
            if(idLocacao != null) locacaoRepository.setStatusLocacao(idLocacao, "encerrada")

            val iLimparNFC = Intent(this, LimparPulseiraNfcActivity::class.java)
            startActivity(iLimparNFC)
        }

        binding.cancelarButton.setOnClickListener {
            val iTelaInicio = Intent(this, TelaInicialGerenteActivity::class.java)
            startActivity(iTelaInicio)
        }
    }
}
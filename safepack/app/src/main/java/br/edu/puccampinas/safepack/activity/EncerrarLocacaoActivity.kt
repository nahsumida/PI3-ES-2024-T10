package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityEncerrarLocacaoBinding
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import com.google.firebase.Timestamp
import java.util.concurrent.TimeUnit

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
            if(idLocacao != null) finalizarLocacao(idLocacao)

            val iLimparNFC = Intent(this, LimparPulseiraNfcActivity::class.java)
            startActivity(iLimparNFC)
        }

        binding.cancelarButton.setOnClickListener {
            val iTelaInicio = Intent(this, TelaInicialGerenteActivity::class.java)
            startActivity(iTelaInicio)
        }
    }

    private fun finalizarLocacao(idLocacao: String) {
        val fim = Timestamp.now()

        locacaoRepository.setFimLocacao(idLocacao, Timestamp.now())
        locacaoRepository.getLocacaoById(idLocacao)
            .addOnSuccessListener { loc ->
                val inicio = loc.getTimestamp("inicio")
                val valorHora = loc.getDouble("valorHora")
                val tempoUtilizado = if(inicio != null) {
                    TimeUnit.MILLISECONDS.toHours(
                        fim.toDate().time - inicio.toDate().time
                    ).toInt()
                } else {
                    0
                }

                val valorUtilizado = valorHora?.times(tempoUtilizado)

                val diaria = 11 * valorHora!!

                val stringTempo = loc.getString("tempo")
                Log.d("EncerrarLocacaoActivity", "stringTempo: $stringTempo")

                val valorEstorno = diaria - valorUtilizado!!
                Log.d("EncerrarLocacaoActivity", "Estorno: $valorEstorno")

                locacaoRepository.setEstorno(idLocacao, valorEstorno)
                locacaoRepository.setFimLocacao(idLocacao, fim)
                locacaoRepository.setStatusLocacao(idLocacao, "encerrada")
            }
    }

}
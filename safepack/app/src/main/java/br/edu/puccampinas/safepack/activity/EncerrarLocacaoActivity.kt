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
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.IOException

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

                val valorEstorno = diaria - valorUtilizado!!

                // Chamar a Firebase Function addEstornoLocacao
                chamarFirebaseFunction(idLocacao, valorEstorno, fim)
            }
    }

    private fun chamarFirebaseFunction(idLocacao: String, valorEstorno: Double, fim: Timestamp) {
        val client = OkHttpClient()
        val url = "https://southamerica-east1-pi3-es-2024-t10.cloudfunctions.net/addEstornoLocacao"

        val json = JSONObject().apply {
            put("idLocacao", idLocacao)
            put("valorEstorno", valorEstorno)
            put("fim", fim.toDate().time)
        }

        val mediaType = "application/json; charset=utf-8".toMediaType()
        val body = RequestBody.create(mediaType, json.toString())

        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("EncerrarLocacaoActivity", "Erro ao chamar a Firebase Function", e)
            }

            override fun onResponse(call: Call, response: Response) {
                val responseBody = response.body?.string()
                if (!response.isSuccessful) {
                    Log.e("EncerrarLocacaoActivity", "Erro na resposta da Firebase Function: $responseBody")
                } else {
                    responseBody?.let {
                        Log.d("EncerrarLocacaoActivity", "Resposta da Firebase Function: $it")
                    } ?: run {
                        Log.e("EncerrarLocacaoActivity", "Resposta da Firebase Function é nula")
                    }
                    runOnUiThread {
                        // Atualizar status da locação e interface do usuário
                        locacaoRepository.setStatusLocacao(idLocacao, "encerrada")
                        Log.d("EncerrarLocacaoActivity", "Estorno registrado com sucesso e status atualizado")
                    }
                }
            }
        })
    }
}
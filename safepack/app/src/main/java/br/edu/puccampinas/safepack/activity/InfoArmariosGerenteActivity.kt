package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityInfoArmariosGerenteBinding
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository

class InfoArmariosGerenteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoArmariosGerenteBinding
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository
    private lateinit var locacaoRepository: LocacaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoArmariosGerenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unidadeLocacaoRepository = UnidadeLocacaoRepository()
        locacaoRepository = LocacaoRepository()

        val idLocacao = intent.getStringExtra("idLocacao")

        if (idLocacao != null) {
            mostrarInfoLocacao(idLocacao)
        }

        binding.okButton.setOnClickListener {
            val iTelaInicial = Intent(this, TelaInicialGerenteActivity::class.java)
            startActivity(iTelaInicial)
        }
    }

    private fun mostrarInfoLocacao(id: String) {
        locacaoRepository.getLocacaoById(id).addOnSuccessListener { loc ->
            val idUnidade = loc.getString("unidadeId")
            Log.d("InfoArmariosActivity", "$idUnidade")
            val idArmario = loc.getString("armarioId")
            Log.d("InfoArmariosActivity", "$idArmario")
            val porta = loc.getDouble("porta")?.toInt()
            if(idUnidade != null && idArmario != null) {
                unidadeLocacaoRepository.getArmariosDaUnidade(idUnidade)
                    .addOnSuccessListener{ armarios ->
                        for(armario in armarios) {
                            if(armario.id == idArmario) {
                                Log.d("InfoArmariosActivity", "$porta")
                                val nomeArmario = armario.getString("nome")
                                Log.d("InfoArmariosActivity", "$nomeArmario")

                                if(porta != null && nomeArmario != null) {
                                    binding.tvArmario.text = stringArmario(
                                        binding.tvArmario.text.toString(),
                                        nomeArmario
                                    )
                                    binding.tvPorta.text = stringPorta(
                                        binding.tvPorta.text.toString(),
                                        porta
                                    )
                                } else {
                                    Log.e("InfoArmarioActivity", "Nome ou porta nulos")
                                }
                            } else {
                                Log.e("InfoArmarioActivity", "Nenhum armario com o id $idArmario")
                            }
                        }
                    }
            }
        }
    }

    private fun stringPorta(texto: String, porta: Int?): String {
        Log.d("InfoArmariosActivity", "$texto $porta")
        return texto + porta
    }

    private fun stringArmario(texto: String, nome: String): String {
        Log.d("InfoArmariosActivity", "$texto $nome")
        return texto + nome
    }
}
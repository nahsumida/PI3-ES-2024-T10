package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityVerificarClientesBinding
import br.edu.puccampinas.safepack.models.Locacao
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import java.lang.Exception

class VerificarClientesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerificarClientesBinding
    private lateinit var locacaoRepository: LocacaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVerificarClientesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locacaoRepository = LocacaoRepository()

        val idLocacao = intent.getStringExtra("idLocacao")
        val numeroCliente = intent.getIntExtra("numeroCliente", 0)
        val qtdClientes = intent.getIntExtra("qtdClientes", 0)

        if(idLocacao != null && numeroCliente != 0) {
            exibirFotoCliente(idLocacao, numeroCliente)
            Log.d("VerificarClientes", "Método exibirFotoCliente chamado")
        } else {
            Log.e("VerificarClientes", "idLocacao ou numeroCliente nulo")
        }

        binding.prosseguirButton.setOnClickListener {
            val iAbrirOuEncerrar = Intent(this, AbrirOuEncerrarActivity::class.java)
            iAbrirOuEncerrar.putExtra("idLocacao", idLocacao)
            startActivity(iAbrirOuEncerrar)
        }

        binding.cancelarButton.setOnClickListener {
            val iTelaInicio = Intent(this, TelaInicialGerenteActivity::class.java)
            startActivity(iTelaInicio)
        }
    }

    private fun exibirFotoCliente(idLoc: String, numCliente: Int) {
        locacaoRepository.getLocacaoById(idLoc)
            .addOnSuccessListener { loc ->
                if(loc != null) {
                    val imageUrl: String?
                    if(numCliente == 1) {
                        imageUrl = loc.getString("fotoPessoa1")
                    } else {
                        imageUrl = loc.getString("fotoPessoa2")
                    }

                    if(imageUrl != null) carregarImagem(imageUrl)
                } else {
                    Log.e("VerificarClientes", "Locação não encontrada")
                }
            }
            .addOnFailureListener { e ->
                Log.e("VerificarClientes", "Erro: e")
            }
    }

    private fun carregarImagem(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .into(binding.imageCliente)
    }
}
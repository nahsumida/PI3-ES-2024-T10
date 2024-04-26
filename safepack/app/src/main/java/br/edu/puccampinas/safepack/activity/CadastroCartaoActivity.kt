package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityCadastroCartaoBinding
import br.edu.puccampinas.safepack.models.Cartao
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import br.edu.puccampinas.safepack.repositories.PessoaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroCartaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroCartaoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pessoaRepository: PessoaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        pessoaRepository = PessoaRepository()

        binding = ActivityCadastroCartaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.habilitarCartaoButton.setOnClickListener(View.OnClickListener {
            var cartao = Cartao(
                nomeTitular =  binding.nomeTitular.text.toString().trim(),
                 dataValidade =  binding.validade.text.toString().trim(),
                 CVV = binding.cvv.text.toString().trim(),
                 numCartao = binding.numCartao.text.toString().trim()
            )

            getPessoaID(auth, pessoaRepository, cartao)
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        })

        binding.arrow.setOnClickListener {
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        }
    }

    //esse funciona

    fun getPessoaID(auth: FirebaseAuth,
                    pessoaR: PessoaRepository,
                    cartao: Cartao) {

        val currentUser = auth.currentUser
        if (currentUser != null) {
            pessoaR.getIdByAuthId(currentUser.uid) { idUser ->
                if (idUser != "null") {
                    addCartaoCredito(idUser, cartao)
                }
            }
        }
    }

    fun addCartaoCredito(idPessoa: String, cartao: Cartao){
        lateinit var firebase: FirebaseFirestore;
        firebase = FirebaseFirestore.getInstance()

        // Criando um objeto cartao com os dados do cartão e uma referência para o documento da pessoa
        val cartao = hashMapOf(
            "dataValidade" to cartao.dataValidade,
            "nome" to cartao.nomeTitular,
            "numero" to cartao.numCartao
        )

        // Adicionando o cartão à subcoleção "cartao" de coleção "pessoa"
        firebase.collection("pessoa").document(idPessoa).collection("cartao")
            .add(cartao)
            .addOnSuccessListener { documentReference ->
                Log.d("CadastroCartao", "Cartão adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Erro", "Erro ao adicionar cartão", e)
            }
    }
}
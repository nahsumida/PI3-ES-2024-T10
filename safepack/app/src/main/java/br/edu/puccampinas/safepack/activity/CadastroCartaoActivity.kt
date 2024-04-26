package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
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

        // inflar o layout da activity
        binding = ActivityCadastroCartaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inicializar as instancias do FirebaseAuth e PessoaRepository
        auth = FirebaseAuth.getInstance()
        pessoaRepository = PessoaRepository()

        // configurar clique do botão habilitar cartão
        binding.habilitarCartaoButton.setOnClickListener(View.OnClickListener {

            // criar objeto Cartao com os dados do formulário
            var cartao = Cartao(
                nomeTitular =  binding.nomeTitular.text.toString().trim(),
                dataValidade =  binding.validade.text.toString().trim(),
                CVV = binding.cvv.text.toString().trim(),
                numCartao = binding.numCartao.text.toString().trim()
            )

            // validar campos do formulário
            if (cartao.nomeTitular.isEmpty() || !cartao.isNomeValido()){
                binding.nomeTitular.setError("Preencha com um nome válido")
            } else if (cartao.dataValidade.isEmpty() || !cartao.isDataValidadeValida()){
                binding.validade.setError("Preencha com uma data de validade válida")
            } else if (cartao.CVV.isEmpty() || !cartao.isCVVValido()) {
                binding.cvv.setError("Preencha com um cvv válido")
            } else if (cartao.numCartao.isEmpty() || !cartao.isNumCartaoValido()){
                binding.numCartao.setError("Preencha com um número de cartão válido")
            }  else {
                getPessoaID(auth, pessoaRepository, cartao)
                val iMaps = Intent(this, MapsActivity::class.java)
                startActivity(iMaps)
            }
        })

        // configurar o clique da seta para voltar a activity anterior
        binding.arrow.setOnClickListener {
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        }
    }

    // método para obter o ID da pessoa e adicionar o cartão
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

    // método para adicionar o cartão de crédito ao firestore
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
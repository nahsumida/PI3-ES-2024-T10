package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityCadastroCartaoBinding
import com.google.firebase.firestore.FirebaseFirestore

class CadastroCartaoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroCartaoBinding
    lateinit var numCartao:String;
    lateinit var nomeTitular:String;
    lateinit var validade:String;
    lateinit var cvv:String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroCartaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.habilitarCartaoButton.setOnClickListener(View.OnClickListener {
            numCartao = binding.numCartao.text.toString().trim();
            nomeTitular = binding.nomeTitular.text.toString().trim();
            validade = binding.validade.text.toString().trim();
            cvv = binding.cvv.text.toString().trim();

            //var idPessoa =  intent.getStringExtra("idPessoa") as String

            addCartaoCredito("SgR0f9S3EuLjRJmduhaw", numCartao, validade,nomeTitular)

        })

        binding.arrow.setOnClickListener {
            val iMaps = Intent(this, MapsActivity::class.java)
            startActivity(iMaps)
        }
    }

    //esse funciona
    fun addCartaoCredito(idPessoa: String,numCartao: String, validade: String, nome: String){
        lateinit var firebase: FirebaseFirestore;
        firebase = FirebaseFirestore.getInstance()

        // Criando um objeto cartao com os dados do cartão e uma referência para o documento da pessoa
        val cartao = hashMapOf(
           // "pessoa" to firebase.collection("pessoa").document(idPessoa),
            "dataValidade" to validade,
            "nome" to nome,
            "numero" to numCartao
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
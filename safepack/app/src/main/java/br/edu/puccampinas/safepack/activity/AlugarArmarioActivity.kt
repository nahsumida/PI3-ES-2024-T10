package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding
import br.edu.puccampinas.safepack.models.Locacao
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import br.edu.puccampinas.safepack.repositories.PessoaRepository
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AlugarArmarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlugarArmarioBinding
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository
    private lateinit var radioGroup: RadioGroup
    private lateinit var locacaoRepository: LocacaoRepository
    private lateinit var pessoaRepository: PessoaRepository
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlugarArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unidadeLocacaoRepository = UnidadeLocacaoRepository()
        locacaoRepository = LocacaoRepository()
        pessoaRepository = PessoaRepository()

        auth = FirebaseAuth.getInstance()

        radioGroup = findViewById(R.id.radioGroup)

        val idUnidade = intent.getStringExtra("idUnidade")
        val statusLogin = intent.getStringExtra("statusLogin")

        if (idUnidade!=null) inserirPrecos(binding, idUnidade, unidadeLocacaoRepository)

        binding.arrow.setOnClickListener {
            val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
            iInfoArmario.putExtra("statusLogin", statusLogin)
            iInfoArmario.putExtra("idUnidade", idUnidade)
            startActivity(iInfoArmario)
        }

        binding.confirmarLocacaoButton.setOnClickListener {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            Log.d("AUTH", "${auth.currentUser?.uid}")
            if(currentUser != null) {
                Log.d("AUTH", "Usuário logado")
                verificarCartao(auth, pessoaRepository) {result ->
                    Log.d("CARTAO", "VERIFICADO: $result")
                    if(result) {
                        Log.d("VERIFICADO", "O usuário possui cartão")
                        val idButton = radioGroup.checkedRadioButtonId
                        val selectedButton: RadioButton = findViewById(idButton)
                        if (idUnidade != null) {
                            adicionarLocacao(
                                idUnidade,
                                unidadeLocacaoRepository,
                                selectedButton.text.toString(),
                                locacaoRepository,
                                pessoaRepository
                            )

                            val iQRCode = Intent(this, QrCodeActivity::class.java)
                            iQRCode.putExtra("idQRCode", idUnidade)
                            startActivity(iQRCode)
                        }
                    } else {
                        Toast.makeText(this, "Cadastre um cartão para poder alugar o armário",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Log.d("AUTH", "Usuário não está logado")
                Toast.makeText(this, "Realize o login para poder alugar o armário",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun inserirPrecos(binding: ActivityAlugarArmarioBinding,
                              id: String,
                              unidadeR: UnidadeLocacaoRepository) {
        unidadeR.getUnidadeById(id).addOnSuccessListener { unidade ->
            if(unidade != null && unidade.exists()) {
                binding.radioButton1.text = stringPreco(binding.radioButton1.text.toString(),
                    calcularValor(.5,unidade.getDouble("valorHora")))
                binding.radioButton2.text = stringPreco(binding.radioButton2.text.toString(),
                    calcularValor(1.0,unidade.getDouble("valorHora")))
                binding.radioButton3.text = stringPreco(binding.radioButton3.text.toString(),
                    calcularValor(2.0,unidade.getDouble("valorHora")))
                binding.radioButton4.text = stringPreco(binding.radioButton4.text.toString(),
                    calcularValor(2.5,unidade.getDouble("valorHora")))
                binding.radioButton5.text = stringPreco(binding.radioButton5.text.toString(),
                    calcularValor(3.0,unidade.getDouble("valorHora")))
                binding.radioButton6.text = stringPreco(binding.radioButton6.text.toString(),
                    calcularValor(4.0,unidade.getDouble("valorHora")))
            }
        } .addOnFailureListener { e ->
            Log.e("Firestore Valor", "ERRO", e)
        }
    }

    private fun stringPreco(texto: String, valor: Double): String {
        Log.d("String", "$texto + $valor")
        return texto + valor
    }

    private fun calcularValor(tempo: Double, valor: Double?): Double {
        if (valor!=null) return tempo * valor
        return 0.0
    }

    private fun verificarCartao(auth: FirebaseAuth,
                                pessoaR: PessoaRepository,
                                callback: (Boolean) -> Unit) {

        val authId: String? = auth.currentUser?.uid
        var pessoaId = ""
        pessoaR.getAllPessoas()
            .addOnSuccessListener { pessoas ->
                var cartaoEncontrado = false
                for(pessoa in pessoas) {
                    if(pessoa.getString("authID").equals(authId)) {
                        pessoaId += pessoa.id
                        pessoaR.getCartaoPessoa(pessoaId)
                            .addOnSuccessListener { cartoes ->
                                for(cartao in cartoes) {
                                    cartaoEncontrado = true
                                    callback(true)
                                }
                            }
                            .addOnFailureListener {e ->
                                Log.e("GET_CARTAO", "ERRO", e)
                            }
                        break
                    }
                }
                if(!cartaoEncontrado) callback(false)
            }
    }

    private fun converterString(texto: String): String {
        val primeiroChar = texto[0]
        when(primeiroChar) {
            '3' -> return "30 minutos"
            '1' -> return "1 hora"
            '2' -> return "2 horas"
            '4' -> return "4 horas"
            '6' -> return "6 horas"
            'a' -> return "até as 18:00"
        }
        return "0"
    }

    private fun adicionarLocacao(idUnidade: String,
                                 unidadeR: UnidadeLocacaoRepository,
                                 textoRadio: String,
                                 locacaoR: LocacaoRepository,
                                 pessoaR: PessoaRepository){
        val tempo = converterString(textoRadio)
        var armarioId = ""
        var locatarioId = ""
        val inicio = Timestamp.now()
        val authId:String? = auth.currentUser?.uid
        val status = "pendente"

        unidadeR.getArmariosDaUnidade(idUnidade)
            .addOnSuccessListener { armarios ->
                for(armario in armarios) {
                    if(armario.getString("status").equals("livre")) {
                        armarioId += armario.id
                        unidadeR.setStatusArmario(idUnidade, armario.id, "ocupado")
                        break
                    }
                }
                pessoaR.getAllPessoas()
                    .addOnSuccessListener { pessoas ->
                        for(pessoa in pessoas) {
                            if(pessoa.getString("authID").equals(authId)) {
                                locatarioId += pessoa.id
                                break
                            }
                        }
                        unidadeR.getUnidadeById(idUnidade).addOnSuccessListener { unidade ->
                            if(unidade != null && unidade.exists()) {
                                val valorHora = unidade.getDouble("valorHora")

                                val locacao = Locacao(
                                    armarioId,
                                    inicio,
                                    locatarioId,
                                    status,
                                    tempo,
                                    idUnidade,
                                    valorHora)

                                locacaoR.addLocacao(locacao)
                            }
                        }
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "ERRO getArmarios", e)
            }
    }
}
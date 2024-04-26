package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
import java.util.Calendar

class AlugarArmarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlugarArmarioBinding
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository
    private lateinit var radioGroup: RadioGroup
    private lateinit var locacaoRepository: LocacaoRepository
    private lateinit var pessoaRepository: PessoaRepository
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflar o layout da activity
        binding = ActivityAlugarArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inicializar os repositórios e o FirebaseAuth
        unidadeLocacaoRepository = UnidadeLocacaoRepository()
        locacaoRepository = LocacaoRepository()
        pessoaRepository = PessoaRepository()
        auth = FirebaseAuth.getInstance()

        // obter referencias para os elementos do layout
        radioGroup = findViewById(R.id.radioGroup)

        //obter dados passados na Intent
        val idUnidade = intent.getStringExtra("idUnidade")
        val statusLogin = intent.getStringExtra("statusLogin")

        // inserir os preços dos radio buttons
        if (idUnidade!=null) inserirPrecos(binding, idUnidade, unidadeLocacaoRepository)

        // configurar clique do botão de seta para voltar para a activity anterior
        binding.arrow.setOnClickListener {
            val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
            iInfoArmario.putExtra("statusLogin", statusLogin)
            iInfoArmario.putExtra("idUnidade", idUnidade)
            startActivity(iInfoArmario)
        }

        //configurar clique do botão de confirmar locação
        binding.confirmarLocacaoButton.setOnClickListener {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            Log.d("AUTH", "${auth.currentUser?.uid}")
            if(currentUser != null) {
                var cartaoVerificado = false

                // verificar se o usuário possui cartão cadastrado
                verificarCartao(auth, pessoaRepository) {result ->
                    Log.d("CARTAO", "VERIFICADO: $result")
                    if(result && !cartaoVerificado) {
                        Log.d("VERIFICADO", "O usuário possui cartão")
                        cartaoVerificado = true

                        // obter radio button selecionado
                        val idButton = radioGroup.checkedRadioButtonId
                        val selectedButton: RadioButton = findViewById(idButton)
                        if (idUnidade != null) {

                            // adicionar a locação ao firebase firestore
                            adicionarLocacao(
                                idUnidade,
                                unidadeLocacaoRepository,
                                selectedButton.text.toString(),
                                locacaoRepository,
                                pessoaRepository
                            )

                            // obter unidade de locação para calcular o valor da locação
                            unidadeLocacaoRepository.getUnidadeById(idUnidade)
                                .addOnSuccessListener { unidade ->

                                    // calcular e exibir o valor da diária
                                    val caucaoDiaria = calcularValor(8.0,
                                        unidade.getDouble("valorHora"))
                                    Log.d("COBRANÇA CARTÃO", "Valor diária: R$$caucaoDiaria")

                                    // abrir a activity QrCodeActivity
                                    val iQRCode = Intent(this, QrCodeActivity::class.java)
                                    iQRCode.putExtra("idQRCode", idUnidade)
                                    startActivity(iQRCode)
                                }
                        }
                    } else if (!cartaoVerificado) {

                        // exibir mensagem se o usuário não tiver um cartão cadastrado
                        Toast.makeText(this, "Cadastre um cartão para poder alugar o armário",
                            Toast.LENGTH_SHORT).show()
                    }
                }
            } else {

                // exibir mensagem se o usuário não estiver logado
                Log.d("AUTH", "Usuário não está logado")
                Toast.makeText(this, "Realize o login para poder alugar o armário",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // verificar horário para exibir ou ocultar o radio button "até as 18:00"
        verificarHorario()
    }

    // método para verificar se é hora de exibir o radio button
    private fun verificarHorario() {
        val cal = Calendar.getInstance()
        val horaAtual = cal.get(Calendar.HOUR_OF_DAY)

        // exibir o radio button entre 7h e 8h
        if(horaAtual in 7..8) {
            binding.radioButton6.visibility = View.VISIBLE
        } else {
            binding.radioButton6.visibility = View.GONE
        }
    }

    // método para inserir os preços nos radio buttons
    private fun inserirPrecos(binding: ActivityAlugarArmarioBinding,
                              id: String,
                              unidadeR: UnidadeLocacaoRepository) {
        unidadeR.getUnidadeById(id).addOnSuccessListener { unidade ->
            if(unidade != null && unidade.exists()) {

                // calcular e exibir os preços nos radio buttons
                binding.radioButton1.text = stringPreco(binding.radioButton1.text.toString(),
                    calcularValor(.5,unidade.getDouble("valorHora")))
                binding.radioButton2.text = stringPreco(binding.radioButton2.text.toString(),
                    calcularValor(1.0,unidade.getDouble("valorHora")))
                binding.radioButton3.text = stringPreco(binding.radioButton3.text.toString(),
                    calcularValor(2.0,unidade.getDouble("valorHora")))
                binding.radioButton4.text = stringPreco(binding.radioButton4.text.toString(),
                    calcularValor(4.0,unidade.getDouble("valorHora")))
                binding.radioButton5.text = stringPreco(binding.radioButton5.text.toString(),
                    calcularValor(6.0,unidade.getDouble("valorHora")))
                binding.radioButton6.text = stringPreco(binding.radioButton6.text.toString(),
                    calcularValor(8.0,unidade.getDouble("valorHora")))
            }
        } .addOnFailureListener { e ->
            Log.e("Firestore Valor", "ERRO", e)
        }
    }

    // método para formatar o texto com o preço
    private fun stringPreco(texto: String, valor: Double): String {
        Log.d("PREÇO", "$texto + $valor")
        return texto + valor
    }

    // método para calcular o valor da locação
    private fun calcularValor(tempo: Double, valor: Double?): Double {
        if (valor!=null) return tempo * valor
        return 0.0
    }

    // método para verificar se o usuário possui o cartão cadastrado
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
                                }

                                if(cartaoEncontrado) {
                                    callback(true)
                                } else {
                                    callback(false)
                                }
                            }
                            .addOnFailureListener {e ->
                                Log.e("GET_CARTAO", "ERRO", e)
                            }
                        break
                    }
                }
            }
    }

    // método para converter o texto do radio button em tempo
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

    // método para adicionar a locação ao firebase firestore
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
                    armarioId += armario.id
                    break
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
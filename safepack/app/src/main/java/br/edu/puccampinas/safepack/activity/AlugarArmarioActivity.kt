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
import br.edu.puccampinas.safepack.model.Locacao
import br.edu.puccampinas.safepack.repository.LocacaoRepository
import br.edu.puccampinas.safepack.repository.UnidadeLocacaoRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AlugarArmarioActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAlugarArmarioBinding
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository
    private lateinit var radioGroup: RadioGroup
    private lateinit var locacaoRepository: LocacaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAlugarArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unidadeLocacaoRepository = UnidadeLocacaoRepository()

        locacaoRepository = LocacaoRepository()

        radioGroup = findViewById(R.id.radioGroup)

        val idUnidade = intent.getStringExtra("idUnidade")

        if (idUnidade!=null) inserirPrecos(binding, idUnidade, unidadeLocacaoRepository)

        binding.arrow.setOnClickListener {
            val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
            startActivity(iInfoArmario)
        }

        binding.confirmarLocacaoButton.setOnClickListener {
            val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            Log.d("AUTH", "${currentUser}")
            if(currentUser!=null) {
                val idButton = radioGroup.checkedRadioButtonId
                val selectedButton: RadioButton = findViewById(idButton)
                if(idUnidade != null) {
                    adicionarLocacao(idUnidade,
                        unidadeLocacaoRepository,
                        selectedButton.text.toString(),
                        locacaoRepository)

                    val iQRCode = Intent(this, QrCodeActivity::class.java)
                    iQRCode.putExtra("idQRCode", idUnidade)
                    startActivity(iQRCode)
                }
            } else {
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
                                 locacaoR: LocacaoRepository){
        val tempo = converterString(textoRadio)
        var armarioId = ""
        val locatarioId = "teste"
        val inicio = Timestamp.now()

        unidadeR.getArmariosDaUnidade(idUnidade)
            .addOnSuccessListener { armarios ->
                for(armario in armarios) {
                    if(armario.getString("status").equals("livre")) {
                        armarioId += armario.id
                        unidadeR.setStatusArmario(idUnidade, armario.id, "ocupado")
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
                            tempo,
                            idUnidade,
                            valorHora)

                        locacaoR.addLocacao(locacao)
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "ERRO getArmarios", e)
            }
    }
}
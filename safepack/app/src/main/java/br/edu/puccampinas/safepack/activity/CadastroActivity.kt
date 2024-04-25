package br.edu.puccampinas.safepack.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityCadastroBinding
import br.edu.puccampinas.safepack.model.Pessoa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.type.DateTime
import okio.ByteString
import java.net.URL
import java.text.SimpleDateFormat

class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding

    private lateinit var auth: FirebaseAuth;
    private lateinit var functions: FirebaseFunctions;

    lateinit var email: String;
    lateinit var senha: String;
    lateinit var senhaConf: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance();
        functions = FirebaseFunctions.getInstance();

        binding.cadastroButton.setOnClickListener(View.OnClickListener {
            email = binding.email.text.toString().trim();
            senha = binding.senha.text.toString().trim();
            senhaConf = binding.senhaConfirmacao.text.toString().trim();

            var pessoa = Pessoa(
                nomeCompleto = binding.nome.text.toString().trim(),
                cpf = binding.cpf.text.toString().trim(),
                dataNascimento = binding.dataNascimento.text.toString().trim(), //LocalDate.of(1990, 5, 15), // Supondo que a data de nascimento seja 15/05/1990
                telefone = binding.telefone.text.toString().trim(),
                senha = binding.senha.text.toString().trim(),
                authID = "",
                ehGerente = false
            )

            if (pessoa.nomeCompleto.isEmpty() || !pessoa.isNomeValido()){
                binding.nome.setError("Preencha com um nome válido")
            } else if (pessoa.cpf.isEmpty() || !pessoa.isCpfValido()){
                binding.cpf.setError("Preencha com um cpf válido")
            } else if (pessoa.dataNascimento.isEmpty() || !pessoa.isDataNascimentoValida()) {
                binding.dataNascimento.setError("Preencha com uma data válida")
            } else if (pessoa.telefone.isEmpty() || !pessoa.isTelefoneValido()){
                binding.telefone.setError("Preencha com um telefone válido")
            } else if (pessoa.senha.isEmpty() || !pessoa.isSenhaValida()){
                binding.senha.setError("Preencha com uma senha de pelo menos 6 digitos")
            } else if (email.isEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.email.setError("Preencha com um email válido")
            } else if (senhaConf.isEmpty() || senhaConf != senha) {
                binding.senhaConfirmacao.setError("Preencha a confimação igual a senha")
            } else {
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "signInWithCustomToken:success")
                            pessoa.authID =  task.result.user?.uid.toString()

                            addPessoa(pessoa)

                            val iLogin = Intent(this, MainActivity::class.java)
                            startActivity(iLogin)
                        } else {
                            Toast.makeText(this, "Cadaastro falhou",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        })

        binding.arrow.setOnClickListener(View.OnClickListener {
            val iLogin = Intent(this, MainActivity::class.java)
            startActivity(iLogin)
        })

        binding.voltar.setOnClickListener(View.OnClickListener {
            val iVoltar = Intent(this, MainActivity::class.java)
            startActivity(iVoltar)
        })
    }

    fun addPessoa(pessoa: Pessoa){
        lateinit var firebase: FirebaseFirestore;
        firebase = FirebaseFirestore.getInstance()

        // Criando um objeto pessoa com os dados do usuario
        val hmPessoa = hashMapOf(
            "authID" to pessoa.authID,
            "cpf" to pessoa.cpf,
            "dataNascimento" to pessoa.dataNascimento,
            "nome" to pessoa.nomeCompleto,
            "telefone" to pessoa.telefone,
            "ehGerente" to pessoa.ehGerente,
        )

        // Adicionando a pessoa no firestore
        firebase.collection("pessoa")
            .add(hmPessoa)
            .addOnSuccessListener { documentReference ->
                Log.d("CadastroPessoa", "Pessoa adicionado com ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.w("Erro", "Erro ao adicionar pessoa", e)
            }
    }
}
package br.edu.puccampinas.safepack

import android.os.Bundle
import android.os.PersistableBundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityCadastroBinding
import br.edu.puccampinas.safepack.databinding.ActivityLoginBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

class CadastroActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etCPF: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etSenha: EditText
    private lateinit var etSenhaConf: EditText
    private lateinit var btCadastrar: Button

    //private val db = com.google.firebase.ktx.Firebase.firestore
    //private var functions = Firebase.functions
    private lateinit var binding: ActivityCadastroBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_cadastro)

        etName = findViewById(R.id.nome)
        etCPF = findViewById(R.id.cpf)
        etEmail = findViewById(R.id.email)
        etTelefone = findViewById(R.id.telefone)
        etSenha = findViewById(R.id.nome)
        etSenhaConf = findViewById(R.id.nome)
        btCadastrar = findViewById(R.id.cadastroButton)

        btCadastrar.setOnClickListener {
            val name = etName.text.toString().trim()
            val cpf = etCPF.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val telefone = etTelefone.text.toString().trim()
            etSenha = findViewById(R.id.nome)
            etSenhaConf = findViewById(R.id.nome)

            val pessoaMap = hashMapOf(
                "nome" to name,
                "cpf" to cpf,
                "email" to email,
                "telefone" to telefone
            )
/*
            db.collection("pessoa").document().set(pessoaMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Cadastro realizado com sucesso", Toast.LENGTH_SHORT).show()
                    etName.text.clear()
                    etCPF.text.clear()
                    etEmail.text.clear()
                    etTelefone.text.clear()
                    etSenha.text.clear()
                    etSenhaConf.text.clear()
                }
                .addOnFailureListener{
                    Toast.makeText(this, "Cadastro falhou", Toast.LENGTH_SHORT).show()
                }*/
        }
    }
}
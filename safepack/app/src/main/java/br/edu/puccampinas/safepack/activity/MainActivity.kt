package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityLoginBinding
import br.edu.puccampinas.safepack.databinding.ActivityMapsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth;
    var email: String ="";
    var senha: String = "";
    var authID: String? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance();

        binding.loginButton.setOnClickListener(View.OnClickListener {
            email = binding.email.text.toString().trim();
            senha = binding.senha.text.toString().trim();
            //if (email.isNotEmpty() && email.matches(Patterns.EMAIL_ADDRESS.toRegex())){
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                if (senha.isNotEmpty()){
                    auth.signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener{task ->
                            if (task.isSuccessful){
                                authID = task.result.user?.uid.toString()
                                binding.loginButton.setText(authID)
                                Toast.makeText(this, "Login sucesso",
                                    Toast.LENGTH_SHORT).show()
                                val iMaps = Intent(this, MapsActivity::class.java)
                                startActivity(iMaps)

                            } else {
                                Toast.makeText(this, "Login falhou",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                   /*if (authID != null){
                        //val idPessoa = getPessoaByAuthID(authID.toString())
                        //intent.putExtra("idPessoa", idPessoa);
                    }*/
                } else {
                    binding.senha.setError("Senha não pode ser vazia")
                }
            } else if (email.isEmpty()){
                binding.email.setError("Email não pode ser vazio")
            } else {
                binding.email.setError("Insira um email válido")
            }
        })

        binding.fazerCadastroText.setOnClickListener(View.OnClickListener {
            val iCreateAccount = Intent(this, CadastroActivity::class.java)
            startActivity(iCreateAccount)
        })

        binding.verMapaButton.setOnClickListener(View.OnClickListener {
            val iMaps = Intent(this, MapsNoLoginActivity::class.java)
            startActivity(iMaps)
        })

        binding.esqueceuSenhaText.setOnClickListener {
            val iAlterarSenha = Intent(this, AlterarSenhaActivity::class.java)
            startActivity(iAlterarSenha)
        }
    }

    fun getPessoaByAuthID(authID: String) : String{
        var idPessoa:String = "";
        val db = FirebaseFirestore.getInstance()
        db.collection("pessoa")
            .whereEqualTo("authID", authID)
            .get()
            .addOnSuccessListener { result: QuerySnapshot ->
                for (document: DocumentSnapshot in result) {
                    Log.d("Documento", "${document.id} => ${document.data}")
                    // Aqui você pode manipular cada documento encontrado
                    idPessoa = document.id
                }
                if (result.isEmpty) {
                    Log.d("Documento", "Nenhum documento encontrado com o authID: $authID")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("Erro", "Erro ao buscar documentos: ", exception)
            }
        return idPessoa
    }
}
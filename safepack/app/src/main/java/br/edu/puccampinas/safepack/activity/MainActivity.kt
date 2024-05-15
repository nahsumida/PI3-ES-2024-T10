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
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import br.edu.puccampinas.safepack.repositories.PessoaRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth;
    lateinit var email: String;
    lateinit var senha: String;
    lateinit var pessoaRepository: PessoaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflar layout da activity
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inicializar a instancia do FirebaseAuth e PessoaRepository
        auth = FirebaseAuth.getInstance();
        pessoaRepository = PessoaRepository()

        // verificar se o usuário está logado e se é o primeiro login
        val primeiroLogin = intent.getStringExtra("primeiroLogin")
        if(auth.currentUser != null && primeiroLogin == null) {
            isGerente(auth, pessoaRepository) { result ->
                Log.d("isGerente", result.toString())
                if (result) {
                    val iGerente = Intent(this, TelaInicialGerenteActivity::class.java)
                    startActivity(iGerente)
                } else {
                    val iMaps = Intent(this, MapsActivity::class.java)
                    startActivity(iMaps)
                }
            }
        }

        // configurar o clique do botão de login
        binding.loginButton.setOnClickListener(View.OnClickListener {

            // obter valores dos campos email e senha
            email = binding.email.text.toString().trim();
            senha = binding.senha.text.toString().trim();

            // verifica se o email é válido
            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                // verifica se a senha não está vazia
                if (senha.isNotEmpty()){
                    // realiza o login do usuário com email e senha
                    auth.signInWithEmailAndPassword(email, senha)
                        .addOnCompleteListener{task ->
                            if (task.isSuccessful){
                                // verifica se é gerente ou cliente
                                isGerente(auth, pessoaRepository) {result ->
                                    Log.d("isGerente", result.toString())
                                    if(result) {
                                        val iGerente = Intent(this, TelaInicialGerenteActivity::class.java)
                                        startActivity(iGerente)
                                    } else {
                                        // verifica se o email foi verificado
                                        if(auth.currentUser?.isEmailVerified == true) {
                                            val iMaps = Intent(this, MapsActivity::class.java)
                                            startActivity(iMaps)
                                        } else {
                                            auth.signOut()
                                            Toast.makeText(this, "Verifique o email cadastrado",
                                                Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(this, "Email ou senha incorretos",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                } else {
                    binding.senha.setError("Senha não pode ser vazia")
                }
            } else if (email.isEmpty()){
                binding.email.setError("Email não pode ser vazio")
            } else {
                binding.email.setError("Insira um email válido")
            }
        })

        // configurar clique do texto de cadastro
        binding.fazerCadastroText.setOnClickListener(View.OnClickListener {
            val iCreateAccount = Intent(this, CadastroActivity::class.java)
            startActivity(iCreateAccount)
        })

        // configura o clique do botão de ver o mapa
        binding.verMapaButton.setOnClickListener(View.OnClickListener {
            val iMaps = Intent(this, MapsNoLoginActivity::class.java)
            startActivity(iMaps)
        })

        // configura o clique no texto "Esqueceu a senha?"
        binding.esqueceuSenhaText.setOnClickListener {
            val iRecuperarSenha = Intent(this, RecuperarSenhaActivity::class.java)
            startActivity(iRecuperarSenha)
        }
    }

    private fun isGerente(auth: FirebaseAuth,
                  pessoaR: PessoaRepository,
                  callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser

        if(currentUser != null) {
            pessoaR.getIdByAuthId(currentUser.uid) {idUser ->
                if(idUser != "null") {
                    pessoaR.getPessoaById(idUser).addOnSuccessListener {pessoa ->
                        if(pessoa.getBoolean("ehGerente") == true) {
                            callback(true)
                        } else {
                            callback(false)
                        }
                    }
                }
            }
        }
    }
}
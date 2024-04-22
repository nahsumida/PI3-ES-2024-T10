package br.edu.puccampinas.safepack.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityLoginBinding
import br.edu.puccampinas.safepack.databinding.ActivityMapsBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private lateinit var auth: FirebaseAuth;
    var email:String ="";
    var senha:String = "";

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
                        .addOnCompleteListener{
                            if (it.isSuccessful){
                                // uid do usuario logado = it.result.user?.uid.toString()
                                val iMaps = Intent(this, MapsActivity::class.java)
                                startActivity(iMaps)
                            } else {
                                binding.esqueceuSenhaText.setText("nao foi em")
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
}
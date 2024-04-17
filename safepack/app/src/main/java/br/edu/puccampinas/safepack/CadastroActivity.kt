package br.edu.puccampinas.safepack

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityCadastroBinding
import br.edu.puccampinas.safepack.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding

    private lateinit var auth: FirebaseAuth;
    var email:String ="";
    var senha:String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_cadastro);

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance();


        binding.cadastroButton.setOnClickListener(View.OnClickListener {
            email = binding.email.text.toString().trim();
            senha = binding.senha.text.toString().trim();

            if (email.isEmpty()){
                binding.email.setError("Email não pode estar vazio")
            }
            if (senha.isEmpty()){
                binding.senha.setError("Senha não pode estar vazia")
            } else {
                auth.createUserWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ContentValues.TAG, "signInWithCustomToken:success")
                            val iLogin = Intent(this, MainActivity::class.java)
                            startActivity(iLogin)
                        }
                    }
            }
        })

        binding.arrow.setOnClickListener(View.OnClickListener {
            val iLogin = Intent(this, MainActivity::class.java)
            startActivity(iLogin)
        })

    }
}
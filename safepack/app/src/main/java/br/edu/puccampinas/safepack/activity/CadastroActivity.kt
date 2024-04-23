package br.edu.puccampinas.safepack.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityCadastroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import java.net.URL

class CadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroBinding

    private lateinit var auth: FirebaseAuth;
    private lateinit var functions: FirebaseFunctions;

    var email:String ="";
    var senha:String = "";
    var uidAuth:String = "";

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance();
        functions = FirebaseFunctions.getInstance();

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
                            //var uidAuth =  task.result.user?.uid.toString()
                            val iLogin = Intent(this, MainActivity::class.java)
                            startActivity(iLogin)
                        } else {
                            binding.cadastroButton.setText("deu ruim")
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

    fun addSamplePerson(){
        // Crie os dados para enviar para sua função. Isso pode ser um Map ou um objeto personalizado.
        val data = hashMapOf(
            "key" to "value"
        )


        // Chame sua função do Firebase pelo nome. Substitua "yourFunctionName" pelo nome da sua função.
        functions
            .getHttpsCallableFromUrl(URL("https://southamerica-east1-pi3-es-2024-t10.cloudfunctions.net/addSamplePerson"))
            .call()
            .addOnSuccessListener { result ->
                // Trate o sucesso da chamada aqui. `result.data` contém a resposta da sua função.
                println("Function result: ${result.data}")
            }
            .addOnFailureListener { e ->
                // Trate o erro aqui.
                e.printStackTrace()
            }
    }
}
package br.edu.puccampinas.safepack.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding
import br.edu.puccampinas.safepack.databinding.ActivityCadastroBinding
import br.edu.puccampinas.safepack.databinding.ActivityRecuperarSenhaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import java.net.URL

class RecuperarSenhaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperarSenhaBinding
    private lateinit var auth: FirebaseAuth;

    lateinit var email: String;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance();

        val binding = ActivityRecuperarSenhaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.enviarLinkButton.setOnClickListener {
            email = binding.emailCadastrado.text.toString().trim();

            if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailCadastrado.setError("Preencha com um email válido")
            } else {
                auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    //se o envio for um sucesso
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "sendPasswordResetEmail:success")
                        Toast.makeText(
                            baseContext,
                            "Email de recuperação enviado, verifique seu email",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.w(ContentValues.TAG, "sendPasswordResetEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Falha ao enviar email de recuperação",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
            val iLoginActivity = Intent(this, MainActivity::class.java)
            startActivity(iLoginActivity)
        }

        binding.cancelarText.setOnClickListener{
            val iLoginActivity = Intent(this, MainActivity::class.java)
            startActivity(iLoginActivity)
        }

        binding.arrow.setOnClickListener{
            val iLoginActivity = Intent(this, MainActivity::class.java)
            startActivity(iLoginActivity)
        }

    }
}
package br.edu.puccampinas.safepack

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var loginButton: Button
    lateinit var btCadastro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_cadastro)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fazerCadastroText.setOnClickListener{
             val iCreateAccount = Intent(this, CadastroActivity::class.java)
             startActivity(iCreateAccount)
        }
    }
}
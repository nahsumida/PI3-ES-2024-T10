package br.edu.puccampinas.safepack

import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityLoginBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var loginButton: Button
    lateinit var cadastroButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener(View.OnClickListener {})
    }
}
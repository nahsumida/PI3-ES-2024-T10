package br.edu.puccampinas.safepack.activity

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding
import br.edu.puccampinas.safepack.databinding.ActivityCadastroBinding
import br.edu.puccampinas.safepack.databinding.ActivityRecuperarSenhaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions
import java.net.URL

class RecuperarSenhaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecuperarSenhaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityAlugarArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrow.setOnClickListener {
            val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
            startActivity(iInfoArmario)
        }

    }
}
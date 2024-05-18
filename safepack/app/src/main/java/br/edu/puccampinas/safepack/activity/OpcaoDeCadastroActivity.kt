package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityOpcaoDeCadastroBinding

class OpcaoDeCadastroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOpcaoDeCadastroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOpcaoDeCadastroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.arrow.setOnClickListener {
            val iQrCodeScan = Intent(this, QrCodeLeituraActivity::class.java)
            startActivity(iQrCodeScan)
        }
    }
}
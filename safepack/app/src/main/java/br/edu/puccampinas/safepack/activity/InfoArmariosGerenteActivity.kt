package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityInfoArmariosGerenteBinding

class InfoArmariosGerenteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInfoArmariosGerenteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoArmariosGerenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.okButton.setOnClickListener {
            val iTelaInicial = Intent(this, TelaInicialGerenteActivity::class.java)
            startActivity(iTelaInicial)
        }
    }
}
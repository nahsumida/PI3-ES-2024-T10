package br.edu.puccampinas.safepack.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityTelaInicialGerenteBinding

class TelaInicialGerenteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTelaInicialGerenteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTelaInicialGerenteBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
package br.edu.puccampinas.safepack

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.databinding.ActivityInfoArmarioBinding

class InfoArmarioActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityInfoArmarioBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityAbrirArmarioNfcBinding
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding

class AbrirArmarioNfcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAbrirArmarioNfcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAbrirArmarioNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // valores de teste, esses valores devem estar presentes na nfc
        val idLocacao = "zwu1ZJi5beUHmcwQCEj9"
        val numeroCliente = 1
        val qtdClientes = 1

        binding.button.setOnClickListener {
            val iVerificarClientes = Intent(this, VerificarClientesActivity::class.java)
            iVerificarClientes.putExtra("idLocacao", idLocacao)
            iVerificarClientes.putExtra("numeroCliente", numeroCliente)
            iVerificarClientes.putExtra("qtdClientes", qtdClientes)
            startActivity(iVerificarClientes)
        }
    }
}
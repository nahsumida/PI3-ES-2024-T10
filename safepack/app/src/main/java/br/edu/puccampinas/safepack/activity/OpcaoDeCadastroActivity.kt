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

        val idLocacao = intent.getStringExtra("idLocacao")

        binding.arrow.setOnClickListener {
            val iQrCodeScan = Intent(this, QrCodeLeituraActivity::class.java)
            startActivity(iQrCodeScan)
        }

        binding.umaPessoaButton.setOnClickListener {
            val iFotoCliente = Intent(this, FotoClienteActivity::class.java)
            iFotoCliente.putExtra("idLocacao", idLocacao)
            iFotoCliente.putExtra("qtdClientes", "1")
            iFotoCliente.putExtra("clienteAtual", "1")
            startActivity(iFotoCliente)
        }

        binding.duasPessoasButton.setOnClickListener {
            val iFotoCliente = Intent(this, FotoClienteActivity::class.java)
            iFotoCliente.putExtra("idLocacao", idLocacao)
            iFotoCliente.putExtra("qtdClientes", "2")
            iFotoCliente.putExtra("clienteAtual", "1")
            startActivity(iFotoCliente)
        }
    }
}
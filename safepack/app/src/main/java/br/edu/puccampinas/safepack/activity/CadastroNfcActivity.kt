package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityCadastroNfcBinding

class CadastroNfcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroNfcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val qtdClientes = intent.getStringExtra("qtdClientes")
        val clienteAtual = intent.getStringExtra("clienteAtual")
        val idLocacao = intent.getStringExtra("idLocacao")
        val fotoCliente1 = intent.getStringExtra("fotoCliente1")
        val fotoCliente2 = intent.getStringExtra("fotoCliente2")
        Log.d("CadastroNfcActivity", "qtdClientes: $qtdClientes")
        Log.d("CadastroNfcActivity", "clienteAtual: $clienteAtual")
        Log.d("CadastroNfcActivity", "idLocacao: $idLocacao")
        Log.d("CadastroNfcActivity", "fotoCliente1: $fotoCliente1")
        Log.d("CadastroNfcActivity", "fotoCliente2: $fotoCliente2")


        binding.button.setOnClickListener {
            if (qtdClientes.equals("2") && clienteAtual.equals("1")) {
                val iFotoCliente = Intent(this, FotoClienteActivity::class.java)
                iFotoCliente.putExtra("qtdClientes", "2")
                iFotoCliente.putExtra("clienteAtual", "2")
                iFotoCliente.putExtra("idLocacao", idLocacao)
                iFotoCliente.putExtra("fotoCliente1", fotoCliente1)
                startActivity(iFotoCliente)
            } else if(qtdClientes.equals("2") && clienteAtual.equals("2")) {
                val iFinalizacao = Intent(this, FinalizacaoDeAluguelActivity::class.java)
                iFinalizacao.putExtra("qtdClientes", qtdClientes)
                iFinalizacao.putExtra("clienteAtual", "2")
                iFinalizacao.putExtra("idLocacao", idLocacao)
                iFinalizacao.putExtra("fotoCliente1", fotoCliente1)
                iFinalizacao.putExtra("fotoCliente2", fotoCliente2)
                startActivity(iFinalizacao)
            } else if(qtdClientes.equals("1")) {
                val iFinalizacao = Intent(this, FinalizacaoDeAluguelActivity::class.java)
                iFinalizacao.putExtra("qtdClientes", qtdClientes)
                iFinalizacao.putExtra("clienteAtual", clienteAtual)
                iFinalizacao.putExtra("idLocacao", idLocacao)
                iFinalizacao.putExtra("fotoCliente1", fotoCliente1)
                startActivity(iFinalizacao)
            } else {
                Log.e("CadastroNfcActivity", "Erro nas intens")
            }
        }
    }
}
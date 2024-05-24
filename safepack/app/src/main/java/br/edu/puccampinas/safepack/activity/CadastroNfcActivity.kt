package br.edu.puccampinas.safepack.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityCadastroNfcBinding
import java.io.IOException

class CadastroNfcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCadastroNfcBinding
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intentFiltersArray: Array<IntentFilter>

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Este dispositivo não suporta NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val idLocacao = intent.getStringExtra("idLocacao")
        val clienteAtual = intent.getStringExtra("clienteAtual")
        val qtdClientes = intent.getStringExtra("qtdClientes")

        Log.d("idLocacao", idLocacao ?: "idLocacao is null")

        // Create a PendingIntent to handle NFC intents
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP).apply {
                putExtra("idLocacao", idLocacao)
                putExtra("clienteAtual", clienteAtual)
                putExtra("qtdClientes", qtdClientes)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // Create an IntentFilter for NDEF discovery
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        intentFiltersArray = arrayOf(tagDetected)

        intent?.let {
            handleIntent(it)
        }
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun handleIntent(intent: Intent) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            val idLocacao = intent.getStringExtra("idLocacao")
            val clienteAtual = intent.getStringExtra("clienteAtual")
            val qtdClientes = intent.getStringExtra("qtdClientes")

            Log.d("idLocacao", idLocacao ?: "idLocacao is null")

            tag?.let {
                val message = "$clienteAtual $qtdClientes $idLocacao"
                writeNfcTag(it, message)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun writeNfcTag(tag: Tag, text: String) {
        val ndefMessage = createNdefMessage(text)
        val ndef = Ndef.get(tag)

        if (ndef != null) {
            try {
                ndef.connect()
                if (ndef.isWritable) {
                    ndef.writeNdefMessage(ndefMessage)
                    Toast.makeText(this, "Tag escrita com sucesso", Toast.LENGTH_SHORT).show()
                    handleButtonClick()
                } else {
                    Toast.makeText(this, "Tag não é gravável", Toast.LENGTH_SHORT).show()
                }
            } catch (e: IOException) {
                Log.e("CadastroNfcActivity", "Erro ao escrever na tag", e)
                Toast.makeText(this, "Erro ao escrever na tag: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                try {
                    ndef.close()
                } catch (e: IOException) {
                    Log.e("CadastroNfcActivity", "Erro ao fechar conexão com a tag", e)
                }
            }
        } else {
            Toast.makeText(this, "Tag não suportada", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createNdefMessage(text: String): NdefMessage {
        val ndefRecord = NdefRecord.createTextRecord("en", text)
        return NdefMessage(arrayOf(ndefRecord))
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun handleButtonClick() {
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

        when {
            qtdClientes == "2" && clienteAtual == "1" -> {
                val iFotoCliente = Intent(this, FotoClienteActivity::class.java).apply {
                    putExtra("qtdClientes", "2")
                    putExtra("clienteAtual", "2")
                    putExtra("idLocacao", idLocacao)
                    putExtra("fotoCliente1", fotoCliente1)
                }
                startActivity(iFotoCliente)
            }
            qtdClientes == "2" && clienteAtual == "2" -> {
                val iFinalizacao = Intent(this, FinalizacaoDeAluguelActivity::class.java).apply {
                    putExtra("qtdClientes", qtdClientes)
                    putExtra("clienteAtual", "2")
                    putExtra("idLocacao", idLocacao)
                    putExtra("fotoCliente1", fotoCliente1)
                    putExtra("fotoCliente2", fotoCliente2)
                }
                startActivity(iFinalizacao)
            }
            qtdClientes == "1" -> {
                val iFinalizacao = Intent(this, FinalizacaoDeAluguelActivity::class.java).apply {
                    putExtra("qtdClientes", qtdClientes)
                    putExtra("clienteAtual", clienteAtual)
                    putExtra("idLocacao", idLocacao)
                    putExtra("fotoCliente1", fotoCliente1)
                }
                startActivity(iFinalizacao)
            }
            else -> {
                Log.e("CadastroNfcActivity", "Erro nas intents")
            }
        }
    }
}
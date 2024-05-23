package br.edu.puccampinas.safepack.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NdefRecord.createMime
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityCadastroNfcBinding
import java.io.IOException


// valores de teste, esses valores devem estar presentes na nfc
val idLocacao = "zwu1ZJi5beUHmcwQCEj9"
val numeroCliente = 1
val qtdClientes = 1


class CadastroNfcActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCadastroNfcBinding
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var writeTagFilters: Array<IntentFilter>

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCadastroNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Este dispositivo não suporta NFC", Toast.LENGTH_SHORT).show()
        }

        // Create a PendingIntent to handle NFC intents
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0
        )

        // Create an IntentFilter for NDEF discovery
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        writeTagFilters = arrayOf(tagDetected)

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
    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, writeTagFilters, null)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (NfcAdapter.ACTION_TAG_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            tag?.let {
                val qtdClientes = intent.getStringExtra("qtdClientes")
                val clienteAtual = intent.getStringExtra("clienteAtual")
                val idLocacao = intent.getStringExtra("idLocacao")
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
            ndef.connect()
            if (ndef.isWritable) {
                ndef.writeNdefMessage(ndefMessage)
                Toast.makeText(this, "Tag escrita com sucesso", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Tag não é gravável", Toast.LENGTH_SHORT).show()
            }
            ndef.close()
        } else {
            Toast.makeText(this, "Tag não suportada", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun createNdefMessage(text: String): NdefMessage {
        val ndefRecord = NdefRecord.createTextRecord("en", text)
        return NdefMessage(arrayOf(ndefRecord))
    }
}
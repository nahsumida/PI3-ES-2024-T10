package br.edu.puccampinas.safepack.activity

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityAbrirArmarioNfcBinding
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset

class AbrirArmarioNfcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAbrirArmarioNfcBinding
    private lateinit var nfcAdapter: NfcAdapter
    private lateinit var pendingIntent: PendingIntent
    private lateinit var intentFiltersArray: Array<IntentFilter>
    private lateinit var techListsArray: Array<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAbrirArmarioNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            Log.e("AbrirArmarioNfcActivity", "getDefaultAdapter retornou null")
            Toast.makeText(this, "Este dispositivo não suporta NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Configurar o PendingIntent para foreground dispatch
        val intent = Intent(this, javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        val ndef = IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED).apply {
            try {
                addDataType("*/*")
            } catch (e: IntentFilter.MalformedMimeTypeException) {
                throw RuntimeException("Falha ao adicionar tipo MIME.", e)
            }
        }

        intentFiltersArray = arrayOf(ndef)
        techListsArray = arrayOf(arrayOf(Ndef::class.java.name))
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val action = intent.action
        if (NfcAdapter.ACTION_TAG_DISCOVERED == action || NfcAdapter.ACTION_NDEF_DISCOVERED == action || NfcAdapter.ACTION_TECH_DISCOVERED == action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            tag?.let {
                val ndefMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
                if (ndefMessages != null) {
                    try {
                        readNfcTag(ndefMessages)
                    } catch (e: UnsupportedEncodingException) {
                        Toast.makeText(this, "Erro de decodificação", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Throws(UnsupportedEncodingException::class)
    private fun readNfcTag(ndefMessages: Array<Parcelable>) {
        if (ndefMessages.isNotEmpty()) {
            val ndefMessage = ndefMessages[0] as NdefMessage
            val records = ndefMessage.records
            for (ndefRecord in records) {
                if (ndefRecord.tnf == NdefRecord.TNF_WELL_KNOWN && ndefRecord.type.contentEquals(NdefRecord.RTD_TEXT)) {
                    val payload = ndefRecord.payload
                    val textEncoding = if ((payload[0].toInt() and 128) == 0) Charset.forName("UTF-8") else Charset.forName("UTF-16")
                    val languageCodeLength = payload[0].toInt() and 51
                    val text = String(payload, languageCodeLength + 1, payload.size - languageCodeLength - 1, textEncoding)

                    callVerificarClientesActivity(text)
                }
            }
        }
    }

    private fun callVerificarClientesActivity(nfcText:String) {
        val parts = nfcText.split(" ")

        val numeroCliente = parts[0].toInt()
        val qtdClientes = parts[1].toInt()
        val idLocacao = parts[2]

        Log.d("numeroCliente", "${numeroCliente}")
        Log.d("qtdClientes", "${qtdClientes}")
        Log.d("idLocacao", "${idLocacao}")

        val iVerificarClientes = Intent(this, VerificarClientesActivity::class.java)
        iVerificarClientes.putExtra("idLocacao", idLocacao)
        iVerificarClientes.putExtra("numeroCliente", numeroCliente)
        iVerificarClientes.putExtra("qtdClientes", qtdClientes)
        startActivity(iVerificarClientes)
    }
}
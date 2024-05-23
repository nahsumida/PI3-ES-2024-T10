package br.edu.puccampinas.safepack.activity

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.FormatException
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityAbrirArmarioNfcBinding
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding
import br.edu.puccampinas.safepack.databinding.ActivityLimparPulseiraNfcBinding
import java.io.IOException

class LimparPulseiraNfcActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLimparPulseiraNfcBinding
    private var nfcAdapter: NfcAdapter? = null
    private lateinit var pendingIntent: PendingIntent
    private lateinit var writeTagFilters: Array<IntentFilter>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLimparPulseiraNfcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        if (nfcAdapter == null) {
            Toast.makeText(this, "Este dispositivo não suporta NFC", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Create a PendingIntent to handle NFC intents
        pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // Create an IntentFilter for NDEF discovery
        val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED).apply {
            addCategory(Intent.CATEGORY_DEFAULT)
        }

        writeTagFilters = arrayOf(tagDetected)
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
                clearNfcTag(it)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun clearNfcTag(tag: Tag) {
        val ndef = Ndef.get(tag)

        if (ndef != null) {
            ndef.connect()
            if (ndef.isWritable) {
                try {
                    // Write an empty NdefMessage to clear the tag
                    val emptyMessage = NdefMessage(arrayOf(NdefRecord.createTextRecord("en", "")))
                    ndef.writeNdefMessage(emptyMessage)
                    Toast.makeText(this, "Tag limpa com sucesso", Toast.LENGTH_SHORT).show()
                    val iLocacaoEncerrada = Intent(this, LocacaoEncerradaActivity::class.java)
                    startActivity(iLocacaoEncerrada)
                } catch (e: Exception) {
                    Toast.makeText(this, "Erro ao limpar a tag: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Tag não é gravável", Toast.LENGTH_SHORT).show()
            }
            ndef.close()
        } else {
            Toast.makeText(this, "Tag não suportada", Toast.LENGTH_SHORT).show()
        }
    }
}
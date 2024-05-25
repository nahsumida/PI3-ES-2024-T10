package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityQrCodeBinding
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

class QrCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrCodeBinding
    private lateinit var unidadeR: UnidadeLocacaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unidadeR = UnidadeLocacaoRepository()

        val idUnidade = intent.getStringExtra("idUnidade")
        val idLocacao = intent.getStringExtra("idQRCode")

        if(idUnidade != null) adicionarGerente(unidadeR, binding, idUnidade)

        val bitmap: Bitmap? = gerarQRCode(idLocacao, 800, 800)

        bitmap?.let {
            binding.imageQRCode.setImageBitmap(it)
        }

        binding.avancarButton.setOnClickListener {
            val iArmarioLiberado = Intent(this, ArmarioLiberadoActivity::class.java)
            startActivity(iArmarioLiberado)
        }

    }

    private fun gerarQRCode(texto: String?, largura: Int, altura: Int): Bitmap? {
        val qrCodeWriter = QRCodeWriter()

        try {
            val bitMatrix: BitMatrix = qrCodeWriter.encode(texto,
                BarcodeFormat.QR_CODE,
                largura,
                altura)
            val bitmap = Bitmap.createBitmap(largura, altura, Bitmap.Config.ARGB_8888)

            for(x in 0 until largura) {
                for (y in 0 until altura) {
                    bitmap.setPixel(x, y, if(bitMatrix[x, y]) 0xFF000000.toInt() else 0xFFFFFFFF.toInt())
                }
            }
            return bitmap
        } catch (e: WriterException) {
            e.printStackTrace()
        }

        return null
    }

    private fun adicionarGerente(unidadeR: UnidadeLocacaoRepository,
                                 binding: ActivityQrCodeBinding,
                                 idUnidade: String) {
        unidadeR.getUnidadeById(idUnidade)
            .addOnSuccessListener { unidade ->
                if (unidade.exists()) {
                    val nomeGerente = unidade.getString("gerente")
                    Log.d("QrCodeActivity", "Nome do gerente: $nomeGerente")
                    if(nomeGerente != null) {
                        val textoGerente = "${binding.tvGerente.text}${unidade.getString("gerente")}"
                        binding.tvGerente.text = textoGerente
                    }
                } else {
                    Log.d("QrCodeActivity", "A unidade não existe")
                }
            }
            .addOnFailureListener { e ->
                Log.e("QrCodeActivity", "Erro: $e")
            }
    }
}
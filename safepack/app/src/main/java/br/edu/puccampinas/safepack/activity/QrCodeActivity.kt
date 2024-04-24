package br.edu.puccampinas.safepack.activity

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityQrCodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

class QrCodeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*
        val idLocacao = intent.getStringExtra("idLocacao")
        val largura = 800
        val altura = 800

        val bitmap: Bitmap? = gerarQRCode(idLocacao, largura, altura)

        bitmap?.let {
            binding.imageQRCode.setImageBitmap(it)
        } */

    }

    /*private fun gerarQRCode(texto: String?, largura: Int, altura: Int): Bitmap? {
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
    }*/
}
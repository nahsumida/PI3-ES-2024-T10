package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import br.edu.puccampinas.safepack.databinding.ActivityQrCodeLeituraBinding
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class QrCodeLeituraActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQrCodeLeituraBinding
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var locacaoRepository: LocacaoRepository
    private lateinit var qrCodeAnalyzer: QrCodeAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQrCodeLeituraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        locacaoRepository = LocacaoRepository()

        qrCodeAnalyzer = QrCodeAnalyzer { qrCode ->
            runOnUiThread {
                checkQRCodeInFirestore(qrCode)
            }
        }

        startCamera()

        binding.voltarButton.setOnClickListener {
            val iTelaGerente = Intent(this, TelaInicialGerenteActivity::class.java)
            startActivity(iTelaGerente)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, qrCodeAnalyzer)
                }

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageAnalyzer
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Erro ao abrir câmera", e)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun checkQRCodeInFirestore(qrCode: String) {
        locacaoRepository.getLocacaoById(qrCode)
            .addOnSuccessListener { locacao ->
                qrCodeAnalyzer.resetProcessingFlag()
                if(locacao.exists()) {
                    val iOpcaoCadastro = Intent(this, OpcaoDeCadastroActivity::class.java)
                    iOpcaoCadastro.putExtra("QrCode", qrCode)
                    startActivity(iOpcaoCadastro)
                } else {
                    Toast.makeText(
                        this,
                        "O QR Code informado não é válido",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("QRCode", "Erro ao verificar QRCode", exception)
            }
    }
}

private class QrCodeAnalyzer(private val onQRCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader().apply {
        setHints(mapOf(DecodeHintType.POSSIBLE_FORMATS to arrayListOf(BarcodeFormat.QR_CODE)))
    }
    private var isProcessing = false
    private var lastScannedTime = 0L

    @OptIn(ExperimentalGetImage::class) override fun analyze(image: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if(isProcessing || (currentTime - lastScannedTime < 2000)) {
            image.close()
            return
        }

        val mediaImage = image.image
        if(mediaImage != null) {
            val rotationDegrees = image.imageInfo.rotationDegrees
            val source = mediaImage.toLuminanceSource(rotationDegrees)
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

            try {
                val result = reader.decodeWithState(binaryBitmap)
                lastScannedTime = currentTime
                isProcessing = true
                onQRCodeScanned(result.text)
            } catch(e: Exception) {
                Log.e("QRCode", "QrCode não encontrado", e)
            } finally {
                image.close()
            }
        }
    }

    private fun Image.toLuminanceSource(rotationDegrees: Int): LuminanceSource {
        val yPlane = planes[0]
        val yBuffer = yPlane.buffer
        val ySize = yBuffer.remaining()
        val yData = ByteArray(ySize)
        yBuffer.get(yData, 0, ySize)

        return PlanarYUVLuminanceSource(yData, width, height, 0, 0, width, height, false)
    }

    fun resetProcessingFlag() {
        isProcessing = false
    }
}
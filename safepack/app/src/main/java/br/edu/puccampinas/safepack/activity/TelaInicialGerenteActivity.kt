package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityTelaInicialGerenteBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class TelaInicialGerenteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTelaInicialGerenteBinding
    private lateinit var auth: FirebaseAuth
    private val cameraProviderResult =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) {
                val iQrCodeScan = Intent(this, QrCodeLeituraActivity::class.java)
                startActivity(iQrCodeScan)
            } else {
                Snackbar.make(binding.root,
                    "Você não concedeu permissão para usar a câmera",
                    Snackbar.LENGTH_INDEFINITE).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTelaInicialGerenteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // instanciar o auth
        auth = FirebaseAuth.getInstance()

        // listener de clique do botão de sair
        binding.sairButton.setOnClickListener {
            sair()
        }

        // listener de clique no botão de abrir armário
        binding.abrirArmarioButton.setOnClickListener {
            cameraProviderResult.launch(android.Manifest.permission.CAMERA)
        }

        // listener de clique no botão de acessar armário
        binding.acessarArmarioButton.setOnClickListener {

        }
    }

    private fun sair() {
        val builder = AlertDialog.Builder(this@TelaInicialGerenteActivity)

        builder.setTitle("Safepack")
        builder.setMessage("Tem certeza que deseja sair?")

        builder.setPositiveButton("Sim") { dialog, which ->
            auth.signOut()
            val iLogin = Intent(this, MainActivity::class.java)
            startActivity(iLogin)
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}
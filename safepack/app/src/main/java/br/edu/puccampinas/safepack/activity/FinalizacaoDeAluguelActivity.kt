package br.edu.puccampinas.safepack.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding
import br.edu.puccampinas.safepack.databinding.ActivityFinalizacaoDeAluguelBinding
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class FinalizacaoDeAluguelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinalizacaoDeAluguelBinding
    private lateinit var locacaoRepository: LocacaoRepository
    private lateinit var storage: FirebaseStorage

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFinalizacaoDeAluguelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locacaoRepository = LocacaoRepository()
        storage = FirebaseStorage.getInstance()

        val qtdClientes = intent.getStringExtra("qtdClientes")
        val clienteAtual = intent.getStringExtra("clienteAtual")
        val idLocacao = intent.getStringExtra("idLocacao")
        val fotoCliente1 = intent.getStringExtra("fotoCliente1")
        val fotoCliente2 = intent.getStringExtra("fotoCliente2")
        Log.d("FinalizacaoDeAluguelActivity", "qtdClientes: $qtdClientes")
        Log.d("FinalizacaoDeAluguelActivity", "clienteAtual: $clienteAtual")
        Log.d("FinalizacaoDeAluguelActivity", "idLocacao: $idLocacao")
        Log.d("FinalizacaoDeAluguelActivity", "fotoCliente1: $fotoCliente1")
        Log.d("FinalizacaoDeAluguelActivity", "fotoCliente2: $fotoCliente2")

        if(qtdClientes.equals("2")) {
            binding.lLduasPessoas.visibility = View.VISIBLE
        } else if(qtdClientes.equals("1")) {
            binding.lLumaPessoa.visibility = View.VISIBLE
        }

        binding.confirmarLocacaoButton.setOnClickListener {
            if(qtdClientes.equals("2")) {
                addInfo(qtdClientes,
                    idLocacao,
                    fotoCliente1,
                    fotoCliente2,
                    "teste_id1",
                    "teste_id2")
            } else if(qtdClientes.equals("1")) {
                addInfo(qtdClientes,
                    idLocacao,
                    fotoCliente1,
                    null,
                    "teste_id1",
                    null)
            }

            if(idLocacao != null) locacaoRepository.setStatusLocacao(idLocacao, "ativa")

            val iInfoGerente = Intent(this, InfoArmariosGerenteActivity::class.java)
            iInfoGerente.putExtra("idLocacao", idLocacao)
            startActivity(iInfoGerente)
        }
    }

    private fun uploadImageToStorage(path: String, pos: Int, locacaoId: String) {
        val photoFile = File(path)

        val fileName = photoFile.name
        val storageRef = storage.reference.child("images/$fileName")

        storageRef.putFile(Uri.fromFile(photoFile))
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    locacaoRepository.setFotoPessoaLocacao(locacaoId, uri.toString(), pos)
                }
            }
    }

    private fun addInfo(qtdClientes: String?,
                        idLocacao: String?,
                        fotoCliente1: String?,
                        fotoCliente2: String?,
                        idNfcPessoa1: String?,
                        idNfcPessoa2: String?) {
        if(qtdClientes.equals("1") &&
            fotoCliente1 != null &&
            idLocacao != null &&
            idNfcPessoa1 != null) {

            uploadImageToStorage(fotoCliente1, 1, idLocacao)
            locacaoRepository.setIdNfcLocacao(idLocacao, idNfcPessoa1, 1)

        } else if(qtdClientes.equals("2") && idLocacao != null) {
            if(fotoCliente1 != null && idNfcPessoa1 != null) {
                uploadImageToStorage(fotoCliente1, 1, idLocacao)
                locacaoRepository.setIdNfcLocacao(idLocacao, idNfcPessoa1, 1)
            }
            if(fotoCliente2 != null && idNfcPessoa2 != null) {
                uploadImageToStorage(fotoCliente2, 2, idLocacao)
                locacaoRepository.setIdNfcLocacao(idLocacao, idNfcPessoa2, 2)
            }
        }
    }
}
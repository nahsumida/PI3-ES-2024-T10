package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityInfoArmarioBinding
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository

class InfoArmarioActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityInfoArmarioBinding
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInfoArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        unidadeLocacaoRepository = UnidadeLocacaoRepository()

        val idUnidade = intent.getStringExtra("idUnidade")

        if (idUnidade != null) apresentarInfoArmario(idUnidade, unidadeLocacaoRepository)

        val activityAnterior = intent.getStringExtra("activityAnterior")

        binding.alugarArmarioButton.setOnClickListener {
            val iAlugarArmario = Intent(this, AlugarArmarioActivity::class.java)
            iAlugarArmario.putExtra("idUnidade", idUnidade)
            startActivity(iAlugarArmario)
        }

        binding.arrow.setOnClickListener {
            if(activityAnterior.equals("Maps")) {
                val iMaps = Intent(this, MapsActivity::class.java)
                startActivity(iMaps)
            } else {
                val iMapsNoLogin = Intent(this, MapsNoLoginActivity::class.java)
                startActivity(iMapsNoLogin)
            }
        }

        binding.irAoLocalButton.setOnClickListener {
            val latitude = -22.8360456
            val longitude = -47.0564085
            val uri = "geo:$latitude,$longitude"

            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

            intent.`package` = "com.google.android.apps.maps"

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(
                    this, "Google maps não está instalado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun apresentarInfoArmario(id: String, unidadeR: UnidadeLocacaoRepository) {
        unidadeR.getUnidadeById(id)
            .addOnSuccessListener { unidade ->
                if(unidade != null && unidade.exists()) {
                    Log.d("Firestore", "Unidade: ${unidade.data}")
                    val endereco = unidade.getString("endereco")
                    val referencia = unidade.getString("referencia")
                    runOnUiThread {
                        binding.tvLocalizacao.text = endereco.toString()
                        binding.tvReferencia.text = referencia.toString()
                    }
                } else {
                    Log.d("Firestore", "Nenhum documento com esse ID encontrado")
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao recuperar documento: ", e)
            }
    }
}
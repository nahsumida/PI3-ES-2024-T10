package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.puccampinas.safepack.databinding.ActivityInfoArmarioBinding
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository
import com.google.android.gms.maps.model.LatLng

class InfoArmarioActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityInfoArmarioBinding
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // inflar layout activity
        binding = ActivityInfoArmarioBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inicializar instancia do UnidadeRepository
        unidadeLocacaoRepository = UnidadeLocacaoRepository()

        // obter dados passados pela intent
        val idUnidade = intent.getStringExtra("idUnidade")
        val latUser = intent.getStringExtra("latitude")?.toDouble()
        val longUser = intent.getStringExtra("longitude")?.toDouble()
        val statusLogin = intent.getStringExtra("statusLogin")
        val activityAnterior = intent.getStringExtra("activityAnterior")

        // verificar se os dados de localização do usuário passados e exibindo-os no log
        if(latUser != null && longUser != null) Log.d("Localização",
            "Sua loc: $latUser, $longUser")

        // apresentar as informações do armário
        if (idUnidade != null) apresentarInfoArmario(idUnidade, unidadeLocacaoRepository)

        // configurar clique do botão de alugar armário
        binding.alugarArmarioButton.setOnClickListener {
            if(latUser != null && longUser != null && idUnidade != null) {
                verificarProximidade(latUser, longUser, idUnidade, unidadeLocacaoRepository) {result ->
                    if(result) {
                        val iAlugarArmario = Intent(this, AlugarArmarioActivity::class.java)
                        iAlugarArmario.putExtra("idUnidade", idUnidade)
                        iAlugarArmario.putExtra("statusLogin", statusLogin)
                        startActivity(iAlugarArmario)
                    } else {
                        Toast.makeText(
                            this, "Aproxime-se do armário para realizar a locação",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        // configurar clique da seta para voltar para a activity anterior
        binding.arrow.setOnClickListener {
            if(activityAnterior.equals("Maps")) {
                val iMaps = Intent(this, MapsActivity::class.java)
                startActivity(iMaps)
            } else if(activityAnterior.equals("MapsNoLogin")){
                val iMapsNoLogin = Intent(this, MapsNoLoginActivity::class.java)
                startActivity(iMapsNoLogin)
            } else if(statusLogin.equals("1")) {
                val iMaps = Intent(this, MapsActivity::class.java)
                startActivity(iMaps)
            } else if(statusLogin.equals("0")) {
                val iMapsNoLogin = Intent(this, MapsNoLoginActivity::class.java)
                startActivity(iMapsNoLogin)
            }
        }

        // configurar clique do botão de ir ao local
        binding.irAoLocalButton.setOnClickListener {
            if(idUnidade != null) {
                unidadeLatLng(idUnidade, unidadeLocacaoRepository) {latLng ->
                    val latitude = latLng.latitude
                    val longitude = latLng.longitude
                    val uri = "https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude"

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))

                    intent.setPackage("com.google.android.apps.maps")

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
        }
    }

    // método para obter a latitude e longitude da unidade de locação
    private fun unidadeLatLng(idUnidade: String,
                              unidadeR: UnidadeLocacaoRepository,
                              callback: (LatLng) -> Unit) {
        unidadeR.getUnidadeById(idUnidade).addOnSuccessListener { unidade ->
            val geoPoint = unidade.getGeoPoint("geolocalizacao")
            if(geoPoint != null) {
                val latLngUnidade = LatLng(geoPoint.latitude, geoPoint.longitude)
                callback(latLngUnidade)
            }
        }
    }

    // método para apresentar as informações do armário na interface
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

    // método para verificar se o usuário está próximo o suficiente da unidade para alugar o armario
    private fun verificarProximidade(latUser: Double,
                                     longUser: Double,
                                     idUnidade: String,
                                     unidadeR: UnidadeLocacaoRepository,
                                     callback: (Boolean) -> Unit){
        unidadeR.getUnidadeById(idUnidade)
            .addOnSuccessListener { unidade ->
                val geoPoint = unidade.getGeoPoint("geolocalizacao")
                if (geoPoint != null) {
                    val latLngUnidade = LatLng(geoPoint.latitude, geoPoint.longitude)
                    val latLngUser = LatLng(latUser, longUser)
                    val distancia = calcularDistancia(latLngUnidade, latLngUser)
                    val distanciaMaxima = 50 // distancia máxima permitida em metros
                    val result = distancia <= distanciaMaxima
                    callback(result)
                } else {
                    callback(false)
                }
            }
    }

    // método para calcular a distância entre duas coodenadas
    private fun calcularDistancia(unidade: LatLng, user: LatLng): Double {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            unidade.latitude,
            unidade.longitude,
            user.latitude,
            user.longitude,
            results
        )
        return results[0].toDouble()
    }
}
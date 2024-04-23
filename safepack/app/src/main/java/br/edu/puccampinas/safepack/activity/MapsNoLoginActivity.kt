package br.edu.puccampinas.safepack.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import br.edu.puccampinas.safepack.R

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import br.edu.puccampinas.safepack.databinding.ActivityMapsNoLoginBinding
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.Marker

class MapsNoLoginActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsNoLoginBinding
    private lateinit var mapsLoginButton: Button
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsNoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsLoginButton = findViewById(R.id.mapsLoginButton)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        unidadeLocacaoRepository = UnidadeLocacaoRepository()

        mapsLoginButton.setOnClickListener {
            val iLogin = Intent(this, MainActivity::class.java)
            startActivity(iLogin)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener(this)

        addMarkers(mMap)
    }

    private fun addMarkers(googleMap: GoogleMap) {
        mMap = googleMap

        unidadeLocacaoRepository.getAllUnidades()
            .addOnSuccessListener { unidades ->
                for (unidade in unidades) {
                    val geoPoint = unidade.getGeoPoint("geoLocalizacao")
                    if(geoPoint != null) {
                        val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)
                        val marker = mMap.addMarker(
                            MarkerOptions()
                                .position(latLng)
                                .title("Informações do armário")
                        )
                        if (marker != null) {
                            marker.tag = unidade.id
                        }
                    }
                }
                //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places[0].latLng, 15F))
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao recuperar dados: ${exception.message}")
            }
    }

    override fun onInfoWindowClick(marker: Marker) {
        val idUnidade = marker.tag as? String
        val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
        iInfoArmario.putExtra("idUnidade", idUnidade)
        iInfoArmario.putExtra("activityAnterior", "MapsNoLogin")
        startActivity(iInfoArmario)
    }
}

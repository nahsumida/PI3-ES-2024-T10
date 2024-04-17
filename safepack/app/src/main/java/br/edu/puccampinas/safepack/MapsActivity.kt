package br.edu.puccampinas.safepack

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import br.edu.puccampinas.safepack.databinding.ActivityMapsBinding
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.Marker

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val places = arrayListOf(
        Place("PUCC Campus 1", LatLng(-22.8360456,-47.0564085),
            "Av. Reitor Benedito José Barreto Fonseca, H15 - Parque dos Jacarandás, Campinas - SP"),
        Place("Oxxo PUCC", LatLng(-22.8363415,-47.0531125),
            "Av. Profa. Ana Maria Silvestre Adade, 607 - Parque das Universidades, Campinas - SP, 13086-130")
    )
    private lateinit var mapsButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsButton = findViewById(R.id.mapsButton)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapsButton.setOnClickListener {
            val iCreditCard = Intent(this, CadastroCartaoActivity::class.java)
            startActivity(iCreditCard)
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener(this)

        addMarkers(mMap)
    }

    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title("Informações do armário")
                    .position(place.latLng)
            )
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(places[0].latLng, 15F))
    }

    override fun onInfoWindowClick(marker: Marker) {
        val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
        startActivity(iInfoArmario)
    }

}

data class Place (
    val name: String,
    val latLng: LatLng,
    val address: String,
)
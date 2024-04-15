package br.edu.puccampinas.safepack

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import br.edu.puccampinas.safepack.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var binding: ActivityMapsBinding? = null
    private val places = arrayListOf(
        Place("PUCC Campus 1", LatLng(-22.8360456,-47.0564085),
            "Av. Reitor Benedito José Barreto Fonseca, H15 - Parque dos Jacarandás, Campinas - SP"),
        Place("Oxxo PUCC", LatLng(-22.8363415,-47.0531125),
            "Av. Profa. Ana Maria Silvestre Adade, 607 - Parque das Universidades, Campinas - SP, 13086-130")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        //mapFragment.getMapAsync(this)
        mapFragment.getMapAsync { googleMap ->
            addMarkers(googleMap)
        }
    }

    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
                    .snippet(place.address)
            )
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(places[0].latLng))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val pucc = LatLng(-22.8360456,-47.0564085)
        mMap.addMarker(MarkerOptions().position(pucc).title("Marcador na PUCC"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pucc))
    }


}
data class Place (
    val name: String,
    val latLng: LatLng,
    val address: String,
)
package br.edu.puccampinas.safepack.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.edu.puccampinas.safepack.R

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import br.edu.puccampinas.safepack.databinding.ActivityMapsNoLoginBinding
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker

class MapsNoLoginActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsNoLoginBinding
    private lateinit var mapsLoginButton: Button
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsNoLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsLoginButton = findViewById(R.id.mapsLoginButton)

        unidadeLocacaoRepository = UnidadeLocacaoRepository()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapsLoginButton.setOnClickListener {
            val iLogin = Intent(this, MainActivity::class.java)
            startActivity(iLogin)
        }
    }

    override fun onStart() {
        super.onStart()
        if(ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            initMap()
        }
    }

    override fun onResume() {
        super.onResume()
        obterLocalizacao {  }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initMap()
            }
        } else {
            Log.e("Location", "Permissão de localização negada")
        }
    }

    private fun initMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.setOnInfoWindowClickListener(this)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val userLatLng = LatLng(location.latitude, location.longitude)
                        mMap.addMarker(
                            MarkerOptions()
                                .position(userLatLng)
                                .title("Sua Localização")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
                    }
                }
        }

        addMarkers(mMap)
    }

    private fun addMarkers(googleMap: GoogleMap) {
        mMap = googleMap

        unidadeLocacaoRepository.getAllUnidades()
            .addOnSuccessListener { unidades ->
                for (unidade in unidades) {
                    val geoPoint = unidade.getGeoPoint("geolocalizacao")
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
        Log.d("WINDOW_CLICK", "Id: $idUnidade")
        obterLocalizacao { latLngUser ->
            val iInfoArmario = Intent(this, InfoArmarioActivity::class.java)
            iInfoArmario.putExtra("idUnidade", idUnidade)
            iInfoArmario.putExtra("activityAnterior", "MapsNoLogin")
            iInfoArmario.putExtra("latitude", "${latLngUser.latitude}")
            Log.d("LOCALIZACAO", "Latitude: ${latLngUser.latitude}")
            iInfoArmario.putExtra("longitude", "${latLngUser.longitude}")
            Log.d("LOCALIZACAO", "Longitude: ${latLngUser.longitude}")
            iInfoArmario.putExtra("statusLogin", "0")
            startActivity(iInfoArmario)
        }
    }

    private fun obterLocalizacao(callback: (LatLng) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    callback(latLng) // Chama a função de callback com a localização
                }
            }
        }
    }
}

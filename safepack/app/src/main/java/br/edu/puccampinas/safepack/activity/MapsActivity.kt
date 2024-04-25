package br.edu.puccampinas.safepack.activity

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.util.TimeUnit
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import br.edu.puccampinas.safepack.R

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import br.edu.puccampinas.safepack.databinding.ActivityMapsBinding
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import br.edu.puccampinas.safepack.repositories.PessoaRepository
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, OnInfoWindowClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mapsButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var unidadeLocacaoRepository: UnidadeLocacaoRepository
    private lateinit var locacaoRepository: LocacaoRepository
    private lateinit var pessoaRepository: PessoaRepository
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mapsButton = findViewById(R.id.mapsButton)

        auth = FirebaseAuth.getInstance()

        unidadeLocacaoRepository = UnidadeLocacaoRepository()
        locacaoRepository = LocacaoRepository()
        pessoaRepository = PessoaRepository()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapsButton.setOnClickListener {
            val iCreditCard = Intent(this, CadastroCartaoActivity::class.java)
            startActivity(iCreditCard)
        }

        binding.sairButton.setOnClickListener {
            sair()
        }

        retirarBotaoCartao()
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

        val alertDialog = intent.getStringExtra("alertDialog")

        if(alertDialog == null) {
            verificarStatusLocacao(auth, locacaoRepository, pessoaRepository) {status ->
                if(status) {
                    createAlertDialog()
                }
            }
        }
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
            iInfoArmario.putExtra("activityAnterior", "Maps")
            iInfoArmario.putExtra("latitude", "${latLngUser.latitude}")
            Log.d("LOCALIZACAO", "Latitude: ${latLngUser.latitude}")
            iInfoArmario.putExtra("longitude", "${latLngUser.longitude}")
            Log.d("LOCALIZACAO", "Longitude: ${latLngUser.longitude}")
            iInfoArmario.putExtra("statusLogin", "1")
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

    private fun verificarStatusLocacao(auth: FirebaseAuth,
                                       locacaoR: LocacaoRepository,
                                       pessoaR: PessoaRepository,
                                       callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            pessoaR.getIdByAuthId(currentUser.uid) {idUser ->
                if(idUser != "null") {
                    locacaoR.getLocacaoIdByUserIdPendente(idUser) {idLocacao ->
                        if(idLocacao != "null") {
                            callback(true)
                        }
                        callback(false)
                    }
                }
            }
        }
    }

    private fun createAlertDialog() {
        val builder = AlertDialog.Builder(this@MapsActivity)

        builder.setTitle("Reserva não finalizada")
        builder.setMessage("A reserva não foi finalizada. Deseja finalizá-la?")

        builder.setPositiveButton("Sim") { dialog, which ->
            val currentUser = auth.currentUser
            if(currentUser != null) {
                pessoaRepository.getIdByAuthId(currentUser.uid) {idUser ->
                    if(idUser != "null") {
                        locacaoRepository.getLocacaoIdByUserIdPendente(idUser) {idLocacao ->
                            if(idLocacao != "null") {
                                locacaoRepository.getLocacaoById(idLocacao)
                                    .addOnSuccessListener{locacao ->
                                        val idUnidade = locacao.getString("unidadeId")
                                        val iQRCode = Intent(this, QrCodeActivity::class.java)
                                        iQRCode.putExtra("idQRCode", idUnidade)
                                        iQRCode.putExtra("alertDialog", "1")
                                        startActivity(iQRCode)
                                    }
                            }
                        }
                    }
                }
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, which ->
            alterarStatusLocacao(auth, locacaoRepository, pessoaRepository)
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun alterarStatusLocacao(auth: FirebaseAuth,
                                     locacaoR: LocacaoRepository,
                                     pessoaR: PessoaRepository) {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            pessoaR.getIdByAuthId(currentUser.uid) {idUser ->
                if(idUser != "null") {
                    locacaoR.getLocacaoIdByUserIdPendente(idUser) {idLocacao ->
                        if(idLocacao != "null") {
                            locacaoR.setStatusLocacao(idLocacao, "encerrada")
                        }
                    }
                }
            }
        }
    }

    private fun retirarBotaoCartao() {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        Log.d("AUTH", "${auth.currentUser?.uid}")
        if(currentUser != null) {
            Log.d("AUTH", "Usuário logado")
            verificarCartao(auth, pessoaRepository) {result ->
                Log.d("CARTAO", "VERIFICADO: $result")
                if(result) {
                    mapsButton.visibility = View.GONE
                }
            }
        }
    }

    private fun verificarCartao(auth: FirebaseAuth,
                                pessoaR: PessoaRepository,
                                callback: (Boolean) -> Unit) {

        val authId: String? = auth.currentUser?.uid
        var pessoaId = ""
        pessoaR.getAllPessoas()
            .addOnSuccessListener { pessoas ->
                var cartaoEncontrado = false
                for(pessoa in pessoas) {
                    if(pessoa.getString("authID").equals(authId)) {
                        pessoaId += pessoa.id
                        pessoaR.getCartaoPessoa(pessoaId)
                            .addOnSuccessListener { cartoes ->
                                for(cartao in cartoes) {
                                    cartaoEncontrado = true
                                    callback(true)
                                }
                            }
                            .addOnFailureListener {e ->
                                Log.e("GET_CARTAO", "ERRO", e)
                            }
                        break
                    }
                }
                if(!cartaoEncontrado) callback(false)
            }
    }

    private fun sair() {
        val builder = AlertDialog.Builder(this@MapsActivity)

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

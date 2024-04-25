package br.edu.puccampinas.safepack.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.edu.puccampinas.safepack.R
import br.edu.puccampinas.safepack.databinding.ActivityAlugarArmarioBinding
import br.edu.puccampinas.safepack.databinding.ActivityArmarioLiberadoBinding
import br.edu.puccampinas.safepack.repositories.LocacaoRepository
import br.edu.puccampinas.safepack.repositories.PessoaRepository
import br.edu.puccampinas.safepack.repositories.UnidadeLocacaoRepository
import com.google.firebase.auth.FirebaseAuth

class ArmarioLiberadoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityArmarioLiberadoBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var pessoaRepository: PessoaRepository
    private lateinit var locacaoRepository: LocacaoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArmarioLiberadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alertDialog = intent.getStringExtra("alertDialog")

        auth = FirebaseAuth.getInstance()
        pessoaRepository = PessoaRepository()
        locacaoRepository = LocacaoRepository()

        binding.voltarMenuButton.setOnClickListener {
            alterarStatusLocacao(auth, "ativa", locacaoRepository, pessoaRepository)
            val iMaps = Intent(this, MapsActivity::class.java)
            if(alertDialog != null) iMaps.putExtra("alertDialog", alertDialog)
            startActivity(iMaps)
        }
    }

    private fun alterarStatusLocacao(auth: FirebaseAuth,
                                     status: String,
                                     locacaoR: LocacaoRepository,
                                     pessoaR: PessoaRepository) {
        val currentUser = auth.currentUser
        if(currentUser != null) {
            pessoaR.getIdByAuthId(currentUser.uid) {idUser ->
                if(idUser != "null") {
                    locacaoR.getLocacaoIdByUserIdPendente(idUser) {idLocacao ->
                        if(idLocacao != "null") {
                            locacaoR.setStatusLocacao(idLocacao, status)
                        }
                    }
                }
            }
        }
    }
}
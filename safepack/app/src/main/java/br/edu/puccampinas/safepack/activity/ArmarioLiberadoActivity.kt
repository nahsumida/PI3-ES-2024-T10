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

        // inflar o layout da activity
        binding = ActivityArmarioLiberadoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // inicializar as instâncias do firebase auth e dos repositorios
        auth = FirebaseAuth.getInstance()
        pessoaRepository = PessoaRepository()
        locacaoRepository = LocacaoRepository()

        // configurar clique do botão "voltar ao menu"
        binding.voltarMenuButton.setOnClickListener {
            //alterarStatusLocacao(auth, "ativa", locacaoRepository, pessoaRepository)
            val iMaps = Intent(this, MapsActivity::class.java)
            iMaps.putExtra("alertDialog", "1")
            startActivity(iMaps)
        }
    }

    // método para alterar o status da locação
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
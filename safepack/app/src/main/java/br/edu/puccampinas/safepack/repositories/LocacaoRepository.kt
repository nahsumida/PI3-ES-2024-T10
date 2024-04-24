package br.edu.puccampinas.safepack.repositories

import android.util.Log
import br.edu.puccampinas.safepack.models.Locacao
import com.google.firebase.firestore.FirebaseFirestore

class LocacaoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val locacoes = db.collection("locacao")

    fun addLocacao(locacao: Locacao){
        locacoes.add(locacao)
            .addOnSuccessListener {
                Log.d("FIRESTORE", "ADD Firestore OK")
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "ERRO",e)
            }
    }
}
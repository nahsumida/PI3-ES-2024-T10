package br.edu.puccampinas.safepack.repositories

import android.util.Log
import br.edu.puccampinas.safepack.models.Locacao
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LocacaoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val locacoesCollection = db.collection("locacao")

    fun addLocacao(locacao: Locacao){
        locacoesCollection.add(locacao)
            .addOnSuccessListener {
                Log.d("FIRESTORE", "ADD Firestore OK")
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "ERRO",e)
            }
    }

    fun getAllLocacoes(): Task<QuerySnapshot> {
        return locacoesCollection.get()
    }

    fun getLocacaoIdByUserIdPendente(userId: String, callback: (String) -> Unit) {
        getAllLocacoes().addOnSuccessListener { locacoes ->
            for(locacao in locacoes) {
                if(locacao.getString("locatarioId").equals(userId) &&
                    locacao.getString("status").equals("pendente"))
                    callback(locacao.id)
            }
            callback("null")
        }
    }

    fun setStatusLocacao(locacaoId: String, status: String) {
        val novoStatus: Map<String, String> = hashMapOf("status" to status)
        val locacao = locacoesCollection.document(locacaoId)

        locacao.update(novoStatus)
    }
}
package br.edu.puccampinas.safepack.repositories

import android.util.Log
import br.edu.puccampinas.safepack.models.Locacao
import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class LocacaoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val locacoesCollection = db.collection("locacao")

    fun addLocacao(locacao: Locacao): Task<DocumentReference>{
        return locacoesCollection.add(locacao)
    }

    fun getAllLocacoes(): Task<QuerySnapshot> {
        return locacoesCollection.get()
    }

    fun getLocacaoById(locacaoId: String): Task<DocumentSnapshot> {
        return locacoesCollection.document(locacaoId).get()
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

    fun setFimLocacao(locacaoId: String, tempo: Timestamp) {
        val fim: Map<String, Timestamp> = hashMapOf("fim" to tempo)
        val locacao = locacoesCollection.document(locacaoId)

        locacao.update(fim)
    }

    fun setFotoPessoaLocacao(locacaoId: String, foto: String, clienteAtual: Int) {
        val novaFoto: Map<String, String>
        if(clienteAtual == 1) {
            novaFoto = hashMapOf("fotoPessoa1" to foto)
        } else {
            novaFoto = hashMapOf("fotoPessoa2" to foto)
        }

        val locacao = locacoesCollection.document(locacaoId)

        locacao.update(novaFoto)
    }

    fun setIdNfcLocacao(locacaoId: String, id: String, clienteAtual: Int) {
        val novoId: Map<String, String>
        if(clienteAtual == 1) {
            novoId = hashMapOf("idNfcPessoa1" to id)
        } else {
            novoId = hashMapOf("idNfcPessoa2" to id)
        }

        val locacao = locacoesCollection.document(locacaoId)

        locacao.update(novoId)
    }

    fun setEstorno(locacaoId: String, estorno: Double) {
        val estornoMulta: Map<String, Double> = hashMapOf("estorno" to estorno)

        locacoesCollection.document(locacaoId).update(estornoMulta)
    }
}
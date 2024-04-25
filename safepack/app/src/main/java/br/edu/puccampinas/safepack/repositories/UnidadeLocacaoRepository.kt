package br.edu.puccampinas.safepack.repositories

import br.edu.puccampinas.safepack.models.UnidadeLocacao
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class UnidadeLocacaoRepository {
    private val db = FirebaseFirestore.getInstance()
    private val unidades = db.collection("unidadeLocacao")

    fun getAllUnidades(): Task<QuerySnapshot> {
        return db.collection("unidadeLocacao").get()
    }

    fun getUnidadeById(idUnidade: String): Task<DocumentSnapshot> {
        return db.collection("unidadeLocacao").document(idUnidade).get()
    }

    fun getArmariosDaUnidade(unidadeId: String): Task<QuerySnapshot> {
        val armariosCollection = unidades.document(unidadeId).collection("armario")

        return armariosCollection.get()
    }

    fun setStatusArmario(unidadeId: String, armarioId:String, status: String) {
        val novoArmario: Map<String, String> = hashMapOf("status" to status)

        val armario = unidades.document(unidadeId).collection("armario").document(armarioId)

        armario.update(novoArmario)
    }
}
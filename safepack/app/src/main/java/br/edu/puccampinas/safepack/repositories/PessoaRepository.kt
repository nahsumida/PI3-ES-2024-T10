package br.edu.puccampinas.safepack.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PessoaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val unidades = db.collection("unidadeLocacao")

    fun getAllUnidades(): Task<QuerySnapshot> {
        return db.collection("unidadeLocacao").get()
    }

    fun getUnidadeById(idUnidade: String): Task<DocumentSnapshot> {
        return db.collection("unidadeLocacao").document(idUnidade).get()
    }
}
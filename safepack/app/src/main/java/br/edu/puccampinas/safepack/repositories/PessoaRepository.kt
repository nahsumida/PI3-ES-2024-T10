package br.edu.puccampinas.safepack.repositories

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

class PessoaRepository {
    private val db = FirebaseFirestore.getInstance()
    private val pessoas = db.collection("pessoa")

    fun getAllPessoas(): Task<QuerySnapshot> {
        return pessoas.get()
    }

    fun getCartaoPessoa(pessoaId: String): Task<QuerySnapshot> {
        val cartaoCollection = pessoas.document(pessoaId).collection("cartao")

        return cartaoCollection.get()
    }

    fun getIdByAuthId(authId: String, callback: (String) -> Unit) {
        getAllPessoas().addOnSuccessListener { pessoas ->
            for(pessoa in pessoas) {
                if(pessoa.getString("authID").equals(authId)) {
                    callback(pessoa.id)
                }
            }
            callback("null")
        }
    }

    fun getPessoaById(pessoaId: String): Task<DocumentSnapshot> {
        return pessoas.document(pessoaId).get()
    }
}
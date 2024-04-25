package br.edu.puccampinas.safepack.model

import com.google.firebase.Timestamp

data class Locacao (
    val armarioId: String,
    val inicio: Timestamp,
    val locatarioId: String,
    val tempo: String,
    val unidadeId: String,
    val valorHora: Double?
)
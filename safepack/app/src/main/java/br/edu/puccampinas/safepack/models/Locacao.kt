package br.edu.puccampinas.safepack.models

import com.google.firebase.Timestamp

data class Locacao (
    val armarioId: String,
    val porta: Int?,
    val inicio: Timestamp,
    val locatarioId: String,
    val status: String,
    val tempo: String,
    val unidadeId: String,
    val valorHora: Double?,
    val fotoPessoa1: String? = null,
    val fotoPessoa2: String? = null,
)
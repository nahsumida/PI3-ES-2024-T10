package br.edu.puccampinas.safepack.models

import com.google.firebase.Timestamp

data class Locacao (
    val armarioId: String,
    val inicio: Timestamp,
    val locatarioId: String,
    val status: String,
    val tempo: String,
    val unidadeId: String,
    val valorHora: Double?,
    val fotoPessoa1: String? = null,
    val idNfcPessoa1: String? = null,
    val fotoPessoa2: String? = null,
    val idNfcPessoa2: String? = null
)
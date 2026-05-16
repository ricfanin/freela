package com.freela.app.domain.model

data class FileAllegato(
    val id: Long = 0,
    val clienteId: Long,
    val nomeFile: String,
    val path: String,
    val tipoMime: String,
    val dataCaricamento: Long,
)

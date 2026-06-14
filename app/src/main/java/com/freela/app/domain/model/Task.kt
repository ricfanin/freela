package com.freela.app.domain.model

enum class Priorita { BASSA, MEDIA, ALTA }

data class Task(
    val id: Long = 0,
    val titolo: String,
    val descrizione: String? = null,
    val clienteId: Long? = null,
    val scadenza: Long,
    val priorita: Priorita = Priorita.MEDIA,
    val completato: Boolean = false,
    val dataCompletamento: Long? = null,
)

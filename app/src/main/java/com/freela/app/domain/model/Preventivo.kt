package com.freela.app.domain.model

enum class StatoPreventivo { INVIATO, ACCETTATO, RIFIUTATO, SCADUTO }

data class Preventivo(
    val id: Long = 0,
    val clienteId: Long,
    val importo: Double,
    val dataInvio: Long,
    val stato: StatoPreventivo = StatoPreventivo.INVIATO,
    val note: String? = null,
)

package com.freela.app.domain.model

enum class TipoInterazione { CALL, MEETING, EMAIL, MESSAGGIO, ALTRO, NOTA }

data class Interazione(
    val id: Long = 0,
    val clienteId: Long,
    val tipo: TipoInterazione,
    val data: Long,
    val durataMinuti: Int? = null,
    val descrizione: String? = null,
    val latitudine: Double? = null,
    val longitudine: Double? = null,
    val indirizzo: String? = null,
)

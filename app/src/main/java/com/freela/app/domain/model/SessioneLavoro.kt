package com.freela.app.domain.model

data class SessioneLavoro(
    val id: Long = 0,
    val clienteId: Long,
    val progettoId: Long? = null,
    val inizio: Long,
    val fine: Long? = null,
    val descrizione: String? = null,
    val inserimentoManuale: Boolean = false,
) {
    val durataMillis: Long? get() = fine?.let { it - inizio }
}

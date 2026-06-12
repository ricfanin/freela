package com.freela.app.domain.model

/** Stato di avanzamento di un progetto. */
enum class StatoProgetto { IN_CORSO, DA_INIZIARE, COMPLETATO }

/** Progetto associato a un cliente (PRD: area Progetti). */
data class Progetto(
    val id: Long = 0,
    val clienteId: Long,
    val nome: String,
    val deadline: Long? = null,
    val oreStimate: Int = 0,
    val stato: StatoProgetto = StatoProgetto.DA_INIZIARE,
    val dataCreazione: Long = System.currentTimeMillis(),
)

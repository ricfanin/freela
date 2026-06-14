package com.freela.app.domain.model

enum class StatoProgetto { IN_CORSO, DA_INIZIARE, COMPLETATO }

data class Progetto(
    val id: Long = 0,
    val clienteId: Long,
    val nome: String,
    val deadline: Long? = null,
    val oreStimate: Int = 0,
    val stato: StatoProgetto = StatoProgetto.DA_INIZIARE,
    val dataCreazione: Long = System.currentTimeMillis(),
)

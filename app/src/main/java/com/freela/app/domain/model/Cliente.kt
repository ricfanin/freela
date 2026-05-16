package com.freela.app.domain.model

/** Modello di dominio Cliente (PRD §9.1). Indipendente da Room. */
data class Cliente(
    val id: Long = 0,
    val nome: String,
    val telefono: String? = null,
    val email: String? = null,
    val fonteAcquisizione: String? = null,
    val faseCorrente: FasePipeline = FasePipeline.NUOVO_LEAD,
    val dataCreazione: Long = System.currentTimeMillis(),
    val note: String? = null,
    val fotoPath: String? = null,
    val orePreventivate: Float? = null,
    val importoPreventivato: Double? = null,
    val avatarColor: String? = null,
    val tags: List<Tag> = emptyList(),
)

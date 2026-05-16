package com.freela.app.domain.model

/** Stato persistente in DB (PRD §9.1 Fattura). */
enum class StatoFattura { EMESSA, PAGATA }

/** Stato derivato a runtime (PRD FR-22) — include IN_RITARDO. */
enum class StatoFatturaUi { EMESSA, PAGATA, IN_RITARDO }

data class Fattura(
    val id: Long = 0,
    val numero: String,
    val clienteId: Long,
    val importo: Double,
    val dataEmissione: Long,
    val dataScadenza: Long,
    val dataPagamento: Long? = null,
    val stato: StatoFattura = StatoFattura.EMESSA,
) {
    /** Calcolo runtime di IN_RITARDO. */
    fun statoUi(now: Long = System.currentTimeMillis()): StatoFatturaUi = when {
        stato == StatoFattura.PAGATA -> StatoFatturaUi.PAGATA
        dataScadenza < now -> StatoFatturaUi.IN_RITARDO
        else -> StatoFatturaUi.EMESSA
    }
}

package com.freela.app.domain.model

import androidx.annotation.StringRes
import com.freela.app.R

/**
 * Dieci fasi della pipeline (PRD FR-05).
 * Ordine = ordine canonico mostrato in tutte le UI (kanban, ribbon, list).
 */
enum class FasePipeline(
    val ordine: Int,
    @StringRes val labelRes: Int,
    @StringRes val shortRes: Int,
) {
    NUOVO_LEAD(0, R.string.stage_nuovo_lead, R.string.stage_short_nuovo_lead),
    PRIMO_CONTATTO(1, R.string.stage_primo_contatto, R.string.stage_short_primo_contatto),
    PREVENTIVO_INVIATO(2, R.string.stage_preventivo_inviato, R.string.stage_short_preventivo_inviato),
    IN_TRATTATIVA(3, R.string.stage_in_trattativa, R.string.stage_short_in_trattativa),
    CONFERMATO(4, R.string.stage_confermato, R.string.stage_short_confermato),
    IN_CORSO(5, R.string.stage_in_corso, R.string.stage_short_in_corso),
    CONSEGNATO(6, R.string.stage_consegnato, R.string.stage_short_consegnato),
    IN_ATTESA_PAGAMENTO(7, R.string.stage_in_attesa_pagamento, R.string.stage_short_in_attesa_pagamento),
    CHIUSO(8, R.string.stage_chiuso, R.string.stage_short_chiuso),
    CLIENTE_RICORRENTE(9, R.string.stage_cliente_ricorrente, R.string.stage_short_cliente_ricorrente);

    companion object {
        val ordered: List<FasePipeline> = entries.sortedBy { it.ordine }
    }
}

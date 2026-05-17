package com.freela.app.domain.usecase

import com.freela.app.domain.model.Fattura
import com.freela.app.domain.model.StatoFatturaUi
import javax.inject.Inject

/**
 * Calcola lo stato UI di una fattura (PRD FR-22): EMESSA/PAGATA persistito + IN_RITARDO derivato.
 */
class CalcolaStatoFattura @Inject constructor() {
    operator fun invoke(fattura: Fattura, now: Long = System.currentTimeMillis()): StatoFatturaUi =
        fattura.statoUi(now)
}

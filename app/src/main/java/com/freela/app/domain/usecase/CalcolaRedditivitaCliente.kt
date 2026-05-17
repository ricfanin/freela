package com.freela.app.domain.usecase

import javax.inject.Inject
import kotlin.math.roundToInt

data class RedditivitaCliente(
    val orePreventivate: Float,
    val oreReali: Float,
    val differenza: Float,
    val ricavoOrarioStimato: Double?,
)

/**
 * PRD FR-20: confronto preventivato vs reale + ricavo orario effettivo.
 */
class CalcolaRedditivitaCliente @Inject constructor() {

    operator fun invoke(orePreventivate: Float?, oreReali: Float, budget: Double?): RedditivitaCliente {
        val p = orePreventivate ?: 0f
        val ricavo = budget?.takeIf { oreReali > 0f }?.div(oreReali)?.let { (it * 100).roundToInt() / 100.0 }
        return RedditivitaCliente(
            orePreventivate = p,
            oreReali = oreReali,
            differenza = oreReali - p,
            ricavoOrarioStimato = ricavo,
        )
    }
}

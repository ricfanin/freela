package com.freela.app.domain.repository

import com.freela.app.domain.model.PersonaDemo
import kotlinx.coroutines.flow.Flow

enum class TemaPreferito { SISTEMA, CHIARO, SCURO }

interface SettingsRepository {
    val onboardingCompleted: Flow<Boolean>
    val personaCorrente: Flow<PersonaDemo?>
    val temaPreferito: Flow<TemaPreferito>
    val giorniSenzaContatto: Flow<Int>
    val giorniFollowUpPreventivo: Flow<Int>

    suspend fun completaOnboarding()
    suspend fun impostaPersona(persona: PersonaDemo)
    suspend fun impostaTema(tema: TemaPreferito)
    suspend fun impostaSogliaSenzaContatto(giorni: Int)
    suspend fun impostaSogliaFollowUp(giorni: Int)
}

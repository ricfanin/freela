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
    val notifScadenzeFatture: Flow<Boolean>
    val notifPromemoriaClienti: Flow<Boolean>
    val notifRiepilogoGiornaliero: Flow<Boolean>
    val nomeUtente: Flow<String?>
    val ruolo: Flow<String?>
    val valuta: Flow<String>

    suspend fun completaOnboarding()
    suspend fun impostaProfilo(nome: String?, ruolo: String?, valuta: String)
    suspend fun impostaPersona(persona: PersonaDemo)
    suspend fun impostaTema(tema: TemaPreferito)
    suspend fun impostaSogliaSenzaContatto(giorni: Int)
    suspend fun impostaSogliaFollowUp(giorni: Int)
    suspend fun impostaNotifScadenzeFatture(attiva: Boolean)
    suspend fun impostaNotifPromemoriaClienti(attiva: Boolean)
    suspend fun impostaNotifRiepilogoGiornaliero(attiva: Boolean)
}

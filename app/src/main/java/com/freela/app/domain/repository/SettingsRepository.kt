package com.freela.app.domain.repository

import kotlinx.coroutines.flow.Flow

enum class TemaPreferito { SISTEMA, CHIARO, SCURO }

interface SettingsRepository {
    val onboardingCompleted: Flow<Boolean>
    val temaPreferito: Flow<TemaPreferito>
    val notifScadenzeFatture: Flow<Boolean>
    val notifPromemoriaClienti: Flow<Boolean>
    val notifRiepilogoGiornaliero: Flow<Boolean>
    val nomeUtente: Flow<String?>
    val ruolo: Flow<String?>
    val valuta: Flow<String>

    suspend fun completaOnboarding()
    suspend fun impostaProfilo(nome: String?, ruolo: String?, valuta: String)
    suspend fun impostaTema(tema: TemaPreferito)
    suspend fun impostaNotifScadenzeFatture(attiva: Boolean)
    suspend fun impostaNotifPromemoriaClienti(attiva: Boolean)
    suspend fun impostaNotifRiepilogoGiornaliero(attiva: Boolean)

    // azzera tutto: profilo, tema, notifiche e flag onboarding
    suspend fun logout()
}

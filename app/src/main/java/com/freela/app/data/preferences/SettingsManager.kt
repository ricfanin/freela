package com.freela.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TemaPreferito
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "freela_prefs")

@Singleton
class SettingsManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SettingsRepository {

    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val TEMA = stringPreferencesKey("tema_preferito")
        val SOGLIA_SENZA_CONTATTO = intPreferencesKey("soglia_senza_contatto_giorni")
        val SOGLIA_FOLLOW_UP = intPreferencesKey("soglia_follow_up_preventivo_giorni")
        val NOTIF_SCADENZE = booleanPreferencesKey("notif_scadenze_fatture")
        val NOTIF_PROMEMORIA = booleanPreferencesKey("notif_promemoria_clienti")
        val NOTIF_RIEPILOGO = booleanPreferencesKey("notif_riepilogo_giornaliero")
        val NOME_UTENTE = stringPreferencesKey("nome_utente")
        val RUOLO = stringPreferencesKey("ruolo_utente")
        val VALUTA = stringPreferencesKey("valuta")
    }

    override val onboardingCompleted: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_COMPLETED] ?: false }

    override val temaPreferito: Flow<TemaPreferito> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.TEMA]?.let { runCatching { TemaPreferito.valueOf(it) }.getOrNull() }
                ?: TemaPreferito.SISTEMA
        }

    override val giorniSenzaContatto: Flow<Int> =
        context.dataStore.data.map { it[Keys.SOGLIA_SENZA_CONTATTO] ?: 14 }

    override val giorniFollowUpPreventivo: Flow<Int> =
        context.dataStore.data.map { it[Keys.SOGLIA_FOLLOW_UP] ?: 5 }

    override val notifScadenzeFatture: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.NOTIF_SCADENZE] ?: true }

    override val notifPromemoriaClienti: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.NOTIF_PROMEMORIA] ?: true }

    override val notifRiepilogoGiornaliero: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.NOTIF_RIEPILOGO] ?: false }

    override val nomeUtente: Flow<String?> =
        context.dataStore.data.map { it[Keys.NOME_UTENTE]?.ifBlank { null } }

    override val ruolo: Flow<String?> =
        context.dataStore.data.map { it[Keys.RUOLO]?.ifBlank { null } }

    override val valuta: Flow<String> =
        context.dataStore.data.map { it[Keys.VALUTA] ?: "EUR" }

    override suspend fun completaOnboarding() {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = true }
    }

    override suspend fun impostaProfilo(nome: String?, ruolo: String?, valuta: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.NOME_UTENTE] = nome?.trim().orEmpty()
            prefs[Keys.RUOLO] = ruolo.orEmpty()
            prefs[Keys.VALUTA] = valuta
        }
    }

    override suspend fun impostaTema(tema: TemaPreferito) {
        context.dataStore.edit { it[Keys.TEMA] = tema.name }
    }

    override suspend fun impostaSogliaSenzaContatto(giorni: Int) {
        context.dataStore.edit { it[Keys.SOGLIA_SENZA_CONTATTO] = giorni }
    }

    override suspend fun impostaSogliaFollowUp(giorni: Int) {
        context.dataStore.edit { it[Keys.SOGLIA_FOLLOW_UP] = giorni }
    }

    override suspend fun impostaNotifScadenzeFatture(attiva: Boolean) {
        context.dataStore.edit { it[Keys.NOTIF_SCADENZE] = attiva }
    }

    override suspend fun impostaNotifPromemoriaClienti(attiva: Boolean) {
        context.dataStore.edit { it[Keys.NOTIF_PROMEMORIA] = attiva }
    }

    override suspend fun impostaNotifRiepilogoGiornaliero(attiva: Boolean) {
        context.dataStore.edit { it[Keys.NOTIF_RIEPILOGO] = attiva }
    }

    override suspend fun logout() {
        context.dataStore.edit { it.clear() }
    }
}

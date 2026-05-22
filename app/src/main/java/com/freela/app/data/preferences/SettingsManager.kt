package com.freela.app.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.freela.app.domain.model.PersonaDemo
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
        val PERSONA = stringPreferencesKey("current_persona")
        val TEMA = stringPreferencesKey("tema_preferito")
        val SOGLIA_SENZA_CONTATTO = intPreferencesKey("soglia_senza_contatto_giorni")
        val SOGLIA_FOLLOW_UP = intPreferencesKey("soglia_follow_up_preventivo_giorni")
    }

    override val onboardingCompleted: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.ONBOARDING_COMPLETED] ?: false }

    override val personaCorrente: Flow<PersonaDemo?> =
        context.dataStore.data.map { prefs -> PersonaDemo.fromKey(prefs[Keys.PERSONA]) }

    override val temaPreferito: Flow<TemaPreferito> =
        context.dataStore.data.map { prefs ->
            prefs[Keys.TEMA]?.let { runCatching { TemaPreferito.valueOf(it) }.getOrNull() }
                ?: TemaPreferito.SISTEMA
        }

    override val giorniSenzaContatto: Flow<Int> =
        context.dataStore.data.map { it[Keys.SOGLIA_SENZA_CONTATTO] ?: 14 }

    override val giorniFollowUpPreventivo: Flow<Int> =
        context.dataStore.data.map { it[Keys.SOGLIA_FOLLOW_UP] ?: 5 }

    override suspend fun completaOnboarding() {
        context.dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = true }
    }

    override suspend fun impostaPersona(persona: PersonaDemo) {
        context.dataStore.edit { it[Keys.PERSONA] = persona.key }
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
}

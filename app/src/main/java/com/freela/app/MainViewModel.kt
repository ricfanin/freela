package com.freela.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.freela.app.domain.repository.SettingsRepository
import com.freela.app.domain.repository.TemaPreferito
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

// tengo il tema a livello di activity per applicarlo prima del navhost, e siccome
// datastore è reattivo al cambio dalle impostazioni la ui ricompone da sola
@HiltViewModel
class MainViewModel @Inject constructor(
    settings: SettingsRepository,
) : ViewModel() {

    val tema: StateFlow<TemaPreferito> =
        settings.temaPreferito
            .stateIn(viewModelScope, SharingStarted.Eagerly, TemaPreferito.SISTEMA)
}

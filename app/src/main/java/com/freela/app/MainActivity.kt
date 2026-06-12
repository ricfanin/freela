package com.freela.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.freela.app.domain.repository.TemaPreferito
import com.freela.app.notification.NotificationHelper
import com.freela.app.ui.navigation.FreelaNavHost
import com.freela.app.ui.theme.FreelaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // POST_NOTIFICATIONS (runtime da Android 13, NFR-12) viene richiesto in modo
    // contestualizzato nello step "Notifiche" dell'onboarding (OnboardingScreen).
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deepLink = mutableStateOf(intent?.getStringExtra(NotificationHelper.EXTRA_NAV_DESTINATION))

        setContent {
            val tema by viewModel.tema.collectAsStateWithLifecycle()
            val darkTheme = when (tema) {
                TemaPreferito.SISTEMA -> isSystemInDarkTheme()
                TemaPreferito.CHIARO -> false
                TemaPreferito.SCURO -> true
            }
            FreelaTheme(darkTheme = darkTheme) {
                var dest by deepLink
                FreelaNavHost(
                    deepLinkDestination = dest,
                    onDeepLinkHandled = { dest = null },
                )
            }
        }
    }
}

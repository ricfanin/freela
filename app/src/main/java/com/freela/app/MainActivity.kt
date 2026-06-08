package com.freela.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.freela.app.notification.NotificationHelper
import com.freela.app.ui.navigation.FreelaNavHost
import com.freela.app.ui.theme.FreelaTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // POST_NOTIFICATIONS (runtime da Android 13, NFR-12) viene richiesto in modo
    // contestualizzato nello step "Notifiche" dell'onboarding (OnboardingScreen).
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val deepLink = mutableStateOf(intent?.getStringExtra(NotificationHelper.EXTRA_NAV_DESTINATION))

        setContent {
            FreelaTheme {
                var dest by deepLink
                FreelaNavHost(
                    deepLinkDestination = dest,
                    onDeepLinkHandled = { dest = null },
                )
            }
        }
    }
}

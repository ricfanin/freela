package com.freela.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.freela.app.ui.components.FreelaBottomNav
import com.freela.app.ui.components.FreelaTab
import com.freela.app.ui.screens.boot.BootViewModel
import com.freela.app.ui.screens.clienti.ClienteDetailScreen
import com.freela.app.ui.screens.clienti.ClientiScreen
import com.freela.app.ui.screens.clienti.NuovoClienteScreen
import com.freela.app.ui.screens.progetti.NuovoProgettoScreen
import com.freela.app.ui.screens.finanze.FinanzeScreen
import com.freela.app.ui.screens.oggi.OggiScreen
import com.freela.app.ui.screens.onboarding.OnboardingScreen
import com.freela.app.ui.screens.pipeline.PipelineScreen
import com.freela.app.ui.screens.progetti.ProgettiScreen
import com.freela.app.ui.screens.progetti.ProgettoDetailScreen
import com.freela.app.ui.screens.settings.SettingsScreen
import com.freela.app.ui.screens.storico.StoricoScreen
import com.freela.app.ui.screens.task.TaskScreen
import com.freela.app.ui.screens.tracking.TimerScreen
import com.freela.app.ui.theme.Freela

@Composable
fun FreelaNavHost(
    deepLinkDestination: String? = null,
    onDeepLinkHandled: () -> Unit = {},
    bootViewModel: BootViewModel = hiltViewModel(),
) {
    val navController = rememberNavController()
    val bootState by bootViewModel.state.collectAsStateWithLifecycle()

    // aspetto il boot prima di costruire la navhost, se no la startDestination cambia
    // a metà e si ricrea il grafo, e la bottom bar si rompe (oggi finiva su finanze)
    if (!bootState.ready) {
        Box(Modifier.fillMaxSize().background(Freela.tokens.bg))
        return
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTab: FreelaTab? = topLevelRoutes[currentRoute]

    // calcolata una volta sola così resta stabile e il grafo non si ricrea, il passaggio
    // da onboarding a oggi lo fa a mano OnboardingScreen.onStart
    val startDestination = remember {
        if (bootState.onboardingCompleted) Routes.OGGI else Routes.ONBOARDING
    }

    // deep-link dalle notifiche: navigo quando il boot è pronto e l'onboarding è fatto
    LaunchedEffect(deepLinkDestination, bootState.ready) {
        val dest = deepLinkDestination
        if (dest != null && bootState.ready && bootState.onboardingCompleted) {
            navController.navigate(dest) { launchSingleTop = true }
            onDeepLinkHandled()
        }
    }

    Scaffold(
        bottomBar = {
            if (currentTab != null) {
                FreelaBottomNav(
                    active = currentTab,
                    onTabClick = { tab ->
                        navController.navigate(tab.toRoute()) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { innerPadding: PaddingValues ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Routes.ONBOARDING) {
                OnboardingScreen(
                    onStart = {
                        navController.navigate(Routes.OGGI) {
                            popUpTo(Routes.ONBOARDING) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.OGGI) {
                OggiScreen(
                    onNavigateToCliente = { id -> navController.navigate(Routes.clienteDetail(id)) },
                    onNavigateToFinanze = { navController.navigate(Routes.FINANZE) },
                    onNavigateToStorico = { navController.navigate(Routes.STORICO) },
                    onNavigateToSettings = { navController.navigate(Routes.SETTINGS) },
                    onStartTimer = { navController.navigate(Routes.TIMER) },
                )
            }
            composable(Routes.PIPELINE) {
                PipelineScreen(
                    onNavigateToCliente = { id -> navController.navigate(Routes.clienteDetail(id)) },
                    onNuovoCliente = { navController.navigate(Routes.NUOVO_CLIENTE) },
                )
            }
            composable(Routes.CLIENTI) {
                ClientiScreen(
                    onNavigateToCliente = { id -> navController.navigate(Routes.clienteDetail(id)) },
                    onNuovoCliente = { navController.navigate(Routes.NUOVO_CLIENTE) },
                )
            }
            composable(
                route = Routes.CLIENTE_DETAIL,
                arguments = listOf(navArgument(Routes.ARG_CLIENTE_ID) { type = NavType.LongType }),
            ) {
                ClienteDetailScreen(
                    onBack = { navController.popBackStack() },
                    onStartTimer = { navController.navigate(Routes.TIMER) },
                    onNavigateToFinanze = { navController.navigate(Routes.FINANZE) },
                )
            }
            composable(Routes.TASK) { TaskScreen() }
            composable(Routes.PROGETTI) {
                ProgettiScreen(
                    onNavigateToProgetto = { id -> navController.navigate(Routes.progettoDetail(id)) },
                    onNuovoProgetto = { navController.navigate(Routes.NUOVO_PROGETTO) },
                )
            }
            composable(
                route = Routes.PROGETTO_DETAIL,
                arguments = listOf(navArgument(Routes.ARG_PROGETTO_ID) { type = NavType.LongType }),
            ) {
                ProgettoDetailScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.NUOVO_CLIENTE) {
                NuovoClienteScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.NUOVO_PROGETTO) {
                NuovoProgettoScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.TIMER) {
                TimerScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.FINANZE) { FinanzeScreen() }
            composable(Routes.STORICO) {
                StoricoScreen(onBack = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Routes.ONBOARDING) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}

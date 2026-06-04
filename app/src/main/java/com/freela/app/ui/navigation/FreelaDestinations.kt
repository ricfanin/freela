package com.freela.app.ui.navigation

import com.freela.app.ui.components.FreelaTab

/**
 * Routes della NavHost. Single source of truth dei nomi rotta.
 */
object Routes {
    const val ONBOARDING = "onboarding"
    const val OGGI = "oggi"
    const val PIPELINE = "pipeline"
    const val CLIENTI = "clienti"
    const val CLIENTE_DETAIL = "clienti/{clienteId}"
    fun clienteDetail(id: Long) = "clienti/$id"
    const val TASK = "task"
    const val TIMER = "timer"
    const val FINANZE = "finanze"
    const val STORICO = "storico"
    const val SETTINGS = "settings"

    const val ARG_CLIENTE_ID = "clienteId"
}

/** Route dove mostrare la BottomNav. */
val topLevelRoutes = mapOf(
    Routes.OGGI to FreelaTab.OGGI,
    Routes.PIPELINE to FreelaTab.PIPELINE,
    Routes.CLIENTI to FreelaTab.CLIENTI,
    Routes.TASK to FreelaTab.TASK,
    Routes.FINANZE to FreelaTab.FINANZE,
)

fun FreelaTab.toRoute(): String = when (this) {
    FreelaTab.OGGI -> Routes.OGGI
    FreelaTab.PIPELINE -> Routes.PIPELINE
    FreelaTab.CLIENTI -> Routes.CLIENTI
    FreelaTab.TASK -> Routes.TASK
    FreelaTab.FINANZE -> Routes.FINANZE
}

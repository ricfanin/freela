package com.freela.app.ui.navigation

import com.freela.app.ui.components.FreelaTab

// nomi delle rotte, definiti tutti qui così non si scrivono a mano in giro
object Routes {
    const val ONBOARDING = "onboarding"
    const val OGGI = "oggi"
    const val PIPELINE = "pipeline"
    const val CLIENTI = "clienti"
    const val CLIENTE_DETAIL = "clienti/{clienteId}"
    fun clienteDetail(id: Long) = "clienti/$id"
    const val TASK = "task"
    const val PROGETTI = "progetti"
    const val PROGETTO_DETAIL = "progetti/{progettoId}"
    fun progettoDetail(id: Long) = "progetti/$id"
    const val ARG_PROGETTO_ID = "progettoId"
    const val NUOVO_CLIENTE = "nuovo_cliente"
    const val NUOVO_PROGETTO = "nuovo_progetto"
    const val TIMER = "timer"
    const val FINANZE = "finanze"
    const val STORICO = "storico"
    const val SETTINGS = "settings"

    const val ARG_CLIENTE_ID = "clienteId"
}

// rotte su cui si vede la bottom bar
val topLevelRoutes = mapOf(
    Routes.OGGI to FreelaTab.OGGI,
    Routes.PIPELINE to FreelaTab.PIPELINE,
    Routes.CLIENTI to FreelaTab.CLIENTI,
    Routes.PROGETTI to FreelaTab.PROGETTI,
    Routes.FINANZE to FreelaTab.FINANZE,
)

fun FreelaTab.toRoute(): String = when (this) {
    FreelaTab.OGGI -> Routes.OGGI
    FreelaTab.PIPELINE -> Routes.PIPELINE
    FreelaTab.CLIENTI -> Routes.CLIENTI
    FreelaTab.PROGETTI -> Routes.PROGETTI
    FreelaTab.FINANZE -> Routes.FINANZE
}

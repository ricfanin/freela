package com.freela.app.data.seed

import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.PersonaDemo
import com.freela.app.domain.model.TipoInterazione

/**
 * Payload statico delle 3 persone demo, fedele a design_handoff_freela/data.jsx.
 * I `localId` sono identificatori interni al payload, mappati ai veri DB id dal SeedDataSource.
 */
data class ClientePayload(
    val localId: Int,
    val nome: String,
    val tag: String,
    val fonte: String,
    val stage: FasePipeline,
    val budget: Double? = null,
    val orePreventivate: Float? = null,
    val oreReali: Float = 0f,
    val avatarColor: String? = null,
    val note: String? = null,
    val giorniDallUltimaInterazione: Int = 3,
    val tipoUltimaInterazione: TipoInterazione = TipoInterazione.CALL,
)

data class TaskPayload(
    val titolo: String,
    val clienteLocalId: Int,
    val urgente: Boolean = false,
    val oraOpzionale: Int? = null,
    val giorniInAvanti: Int = 0,
)

data class FatturaPayload(
    val numero: String,
    val clienteLocalId: Int,
    val importo: Double,
    val giorniDallaScadenza: Int, // positivo = già scaduta da N giorni; negativo = in scadenza fra N
    val pagata: Boolean = false,
)

data class SeedPayload(
    val clienti: List<ClientePayload>,
    val taskOggi: List<TaskPayload>,
    val taskSettimana: List<TaskPayload>,
    val taskSuggeriti: List<TaskPayload>,
    val fatture: List<FatturaPayload>,
)

object PersonaSeed {
    fun of(persona: PersonaDemo): SeedPayload = when (persona) {
        PersonaDemo.GIULIA -> giulia
        PersonaDemo.LUCA -> luca
        PersonaDemo.SARA -> sara
    }

    private val giulia = SeedPayload(
        clienti = listOf(
            ClientePayload(1, "Forno Antico Bertelli", "Food", "Passaparola", FasePipeline.IN_CORSO,
                budget = 1800.0, orePreventivate = 20f, oreReali = 14f, avatarColor = "#E8C19C",
                note = "Preferiscono toni caldi, pasticceria tradizionale.",
                giorniDallUltimaInterazione = 2, tipoUltimaInterazione = TipoInterazione.CALL),
            ClientePayload(2, "Olivia · skincare", "Beauty", "Instagram", FasePipeline.PREVENTIVO_INVIATO,
                budget = 2400.0, orePreventivate = 32f, oreReali = 0f, avatarColor = "#D9B7C9",
                note = "Founder solo, brand piccolo ma curato.",
                giorniDallUltimaInterazione = 5, tipoUltimaInterazione = TipoInterazione.EMAIL),
            ClientePayload(3, "Vivaio Le Camelie", "Local", "Passaparola", FasePipeline.CONFERMATO,
                budget = 1200.0, orePreventivate = 16f, oreReali = 2f, avatarColor = "#BFD4B2",
                giorniDallUltimaInterazione = 1),
            ClientePayload(4, "Carlo Tessitori", "Coach", "LinkedIn", FasePipeline.PRIMO_CONTATTO,
                avatarColor = "#C9C0E0", giorniDallUltimaInterazione = 3),
            ClientePayload(5, "Studio Dentale Rosa", "Health", "Sito web", FasePipeline.IN_ATTESA_PAGAMENTO,
                budget = 950.0, orePreventivate = 12f, oreReali = 12f, avatarColor = "#E8B8B8",
                giorniDallUltimaInterazione = 8, tipoUltimaInterazione = TipoInterazione.MESSAGGIO),
            ClientePayload(6, "Riccardo Vinaio", "Food", "Passaparola", FasePipeline.NUOVO_LEAD,
                avatarColor = "#D8C28F", giorniDallUltimaInterazione = 0),
            ClientePayload(7, "Bottega Lina", "Retail", "Instagram", FasePipeline.CLIENTE_RICORRENTE,
                budget = 3600.0, orePreventivate = 48f, oreReali = 41f, avatarColor = "#B7C6D9",
                giorniDallUltimaInterazione = 1),
            ClientePayload(8, "Marco Atletica", "Sport", "Evento", FasePipeline.CONSEGNATO,
                budget = 1500.0, orePreventivate = 22f, oreReali = 24f, avatarColor = "#A8C6BD",
                giorniDallUltimaInterazione = 4),
        ),
        taskOggi = listOf(
            TaskPayload("Olivia · follow-up preventivo da 5g", clienteLocalId = 2, urgente = true, oraOpzionale = 10),
            TaskPayload("Carlo · call alle 14:30", clienteLocalId = 4, oraOpzionale = 14),
            TaskPayload("Reel pasta fresca", clienteLocalId = 1, oraOpzionale = 18),
            TaskPayload("Brief gennaio", clienteLocalId = 3, giorniInAvanti = 1),
        ),
        taskSettimana = listOf(
            TaskPayload("Inviare contratto firmato", clienteLocalId = 7, giorniInAvanti = 3),
            TaskPayload("Mood-board v2", clienteLocalId = 2, giorniInAvanti = 4),
        ),
        taskSuggeriti = listOf(
            TaskPayload("Sollecito preventivo (5 giorni)", clienteLocalId = 2),
            TaskPayload("Cliente senza contatto da 14 giorni", clienteLocalId = 4),
        ),
        fatture = listOf(
            FatturaPayload("2025-018", clienteLocalId = 5, importo = 950.0, giorniDallaScadenza = 8),
            FatturaPayload("2025-022", clienteLocalId = 8, importo = 1500.0, giorniDallaScadenza = -4),
            FatturaPayload("2025-016", clienteLocalId = 7, importo = 3600.0, giorniDallaScadenza = 20, pagata = true),
            FatturaPayload("2025-014", clienteLocalId = 3, importo = 1200.0, giorniDallaScadenza = 35, pagata = true),
            FatturaPayload("2025-021", clienteLocalId = 1, importo = 1800.0, giorniDallaScadenza = -15),
        ),
    )

    private val luca = SeedPayload(
        clienti = listOf(
            ClientePayload(1, "Cooperativa Argo", "Non-profit", "LinkedIn", FasePipeline.IN_CORSO,
                budget = 5200.0, orePreventivate = 40f, oreReali = 18f, avatarColor = "#BFC9E0",
                giorniDallUltimaInterazione = 0),
            ClientePayload(2, "Tania Avvocato", "Legal", "Passaparola", FasePipeline.IN_TRATTATIVA,
                budget = 3400.0, orePreventivate = 24f, oreReali = 0f, avatarColor = "#C8D2C0",
                giorniDallUltimaInterazione = 2, tipoUltimaInterazione = TipoInterazione.EMAIL),
            ClientePayload(3, "Foglie Editore", "Editoria", "Sito web", FasePipeline.CONFERMATO,
                budget = 6800.0, orePreventivate = 56f, oreReali = 4f, avatarColor = "#D8C8B0",
                giorniDallUltimaInterazione = 1),
            ClientePayload(4, "Tito Architetti", "Studio", "Evento", FasePipeline.PREVENTIVO_INVIATO,
                avatarColor = "#C0BFB8", giorniDallUltimaInterazione = 3),
            ClientePayload(5, "Lara Pasticceria", "Food", "Instagram", FasePipeline.CONSEGNATO,
                budget = 2200.0, orePreventivate = 28f, oreReali = 32f, avatarColor = "#E8C6BC",
                giorniDallUltimaInterazione = 6),
        ),
        taskOggi = listOf(
            TaskPayload("Tania · follow-up proposta", clienteLocalId = 2, oraOpzionale = 11),
            TaskPayload("Tito · revisione preventivo", clienteLocalId = 4, urgente = true, oraOpzionale = 15),
            TaskPayload("Mock homepage v2", clienteLocalId = 1, oraOpzionale = 18),
        ),
        taskSettimana = listOf(
            TaskPayload("Sitemap Foglie", clienteLocalId = 3, giorniInAvanti = 3),
            TaskPayload("Fattura finale Lara", clienteLocalId = 5, giorniInAvanti = 2),
        ),
        taskSuggeriti = listOf(
            TaskPayload("Sollecito preventivo Tito (3 giorni)", clienteLocalId = 4),
        ),
        fatture = listOf(
            FatturaPayload("2025-011", clienteLocalId = 5, importo = 2200.0, giorniDallaScadenza = -3),
            FatturaPayload("2025-009", clienteLocalId = 1, importo = 2600.0, giorniDallaScadenza = 25, pagata = true),
            FatturaPayload("2025-007", clienteLocalId = 3, importo = 1700.0, giorniDallaScadenza = 12),
        ),
    )

    private val sara = SeedPayload(
        clienti = listOf(
            ClientePayload(1, "Anna & Davide", "Wedding", "Instagram", FasePipeline.CONFERMATO,
                budget = 2800.0, orePreventivate = 18f, oreReali = 6f, avatarColor = "#E0C7BC",
                note = "Cerimonia 14 giugno. Anniversario nonna importante.",
                giorniDallUltimaInterazione = 1, tipoUltimaInterazione = TipoInterazione.MEETING),
            ClientePayload(2, "Galleria Lume", "Arte", "Passaparola", FasePipeline.IN_CORSO,
                budget = 1400.0, orePreventivate = 12f, oreReali = 9f, avatarColor = "#B8C8C8",
                giorniDallUltimaInterazione = 3),
            ClientePayload(3, "Brand Solea", "Fashion", "Sito web", FasePipeline.PREVENTIVO_INVIATO,
                avatarColor = "#D9CDB7", giorniDallUltimaInterazione = 4, tipoUltimaInterazione = TipoInterazione.EMAIL),
            ClientePayload(4, "Famiglia Conti", "Privati", "Passaparola", FasePipeline.CLIENTE_RICORRENTE,
                budget = 600.0, orePreventivate = 4f, oreReali = 4f, avatarColor = "#E2D6BC",
                giorniDallUltimaInterazione = 12),
            ClientePayload(5, "Hotel Rive", "Hospitality", "LinkedIn", FasePipeline.IN_ATTESA_PAGAMENTO,
                budget = 1900.0, orePreventivate = 14f, oreReali = 16f, avatarColor = "#C8C0B8",
                giorniDallUltimaInterazione = 15),
        ),
        taskOggi = listOf(
            TaskPayload("Sopralluogo Villa Pisani · 11:00", clienteLocalId = 1, urgente = true, oraOpzionale = 11),
            TaskPayload("Solea · follow-up preventivo da 4g", clienteLocalId = 3, oraOpzionale = 16),
        ),
        taskSettimana = listOf(
            TaskPayload("Selezione opening · 60 scatti", clienteLocalId = 2, giorniInAvanti = 2),
            TaskPayload("Sessione bambini primavera", clienteLocalId = 4, giorniInAvanti = 5),
        ),
        taskSuggeriti = listOf(
            TaskPayload("Sollecito #021 Hotel Rive", clienteLocalId = 5),
        ),
        fatture = listOf(
            FatturaPayload("2025-021", clienteLocalId = 5, importo = 1900.0, giorniDallaScadenza = 4),
            FatturaPayload("2025-018", clienteLocalId = 1, importo = 1400.0, giorniDallaScadenza = 8),
            FatturaPayload("2025-014", clienteLocalId = 4, importo = 950.0, giorniDallaScadenza = 20, pagata = true),
        ),
    )
}

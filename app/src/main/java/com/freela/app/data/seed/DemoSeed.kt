package com.freela.app.data.seed

import com.freela.app.domain.model.FasePipeline
import com.freela.app.domain.model.TipoInterazione

// dati demo dell'app, tenuti coerenti tra loro: fasi, ore, progetti e fatture combaciano
// (es. cliente in attesa di pagamento = fattura scaduta non pagata).
// i localId sono interni al payload e vengono mappati ai veri id del db al momento del seed
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
    val progettoNome: String? = null,
)

data class TaskPayload(
    val titolo: String,
    val clienteLocalId: Int,
    val urgente: Boolean = false,
    val oraOpzionale: Int? = null,
    val giorniInAvanti: Int = 0,
)

data class FatturaPayload(
    val progressivo: Int, // diventa "<anno>-0NN" al momento del seed
    val clienteLocalId: Int,
    val importo: Double,
    val giorniDallaScadenza: Int, // positivo = già scaduta da N giorni; negativo = in scadenza fra N
    val pagata: Boolean = false,
)

data class SeedPayload(
    val clienti: List<ClientePayload>,
    val taskOggi: List<TaskPayload>,
    val taskSettimana: List<TaskPayload>,
    val fatture: List<FatturaPayload>,
)

object DemoSeed {

    val payload = SeedPayload(
        clienti = listOf(
            ClientePayload(
                1, "Forno Antico Bertelli", "Food", "Passaparola", FasePipeline.IN_CORSO,
                budget = 1800.0, orePreventivate = 20f, oreReali = 14f, avatarColor = "#E8C19C",
                note = "Preferiscono toni caldi, pasticceria tradizionale.",
                giorniDallUltimaInterazione = 2, tipoUltimaInterazione = TipoInterazione.CALL,
                progettoNome = "Restyling brand pasticceria",
            ),
            ClientePayload(
                2, "Olivia Skincare", "Beauty", "Instagram", FasePipeline.PREVENTIVO_INVIATO,
                budget = 2400.0, orePreventivate = 32f, oreReali = 0f, avatarColor = "#D9B7C9",
                note = "Founder solo, brand piccolo ma curato.",
                giorniDallUltimaInterazione = 5, tipoUltimaInterazione = TipoInterazione.EMAIL,
                progettoNome = "E-commerce skincare",
            ),
            ClientePayload(
                3, "Vivaio Le Camelie", "Local", "Passaparola", FasePipeline.CONFERMATO,
                budget = 1200.0, orePreventivate = 16f, oreReali = 3f, avatarColor = "#BFD4B2",
                giorniDallUltimaInterazione = 1, tipoUltimaInterazione = TipoInterazione.MESSAGGIO,
                progettoNome = "Sito vetrina vivaio",
            ),
            ClientePayload(
                4, "Carlo Tessitori", "Coach", "LinkedIn", FasePipeline.PRIMO_CONTATTO,
                avatarColor = "#C9C0E0",
                giorniDallUltimaInterazione = 3, tipoUltimaInterazione = TipoInterazione.EMAIL,
            ),
            ClientePayload(
                5, "Studio Dentale Rosa", "Health", "Sito web", FasePipeline.IN_ATTESA_PAGAMENTO,
                budget = 950.0, orePreventivate = 12f, oreReali = 12f, avatarColor = "#E8B8B8",
                giorniDallUltimaInterazione = 8, tipoUltimaInterazione = TipoInterazione.MESSAGGIO,
                progettoNome = "Sito + prenotazioni online",
            ),
            ClientePayload(
                6, "Riccardo Vinaio", "Food", "Passaparola", FasePipeline.NUOVO_LEAD,
                avatarColor = "#D8C28F",
                giorniDallUltimaInterazione = 1, tipoUltimaInterazione = TipoInterazione.MESSAGGIO,
            ),
            ClientePayload(
                7, "Bottega Lina", "Retail", "Instagram", FasePipeline.CLIENTE_RICORRENTE,
                budget = 3600.0, orePreventivate = 48f, oreReali = 41f, avatarColor = "#B7C6D9",
                giorniDallUltimaInterazione = 1, tipoUltimaInterazione = TipoInterazione.CALL,
                progettoNome = "Gestione social mensile",
            ),
            ClientePayload(
                8, "Marco Atletica", "Sport", "Evento", FasePipeline.CONSEGNATO,
                budget = 1500.0, orePreventivate = 22f, oreReali = 22f, avatarColor = "#A8C6BD",
                giorniDallUltimaInterazione = 4, tipoUltimaInterazione = TipoInterazione.MEETING,
                progettoNome = "Identità visiva evento",
            ),
        ),
        taskOggi = listOf(
            TaskPayload("Olivia · follow-up preventivo (5 giorni)", clienteLocalId = 2, urgente = true, oraOpzionale = 10),
            TaskPayload("Carlo · call conoscitiva", clienteLocalId = 4, oraOpzionale = 14),
            TaskPayload("Reel pasta fresca", clienteLocalId = 1, oraOpzionale = 18),
        ),
        taskSettimana = listOf(
            TaskPayload("Inviare contratto firmato", clienteLocalId = 7, giorniInAvanti = 3),
            TaskPayload("Mood-board v2", clienteLocalId = 2, giorniInAvanti = 4),
            TaskPayload("Consegna materiali finali", clienteLocalId = 8, giorniInAvanti = 2),
        ),
        fatture = listOf(
            // Studio Dentale Rosa · lavoro consegnato, in attesa di pagamento (scaduta)
            FatturaPayload(progressivo = 24, clienteLocalId = 5, importo = 950.0, giorniDallaScadenza = 8),
            // Marco Atletica · consegnato, fattura emessa in scadenza
            FatturaPayload(progressivo = 23, clienteLocalId = 8, importo = 1500.0, giorniDallaScadenza = -6),
            // Bottega Lina · cliente ricorrente, fattura del mese già saldata
            FatturaPayload(progressivo = 21, clienteLocalId = 7, importo = 3600.0, giorniDallaScadenza = 20, pagata = true),
            // Forno Antico Bertelli · acconto 50% versato a inizio lavori
            FatturaPayload(progressivo = 22, clienteLocalId = 1, importo = 900.0, giorniDallaScadenza = 10, pagata = true),
            // Studio Dentale Rosa · lavoro precedente già saldato (cliente di ritorno)
            FatturaPayload(progressivo = 18, clienteLocalId = 5, importo = 950.0, giorniDallaScadenza = 60, pagata = true),
        ),
    )
}
